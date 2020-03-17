//
//  Video.swift
//  Plugin
//
//  Created by  Quéau Jean Pierre on 13/01/2020.
//  Copyright © 2020 Max Lynch. All rights reserved.
//

import Foundation
class Video: NSObject {
    var urlPath: String?
    var imgPath: String?
    var title: String?
    var content: String?
    var width: Int?
    var height: Int?
    override init() {
        
    }
    init(urlPath:String, imgPath:String?, title:String, content:String, width:Int, height:Int) {
        self.urlPath = urlPath
        self.imgPath = imgPath
        self.title = title
        self.content = content
        self.width = width
        self.height = height
    }
}
