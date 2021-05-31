//
//  CapacitorVideoPlayerPlugin.Observers.swift
//  Plugin
//
//  Created by  Quéau Jean Pierre on 30/05/2021.
//  Copyright © 2021 Max Lynch. All rights reserved.
//

import Foundation
import UIKit

extension CapacitorVideoPlayerPlugin {

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

    // MARK: - videoPathInternalReady

    func videoPathInternalReady(notification: Notification) {
        self.bridge?.viewController?.dismiss(animated: true, completion: {
        })
        guard let info = notification.userInfo as? [String: Any] else { return }
        guard let videoUrl = info["videoUrl"] as? URL else {
            NotificationCenter.default.post(name: .playerFullscreenDismiss, object: nil)
            return
        }
        guard let call = self.call else { return }
        self.createVideoPlayerFullscreenView(call: call,
                                             videoUrl: videoUrl,
                                             subTitleUrl: nil,
                                             subTitleLanguage: nil,
                                             subTitleOptions: nil)
        return
    }

}
