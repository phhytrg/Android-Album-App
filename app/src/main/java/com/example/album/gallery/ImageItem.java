package com.example.album.gallery;

import com.example.album.data.Image;

public class ImageItem extends ListItem {

    private Image image;

    // here getters and setters
    // for title and so on, built
    // using event

    public ImageItem(Image image) {
        this.image = image;
    }

    @Override
    public int getType() {
        return TYPE_EVENT;
    }

    public Image getImage() {
        return image;
    }
}
