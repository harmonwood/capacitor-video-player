import Hls from 'hls.js';

export class VideoPlayer {
  public videoEl: HTMLVideoElement | undefined;
  public pipMode = false;
  public pipWindow: Window | undefined;
  public isPlaying: boolean | undefined;

  private _url: string;
  private _playerId: string;
  private _container: any;
  private _mode: string;
  private _width: number;
  private _height: number;
  private _zIndex: number;
  private _initial: any;
  private _videoType: string | null = null;
  private _videoContainer: any = null;
  private _firstReadyToPlay = true;
  private _isEnded = false;
  private _videoRate = 1.0;
  private _videoExitOnEnd = true;
  private _videoLoopOnEnd = false;

  constructor(
    mode: string,
    url: string,
    playerId: string,
    rate: number,
    exitOnEnd: boolean,
    loopOnEnd: boolean,
    container: any,
    zIndex: number,
    width?: number,
    height?: number,
  ) {
    this._url = url;
    this._container = container;
    this._mode = mode;
    this._width = width ? width : 320;
    this._height = height ? height : 180;
    this._mode = mode;
    this._videoRate = rate;
    this._zIndex = zIndex ? zIndex : 1;
    this._playerId = playerId;
    this._videoExitOnEnd = exitOnEnd;
    this._videoLoopOnEnd = loopOnEnd;
  }

