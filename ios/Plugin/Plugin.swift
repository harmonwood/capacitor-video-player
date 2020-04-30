import Foundation
import Capacitor

import AVKit

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitor.ionicframework.com/docs/plugins/ios
 */
@objc(CapacitorVideoPlayer)
public class CapacitorVideoPlayer: CAPPlugin {
     
    var player: AVPlayer!
    public var call: CAPPluginCall!
    var videoPlayer: AVPlayerViewController!
    var videoPlayerTableViewController: VideoPlayerTableViewController!
    var videoPlayerFullScreenView: FullScreenVideoPlayerView!
    var mode : String!
    var playerList: [String: SmallVideoPlayerView] = [:]
    
    // MARK: - Load plugin
        
    override public func load() {
        // add listeners here
        NotificationCenter.default.addObserver(self, selector: #selector(didFinishPlaying),
                                               name:Notification.Name.AVPlayerItemDidPlayToEndTime, object: nil)
        // add Observers
        NotificationCenter.default.addObserver(forName: .playerItemPlay, object: nil, queue: nil, using: playerItemPlay)
        NotificationCenter.default.addObserver(forName: .playerItemPause, object: nil, queue: nil, using: playerItemPause)
        NotificationCenter.default.addObserver(forName: .playerItemEnd, object: nil, queue: nil, using: playerItemEnd)
        NotificationCenter.default.addObserver(forName: .playerItemReady, object: nil, queue: nil, using: playerItemReady)
        NotificationCenter.default.addObserver(forName: .playerInTableDismiss, object: nil, queue: nil, using: playerInTableDismiss)
    }
    
