import { registerPlugin } from '@capacitor/core';

import type { CapacitorVideoPlayerPlugin } from './definitions';

const CapacitorVideoPlayer = registerPlugin<CapacitorVideoPlayerPlugin>(
  'CapacitorVideoPlayer',
  {
    web: () => import('./web').then(m => new m.CapacitorVideoPlayerWeb()),
  },
);

export * from './definitions';
export { CapacitorVideoPlayer };
