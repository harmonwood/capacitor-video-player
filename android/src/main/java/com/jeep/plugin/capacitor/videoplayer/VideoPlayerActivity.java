package com.jeep.plugin.capacitor.videoplayer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.net.Uri;
import android.util.Log;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;
import android.media.MediaPlayer.OnPreparedListener;

import com.jeep.plugin.capacitor.videoplayer.R;

public class VideoPlayerActivity  extends AppCompatActivity {
    private static final String TAG = "VideoPlayerActivity";
    VideoView videoView;
    MediaController mCtrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoplayer);
        videoView = (VideoView) findViewById(R.id.videoViewId);

        // Get the Intent that started this activity and extract the string
        final Intent intent = getIntent();
        Uri url = intent.getParcelableExtra("videoUri");
        Log.v(TAG,"display url: "+url);
        if (url != null) {
            // set to Full Screen
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();
            // define a Media Controller
            mCtrl = new MediaController(this);
            mCtrl.setAnchorView(videoView);
            videoView.setMediaController(mCtrl);
            videoView.setVideoURI(url);
            videoView.setOnPreparedListener(new OnPreparedListener(){
                public void onPrepared(MediaPlayer mp) {
                    videoView.start();
                }
            });
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                // On completion.
                    setResult(RESULT_OK, intent.putExtra("result", true));
                    finish();
                }
            });

        }
        else {
            setResult(RESULT_CANCELED, intent.putExtra("result", false));
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, getIntent().putExtra("result", false));
        finish();
    }
}