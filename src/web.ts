import { WebPlugin } from '@capacitor/core';
import { CapacitorVideoPlayerPlugin, capVideoPlayerOptions, capVideoPlayerResult } from './definitions';
import { VideoPlayer } from './web-utils/videoplayer';

export interface IPlayerSize {
  height?: number;
  width?: number;
}

export class CapacitorVideoPlayerWeb extends WebPlugin implements CapacitorVideoPlayerPlugin {
  private _players: any = [];

  constructor() {

    super({
      name: 'CapacitorVideoPlayer',
      platforms: ['web']
    });
  }
  /**
   *  Player initialization
   * 
   * @param options 
   */
  async initPlayer(options: capVideoPlayerOptions): Promise<capVideoPlayerResult> {
    let mode:string = options.mode;
    if (mode == null || mode.length === 0) {
      return Promise.reject("VideoPlayer initPlayer: Must provide a Mode (fullscreen/embedded)");
    }
    if(mode === "fullscreen" || mode === "embedded") {
      let url:string = options.url;
      if (url == null || url.length === 0) {
        return Promise.reject("VideoPlayer initPlayer: Must provide a Video Url");
      }
      if (mode === "embedded") {
        let playerId:string = options.playerId;
        if (playerId == null || playerId.length === 0) {
          return Promise.reject("VideoPlayer initPlayer: Must provide a Player Id");
        }
        const playerSize: IPlayerSize = this.checkSize(options)
        const result = await this._initializeVideoPlayerEmbedded(url,playerId,playerSize)
        return Promise.resolve({ result: result });
      }
      if( mode === "fullscreen") {
        const result = await this._initializeVideoPlayerFullScreen(url)
        return Promise.resolve({ result: result });
      }
    } else {
      return Promise.reject("VideoPlayer initPlayer: Must provide a Mode either fullscreen or embedded)");
    }
  }
  /**
   * Play the current video from a given playerId
   * 
   * @param options 
   */
  async play(options: capVideoPlayerOptions): Promise<capVideoPlayerResult> {
    let playerId:string = options.playerId;
    if (playerId == null || playerId.length === 0) {
      playerId = "fullscreen";
    }
    if(this._players[playerId]) {
      this._players[playerId].videoEl.play();
      return Promise.resolve({ method: "play", result: true });
    } else {
      return Promise.reject("VideoPlayer Play: Given PlayerId does not exist)");
    }
  }
  /**
   * Pause the current video from a given playerId
   * 
   * @param options 
   */
  async pause(options: capVideoPlayerOptions): Promise<capVideoPlayerResult> {
    let playerId:string = options.playerId;
    if (playerId == null || playerId.length === 0) {
      playerId = "fullscreen";
    }
    if(this._players[playerId]) {
      this._players[playerId].videoEl.pause();
      return Promise.resolve({ method: "pause", result: true });
    } else {
      return Promise.reject("VideoPlayer Pause: Given PlayerId does not exist)");
    }
  }
  /**
   * Get the duration of the current video from a given playerId
   * 
   * @param options 
   */
  async getDuration(options: capVideoPlayerOptions): Promise<capVideoPlayerResult> {
    let playerId:string = options.playerId;
    if (playerId == null || playerId.length === 0) {
      playerId = "fullscreen";
    }
    if(this._players[playerId]) {
      let duration: number = this._players[playerId].videoEl.duration;
      return Promise.resolve({ method: "getDuration", result: true , value: duration});
    } else {
      return Promise.reject("VideoPlayer Pause: Given PlayerId does not exist)");
    }
  }
  /**
   * Set the volume of the current video from a given playerId
   * 
   * @param options 
   */
  async setVolume(options: capVideoPlayerOptions): Promise<capVideoPlayerResult> {
    let playerId:string = options.playerId;
    if (playerId == null || playerId.length === 0) {
      playerId = "fullscreen";
    }
    let volume: number = options.volume ? options.volume : 0.5;
    if(this._players[playerId]) {
      this._players[playerId].videoEl.volume = volume;
      return Promise.resolve({ method: "setVolume", result: true, value: volume });
    } else {
      return Promise.reject("VideoPlayer Pause: Given PlayerId does not exist)");
    }
  }
  /**
   * Get the volume of the current video from a given playerId
   * 
   * @param options 
   */
  async getVolume(options: capVideoPlayerOptions): Promise<capVideoPlayerResult> {
    let playerId:string = options.playerId;
    if (playerId == null || playerId.length === 0) {
      playerId = "fullscreen";
    }
    if(this._players[playerId]) {
      let volume: number = this._players[playerId].videoEl.volume;
      return Promise.resolve({ method: "getVolume", result: true , value: volume});
    } else {
      return Promise.reject("VideoPlayer Pause: Given PlayerId does not exist)");
    }
  }
  /**
   * Set the muted property of the current video from a given playerId
   * 
   * @param options 
   */
  async setMuted(options: capVideoPlayerOptions): Promise<capVideoPlayerResult> {
    let playerId:string = options.playerId;
    if (playerId == null || playerId.length === 0) {
      playerId = "fullscreen";
    }
    let muted: boolean = options.muted ? options.muted : false;
    if(this._players[playerId]) {
      this._players[playerId].videoEl.muted = muted;
      return Promise.resolve({ method: "setMuted", result: true, value: muted });
    } else {
      return Promise.reject("VideoPlayer Pause: Given PlayerId does not exist)");
    }
  }
  /**
   * Get the muted property of the current video from a given playerId
   * 
   * @param options 
   */
  async getMuted(options: capVideoPlayerOptions): Promise<capVideoPlayerResult> {
    let playerId:string = options.playerId;
    if (playerId == null || playerId.length === 0) {
      playerId = "fullscreen";
    }
    if(this._players[playerId]) {
      let muted: boolean = this._players[playerId].videoEl.muted;
      return Promise.resolve({ method: "getMuted", result: true , value: muted});
    } else {
      return Promise.reject("VideoPlayer Pause: Given PlayerId does not exist)");
    }
  }
  /**
   * Set the current time of the current video from a given playerId
   * 
   * @param options 
   */
  async setCurrentTime(options: capVideoPlayerOptions): Promise<capVideoPlayerResult> {
    let playerId:string = options.playerId;
    if (playerId == null || playerId.length === 0) {
      playerId = "fullscreen";
    }
    let seekTime: number = options.seektime ? options.seektime : 0;
    if(this._players[playerId]) {
      const duration: number = this._players[playerId].videoEl.duration;
      seekTime = seekTime <= duration && seekTime >= 0 ? seekTime : duration / 2;  
      this._players[playerId].videoEl.currentTime = seekTime;
      return Promise.resolve({ method: "setCurrentTime", result: true, value:seekTime });
    } else {
      return Promise.reject("VideoPlayer Pause: Given PlayerId does not exist)");
    }
  }
  /**
   * Get the current time of the current video from a given playerId
   * 
   * @param options 
   */
  async getCurrentTime(options: capVideoPlayerOptions): Promise<capVideoPlayerResult> {
    let playerId:string = options.playerId;
    if (playerId == null || playerId.length === 0) {
      playerId = "fullscreen";
    }
    if(this._players[playerId]) {
      const seekTime: number = this._players[playerId].videoEl.currentTime;
      return Promise.resolve({ method: "getCurrentTime", result: true , value: seekTime});
    } else {
      return Promise.reject("VideoPlayer Pause: Given PlayerId does not exist)");
    }
  }

