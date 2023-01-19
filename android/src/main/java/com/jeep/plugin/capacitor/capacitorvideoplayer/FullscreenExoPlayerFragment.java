package com.jeep.plugin.capacitor.capacitorvideoplayer;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.PictureInPictureParams;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.util.Rational;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.mediarouter.app.MediaRouteButton;
import com.getcapacitor.JSObject;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.ext.cast.CastPlayer;
import com.google.android.exoplayer2.ext.cast.SessionAvailabilityListener;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.ExoTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.CaptionStyleCompat;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastState;
import com.jeep.plugin.capacitor.capacitorvideoplayer.Notifications.NotificationCenter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;

public class FullscreenExoPlayerFragment extends Fragment {

    public String videoPath;
    public Float videoRate;
    public String playerId;
    public String subTitle;
    public String language;
    public JSObject subTitleOptions;
    public JSObject headers;
    public Boolean isTV;
    public Boolean isInternal;
    public Long videoId;
    public Boolean exitOnEnd;
    public Boolean loopOnEnd;
    public Boolean pipEnabled;
    public Boolean bkModeEnabled;
    public String title;
    public String smallTitle;
    public String accentColor;
    public Boolean chromecast;

    private static final String TAG = FullscreenExoPlayerFragment.class.getName();
    public static final long UNKNOWN_TIME = -1L;
    private final List<String> supportedFormat = Arrays.asList(
        new String[] { "mp4", "webm", "ogv", "3gp", "flv", "dash", "mpd", "m3u8", "ism", "ytube", "" }
    );
    private PlaybackStateListener playbackStateListener;
    private PlayerView playerView;
    private String vType = null;
    private static ExoPlayer player;
    private boolean playWhenReady = true;
    private boolean firstReadyToPlay = true;
    private boolean isEnded = false;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private Uri uri = null;
    private Uri sturi = null;
    private ProgressBar Pbar;
    private View view;
    private ImageButton closeBtn;
    private ImageButton pipBtn;
    private ImageButton resizeBtn;
    private ConstraintLayout constLayout;
    private LinearLayout linearLayout;
    private TextView header_tv;
    private TextView header_below;
    private DefaultTimeBar exo_progress;
    private TextView cast_message;
    private Context context;
    private boolean isMuted = false;
    private float curVolume = (float) 0.5;
    private String stForeColor = "";
    private String stBackColor = "";
    private Integer stFontSize = 16;
    private boolean isInPictureInPictureMode = false;
    private TrackSelector trackSelector;
    // Current playback position (in milliseconds).
    private int mCurrentPosition;
    private int mDuration;
    private static final int videoStep = 10000;

    // Tag for the instance state bundle.
    private static final String PLAYBACK_TIME = "play_time";

