//
//  FullScreenVideoPlayerView.swift
//  Plugin
//
//  Created by  Quéau Jean Pierre on 13/01/2020.
//  Copyright © 2020 Max Lynch. All rights reserved.
//

import UIKit
import AVKit

class FullScreenVideoPlayerView: UIView {
    private var _videoPath: String
    var player: AVPlayer!
    var videoPlayer: AVPlayerViewController!

    init(videoPath: String) {
        self._videoPath = videoPath
        super.init(frame: .zero)
      
        initializePlayer()
    }

    // MARK: - Init Player

    private func initializePlayer() {
        self.videoPlayer = AVPlayerViewController()
        let url = URL(string: self._videoPath)
        self.player = AVPlayer(url:url!)
        self.videoPlayer.player = self.player
        /* this does not work
        // Set timeControlStatus Observer
        self.videoPlayer.player!.addObserver(self, forKeyPath: "timeControlStatus", options: NSKeyValueObservingOptions.new, context: nil)
        */
    }
    
/* not required as it does not work
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        print("in observeValue keyPath \(String(describing: keyPath))")
        let videoId = ["videoId":"fullscreen"] as [String : String]
        print("in observeValue timeControlStatus \(keyPath!)")
        if keyPath == "timeControlStatus" {
            if self.videoPlayer.player!.timeControlStatus == .playing  {
                print("Playing Fullscreen")
 //               NotificationCenter.default.post(name: .playerItemPlay, object:videoId)
                NotificationCenter.default.post(name: .playerItemPlay, object: nil, userInfo: videoId)
            } else {
                print("Stop Fullscreen")
//                NotificationCenter.default.post(name: .playerItemPause, object:videoId)
                NotificationCenter.default.post(name: .playerItemPause, object: nil, userInfo: videoId)
            }
        }
     }
*/
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

}
