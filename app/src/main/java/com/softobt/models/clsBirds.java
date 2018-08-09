package com.softobt.models;

import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.softobt.mainapplication.PoultryApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by Abdulgafar Obeitor on 6/14/2017.
 */
public class clsBirds{
    private String name;
    private String type;
    private int sold;
    private String birthDate;
    private int sick;
    private int total;
    private String id;
    private boolean laysEgg;
    private ArrayList<Death> deaths ;
    public static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    public static final String WEEK ="week", MONTH = "month", DAY = "day";
    public clsBirds(){
        deaths = new ArrayList<>();
    }
    public clsBirds(RealmBirds realmBirds){
        this.id = realmBirds.getId();
        this.name = realmBirds.getName();
        this.type = realmBirds.getType();
        this.sold = realmBirds.getSold();
        this.sick = realmBirds.getSick();
        this.laysEgg = realmBirds.isLaysEgg();
        this.total = realmBirds.getTotal();
        this.birthDate = realmBirds.getBirthDate();
        deaths = new ArrayList<>();
        for(RealmDeath d : realmBirds.getDeaths()){
            deaths.add(new Death(d));
        }
    }
    public clsBirds(String name, String type,boolean laysEgg, int amount, String arrivalAge, Calendar arrivalDate){
        this.name = name;
        this.type = type;
        this.total = amount;
        this.laysEgg = laysEgg;
        this.sold = this.sick = 0;
        deaths = new ArrayList<>();

        int number = Integer.parseInt(arrivalAge.split("-")[0]);
        String unit = arrivalAge.split("-")[1];
        if(unit.equalsIgnoreCase(WEEK)){
            number*=7;
            arrivalDate.add(Calendar.DATE,(0-number));
        }
        else if(unit.equalsIgnoreCase(DAY)){
            arrivalDate.add(Calendar.DATE,(0-number));
        }
        this.birthDate = sdf.format(arrivalDate.getTime());
    }
    public static clsBirds addBirdFlock(int count, String name, String type, boolean laysEgg, int amount, String arrivalAge, Calendar arrivalDate){
        clsBirds birds = new clsBirds(name,type,laysEgg,amount,arrivalAge,arrivalDate);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(PoultryApplication.CURRENT_FARM_CODE+"/Bulk-count").setValue(count);
        DatabaseReference birdRecordNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Birds").push();
        birdRecordNode.setValue(birds);
        birds.id = birdRecordNode.getKey();
        return birds;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    public ArrayList<Death> getDeaths(){
        return this.deaths;
    }
    public void setDeaths(ArrayList<Death> deaths){
        this.deaths = deaths;
        if(this.deaths == null){
            this.deaths = new ArrayList<>();
        }
    }

    @Exclude
    public int getDeathCount() {
        int dead = 0;
        if(this.deaths==null)
            this.deaths = new ArrayList<>();
        for(Death death : this.deaths){
            dead+=death.getAmount();
        }
        return dead;
    }
    @Exclude
    public int getDead(){
        return getDeathCount();
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public int getSick() {
        return sick;
    }

    public void setSick(int sick) {
        this.sick = sick;
    }
    public int getTotal() {
        return total;
    }
    public void setTotal(int total) {
        this.total = total;
    }

    public boolean isLaysEgg() {
        return laysEgg;
    }

    public void setLaysEgg(boolean laysEgg) {
        this.laysEgg = laysEgg;
    }


    public void addEggRecord(String date, long eggCount, int cracked)throws ParseException, NumberFormatException{
        Calendar cal = Calendar.getInstance();
        cal.setTime(new SimpleDateFormat("dd/MM/yy").parse(date));
        String d = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference eggRecordNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Eggs/"+d+"/"+this.getId());
        eggRecordNode.setValue(new clsEggs(eggCount,cracked));
    }
    @Exclude
    public String getId(){
        return this.id;
    }

    public void setId(String id){
        this.id = id;
    }
    @Exclude
    public String getAge(){
        String age = "";
        Calendar today = Calendar.getInstance();
        Calendar birthdate = Calendar.getInstance();
        try {
            birthdate.setTime(sdf.parse(this.birthDate));
        }
        catch(ParseException e){
            return "Invalid Date";
        }
        int days = (int)((today.getTimeInMillis() - birthdate.getTimeInMillis()) / (1000*3600*24));
        if(days < 14){
            age = days+" "+DAY;
        }
        else{
            days = days/7;
            if(days < 52){
                age = days+" "+WEEK;
            }
            else{
                days = (days*7)/30;
                age = days+" "+MONTH;
            }
        }
        if(days>1)
            age+="s";
        return age;
    }
    @Exclude
    public int getActive() {
        return (this.getTotal() - (this.getSold() + this.getDead()));
    }
}
