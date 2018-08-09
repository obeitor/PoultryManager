package com.softobt.mainapplication;

import android.app.Application;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.softobt.models.FinanceSummary;
import com.softobt.models.RealmBirds;
import com.softobt.models.RealmEggSummary;
import com.softobt.models.RealmFinance;
import com.softobt.models.RealmMedic;
import com.softobt.models.clsBirds;
import com.softobt.models.clsFinance;
import com.softobt.models.clsMedic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.softobt.poultrymanager.R.string.birds;

/**
 * Created by Abdulgafar Obeitor on 8/7/2017.
 */
public class RealmController {

    private static RealmController instance;
    private final Realm realm;
    public RealmController(Application application){
        realm = Realm.getDefaultInstance();
    }
    public static RealmController with(Fragment fragment){
        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(AppCompatActivity activity){
        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }
    public static RealmController with(Application application){
        if(instance == null){
            instance = new RealmController(application);
        }
        return instance;
    }
    public static RealmController getInstance(){
        return instance;
    }
    public Realm getRealm(){
        return this.realm;
    }
    public void refresh(){
        realm.refresh();
    }
    public void updateBirds(ArrayList<clsBirds> birds){
        realm.beginTransaction();
        realm.where(RealmBirds.class).findAll().deleteAllFromRealm();
        for(clsBirds bird : birds){
            realm.copyToRealm(new RealmBirds(bird));
        }
        realm.commitTransaction();
    }
    public void updateBird(clsBirds bird){
        realm.beginTransaction();
        realm.where(RealmBirds.class).equalTo("id",bird.getId()).findAll().deleteAllFromRealm();
        realm.copyToRealm(new RealmBirds(bird));
        realm.commitTransaction();

    }
    public void updateMedics(ArrayList<clsMedic> medics){
        realm.beginTransaction();
        realm.where(RealmMedic.class).findAll().deleteAllFromRealm();
        for(clsMedic medic : medics){
            realm.copyToRealm(new RealmMedic(medic));
        }
        realm.commitTransaction();
    }
    public void updateMedic(clsMedic medic){
        realm.beginTransaction();
        realm.where(RealmMedic.class).equalTo("id",medic.getId()).findAll().deleteAllFromRealm();
        realm.copyToRealm(new RealmMedic(medic));
        realm.commitTransaction();

    }
    public void updateFinances(ArrayList<FinanceSummary> fin){
        realm.beginTransaction();
        realm.where(RealmFinance.class).findAll().deleteAllFromRealm();
        for(FinanceSummary f : fin){
            realm.copyToRealm(new RealmFinance(f));
        }
        realm.commitTransaction();
    }

    public void updateFinance(FinanceSummary fin){
            realm.beginTransaction();
        realm.where(RealmFinance.class).equalTo("id",fin.getId()).findAll().deleteAllFromRealm();
            realm.copyToRealm(new RealmFinance(fin));
            realm.commitTransaction();

    }
    public void updateEggSummary(RealmEggSummary eggSummary){
        realm.beginTransaction();
        realm.where(RealmEggSummary.class).findAll().deleteAllFromRealm();
        realm.copyToRealm(eggSummary);
        realm.commitTransaction();
    }
    public RealmEggSummary getEggSummary(){
        return realm.where(RealmEggSummary.class).findFirst();
    }
    public int getLatestFinance(){
        return realm.where(RealmFinance.class).max("dateTime").intValue();
    }

    public ArrayList<clsMedic> getSomeMedicsToView(){
        Calendar today = Calendar.getInstance();
        Calendar weekago = Calendar.getInstance();weekago.add(Calendar.DATE,0-8);
        int intToday = Integer.parseInt(clsMedic.sdf.format(today.getTime()));
        int intWeekAgo = Integer.parseInt(clsMedic.sdf.format(weekago.getTime()));
        RealmResults<RealmMedic> results = realm.where(RealmMedic.class).greaterThan("dueDate",intWeekAgo).findAll();

        ArrayList<clsMedic> medics = new ArrayList<>();
        for(RealmMedic m : results.where().greaterThan("dueDate",intToday).or().equalTo("dueDate",intToday).findAllSorted("dueDate",Sort.ASCENDING)){
            medics.add(new clsMedic(m));
        }
        for(RealmMedic m : results.where().lessThan("dueDate",intToday).findAllSorted("dueDate",Sort.DESCENDING)){
            medics.add(new clsMedic(m));
        }
        return medics;
    }

    public ArrayList<FinanceSummary> getSomeFinancesToView(){
        Calendar weekago = Calendar.getInstance();weekago.add(Calendar.DATE,0-8);
        String d = new SimpleDateFormat("yyyyMMdd").format(weekago.getTime());
        int date = Integer.parseInt(d);
        RealmResults<RealmFinance> res = realm.where(RealmFinance.class).greaterThan("dateTime",date).findAllSorted("dateTime", Sort.DESCENDING);
        ArrayList<FinanceSummary> fin = new ArrayList<>();
            for (RealmFinance f : res) {
                fin.add(new FinanceSummary(f));
            }

        return fin;
    }
    public ArrayList<FinanceSummary> getAllFinances(){
        RealmResults<RealmFinance> res = realm.where(RealmFinance.class).findAllSorted("dateTime", Sort.DESCENDING);
        ArrayList<FinanceSummary> fin = new ArrayList<>();
        for (RealmFinance f : res) {
                fin.add(new FinanceSummary(f));
        }
        return fin;
    }

    public ArrayList<clsBirds> getActiveFlocks(){
        RealmResults<RealmBirds> res = realm.where(RealmBirds.class).findAll();
        ArrayList<clsBirds> birds = new ArrayList<>();
        for(RealmBirds bird : res){
            clsBirds b = new clsBirds(bird);
            if(b.getActive()>0)
                birds.add(b);

        }
        return birds;
    }

    public ArrayList<clsBirds> getAllLayers(){
        RealmResults<RealmBirds> res = realm.where(RealmBirds.class).equalTo("laysEgg",true).findAll();
        ArrayList<clsBirds> birds = new ArrayList<>();
        for(RealmBirds bird : res){
            clsBirds b = new clsBirds(bird);
            if(b.getActive()>0)
                birds.add(b);
        }
        return birds;
    }
    public void deleteAllRealmData(){
        this.realm.beginTransaction();
        this.realm.deleteAll();
        this.realm.commitTransaction();
    }
}
