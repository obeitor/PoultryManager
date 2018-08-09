package com.softobt.poultrymanager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.softobt.adapters.BirdArrayAdapter;
import com.softobt.mainapplication.PoultryApplication;
import com.softobt.mainapplication.RealmController;
import com.softobt.models.FinanceSummary;
import com.softobt.models.RealmEggSummary;
import com.softobt.models.clsBirds;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Abdulgafar Obeitor on 6/9/2017.
 */
public class HomeFragment extends Fragment {
    private ProgressDialog dialog;
    //Finance
    TextView todayCredit,todayDebit,weekCredit,weekDebit,monthCredit,monthDebit,accountBal;
    TextView todayEggView, yesterdayEggView, eggBalanceView;
    ListView poultryList;
    BirdArrayAdapter birdArrayAdapter;
    ArrayList<clsBirds> birdsList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.home_fragment,container,false);
        dialog = new ProgressDialog(getActivity());
        //Finance
        todayCredit = (TextView)returnView.findViewById(R.id.today_credit);
        todayDebit = (TextView)returnView.findViewById(R.id.today_debit);
        weekCredit = (TextView)returnView.findViewById(R.id.thisweek_credit);
        weekDebit = (TextView)returnView.findViewById(R.id.thisweek_debit);
        monthCredit = (TextView)returnView.findViewById(R.id.thismonth_credit);
        monthDebit = (TextView)returnView.findViewById(R.id.thismonth_debit);
        accountBal = (TextView)returnView.findViewById(R.id.total_bal_finance);
        poultryList = (ListView)returnView.findViewById(R.id.poultry_list_view);
        birdsList = new ArrayList<>();
        birdArrayAdapter = new BirdArrayAdapter(getActivity(),birdsList,BirdArrayAdapter.homePageSummary);
        poultryList.setAdapter(birdArrayAdapter);
        finishCount = 0;
        //Egg
        eggBalanceView = (TextView)returnView.findViewById(R.id.eggbalance_home);
        todayEggView = (TextView)returnView.findViewById(R.id.today_egg_home);
        yesterdayEggView = (TextView)returnView.findViewById(R.id.yesterday_egg_home);
        loadBirds();//get layer count first before loading eggs
        loadEggs();
        loadFinance();
        return returnView;
    }
    private int layersCount = 0;
    private void loadEggs(){
        RealmEggSummary rs = RealmController.with(this).getEggSummary();
        if(rs==null){
            Snackbar.make(getActivity().getWindow().getDecorView(),"Go to Egg page to get updated egg record",Snackbar.LENGTH_LONG)
                    .setAction("Action",null).show();
            return;
        }
        long t = 0,y=0;
        if(new SimpleDateFormat("dd/MM/yy").format(Calendar.getInstance().getTime()).equalsIgnoreCase(rs.getDate())){
            t = rs.getToday();y=rs.getYesterday();
        }
        else{
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE,-1);
            if(new SimpleDateFormat("dd/MM/yy").format(c.getTime()).equalsIgnoreCase(rs.getDate()))
                y = rs.getToday();
        }
        long es = rs.getBalance();
        float percent = ((float)t/(float)layersCount);
        percent *= 100;
        eggBalanceView.setText((es/30)+" C, "+(es%30)+" E");
        todayEggView.setText((t/30)+" C, "+(t%30)+" E "+String.format("%.2f",percent)+"%");
        yesterdayEggView.setText((y/30)+" C, "+(y%30)+" E ");
        Snackbar.make(getActivity().getWindow().getDecorView(),"Egg page would give latest egg record",Snackbar.LENGTH_LONG)
                .setAction("Action",null).show();
        closeDialog();
    }
    private void loadBirdsFromServer(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference birdSummaryNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Birds");
        birdSummaryNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<clsBirds> birds = new ArrayList<>();
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    clsBirds bird = d.getValue(clsBirds.class);
                    bird.setId(d.getKey());
                    birds.add(bird);
                }
                RealmController.with(HomeFragment.this).updateBirds(birds);
                closeDialog();
                loadBirds();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                closeDialog();
                Snackbar.make(getActivity().getWindow().getDecorView(),"Failed To Get Birds",Snackbar.LENGTH_LONG)
                        .setAction("Action",null).show();
            }
        });
    }
    private void loadBirds(){
        birdsList.clear();
        birdsList.addAll(RealmController.with(HomeFragment.this).getActiveFlocks());
        for(clsBirds b : birdsList){
            if(b.isLaysEgg())
                layersCount+=b.getActive();
        }
        birdArrayAdapter.notifyDataSetChanged();
    }
    private void loadFinanceFromServer(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference financeSummaryNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/FinanceSummary");
        financeSummaryNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    for(DataSnapshot dd : d.getChildren()) {
                        FinanceSummary summary = dd.getValue(FinanceSummary.class);
                        RealmController.getInstance().updateFinance(summary);
                    }
                }
                loadFinance();
                closeDialog();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                closeDialog();
                Snackbar.make(getActivity().getWindow().getDecorView(),"Failed To Get Finances",Snackbar.LENGTH_LONG)
                        .setAction("Action",null).show();
            }
        });
    }
    private void loadFinance(){
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY,0);today.set(Calendar.MINUTE,0);today.set(Calendar.SECOND,0);
        Calendar week = Calendar.getInstance();week.set(Calendar.DAY_OF_WEEK,1);
        week.set(Calendar.HOUR_OF_DAY,0);week.set(Calendar.MINUTE,0);week.set(Calendar.SECOND,0);
        Calendar month = Calendar.getInstance();month.set(Calendar.DAY_OF_MONTH,1);
        month.set(Calendar.HOUR_OF_DAY,0);month.set(Calendar.MINUTE,0);month.set(Calendar.SECOND,0);
        double tdebit=0,tcredit=0,wdebit=0,wcredit=0,mdebit=0,mcredit=0,bal=0;
        for(FinanceSummary f : RealmController.with(HomeFragment.this).getAllFinances()) {
            bal = f.isCredit()?bal+f.getAmount():bal-f.getAmount();
            if(f.getDateTime().after(today)){
                if(f.isCredit())
                    tcredit+=f.getAmount();
                else
                    tdebit+=f.getAmount();
            }
            if(f.getDateTime().after(week)){
                if(f.isCredit())
                    wcredit+=f.getAmount();
                else
                    wdebit+=f.getAmount();
            }
            if(f.getDateTime().after(month)){
                if(f.isCredit())
                    mcredit+=f.getAmount();
                else
                    mdebit+=f.getAmount();
            }
        }
        todayDebit.setText(PoultryApplication.formatCash(tdebit));todayCredit.setText(PoultryApplication.formatCash(tcredit));
        weekDebit.setText(PoultryApplication.formatCash(wdebit));weekCredit.setText(PoultryApplication.formatCash(wcredit));
        monthDebit.setText(PoultryApplication.formatCash(mdebit));monthCredit.setText(PoultryApplication.formatCash(mcredit));
        accountBal.setText(PoultryApplication.formatCash(bal));
    }

    private int finishCount;
    private int finished(){
        finishCount++;
        return finishCount;
    }
    private void closeDialog(){
        if(finished()==3) {
            dialog.dismiss();
            finishCount = 0;
        }
    }


}
