package com.softobt.models;

import io.realm.RealmObject;

/**
 * Created by Abdulgafar Obeitor on 6/10/2018.
 */
public class RealmEggSummary extends RealmObject {
    String date;
    long today;
    long yesterday;
    long balance;

    public RealmEggSummary() {
    }

    public RealmEggSummary(String date, long today, long yesterday, long balance) {
        this.date = date;
        this.today = today;
        this.yesterday = yesterday;
        this.balance = balance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getToday() {
        return today;
    }

    public void setToday(long today) {
        this.today = today;
    }

    public long getYesterday() {
        return yesterday;
    }

    public void setYesterday(long yesterday) {
        this.yesterday = yesterday;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }
}
