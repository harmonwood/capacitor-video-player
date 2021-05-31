//
//  CapacitorVideoPlayerPlugin.swift
//  Plugin
//
//  Created by  Quéau Jean Pierre on 30/05/2021.
//  Copyright © 2021 Max Lynch. All rights reserved.
//

import Foundation

extension CapacitorVideoPlayerPlugin {

    // MARK: - getURLFromFilePath

    func getURLFromFilePath(filePath: String) -> [String: Any] {
        var dict: [String: Any] = [:]
        dict["message"] = ""
        let docPath: String = NSSearchPathForDirectoriesInDomains(.documentDirectory,
                                                                  .userDomainMask, true)[0]
        if String(filePath.prefix(11)) == "application" {
            let path: String = String(filePath.dropFirst(12))
            let vPath: String = docPath.appendingFormat("/\(path)")
            if !isFileExists(filePath: vPath) {
                print("*** file does not exist at path \n \(vPath) \n***")
                let info: [String: Any] = ["dismiss": true]
                self.notifyListeners("jeepCapVideoPlayerExit", data: info, retainUntilConsumed: true)
                dict["message"] = "file does not exist"
                return dict
            }
            dict["url"] = URL(fileURLWithPath: vPath)
            return dict
        }
        if String(filePath.prefix(4)) == "http" {
            if let url = URL(string: filePath) {
                dict["url"] = url
                return dict
            } else {
                dict["message"] = "cannot convert filePath in URL"
                return dict
            }
        }
        if String(filePath.prefix(13)) == "public/assets" {
            if let appFolder = Bundle.main.resourceURL {
                dict["url"] = appFolder.appendingPathComponent(filePath)
                return dict
            } else {
                dict["message"] = "did not find appFolder"
                return dict
            }
        }
        dict["message"] = "filePath not implemented"
        return dict
    }

    // MARK: - isFileExists

    func isFileExists(filePath: String) -> Bool {
        var ret: Bool = false
        let fileManager = FileManager.default
        if fileManager.fileExists(atPath: filePath) {
            ret = true
        }
        return ret
    }

}
