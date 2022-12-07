package com.example.album.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Image implements Parcelable {
    private Uri imageUri;
    private LocalDateTime date;
    private int width,height;
    private String bucketName;
    private long id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public Image() {}

    private Image(Parcel in) {
        imageUri = in.readParcelable(Uri.class.getClassLoader());
        date = Instant.ofEpochMilli(in.readLong()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        width = in.readInt();
        height = in.readInt();
        bucketName = in.readString();
    }


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

    public Uri getImageUri() {
        return imageUri;
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
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(imageUri, flags);
        dest.writeLong(date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        dest.writeInt(width);
        dest.writeLong(height);
        dest.writeString(bucketName);
    }

    public static final Parcelable.Creator<Image> CREATOR
            = new Parcelable.Creator<Image>() {
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}