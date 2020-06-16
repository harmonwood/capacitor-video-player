import Foundation
import Capacitor
import AVKit
import UIKit

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitor.ionicframework.com/docs/plugins/ios
 */
@objc(CapacitorVideoPlayer)
public class CapacitorVideoPlayer: CAPPlugin {
     
    public var player: AVPlayer!
    public var call: CAPPluginCall!
    public var videoPlayer: AVPlayerViewController!
    public var bgPlayer: AVPlayer!
    var videoPlayerFullScreenView: FullScreenVideoPlayerView!
    var audioSession: AVAudioSession!
    var mode: String!
    var fsPlayerId: String = "fullscreen"
    
    override public func load() {
        self.addObserversToNotificationCenter()
    }
        
    // MARK: - Echo
    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.success([ "result": true, "method":"echo", "value": value])
    }
    
    // MARK: - Init player(s)
    
    @objc func initPlayer(_ call: CAPPluginCall)  {
        self.call = call

        guard let mode = call.options["mode"] as? String else {
            let error:String = "Must provide a Mode (fullscreen/embedded)"
            print(error)
            call.success([ "result": false, "method":"initPlayer", "message": error])
            return
        }
        guard let playerId = call.options["playerId"] as? String else {
            let error:String = "Must provide a PlayerId"
            print(error)
            call.success([ "result": false, "method":"initPlayer", "message": error])
            return
        }
        self.fsPlayerId = playerId
        self.mode = mode
        // add audio session
        self.audioSession = AVAudioSession.sharedInstance()
        do {
            // Set the audio session category, mode, and options.
            try self.audioSession.setCategory(.playback, mode: .moviePlayback, options: [])
        } catch {
            let error:String = "Failed to set audio session category."
            print(error)
            call.success([ "result": false, "method":"initPlayer", "message": error])
            return
        }

        if (mode == "fullscreen") {
            guard let url = call.options["url"] as? String else {
                let error:String = "Must provide a video url"
                print(error)
                call.success([ "result": false, "method":"initPlayer", "message": error])
                return
            }

            DispatchQueue.main.async {
                self.videoPlayerFullScreenView = FullScreenVideoPlayerView(videoPath: url,playerId: self.fsPlayerId,exitOnEnd: true)
                self.bgPlayer = self.videoPlayerFullScreenView.videoPlayer.player

                // Present the Player View Controller
                self.bridge.viewController  .present(self.videoPlayerFullScreenView.videoPlayer, animated: true, completion:{
                    do {
                        // Activate the audio session.
                        try self.audioSession.setActive(true)
                        call.success([ "result": true, "method":"initPlayer", "value": true])
                        return
                    } catch {
                        let error:String = "Failed to set audio session category"
                        print(error)
                        call.success([ "result": false, "method":"initPlayer", "message": error])
                        return
                    }
 
                });
            }
        } else if (mode == "embedded") {
            call.success([ "result": false, "method":"initPlayer","message":"Not implemented"])
        }

    }
    
    // MARK: - Add Observers
    
    @objc func addObserversToNotificationCenter() {
        // add Observers

        NotificationCenter.default.addObserver(forName: .playerItemPlay, object: nil, queue: nil, using: playerItemPlay)
        NotificationCenter.default.addObserver(forName: .playerItemPause, object: nil, queue: nil, using: playerItemPause)
        NotificationCenter.default.addObserver(forName: .playerItemEnd, object: nil, queue: nil, using: playerItemEnd)
        NotificationCenter.default.addObserver(forName: .playerItemReady, object: nil, queue: nil, using: playerItemReady)
        NotificationCenter.default.addObserver(forName: .playerFullscreenDismiss, object: nil, queue: nil, using: playerFullscreenDismiss)

        NotificationCenter.default.addObserver(forName: UIApplication.didEnterBackgroundNotification, object: nil, queue: nil) { (notification) in
            self.videoPlayerFullScreenView.videoPlayer.player = nil
        }
        NotificationCenter.default.addObserver(forName: UIApplication.willEnterForegroundNotification, object: nil, queue: OperationQueue.main) { (notification) in
            self.videoPlayerFullScreenView.videoPlayer.player = self.bgPlayer
        }
    }
    
    // MARK: - Play the given player

    @objc func isPlaying(_ call: CAPPluginCall)  {
        self.call = call
        guard let playerId = call.options["playerId"] as? String else {
            let error:String = "Must provide a playerId"
            print(error)
            call.success([ "result": false, "method":"isPlaying", "message": error])
            return
        }
        if (self.mode == "fullscreen" && self.fsPlayerId == playerId) {

             if (self.videoPlayerFullScreenView != nil) {
                 if let playerView = self.videoPlayerFullScreenView {
                     DispatchQueue.main.async {
                        let isPlaying: Bool = playerView.isPlaying;
                        call.success([ "result": true, "method":"isPlaying", "value": isPlaying])
                        return
                     }
                 } else {
                    let error:String = "Fullscreen player not found"
                    print(error)
                    call.success([ "result": false, "method":"isPlaying", "message": error])
                    return
                }
                 
             } else {
                let error:String = "No videoPlayerFullScreenView"
                print(error)
                call.success([ "result": false, "method":"isPlaying", "message": error])
                return
            }

        }

    }
    // MARK: - Play the given player

    @objc func play(_ call: CAPPluginCall)  {
        self.call = call
        guard let playerId = call.options["playerId"] as? String else {
            let error:String = "Must provide a playerId"
            print(error)
            call.success([ "result": false, "method":"play", "message": error])
            return
        }
        if (self.mode == "fullscreen" && self.fsPlayerId == playerId) {

            if (self.videoPlayerFullScreenView != nil) {
                if let playerView = self.videoPlayerFullScreenView {
                    DispatchQueue.main.async {
                        playerView.play()
                        call.success([ "result": true, "method":"play","value": true])
                        return
                    }
                } else {
                   let error:String = "Fullscreen player not found"
                   print(error)
                   call.success([ "result": false, "method":"play", "message": error])
                   return
               }
                
            } else {
               let error:String = "No videoPlayerFullScreenView"
               print(error)
               call.success([ "result": false, "method":"play", "message": error])
               return
           }
        }
    }
    
    // MARK: - Pause the given player

    @objc func pause(_ call: CAPPluginCall)  {
        self.call = call

        guard let playerId = call.options["playerId"] as? String else {
            let error:String = "Must provide a playerId"
            print(error)
            call.success([ "result": false, "method":"pause", "message": error])
            return
        }
        if (self.mode == "fullscreen" && self.fsPlayerId == playerId) {
            if(self.videoPlayerFullScreenView != nil) {
                if let playerView = self.videoPlayerFullScreenView {
                    DispatchQueue.main.async {
                        playerView.pause()
                        call.success([ "result": true, "method":"pause","value":true])
                        return
                    }
                } else {
                   let error:String = "Fullscreen player not found"
                   print(error)
                   call.success([ "result": false, "method":"pause", "message": error])
                   return
                }
            } else {
               let error:String = "No videoPlayerFullScreenView"
               print(error)
               call.success([ "result": false, "method":"pause", "message": error])
               return

            }
        }
    }

    // MARK: - get Duration for the given player

    @objc func getDuration(_ call: CAPPluginCall)  {
        self.call = call

        guard let playerId = call.options["playerId"] as? String else {
            let error:String = "Must provide a playerId"
            print(error)
            call.success([ "result": false, "method":"getDuration", "message": error])
            return
        }
        if (self.mode == "fullscreen" && self.fsPlayerId == playerId) {
            if (videoPlayerFullScreenView != nil) {
                if let playerView = self.videoPlayerFullScreenView {
                    DispatchQueue.main.async {
                        let duration: Double = playerView.getDuration()
                        call.success([ "result": true, "method":"getDuration", "value": duration])
                        return
                    }
                } else {
                    let error:String = "Fullscreen playerId not found"
                    print(error)
                    call.success([ "result": false, "method":"getDuration", "message": error])
                    return
                }
            } else {
                let error:String = "No videoPlayerFullScreenView"
                print(error)
                call.success([ "result": false, "method":"getDuration", "message": error])
                return
            }

        }
    }
    
    // MARK: - get CurrentTime for the given player

    @objc func getCurrentTime(_ call: CAPPluginCall)  {
        self.call = call

        guard let playerId = call.options["playerId"] as? String else {
            let error:String = "Must provide a playerId"
            print(error)
            call.success([ "result": false, "method":"getCurrentTime", "message": error])
            return
        }
        if (self.mode == "fullscreen" && self.fsPlayerId == playerId) {
            if (videoPlayerFullScreenView != nil) {
                if let playerView = self.videoPlayerFullScreenView {
                    DispatchQueue.main.async {
                        let currentTime: Double = playerView.getCurrentTime()
                        call.success([ "result": true, "method":"getCurrentTime", "value": currentTime])
                        return
                    }
                } else {
                    let error:String = "Fullscreen playerId not found"
                    print(error)
                    call.success([ "result": false, "method":"getCurrentTime", "message": error])
                    return
                }
            } else {
                let error:String = "No videoPlayerFullScreenView"
                print(error)
                call.success([ "result": false, "method":"getCurrentTime", "message": error])
                return
            }
        }
    }
    
    // MARK: - set CurrentTime for the given player

    @objc func setCurrentTime(_ call: CAPPluginCall)  {
        self.call = call

        guard let playerId = call.options["playerId"] as? String else {
            let error:String = "Must provide a playerId"
            print(error)
            call.success([ "result": false, "method":"setCurrentTime", "message": error])
            return
        }
        guard let seekTime = call.options["seektime"] as? Double else {
            let error:String = "Must provide a time in second"
            print(error)
            call.success([ "result": false, "method":"setCurrentTime", "message": error])
            return
        }
        if (self.mode == "fullscreen" && self.fsPlayerId == playerId) {
            if (videoPlayerFullScreenView != nil) {
                if let playerView = self.videoPlayerFullScreenView {
                    DispatchQueue.main.async {
                        playerView.setCurrentTime(time: seekTime)
                        call.success([ "result": true, "method":"setCurrentTime", "value": seekTime])
                        return
                    }
                } else {
                    let error:String = "Fullscreen playerId not found"
                    print(error)
                    call.success([ "result": false, "method":"setCurrentTime", "message": error])
                    return
                }
            } else {
                let error:String = "No videoPlayerFullScreenView"
                print(error)
                call.success([ "result": false, "method":"setCurrentTime", "message": error])
                return
            }

        }
    }
    
    // MARK: - get Volume for the given player

    @objc func getVolume(_ call: CAPPluginCall)  {
        self.call = call

        guard let playerId = call.options["playerId"] as? String else {
            let error:String = "Must provide a playerId"
            print(error)
            call.success([ "result": false, "method":"getVolume", "message": error])
            return
        }
        if (self.mode == "fullscreen" && self.fsPlayerId == playerId) {
            if (videoPlayerFullScreenView != nil) {
                if let playerView = self.videoPlayerFullScreenView {
                    DispatchQueue.main.async {
                        let volume: Float = playerView.getVolume()
                        call.success([ "result": true, "method":"getVolume", "value": volume])
                        return
                    }
                } else {
                    let error:String = "Fullscreen playerId not found"
                    print(error)
                    call.success([ "result": false, "method":"getVolume", "message": error])
                    return
                }
            } else {
                let error:String = "No videoPlayerFullScreenView"
                print(error)
                call.success([ "result": false, "method":"getVolume", "message": error])
                return
            }
        }
    }
    
    // MARK: - set Volume for the given player

    @objc func setVolume(_ call: CAPPluginCall)  {
        self.call = call

        guard let playerId = call.options["playerId"] as? String else {
            let error:String = "Must provide a playerId"
            print(error)
            call.success([ "result": false, "method":"setVolume", "message": error])
            return
        }
        guard let volume = call.options["volume"] as? Float else {
            let error:String = "Must provide a volume value"
            print(error)
            call.success([ "result": false, "method":"setVolume", "message": error])

            return
        }
        if (self.mode == "fullscreen" && self.fsPlayerId == playerId) {
           if (videoPlayerFullScreenView != nil) {
               if let playerView = self.videoPlayerFullScreenView {
                   DispatchQueue.main.async {
                       playerView.setVolume(volume:volume)
                       call.success([ "result": true, "method":"setVolume", "value": volume])
                    return
                   }
               } else {
                   let error:String = "Fullscreen playerId not found"
                   print(error)
                   call.success([ "result": false, "method":"setVolume", "message": error])
                   return
               }
           } else {
               let error:String = "No videoPlayerFullScreenView"
               print(error)
               call.success([ "result": false, "method":"setVolume", "message": error])
               return
           }
       }

    }
    
    // MARK: - get Muted for the given player

    @objc func getMuted(_ call: CAPPluginCall)  {
        self.call = call

        guard let playerId = call.options["playerId"] as? String else {
            let error:String = "Must provide a playerId"
            print(error)
            call.success([ "result": false, "method":"getMuted", "message": error])
            return
        }
        if (self.mode == "fullscreen" && self.fsPlayerId == playerId) {
            if (videoPlayerFullScreenView != nil) {
                if let playerView = self.videoPlayerFullScreenView {
                    DispatchQueue.main.async {
                        let muted: Bool = playerView.getMuted()
                        call.success([ "result": true, "method":"getMuted", "value": muted])
                        return
                    }
                } else {
                    let error:String = "Fullscreen playerId not found"
                    print(error)
                    call.success([ "result": false, "method":"getMuted", "message": error])
                    return
                }
            } else {
                let error:String = "No videoPlayerFullScreenView"
                print(error)
                call.success([ "result": false, "method":"getMuted", "message": error])
                return
            }
        }
    }
    
    // MARK: - set Muted for the given player

    @objc func setMuted(_ call: CAPPluginCall)  {
        self.call = call

        guard let playerId = call.options["playerId"] as? String else {
            let error:String = "Must provide a playerId"
            print(error)
            call.success([ "result": false, "method":"setMuted", "message": error])
            return
        }
        guard let muted = call.options["muted"] as? Bool else {
            let error:String = "Must provide a boolean true/false"
            print(error)
            call.success([ "result": false, "method":"setMuted", "message": error])
            return
        }
        if (self.mode == "fullscreen" && self.fsPlayerId == playerId) {
            if (videoPlayerFullScreenView != nil) {
                if let playerView = self.videoPlayerFullScreenView {
                    DispatchQueue.main.async {
                        playerView.setMuted(muted:muted)
                        call.success([ "result": true, "method":"setMuted", "value": muted])
                        return
                    }
                } else {
                    let error:String = "Fullscreen playerId not found"
                    print(error)
                    call.success([ "result": false, "method":"setMuted", "message": error])
                    return
                }
            } else {
                let error:String = "No videoPlayerFullScreenView"
                print(error)
                call.success([ "result": false, "method":"setMuted", "message": error])

                return
            }
        }
    }
    
    // MARK: - Stop all player(s) playing
    
    @objc func stopAllPlayers(_ call: CAPPluginCall)  {
        self.call = call
        if (self.mode == "fullscreen") {
            if(self.videoPlayerFullScreenView != nil) {
                if let playerView = self.videoPlayerFullScreenView {
                    DispatchQueue.main.async {
                        if(playerView.isPlaying) {
                            playerView.pause()
                        }
                        call.success([ "result": true, "method":"stopAllPlayers","value":true])
                        return
                    }
                } else {
                   let error:String = "Fullscreen player not found"
                   print(error)
                   call.success([ "result": false, "method":"stopAllPlayers", "message": error])
                   return
                }
            } else {
               let error:String = "No videoPlayerFullScreenView"
               print(error)
               call.success([ "result": false, "method":"stopAllPlayers", "message": error])
               return

            }

        }
    }
    // MARK: - Handle Notifications

    @objc func playerItemPlay(notification: Notification) -> Void {
        guard let info = notification.userInfo as? [String: Any] else { return }
        DispatchQueue.main.async {
            self.notifyListeners("jeepCapVideoPlayerPlay", data: info, retainUntilConsumed: true)
            return
        }
    }
    @objc func playerItemPause(notification: Notification) -> Void {
        guard let info = notification.userInfo as? [String: Any] else { return }
        DispatchQueue.main.async {
            self.notifyListeners("jeepCapVideoPlayerPause", data: info, retainUntilConsumed: true)
            return
        }
    }

    @objc func playerItemEnd(notification: Notification) -> Void {
        guard let info = notification.userInfo as? [String: Any] else { return }
        DispatchQueue.main.async {
            self.notifyListeners("jeepCapVideoPlayerEnded", data: info, retainUntilConsumed: true)
            if (self.mode == "fullscreen") {
                self.playerFullscreenExit()
            }
                
            return
        }
    }
    @objc func playerItemReady(notification: Notification) -> Void {
        guard let info = notification.userInfo as? [String: Any] else { return }
        DispatchQueue.main.async {
            self.notifyListeners("jeepCapVideoPlayerReady", data: info, retainUntilConsumed: true)
            if(self.mode == "fullscreen" && info["fromPlayerId"] as! String == self.fsPlayerId) {
                self.videoPlayerFullScreenView.play()
            }
            return
        }
    }

    @objc func playerFullscreenDismiss(notification: Notification) -> Void {
        let info : [String:Any] = ["dismiss":true]
        DispatchQueue.main.async {
            
            self.notifyListeners("jeepCapVideoPlayerExit", data: info, retainUntilConsumed: true)
            if (self.mode == "fullscreen") {
                self.playerFullscreenExit()
            }
            return
        }
    }
 
    func playerFullscreenExit() -> Void {
        self.videoPlayerFullScreenView.removeObservers()
        NotificationCenter.default.removeObserver(self)
        self.bridge.viewController.dismiss(animated: true, completion: {
            do {
                // DeActivate the audio session.
                try self.audioSession.setActive(false)
                self.audioSession = nil
            } catch {
                let error:String = "playerFullscreenExit: Failed to deactivate audio session category"
                print(error)
            }

        })
        return
    }

}

