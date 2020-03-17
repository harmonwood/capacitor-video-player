package com.jeep.plugin.capacitor;
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

import android.view.KeyEvent;
import android.widget.Toast;

import com.jeep.plugin.capacitor.capacitorvideoplayer.R;

public class VideoPlayerActivity  extends AppCompatActivity {
    private static final String TAG = "VideoPlayerActivity";
    VideoView videoView;
    MediaController mCtrl;
    Boolean isTV;

    // Current playback position (in milliseconds).
    private int mCurrentPosition = 0;
    private int mDuration;
    private static final int videoStep = 15000;

    // Tag for the instance state bundle.
    private static final String PLAYBACK_TIME = "play_time";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoplayer);
        videoView = (VideoView) findViewById(R.id.videoViewId);


        // Get the Intent that started this activity and extract the string
        final Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String videoPath = extras.getString("videoPath");
        isTV = extras.getBoolean("isTV");
        if(isTV) {
            Toast.makeText(this, "Device is a TV ", Toast.LENGTH_SHORT).show();
        }

        Uri url = Uri.parse(videoPath);
        Log.v(TAG,"display url: "+url);
        Log.v(TAG,"display isTV: "+isTV);

        if (url != null) {
            // set to Full Screen
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();

            if (savedInstanceState != null) {
                mCurrentPosition = savedInstanceState.getInt(PLAYBACK_TIME);
            }

            // define a Media Controller
            mCtrl = new MediaController(this);
            //mCtrl.setMediaPlayer(videoView);
            mCtrl.setAnchorView(videoView);
            videoView.setMediaController(mCtrl);
            videoView.setVideoURI(url);
            videoView.setOnPreparedListener(new OnPreparedListener(){
                public void onPrepared(MediaPlayer mp) {
                    mDuration = videoView.getDuration();

                    // Restore saved position, if available.
                    if (mCurrentPosition > 0) {
                        videoView.seekTo(mCurrentPosition);
                    } else {
                        // Skipping to 1 shows the first frame of the video.
                        videoView.seekTo(1);
                    }

                    videoView.start();
                }
            });

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    // On completion.
                    setResult(RESULT_OK, intent.putExtra("result", true));
                    videoView.seekTo(0);
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current playback position (in milliseconds) to the
        // instance state bundle.
        outState.putInt(PLAYBACK_TIME, videoView.getCurrentPosition());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(isTV) {
            int videoPosition = videoView.getCurrentPosition();
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    fastForward(videoPosition);
                    return true;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    rewind(videoPosition);
                    return true;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    play_pause();
                    return true;
                case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                    fastForward(videoPosition);
                    return true;
                case KeyEvent.KEYCODE_MEDIA_REWIND:
                    rewind(videoPosition);
                    return true;
                case KeyEvent.KEYCODE_BACK:
                    onBackPressed();
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onBackPressed() {
        videoView.stopPlayback();
        setResult(RESULT_CANCELED, getIntent().putExtra("result", false));
        finish();
    }

    private void fastForward(int position) {
        if(position < mDuration - videoStep ) {
            if(videoView.isPlaying()) {
                videoView.pause();
            }
            videoView.seekTo(position + videoStep);
            videoView.start();
        }
    }
    private void rewind(int position) {
        if(position > videoStep ) {
            if(videoView.isPlaying()) {
                videoView.pause();
            }
            videoView.seekTo(position - videoStep);
            videoView.start();
        }
    }
    private void play_pause() {
        if(videoView.isPlaying()) {
            videoView.pause();
        } else {
            videoView.start();
        }
    }
}
