package com.jeep.plugin.capacitor.capacitorvideoplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.PictureInPictureParams;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.mediarouter.app.MediaRouteButton;
import androidx.mediarouter.media.MediaControlIntent;
import androidx.mediarouter.media.MediaRouteSelector;
import androidx.mediarouter.media.MediaRouter;
import com.getcapacitor.JSObject;
import androidx.media3.common.C;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.LoadControl;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.Player;
import androidx.media3.common.AudioAttributes;
import androidx.media3.cast.CastPlayer;
import androidx.media3.cast.SessionAvailabilityListener;
import androidx.media3.session.MediaSession;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.MergingMediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.exoplayer.source.SingleSampleMediaSource;
import androidx.media3.exoplayer.dash.DashMediaSource;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.exoplayer.smoothstreaming.SsMediaSource;
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.trackselection.ExoTrackSelection;
import androidx.media3.exoplayer.trackselection.TrackSelector;
import androidx.media3.ui.AspectRatioFrameLayout;
import androidx.media3.ui.CaptionStyleCompat;
import androidx.media3.ui.DefaultTimeBar;
import androidx.media3.ui.PlayerControlView;
import androidx.media3.ui.PlayerView;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.common.VideoSize;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.util.Util;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.jeep.plugin.capacitor.capacitorvideoplayer.Notifications.NotificationCenter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.json.JSONException;

public class FullscreenExoPlayerFragment extends Fragment {

  public String videoPath = "";
  public Float videoRate = 1.0f;
  public String playerId = "";
  public String subTitle = "";
  public String language = "";
  public JSObject subTitleOptions = new JSObject();
  public JSObject headers = new JSObject();
  public Boolean isTV = false;
  public Boolean isInternal = false;
  public Long videoId = 0L;
  public Boolean exitOnEnd = true;
  public Boolean loopOnEnd = false;
  public Boolean pipEnabled = true;
  public Boolean bkModeEnabled = true;
  public Boolean showControls = true;
  public String displayMode = "all";
  public String title = "";
  public String smallTitle = "";
  public String accentColor = "";
  public Boolean chromecast = true;
  public String artwork = "";

  private static final String TAG = FullscreenExoPlayerFragment.class.getName();
  public static final long UNKNOWN_TIME = -1L;
  private final List<String> supportedFormat = Arrays.asList(
    new String[] { "mp4", "webm", "ogv", "3gp", "flv", "dash", "mpd", "m3u8", "ism", "ytube", "" }
  );
  private Player.Listener listener;
  private PlayerView styledPlayerView;
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
  private static ImageView cast_image;
  private DefaultTimeBar exo_progress;
  private TextView exo_position;
  private TextView exo_duration;
  private TextView exo_label_separation;
  private TextView live_text;
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
  private boolean isCastSession = false;

  // Tag for the instance state bundle.
  private static final String PLAYBACK_TIME = "play_time";

  private PictureInPictureParams.Builder pictureInPictureParams;
  private MediaSession mediaSession;
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
  private MediaRouteButton mediaRouteButton;
  private CastContext castContext;
  private CastPlayer castPlayer;
  private MediaItem mediaItem;
  private MediaRouter mRouter;
  private MediaRouter.Callback mCallback = new EmptyCallback();
  private MediaRouteSelector mSelector;
  private CastStateListener castStateListener = null;
  private Boolean playerReady = false;

  // Thread safety and lifecycle management
  private boolean isFragmentDestroyed = false;
  private Handler mainHandler;

  /**
   * Create Fragment View
   * @param inflater
   * @param container
   * @param savedInstanceState
   * @return View
   */
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    
    context = requireContext();
    
    packageManager = context.getPackageManager();

    // Check if this fragment is an orphaned recreation (e.g. after process death / activity recreation)
    // where no video path or internal media has been provided by the plugin.
    boolean isInternalVideo = Boolean.TRUE.equals(isInternal);
    if ((videoPath == null || videoPath.trim().isEmpty()) && !isInternalVideo) {
      Log.w(TAG, "FullscreenExoPlayerFragment recreated without video source; removing orphaned fragment.");
      try {
        getParentFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
      } catch (Exception e) {
        Log.e(TAG, "Failed to remove orphaned fragment", e);
      }
      return new View(context);
    }
    
    // Initialize thread safety components
    mainHandler = new Handler(Looper.getMainLooper());
    isFragmentDestroyed = false;
    
    view = inflater.inflate(R.layout.fragment_fs_exoplayer, container, false);
    constLayout = view.findViewById(R.id.fsExoPlayer);
    linearLayout = view.findViewById(R.id.linearLayout);
    styledPlayerView = view.findViewById(R.id.videoViewId);
    header_tv = view.findViewById(R.id.header_tv);
    header_below = view.findViewById(R.id.header_below);
    Pbar = view.findViewById(R.id.indeterminateBar);
    exo_progress = view.findViewById(R.id.exo_progress);
    exo_progress.setVisibility(View.GONE);
    exo_position = view.findViewById(R.id.exo_position);
    exo_position.setVisibility(View.GONE);
    exo_duration = view.findViewById(R.id.exo_duration);
    exo_duration.setVisibility(View.GONE);
    exo_label_separation = view.findViewById(R.id.exo_label_separation);
    exo_label_separation.setVisibility(View.GONE);
    live_text = view.findViewById(R.id.live_text);
    resizeBtn = view.findViewById(R.id.exo_resize);
    cast_image = view.findViewById(R.id.cast_image);
    mediaRouteButton = view.findViewById(R.id.media_route_button);
    styledPlayerView.setShowPreviousButton(false);
    styledPlayerView.setShowNextButton(false);
    styledPlayerView.setShowFastForwardButton(false);
    styledPlayerView.setShowRewindButton(false);

