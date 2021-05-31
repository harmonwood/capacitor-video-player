//
//  CapacitorVideoPlayerPlugin.Notifications.swift
//  Plugin
//
//  Created by  Quéau Jean Pierre on 30/05/2021.
//  Copyright © 2021 Max Lynch. All rights reserved.
//

import Foundation

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
        let info: [String: Any] = ["dismiss": true]
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
            vPFSV.videoPlayer.player = nil
            self.videoPlayerFullScreenView = nil
        }
        self.bridge?.viewController?.dismiss(animated: true, completion: {
            do {
                // DeActivate the audio session.
                try self.audioSession?.setActive(false)
                self.audioSession = nil
            } catch {
                let error: String = "playerFullscreenExit: Failed to deactivate " +
                    "audio session category"
                print(error)
            }

        })
        return
    }

}
