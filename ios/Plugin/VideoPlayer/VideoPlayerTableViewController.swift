//
//  VideoPlayerTableViewController.swift
//  Plugin
//
//  Created by  Quéau Jean Pierre on 13/01/2020.
//  Copyright © 2020 Max Lynch. All rights reserved.
//

import UIKit
import AVFoundation

class VideoPlayerTableViewController: UITableViewController, UINavigationControllerDelegate {
    public var videoPlayers: [String:SmallVideoPlayerView] = [:]
    public var _vpIndex: [Int:String] = [:]
    private var _videos: [Video] = []
    private var _pageTitle: String!
    private var _videoWidth: Int!
    private var _videoHeight: Int!
    private var _videoList: Array<Dictionary<String,String>>!
    let cellId = "cellId"



    override func loadView() {
        super.loadView()
        navigationController?.delegate = self

        // Do any additional setup after loading the view.
        navigationItem.title = "Video Players in Table View"
        let backBarButtonItem = UIBarButtonItem(title: "< Back", style: .done, target: self, action: #selector(self.backToPage))
        self.navigationItem.leftBarButtonItem  = backBarButtonItem

        view.backgroundColor = .white
        setVideos()
        tableView.register(VideoPlayerTableViewCell.self,forCellReuseIdentifier: cellId)
     }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
}
     
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {

        let cell = tableView.dequeueReusableCell(withIdentifier: cellId, for: indexPath) as! VideoPlayerTableViewCell
            let video = self._videos[indexPath.row]
            let url = URL(string: video.urlPath!)
            let playerItem : AVPlayerItem = AVPlayerItem(url: url! as URL)
            let avPlayer:AVPlayer = AVPlayer(playerItem: playerItem)
            let titleTapGesture = UITapGestureRecognizer(target:self,action:#selector(self.tapFunction))
            let svpTapGesture = UITapGestureRecognizer(target: self, action: #selector(self.wasTapped))
            /* replace by swipe gesture
            let svpDoubleTapGesture = UITapGestureRecognizer(target: self,
                           action: #selector(self.wasDoubleTapped))
            svpDoubleTapGesture.numberOfTapsRequired = 2
            svpTapGesture.require(toFail: svpDoubleTapGesture)
            */
            let svpLeftRecognizer = UISwipeGestureRecognizer(target: self, action:
                #selector(self.wasSwipe))
            svpLeftRecognizer.direction = .left
            let svpRightRecognizer = UISwipeGestureRecognizer(target: self, action:
                #selector(self.wasSwipe))
            svpRightRecognizer.direction = .right
            cell.titleLabel.isUserInteractionEnabled = true
            cell.titleLabel.addGestureRecognizer(titleTapGesture)

            cell.titleLabel.text = video.title
            cell.contentLabel.text = video.content
            cell.videoWidth = video.width!
            cell.videoHeight = video.height!
            cell.smallVideoPlayer.playerLayer.player = avPlayer
            cell.smallVideoPlayer.videoId = video.title
            cell.smallVideoPlayer.isUserInteractionEnabled = true
            cell.smallVideoPlayer.addGestureRecognizer(svpTapGesture)
            cell.smallVideoPlayer.addGestureRecognizer(svpLeftRecognizer)
            cell.smallVideoPlayer.addGestureRecognizer(svpRightRecognizer)
            /* replace by swipe gesture
            cell.smallVideoPlayer.addGestureRecognizer(svpDoubleTapGesture)
            */

            // add Observers
            cell.smallVideoPlayer.addObservers()
            NotificationCenter.default.addObserver(self,
                                                   selector: #selector(self.playerItemDidReachEnd(_:)),
                                                         name: NSNotification.Name.AVPlayerItemDidPlayToEndTime,
                                                         object: avPlayer.currentItem)
            self.videoPlayers.updateValue(cell.smallVideoPlayer,forKey:video.title!)
            self._vpIndex.updateValue(video.title!,forKey:indexPath.row)
            return cell
     }

     override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self._videos.count
     }
    
    // MARK: - Set-up pageTitle
    
    var pageTitle: String? {
        get {
            return self._pageTitle
        }
        set {
            self._pageTitle = newValue
        }
    }

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
    
    // MARK: - Set-up videoList
    
    var videoList: Array<Dictionary<String,String>>? {
        get {
            return self._videoList
        }
        set {
            self._videoList = newValue
        }
    }

    // MARK: - Setup Video
    
    func setVideos() {
        let videoM = VideoModel()
        for video in self._videoList {
            let v: Video = Video()
            v.height = self._videoHeight
            v.width = self._videoWidth
            v.title = video["title"]
            v.content = video["content"]
            v.urlPath = video["urlPath"]
            v.imgPath = nil
            videoM.addVideo(video:v)
        }

        // Get the videos
 //       videoM.printVideos()
        self._videos = videoM.getVideos()

    }
    
    // MARK: - Pause all Players
    
    @objc func backToPage() {
        DispatchQueue.main.async{
            self.pauseAllPlayers()
            NotificationCenter.default.post(name: .playerInTableDismiss, object: nil)
        }
    }
 
    func pauseAllPlayers() {
        let keys = self.videoPlayers.keys
        for key in keys {
            if(key != "fullscreen") {
                if (self.videoPlayers[key]?.playerLayer.player!.rate == 1 ) {
                    self.videoPlayers[key]?.pause()
                }
            }
        }
    }
    func destroyAllGestures() {
        self.tableView.gestureRecognizers?.removeAll()
    }
    func destroyAllPlayers() {
        let keys = self.videoPlayers.keys
        for key in keys {
            if(key != "fullscreen") {
                self.videoPlayers[key]?.destroyPlayer()
            }
        }
    }

    // MARK: - Notifications Handling
    
    @objc func playerItemDidReachEnd(_ notification: NSNotification) {
        let keys = self.videoPlayers.keys
        for key in keys {
            if let p = notification.object as? AVPlayerItem, p ==
                self.videoPlayers[key]?.playerLayer.player!.currentItem {
                let time:Double = Double(CMTimeGetSeconds((self.videoPlayers[key]?.playerLayer.player!.currentItem!.duration)!))
                let vId: [String:Any] = ["fromPlayerId": key,"currentTime":Float(time),"fromInternal":true]
                self.videoPlayers[key]?.setCurrentTime(time: .zero)
                NotificationCenter.default.post(name: .playerItemEnd, object: nil, userInfo: vId)
            }
        }
    }
    
    // MARK: - Handling Tap Gesture
    
    @objc func tapFunction(sender:UITapGestureRecognizer) {
        // Get references
        guard (sender.view as? UILabel) != nil else {
            print("Error not a label")
            return
        }
        let position = sender.location(in: self.tableView)
        guard let index = self.tableView.indexPathForRow(at: position) else {
            print("Error label not in tableView")
            return
        }

        let video = self._videos[index.row]
        let videoPlayerFullScreenView = FullScreenVideoPlayerView(videoPath:video.urlPath!)
        // Pause all the small players
        self.pauseAllPlayers()
        // Set a Notification
        NotificationCenter.default.addObserver(self,
                                               selector: #selector(playerItemDidReachEnd(_:)),
                                                     name: NSNotification.Name.AVPlayerItemDidPlayToEndTime,
                                                     object: videoPlayerFullScreenView.videoPlayer.player?.currentItem)

        // Present the fullscreen player
        present(videoPlayerFullScreenView.videoPlayer, animated: true) {
            videoPlayerFullScreenView.player.play()
        }
    }
    @objc func wasTapped(sender:UITapGestureRecognizer) {
        // Get references
        guard (sender.view as? SmallVideoPlayerView) != nil else {
            print("Error not a SmallVideoPlayerView")
            return
        }
        let position = sender.location(in: self.tableView)
        guard let index = self.tableView.indexPathForRow(at: position) else {
            print("Error SmallVideoPlayerView not in tableView")
            return
        }
        guard let svpName = self._vpIndex[index.row] else {
            print("Error SmallVideoPlayerView Index not found")
            return
        }
        let svp: SmallVideoPlayerView = self.videoPlayers[svpName]!
        svp.wasTapped()
    }
    /* replace by swipe gesture
    @objc func wasDoubleTapped(sender:UITapGestureRecognizer) {
        // Get references
        guard (sender.view as? SmallVideoPlayerView) != nil else {
            print("Error not a SmallVideoPlayerView")
            return
        }
        let position = sender.location(in: self.tableView)
        guard let index = self.tableView.indexPathForRow(at: position) else {
            print("Error SmallVideoPlayerView not in tableView")
            return
        }
        guard let svpName = self._vpIndex[index.row] else {
            print("Error SmallVideoPlayerView Index not found")
            return
        }
        let svp: SmallVideoPlayerView = self.videoPlayers[svpName]!
        svp.wasDoubleTapped()
    }
     */
    @objc func wasSwipe(sender: UISwipeGestureRecognizer) {
        // Get references
         guard (sender.view as? SmallVideoPlayerView) != nil else {
             print("Error not a SmallVideoPlayerView")
             return
         }
         let position = sender.location(in: self.tableView)
         guard let index = self.tableView.indexPathForRow(at: position) else {
             print("Error SmallVideoPlayerView not in tableView")
             return
         }
         guard let svpName = self._vpIndex[index.row] else {
             print("Error SmallVideoPlayerView Index not found")
             return
         }
         let svp: SmallVideoPlayerView = self.videoPlayers[svpName]!
        svp.wasSwipe(direction:sender.direction)

    }
}
