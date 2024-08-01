export type extension = 'mp4' | 'webm' | 'cmaf' | 'cmfv' | 'm3u8';
export type mimeType = 'video/mp4' | 'application/x-mpegURL';

export const videoTypes: Record<extension, mimeType> = {
  mp4: 'video/mp4',
  webm: 'video/mp4',
  cmaf: 'video/mp4',
  cmfv: 'video/mp4',
  m3u8: 'application/x-mpegURL',
};
