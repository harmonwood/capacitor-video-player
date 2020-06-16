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
    private var _videoPath: String
    private var _isReadyToPlay: Bool = false
    private var _videoId: String = "fullscreen"
    private var _currentTime: Double = 0
    private var _duration: Double = 0
    private var _isLoaded: [String:Bool] = [:]
    private var _isBufferEmpty: [String:Bool] = [:]
    private var _isEnded: Bool = false
    private var _exitOnEnd: Bool = true

    
    var player: AVPlayer!
    var videoPlayer: AVPlayerViewController
    var asset: AVAsset!
    var playerItem: AVPlayerItem!
    var isPlaying: Bool!


    init(videoPath: String,playerId: String,exitOnEnd: Bool) {
        self._videoPath = videoPath
        self._exitOnEnd = exitOnEnd
        self._videoId = playerId
        self.videoPlayer = AVPlayerViewController()
        let url = URL(string: self._videoPath)
        self.asset = AVAsset(url:url!)
        self.playerItem = AVPlayerItem(asset:asset)
        self.player = AVPlayer(playerItem: playerItem)
        self.videoPlayer.player = self.player
        self._isLoaded.updateValue(false, forKey: self._videoId)

//        initializePlayer()
        super.init(frame: .zero)
        self.addObservers()

    }

    // MARK: - Init Player

    private func initializePlayer() {
        self.videoPlayer = AVPlayerViewController()
        let url = URL(string: self._videoPath)
        self.asset = AVAsset(url:url!)
        self.playerItem = AVPlayerItem(asset:asset)
        self.player = AVPlayer(playerItem: playerItem)
        self.videoPlayer.player = self.player
        self._isLoaded.updateValue(false, forKey: self._videoId)
        self.addObservers()
    }
    private func addObservers() {
        self.playerItem.addObserver(self,
                               forKeyPath: #keyPath(AVPlayerItem.status),
                               options: [.old, .new],
                               context: nil)
        self.player.addObserver(self, forKeyPath: #keyPath(AVPlayer.rate),
                                options: [.old, .new],
                                context: nil)
        self.videoPlayer.addObserver(self, forKeyPath:#keyPath(UIViewController.view.frame), options: [.old, .new], context: nil)
        self.playerItem.addObserver(self,
                forKeyPath: #keyPath(AVPlayerItem.isPlaybackBufferEmpty),
                options: [.old, .new],
                context: nil)
    }
    func removeObservers() {
        self.playerItem.removeObserver(self,
        forKeyPath: #keyPath(AVPlayerItem.status))
        self.player.removeObserver(self, forKeyPath: #keyPath(AVPlayer.rate))
        self.videoPlayer.removeObserver(self, forKeyPath:#keyPath(UIViewController.view.frame))
        self.playerItem.removeObserver(self,
        forKeyPath: #keyPath(AVPlayerItem.isPlaybackBufferEmpty))
    }
    
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        let obj = object as! NSObject
        if obj == self.videoPlayer {
            if keyPath == #keyPath(UIViewController.view.frame)  {
                if self.videoPlayer.isBeingDismissed && !self._isEnded {
                    if !self.isPlaying {
                        self.pause()
                    }
                    NotificationCenter.default.post(name: .playerFullscreenDismiss, object: nil)
                }
            }
        } else if obj == self.playerItem {
            if keyPath == #keyPath(AVPlayerItem.status) {
                let status: AVPlayerItem.Status
                
                // Get the status change from the change dictionary
                if let statusNumber = change?[.newKey] as? NSNumber {
                    status = AVPlayerItem.Status(rawValue: statusNumber.intValue)!
                } else {
                    status = .unknown
                }
                
                // Switch over the status
                switch status {
                case .readyToPlay:
                // Player item is ready to play.
                    self._isLoaded.updateValue(true, forKey: self._videoId)
                    self._isReadyToPlay = true
                    self._isEnded = false
                    self._currentTime = CMTimeGetSeconds(self.playerItem.currentTime())

                    let vId: [String:Any] = ["fromPlayerId": self._videoId,"currentTime": self._currentTime ]
                    NotificationCenter.default.post(name: .playerItemReady, object: nil, userInfo: vId)
                case .failed:
                    print("in failed")
                    self._isLoaded.updateValue(false, forKey: self._videoId)
                case .unknown:
                    // Player item is not yet ready.
                    print("playerItem not yet ready")
                    
                @unknown default:
                    print("playerItem Error \(String(describing: self.playerItem.error))")
                }
            }  else if keyPath == #keyPath(AVPlayerItem.isPlaybackBufferEmpty) {
                let empty : Bool? = self.playerItem.isPlaybackBufferEmpty
                if empty! {
                    self._isBufferEmpty.updateValue(true, forKey: self._videoId)
                } else {
                    self._isBufferEmpty.updateValue(false, forKey: self._videoId)
                }
            }
        } else if obj == self.player {

            if keyPath == #keyPath(AVPlayer.rate) {
                let rate: Float = player.rate
                self._currentTime = CMTimeGetSeconds(self.playerItem.currentTime())
                self._duration = CMTimeGetSeconds(self.playerItem.duration)
                let vId: [String:Any] = ["fromPlayerId": self._videoId,"currentTime":self._currentTime]
                if !self._isLoaded[self._videoId]! {
                    print("AVPlayer Rate for player \(self._videoId): Loading")
                } else if rate == 1.0 && self._isReadyToPlay {
                     print("AVPlayer Rate for player \(self._videoId): Playing")
                     self.isPlaying = true
                     NotificationCenter.default.post(name: .playerItemPlay, object: nil, userInfo: vId)
                } else if rate == 0 && !self._isEnded && abs(self._currentTime - self._duration) < 0.2 {
                    print("AVPlayer Rate for player \(self._videoId): Ended")
                    self._isEnded = true
                    self.isPlaying = false
                    if(_exitOnEnd) {
                        NotificationCenter.default.post(name: .playerItemEnd, object: nil, userInfo: vId)
                    }
                } else if rate == 0 {
                    print("AVPlayer Rate for player \(self._videoId): Paused")
                    self.isPlaying = false
                    NotificationCenter.default.post(name: .playerItemPause, object: nil, userInfo: vId)
                } else if self._isBufferEmpty[self._videoId]! {
                    print("AVPlayer Rate for player \(self._videoId): Buffer Empty Loading")
                }
            }
        }
    }
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
        self.player.seek(to:seekTime)
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
