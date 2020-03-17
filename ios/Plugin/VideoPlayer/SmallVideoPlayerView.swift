//
//  SmallVideoPlayerView.swift
//  Plugin
//
//  Created by  Quéau Jean Pierre on 13/01/2020.
//  Copyright © 2020 Max Lynch. All rights reserved.
//

import UIKit

import AVFoundation

class SmallVideoPlayerView: UIView {
    private var _togglePlay: Bool = false
    private var _videoId: String?
    private var _isLoaded: [String:Bool] = [:]
    private var _isBufferEmpty: [String:Bool] = [:]
    private var _fromInternal: Bool = false
    private var _isPlaying: Bool = false
    private var _currentTime: Double = 0


    override final class var layerClass: AnyClass {
        return AVPlayerLayer.self
    }
    override var layer: AVPlayerLayer {
        return super.layer as! AVPlayerLayer
    }

    var playerLayer : AVPlayerLayer {
         return layer
    }
    // MARK: - Set-up AVPlayer
    
    var player: AVPlayer? {
        get {
            return playerLayer.player
        }
        set {
            playerLayer.player = newValue

        }
    }
    
    // MARK: - Set-up videoId
    
    var videoId: String? {
        get {
            return self._videoId
        }
        set {
            self._videoId = newValue
        }
    }
    
    // MARK: - Destroy Player
    
    func destroyPlayer() {
        self.player!.seek(to:CMTime.zero)
        self.removeObservers()
        self._currentTime = 0
        self._fromInternal = false
        self._isLoaded = [:]
        self._isBufferEmpty = [:]
        self._togglePlay = false
        self._isPlaying = false
        self.player = nil
        self.playerLayer.removeFromSuperlayer()
        self.layer.removeFromSuperlayer()
    }
    
    // MARK: - Add Observers
    
