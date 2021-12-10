package com.jeep.plugin.capacitor.capacitorvideoplayer.Utilities;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.getcapacitor.Bridge;

public class FragmentUtils {

    private Bridge bridge;

    public FragmentUtils(Bridge bridge) {
        this.bridge = bridge;
    }

    public void loadFragment(Fragment vpFragment, int frameLayoutId) {
        // create a FragmentManager
        FragmentManager fm = bridge.getActivity().getSupportFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        fragmentTransaction.replace(frameLayoutId, vpFragment);
        fragmentTransaction.commit(); // save the changes
    }

    public void removeFragment(/*VideoPlayerFragmentFullscreenExoPlayer*/Fragment vpFragment) {
        FragmentManager fm = bridge.getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.remove(vpFragment);
        fragmentTransaction.commit();
    }
}