    Activity mAct = requireActivity();
    if ("landscape".equalsIgnoreCase(displayMode)) {
      mAct.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
    if ("portrait".equalsIgnoreCase(displayMode)) {
      mAct.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    boolean isShowControls = showControls != null ? showControls : true;
    if (!isShowControls) {
      styledPlayerView.setUseController(false);
    } else {
      styledPlayerView.setUseController(true);
    }

    boolean isChromecast = chromecast != null ? chromecast : true;
    if (!isChromecast) {
      mediaRouteButton.setVisibility(View.GONE);
    } else {
      initializeCastService();
    }

    if (title != null && !title.trim().isEmpty()) {
      header_tv.setText(title);
    }
    if (smallTitle != null && !smallTitle.trim().isEmpty()) {
      header_below.setText(smallTitle);
    }
    if (accentColor != null && !accentColor.trim().isEmpty()) {
      try {
        int parsedColor = Color.parseColor(accentColor);
        Pbar.getIndeterminateDrawable().setColorFilter(parsedColor, android.graphics.PorterDuff.Mode.MULTIPLY);
        exo_progress.setPlayedColor(parsedColor);
        exo_progress.setScrubberColor(parsedColor);
      } catch (Exception e) {
        Log.e(TAG, "Invalid accent color: " + accentColor, e);
      }
    }

    closeBtn = view.findViewById(R.id.exo_close);
    pipBtn = view.findViewById(R.id.exo_pip);
    styledPlayerView.requestFocus();
    linearLayout.setVisibility(View.INVISIBLE);
    styledPlayerView.setControllerShowTimeoutMs(3000);
    styledPlayerView.setControllerVisibilityListener(
      new PlayerView.ControllerVisibilityListener() {
        @Override
        public void onVisibilityChanged(int visibility) {
          if (linearLayout != null) {
            linearLayout.setVisibility(visibility);
          }
        }
      }
    );

    listener =
      new Player.Listener() {
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int state) {
          if (isFragmentDestroyed || player == null || styledPlayerView == null) {
            return;
          }
          String stateString;
          Map<String, Object> info = new HashMap<String, Object>() {
            {
              put("fromPlayerId", playerId);
              put("currentTime", String.valueOf(player.getCurrentPosition() / 1000));
            }
          };

          switch (state) {
            case ExoPlayer.STATE_IDLE:
              stateString = "ExoPlayer.STATE_IDLE      -";
              Toast.makeText(context, "Video Url not found", Toast.LENGTH_SHORT).show();
              playerExit();
              break;
            case ExoPlayer.STATE_BUFFERING:
              stateString = "ExoPlayer.STATE_BUFFERING -";
              Pbar.setVisibility(View.VISIBLE);
              break;
            case ExoPlayer.STATE_READY:
              stateString = "ExoPlayer.STATE_READY     -";
              Pbar.setVisibility(View.GONE);
              playerReady = true;
              boolean isShowControls = showControls != null ? showControls : true;
              if (!isShowControls) {
                styledPlayerView.setUseController(false);
              } else {
                styledPlayerView.setUseController(true);
              }
              linearLayout.setVisibility(View.INVISIBLE);
              Log.v(TAG, "**** in ExoPlayer.STATE_READY firstReadyToPlay " + firstReadyToPlay);

              if (firstReadyToPlay) {
                firstReadyToPlay = false;
                NotificationCenter.defaultCenter().postNotification("playerItemReady", info);
                play();
                Log.v(TAG, "**** in ExoPlayer.STATE_READY firstReadyToPlay player.isPlaying" + player.isPlaying());
                player.seekTo(currentWindow, playbackPosition);

                // We show progress bar, position and duration only when the video is not live
                if (!player.isCurrentMediaItemLive()) {
                  exo_progress.setVisibility(View.VISIBLE);
                  exo_position.setVisibility(View.VISIBLE);
                  exo_duration.setVisibility(View.VISIBLE);
                  exo_label_separation.setVisibility(View.VISIBLE);
                  styledPlayerView.setShowFastForwardButton(true);
                  styledPlayerView.setShowRewindButton(true);
                } else {
                  live_text.setVisibility(View.VISIBLE);
                }
              } else {
                Log.v(TAG, "**** in ExoPlayer.STATE_READY isPlaying " + player.isPlaying());
                if (player.isPlaying()) {
                  Log.v(TAG, "**** in ExoPlayer.STATE_READY going to notify playerItemPlay ");
                  NotificationCenter.defaultCenter().postNotification("playerItemPlay", info);
                  resizeBtn.setVisibility(View.VISIBLE);

                  boolean isPip = Boolean.TRUE.equals(pipEnabled);
                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isPip) {
                    pipBtn.setVisibility(View.VISIBLE);
                  }
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
              boolean isExitOnEnd = exitOnEnd != null ? exitOnEnd : true;
              if (isExitOnEnd) {
                releasePlayer();
/*
                Activity mAct = getActivity();
                int mOrient = mAct.getRequestedOrientation();
                if (mOrient == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                  mAct.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
*/
                NotificationCenter.defaultCenter().postNotification("playerItemEnd", info);
              }
              break;
            default:
              stateString = "UNKNOWN_STATE             -";
              break;
          }
        }
      };

    boolean isDeviceTV = Boolean.TRUE.equals(isTV);
    if (isDeviceTV) {
      Toast.makeText(context, "Device is a TV ", Toast.LENGTH_SHORT).show();
    }

    if (!Boolean.TRUE.equals(isInternal)) {
      if (videoPath != null && !videoPath.trim().isEmpty()) {
        uri = Uri.parse(videoPath);
      }
      sturi = (subTitle != null && !subTitle.trim().isEmpty()) ? Uri.parse(subTitle) : null;

      if (subTitleOptions != null) {
        stForeColor = subTitleOptions.has("foregroundColor") ? subTitleOptions.getString("foregroundColor") : "rgba(255,255,255,1)";
        stBackColor = subTitleOptions.has("backgroundColor") ? subTitleOptions.getString("backgroundColor") : "rgba(0,0,0,1)";
        stFontSize = subTitleOptions.has("fontSize") ? subTitleOptions.getInteger("fontSize") : 16;
      }
      // get video type
      if (uri != null) {
        vType = getVideoType(uri);
      }
      Log.v(TAG, "display url: " + uri);
      Log.v(TAG, "display subtitle url: " + sturi);
      Log.v(TAG, "display isTV: " + isTV);
      Log.v(TAG, "display vType: " + vType);
    }
    if (uri != null || Boolean.TRUE.equals(isInternal)) {
      // go fullscreen
      Activity hostActivity = getActivity();
      if (hostActivity != null && hostActivity.getWindow() != null) {
        hostActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hostActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
      }
      if (savedInstanceState != null) {
        mCurrentPosition = savedInstanceState.getInt(PLAYBACK_TIME);
      }

      if (hostActivity != null) {
        hostActivity.runOnUiThread(
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
                      Log.v(TAG, "$$$$ onKey " + keyCode + " $$$$");
                      if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
                        Log.v(TAG, "$$$$ Going to backpress $$$$");
                        backPressed();
                      } else if (Boolean.TRUE.equals(isTV)) {
                        long videoPosition = (player == null) ? 0 : player.getCurrentPosition();
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
        }
      } else {
        Log.d(TAG, "Video path wrong or type not supported");
        Toast.makeText(context, "Video path wrong or type not supported", Toast.LENGTH_SHORT).show();
      }
    adjustAspectRatio();
    return view;
  }

  /**
   * Sets the cast image in playerView when it is connected to a cast device
   */
  private class setCastImage extends AsyncTask<Void, Void, Bitmap> {

    protected Bitmap doInBackground(Void... params) {
      final String image = artwork;
      if (image != null && !image.trim().isEmpty()) {
        try {
          URL url = new URL(image);
          HttpURLConnection connection = (HttpURLConnection) url.openConnection();
          connection.setDoInput(true);
          connection.connect();
          InputStream input = connection.getInputStream();
          Bitmap myBitmap = BitmapFactory.decodeStream(input);
          return myBitmap;
        } catch (IOException | IllegalArgumentException e) {
          e.printStackTrace();
          return null;
        }
      } else {
        return null;
      }
    }

    @Override
    protected void onPostExecute(Bitmap result) {
      if (cast_image != null) {
        cast_image.setImageBitmap(result);
      }
    }
  }

  /**
   * Show controller
   */
  public void showController() {
    if (styledPlayerView != null) {
      styledPlayerView.showController();
    }
  }

  /**
   * isControllerIsFullyVisible
   */
  public boolean isControllerIsFullyVisible() {
    return styledPlayerView != null && styledPlayerView.isControllerFullyVisible();
  }

  /**
   * Check if player operations are safe to perform
   */
  private boolean isPlayerOperationSafe() {
    return !isFragmentDestroyed && 
           player != null && 
           getActivity() != null && 
           !getActivity().isFinishing() && 
           !getActivity().isDestroyed() &&
           isAdded();
  }

  /**
   * Perform backPressed Action
   */
  private void backPressed() {
    if (isCastSession) {
      playerExit();
      return;
    }
    if (
      !isInPictureInPictureMode &&
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
        packageManager != null &&
        packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE) &&
        Boolean.TRUE.equals(isPIPModeeEnabled) &&
        Boolean.TRUE.equals(pipEnabled) &&
        Boolean.TRUE.equals(playerReady) // <- playerReady: this prevents a crash if the user presses back before the player is ready (when enters in pip mode and tries to get the aspect ratio)
    ) {
      pictureInPictureMode();
    } else {
      playerExit();
    }
  }

  private void resizePressed() {
    if (styledPlayerView == null) {
      return;
    }
    if (resizeStatus == AspectRatioFrameLayout.RESIZE_MODE_FIT) {
      styledPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
      resizeStatus = AspectRatioFrameLayout.RESIZE_MODE_FILL;
      if (resizeBtn != null) resizeBtn.setImageResource(R.drawable.ic_zoom);
    } else if (resizeStatus == AspectRatioFrameLayout.RESIZE_MODE_FILL) {
      styledPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
      resizeStatus = AspectRatioFrameLayout.RESIZE_MODE_ZOOM;
      if (resizeBtn != null) resizeBtn.setImageResource(R.drawable.ic_fit);
    } else {
      styledPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
      resizeStatus = AspectRatioFrameLayout.RESIZE_MODE_FIT;
      if (resizeBtn != null) resizeBtn.setImageResource(R.drawable.ic_expand);
    }
  }

  public void playerExit() {
    if (player != null) {
      Map<String, Object> info = new HashMap<String, Object>() {
      {
        put("dismiss", "1");
        put("currentTime", getCurrentTime());
      }
    };
      player.seekTo(0);
      player.setVolume(curVolume);
    releasePlayer();
/* 
    Activity mAct = getActivity();
    int mOrient = mAct.getRequestedOrientation();
    if (mOrient == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
      mAct.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
*/
    // We control if the user lock the screen when the player is in pip mode
    try {
      NotificationCenter.defaultCenter().postNotification("playerFullscreenDismiss", info);
    } catch (Exception e) {
      Log.e(TAG, "Error in posting notification");
    }
    }
  }

  /**
   * Perform pictureInPictureMode Action
   */
  private void pictureInPictureMode() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && packageManager != null && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
      // Store activity reference to avoid multiple getActivity() calls and check for null
      // Fragment may be detached from activity during transitions or configuration changes
      Activity activity = getActivity();
      if (activity == null) {
        Log.v(TAG, "pictureInPictureMode: activity is null");
        return;
      }
      if (styledPlayerView != null) {
        styledPlayerView.setUseController(false);
        styledPlayerView.setControllerAutoShow(false);
      }
      if (linearLayout != null) {
        linearLayout.setVisibility(View.INVISIBLE);
      }
      Log.v(TAG, "PIP break 1");
      // require android O or higher
      if (
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
      ) {
        pictureInPictureParams = new PictureInPictureParams.Builder();
        // setup aspect ratio of the PIP window; fall back to 16:9 for audio-only or not-yet-decoded video
        if (player != null) {
          VideoSize videoSize = player.getVideoSize();
          if (videoSize != VideoSize.UNKNOWN) {
            pictureInPictureParams.setAspectRatio(new Rational(videoSize.width, videoSize.height));
          } else {
            pictureInPictureParams.setAspectRatio(new Rational(16, 9));
          }
        } else {
          pictureInPictureParams.setAspectRatio(new Rational(16, 9));
        }

        // Catch IllegalStateException that occurs when:
        // 1. Activity doesn't have android:supportsPictureInPicture="true" in manifest
        // 2. Device doesn't support PIP (even though PackageManager check passed)
        // 3. PIP is disabled in system settings
        // This prevents crashes on devices with PIP hardware support but missing app configuration
        try {
          activity.enterPictureInPictureMode(pictureInPictureParams.build());
        } catch (IllegalStateException e) {
          Log.v(TAG, "pictureInPictureMode: failed to enter PIP", e);
          return;
        }
        Log.v(TAG, "PIP break 2");
      } else {
        try {
          activity.enterPictureInPictureMode();
        } catch (IllegalStateException e) {
          Log.v(TAG, "pictureInPictureMode: failed to enter PIP", e);
          return;
        }
        Log.v(TAG, "PIP break 3");
      }
      isInPictureInPictureMode = activity.isInPictureInPictureMode();
      if (sturi != null) {
        setSubtitle(true);
      }
      if (player != null) play();

      handler.postDelayed(mRunnable, 100);
      Log.v(TAG, "PIP break 4");
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
    //if (chromecast && castContext != null) mRouter.addCallback(mSelector, mCallback, MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);

    if (Util.SDK_INT >= 24) {
      if (styledPlayerView != null) {
        // If cast is playing then it doesn't start the local player once get backs from background
        if (castContext != null && Boolean.TRUE.equals(chromecast) && castPlayer != null && castPlayer.isCastSessionAvailable()) return;

        initializePlayer();
        if (player != null && player.getCurrentPosition() != 0) {
          firstReadyToPlay = false;
          play();
        }
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
    if (context != null && Boolean.TRUE.equals(bkModeEnabled)) isAppBackground = isApplicationSentToBackground(context);
    if (isInPictureInPictureMode) {
      if (linearLayout != null) {
        linearLayout.setVisibility(View.VISIBLE);
      }
      playerExit();
      Activity activity = getActivity();
      if (activity != null) {
        activity.finishAndRemoveTask();
      }
    }
  }

  /**
   * Perform onDestroy Action
   */
  @Override
  public void onDestroy() {
    // Release player before setting isFragmentDestroyed and calling super to ensure proper cleanup order
    releasePlayer();
    isFragmentDestroyed = true;
    
    // Clean up handlers
    handler.removeCallbacks(mRunnable);
    if (mainHandler != null) {
        mainHandler.removeCallbacksAndMessages(null);
        mainHandler = null;
    }
    
    if (Boolean.TRUE.equals(chromecast) && mRouter != null) mRouter.removeCallback(mCallback);
    
    super.onDestroy();
  }

  /**
   * Perform onDetach Action
   */
  @Override
  public void onDetach() {
    isFragmentDestroyed = true;
    super.onDetach();
  }

  /**
   * Perform onDestroyView Action
   */
  @Override
  public void onDestroyView() {
    if (styledPlayerView != null) {
      styledPlayerView.setPlayer(null);
      styledPlayerView = null;
    }
    super.onDestroyView();
  }

  /**
   * Perform onPause Action
   */
  @Override
  public void onPause() {
    super.onPause();
    if (Boolean.TRUE.equals(chromecast) && castContext != null && castStateListener != null) {
      castContext.removeCastStateListener(castStateListener);
    }
    boolean isAppBackground = false;
    if (context != null && Boolean.TRUE.equals(bkModeEnabled)) isAppBackground = isApplicationSentToBackground(context);

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
      if (linearLayout != null && linearLayout.getVisibility() == View.VISIBLE) {
        linearLayout.setVisibility(View.INVISIBLE);
      }
      if ((isInPictureInPictureMode || isAppBackground) && player != null) play();
    }
  }

  public void releasePlayer() {
    if (isFragmentDestroyed) {
        Log.w("VideoPlayer", "Fragment already destroyed, skipping player release");
        return;
    }
    
    if (player != null) {
        try {
            // Ensure release called from main thread
            // If current thread is main, release, if not post message to main
            if (Looper.myLooper() == Looper.getMainLooper()) {
                releasePlayerInternal();
            } else if (mainHandler != null) {
                mainHandler.post(() -> releasePlayerInternal());
            } else {
                Log.w("VideoPlayer", "Cannot release player from background thread without a handler. Player may not be released properly.");
            }
        } catch (Exception e) {
            Log.e("VideoPlayer", "Error releasing player", e);
            // Force cleanup
            player = null;
            if (castPlayer != null) {
                castPlayer = null;
            }
        }
    }
  }

  private void releasePlayerInternal() {
    if (styledPlayerView != null) {
      styledPlayerView.setPlayer(null);
    }
    if (player != null && !isFragmentDestroyed) {
        try {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();

            // Media3 Migration Note: MediaSession cleanup changed
            // Old: MediaSessionConnector.setPlayer(null) + MediaSessionCompat.setActive(false)
            // New: MediaSession.release() - properly releases all resources and removes session ID
            // Set to null after release to allow recreation with same ID on next initialization
            if (mediaSession != null) {
                mediaSession.release();
                mediaSession = null;
            }

            player.setRepeatMode(Player.REPEAT_MODE_OFF);
            player.removeListener(listener);
            player.stop(); // Add this line to properly stop the player
            player.release();
        } catch (Exception e) {
            Log.e("VideoPlayer", "Error in releasePlayerInternal", e);
        } finally {
            player = null;
        }
        
        showSystemUI();
        resetVariables();
    }
    
    // Move chromecast handling outside the player null check
    // and add null check for castPlayer
    if (Boolean.TRUE.equals(chromecast) && castPlayer != null) {
        try {
            castPlayer.release();
        } catch (Exception e) {
            Log.e("VideoPlayer", "Error releasing cast player", e);
        } finally {
            castPlayer = null;
        }
    }
  }

  /**
   * Perform onResume Action
   */
  @Override
  public void onResume() {
    super.onResume();
    //if (chromecast && castContext != null) castContext.addCastStateListener(castStateListener);
    // Skip resume handling when the view was never inflated (orphaned fragment
    // recreation after process death returns an empty View in onCreateView, so
    // styledPlayerView stays null while async removal is still pending).
    if (styledPlayerView == null) {
      return;
    }
    if (!isInPictureInPictureMode) {
      hideSystemUi();
      if ((Util.SDK_INT < 24 || player == null)) {
        initializePlayer();
      }
    } else {
      isInPictureInPictureMode = false;
      if (
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
        packageManager != null &&
        packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
      ) {
        boolean isShowControls = showControls != null ? showControls : true;
        if (!isShowControls) {
          styledPlayerView.setUseController(false);
        } else {
          styledPlayerView.setUseController(true);
        }
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
    if (styledPlayerView != null) styledPlayerView.setSystemUiVisibility(
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
    Activity activity = getActivity();
    if (activity != null && activity.getWindow() != null) {
      activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
      activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
      View decorView = activity.getWindow().getDecorView();
      if (decorView != null) {
        decorView.setSystemUiVisibility(View.VISIBLE);
      }
    }
  }

  /**
   * Initialize the player
   */
  private void initializePlayer() {
    // Guard against orphaned fragment recreations where onCreateView returned
    // an empty View without inflating the layout (styledPlayerView is null).
    if (styledPlayerView == null) {
      return;
    }
    Context playerContext = context != null ? context : getContext();
    if (playerContext == null) {
      Log.w(TAG, "initializePlayer: context is null, skipping");
      return;
    }
    if (player == null) {
      ExoTrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
      trackSelector = new DefaultTrackSelector(playerContext, videoTrackSelectionFactory);
      LoadControl loadControl = new DefaultLoadControl();

      // Media3 Migration Note: DefaultBandwidthMeter now configured automatically by default
      player =
        new ExoPlayer.Builder(playerContext)
          .setSeekBackIncrementMs(10000)
          .setSeekForwardIncrementMs(10000)
          .setTrackSelector(trackSelector)
          .setLoadControl(loadControl)
          .build();
    }

    styledPlayerView.setPlayer(player);

    MediaSource mediaSource = null;
    if (!Boolean.TRUE.equals(isInternal)) {
      // Re-derive uri/vType from videoPath on every init. They are cleared by
      // resetVariables() after release, and onStart/onResume can re-enter
      // initializePlayer() without going through onCreateView, which was the
      // sole place they were previously computed (NPE on vType.equals()).
      if (uri == null && videoPath != null && !videoPath.trim().isEmpty()) {
        try {
          uri = Uri.parse(videoPath);
        } catch (Exception e) {
          Log.e(TAG, "Invalid video path: " + videoPath, e);
          uri = null;
        }
      }
      if (vType == null && uri != null) {
        vType = getVideoType(uri);
      }
      if (vType == null) {
        vType = "";
      }
      if (videoPath != null && (videoPath.startsWith("file:///android_asset") || videoPath.startsWith("content://media"))) {
        mediaSource = buildAssetMediaSource(uri);
      } else if (videoPath != null && !videoPath.trim().isEmpty()) {
        mediaSource = buildHttpMediaSource();
      }
    } else {
      if (videoId != null && videoId > 0) {
        Uri videoUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoId);
        mediaSource = buildInternalMediaSource(videoUri);
      }
    }

    if (mediaSource != null) {
      player.setAudioAttributes(AudioAttributes.DEFAULT, true);
      player.addListener(listener);
      player.prepare(mediaSource, false, false);
      boolean isLoop = Boolean.TRUE.equals(loopOnEnd);
      if (isLoop) {
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
    // Media3 Migration Note: MediaSessionConnector replaced with MediaSession.Builder; null check prevents duplicate session ID
    if (mediaSession == null) {
      try {
        mediaSession = new MediaSession.Builder(playerContext, player)
          .setId("capacitorvideoplayer")
          .build();
      } catch (Exception e) {
        Log.e(TAG, "Failed to build MediaSession", e);
      }
    }

    NotificationCenter.defaultCenter().postNotification("initializePlayer", info);
  }

  private void setSubtitle(boolean transparent) {
    if (styledPlayerView == null || styledPlayerView.getSubtitleView() == null) {
      return;
    }
    int foreground;
    int background;
    if (!transparent) {
      foreground = Color.WHITE;
      background = Color.BLACK;
      if (stForeColor != null && stForeColor.length() > 4 && stForeColor.substring(0, 4).equals("rgba")) {
        foreground = getColorFromRGBA(stForeColor);
      }
      if (stBackColor != null && stBackColor.length() > 4 && stBackColor.substring(0, 4).equals("rgba")) {
        background = getColorFromRGBA(stBackColor);
      }
    } else {
      foreground = Color.TRANSPARENT;
      background = Color.TRANSPARENT;
    }
    styledPlayerView
      .getSubtitleView()
      .setStyle(
        new CaptionStyleCompat(foreground, background, Color.TRANSPARENT, CaptionStyleCompat.EDGE_TYPE_NONE, Color.WHITE, null)
      );
    styledPlayerView.getSubtitleView().setFixedTextSize(TypedValue.COMPLEX_UNIT_DIP, stFontSize != null ? stFontSize : 16);
    styledPlayerView.setShowSubtitleButton(true);
  }

  /**
   * Build the Asset MediaSource
   */
  private MediaSource buildAssetMediaSource(Uri uri) {
    if (uri == null) {
      return null;
    }
    Context dataContext = context != null ? context : getContext();
    if (dataContext == null) {
      return null;
    }
    MediaSource mediaSource = null;
    // Media3 Migration Note: DefaultDataSourceFactory replaced with DefaultDataSource.Factory
    DataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(dataContext);
    mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri));
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
    if (uri == null) {
      return null;
    }
    Context dataContext = context != null ? context : getContext();
    if (dataContext == null) {
      return null;
    }
    DataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(dataContext);
    return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri));
  }

  /**
   * Build the Http MediaSource
   * @return MediaSource
   */
  private MediaSource buildHttpMediaSource() {
    if (uri == null) {
      Log.e(TAG, "buildHttpMediaSource: uri is null, cannot build media source");
      return null;
    }
    // vType may still be null if getVideoType was never called (e.g. re-init
    // after release). Default to progressive rather than NPE on equals().
    String type = vType != null ? vType : "";
    MediaSource mediaSource = null;

    DefaultHttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory();
    httpDataSourceFactory.setUserAgent("jeep-exoplayer-plugin");
    httpDataSourceFactory.setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS);
    httpDataSourceFactory.setReadTimeoutMs(1800000);
    httpDataSourceFactory.setAllowCrossProtocolRedirects(true);

    // If headers is not null and has data we pass them to the HttpDataSourceFactory
    if (headers != null) {
      try {
        if (headers.length() > 0 && headers.names() != null) {
          // We map the headers(JSObject) to a Map<String, String>
          Map<String, String> headersMap = new HashMap<String, String>();
          for (int i = 0; i < headers.names().length(); i++) {
            try {
              String key = headers.names().getString(i);
              Object value = headers.get(key);
              if (key != null && value != null) {
                headersMap.put(key, value.toString());
              }
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
          httpDataSourceFactory.setDefaultRequestProperties(headersMap);
        }
      } catch (Exception e) {
        Log.e(TAG, "Error applying headers", e);
      }
    }

    DataSource.Factory dataSourceFactory;
    Context dataContext = context != null ? context : getContext();
    if (dataContext == null) {
      Log.e(TAG, "buildHttpMediaSource: context is null, cannot build media source");
      return null;
    }
    dataSourceFactory = new DefaultDataSource.Factory(dataContext, httpDataSourceFactory);

    if (
      "mp4".equals(type) ||
        "webm".equals(type) ||
        "ogv".equals(type) ||
        "3gp".equals(type) ||
        "flv".equals(type) ||
        "".equals(type)
    ) {
      mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri));
    } else if ("dash".equals(type) || "mpd".equals(type)) {
      /* adaptive streaming Dash stream */
      DashMediaSource.Factory mediaSourceFactory = new DashMediaSource.Factory(dataSourceFactory);
      mediaSource = mediaSourceFactory.createMediaSource(MediaItem.fromUri(uri));
    } else if ("m3u8".equals(type)) {
      mediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri));
    } else if ("ism".equals(type)) {
      mediaSource = new SsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri));
    } else {
      Log.w(TAG, "buildHttpMediaSource: unsupported video type '" + type + "', falling back to progressive");
      mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri));
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
    if (rgbaColor == null || !rgbaColor.contains("(") || !rgbaColor.contains(")")) {
      return ret;
    }
    try {
      String color = rgbaColor.substring(rgbaColor.indexOf("(") + 1, rgbaColor.indexOf(")"));
      List<String> colors = Arrays.asList(color.split(","));
      if (colors.size() == 4) {
        ret =
          (Math.round(Float.parseFloat(colors.get(3).trim()) * 255) & 0xff) << 24 |
            (Integer.parseInt(colors.get(0).trim()) & 0xff) << 16 |
            (Integer.parseInt(colors.get(1).trim()) & 0xff) << 8 |
            (Integer.parseInt(colors.get(2).trim()) & 0xff);
      }
    } catch (Exception e) {
      Log.e(TAG, "Error parsing rgbaColor: " + rgbaColor, e);
    }
    return ret;
  }

  private MediaSource getSubTitle(MediaSource mediaSource, Uri sturi, DataSource.Factory dataSourceFactory) {
    if (mediaSource == null || sturi == null) {
      return mediaSource;
    }
    // Create mediaSource with subtitle
    MediaSource[] mediaSources = new MediaSource[2];
    mediaSources[0] = mediaSource;
    String mimeType = getMimeType(sturi);
    if (mimeType == null || mimeType.isEmpty()) {
      Log.w(TAG, "getSubTitle: unknown subtitle mime type, skipping subtitles");
      return mediaSource;
    }

    // We get the language label from the language code
    String safeLanguage = language != null ? language : "";
    String languageLabel;
    try {
      languageLabel = Locale.forLanguageTag(safeLanguage).getDisplayLanguage();
    } catch (Exception e) {
      Log.e(TAG, "Invalid language tag: " + safeLanguage, e);
      languageLabel = safeLanguage;
    }
    MediaItem.SubtitleConfiguration subConfig = new MediaItem.SubtitleConfiguration.Builder(sturi)
      .setMimeType(mimeType)
      .setUri(sturi)
      .setId(subTitle)
      .setLabel(languageLabel)
      .setRoleFlags(C.ROLE_FLAG_CAPTION)
      .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
      .setLanguage(safeLanguage)
      .build();

    SingleSampleMediaSource subtitleSource = new SingleSampleMediaSource.Factory(dataSourceFactory)
      .createMediaSource(subConfig, C.TIME_UNSET);

    mediaSources[1] = subtitleSource;

    mediaSource = new MergingMediaSource(mediaSources);
    return mediaSource;
  }

  private String getMimeType(Uri sturi) {
    if (sturi == null) {
      return "";
    }
    String lastSegment;
    try {
      lastSegment = sturi.getLastPathSegment();
    } catch (Exception e) {
      Log.e(TAG, "getMimeType: failed to get last path segment", e);
      return "";
    }
    if (lastSegment == null || lastSegment.isEmpty()) {
      return "";
    }
    int dotIndex = lastSegment.lastIndexOf(".");
    String extension = (dotIndex != -1 ? lastSegment.substring(dotIndex + 1) : lastSegment).toLowerCase(Locale.ROOT);
    String mimeType = "";
    if ("vtt".equals(extension)) {
      mimeType = MimeTypes.TEXT_VTT;
    } else if ("srt".equals(extension)) {
      mimeType = MimeTypes.APPLICATION_SUBRIP;
    } else if ("ssa".equals(extension) || "ass".equals(extension)) {
      mimeType = MimeTypes.TEXT_SSA;
    } else if ("ttml".equals(extension) || "dfxp".equals(extension) || "xml".equals(extension)) {
      mimeType = MimeTypes.APPLICATION_TTML;
    }
    return mimeType;
  }

  /**
   * Fast Forward TV
   */
  private void fastForward(long position, int times) {
    if (player != null && position < mDuration - videoStep) {
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
    if (player != null && position > videoStep) {
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
    if (player != null) {
      player.setPlayWhenReady(!player.isPlaying());
    }
  }

  /**
   * Check if the player is playing
   * @return boolean
   */
  public boolean isPlaying() {
    return player != null && player.isPlaying();
  }

  /**
   * Start the player
   */
  public void play() {
    if (isFragmentDestroyed || player == null) {
        Log.w("VideoPlayer", "Cannot play - fragment destroyed or player null");
        return;
    }
    
    try {
        // Ensure we're on the main thread
        if (Looper.myLooper() != Looper.getMainLooper()) {
            if (mainHandler != null) {
                mainHandler.post(() -> playInternal());
            }
            return;
        }
        
        playInternal();
    } catch (Exception e) {
        Log.e("VideoPlayer", "Error during play operation", e);
    }
  }

  private void playInternal() {
    if (player != null && !isFragmentDestroyed) {
        try {
            PlaybackParameters param = new PlaybackParameters(videoRate != null ? videoRate : 1.0f);
            player.setPlaybackParameters(param);

            /* If the user start the cast before the player is ready and playing, then the video will start
              in the device and chromecast at the same time. This is to avoid that behaviour.*/
            if (!isCastSession) {
                player.setPlayWhenReady(true);
            }
        } catch (Exception e) {
            Log.e("VideoPlayer", "Error in playInternal", e);
        }
    }
  }

  /**
   * Pause the player
   */
  public void pause() {
    if (!isPlayerOperationSafe()) {
        return;
    }
    
    try {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            if (mainHandler != null) {
                mainHandler.post(() -> pauseInternal());
            }
            return;
        }
        pauseInternal();
    } catch (Exception e) {
        Log.e("VideoPlayer", "Error pausing player", e);
    }
  }

  private void pauseInternal() {
    if (player != null && !isFragmentDestroyed) {
        try {
            player.setPlayWhenReady(false);
        } catch (Exception e) {
            Log.e("VideoPlayer", "Error in pauseInternal", e);
        }
    }
  }

  /**
   * Get the player duration
   * @return int in seconds
   */
  public int getDuration() {
    if (player == null) {
      return 0;
    }
    return player.getDuration() == UNKNOWN_TIME ? 0 : (int) (player.getDuration() / 1000);
  }

  /**
   * Get the player current position
   * @return int in seconds
   */
  public int getCurrentTime() {
    if (player == null) {
      return 0;
    }
    return player.getCurrentPosition() == UNKNOWN_TIME ? 0 : (int) (player.getCurrentPosition() / 1000);
  }

  /**
   * Set the player current position
   * @param timeSecond int
   */
  public void setCurrentTime(int timeSecond) {
    if (!isPlayerOperationSafe()) {
        Log.w("VideoPlayer", "Cannot set current time - player operation not safe");
        return;
    }
    
    try {
        if (isInPictureInPictureMode) {
            if (styledPlayerView != null) {
                styledPlayerView.setUseController(false);
            }
            if (linearLayout != null) {
                linearLayout.setVisibility(View.INVISIBLE);
            }
        }
        long seekPosition = player.getCurrentPosition() == UNKNOWN_TIME
            ? 0
            : Math.min(Math.max(0, timeSecond * 1000), player.getDuration());
        player.seekTo(seekPosition);
    } catch (Exception e) {
        Log.e("VideoPlayer", "Error setting current time", e);
    }
  }

  /**
   * Return the player volume
   * @return float
   */
  public float getVolume() {
    return player != null ? player.getVolume() : curVolume;
  }

  /**
   * Set the player volume
   * @param _volume float range 0,1
   */
  public void setVolume(float _volume) {
    float volume = Math.min(Math.max(0, _volume), 1L);
    curVolume = volume;
    if (player != null) {
      player.setVolume(volume);
    }
  }

  /**
   * Return the player rate
   * @return float
   */
  public float getRate() {
    return videoRate != null ? videoRate : 1.0f;
  }

  /**
   * Set the player rate
   * @param _rate float range [0.25f, 0.5f, 0.75f, 1f, 2f, 4f]
   */
  public void setRate(float _rate) {
    videoRate = _rate;
    if (player != null) {
      PlaybackParameters param = new PlaybackParameters(videoRate != null ? videoRate : 1.0f);
      player.setPlaybackParameters(param);
    }
  }

  /**
   * Switch Off/On the player volume
   * @param _isMuted boolean
   */
  public void setMuted(boolean _isMuted) {
    isMuted = _isMuted;
    if (player != null) {
      if (isMuted) {
        curVolume = player.getVolume();
        player.setVolume(0L);
      } else {
        player.setVolume(curVolume);
      }
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
   * Apply white color to MediaRouteButton
   * @param button
   */
  private void mediaRouteButtonColorWhite(MediaRouteButton button) {
    if (button == null || getContext() == null) return;
    Context castContext = new ContextThemeWrapper(getContext(), androidx.mediarouter.R.style.Theme_MediaRouter);

    TypedArray a = castContext.obtainStyledAttributes(
      null,
      androidx.mediarouter.R.styleable.MediaRouteButton,
      androidx.mediarouter.R.attr.mediaRouteButtonStyle,
      0
    );
    Drawable drawable = a.getDrawable(androidx.mediarouter.R.styleable.MediaRouteButton_externalRouteEnabledDrawable);
    a.recycle();
    if (drawable == null) {
      return;
    }
    DrawableCompat.setTint(drawable, getContext().getResources().getColor(R.color.white));
    drawable.setState(button.getDrawableState());
    button.setRemoteIndicatorDrawable(drawable);
  }

  /**
   * Get video Type from Uri
   * @param uri
   * @return video type
   */
  private String getVideoType(Uri uri) {
    if (uri == null) {
      return "";
    }
    String ret = null;
    Object obj = uri.getLastPathSegment();
    String lastSegment = (obj == null) ? "" : uri.getLastPathSegment();
    for (String type : supportedFormat) {
      if (ret != null) break;
      if (lastSegment.length() > 0 && lastSegment.contains(type)) ret = type;
      if (ret == null) {
        List<String> segments = uri.getPathSegments();
        if (segments != null && segments.size() > 0) {
          String segment;
          if (segments.get(segments.size() - 1).equals("manifest") && segments.size() >= 2) {
            segment = segments.get(segments.size() - 2);
          } else {
            segment = segments.get(segments.size() - 1);
          }
          if (segment != null) {
            for (String sType : supportedFormat) {
              if (segment.contains(sType)) {
                ret = sType;
                break;
              }
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
    if (context == null) {
      return false;
    }
    int pid = android.os.Process.myPid();
    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    if (am == null) {
      return false;
    }
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

  /**
   * Function to initialize the cast service and everything related to it
   * @return void
   */
  private void initializeCastService() {
    Executor executor = Executors.newSingleThreadExecutor();
    Task<CastContext> task = CastContext.getSharedInstance(context, executor);

    task.addOnCompleteListener(new OnCompleteListener<CastContext>() {
      @Override
      public void onComplete(Task<CastContext> task) {
        if (isFragmentDestroyed || !isAdded() || context == null || mediaRouteButton == null) {
          return;
        }
        if (task.isSuccessful()) {
          castContext = task.getResult();
          castPlayer = new CastPlayer(castContext);
          mRouter = MediaRouter.getInstance(context);
          mSelector =
                  new MediaRouteSelector.Builder()
                          .addControlCategories(Arrays.asList(MediaControlIntent.CATEGORY_LIVE_AUDIO, MediaControlIntent.CATEGORY_LIVE_VIDEO))
                          .build();

          mediaRouteButtonColorWhite(mediaRouteButton);
          if (castContext != null && castContext.getCastState() != CastState.NO_DEVICES_AVAILABLE) mediaRouteButton.setVisibility(
                  View.VISIBLE
          );

          castStateListener =
                  state -> {
                    if (mediaRouteButton == null) {
                      return;
                    }
                    if (state == CastState.NO_DEVICES_AVAILABLE) {
                      mediaRouteButton.setVisibility(View.GONE);
                    } else {
                      if (mediaRouteButton.getVisibility() == View.GONE) {
                        mediaRouteButton.setVisibility(View.VISIBLE);
                      }
                    }
                  };
          CastButtonFactory.setUpMediaRouteButton(context, mediaRouteButton);

          MediaMetadata movieMetadata;
          if (artwork != null && !artwork.trim().isEmpty()) {
            MediaMetadata.Builder metadataBuilder = new MediaMetadata.Builder()
              .setTitle(title)
              .setSubtitle(smallTitle)
              .setMediaType(MediaMetadata.MEDIA_TYPE_MOVIE);
            try {
              metadataBuilder.setArtworkUri(Uri.parse(artwork));
            } catch (Exception e) {
              Log.e(TAG, "Invalid artwork uri: " + artwork, e);
            }
            movieMetadata = metadataBuilder.build();
            new setCastImage().execute();
          } else {
            movieMetadata = new MediaMetadata.Builder()
                    .setTitle(title)
                    .setSubtitle(smallTitle)
                    .build();
          }
          mediaItem =
                  new MediaItem.Builder()
                          .setUri(videoPath)
                          .setMimeType(MimeTypes.VIDEO_UNKNOWN)
                          .setMediaMetadata(movieMetadata)
                          .build();

          castPlayer.setSessionAvailabilityListener(
                  new SessionAvailabilityListener() {
                    @Override
                    public void onCastSessionAvailable() {
                      isCastSession = true;
                      final Long videoPosition = player != null ? player.getCurrentPosition() : 0L;
                      if (pipBtn != null && Boolean.TRUE.equals(pipEnabled)) {
                        pipBtn.setVisibility(View.GONE);
                      }
                      if (resizeBtn != null) {
                        resizeBtn.setVisibility(View.GONE);
                      }
                      if (player != null) {
                        player.setPlayWhenReady(false);
                      }
                      if (cast_image != null) {
                        cast_image.setVisibility(View.VISIBLE);
                      }
                      castPlayer.setMediaItem(mediaItem, videoPosition);
                      if (styledPlayerView != null) {
                        styledPlayerView.setPlayer(castPlayer);
                        styledPlayerView.setControllerShowTimeoutMs(0);
                        styledPlayerView.setControllerHideOnTouch(false);
                        //We perform a click because for some weird reason, the layout is black until the user clicks on it
                        styledPlayerView.performClick();
                      }
                    }

                    @Override
                    public void onCastSessionUnavailable() {
                      isCastSession = false;
                      final Long videoPosition = castPlayer != null ? castPlayer.getCurrentPosition() : 0L;
                      if (pipBtn != null && Boolean.TRUE.equals(pipEnabled)) {
                        pipBtn.setVisibility(View.VISIBLE);
                      }
                      if (resizeBtn != null) {
                        resizeBtn.setVisibility(View.VISIBLE);
                      }
                      if (cast_image != null) {
                        cast_image.setVisibility(View.GONE);
                      }
                      if (styledPlayerView != null) {
                        styledPlayerView.setPlayer(player);
                      }
                      if (player != null) {
                        player.setPlayWhenReady(true);
                        player.seekTo(videoPosition);
                      }
                      if (styledPlayerView != null) {
                        styledPlayerView.setControllerShowTimeoutMs(3000);
                        styledPlayerView.setControllerHideOnTouch(true);
                      }
                    }
                  }
          );

          castPlayer.addListener(
                  new Player.Listener() {
                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int state) {
                      Map<String, Object> info = new HashMap<String, Object>() {
                        {
                          put("fromPlayerId", playerId);
                          put("currentTime", String.valueOf(player != null ? player.getCurrentPosition() / 1000 : 0));
                        }
                      };
                      switch (state) {
                        case CastPlayer.STATE_READY:
                          if (castPlayer != null && castPlayer.isPlaying()) {
                            NotificationCenter.defaultCenter().postNotification("playerItemPlay", info);
                          } else {
                            NotificationCenter.defaultCenter().postNotification("playerItemPause", info);
                          }
                          break;
                        default:
                          break;
                      }
                    }
                  }
          );

          castContext.addCastStateListener(castStateListener);
          if (mRouter != null && mSelector != null && mCallback != null) {
            mRouter.addCallback(mSelector, mCallback, MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
          }

        } else {
          Exception e = task.getException();
          if (e != null) {
            e.printStackTrace();
          } else {
            Log.e(TAG, "initializeCastService: CastContext task failed with no exception");
          }
        }
      }
    });
  }

  private final class EmptyCallback extends MediaRouter.Callback {}

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    adjustAspectRatio();
  }

  private void adjustAspectRatio() {
    if (styledPlayerView != null) {
      styledPlayerView.setResizeMode(resizeStatus);
    }
  }
}
