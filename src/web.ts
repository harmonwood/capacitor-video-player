import { WebPlugin } from '@capacitor/core';
import {
  CapacitorVideoPlayerPlugin,
  capVideoPlayerOptions,
  capVideoPlayerResult,
} from './definitions';
import { VideoPlayer } from './web-utils/videoplayer';

export interface IPlayerSize {
  height?: number;
  width?: number;
}

export class CapacitorVideoPlayerWeb extends WebPlugin
  implements CapacitorVideoPlayerPlugin {
  private _players: any = [];

  constructor() {
    super({
      name: 'CapacitorVideoPlayer',
      platforms: ['web'],
    });
  }
  async echo(options: { value: string }): Promise<capVideoPlayerResult> {
    console.log('ECHO', options);
    return Promise.resolve({ result: true, method: 'echo', value: options });
  }

  /**
   *  Player initialization
   *
   * @param options
   */
  async initPlayer(
    options: capVideoPlayerOptions,
  ): Promise<capVideoPlayerResult> {
    let mode: string = options.mode;
    if (mode == null || mode.length === 0) {
      return Promise.resolve({
        result: false,
        method: 'initPlayer',
        message: 'Must provide a Mode (fullscreen/embedded)',
      });
    }
    if (mode === 'fullscreen' || mode === 'embedded') {
      let url: string = options.url;
      if (url == null || url.length === 0) {
        return Promise.resolve({
          result: false,
          method: 'initPlayer',
          message: 'Must provide a Video Url',
        });
      }
      let playerId: string = options.playerId;
      if (playerId == null || playerId.length === 0) {
        return Promise.resolve({
          result: false,
          method: 'initPlayer',
          message: 'Must provide a Player Id',
        });
      }
      let componentTag: string = options.componentTag;
      if (componentTag == null || componentTag.length === 0) {
        return Promise.resolve({
          result: false,
          method: 'initPlayer',
          message: 'Must provide a Component Tag',
        });
      }
      let playerSize: IPlayerSize = null;
      if (mode === 'embedded') {
        playerSize = this.checkSize(options);
      }
      const result = await this._initializeVideoPlayer(
        url,
        playerId,
        mode,
        componentTag,
        playerSize,
      );
      return Promise.resolve({ result: result });
    } else {
      return Promise.resolve({
        result: false,
        method: 'initPlayer',
        message: 'Must provide a Mode either fullscreen or embedded)',
      });
    }
  }
  /**
   * Return if a given playerId is playing
   *
   * @param options
   */
  async isPlaying(
    options: capVideoPlayerOptions,
  ): Promise<capVideoPlayerResult> {
    let playerId: string = options.playerId;
    if (playerId == null || playerId.length === 0) {
      playerId = 'fullscreen';
    }
    if (this._players[playerId]) {
      let playing: boolean = false;
      if (!this._players[playerId].videoEl.paused) playing = true;
      return Promise.resolve({
        method: 'isPlaying',
        result: true,
        value: playing,
      });
    } else {
      return Promise.resolve({
        method: 'isPlaying',
        result: false,
        message: 'Given PlayerId does not exist)',
      });
    }
  }

  /**
   * Play the current video from a given playerId
   *
   * @param options
   */
  async play(options: capVideoPlayerOptions): Promise<capVideoPlayerResult> {
    let playerId: string = options.playerId;
    if (playerId == null || playerId.length === 0) {
      playerId = 'fullscreen';
    }
    if (this._players[playerId]) {
      await this._players[playerId].videoEl.play();
      return Promise.resolve({ method: 'play', result: true, value: true });
    } else {
      return Promise.resolve({
        method: 'play',
        result: false,
        message: 'Given PlayerId does not exist)',
      });
    }
  }
  /**
   * Pause the current video from a given playerId
   *
   * @param options
   */
  async pause(options: capVideoPlayerOptions): Promise<capVideoPlayerResult> {
    let playerId: string = options.playerId;
    if (playerId == null || playerId.length === 0) {
      playerId = 'fullscreen';
    }
    if (this._players[playerId]) {
      if (!this._players[playerId].videoEl.pause)
        await this._players[playerId].videoEl.pause();
      return Promise.resolve({ method: 'pause', result: true });
    } else {
      return Promise.resolve({
        method: 'pause',
        result: false,
        message: 'Given PlayerId does not exist)',
      });
    }
  }
  /**
   * Get the duration of the current video from a given playerId
   *
   * @param options
   */
  async getDuration(
    options: capVideoPlayerOptions,
  ): Promise<capVideoPlayerResult> {
    let playerId: string = options.playerId;
    if (playerId == null || playerId.length === 0) {
      playerId = 'fullscreen';
    }
    if (this._players[playerId]) {
      let duration: number = this._players[playerId].videoEl.duration;
      return Promise.resolve({
        method: 'getDuration',
        result: true,
        value: duration,
      });
    } else {
      return Promise.resolve({
        method: 'getDuration',
        result: false,
        message: 'Given PlayerId does not exist)',
      });
    }
  }
  /**
   * Set the volume of the current video from a given playerId
   *
   * @param options
   */
  async setVolume(
    options: capVideoPlayerOptions,
  ): Promise<capVideoPlayerResult> {
    let playerId: string = options.playerId;
    if (playerId == null || playerId.length === 0) {
      playerId = 'fullscreen';
    }
    let volume: number = options.volume ? options.volume : 0.5;
    if (this._players[playerId]) {
      this._players[playerId].videoEl.volume = volume;
      return Promise.resolve({
        method: 'setVolume',
        result: true,
        value: volume,
      });
    } else {
      return Promise.resolve({
        method: 'setVolume',
        result: false,
        message: 'Given PlayerId does not exist)',
      });
    }
  }
  /**
   * Get the volume of the current video from a given playerId
   *
   * @param options
   */
  async getVolume(
    options: capVideoPlayerOptions,
  ): Promise<capVideoPlayerResult> {
    let playerId: string = options.playerId;
    if (playerId == null || playerId.length === 0) {
      playerId = 'fullscreen';
    }
    if (this._players[playerId]) {
      let volume: number = this._players[playerId].videoEl.volume;
      return Promise.resolve({
        method: 'getVolume',
        result: true,
        value: volume,
      });
    } else {
      return Promise.resolve({
        method: 'getVolume',
        result: false,
        message: 'Given PlayerId does not exist)',
      });
    }
  }
  /**
   * Set the muted property of the current video from a given playerId
   *
   * @param options
   */
  async setMuted(
    options: capVideoPlayerOptions,
  ): Promise<capVideoPlayerResult> {
    let playerId: string = options.playerId;
    if (playerId == null || playerId.length === 0) {
      playerId = 'fullscreen';
    }
    let muted: boolean = options.muted ? options.muted : false;
    if (this._players[playerId]) {
      this._players[playerId].videoEl.muted = muted;
      console.log(
        'in setMuted videoEL ',
        this._players[playerId].videoEl.outerHTML,
      );
      return Promise.resolve({
        method: 'setMuted',
        result: true,
        value: muted,
      });
    } else {
      return Promise.resolve({
        method: 'setMuted',
        result: false,
        message: 'Given PlayerId does not exist)',
      });
    }
  }
  /**
   * Get the muted property of the current video from a given playerId
   *
   * @param options
   */
  async getMuted(
    options: capVideoPlayerOptions,
  ): Promise<capVideoPlayerResult> {
    let playerId: string = options.playerId;
    if (playerId == null || playerId.length === 0) {
      playerId = 'fullscreen';
    }
    if (this._players[playerId]) {
      console.log(
        'in getMuted videoEL ',
        this._players[playerId].videoEl.outerHTML,
      );
      let muted: boolean = this._players[playerId].videoEl.muted;
      return Promise.resolve({
        method: 'getMuted',
        result: true,
        value: muted,
      });
    } else {
      return Promise.resolve({
        method: 'getMuted',
        result: false,
        message: 'Given PlayerId does not exist)',
      });
    }
  }
  /**
   * Set the current time of the current video from a given playerId
   *
   * @param options
   */
  async setCurrentTime(
    options: capVideoPlayerOptions,
  ): Promise<capVideoPlayerResult> {
    let playerId: string = options.playerId;
    if (playerId == null || playerId.length === 0) {
      playerId = 'fullscreen';
    }
    let seekTime: number = options.seektime ? options.seektime : 0;
    if (this._players[playerId]) {
      const duration: number = this._players[playerId].videoEl.duration;
      seekTime =
        seekTime <= duration && seekTime >= 0 ? seekTime : duration / 2;
      this._players[playerId].videoEl.currentTime = seekTime;
      return Promise.resolve({
        method: 'setCurrentTime',
        result: true,
        value: seekTime,
      });
    } else {
      return Promise.resolve({
        method: 'setCurrentTime',
        result: false,
        message: 'Given PlayerId does not exist)',
      });
    }
  }
  /**
   * Get the current time of the current video from a given playerId
   *
   * @param options
   */
  async getCurrentTime(
    options: capVideoPlayerOptions,
  ): Promise<capVideoPlayerResult> {
    let playerId: string = options.playerId;
    if (playerId == null || playerId.length === 0) {
      playerId = 'fullscreen';
    }
    if (this._players[playerId]) {
      const seekTime: number = this._players[playerId].videoEl.currentTime;
      return Promise.resolve({
        method: 'getCurrentTime',
        result: true,
        value: seekTime,
      });
    } else {
      return Promise.resolve({
        method: 'getCurrentTime',
        result: false,
        message: 'Given PlayerId does not exist)',
      });
    }
  }
  /**
   * Get the current time of the current video from a given playerId
   *
   */
  async stopAllPlayers(): Promise<capVideoPlayerResult> {
    for (let i in this._players) {
      if (!this._players[i].videoEl.paused) this._players[i].videoEl.pause();
    }
    return Promise.resolve({
      method: 'stopAllPlayers',
      result: true,
      value: true,
    });
  }
  private checkSize(options: capVideoPlayerOptions): IPlayerSize {
    let playerSize: IPlayerSize = {
      width: options.width ? options.width : 320,
      height: options.height ? options.height : 180,
    };
    let ratio: number = playerSize.height / playerSize.width;
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
  private async _initializeVideoPlayer(
    url: string,
    playerId: string,
    mode: string,
    componentTag: string,
    playerSize: IPlayerSize,
  ): Promise<any> {
    const videoURL: string = url ? encodeURI(url) : null;
    if (videoURL === null) return Promise.resolve(false);
    const videoContainer: HTMLDivElement = await this._getContainerElement(
      playerId,
      componentTag,
    );
    if (videoContainer === null) return Promise.resolve(false);
    if (mode === 'embedded' && playerSize == null)
      return Promise.resolve(false);
    // add listeners
    videoContainer.addEventListener('videoPlayerPlay', (ev: CustomEvent) => {
      this.handlePlayerPlay(ev.detail);
    });
    videoContainer.addEventListener('videoPlayerPause', (ev: CustomEvent) => {
      this.handlePlayerPause(ev.detail);
    });
    videoContainer.addEventListener('videoPlayerEnded', (ev: CustomEvent) => {
      if (mode === 'fullscreen') {
        videoContainer.remove();
      }
      this.handlePlayerEnded(ev.detail);
    });
    videoContainer.addEventListener('videoPlayerReady', (ev: CustomEvent) => {
      this.handlePlayerReady(ev.detail);
    });
    videoContainer.addEventListener('videoPlayerExit', (ev: CustomEvent) => {
      if (mode === 'fullscreen') {
        videoContainer.remove();
      }
      this.handlePlayerExit(ev.detail);
    });

    if (mode === 'embedded') {
      this._players[playerId] = new VideoPlayer(
        'embedded',
        videoURL,
        playerId,
        videoContainer,
        2,
        playerSize.width,
        playerSize.height,
      );
    } else if (mode === 'fullscreen') {
      this._players['fullscreen'] = new VideoPlayer(
        'fullscreen',
        videoURL,
        'fullscreen',
        videoContainer,
        99995,
      );
    } else {
      return Promise.resolve({
        method: 'initPlayer',
        result: false,
        message: 'mode not supported',
      });
    }
    return Promise.resolve({ method: 'initPlayer', result: true, value: true });
  }
  private async _getContainerElement(
    playerId: string,
    componentTag: string,
  ): Promise<HTMLDivElement> {
    let cmpTagEl: HTMLElement = null;
    cmpTagEl = document.querySelector(`${componentTag}`);
    if (cmpTagEl === null) return Promise.resolve(null);
    let container: HTMLDivElement = null;
    try {
      container = cmpTagEl.shadowRoot.querySelector(`#${playerId}`);
    } catch {
      container = cmpTagEl.querySelector(`#${playerId}`);
    }
    const videoContainer: HTMLDivElement = document.createElement('div');
    container.appendChild(videoContainer);
    return Promise.resolve(videoContainer);
  }
  private handlePlayerPlay(data: any) {
    this.notifyListeners('jeepCapVideoPlayerPlay', data);
  }
  private handlePlayerPause(data: any) {
    this.notifyListeners('jeepCapVideoPlayerPause', data);
  }
  private handlePlayerEnded(data: any) {
    this.notifyListeners('jeepCapVideoPlayerEnded', data);
  }
  private handlePlayerExit(data: any) {
    this.notifyListeners('jeepCapVideoPlayerExit', data);
  }
  private handlePlayerReady(data: any) {
    this.notifyListeners('jeepCapVideoPlayerReady', data);
  }
}

const CapacitorVideoPlayer = new CapacitorVideoPlayerWeb();

export { CapacitorVideoPlayer };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(CapacitorVideoPlayer);
