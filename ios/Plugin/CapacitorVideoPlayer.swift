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
                                             options: [String: Any]?

    ) -> FullScreenVideoPlayerView {

        let videoPlayerFullScreenView = FullScreenVideoPlayerView(
            url: videoUrl, rate: rate, playerId: playerId, exitOnEnd: exitOnEnd,
            loopOnEnd: loopOnEnd, pipEnabled: pipEnabled, stUrl: subTitleUrl,
            stLanguage: language, stOptions: options)
        return videoPlayerFullScreenView
    }
    // swiftlint:enable function_parameter_count

    @objc public func pickVideoFromInternal(rate: Float, exitOnEnd: Bool,
                                            loopOnEnd: Bool, pipEnabled: Bool) ->
    VideoPickerViewController {
        let videoPickerViewController = VideoPickerViewController()
        videoPickerViewController.rate = rate
        videoPickerViewController.exitOnEnd = exitOnEnd
        videoPickerViewController.loopOnEnd = loopOnEnd
        videoPickerViewController.exitOnEnd = pipEnabled
        return videoPickerViewController

    }
}
