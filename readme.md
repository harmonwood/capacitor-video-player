<p align="center"><br><img src="https://user-images.githubusercontent.com/236501/85893648-1c92e880-b7a8-11ea-926d-95355b8175c7.png" width="128" height="128" /></p>
<h3 align="center">Video Player</h3>
<p align="center"><strong><code>capacitor-video-player</code></strong></p>
<br>
<p align="center" style="font-size:50px;color:red"><strong>CAPACITOR 3 </strong></p><br>
<p align="center">
  Capacitor Video Player Plugin is a custom Native Capacitor plugin to play a video 
<br>
  <strong>fullscreen</strong> on IOS, Android, Web and Electron platforms
<br>
  <strong>embedded</strong> on Web and Electron platforms
</p>

<p align="center">
  <img src="https://img.shields.io/maintenance/yes/2021?style=flat-square" />
  <a href="https://github.com/jepiqueau/capacitor-video-player/actions?query=workflow%3A%22CI%22"><img src="https://img.shields.io/github/workflow/status/jepiqueau/capacitor-video-player/CI?style=flat-square" /></a>
  <a href="https://www.npmjs.com/package/capacitor-video-player"><img src="https://img.shields.io/npm/l/capacitor-video-player.svg?style=flat-square" /></a>
<br>
  <a href="https://www.npmjs.com/package/capacitor-video-player"><img src="https://img.shields.io/npm/dw/capacitor-video-player?style=flat-square" /></a>
  <a href="https://www.npmjs.com/package/capacitor-video-player"><img src="https://img.shields.io/npm/v/capacitor-video-player?style=flat-square" /></a>
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
<a href="#contributors-"><img src="https://img.shields.io/badge/all%20contributors-4-orange?style=flat-square" /></a>
<!-- ALL-CONTRIBUTORS-BADGE:END -->
</p>

## Maintainers

