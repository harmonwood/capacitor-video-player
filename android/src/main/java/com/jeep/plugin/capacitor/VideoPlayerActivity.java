package com.jeep.plugin.capacitor;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import android.view.KeyEvent;
import android.widget.Toast;


import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.jeep.plugin.capacitor.capacitorvideoplayer.R;

import java.util.Arrays;
import java.util.List;

public class VideoPlayerActivity  extends AppCompatActivity {
    private static final String TAG = VideoPlayerActivity.class.getName();
    private List<String> supportedFormat = Arrays.asList(
            new String[]{"mp4","webm","ogv","3gp","flv","dash","mpd","m3u8","ism","ytube"});
    private PlaybackStateListener playbackStateListener;
    private PlayerView playerView;
    private String vType = null;
    private SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private Uri uri = null;

    private Boolean isTV;
    private ProgressBar Pbar;

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
        playerView = (PlayerView) findViewById(R.id.videoViewId);
        Pbar = (ProgressBar)findViewById(R.id.indeterminateBar);
        // Listening for events
        playbackStateListener = new PlaybackStateListener();

        // Get the Intent that started this activity and extract the string
        final Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String videoPath = extras.getString("videoPath");
        isTV = extras.getBoolean("isTV");
        if(isTV) {
            Toast.makeText(this, "Device is a TV ", Toast.LENGTH_SHORT).show();
        }

        uri = Uri.parse(videoPath);
        Log.v(TAG,"display url: "+uri);
        Log.v(TAG,"display isTV: "+isTV);

        vType = getVideoType(uri);

        if (uri != null && vType != null) {
            // set to Full Screen
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();

            if (savedInstanceState != null) {
                mCurrentPosition = savedInstanceState.getInt(PLAYBACK_TIME);
            }

         }
        else {
            Log.d(TAG, "Video path wrong or type not supported");
            Toast.makeText(this, "Video path wrong or type not supported", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED, intent.putExtra("result", false));
            finish();
        }

    }
    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, getIntent().putExtra("result", false));
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24) {
            initializePlayer();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        boolean isAppBackground = isApplicationSentToBackground(this);
        if (!isAppBackground){
            if (Util.SDK_INT >= 24) {
            releasePlayer();
            }
            setResult(RESULT_CANCELED, getIntent().putExtra("result", false));
            finish();
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        player.setPlayWhenReady(false);
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }

    }
    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.removeListener(playbackStateListener);
            player.release();
            player = null;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer();
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void initializePlayer() {
        if (player == null) {
            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(this)
                    .build();
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
            TrackSelector trackSelector = new
                    DefaultTrackSelector(this, videoTrackSelectionFactory);
            LoadControl loadControl = new DefaultLoadControl();
            player = new SimpleExoPlayer.Builder(this)
                    .setTrackSelector(trackSelector)
                    .setLoadControl(loadControl)
                    .setBandwidthMeter(bandwidthMeter).build();


        }
        playerView.setPlayer(player);
        MediaSource mediaSource = buildMediaSource();

        if(mediaSource != null) {
            player.setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true);
            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow, playbackPosition);
            player.addListener(playbackStateListener);
            player.prepare(mediaSource, false, false);
        }
    }

    private MediaSource buildMediaSource() {
        MediaSource mediaSource = null;
        HttpDataSource.Factory  httpDataSourceFactory = new
                DefaultHttpDataSourceFactory( "jeep-exoplayer-plugin",
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                1800000,
                true);


        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, httpDataSourceFactory);

        if (vType.equals("mp4") || vType.equals("webm") || vType.equals("ogv")
                || vType.equals("3gp") || vType.equals("flv")) {
            mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(uri);
        } else if (vType.equals("dash") || vType.equals("mpd") ) {
            /* adaptive streaming Dash stream */
            DashMediaSource.Factory mediaSourceFactory = new DashMediaSource.Factory(dataSourceFactory);
            mediaSource = mediaSourceFactory.createMediaSource(uri);
        } else if(vType.equals("m3u8")) {
            mediaSource = new HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(uri);
        } else if(vType.equals("ism")) {
            mediaSource = new SsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(uri);
        }
        return mediaSource;
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current playback position (in milliseconds) to the
        // instance state bundle.
        outState.putInt(PLAYBACK_TIME, (int) player.getCurrentPosition());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(isTV) {
            long videoPosition = player.getCurrentPosition();
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    fastForward(videoPosition,1);
                    return true;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    rewind(videoPosition,1);
                    return true;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    play_pause();
                    return true;
                case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                    fastForward(videoPosition,2);
                    return true;
                case KeyEvent.KEYCODE_MEDIA_REWIND:
                    rewind(videoPosition,2);
                    return true;
                case KeyEvent.KEYCODE_BACK:
                    onBackPressed();
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void fastForward(long position,int times) {
        if(position < mDuration - videoStep ) {
            if(player.isPlaying()) {
                player.setPlayWhenReady(false);
            }
            player.seekTo(position + (long) times * videoStep);
            player.setPlayWhenReady(true);
        }
    }
    private void rewind(long position,int times) {
        if(position > videoStep ) {
            if(player.isPlaying()) {
                player.setPlayWhenReady(false);
            }
            player.seekTo(position - (long) times * videoStep);
            player.setPlayWhenReady(true);
        }
    }
    private void play_pause() {
        if(player.isPlaying()) {
            player.setPlayWhenReady(false);
        } else {
            player.setPlayWhenReady(true);
        }
    }

    private class PlaybackStateListener implements Player.EventListener{

        @Override
        public void onPlayerStateChanged(boolean playWhenReady,
                                         int playbackState) {
            String stateString;
            switch (playbackState) {
                case ExoPlayer.STATE_IDLE:
                    stateString = "ExoPlayer.STATE_IDLE      -";
                    break;
                case ExoPlayer.STATE_BUFFERING:
                    stateString = "ExoPlayer.STATE_BUFFERING -";
                    Pbar.setVisibility(View.VISIBLE);
                    break;
                case ExoPlayer.STATE_READY:
                    stateString = "ExoPlayer.STATE_READY     -";
                    Pbar.setVisibility(View.GONE);
                    break;
                case ExoPlayer.STATE_ENDED:
                    stateString = "ExoPlayer.STATE_ENDED     -";
                    setResult(RESULT_CANCELED, getIntent().putExtra("result", false));
                    finish();
                    break;
                default:
                    stateString = "UNKNOWN_STATE             -";
                    break;
            }
            Log.d(TAG, "changed state to " + stateString
                    + " playWhenReady: " + playWhenReady);
        }
    }
    private String getVideoType(Uri uri) {
        String ret = null;
        String ext;
        String lastSegment = uri.getLastPathSegment();
        for (String type : supportedFormat) {
            if(lastSegment.contains(type)) ret = type;
            if (ret == null) {
                List<String> segments = uri.getPathSegments();
                for (String segment : segments) {
                    for (String sType : supportedFormat) {
                        if(segment.contains(sType)) {
                            ret = sType;
                            break;
                        }
                    }
                }
            }
        }
        return ret;
    }
    public boolean isApplicationSentToBackground(final Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : am
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return true;
            }
        }
        return false;
    }

}
