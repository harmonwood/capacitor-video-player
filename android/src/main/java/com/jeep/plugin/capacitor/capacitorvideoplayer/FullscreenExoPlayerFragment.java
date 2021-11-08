package com.jeep.plugin.capacitor.capacitorvideoplayer;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Fragment;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.CaptioningManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.getcapacitor.JSObject;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.text.CaptionStyleCompat;
import com.google.android.exoplayer2.text.CaptionStyleCompat;
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
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.jeep.plugin.capacitor.capacitorvideoplayer.Notifications.NotificationCenter;
import com.jeep.plugin.capacitor.capacitorvideoplayer.R;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FullscreenExoPlayerFragment extends Fragment {

    public String videoPath;
    public String playerId;
    public String subTitle;
    public String language;
    public JSObject subTitleOptions;
    public Boolean isTV;
    public Boolean isInternal;
    public Long videoId;
    public Boolean hideCloseButton;

    private static final String TAG = FullscreenExoPlayerFragment.class.getName();
    public static final long UNKNOWN_TIME = -1L;
    private List<String> supportedFormat = Arrays.asList(
        new String[] { "mp4", "webm", "ogv", "3gp", "flv", "dash", "mpd", "m3u8", "ism", "ytube", "" }
    );
    private FullscreenExoPlayerFragment.PlaybackStateListener playbackStateListener;
    private PlayerView playerView;
    private String vType = null;
    private static SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private boolean firstReadyToPlay = true;
    private boolean isEnded = false;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private Uri uri = null;
    private Uri sturi = null;
    private Looper looper;
    private static Handler handler;
    private ProgressBar Pbar;
    private View view;
    private ImageButton btn;
    private ConstraintLayout constLayout;
    private Context context;
    private boolean isMuted = false;
    private float curVolume = (float) 0.5;
    private String stForeColor = "";
    private String stBackColor = "";
    private Integer stFontSize = 16;

    // Current playback position (in milliseconds).
    private int mCurrentPosition = 0;
    private int mDuration;
    private static final int videoStep = 15000;

    // Tag for the instance state bundle.
    private static final String PLAYBACK_TIME = "play_time";

    /**
     * Create Fragment View
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = container.getContext();

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fs_exoplayer, container, false);
        constLayout = view.findViewById(R.id.fsExoPlayer);
        playerView = view.findViewById(R.id.videoViewId);
        Pbar = view.findViewById(R.id.indeterminateBar);
        btn = (ImageButton) view.findViewById(R.id.exo_close);

        if (hideCloseButton) {
            btn.setVisibility(View.GONE);
        }

        // Listening for events
        playbackStateListener = new FullscreenExoPlayerFragment.PlaybackStateListener();
        if (isTV) {
            Toast.makeText(context, "Device is a TV ", Toast.LENGTH_SHORT).show();
        }

        if (!isInternal) {
            uri = Uri.parse(videoPath);
            sturi = subTitle != null ? Uri.parse(subTitle) : null;

            stForeColor = subTitleOptions.has("foregroundColor") ? subTitleOptions.getString("foregroundColor") : "rgba(255,255,255,1)";
            stBackColor = subTitleOptions.has("backgroundColor") ? subTitleOptions.getString("backgroundColor") : "rgba(0,0,0,1)";
            stFontSize = subTitleOptions.has("fontSize") ? subTitleOptions.getInteger("fontSize") : 16;
            // get video type
            vType = getVideoType(uri);
            Log.v(TAG, "display url: " + uri);
            Log.v(TAG, "display subtitle url: " + sturi);
            Log.v(TAG, "display isTV: " + isTV);
            Log.v(TAG, "display vType: " + vType);
        }
        if (uri != null || isInternal) {
            // go fullscreen
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (savedInstanceState != null) {
                mCurrentPosition = savedInstanceState.getInt(PLAYBACK_TIME);
            }

            getActivity()
                .runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            btn.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        backPressed();
                                    }
                                }
                            );
                            // Set the onKey listener
                            view.setFocusableInTouchMode(true);
                            view.requestFocus();
                            view.setOnKeyListener(
                                new View.OnKeyListener() {
                                    @Override
                                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                                        if (event.getAction() == KeyEvent.ACTION_UP) {
                                            long videoPosition = player.getCurrentPosition();
                                            Log.v(TAG, "$$$$ onKey " + keyCode + " $$$$");
                                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                                backPressed();
                                            } else if (isTV) {
                                                switch (keyCode) {
                                                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                                                        fastForward(videoPosition, 1);
                                                        break;
                                                    case KeyEvent.KEYCODE_DPAD_LEFT:
                                                        rewind(videoPosition, 1);
                                                        break;
                                                    case KeyEvent.KEYCODE_DPAD_CENTER:
                                                        play_pause();
                                                        break;
                                                    case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                                                        fastForward(videoPosition, 2);
                                                        break;
                                                    case KeyEvent.KEYCODE_MEDIA_REWIND:
                                                        rewind(videoPosition, 2);
                                                        break;
                                                }
                                            }
                                            return true;
                                        } else {
                                            return false;
                                        }
                                    }
                                }
                            );

                            // initialize the player
                            initializePlayer();
                        }
                    }
                );
        } else {
            Log.d(TAG, "Video path wrong or type not supported");
            Toast.makeText(context, "Video path wrong or type not supported", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    /**
     * Perform backPressed Action
     */
    private void backPressed() {
        Map<String, Object> info = new HashMap<String, Object>() {
            {
                put("dismiss", "1");
            }
        };
        player.seekTo(0);
        player.setVolume(curVolume);
        releasePlayer();
        NotificationCenter.defaultCenter().postNotification("playerFullscreenDismiss", info);
    }

    /**
     * Perform onStart Action
     */
    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24) {
            initializePlayer();
        }
    }

    /**
     * Perform onStop Action
     */
    @Override
    public void onStop() {
        super.onStop();
        boolean isAppBackground = isApplicationSentToBackground(context);
        if (!isAppBackground) {
            if (Util.SDK_INT >= 24) {
                releasePlayer();
            }
        }
    }

    /**
     * Perform onPause Action
     */
    @Override
    public void onPause() {
        super.onPause();
        if (player != null) player.setPlayWhenReady(false);
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
    }

    /**
     * Release the player
     */
    public void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.removeListener(playbackStateListener);
            player.release();
            player = null;
            showSystemUI();
            resetVariables();
        }
    }

    /**
     * Perform onResume Action
     */
    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer();
        }
    }

    /**
     * Hide System UI
     */
    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        if (playerView != null) playerView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LOW_PROFILE |
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }

    /**
     * Leave the fullsreen mode and reset the status bar to visible
     */
    private void showSystemUI() {
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.VISIBLE);
    }

    /**
     * Initialize the player
     */
    private void initializePlayer() {
        if (player == null) {
            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(context).build();
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
            TrackSelector trackSelector = new DefaultTrackSelector(context, videoTrackSelectionFactory);
            LoadControl loadControl = new DefaultLoadControl();
            player =
                new SimpleExoPlayer.Builder(context)
                    .setTrackSelector(trackSelector)
                    .setLoadControl(loadControl)
                    .setBandwidthMeter(bandwidthMeter)
                    .build();
        }
        playerView.setPlayer(player);
        MediaSource mediaSource;
        if (!isInternal) {
            Log.v(TAG, "****** videoPath " + videoPath);
            if (videoPath.substring(0, 21).equals("file:///android_asset")) {
                mediaSource = buildAssetMediaSource(uri);
            } else {
                mediaSource = buildHttpMediaSource();
            }
        } else {
            Uri videoUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoId);
            mediaSource = buildInternalMediaSource(videoUri);
        }

        if (mediaSource != null) {
            player.setAudioAttributes(AudioAttributes.DEFAULT, true);
            player.addListener(playbackStateListener);
            player.prepare(mediaSource, false, false);
        }
        Map<String, Object> info = new HashMap<String, Object>() {
            {
                put("fromPlayerId", playerId);
            }
        };
        int foreground = Color.WHITE;
        int background = Color.BLACK;
        if (stForeColor.length() > 4 && stForeColor.substring(0, 4).equals("rgba")) {
            foreground = getColorFromRGBA(stForeColor);
        }
        if (stBackColor.length() > 4 && stBackColor.substring(0, 4).equals("rgba")) {
            background = getColorFromRGBA(stBackColor);
        }

        playerView
            .getSubtitleView()
            .setStyle(
                new CaptionStyleCompat(foreground, background, Color.TRANSPARENT, CaptionStyleCompat.EDGE_TYPE_NONE, Color.WHITE, null)
            );
        playerView.getSubtitleView().setFixedTextSize(TypedValue.COMPLEX_UNIT_DIP, stFontSize);
        NotificationCenter.defaultCenter().postNotification("initializePlayer", info);
    }

    /**
     * Build the Asset MediaSource
     */
    private MediaSource buildAssetMediaSource(Uri uri) {
        MediaSource mediaSource = null;
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, "jeep-exoplayer-plugin");
        mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        // Get the subtitles if any
        if (sturi != null) {
            mediaSource = getSubTitle(mediaSource, sturi, dataSourceFactory);
        }
        return mediaSource;
    }

    /**
     * Build the Internal MediaSource
     */
    private MediaSource buildInternalMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, "jeep-exoplayer-plugin");
        return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
    }

    /**
     * Build the Http MediaSource
     * @return MediaSource
     */
    private MediaSource buildHttpMediaSource() {
        MediaSource mediaSource = null;
        HttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSourceFactory(
            "jeep-exoplayer-plugin",
            DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
            1800000,
            true
        );

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, httpDataSourceFactory);

        if (
            vType.equals("mp4") ||
            vType.equals("webm") ||
            vType.equals("ogv") ||
            vType.equals("3gp") ||
            vType.equals("flv") ||
            vType.equals("")
        ) {
            mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        } else if (vType.equals("dash") || vType.equals("mpd")) {
            /* adaptive streaming Dash stream */
            DashMediaSource.Factory mediaSourceFactory = new DashMediaSource.Factory(dataSourceFactory);
            mediaSource = mediaSourceFactory.createMediaSource(uri);
        } else if (vType.equals("m3u8")) {
            mediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        } else if (vType.equals("ism")) {
            mediaSource = new SsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        }
        // Get the subtitles if any
        if (sturi != null) {
            mediaSource = getSubTitle(mediaSource, sturi, dataSourceFactory);
        }
        return mediaSource;
    }

    /**
     * Save instance state
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current playback position (in milliseconds) to the
        // instance state bundle.
        if (player != null) {
            outState.putInt(PLAYBACK_TIME, (int) player.getCurrentPosition());
        }
    }

    private int getColorFromRGBA(String rgbaColor) {
        int ret = 0;
        String color = rgbaColor.substring(rgbaColor.indexOf("(") + 1, rgbaColor.indexOf(")"));
        List<String> colors = Arrays.asList(color.split(","));
        if (colors.size() == 4) {
            ret =
                (Math.round(Float.parseFloat(colors.get(3).trim()) * 255) & 0xff) << 24 |
                (Integer.parseInt(colors.get(0).trim()) & 0xff) << 16 |
                (Integer.parseInt(colors.get(1).trim()) & 0xff) << 8 |
                (Integer.parseInt(colors.get(2).trim()) & 0xff);
        }
        return ret;
    }

    private MediaSource getSubTitle(MediaSource mediaSource, Uri sturi, DataSource.Factory dataSourceFactory) {
        // Create mediaSource with subtitle
        MediaSource[] mediaSources = new MediaSource[2];
        mediaSources[0] = mediaSource;
        String mimeType = getMimeType(sturi);
        //Add subtitles
        SingleSampleMediaSource subtitleSource = new SingleSampleMediaSource.Factory(dataSourceFactory)
            .createMediaSource(sturi, Format.createTextSampleFormat(null, mimeType, Format.NO_VALUE, language, null), C.TIME_UNSET);

        mediaSources[1] = subtitleSource;

        mediaSource = new MergingMediaSource(mediaSources);
        return mediaSource;
    }

    private String getMimeType(Uri sturi) {
        String lastSegment = sturi.getLastPathSegment();
        String extension = lastSegment.substring(lastSegment.lastIndexOf(".") + 1);
        String mimeType = "";
        if (extension.equals("vtt")) {
            mimeType = MimeTypes.TEXT_VTT;
        } else if (extension.equals("srt")) {
            mimeType = MimeTypes.APPLICATION_SUBRIP;
        } else if (extension.equals("ssa") || extension.equals("ass")) {
            mimeType = MimeTypes.TEXT_SSA;
        } else if (extension.equals("ttml") || extension.equals("dfxp") || extension.equals("xml")) {
            mimeType = MimeTypes.APPLICATION_TTML;
        }
        return mimeType;
    }

    /**
     * Fast Forward TV
     */
    private void fastForward(long position, int times) {
        if (position < mDuration - videoStep) {
            if (player.isPlaying()) {
                player.setPlayWhenReady(false);
            }
            player.seekTo(position + (long) times * videoStep);
            player.setPlayWhenReady(true);
        }
    }

    /**
     * Rewind TV
     */
    private void rewind(long position, int times) {
        if (position > videoStep) {
            if (player.isPlaying()) {
                player.setPlayWhenReady(false);
            }
            player.seekTo(position - (long) times * videoStep);
            player.setPlayWhenReady(true);
        }
    }

    /**
     * Play Pause TV
     */
    private void play_pause() {
        if (player.isPlaying()) {
            player.setPlayWhenReady(false);
        } else {
            player.setPlayWhenReady(true);
        }
    }

    /**
     * Check if the player is playing
     * @return boolean
     */
    public boolean isPlaying() {
        return player.isPlaying();
    }

    /**
     * Start the player
     */
    public void play() {
        player.setPlayWhenReady(true);
    }

    /**
     * Pause the player
     */
    public void pause() {
        player.setPlayWhenReady(false);
    }

    /**
     * Get the player duration
     * @return int in seconds
     */
    public int getDuration() {
        return player.getDuration() == UNKNOWN_TIME ? 0 : (int) (player.getDuration() / 1000);
    }

    /**
     * Get the player current position
     * @return int in seconds
     */
    public int getCurrentTime() {
        return player.getCurrentPosition() == UNKNOWN_TIME ? 0 : (int) (player.getCurrentPosition() / 1000);
    }

    /**
     * Set the player current position
     * @param timeSecond int
     */
    public void setCurrentTime(int timeSecond) {
        long seekPosition = player.getCurrentPosition() == UNKNOWN_TIME
            ? 0
            : Math.min(Math.max(0, timeSecond * 1000), player.getDuration());
        player.seekTo(seekPosition);
    }

    /**
     * Return the player volume
     * @return float
     */
    public float getVolume() {
        return player.getVolume();
    }

    /**
     * Set the player volume
     * @param _volume float range 0,1
     */
    public void setVolume(float _volume) {
        float volume = Math.min(Math.max(0, _volume), 1L);
        player.setVolume(volume);
    }

    /**
     * Switch Off/On the player volume
     * @param _isMuted boolean
     */
    public void setMuted(boolean _isMuted) {
        isMuted = _isMuted;
        if (isMuted) {
            curVolume = player.getVolume();
            player.setVolume(0L);
        } else {
            player.setVolume(curVolume);
        }
    }

    /**
     * Check if the player is muted
     * @return boolean
     */
    public boolean getMuted() {
        return isMuted;
    }

    /**
     * Player Event Listener
     */
    private class PlaybackStateListener implements Player.EventListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            String stateString;
            Map<String, Object> info = new HashMap<String, Object>() {
                {
                    put("fromPlayerId", playerId);
                    put("currentTime", String.valueOf(player.getCurrentPosition() / 1000));
                }
            };
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
                    Log.v(TAG, "**** in ExoPlayer.STATE_READY firstReadyToPlay " + firstReadyToPlay);

                    if (firstReadyToPlay) {
                        firstReadyToPlay = false;
                        NotificationCenter.defaultCenter().postNotification("playerItemReady", info);
                        player.setPlayWhenReady(true);
                        Log.v(TAG, "**** in ExoPlayer.STATE_READY firstReadyToPlay player.isPlaying" + player.isPlaying());
                        player.seekTo(currentWindow, playbackPosition);
                    } else {
                        Log.v(TAG, "**** in ExoPlayer.STATE_READY isPlaying " + player.isPlaying());
                        if (player.isPlaying()) {
                            Log.v(TAG, "**** in ExoPlayer.STATE_READY going to notify playerItemPlay ");
                            NotificationCenter.defaultCenter().postNotification("playerItemPlay", info);
                        } else {
                            Log.v(TAG, "**** in ExoPlayer.STATE_READY going to notify playerItemPause ");
                            NotificationCenter.defaultCenter().postNotification("playerItemPause", info);
                        }
                    }
                    break;
                case ExoPlayer.STATE_ENDED:
                    stateString = "ExoPlayer.STATE_ENDED     -";
                    player.seekTo(0);
                    player.setVolume(curVolume);
                    releasePlayer();
                    NotificationCenter.defaultCenter().postNotification("playerItemEnd", info);
                    break;
                default:
                    stateString = "UNKNOWN_STATE             -";
                    break;
            }
        }
    }

    /**
     * Get video Type from Uri
     * @param uri
     * @return video type
     */
    private String getVideoType(Uri uri) {
        String ret = null;
        String lastSegment = uri.getLastPathSegment();
        for (String type : supportedFormat) {
            if (ret != null) break;
            if (lastSegment.contains(type)) ret = type;
            if (ret == null) {
                List<String> segments = uri.getPathSegments();
                String segment;
                if (segments.get(segments.size() - 1).equals("manifest")) {
                    segment = segments.get(segments.size() - 2);
                } else {
                    segment = segments.get(segments.size() - 1);
                }
                for (String sType : supportedFormat) {
                    if (segment.contains(sType)) {
                        ret = sType;
                        break;
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Reset Variables for multiple runs
     */
    private void resetVariables() {
        vType = null;
        playerView = null;
        playWhenReady = true;
        firstReadyToPlay = true;
        isEnded = false;
        currentWindow = 0;
        playbackPosition = 0;
        uri = null;
        isMuted = false;
        curVolume = (float) 0.5;
        mCurrentPosition = 0;
    }

    /**
     * Check if the application has been sent to the background
     * @param context
     * @return boolean
     */
    public boolean isApplicationSentToBackground(final Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : am.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return true;
            }
        }
        return false;
    }
}
