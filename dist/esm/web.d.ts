import { WebPlugin } from '@capacitor/core';
import { CapacitorVideoPlayerPlugin, capVideoPlayerOptions, capVideoPlayerResult } from './definitions';
export declare class CapacitorVideoPlayerWeb extends WebPlugin implements CapacitorVideoPlayerPlugin {
    private _videoEl;
    private _videoContainer;
    private _container;
    private _url;
    constructor();
    play(options: capVideoPlayerOptions): Promise<capVideoPlayerResult>;
    private _initializeVideoPlayer;
}
declare const CapacitorVideoPlayer: CapacitorVideoPlayerWeb;
export { CapacitorVideoPlayer };
