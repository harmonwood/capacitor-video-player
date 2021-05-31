# Usage Release 3.0.0 Documentation

- In your code

```ts
import { Capacitor } from '@capacitor/core';
import { CapacitorVideoPlayer } from 'capacitor-video-player';

export const setVideoPlayer = async (): Promise<any>=> {
  const platform = Capacitor.getPlatform();
  return {plugin:CapacitorVideoPlayer, platform};
};

```
