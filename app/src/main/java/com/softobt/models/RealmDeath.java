package com.softobt.models;

import io.realm.RealmObject;

/**
 * Created by Abdulgafar Obeitor on 2/26/2018.
 */
public class RealmDeath extends RealmObject {
    int amount;
    int date;

    public RealmDeath(){

    }
    public RealmDeath(Death death){
        this.date = death.getDate();
        this.amount = death.getAmount();
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
