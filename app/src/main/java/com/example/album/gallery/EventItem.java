package com.example.album.gallery;
public class EventItem extends ListItem {

    private Event event;

    // here getters and setters
    // for title and so on, built
    // using event

    public EventItem(Event event) {
        this.event = event;
    }

    @Override
    public int getType() {
        return TYPE_EVENT;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }
}
