package com.example.album.gallery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.example.album.R;

import java.util.Collections;
import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface AdapterCallback{
        void OnItemClick(EventItem item);
        void LinearItemDecoration(ImageView imageView);
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder{
        //TODO
        TextView headerText;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerText = itemView.findViewById(R.id.date);
        }
    }

    private static class ImageViewHolder extends RecyclerView.ViewHolder{
        /** TODO */
//        RecyclerView recyclerView;

        ImageView imageView;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
//            itemView.setOnClickListener(v -> listener.OnItemClick((EventItem) items.get(getAdapterPosition())));
        }
    }

    private List<ListItem> items = Collections.emptyList();
    private PhotosAdapter.AdapterCallback listener;
    private boolean isLinearLayout = false;
    ConstraintSet set;

    public void setLinearLayout(boolean linearLayout) {
        isLinearLayout = linearLayout;
    }

    public PhotosAdapter(List<ListItem> items, PhotosAdapter.AdapterCallback listener) {
        this.items = items;
        this.listener = listener;
        set = new ConstraintSet();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType){
            case ListItem.TYPE_HEADER:{
                //TODO
                View view = inflater.inflate(R.layout.date_header,parent,false);
                return new HeaderViewHolder(view);
            }
            case ListItem.TYPE_EVENT:{
                //TODO
                View itemView = inflater.inflate(R.layout.image_item, parent, false);
                return new ImageViewHolder(itemView);
            }
            default:
                throw new IllegalStateException("unsupported item type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType){
            case ListItem.TYPE_HEADER:{
                HeaderItem header = (HeaderItem) items.get(position);
                HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
                viewHolder.headerText.setText(DateUtils.formatDate(header.getDate()));
                break;
            }
            case ListItem.TYPE_EVENT:{
                EventItem eventItem = (EventItem) items.get(position);
                ImageViewHolder viewHolder = (ImageViewHolder) holder;
                ConstraintLayout.LayoutParams itemView = (ConstraintLayout.LayoutParams)viewHolder
                        .imageView.getLayoutParams();
                viewHolder.imageView.setImageResource(eventItem.getEvent().getImageId());
                if(isLinearLayout){
                    int h = viewHolder.imageView.getDrawable().getIntrinsicHeight();
                    int w = viewHolder.imageView.getDrawable().getIntrinsicWidth();
                    set.clone((ConstraintLayout) holder.itemView);
                    set.setDimensionRatio(viewHolder.imageView.getId(), w+":"+h);
                    set.applyTo((ConstraintLayout) holder.itemView);
                    listener.LinearItemDecoration(viewHolder.imageView);
                }else{
                    viewHolder.imageView.setBackground(null);
                    set.clone((ConstraintLayout) holder.itemView);
                    set.setDimensionRatio(viewHolder.imageView.getId(), "1:1");
                    set.applyTo((ConstraintLayout) holder.itemView);
                }
                viewHolder.imageView.setOnClickListener(v -> listener.OnItemClick(eventItem));
                break;
            }
            default:
                throw new IllegalStateException("unsupported item view type");
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }
}
