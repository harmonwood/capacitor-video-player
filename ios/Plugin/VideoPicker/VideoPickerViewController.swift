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
    private var _exitOnEnd: Bool = true
    private var _loopOnEnd: Bool = false
    private var _pipEnabled: Bool = true
    private var _backModeEnabled: Bool = true
    private var _showControls: Bool = true
    private var _displayMode: String = "portrait"

    // MARK: - Set-up rate

    var rate: Float {
        get {
            return self._videoRate
        }
        set {
            self._videoRate = newValue
        }
    }

    // MARK: - Set-up exitOnEnd

    var exitOnEnd: Bool {
        get {
            return self._exitOnEnd
        }
        set {
            self._exitOnEnd = newValue
        }
    }

    // MARK: - Set-up loopOnEnd

    var loopOnEnd: Bool {
        get {
            return self._loopOnEnd
        }
        set {
            self._loopOnEnd = newValue
        }
    }

    // MARK: - Set-up pipEnabled

    var pipEnabled: Bool {
        get {
            return self._pipEnabled
        }
        set {
            self._pipEnabled = newValue
        }
    }

    // MARK: - Set-up backModeEnabled

    var backModeEnabled: Bool {
        get {
            return self._backModeEnabled
        }
        set {
            self._backModeEnabled = newValue
        }
    }
    // MARK: - Set-up showControls

    var showControls: Bool {
        get {
            return self._showControls
        }
        set {
            self._showControls = newValue
        }
    }

    // MARK: - Set-up displayMode

    var displayMode: String {
        get {
            return self._displayMode
        }
        set {
            self._displayMode = newValue
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
        let vId: [String: Any] = [
            "videoUrl": url, "videoRate": rate,
            "exitOnEnd": exitOnEnd, "loopOnEnd": loopOnEnd,
            "pipEnabled": pipEnabled,
            "backModeEnabled": backModeEnabled,
            "showControls": showControls,
            "displayMode": displayMode]
        NotificationCenter.default.post(name: .videoPathInternalReady, object: nil, userInfo: vId)
    }
    public func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        let vId: [String: Any] = [
            "videoUrl": "", "videoRate": rate,
            "exitOnEnd": exitOnEnd, "loopOnEnd": loopOnEnd,
            "pipEnabled": pipEnabled,
            "backModeEnabled": backModeEnabled,
            "showControls": showControls,
            "displayMode": displayMode
        ]
        NotificationCenter.default.post(name: .videoPathInternalReady,
                                        object: nil, userInfo: vId)
    }
}

// MARK: - UINavigationControllerDelegate
extension VideoPickerViewController: UINavigationControllerDelegate {
}
