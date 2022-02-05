#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(CapacitorVideoPlayerPlugin, "CapacitorVideoPlayer",
           CAP_PLUGIN_METHOD(echo, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(initPlayer, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(isPlaying, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(play, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(pause, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(getDuration, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(getCurrentTime, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(setCurrentTime, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(getVolume, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(setVolume, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(getMuted, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(setMuted, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(stopAllPlayers, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(getRate, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(setRate, CAPPluginReturnPromise);
)
