package com.example.album.data;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class ImagesModel
        extends AndroidViewModel {

    private static final Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private MutableLiveData<Cursor> cursor;
//    private MutableLiveData<List<Image>> images = new MutableLiveData<>();
    private MutableLiveData<List<Album>> albums = new MutableLiveData<>();
    ImagesLiveData images;

    public ImagesModel(@NonNull Application application) {
        super(application);
        Context context = application.getApplicationContext();
        images = new ImagesLiveData(context, uri);
    }

    public MutableLiveData<Cursor> getCursor() {
        return cursor;
    }

//    public void setCursor(MutableLiveData<Cursor> cursor) {
//        this.cursor = cursor;
//        images = createImagesList();
//    }

    public ImagesLiveData getImages(){
        return images;
    }


    @Nullable
    private MutableLiveData<List<Image>> createImagesList(){
        if(cursor.getValue() == null){
            return null;
        }
        cursor.getValue().moveToFirst();
        List<Image> imagesList = new ArrayList<>();
        while (!cursor.getValue().isAfterLast()){
            Image newImage = new Image();
            Cursor c = cursor.getValue();
            String path = cursor.getValue().getString(cursor.getValue().getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            long date = cursor.getValue().getLong(cursor.getValue().getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED))*1000L;
            int width = c.getInt(c.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH));
            int height = c.getInt(c.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT));
            String bucketName = c.getString(c.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));

            LocalDateTime localDate = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDateTime();
            newImage.setDate(localDate);

            newImage.setImageId(id);

            newImage.setBucketName(bucketName);

            newImage.setImageUri(Uri.fromFile(new File(path)));

            newImage.setWidth(width);

            newImage.setHeight(height);

            imagesList.add(newImage);
            cursor.getValue().moveToNext();
        }
        MutableLiveData<List<Image>> images = new MutableLiveData<>();
        images.setValue(imagesList);
        return images;
    }
}
