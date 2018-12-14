package com.jeep.plugin.capacitor.videoplayer;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import android.content.Context;
import android.util.Log;
import android.content.Intent;
import android.net.Uri;


@NativePlugin()
public class CapacitorVideoPlayer extends Plugin {
    private static final String TAG = "CapacitorVideoPlayer";

    @PluginMethod()
    public void play(PluginCall call) {
        String url = call.getString("url");
        if(url == null) {
            call.reject("Must provide an url");
            return;
        }
        Log.v(TAG,"display url: "+url);
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
        intent.putExtra("videoUri",uri);
        getActivity().startActivity(intent);
        
        JSObject ret = new JSObject();
        ret.put("result", true);
        call.success(ret);
    }
}
