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
    @objc public func createFullscreenPlayer(playerId: String, videoUrl: URL,
                                             subTitleUrl: URL?, language: String?,
                                             options: [String: Any]?
    ) -> FullScreenVideoPlayerView {

        let videoPlayerFullScreenView = FullScreenVideoPlayerView(
            url: videoUrl, stUrl: subTitleUrl, stLanguage: language,
            stOptions: options, playerId: playerId,
            exitOnEnd: true)
        return videoPlayerFullScreenView
    }
    @objc public func pickVideoFromInternal() -> VideoPickerViewController {
        let videoPickerViewController = VideoPickerViewController()
        return videoPickerViewController

    }
}
