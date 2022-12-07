package com.example.album.data.livedata;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import com.example.album.data.Image;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class ImagesLiveData extends ContentProviderLiveData<List<Image>> {
    private final Context context;
    private final Uri uri;

    public ImagesLiveData(Context context, Uri uri) {
        super(context, uri);
        this.context = context;
        this.uri = uri;
        setValue(getContentProviderValue());
//        List<Image> images = getContentProviderValue();
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
                MediaStore.Images.ImageColumns.DESCRIPTION,
                MediaStore.Images.ImageColumns.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.SIZE
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
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
            long date = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED))*1000L;
            int width = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH));
            int height = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT));
            String bucketName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DESCRIPTION));
            Long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));


            LocalDateTime localDate = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDateTime();
            newImage.setId(id);
            newImage.setDescription(description);
            newImage.setName(name);
            newImage.setDate(localDate);
            newImage.setBucketName(bucketName);
            newImage.setImageUri(Uri.fromFile(new File(path)));
            newImage.setWidth(width);
            newImage.setHeight(height);
            newImage.setSize(size);
            imagesList.add(newImage);
        }

        cursor.close();
        postValue(imagesList);
        return imagesList;
    }

    public boolean updateValue(long id, ContentValues values){
        Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
        int res = context.getContentResolver().update(uri, values, null, null);
        return res != 0;
    }
}
