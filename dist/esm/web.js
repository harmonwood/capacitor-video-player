import { WebPlugin } from '@capacitor/core';
import { VideoPlayer } from './web-utils/videoplayer';
export class CapacitorVideoPlayerWeb extends WebPlugin {
    constructor() {
        super();
        this._players = [];
        this.addListeners();
    }
    async echo(options) {
        return Promise.resolve({ result: true, method: 'echo', value: options });
    }
    /**
     *  Player initialization
     *
     * @param options
     */
    async initPlayer(options) {
        if (options == null) {
            return Promise.resolve({
                result: false,
                method: 'initPlayer',
                message: 'Must provide a capVideoPlayerOptions object',
            });
        }
        this.mode = options.mode ? options.mode : '';
        if (this.mode == null || this.mode.length === 0) {
            return Promise.resolve({
                result: false,
                method: 'initPlayer',
                message: 'Must provide a Mode (fullscreen/embedded)',
            });
        }
        if (this.mode === 'fullscreen' || this.mode === 'embedded') {
            const url = options.url ? options.url : '';
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
            const playerId = options.playerId ? options.playerId : '';
            if (playerId == null || playerId.length === 0) {
                return Promise.resolve({
                    result: false,
                    method: 'initPlayer',
                    message: 'Must provide a Player Id',
                });
            }
            const rate = options.rate ? options.rate : 1.0;
            let exitOnEnd = true;
            if (Object.keys(options).includes('exitOnEnd')) {
                const exitRet = options.exitOnEnd;
                exitOnEnd = exitRet != null ? exitRet : true;
            }
            let loopOnEnd = false;
            if (Object.keys(options).includes('loopOnEnd') && !exitOnEnd) {
                const loopRet = options.loopOnEnd;
                loopOnEnd = loopRet != null ? loopRet : false;
            }
            const componentTag = options.componentTag
                ? options.componentTag
                : '';
            if (componentTag == null || componentTag.length === 0) {
                return Promise.resolve({
                    result: false,
                    method: 'initPlayer',
                    message: 'Must provide a Component Tag',
                });
            }
            let playerSize = null;
            if (this.mode === 'embedded') {
                playerSize = this.checkSize(options);
            }
            const result = await this._initializeVideoPlayer(url, playerId, this.mode, rate, exitOnEnd, loopOnEnd, componentTag, playerSize);
            return Promise.resolve({ result: result });
        }
        else {
            return Promise.resolve({
                result: false,
                method: 'initPlayer',
                message: 'Must provide a Mode either fullscreen or embedded)',
            });
        }
    }
    /**
     * Return if a given playerId is playing
     *
     * @param options
     */
    async isPlaying(options) {
        if (options == null) {
            return Promise.resolve({
                result: false,
                method: 'isPlaying',
                message: 'Must provide a capVideoPlayerIdOptions object',
            });
        }
        let playerId = options.playerId ? options.playerId : '';
        if (playerId == null || playerId.length === 0) {
            playerId = 'fullscreen';
        }
        if (this._players[playerId]) {
            const playing = this._players[playerId].isPlaying;
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
    }
    /**
     * Play the current video from a given playerId
     *
     * @param options
     */
    async play(options) {
        if (options == null) {
            return Promise.resolve({
                result: false,
                method: 'play',
                message: 'Must provide a capVideoPlayerIdOptions object',
            });
        }
        let playerId = options.playerId ? options.playerId : '';
        if (playerId == null || playerId.length === 0) {
            playerId = 'fullscreen';
        }
        if (this._players[playerId]) {
            await this._players[playerId].videoEl.play();
            return Promise.resolve({ method: 'play', result: true, value: true });
        }
        else {
            return Promise.resolve({
                method: 'play',
                result: false,
                message: 'Given PlayerId does not exist)',
            });
        }
    }
    /**
     * Pause the current video from a given playerId
     *
     * @param options
     */
    async pause(options) {
        if (options == null) {
            return Promise.resolve({
                result: false,
                method: 'pause',
                message: 'Must provide a capVideoPlayerIdOptions object',
            });
        }
        let playerId = options.playerId ? options.playerId : '';
        if (playerId == null || playerId.length === 0) {
            playerId = 'fullscreen';
        }
        if (this._players[playerId]) {
            if (this._players[playerId].isPlaying)
                await this._players[playerId].videoEl.pause();
            return Promise.resolve({ method: 'pause', result: true, value: true });
        }
        else {
            return Promise.resolve({
                method: 'pause',
                result: false,
                message: 'Given PlayerId does not exist)',
            });
        }
    }
    /**
     * Get the duration of the current video from a given playerId
     *
     * @param options
     */
    async getDuration(options) {
        if (options == null) {
            return Promise.resolve({
                result: false,
                method: 'getDuration',
                message: 'Must provide a capVideoPlayerIdOptions object',
            });
        }
        let playerId = options.playerId ? options.playerId : '';
        if (playerId == null || playerId.length === 0) {
            playerId = 'fullscreen';
        }
        if (this._players[playerId]) {
            const duration = this._players[playerId].videoEl.duration;
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
    }
    /**
     * Set the rate of the current video from a given playerId
     *
     * @param options
     */
    async setRate(options) {
        if (options == null) {
            return Promise.resolve({
                result: false,
                method: 'setRate',
                message: 'Must provide a capVideoRateOptions object',
            });
        }
        let playerId = options.playerId ? options.playerId : '';
        if (playerId == null || playerId.length === 0) {
            playerId = 'fullscreen';
        }
        const rateList = [0.25, 0.5, 0.75, 1.0, 2.0, 4.0];
        const rate = options.rate && rateList.includes(options.rate) ? options.rate : 1.0;
        if (this._players[playerId]) {
            this._players[playerId].videoEl.playbackRate = rate;
            return Promise.resolve({
                method: 'setRate',
                result: true,
                value: rate,
            });
        }
        else {
            return Promise.resolve({
                method: 'setRate',
                result: false,
                message: 'Given PlayerId does not exist)',
            });
        }
    }
    /**
     * Get the volume of the current video from a given playerId
     *
     * @param options
     */
    async getRate(options) {
        if (options == null) {
            return Promise.resolve({
                result: false,
                method: 'getRate',
                message: 'Must provide a capVideoPlayerIdOptions object',
            });
        }
        let playerId = options.playerId ? options.playerId : '';
        if (playerId == null || playerId.length === 0) {
            playerId = 'fullscreen';
        }
        if (this._players[playerId]) {
            const rate = this._players[playerId].videoEl.playbackRate;
            return Promise.resolve({
                method: 'getRate',
                result: true,
                value: rate,
            });
        }
        else {
            return Promise.resolve({
                method: 'getRate',
                result: false,
                message: 'Given PlayerId does not exist)',
            });
        }
    }
    /**
     * Set the volume of the current video from a given playerId
     *
     * @param options
     */
    async setVolume(options) {
        if (options == null) {
            return Promise.resolve({
                result: false,
                method: 'setVolume',
                message: 'Must provide a capVideoVolumeOptions object',
            });
        }
        let playerId = options.playerId ? options.playerId : '';
        if (playerId == null || playerId.length === 0) {
            playerId = 'fullscreen';
        }
        const volume = options.volume ? options.volume : 0.5;
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
    }
    /**
     * Get the volume of the current video from a given playerId
     *
     * @param options
     */
    async getVolume(options) {
        if (options == null) {
            return Promise.resolve({
                result: false,
                method: 'getVolume',
                message: 'Must provide a capVideoPlayerIdOptions object',
            });
        }
        let playerId = options.playerId ? options.playerId : '';
        if (playerId == null || playerId.length === 0) {
            playerId = 'fullscreen';
        }
        if (this._players[playerId]) {
            const volume = this._players[playerId].videoEl.volume;
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
    }
    /**
     * Set the muted property of the current video from a given playerId
     *
     * @param options
     */
    async setMuted(options) {
        if (options == null) {
            return Promise.resolve({
                result: false,
                method: 'setMuted',
                message: 'Must provide a capVideoMutedOptions object',
            });
        }
        let playerId = options.playerId ? options.playerId : '';
        if (playerId == null || playerId.length === 0) {
            playerId = 'fullscreen';
        }
        const muted = options.muted ? options.muted : false;
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
    }
    /**
     * Get the muted property of the current video from a given playerId
     *
     * @param options
     */
    async getMuted(options) {
        if (options == null) {
            return Promise.resolve({
                result: false,
                method: 'getMuted',
                message: 'Must provide a capVideoPlayerIdOptions object',
            });
        }
        let playerId = options.playerId ? options.playerId : '';
        if (playerId == null || playerId.length === 0) {
            playerId = 'fullscreen';
        }
        if (this._players[playerId]) {
            const muted = this._players[playerId].videoEl.muted;
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
    }
    /**
     * Set the current time of the current video from a given playerId
     *
     * @param options
     */
    async setCurrentTime(options) {
        if (options == null) {
            return Promise.resolve({
                result: false,
                method: 'setCurrentTime',
                message: 'Must provide a capVideoTimeOptions object',
            });
        }
        let playerId = options.playerId ? options.playerId : '';
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
    }
    /**
     * Get the current time of the current video from a given playerId
     *
     * @param options
     */
    async getCurrentTime(options) {
        if (options == null) {
            return Promise.resolve({
                result: false,
                method: 'getCurrentTime',
                message: 'Must provide a capVideoPlayerIdOptions object',
            });
        }
        let playerId = options.playerId ? options.playerId : '';
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
    }
    /**
     * Get the current time of the current video from a given playerId
     *
     */
    async stopAllPlayers() {
        for (const i in this._players) {
            if (this._players[i].pipMode) {
                const doc = document;
                if (doc.pictureInPictureElement) {
                    await doc.exitPictureInPicture();
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
    }
    /**
     * Show controller
     *
     */
    async showController() {
        return Promise.resolve({
            method: 'showController',
            result: true,
            value: true,
        });
    }
    /**
     * isControllerIsFullyVisible
     *
     */
    async isControllerIsFullyVisible() {
        return Promise.resolve({
            method: 'isControllerIsFullyVisible',
            result: true,
            value: true,
        });
    }
    /**
     * Exit the current player
     *
     */
    async exitPlayer() {
        return Promise.resolve({
            method: 'exitPlayer',
            result: true,
            value: true,
        });
    }
    checkSize(options) {
        const playerSize = {
            width: options.width ? options.width : 320,
            height: options.height ? options.height : 180,
        };
        const ratio = playerSize.height / playerSize.width;
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
    async _initializeVideoPlayer(url, playerId, mode, rate, exitOnEnd, loopOnEnd, componentTag, playerSize) {
        const videoURL = url
            ? url.indexOf('%2F') == -1
                ? encodeURI(url)
                : url
            : null;
        if (videoURL === null)
            return Promise.resolve(false);
        this.videoContainer =
            await this._getContainerElement(playerId, componentTag);
        if (this.videoContainer === null)
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
        if (mode === 'embedded') {
            this._players[playerId] = new VideoPlayer('embedded', videoURL, playerId, rate, exitOnEnd, loopOnEnd, this.videoContainer, 2, playerSize.width, playerSize.height);
            await this._players[playerId].initialize();
        }
        else if (mode === 'fullscreen') {
            this._players['fullscreen'] = new VideoPlayer('fullscreen', videoURL, 'fullscreen', rate, exitOnEnd, loopOnEnd, this.videoContainer, 99995);
            await this._players['fullscreen'].initialize();
        }
        else {
            return Promise.resolve({
                method: 'initPlayer',
                result: false,
                message: 'mode not supported',
            });
        }
        return Promise.resolve({ method: 'initPlayer', result: true, value: true });
    }
    async _getContainerElement(playerId, componentTag) {
        const videoContainer = document.createElement('div');
        videoContainer.id = `vc_${playerId}`;
        if (componentTag != null && componentTag.length > 0) {
            const cmpTagEl = document.querySelector(`${componentTag}`);
            if (cmpTagEl === null)
                return Promise.resolve(null);
            let container = null;
            const shadowRoot = cmpTagEl.shadowRoot ? cmpTagEl.shadowRoot : null;
            if (shadowRoot != null) {
                container = shadowRoot.querySelector(`[id='${playerId}']`);
            }
            else {
                container = cmpTagEl.querySelector(`[id='${playerId}']`);
            }
            if (container != null)
                container.appendChild(videoContainer);
            return Promise.resolve(videoContainer);
        }
        else {
            return Promise.resolve(null);
        }
    }
    handlePlayerPlay(data) {
        this.notifyListeners('jeepCapVideoPlayerPlay', data);
    }
    handlePlayerPause(data) {
        this.notifyListeners('jeepCapVideoPlayerPause', data);
    }
    handlePlayerEnded(data) {
        var _a;
        if (this.mode === 'fullscreen') {
            (_a = this.videoContainer) === null || _a === void 0 ? void 0 : _a.remove();
        }
        this.removeListeners();
        this.notifyListeners('jeepCapVideoPlayerEnded', data);
    }
    handlePlayerExit() {
        var _a;
        if (this.mode === 'fullscreen') {
            (_a = this.videoContainer) === null || _a === void 0 ? void 0 : _a.remove();
        }
        const retData = { dismiss: true };
        this.removeListeners();
        this.notifyListeners('jeepCapVideoPlayerExit', retData);
    }
    handlePlayerReady(data) {
        this.notifyListeners('jeepCapVideoPlayerReady', data);
    }
    addListeners() {
        document.addEventListener('videoPlayerPlay', (ev) => {
            this.handlePlayerPlay(ev.detail);
        }, false);
        document.addEventListener('videoPlayerPause', (ev) => {
            this.handlePlayerPause(ev.detail);
        }, false);
        document.addEventListener('videoPlayerEnded', (ev) => {
            this.handlePlayerEnded(ev.detail);
        }, false);
        document.addEventListener('videoPlayerReady', (ev) => {
            this.handlePlayerReady(ev.detail);
        }, false);
        document.addEventListener('videoPlayerExit', () => {
            this.handlePlayerExit();
        }, false);
    }
    removeListeners() {
        document.removeEventListener('videoPlayerPlay', (ev) => {
            this.handlePlayerPlay(ev.detail);
        }, false);
        document.removeEventListener('videoPlayerPause', (ev) => {
            this.handlePlayerPause(ev.detail);
        }, false);
        document.removeEventListener('videoPlayerEnded', (ev) => {
            this.handlePlayerEnded(ev.detail);
        }, false);
        document.removeEventListener('videoPlayerReady', (ev) => {
            this.handlePlayerReady(ev.detail);
        }, false);
        document.removeEventListener('videoPlayerExit', () => {
            this.handlePlayerExit();
        }, false);
    }
}
//# sourceMappingURL=web.js.map