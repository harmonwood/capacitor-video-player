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
                                             subTitleUrl: URL?,
                                             language: String?,
                                             options: [String: Any]?

    ) -> FullScreenVideoPlayerView {

        let videoPlayerFullScreenView = FullScreenVideoPlayerView(
            url: videoUrl, rate: rate, stUrl: subTitleUrl,
            stLanguage: language, stOptions: options,
            playerId: playerId, exitOnEnd: exitOnEnd)
        return videoPlayerFullScreenView
    }
    // swiftlint:enable function_parameter_count

    @objc public func pickVideoFromInternal(rate: Float, exitOnEnd: Bool) ->
    VideoPickerViewController {
        let videoPickerViewController = VideoPickerViewController()
        videoPickerViewController.rate = rate
        videoPickerViewController.exitOnEnd = exitOnEnd
        return videoPickerViewController

    }
}
