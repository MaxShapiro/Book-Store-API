package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

public class BookOrderEvent implements Event {


    private final String bookName;
    private Customer customer;
    private Integer orderId;
    private int orderTick;

    public BookOrderEvent(String bookName, Customer customer, Integer orderId, int orderTick){
        this.bookName = bookName;
        this.customer = customer;
        this.orderTick = orderTick;
        this.orderId = orderId;
    }

    public String getBook() {
        return bookName;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public int getOrderTick() {
        return orderTick;
    }

}
