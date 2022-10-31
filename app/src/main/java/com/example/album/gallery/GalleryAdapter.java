package com.example.album.gallery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.album.R;
import com.example.album.detail_album.DetailAlbumAdapter;
import com.example.album.item_decoration.GridSpacingItemDecoration;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

    public void setLinearLayout(boolean linearLayout) {
        isLinearLayout = linearLayout;
    }

    boolean isLinearLayout;
    View view;

    int[] images = {R.drawable.image2,R.drawable.image2,R.drawable.image2,
            R.drawable.image2,R.drawable.image2,R.drawable.image2,
            R.drawable.image2,R.drawable.image2,R.drawable.image2,
            R.drawable.image2,R.drawable.image2,R.drawable.image2,
            R.drawable.image2,R.drawable.image2,R.drawable.image2,
            R.drawable.image2,R.drawable.image2,R.drawable.image2};

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_item,parent,false);
        return new GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        holder.textView.setText("01/01/2022");
        DetailAlbumAdapter detailAlbumAdapter = new DetailAlbumAdapter();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(holder.recyclerView.getContext(), 4);
        holder.recyclerView.setAdapter(detailAlbumAdapter);
        holder.recyclerView.setLayoutManager(gridLayoutManager);
        while (holder.recyclerView.getItemDecorationCount() > 0) {
            holder.recyclerView.removeItemDecorationAt(0);
        }
        holder.recyclerView.addItemDecoration(new GridSpacingItemDecoration(4,8,false));
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public class GalleryViewHolder extends RecyclerView.ViewHolder{
        RecyclerView recyclerView;
        AppCompatTextView textView;
        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.gallery_recyclerview);
            textView = itemView.findViewById(R.id.date_modify_textview);
        }
    }
}