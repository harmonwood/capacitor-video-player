var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
import { WebPlugin } from '@capacitor/core';
import { VideoPlayer } from './web-utils/videoplayer';
export class CapacitorVideoPlayerWeb extends WebPlugin {
    constructor() {
        super({
            name: 'CapacitorVideoPlayer',
            platforms: ['web'],
        });
        this._players = [];
    }
    echo(options) {
        return __awaiter(this, void 0, void 0, function* () {
            console.log('ECHO', options);
            return Promise.resolve({ result: true, method: 'echo', value: options });
        });
    }
    /**
     *  Player initialization
     *
     * @param options
     */
    initPlayer(options) {
        return __awaiter(this, void 0, void 0, function* () {
            let mode = options.mode;
            if (mode == null || mode.length === 0) {
                return Promise.resolve({
                    result: false,
                    method: 'initPlayer',
                    message: 'Must provide a Mode (fullscreen/embedded)',
                });
            }
            if (mode === 'fullscreen' || mode === 'embedded') {
                let url = options.url;
                if (url == null || url.length === 0) {
                    return Promise.resolve({
                        result: false,
                        method: 'initPlayer',
                        message: 'Must provide a Video Url',
                    });
                }
                if (url == 'internal') {
                    return Promise.resolve({
                        result: false,
                        method: 'initPlayer',
                        message: 'Internal Videos not supported on Web Platform',
                    });
                }
                let playerId = options.playerId;
                if (playerId == null || playerId.length === 0) {
                    return Promise.resolve({
                        result: false,
                        method: 'initPlayer',
                        message: 'Must provide a Player Id',
                    });
                }
                let componentTag = options.componentTag;
                if (componentTag == null || componentTag.length === 0) {
                    return Promise.resolve({
                        result: false,
                        method: 'initPlayer',
                        message: 'Must provide a Component Tag',
                    });
                }
                let playerSize = null;
                if (mode === 'embedded') {
                    playerSize = this.checkSize(options);
                }
                const result = yield this._initializeVideoPlayer(url, playerId, mode, componentTag, playerSize);
                return Promise.resolve({ result: result });
            }
            else {
                return Promise.resolve({
                    result: false,
                    method: 'initPlayer',
                    message: 'Must provide a Mode either fullscreen or embedded)',
                });
            }
        });
    }
    /**
     * Return if a given playerId is playing
     *
     * @param options
     */
    isPlaying(options) {
        return __awaiter(this, void 0, void 0, function* () {
            let playerId = options.playerId;
            if (playerId == null || playerId.length === 0) {
                playerId = 'fullscreen';
            }
            if (this._players[playerId]) {
                let playing = this._players[playerId].isPlaying;
                return Promise.resolve({
                    method: 'isPlaying',
                    result: true,
                    value: playing,
                });
            }
            else {
                return Promise.resolve({
                    method: 'isPlaying',
                    result: false,
                    message: 'Given PlayerId does not exist)',
                });
            }
        });
    }
    /**
     * Play the current video from a given playerId
     *
     * @param options
     */
    play(options) {
        return __awaiter(this, void 0, void 0, function* () {
            let playerId = options.playerId;
            if (playerId == null || playerId.length === 0) {
                playerId = 'fullscreen';
            }
            if (this._players[playerId]) {
                yield this._players[playerId].videoEl.play();
                return Promise.resolve({ method: 'play', result: true, value: true });
            }
            else {
                return Promise.resolve({
                    method: 'play',
                    result: false,
                    message: 'Given PlayerId does not exist)',
                });
            }
        });
    }
    /**
     * Pause the current video from a given playerId
     *
     * @param options
     */
    pause(options) {
        return __awaiter(this, void 0, void 0, function* () {
            let playerId = options.playerId;
            if (playerId == null || playerId.length === 0) {
                playerId = 'fullscreen';
            }
            if (this._players[playerId]) {
                if (this._players[playerId].isPlaying)
                    yield this._players[playerId].videoEl.pause();
                return Promise.resolve({ method: 'pause', result: true, value: true });
            }
            else {
                return Promise.resolve({
                    method: 'pause',
                    result: false,
                    message: 'Given PlayerId does not exist)',
                });
            }
        });
    }
    /**
     * Get the duration of the current video from a given playerId
     *
     * @param options
     */
    getDuration(options) {
        return __awaiter(this, void 0, void 0, function* () {
            let playerId = options.playerId;
            if (playerId == null || playerId.length === 0) {
                playerId = 'fullscreen';
            }
            if (this._players[playerId]) {
                let duration = this._players[playerId].videoEl.duration;
                return Promise.resolve({
                    method: 'getDuration',
                    result: true,
                    value: duration,
                });
            }
            else {
                return Promise.resolve({
                    method: 'getDuration',
                    result: false,
                    message: 'Given PlayerId does not exist)',
                });
            }
        });
    }
    /**
     * Set the volume of the current video from a given playerId
     *
     * @param options
     */
    setVolume(options) {
        return __awaiter(this, void 0, void 0, function* () {
            let playerId = options.playerId;
            if (playerId == null || playerId.length === 0) {
                playerId = 'fullscreen';
            }
            let volume = options.volume ? options.volume : 0.5;
            if (this._players[playerId]) {
                this._players[playerId].videoEl.volume = volume;
                return Promise.resolve({
                    method: 'setVolume',
                    result: true,
                    value: volume,
                });
            }
            else {
                return Promise.resolve({
                    method: 'setVolume',
                    result: false,
                    message: 'Given PlayerId does not exist)',
                });
            }
        });
    }
    /**
     * Get the volume of the current video from a given playerId
     *
     * @param options
     */
    getVolume(options) {
        return __awaiter(this, void 0, void 0, function* () {
            let playerId = options.playerId;
            if (playerId == null || playerId.length === 0) {
                playerId = 'fullscreen';
            }
            if (this._players[playerId]) {
                let volume = this._players[playerId].videoEl.volume;
                return Promise.resolve({
                    method: 'getVolume',
                    result: true,
                    value: volume,
                });
            }
            else {
                return Promise.resolve({
                    method: 'getVolume',
                    result: false,
                    message: 'Given PlayerId does not exist)',
                });
            }
        });
    }
    /**
     * Set the muted property of the current video from a given playerId
     *
     * @param options
     */
    setMuted(options) {
        return __awaiter(this, void 0, void 0, function* () {
            let playerId = options.playerId;
            if (playerId == null || playerId.length === 0) {
                playerId = 'fullscreen';
            }
            let muted = options.muted ? options.muted : false;
            if (this._players[playerId]) {
                this._players[playerId].videoEl.muted = muted;
                return Promise.resolve({
                    method: 'setMuted',
                    result: true,
                    value: muted,
                });
            }
            else {
                return Promise.resolve({
                    method: 'setMuted',
                    result: false,
                    message: 'Given PlayerId does not exist)',
                });
            }
        });
    }
    /**
     * Get the muted property of the current video from a given playerId
     *
     * @param options
     */
    getMuted(options) {
        return __awaiter(this, void 0, void 0, function* () {
            let playerId = options.playerId;
            if (playerId == null || playerId.length === 0) {
                playerId = 'fullscreen';
            }
            if (this._players[playerId]) {
                let muted = this._players[playerId].videoEl.muted;
                return Promise.resolve({
                    method: 'getMuted',
                    result: true,
                    value: muted,
                });
            }
            else {
                return Promise.resolve({
                    method: 'getMuted',
                    result: false,
                    message: 'Given PlayerId does not exist)',
                });
            }
        });
    }
    /**
     * Set the current time of the current video from a given playerId
     *
     * @param options
     */
    setCurrentTime(options) {
        return __awaiter(this, void 0, void 0, function* () {
            let playerId = options.playerId;
            if (playerId == null || playerId.length === 0) {
                playerId = 'fullscreen';
            }
            let seekTime = options.seektime ? options.seektime : 0;
            if (this._players[playerId]) {
                const duration = this._players[playerId].videoEl.duration;
                seekTime =
                    seekTime <= duration && seekTime >= 0 ? seekTime : duration / 2;
                this._players[playerId].videoEl.currentTime = seekTime;
                return Promise.resolve({
                    method: 'setCurrentTime',
                    result: true,
                    value: seekTime,
                });
            }
            else {
                return Promise.resolve({
                    method: 'setCurrentTime',
                    result: false,
                    message: 'Given PlayerId does not exist)',
                });
            }
        });
    }
    /**
     * Get the current time of the current video from a given playerId
     *
     * @param options
     */
    getCurrentTime(options) {
        return __awaiter(this, void 0, void 0, function* () {
            let playerId = options.playerId;
            if (playerId == null || playerId.length === 0) {
                playerId = 'fullscreen';
            }
            if (this._players[playerId]) {
                const seekTime = this._players[playerId].videoEl.currentTime;
                return Promise.resolve({
                    method: 'getCurrentTime',
                    result: true,
                    value: seekTime,
                });
            }
            else {
                return Promise.resolve({
                    method: 'getCurrentTime',
                    result: false,
                    message: 'Given PlayerId does not exist)',
                });
            }
        });
    }
    /**
     * Get the current time of the current video from a given playerId
     *
     */
    stopAllPlayers() {
        return __awaiter(this, void 0, void 0, function* () {
            for (let i in this._players) {
                if (this._players[i].pipMode) {
                    var doc = document;
                    if (doc.pictureInPictureElement) {
                        yield doc.exitPictureInPicture();
                    }
                }
                if (!this._players[i].videoEl.paused)
                    this._players[i].videoEl.pause();
            }
            return Promise.resolve({
                method: 'stopAllPlayers',
                result: true,
                value: true,
            });
        });
    }
    checkSize(options) {
        let playerSize = {
            width: options.width ? options.width : 320,
            height: options.height ? options.height : 180,
        };
        let ratio = playerSize.height / playerSize.width;
        if (playerSize.width > window.innerWidth) {
            playerSize.width = window.innerWidth;
            playerSize.height = Math.floor(playerSize.width * ratio);
        }
        if (playerSize.height > window.innerHeight) {
            playerSize.height = window.innerHeight;
            playerSize.width = Math.floor(playerSize.height / ratio);
        }
        return playerSize;
    }
    _initializeVideoPlayer(url, playerId, mode, componentTag, playerSize) {
        return __awaiter(this, void 0, void 0, function* () {
            const videoURL = url
                ? url.indexOf('%2F') == -1
                    ? encodeURI(url)
                    : url
                : null;
            if (videoURL === null)
                return Promise.resolve(false);
            const videoContainer = yield this._getContainerElement(playerId, componentTag);
            if (videoContainer === null)
                return Promise.resolve({
                    method: 'initPlayer',
                    result: false,
                    message: 'componentTag or divContainerElement must be provided',
                });
            if (mode === 'embedded' && playerSize == null)
                return Promise.resolve({
                    method: 'initPlayer',
                    result: false,
                    message: 'playerSize must be defined in embedded mode',
                });
            // add listeners
            videoContainer.addEventListener('videoPlayerPlay', (ev) => {
                this.handlePlayerPlay(ev.detail);
            });
            videoContainer.addEventListener('videoPlayerPause', (ev) => {
                this.handlePlayerPause(ev.detail);
            });
            videoContainer.addEventListener('videoPlayerEnded', (ev) => {
                if (mode === 'fullscreen') {
                    videoContainer.remove();
                }
                this.handlePlayerEnded(ev.detail);
            });
            videoContainer.addEventListener('videoPlayerReady', (ev) => {
                this.handlePlayerReady(ev.detail);
            });
            videoContainer.addEventListener('videoPlayerExit', () => {
                if (mode === 'fullscreen') {
                    videoContainer.remove();
                }
                this.handlePlayerExit();
            });
            if (mode === 'embedded') {
                this._players[playerId] = new VideoPlayer('embedded', videoURL, playerId, videoContainer, 2, playerSize.width, playerSize.height);
                yield this._players[playerId].initialize();
            }
            else if (mode === 'fullscreen') {
                this._players['fullscreen'] = new VideoPlayer('fullscreen', videoURL, 'fullscreen', videoContainer, 99995);
                yield this._players['fullscreen'].initialize();
            }
            else {
                return Promise.resolve({
                    method: 'initPlayer',
                    result: false,
                    message: 'mode not supported',
                });
            }
            return Promise.resolve({ method: 'initPlayer', result: true, value: true });
        });
    }
    _getContainerElement(playerId, componentTag) {
        return __awaiter(this, void 0, void 0, function* () {
            const videoContainer = document.createElement('div');
            videoContainer.id = `vc_${playerId}`;
            if (componentTag != null && componentTag.length > 0) {
                let cmpTagEl = null;
                cmpTagEl = document.querySelector(`${componentTag}`);
                if (cmpTagEl === null)
                    return Promise.resolve(null);
                let container = null;
                try {
                    container = cmpTagEl.shadowRoot.querySelector(`#${playerId}`);
                }
                catch (_a) {
                    container = cmpTagEl.querySelector(`#${playerId}`);
                }
                container.appendChild(videoContainer);
                return Promise.resolve(videoContainer);
            }
            else {
                return Promise.resolve(null);
            }
        });
    }
    handlePlayerPlay(data) {
        this.notifyListeners('jeepCapVideoPlayerPlay', data);
    }
    handlePlayerPause(data) {
        this.notifyListeners('jeepCapVideoPlayerPause', data);
    }
    handlePlayerEnded(data) {
        this.notifyListeners('jeepCapVideoPlayerEnded', data);
    }
    handlePlayerExit() {
        const retData = { dismiss: true };
        this.notifyListeners('jeepCapVideoPlayerExit', retData);
    }
    handlePlayerReady(data) {
        this.notifyListeners('jeepCapVideoPlayerReady', data);
    }
}
const CapacitorVideoPlayer = new CapacitorVideoPlayerWeb();
export { CapacitorVideoPlayer };
import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(CapacitorVideoPlayer);
//# sourceMappingURL=web.js.map