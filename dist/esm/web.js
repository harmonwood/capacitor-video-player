var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : new P(function (resolve) { resolve(result.value); }).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
import { WebPlugin } from '@capacitor/core';
export class CapacitorVideoPlayerWeb extends WebPlugin {
    constructor() {
        super({
            name: 'CapacitorVideoPlayer',
            platforms: ['web']
        });
    }
    play(options) {
        return __awaiter(this, void 0, void 0, function* () {
            let url = options.url;
            if (url == null) {
                return Promise.reject("Must provide a Video Url");
            }
            const result = yield this._initializeVideoPlayer(url);
            return Promise.resolve({ result: result });
        });
    }
    _initializeVideoPlayer(url) {
        return __awaiter(this, void 0, void 0, function* () {
            // encode the url
            this._url = url ? encodeURI(url) : null;
            if (this._url === null)
                return Promise.resolve(false);
            // create a container
            this._container = document.createElement('div');
            this._container.style.position = 'absolute';
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
            const width = this._container.offsetWidth;
            const height = this._container.offsetHeight;
            const xmlns = "http://www.w3.org/2000/svg";
            const svg = document.createElementNS(xmlns, 'svg');
            svg.setAttributeNS(null, 'width', width.toString());
            svg.setAttributeNS(null, 'height', height.toString());
            const viewbox = '0 0 ' + width.toString() + ' ' + height.toString();
            svg.setAttributeNS(null, 'viewBox', viewbox);
            svg.style.zIndex = '99998';
            const rect = document.createElementNS(xmlns, 'rect');
            rect.setAttributeNS(null, "x", "0");
            rect.setAttributeNS(null, "y", "0");
            rect.setAttributeNS(null, "width", width.toString());
            rect.setAttributeNS(null, "height", height.toString());
            rect.setAttributeNS(null, "fill", "#000000");
            svg.appendChild(rect);
            this._container.appendChild(svg);
            const heightVideo = width * 9 / 16;
            this._videoContainer = document.createElement('div');
            this._videoContainer.style.position = 'absolute';
            this._videoContainer.style.left = "0";
            this._videoContainer.style.width = width.toString() + 'px';
            this._videoContainer.style.height = heightVideo.toString() + 'px';
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
            yield this._videoContainer.appendChild(this._videoEl);
            // below doesn't work on Safari
            if (!this._videoEl.webkitDisplayingFullscreen)
                this._videoEl.webkitEnterFullscreen();
            return Promise.resolve(true);
        });
    }
}
const CapacitorVideoPlayer = new CapacitorVideoPlayerWeb();
export { CapacitorVideoPlayer };
//# sourceMappingURL=web.js.map