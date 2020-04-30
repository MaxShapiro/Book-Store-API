package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

public class OrderPair implements Serializable {

    private String bookTitle;
    private Integer tick;


    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public Integer getTick() {
        return tick;
    }

    public void setTick(Integer tick) {
        this.tick = tick;
    }
}
