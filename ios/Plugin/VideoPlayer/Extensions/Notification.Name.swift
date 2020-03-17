//
//  Notification.Name.swift
//  Plugin
//
//  Created by  Quéau Jean Pierre on 13/01/2020.
//  Copyright © 2020 Max Lynch. All rights reserved.
//

import Foundation
extension NSNotification.Name {
    static var playerItemPlay: Notification.Name{return .init(rawValue: "playerItemPlay")}
    static var playerItemPause: Notification.Name{return .init(rawValue: "playerItemPause")}
    static var playerItemEnd: Notification.Name{return .init(rawValue: "playerItemEnd")}
    static var playerItemReady: Notification.Name{return .init(rawValue: "playerItemReady")}
    static var playerInTableDismiss: Notification.Name{return .init(rawValue: "playerInTableDismiss")}
}
