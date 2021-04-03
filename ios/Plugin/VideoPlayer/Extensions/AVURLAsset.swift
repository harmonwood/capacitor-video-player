//
//  AVURLAsset.swift
//  CapacitorVideoPlayer
//
//  Created by  Quéau Jean Pierre on 02/04/2021.
//

import AVKit

extension AVURLAsset {

    var videoSize: CGSize? {
        tracks(withMediaType: .video).first.flatMap {
            tracks.count > 0 ? $0.naturalSize.applying($0.preferredTransform) : nil
        }
    }
}