    // MARK: - Echo
    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.success([
            "value": value
        ])
    }
    
    // MARK: - Init player(s)
    
    @objc func initPlayer(_ call: CAPPluginCall)  {
        self.call = call

        guard let mode = call.options["mode"] as? String else {
            let error:String = "VideoPlayer initPlayer: Must provide a Mode (fullscreen/embedded/intable)"
            print(error)
            call.reject(error)
            return
        }
        self.mode = mode
        if (mode == "fullscreen") {
            guard let url = call.options["url"] as? String else {
                let error:String = "VideoPlayer initPlayer: Must provide a video url"
                print(error)
                call.reject(error)
                return
            }
            DispatchQueue.main.async {
                self.videoPlayerFullScreenView = FullScreenVideoPlayerView(videoPath: url)

                self.bridge.viewController.present(self.videoPlayerFullScreenView.videoPlayer, animated: true, completion:{
                    self.videoPlayerFullScreenView.player.play()
                });
            }
        } else if (mode == "embedded") {
            call.success([ "result": false, "message":"Not implemented"])

        } else if (mode == "intable") {
            let pageTitle: String = call.getString("pageTitle") ?? "In Table Videos"
            let videoWidth: Int = call.getInt("width") ?? 320
            let videoHeight: Int = call.getInt("height") ?? 180

            guard let videoList = call.options["videoList"] as? Array<Dictionary<String,String>> else {
                let error:String = "VideoPlayer initPlayer: Must provide a video list"
                print(error)
                call.reject(error)
                return
            }
            
            DispatchQueue.main.async {
                self.videoPlayerTableViewController = VideoPlayerTableViewController()
                self.videoPlayerTableViewController.pageTitle = pageTitle
                self.videoPlayerTableViewController.videoWidth = videoWidth
                self.videoPlayerTableViewController.videoHeight = videoHeight
                self.videoPlayerTableViewController.videoList = videoList
                let navigationController = UINavigationController(rootViewController: self.videoPlayerTableViewController)
                navigationController.modalPresentationStyle = .fullScreen
                self.bridge.viewController.present(navigationController, animated: true, completion:{
                    self.notifyListeners("jeepCapVideoPlayerInTableReady", data: ["ready": true], retainUntilConsumed: true)

                });

            }
        }

    }
    
    // MARK: - Play the given player

    @objc func play(_ call: CAPPluginCall)  {
        self.call = call

        guard let playerId = call.options["playerId"] as? String else {
            let error:String = "VideoPlayer play: Must provide a playerId"
            print(error)
            call.reject(error)
            return
        }
        if (self.videoPlayerTableViewController != nil && !self.videoPlayerTableViewController.videoPlayers.isEmpty) {
             if let player = self.videoPlayerTableViewController.videoPlayers[playerId] {
                DispatchQueue.main.async {
                    player.play()
                }
            } else {
                let error:String = "VideoPlayer play: Given playerId not found"
                print(error)
                call.reject(error)
                return
            }
        } else {
            let error:String = "VideoPlayer play: No videoPlayerTableViewController"
            print(error)
            call.reject(error)
            return
        }
    }
    
    // MARK: - Pause the given player

    @objc func pause(_ call: CAPPluginCall)  {
        self.call = call

        guard let playerId = call.options["playerId"] as? String else {
            let error:String = "VideoPlayer pause: Must provide a playerId"
            print(error)
            call.reject(error)
            return
        }
         if (self.videoPlayerTableViewController != nil && !self.videoPlayerTableViewController.videoPlayers.isEmpty) {
            if let player = self.videoPlayerTableViewController.videoPlayers[playerId] {
                DispatchQueue.main.async {
                    player.pause()
                }
            } else {
                let error:String = "VideoPlayer pause: Given playerId not found"
                print(error)
                call.reject(error)
                return
            }
        } else {
            let error:String = "VideoPlayer pause: No videoPlayerTableViewController"
            print(error)
            call.reject(error)
            return
        }
    }
    
    // MARK: - get Duration for the given player

    @objc func getDuration(_ call: CAPPluginCall)  {
        self.call = call

        guard let playerId = call.options["playerId"] as? String else {
            let error:String = "VideoPlayer getDuration: Must provide a playerId"
            print(error)
            call.reject(error)
            return
        }
        if (self.videoPlayerTableViewController != nil && !self.videoPlayerTableViewController.videoPlayers.isEmpty) {
            if let player = self.videoPlayerTableViewController.videoPlayers[playerId] {
                DispatchQueue.main.async {
                    let duration: Double = player.getDuration()
                    call.success([ "result": true, "method":"getDuration", "value": duration])
                }
            } else {
                let error:String = "VideoPlayer getDuration: Given playerId not found"
                print(error)
                call.reject(error)
                return
            }
        } else {
            let error:String = "VideoPlayer getDuration: No videoPlayerTableViewController"
            print(error)
            call.reject(error)
            return
        }
    }
    
    // MARK: - get CurrentTime for the given player

    @objc func getCurrentTime(_ call: CAPPluginCall)  {
        self.call = call

        guard let playerId = call.options["playerId"] as? String else {
            let error:String = "VideoPlayer getCurrentTime: Must provide a playerId"
            print(error)
            call.reject(error)
            return
        }
        if (self.videoPlayerTableViewController != nil && !self.videoPlayerTableViewController.videoPlayers.isEmpty) {
            if let player = self.videoPlayerTableViewController.videoPlayers[playerId] {
                DispatchQueue.main.async {
                    let currentTime: Double = player.getCurrentTime()
                    call.success([ "result": true, "method":"getCurrentTime", "value": currentTime])
                }
            } else {
                let error:String = "VideoPlayer getCurrentTime: Given playerId not found"
                print(error)
                call.reject(error)
                return
            }
        } else {
            let error:String = "VideoPlayer getCurrentTime: No videoPlayerTableViewController"
            print(error)
            call.reject(error)
            return
        }
    }
    
    // MARK: - set CurrentTime for the given player

    @objc func setCurrentTime(_ call: CAPPluginCall)  {
        self.call = call

        guard let playerId = call.options["playerId"] as? String else {
            let error:String = "VideoPlayer setCurrentTime: Must provide a playerId"
            print(error)
            call.reject(error)
            return
        }
        guard let seekTime = call.options["seektime"] as? Double else {
            let error:String = "VideoPlayer setCurrentTime: Must provide a playerId"
            print(error)
            call.reject(error)
            return
        }
        if (self.videoPlayerTableViewController != nil && !self.videoPlayerTableViewController.videoPlayers.isEmpty) {
            if let player = self.videoPlayerTableViewController.videoPlayers[playerId] {
                DispatchQueue.main.async {
                    player.setCurrentTime(time: seekTime)
                    call.success([ "result": true, "method":"setCurrentTime", "value": seekTime])
                }
            } else {
                let error:String = "VideoPlayer setCurrentTime: Given playerId not found"
                print(error)
                call.reject(error)
                return
            }
        } else {
            let error:String = "VideoPlayer setCurrentTime: No videoPlayerTableViewController"
            print(error)
            call.reject(error)
            return
        }
    }
    
    // MARK: - get Volume for the given player

    @objc func getVolume(_ call: CAPPluginCall)  {
        self.call = call

        guard let playerId = call.options["playerId"] as? String else {
            let error:String = "VideoPlayer getVolume: Must provide a playerId"
            print(error)
            call.reject(error)
            return
        }
        if (self.videoPlayerTableViewController != nil && !self.videoPlayerTableViewController.videoPlayers.isEmpty) {
            if let player = self.videoPlayerTableViewController.videoPlayers[playerId] {
                DispatchQueue.main.async {
                    let volume: Float = player.getVolume()
                    call.success([ "result": true, "method":"getVolume", "value": volume])
                }
            } else {
                let error:String = "VideoPlayer getVolume: Given playerId not found"
                print(error)
                call.reject(error)
                return
            }
        } else {
            let error:String = "VideoPlayer getVolume: No videoPlayerTableViewController"
            print(error)
            call.reject(error)
            return
        }
    }
    
    // MARK: - set Volume for the given player

    @objc func setVolume(_ call: CAPPluginCall)  {
        self.call = call

        guard let playerId = call.options["playerId"] as? String else {
            let error:String = "VideoPlayer setVolume: Must provide a playerId"
            print(error)
            call.reject(error)
            return
        }
        guard let volume = call.options["volume"] as? Float else {
            let error:String = "VideoPlayer setVolume: Must provide a playerId"
            print(error)
            call.reject(error)
            return
        }
        if (self.videoPlayerTableViewController != nil && !self.videoPlayerTableViewController.videoPlayers.isEmpty) {
            if let player = self.videoPlayerTableViewController.videoPlayers[playerId] {
                DispatchQueue.main.async {
                    player.setVolume(volume:volume)
                    call.success([ "result": true, "method":"setVolume", "value": volume])
                }
            } else {
                let error:String = "VideoPlayer setVolume: Given playerId not found"
                print(error)
                call.reject(error)
                return
            }
        } else {
            let error:String = "VideoPlayer setVolume: No videoPlayerTableViewController"
            print(error)
            call.reject(error)
            return
        }
    }
    
    // MARK: - get Muted for the given player

    @objc func getMuted(_ call: CAPPluginCall)  {
        self.call = call

        guard let playerId = call.options["playerId"] as? String else {
            let error:String = "VideoPlayer getMuted: Must provide a playerId"
            print(error)
            call.reject(error)
            return
        }
        if (self.videoPlayerTableViewController != nil && !self.videoPlayerTableViewController.videoPlayers.isEmpty) {
            if let player = self.videoPlayerTableViewController.videoPlayers[playerId] {
                DispatchQueue.main.async {
                    let muted: Bool = player.getMuted()
                    call.success([ "result": true, "method":"getMuted", "value": muted])
                }
            } else {
                let error:String = "VideoPlayer getMuted: Given playerId not found"
                print(error)
                call.reject(error)
                return
            }
        } else {
            let error:String = "VideoPlayer getMuted: No videoPlayerTableViewController"
            print(error)
            call.reject(error)
            return
        }
    }
    
    // MARK: - set Muted for the given player

    @objc func setMuted(_ call: CAPPluginCall)  {
        self.call = call

        guard let playerId = call.options["playerId"] as? String else {
            let error:String = "VideoPlayer setMuted: Must provide a playerId"
            print(error)
            call.reject(error)
            return
        }
        guard let muted = call.options["muted"] as? Bool else {
            let error:String = "VideoPlayer setMuted: Must provide a playerId"
            print(error)
            call.reject(error)
            return
        }
        if (self.videoPlayerTableViewController != nil && !self.videoPlayerTableViewController.videoPlayers.isEmpty) {
            if let player = self.videoPlayerTableViewController.videoPlayers[playerId] {
                DispatchQueue.main.async {
                    player.setMuted(muted:muted)
                    call.success([ "result": true, "method":"setMuted", "value": muted])
                }
            } else {
                let error:String = "VideoPlayer setMuted: Given playerId not found"
                print(error)
                call.reject(error)
                return
            }
        } else {
            let error:String = "VideoPlayer setMuted: No videoPlayerTableViewController"
            print(error)
            call.reject(error)
            return
        }
    }
    
    // MARK: - Stop all player(s) playing
    
    @objc func stopAllPlayers()  {
         if (self.videoPlayerTableViewController != nil && !self.videoPlayerTableViewController.videoPlayers.isEmpty) {
            self.videoPlayerTableViewController.pauseAllPlayers()
            call.success([ "result": true, "method":"stopAllPlayers"])
         } else {
            let error:String = "VideoPlayer stopAllPlayers: No videoPlayerTableViewController"
            print(error)
            call.reject(error)
            return
        }

    }

    // MARK: - Handle Notifications

    @objc func didFinishPlaying() {
        if (mode == "fullscreen") {
            DispatchQueue.main.async {
                self.videoPlayerFullScreenView.videoPlayer.dismiss(animated: true,completion: nil)
                self.call.success([ "result": true])
            }
        }
    }
    @objc func playerItemPlay(notification: Notification) -> Void {
        guard let info = notification.userInfo as? [String: Any] else { return }
        DispatchQueue.main.async {
            self.notifyListeners("jeepCapVideoPlayerPlay", data: info, retainUntilConsumed: true)
        }
    }
    @objc func playerItemPause(notification: Notification) -> Void {
        guard let info = notification.userInfo as? [String: Any] else { return }
        DispatchQueue.main.async {
            self.notifyListeners("jeepCapVideoPlayerPause", data: info, retainUntilConsumed: true)
        }
    }
    @objc func playerItemEnd(notification: Notification) -> Void {
        guard let info = notification.userInfo as? [String: Any] else { return }
        DispatchQueue.main.async {
            self.notifyListeners("jeepCapVideoPlayerEnd", data: info, retainUntilConsumed: true)
        }
    }
    @objc func playerItemReady(notification: Notification) -> Void {
        guard let info = notification.userInfo as? [String: Any] else { return }
        DispatchQueue.main.async {
            self.notifyListeners("jeepCapVideoPlayerReady", data: info, retainUntilConsumed: true)
        }
    }
    @objc func playerInTableDismiss(notification: Notification) -> Void {
         let info : [String:Any] = ["dismiss":true]
        DispatchQueue.main.async {
            self.notifyListeners("jeepCapVideoPlayerDismiss", data: info, retainUntilConsumed: true)
            NotificationCenter.default.removeObserver(self)
            self.videoPlayerTableViewController.destroyAllGestures()
            self.videoPlayerTableViewController.destroyAllPlayers()
            self.playerList = [:]
            self.videoPlayer = nil
            self.videoPlayerTableViewController = nil
            self.videoPlayerFullScreenView = nil
            self.player = nil
            self.bridge.viewController.dismiss(animated: true, completion: nil)
        }
    }

}

