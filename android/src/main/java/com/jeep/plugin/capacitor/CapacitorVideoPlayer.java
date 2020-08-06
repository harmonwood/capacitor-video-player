package com.jeep.plugin.capacitor;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.jeep.plugin.capacitor.capacitorvideoplayer.R;

@NativePlugin(requestCodes = { CapacitorVideoPlayer.RequestCodes.Video })
public class CapacitorVideoPlayer extends Plugin {
    private static final String TAG = "CapacitorVideoPlayer";
    private Context context;
    private String videoPath;
    private Boolean isTV;
    private String fsPlayerId;
    private String mode;
    private FullscreenExoPlayerFragment fsFragment;

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", value);
        call.success(ret);
    }

    @PluginMethod
    public void initPlayer(PluginCall call) {
        saveCall(call);
        context = getContext();
        JSObject ret = new JSObject();
        ret.put("method", "initPlayer");
        ret.put("result", false);
        // Check if running on a TV Device
        isTV = isDeviceTV(context);
        Log.d(TAG, "**** isTV " + isTV + " ****");

        String _mode = call.getString("mode");
        if (_mode == null) {
            ret.put("message", "Must provide a Mode (fullscreen/embedded)");
            call.success(ret);
            return;
        }
        mode = _mode;
        String playerId = call.getString("playerId");
        if (playerId == null) {
            ret.put("message", "Must provide a PlayerId");
            call.success(ret);
            return;
        }
        if (mode.equals("fullscreen")) {
            fsPlayerId = playerId;
            String url = call.getString("url");
            if (url == null) {
                ret.put("message", "Must provide an url");
                call.success(ret);
                return;
            }
            Log.v(TAG, "display url: " + url);
            String http = url.substring(0, 4);
            if (http.equals("http")) {
                videoPath = url;
            } else {
                videoPath = "android.resource://" + context.getPackageName() + "/" + url; // works
                //            url = "content://"+appPath+ "/" + url ;
                Log.v(TAG, "calculated url: " + url);
            }
        } else if (mode == "embedded") {
            ret.put("message", "Embedded Mode not implemented");
            call.success(ret);
            return;
        }
        Log.v(TAG, "videoPath: " + videoPath);
        AddObserversToNotificationCenter();
        fsFragment = new FullscreenExoPlayerFragment();

        fsFragment.videoPath = videoPath;
        fsFragment.isTV = false;
        fsFragment.playerId = playerId;

        bridge
            .getActivity()
            .runOnUiThread(
                new Runnable() {

                    @Override
                    public void run() {
                        loadFragment(fsFragment);
                    }
                }
            );
    }

    @PluginMethod
    public void isPlaying(final PluginCall call) {
        saveCall(call);
        JSObject ret = new JSObject();
        ret.put("method", "isPlaying");
        String playerId = call.getString("playerId");
        if (playerId == null) {
            ret.put("result", false);
            ret.put("message", "Must provide a PlayerId");
            call.success(ret);
            return;
        }
        if (mode.equals("fullscreen") && fsPlayerId.equals(playerId)) {
            bridge
                .getActivity()
                .runOnUiThread(
                    new Runnable() {

                        @Override
                        public void run() {
                            boolean playing = fsFragment.isPlaying();
                            JSObject data = new JSObject();
                            data.put("result", true);
                            data.put("method", "isPlaying");
                            data.put("value", playing);
                            call.success(data);
                        }
                    }
                );
        } else {
            ret.put("result", false);
            ret.put("message", "player is not defined");
            call.success(ret);
        }
    }

    @PluginMethod
    public void play(final PluginCall call) {
        saveCall(call);
        JSObject ret = new JSObject();
        ret.put("method", "play");
        String playerId = call.getString("playerId");
        if (playerId == null) {
            ret.put("result", false);
            ret.put("message", "Must provide a PlayerId");
            call.success(ret);
            return;
        }
        if (mode.equals("fullscreen") && fsPlayerId.equals(playerId)) {
            bridge
                .getActivity()
                .runOnUiThread(
                    new Runnable() {

                        @Override
                        public void run() {
                            fsFragment.play();
                            boolean playing = fsFragment.isPlaying();
                            JSObject data = new JSObject();
                            data.put("result", true);
                            data.put("method", "play");
                            data.put("value", true);
                            call.success(data);
                        }
                    }
                );
        } else {
            ret.put("result", false);
            ret.put("message", "player is not defined");
            call.success(ret);
        }
    }

    @PluginMethod
    public void pause(final PluginCall call) {
        saveCall(call);
        JSObject ret = new JSObject();
        ret.put("method", "pause");
        String playerId = call.getString("playerId");
        if (playerId == null) {
            ret.put("result", false);
            ret.put("message", "Must provide a PlayerId");
            call.success(ret);
            return;
        }
        if (mode.equals("fullscreen") && fsPlayerId.equals(playerId)) {
            bridge
                .getActivity()
                .runOnUiThread(
                    new Runnable() {

                        @Override
                        public void run() {
                            fsFragment.pause();
                            JSObject data = new JSObject();
                            data.put("result", true);
                            data.put("method", "pause");
                            data.put("value", true);
                            call.success(data);
                        }
                    }
                );
        } else {
            ret.put("result", false);
            ret.put("message", "player is not defined");
            call.success(ret);
        }
    }

    @PluginMethod
    public void getDuration(final PluginCall call) {
        saveCall(call);
        JSObject ret = new JSObject();
        ret.put("method", "getDuration");
        String playerId = call.getString("playerId");
        if (playerId == null) {
            ret.put("result", false);
            ret.put("message", "Must provide a PlayerId");
            call.success(ret);
            return;
        }
        if (mode.equals("fullscreen") && fsPlayerId.equals(playerId)) {
            bridge
                .getActivity()
                .runOnUiThread(
                    new Runnable() {

                        @Override
                        public void run() {
                            JSObject ret = new JSObject();
                            ret.put("method", "getDuration");
                            int duration = fsFragment.getDuration();
                            ret.put("result", true);
                            ret.put("value", duration);
                            call.success(ret);
                        }
                    }
                );
        } else {
            ret.put("result", false);
            ret.put("message", "player is not defined");
            call.success(ret);
        }
    }

    @PluginMethod
    public void getCurrentTime(final PluginCall call) {
        saveCall(call);
        JSObject ret = new JSObject();
        ret.put("method", "getCurrentTime");
        String playerId = call.getString("playerId");
        if (playerId == null) {
            ret.put("result", false);
            ret.put("message", "Must provide a PlayerId");
            call.success(ret);
            return;
        }
        if (mode.equals("fullscreen") && fsPlayerId.equals(playerId)) {
            bridge
                .getActivity()
                .runOnUiThread(
                    new Runnable() {

                        @Override
                        public void run() {
                            JSObject ret = new JSObject();
                            ret.put("method", "getCurrentTime");
                            int curTime = fsFragment.getCurrentTime();
                            ret.put("result", true);
                            ret.put("value", curTime);
                            call.success(ret);
                        }
                    }
                );
        } else {
            ret.put("result", false);
            ret.put("message", "player is not defined");
            call.success(ret);
        }
    }

    @PluginMethod
    public void setCurrentTime(final PluginCall call) {
        saveCall(call);
        JSObject ret = new JSObject();
        ret.put("method", "setCurrentTime");
        String playerId = call.getString("playerId");
        if (playerId == null) {
            ret.put("result", false);
            ret.put("message", "Must provide a PlayerId");
            call.success(ret);
            return;
        }
        Double value = call.getDouble("seektime");
        if (value == null) {
            ret.put("result", false);
            ret.put("message", "Must provide a time in second");
            call.success(ret);
            return;
        }
        final int cTime = (int) Math.round(value);
        if (mode.equals("fullscreen") && fsPlayerId.equals(playerId)) {
            bridge
                .getActivity()
                .runOnUiThread(
                    new Runnable() {

                        @Override
                        public void run() {
                            fsFragment.setCurrentTime(cTime);
                            JSObject ret = new JSObject();
                            ret.put("result", true);
                            ret.put("method", "setCurrentTime");
                            ret.put("value", cTime);
                            call.success(ret);
                        }
                    }
                );
        } else {
            ret.put("result", false);
            ret.put("message", "player is not defined");
            call.success(ret);
        }
    }

    @PluginMethod
    public void getVolume(final PluginCall call) {
        saveCall(call);
        JSObject ret = new JSObject();
        ret.put("method", "getVolume");
        String playerId = call.getString("playerId");
        if (playerId == null) {
            ret.put("result", false);
            ret.put("message", "Must provide a PlayerId");
            call.success(ret);
            return;
        }
        if (mode.equals("fullscreen") && fsPlayerId.equals(playerId)) {
            bridge
                .getActivity()
                .runOnUiThread(
                    new Runnable() {

                        @Override
                        public void run() {
                            Float volume = fsFragment.getVolume();
                            JSObject ret = new JSObject();
                            ret.put("result", true);
                            ret.put("method", "getVolume");
                            ret.put("value", volume);
                            call.success(ret);
                        }
                    }
                );
        } else {
            ret.put("result", false);
            ret.put("message", "player is not defined");
            call.success(ret);
        }
    }

    @PluginMethod
    public void setVolume(final PluginCall call) {
        saveCall(call);
        JSObject ret = new JSObject();
        ret.put("method", "setVolume");
        String playerId = call.getString("playerId");
        if (playerId == null) {
            ret.put("result", false);
            ret.put("message", "Must provide a PlayerId");
            call.success(ret);
            return;
        }
        String value = call.getString("volume");
        if (value == null) {
            ret.put("result", false);
            ret.put("method", "setVolume");
            ret.put("message", "Must provide a volume value");
            call.success(ret);
            return;
        }
        final Float volume = Float.valueOf(value.trim());
        if (mode.equals("fullscreen") && fsPlayerId.equals(playerId)) {
            bridge
                .getActivity()
                .runOnUiThread(
                    new Runnable() {

                        @Override
                        public void run() {
                            fsFragment.setVolume(volume);
                            JSObject ret = new JSObject();
                            ret.put("result", true);
                            ret.put("method", "setVolume");
                            ret.put("value", volume);
                            call.success(ret);
                        }
                    }
                );
        } else {
            ret.put("result", false);
            ret.put("message", "player is not defined");
            call.success(ret);
        }
    }

    @PluginMethod
    public void getMuted(final PluginCall call) {
        saveCall(call);
        JSObject ret = new JSObject();
        ret.put("method", "getMuted");
        String playerId = call.getString("playerId");
        if (playerId == null) {
            ret.put("result", false);
            ret.put("message", "Must provide a PlayerId");
            call.success(ret);
            return;
        }
        if (mode.equals("fullscreen") && fsPlayerId.equals(playerId)) {
            bridge
                .getActivity()
                .runOnUiThread(
                    new Runnable() {

                        @Override
                        public void run() {
                            boolean value = fsFragment.getMuted();
                            JSObject ret = new JSObject();
                            ret.put("result", true);
                            ret.put("method", "getMuted");
                            ret.put("value", value);
                            call.success(ret);
                        }
                    }
                );
        } else {
            ret.put("result", false);
            ret.put("message", "player is not defined");
            call.success(ret);
        }
    }

    @PluginMethod
    public void setMuted(final PluginCall call) {
        saveCall(call);
        JSObject ret = new JSObject();
        ret.put("method", "setMuted");
        String playerId = call.getString("playerId");
        if (playerId == null) {
            ret.put("result", false);
            ret.put("message", "Must provide a PlayerId");
            call.success(ret);
            return;
        }
        Boolean value = call.getBoolean("muted");
        if (value == null) {
            ret.put("result", true);
            ret.put("message", "Must provide a boolean true/false");
            call.success(ret);
            return;
        }
        final boolean bValue = value;
        if (mode.equals("fullscreen") && fsPlayerId.equals(playerId)) {
            bridge
                .getActivity()
                .runOnUiThread(
                    new Runnable() {

                        @Override
                        public void run() {
                            fsFragment.setMuted(bValue);
                            JSObject ret = new JSObject();
                            ret.put("result", true);
                            ret.put("method", "setMuted");
                            ret.put("value", bValue);
                            call.success(ret);
                        }
                    }
                );
        } else {
            ret.put("result", false);
            ret.put("message", "player is not defined");
            call.success(ret);
        }
    }

    @PluginMethod
    public void stopAllPlayers(final PluginCall call) {
        saveCall(call);
        bridge
            .getActivity()
            .runOnUiThread(
                new Runnable() {

                    @Override
                    public void run() {
                        if (fsFragment != null) fsFragment.pause();
                        JSObject ret = new JSObject();
                        ret.put("result", true);
                        ret.put("method", "stopAllPlayers");
                        ret.put("value", true);
                        call.success(ret);
                    }
                }
            );
    }

    public interface RequestCodes {
        int Video = 10001;
    }

    public boolean isDeviceTV(Context context) {
        //Since Android TV is only API 21+ that is the only time we will compare configurations
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UiModeManager uiManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
            return uiManager != null && uiManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION;
        }
        return false;
    }

    private void loadFragment(/*VideoPlayerFragment*/FullscreenExoPlayerFragment vpFragment) {
        // create a FragmentManager
        FragmentManager fm = getBridge().getActivity().getFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.frameLayout, vpFragment);
        fragmentTransaction.commit(); // save the changes
    }

    private void removeFragment(/*VideoPlayerFragment*/FullscreenExoPlayerFragment vpFragment) {
        FragmentManager fm = getBridge().getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.remove(vpFragment);
        fragmentTransaction.commit();
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
                        data.put("playerId", this.getInfo().get("playerId"));
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
                        data.put("playerId", this.getInfo().get("playerId"));
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
                        data.put("playerId", this.getInfo().get("playerId"));
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
                        data.put("playerId", this.getInfo().get("playerId"));
                        data.put("currentTime", this.getInfo().get("currentTime"));
                        bridge
                            .getActivity()
                            .runOnUiThread(
                                new Runnable() {

                                    @Override
                                    public void run() {
                                        removeFragment(fsFragment);
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
                        if (Integer.valueOf(this.getInfo().get("dismiss")) == 1) ret = true;
                        data.put("dismiss", ret);
                        bridge
                            .getActivity()
                            .runOnUiThread(
                                new Runnable() {

                                    @Override
                                    public void run() {
                                        removeFragment(fsFragment);
                                        fsFragment = null;
                                        NotificationCenter.defaultCenter().removeAllNotifications();
                                        notifyListeners("jeepCapVideoPlayerExit", data);
                                    }
                                }
                            );
                    }
                }
            );
    }
}