  private checkSize(options:capVideoPlayerOptions): IPlayerSize {
    let playerSize: IPlayerSize = {
      width : options.width ? options.width : 320,
      height: options.height ? options.height : 180
    }
    let ratio: number = playerSize.height / playerSize.width ;
    if (playerSize.width > window.innerWidth) {
      playerSize.width = window.innerWidth;
      playerSize.height = Math.floor(playerSize.width * ratio);
    }
    if (playerSize.height > window.innerHeight) {
      playerSize.height = window.innerHeight;
      playerSize.width = Math.floor(playerSize.height / ratio);
    }
    return playerSize;
  }
  private async _initializeVideoPlayerEmbedded(url: string, playerId: string,playerSize:IPlayerSize) : Promise<boolean> {
    const videoURL:string = url ? encodeURI(url) : null;
    if(videoURL === null) return Promise.resolve(false);
    const videoContainer: HTMLDivElement = document.querySelector(`#${playerId}`);
    this._players[playerId] = new VideoPlayer("embedded",videoURL,playerId,videoContainer,2,playerSize.width,playerSize.height);
    return Promise.resolve(true);
  }
  private async _initializeVideoPlayerFullScreen(url: string) : Promise<boolean> {
    // encode the url
    const videoURL:string = url ? encodeURI(url) : null;
    if(videoURL === null) return Promise.resolve(false);
    // create the video player
    this._players["fullscreen"] = new VideoPlayer("fullscreen",videoURL,"fullscreen",document.body,99995);
    this._players["fullscreen"].videoEl.play();
    return Promise.resolve(true);
  }
}

const CapacitorVideoPlayer = new CapacitorVideoPlayerWeb();

export { CapacitorVideoPlayer };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(CapacitorVideoPlayer);
