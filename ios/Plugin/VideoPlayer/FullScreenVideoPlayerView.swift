//
//  FullScreenVideoPlayerView.swift
//  Plugin
//
//  Created by  Quéau Jean Pierre on 13/01/2020.
//  Copyright © 2021 Max Lynch. All rights reserved.
//

import UIKit
import AVKit
import MediaPlayer

// swiftlint:disable file_length
// swiftlint:disable type_body_length
open class FullScreenVideoPlayerView: UIView, AVContentKeySessionDelegate {
    private var _url: URL
    private var _isReadyToPlay: Bool = false
    private var _videoId: String = "fullscreen"
    private var _currentTime: Double = 0
    private var _duration: Double = 0
    private var _isLoaded: [String: Bool] = [:]
    private var _isBufferEmpty: [String: Bool] = [:]
    private var _exitOnEnd: Bool = true
    private var _loopOnEnd: Bool = false
    private var _pipEnabled: Bool = true
    private var _firstReadyToPlay: Bool = true
    private var _stUrl: URL?
    private var _stLanguage: String?
    private var _stHeaders: [String: String]?
    private var _stOptions: [String: Any]?
    private var _videoRate: Float
    private var _showControls: Bool = true
    private var _displayMode: String = "all"
    private var _title: String?
    private var _smallTitle: String?
    private var _artwork: String?
    private var _drm: [String: Any]?
    private var _drmlicenseUri: String?
    private var _drmcertUri: String?
    private var _drmHeaders: [String: String]?

    var player: AVPlayer?
    var videoPlayer: AVPlayerViewController
    var videoAsset: AVURLAsset
    var playerItem: AVPlayerItem?
    var isPlaying: Bool
    var itemBufferObserver: NSKeyValueObservation?
    var itemStatusObserver: NSKeyValueObservation?
    var playerRateObserver: NSKeyValueObservation?
    var videoPlayerFrameObserver: NSKeyValueObservation?
    var videoPlayerMoveObserver: NSKeyValueObservation?
    var periodicTimeObserver: Any?
    var contentKeySession: AVContentKeySession! // Content Key Session for DRM
    let urlSession = URLSession(configuration: .default)  // URLSession for network requests

    init(url: URL, rate: Float, playerId: String, exitOnEnd: Bool,
         loopOnEnd: Bool, pipEnabled: Bool, showControls: Bool,
         displayMode: String, stUrl: URL?, stLanguage: String?,
         stHeaders: [String: String]?, stOptions: [String: Any]?,
         title: String?, smallTitle: String?, artwork: String?, drm: [String: Any]?) {
        //self._videoPath = videoPath
        self._url = url
        self._stUrl = stUrl
        self._stLanguage = stLanguage
        self._stOptions = stOptions
        self._exitOnEnd = exitOnEnd
        self._loopOnEnd = loopOnEnd
        self._pipEnabled = pipEnabled
        self._videoId = playerId
        self._videoRate = rate
        self._stHeaders = stHeaders
        self._displayMode = displayMode
        self.videoPlayer = AllOrientationAVPlayerController()
        if displayMode == "landscape" {
            self.videoPlayer = LandscapeAVPlayerController()
        }
        if displayMode == "portrait" {
            self.videoPlayer = PortraitAVPlayerController()
        }
        self._showControls = showControls
        self._title = title
        self._smallTitle = smallTitle
        self._artwork = artwork
        self._drm = drm

        if let headers = self._stHeaders {
            self.videoAsset = AVURLAsset(url: url, options: ["AVURLAssetHTTPHeaderFieldsKey": headers])
        } else {
            self.videoAsset = AVURLAsset(url: url)
        }

        self.isPlaying = false
        super.init(frame: .zero)
        if (self._drm != nil) {
            self.setupDRM()
        }
        self.initialize()
        self.addObservers()
    }