  public async initialize(): Promise<void> {
    // get the video type
    const retB: boolean = this._getVideoType();
    if (retB) {
      // style the container
      if (this._mode === 'fullscreen') {
        this._container.style.position = 'absolute';
        this._container.style.width = '100vw';
        this._container.style.height = '100vh';
      }
      if (this._mode === 'embedded') {
        this._container.style.position = 'relative';
        this._container.style.width = this._width.toString() + 'px';
        this._container.style.height = this._height.toString() + 'px';
      }
      this._container.style.left = '0';
      this._container.style.top = '0';
      this._container.style.display = 'flex';
      this._container.style.alignItems = 'center';
      this._container.style.justifyContent = 'center';
      this._container.style.backgroundColor = '#000000';
      this._container.style.zIndex = this._zIndex.toString();
      const width: number =
        this._mode === 'fullscreen'
          ? window.innerWidth /*this._container.offsetWidth*/
          : this._width;
      const height: number =
        this._mode === 'fullscreen'
          ? window.innerHeight /*this._container.offsetHeight*/
          : this._height;
      const xmlns = 'http://www.w3.org/2000/svg';

      const svg = document.createElementNS(xmlns, 'svg');
      svg.setAttributeNS(null, 'width', width.toString());
      svg.setAttributeNS(null, 'height', height.toString());
      const viewbox = '0 0 ' + width.toString() + ' ' + height.toString();
      svg.setAttributeNS(null, 'viewBox', viewbox);
      svg.style.zIndex = (this._zIndex + 1).toString();
      const rect = document.createElementNS(xmlns, 'rect');
      rect.setAttributeNS(null, 'x', '0');
      rect.setAttributeNS(null, 'y', '0');
      rect.setAttributeNS(null, 'width', width.toString());
      rect.setAttributeNS(null, 'height', height.toString());
      rect.setAttributeNS(null, 'fill', '#000000');
      svg.appendChild(rect);
      this._container.appendChild(svg);

      const heightVideo: number = (width * this._height) / this._width;
      this._videoContainer = document.createElement('div');
      this._videoContainer.style.position = 'absolute';
      this._videoContainer.style.left = '0';
      this._videoContainer.style.width = width.toString() + 'px';
      this._videoContainer.style.height = heightVideo.toString() + 'px';
      this._videoContainer.style.zIndex = (this._zIndex + 2).toString();
      this._container.appendChild(this._videoContainer);
      /*   Create Video Element */
      const isCreated = await this.createVideoElement(width, heightVideo);
      if (!isCreated) {
        this._createEvent(
          'Exit',
          this._playerId,
          'Video Error: failed to create the Video Element',
        );
      }
    } else {
      this._createEvent(
        'Exit',
        this._playerId,
        'Url Error: type not supported',
      );
    }
    return;
  }
  private async createVideoElement(
    width: number,
    height: number,
  ): Promise<boolean> {
    this.videoEl = document.createElement('video');
    this.videoEl.controls = true;
    this.videoEl.style.zIndex = (this._zIndex + 3).toString();
    this.videoEl.style.width = `${width.toString()}px`;
    this.videoEl.style.height = `${height.toString()}px`;
    this.videoEl.playbackRate = this._videoRate;
    this._videoContainer.appendChild(this.videoEl);
    // set the player
    const isSet: boolean = await this._setPlayer();
    if (isSet) {
      this.videoEl.onended = async () => {
        this._isEnded = true;
        this.isPlaying = false;
        if (this.videoEl) {
          this.videoEl.currentTime = 0;
        }
        if (this._videoExitOnEnd) {
          if (this._mode === 'fullscreen') {
            this._closeFullscreen();
          }
          this._createEvent('Ended', this._playerId);
        } else {
          if (this._videoLoopOnEnd && this.videoEl != null) {
            await this.videoEl.play();
          }
        }
      };
      this.videoEl.oncanplay = async () => {
        if (this._firstReadyToPlay) {
          this._createEvent('Ready', this._playerId);
          if (this.videoEl != null) {
            this.videoEl.muted = false;
            if (this._mode === 'fullscreen') await this.videoEl.play();
            this._firstReadyToPlay = false;
          }
        }
      };
      this.videoEl.onplay = () => {
        this.isPlaying = true;
        if (this._firstReadyToPlay) this._firstReadyToPlay = false;
        this._createEvent('Play', this._playerId);
      };
      this.videoEl.onplaying = () => {
        this._createEvent('Playing', this._playerId);
      };
      this.videoEl.onpause = () => {
        this.isPlaying = false;
        this._createEvent('Pause', this._playerId);
      };
      if (this._mode === 'fullscreen') {
        // create the video player exit button
        const exitEl: HTMLButtonElement = document.createElement('button');
        exitEl.textContent = 'X';
        exitEl.style.position = 'absolute';
        exitEl.style.left = '1%';
        exitEl.style.top = '5%';
        exitEl.style.width = '5vmin';
        exitEl.style.padding = '0.5%';
        exitEl.style.fontSize = '1.2rem';
        exitEl.style.background = 'rgba(51,51,51,.4)';
        exitEl.style.color = '#fff';
        exitEl.style.visibility = 'hidden';
        exitEl.style.zIndex = (this._zIndex + 4).toString();
        exitEl.style.border = '1px solid rgba(51,51,51,.4)';
        exitEl.style.borderRadius = '20px';
        this._videoContainer.onclick = async () => {
          this._initial = await this._doHide(exitEl, 3000);
        };
        this._videoContainer.ontouchstart = async () => {
          this._initial = await this._doHide(exitEl, 3000);
        };

        this._videoContainer.onmousemove = async () => {
          this._initial = await this._doHide(exitEl, 3000);
        };

        exitEl.onclick = () => {
          this._createEvent('Exit', this._playerId);
        };
        exitEl.ontouchstart = () => {
          this._createEvent('Exit', this._playerId);
        };

        this._videoContainer.appendChild(exitEl);
        this._initial = await this._doHide(exitEl, 3000);

        this._goFullscreen();
      }
    }
    return isSet;
  }

  private async _goFullscreen(): Promise<void> {
    if (this._container.mozRequestFullScreen) {
      /* Firefox */
      this._container.mozRequestFullScreen();
    } else if (this._container.webkitRequestFullscreen) {
      /* Chrome, Safari & Opera */
      this._container.webkitRequestFullscreen();
    } else if (this._container.msRequestFullscreen) {
      /* IE/Edge */
      this._container.msRequestFullscreen();
    } else if (this._container.requestFullscreen) {
      this._container.requestFullscreen();
    }
    return;
  }

