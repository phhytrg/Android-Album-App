package com.example.album;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> pictureName;
    ArrayList<Integer> pictureId;
    Boolean flag;

    LayoutInflater inflater;

    public GridAdapter(Context context, ArrayList<String> pictureName, ArrayList<Integer> pictureId, Boolean flag) {
        this.context = context;
        this.pictureName = pictureName;
        this.pictureId = pictureId;
        this.flag = flag;
    }

    @Override
    public int getCount() {
        return pictureName.size();
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
        TextView textView = convertView.findViewById(R.id.album_name);
        ImageView tick = convertView.findViewById(R.id.tick);

        imageView.setImageResource(pictureId.get(position));
        textView.setText(pictureName.get(position));

        if (flag == true){
            tick.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}
