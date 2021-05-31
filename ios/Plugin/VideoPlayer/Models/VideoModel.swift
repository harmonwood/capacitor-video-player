//
//  VideoModel.swift
//  Plugin
//
//  Created by  Quéau Jean Pierre on 13/01/2020.
//  Copyright © 2021 Max Lynch. All rights reserved.
//

import UIKit

class VideoModel: NSObject {
    var videos: [Video] = []

    func addVideo(video: Video) {
        videos.append(video)
    }

    func getVideos() -> [Video] {
        return videos
    }

    func printVideos() {
        for video in self.videos {
            guard let urlPath: String = video.urlPath else {
                continue
            }
            guard let title: String = video.title else {
                continue
            }
            guard let content: String = video.content else {
                continue
            }
            guard let width: Int = video.width else {
                continue
            }
            guard let height: Int = video.height else {
                continue
            }
            print("Video \(urlPath) \n \(title) \n \(content) \n \(width) \(height)")
        }
    }
}
