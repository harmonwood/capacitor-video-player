//
//  Plugin.m
//  Plugin
//
//  Created by  Quéau Jean Pierre on 19/06/2019.
//  Copyright © 2019 Max Lynch. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(CapacitorVideoPlayer, "CapacitorVideoPlayer",
           CAP_PLUGIN_METHOD(play, CAPPluginReturnPromise);
           )
