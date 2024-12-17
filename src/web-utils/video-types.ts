export type videoExtension = 'mp4' | 'webm' | 'cmaf' | 'cmfv' | 'm3u8';
export type videoMimeType = 'video/mp4' | 'application/x-mpegURL';
export const possibleQueryParameterExtensions: string[] = [
  'file',
  'extension',
  'filetype',
  'type',
  'ext',
];

export const videoTypes: Record<videoExtension, videoMimeType> = {
  mp4: 'video/mp4',
  webm: 'video/mp4',
  cmaf: 'video/mp4',
  cmfv: 'video/mp4',
  m3u8: 'application/x-mpegURL',
};
