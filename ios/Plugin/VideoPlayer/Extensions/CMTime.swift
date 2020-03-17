//
//  CMTime.swift
//  Plugin
//
//  Created by  Quéau Jean Pierre on 13/01/2020.
//  Copyright © 2020 Max Lynch. All rights reserved.
//

import CoreMedia

extension CMTime {
    public var stringValue: String {
        let totalSeconds = Int(self.seconds)
        let hours = totalSeconds / 3600
        let minutes = totalSeconds % 3600 / 60
        let seconds = totalSeconds % 3600 % 60
        if hours > 0 {
            return String(format: "%i:%02i:%02i", hours, minutes, seconds)
        } else {
            return String(format: "%02i:%02i", minutes, seconds)
        }
    }
}

