//
//  AllOrientationAVPlayerController.swift
//  CapacitorVideoPlayer
//
//  Created by Manuel Garcia Marin on 24-05-23.
//

import AVKit
class AllOrientationAVPlayerController: AVPlayerViewController {
    override var supportedInterfaceOrientations: UIInterfaceOrientationMask {
        return .all    }
}
