package com.example.album.gallery;

import androidx.annotation.NonNull;

import java.util.Date;

public class Event {

    private int imageId;
    private Date date;

    public Event(@NonNull int imageId, @NonNull Date date) {
        this.imageId = imageId;
        this.date = date;
    }

    public int getImageId() {
        return imageId;
    }

    public Date getDate() {
        return date;
    }

}
