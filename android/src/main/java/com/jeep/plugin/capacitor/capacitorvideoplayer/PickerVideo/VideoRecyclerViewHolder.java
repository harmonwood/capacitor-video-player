package com.jeep.plugin.capacitor.capacitorvideoplayer.PickerVideo;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.jeep.plugin.capacitor.capacitorvideoplayer.Notifications.NotificationCenter;
import com.jeep.plugin.capacitor.capacitorvideoplayer.R;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.Map;

public class VideoRecyclerViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = VideoRecyclerViewHolder.class.getName();
    ImageView tv_thumbnail;
    TextView tv_title, tv_duration;
    View parent;

    public VideoRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        parent = itemView;
        tv_title = itemView.findViewById(R.id.tv_title);
        tv_duration = itemView.findViewById(R.id.tv_duration);
        tv_thumbnail = itemView.findViewById(R.id.tv_thumbnail);
    }

    public void onBind(PickerVideoFragment parentFragment, final ModelVideo modelVideo) {
        parent.setTag(this);

        tv_title.setText(modelVideo.getTitle());
        tv_duration.setText(modelVideo.getDuration());

        parentFragment
            .getActivity()
            .runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        Picasso.get().load(modelVideo.getData()).placeholder(R.drawable.ic_image_background).fit().into(tv_thumbnail);
                    }
                }
            );

        parent.setOnClickListener(
            new View.OnClickListener() {
                @SuppressWarnings("serial")
                @Override
                public void onClick(View v) {
                    Map<String, Object> info = new HashMap<String, Object>() {
                        {
                            long videoId = modelVideo.getId();
                            Log.v(TAG, "**** in onBindViewHolder videoId: " + videoId + " ****");
                            put("videoId", videoId);
                        }
                    };
                    NotificationCenter.defaultCenter().postNotification("videoPathInternalReady", info);
                }
            }
        );
        return;
    }
}
