package com.softobt.models;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Abdulgafar Obeitor on 8/7/2017.
 */
public class RealmBirds extends RealmObject {

    private String name;
    private String type;
    private int sold;
    private String birthDate;
    private int sick;
    private int total;
    private String id;
    private boolean laysEgg;
    private RealmList<RealmDeath> deaths;

    public RealmBirds(){

    }
    public RealmBirds(clsBirds bird){
        this.name = bird.getName();
        this.type = bird.getType();
        this.sold = bird.getSold();
        this.deaths = new RealmList<>();
        for(Death d : bird.getDeaths()){
            deaths.add(new RealmDeath(d));
        }
        this.birthDate = bird.getBirthDate();
        this.sick = bird.getSick();
        this.total = bird.getTotal();
        this.id = bird.getId();
        this.laysEgg = bird.isLaysEgg();
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }


    public int getSold() {
        return sold;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public int getSick() {
        return sick;
    }

    public int getTotal() {
        return total;
    }

    public String getId() {
        return id;
    }

    public boolean isLaysEgg() {
        return laysEgg;
    }

    public RealmList<RealmDeath> getDeaths() {
        return deaths;
    }

}
