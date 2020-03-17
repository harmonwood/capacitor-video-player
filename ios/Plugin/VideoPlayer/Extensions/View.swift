//
//  Extension.swift
//  Sample_TableView
//
//  Created by Esat Kemal Ekren on 5.04.2018.
//  Copyright © 2018 Esat Kemal Ekren. All rights reserved.
//

import UIKit

extension UIView {
    
    func anchor (top: NSLayoutYAxisAnchor? = nil, left: NSLayoutXAxisAnchor? = nil, bottom: NSLayoutYAxisAnchor? = nil, right: NSLayoutXAxisAnchor? = nil,  paddingTop: CGFloat, paddingLeft: CGFloat, paddingBottom: CGFloat, paddingRight: CGFloat, width: CGFloat, height: CGFloat, enableInsets: Bool) {
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
        
        if top != nil {
            self.topAnchor.constraint(equalTo: top!, constant: paddingTop+topInset).isActive = true
        }
        if left != nil {
            self.leftAnchor.constraint(equalTo: left!, constant: paddingLeft).isActive = true
        }
        if right != nil {
            self.rightAnchor.constraint(equalTo: right!, constant: -paddingRight).isActive = true
        }
        if bottom != nil {
            self.bottomAnchor.constraint(equalTo: bottom!, constant: -paddingBottom-bottomInset).isActive = true
        }
        if height != 0 {
            self.heightAnchor.constraint(equalToConstant: height).isActive = true
        }
        if width != 0 {
            self.widthAnchor.constraint(equalToConstant: width).isActive = true
        }
        
    }
    
}
