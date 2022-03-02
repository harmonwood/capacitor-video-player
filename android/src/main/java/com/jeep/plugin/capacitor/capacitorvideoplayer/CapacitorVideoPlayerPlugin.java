package com.jeep.plugin.capacitor.capacitorvideoplayer;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.getcapacitor.Bridge;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.jeep.plugin.capacitor.capacitorvideoplayer.Notifications.MyRunnable;
import com.jeep.plugin.capacitor.capacitorvideoplayer.Notifications.NotificationCenter;
import com.jeep.plugin.capacitor.capacitorvideoplayer.PickerVideo.PickerVideoFragment;
import com.jeep.plugin.capacitor.capacitorvideoplayer.Utilities.FilesUtils;
import com.jeep.plugin.capacitor.capacitorvideoplayer.Utilities.FragmentUtils;
import java.util.HashMap;
import java.util.Map;

@CapacitorPlugin(name = "CapacitorVideoPlayer")
public class CapacitorVideoPlayerPlugin extends Plugin {

    private CapacitorVideoPlayer implementation;
    private static final String TAG = "CapacitorVideoPlayer";
    private final int frameLayoutViewId = 256;
    private final int pickerLayoutViewId = 257;

    private Context context;
    private String videoPath;
    private String subTitlePath;
    private Boolean isTV;
    private String fsPlayerId;
    private String mode;
    private Boolean exitOnEnd = true;
    private Boolean loopOnEnd = false;
    private Boolean pipEnabled = true;
    private FullscreenExoPlayerFragment fsFragment;
    private PickerVideoFragment pkFragment;
    private FilesUtils filesUtils;
    private FragmentUtils fragmentUtils;
    private PluginCall call;
    private Float rateList[] = { 0.25f, 0.5f, 0.75f, 1f, 2f, 4f };
    private Float videoRate = 1f;

    @Override
    public void load() {
        // Get context
        this.context = getContext();
        implementation = new CapacitorVideoPlayer(this.context);
        this.filesUtils = new FilesUtils(this.context);
        this.fragmentUtils = new FragmentUtils(getBridge());
    }

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

