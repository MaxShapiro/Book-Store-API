package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

public class CreditCard implements Serializable {

    private int number;

    private int amount;

    public int getNumber() {
        return number;
    }

    public int getAmount() {
        return amount;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
