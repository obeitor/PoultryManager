package com.softobt.models;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.softobt.mainapplication.PoultryApplication;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Abdulgafar Obeitor on 7/31/2017.
 */
public class clsCustomers {
    private String name;
    private double totalOwed;
    private double totalPayed;
    private String id;
    private long numberOfEggs;
    private long numberOfBirds;
    private clsCustomers(String name) {
        this.name = name;
        totalOwed = 0;
        totalPayed = 0;
        id = "";
        numberOfEggs  = 0;
        numberOfBirds = 0;
    }

    public clsCustomers() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTotalOwed() {
        return totalOwed;
    }

    public void setTotalOwed(double totalOwed) {
        this.totalOwed = totalOwed;
    }

    public double getTotalPayed() {
        return totalPayed;
    }

    public void setTotalPayed(double totalPayed) {
        this.totalPayed = totalPayed;
    }

    public long getNumberOfBirds() {
        return numberOfBirds;
    }

    public void setNumberOfBirds(long numberOfBirds) {
        this.numberOfBirds = numberOfBirds;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public long getNumberOfEggs() {
        return numberOfEggs;
    }

    public void setNumberOfEggs(long numberOfEggs) {
        this.numberOfEggs = numberOfEggs;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static clsCustomers addCustomer(String name){
        clsCustomers customer = new clsCustomers(name);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference customerNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Customers").push();
        customerNode.setValue(customer);
        customer.setId(customerNode.getKey());
        return customer;
    }
    public void sellProduct(double amountPaid, double amountDue, int numberOfBirds, long numberOfEggs){
        this.numberOfBirds+=numberOfBirds;
        this.numberOfEggs+=numberOfEggs;
        this.totalPayed+=amountPaid;
        this.totalOwed+=amountDue;
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference customerNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Customers/"+this.getId());
        customerNode.setValue(this);
    }
}
