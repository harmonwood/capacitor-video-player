export class VideoPlayer {
public videoEl: HTMLVideoElement
private _url: string;
private _playerId: string;
private _container: any;
private _mode: string;
private _width: number;
private _height: number;
private _zIndex: number;
private _initial: any;

    constructor(mode: string, url:string, playerId:string,container:any, zIndex:number, width?:number, height?:number) {
        this._url = url;
        this._container = container;
        this._mode = mode;
        this._width = width ? width : 320;
        this._height = height ? height : 180;
        this._mode = mode;
        this._zIndex = zIndex ? zIndex: 1;
        this._playerId = playerId;
        this.initialize();
    }
    private async initialize() {
        // create a container
        const container: HTMLDivElement = document.createElement('div');
        if(this._mode === "fullscreen") {
            container.style.position = 'absolute';
            container.style.width = '100vw';
            container.style.height = '100vh';
        }
        if(this._mode === "embedded") {
            container.style.position = 'relative';
            container.style.width = this._width.toString()+'px';
            container.style.height = this._height.toString()+'px';
        }
        container.style.left = '0';
        container.style.top = '0';
        container.style.display = 'flex';
        container.style.alignItems = 'center';
        container.style.justifyContent = 'center';
        container.style.backgroundColor = '#000000';  
        container.style.zIndex = this._zIndex.toString();
        this._container.appendChild(container);

        const width: number = this._mode === "fullscreen" ? container.offsetWidth : this._width;
        const height: number = this._mode === "fullscreen" ? container.offsetHeight : this._height;
        
        const xmlns = "http://www.w3.org/2000/svg";

        const svg = document.createElementNS(xmlns,'svg');
        svg.setAttributeNS(null,'width',width.toString());
        svg.setAttributeNS(null,'height',height.toString());
        const viewbox = '0 0 '+width.toString()+' '+height.toString();
        svg.setAttributeNS(null,'viewBox',viewbox);
        svg.style.zIndex = (this._zIndex + 1).toString();
        const rect = document.createElementNS(xmlns,'rect');
        rect.setAttributeNS (null, "x", "0");
        rect.setAttributeNS (null, "y", "0");
        rect.setAttributeNS (null, "width", width.toString());
        rect.setAttributeNS (null, "height", height.toString());
        rect.setAttributeNS (null, "fill", "#000000");
        svg.appendChild(rect);
        container.appendChild(svg);

        const heightVideo: number = width * this._height / this._width;
        const videoContainer: any = document.createElement('div');
        videoContainer.style.position = 'absolute';
        videoContainer.style.left = "0";
        videoContainer.style.width = width.toString()+'px';
        videoContainer.style.height = heightVideo.toString()+'px';
        videoContainer.style.zIndex = (this._zIndex + 2).toString();
        container.appendChild(videoContainer);
        
        this.videoEl = document.createElement('video');
        this.videoEl.controls = true;
        this.videoEl.src = this._url;
        this.videoEl.style.zIndex = (this._zIndex + 3).toString();
        if(this._mode === "fullscreen") {
            this.videoEl.style.width = "100%";
        }
        if(this._mode === "embedded") {
            this.videoEl.style.width = `${width.toString()}px`;
            this.videoEl.style.height = `${height.toString()}px`;
        }
        videoContainer.appendChild(this.videoEl); 
        this.videoEl.onended = () => {
            if(this._mode === "fullscreen") container.remove();
            this._closeFullscreen();
            this._createEvent("Ended",this._playerId);
        };
        this.videoEl.onplay = () => {
            this._createEvent("Play",this._playerId);
        };
        this.videoEl.onplaying = () => {
            this._createEvent("Playing",this._playerId);
        };
        this.videoEl.onpause = () => {
            this._createEvent("Pause",this._playerId);
        };
        if(this._mode === "fullscreen") {
            // create the video player exit button
            const exitEl:HTMLButtonElement = document.createElement('button');
            exitEl.textContent = "X";
            exitEl.style.position = 'absolute';
            exitEl.style.left = "1%";
            exitEl.style.top = "5%";
            exitEl.style.width = "5vmin";
            exitEl.style.padding = "0.5%";
            exitEl.style.fontSize = "1.2rem";
            exitEl.style.background = "rgba(51,51,51,.4)";
            exitEl.style.color = "#fff";
            exitEl.style.visibility = "hidden";
            exitEl.style.zIndex = (this._zIndex + 4).toString();
            exitEl.style.border = "1px solid rgba(51,51,51,.4)";
            exitEl.style.borderRadius = "20px";
            this.videoEl.onclick = async () => {
                this._initial = await this._doHide(exitEl,3000);
            };
            this.videoEl.ontouchstart = async () => {
                this._initial = await this._doHide(exitEl,3000);
            };
  
            this.videoEl.onmousemove = async () => {
                this._initial = await this._doHide(exitEl,3000);
            };
  
            exitEl.onclick = () => {
                container.remove();
            }
            exitEl.ontouchstart = () => {
                container.remove();
            }
  
            videoContainer.appendChild(exitEl);
            this._initial = await this._doHide(exitEl,3000);
        
            if (videoContainer.requestFullscreen) {
                videoContainer.requestFullscreen();           
            } else if (videoContainer.mozRequestFullScreen) { /* Firefox */
                videoContainer.mozRequestFullScreen();
            } else if (videoContainer.webkitRequestFullscreen) { /* Chrome, Safari & Opera */
                videoContainer.webkitRequestFullscreen();
            } else if (videoContainer.msRequestFullscreen) { /* IE/Edge */
                videoContainer.msRequestFullscreen();
            }
  
        }
    }
    private async _doHide(exitEl:HTMLButtonElement, duration:number) {
        clearTimeout(this._initial);
        exitEl.style.visibility = "visible";
        let initial = setTimeout(() => {
        exitEl.style.visibility = "hidden";
        },duration);
        return initial;
    }
    private _createEvent(ev:string,playerId:string) {
        const currentTime: number = this.videoEl.currentTime;
        const event = new CustomEvent(`jeepCapVideoPlayer${ev}`, { detail: {fromPlayerId:playerId,currentTime:currentTime}}); 
        document.dispatchEvent(event) ;      
    }
    private _closeFullscreen() {
        const mydoc: any = document;
        const isInFullScreen: boolean = (mydoc.fullscreenElement && mydoc.fullscreenElement !== null) ||
        (mydoc.webkitFullscreenElement && mydoc.webkitFullscreenElement !== null) ||
        (mydoc.mozFullScreenElement && mydoc.mozFullScreenElement !== null) ||
        (mydoc.msFullscreenElement && mydoc.msFullscreenElement !== null);
        if (isInFullScreen) {

            if (mydoc.exitFullscreen) {
                mydoc.exitFullscreen();
            } else if (mydoc.mozCancelFullScreen) {
                mydoc.mozCancelFullScreen();
            } else if (mydoc.webkitExitFullscreen) {
                mydoc.webkitExitFullscreen();
            } else if (mydoc.msExitFullscreen) {
                mydoc.msExitFullscreen();
            }
        }
    }    
}