export interface CapacitorVideoPlayerPlugin {
  /**
   * Echo
   *
   */
  echo(options: capEchoOptions): Promise<capVideoPlayerResult>;
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
   * Set the rate of the current video from a given playerId
   *
   */
  setRate(options: capVideoRateOptions): Promise<capVideoPlayerResult>;
  /**
   * Get the rate of the current video from a given playerId
   *
   */
  getRate(options: capVideoPlayerIdOptions): Promise<capVideoPlayerResult>;
  /**
   * Stop all players playing
   *
   */
  stopAllPlayers(): Promise<capVideoPlayerResult>;
  /**
   * Show controller
   *
   */
  showController(): Promise<capVideoPlayerResult>;
  /**
   * isControllerIsFullyVisible
   *
   */
  isControllerIsFullyVisible(): Promise<capVideoPlayerResult>;
  /**
   * Exit player
   *
   */
  exitPlayer(): Promise<capVideoPlayerResult>;
}
export interface capEchoOptions {
  /**
   *  String to be echoed
   */

  value?: string;
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
   * The url of subtitle associated with the video
   */
  subtitle?: string;
  /**
   * The language of subtitle
   * see https://github.com/libyal/libfwnt/wiki/Language-Code-identifiers
   */
  language?: string;
  /**
   * SubTitle Options
   */
  subtitleOptions?: SubTitleOptions;
  /**
   * Id of DIV Element parent of the player
   */
  playerId?: string;
  /**
   * Initial playing rate
   */
  rate?: number;
  /**
   * Exit on VideoEnd (iOS, Android)
   * default: true
   */
  exitOnEnd?: boolean;
  /**
   * Loop on VideoEnd when exitOnEnd false (iOS, Android)
   * default: false
   */
  loopOnEnd?: boolean;
  /**
   * Picture in Picture Enable (iOS, Android)
   * default: true
   */
  pipEnabled?: boolean;
  /**
   * Background Mode Enable (iOS, Android)
   * default: true
   */
  bkmodeEnabled?: boolean;
  /**
   * Show Controls Enable (iOS, Android)
   * default: true
   */
  showControls?: boolean;
  /**
   * Display Mode ["all", "portrait", "landscape"] (iOS, Android)
   * default: "all"
   */
  displayMode?: string;
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
  /**
   * Headers for the request (iOS, Android)
   * by Manuel García Marín (https://github.com/PhantomPainX)
   */
  headers?: {
    [key: string]: string;
  };
  /**
   * Title shown in the player (Android)
   * by Manuel García Marín (https://github.com/PhantomPainX)
   */
  title?: string;
  /**
   * Subtitle shown below the title in the player (Android)
   * by Manuel García Marín (https://github.com/PhantomPainX)
   */
  smallTitle?: string;
  /**
   * ExoPlayer Progress Bar and Spinner color (Android)
   * by Manuel García Marín (https://github.com/PhantomPainX)
   * Must be a valid hex color code
   * default: #FFFFFF
   */
  accentColor?: string;
  /**
   * Chromecast enable/disable (Android)
   * by Manuel García Marín (https://github.com/PhantomPainX)
   * default: true
   */
  chromecast?: boolean;
  /**
   * Artwork url to be shown in Chromecast player
   * by Manuel García Marín (https://github.com/PhantomPainX)
   * default: ""
   */
  artwork?: string;

  /**
   * DRM options
   */
  drm?: capVideoPlayerDRMOptions;
}

export interface capVideoPlayerDRMOptions {
  /**
   * Object with widevine (android) and fairplay (ios) options
   */
  widevine?: {
    licenseUri?: string;
    headers?: {
      [key: string]: string;
    }
  }
  fairplay: {
    licenseUri: string;
    certificateUri: string;
    headers?: {
      [key: string]: string;
    }
  }
}
export interface capVideoPlayerIdOptions {
  /**
   * Id of DIV Element parent of the player
   */
  playerId?: string;
}
export interface capVideoRateOptions {
  /**
   * Id of DIV Element parent of the player
   */
  playerId?: string;
  /**
   * Rate value
   */
  rate?: number;
}
export interface capVideoVolumeOptions {
  /**
   * Id of DIV Element parent of the player
   */
  playerId?: string;
  /**
   * Volume value between [0 - 1]
   */
  volume?: number;
}
export interface capVideoTimeOptions {
  /**
   * Id of DIV Element parent of the player
   */
  playerId?: string;
  /**
   * Video time value you want to seek to
   */
  seektime?: number;
}
export interface capVideoMutedOptions {
  /**
   * Id of DIV Element parent of the player
   */
  playerId?: string;
  /**
   * Muted value true or false
   */
  muted?: boolean;
}
export interface capVideoListener {
  /**
   * Id of DIV Element parent of the player
   */
  playerId?: string;
  /**
   * Video current time when listener trigerred
   */
  currentTime?: number;
}
export interface capExitListener {
  /**
   * Dismiss value true or false
   */
  dismiss?: boolean;
  /**
   * Video current time when listener trigerred
   */
  currentTime?: number;
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
export interface SubTitleOptions {
  /**
   * Foreground Color in RGBA (default rgba(255,255,255,1)
   */
  foregroundColor?: string;
  /**
   * Background Color in RGBA (default rgba(0,0,0,1)
   */
  backgroundColor?: string;
  /**
   * Font Size in pixels (default 16)
   */
  fontSize?: number;
}
