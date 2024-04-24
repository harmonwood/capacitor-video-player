import Foundation
import AVKit
import UIKit
enum CapacitorVideoPlayerError: Error {
    case failed(message: String)
}

@objc public class CapacitorVideoPlayer: NSObject {

    @objc public func echo(_ value: String) -> String {
        return value
    }
    // swiftlint:disable function_parameter_count
    @objc public func createFullscreenPlayer(playerId: String,
                                             videoUrl: URL,
                                             rate: Float,
                                             exitOnEnd: Bool,
                                             loopOnEnd: Bool,
                                             pipEnabled: Bool,
                                             showControls: Bool,
                                             displayMode: String,
                                             subTitleUrl: URL?,
                                             language: String?,
                                             headers: [String: String]?,
                                             options: [String: Any]?,
                                             title: String?,
                                             smallTitle: String?,
                                             artwork: String?,
                                             drm: [String: Any]?
    ) -> FullScreenVideoPlayerView {

        let videoPlayerFullScreenView = FullScreenVideoPlayerView(
            url: videoUrl, rate: rate, playerId: playerId,
            exitOnEnd: exitOnEnd, loopOnEnd: loopOnEnd,
            pipEnabled: pipEnabled, showControls: showControls,
            displayMode: displayMode,
            stUrl: subTitleUrl, stLanguage: language,
            stHeaders: headers, stOptions: options,
            title: title, smallTitle: smallTitle, artwork: artwork, drm: drm)
        return videoPlayerFullScreenView
    }

    // swiftlint:disable function_parameter_count
    @objc public func pickVideoFromInternal(rate: Float, exitOnEnd: Bool,
                                            loopOnEnd: Bool,
                                            pipEnabled: Bool,
                                            backModeEnabled: Bool,
                                            showControls: Bool,
                                            displayMode: String) ->
    VideoPickerViewController {
        let videoPickerViewController = VideoPickerViewController()
        videoPickerViewController.rate = rate
        videoPickerViewController.exitOnEnd = exitOnEnd
        videoPickerViewController.loopOnEnd = loopOnEnd
        videoPickerViewController.pipEnabled = pipEnabled
        videoPickerViewController.backModeEnabled = backModeEnabled
        videoPickerViewController.showControls = showControls
        videoPickerViewController.displayMode = displayMode

        return videoPickerViewController

    }
    // swiftlint:enable function_parameter_count
}
