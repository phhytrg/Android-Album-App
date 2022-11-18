package com.example.album.detail_album;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.album.R;
import com.example.album.gallery.GalleryFragment;
import com.example.album.gallery.GalleryFragmentDirections;

public class DetailAlbumAdapter extends RecyclerView.Adapter<DetailAlbumAdapter.GalleryViewHolder> {

    View view;
    Context context;
    boolean isLinearLayout;

    public DetailAlbumAdapter(Context context) {
        this.context = context;
    }

    public void setLinearLayout(boolean linearLayout) {
        isLinearLayout = linearLayout;
    }

    int[] images = {R.drawable.photo1,R.drawable.photo2,R.drawable.photo2,
            R.drawable.photo3,R.drawable.photo4,R.drawable.photo5,
            R.drawable.photo6,R.drawable.photo7,R.drawable.photo8,
            R.drawable.image,R.drawable.image2,R.drawable.image2,
            R.drawable.image2,R.drawable.image2,R.drawable.image2,
            R.drawable.image2,R.drawable.image2,R.drawable.image2};

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_item,parent,false);
        return new GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        holder.imageView.setImageResource(images[position]);
        ConstraintLayout.LayoutParams a = (ConstraintLayout.LayoutParams)holder.imageView.getLayoutParams();
        if(isLinearLayout){
            int h = holder.imageView.getDrawable().getIntrinsicHeight();
            int w = holder.imageView.getDrawable().getIntrinsicWidth();
            a.dimensionRatio = "H,"+ w +":"+ h;
        }else{
            a.dimensionRatio = "H,1:1";
            a.width=0;
            a.height=0;
        }
        holder.imageView.setOnClickListener(v -> {
            NavHostFragment hostFragment = (NavHostFragment) ((FragmentActivity)context)
                    .getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment);
            if(hostFragment == null)
                return;

            //Get current fragment
            Fragment fragment = hostFragment
                    .getChildFragmentManager()
                    .getFragments().get(0);

            //Get current fragment's name
            String currentFragment = fragment.getClass().getSimpleName();

            //There are 2 different path for navigation.
            //The first comes from DetailAlbumFragment -> DetailFragment
            //The second comes from GalleryFragment -> Detail Fragment
            if(currentFragment.equals(DetailAlbumFragment.class.getSimpleName())){
                NavDirections action = DetailAlbumFragmentDirections
                        .actionDetailAlbumFragmentToDetailFragment(images[position]);
                Navigation.findNavController(v)
                        .navigate(action);
            }
            if(currentFragment.equals(GalleryFragment.class.getSimpleName())){
                NavDirections action = GalleryFragmentDirections
                        .actionGalleryFragmentToDetailFragment(images[position]);
                Navigation.findNavController(v)
                        .navigate(action);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public static class GalleryViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}