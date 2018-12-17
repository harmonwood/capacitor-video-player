import { WebPlugin } from '@capacitor/core';
import { CapacitorVideoPlayerPlugin, capVideoPlayerOptions, capVideoPlayerResult  } from './definitions';

export class CapacitorVideoPlayerWeb extends WebPlugin implements CapacitorVideoPlayerPlugin {

  private _videoEl: any;
  private _videoContainer: HTMLDivElement;
  private _container: HTMLDivElement;
  private _url: string;

  constructor() {
    super({
      name: 'CapacitorVideoPlayer',
      platforms: ['web']
    });
  }

  async play(options: capVideoPlayerOptions): Promise<capVideoPlayerResult> {
    let url:string = options.url;
    if (url == null) {
      return Promise.reject("Must provide a Video Url");
    }
    const result = await this._initializeVideoPlayer(url)
    return Promise.resolve({ result: result });
  }

  private async _initializeVideoPlayer(url: string) : Promise<boolean> {
    // encode the url
    this._url = url ? encodeURI(url) : null;
    if(this._url === null) return Promise.resolve(false);
    // create a container
    this._container = document.createElement('div');
    this._container.style.position = 'absolute'
    this._container.style.width = '100vw';
    this._container.style.height = '100vh';
    this._container.style.left = '0';
    this._container.style.top = '0';
    this._container.style.display = 'flex';
    this._container.style.alignItems = 'center';
    this._container.style.justifyContent = 'center';
    this._container.style.backgroundColor = '#000000';  
    this._container.style.zIndex = '99997';
    document.body.appendChild(this._container);

    // create a video container
    const width: number = this._container.offsetWidth;
    const height: number = this._container.offsetHeight;
    const xmlns = "http://www.w3.org/2000/svg";

    const svg = document.createElementNS(xmlns,'svg');
    svg.setAttributeNS(null,'width',width.toString());
    svg.setAttributeNS(null,'height',height.toString());
    const viewbox = '0 0 '+width.toString()+' '+height.toString();
    svg.setAttributeNS(null,'viewBox',viewbox);
    svg.style.zIndex = '99998';
    const rect = document.createElementNS(xmlns,'rect');
    rect.setAttributeNS (null, "x", "0");
    rect.setAttributeNS (null, "y", "0");
    rect.setAttributeNS (null, "width", width.toString());
    rect.setAttributeNS (null, "height", height.toString());
    rect.setAttributeNS (null, "fill", "#000000");
    svg.appendChild(rect);
    this._container.appendChild(svg);

    const heightVideo: number = width * 9 /16;
    this._videoContainer = document.createElement('div');
    this._videoContainer.style.position = 'absolute';
    this._videoContainer.style.left = "0";
    this._videoContainer.style.width = width.toString()+'px';
    this._videoContainer.style.height = heightVideo.toString()+'px';
    this._videoContainer.style.zIndex = '99999';
    this._container.appendChild(this._videoContainer);
    // create the video player
    this._videoEl = document.createElement('video');
    this._videoEl.controls = true;
    this._videoEl.src = this._url;
    this._videoEl.style.width = "100%";
    /*
    this._videoEl.onplay = () => {
      if(!this._videoEl.webkitDisplayingFullscreen) this._videoEl.webkitEnterFullscreen();           
      this._videoEl.webkitEnterFullscreen();           
    }; 
    */
    this._videoEl.onended = () => {
      this._container.remove();
    };

    await this._videoContainer.appendChild(this._videoEl);
    if (this._videoEl.requestFullscreen) {
      this._videoEl.requestFullscreen();           
    } else if (this._videoEl.mozRequestFullScreen) { /* Firefox */
      this._videoEl.mozRequestFullScreen();
    } else if (this._videoEl.webkitRequestFullscreen) { /* Chrome, Safari & Opera */
      this._videoEl.webkitRequestFullscreen();
    } else if (this._videoEl.msRequestFullscreen) { /* IE/Edge */
      this._videoEl.msRequestFullscreen();
    }
    return Promise.resolve(true);
  }
}

const CapacitorVideoPlayer = new CapacitorVideoPlayerWeb();

export { CapacitorVideoPlayer };
