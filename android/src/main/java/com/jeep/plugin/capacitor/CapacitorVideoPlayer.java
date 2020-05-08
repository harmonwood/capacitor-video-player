package com.jeep.plugin.capacitor;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import java.io.File;

import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.net.Uri;
import android.content.Context;

import android.app.UiModeManager;
import android.os.Build;
import android.content.res.Configuration;


@NativePlugin(requestCodes = {CapacitorVideoPlayer.RequestCodes.Video})
public class CapacitorVideoPlayer extends Plugin {
    private static final String TAG = "CapacitorVideoPlayer";
    private Context context;
    private String videoPath;
    private Boolean isTV;

    @PluginMethod()
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", value);
        call.success(ret);
    }
    @PluginMethod()
    public void initPlayer(PluginCall call) {
        saveCall(call);
        context = getContext();
        // Check if running on a TV Device
        isTV = isDeviceTV(context);
        Log.d(TAG,"**** isTV "+isTV+ " ****");

        String mode = call.getString("mode");
        if(mode == null) {
            call.reject("VideoPlayer initPlayer: Must provide a Mode (fullscreen/embedded)");
            return;
        }
        String url = call.getString("url");
        if(url == null) {
            call.reject("VideoPlayer initPlayer: Must provide an url");
            return;
        }
        if (mode == "embedded") {
            call.reject("VideoPlayer initPlayer: Embedded Mode not yet implemented");
            return;
        }
        Log.v(TAG,"display url: "+url);
        String http = url.substring(0,4);
        if (http.equals("http")) {
            videoPath = url;
        } else {
            videoPath = "android.resource://" + context.getPackageName() + "/" + url; // works
            //            url = "content://"+appPath+ "/" + url ;
            Log.v(TAG,"calculated url: "+url);
        }
        Log.v(TAG,"videoPath: "+ videoPath);
        Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
        Bundle extras = new Bundle();
        extras.putString("videoPath",videoPath);
        extras.putBoolean("isTV",isTV);
        intent.putExtras(extras);

        startActivityForResult(call, intent, RequestCodes.Video);
    }

    @Override
    protected void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        super.handleOnActivityResult(requestCode, resultCode, data);
        PluginCall savedCall = getSavedCall();

        if (savedCall == null) {
            return;
        }
        JSObject ret = new JSObject();
        if (requestCode == RequestCodes.Video) {
            if(data != null) {
                ret.put("result", data.getBooleanExtra("result", false));
            } else {
                ret.put("result",false);
            }
            savedCall.resolve(ret);
            return;
        }
        savedCall.reject("Plugin error");
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
}
