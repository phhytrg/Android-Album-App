package com.example.album.gallery;

import java.util.Date;

public class HeaderItem extends ListItem {

    private Date date;

    // here getters and setters
    // for title and so on, built
    // using date

    public HeaderItem(Date date) {
        this.date = date;
    }

    @Override
    public int getType() {
        return TYPE_HEADER;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }
}

