//
//  FullScreenVideoPlayerView.swift
//  Plugin
//
//  Created by  Quéau Jean Pierre on 13/01/2020.
//  Copyright © 2020 Max Lynch. All rights reserved.
//

import UIKit
import AVKit

class FullScreenVideoPlayerView: UIView {
    private var _url: URL
    private var _isReadyToPlay: Bool = false
    private var _videoId: String = "fullscreen"
    private var _currentTime: Double = 0
    private var _duration: Double = 0
    private var _isLoaded: [String: Bool] = [:]
    private var _isBufferEmpty: [String: Bool] = [:]
    private var _isEnded: Bool = false
    private var _exitOnEnd: Bool = true

    var player: AVPlayer
    var videoPlayer: AVPlayerViewController
    var asset: AVAsset
    var playerItem: AVPlayerItem
    var isPlaying: Bool
    var itemBufferObserver: NSKeyValueObservation?
    var itemStatusObserver: NSKeyValueObservation?
    var playerRateObserver: NSKeyValueObservation?
    var videoPlayerFrameObserver: NSKeyValueObservation?

    init(url: URL, playerId: String, exitOnEnd: Bool) {
        self._url = url
        self._exitOnEnd = exitOnEnd
        self._videoId = playerId
        self.videoPlayer = AVPlayerViewController()
        self.asset = AVAsset(url: url)
        self.playerItem = AVPlayerItem(asset: self.asset)
        self.player = AVPlayer(playerItem: playerItem)
        self.videoPlayer.player = self.player
        self._isLoaded.updateValue(false, forKey: self._videoId)
        self.isPlaying = false

        super.init(frame: .zero)

        self.addObservers()
    }

    // MARK: - Add Observers

    // swiftlint:disable function_body_length
    // swiftlint:disable cyclomatic_complexity
    private func addObservers() {

        self.itemStatusObserver = self.playerItem.observe(\.status, options: [.new, .old],
                                                          changeHandler: {(playerItem, _) in
            // Switch over the status
            switch playerItem.status {
            case .readyToPlay:
            // Player item is ready to play.
                self._isLoaded.updateValue(true, forKey: self._videoId)
                self._isReadyToPlay = true
                self._isEnded = false
                self._currentTime = CMTimeGetSeconds(self.playerItem.currentTime())

                let vId: [String: Any] = ["fromPlayerId": self._videoId, "currentTime": self._currentTime ]
                NotificationCenter.default.post(name: .playerItemReady, object: nil, userInfo: vId)
            case .failed:
                print("failing to load")
                self._isLoaded.updateValue(false, forKey: self._videoId)
            case .unknown:
                // Player item is not yet ready.
                print("playerItem not yet ready")

            @unknown default:
                print("playerItem Error \(String(describing: self.playerItem.error))")
            }

        })

        self.itemBufferObserver = self.playerItem.observe(\.isPlaybackBufferEmpty, options: [.new, .old],
                                                          changeHandler: {(playerItem, _) in
            let empty: Bool = self.playerItem.isPlaybackBufferEmpty
            if empty {
                self._isBufferEmpty.updateValue(true, forKey: self._videoId)
            } else {
                self._isBufferEmpty.updateValue(false, forKey: self._videoId)
            }
        })
        self.playerRateObserver = self.player.observe(\.rate, options: [.new, .old],
                                                          changeHandler: {(player, _) in
            let rate: Float = player.rate
            self._currentTime = CMTimeGetSeconds(self.playerItem.currentTime())
            self._duration = CMTimeGetSeconds(self.playerItem.duration)
            let vId: [String: Any] = ["fromPlayerId": self._videoId, "currentTime": self._currentTime]
            if !(self._isLoaded[self._videoId] ?? true) {
                print("AVPlayer Rate for player \(self._videoId): Loading")
            } else if rate == 1.0 && self._isReadyToPlay {
                print("AVPlayer Rate for player \(self._videoId): Playing")
                self.isPlaying = true
                NotificationCenter.default.post(name: .playerItemPlay, object: nil, userInfo: vId)
            } else if rate == 0 && !self._isEnded && abs(self._currentTime - self._duration) < 0.2 {
                print("AVPlayer Rate for player \(self._videoId): Ended")
                self._isEnded = true
                self.isPlaying = false
                if self._exitOnEnd {
                    NotificationCenter.default.post(name: .playerItemEnd, object: nil, userInfo: vId)
                }
            } else if rate == 0 {
                print("AVPlayer Rate for player \(self._videoId): Paused")
                self.isPlaying = false
                NotificationCenter.default.post(name: .playerItemPause, object: nil, userInfo: vId)
            } else if self._isBufferEmpty[self._videoId] ?? true {
                print("AVPlayer Rate for player \(self._videoId): Buffer Empty Loading")
            }
        })
        self.videoPlayerFrameObserver = self.videoPlayer.observe(\.view.frame, options: [.new, .old],
                                                                 changeHandler: {(videoPlayer, _) in
            if self.videoPlayer.isBeingDismissed && !self._isEnded {
                if !self.isPlaying {
                    self.pause()
                }
                NotificationCenter.default.post(name: .playerFullscreenDismiss, object: nil)
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

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    // MARK: - Set-up Public functions

    @objc func play() {
        self.isPlaying = true
        self.player.play()
    }
    @objc func pause() {
        self.isPlaying = false
        self.player.pause()
    }
    @objc func didFinishPlaying() -> Bool {
        return self._isEnded
    }
    @objc func getDuration() -> Double {
        return Double(CMTimeGetSeconds(self.playerItem.duration))
    }
    @objc func getCurrentTime() -> Double {
        return self._currentTime
    }
    @objc func setCurrentTime(time: Double) {
        let seekTime: CMTime = CMTimeMake(value: Int64(time*1000), timescale: 1000)
        self.player.seek(to: seekTime)
        self._currentTime = time
    }
    @objc func getVolume() -> Float {
        return self.player.volume
    }
    @objc func setVolume(volume: Float) {
        self.player.volume = volume
    }
    @objc func getMuted() -> Bool {
        return self.player.isMuted
    }
    @objc func setMuted(muted: Bool) {
        self.player.isMuted = muted
    }

}
