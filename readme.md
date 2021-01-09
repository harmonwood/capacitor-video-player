<p align="center"><br><img src="https://user-images.githubusercontent.com/236501/85893648-1c92e880-b7a8-11ea-926d-95355b8175c7.png" width="128" height="128" /></p>
<h3 align="center">Video Player</h3>
<p align="center"><strong><code>capacitor-video-player</code></strong></p>
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
  <a href="https://www.npmjs.com/package/jepiqueau/capacitor-video-player"><img src="https://img.shields.io/npm/l/capacitor-video-player.svg?style=flat-square" /></a>
<br>
  <a href="https://www.npmjs.com/package/jepiqueau/capacitor-video-player"><img src="https://img.shields.io/npm/dw/capacitor-video-player?style=flat-square" /></a>
  <a href="https://www.npmjs.com/package/jepiqueau/capacitor-video-player"><img src="https://img.shields.io/npm/v/capacitor-video-player?style=flat-square" /></a>
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
<a href="#contributors-"><img src="https://img.shields.io/badge/all%20contributors-1-orange?style=flat-square" /></a>
<!-- ALL-CONTRIBUTORS-BADGE:END -->
</p>

## Maintainers

| Maintainer        | GitHub                                    | Social |
| ----------------- | ----------------------------------------- | ------ |
| Quéau Jean Pierre | [jepiqueau](https://github.com/jepiqueau) |        |

## Browser Support

The plugin follows the guidelines from the `Capacitor Team`,

- [Capacitor Browser Support](https://capacitorjs.com/docs/v3/web#browser-support)

meaning that it will not work in IE11 without additional JavaScript transformations, e.g. with [Babel](https://babeljs.io/).

## Installation

```bash
npm install capacitor-video-player
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
| initPlayer (url internal)          | ✅      | ✅  | ❌       | ❌  |
| initPlayer (url application/files) | ✅      | ✅  | ❌       | ❌  |
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

### Ionic/Angular

- [test-angular-jeep-capacitor-plugins](https://github.com/jepiqueau/capacitor-apps/tree/master/IonicAngular/jeep-test-app)

### Application Starter

- [pwa-video-player-app-starter](https://github.com/jepiqueau/pwa-video-player-app-starter)

- [angular-video-player-app-starter](https://github.com/jepiqueau/angular-videoplayer-app-starter)

- [react-video-player-app-starter](https://github.com/jepiqueau/react-video-player-app-starter)

## Usage

- [see capacitor documentation](https://capacitor.ionicframework.com/docs/getting-started/with-ionic)

- In your code

```ts
 import { Component, OnInit } from '@angular/core';
 import { Plugins } from '@capacitor/core';
 import * as WebVPPlugin from 'capacitor-video-player';
 const { CapacitorVideoPlayer,Device } = Plugins;

 @Component({
   tag: 'my-page',
   styleUrl: 'my-page.css'
 })
 export class MyPage implements OnInit {
    private _videoPlayer: any;
    private _url: string;
    private _handlerPlay: any;
    private _handlerPause: any;
    private _handlerEnded: any;
    private _handlerReady: any;
    private _handlerExit: any;
    private _first: boolean = false;
    private _apiTimer1: any;
    private _apiTimer2: any;
    private _apiTimer3: any;
    private _testApi: boolean = true;

    ...
    async ngOnInit() {
      // define the plugin to use
      const info = await Device.getInfo();
      if (info.platform === "ios" || info.platform === "android") {
        this._videoPlayer = CapacitorVideoPlayer;
      } else {
        this._videoPlayer = WebVPPlugin.CapacitorVideoPlayer
      }
      // define the video url
      this._url = "https://archive.org/download/BigBuckBunny_124/Content/big_buck_bunny_720p_surround.mp4"
      // add listeners to the plugin
      this._addListenersToPlayerPlugin();
    }
    async ionViewDidEnter() {
      ...
      const res:any  = await this._videoPlayer.initPlayer({mode:"fullscreen",url:this._url,playerId:"fullscreen",componentTag:"my-page"});
      ...

    }

    private _addListenersToPlayerPlugin() {
      this._handlerPlay = this._videoPlayer.addListener('jeepCapVideoPlayerPlay', (data:any) => {
        console.log('Event jeepCapVideoPlayerPlay ', data);
        ...
      }, false);
      this._handlerPause = this._videoPlayer.addListener('jeepCapVideoPlayerPause', (data:any) => {
        console.log('Event jeepCapVideoPlayerPause ', data);
        ...
      }, false);
      this._handlerEnded = this._videoPlayer.addListener('jeepCapVideoPlayerEnded', async (data:any) => {
        console.log('Event jeepCapVideoPlayerEnded ', data);
        ...
      }, false);
      this._handlerExit = this._videoPlayer.addListener('jeepCapVideoPlayerExit', async (data:any) => {
        console.log('Event jeepCapVideoPlayerExit ', data)
        ...
        }, false);
      this._handlerReady = this._videoPlayer.addListener('jeepCapVideoPlayerReady', async (data:any) => {
        console.log('Event jeepCapVideoPlayerReady ', data)
        console.log("testVideoPlayerPlugin testAPI ",this._testApi);
        console.log("testVideoPlayerPlugin first ",this._first);
        if(this._testApi && this._first) {
          // test the API
          this._first = false;
          console.log("testVideoPlayerPlugin calling isPlaying ");
          const isPlaying = await this._videoPlayer.isPlaying({playerId:"fullscreen"});
          console.log('const isPlaying ', isPlaying)
          this._apiTimer1 = setTimeout(async () => {
            const pause = await this._videoPlayer.pause({playerId:"fullscreen"});
            console.log('const pause ', pause)
            const isPlaying = await this._videoPlayer.isPlaying({playerId:"fullscreen"});
            console.log('const isPlaying after pause ', isPlaying)
            let currentTime = await this._videoPlayer.getCurrentTime({playerId:"fullscreen"});
            console.log('const currentTime ', currentTime);
            let muted = await this._videoPlayer.getMuted({playerId:"fullscreen"});
            console.log('initial muted ', muted);
            const setMuted = await this._videoPlayer.setMuted({playerId:"fullscreen",muted:!muted.value});
            console.log('setMuted ', setMuted);
            muted = await this._videoPlayer.getMuted({playerId:"fullscreen"});
            console.log('const muted ', muted);
            const duration = await this._videoPlayer.getDuration({playerId:"fullscreen"});
            console.log("duration ",duration);
            // valid for movies havin a duration > 25
            const seektime = currentTime.value + 0.5 * duration.value < duration.value -25 ? currentTime.value + 0.5 * duration.value
                            : duration.value -25;
            const setCurrentTime = await this._videoPlayer.setCurrentTime({playerId:"fullscreen",seektime:(seektime)});
            console.log('const setCurrentTime ', setCurrentTime);
            const play = await this._videoPlayer.play({playerId:"fullscreen"});
            console.log("play ",play);
            this._apiTimer2 = setTimeout(async () => {
              const setMuted = await this._videoPlayer.setMuted({playerId:"fullscreen",muted:false});
              console.log('setMuted ', setMuted);
              const setVolume = await this._videoPlayer.setVolume({playerId:"fullscreen",volume:0.5});
              console.log("setVolume ",setVolume);
              const volume = await this._videoPlayer.getVolume({playerId:"fullscreen"});
              console.log("Volume ",volume);
              this._apiTimer3 = setTimeout(async () => {
                const pause = await this._videoPlayer.pause({playerId:"fullscreen"});
                console.log('const pause ', pause);
                const duration = await this._videoPlayer.getDuration({playerId:"fullscreen"});
                console.log("duration ",duration);
                const volume = await this._videoPlayer.setVolume({playerId:"fullscreen",volume:1.0});
                console.log("Volume ",volume);
                const setCurrentTime = await this._videoPlayer.setCurrentTime({playerId:"fullscreen",seektime:(duration.value - 3)});
                console.log('const setCurrentTime ', setCurrentTime);
                const play = await this._videoPlayer.play({playerId:"fullscreen"});
                console.log('const play ', play);
              }, 10000);
            }, 10000);

          }, 5000);
        }
      }, false);

    }
    ...
 }
```

then in my-page.html

```html
...
<!-- Mandatory id="fullscreen" -->
<div id="fullscreen" slot="fixed"></div>
...
```

### Usage on PWA

- in your code

```ts
import {  CapacitorVideoPlayer } from 'capacitor-video-player';
 @Component( ... )
 export class MyPage {
    private _videoPlayer: any;
    private _url: string;
    private _handlerPlay: any;
    private _handlerPause: any;
    private _handlerEnded: any;
    private _handlerReady: any;
    private _handlerPlaying: any;
    private _handlerExit: any;

    componentWillLoad() {
      ...
      this._videoPlayer = CapacitorVideoPlayer;
      this._url = "https://archive.org/download/BigBuckBunny_124/Content/big_buck_bunny_720p_surround.mp4";
      this._addListenersToPlayerPlugin();
      ...
    }
    async componentDidLoad() {
      ...
      const res:any  = await videoPlayer.initPlayer({mode:"fullscreen",url:this._url,playerId="fullscreen",componentTag="my-page"});
      console.log('result of init ', res)
      ...
    }
    private _addListenersToPlayerPlugin() {
      this._handlerPlay = this._videoPlayer.addListener('jeepCapVideoPlayerPlay', (data:any) => {
        console.log('Event jeepCapVideoPlayerPlay ', data);
        ...
      }, false);
      this._handlerPause = this._videoPlayer.addListener('jeepCapVideoPlayerPause', (data:any) => {
        console.log('Event jeepCapVideoPlayerPause ', data);
        ...
      }, false);
      this._handlerEnded = this._videoPlayer.addListener('jeepCapVideoPlayerEnded', async (data:any) => {
        console.log('Event jeepCapVideoPlayerEnded ', data);
        ...
      }, false);
      this._handlerExit = this._videoPlayer.addListener('jeepCapVideoPlayerExit', async (data:any) => {
        console.log('Event jeepCapVideoPlayerExit ', data)
        ...
        }, false);
      this._handlerReady = this._videoPlayer.addListener('jeepCapVideoPlayerReady', async (data:any) => {
        console.log('Event jeepCapVideoPlayerReady ', data)
        ...
      }, false);
      this._handlerPlaying = this._videoPlayer.addListener('jeepCapVideoPlayerPlaying', async (data:any) => {
        console.log('Event jeepCapVideoPlayerPlaying ', data)
        ...
      }, false);

    }
    render() {
      return (
        <Host>
          <slot>
            <div id="fullscreen">

            </div>
          </slot>
        </Host>
      );
    }
 }
```

```bash
npm run build
npx cap copy
npx cap copy web
npm start
```

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
  </tr>
</table>

<!-- markdownlint-enable -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!
