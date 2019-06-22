import { WebPlugin } from '@capacitor/core';
import { CapacitorVideoPlayerPlugin, capVideoPlayerOptions, capVideoPlayerResult } from './definitions';
export declare class CapacitorVideoPlayerWeb extends WebPlugin implements CapacitorVideoPlayerPlugin {
    private _videoEl;
    private _videoContainer;
    private _container;
    private _exitEl;
    private _url;
    private _initial;
    constructor();
    play(options: capVideoPlayerOptions): Promise<capVideoPlayerResult>;
    private _doHide;
    private _initializeVideoPlayer;
}
declare const CapacitorVideoPlayer: CapacitorVideoPlayerWeb;
export { CapacitorVideoPlayer };
