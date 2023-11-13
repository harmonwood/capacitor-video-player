//
//  CapacitorVideoPlayerPlugin.Notifications.swift
//  Plugin
//
//  Created by  Quéau Jean Pierre on 30/05/2021.
//  Copyright © 2021 Max Lynch. All rights reserved.
//

import Foundation
import MediaPlayer

// MARK: - Handle Notifications

extension CapacitorVideoPlayerPlugin {

    // MARK: - playerItemPlay

    @objc func playerItemPlay(notification: Notification) {
        guard let info = notification.userInfo as? [String: Any] else { return }
        DispatchQueue.main.async {
            self.notifyListeners("jeepCapVideoPlayerPlay", data: info, retainUntilConsumed: true)
            return
        }
    }

    // MARK: - playerItemPause

    @objc func playerItemPause(notification: Notification) {
        guard let info = notification.userInfo as? [String: Any] else { return }
        DispatchQueue.main.async {
            self.notifyListeners("jeepCapVideoPlayerPause", data: info, retainUntilConsumed: true)
            return
        }
    }

    // MARK: - playerItemEnd

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

    // MARK: - playerItemReady

    @objc func playerItemReady(notification: Notification) {
        guard let info = notification.userInfo as? [String: Any] else { return }
        guard let playerId = info["fromPlayerId"] as? String else { return}
        DispatchQueue.main.async {
            self.notifyListeners("jeepCapVideoPlayerReady", data: info, retainUntilConsumed: true)
            if self.mode == "fullscreen" && playerId == self.fsPlayerId {
                if let vPFSV = self.videoPlayerFullScreenView {
                    vPFSV.play()
                }
            }
            return
        }
    }

    // MARK: - playerFullscreenDismiss

    @objc func playerFullscreenDismiss(notification: Notification) {

        var currentTime: Double = 0.0
        if let playerView = self.videoPlayerFullScreenView {
            currentTime = playerView.getRealCurrentTime()
        }
        let info: [String: Any] = ["dismiss": true, "currentTime": currentTime]
        DispatchQueue.main.async {
            if self.mode == "fullscreen" {
                if let vPFSV = self.videoPlayerFullScreenView {
                    vPFSV.pause()
                }
                self.playerFullscreenExit()
            }
            self.notifyListeners("jeepCapVideoPlayerExit", data: info, retainUntilConsumed: true)
            return
        }
    }

    // MARK: - playerFullscreenExit

    func playerFullscreenExit() {
        if let vPFSV = self.videoPlayerFullScreenView {
            vPFSV.removeObservers()
            self.terminateNowPlayingInfo()
            vPFSV.videoPlayer.player = nil
            vPFSV.player = nil
            /*
            if self.displayMode == "landscape" {
                vPFSV.videoPlayer = PortraitAVPlayerController()
            } else if self.displayMode == "portrait" {
                vPFSV.videoPlayer = LandscapeAVPlayerController()
            } else {
                vPFSV.videoPlayer = AllOrientationAVPlayerController()
            }
            */
            self.videoPlayerFullScreenView = nil
        }
        if let viewController = self.bridge?.viewController {
            viewController.dismiss(animated: true, completion: {
                if self.backModeEnabled {
                    if let audioSession = self.audioSession {
                        do {
                            try audioSession.setActive(false)
                            self.audioSession = nil
                        } catch {
                            let error: String = "playerFullscreenExit: Failed to deactivate audio session category"
                            print(error)
                        }
                    }
                }
            })
        }
    }

    private func terminateNowPlayingInfo() {
        if let vPFSV = self.videoPlayerFullScreenView {
            if let token = vPFSV.periodicTimeObserver {
                vPFSV.player?.removeTimeObserver(token)
                vPFSV.periodicTimeObserver = nil
            }
            let rcc = MPRemoteCommandCenter.shared()
            rcc.changePlaybackPositionCommand.isEnabled = false
            rcc.playCommand.isEnabled = false
            rcc.pauseCommand.isEnabled = false
            rcc.skipForwardCommand.isEnabled = false
            rcc.skipBackwardCommand.isEnabled = false
            MPNowPlayingInfoCenter.default().nowPlayingInfo = [:]
            MPNowPlayingInfoCenter.default().nowPlayingInfo = nil
        }
    }

}
