import { WebPlugin } from '@capacitor/core';
import { CapacitorVideoPlayerPlugin, capVideoPlayerOptions, capVideoPlayerResult } from './definitions';
export declare class CapacitorVideoPlayerWeb extends WebPlugin implements CapacitorVideoPlayerPlugin {
    constructor();
    play(options: capVideoPlayerOptions): Promise<capVideoPlayerResult>;
}
declare const CapacitorVideoPlayer: CapacitorVideoPlayerWeb;
export { CapacitorVideoPlayer };
