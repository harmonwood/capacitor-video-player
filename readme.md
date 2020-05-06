# Capacitor Video Player Plugin

Capacitor Video Player Plugin is a custom Native Capacitor plugin to play a video 
 - fullscreen on IOS, Android, Web and Electron platforms 
 - embedded on Web and Electron platforms
As capacitor provides first-class Progressive Web App support so you can use this plugin in your app which will later be deployed to the app stores and the mobile web.

### `available since 2.0.1-5`

  - Adding support for Hls sreaming video
  - the playerId is required in fullscreen mode
  - the component or selector tag is required


## Methods Available for IOS, Android, Web Plugins

### `initPlayer(options) => Promise<{result:boolean}>`

Initialize the Video Player 

#### options
 - available for IOS, Android, Web Plugins

   ```{mode: "fullscreen", url: string, playerId: string, componentTag:string}```
   initialize the player for a video given by an url
 
    the `url` parameter can be :
      . "https:/..../video.mp4" for files from the web
      . if not "https"
        . for IOS "public/assets/video/video.mp4" 
        . for Android "/raw/video" without the type. You have to create manually a raw folder under the res folder of your app and copy the video file in it
        . for Web "assets/video/video.mp4"

    the `playerId` is the id of a div element used as container for the player

    the `componentTag` is the component tag or component selector from where the video player is called


 - available for  Web Plugin only 

   ```{mode: "embedded", url: string, playerId: string,componentTag:string, width:number, height:number}```
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

### `stopAllPlayers() => Promise<{method:string,result:boolean}>`

Stop all Players in "embedded" mode

#### returns
Type: `Promise<{method:string,result:boolean}>`


## Events available for Web plugin

| Event                       | Description                                        | Type                                 |
| --------------------------- | -------------------------------------------------- | ------------------------------------ |
| `jeepCapVideoPlayerPlay`    | Emitted when the video start to play               | `CustomEvent<{fromPlayerId:string,currentTime:number}>` |
| `jeepCapVideoPlayerPause`   | Emitted when the video is paused                   | `CustomEvent<{fromPlayerId:string,currentTime:number}>` |
| `jeepCapVideoPlayerPlaying` | Emitted when the video restart to play             | `CustomEvent<{fromPlayerId:string,currentTime:number}>` |
| `jeepCapVideoPlayerEnded`   | Emitted when the video has ended                   | `CustomEvent<{fromPlayerId:string,currentTime:number}>` |
| `jeepCapVideoPlayerExit`    | Emitted when the Exit button clicked in fullscreen | `CustomEvent<{fromPlayerId:string}>` |

the target for the events is the document

## Applications demonstrating the use of the plugin

