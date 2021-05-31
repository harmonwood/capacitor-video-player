//
//  CapacitorVideoPlayerPlugin.Fullscreen.swift
//  Plugin
//
//  Created by  Quéau Jean Pierre on 30/05/2021.
//  Copyright © 2021 Max Lynch. All rights reserved.
//

import Foundation
import Capacitor
import AVKit

extension CapacitorVideoPlayerPlugin {

    // MARK: - createVideoPlayerFullScreenView

    func createVideoPlayerFullscreenView(call: CAPPluginCall, videoUrl: URL,
                                         subTitleUrl: URL?, subTitleLanguage: String?,
                                         subTitleOptions: [String: Any]?) {
        DispatchQueue.main.async { [weak self] in
            let playerId: String = self?.fsPlayerId ?? "fullscreen"
            if let fullscreenView = self?.implementation
                .createFullscreenPlayer(playerId: playerId, videoUrl: videoUrl,
                                        subTitleUrl: subTitleUrl,
                                        language: subTitleLanguage,
                                        options: subTitleOptions) {
                self?.videoPlayerFullScreenView = fullscreenView
                self?.bgPlayer = self?.videoPlayerFullScreenView?.videoPlayer.player
                guard let videoPlayer: AVPlayerViewController =
                        self?.videoPlayerFullScreenView?.videoPlayer else {
                    let error: String = "No videoPlayer available"
                    print(error)
                    call.resolve([ "result": false, "method": "createVideoPlayerFullScreenView",
                                   "message": error])
                    return
                }

                self?.bridge?.viewController?.present(videoPlayer, animated: true, completion: {
                    // add audio session
                    self?.audioSession = AVAudioSession.sharedInstance()
                    // Set the audio session category, mode, and options.
                    try? self?.audioSession?.setCategory(.playback, mode: .moviePlayback,
                                                         options: [.mixWithOthers,
                                                                   .allowAirPlay])
                    // Activate the audio session.
                    try? self?.audioSession?.setActive(true)
                    call.resolve([ "result": true,
                                   "method": "createVideoPlayerFullScreenView",
                                   "value": true])
                    return

                })
            }
        }
    }

}
