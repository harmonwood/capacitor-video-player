//
//  FullScreenVideoPlayerView.swift
//  Plugin
//
//  Created by  Quéau Jean Pierre on 13/01/2020.
//  Copyright © 2021 Max Lynch. All rights reserved.
//

import UIKit
import AVKit

// swiftlint:disable type_body_length
open class FullScreenVideoPlayerView: UIView {
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
    private var _stOptions: [String: Any]?
    private var _videoRate: Float

    var player: AVPlayer?
    var videoPlayer: AVPlayerViewController
    var videoAsset: AVURLAsset
    var playerItem: AVPlayerItem?
    var isPlaying: Bool
    var itemBufferObserver: NSKeyValueObservation?
    var itemStatusObserver: NSKeyValueObservation?
    var playerRateObserver: NSKeyValueObservation?
    var videoPlayerFrameObserver: NSKeyValueObservation?

    init(url: URL, rate: Float, playerId: String, exitOnEnd: Bool,
         loopOnEnd: Bool, pipEnabled: Bool, stUrl: URL?,
         stLanguage: String?, stOptions: [String: Any]?) {
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
        self.videoPlayer = AVPlayerViewController()
        self.videoAsset = AVURLAsset(url: url)
        self.isPlaying = false
        super.init(frame: .zero)
        self.initialize()
        self.addObservers()
    }

    // swiftlint:disable function_body_length
    private func initialize() {
        // Set SubTitles if any
        if let subTitleUrl = self._stUrl {
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
        self.player = AVPlayer(playerItem: playerItem)
        self.player?.currentItem?.audioTimePitchAlgorithm = .timeDomain
        self.videoPlayer.player = self.player
        self.videoPlayer.allowsPictureInPicturePlayback = false
        if isPIPModeAvailable && self._pipEnabled {
            self.videoPlayer.allowsPictureInPicturePlayback = true
        }

        self._isLoaded.updateValue(false, forKey: self._videoId)

    }
    // swiftlint:enable function_body_length

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

}

// swiftlint:enable type_body_length
