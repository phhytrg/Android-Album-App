package com.example.album.gallery;

import java.time.LocalDate;

public class HeaderItem extends ListItem {

    private LocalDate date;

    // here getters and setters
    // for title and so on, built
    // using date

    public HeaderItem(LocalDate date) {
        this.date = date;
    }

    @Override
    public int getType() {
        return TYPE_HEADER;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }
}

