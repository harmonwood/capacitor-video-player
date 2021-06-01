package com.jeep.plugin.capacitor.capacitorvideoplayer.PickerVideo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.jeep.plugin.capacitor.capacitorvideoplayer.R;
import java.util.ArrayList;

//public class AdapterVideoList extends RecyclerView.Adapter<AdapterVideoList.MyViewHolder> {
public class AdapterVideoList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = AdapterVideoList.class.getName();

    ArrayList<ModelVideo> videosList;
    Context context;
    PickerVideoFragment parentFragment;

    public AdapterVideoList(Context context, PickerVideoFragment parent, ArrayList<ModelVideo> videosList) {
        this.context = context;
        this.videosList = videosList;
        this.parentFragment = parent;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new VideoRecyclerViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_video, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ((VideoRecyclerViewHolder) viewHolder).onBind(parentFragment, videosList.get(position));
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return videosList.size();
    }
}
