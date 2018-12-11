declare global {
  interface PluginRegistry {
    CapacitorVideoPlayer?: CapacitorVideoPlayerPlugin;
  }
}

export interface CapacitorVideoPlayerPlugin {
  /**
   * Playing a video 
   * @param {capVideoPlayerOptions} options { url: string }
   * @returns {Promise<VideoPlayerResult>} {result: boolean}
   */
  play(options: capVideoPlayerOptions): Promise<capVideoPlayerResult>; 
}

export interface capVideoPlayerOptions {
  /**
   * The url of the video to play
   */
  url: string;
}
export interface capVideoPlayerResult {
  /**
   * result set to true when successful else false
   */
  result?: boolean;
}

