package com.example.album.gallery;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.album.R;
import com.example.album.detail_album.DetailAlbumAdapter;
import com.example.album.item_decoration.GridSpacingItemDecoration;
import com.example.album.item_decoration.LinearSpacingItemDecoration;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {


    boolean isLinearLayout;
    private Activity activity;

    public GalleryAdapter(boolean isLinearLayout, Activity activity) {
        this.isLinearLayout = isLinearLayout;
        this.activity = activity;
    }

    public void setLinearLayout(boolean linearLayout) {
        isLinearLayout = linearLayout;
    }

    View view;

    int[] images = {R.drawable.image2,R.drawable.image2,R.drawable.image2,
            R.drawable.image2,R.drawable.image2,R.drawable.image2,
            R.drawable.image2,R.drawable.image2,R.drawable.image2,
            R.drawable.image2,R.drawable.image2,R.drawable.image2,
            R.drawable.image2,R.drawable.image2,R.drawable.image2,
            R.drawable.image2,R.drawable.image2,R.drawable.image2};
    String[] dates = {"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17"};

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_item,parent,false);
        return new GalleryViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        holder.textView.setText(dates[position]);
        DetailAlbumAdapter detailAlbumAdapter = new DetailAlbumAdapter();
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(holder.recyclerView.getContext());
        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(holder.recyclerView.getContext(), 4);
        holder.recyclerView.setAdapter(detailAlbumAdapter);

        while (holder.recyclerView.getItemDecorationCount() > 0) {
            holder.recyclerView.removeItemDecorationAt(0);
        }
        if(isLinearLayout) {
            holder.recyclerView.setLayoutManager(linearLayoutManager);
            holder.recyclerView.addItemDecoration(new LinearSpacingItemDecoration(8,false));
        }
        else{
            holder.recyclerView.setLayoutManager(gridLayoutManager);
            holder.recyclerView.addItemDecoration(new GridSpacingItemDecoration(4,8,false));
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public static class GalleryViewHolder extends RecyclerView.ViewHolder{
        RecyclerView recyclerView;
        AppCompatTextView textView;
        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.gallery_recyclerview);
            textView = itemView.findViewById(R.id.date_modify_textview);
        }
    }

    private void setIcon(MenuItem menuItem){
        if(menuItem == null)
            return;
        if(isLinearLayout){
            menuItem.setIcon(ContextCompat.getDrawable(this.activity,R.drawable.ic_grid_layout));
        }
        else{
            menuItem.setIcon(ContextCompat.getDrawable(this.activity,R.drawable.ic_linear_layout));
        }
    }


}