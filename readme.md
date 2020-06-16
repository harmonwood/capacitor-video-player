# Capacitor Video Player Plugin

Capacitor Video Player Plugin is a custom Native Capacitor plugin to play a video 
 - fullscreen on IOS, Android, Web and Electron platforms 
 - embedded on Web and Electron platforms
As capacitor provides first-class Progressive Web App support so you can use this plugin in your app which will later be deployed to the app stores and the mobile web.

### `available since 2.0.1-5`

  - Adding support for Hls sreaming video for Web and IOS plugins
  - the playerId is required in fullscreen mode
  - the component or selector tag is required
  - in the Web player, the video start with the muted on

### `available since 2.0.1`

  - Adding support for DASH, HLS, ISM videos for Android plugin by using the ExoPlayer

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

#### returns for all methods
  - success {"result": true, "method":"methodName", "value": valueReturned}
  - reject {"result": false, "method":"methodName", "message": errorMessage}

Type: `Promise<{result:boolean, method:string,value:any}>`
Type: `Promise<{result:boolean, method:string,message:string}>`


## Methods Available for Interacting with the plugin

### `isPlaying(options) => Promise<{method:string,result:boolean,value?:any,message?:string}>`

Return the isPlaying video player status

#### options

   ```{playerId: string}  default "fullscreen"```

### `play(options) => Promise<{method:string,result:boolean,value?:any,message?:string}>`

Play the Video 

#### options

   ```{playerId: string}  default "fullscreen"```

### `pause(options) => Promise<{method:string,result:boolean,value?:any,message?:string}>`

Pause the Video 

#### options

   ```{playerId: string}  default "fullscreen"```

### `getDuration(options) => Promise<{method:string,result:boolean,value?:any,message?:string}>`

Get the Duration of the Video in seconds

#### options

   ```{playerId: string}  default "fullscreen"```

### `setVolume(options) => Promise<{method:string,result:boolean,value?:any,message?:string}>`

Set the Volume of the Video 

#### options

   ```{playerId: string, volume: number}  range[0-1] default 0.5```

### `getVolume(options) => Promise<{method:string,result:boolean,value?:any,message?:string}>`

Get the Volume of the Video 

#### options

   ```{playerId: string}  default "fullscreen"```

### `setMuted(options) => Promise<{method:string,result:boolean,value?:any,message?:string}>`

Set the Muted parameter of the Video 

#### options

   ```{playerId: string, muted: boolean} ```

### `getMuted(options) => Promise<{method:string,result:boolean,value?:any,message?:string}>`

Get the Muted parameter of the Video 

#### options

   ```{playerId: string}  default "fullscreen"```

### `setCurrentTime(options) => Promise<{method:string,result:boolean,value?:any,message?:string}>`

Set the Current Time to Seek the Video to in seconds

#### options

   ```{playerId: string, seektime: number} ```

### `getCurrentTime(options) => Promise<{method:string,result:boolean,value?:any,message?:string}>`

Get the Current Time of the Video in seconds

#### options

   ```{playerId: string}  default "fullscreen"```

### `stopAllPlayers() => Promise<{method:string,result:boolean,value?:any,message?:string}>`

Stop all Players in "embedded" mode

#### returns
Type: `Promise<{method:string,result:boolean}>`


## Plugin Listeners

The listeners are attached to the plugin not anymore to the DOM document element.

| Event                       | Description                                        | Type                                 |
| --------------------------- | -------------------------------------------------- | ------------------------------------ |
| `jeepCapVideoPlayerReady`    | Emitted when the video start to play              | `data:{fromPlayerId:string,currentTime:number}` |
| `jeepCapVideoPlayerPlay`    | Emitted when the video start to play               | `data:{fromPlayerId:string,currentTime:number}` |
| `jeepCapVideoPlayerPause`   | Emitted when the video is paused                   | `data:{fromPlayerId:string,currentTime:number}` |
| `jeepCapVideoPlayerEnded`   | Emitted when the video has ended                   | `data:{fromPlayerId:string,currentTime:number}` |
| `jeepCapVideoPlayerExit`    | Emitted when the Exit button is clicked            | `data:{dismiss:boolean}` |

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
    private _handlerPlaying: any;
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
    this._handlerPlaying = this._videoPlayer.addListener('jeepCapVideoPlayerPlaying', async (data:any) => { 
      console.log('Event jeepCapVideoPlayerPlaying ', data)
      ...
    }, false);

  }

        ...
      }
    }
    ...
  }
 ```

 ```html
  ...
  <!-- Mandatory id="fullscreen" -->
  <div id="fullscreen" slot="fixed">
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

**Enabling Background Audio**
In Xcode follows these steps

 - Choose your project in the Project Navigator,
 - Choose the target on the Project Editor,
 - Choose the Signing & Capabilities tab,
 - Add the capability for ```Background Modes```,
 - Select the ```Audio, Airplay, and Picture-in-Picture``` option under the list of available modes.


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

