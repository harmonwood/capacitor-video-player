## 4.1.0-1 (2022-09-04)

### Chores

- Update to Capacitor 4.1.0

### Bug Fixes

 - Android Fix bug in initPlayer with permissions

## 4.0.1 (2022-09-04)

### Chores

- Update to Capacitor 4.0.1

## 3.7.5 (2022-09-05)

### Bug Fixes

 - Android Fix bug in initPlayer with permissions

## 3.7.4 (2022-09-03)

### Bug Fixes

 - The latest version doesn't play video files from the local device storage #89

### Add Features

 - add permission request for reading media file for API > 28
 
## 3.7.3 (2022-08-31)

### Bug Fixes

 - API.md Android Quirks
 - iOS - Swiping Video Off Screen Does Not Destroy The Video #105

## 3.7.2 (2022-08-31)

### Chores

 - (Android) Exoplayer 2.16.0 by Manuel García Marín (https://github.com/PhantomPainX)

### Added Features

 - (Android) Add Chromecast features

## 3.7.1 (2022-08-27)

 - (Android) New layout, features and some fixes #106 by Manuel García Marín (https://github.com/PhantomPainX)


## 3.7.0 (2022-08-23)

### Chores

- Update to Capacitor 3.7.0

### Add Features

 - Headers support for iOS and Android #101

## 3.4.2 (2022-04-12)

### Bug Fixes

 - Fix Notification center ConcurrentModificationException issue#86


## 3.4.2-2 (2022-03-31)

### Bug Fixes

 - Fix error when trying to create a product archive in Xcode issue#85

## 3.4.2-1 (2022-03-26)

### Chores

- Update to Capacitor 3.4.2

### Bug Fixes

 - Fix application crashes sometimes on stop issue#84 Android

## 3.4.1 (2022-03-12)

### Add Features

 - Add `bkmodeEnabled` boolean parameter in `capVideoPlayerOptions` to enable/disable BackgroundMode (iOS, Android only)

## 3.4.1-3 (2022-03-08)

### Bug Fixes

 - Fix .setCategory(.playback  iOS

## 3.4.1-2 (2022-03-08)

### Bug Fixes

 - Fix issue#78 iOS
 - Fix issue#79 Android
 - Fix issue#80 Android

## 3.4.1-1 (2022-03-02)

### Chores

- Update to Capacitor 3.4.1

### Add Features

 - Add `pipEnable` boolean parameter in `capVideoPlayerOptions` (iOS, Android)

### Bug Fixes

 - Fix audio continue to play when tap on X in PIP window (Android)

## 3.4.0 (2022-03-01)

### Bug Fixes

- Fix On Android, apps exit if you play a video, pause, and then resume the app issue#77

## 3.4.0-4 (2022-02-13)

### Add Features

 - Add `loopOnEnd` boolean parameter in `capVideoPlayerOptions` (iOS, Android)

### Bug Fixes

- Fix feat: move loopOnEnd to initPlayer issue#76

## 3.4.0-3 (2022-02-07)

### Add Features

 - Add `exitOnEnd` boolean parameter in `capVideoPlayerOptions` (iOS, Android)

### Bug Fixes

- Fix feat: move exitOnEnd to initPlayer issue#74

## 3.4.0-2 (2022-02-05)

### Add Features

- Add methods `getRate` and `setRate(rate)` with rate in [0.25,0.5,0.75,1.0,2.0,4.0] otherwise 1.0

### Bug Fixes

- Fix feature request > speed control issue#67

## 3.4.0-1 (2022-01-26)

### Chores

- Update to Capacitor 3.4.0

## Bug Fixes

- fix dynamic id in web.ts
- update README


## 3.3.2 (2021-12-11)

### Chores

- Update to Capacitor 3.3.2

### Bug Fixes

- fix from PiP to Fullscreen iOS platform

## 3.3.1 (2021-12-11)

### Add Features

- Add Picture in Picture iOS platform
- Add link to a `vite-react-videoplayer-app` 

### Bug Fixes

- fix Controls position for Picture in Picture Android
- fix setVolume as Float Android

## 3.3.1-2 (2021-11-17)

### Bug Fixes

- iOS fix DCIM folder as URL-Path issue#56
- iOS fix Player does not play video on iOS with "filePath not implemented" message issue#61

## 3.3.1-1 (2021-11-13)

### Chores

- Update to Capacitor 3.3.1

### Add Features

- Add Picture in Picture Android platform


## 3.3.0-1 (2021-11-08)

### Bug Fixes

- Android fix DCIM folder as URL-Path issue#56

## 3.2.5 (2021-11-06)

### Chores

- Update to Capacitor 3.2.5

### Bug Fixes

- fix Play from folders inside Application Folder issue#55

## 3.2.0-2 (2021-08-30)

### Bug Fixes

- fix package.json version

## 3.2.0-1 (2021-08-30)

### Chores

- Update to Capacitor 3.2.0

## 3.0.0-rc.2 (2021-06-21)

### Chores

- Update to Capacitor 3.0.0

### Bug Fixes

- fix Android subtitle issue#46

## 3.0.0-rc.1 (2021-06-01)

### Added Features

- Android platform

## 3.0.0-alpha.1 (2021-05-31)

### Chores

- Update to Capacitor 3.0.0

### Added Features

- iOS, Web, Electron platforms


## 2.4.7 (2021-05-31)

- Final version with Capacitor 2.4.7

### Bug Fixes

- fix Add test script in package.json

## 2.4.7-2 (2021-04-03)

### Added Features

- Add SubTitle to iOS platform issue#40
- Add change to foreground & background colors as well as font size

## 2.4.7-1 (2021-03-30)

### Chores

- Update to Capacitor 2.4.7

### Added Features

- Add SubTitle to Android platform issue#40

## 2.4.5-2 (2021-01-09)

### Bug Fixes

- fix Add Back Button issue#35 on Android

## 2.4.5-1 (2021-01-09)

### Chores

- Update to Capacitor 2.4.5
- @capacitor-community/electron 1.3.2

### Bug Fixes

- fix Screen falling asleep issue#33 on Android

## 2.4.2-2 (2020-10-04)

### Added Features

- add docgen to generate the API documentation

### Bug Fixes

- cleanup divContainer element as input of initPlayer

## 2.4.2-1 (2020-09-25)

### Chores

- Update to Capacitor 2.4.2

## 2.3.1-3 (2020-09-22)

### Bug Fixes

- fix CapacitorVideoPlayer.podspec
- fix return data in handlePlayerExit (Web)
- remove the wasPaused variable (Android)
- nullify the player in playerFullscreenExit (iOS)

## 2.3.1-2 (2020-09-16)

### Added Features

- add a link to a Ionic/React app starter

### Bug Fixes

- fix bugs to make it compatible with Ionic/React

## 2.3.1-1 (2020-08-27)

### Bug Fixes

- fix issue#23 iOS & Android video from app asset video folder

## 2.3.1-0 (2020-08-26)

### Bug Fixes

- fix issue#18 check for videoPlayFullScreenView nil

## 2.3.0 (2020-08-26)

### Added Features

- add read video from internal application folder on iOS
- add read video from internal application folder on Android

## 2.3.0-beta.4 (2020-08-20)

### Bug Fixes

- fix issue#20 Exit player when in PIP mode
- fix issue#19 Play video from local storage by giving the **url = "internal"**

## 2.3.0-beta.3 (2020-08-09)

### Bug Fixes

- fix issue#17 showing the status bar after fullscreen mode

## 2.3.0-beta.2 (2020-08-07)

### Bug Fixes

- fix issue#18 play video with url parameters
- fix issue#17 create programmatically the FrameLayout for the fragment

## 2.3.0-beta.1 (2020-08-06)

### Chores

- Update to Capacitor 2.1.0

### Bug Fixes

- fix Lint and SwiftLint issues

## 2.1.0 (2020-06-15)

### Added Features

- add interactive apis for native players (issue#15)
- remove Web Plugin Events
- add plugin listeners for Web and Native Plugins

## 2.1.0-3 (2020-05-28)

### Bug Fixes

- fix Android ism type not playing

## 2.1.0-2 (2020-05-28)

### Bug Fixes

- fix issue#14 url without type

## 2.1.0-1

### Chores

- Update to Capacitor 2.1.0

### Added Features

- issue#13 Background Video support for ios

## 2.0.1 (2020-05-08)

### Added Features

- Add support for DASH, HLS, ISM videos for Android plugin by using the ExoPlayer (issue#10)

## 2.0.1-6 (2020-05-06)

### Bug Fixes

- fix README

## 2.0.1-5 (2020-05-06)

### Added Features

- Add support for Hls video for Web & IOS plugin (issue#10)

## 2.0.1-4 (2020-04-30)

### Bug Fixes

- fix issue#12 add stopAllPlayers() method

## 2.0.1-3 (2020-04-30)

### Bug Fixes

- fix issue#11 test-angular-jeep-capacitor-plugins link in readme

## 2.0.1-2(2020-04-23)

### Added Features

- add a Progress Bar in Android Plugin

## 2.0.1-1(2020-04-19)

### Chores

- Update to Capacitor 2.0.1
- Update to AndroidX

## 1.5.1 (2020-03-17)

### Added Features

- Undeprecating the npm package to allow user to load only this capacitor plugin in there applications (advise by the Ionic Capacitor team)
