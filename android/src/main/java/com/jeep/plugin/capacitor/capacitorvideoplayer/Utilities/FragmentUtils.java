package com.jeep.plugin.capacitor.capacitorvideoplayer.Utilities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import com.getcapacitor.Bridge;

public class FragmentUtils {

    private Bridge bridge;

    public FragmentUtils(Bridge bridge) {
        this.bridge = bridge;
    }

    public void loadFragment(Fragment vpFragment, int frameLayoutId) {
        // create a FragmentManager
        FragmentManager fm = bridge.getActivity().getFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        fragmentTransaction.replace(frameLayoutId, vpFragment);
        fragmentTransaction.commit(); // save the changes
    }

    public void removeFragment(/*VideoPlayerFragmentFullscreenExoPlayer*/Fragment vpFragment) {
        FragmentManager fm = bridge.getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.remove(vpFragment);
        fragmentTransaction.commit();
    }
}