  private async _setPlayer(): Promise<boolean> {
    return new Promise(resolve => {
      if (this.videoEl != null) {
        if (Hls.isSupported() && this._videoType === 'application/x-mpegURL') {
          const hls = new Hls();
          hls.loadSource(this._url);
          hls.attachMedia(this.videoEl);
          hls.once(Hls.Events.FRAG_PARSED, () => {
            if (this.videoEl != null) {
              this.videoEl.muted = true;
              this.videoEl.crossOrigin = 'anonymous';
              resolve(true);
            } else {
              resolve(false);
            }
          });
        } else if (this._videoType === 'video/mp4') {
          // CMAF (fMP4) && MP4
          this.videoEl.src = this._url;
          if (
            this._url.substring(0, 5) != 'https' &&
            this._url.substring(0, 4) === 'http'
          )
            this.videoEl.crossOrigin = 'anonymous';
          if (
            this._url.substring(0, 5) === 'https' ||
            this._url.substring(0, 4) === 'http'
          )
            this.videoEl.muted = true;
          resolve(true);
        } else {
          // Not Supported
          resolve(false);
        }
        this.videoEl.addEventListener('enterpictureinpicture', (event: any) => {
          this.pipWindow = event.pictureInPictureWindow;
          this.pipMode = true;
          this._closeFullscreen();
        });

        this.videoEl.addEventListener('leavepictureinpicture', () => {
          this.pipMode = false;
          if (!this._isEnded) {
            this._goFullscreen();
            if (this.videoEl != null) this.videoEl.play();
          }
        });
      } else {
        resolve(false);
      }
    });
  }

  private _getVideoType(): boolean {
    let ret = false;
    let vType = '';
    const sUrl: string = this._url ? this._url : '';
    if (sUrl != null && sUrl.length > 0) {
      try {
        const val = sUrl.substring(sUrl.lastIndexOf('/')).match(/(.*)\.(.*)/);
        if (val == null) {
          vType = '';
        } else {
          const a = sUrl.match(/(.*)\.(.*)/);
          vType = a != null ? a[2].split('?')[0] : '';
        }
        switch (vType) {
          case 'mp4':
          case '':
          case 'webm':
          case 'cmaf':
          case 'cmfv':
          case 'cmfa': {
            this._videoType = 'video/mp4';
            break;
          }
          case 'm3u8': {
            this._videoType = 'application/x-mpegURL';
            break;
          }
          /*
                  case "mpd" : {
                  this._videoType = "application/dash+xml";
                  break;
                  }
          */
          /*
                  case "youtube" : {
                  this._videoType = "video/youtube";
                  break;
                  }
          */
          default: {
            this._videoType = null;
            break;
          }
        }
        ret = true;
      } catch {
        ret = false;
      }
    }
    return ret;
  }

  private async _doHide(exitEl: HTMLButtonElement, duration: number) {
    clearTimeout(this._initial);
    exitEl.style.visibility = 'visible';
    const initial = setTimeout(() => {
      exitEl.style.visibility = 'hidden';
    }, duration);
    return initial;
  }
  private _createEvent(ev: string, playerId: string, msg?: string) {
    const message = msg ? msg : null;
    let event: CustomEvent;
    if (message != null) {
      event = new CustomEvent(`videoPlayer${ev}`, {
        detail: { fromPlayerId: playerId, message: message },
      });
    } else {
      const currentTime: number = this.videoEl ? this.videoEl.currentTime : 0;
      event = new CustomEvent(`videoPlayer${ev}`, {
        detail: { fromPlayerId: playerId, currentTime: currentTime },
      });
    }
    document.dispatchEvent(event);
  }

  private _closeFullscreen() {
    const mydoc: any = document;
    const isInFullScreen: boolean =
      (mydoc.fullscreenElement && mydoc.fullscreenElement !== null) ||
      (mydoc.webkitFullscreenElement &&
        mydoc.webkitFullscreenElement !== null) ||
      (mydoc.mozFullScreenElement && mydoc.mozFullScreenElement !== null) ||
      (mydoc.msFullscreenElement && mydoc.msFullscreenElement !== null);
    if (isInFullScreen) {
      if (mydoc.mozCancelFullScreen) {
        mydoc.mozCancelFullScreen();
      } else if (mydoc.webkitExitFullscreen) {
        mydoc.webkitExitFullscreen();
      } else if (mydoc.msExitFullscreen) {
        mydoc.msExitFullscreen();
      } else if (mydoc.exitFullscreen) {
        mydoc.exitFullscreen();
      }
    }
  }
}