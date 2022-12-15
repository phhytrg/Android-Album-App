package com.example.album.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.example.album.R;

import java.util.Collections;
import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface AdapterCallback{
        void onItemClick(ImageItem item);
        void linearItemDecoration(ImageView imageView);
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
//            itemView.setOnClickListener(v -> listener.OnItemClick((ImageItem) items.get(getAdapterPosition())));
        }
    }

    private List<ListItem> items = Collections.emptyList();
    private PhotosAdapter.AdapterCallback listener;
    private boolean isLinearLayout = false;
    private Context context;
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
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
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
                ImageItem imageItem = (ImageItem) items.get(position);
                ImageViewHolder viewHolder = (ImageViewHolder) holder;

                //item decoration
                if(isLinearLayout){
                    int w = imageItem.getImage().getWidth();
                    int h = imageItem.getImage().getHeight();
                    set.clone((ConstraintLayout) holder.itemView);
                    set.setDimensionRatio(viewHolder.imageView.getId(), w+":"+h);
                    set.applyTo((ConstraintLayout) holder.itemView);
                    listener.linearItemDecoration(viewHolder.imageView);
                }else{
                    viewHolder.imageView.setBackground(null);
                    set.clone((ConstraintLayout) holder.itemView);
                    set.setDimensionRatio(viewHolder.imageView.getId(), "1:1");
                    set.applyTo((ConstraintLayout) holder.itemView);
                }

                Glide.with(context)
                        .load(imageItem.getImage().getImageUri())
                        .fitCenter()
                        .placeholder(R.drawable.image_border)
                        .signature(new ObjectKey(imageItem.getImage().getId()))
                        .into(viewHolder.imageView);
                viewHolder.imageView.setOnClickListener(v -> listener.onItemClick(imageItem));
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
