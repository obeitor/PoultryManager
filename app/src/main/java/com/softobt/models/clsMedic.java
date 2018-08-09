package com.softobt.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.softobt.mainapplication.PoultryApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Abdulgafar Obeitor on 2/27/2018.
 */
public class clsMedic {
    public final static int IN_PROGRESS = 1,COMPLETED = 2, NOT_DUE = 3, PAST_DUE = 4, IS_DUE = 5, IS_TOMORROW = 6;
    private String name;
    private int age;
    private String birdId;
    private boolean vaccine;
    private int status;
    private String disease;
    private clsBirds bird;
    private String dueDate;
    private String givenDate;
    private String id;
    private boolean bought;

    public clsMedic(){

    }

    public clsMedic(RealmMedic medic){
        this.name = medic.getName();
        this.age = medic.getAge();
        this.birdId = medic.getBirdId();
        this.vaccine = medic.isVaccine();
        this.status = medic.getStatus();
        this.disease = medic.getDisease();
        this.dueDate = medic.getDueDate()+"";
        this.id = medic.getId();
        this.bought = medic.isBought();
        this.givenDate = medic.getGivenDate();
    }

    public clsMedic(boolean vaccine, clsBirds bird,String dueDate, String name, String disease){
        this.vaccine = vaccine;
        this.bird = bird;
        this.birdId = bird.getId();
        this.dueDate = dueDate;
        this.givenDate = this.dueDate;
        this.name = name;
        this.disease = disease;
        this.bought = false;
        Calendar birthdate = Calendar.getInstance();
        Calendar duedate = Calendar.getInstance();
        try{
            birthdate.setTime(sdf.parse(bird.getBirthDate()));
            duedate.setTime(sdf.parse(this.dueDate));
            this.age = (int)((duedate.getTimeInMillis() - birthdate.getTimeInMillis()) / (1000*3600*24));
            if(this.age<0)this.age = 0;
        }
        catch(ParseException e){}
        updateStatus();
    }

    public clsMedic(boolean vaccine, clsBirds bird, int age,String ageUnit, String name, String disease) {
        this.vaccine = vaccine;
        this.bird = bird;
        this.birdId = bird.getId();
        this.bought = false;
        if(ageUnit.equalsIgnoreCase(clsBirds.DAY))
            this.age = age;
        else if(ageUnit.equalsIgnoreCase(clsBirds.WEEK))
            this.age = age*7;
        else if(ageUnit.equalsIgnoreCase(clsBirds.MONTH))
            this.age = age*30;
        this.name = name;
        this.disease = disease;
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(sdf.parse(bird.getBirthDate()));
            c.add(Calendar.DATE, this.age);
            this.dueDate = sdf.format(c.getTime());
        }
        catch(ParseException e){}
        this.givenDate = this.dueDate;
        updateStatus();
    }
    private void updateStatus(){
        try {
            Date dueDate = sdf.parse(this.dueDate);
            Calendar c = Calendar.getInstance();
            Date today = sdf.parse(sdf.format(c.getTime()));
            c.setTime(today);c.add(Calendar.DATE,1);
            Date tomorrow = c.getTime();
            int diff = dueDate.compareTo(today);
            if(this.status != COMPLETED && this.status != IN_PROGRESS) {
                if (diff == 0 )
                    this.status = IS_DUE;
                else if (diff < 0) this.status = PAST_DUE;
                else this.status = NOT_DUE;
                if(tomorrow.compareTo(dueDate)==0)
                    this.status = IS_TOMORROW;
            }
        }catch(ParseException e){}
    }
    public String showStatus(){
        updateStatus();
        switch (this.status){
            case IS_DUE:
                return "Is Due Today";
            case IS_TOMORROW:
                return "Is Due Tomorrow";
            case NOT_DUE:
                return "Is Not Due";
            case PAST_DUE:
                return "Is Past Due";
            case IN_PROGRESS:
                return "Is In Progress";
            case COMPLETED:
                return "Is Completed";
            default:
                return "Is Undeterminable";
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBirdId() {
        return birdId;
    }

    public void setBirdId(String birdId) {
        this.birdId = birdId;
    }

    public boolean isVaccine() {
        return vaccine;
    }

    public void setVaccine(boolean vaccine) {
        this.vaccine = vaccine;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    @Exclude
    public clsBirds getBird() {
        return bird;
    }

    public void setBird(clsBirds bird) {
        this.bird = bird;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getGivenDate() {
        return givenDate;
    }

    public void setGivenDate(String givenDate) {
        this.givenDate = givenDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }

    public static ArrayList<clsMedic> addMedication(boolean vaccine, clsBirds bird, int age, String ageUnit, Calendar dueDate, String name, String disease, int daysCount){
        ArrayList<clsMedic> medics = new ArrayList<>();
        for(int i=0;i<daysCount;i++){
            clsMedic medic;
            if(dueDate==null){
                medic = new clsMedic(vaccine,bird,age+i,ageUnit,name,disease);
            }
            else{
                dueDate.add(Calendar.DATE,(i==0?0:1));
                medic = new clsMedic(vaccine,bird,sdf.format(dueDate.getTime()),name,disease);
            }
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            DatabaseReference medicRecordNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Medic/"+medic.dueDate).push();
            medicRecordNode.setValue(medic);
            medic.id = medicRecordNode.getKey();
            medics.add(medic);
        }
        return medics;
    }

    public String showDate(){
        try {
            return new SimpleDateFormat("dd MMM, yy").format(sdf.parse(this.givenDate));
        }
        catch(ParseException e){}
        return "Unknown Date";
    }

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
}

