# API Documentation

## Methods Available for IOS, Android, Web Plugins

### `initPlayer(options) => Promise<{result:boolean}>`

Initialize the Video Player

#### options

- available for IOS, Android, Web Plugins

  `{mode: "fullscreen", url: string, playerId: string, componentTag:string}`
  initialize the player for a video given by an url

  the `url` parameter can be :
  . "https:/..../video.mp4" for files from the web
  . if not "https"
  . for IOS "public/assets/video/video.mp4"
  . for Android "/raw/video" without the type. You have to create manually a raw folder under the res folder of your app and copy the video file in it
  . for Web "assets/video/video.mp4"

  the `playerId` is the id of a div element used as container for the player

  the `componentTag` is the component tag or component selector from where the video player is called

* available for Web Plugin only

  `{mode: "embedded", url: string, playerId: string,componentTag:string, width:number, height:number}`
  initialize a video given by an url with a given playerId and size

#### returns for all methods

- success {"result": true, "method":"methodName", "value": valueReturned}
- reject {"result": false, "method":"methodName", "message": errorMessage}

Type: `Promise<{result:boolean, method:string,value:any}>`
Type: `Promise<{result:boolean, method:string,message:string}>`

## Methods Available for Interacting with the plugin

### `isPlaying(options) => Promise<{method:string,result:boolean,value?:any,message?:string}>`

Return the isPlaying video player status

#### options

`{playerId: string} default "fullscreen"`

### `play(options) => Promise<{method:string,result:boolean,value?:any,message?:string}>`

Play the Video

#### options

`{playerId: string} default "fullscreen"`

### `pause(options) => Promise<{method:string,result:boolean,value?:any,message?:string}>`

Pause the Video

#### options

`{playerId: string} default "fullscreen"`

### `getDuration(options) => Promise<{method:string,result:boolean,value?:any,message?:string}>`

Get the Duration of the Video in seconds

#### options

`{playerId: string} default "fullscreen"`

### `setVolume(options) => Promise<{method:string,result:boolean,value?:any,message?:string}>`

Set the Volume of the Video

#### options

`{playerId: string, volume: number} range[0-1] default 0.5`

### `getVolume(options) => Promise<{method:string,result:boolean,value?:any,message?:string}>`

Get the Volume of the Video

#### options

`{playerId: string} default "fullscreen"`

### `setMuted(options) => Promise<{method:string,result:boolean,value?:any,message?:string}>`

Set the Muted parameter of the Video

#### options

`{playerId: string, muted: boolean}`

### `getMuted(options) => Promise<{method:string,result:boolean,value?:any,message?:string}>`

Get the Muted parameter of the Video

#### options

`{playerId: string} default "fullscreen"`

### `setCurrentTime(options) => Promise<{method:string,result:boolean,value?:any,message?:string}>`

Set the Current Time to Seek the Video to in seconds

#### options

`{playerId: string, seektime: number}`

### `getCurrentTime(options) => Promise<{method:string,result:boolean,value?:any,message?:string}>`

Get the Current Time of the Video in seconds

#### options

`{playerId: string} default "fullscreen"`

### `stopAllPlayers() => Promise<{method:string,result:boolean,value?:any,message?:string}>`

Stop all Players in "embedded" mode

#### returns

Type: `Promise<{method:string,result:boolean}>`

## Plugin Listeners

The listeners are attached to the plugin not anymore to the DOM document element.

| Event                     | Description                             | Type                                            |
| ------------------------- | --------------------------------------- | ----------------------------------------------- |
| `jeepCapVideoPlayerReady` | Emitted when the video start to play    | `data:{fromPlayerId:string,currentTime:number}` |
| `jeepCapVideoPlayerPlay`  | Emitted when the video start to play    | `data:{fromPlayerId:string,currentTime:number}` |
| `jeepCapVideoPlayerPause` | Emitted when the video is paused        | `data:{fromPlayerId:string,currentTime:number}` |
| `jeepCapVideoPlayerEnded` | Emitted when the video has ended        | `data:{fromPlayerId:string,currentTime:number}` |
| `jeepCapVideoPlayerExit`  | Emitted when the Exit button is clicked | `data:{dismiss:boolean}`                        |