| Maintainer        | GitHub                                    | Social |
| ----------------- | ----------------------------------------- | ------ |
| Quéau Jean Pierre | [jepiqueau](https://github.com/jepiqueau) |        |


## LATEST FOR CAPACITOR 3 (Master)

The master release has been upgraded to `@capacitor/core@3.3.1`.

🚨 Since release 3.3.1 ->> 🚨

the PIP (Picture in Picture) mode has been added to the Android (API >= N) and iOS (iPad - iOS >= 13, iPhone - iOS >= 14) plugins. Is it accessible by clicking on the PiP button next to the Close button on the top-left window corner.

In Android, to have it running in your application, you must include in the `AndroidManifest.xml`file under the `<activity` tag the following
```
    android:resizeableActivity="true"
    android:supportsPictureInPicture="true"
```

In iOS, to have it running in your application, you must set the `Background Modes` application capabilities and switch on the mode `Audio, Air-Play, and Picture in Picture` 

🚨 Since release 3.3.1 <<- 🚨

## PREVIOUS CAPACITOR 2.4.7

The `2.4.7` is now 🛑 NOT MAINTAINED ANYMORE 🛑 and can be used as is.

## Browser Support

The plugin follows the guidelines from the `Capacitor Team`,

- [Capacitor Browser Support](https://capacitorjs.com/docs/v3/web#browser-support)

meaning that it will not work in IE11 without additional JavaScript transformations, e.g. with [Babel](https://babeljs.io/).

## Installation

### Release 2.4.7

  ```bash
  npm install --save capacitor-video-player@2.4.7
  npx cap sync
  npx cap sync @capacitor-community/electron
  ```
  - On Web and Electron, no further steps are needed.

  - on iOS, you need to set your app Capabilities Background Modes (Audio and AirPlay) in Xcode

  - On Android, register the plugin in your main activity:

  ```java
  import com.jeep.plugin.capacitor.CapacitorVideoPlayer;

  public class MainActivity extends BridgeActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      // Initializes the Bridge
      this.init(
          savedInstanceState,
          new ArrayList<Class<? extends Plugin>>() {
            {
              // Additional plugins you've installed go here
              // Ex: add(TotallyAwesomePlugin.class);
              add(CapacitorVideoPlayer.class);
            }
          }
        );
    }
  }

  ```

### Release 3.0.0 

  ```bash
  npm install --save capacitor-video-player@latest
  npx cap sync
  npx cap sync @capacitor-community/electron
  ```

  - On Web and Electron , 
  ```
  npm install --save hls.js
  ```

  - on iOS, you need to set your app Capabilities Background Modes (Audio and AirPlay) in Xcode

  - On Android, the `AndroidManifest.xml`should include
  ```
    <!-- Permissions -->

    <uses-permission android:name="android.permission.INTERNET" />
    <!-- Camera, Photos, input file -->
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>

  ```


### Build
  Then build YOUR_APPLICATION

  ```
  npm run build
  npx cap copy
  npx cap copy web
  npx cap copy @capacitor-community/electron
  npx cap open android
  npx cap open ios
  npx cap open @capacitor-community/electron
  npx cap serve
  ```

## Configuration

No configuration required for this plugin

## Supported methods

| Name                               | Android | iOS | Electron | Web |
| :--------------------------------- | :------ | :-- | :------- | :-- |
| initPlayer (mode fullscreen)       | ✅      | ✅  | ✅       | ✅  |
| initPlayer (mode embedded)         | ❌      | ❌  | ✅       | ✅  |
| initPlayer (url assets)            | ✅      | ✅  | ✅       | ✅  |
| initPlayer (url internal)          | ✅      | ✅  | ❌       | ❌  |
| initPlayer (url application/files) | ✅      | ✅  | ❌       | ❌  |
| initPlayer (subtitles)             | ✅      | ✅  | ❌       | ❌  |
| isPlaying                          | ✅      | ✅  | ✅       | ✅  |
| play                               | ✅      | ✅  | ✅       | ✅  |
| pause                              | ✅      | ✅  | ✅       | ✅  |
| getCurrentTime                     | ✅      | ✅  | ✅       | ✅  |
| setCurrentTime                     | ✅      | ✅  | ✅       | ✅  |
| getDuration                        | ✅      | ✅  | ✅       | ✅  |
| getMuted                           | ✅      | ✅  | ✅       | ✅  |
| setMuted                           | ✅      | ✅  | ✅       | ✅  |
| getVolume                          | ✅      | ✅  | ✅       | ✅  |
| setVolume                          | ✅      | ✅  | ✅       | ✅  |
| stopAllPlayers                     | ✅      | ✅  | ✅       | ✅  |
| getRate                            | ✅      | ✅  | ✅       | ✅  |
| setRate                            | ✅      | ✅  | ✅       | ✅  |

## Supported listeners

| Name                    | Android | iOS | Electron | Web |
| :---------------------- | :------ | :-- | :------- | :-- |
| jeepCapVideoPlayerReady | ✅      | ✅  | ✅       | ✅  |
| jeepCapVideoPlayerPlay  | ✅      | ✅  | ✅       | ✅  |
| jeepCapVideoPlayerPause | ✅      | ✅  | ✅       | ✅  |
| jeepCapVideoPlayerEnded | ✅      | ✅  | ✅       | ✅  |
| jeepCapVideoPlayerExit  | ✅      | ✅  | ✅       | ✅  |

## Documentation

[API_Documentation](https://github.com/jepiqueau/capacitor-video-player/blob/master/docs/API.md)

## Applications demonstrating the use of the plugin

### Application Starter 2.4.7

- [pwa-video-player-app-starter](https://github.com/jepiqueau/pwa-video-player-app-starter)

- [angular-video-player-app-starter](https://github.com/jepiqueau/angular-videoplayer-app-starter/tree/0.6.4)

- [react-video-player-app-starter](https://github.com/jepiqueau/react-video-player-app-starter/blob/0.0.5)

### Application Starter 3.0.0 🚧

- [angular-video-player-app-starter](https://github.com/jepiqueau/angular-videoplayer-app-starter)

- [react-video-player-app-starter](https://github.com/jepiqueau/react-video-player-app-starter)

- [vite-react-videoplayer-app](https://github.com/jepiqueau/vite-react-videoplayer-app)
 
- [vue-videoplayer-app](https://github.com/jepiqueau/vue-videoplayer-app-starter)

## Usage 2.4.7

- [see capacitor documentation](https://capacitor.ionicframework.com/docs/getting-started/with-ionic)

- [see usage 2.4.7](https://github.com/jepiqueau/capacitor-video-player/blob/master/docs/Usage_2.4.7.md)

## Usage 3.0.0 🚧

- [see capacitor documentation](https://capacitor.ionicframework.com/docs/getting-started/with-ionic)

- [see usage 3.0.0](https://github.com/jepiqueau/capacitor-video-player/blob/master/docs/Usage_3.0.0.md)

## Dependencies

- hls.js for HLS videos on Web and Electron platforms
- ExoPlayer for HLS, DASH, SmoothStreaming videos on Android platform

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="https://github.com/jepiqueau"><img src="https://avatars3.githubusercontent.com/u/16580653?v=4" width="100px;" alt=""/><br /><sub><b>Jean Pierre Quéau</b></sub></a><br /><a href="https://github.com/jepiqueau/capacitor-video-player/commits?author=jepiqueau" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/yelhouti"><img src="https://avatars.githubusercontent.com/u/5471639?v=4" width="100px;" alt=""/><br /><sub><b>Yelhouti</b></sub></a><br /><a href="https://github.com/jepiqueau/capacitor-video-player/commits?author=yelhouti" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/mamane10"><img src="https://avatars.githubusercontent.com/u/46500089?v=4" width="100px;" alt=""/><br /><sub><b>Mamane10</b></sub></a><br /><a href="https://github.com/jepiqueau/capacitor-video-player/commits?author=mamane10" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/KANekT"><img src="https://avatars.githubusercontent.com/u/580273?v=4" width="100px;" alt=""/><br /><sub><b>Пронин Андрей KANekT</b></sub></a><br /><a href="https://github.com/jepiqueau/capacitor-video-player/commits?author=KANekT" title="Code">💻</a></td>
  </tr>
</table>

<!-- markdownlint-enable -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!
