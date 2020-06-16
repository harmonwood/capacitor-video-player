var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
//import Hls from "hls.js";
import Hls from "./hls/hls.js";
import { isSupported } from './hls-utils';
export class VideoPlayer {
    constructor(mode, url, playerId, container, zIndex, width, height) {
        this._videoType = null;
        //private _videoClass: string = null;
        this._videoContainer = null;
        this._isSupported = false;
        this._firstReadyToPlay = true;
        this._url = url;
        this._container = container;
        this._mode = mode;
        this._width = width ? width : 320;
        this._height = height ? height : 180;
        this._mode = mode;
        this._zIndex = zIndex ? zIndex : 1;
        this._playerId = playerId;
        this.initialize();
    }
    initialize() {
        return __awaiter(this, void 0, void 0, function* () {
            if (isSupported())
                this._isSupported = true;
            // get the video type
            const retB = this._getVideoType();
            if (retB) {
                // style the container
                if (this._mode === "fullscreen") {
                    this._container.style.position = 'absolute';
                    this._container.style.width = '100vw';
                    this._container.style.height = '100vh';
                }
                if (this._mode === "embedded") {
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
                const width = this._mode === "fullscreen" ? this._container.offsetWidth : this._width;
                const height = this._mode === "fullscreen" ? this._container.offsetHeight : this._height;
                const xmlns = "http://www.w3.org/2000/svg";
                const svg = document.createElementNS(xmlns, 'svg');
                svg.setAttributeNS(null, 'width', width.toString());
                svg.setAttributeNS(null, 'height', height.toString());
                const viewbox = '0 0 ' + width.toString() + ' ' + height.toString();
                svg.setAttributeNS(null, 'viewBox', viewbox);
                svg.style.zIndex = (this._zIndex + 1).toString();
                const rect = document.createElementNS(xmlns, 'rect');
                rect.setAttributeNS(null, "x", "0");
                rect.setAttributeNS(null, "y", "0");
                rect.setAttributeNS(null, "width", width.toString());
                rect.setAttributeNS(null, "height", height.toString());
                rect.setAttributeNS(null, "fill", "#000000");
                svg.appendChild(rect);
                this._container.appendChild(svg);
                const heightVideo = width * this._height / this._width;
                this._videoContainer = document.createElement('div');
                this._videoContainer.style.position = 'absolute';
                this._videoContainer.style.left = "0";
                this._videoContainer.style.width = width.toString() + 'px';
                this._videoContainer.style.height = heightVideo.toString() + 'px';
                this._videoContainer.style.zIndex = (this._zIndex + 2).toString();
                this._container.appendChild(this._videoContainer);
                /*   Create Video Element */
                const isCreated = yield this.createVideoElement(width, heightVideo);
                if (!isCreated) {
                    this._createEvent("Exit", this._playerId, "Video Error: failed to create the Video Element");
                }
            }
            else {
                this._createEvent("Exit", this._playerId, "Url Error: type not supported");
            }
        });
    }
    createVideoElement(width, height) {
        return __awaiter(this, void 0, void 0, function* () {
            this.videoEl = document.createElement('video');
            this.videoEl.controls = true;
            this.videoEl.style.zIndex = (this._zIndex + 3).toString();
            this.videoEl.style.width = `${width.toString()}px`;
            this.videoEl.style.height = `${height.toString()}px`;
            this._videoContainer.appendChild(this.videoEl);
            // set the player 
            const isSet = yield this._setPlayer();
            if (isSet) {
                this.videoEl.onended = () => {
                    if (this._mode === "fullscreen") {
                        this._closeFullscreen();
                    }
                    this._createEvent("Ended", this._playerId);
                };
                this.videoEl.oncanplay = () => __awaiter(this, void 0, void 0, function* () {
                    if (this._firstReadyToPlay) {
                        this._createEvent("Ready", this._playerId);
                        this.videoEl.muted = false;
                        console.log("in onCanPlay videoEL ", this.videoEl.outerHTML);
                        if (this._mode === "fullscreen")
                            yield this.videoEl.play();
                        this._firstReadyToPlay = false;
                    }
                });
                this.videoEl.onplay = () => {
                    this._createEvent("Play", this._playerId);
                };
                this.videoEl.onplaying = () => {
                    this._createEvent("Playing", this._playerId);
                };
                this.videoEl.onpause = () => {
                    this._createEvent("Pause", this._playerId);
                };
                if (this._mode === "fullscreen") {
                    // create the video player exit button
                    const exitEl = document.createElement('button');
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
                    this._videoContainer.onclick = () => __awaiter(this, void 0, void 0, function* () {
                        this._initial = yield this._doHide(exitEl, 3000);
                    });
                    this._videoContainer.ontouchstart = () => __awaiter(this, void 0, void 0, function* () {
                        this._initial = yield this._doHide(exitEl, 3000);
                    });
                    this._videoContainer.onmousemove = () => __awaiter(this, void 0, void 0, function* () {
                        this._initial = yield this._doHide(exitEl, 3000);
                    });
                    exitEl.onclick = () => {
                        this._createEvent("Exit", this._playerId);
                    };
                    exitEl.ontouchstart = () => {
                        this._createEvent("Exit", this._playerId);
                    };
                    this._videoContainer.appendChild(exitEl);
                    this._initial = yield this._doHide(exitEl, 3000);
                    if (this._videoContainer.requestFullscreen) {
                        this._videoContainer.requestFullscreen();
                    }
                    else if (this._videoContainer.mozRequestFullScreen) { /* Firefox */
                        this._videoContainer.mozRequestFullScreen();
                    }
                    else if (this._videoContainer.webkitRequestFullscreen) { /* Chrome, Safari & Opera */
                        this._videoContainer.webkitRequestFullscreen();
                    }
                    else if (this._videoContainer.msRequestFullscreen) { /* IE/Edge */
                        this._videoContainer.msRequestFullscreen();
                    }
                }
            }
            return isSet;
        });
    }
    _setPlayer() {
        return __awaiter(this, void 0, void 0, function* () {
            return new Promise((resolve) => __awaiter(this, void 0, void 0, function* () {
                if (this._isSupported && this._videoType === "application/x-mpegURL") {
                    //                const { Hls } = await import('./hls/hls.js');
                    // HLS
                    var hls = new Hls();
                    hls.attachMedia(this.videoEl);
                    hls.on(Hls.Events.MEDIA_ATTACHED, () => {
                        hls.loadSource(this._url);
                        hls.on(Hls.Events.MANIFEST_PARSED, () => {
                            this.videoEl.muted = true;
                            this.videoEl.crossOrigin = "anonymous";
                            resolve(true);
                        });
                    });
                }
                else if (this._videoType === "video/mp4") {
                    // CMAF (fMP4) && MP4
                    this.videoEl.src = this._url;
                    if (this._url.substring(0, 5) != "https" &&
                        this._url.substring(0, 4) === "http")
                        this.videoEl.crossOrigin = "anonymous";
                    if (this._url.substring(0, 5) === "https" ||
                        this._url.substring(0, 4) === "http")
                        this.videoEl.muted = true;
                    resolve(true);
                }
                else {
                    // Not Supported
                    return (false);
                }
                console.log("in setPlayer videoEL ", this.videoEl.outerHTML);
            }));
        });
    }
    _getVideoType() {
        let ret = false;
        let vType = null;
        try {
            const val = this._url.substring(this._url.lastIndexOf('/')).match(/(.*)\.(.*)/);
            if (val == null) {
                vType = "";
            }
            else {
                vType = this._url.match(/(.*)\.(.*)/)[2];
            }
            switch (vType) {
                case "mp4":
                case "":
                case "webm":
                case "cmaf":
                case "cmfv":
                case "cmfa": {
                    this._videoType = "video/mp4";
                    //                this._videoClass = "video-js";
                    break;
                }
                case "m3u8": {
                    this._videoType = "application/x-mpegURL";
                    //                this._videoClass = "vjs-default-skin";
                    break;
                }
                /*
                case "mpd" : {
                this._videoType = "application/dash+xml";
                this._videoClass = "video-js";
                break;
                }
                */
                /*
                case "youtube" : {
                this._videoType = "video/youtube";
                this._videoClass = "video-js vjs-default-skin";
                break;
                }
                */
                default: {
                    this._videoType = null;
                    //                this._videoClass = null;
                    break;
                }
            }
            ret = true;
        }
        catch (_a) {
            ret = false;
        }
        return ret;
    }
    _doHide(exitEl, duration) {
        return __awaiter(this, void 0, void 0, function* () {
            clearTimeout(this._initial);
            exitEl.style.visibility = "visible";
            let initial = setTimeout(() => {
                exitEl.style.visibility = "hidden";
            }, duration);
            return initial;
        });
    }
    _createEvent(ev, playerId, msg) {
        const message = msg ? msg : null;
        let event;
        if (message != null) {
            event = new CustomEvent(`videoPlayer${ev}`, { detail: { fromPlayerId: playerId, message: message } });
        }
        else {
            const currentTime = this.videoEl.currentTime;
            event = new CustomEvent(`videoPlayer${ev}`, { detail: { fromPlayerId: playerId, currentTime: currentTime } });
        }
        this._container.dispatchEvent(event);
    }
    _closeFullscreen() {
        const mydoc = document;
        const isInFullScreen = (mydoc.fullscreenElement && mydoc.fullscreenElement !== null) ||
            (mydoc.webkitFullscreenElement && mydoc.webkitFullscreenElement !== null) ||
            (mydoc.mozFullScreenElement && mydoc.mozFullScreenElement !== null) ||
            (mydoc.msFullscreenElement && mydoc.msFullscreenElement !== null);
        if (isInFullScreen) {
            if (mydoc.exitFullscreen) {
                mydoc.exitFullscreen();
            }
            else if (mydoc.mozCancelFullScreen) {
                mydoc.mozCancelFullScreen();
            }
            else if (mydoc.webkitExitFullscreen) {
                mydoc.webkitExitFullscreen();
            }
            else if (mydoc.msExitFullscreen) {
                mydoc.msExitFullscreen();
            }
        }
    }
}
//# sourceMappingURL=videoplayer.js.map