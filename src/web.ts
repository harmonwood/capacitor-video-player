import { WebPlugin } from '@capacitor/core';
import { CapacitorVideoPlayerPlugin, capVideoPlayerOptions, capVideoPlayerResult } from './definitions';

export class CapacitorVideoPlayerWeb extends WebPlugin implements CapacitorVideoPlayerPlugin {
  private _videoEl: any;
  private _videoContainer: any;
  private _container: HTMLDivElement;
  private _exitEl: HTMLButtonElement;
  private _url: string;
  private _initial: any;


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
  private async _doHide(duration:number) {
    clearTimeout(this._initial);
    this._exitEl.style.visibility = "visible";
    let initial = setTimeout(() => {
      this._exitEl.style.visibility = "hidden";
    },duration);
    return initial;
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
    this._container.style.zIndex = '99996';
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
    svg.style.zIndex = '99997';
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
    this._videoContainer.style.zIndex = '99998';
    this._container.appendChild(this._videoContainer);
    // create the video player
    this._videoEl = document.createElement('video');
    this._videoEl.controls = true;
    this._videoEl.src = this._url;
    this._videoEl.style.width = "100%";
    this._videoEl.style.zIndex = '99998';
    //
    // create the video player exit button
    this._exitEl = document.createElement('button');
    this._exitEl.textContent = "X";
    this._exitEl.style.position = 'absolute';
    this._exitEl.style.left = "1%";
    this._exitEl.style.top = "5%";
    this._exitEl.style.width = "3%";
    this._exitEl.style.padding = "0.5%";
    this._exitEl.style.fontSize = "1.2rem";
    this._exitEl.style.background = "rgba(51,51,51,.4)";
    this._exitEl.style.color = "#fff";
    this._exitEl.style.visibility = "hidden";
    this._exitEl.style.zIndex = '99999';
    this._exitEl.style.border = "1px solid rgba(51,51,51,.4)";
    this._exitEl.style.borderRadius = "20px";

    this._videoEl.onclick = async () => {
      this._initial = await this._doHide(3000);
    };
    this._videoEl.ontouchstart = async () => {
      this._initial = await this._doHide(3000);
    };

    this._videoEl.onmousemove = async () => {
      this._initial = await this._doHide(3000);
    };

    this._videoEl.onended = () => {
      this._container.remove();
    };
    this._exitEl.onclick = () => {
      this._container.remove();
    }
    this._exitEl.ontouchstart = () => {
      this._container.remove();
    }

    await this._videoContainer.appendChild(this._videoEl);
    await this._videoContainer.appendChild(this._exitEl);
    this._initial = await this._doHide(3000);

    if (this._videoContainer.requestFullscreen) {
      this._videoContainer.requestFullscreen();           
    } else if (this._videoContainer.mozRequestFullScreen) { /* Firefox */
      this._videoContainer.mozRequestFullScreen();
    } else if (this._videoContainer.webkitRequestFullscreen) { /* Chrome, Safari & Opera */
      this._videoContainer.webkitRequestFullscreen();
    } else if (this._videoContainer.msRequestFullscreen) { /* IE/Edge */
      this._videoContainer.msRequestFullscreen();
    }
    this._videoEl.play();
    return Promise.resolve(true);
  }
}

const CapacitorVideoPlayer = new CapacitorVideoPlayerWeb();

export { CapacitorVideoPlayer };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(CapacitorVideoPlayer);