    private func setupDRM() {
        if let fairplayDict = self._drm?["fairplay"] as? [String: Any] {
            // Access licenseUri and cast to String
            if let licenseUri = fairplayDict["licenseUri"] as? String {
                self._drmlicenseUri = licenseUri
            }
            // Access certificateUri and cast to String
            if let certificateUri = fairplayDict["certificateUri"] as? String {
                self._drmcertUri = certificateUri
            }
            
            if let drmHeaders = fairplayDict["headers"] as? [String: String] {
                self._drmHeaders = drmHeaders
            }
            
            // Init Content Key Session for DRM
            contentKeySession = AVContentKeySession(keySystem: .fairPlayStreaming)
            contentKeySession?.setDelegate(self, queue: DispatchQueue.main)
            // contentKeySession.setDelegate(self, queue: DispatchQueue(label: "\(Bundle.main.bundleIdentifier!).ContentKeyDelegateQueue"))
            contentKeySession.addContentKeyRecipient(self.videoAsset)
            // videoAsset.resourceLoader.setDelegate(self, queue: DispatchQueue.main)
        }
    }

    // swiftlint:disable function_body_length
    // swiftlint:disable cyclomatic_complexity
    private func initialize() {
        // Set SubTitles if any
        if var subTitleUrl = self._stUrl {
            //check if subtitle is .srt
            if subTitleUrl.pathExtension == "srt" {
                let vttUrl: URL = srtSubtitleToVtt(srtURL: subTitleUrl)
                self._stUrl = vttUrl
                subTitleUrl = vttUrl
            }
            var textStyle: [AVTextStyleRule] = []
            if let opt = self._stOptions {
                textStyle.append(contentsOf: self.setSubTitleStyle(options: opt))
            }

            let subTitleAsset = AVAsset(url: subTitleUrl)
            let composition = AVMutableComposition()

            if let videoTrack = composition.addMutableTrack(
                withMediaType: AVMediaType.video,
                preferredTrackID: Int32(kCMPersistentTrackID_Invalid)) {
                if let audioTrack = composition.addMutableTrack(
                    withMediaType: AVMediaType.audio,
                    preferredTrackID: Int32(kCMPersistentTrackID_Invalid)) {
                    do {
                        try videoTrack.insertTimeRange(
                            CMTimeRangeMake(start: CMTime.zero,
                                            duration: self.videoAsset.duration),
                            of: self.videoAsset.tracks(
                                withMediaType: AVMediaType.video)[0],
                            at: CMTime.zero)
                        // if video has an audio track
                        if self.videoAsset.tracks.count > 0 {
                            let clipAudioTrack = self.videoAsset.tracks(
                                withMediaType: AVMediaType.audio)[0]
                            try audioTrack.insertTimeRange(CMTimeRangeMake(
                                                            start: CMTime.zero,
                                                            duration: self.videoAsset.duration),
                                                           of: clipAudioTrack, at: CMTime.zero)
                        }
                        //Adds subtitle track
                        if let subtitleTrack = composition.addMutableTrack(
                            withMediaType: .text,
                            preferredTrackID: kCMPersistentTrackID_Invalid) {
                            do {
                                let duration = self.videoAsset.duration
                                try subtitleTrack.insertTimeRange(
                                    CMTimeRangeMake(start: CMTime.zero,
                                                    duration: duration),
                                    of: subTitleAsset.tracks(
                                        withMediaType: .text)[0],
                                    at: CMTime.zero)

                                self.playerItem = AVPlayerItem(asset: composition)
                                self.playerItem?.textStyleRules = textStyle

                            } catch {
                                self.playerItem = AVPlayerItem(asset: self.videoAsset)
                            }
                        } else {
                            self.playerItem = AVPlayerItem(asset: self.videoAsset)
                        }
                    } catch {
                        self.playerItem = AVPlayerItem(asset: self.videoAsset)
                    }
                } else {
                    self.playerItem = AVPlayerItem(asset: self.videoAsset)
                }
            } else {
                self.playerItem = AVPlayerItem(asset: self.videoAsset)
            }
        } else {
            self.playerItem = AVPlayerItem(asset: self.videoAsset)
        }
        self.player = AVPlayer(playerItem: self.playerItem)
        self.player?.currentItem?.audioTimePitchAlgorithm = .timeDomain
        if !self._showControls {
            self.videoPlayer.showsPlaybackControls = false
        }
        self.videoPlayer.player = self.player
        self.videoPlayer.updatesNowPlayingInfoCenter = false
        if #available(iOS 13.0, *) {
            self.videoPlayer.isModalInPresentation = true
        } else {
            // Fallback on earlier versions
        }
        self.videoPlayer.allowsPictureInPicturePlayback = false
        if isPIPModeAvailable && self._pipEnabled {
            self.videoPlayer.allowsPictureInPicturePlayback = true
        }

