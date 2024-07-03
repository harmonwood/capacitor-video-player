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

    // swiftlint:disable function_body_length
    // swiftlint:disable function_parameter_count
    func createVideoPlayerFullscreenView(
        call: CAPPluginCall, videoUrl: URL, rate: Float,
        exitOnEnd: Bool, loopOnEnd: Bool, pipEnabled: Bool,
        backModeEnabled: Bool, showControls: Bool,
        displayMode: String,
        subTitleUrl: URL?, subTitleLanguage: String?,
        subTitleOptions: [String: Any]?,
        headers: [String: String]?, title: String?,
        smallTitle: String?, artwork: String?, drm: [String: Any]?) {
        DispatchQueue.main.async { [weak self] in
            let playerId: String = self?.fsPlayerId ?? "fullscreen"
            if let fullscreenView = self?.implementation
                .createFullscreenPlayer(
                    playerId: playerId, videoUrl: videoUrl,
                    rate: rate, exitOnEnd: exitOnEnd, loopOnEnd: loopOnEnd,
                    pipEnabled: pipEnabled,
                    showControls: showControls,
                    displayMode: displayMode,
                    subTitleUrl: subTitleUrl,
                    language: subTitleLanguage, headers: headers, options: subTitleOptions,
                    title: title, smallTitle: smallTitle, artwork: artwork, drm: drm) {
                self?.videoPlayerFullScreenView = fullscreenView
                if backModeEnabled {
                    self?.bgPlayer = self?.videoPlayerFullScreenView?
                        .videoPlayer.player
                }
                guard let videoPlayer: AVPlayerViewController =
                        self?.videoPlayerFullScreenView?.videoPlayer else {
                    let error: String = "No videoPlayer available"
                    print(error)
                    call.resolve([ "result": false, "method": "createVideoPlayerFullScreenView",
                                   "message": error])
                    return
                }
                videoPlayer.delegate = self
                self?.bridge?.viewController?.present(
                    videoPlayer, animated: true, completion: {
                        if backModeEnabled {
                            // add audio session
                            self?.audioSession = AVAudioSession.sharedInstance()
                            // Set the audio session category, mode, and options.
                            do {
                                try self?.audioSession?
                                    .setCategory(.playback,
                                                 mode: .moviePlayback,
                                                 options: [])
                                try self?.audioSession?.setActive(true)
                                call.resolve([
                                                "result": true,
                                                "method": "createVideoPlayerFullScreenView",
                                                "value": true])
                                return
                            } catch let error as NSError {
                                print("Unable to activate audio session:  \(error.localizedDescription)")
                                call.resolve([
                                                "result": false,
                                                "method": "createVideoPlayerFullScreenView",
                                                "message": error.localizedDescription])
                                return
                            }
                        } else {
                            call.resolve([
                                            "result": true,
                                            "method": "createVideoPlayerFullScreenView",
                                            "value": true])
                            return
                        }
                    })

            }
        }
    }
    // swiftlint:enable function_parameter_count
    // swiftlint:enable function_body_length

}
