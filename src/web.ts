import { WebPlugin } from '@capacitor/core';
import { CapacitorVideoPlayerPlugin, capVideoPlayerOptions, capVideoPlayerResult  } from './definitions';

export class CapacitorVideoPlayerWeb extends WebPlugin implements CapacitorVideoPlayerPlugin {
  constructor() {
    super({
      name: 'CapacitorVideoPlayer',
      platforms: ['web']
    });
  }

  async play(options: capVideoPlayerOptions): Promise<capVideoPlayerResult> {
    console.log('ECHO', options);
    return Promise.resolve({ result: false });
  }
}

const CapacitorVideoPlayer = new CapacitorVideoPlayerWeb();

export { CapacitorVideoPlayer };
