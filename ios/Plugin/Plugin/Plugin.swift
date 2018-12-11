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
    var isVideoPlaying: Bool = false
    
    @objc func play(_ call: CAPPluginCall)  {
        guard let url = call.options["url"] as? String else {
            let error:String = "VideoPlayer Play: Must provide a video url"  
            print(error)          
            call.success([
                "result": false
            ])
            return
        }
        guard let urlIn = URL(string: url) else {return}
        let videoPlayer = AVPlayerViewController()
        DispatchQueue.main.async {
            self.player = AVPlayer(url: urlIn)
            videoPlayer.player = self.player
            self.bridge.viewController.present(videoPlayer, animated: true, completion:{
                self.player.play()
            })
        }
        isVideoPlaying = !isVideoPlaying
        call.success([
            "result": true
        ])

    }
}
