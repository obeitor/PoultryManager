package com.softobt.models;

import com.google.firebase.database.Exclude;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Abdulgafar Obeitor on 8/1/2017.
 */
public class FinanceSummary {
    private double amount;
    private boolean credit;
    private String title;
    private String time;
    private String date;
    private String id;
    public FinanceSummary(){

    }
    public FinanceSummary(RealmFinance f){
        this.amount = f.getAmount();
        this.credit = f.isCredit();
        this.title = f.getTitle();
        this.time = f.getTime();
        this.date = f.getDate();
        this.id = f.getId();
    }
    public FinanceSummary(Calendar date, String title, double amount, String direction, String id){
        this.title = title;
        this.amount = amount;
        this.credit = direction.equalsIgnoreCase(clsFinance.CREDIT);
        this.time = new SimpleDateFormat("hh:mm:ss").format(date.getTime());
        this.date = new SimpleDateFormat("dd MMM, yyyy").format(date.getTime());
        this.setId(id);
    }
    public double getAmount(){
        return this.amount;
    }
    public void setAmount(double amount){
        this.amount = amount;
    }
    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getTime(){
        return this.time;
    }
    public void setTime(String time){
        this.time = time;
    }
    public String getDate(){
        return this.date;
    }
    public void setDate(String date){
        this.date = date;
    }
    public boolean isCredit(){return this.credit;}
    public void setCredit(boolean credit){this.credit=credit;}
    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
    @Exclude
    public Calendar getDateTime(){
        Calendar datetime = Calendar.getInstance();
        try{
            datetime.setTime(new SimpleDateFormat("dd MMM, yyyy/hh:mm:ss").parse(date+"/"+time));
            return datetime;
        }
        catch (ParseException e){
            return null;
        }
    }
}
