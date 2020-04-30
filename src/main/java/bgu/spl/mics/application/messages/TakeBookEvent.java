package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class TakeBookEvent implements Event {

    private final String bookTitle;

    public TakeBookEvent(String book){
        this.bookTitle = book;
    }

    public String getBookTitle() {
        return bookTitle;
    }
}
