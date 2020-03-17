//
//  VideoPlayerTableViewCell.swift
//  Plugin
//
//  Created by  Quéau Jean Pierre on 13/01/2020.
//  Copyright © 2020 Max Lynch. All rights reserved.
//

import UIKit

class VideoPlayerTableViewCell: UITableViewCell {
    
    private var _videoWidth: Int?
    private var _videoHeight: Int?
    
    // MARK: - Set-up videoWidth
    
    var videoWidth: Int? {
        get {
            return self._videoWidth
        }
        set {
            self._videoWidth = newValue
        }
    }
    
    // MARK: - Set-up videoHeight
    
    var videoHeight: Int? {
        get {
            return self._videoHeight
        }
        set {
            self._videoHeight = newValue
        }
    }
    
    // MARK: - Set-up titleLabel
    
    let titleLabel : UILabel = {
        let lbl = UILabel()
        lbl.textColor = .black
        lbl.font = UIFont.boldSystemFont(ofSize: 20)
        lbl.textAlignment = .center
        lbl.numberOfLines = 1
        lbl.setContentHuggingPriority(UILayoutPriority(rawValue: 253), for:.vertical)
        return lbl
    }()
    
    // MARK: - Set-up smallVideoPlayer
    
    let smallVideoPlayer : SmallVideoPlayerView = {
        let sVP = SmallVideoPlayerView()
        sVP.sizeToFit()
        sVP.backgroundColor = .black
        return sVP
    }()
    
    // MARK: - Set-up contentLabel
    
    let contentLabel : UILabel = {
        let lbl = UILabel()
        lbl.textColor = .black
        lbl.font = UIFont.systemFont(ofSize: 16)
        lbl.textAlignment = .left
        lbl.numberOfLines = 0
        lbl.setContentHuggingPriority(UILayoutPriority(rawValue: 252), for:.vertical)
        return lbl
    }()

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
    super.init(style: style, reuseIdentifier: reuseIdentifier)
        addSubviews()

    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    // MARK - Add SubViews
    
    func addSubviews() {
        addSubview(titleLabel)
        addSubview(smallVideoPlayer)
        addSubview(contentLabel)
        let videoWidth = CGFloat(self._videoWidth ?? 320)
        let videoHeight = CGFloat(self._videoHeight ?? 180)
        let padVideo = CGFloat(-1 * videoWidth / 2)
        titleLabel.anchor(top: topAnchor, left: leftAnchor, right: rightAnchor, paddingTop: 5, paddingLeft: 10, paddingBottom: 0, paddingRight: 5, width: 0, height: 0, enableInsets: false)

        smallVideoPlayer.anchor(top: titleLabel.bottomAnchor, left: centerXAnchor,bottom: contentLabel.topAnchor, paddingTop: 10, paddingLeft:  padVideo, paddingBottom: 10, paddingRight: 0, width: CGFloat(videoWidth), height: videoHeight, enableInsets: false)

        contentLabel.anchor(left: leftAnchor, bottom: bottomAnchor, right: rightAnchor, paddingTop: 10, paddingLeft: 10, paddingBottom: 5, paddingRight: 10, width: 0, height: 0, enableInsets: false)

    }

    // MARK - Obligatory Inits

    required init?(coder aDecoder: NSCoder) {
      super.init(coder: aDecoder)
      
      backgroundColor = .blue
    }

}
