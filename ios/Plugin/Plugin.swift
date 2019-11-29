import Foundation
import Capacitor

import AVKit

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitor.ionicframework.com/docs/plugins/ios
 */
@objc(CapacitorVideoPlayer)
public class CapacitorVideoPlayer: CAPPlugin {
    
    var player: AVPlayer!
    public var call: CAPPluginCall!
    var videoPlayer: AVPlayerViewController!
    
    override public func load() {
        // add listeners here
        NotificationCenter.default.addObserver(self, selector: #selector(didFinishPlaying),
                                               name:Notification.Name.AVPlayerItemDidPlayToEndTime, object: nil)
        
        
    }
    
    @objc func initPlayer(_ call: CAPPluginCall)  {
        self.call = call

        guard let mode = call.options["mode"] as? String else {
            let error:String = "VideoPlayer initPlayer: Must provide a Mode (fullscreen/embedded)"
            print(error)
            call.reject(error)
            return
        }

        guard let url = call.options["url"] as? String else {
            let error:String = "VideoPlayer initPlayer: Must provide a video url"
            print(error)
            call.reject(error)
            return
        }
        if (mode == "embedded") {
            let error:String = "VideoPlayer initPlayer: Embedded Mode not yet implemented"
            print(error)
            call.reject(error)
            return
        }
        var urlIn: URL
        let http:String = String(url.prefix(4))
        if(http == "http"){
            urlIn = URL(string: url)!
        } else {
            let path = url.components(separatedBy: "/")
            if(path.count > 1) {
                
            } else {
                let error:String = "VideoPlayer initPlayer: When not http url should have a path"
                print(error)
                call.reject(error)
                return
            }
            let cmp = path[path.count-1].components(separatedBy: ".")
            if(cmp.count != 2) {
                let error:String = "VideoPlayer initPlayer: When not http url should have an extension"
                print(error)
                call.reject(error)
                return
            }
            let subdir = url.replacingOccurrences(of: path[path.count-1], with:"")
            print("subdir \(subdir)")
            guard let fileUrl = Bundle.main.url(forResource: cmp[0], withExtension:cmp[1],subdirectory:subdir) else {
                let error:String = "\(cmp[0]).\(cmp[1]) not found"
                print(error)
                call.reject(error)
                return
            }
            print("video path \(fileUrl)")
            urlIn = fileUrl
        }
        self.videoPlayer = AVPlayerViewController()
        DispatchQueue.main.async {
            self.player = AVPlayer(url: urlIn)
            self.videoPlayer.player = self.player
            self.bridge.viewController.present(self.videoPlayer, animated: true, completion:{
                self.player.play()
            });
        }
    }
    
    @objc func didFinishPlaying() {
        self.videoPlayer.dismiss(animated: true,completion: nil)
        call.success([ "result": true])
    }
}
