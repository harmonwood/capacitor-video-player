# API Documentation

## Methods Index

<docgen-index>

* [`echo(...)`](#echo)
* [`initPlayer(...)`](#initplayer)
* [`isPlaying(...)`](#isplaying)
* [`play(...)`](#play)
* [`pause(...)`](#pause)
* [`getDuration(...)`](#getduration)
* [`getCurrentTime(...)`](#getcurrenttime)
* [`setCurrentTime(...)`](#setcurrenttime)
* [`getVolume(...)`](#getvolume)
* [`setVolume(...)`](#setvolume)
* [`getMuted(...)`](#getmuted)
* [`setMuted(...)`](#setmuted)
* [`setRate(...)`](#setrate)
* [`getRate(...)`](#getrate)
* [`stopAllPlayers()`](#stopallplayers)
* [Interfaces](#interfaces)
</docgen-index>
* [Listeners](#listeners)

## Url

### from the Web

. `http:/..../video.mp4`
. `https:/..../video.mp4`

### from Asset

- iOS Plugin
  . `"public/assets/video/video.mp4"`

- Android Plugin
  . `"public/assets/video/video.mp4"` not anymore the `resource/raw folder`

- Web & Electron Plugins
  . `"assets/video/video.mp4"`

### from Application Folder

- iOS Plugin
  . `application/files/video.mp4` is corresponding to :
  **/data/Containers/Data/Applications/YOUR_APPLICATION/Documents/files/video.mp4**

- Android Plugin
  . `application/files/video.mp4` is corresponding to :
  **/data/user/0/YOUR_APPLICATION_PACKAGE/files/video.mp4**

### from your Device Media

- iOS & Android Plugin only
  . `internal`

### from Internal folder

- Android Plugin 
  . `file:///sdcard/DCIM/Camera/YOUR_VIDEO` 
  . `file:///storage/extSdCard/DCIM/Camera/YOUR_VIDEO`

- iOS Plugin 
  . `file:///var/mobile/Media/DCIM/100APPLE/YOUR_VIDEO`
  . `file:///var/mobile/Containers/Data/Application/YOUR_APPLICATION_ID/tmp/YOUR_VIDEO`
  
  
## Subtitle (Android, iOS Only)

### Supported Formats

- Android Plugin
  . `WebVTT .vtt extension`
  . `TTML/SMPTE .ttml, .dfxp, .xml extensions`
  . `SubRip .srt extension`
  . `SubStationAlpha .ssa, .ass extensions`

- iOS Plugin
  . `WebVTT .vtt extension`

### from the Web

. `http:/..../video.vtt`
. `https:/..../video.vtt`

### from Asset

- Android Plugin
  . `"public/assets/video/video.srt"`

- iOS Plugin
  . `"public/assets/video/video.vtt"`

### from Application Folder

- Android Plugin
  . `application/files/video.vtt` is corresponding to :
  **/data/user/0/YOUR_APPLICATION_PACKAGE/files//video.vtt**

- iOS Plugin
  . `application/files/video.vtt` is corresponding to :
  **/data/Containers/Data/Applications/YOUR_APPLICATION/Documents/files/video.vtt**

### from Internal (Not supported)

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### echo(...)

```typescript
echo(options: capEchoOptions) => Promise<capVideoPlayerResult>
```

Echo

| Param         | Type                                                      |
| ------------- | --------------------------------------------------------- |
| **`options`** | <code><a href="#capechooptions">capEchoOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#capvideoplayerresult">capVideoPlayerResult</a>&gt;</code>

--------------------


### initPlayer(...)

```typescript
initPlayer(options: capVideoPlayerOptions) => Promise<capVideoPlayerResult>
```

Initialize a video player

| Param         | Type                                                                    |
| ------------- | ----------------------------------------------------------------------- |
| **`options`** | <code><a href="#capvideoplayeroptions">capVideoPlayerOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#capvideoplayerresult">capVideoPlayerResult</a>&gt;</code>

--------------------


### isPlaying(...)

```typescript
isPlaying(options: capVideoPlayerIdOptions) => Promise<capVideoPlayerResult>
```

Return if a given playerId is playing

| Param         | Type                                                                        |
| ------------- | --------------------------------------------------------------------------- |
| **`options`** | <code><a href="#capvideoplayeridoptions">capVideoPlayerIdOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#capvideoplayerresult">capVideoPlayerResult</a>&gt;</code>

--------------------


### play(...)

```typescript
play(options: capVideoPlayerIdOptions) => Promise<capVideoPlayerResult>
```

Play the current video from a given playerId

| Param         | Type                                                                        |
| ------------- | --------------------------------------------------------------------------- |
| **`options`** | <code><a href="#capvideoplayeridoptions">capVideoPlayerIdOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#capvideoplayerresult">capVideoPlayerResult</a>&gt;</code>

--------------------


### pause(...)

```typescript
pause(options: capVideoPlayerIdOptions) => Promise<capVideoPlayerResult>
```

Pause the current video from a given playerId

| Param         | Type                                                                        |
| ------------- | --------------------------------------------------------------------------- |
| **`options`** | <code><a href="#capvideoplayeridoptions">capVideoPlayerIdOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#capvideoplayerresult">capVideoPlayerResult</a>&gt;</code>

--------------------


### getDuration(...)

```typescript
getDuration(options: capVideoPlayerIdOptions) => Promise<capVideoPlayerResult>
```

Get the duration of the current video from a given playerId

| Param         | Type                                                                        |
| ------------- | --------------------------------------------------------------------------- |
| **`options`** | <code><a href="#capvideoplayeridoptions">capVideoPlayerIdOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#capvideoplayerresult">capVideoPlayerResult</a>&gt;</code>

--------------------


### getCurrentTime(...)

```typescript
getCurrentTime(options: capVideoPlayerIdOptions) => Promise<capVideoPlayerResult>
```

Get the current time of the current video from a given playerId

| Param         | Type                                                                        |
| ------------- | --------------------------------------------------------------------------- |
| **`options`** | <code><a href="#capvideoplayeridoptions">capVideoPlayerIdOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#capvideoplayerresult">capVideoPlayerResult</a>&gt;</code>

--------------------


### setCurrentTime(...)

```typescript
setCurrentTime(options: capVideoTimeOptions) => Promise<capVideoPlayerResult>
```

Set the current time to seek the current video to from a given playerId

| Param         | Type                                                                |
| ------------- | ------------------------------------------------------------------- |
| **`options`** | <code><a href="#capvideotimeoptions">capVideoTimeOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#capvideoplayerresult">capVideoPlayerResult</a>&gt;</code>

--------------------


### getVolume(...)

```typescript
getVolume(options: capVideoPlayerIdOptions) => Promise<capVideoPlayerResult>
```

Get the volume of the current video from a given playerId

| Param         | Type                                                                        |
| ------------- | --------------------------------------------------------------------------- |
| **`options`** | <code><a href="#capvideoplayeridoptions">capVideoPlayerIdOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#capvideoplayerresult">capVideoPlayerResult</a>&gt;</code>

--------------------


### setVolume(...)

```typescript
setVolume(options: capVideoVolumeOptions) => Promise<capVideoPlayerResult>
```

Set the volume of the current video to from a given playerId

| Param         | Type                                                                    |
| ------------- | ----------------------------------------------------------------------- |
| **`options`** | <code><a href="#capvideovolumeoptions">capVideoVolumeOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#capvideoplayerresult">capVideoPlayerResult</a>&gt;</code>

--------------------


### getMuted(...)

```typescript
getMuted(options: capVideoPlayerIdOptions) => Promise<capVideoPlayerResult>
```

Get the muted of the current video from a given playerId

| Param         | Type                                                                        |
| ------------- | --------------------------------------------------------------------------- |
| **`options`** | <code><a href="#capvideoplayeridoptions">capVideoPlayerIdOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#capvideoplayerresult">capVideoPlayerResult</a>&gt;</code>

--------------------


### setMuted(...)

```typescript
setMuted(options: capVideoMutedOptions) => Promise<capVideoPlayerResult>
```

Set the muted of the current video to from a given playerId

| Param         | Type                                                                  |
| ------------- | --------------------------------------------------------------------- |
| **`options`** | <code><a href="#capvideomutedoptions">capVideoMutedOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#capvideoplayerresult">capVideoPlayerResult</a>&gt;</code>

--------------------


### setRate(...)

```typescript
setRate(options: capVideoRateOptions) => Promise<capVideoPlayerResult>
```

Set the rate of the current video from a given playerId

| Param         | Type                                                                |
| ------------- | ------------------------------------------------------------------- |
| **`options`** | <code><a href="#capvideorateoptions">capVideoRateOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#capvideoplayerresult">capVideoPlayerResult</a>&gt;</code>

--------------------


### getRate(...)

```typescript
getRate(options: capVideoPlayerIdOptions) => Promise<capVideoPlayerResult>
```

Get the rate of the current video from a given playerId

| Param         | Type                                                                        |
| ------------- | --------------------------------------------------------------------------- |
| **`options`** | <code><a href="#capvideoplayeridoptions">capVideoPlayerIdOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#capvideoplayerresult">capVideoPlayerResult</a>&gt;</code>

--------------------


### stopAllPlayers()

```typescript
stopAllPlayers() => Promise<capVideoPlayerResult>
```

Stop all players playing

**Returns:** <code>Promise&lt;<a href="#capvideoplayerresult">capVideoPlayerResult</a>&gt;</code>

--------------------


### Interfaces


#### capVideoPlayerResult

| Prop          | Type                 | Description                                   |
| ------------- | -------------------- | --------------------------------------------- |
| **`result`**  | <code>boolean</code> | result set to true when successful else false |
| **`method`**  | <code>string</code>  | method name                                   |
| **`value`**   | <code>any</code>     | value returned                                |
| **`message`** | <code>string</code>  | message string                                |


#### capEchoOptions

| Prop        | Type                | Description         |
| ----------- | ------------------- | ------------------- |
| **`value`** | <code>string</code> | String to be echoed |


#### capVideoPlayerOptions

| Prop                  | Type                                                        | Description                                                                                   |
| --------------------- | ----------------------------------------------------------- | --------------------------------------------------------------------------------------------- |
| **`mode`**            | <code>string</code>                                         | Player mode - "fullscreen" - "embedded" (Web only)                                            |
| **`url`**             | <code>string</code>                                         | The url of the video to play                                                                  |
| **`subtitle`**        | <code>string</code>                                         | The url of subtitle associated with the video                                                 |
| **`language`**        | <code>string</code>                                         | The language of subtitle see https://github.com/libyal/libfwnt/wiki/Language-Code-identifiers |
| **`subtitleOptions`** | <code><a href="#subtitleoptions">SubTitleOptions</a></code> | SubTitle Options                                                                              |
| **`playerId`**        | <code>string</code>                                         | Id of DIV Element parent of the player                                                        |
| **`rate`**            | <code>number</code>                                         | Initial playing rate                                                                          |
| **`exitOnEnd`**       | <code>boolean</code>                                        | Exit on VideoEnd default: true                                                                |
| **`componentTag`**    | <code>string</code>                                         | Component Tag or DOM Element Tag (React app)                                                  |
| **`width`**           | <code>number</code>                                         | Player Width (mode "embedded" only)                                                           |
| **`height`**          | <code>number</code>                                         | Player height (mode "embedded" only)                                                          |


#### SubTitleOptions

| Prop                  | Type                | Description                                           |
| --------------------- | ------------------- | ----------------------------------------------------- |
| **`foregroundColor`** | <code>string</code> | Foreground Color in RGBA (default rgba(255,255,255,1) |
| **`backgroundColor`** | <code>string</code> | Background Color in RGBA (default rgba(0,0,0,1)       |
| **`fontSize`**        | <code>number</code> | Font Size in pixels (default 16)                      |


#### capVideoPlayerIdOptions

| Prop           | Type                | Description                            |
| -------------- | ------------------- | -------------------------------------- |
| **`playerId`** | <code>string</code> | Id of DIV Element parent of the player |


#### capVideoTimeOptions

| Prop           | Type                | Description                            |
| -------------- | ------------------- | -------------------------------------- |
| **`playerId`** | <code>string</code> | Id of DIV Element parent of the player |
| **`seektime`** | <code>number</code> | Video time value you want to seek to   |


#### capVideoVolumeOptions

| Prop           | Type                | Description                            |
| -------------- | ------------------- | -------------------------------------- |
| **`playerId`** | <code>string</code> | Id of DIV Element parent of the player |
| **`volume`**   | <code>number</code> | Volume value between [0 - 1]           |


#### capVideoMutedOptions

| Prop           | Type                 | Description                            |
| -------------- | -------------------- | -------------------------------------- |
| **`playerId`** | <code>string</code>  | Id of DIV Element parent of the player |
| **`muted`**    | <code>boolean</code> | Muted value true or false              |


#### capVideoRateOptions

| Prop           | Type                | Description                            |
| -------------- | ------------------- | -------------------------------------- |
| **`playerId`** | <code>string</code> | Id of DIV Element parent of the player |
| **`rate`**     | <code>number</code> | Rate value                             |

</docgen-api>

### Listeners

The listeners are attached to the plugin not anymore to the DOM document element.

| Listener                    | Type                                  | Description                             |
| --------------------------- | ------------------------------------- | --------------------------------------- |
| **jeepCapVideoPlayerReady** | [capVideoListener](#capvideolistener) | Emitted when the video start to play    |
| **jeepCapVideoPlayerPlay**  | [capVideoListener](#capvideolistener) | Emitted when the video start to play    |
| **jeepCapVideoPlayerPause** | [capVideoListener](#capvideolistener) | Emitted when the video is paused        |
| **jeepCapVideoPlayerEnded** | [capVideoListener](#capvideolistener) | Emitted when the video has ended        |
| **jeepCapVideoPlayerExit**  | [capExitListener](#capexitlistener)   | Emitted when the Exit button is clicked |

#### capVideoListener

| Prop            | Type   | Description                                |
| --------------- | ------ | ------------------------------------------ |
| **playerId**    | string | Id of DIV Element parent of the player     |
| **currentTime** | number | Video current time when listener trigerred |

#### capExitListener

| Prop        | Type    | Description                 |
| ----------- | ------- | --------------------------- |
| **dismiss** | boolean | Dismiss value true or false |