package com.softobt.models;

/**
 * Created by Abdulgafar Obeitor on 8/8/2017.
 */
public class Death {
    int amount;
    int date;

    public Death(){

    }
    public Death(RealmDeath death){
        this.date = death.getDate();
        this.amount  = death.getAmount();
    }
    public Death(int date, int amount){
        this.date = date;
        this.amount = amount;
    }
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }
}
