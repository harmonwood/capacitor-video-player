import { registerPlugin } from '@capacitor/core';
const CapacitorVideoPlayer = registerPlugin('CapacitorVideoPlayer', {
    web: () => import('./web').then(m => new m.CapacitorVideoPlayerWeb()),
});
export * from './definitions';
export { CapacitorVideoPlayer };
//# sourceMappingURL=index.js.map