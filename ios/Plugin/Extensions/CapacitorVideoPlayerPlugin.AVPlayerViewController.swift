//
//  CapacitorVideoPlayerPlugin.AVPlayerViewController.swift
//  CapacitorVideoPlayer
//
//  Created by  Quéau Jean Pierre on 10/12/2021.
//

import AVFoundation
import AVKit

var isInPIPMode: Bool = false
var isInBackgroundMode: Bool = false
var isPIPModeAvailable: Bool = false

extension CapacitorVideoPlayerPlugin: AVPlayerViewControllerDelegate {

    public func playerViewControllerShouldAutomaticallyDismissAtPictureInPictureStart(_ playerViewController: AVPlayerViewController) -> Bool {
        if isPIPModeAvailable {

            isInPIPMode = true
            self.bridge?.viewController?.dismiss(animated: true, completion: nil)

            return true
        } else {
            return false
        }
    }
    public func playerViewController(_ playerViewController: AVPlayerViewController, restoreUserInterfaceForPictureInPictureStopWithCompletionHandler completionHandler: @escaping (Bool) -> Void) {
        if isPIPModeAvailable {
            isInPIPMode = false
            self.bridge?.viewController?.present(playerViewController, animated: true, completion: nil)
        }

    }
    /*
     public func playerViewController(_ playerViewController: AVPlayerViewController, willEndFullScreenPresentationWithAnimationCoordinator coordinator: UIViewControllerTransitionCoordinator) {
     if playerViewController.isBeingDismissed {
     playerViewController.dismiss(animated: false) {
     print(">>>>> playerViewController has been dismiss ")
     }
     }
     }
     */
}
