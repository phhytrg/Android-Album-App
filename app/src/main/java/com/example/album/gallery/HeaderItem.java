package com.example.album.gallery;

import java.time.LocalDateTime;

public class HeaderItem extends ListItem {

    private LocalDateTime date;

    // here getters and setters
    // for title and so on, built
    // using date

    public HeaderItem(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public int getType() {
        return TYPE_HEADER;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public LocalDateTime getDate() {
        return date;
    }
}

