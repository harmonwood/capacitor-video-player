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

    // swiftlint:disable cyclomatic_complexity
    // swiftlint:disable function_body_length
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
        if String(filePath.prefix(29)) == "file:///var/mobile/Containers" {

            let appPath: String = NSSearchPathForDirectoriesInDomains(.applicationDirectory,
                                                                      .userDomainMask,
                                                                      true)[0]
            // get the appId from the appPath
            let pathArray = appPath.components(separatedBy: "Application")
            let appId: String = pathArray[1].replacingOccurrences(of: "/", with: "")
            // remove the appId from the given filePath and replace it with the current appId
            let fPathArray = filePath.components(separatedBy: "Application")
            if let uPath = URL(string: pathArray[0]) {
                let fPath = (uPath.appendingPathComponent("Application")
                                .appendingPathComponent(appId)
                                .appendingPathComponent(
                                    String(fPathArray[1].dropFirst(38)))
                ).absoluteString
                if !isFileExists(filePath: fPath) {
                    print("*** file does not exist at path \n \(fPath) \n***")
                    let info: [String: Any] = ["dismiss": true]
                    self.notifyListeners("jeepCapVideoPlayerExit", data: info,
                                         retainUntilConsumed: true)
                    dict["message"] = "file does not exist"
                    return dict
                }
                dict["url"] = URL(fileURLWithPath: fPath)
            } else {
                dict["message"] = "file path not correct"
            }
            return dict
        }
        if String(filePath.prefix(38)) == "file:///var/mobile/Media/DCIM/100APPLE" {
            let appPath: String = NSSearchPathForDirectoriesInDomains(.applicationDirectory,
                                                                      .userDomainMask,
                                                                      true)[0]
            // get the appId from the appPath
            let pathArray = appPath.components(separatedBy: "Containers")
            let fPathArray = filePath.components(separatedBy: "mobile/")

            if let uPath = URL(string: pathArray[0]) {
                let fPath = (uPath.appendingPathComponent(
                                String(fPathArray[1]))
                ).absoluteString
                if !isFileExists(filePath: fPath) {
                    print("*** file does not exist at path \n \(fPath) \n***")
                    let info: [String: Any] = ["dismiss": true]
                    self.notifyListeners("jeepCapVideoPlayerExit", data: info,
                                         retainUntilConsumed: true)
                    dict["message"] = "file does not exist"
                    return dict
                }
                dict["url"] = URL(fileURLWithPath: fPath)
                return dict
            } else {
                dict["message"] = "file path not correct"
            }
            return dict
        }
        dict["message"] = "filePath not implemented"
        return dict
    }
    // swiftlint:enable function_body_length
    // swiftlint:enable cyclomatic_complexity

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
