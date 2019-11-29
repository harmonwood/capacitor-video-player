# Capacitor Video Player Plugin
Capacitor Video Player Plugin is a custom Native Capacitor plugin to play a video 
 - fullscreen on IOS, Android, Web and Electron platforms 
 - embedded on Web and Electron platforms
As capacitor provides fisrt-class Progressive Web App support so you can use this plugin in your app which will later be deployed to the app stores and the mobile web.


## View Me
[capacitor-video-player](https://pwacapacitorvideoplayertest.firebaseapp.com/)

## Methods Available for IOS, Android, Web Plugins

### `initPlayer(options) => Promise<{result:boolean}>`

Initialize the Video Player 

#### options
 - available for IOS, Android, Web Plugins

   ```{mode: "fullscreen", url: string}```
   play a video given by an url
 
    the url parameter can be :
      . "https:/..../video.mp4" for files from the web
      . if not "https"
        . for IOS "public/assets/video/video.mp4" 
        . for Android "/raw/video" without the type. You have to create manually a raw folder under the res folder of your app and copy the video file in it
        . for Web "assets/video/video.mp4"

 - available for  Web Plugin only 

   ```{mode: "embedded", url: string, playerId: string, width:number, height:number}```
   initialize a video given by an url with a given playerId and size

#### returns
Type: `Promise<{result:boolean}>`


## Methods Available for Web Plugin Only

### `play(options) => Promise<{method:string,result:boolean}>`

Play the Video 

#### options

   ```{playerId: string}  default "fullscreen"```

#### returns
Type: `Promise<{method:string,result:boolean}>`

### `pause(options) => Promise<{method:string,result:boolean}>`

Pause the Video 

#### options

   ```{playerId: string}  default "fullscreen"```

#### returns
Type: `Promise<{method:string,result:boolean}>`

### `getDuration(options) => Promise<{method:string,result:boolean,value:any}>`

Get the Duration of the Video 

#### options

   ```{playerId: string}  default "fullscreen"```

#### returns
Type: `Promise<{method:string,result:boolean,value:number}>`

### `setVolume(options) => Promise<{method:string,result:boolean,value:any}>`

Set the Volume of the Video 

#### options

   ```{playerId: string, volume: number}  range[0-1] default 0.5```

#### returns
Type: `Promise<{method:string,result:boolean,value:number}>`

### `getVolume(options) => Promise<{method:string,result:boolean,value:any}>`

Get the Volume of the Video 

#### options

   ```{playerId: string}  default "fullscreen"```

#### returns
Type: `Promise<{method:string,result:boolean,value:number}>`

### `setMuted(options) => Promise<{method:string,result:boolean,value:any}>`

Set the Muted parameter of the Video 

#### options

   ```{playerId: string, muted: boolean} ```

#### returns
Type: `Promise<{method:string,result:boolean,value:boolean}>`

### `getMuted(options) => Promise<{method:string,result:boolean,value:any}>`

Get the Muted parameter of the Video 

#### options

   ```{playerId: string}  default "fullscreen"```

#### returns
Type: `Promise<{method:string,result:boolean,value:boolean}>`

### `setCurrentTime(options) => Promise<{method:string,result:boolean,value:any}>`

Set the Current Time to Seek the Video to 

#### options

   ```{playerId: string, seektime: number} ```

#### returns
Type: `Promise<{method:string,result:boolean,value:number}>`

### `getCurrentTime(options) => Promise<{method:string,result:boolean,value:any}>`

Get the Current Time of the Video 

#### options

   ```{playerId: string}  default "fullscreen"```

#### returns
Type: `Promise<{method:string,result:boolean,value:number}>`

## Events available for Web plugin

| Event                       | Description                            | Type                                 |
| --------------------------- | -------------------------------------- | ------------------------------------ |
| `jeepCapVideoPlayerPlay`    | Emitted when the video start to play   | `CustomEvent<{fromPlayerId:string,currentTime:number}>` |
| `jeepCapVideoPlayerPause`   | Emitted when the video is paused       | `CustomEvent<{fromPlayerId:string,currentTime:number}>` |
| `jeepCapVideoPlayerPlaying` | Emitted when the video restart to play | `CustomEvent<{fromPlayerId:string,currentTime:number}>` |
| `jeepCapVideoPlayerEnded`   | Emitted when the video has ended       | `CustomEvent<{fromPlayerId:string,currentTime:number}>` |

the target for the events is the document


## To use the Plugin in your Project
```bash
npm install --save capacitor-video-player@latest
```

Ionic App showing an integration of [capacitor-video-player plugin](https://github.com/jepiqueau/ionic-capacitor-video-player)


PWA App showing an integration of 
[capacitor-video-player plugin](https://github.com/jepiqueau/ionicpwacapacitorvideoplayer)


## Remarks
This release of the plugin includes the Native IOS code (Objective-C/Swift),the Native Android code (Java) and the Web code (Typescript) using Capacitor v1.3.0




