package com.getcapacitor.myapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import android.content.res.Configuration;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.jeep.plugin.capacitor.capacitorvideoplayer.FullscreenExoPlayerFragment;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FullscreenExoPlayerFragmentTest {

    @Test
    public void onResumeWithoutPlayerViewDoesNotCrash() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            FullscreenExoPlayerFragment fragment = new FullscreenExoPlayerFragment();
            fragment.onResume();
        });
    }

    @Test
    public void onConfigurationChangedWithoutPlayerViewDoesNotCrash() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            FullscreenExoPlayerFragment fragment = new FullscreenExoPlayerFragment();
            fragment.onConfigurationChanged(new Configuration());
        });
    }

    @Test
    public void onStartWithoutPlayerViewDoesNotCrash() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            FullscreenExoPlayerFragment fragment = new FullscreenExoPlayerFragment();
            fragment.onStart();
        });
    }

    @Test
    public void onPauseAndStopWithoutPlayerViewDoesNotCrash() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            FullscreenExoPlayerFragment fragment = new FullscreenExoPlayerFragment();
            fragment.onPause();
            fragment.onStop();
            fragment.onDestroyView();
        });
    }

    @Test
    public void playerMethodsWithoutInitializedPlayerDoNotCrash() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            FullscreenExoPlayerFragment fragment = new FullscreenExoPlayerFragment();
            assertEquals(0, fragment.getDuration());
            assertEquals(0, fragment.getCurrentTime());
            assertEquals(0.5f, fragment.getVolume(), 0.01f);
            assertFalse(fragment.isPlaying());
            assertFalse(fragment.getMuted());
            assertFalse(fragment.isControllerIsFullyVisible());
            fragment.setVolume(0.8f);
            fragment.setRate(1.25f);
            fragment.setMuted(true);
            fragment.showController();
        });
    }
}
