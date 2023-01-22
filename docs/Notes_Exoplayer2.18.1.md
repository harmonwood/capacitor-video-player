# Notes from PhantomPainX on using v2.18.1

PR Exoplayer v2.18.1 and some updates - fixes #114

## Updated dependencies
 - build.gradle (app)

```java
dependencies {
    ...
    implementation 'com.google.android.gms:play-services-cast-framework:21.2.0'
}
```

 - build.gradle (capacitor-video-player app)

```java
dependencies {
    ...
    implementation 'com.google.android.exoplayer:exoplayer-core:2.18.1'
    implementation 'com.google.android.exoplayer:exoplayer-ui:2.18.1'
    implementation 'com.google.android.exoplayer:exoplayer-hls:2.18.1'
    implementation 'com.google.android.exoplayer:exoplayer-dash:2.18.1'
    implementation 'com.google.android.exoplayer:exoplayer-smoothstreaming:2.18.1'
    implementation 'com.google.android.exoplayer:extension-mediasession:2.18.1'
    implementation 'com.google.android.exoplayer:exoplayer:2.18.1'
    implementation 'com.google.android.exoplayer:extension-cast:2.18.1'
    ...
}
```

## Design and UI fixes
Previously the player used a totally custom UI design. When I updated to exoplayer v2.18.1 I decided to use the default one with some additions: chromecast, pip and resize buttons, titles and back button.

The default design comes with some automatic options like subtitle button, speed and audio selection:

PlayerView was changed to StyledPlayerView because of that
Buttons has now proper DP size.
Fade effect when UI hides/show.

![IMG_20230104_155413](https://user-images.githubusercontent.com/47803967/212520565-2f1ca8f5-5f04-4b91-94e0-3073f56a65a1.jpg)

## Chromecast mode
Previously when using this mode, the player would only show the controls (not stay on screen) with a black background and a message alerting about the cast. This was improved adding an optional param to the initialization called "artwork", this image will be shown in the player, media notification panel and lock screen when casting.

- The artwork must be a remote URL

## Chromecast issues
The MediaRouteButton (cast button)

It will hide when you enter in PIP or BK mode. The button won't show up unless you restart the app. This only happens in Android 13 (some users of my app reported me this issue and I confirmed it with an OnePlus 8T)

As this cast thing can only be tested with real devices, I was only able to test it in my other phone (Samsung Galaxy S9+) with Android 10 and this error didn't happen, so I don't know if this happens in Android 11 and 12.

The error is related with addCastStateListener. It fires state 1 (NO_DEVICES_AVAILABLE) when entering in PIP mode or BKMode and never updates the state again.

Subtitles are not available yet
I tried so hard to get the subtitles working when casting a video but it simple didn't work, sorry for that.

## Why not v2.18.2
It needs a compileSdkVersion of at least 33