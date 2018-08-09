package com.softobt.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.realm.RealmObject;

/**
 * Created by Abdulgafar Obeitor on 2/26/2018.
 */
public class RealmFinance extends RealmObject {

    private double amount;
    private boolean credit;
    private String title;
    private String time;
    private String date;
    private String id;
    private int dateTime;

    public RealmFinance() {
    }
    public RealmFinance(FinanceSummary financeSummary){
        this.amount = financeSummary.getAmount();
        this.credit = financeSummary.isCredit();
        this.title = financeSummary.getTitle();
        String dateTimString = new SimpleDateFormat("yyyyMMdd").format(financeSummary.getDateTime().getTime());
        this.dateTime = Integer.parseInt(dateTimString);
        this.date = financeSummary.getDate();
        this.time = financeSummary.getTime();
        this.id = financeSummary.getId();
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isCredit() {
        return credit;
    }

    public void setCredit(boolean credit) {
        this.credit = credit;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDateTime() {
        return dateTime;
    }

    public void setDateTime(int dateTime) {
        this.dateTime = dateTime;
    }
}
