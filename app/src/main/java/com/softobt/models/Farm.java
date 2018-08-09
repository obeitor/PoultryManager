package com.softobt.models;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.softobt.mainapplication.PoultryApplication;
import com.softobt.simplehash.SimpleMD5;

import java.util.Random;

/**
 * Created by Abdulgafar Obeitor on 6/15/2017.
 */
public class Farm {
    private String name;
    private String pin;
    private String farmCode;
    private String currency;
    private String creator;
    public Farm(){

    }
    private Farm(String name, String pin){
        this.pin = pin;
        this.name = name;
        this.currency = PoultryApplication.currency;
    }
    public void setName(String n){
        this.name = n;
    }
    public void setPin(String p){
        this.pin = p;
    }
    public void setCreator(String email){this.creator = email;}
    public String getPin(){
        return this.pin;
    }
    public String getName(){
        return this.name;
    }
    public String getFarmCode(){return this.farmCode;}
    public String getCurrency(){return this.currency;}
    public String getCreator(){return this.creator;}
    public void setCurrency(String currency){this.currency=currency;}
    public static void updateFarmName(String farmCode,String newName){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference farm = db.child("farms/"+farmCode+"/name");
        farm.setValue(newName);
        farm = db.child("farms/"+farmCode+"/currency");
        farm.setValue(PoultryApplication.currency);
        farm = db.child("farms/"+farmCode+"/creator");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){
            String email = auth.getCurrentUser().getEmail();
            farm.setValue(email);
        }
    }
    public static void updateFarmPin(String farmCode, String pin){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference farm = db.child("farms/"+farmCode+"/pin");
        farm.setValue(SimpleMD5.generatePassword(pin));
    }
    public static String createFarm(String name, String pin){
        String encodedPin = SimpleMD5.generatePassword(pin);
        Farm farm = new Farm(name,encodedPin);
        farm.generateFarmCode();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){
            String email = auth.getCurrentUser().getEmail();
            farm.setCreator(email);
        }
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference farms = db.child("farms/"+farm.farmCode);
        farms.setValue(farm);
        DatabaseReference codes = db.child("codes/"+farm.farmCode);
        codes.setValue(true);
        FirebaseAuth currentAuth = FirebaseAuth.getInstance();
        if(currentAuth.getCurrentUser() != null){
            String uid = currentAuth.getCurrentUser().getUid();
            farms.child("managers/"+uid).setValue(true);
        }
        return farm.farmCode;
    }
    private  void generateFarmCode(){
        String letters = "QWERTYUIOPASDFGHJKLZXCVBNM";
        String f = "";
        Random rand = new Random();
        for(int i=0;i<8;i++){
            f +=letters.charAt(rand.nextInt(letters.length()));
        }
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference codes = db.child("codes/"+f);
        codes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(boolean.class)!=null){
                    generateFarmCode();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        farmCode = f;
    }
}
