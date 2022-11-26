package com.example.album.data;

import android.net.Uri;

import java.time.LocalDate;

public class Image {
    private Uri imageUri;
    private long imageId;
    private LocalDate date;
    int width,height;
    int buckedId;

    public int getBuckedId() {
        return buckedId;
    }

    public void setBuckedId(int buckedId) {
        this.buckedId = buckedId;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

}
