package com.example.album.detail_image;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;


import com.example.album.R;
import com.example.album.data.Image;
import com.example.album.ui.ZoomableImageView;
//import com.example.gallery.R;

import java.io.File;
import java.util.HashMap;

import java.util.List;
import java.util.Objects;

public class ViewPagerAdapter extends PagerAdapter {


    Context context;
    List<Image> images;
    LayoutInflater mLayoutInflater;
    ZoomableImageView imageView;




    public ZoomableImageView getImageView() {return imageView; }

    // Viewpager Constructor
    public ViewPagerAdapter(Context context,List<Image> images) {
        this.context = context;
//        this.pictureFiles = pictureFiles;
        this.images=images;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // return the number of images
        return images.size();
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((RelativeLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        // Inflating the item.xml
        View itemView = mLayoutInflater.inflate(R.layout.fragment_image_detail, container, false);

        // Referencing the image view from the item.xml file
        ZoomableImageView view = itemView.findViewById(R.id.photo_view);

        // Set the image in the imageView
//        view.setImageDrawable(getDrawable(pictureFiles[position].getAbsolutePath()));
        view.setImage(images.get(position));
        // Adding the View
        Objects.requireNonNull(container).addView(itemView);

        return itemView;
    }

    @Override
    public void setPrimaryItem (ViewGroup container, int position, Object object){
        super.setPrimaryItem(container, position, object);
        imageView = ((View)object).findViewById(R.id.photo_view);
//        imageView.setImageDrawable(getDrawable(pictureFiles[position].getAbsolutePath()));
        imageView.setImage(images.get(position));
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((RelativeLayout) object);
    }

}