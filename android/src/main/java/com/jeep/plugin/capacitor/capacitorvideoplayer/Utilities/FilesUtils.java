package com.jeep.plugin.capacitor.capacitorvideoplayer.Utilities;

import android.content.Context;
import java.io.File;

public class FilesUtils {

    private final Context context;

    public FilesUtils(Context context) {
        this.context = context;
    }

    public String getFilePath(String url) {
        if (url.startsWith("file:///")) {
            return url;
        }
        String path = null;
        String http = url.substring(0, 4);
        if (http.equals("http")) {
            path = url;
        } else {
            if (url.substring(0, 11).equals("application")) {
                String filesDir = context.getFilesDir() + "/";
                path = filesDir + url.substring(url.lastIndexOf("files/") + 6);
                File file = new File(path);
                if (!file.exists()) {
                    path = null;
                }
            } else if (url.contains("assets")) {
                path = "file:///android_asset/" + url;
            } else {
                path = null;
            }
        }
        return path;
    }
}
