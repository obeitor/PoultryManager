package com.softobt.models;

import android.app.Activity;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.softobt.mainapplication.PoultryApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Abdulgafar Obeitor on 6/18/2017.
 */
public class clsEggs {
    private long eggCount;
    private long cracked;

    public long getCracked() {
        return cracked;
    }

    public long getEggCount() {
        return eggCount;
    }

    public void setCracked(long cracked) {
        this.cracked = cracked;
    }

    public void setEggCount(long eggCount) {
        this.eggCount = eggCount;
    }
    public clsEggs(){

    }
    protected clsEggs( long eggCount, long cracked){
        this.cracked = cracked;
        this.eggCount = eggCount;
    }
    public String displayEggInfo(){
        String s = "";
        if(this.getEggCount()<30){
            if(this.getEggCount()>1)
                return this.getEggCount()+" Eggs";
            return this.getEggCount()+" Egg";
        }
        long crates = this.getEggCount()/30;
        long eggs = this.getEggCount()%30;
        if(eggs == 0){
            if(crates>1)
                return crates+" Crates";
            return crates+" Crate";
        }
        if(crates > 1)
            s = crates+" Crates";
        else
            s = crates+" Crate";
        if(eggs > 1)
            s += " "+eggs+" Eggs";
        else
            s += " "+eggs+" Egg";
        return s;
    }

}
