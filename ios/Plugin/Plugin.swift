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
    
    @objc func play(_ call: CAPPluginCall)  {
        self.call = call
        guard let url = call.options["url"] as? String else {
            let error:String = "VideoPlayer Play: Must provide a video url"
            print(error)
            call.reject(error)
            return
        }
        guard let urlIn = URL(string: url) else {return}
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

