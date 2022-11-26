package com.example.album.data;

import android.net.Uri;

import java.time.LocalDateTime;

public class Image {
    private Uri imageUri;
    private long imageId;
    private LocalDateTime date;
    int width,height;
    String bucketName;

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Uri getThumbnailUri() {
        return thumbnailUri;
    }

    public void setThumbnailUri(Uri thumbnailUri) {
        this.thumbnailUri = thumbnailUri;
    }

    private Uri thumbnailUri;

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

}
