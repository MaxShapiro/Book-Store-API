package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class CheckBookAvailabilityAndPrice implements Event {

    private final String bookTitle;

    public CheckBookAvailabilityAndPrice(String bookTitle){
        this.bookTitle = bookTitle;
    }

    public String getBookTitle() {
        return bookTitle;
    }


}
