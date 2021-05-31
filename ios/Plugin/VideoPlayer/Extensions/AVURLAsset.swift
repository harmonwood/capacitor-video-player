//
//  AVURLAsset.swift
//  Plugin
//
//  Created by  Quéau Jean Pierre on 02/04/2021.
//  Copyright © 2021 Max Lynch. All rights reserved.
//

import AVKit

extension AVURLAsset {

    var videoSize: CGSize? {
        tracks(withMediaType: .video).first.flatMap {
            tracks.count > 0 ? $0.naturalSize.applying($0.preferredTransform) : nil
        }
    }
}
