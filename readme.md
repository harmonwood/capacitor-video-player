# Capacitor Video Player Plugin
Capacitor Video Player Plugin is a custom Native Capacitor plugin to play a video on IOS, Android, Web and Electron platforms.
As capacitor provides fisrt-class Progressive Web App support so you can use this plugin in your app which will later be deployed to the app stores and the mobile web.


## View Me
[capacitor-video-player](https://pwacapacitorvideoplayertest.firebaseapp.com/)

## Methods available

    play()                              play a video given by an url
 
    the url parameter can be :
      . "https:/..../video.mp4" for files from the web
      . if not "https"
        . for IOS "public/assets/video/video.mp4" 
        . for Android "/raw/video" without the type. You have to create manually a raw folder under the res folder of your app and copy the video file in it
        . for Web "assets/video/video.mp4"

## To use the Plugin in your Project
```bash
npm install --save capacitor-video-player@latest
```

Ionic App showing an integration of [capacitor-video-player plugin](https://github.com/jepiqueau/ionic-capacitor-video-player)


PWA App showing an integration of 
[capacitor-video-player plugin](https://github.com/jepiqueau/ionicpwacapacitorvideoplayer)


## Remarks
This release of the plugin includes the Native IOS code (Objective-C/Swift),the Native Android code (Java) and the Web code (Typescript) using Capacitor v1.1.0