    func addObservers() {
        player!.addObserver(self, forKeyPath: "rate", options: NSKeyValueObservingOptions(rawValue: NSKeyValueObservingOptions.new.rawValue | NSKeyValueObservingOptions.old.rawValue), context: nil)
        player!.currentItem?.addObserver(self, forKeyPath: "status", options: NSKeyValueObservingOptions(rawValue: NSKeyValueObservingOptions.new.rawValue | NSKeyValueObservingOptions.old.rawValue), context: nil)
        player!.currentItem?.addObserver(self, forKeyPath: "playbackBufferEmpty", options: NSKeyValueObservingOptions(rawValue: NSKeyValueObservingOptions.new.rawValue | NSKeyValueObservingOptions.old.rawValue), context: nil)
    }
    func removeObservers() {
        player!.removeObserver(self, forKeyPath: "rate")
        player!.currentItem?.removeObserver(self, forKeyPath: "playbackBufferEmpty")
        player!.currentItem?.removeObserver(self, forKeyPath: "status")

    }
    
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        let obj = object as! NSObject
        if  (obj == self.player!) {
            self._currentTime = CMTimeGetSeconds(self.player!.currentItem!.currentTime())
            let duration = CMTimeGetSeconds(self.player!.currentItem!.duration)
             if keyPath == "rate" {
                let rate: Float = player!.rate
                 let vId: [String:Any] = ["fromPlayerId": self._videoId!,"currentTime":self._currentTime,"fromInternal":self._fromInternal ]
                if !self._isLoaded[self._videoId!]! {
                    print("AVPlayer Rate for player \(self._videoId!): Loading")
                } else if rate == 1.0 {
                    print("AVPlayer Rate for player \(self._videoId!): Playing")
                    self._isPlaying = true
                    self._togglePlay = true
                    NotificationCenter.default.post(name: .playerItemPlay, object: nil, userInfo: vId)
                    self._fromInternal = false
                } else {
                    if self._isBufferEmpty[self._videoId!]! {
                        print("AVPlayer Rate for player \(self._videoId!): Loading")
                    } else {
                        print("AVPlayer Rate for player \(self._videoId!): Paused")
                        self._isPlaying = false
                        self._togglePlay = false
                        if self._currentTime < duration {
                            NotificationCenter.default.post(name: .playerItemPause, object: nil, userInfo: vId)
                            self._fromInternal = false
                        }
                    }
                }
            }
        } else if (obj == self.player!.currentItem) {
            if keyPath == "status" {

                let vId: [String:Any] = ["fromPlayerId": self._videoId!,"currentTime": self._currentTime, "fromInternal":self._fromInternal ]
                let status : AVPlayerItem.Status? = self.player!.currentItem?.status
                if status == AVPlayerItem.Status.failed {
                    self._isLoaded.updateValue(false, forKey: self._videoId!)

                } else if status == AVPlayerItem.Status.readyToPlay  {
                    self._isLoaded.updateValue(true, forKey: self._videoId!)
                    NotificationCenter.default.post(name: .playerItemReady, object: nil, userInfo: vId)
                }
            } else if keyPath == "playbackBufferEmpty" {
                let empty : Bool? = self.player!.currentItem?.isPlaybackBufferEmpty
                if empty! {
                    self._isBufferEmpty.updateValue(true, forKey: self._videoId!)
                } else {
                    self._isBufferEmpty.updateValue(false, forKey: self._videoId!)
                }

            }
            
        }
     }

    // MARK: - Gesture functions
    
    @objc func wasTapped() {
        self._fromInternal = true
        if(!self._togglePlay) {
            self.play()
        } else {
            self.pause()
        }

    }
    /* Not used replace with was Swipe
    @objc func wasDoubleTapped() {
        guard let duration = self.player!.currentItem?.duration.seconds else {return}
        guard let currentTime = self.player!.currentItem?.currentTime().seconds else {return}
        let step = duration / 10
        let newTime = currentTime + step
        print("in doubleTapped \(currentTime) new time: \(newTime)")
        if newTime < (duration - step) {
            self.setCurrentTime(time: Float(newTime))
        } else {
            self.setCurrentTime(time: Float(duration))
        }
     
     }
     */

    @objc func wasSwipe(direction:UISwipeGestureRecognizer.Direction) {

         if self._isPlaying {
            self._fromInternal = true
            self.pause()
        }
 
        let duration:Double = Double(CMTimeGetSeconds(self.playerLayer.player!.currentItem!.duration))
        let currentTime:Double = Double(CMTimeGetSeconds((self.playerLayer.player!.currentItem?.currentTime())!))
        var step:Double  = duration / 10
        if step < 10 { step = 10}
        var newTime: Double
        if direction == .right {
            newTime = currentTime + step
            if newTime > duration {
                newTime = duration - 1
            }
            self.setCurrentTime(time: newTime)
        }
        if direction == .left {
            newTime = currentTime - step
            if newTime < 0 {
                newTime = 0
            }
            self.setCurrentTime(time: newTime)
        }

        if !self._isPlaying {
                self._fromInternal = true
                self.play()
         }


    }
    // MARK: - Set-up Public functions
    
    @objc func play() {
        self._togglePlay = true
        self.player!.play()
    }
    @objc func pause() {
        self._togglePlay = false
        self.player!.pause()
    }
    @objc func seek(to:Float) {
        let seekTime: CMTime = CMTimeMake(value: Int64(to*1000), timescale: 1000)
        self.player!.seek(to:seekTime)
    }
    @objc func getDuration() -> Double {
        return Double(CMTimeGetSeconds(self.player!.currentItem!.duration))
    }
    @objc func getCurrentTime() -> Double {
        return self._currentTime
    }
    @objc func setCurrentTime(time: Double) {
        let seekTime: CMTime = CMTimeMake(value: Int64(time*1000), timescale: 1000)
        self.player!.seek(to:seekTime)
        self._currentTime = time
        
    }
    @objc func getVolume() -> Float {
        return self.player!.volume
    }
    @objc func setVolume(volume: Float) {
        self.player!.volume = volume
    }
    @objc func getMuted() -> Bool {
        return self.player!.isMuted
    }
    @objc func setMuted(muted: Bool) {
        self.player!.isMuted = muted
    }

}
