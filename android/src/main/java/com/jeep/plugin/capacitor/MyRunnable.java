package com.jeep.plugin.capacitor;

import java.util.Map;

public class MyRunnable implements Runnable {
    private Map<String, String> info;
    public Map<String, String> getInfo() {
        return this.info;
    }
    public void setInfo(Map<String, String> _info) {
        this.info = _info;
    }

    @Override
    public void run() {
    }


    public void run(Map<String, String> info) {
    }
}
