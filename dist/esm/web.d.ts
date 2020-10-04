import { WebPlugin } from '@capacitor/core';
import { CapacitorVideoPlayerPlugin, capVideoPlayerOptions, capVideoPlayerIdOptions, capVideoVolumeOptions, capVideoTimeOptions, capVideoMutedOptions, capVideoPlayerResult } from './definitions';
export interface IPlayerSize {
    height?: number;
    width?: number;
}
export declare class CapacitorVideoPlayerWeb extends WebPlugin implements CapacitorVideoPlayerPlugin {
    private _players;
    constructor();
    echo(options: {
        value: string;
    }): Promise<capVideoPlayerResult>;
    /**
     *  Player initialization
     *
     * @param options
     */
    initPlayer(options: capVideoPlayerOptions): Promise<capVideoPlayerResult>;
    /**
     * Return if a given playerId is playing
     *
     * @param options
     */
    isPlaying(options: capVideoPlayerIdOptions): Promise<capVideoPlayerResult>;
    /**
     * Play the current video from a given playerId
     *
     * @param options
     */
    play(options: capVideoPlayerIdOptions): Promise<capVideoPlayerResult>;
    /**
     * Pause the current video from a given playerId
     *
     * @param options
     */
    pause(options: capVideoPlayerIdOptions): Promise<capVideoPlayerResult>;
    /**
     * Get the duration of the current video from a given playerId
     *
     * @param options
     */
    getDuration(options: capVideoPlayerIdOptions): Promise<capVideoPlayerResult>;
    /**
     * Set the volume of the current video from a given playerId
     *
     * @param options
     */
    setVolume(options: capVideoVolumeOptions): Promise<capVideoPlayerResult>;
    /**
     * Get the volume of the current video from a given playerId
     *
     * @param options
     */
    getVolume(options: capVideoPlayerIdOptions): Promise<capVideoPlayerResult>;
    /**
     * Set the muted property of the current video from a given playerId
     *
     * @param options
     */
    setMuted(options: capVideoMutedOptions): Promise<capVideoPlayerResult>;
    /**
     * Get the muted property of the current video from a given playerId
     *
     * @param options
     */
    getMuted(options: capVideoPlayerIdOptions): Promise<capVideoPlayerResult>;
    /**
     * Set the current time of the current video from a given playerId
     *
     * @param options
     */
    setCurrentTime(options: capVideoTimeOptions): Promise<capVideoPlayerResult>;
    /**
     * Get the current time of the current video from a given playerId
     *
     * @param options
     */
    getCurrentTime(options: capVideoPlayerIdOptions): Promise<capVideoPlayerResult>;
    /**
     * Get the current time of the current video from a given playerId
     *
     */
    stopAllPlayers(): Promise<capVideoPlayerResult>;
    private checkSize;
    private _initializeVideoPlayer;
    private _getContainerElement;
    private handlePlayerPlay;
    private handlePlayerPause;
    private handlePlayerEnded;
    private handlePlayerExit;
    private handlePlayerReady;
}
declare const CapacitorVideoPlayer: CapacitorVideoPlayerWeb;
export { CapacitorVideoPlayer };
