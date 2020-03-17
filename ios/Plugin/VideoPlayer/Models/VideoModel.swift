//
//  VideoModel.swift
//  Plugin
//
//  Created by  Quéau Jean Pierre on 13/01/2020.
//  Copyright © 2020 Max Lynch. All rights reserved.
//

import UIKit

class VideoModel: NSObject {
    var videos:[Video] = []
    
    func addVideo(video:Video) {
        videos.append(video)
    }
    
    func getVideos() -> [Video] {
        return videos
    }
    
    func printVideos() {
        for video in self.videos {
            print("Video \(video.urlPath!) \n \(video.title!) \n \(video.content!) \n \(video.width!) \(video.height!)")
        }

    }
}
