# API Documentation

## Methods Index

<!--DOCGEN_INDEX_START-->

- [echo()](#echo)
- [initPlayer()](#initplayer)
- [isPlaying()](#isplaying)
- [play()](#play)
- [pause()](#pause)
- [getDuration()](#getduration)
- [getCurrentTime()](#getcurrenttime)
- [setCurrentTime()](#setcurrenttime)
- [getVolume()](#getvolume)
- [setVolume()](#setvolume)
- [getMuted()](#getmuted)
- [setMuted()](#setmuted)
- [stopAllPlayers()](#stopallplayers)
- [Interfaces](#interfaces)
- [Listeners](#listeners)
<!--DOCGEN_INDEX_END-->

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

<!--DOCGEN_API_START-->
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

## API

### echo

```typescript
echo(options: capEchoOptions) => Promise<capVideoPlayerResult>
```

Echo

| Param       | Type                              |
| ----------- | --------------------------------- |
| **options** | [capEchoOptions](#capechooptions) |

**Returns:** Promise&lt;[capVideoPlayerResult](#capvideoplayerresult)&gt;

---

### initPlayer

```typescript
initPlayer(options: capVideoPlayerOptions) => Promise<capVideoPlayerResult>
```

Initialize a video player

| Param       | Type                                            |
| ----------- | ----------------------------------------------- |
| **options** | [capVideoPlayerOptions](#capvideoplayeroptions) |

**Returns:** Promise&lt;[capVideoPlayerResult](#capvideoplayerresult)&gt;

---

### isPlaying

```typescript
isPlaying(options: capVideoPlayerIdOptions) => Promise<capVideoPlayerResult>
```

Return if a given playerId is playing

| Param       | Type                                                |
| ----------- | --------------------------------------------------- |
| **options** | [capVideoPlayerIdOptions](#capvideoplayeridoptions) |

**Returns:** Promise&lt;[capVideoPlayerResult](#capvideoplayerresult)&gt;

---

### play

```typescript
play(options: capVideoPlayerIdOptions) => Promise<capVideoPlayerResult>
```

Play the current video from a given playerId

| Param       | Type                                                |
| ----------- | --------------------------------------------------- |
| **options** | [capVideoPlayerIdOptions](#capvideoplayeridoptions) |

**Returns:** Promise&lt;[capVideoPlayerResult](#capvideoplayerresult)&gt;

---

### pause

```typescript
pause(options: capVideoPlayerIdOptions) => Promise<capVideoPlayerResult>
```

Pause the current video from a given playerId

| Param       | Type                                                |
| ----------- | --------------------------------------------------- |
| **options** | [capVideoPlayerIdOptions](#capvideoplayeridoptions) |

**Returns:** Promise&lt;[capVideoPlayerResult](#capvideoplayerresult)&gt;

---

### getDuration

```typescript
getDuration(options: capVideoPlayerIdOptions) => Promise<capVideoPlayerResult>
```

Get the duration of the current video from a given playerId

| Param       | Type                                                |
| ----------- | --------------------------------------------------- |
| **options** | [capVideoPlayerIdOptions](#capvideoplayeridoptions) |

**Returns:** Promise&lt;[capVideoPlayerResult](#capvideoplayerresult)&gt;

---

### getCurrentTime

```typescript
getCurrentTime(options: capVideoPlayerIdOptions) => Promise<capVideoPlayerResult>
```

Get the current time of the current video from a given playerId

| Param       | Type                                                |
| ----------- | --------------------------------------------------- |
| **options** | [capVideoPlayerIdOptions](#capvideoplayeridoptions) |

**Returns:** Promise&lt;[capVideoPlayerResult](#capvideoplayerresult)&gt;

---

### setCurrentTime

```typescript
setCurrentTime(options: capVideoTimeOptions) => Promise<capVideoPlayerResult>
```

Set the current time to seek the current video to from a given playerId

| Param       | Type                                        |
| ----------- | ------------------------------------------- |
| **options** | [capVideoTimeOptions](#capvideotimeoptions) |

**Returns:** Promise&lt;[capVideoPlayerResult](#capvideoplayerresult)&gt;

---

### getVolume

```typescript
getVolume(options: capVideoPlayerIdOptions) => Promise<capVideoPlayerResult>
```

Get the volume of the current video from a given playerId

| Param       | Type                                                |
| ----------- | --------------------------------------------------- |
| **options** | [capVideoPlayerIdOptions](#capvideoplayeridoptions) |

**Returns:** Promise&lt;[capVideoPlayerResult](#capvideoplayerresult)&gt;

---

### setVolume

```typescript
setVolume(options: capVideoVolumeOptions) => Promise<capVideoPlayerResult>
```

Set the volume of the current video to from a given playerId

| Param       | Type                                            |
| ----------- | ----------------------------------------------- |
| **options** | [capVideoVolumeOptions](#capvideovolumeoptions) |

**Returns:** Promise&lt;[capVideoPlayerResult](#capvideoplayerresult)&gt;

---

### getMuted

```typescript
getMuted(options: capVideoPlayerIdOptions) => Promise<capVideoPlayerResult>
```

Get the muted of the current video from a given playerId

| Param       | Type                                                |
| ----------- | --------------------------------------------------- |
| **options** | [capVideoPlayerIdOptions](#capvideoplayeridoptions) |

**Returns:** Promise&lt;[capVideoPlayerResult](#capvideoplayerresult)&gt;

---

### setMuted

```typescript
setMuted(options: capVideoMutedOptions) => Promise<capVideoPlayerResult>
```

Set the muted of the current video to from a given playerId

| Param       | Type                                          |
| ----------- | --------------------------------------------- |
| **options** | [capVideoMutedOptions](#capvideomutedoptions) |

**Returns:** Promise&lt;[capVideoPlayerResult](#capvideoplayerresult)&gt;

---

### stopAllPlayers

```typescript
stopAllPlayers() => Promise<capVideoPlayerResult>
```

Stop all players playing

**Returns:** Promise&lt;[capVideoPlayerResult](#capvideoplayerresult)&gt;

---

### Interfaces

#### capVideoPlayerResult

| Prop        | Type    | Description                                   |
| ----------- | ------- | --------------------------------------------- |
| **result**  | boolean | result set to true when successful else false |
| **method**  | string  | method name                                   |
| **value**   | any     | value returned                                |
| **message** | string  | message string                                |

#### capEchoOptions

| Prop      | Type   | Description         |
| --------- | ------ | ------------------- |
| **value** | string | String to be echoed |

#### capVideoPlayerOptions

| Prop                 | Type    | Description                                        |
| -------------------- | ------- | -------------------------------------------------- |
| **mode**             | string  | Player mode - "fullscreen" - "embedded" (Web only) |
| **url**              | string  | The url of the video to play                       |
| **playerId**         | string  | Id of DIV Element parent of the player             |
| **componentTag**     | string  | Component Tag or DOM Element Tag (React app)       |
| **width**            | number  | Player Width (mode "embedded" only)                |
| **height**           | number  | Player height (mode "embedded" only)               |
| **hideCloseButton**  | boolean | Whether to hide the close button                   |

#### capVideoPlayerIdOptions

| Prop         | Type   | Description                            |
| ------------ | ------ | -------------------------------------- |
| **playerId** | string | Id of DIV Element parent of the player |

#### capVideoTimeOptions

| Prop         | Type   | Description                            |
| ------------ | ------ | -------------------------------------- |
| **playerId** | string | Id of DIV Element parent of the player |
| **seektime** | number | Video time value you want to seek to   |

#### capVideoVolumeOptions

| Prop         | Type   | Description                            |
| ------------ | ------ | -------------------------------------- |
| **playerId** | string | Id of DIV Element parent of the player |
| **volume**   | number | Volume value between [0 - 1]           |

#### capVideoMutedOptions

| Prop         | Type    | Description                            |
| ------------ | ------- | -------------------------------------- |
| **playerId** | string  | Id of DIV Element parent of the player |
| **muted**    | boolean | Muted value true or false              |

<!--DOCGEN_API_END-->

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
