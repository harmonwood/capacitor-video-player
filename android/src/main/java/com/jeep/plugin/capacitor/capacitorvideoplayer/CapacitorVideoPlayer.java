package com.jeep.plugin.capacitor.capacitorvideoplayer;

import android.content.Context;
import com.getcapacitor.JSObject;
import com.jeep.plugin.capacitor.capacitorvideoplayer.PickerVideo.PickerVideoFragment;

public class CapacitorVideoPlayer {

    private final Context context;

    CapacitorVideoPlayer(Context context) {
        this.context = context;
    }

    public String echo(String value) {
        return value;
    }

    public FullscreenExoPlayerFragment createFullScreenFragment(
        String videoPath,
        Float videoRate,
        Boolean exitOnEnd,
        Boolean loopOnEnd,
        Boolean pipEnabled,
        String subTitle,
        String language,
        JSObject subTitleOptions,
        Boolean isTV,
        String playerId,
        Boolean isInternal,
        Long videoId
    ) {
        FullscreenExoPlayerFragment fsFragment = new FullscreenExoPlayerFragment();

        fsFragment.videoPath = videoPath;
        fsFragment.videoRate = videoRate;
        fsFragment.exitOnEnd = exitOnEnd;
        fsFragment.loopOnEnd = loopOnEnd;
        fsFragment.pipEnabled = pipEnabled;
        fsFragment.subTitle = subTitle;
        fsFragment.language = language;
        fsFragment.subTitleOptions = subTitleOptions;
        fsFragment.isTV = isTV;
        fsFragment.playerId = playerId;
        fsFragment.isInternal = isInternal;
        fsFragment.videoId = videoId;
        return fsFragment;
    }

    public PickerVideoFragment createPickerVideoFragment() {
        return new PickerVideoFragment();
    }
}
