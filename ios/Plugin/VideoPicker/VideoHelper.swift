//
//  VideoHelper.swift
//  Plugin
//
//  Created by  Quéau Jean Pierre on 28/05/2021.
//  Copyright © 2021 Max Lynch. All rights reserved.
//
//  Copyright (c) 2021 Razeware LLC
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
//  distribute, sublicense, create a derivative work, and/or sell copies of the
//  Software in any work that is designed, intended, or marketed for pedagogical or
//  instructional purposes related to programming, coding, application development,
//  or information technology.  Permission for such use, copying, modification,
//  merger, publication, distribution, sublicensing, creation of derivative works,
//  or sale is expressly withheld.
//
//  This project and source code may use libraries or frameworks that are
//  released under various Open-Source licenses. Use of those libraries and
//  frameworks are governed by their own individual licenses.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.

import AVFoundation
import MobileCoreServices
import UIKit

enum VideoHelper {
    static func orientationFromTransform(
        _ transform: CGAffineTransform
    ) -> (orientation: UIImage.Orientation, isPortrait: Bool) {
        var assetOrientation = UIImage.Orientation.up
        var isPortrait = false
        let tfA = transform.a
        let tfB = transform.b
        let tfC = transform.c
        let tfD = transform.d

        if tfA == 0 && tfB == 1.0 && tfC == -1.0 && tfD == 0 {
            assetOrientation = .right
            isPortrait = true
        } else if tfA == 0 && tfB == -1.0 && tfC == 1.0 && tfD == 0 {
            assetOrientation = .left
            isPortrait = true
        } else if tfA == 1.0 && tfB == 0 && tfC == 0 && tfD == 1.0 {
            assetOrientation = .up
        } else if tfA == -1.0 && tfB == 0 && tfC == 0 && tfD == -1.0 {
            assetOrientation = .down
        }
        return (assetOrientation, isPortrait)
    }

    static func startMediaBrowser(
        delegate: UIViewController & UINavigationControllerDelegate & UIImagePickerControllerDelegate,
        sourceType: UIImagePickerController.SourceType
    ) {
        guard UIImagePickerController.isSourceTypeAvailable(sourceType)
        else { return }

        let mediaUI = UIImagePickerController()
        mediaUI.sourceType = sourceType
        mediaUI.mediaTypes = [kUTTypeMovie as String]
        mediaUI.allowsEditing = false
        mediaUI.delegate = delegate
        delegate.present(mediaUI, animated: true, completion: nil)
    }

    static func videoCompositionInstruction(
        _ track: AVCompositionTrack,
        asset: AVAsset
    ) -> AVMutableVideoCompositionLayerInstruction {
        let instruction = AVMutableVideoCompositionLayerInstruction(assetTrack: track)
        let assetTrack = asset.tracks(withMediaType: AVMediaType.video)[0]

        let transform = assetTrack.preferredTransform
        let assetInfo = orientationFromTransform(transform)

        var scaleToFitRatio = UIScreen.main.bounds.width / assetTrack.naturalSize.width
        if assetInfo.isPortrait {
            scaleToFitRatio = UIScreen.main.bounds.width / assetTrack.naturalSize.height
            let scaleFactor = CGAffineTransform(
                scaleX: scaleToFitRatio,
                y: scaleToFitRatio)
            instruction.setTransform(
                assetTrack.preferredTransform.concatenating(scaleFactor),
                at: .zero)
        } else {
            let scaleFactor = CGAffineTransform(
                scaleX: scaleToFitRatio,
                y: scaleToFitRatio)
            var concat = assetTrack.preferredTransform.concatenating(scaleFactor)
                .concatenating(CGAffineTransform(
                                translationX: 0,
                                y: UIScreen.main.bounds.width / 2))
            if assetInfo.orientation == .down {
                let fixUpsideDown = CGAffineTransform(rotationAngle: CGFloat(Double.pi))
                let windowBounds = UIScreen.main.bounds
                let yFix = assetTrack.naturalSize.height + windowBounds.height
                let centerFix = CGAffineTransform(
                    translationX: assetTrack.naturalSize.width,
                    y: yFix)
                concat = fixUpsideDown.concatenating(centerFix).concatenating(scaleFactor)
            }
            instruction.setTransform(concat, at: .zero)
        }

        return instruction
    }
}

import Foundation