    @PluginMethod
    public void initPlayer(PluginCall call) {
        this.call = call;
        final JSObject ret = new JSObject();
        ret.put("method", "initPlayer");
        ret.put("result", false);
        // Check if running on a TV Device
        isTV = isDeviceTV(context);
        Log.d(TAG, "**** isTV " + isTV + " ****");
        String _mode = call.getString("mode");
        if (_mode == null) {
            ret.put("message", "Must provide a Mode (fullscreen/embedded)");
            call.resolve(ret);
            return;
        }
        mode = _mode;
        String playerId = call.getString("playerId");
        if (playerId == null) {
            ret.put("message", "Must provide a PlayerId");
            call.resolve(ret);
            return;
        }
        videoRate = 1f;
        if (call.getData().has("rate")) {
            Float mRate = call.getFloat("rate");
            if (isInRate(rateList, mRate)) {
                videoRate = mRate;
            }
        }
        Boolean _exitOnEnd = true;
        if (call.getData().has("exitOnEnd")) {
            _exitOnEnd = call.getBoolean("exitOnEnd");
        }
        exitOnEnd = _exitOnEnd;
        Boolean _loopOnEnd = false;
        if (call.getData().has("loopOnEnd")) {
            _loopOnEnd = call.getBoolean("loopOnEnd");
        }
        if (!exitOnEnd) loopOnEnd = _loopOnEnd;
        Boolean _pipEnabled = true;
        if (call.getData().has("pipEnabled")) {
            _pipEnabled = call.getBoolean("pipEnabled");
        }
        pipEnabled = _pipEnabled;

        if ("fullscreen".equals(mode)) {
            fsPlayerId = playerId;
            String url = call.getString("url");
            if (url == null) {
                ret.put("message", "Must provide an url");
                call.resolve(ret);
                return;
            }
            String subtitle = "";
            if (call.getData().has("subtitle")) {
                subtitle = call.getString("subtitle");
            }
            String language = "";
            if (call.getData().has("language")) {
                language = call.getString("language");
            }
            JSObject subTitleOptions = new JSObject();
            if (call.getData().has("subtitleOptions")) {
                subTitleOptions = call.getObject("subtitleOptions");
            }

            AddObserversToNotificationCenter();
            Log.v(TAG, "display url: " + url);
            Log.v(TAG, "display subtitle: " + subtitle);
            Log.v(TAG, "display language: " + language);
            if (url.equals("internal")) {
                createPickerVideoFragment(call);
            } else {
                // get the videoPath
                videoPath = filesUtils.getFilePath(url);
                // get the subTitlePath if any
                if (subtitle != null && subtitle.length() > 0) {
                    subTitlePath = filesUtils.getFilePath(subtitle);
                } else {
                    subTitlePath = null;
                }
                Log.v(TAG, "*** calculated videoPath: " + videoPath);
                Log.v(TAG, "*** calculated subTitlePath: " + subTitlePath);
                if (videoPath != null) {
                    createFullScreenFragment(
                        call,
                        videoPath,
                        videoRate,
                        exitOnEnd,
                        loopOnEnd,
                        pipEnabled,
                        subTitlePath,
                        language,
                        subTitleOptions,
                        isTV,
                        playerId,
                        false,
                        null
                    );
                } else {
                    Map<String, Object> info = new HashMap<String, Object>() {
                        {
                            put("dismiss", "1");
                        }
                    };
                    NotificationCenter.defaultCenter().postNotification("playerFullscreenDismiss", info);
                    ret.put("message", "initPlayer command failed: Video file not found");
                    call.resolve(ret);
                    return;
                }
            }
        } else if ("embedded".equals(mode)) {
            ret.put("message", "Embedded Mode not implemented");
            call.resolve(ret);
            return;
        }
    }