        self._isLoaded.updateValue(false, forKey: self._videoId)

    }
    // swiftlint:enable cyclomatic_complexity
    // swiftlint:enable function_body_length

    public func contentKeySession(_ session: AVContentKeySession, didProvide keyRequest: AVContentKeyRequest) {
        // Extract content identifier and license service URL from the key request
        guard let contentKeyIdentifierString = keyRequest.identifier as? String,
            let contentIdentifier = contentKeyIdentifierString.replacingOccurrences(of: "skd://", with: "") as String?,
            let licenseServiceUrl = contentKeyIdentifierString.replacingOccurrences(of: "skd://", with: "https://") as String?,
            let contentIdentifierData = contentIdentifier.data(using: .utf8)
        else {
            print("ERROR: Failed to retrieve the content identifier from the key request!")
            return
        }
        
        // Completion handler for making streaming content key request
        let handleCkcAndMakeContentAvailable = { [weak self] (spcData: Data?, error: Error?) in
            guard self != nil else { return }
            
            if let error = error {
                print("ERROR: Failed to prepare SPC: \(error.localizedDescription)")
                // Report SPC preparation error to AVFoundation
                keyRequest.processContentKeyResponseError(error)
                return
            }
            
            guard let spcData = spcData else { return }
            
            // Send SPC to the license service to obtain CKC
            guard let url = URL(string: licenseServiceUrl) else {
                print("ERROR: Missing license service URL!")
                return
            }
            
            print("*** DRM:", url)
        
            var licenseRequest = URLRequest(url: URL(string: (self?._drmlicenseUri)!)!)
            licenseRequest.httpMethod = "POST"

            // Set additional headers for the license service request
            if let drmHeaders = self?._drmHeaders {
                for (key, value) in drmHeaders {
//                    print("*** DRM: Key: \(key), Value: \(value)")
                    licenseRequest.setValue(value, forHTTPHeaderField: key)
                }
            }

            licenseRequest.httpBody = spcData
            
            var dataTask: URLSessionDataTask?
            
            dataTask = self!.urlSession.dataTask(with: licenseRequest, completionHandler: { (data, response, error) in
                defer {
                    dataTask = nil
                }

                if let error = error {
                    print("ERROR: Failed to get CKC: \(error.localizedDescription)")
                } else if
                    let ckcData = data,
                    let response = response as? HTTPURLResponse,
                    response.statusCode == 200 {
                    // Create AVContentKeyResponse from CKC data
                    let keyResponse = AVContentKeyResponse(fairPlayStreamingKeyResponseData: ckcData)
                    // Provide the content key response to make protected content available for processing
                    keyRequest.processContentKeyResponse(keyResponse)
                }
            })
            
            dataTask?.resume()
        }
          
        do {
            // Request the application certificate for the content key request
            let applicationCertificate = try requestApplicationCertificate()
            
            // Make the streaming content key request with the specified options
            keyRequest.makeStreamingContentKeyRequestData(
                forApp: applicationCertificate,
                contentIdentifier: contentIdentifierData,
                options: [AVContentKeyRequestProtocolVersionsKey: [1]],
                completionHandler: handleCkcAndMakeContentAvailable
            )
        } catch {
            // Report error in processing content key response
            keyRequest.processContentKeyResponseError(error)
        }
    }

    /*
        Requests the Application Certificate.
    */
    func requestApplicationCertificate() throws -> Data {
        var applicationCertificate: Data? = nil
        
        do {
            // Load the FairPlay application certificate from the specified URL.
            applicationCertificate = try Data(contentsOf: URL(string: self._drmcertUri!)!)
        } catch {
            // Handle any errors that occur while loading the certificate.
            let errorMessage = "Failed to load the FairPlay application certificate. Error: \(error)"
            print(errorMessage)
            throw error
        }
        
        // Return the loaded application certificate.
        return applicationCertificate!
    }
    
    private func setSubTitleStyle(options: [String: Any]) -> [AVTextStyleRule] {
        var styles: [AVTextStyleRule] = []
        var backColor: [Float] = [1.0, 0.0, 0.0, 0.0]
        if let bckCol = options["backgroundColor"] as? String {
            let color = self.getColorFromRGBA(rgba: bckCol)
            backColor = color.count > 0 ? color : backColor
        }
        if let textStyle: AVTextStyleRule = AVTextStyleRule(textMarkupAttributes: [
            kCMTextMarkupAttribute_CharacterBackgroundColorARGB as String:
                backColor
        ]) {
            styles.append(textStyle)
        }

        var foreColor: [Float] = [1.0, 1.0, 1.0, 1.0]
        if let foreCol = options["foregroundColor"] as? String {
            let color = self.getColorFromRGBA(rgba: foreCol)
            foreColor = color.count > 0 ? color : foreColor
        }
        if let textStyle1: AVTextStyleRule = AVTextStyleRule(textMarkupAttributes: [
            kCMTextMarkupAttribute_ForegroundColorARGB as String: foreColor
        ]) {
            styles.append(textStyle1)
        }
        var ftSize = 160
        if let pixSize = options["fontSize"] as? Int {
            ftSize = pixSize * 10
        }
        if let textStyle2: AVTextStyleRule = AVTextStyleRule(textMarkupAttributes: [
            kCMTextMarkupAttribute_RelativeFontSize as String: ftSize,
            kCMTextMarkupAttribute_CharacterEdgeStyle as String: kCMTextMarkupCharacterEdgeStyle_None
        ]) {
            styles.append(textStyle2)
        }
        return styles
    }
    // MARK: - Add Observers

    // swiftlint:disable function_body_length
    // swiftlint:disable cyclomatic_complexity
    private func addObservers() {

        self.itemStatusObserver = self.playerItem?
            .observe(\.status, options: [.new, .old],
                     changeHandler: {(playerItem, _) in
                        // Switch over the status
                        switch playerItem.status {
                        case .readyToPlay:
                            // Player item is ready to play.
                            if self._firstReadyToPlay {
                                self._isLoaded.updateValue(true, forKey: self._videoId)
                                self._isReadyToPlay = true
                                isVideoEnded = false
                                if let item = self.playerItem {
                                    self._currentTime = CMTimeGetSeconds(item.currentTime())
                                }
                                let vId: [String: Any] = ["fromPlayerId": self._videoId, "currentTime": self._currentTime,
                                                          "videoRate": self._videoRate]
                                NotificationCenter.default.post(name: .playerItemReady, object: nil, userInfo: vId)
                                self._firstReadyToPlay = false
                                
                                self.setNowPlayingInfo()
                                self.setRemoteCommandCenter()
                                self.setNowPlayingImage()
                            }
                        case .failed:
                            print("failing to load")
                            self._isLoaded.updateValue(false, forKey: self._videoId)
                        case .unknown:
                            // Player item is not yet ready.
                            print("playerItem not yet ready")

                        @unknown default:
                            print("playerItem Error \(String(describing: self.playerItem?.error))")
                        }

                     })

        self.itemBufferObserver = self.playerItem?
            .observe(\.isPlaybackBufferEmpty,
                     options: [.new, .old], changeHandler: {(playerItem, _) in
                        let empty: Bool = ((self.playerItem?.isPlaybackBufferEmpty) != nil)
                        if empty {
                            self._isBufferEmpty.updateValue(true, forKey: self._videoId)
                        } else {
                            self._isBufferEmpty.updateValue(false, forKey: self._videoId)
                        }
                     })
        self.playerRateObserver = self.player?
            .observe(\.rate, options: [.new, .old], changeHandler: {(player, _) in
                let rate: Float = player.rate
                if let item = self.playerItem {
                    self._currentTime = CMTimeGetSeconds(item.currentTime())
                    self._duration = CMTimeGetSeconds(item.duration)
                }
                let vId: [String: Any] = [
                    "fromPlayerId": self._videoId,
                    "currentTime": self._currentTime,
                    "videoRate": self._videoRate
                ]

                if !(self._isLoaded[self._videoId] ?? true) {
                    print("AVPlayer Rate for player \(self._videoId): Loading")
                } else if rate > 0 && self._isReadyToPlay {
                    if rate != self._videoRate {
                        player.rate = self._videoRate
                    }

                    self.isPlaying = true
                    NotificationCenter.default.post(name: .playerItemPlay, object: nil, userInfo: vId)
                } else if rate == 0 && !isVideoEnded && abs(self._currentTime - self._duration) < 0.2 {
                    self.isPlaying = false
                    player.seek(to: CMTime.zero)
                    self._currentTime = 0
                    if /*!isInPIPMode && */self._exitOnEnd {
                        isVideoEnded = true
                        NotificationCenter.default.post(name: .playerItemEnd, object: nil, userInfo: vId)
                    } else {
                        if self._loopOnEnd {
                            self.play()
                        }
                    }
                } else if rate == 0 {
                    if !isInPIPMode && !isInBackgroundMode && !isRateZero {
                        self.isPlaying = false
                        if !self.videoPlayer.isBeingDismissed {
                            print("AVPlayer Rate for player \(self._videoId): Paused")
                            NotificationCenter.default.post(name: .playerItemPause, object: nil, userInfo: vId)
                        }
                    } else {
                        isRateZero = true
                    }
                } else if self._isBufferEmpty[self._videoId] ?? true {
                    print("AVPlayer Rate for player \(self._videoId): Buffer Empty Loading")
                }
            })
        self.videoPlayerFrameObserver = self.videoPlayer
            .observe(\.view.frame, options: [.new, .old],
                     changeHandler: {(_, _) in
                        if !isInPIPMode {
                            if self.videoPlayer.isBeingDismissed && !isVideoEnded {
                                NotificationCenter.default.post(name: .playerFullscreenDismiss, object: nil)
                            }
                        }

                     })
        self.videoPlayerMoveObserver = self.videoPlayer
            .observe(\.view.center, options: [.new, .old],
                     changeHandler: {(_, _) in
                        if !isInPIPMode {
                            if self.videoPlayer.isBeingDismissed && !isVideoEnded {
                                NotificationCenter.default.post(name: .playerFullscreenDismiss, object: nil)
                            }

                        }

                     })
    }

    // swiftlint:enable function_body_length
    // swiftlint:enable cyclomatic_complexity

    // MARK: - Remove Observers

    func removeObservers() {
        self.itemStatusObserver?.invalidate()
        self.itemBufferObserver?.invalidate()
        self.playerRateObserver?.invalidate()
        self.videoPlayerFrameObserver?.invalidate()
    }

    // MARK: - Required init

    public required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    // MARK: - Set-up Public functions

    @objc func play() {
        self.isPlaying = true
        self.player?.play()
        self.player?.rate = _videoRate
    }
    @objc func pause() {
        self.isPlaying = false
        self.player?.pause()
    }
    @objc func didFinishPlaying() -> Bool {
        return isVideoEnded
    }
    @objc func getDuration() -> Double {
        return Double(CMTimeGetSeconds(self.videoAsset.duration))
    }
    @objc func getCurrentTime() -> Double {
        return self._currentTime
    }
    // This func will return the updated currentTime of player item
    // getCurrentTime() is only updated when player plays, pauses, seek, etc
    // the function is only used in playerFullscreenDismiss() Notification
    public func getRealCurrentTime() -> Double {
        if let item = self.playerItem {
            let currentTime = CMTimeGetSeconds(item.currentTime())
            return currentTime
        } else {
            return 0
        }
    }
    @objc func setCurrentTime(time: Double) {
        let seekTime: CMTime = CMTimeMake(value: Int64(time*1000), timescale: 1000)
        self.player?.seek(to: seekTime)
        self._currentTime = time
    }
    @objc func getVolume() -> Float {
        if let player = self.player {
            return player.volume
        } else {
            return 1.0
        }
    }
    @objc func setVolume(volume: Float) {
        self.player?.volume = volume
    }
    @objc func getRate() -> Float {
        return _videoRate
    }

    @objc func setRate(rate: Float) {
        _videoRate = rate
    }
    @objc func getMuted() -> Bool {
        return ((self.player?.isMuted) != nil)
    }
    @objc func setMuted(muted: Bool) {
        self.player?.isMuted = muted
    }

    private func getColorFromRGBA(rgba: String) -> [Float] {
        if let oPar = rgba.firstIndex(of: "(") {
            if let cPar = rgba.firstIndex(of: ")") {
                let strColor = rgba[rgba.index(after: oPar)..<cPar]
                let array = strColor.components(separatedBy: ",")
                if array.count == 4 {
                    var retArray: [Float] = []
                    retArray.append((array[3]
                                        .trimmingCharacters(in: .whitespaces) as NSString)
                                        .floatValue)
                    retArray.append((array[0]
                                        .trimmingCharacters(in: .whitespaces) as NSString)
                                        .floatValue / 255)
                    retArray.append((array[1]
                                        .trimmingCharacters(in: .whitespaces) as NSString)
                                        .floatValue / 255)
                    retArray.append((array[2]
                                        .trimmingCharacters(in: .whitespaces) as NSString)
                                        .floatValue / 255)
                    return retArray
                } else {
                    return []
                }
            } else {
                return []
            }
        } else {
            return []
        }
    }

    private func srtSubtitleToVtt(srtURL: URL) -> URL {
        guard let cachesURL = FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask).first else {
            fatalError("Couldn't get caches directory")
        }
        let vttFileName = UUID().uuidString + ".vtt"
        let vttURL = cachesURL.appendingPathComponent(vttFileName)
        let session = URLSession(configuration: .default)
        let vttFolderURL = vttURL.deletingLastPathComponent()
        do {
            try FileManager.default.createDirectory(at: vttFolderURL, withIntermediateDirectories: true, attributes: nil)
        } catch let error {
            print("Creating folder error: ", error)
        }
        let task = session.dataTask(with: srtURL) { (data, _, error) in
            guard let data = data, error == nil else {
                print("Download failed: \(error?.localizedDescription ?? "ukn")")
                return
            }
            do {
                let tempSRTURL = URL(fileURLWithPath: NSTemporaryDirectory()).appendingPathComponent("subtitulos.srt")
                try data.write(to: tempSRTURL)
                let srtContent = try String(contentsOf: tempSRTURL, encoding: .utf8)
                let vttContent = srtContent.replacingOccurrences(of: ",", with: ".")
                let vttString = "WEBVTT\n\n" + vttContent
                try vttString.write(toFile: vttURL.path, atomically: true, encoding: .utf8)
                try FileManager.default.removeItem(at: tempSRTURL)

            } catch let error {
                print("Processing subs error: \(error)")
                exit(1)
            }

        }

        task.resume()
        return vttURL
    }
    
    func setRemoteCommandCenter() {
        let rcc = MPRemoteCommandCenter.shared()
        
        rcc.playCommand.isEnabled = true
        rcc.playCommand.addTarget {event in
            self.play()
            return .success
        }
        rcc.pauseCommand.isEnabled = true
        rcc.pauseCommand.addTarget {event in
            self.pause()
            return .success
        }
        rcc.changePlaybackPositionCommand.isEnabled = true
        rcc.changePlaybackPositionCommand.addTarget {event in
            let seconds = (event as? MPChangePlaybackPositionCommandEvent)?.positionTime ?? 0
            let time = CMTime(seconds: seconds, preferredTimescale: 1)
            self.player?.seek(to: time)
            return .success
        }
        rcc.skipForwardCommand.isEnabled = true
        rcc.skipForwardCommand.addTarget {event in
            if let player = self.player, let currentItem = player.currentItem {
                let currentTime = CMTimeGetSeconds(currentItem.currentTime()) + 10
                self.player?.seek(to: CMTimeMakeWithSeconds(currentTime, preferredTimescale: 1))
                return .success
            } else {
                return .commandFailed
            }
        }
        rcc.skipBackwardCommand.isEnabled = true
        rcc.skipBackwardCommand.addTarget {event in
            if let player = self.player, let currentItem = player.currentItem {
                let currentTime = CMTimeGetSeconds(currentItem.currentTime()) - 10
                self.player?.seek(to: CMTimeMakeWithSeconds(currentTime, preferredTimescale: 1))
                return .success
            } else {
                return .commandFailed
            }
        }
        
        // Next and previous track buttons are disabled because we don't have more than 1 video
        rcc.nextTrackCommand.isEnabled = false
        rcc.previousTrackCommand.isEnabled = false
    }
    
    func setNowPlayingImage() {
        if let artwork = self._artwork {
            let session = URLSession(configuration: .default)
            let image = URL(string: artwork)!
            let task = session.dataTask(with: image) { (data, response, error) in
                guard let imageData = data, error == nil else {
                    print("Error while downloading the image: \(error?.localizedDescription ?? "")")
                    return
                }
                
                let image = UIImage(data: imageData)
                DispatchQueue.main.async {
                    var nowPlayingInfo = MPNowPlayingInfoCenter.default().nowPlayingInfo ?? [String: Any]()
                    nowPlayingInfo[MPMediaItemPropertyArtwork] = MPMediaItemArtwork(boundsSize: image?.size ?? CGSize.zero, requestHandler: { _ in
                        return image ?? UIImage()
                    })
                    MPNowPlayingInfoCenter.default().nowPlayingInfo = nowPlayingInfo
                }
            }
            task.resume()
        }
    }
    
    func setNowPlayingInfo() {
        var nowPlayingInfo = [String: Any]()
        
        if let title = self._title {
            nowPlayingInfo[MPMediaItemPropertyTitle] = title
        }
        if let smalltitle = self._smallTitle {
            nowPlayingInfo[MPMediaItemPropertyArtist] = smalltitle
        }
        
        nowPlayingInfo[MPNowPlayingInfoPropertyMediaType] = NSNumber(value: MPNowPlayingInfoMediaType.video.rawValue)
        MPNowPlayingInfoCenter.default().nowPlayingInfo = nowPlayingInfo
        UIApplication.shared.beginReceivingRemoteControlEvents()
        periodicTimeObserver = self.player?.addPeriodicTimeObserver(forInterval: CMTimeMake(value: 1, timescale: 1), queue: DispatchQueue.main) { [weak self] time in
            guard let self = self else { return }
            
            var nowPlayingInfo = MPNowPlayingInfoCenter.default().nowPlayingInfo ?? [String: Any]()
            if let currentItem = self.player?.currentItem,
               let currentTime = self.player?.currentTime(),
               currentItem.status == .readyToPlay {
                
                let elapsedTime = CMTimeGetSeconds(currentTime)
                if currentItem.isPlaybackLikelyToKeepUp {
                    nowPlayingInfo[MPNowPlayingInfoPropertyPlaybackRate] = self.player?.rate
                } else {
                    nowPlayingInfo[MPNowPlayingInfoPropertyPlaybackRate] = 0
                }
                
                nowPlayingInfo[MPNowPlayingInfoPropertyElapsedPlaybackTime] = Float(elapsedTime)
                nowPlayingInfo[MPMediaItemPropertyPlaybackDuration] = currentItem.duration.seconds
                
                MPNowPlayingInfoCenter.default().nowPlayingInfo = nowPlayingInfo
            }
        }
    }
}

// swiftlint:enable type_body_length
// swiftlint:enable file_length
