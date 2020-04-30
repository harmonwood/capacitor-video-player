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
            platforms: ['web']
        });
        this._players = [];
    }
    echo(options) {
        return __awaiter(this, void 0, void 0, function* () {
            console.log('ECHO', options);
            return options;
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
                return Promise.reject("VideoPlayer initPlayer: Must provide a Mode (fullscreen/embedded)");
            }
            if (mode === "fullscreen" || mode === "embedded") {
                let url = options.url;
                if (url == null || url.length === 0) {
                    return Promise.reject("VideoPlayer initPlayer: Must provide a Video Url");
                }
                if (mode === "embedded") {
                    let playerId = options.playerId;
                    if (playerId == null || playerId.length === 0) {
                        return Promise.reject("VideoPlayer initPlayer: Must provide a Player Id");
                    }
                    const playerSize = this.checkSize(options);
                    const result = yield this._initializeVideoPlayerEmbedded(url, playerId, playerSize);
                    return Promise.resolve({ result: result });
                }
                if (mode === "fullscreen") {
                    const result = yield this._initializeVideoPlayerFullScreen(url);
                    return Promise.resolve({ result: result });
                }
            }
            else {
                return Promise.reject("VideoPlayer initPlayer: Must provide a Mode either fullscreen or embedded)");
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
                playerId = "fullscreen";
            }
            if (this._players[playerId]) {
                this._players[playerId].videoEl.play();
                return Promise.resolve({ method: "play", result: true });
            }
            else {
                return Promise.reject("VideoPlayer Play: Given PlayerId does not exist)");
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
                playerId = "fullscreen";
            }
            if (this._players[playerId]) {
                this._players[playerId].videoEl.pause();
                return Promise.resolve({ method: "pause", result: true });
            }
            else {
                return Promise.reject("VideoPlayer Pause: Given PlayerId does not exist)");
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
                playerId = "fullscreen";
            }
            if (this._players[playerId]) {
                let duration = this._players[playerId].videoEl.duration;
                return Promise.resolve({ method: "getDuration", result: true, value: duration });
            }
            else {
                return Promise.reject("VideoPlayer Pause: Given PlayerId does not exist)");
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
                playerId = "fullscreen";
            }
            let volume = options.volume ? options.volume : 0.5;
            if (this._players[playerId]) {
                this._players[playerId].videoEl.volume = volume;
                return Promise.resolve({ method: "setVolume", result: true, value: volume });
            }
            else {
                return Promise.reject("VideoPlayer Pause: Given PlayerId does not exist)");
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
                playerId = "fullscreen";
            }
            if (this._players[playerId]) {
                let volume = this._players[playerId].videoEl.volume;
                return Promise.resolve({ method: "getVolume", result: true, value: volume });
            }
            else {
                return Promise.reject("VideoPlayer Pause: Given PlayerId does not exist)");
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
                playerId = "fullscreen";
            }
            let muted = options.muted ? options.muted : false;
            if (this._players[playerId]) {
                this._players[playerId].videoEl.muted = muted;
                return Promise.resolve({ method: "setMuted", result: true, value: muted });
            }
            else {
                return Promise.reject("VideoPlayer Pause: Given PlayerId does not exist)");
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
                playerId = "fullscreen";
            }
            if (this._players[playerId]) {
                let muted = this._players[playerId].videoEl.muted;
                return Promise.resolve({ method: "getMuted", result: true, value: muted });
            }
            else {
                return Promise.reject("VideoPlayer Pause: Given PlayerId does not exist)");
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
                playerId = "fullscreen";
            }
            let seekTime = options.seektime ? options.seektime : 0;
            if (this._players[playerId]) {
                const duration = this._players[playerId].videoEl.duration;
                seekTime = seekTime <= duration && seekTime >= 0 ? seekTime : duration / 2;
                this._players[playerId].videoEl.currentTime = seekTime;
                return Promise.resolve({ method: "setCurrentTime", result: true, value: seekTime });
            }
            else {
                return Promise.reject("VideoPlayer Pause: Given PlayerId does not exist)");
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
                playerId = "fullscreen";
            }
            if (this._players[playerId]) {
                const seekTime = this._players[playerId].videoEl.currentTime;
                return Promise.resolve({ method: "getCurrentTime", result: true, value: seekTime });
            }
            else {
                return Promise.reject("VideoPlayer Pause: Given PlayerId does not exist)");
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
                this._players[i].videoEl.pause();
            }
            ;
            return Promise.resolve({ method: "stopAllPlayers", result: true });
        });
    }
    checkSize(options) {
        let playerSize = {
            width: options.width ? options.width : 320,
            height: options.height ? options.height : 180
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
    _initializeVideoPlayerEmbedded(url, playerId, playerSize) {
        return __awaiter(this, void 0, void 0, function* () {
            const videoURL = url ? encodeURI(url) : null;
            if (videoURL === null)
                return Promise.resolve(false);
            const videoContainer = document.querySelector(`#${playerId}`);
            this._players[playerId] = new VideoPlayer("embedded", videoURL, playerId, videoContainer, 2, playerSize.width, playerSize.height);
            return Promise.resolve(true);
        });
    }
    _initializeVideoPlayerFullScreen(url) {
        return __awaiter(this, void 0, void 0, function* () {
            // encode the url
            const videoURL = url ? encodeURI(url) : null;
            if (videoURL === null)
                return Promise.resolve(false);
            // create the video player
            this._players["fullscreen"] = new VideoPlayer("fullscreen", videoURL, "fullscreen", document.body, 99995);
            this._players["fullscreen"].videoEl.play();
            return Promise.resolve(true);
        });
    }
}
const CapacitorVideoPlayer = new CapacitorVideoPlayerWeb();
export { CapacitorVideoPlayer };
import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(CapacitorVideoPlayer);
//# sourceMappingURL=web.js.map