### Ionic/Angular

  - [test-angular-jeep-capacitor-plugins](https://github.com/jepiqueau/capacitor-apps/tree/master/IonicAngular/jeep-test-app)

### Application Starter

  - [pwa-video-player-app-starter](https://github.com/jepiqueau/pwa-video-player-app-starter)

  - [angular-video-player-starter](https://github.com/jepiqueau/angular-videoplayer-app-starter)




## Using the Plugin in your App

 - [see capacitor documentation](https://capacitor.ionicframework.com/docs/getting-started/with-ionic)

 - Plugin installation

  ```bash
  npm install --save capacitor-video-player@latest
  ```
 - In your code

 ```ts
  import { Plugins } from '@capacitor/core';
  import * as WebVPPlugin from 'capacitor-video-player';
  const { CapacitorVideoPlayer,Device } = Plugins;

  @Component({
    tag: 'my-page',
    styleUrl: 'my-page.css',
    shadow: true,
  })
  export class MyPage {
    _videoPlayer: any;
    _url: string;

    ...

    async ngAfterViewInit()() {
      const info = await Device.getInfo();
      if (info.platform === "ios" || info.platform === "android") {
        this._videoPlayer = CapacitorVideoPlayer;
      } else {
        this._videoPlayer = WebVPPlugin.CapacitorVideoPlayer
      }

    }

    async testVideoPlayerPlugin() {
      this._url = "https://archive.org/download/BigBuckBunny_124/Content/big_buck_bunny_720p_surround.mp4";
      document.addEventListener('jeepCapVideoPlayerPlay', (e:CustomEvent) => { console.log('Event jeepCapVideoPlayerPlay ', e.detail)}, false);
      document.addEventListener('jeepCapVideoPlayerPause', (e:CustomEvent) => { console.log('Event jeepCapVideoPlayerPause ', e.detail)}, false);
      document.addEventListener('jeepCapVideoPlayerEnded', (e:CustomEvent) => { console.log('Event jeepCapVideoPlayerEnded ', e.detail)}, false);
      const res:any  = await this._videoPlayer.initPlayer({mode:"fullscreen",url:this._url,playerId:"vp-container",componentTag:"my-page"});

        ...
      }
    }
    ...
  }
 ```

 ```html
  ...
    <div id="fullscreen">
    </div>      
  ...
 ```

### Running on Android

 ```bash
 npx cap update
 npm run build
 npx cap copy
 npx cap open android
 ``` 
 Android Studio will be opened with your project and will sync the files.
 In Android Studio go to the file MainActivity

 ```java 
  ...
 import com.jeep.plugin.capacitor.CapacitorVideoPlayer;

  ...

  public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      // Initializes the Bridge
      this.init(savedInstanceState, new ArrayList<Class<? extends Plugin>>() {{
        // Additional plugins you've installed go here
        // Ex: add(TotallyAwesomePlugin.class);
        add(CapacitorVideoPlayer.class);
      }});
    }
  }
 ``` 
### Running on IOS

 Modify the Podfile under the ios folder as follows

 ```
 platform :ios, '11.0'
 use_frameworks!

 # workaround to avoid Xcode 10 caching of Pods that requires
 # Product -> Clean Build Folder after new Cordova plugins installed
 # Requires CocoaPods 1.6 or newer
 install! 'cocoapods', :disable_input_output_paths => true

 def capacitor_pods
  # Automatic Capacitor Pod dependencies, do not delete
  pod 'Capacitor', :path => '../../node_modules/@capacitor/ios'
  pod 'CapacitorCordova', :path => '../../node_modules/@capacitor/ios'
  # Do not delete
 end

 target 'App' do
  capacitor_pods
  # Add your Pods here
 end
 ```

 ```bash
 npx cap update
 npm run build
 npx cap copy
 npx cap open ios
 ```

### Running on Electron

 ```bash
 npx cap update
 npm run build
 npx cap copy
 npx cap copy web
 npx cap open electron
 ``` 

### Running on PWA

 - in your code
 ```ts
 import {  CapacitorVideoPlayer } from 'capacitor-video-player';
  @Component( ... )
  export class MyApp {
    componentDidLoad() {
    const videoPlayer: any = CapacitorVideoPlayer;
    const url:string = "https://archive.org/download/BigBuckBunny_124/Content/big_buck_bunny_720p_surround.mp4";
    document.addEventListener('jeepCapVideoPlayerPlay', (e:CustomEvent) => { console.log('Event jeepCapVideoPlayerPlay ', e.detail)}, false);
    document.addEventListener('jeepCapVideoPlayerPause', (e:CustomEvent) => { console.log('Event jeepCapVideoPlayerPause ', e.detail)}, false);
    document.addEventListener('jeepCapVideoPlayerEnded', (e:CustomEvent) => { console.log('Event jeepCapVideoPlayerEnded ', e.detail)}, false);
    const res:any  = await videoPlayer.initPlayer({mode:"fullscreen",url:url,playerId="fullscreen",componentTag="my-page"});
    console.log('result of echo ', res)
    }
  }
 ```

 ```bash
 npm run build
 npx cap copy
 npx cap copy web
 npm start
 ``` 

