declare module "@capacitor/core" {
  interface PluginRegistry {
    CapacitorVideoPlayer: CapacitorVideoPlayerPlugin;
  }
}

export interface CapacitorVideoPlayerPlugin {
  /**
   * Initialize a video 
   * @param {capVideoPlayerOptions} options { url: string }
   * @returns {Promise<VideoPlayerResult>} {result: boolean}
   */
  initPlayer(options: capVideoPlayerOptions): Promise<capVideoPlayerResult>; 
}

export interface capVideoPlayerOptions {
  /**
   * The url of the video to play
   */
  mode: string
  url: string;
  playerId: string;
  width: number;
  height: number;
  volume: number;
  seektime: number;
  muted: boolean;
}
export interface capVideoPlayerResult {
  /**
   * result set to true when successful else false
   */
  result?: boolean;
  method?: string;
  value?: any;
}

