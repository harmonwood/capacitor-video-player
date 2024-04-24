//
//  CapacitorVideoPlayerPlugin.Observers.swift
//  Plugin
//
//  Created by  Quéau Jean Pierre on 30/05/2021.
//  Copyright © 2021 Max Lynch. All rights reserved.
//

import Foundation
import UIKit
import AVFoundation

extension CapacitorVideoPlayerPlugin {

    // MARK: - Add Observers

    // swiftlint:disable function_body_length
    @objc func addObserversToNotificationCenter() {
        // add Observers

        playObserver = NotificationCenter.default
            .addObserver(forName: .playerItemPlay, object: nil, queue: nil,
                         using: playerItemPlay)
        pauseObserver = NotificationCenter.default
            .addObserver(forName: .playerItemPause, object: nil,
                         queue: nil, using: playerItemPause)
        endObserver = NotificationCenter.default
            .addObserver(forName: .playerItemEnd, object: nil, queue: nil,
                         using: playerItemEnd)
        readyObserver = NotificationCenter.default
            .addObserver(forName: .playerItemReady, object: nil,
                         queue: nil, using: playerItemReady)
        fsDismissObserver = NotificationCenter.default
            .addObserver(forName: .playerFullscreenDismiss, object: nil,
                         queue: nil, using: playerFullscreenDismiss)
        vpInternalObserver = NotificationCenter.default
            .addObserver(forName: .videoPathInternalReady, object: nil,
                         queue: nil, using: videoPathInternalReady)

        backgroundObserver =
            NotificationCenter.default.addObserver(
                forName: UIApplication.didEnterBackgroundNotification,
                object: nil, queue: nil) { (_) in
                if self.backModeEnabled {
                    isInBackgroundMode = true
                    if !isInPIPMode &&
                        self.videoPlayerFullScreenView != nil {
                        self.videoPlayerFullScreenView?
                            .videoPlayer.player = nil

                        if let playerItem = self.videoPlayerFullScreenView?
                            .playerItem {
                            self.videoTrackEnable(playerItem: playerItem,
                                                  enable: false)
                        }
                    }
                } else {
                    if self.videoPlayerFullScreenView != nil {

                        self.videoPlayerFullScreenView?
                            .videoPlayer.player?.pause()
                    }
                }
            }
        foregroundObserver =
            NotificationCenter.default.addObserver(forName: UIApplication.willEnterForegroundNotification, object: nil,
                                                   queue: OperationQueue.main) { (_) in

                if self.backModeEnabled {
                    isInBackgroundMode = false
                    if !isInPIPMode && self.bgPlayer != nil &&
                        self.videoPlayerFullScreenView != nil {
                        //enable video track
                        if let playerItem =
                            self.videoPlayerFullScreenView?.playerItem {
                            self.videoTrackEnable(playerItem: playerItem,
                                                  enable: true)
                        }

                        self.videoPlayerFullScreenView?
                            .videoPlayer.player = self.bgPlayer
                    }
                } else {
                    if self.videoPlayerFullScreenView != nil {

                        if let playerItem =
                            self.videoPlayerFullScreenView?.playerItem {
                            self.videoTrackEnable(playerItem: playerItem,
                                                  enable: true)
                        }
                        self.videoPlayerFullScreenView?
                            .videoPlayer.player?.play()
                    }

                }
            }
    }
    // swiftlint:enable function_body_length

    // MARK: - videoTrackEnable

    func videoTrackEnable(playerItem: AVPlayerItem, enable: Bool) {
        let tracks = playerItem.tracks
        for playerItemTrack in tracks {
            // Find the video tracks.
            if let mediaType = playerItemTrack.assetTrack?.mediaType {
                if mediaType == AVMediaType.video {
                    // Enable/Disable the track.
                    playerItemTrack.isEnabled = enable
                }
            }
        }

    }

    // MARK: - videoPathInternalReady

    // swiftlint:disable function_body_length
    func videoPathInternalReady(notification: Notification) {
        self.bridge?.viewController?.dismiss(animated: true, completion: {
        })
        guard let info = notification.userInfo as? [String: Any]
        else { return }
        guard let videoUrl = info["videoUrl"] as? URL else {
            NotificationCenter.default.post(
                name: .playerFullscreenDismiss, object: nil)
            return
        }
        guard let videoRate = info["videoRate"] as? Float else {
            NotificationCenter.default.post(
                name: .playerFullscreenDismiss, object: nil)
            return
        }
        guard let exitOnEnd = info["exitOnEnd"] as? Bool else {
            NotificationCenter.default.post(
                name: .playerFullscreenDismiss, object: nil)
            return
        }
        guard let loopOnEnd = info["loopOnEnd"] as? Bool else {
            NotificationCenter.default.post(
                name: .playerFullscreenDismiss, object: nil)
            return
        }
        guard let pipEnabled = info["pipEnabled"] as? Bool else {
            NotificationCenter.default.post(
                name: .playerFullscreenDismiss, object: nil)
            return
        }
        guard let showControls = info["showControls"] as? Bool else {
            NotificationCenter.default.post(
                name: .playerFullscreenDismiss, object: nil)
            return
        }
        guard let displayMode = info["displayMode"] as? String else {
            NotificationCenter.default.post(
                name: .playerFullscreenDismiss, object: nil)
            return
        }
        guard let backModeEnabled = info["backModeEnabled"] as? Bool else {
            NotificationCenter.default.post(
                name: .playerFullscreenDismiss, object: nil)
            return
        }
        guard let call = self.call else { return }

        self.createVideoPlayerFullscreenView(
            call: call, videoUrl: videoUrl,
            rate: videoRate, exitOnEnd: exitOnEnd,
            loopOnEnd: loopOnEnd, pipEnabled: pipEnabled,
            backModeEnabled: backModeEnabled,
            showControls: showControls,
            displayMode: displayMode,
            subTitleUrl: nil, subTitleLanguage: nil,
            subTitleOptions: nil, headers: nil,
            title: nil, smallTitle: nil, artwork: nil, drm: nil)
        return
    }
    // swiftlint:enable function_body_length

}
