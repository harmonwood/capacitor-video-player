declare module "@capacitor/core" {
  interface PluginRegistry {
    CapacitorVideoPlayer: CapacitorVideoPlayerPlugin;
  }
}

export interface CapacitorVideoPlayerPlugin {
  echo(options: { value: string }): Promise<{value: string}>;

  /**
   * Initialize a video 
   * @param {capVideoPlayerOptions} options { url: string }
   * @returns {Promise<VideoPlayerResult>} {result: boolean}
   */
  initPlayer(options: capVideoPlayerOptions): Promise<capVideoPlayerResult>; 
  /**
   * Play the current video from a given playerId
   * 
   * @param options 
   * @returns {Promise<VideoPlayerResult>} {result: boolean, method: string, value:string}
   */
  play(options: capVideoPlayerOptions): Promise<capVideoPlayerResult>;
  /**
   * Pause the current video from a given playerId
   * 
   * @param options 
   * @returns {Promise<VideoPlayerResult>} {result: boolean, method: string, value:string}
   */
  pause(options: capVideoPlayerOptions): Promise<capVideoPlayerResult>;
  /**
   * Get the duration of the current video from a given playerId
   * 
   * @param options 
   * @returns {Promise<VideoPlayerResult>} {result: boolean, method: string, value:string}
   */
  getDuration(options: capVideoPlayerOptions): Promise<capVideoPlayerResult> 
  /**
   * Get the current time of the current video from a given playerId
   * 
   * @param options 
   * @returns {Promise<VideoPlayerResult>} {result: boolean, method: string, value:string}
   */
  getCurrentTime(options: capVideoPlayerOptions): Promise<capVideoPlayerResult> 
  /**
   * Set the current time to seek the current video to from a given playerId
   * 
   * @param options 
   * @returns {Promise<VideoPlayerResult>} {result: boolean, method: string, value:string}
   */
  setCurrentTime(options: capVideoPlayerOptions): Promise<capVideoPlayerResult>
  /**
   * Get the volume of the current video from a given playerId
   * 
   * @param options 
   * @returns {Promise<VideoPlayerResult>} {result: boolean, method: string, value:string}
   */
  getVolume(options: capVideoPlayerOptions): Promise<capVideoPlayerResult> 
  /**
   * Set the volume of the current video to from a given playerId
   * 
   * @param options 
   * @returns {Promise<VideoPlayerResult>} {result: boolean, method: string, value:string}
   */
  setVolume(options: capVideoPlayerOptions): Promise<capVideoPlayerResult>
  /**
   * Get the muted of the current video from a given playerId
   * 
   * @param options 
   * @returns {Promise<VideoPlayerResult>} {result: boolean, method: string, value:string}
   */
  getMuted(options: capVideoPlayerOptions): Promise<capVideoPlayerResult> 
  /**
   * Set the muted of the current video to from a given playerId
   * 
   * @param options 
   * @returns {Promise<VideoPlayerResult>} {result: boolean, method: string, value:string}
   */
  setMuted(options: capVideoPlayerOptions): Promise<capVideoPlayerResult>
  /**
   * Stop all players playing
   * 
   * @returns {Promise<VideoPlayerResult>} {result: boolean, method: string, value:string}
   */
  stopAllPlayers(): Promise<capVideoPlayerResult>
}
export interface capVideoPlayerOptions {
  /**
   * The url of the video to play
   */
  mode: string
  url: string;
  playerId: string;
  componentTag: string;
  width?: number;
  height?: number;
  volume?: number;
  seektime?: number;
  muted?: boolean;
  videoList?: Array<any>;
  pageTitle?: string;
}
export interface capVideoPlayerResult {
  /**
   * result set to true when successful else false
   */
  result?: boolean;
  method?: string;
  value?: any;
}
