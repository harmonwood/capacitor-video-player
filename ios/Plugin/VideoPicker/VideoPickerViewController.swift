//
//  VideoPickerViewController.swift
//  Plugin
//
//  Created by  Quéau Jean Pierre on 11/08/2020.
//  Copyright © 2021 Max Lynch. All rights reserved.
//

import AVKit
import MobileCoreServices
import UIKit

open class VideoPickerViewController: UIViewController {
    private var _videoRate: Float = 1.0

    // MARK: - Set-up rate

    var rate: Float {
        get {
            return self._videoRate
        }
        set {
            self._videoRate = newValue
        }
    }

    override open func viewDidLoad() {
        super.viewDidLoad()
        VideoHelper.startMediaBrowser(delegate: self,
                                      sourceType: .savedPhotosAlbum)
    }

}

// MARK: - UIImagePickerControllerDelegate
extension VideoPickerViewController: UIImagePickerControllerDelegate {
    public func imagePickerController(
        _ picker: UIImagePickerController,
        didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey: Any]
    ) {
        guard
            let mediaType = info[UIImagePickerController.InfoKey.mediaType] as? String,
            mediaType == (kUTTypeMovie as String),
            let url = info[UIImagePickerController.InfoKey.mediaURL] as? URL
        else {
            return
        }
        let vId: [String: Any] = ["videoUrl": url,
                                  "videoRate": rate]
        NotificationCenter.default.post(name: .videoPathInternalReady, object: nil, userInfo: vId)
    }
    public func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        let vId: [String: Any] = ["videoUrl": "",
                                  "videoRate": rate]
        NotificationCenter.default.post(name: .videoPathInternalReady, object: nil, userInfo: vId)
    }
}

// MARK: - UINavigationControllerDelegate
extension VideoPickerViewController: UINavigationControllerDelegate {
}
