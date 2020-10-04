declare module '@capacitor/core' {
  interface PluginRegistry {
    CapacitorVideoPlayer: CapacitorVideoPlayerPlugin;
  }
}

export interface CapacitorVideoPlayerPlugin {
  /**
   * Echo
   *
   */
  echo(options: { value: string }): Promise<capVideoPlayerResult>;
  /**
   * Initialize a video player
   *
   */
  initPlayer(options: capVideoPlayerOptions): Promise<capVideoPlayerResult>;
  /**
   * Return if a given playerId is playing
   *
   */
  isPlaying(options: capVideoPlayerIdOptions): Promise<capVideoPlayerResult>;
  /**
   * Play the current video from a given playerId
   *
   */
  play(options: capVideoPlayerIdOptions): Promise<capVideoPlayerResult>;
  /**
   * Pause the current video from a given playerId
   *
   */
  pause(options: capVideoPlayerIdOptions): Promise<capVideoPlayerResult>;
  /**
   * Get the duration of the current video from a given playerId
   *
   */
  getDuration(options: capVideoPlayerIdOptions): Promise<capVideoPlayerResult>;
  /**
   * Get the current time of the current video from a given playerId
   *
   */
  getCurrentTime(
    options: capVideoPlayerIdOptions,
  ): Promise<capVideoPlayerResult>;
  /**
   * Set the current time to seek the current video to from a given playerId
   *
   */
  setCurrentTime(options: capVideoTimeOptions): Promise<capVideoPlayerResult>;
  /**
   * Get the volume of the current video from a given playerId
   *
   */
  getVolume(options: capVideoPlayerIdOptions): Promise<capVideoPlayerResult>;
  /**
   * Set the volume of the current video to from a given playerId
   *
   */
  setVolume(options: capVideoVolumeOptions): Promise<capVideoPlayerResult>;
  /**
   * Get the muted of the current video from a given playerId
   *
   */
  getMuted(options: capVideoPlayerIdOptions): Promise<capVideoPlayerResult>;
  /**
   * Set the muted of the current video to from a given playerId
   *
   */
  setMuted(options: capVideoMutedOptions): Promise<capVideoPlayerResult>;
  /**
   * Stop all players playing
   *
   */
  stopAllPlayers(): Promise<capVideoPlayerResult>;
}
export interface capVideoPlayerOptions {
  /**
   * Player mode
   *  - "fullscreen"
   *  - "embedded" (Web only)
   */
  mode?: string;
  /**
   * The url of the video to play
   */
  url?: string;
  /**
   * Id of DIV Element parent of the player
   */
  playerId?: string;
  /**
   * Component Tag or DOM Element Tag (React app)
   */
  componentTag?: string;
  /**
   * Player Width (mode "embedded" only)
   */
  width?: number;
  /**
   * Player height (mode "embedded" only)
   */
  height?: number;
}
export interface capVideoPlayerIdOptions {
  playerId?: string;
}
export interface capVideoVolumeOptions {
  playerId?: string;
  volume?: number;
}
export interface capVideoTimeOptions {
  playerId?: string;
  seektime?: number;
}
export interface capVideoMutedOptions {
  playerId?: string;
  muted?: boolean;
}
export interface capVideoListener {
  playerId?: string;
  currentTime?: number;
}
export interface capExitListener {
  dismiss?: boolean;
}
export interface capVideoPlayerResult {
  /**
   * result set to true when successful else false
   */
  result?: boolean;
  /**
   * method name
   */
  method?: string;
  /**
   * value returned
   */
  value?: any;
  /**
   * message string
   */
  message?: string;
}
