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
                                             subTitleUrl: URL?,
                                             language: String?,
                                             headers: [String: String]?,
                                             options: [String: Any]?

    ) -> FullScreenVideoPlayerView {

        let videoPlayerFullScreenView = FullScreenVideoPlayerView(
            url: videoUrl, rate: rate, playerId: playerId,
            exitOnEnd: exitOnEnd, loopOnEnd: loopOnEnd,
            pipEnabled: pipEnabled, stUrl: subTitleUrl,
            stLanguage: language, stHeaders: headers, stOptions: options)
        return videoPlayerFullScreenView
    }
    // swiftlint:enable function_parameter_count

    @objc public func pickVideoFromInternal(rate: Float, exitOnEnd: Bool,
                                            loopOnEnd: Bool,
                                            pipEnabled: Bool,
                                            backModeEnabled: Bool) ->
    VideoPickerViewController {
        let videoPickerViewController = VideoPickerViewController()
        videoPickerViewController.rate = rate
        videoPickerViewController.exitOnEnd = exitOnEnd
        videoPickerViewController.loopOnEnd = loopOnEnd
        videoPickerViewController.pipEnabled = pipEnabled
        videoPickerViewController.backModeEnabled = backModeEnabled
        return videoPickerViewController

    }
}
