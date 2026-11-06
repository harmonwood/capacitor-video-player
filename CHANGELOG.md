## [8.0.0](https://github.com/e-picsa/capacitor-video-player/compare/capacitor-video-player-v7.2.3...capacitor-video-player-v8.0.0) (2026-05-17)


### ⚠ BREAKING CHANGES

* upgrade to Capacitor 8 ([#18](https://github.com/e-picsa/capacitor-video-player/issues/18))

### Features

* upgrade to Capacitor 8 ([#18](https://github.com/e-picsa/capacitor-video-player/issues/18)) ([7da0888](https://github.com/e-picsa/capacitor-video-player/commit/7da08885f3b2431054dd64c8d36a58f87f8ca545))

## [8.0.2](https://github.com/e-picsa/capacitor-video-player/compare/capacitor-video-player-v8.0.1...capacitor-video-player-v8.0.2) (2026-11-06)


### Bug Fixes

* **android:** guard orphaned fragment resume against null PlayerView ([#26](https://github.com/e-picsa/capacitor-video-player/issues/26)) ([8156951](https://github.com/e-picsa/capacitor-video-player/commit/81569513e2dd6b702d5cf57ef243d6b4d00b1b3d))

## [8.0.1](https://github.com/e-picsa/capacitor-video-player/compare/capacitor-video-player-v8.0.0...capacitor-video-player-v8.0.1) (2026-10-14)


### Bug Fixes

* **android:** resolve NPE in FullscreenExoPlayerFragment on activity recreation ([#23](https://github.com/e-picsa/capacitor-video-player/issues/23)) ([8bcf645](https://github.com/e-picsa/capacitor-video-player/commit/8bcf6456fdeb7231ca9d6626dd882e043d96fa48))

## [7.2.3](https://github.com/e-picsa/capacitor-video-player/compare/capacitor-video-player-v7.2.2...capacitor-video-player-v7.2.3) (2026-03-25)


### Bug Fixes

* landscape aspect ratio ([#16](https://github.com/e-picsa/capacitor-video-player/issues/16)) ([0b52204](https://github.com/e-picsa/capacitor-video-player/commit/0b522048d65171b76d96a7a8b9a334cc94c443f3))

## [7.2.2](https://github.com/e-picsa/capacitor-video-player/compare/capacitor-video-player-v7.2.1...capacitor-video-player-v7.2.2) (2026-03-13)


### Bug Fixes

* release permissions ([171e417](https://github.com/e-picsa/capacitor-video-player/commit/171e417ef1d7239db9eb520d4973f42c9c973054))

## [7.2.1](https://github.com/e-picsa/capacitor-video-player/compare/capacitor-video-player-v7.2.0...capacitor-video-player-v7.2.1) (2026-03-13)


### Miscellaneous Chores

* revert release-please version ([42583f1](https://github.com/e-picsa/capacitor-video-player/commit/42583f1e360063c53e445510082b63289c0895dd))
* tidying ([caf91e0](https://github.com/e-picsa/capacitor-video-player/commit/caf91e07d8b6c6412cff5428e2bb729b7bb9f6af))

## [7.2.0](https://github.com/e-picsa/capacitor-video-player/compare/capacitor-video-player-v7.1.0...capacitor-video-player-v7.2.0) (2026-03-13)


### Features

* add example testing app ([86d78ad](https://github.com/e-picsa/capacitor-video-player/commit/86d78adaa08a36594a9975ca317620b313fd44f0))
* add placeholder app platforms ([920c8b8](https://github.com/e-picsa/capacitor-video-player/commit/920c8b8ee3e4b898f9927453a79f10f0cefc9d1e))
* add placeholder example app ([8bb6b9d](https://github.com/e-picsa/capacitor-video-player/commit/8bb6b9dfebb8ff409dbe51c013b154f634c9dfd8))
* add support for capacitor 7 ([3266610](https://github.com/e-picsa/capacitor-video-player/commit/3266610177023d9beebd2d5693b14083d1ee4878))
* allow android player to accept absolut paths ([c62c70a](https://github.com/e-picsa/capacitor-video-player/commit/c62c70a60e400bd1c9aed18081865c6f0f41d2e6))
* allow android player to accept absoluth paths ([fb74b8e](https://github.com/e-picsa/capacitor-video-player/commit/fb74b8e36115f562f2e70a30b23101f8ec9bfc21))
* **ci:** publishing and release automation ([#7](https://github.com/e-picsa/capacitor-video-player/issues/7)) ([48d4174](https://github.com/e-picsa/capacitor-video-player/commit/48d4174155e4b1f0d4a582fff6264bbaebfdf2f3))


### Bug Fixes

* **#166:** run only first time fragment gets loaded ([61edc7d](https://github.com/e-picsa/capacitor-video-player/commit/61edc7d83828d51d0ef1af4f1c8df5071b0720bb))
* exoplayer fragment release ([ef24808](https://github.com/e-picsa/capacitor-video-player/commit/ef24808e6b11740d1b37dfc4c7aecb081108c671))
* exoplayer fragment release ([ae259da](https://github.com/e-picsa/capacitor-video-player/commit/ae259da081bd6c7c6041d855e0d0e69a37dd8ab1))
* NowPlayingInfo ([623ec83](https://github.com/e-picsa/capacitor-video-player/commit/623ec83bd5e46156c23fab0004aa93bfa2f89ac2))
* pip exit handler ([8f13423](https://github.com/e-picsa/capacitor-video-player/commit/8f13423a495d9dbde552ffcc9e806b40a723c376))
* prevent NPE when calling is paused before init ([d4edf3d](https://github.com/e-picsa/capacitor-video-player/commit/d4edf3dd069b385c454b2ff5a9eac58e8eba30fc))
* prevent NPE when calling is paused before init ([984d330](https://github.com/e-picsa/capacitor-video-player/commit/984d330b7276236cbbbaf5a64685ebe780c2947d))
* web regex ([b7d1bee](https://github.com/e-picsa/capacitor-video-player/commit/b7d1bee536f96b226eb6e821cbf5fe9440a75f87))
* web regex ([6c8e93a](https://github.com/e-picsa/capacitor-video-player/commit/6c8e93a8c07e3166bbb924f964151225b00aec1a))
* work on gemini comments ([063b095](https://github.com/e-picsa/capacitor-video-player/commit/063b095e085fafe14996ffbae81b970d53bcfec0))

## Legacy Changelog

See [harmonwood/capacitor-video-player](https://github.com/harmonwood/capacitor-video-player/blob/main/CHANGELOG.md)
