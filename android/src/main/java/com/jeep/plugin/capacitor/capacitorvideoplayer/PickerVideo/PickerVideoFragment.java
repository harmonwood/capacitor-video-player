package com.jeep.plugin.capacitor.capacitorvideoplayer.PickerVideo;

//import android.app.Fragment;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.jeep.plugin.capacitor.capacitorvideoplayer.Notifications.NotificationCenter;
import com.jeep.plugin.capacitor.capacitorvideoplayer.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PickerVideoFragment extends Fragment {

    private static final String TAG = PickerVideoFragment.class.getName();

    private View view;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewLayoutManager;
    private Context context;
    private ArrayList<ModelVideo> videosList = new ArrayList<ModelVideo>();
    private AdapterVideoList adapterVideoList;

    /**
     * Create Fragment View
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View
     */
    @SuppressWarnings("serial")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = container.getContext();
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_picker_video, container, false);
        loadVideos();
        Log.v(TAG, "*** videosList.size() " + videosList.size());
        if (videosList.size() > 0) {
            recyclerView = view.findViewById(R.id.recyclerView_videos);
            recyclerViewLayoutManager = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(recyclerViewLayoutManager);
            adapterVideoList = new AdapterVideoList(context, this, videosList);
            recyclerView.setAdapter(adapterVideoList);
        } else {
            Map<String, Object> info = new HashMap<String, Object>() {
                {
                    long videoId = -1;
                    put("videoId", videoId);
                }
            };
            Log.v(TAG, "*** info " + info.toString());
            NotificationCenter.defaultCenter().postNotification("videoPathInternalReady", info);
        }
        return view;
    }

    private void loadVideos() {
        String[] projection = { MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DURATION };
        String sortOrder = MediaStore.Video.Media.DATE_ADDED + " DESC";

        Cursor cursor = getActivity()
            .getApplication()
            .getContentResolver()
            .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder);
        if (cursor != null) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumn);
                String title = cursor.getString(titleColumn);
                int duration = cursor.getInt(durationColumn);

                Uri data = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                String duration_formatted;
                int sec = (duration / 1000) % 60;
                int min = (duration / (1000 * 60)) % 60;
                int hrs = duration / (1000 * 60 * 60);

                if (hrs == 0) {
                    duration_formatted = String.valueOf(min).concat(":".concat(String.format(Locale.UK, "%02d", sec)));
                } else {
                    duration_formatted =
                        String
                            .valueOf(hrs)
                            .concat(
                                ":".concat(String.format(Locale.UK, "%02d", min).concat(":".concat(String.format(Locale.UK, "%02d", sec))))
                            );
                }
                Log.v(TAG, "**** video " + id + " " + title + " " + duration_formatted + " ****");
                videosList.add(new ModelVideo(id, data, title, duration_formatted));
            }
        }
    }
}
