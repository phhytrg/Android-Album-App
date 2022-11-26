package com.example.album.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class ImagesLiveData extends ContentProviderLiveData<List<Image>>{
    private Context context;
    private final Uri uri;
    List<Image> images;

    public ImagesLiveData(Context context, Uri uri) {
        super(context, uri);
        this.context = context;
        this.uri = uri;
        images = getContentProviderValue();
    }

    @Nullable
    @Override
    public List<Image> getValue() {
        return super.getValue();
    }

    @Override
    List<Image> getContentProviderValue() {
        String[]projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.ORIENTATION,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.MIME_TYPE ,
                MediaStore.Images.ImageColumns.DATE_MODIFIED,
                MediaStore.Images.ImageColumns.WIDTH,
                MediaStore.Images.ImageColumns.HEIGHT,
                MediaStore.Images.ImageColumns.DESCRIPTION
        };

        Cursor cursor = context.getContentResolver().query(
                uri,
                projection,
                null,
                null,
                "date_modified DESC"
        );
        List<Image> imagesList = new ArrayList<>();
        while (cursor.moveToNext()){
            Image newImage = new Image();
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            long date = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED))*1000L;
            int width = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH));
            int height = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT));
            String bucketName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));

            LocalDateTime localDate = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDateTime();
            newImage.setDate(localDate);
            newImage.setImageId(id);
            newImage.setBucketName(bucketName);
            newImage.setImageUri(Uri.fromFile(new File(path)));
            newImage.setWidth(width);
            newImage.setHeight(height);
            imagesList.add(newImage);
        }

        postValue(imagesList);
        return imagesList;
    }

}