    private PictureInPictureParams.Builder pictureInPictureParams;
    private MediaSessionCompat mediaSession;
    private MediaSessionConnector mediaSessionConnector;
    private PlayerControlView.VisibilityListener visibilityListener;
    private PackageManager packageManager;
    private Boolean isPIPModeeEnabled = true;
    final Handler handler = new Handler();
    final Runnable mRunnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        public void run() {
            checkPIPPermission();
        }
    };

    private Integer resizeStatus = AspectRatioFrameLayout.RESIZE_MODE_FIT;
    private Toast toastMessage;
    private MediaRouteButton mediaRouteButton;
    private CastContext castContext;
    private CastPlayer castPlayer;
    private MediaItem mediaItem;

    /**
     * Create Fragment View
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = container.getContext();
        packageManager = context.getPackageManager();
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fs_exoplayer, container, false);
        constLayout = view.findViewById(R.id.fsExoPlayer);
        linearLayout = view.findViewById(R.id.linearLayout);
        playerView = view.findViewById(R.id.videoViewId);
        header_tv = view.findViewById(R.id.header_tv);
        header_below = view.findViewById(R.id.header_below);
        Pbar = view.findViewById(R.id.indeterminateBar);
        exo_progress = view.findViewById(R.id.exo_progress);
        resizeBtn = view.findViewById(R.id.exo_resize);
        cast_message = view.findViewById(R.id.cast_message);

        castContext = CastContext.getSharedInstance(getContext());

        mediaRouteButton = view.findViewById(R.id.media_route_button);
        castPlayer = new CastPlayer(CastContext.getSharedInstance(context));

        if (!chromecast) {
            mediaRouteButton.setVisibility(View.GONE);
        } else {
            CastButtonFactory.setUpMediaRouteButton(context, mediaRouteButton);

            if (castContext.getCastState() != CastState.NO_DEVICES_AVAILABLE) mediaRouteButton.setVisibility(View.VISIBLE);

            castContext.addCastStateListener(
                state -> {
                    if (state == CastState.NO_DEVICES_AVAILABLE) {
                        mediaRouteButton.setVisibility(View.GONE);
                    } else {
                        if (mediaRouteButton.getVisibility() == View.GONE) {
                            mediaRouteButton.setVisibility(View.VISIBLE);
                        }
                    }
                }
            );

            String castTitle = "";
            if (title != "") {
                castTitle = title;
                if (smallTitle != "") castTitle = title + " (" + smallTitle + ")";
            } else {
                if (smallTitle != "") castTitle = smallTitle;
            }

            MediaMetadata movieMetadata = new MediaMetadata.Builder().setTitle(castTitle).build();

            mediaItem =
                new MediaItem.Builder().setUri(videoPath).setMimeType(MimeTypes.VIDEO_UNKNOWN).setMediaMetadata(movieMetadata).build();

            castPlayer.setSessionAvailabilityListener(
                new SessionAvailabilityListener() {
                    @Override
                    public void onCastSessionAvailable() {
                        final Long videoPosition = player.getCurrentPosition();
                        if (pipEnabled) {
                            pipBtn.setClickable(false);
                        }
                        resizeBtn.setClickable(false);
                        player.setPlayWhenReady(false);
                        cast_message.setVisibility(View.VISIBLE);
                        playerView.setPlayer(castPlayer);
                        castPlayer.setMediaItem(mediaItem, videoPosition);
                        Toast.makeText(context, "Casting started", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCastSessionUnavailable() {
                        final Long videoPosition = castPlayer.getCurrentPosition();
                        if (pipEnabled) {
                            pipBtn.setClickable(true);
                        }
                        resizeBtn.setClickable(true);
                        cast_message.setVisibility(View.GONE);
                        playerView.setPlayer(player);
                        player.setPlayWhenReady(true);
                        player.seekTo(videoPosition);
                    }
                }
            );
        }

        if (title != "") {
            header_tv.setText(title);
        }
        if (smallTitle != "") {
            header_below.setText(smallTitle);
        }
        if (accentColor != "") {
            Pbar.getIndeterminateDrawable().setColorFilter(Color.parseColor(accentColor), android.graphics.PorterDuff.Mode.MULTIPLY);
            exo_progress.setPlayedColor(Color.parseColor(accentColor));
            exo_progress.setScrubberColor(Color.parseColor(accentColor));
        }

        closeBtn = view.findViewById(R.id.exo_close);
        pipBtn = view.findViewById(R.id.exo_pip);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || !pipEnabled) {
            pipBtn.setVisibility(View.GONE);
        }
        playerView.requestFocus();
        linearLayout.setVisibility(View.INVISIBLE);
        playerView.setControllerShowTimeoutMs(3000);
        playerView.setControllerVisibilityListener(
            new PlayerControlView.VisibilityListener() {
                @Override
                public void onVisibilityChange(int visibility) {
                    linearLayout.setVisibility(visibility);
                }
            }
        );
        // Listening for events
        playbackStateListener = new PlaybackStateListener();
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
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            if (savedInstanceState != null) {
                mCurrentPosition = savedInstanceState.getInt(PLAYBACK_TIME);
            }

            getActivity()
                .runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
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
                                            if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
                                                Log.v(TAG, "$$$$ Going to backpress $$$$");
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

                            closeBtn.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        playerExit();
                                    }
                                }
                            );
                            pipBtn.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        pictureInPictureMode();
                                    }
                                }
                            );
                            resizeBtn.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        resizePressed();
                                    }
                                }
                            );
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
        if (
            !isInPictureInPictureMode &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
            packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE) &&
            isPIPModeeEnabled &&
            pipEnabled
        ) {
            pictureInPictureMode();
        } else {
            playerExit();
        }
    }

    private void resizePressed() {
        if (toastMessage != null) toastMessage.cancel();

        if (resizeStatus == AspectRatioFrameLayout.RESIZE_MODE_FIT) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
            resizeStatus = AspectRatioFrameLayout.RESIZE_MODE_FILL;
            resizeBtn.setImageResource(R.drawable.ic_zoom);
            toastMessage = Toast.makeText(context, "Mode Fill", Toast.LENGTH_SHORT);
            toastMessage.show();
        } else if (resizeStatus == AspectRatioFrameLayout.RESIZE_MODE_FILL) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
            resizeStatus = AspectRatioFrameLayout.RESIZE_MODE_ZOOM;
            resizeBtn.setImageResource(R.drawable.ic_fit);
            toastMessage = Toast.makeText(context, "Mode Zoom", Toast.LENGTH_SHORT);
            toastMessage.show();
        } else {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            resizeStatus = AspectRatioFrameLayout.RESIZE_MODE_FIT;
            resizeBtn.setImageResource(R.drawable.ic_expand);
            toastMessage = Toast.makeText(context, "Mode Fit", Toast.LENGTH_SHORT);
            toastMessage.show();
        }
    }

    private void playerExit() {
        Map<String, Object> info = new HashMap<String, Object>() {
            {
                put("dismiss", "1");
            }
        };
        if (player != null) {
            player.seekTo(0);
            player.setVolume(curVolume);
        }
        releasePlayer();
        NotificationCenter.defaultCenter().postNotification("playerFullscreenDismiss", info);
    }

    /**
     * Perform pictureInPictureMode Action
     */
    private void pictureInPictureMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
            playerView.setUseController(false);
            linearLayout.setVisibility(View.INVISIBLE);
            // require android O or higher
            if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
            ) {
                pictureInPictureParams = new PictureInPictureParams.Builder();
                // setup height and width of the PIP window
                Rational aspectRatio = new Rational(playerView.getWidth(), playerView.getHeight());
                pictureInPictureParams.setAspectRatio(aspectRatio).build();
                getActivity().enterPictureInPictureMode(pictureInPictureParams.build());
            } else {
                getActivity().enterPictureInPictureMode();
            }
            isInPictureInPictureMode = getActivity().isInPictureInPictureMode();
            if (sturi != null) {
                setSubtitle(true);
            }
            if (player != null) play();

            handler.postDelayed(mRunnable, 100);
        } else {
            Log.v(TAG, "pictureInPictureMode: doesn't support PIP");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void checkPIPPermission() {
        isPIPModeeEnabled = isInPictureInPictureMode;
        if (!isInPictureInPictureMode) {
            backPressed();
        }
    }

    /**
     * Perform onStart Action
     */
    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24) {
            if (playerView != null) {
                // If cast is playing then it doesn't start the local player once get backs from background
                if (!castPlayer.isCastSessionAvailable()) {
                    initializePlayer();

                    if (player.getCurrentPosition() != 0) {
                        firstReadyToPlay = false;
                        play();
                    }
                }
            } else {
                getActivity().finishAndRemoveTask();
            }
        }
    }

    /**
     * Perform onStop Action
     */
    @Override
    public void onStop() {
        super.onStop();
        boolean isAppBackground = false;
        if (bkModeEnabled) isAppBackground = isApplicationSentToBackground(context);
        if (isInPictureInPictureMode) {
            linearLayout.setVisibility(View.VISIBLE);
            playerExit();
            getActivity().finishAndRemoveTask();
        } else {
            /*          if (!isAppBackground) {
                if (Util.SDK_INT >= 24) {
                    if (player != null) {
                        player.seekTo(0);
                        player.setVolume(curVolume);
                    }
                    releasePlayer();
                }
            }
*/
        }
    }

    /**
     * Perform onDestroy Action
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    /**
     * Perform onPause Action
     */
    @Override
    public void onPause() {
        super.onPause();
        boolean isAppBackground = false;
        if (bkModeEnabled) isAppBackground = isApplicationSentToBackground(context);

        if (!isInPictureInPictureMode) {
            if (Util.SDK_INT < 24) {
                if (player != null) player.setPlayWhenReady(false);
                releasePlayer();
            } else {
                if (isAppBackground) {
                    if (player != null) {
                        if (player.isPlaying()) play();
                    }
                } else {
                    pause();
                }
            }
        } else {
            if (linearLayout.getVisibility() == View.VISIBLE) {
                linearLayout.setVisibility(View.INVISIBLE);
            }
            if ((isInPictureInPictureMode || isAppBackground) && player != null) play();
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
            mediaSessionConnector.setPlayer(null);
            mediaSession.setActive(false);
            player.setRepeatMode(player.REPEAT_MODE_OFF);
            player.removeListener(playbackStateListener);
            player.release();
            player = null;
            showSystemUI();
            resetVariables();
            castPlayer.release();
        }
    }

    /**
     * Perform onResume Action
     */
    @Override
    public void onResume() {
        super.onResume();
        if (!isInPictureInPictureMode) {
            hideSystemUi();
            if ((Util.SDK_INT < 24 || player == null)) {
                initializePlayer();
            }
        } else {
            isInPictureInPictureMode = false;
            if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
            ) {
                playerView.setUseController(true);
            }
            if (sturi != null) {
                setSubtitle(false);
            }
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
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.VISIBLE);
    }

    /**
     * Initialize the player
     */
    private void initializePlayer() {
        if (player == null) {
            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(context).build();
            ExoTrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
            trackSelector = new DefaultTrackSelector(context, videoTrackSelectionFactory);
            LoadControl loadControl = new DefaultLoadControl();
            player =
                new ExoPlayer.Builder(context)
                    .setSeekBackIncrementMs(10000)
                    .setSeekForwardIncrementMs(10000)
                    .setTrackSelector(trackSelector)
                    .setLoadControl(loadControl)
                    .setBandwidthMeter(bandwidthMeter)
                    .build();
        }

        playerView.setPlayer(player);

        MediaSource mediaSource;
        if (!isInternal) {
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
            if (loopOnEnd) {
                player.setRepeatMode(player.REPEAT_MODE_ONE);
            } else {
                player.setRepeatMode(player.REPEAT_MODE_OFF);
            }
        }
        Map<String, Object> info = new HashMap<String, Object>() {
            {
                put("fromPlayerId", playerId);
            }
        };
        if (sturi != null) {
            setSubtitle(false);
        }
        //Use Media Session Connector from the EXT library to enable MediaSession Controls in PIP.
        mediaSession = new MediaSessionCompat(context, "capacitorvideoplayer");
        mediaSessionConnector = new MediaSessionConnector(mediaSession);
        mediaSessionConnector.setPlayer(player);
        mediaSession.setActive(true);

        NotificationCenter.defaultCenter().postNotification("initializePlayer", info);
    }

    private void setSubtitle(boolean transparent) {
        int foreground;
        int background;
        if (!transparent) {
            foreground = Color.WHITE;
            background = Color.BLACK;
            if (stForeColor.length() > 4 && stForeColor.substring(0, 4).equals("rgba")) {
                foreground = getColorFromRGBA(stForeColor);
            }
            if (stBackColor.length() > 4 && stBackColor.substring(0, 4).equals("rgba")) {
                background = getColorFromRGBA(stBackColor);
            }
        } else {
            foreground = Color.TRANSPARENT;
            background = Color.TRANSPARENT;
        }
        playerView
            .getSubtitleView()
            .setStyle(
                new CaptionStyleCompat(foreground, background, Color.TRANSPARENT, CaptionStyleCompat.EDGE_TYPE_NONE, Color.WHITE, null)
            );
        playerView.getSubtitleView().setFixedTextSize(TypedValue.COMPLEX_UNIT_DIP, stFontSize);
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

        DefaultHttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory();
        httpDataSourceFactory.setUserAgent("jeep-exoplayer-plugin");
        httpDataSourceFactory.setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS);
        httpDataSourceFactory.setReadTimeoutMs(1800000);
        httpDataSourceFactory.setAllowCrossProtocolRedirects(true);

        // If headers is not null and has data we pass them to the HttpDataSourceFactory
        if (headers != null && headers.length() > 0) {
            // We map the headers(JSObject) to a Map<String, String>
            Map<String, String> headersMap = new HashMap<String, String>();
            for (int i = 0; i < headers.names().length(); i++) {
                try {
                    headersMap.put(headers.names().getString(i), headers.get(headers.names().getString(i)).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            httpDataSourceFactory.setDefaultRequestProperties(headersMap);
        }

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

        MediaItem.SubtitleConfiguration subConfig = new MediaItem.SubtitleConfiguration.Builder(sturi)
            .setMimeType(mimeType)
            .setUri(sturi)
            .setSelectionFlags(Format.NO_VALUE)
            .setLanguage(language)
            .build();

        SingleSampleMediaSource subtitleSource = new SingleSampleMediaSource.Factory(dataSourceFactory)
            .createMediaSource(subConfig, C.TIME_UNSET);

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
            play();
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
            play();
        }
    }

    /**
     * Play Pause TV
     */
    private void play_pause() {
        player.setPlayWhenReady(!player.isPlaying());
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
        PlaybackParameters param = new PlaybackParameters(videoRate);
        player.setPlaybackParameters(param);
        player.setPlayWhenReady(true);
    }

    /**
     * Pause the player
     */
    public void pause() {
        if (player != null) player.setPlayWhenReady(false);
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
        if (isInPictureInPictureMode) {
            playerView.setUseController(false);
            linearLayout.setVisibility(View.INVISIBLE);
        }
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
     * Return the player rate
     * @return float
     */
    public float getRate() {
        return videoRate;
    }

    /**
     * Set the player rate
     * @param _rate float range [0.25f, 0.5f, 0.75f, 1f, 2f, 4f]
     */
    public void setRate(float _rate) {
        videoRate = _rate;
        PlaybackParameters param = new PlaybackParameters(videoRate);
        player.setPlaybackParameters(param);
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
                    Toast.makeText(context, "Video could not be played", Toast.LENGTH_SHORT).show();
                    playerExit();
                    break;
                case ExoPlayer.STATE_BUFFERING:
                    stateString = "ExoPlayer.STATE_BUFFERING -";
                    Pbar.setVisibility(View.VISIBLE);
                    break;
                case ExoPlayer.STATE_READY:
                    stateString = "ExoPlayer.STATE_READY     -";
                    Pbar.setVisibility(View.GONE);
                    playerView.setUseController(true);
                    linearLayout.setVisibility(View.INVISIBLE);
                    Log.v(TAG, "**** in ExoPlayer.STATE_READY firstReadyToPlay " + firstReadyToPlay);

                    if (firstReadyToPlay) {
                        firstReadyToPlay = false;
                        NotificationCenter.defaultCenter().postNotification("playerItemReady", info);
                        play();
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
                    Log.v(TAG, "**** in ExoPlayer.STATE_ENDED going to notify playerItemEnd ");

                    player.seekTo(0);
                    player.setVolume(curVolume);
                    player.setPlayWhenReady(false);
                    if (exitOnEnd) {
                        releasePlayer();
                        NotificationCenter.defaultCenter().postNotification("playerItemEnd", info);
                    }
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
        Object obj = uri.getLastPathSegment();
        String lastSegment = (obj == null) ? "" : uri.getLastPathSegment();
        for (String type : supportedFormat) {
            if (ret != null) break;
            if(lastSegment.length() > 0 && lastSegment.contains(type)) ret = type;
            if (ret == null) {
                List<String> segments = uri.getPathSegments();
                if(segments.size() > 0) {
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
        }
        ret = (ret != null) ? ret : "";
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
        List<ActivityManager.RunningAppProcessInfo> procInfos = am.getRunningAppProcesses();
        if (procInfos != null) {
            for (ActivityManager.RunningAppProcessInfo appProcess : procInfos) {
                if (appProcess.pid == pid) {
                    return true;
                }
            }
        }
        return false;
    }
}
