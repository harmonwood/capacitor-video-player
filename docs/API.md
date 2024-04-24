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
* [`showController()`](#showcontroller)
* [`isControllerIsFullyVisible()`](#iscontrollerisfullyvisible)
* [`exitPlayer()`](#exitplayer)
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

### from DCIM folder

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

### from Internal (Gallery, DCIM)

- Android plugin
  .for API higher than 28 add the following in the app manifest file

  ```
      <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32"/>
  ```
  and you will be prompted for authorization to access media files. 

## Chromecast Support

  Android Only

### Chromecast explanation

The cast option is enabled by default, otherwise you can disable it with the option chromecast = false in InitPlayer options.

Cast button will only be available if your cast devices are connected in the same WIFI network, if not, the button won't be visible.

Some videos won't work with Cast, as not every video format is supported.

When you start casting, the video controllers will be available to control your cast. If you exit the app or close the video, the cast will end automatically.

Cast title will be the same as title and smallTitle, if these are not added, then it will be blank

### Android Quirks

Since 3.7.2, you need to add few things in your Android project to get the plugin working.

- build.gradle (app)

```java
dependencies {
    ...
    implementation 'com.google.android.gms:play-services-cast-framework:21.2.0'
}
```

 - AndroidManifest.xml

```java
<application>
    ...
    <meta-data
        android:name="com.google.android.gms.cast.framework.OPTIONS_PROVIDER_CLASS_NAME"
        android:value="com.google.android.exoplayer2.ext.cast.DefaultCastOptionsProvider" />
</application>
```

 - MainActivity.java

```java
import android.os.Bundle;

import com.google.android.gms.cast.framework.CastContext;


public class MainActivity extends BridgeActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    CastContext.getSharedInstance(this); // <--- add this
  }
}
```

## Methods

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


### showController()

```typescript
showController() => Promise<capVideoPlayerResult>
```

Show controller

**Returns:** <code>Promise&lt;<a href="#capvideoplayerresult">capVideoPlayerResult</a>&gt;</code>

--------------------


### isControllerIsFullyVisible()

```typescript
isControllerIsFullyVisible() => Promise<capVideoPlayerResult>
```

isControllerIsFullyVisible

**Returns:** <code>Promise&lt;<a href="#capvideoplayerresult">capVideoPlayerResult</a>&gt;</code>

--------------------


### exitPlayer()

```typescript
exitPlayer() => Promise<capVideoPlayerResult>
```

Exit player

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

| Prop                  | Type                                                                          | Description                                                                                                                                                 |
| --------------------- | ----------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`mode`**            | <code>string</code>                                                           | Player mode - "fullscreen" - "embedded" (Web only)                                                                                                          |
| **`url`**             | <code>string</code>                                                           | The url of the video to play                                                                                                                                |
| **`subtitle`**        | <code>string</code>                                                           | The url of subtitle associated with the video                                                                                                               |
| **`language`**        | <code>string</code>                                                           | The language of subtitle see https://github.com/libyal/libfwnt/wiki/Language-Code-identifiers                                                               |
| **`subtitleOptions`** | <code><a href="#subtitleoptions">SubTitleOptions</a></code>                   | SubTitle Options                                                                                                                                            |
| **`playerId`**        | <code>string</code>                                                           | Id of DIV Element parent of the player                                                                                                                      |
| **`rate`**            | <code>number</code>                                                           | Initial playing rate                                                                                                                                        |
| **`exitOnEnd`**       | <code>boolean</code>                                                          | Exit on VideoEnd (iOS, Android) default: true                                                                                                               |
| **`loopOnEnd`**       | <code>boolean</code>                                                          | Loop on VideoEnd when exitOnEnd false (iOS, Android) default: false                                                                                         |
| **`pipEnabled`**      | <code>boolean</code>                                                          | Picture in Picture Enable (iOS, Android) default: true                                                                                                      |
| **`bkmodeEnabled`**   | <code>boolean</code>                                                          | Background Mode Enable (iOS, Android) default: true                                                                                                         |
| **`showControls`**    | <code>boolean</code>                                                          | Show Controls Enable (iOS, Android) default: true                                                                                                           |
| **`displayMode`**     | <code>string</code>                                                           | Display Mode ["all", "portrait", "landscape"] (iOS, Android) default: "all"                                                                                 |
| **`componentTag`**    | <code>string</code>                                                           | Component Tag or DOM Element Tag (React app)                                                                                                                |
| **`width`**           | <code>number</code>                                                           | Player Width (mode "embedded" only)                                                                                                                         |
| **`height`**          | <code>number</code>                                                           | Player height (mode "embedded" only)                                                                                                                        |
| **`headers`**         | <code>{ [key: string]: string; }</code>                                       | Headers for the request (iOS, Android) by Manuel García Marín (https://github.com/PhantomPainX)                                                             |
| **`title`**           | <code>string</code>                                                           | Title shown in the player (Android) by Manuel García Marín (https://github.com/PhantomPainX)                                                                |
| **`smallTitle`**      | <code>string</code>                                                           | Subtitle shown below the title in the player (Android) by Manuel García Marín (https://github.com/PhantomPainX)                                             |
| **`accentColor`**     | <code>string</code>                                                           | ExoPlayer Progress Bar and Spinner color (Android) by Manuel García Marín (https://github.com/PhantomPainX) Must be a valid hex color code default: #FFFFFF |
| **`chromecast`**      | <code>boolean</code>                                                          | Chromecast enable/disable (Android) by Manuel García Marín (https://github.com/PhantomPainX) default: true                                                  |
| **`artwork`**         | <code>string</code>                                                           | Artwork url to be shown in Chromecast player by Manuel García Marín (https://github.com/PhantomPainX) default: ""                                           |
| **`drm`**             | <code><a href="#capvideoplayerdrmoptions">capVideoPlayerDRMOptions</a></code> | DRM options                                                                                                                                                 |


#### SubTitleOptions

| Prop                  | Type                | Description                                           |
| --------------------- | ------------------- | ----------------------------------------------------- |
| **`foregroundColor`** | <code>string</code> | Foreground Color in RGBA (default rgba(255,255,255,1) |
| **`backgroundColor`** | <code>string</code> | Background Color in RGBA (default rgba(0,0,0,1)       |
| **`fontSize`**        | <code>number</code> | Font Size in pixels (default 16)                      |


#### capVideoPlayerDRMOptions

| Prop           | Type                                                                                               |
| -------------- | -------------------------------------------------------------------------------------------------- |
| **`widevine`** | <code>{ licenseUri?: string; headers?: { [key: string]: string; }; }</code>                        |
| **`fairplay`** | <code>{ licenseUri: string; certificateUri: string; headers?: { [key: string]: string; }; }</code> |


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

| Prop            | Type    | Description                                |
| --------------- | ------- | ------------------------------------------ |
| **dismiss**     | boolean | Dismiss value true or false                |
| **currentTime** | number  | Video current time when listener trigerred |