    @PluginMethod
    public void isPlaying(final PluginCall call) {
        this.call = call;
        JSObject ret = new JSObject();
        ret.put("method", "isPlaying");
        String playerId = call.getString("playerId");
        if (playerId == null) {
            ret.put("result", false);
            ret.put("message", "Must provide a PlayerId");
            call.resolve(ret);
            return;
        }
        if ("fullscreen".equals(mode) && fsPlayerId.equals(playerId)) {
            bridge
                .getActivity()
                .runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            JSObject ret = new JSObject();
                            ret.put("method", "isPlaying");
                            if (fsFragment != null) {
                                boolean playing = fsFragment.isPlaying();
                                ret.put("result", true);
                                ret.put("value", playing);
                                call.resolve(ret);
                            } else {
                                ret.put("result", false);
                                ret.put("message", "Fullscreen fragment is not defined");
                                call.resolve(ret);
                            }
                        }
                    }
                );
        } else {
            ret.put("result", false);
            ret.put("message", "player is not defined");
            call.resolve(ret);
        }
    }

    @PluginMethod
    public void play(final PluginCall call) {
        this.call = call;
        JSObject ret = new JSObject();
        ret.put("method", "play");
        String playerId = call.getString("playerId");
        if (playerId == null) {
            ret.put("result", false);
            ret.put("message", "Must provide a PlayerId");
            call.resolve(ret);
            return;
        }
        if ("fullscreen".equals(mode) && fsPlayerId.equals(playerId)) {
            bridge
                .getActivity()
                .runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            JSObject ret = new JSObject();
                            ret.put("method", "play");
                            if (fsFragment != null) {
                                fsFragment.play();
                                boolean playing = fsFragment.isPlaying();
                                ret.put("result", true);
                                ret.put("value", true);
                                call.resolve(ret);
                            } else {
                                ret.put("result", false);
                                ret.put("message", "Fullscreen fragment is not defined");
                                call.resolve(ret);
                            }
                        }
                    }
                );
        } else {
            ret.put("result", false);
            ret.put("message", "player is not defined");
            call.resolve(ret);
        }
    }

    @PluginMethod
    public void pause(final PluginCall call) {
        this.call = call;
        JSObject ret = new JSObject();
        ret.put("method", "pause");
        String playerId = call.getString("playerId");
        if (playerId == null) {
            ret.put("result", false);
            ret.put("message", "Must provide a PlayerId");
            call.resolve(ret);
            return;
        }
        if ("fullscreen".equals(mode) && fsPlayerId.equals(playerId)) {
            bridge
                .getActivity()
                .runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            JSObject ret = new JSObject();
                            ret.put("method", "pause");
                            if (fsFragment != null) {
                                fsFragment.pause();
                                ret.put("result", true);
                                ret.put("value", true);
                                call.resolve(ret);
                            } else {
                                ret.put("result", false);
                                ret.put("message", "Fullscreen fragment is not defined");
                                call.resolve(ret);
                            }
                        }
                    }
                );
        } else {
            ret.put("result", false);
            ret.put("message", "player is not defined");
            call.resolve(ret);
        }
    }

    @PluginMethod
    public void getDuration(final PluginCall call) {
        this.call = call;
        JSObject ret = new JSObject();
        ret.put("method", "getDuration");
        String playerId = call.getString("playerId");
        if (playerId == null) {
            ret.put("result", false);
            ret.put("message", "Must provide a PlayerId");
            call.resolve(ret);
            return;
        }
        if ("fullscreen".equals(mode) && fsPlayerId.equals(playerId)) {
            bridge
                .getActivity()
                .runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            JSObject ret = new JSObject();
                            ret.put("method", "getDuration");
                            if (fsFragment != null) {
                                int duration = fsFragment.getDuration();
                                ret.put("result", true);
                                ret.put("value", duration);
                                call.resolve(ret);
                            } else {
                                ret.put("result", false);
                                ret.put("message", "Fullscreen fragment is not defined");
                                call.resolve(ret);
                            }
                        }
                    }
                );
        } else {
            ret.put("result", false);
            ret.put("message", "player is not defined");
            call.resolve(ret);
        }
    }

    @PluginMethod
    public void getCurrentTime(final PluginCall call) {
        this.call = call;
        JSObject ret = new JSObject();
        ret.put("method", "getCurrentTime");
        String playerId = call.getString("playerId");
        if (playerId == null) {
            ret.put("result", false);
            ret.put("message", "Must provide a PlayerId");
            call.resolve(ret);
            return;
        }
        if ("fullscreen".equals(mode) && fsPlayerId.equals(playerId)) {
            bridge
                .getActivity()
                .runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            JSObject ret = new JSObject();
                            ret.put("method", "getCurrentTime");
                            if (fsFragment != null) {
                                int curTime = fsFragment.getCurrentTime();
                                ret.put("result", true);
                                ret.put("value", curTime);
                                call.resolve(ret);
                            } else {
                                ret.put("result", false);
                                ret.put("message", "Fullscreen fragment is not defined");
                                call.resolve(ret);
                            }
                        }
                    }
                );
        } else {
            ret.put("result", false);
            ret.put("message", "player is not defined");
            call.resolve(ret);
        }
    }

    @PluginMethod
    public void setCurrentTime(final PluginCall call) {
        this.call = call;
        JSObject ret = new JSObject();
        ret.put("method", "setCurrentTime");
        String playerId = call.getString("playerId");
        if (playerId == null) {
            ret.put("result", false);
            ret.put("message", "Must provide a PlayerId");
            call.resolve(ret);
            return;
        }
        Double value = call.getDouble("seektime");
        if (value == null) {
            ret.put("result", false);
            ret.put("message", "Must provide a time in second");
            call.resolve(ret);
            return;
        }
        final int cTime = (int) Math.round(value);
        if ("fullscreen".equals(mode) && fsPlayerId.equals(playerId)) {
            bridge
                .getActivity()
                .runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            JSObject ret = new JSObject();
                            ret.put("method", "setCurrentTime");
                            if (fsFragment != null) {
                                fsFragment.setCurrentTime(cTime);
                                ret.put("result", true);
                                ret.put("value", cTime);
                                call.resolve(ret);
                            } else {
                                ret.put("result", false);
                                ret.put("message", "Fullscreen fragment is not defined");
                                call.resolve(ret);
                            }
                        }
                    }
                );
        } else {
            ret.put("result", false);
            ret.put("message", "player is not defined");
            call.resolve(ret);
        }
    }

    @PluginMethod
    public void getVolume(final PluginCall call) {
        this.call = call;
        JSObject ret = new JSObject();
        ret.put("method", "getVolume");
        String playerId = call.getString("playerId");
        if (playerId == null) {
            ret.put("result", false);
            ret.put("message", "Must provide a PlayerId");
            call.resolve(ret);
            return;
        }
        if ("fullscreen".equals(mode) && fsPlayerId.equals(playerId)) {
            bridge
                .getActivity()
                .runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            JSObject ret = new JSObject();
                            ret.put("method", "getVolume");
                            if (fsFragment != null) {
                                Float volume = fsFragment.getVolume();
                                ret.put("result", true);
                                ret.put("value", volume);
                                call.resolve(ret);
                            } else {
                                ret.put("result", false);
                                ret.put("message", "Fullscreen fragment is not defined");
                                call.resolve(ret);
                            }
                        }
                    }
                );
        } else {
            ret.put("result", false);
            ret.put("message", "player is not defined");
            call.resolve(ret);
        }
    }

    @PluginMethod
    public void setVolume(final PluginCall call) {
        this.call = call;
        JSObject ret = new JSObject();
        ret.put("method", "setVolume");
        String playerId = call.getString("playerId");
        if (playerId == null) {
            ret.put("result", false);
            ret.put("message", "Must provide a PlayerId");
            call.resolve(ret);
            return;
        }
        Float volume = call.getFloat("volume");
        if (volume == null) {
            ret.put("result", false);
            ret.put("method", "setVolume");
            ret.put("message", "Must provide a volume value");
            call.resolve(ret);
            return;
        }
        if ("fullscreen".equals(mode) && fsPlayerId.equals(playerId)) {
            bridge
                .getActivity()
                .runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            JSObject ret = new JSObject();
                            ret.put("method", "setVolume");
                            if (fsFragment != null) {
                                fsFragment.setVolume(volume);
                                ret.put("result", true);
                                ret.put("value", volume);
                                call.resolve(ret);
                            } else {
                                ret.put("result", false);
                                ret.put("message", "Fullscreen fragment is not defined");
                                call.resolve(ret);
                            }
                        }
                    }
                );
        } else {
            ret.put("result", false);
            ret.put("message", "player is not defined");
            call.resolve(ret);
        }
    }

    @PluginMethod
    public void getMuted(final PluginCall call) {
        this.call = call;
        JSObject ret = new JSObject();
        ret.put("method", "getMuted");
        String playerId = call.getString("playerId");
        if (playerId == null) {
            ret.put("result", false);
            ret.put("message", "Must provide a PlayerId");
            call.resolve(ret);
            return;
        }
        if ("fullscreen".equals(mode) && fsPlayerId.equals(playerId)) {
            bridge
                .getActivity()
                .runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            JSObject ret = new JSObject();
                            ret.put("method", "getMuted");
                            if (fsFragment != null) {
                                boolean value = fsFragment.getMuted();
                                ret.put("result", true);
                                ret.put("value", value);
                                call.resolve(ret);
                            } else {
                                ret.put("result", false);
                                ret.put("message", "Fullscreen fragment is not defined");
                                call.resolve(ret);
                            }
                        }
                    }
                );
        } else {
            ret.put("result", false);
            ret.put("message", "player is not defined");
            call.resolve(ret);
        }
    }

    @PluginMethod
    public void setMuted(final PluginCall call) {
        this.call = call;
        JSObject ret = new JSObject();
        ret.put("method", "setMuted");
        String playerId = call.getString("playerId");
        if (playerId == null) {
            ret.put("result", false);
            ret.put("message", "Must provide a PlayerId");
            call.resolve(ret);
            return;
        }
        Boolean value = call.getBoolean("muted");
        if (value == null) {
            ret.put("result", true);
            ret.put("message", "Must provide a boolean true/false");
            call.resolve(ret);
            return;
        }
        final boolean bValue = value;
        if ("fullscreen".equals(mode) && fsPlayerId.equals(playerId)) {
            bridge
                .getActivity()
                .runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            JSObject ret = new JSObject();
                            ret.put("method", "setMuted");
                            if (fsFragment != null) {
                                fsFragment.setMuted(bValue);
                                ret.put("result", true);
                                ret.put("value", bValue);
                                call.resolve(ret);
                            } else {
                                ret.put("result", false);
                                ret.put("message", "Fullscreen fragment is not defined");
                                call.resolve(ret);
                            }
                        }
                    }
                );
        } else {
            ret.put("result", false);
            ret.put("message", "player is not defined");
            call.resolve(ret);
        }
    }

    @PluginMethod
    public void getRate(final PluginCall call) {
        this.call = call;
        JSObject ret = new JSObject();
        ret.put("method", "getRate");
        String playerId = call.getString("playerId");
        if (playerId == null) {
            ret.put("result", false);
            ret.put("message", "Must provide a PlayerId");
            call.resolve(ret);
            return;
        }
        if ("fullscreen".equals(mode) && fsPlayerId.equals(playerId)) {
            bridge
                .getActivity()
                .runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            JSObject ret = new JSObject();
                            ret.put("method", "getRate");
                            if (fsFragment != null) {
                                Float rate = fsFragment.getRate();
                                ret.put("result", true);
                                ret.put("value", rate);
                                call.resolve(ret);
                            } else {
                                ret.put("result", false);
                                ret.put("message", "Fullscreen fragment is not defined");
                                call.resolve(ret);
                            }
                        }
                    }
                );
        } else {
            ret.put("result", false);
            ret.put("message", "player is not defined");
            call.resolve(ret);
        }
    }

    @PluginMethod
    public void setRate(final PluginCall call) {
        this.call = call;
        JSObject ret = new JSObject();
        ret.put("method", "setRate");
        String playerId = call.getString("playerId");
        if (playerId == null) {
            ret.put("result", false);
            ret.put("message", "Must provide a PlayerId");
            call.resolve(ret);
            return;
        }
        Float rate = call.getFloat("rate");
        if (rate == null) {
            ret.put("result", false);
            ret.put("method", "setRate");
            ret.put("message", "Must provide a volume value");
            call.resolve(ret);
            return;
        }
        if (isInRate(rateList, rate)) {
            videoRate = rate;
        } else {
            videoRate = 1f;
        }
        if ("fullscreen".equals(mode) && fsPlayerId.equals(playerId)) {
            bridge
                .getActivity()
                .runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            JSObject ret = new JSObject();
                            ret.put("method", "setRate");
                            if (fsFragment != null) {
                                fsFragment.setRate(videoRate);
                                ret.put("result", true);
                                ret.put("value", videoRate);
                                call.resolve(ret);
                            } else {
                                ret.put("result", false);
                                ret.put("message", "Fullscreen fragment is not defined");
                                call.resolve(ret);
                            }
                        }
                    }
                );
        } else {
            ret.put("result", false);
            ret.put("message", "player is not defined");
            call.resolve(ret);
        }
    }

    @PluginMethod
    public void stopAllPlayers(PluginCall call) {
        this.call = call;
        bridge
            .getActivity()
            .runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        JSObject ret = new JSObject();
                        ret.put("method", "stopAllPlayers");
                        if (fsFragment != null) {
                            fsFragment.pause();
                            ret.put("result", true);
                            ret.put("value", true);
                            call.resolve(ret);
                        } else {
                            ret.put("result", false);
                            ret.put("message", "Fullscreen fragment is not defined");
                            call.resolve(ret);
                        }
                    }
                }
            );
    }

    public boolean isDeviceTV(Context context) {
        //Since Android TV is only API 21+ that is the only time we will compare configurations
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UiModeManager uiManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
            return uiManager != null && uiManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION;
        }
        return false;
    }

    private Boolean isInRate(Float arr[], Float rate) {
        Boolean ret = false;
        for (Float el : arr) {
            if (el.equals(rate)) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    private void AddObserversToNotificationCenter() {
        NotificationCenter
            .defaultCenter()
            .addMethodForNotification(
                "playerItemPlay",
                new MyRunnable() {
                    @Override
                    public void run() {
                        JSObject data = new JSObject();
                        data.put("fromPlayerId", this.getInfo().get("fromPlayerId"));
                        data.put("currentTime", this.getInfo().get("currentTime"));
                        notifyListeners("jeepCapVideoPlayerPlay", data);
                        return;
                    }
                }
            );
        NotificationCenter
            .defaultCenter()
            .addMethodForNotification(
                "playerItemPause",
                new MyRunnable() {
                    @Override
                    public void run() {
                        JSObject data = new JSObject();
                        data.put("fromPlayerId", this.getInfo().get("fromPlayerId"));
                        data.put("currentTime", this.getInfo().get("currentTime"));
                        notifyListeners("jeepCapVideoPlayerPause", data);
                        return;
                    }
                }
            );
        NotificationCenter
            .defaultCenter()
            .addMethodForNotification(
                "playerItemReady",
                new MyRunnable() {
                    @Override
                    public void run() {
                        JSObject data = new JSObject();
                        data.put("fromPlayerId", this.getInfo().get("fromPlayerId"));
                        data.put("currentTime", this.getInfo().get("currentTime"));
                        notifyListeners("jeepCapVideoPlayerReady", data);
                        return;
                    }
                }
            );
        NotificationCenter
            .defaultCenter()
            .addMethodForNotification(
                "playerItemEnd",
                new MyRunnable() {
                    @Override
                    public void run() {
                        final JSObject data = new JSObject();
                        data.put("fromPlayerId", this.getInfo().get("fromPlayerId"));
                        data.put("currentTime", this.getInfo().get("currentTime"));
                        bridge
                            .getActivity()
                            .runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        FrameLayout frameLayoutView = getBridge().getActivity().findViewById(frameLayoutViewId);

                                        if (frameLayoutView != null) {
                                            ((ViewGroup) getBridge().getWebView().getParent()).removeView(frameLayoutView);
                                            fragmentUtils.removeFragment(fsFragment);
                                        }
                                        fsFragment = null;
                                        NotificationCenter.defaultCenter().removeAllNotifications();
                                        notifyListeners("jeepCapVideoPlayerEnded", data);
                                    }
                                }
                            );
                    }
                }
            );
        NotificationCenter
            .defaultCenter()
            .addMethodForNotification(
                "playerFullscreenDismiss",
                new MyRunnable() {
                    @Override
                    public void run() {
                        boolean ret = false;
                        final JSObject data = new JSObject();
                        if (Integer.valueOf((String) this.getInfo().get("dismiss")) == 1) ret = true;
                        data.put("dismiss", ret);
                        bridge
                            .getActivity()
                            .runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        FrameLayout frameLayoutView = getBridge().getActivity().findViewById(frameLayoutViewId);

                                        if (frameLayoutView != null) {
                                            ((ViewGroup) getBridge().getWebView().getParent()).removeView(frameLayoutView);
                                            fragmentUtils.removeFragment(fsFragment);
                                        }
                                        fsFragment = null;
                                        NotificationCenter.defaultCenter().removeAllNotifications();
                                        notifyListeners("jeepCapVideoPlayerExit", data);
                                    }
                                }
                            );
                    }
                }
            );
        NotificationCenter
            .defaultCenter()
            .addMethodForNotification(
                "videoPathInternalReady",
                new MyRunnable() {
                    @Override
                    public void run() {
                        long videoId = (Long) this.getInfo().get("videoId");
                        // Get the previously saved call
                        FrameLayout pickerLayoutView = getBridge().getActivity().findViewById(pickerLayoutViewId);
                        if (pickerLayoutView != null) {
                            ((ViewGroup) getBridge().getWebView().getParent()).removeView(pickerLayoutView);
                            fragmentUtils.removeFragment(pkFragment);
                        }
                        pkFragment = null;
                        if (videoId != -1) {
                            createFullScreenFragment(
                                call,
                                videoPath,
                                videoRate,
                                exitOnEnd,
                                loopOnEnd,
                                pipEnabled,
                                null,
                                null,
                                null,
                                isTV,
                                fsPlayerId,
                                true,
                                videoId
                            );
                        } else {
                            Toast.makeText(context, "No Video files found ", Toast.LENGTH_SHORT).show();
                            Map<String, Object> info = new HashMap<String, Object>() {
                                {
                                    put("dismiss", "1");
                                }
                            };
                            NotificationCenter.defaultCenter().postNotification("playerFullscreenDismiss", info);
                        }
                    }
                }
            );
    }

    private void createFullScreenFragment(
        final PluginCall call,
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
        fsFragment =
            implementation.createFullScreenFragment(
                videoPath,
                videoRate,
                exitOnEnd,
                loopOnEnd,
                pipEnabled,
                subTitle,
                language,
                subTitleOptions,
                isTV,
                playerId,
                isInternal,
                videoId
            );
        bridge
            .getActivity()
            .runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        JSObject ret = new JSObject();
                        ret.put("method", "initPlayer");
                        FrameLayout frameLayoutView = getBridge().getActivity().findViewById(frameLayoutViewId);
                        if (frameLayoutView != null) {
                            ret.put("result", false);
                            ret.put("message", "FrameLayout for ExoPlayer already exists");
                        } else {
                            // Initialize a new FrameLayout as container for fragment
                            frameLayoutView = new FrameLayout(getActivity().getApplicationContext());
                            frameLayoutView.setId(frameLayoutViewId);
                            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT
                            );
                            // Apply the Layout Parameters to frameLayout
                            frameLayoutView.setLayoutParams(lp);

                            ((ViewGroup) getBridge().getWebView().getParent()).addView(frameLayoutView);
                            fragmentUtils.loadFragment(fsFragment, frameLayoutViewId);
                            ret.put("result", true);
                        }
                        call.resolve(ret);
                    }
                }
            );
    }

    private void createPickerVideoFragment(final PluginCall call) {
        pkFragment = implementation.createPickerVideoFragment();

        bridge
            .getActivity()
            .runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        JSObject ret = new JSObject();
                        ret.put("method", "initPlayer");
                        FrameLayout pickerLayoutView = getBridge().getActivity().findViewById(pickerLayoutViewId);
                        if (pickerLayoutView != null) {
                            ret.put("result", false);
                            ret.put("message", "FrameLayout for VideoPicker already exists");
                        } else {
                            // Initialize a new FrameLayout as container for fragment
                            pickerLayoutView = new FrameLayout(getActivity().getApplicationContext());
                            pickerLayoutView.setId(pickerLayoutViewId);
                            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT
                            );
                            // Apply the Layout Parameters to frameLayout
                            pickerLayoutView.setLayoutParams(lp);

                            ((ViewGroup) getBridge().getWebView().getParent()).addView(pickerLayoutView);
                            fragmentUtils.loadFragment(pkFragment, pickerLayoutViewId);
                            ret.put("result", true);
                        }
                        call.resolve(ret);
                    }
                }
            );
    }
}
