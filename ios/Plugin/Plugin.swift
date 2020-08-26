import Foundation
import Capacitor
import AVKit
import UIKit

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitor.ionicframework.com/docs/plugins/ios
 */
@objc(CapacitorVideoPlayer)
// swiftlint:disable type_body_length
// swiftlint:disable file_length
public class CapacitorVideoPlayer: CAPPlugin {

    public var player: AVPlayer?
    public var call: CAPPluginCall?
    public var videoPlayer: AVPlayerViewController?
    public var bgPlayer: AVPlayer?
    var videoPlayerFullScreenView: FullScreenVideoPlayerView?
    var audioSession: AVAudioSession?
    var mode: String?
    var fsPlayerId: String = "fullscreen"
    var playObserver: Any?
    var pauseObserver: Any?
    var endObserver: Any?
    var readyObserver: Any?
    var fsDismissObserver: Any?
    var backgroundObserver: Any?
    var foregroundObserver: Any?
    var vpInternalObserver: Any?
    var docPath: String = ""

    override public func load() {
        self.addObserversToNotificationCenter()
    }
    deinit {
        NotificationCenter.default.removeObserver(playObserver as Any)
        NotificationCenter.default.removeObserver(pauseObserver as Any)
        NotificationCenter.default.removeObserver(endObserver as Any)
        NotificationCenter.default.removeObserver(readyObserver as Any)
        NotificationCenter.default.removeObserver(fsDismissObserver as Any)
        NotificationCenter.default.removeObserver(backgroundObserver as Any)
        NotificationCenter.default.removeObserver(foregroundObserver as Any)
        NotificationCenter.default.removeObserver(vpInternalObserver as Any)
    }
    // MARK: - Echo
    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.success([ "result": true, "method": "echo", "value": value])
    }

    // MARK: - Init player(s)

    @objc func initPlayer(_ call: CAPPluginCall) {
        self.call = call

        guard let mode = call.options["mode"] as? String else {
            let error: String = "Must provide a Mode (fullscreen/embedded)"
            print(error)
            call.success([ "result": false, "method": "initPlayer", "message": error])
            return
        }
        guard let playerId = call.options["playerId"] as? String else {
            let error: String = "Must provide a PlayerId"
            print(error)
            call.success([ "result": false, "method": "initPlayer", "message": error])
            return
        }
        self.fsPlayerId = playerId
        self.mode = mode
        self.docPath = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)[0]
        if mode == "fullscreen" {
            guard var videoPath = call.options["url"] as? String else {
                let error: String = "Must provide a video url"
                print(error)
                call.success([ "result": false, "method": "initPlayer", "message": error])
                return
            }
            if videoPath == "internal" {
                self.pickVideoFromInternal()
            } else {
                if String(videoPath.prefix(11)) == "application" {
                    let path: String = String(videoPath.dropFirst(12))
                    videoPath = docPath.appendingFormat("/\(path)")
                    if !isFileExists(filePath: videoPath) {
                        print("*** video file does not exist at path \n \(videoPath) \n***")
                        let info: [String: Any] = ["dismiss": true]
                        self.notifyListeners("jeepCapVideoPlayerExit", data: info, retainUntilConsumed: true)
                        call.success([ "result": false, "method": "initPlayer", "message": "video file does not exist"])
                        return
                    }
                    let url = URL(fileURLWithPath: videoPath)
                    createVideoPlayerFullScreenView(call: call, videoUrl: url)
                } else {
                    if videoPath.count > 0 {
                        if let url = URL(string: videoPath) {
                            createVideoPlayerFullScreenView(call: call, videoUrl: url)
                        }
                    }
                }
            }
        } else if mode == "embedded" {
            call.success([ "result": false, "method": "initPlayer", "message": "Not implemented"])
        }

    }

    // MARK: - createVideoPlayerFullScreenView

    func createVideoPlayerFullScreenView(call: CAPPluginCall, videoUrl: URL) {
        DispatchQueue.main.async {

            self.videoPlayerFullScreenView =
            FullScreenVideoPlayerView(url: videoUrl, playerId: self.fsPlayerId, exitOnEnd: true)
            self.bgPlayer = self.videoPlayerFullScreenView?.videoPlayer.player
            guard let videoPlayer: AVPlayerViewController =
                self.videoPlayerFullScreenView?.videoPlayer else {
                let error: String = "No videoPlayer available"
                print(error)
                call.success([ "result": false, "method": "createVideoPlayerFullScreenView", "message": error])
                return
            }
            // Present the Player View Controller
            self.bridge.viewController.present(videoPlayer, animated: true, completion: {
                // add audio session
                self.audioSession = AVAudioSession.sharedInstance()
                // Set the audio session category, mode, and options.
                try? self.audioSession?.setCategory(.playback, mode: .moviePlayback,
                                                   options: [.mixWithOthers, .allowAirPlay])
                // Activate the audio session.
                try? self.audioSession?.setActive(true)
                call.success([ "result": true, "method": "createVideoPlayerFullScreenView", "value": true])
                return
            })
        }
    }

    // MARK: - Pick Video From Internal

    func pickVideoFromInternal() {
        DispatchQueue.main.async {

            let videoPickerViewController: VideoPickerViewController =
                VideoPickerViewController()
            self.bridge.viewController.present(videoPickerViewController, animated: true, completion: {
                return
            })
        }
        return

    }
    // MARK: - Add Observers

    @objc func addObserversToNotificationCenter() {
        // add Observers

        playObserver = NotificationCenter.default.addObserver(forName: .playerItemPlay, object: nil, queue: nil,
                                                              using: playerItemPlay)
        pauseObserver = NotificationCenter.default.addObserver(forName: .playerItemPause, object: nil, queue: nil,
                                                               using: playerItemPause)
        endObserver = NotificationCenter.default.addObserver(forName: .playerItemEnd, object: nil, queue: nil,
                                                             using: playerItemEnd)
        readyObserver = NotificationCenter.default.addObserver(forName: .playerItemReady, object: nil, queue: nil,
                                                               using: playerItemReady)
        fsDismissObserver = NotificationCenter.default.addObserver(forName: .playerFullscreenDismiss, object: nil,
                                                                   queue: nil, using: playerFullscreenDismiss)
        vpInternalObserver = NotificationCenter.default.addObserver(forName: .videoPathInternalReady, object: nil,
                                                                   queue: nil, using: videoPathInternalReady)

        backgroundObserver =
            NotificationCenter.default.addObserver(forName: UIApplication.didEnterBackgroundNotification, object: nil,
                                                   queue: nil) { (_) in
                    if self.videoPlayerFullScreenView != nil {
                        self.videoPlayerFullScreenView?.videoPlayer.player = nil
                    }
        }
        foregroundObserver =
            NotificationCenter.default.addObserver(forName: UIApplication.willEnterForegroundNotification, object: nil,
                                                   queue: OperationQueue.main) { (_) in
            if self.bgPlayer != nil && self.videoPlayerFullScreenView != nil {
                self.videoPlayerFullScreenView?.videoPlayer.player = self.bgPlayer
            }
        }
    }

    // MARK: - Play the given player

    @objc func isPlaying(_ call: CAPPluginCall) {
        self.call = call
        guard let playerId = call.options["playerId"] as? String else {
            let error: String = "Must provide a playerId"
            print(error)
            call.success([ "result": false, "method": "isPlaying", "message": error])
            return
        }
        if self.mode == "fullscreen" && self.fsPlayerId == playerId {

            if self.videoPlayerFullScreenView != nil {
                if let playerView = self.videoPlayerFullScreenView {
                     DispatchQueue.main.async {
                        let isPlaying: Bool = playerView.isPlaying
                        call.success([ "result": true, "method": "isPlaying", "value": isPlaying])
                        return
                     }
                } else {
                    let error: String = "Fullscreen player not found"
                    print(error)
                    call.success([ "result": false, "method": "isPlaying", "message": error])
                    return
                }

            } else {
                let error: String = "No videoPlayerFullScreenView"
                print(error)
                call.success([ "result": false, "method": "isPlaying", "message": error])
                return
            }

        }

    }
    // MARK: - Play the given player

    @objc func play(_ call: CAPPluginCall) {
        self.call = call
        guard let playerId = call.options["playerId"] as? String else {
            let error: String = "Must provide a playerId"
            print(error)
            call.success([ "result": false, "method": "play", "message": error])
            return
        }
        if self.mode == "fullscreen" && self.fsPlayerId == playerId {

            if self.videoPlayerFullScreenView != nil {
                if let playerView = self.videoPlayerFullScreenView {
                    DispatchQueue.main.async {
                        playerView.play()
                        call.success([ "result": true, "method": "play", "value": true])
                        return
                    }
                } else {
                   let error: String = "Fullscreen player not found"
                   print(error)
                   call.success([ "result": false, "method": "play", "message": error])
                   return
               }

            } else {
               let error: String = "No videoPlayerFullScreenView"
               print(error)
               call.success([ "result": false, "method": "play", "message": error])
               return
           }
        }
    }

    // MARK: - Pause the given player

    @objc func pause(_ call: CAPPluginCall) {
        self.call = call

        guard let playerId = call.options["playerId"] as? String else {
            let error: String = "Must provide a playerId"
            print(error)
            call.success([ "result": false, "method": "pause", "message": error])
            return
        }
        if self.mode == "fullscreen" && self.fsPlayerId == playerId {
            if self.videoPlayerFullScreenView != nil {
                if let playerView = self.videoPlayerFullScreenView {
                    DispatchQueue.main.async {
                        playerView.pause()
                        call.success([ "result": true, "method": "pause", "value": true])
                        return
                    }
                } else {
                   let error: String = "Fullscreen player not found"
                   print(error)
                   call.success([ "result": false, "method": "pause", "message": error])
                   return
                }
            } else {
               let error: String = "No videoPlayerFullScreenView"
               print(error)
               call.success([ "result": false, "method": "pause", "message": error])
               return

            }
        }
    }

    // MARK: - get Duration for the given player

    @objc func getDuration(_ call: CAPPluginCall) {
        self.call = call

        guard let playerId = call.options["playerId"] as? String else {
            let error: String = "Must provide a playerId"
            print(error)
            call.success([ "result": false, "method": "getDuration", "message": error])
            return
        }
        if self.mode == "fullscreen" && self.fsPlayerId == playerId {
            if videoPlayerFullScreenView != nil {
                if let playerView = self.videoPlayerFullScreenView {
                    DispatchQueue.main.async {
                        let duration: Double = playerView.getDuration()
                        call.success([ "result": true, "method": "getDuration", "value": duration])
                        return
                    }
                } else {
                    let error: String = "Fullscreen playerId not found"
                    print(error)
                    call.success([ "result": false, "method": "getDuration", "message": error])
                    return
                }
            } else {
                let error: String = "No videoPlayerFullScreenView"
                print(error)
                call.success([ "result": false, "method": "getDuration", "message": error])
                return
            }

        }
    }

    // MARK: - get CurrentTime for the given player

    @objc func getCurrentTime(_ call: CAPPluginCall) {
        self.call = call

        guard let playerId = call.options["playerId"] as? String else {
            let error: String = "Must provide a playerId"
            print(error)
            call.success([ "result": false, "method": "getCurrentTime", "message": error])
            return
        }
        if self.mode == "fullscreen" && self.fsPlayerId == playerId {
            if videoPlayerFullScreenView != nil {
                if let playerView = self.videoPlayerFullScreenView {
                    DispatchQueue.main.async {
                        let currentTime: Double = playerView.getCurrentTime()
                        call.success([ "result": true, "method": "getCurrentTime", "value": currentTime])
                        return
                    }
                } else {
                    let error: String = "Fullscreen playerId not found"
                    print(error)
                    call.success([ "result": false, "method": "getCurrentTime", "message": error])
                    return
                }
            } else {
                let error: String = "No videoPlayerFullScreenView"
                print(error)
                call.success([ "result": false, "method": "getCurrentTime", "message": error])
                return
            }
        }
    }

    // MARK: - set CurrentTime for the given player

    @objc func setCurrentTime(_ call: CAPPluginCall) {
        self.call = call

        guard let playerId = call.options["playerId"] as? String else {
            let error: String = "Must provide a playerId"
            print(error)
            call.success([ "result": false, "method": "setCurrentTime", "message": error])
            return
        }
        guard let seekTime = call.options["seektime"] as? Double else {
            let error: String = "Must provide a time in second"
            print(error)
            call.success([ "result": false, "method": "setCurrentTime", "message": error])
            return
        }
        if self.mode == "fullscreen" && self.fsPlayerId == playerId {
            if videoPlayerFullScreenView != nil {
                if let playerView = self.videoPlayerFullScreenView {
                    DispatchQueue.main.async {
                        playerView.setCurrentTime(time: seekTime)
                        call.success([ "result": true, "method": "setCurrentTime", "value": seekTime])
                        return
                    }
                } else {
                    let error: String = "Fullscreen playerId not found"
                    print(error)
                    call.success([ "result": false, "method": "setCurrentTime", "message": error])
                    return
                }
            } else {
                let error: String = "No videoPlayerFullScreenView"
                print(error)
                call.success([ "result": false, "method": "setCurrentTime", "message": error])
                return
            }

        }
    }

    // MARK: - get Volume for the given player

    @objc func getVolume(_ call: CAPPluginCall) {
        self.call = call

        guard let playerId = call.options["playerId"] as? String else {
            let error: String = "Must provide a playerId"
            print(error)
            call.success([ "result": false, "method": "getVolume", "message": error])
            return
        }
        if self.mode == "fullscreen" && self.fsPlayerId == playerId {
            if videoPlayerFullScreenView != nil {
                if let playerView = self.videoPlayerFullScreenView {
                    DispatchQueue.main.async {
                        let volume: Float = playerView.getVolume()
                        call.success([ "result": true, "method": "getVolume", "value": volume])
                        return
                    }
                } else {
                    let error: String = "Fullscreen playerId not found"
                    print(error)
                    call.success([ "result": false, "method": "getVolume", "message": error])
                    return
                }
            } else {
                let error: String = "No videoPlayerFullScreenView"
                print(error)
                call.success([ "result": false, "method": "getVolume", "message": error])
                return
            }
        }
    }

    // MARK: - set Volume for the given player

    @objc func setVolume(_ call: CAPPluginCall) {
        self.call = call

        guard let playerId = call.options["playerId"] as? String else {
            let error: String = "Must provide a playerId"
            print(error)
            call.success([ "result": false, "method": "setVolume", "message": error])
            return
        }
        guard let volume = call.options["volume"] as? Float else {
            let error: String = "Must provide a volume value"
            print(error)
            call.success([ "result": false, "method": "setVolume", "message": error])

            return
        }
        if self.mode == "fullscreen" && self.fsPlayerId == playerId {
           if videoPlayerFullScreenView != nil {
               if let playerView = self.videoPlayerFullScreenView {
                   DispatchQueue.main.async {
                       playerView.setVolume(volume: volume)
                       call.success([ "result": true, "method": "setVolume", "value": volume])
                    return
                   }
               } else {
                   let error: String = "Fullscreen playerId not found"
                   print(error)
                   call.success([ "result": false, "method": "setVolume", "message": error])
                   return
               }
           } else {
               let error: String = "No videoPlayerFullScreenView"
               print(error)
               call.success([ "result": false, "method": "setVolume", "message": error])
               return
           }
       }

    }

    // MARK: - get Muted for the given player

    @objc func getMuted(_ call: CAPPluginCall) {
        self.call = call

        guard let playerId = call.options["playerId"] as? String else {
            let error: String = "Must provide a playerId"
            print(error)
            call.success([ "result": false, "method": "getMuted", "message": error])
            return
        }
        if self.mode == "fullscreen" && self.fsPlayerId == playerId {
            if videoPlayerFullScreenView != nil {
                if let playerView = self.videoPlayerFullScreenView {
                    DispatchQueue.main.async {
                        let muted: Bool = playerView.getMuted()
                        call.success([ "result": true, "method": "getMuted", "value": muted])
                        return
                    }
                } else {
                    let error: String = "Fullscreen playerId not found"
                    print(error)
                    call.success([ "result": false, "method": "getMuted", "message": error])
                    return
                }
            } else {
                let error: String = "No videoPlayerFullScreenView"
                print(error)
                call.success([ "result": false, "method": "getMuted", "message": error])
                return
            }
        }
    }

    // MARK: - set Muted for the given player

    @objc func setMuted(_ call: CAPPluginCall) {
        self.call = call

        guard let playerId = call.options["playerId"] as? String else {
            let error: String = "Must provide a playerId"
            print(error)
            call.success([ "result": false, "method": "setMuted", "message": error])
            return
        }
        guard let muted = call.options["muted"] as? Bool else {
            let error: String = "Must provide a boolean true/false"
            print(error)
            call.success([ "result": false, "method": "setMuted", "message": error])
            return
        }
        if self.mode == "fullscreen" && self.fsPlayerId == playerId {
            if videoPlayerFullScreenView != nil {
                if let playerView = self.videoPlayerFullScreenView {
                    DispatchQueue.main.async {
                        playerView.setMuted(muted: muted)
                        call.success([ "result": true, "method": "setMuted", "value": muted])
                        return
                    }
                } else {
                    let error: String = "Fullscreen playerId not found"
                    print(error)
                    call.success([ "result": false, "method": "setMuted", "message": error])
                    return
                }
            } else {
                let error: String = "No videoPlayerFullScreenView"
                print(error)
                call.success([ "result": false, "method": "setMuted", "message": error])

                return
            }
        }
    }

    // MARK: - Stop all player(s) playing

    @objc func stopAllPlayers(_ call: CAPPluginCall) {
        self.call = call
        if self.mode == "fullscreen" {
            if self.videoPlayerFullScreenView != nil {
                if let playerView = self.videoPlayerFullScreenView {
                    DispatchQueue.main.async {
                        if playerView.isPlaying {
                            playerView.pause()
                        }
                        call.success([ "result": true, "method": "stopAllPlayers", "value": true])
                        return
                    }
                } else {
                   let error: String = "Fullscreen player not found"
                   print(error)
                   call.success([ "result": false, "method": "stopAllPlayers", "message": error])
                   return
                }
            } else {
               let error: String = "No videoPlayerFullScreenView"
               call.success([ "result": false, "method": "stopAllPlayers", "message": error])
               return

            }

        }
    }
    // MARK: - Handle Notifications

    @objc func playerItemPlay(notification: Notification) {
        guard let info = notification.userInfo as? [String: Any] else { return }
        DispatchQueue.main.async {
            self.notifyListeners("jeepCapVideoPlayerPlay", data: info, retainUntilConsumed: true)
            return
        }
    }
    @objc func playerItemPause(notification: Notification) {
        guard let info = notification.userInfo as? [String: Any] else { return }
        DispatchQueue.main.async {
            self.notifyListeners("jeepCapVideoPlayerPause", data: info, retainUntilConsumed: true)
            return
        }
    }

    @objc func playerItemEnd(notification: Notification) {
        guard let info = notification.userInfo as? [String: Any] else { return }
        DispatchQueue.main.async {
            self.notifyListeners("jeepCapVideoPlayerEnded", data: info, retainUntilConsumed: true)
            if self.mode == "fullscreen" {
                self.playerFullscreenExit()
            }

            return
        }
    }
    @objc func playerItemReady(notification: Notification) {
        guard let info = notification.userInfo as? [String: Any] else { return }
        guard let playerId = info["fromPlayerId"] as? String else { return}
        DispatchQueue.main.async {
            self.notifyListeners("jeepCapVideoPlayerReady", data: info, retainUntilConsumed: true)
            if self.mode == "fullscreen" && playerId == self.fsPlayerId {
                self.videoPlayerFullScreenView?.play()
            }
            return
        }
    }

    @objc func playerFullscreenDismiss(notification: Notification) {
        let info: [String: Any] = ["dismiss": true]
        DispatchQueue.main.async {
            self.notifyListeners("jeepCapVideoPlayerExit", data: info, retainUntilConsumed: true)
            if self.mode == "fullscreen" {
                self.playerFullscreenExit()
            }
            return
        }
    }

    func playerFullscreenExit() {
        self.videoPlayerFullScreenView?.removeObservers()
        self.bridge.viewController.dismiss(animated: true, completion: {
            do {
                // DeActivate the audio session.
                try self.audioSession?.setActive(false)
                self.audioSession = nil
            } catch {
                let error: String = "playerFullscreenExit: Failed to deactivate audio session category"
                print(error)
            }

        })
        return
    }
    func videoPathInternalReady(notification: Notification) {
        self.bridge.viewController.dismiss(animated: true, completion: {
        })
        guard let info = notification.userInfo as? [String: Any] else { return }
        guard let videoUrl = info["videoUrl"] as? URL else {
            NotificationCenter.default.post(name: .playerFullscreenDismiss, object: nil)
            return
        }
        guard let call = self.call else { return }
        self.createVideoPlayerFullScreenView(call: call, videoUrl: videoUrl)
        return
    }

    // MARK: - isFileExists

    func isFileExists(filePath: String) -> Bool {
        var ret: Bool = false
        let fileManager = FileManager.default
        if fileManager.fileExists(atPath: filePath) {
            ret = true
        }
        return ret
    }

}
// swiftlint:enable type_body_length
// swiftlint:enable file_length
