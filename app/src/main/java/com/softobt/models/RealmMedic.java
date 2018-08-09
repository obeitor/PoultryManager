package com.softobt.models;

import io.realm.RealmObject;

/**
 * Created by Abdulgafar Obeitor on 2/27/2018.
 */
public class RealmMedic extends RealmObject{
    private String name;
    private int age;
    private String birdId;
    private boolean vaccine;
    private int status;
    private String disease;
    private int dueDate;
    private String givenDate;
    private String id;
    private boolean bought;

    public RealmMedic(){

    }
    public RealmMedic(clsMedic medic){
        this.name = medic.getName();
        this.age = medic.getAge();
        this.birdId = medic.getBirdId();
        this.vaccine = medic.isVaccine();
        this.status = medic.getStatus();
        this.disease = medic.getDisease();
        this.dueDate = Integer.parseInt(medic.getDueDate());
        this.givenDate = medic.getGivenDate();
        this.id = medic.getId();
        this.bought = medic.isBought();
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getBirdId() {
        return birdId;
    }

    public boolean isVaccine() {
        return vaccine;
    }

    public int getStatus() {
        return status;
    }

    public String getDisease() {
        return disease;
    }

    public int getDueDate() {
        return dueDate;
    }

    public String getId() {
        return id;
    }

    public boolean isBought() {
        return bought;
    }

    public String getGivenDate() {
        return givenDate;
    }
}
