package com.example.album.data;

import java.util.List;

public class Album {
    private List<Image> images;

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getAlbumNames() {
        return albumNames;
    }

    public void setAlbumNames(String albumNames) {
        this.albumNames = albumNames;
    }

    private String albumNames;
}
