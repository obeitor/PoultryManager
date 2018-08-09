package com.softobt.models;

import io.realm.RealmObject;

/**
 * Created by Abdulgafar Obeitor on 8/10/2017.
 */
public class RealmCustomers extends RealmObject {
    private String id;
    private String name;
    private double due;
    private double paid;
    private long numberOfEggs;
    private long numberOfBirds;

    public RealmCustomers(clsCustomers customer) {
        this.id = customer.getId();
        this.name = customer.getName();
        this.due = customer.getTotalOwed();
        this.paid = customer.getTotalPayed();
        this.numberOfBirds = customer.getNumberOfBirds();
        this.numberOfEggs = customer.getNumberOfEggs();
    }

    public RealmCustomers() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getDue() {
        return due;
    }

    public void setDue(double due) {
        this.due = due;
    }

    public double getPaid() {
        return paid;
    }

    public void setPaid(double paid) {
        this.paid = paid;
    }

    public long getNumberOfEggs() {
        return numberOfEggs;
    }

    public void setNumberOfEggs(long numberOfEggs) {
        this.numberOfEggs = numberOfEggs;
    }

    public long getNumberOfBirds() {
        return numberOfBirds;
    }

    public void setNumberOfBirds(long numberOfBirds) {
        this.numberOfBirds = numberOfBirds;
    }
}
