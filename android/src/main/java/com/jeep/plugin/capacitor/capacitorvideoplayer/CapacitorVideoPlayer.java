package com.jeep.plugin.capacitor.capacitorvideoplayer;

import android.content.Context;
import com.getcapacitor.JSObject;
import com.jeep.plugin.capacitor.capacitorvideoplayer.PickerVideo.PickerVideoFragment;

public class CapacitorVideoPlayer {

    private Context context;

    CapacitorVideoPlayer(Context context) {
        this.context = context;
    }

    public String echo(String value) {
        return value;
    }

    public FullscreenExoPlayerFragment createFullScreenFragment(
        String videoPath,
        String subTitle,
        String language,
        JSObject subTitleOptions,
        Boolean isTV,
        String playerId,
        Boolean isInternal,
        Long videoId,
        Boolean hideCloseButton
    ) {
        FullscreenExoPlayerFragment fsFragment = new FullscreenExoPlayerFragment();

        fsFragment.videoPath = videoPath;
        fsFragment.subTitle = subTitle;
        fsFragment.language = language;
        fsFragment.subTitleOptions = subTitleOptions;
        fsFragment.isTV = isTV;
        fsFragment.playerId = playerId;
        fsFragment.isInternal = isInternal;
        fsFragment.videoId = videoId;
        fsFragment.hideCloseButton = hideCloseButton;
        return fsFragment;
    }

    public PickerVideoFragment createPickerVideoFragment() {
        PickerVideoFragment pkFragment = new PickerVideoFragment();
        return pkFragment;
    }
}
