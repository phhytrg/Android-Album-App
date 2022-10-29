package com.example.album;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class GridAdapter extends BaseAdapter {
    Context context;
    String[] pictureName;
    int[] pictureId;

    LayoutInflater inflater;

    public GridAdapter(Context context, String[] pictureName, int[] pictureId) {
        this.context = context;
        this.pictureName = pictureName;
        this.pictureId = pictureId;
    }

    @Override
    public int getCount() {
        return pictureName.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(inflater==null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView==null)
            convertView = inflater.inflate(R.layout.album_item, null);

        ImageView imageView = convertView.findViewById(R.id.avatar);
        TextView textView = convertView.findViewById(R.id.album_name); //item name

        imageView.setImageResource(pictureId[position]);
        textView.setText(pictureName[position]);

        return convertView;
    }

//    @Override
//    public void addItem(){
//
//    }
}
