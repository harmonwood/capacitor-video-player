//
//  View.swift
//  Plugin
//
//  Sample_TableView
//
//  Created by Esat Kemal Ekren on 5.04.2018.
//  Copyright © 2021 Esat Kemal Ekren. All rights reserved.
//  Copyright © 2021 Max Lynch. All rights reserved.
//

import UIKit

extension UIView {
    // swiftlint:disable function_parameter_count
    func anchor (paddingTop: CGFloat, paddingLeft: CGFloat, paddingBottom: CGFloat,
                 paddingRight: CGFloat, width: CGFloat, height: CGFloat, enableInsets: Bool,
                 top: NSLayoutYAxisAnchor? = nil, left: NSLayoutXAxisAnchor? = nil,
                 bottom: NSLayoutYAxisAnchor? = nil, right: NSLayoutXAxisAnchor? = nil) {

        var topInset = CGFloat(0)
        var bottomInset = CGFloat(0)

        if #available(iOS 11, *), enableInsets {
            let insets = self.safeAreaInsets
            topInset = insets.top
            bottomInset = insets.bottom
            print("Top: \(topInset)")
            print("bottom: \(bottomInset)")
        }

        translatesAutoresizingMaskIntoConstraints = false

        if let mTop = top {
            self.topAnchor.constraint(equalTo: mTop, constant: paddingTop+topInset).isActive = true
        }
        if let mLeft = left {
            self.leftAnchor.constraint(equalTo: mLeft, constant: paddingLeft).isActive = true
        }
        if let mRight = right {
            self.rightAnchor.constraint(equalTo: mRight, constant: -paddingRight).isActive = true
        }
        if let mBottom = bottom {
            self.bottomAnchor.constraint(equalTo: mBottom, constant: -paddingBottom-bottomInset).isActive = true
        }
        if height != 0 {
            self.heightAnchor.constraint(equalToConstant: height).isActive = true
        }
        if width != 0 {
            self.widthAnchor.constraint(equalToConstant: width).isActive = true
        }
    }
    // swiftlint:enable function_parameter_count
}
