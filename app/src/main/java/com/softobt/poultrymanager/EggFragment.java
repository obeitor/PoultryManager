package com.softobt.poultrymanager;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.softobt.adapters.BirdArrayAdapter;
import com.softobt.mainapplication.PoultryApplication;
import com.softobt.mainapplication.RealmController;
import com.softobt.models.EggSummary;
import com.softobt.models.FinanceSummary;
import com.softobt.models.RealmEggSummary;
import com.softobt.models.clsBirds;
import com.softobt.models.clsEggs;
import com.softobt.models.clsFinance;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Abdulgafar Obeitor on 6/16/2017.
 */
public class EggFragment extends Fragment{
    private ProgressDialog dialog;
    //main Fragment
    private TextView todayCount, yesterdayCount, eggsBalanceCount,selectedCountView, selectedCountDate_DateRange;
    private Button selectDate, selectDateRange, sellEggBtn;
    private FloatingActionButton addNewEggButton;

    private LayoutInflater popInflater;
    //select Date Range Pop
    private View popUpView_DateRange;
    private PopupWindow popupWindow_DateRange;
    private TextView startDateEntry, endDateEntry;
    private Button okSelectRange;private ImageButton cancelSelectRange;
    //enter new Egg
    private View popUpView_NewEggEntry;
    private PopupWindow popupWindow_EggEntry;
    private TextView eggDateEntry;
    private EditText numberOfCratesEntry, numberOfEggsEntry,numberOfCrackedEntry;
    private Button okNewEggEntry;private ImageButton cancelNewEggEntry;
    private Spinner selectLayerOwner;
    //sell egg
    private View popUpView_SellEgg;
    private PopupWindow popupWindow_SellEgg;
    private TextView sellEggDate;
    private EditText sellEggCrate, sellEggPieces,sellEggAmountDue,sellEggAmountPaid,sellEggBuyerName;
    private Button okSellEgg;private ImageButton cancelSellEgg;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View returnView = inflater.inflate(R.layout.egg_fragment,container,false);
        dialog = new ProgressDialog(getActivity());
        todayCount = (TextView)returnView.findViewById(R.id.todayEggCount);
        yesterdayCount = (TextView)returnView.findViewById(R.id.ysdayEggCount);
        eggsBalanceCount = (TextView)returnView.findViewById(R.id.eggBalanceCount);
        selectedCountView = (TextView)returnView.findViewById(R.id.specDayCount);
        selectedCountDate_DateRange = (TextView)returnView.findViewById(R.id.specDay);
        selectDate = (Button)returnView.findViewById(R.id.select_date_btn);
        selectDateRange = (Button)returnView.findViewById(R.id.select_daterange_btn);
        sellEggBtn = (Button)returnView.findViewById(R.id.selectSellEgg);
        addNewEggButton = (FloatingActionButton)returnView.findViewById(R.id.add_new_egg);

        //pop ups
        popInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //select date range popup
        popUpView_DateRange = popInflater.inflate(R.layout.select_date_range,null);
        popupWindow_DateRange = new PopupWindow(popUpView_DateRange, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,
                true);
        startDateEntry = (TextView) popUpView_DateRange.findViewById(R.id.startDate);
        endDateEntry = (TextView) popUpView_DateRange.findViewById(R.id.endDate);
        okSelectRange = (Button) popUpView_DateRange.findViewById(R.id.show_range);
        cancelSelectRange = (ImageButton) popUpView_DateRange.findViewById(R.id.close_seldate_range);
        //enter new egg entry
        popUpView_NewEggEntry = popInflater.inflate(R.layout.add_egg_record,null);
        popupWindow_EggEntry = new PopupWindow(popUpView_NewEggEntry,ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,
                true);
        eggDateEntry = (TextView)popUpView_NewEggEntry.findViewById(R.id.dateToaddEgg);
        numberOfCratesEntry = (EditText)popUpView_NewEggEntry.findViewById(R.id.add_number_of_crates);
        numberOfEggsEntry = (EditText)popUpView_NewEggEntry.findViewById(R.id.add_number_of_eggs);
        numberOfCrackedEntry = (EditText)popUpView_NewEggEntry.findViewById(R.id.add_number_of_cracked);
        okNewEggEntry = (Button)popUpView_NewEggEntry.findViewById(R.id.add_egg_entry_btn);
        cancelNewEggEntry = (ImageButton)popUpView_NewEggEntry.findViewById(R.id.close_addEgg_view);
        selectLayerOwner = (Spinner)popUpView_NewEggEntry.findViewById(R.id.select_bird_to_add_agg);
        selectLayerOwner.setAdapter(new BirdArrayAdapter(getActivity(), RealmController.with(this).getAllLayers(),BirdArrayAdapter.spinnerSummary));
        //sell egg
        popUpView_SellEgg = popInflater.inflate(R.layout.sell_egg,null);
        popupWindow_SellEgg = new PopupWindow(popUpView_SellEgg,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,
                true);
        sellEggDate = (TextView)popUpView_SellEgg.findViewById(R.id.eggSaleDate);
        sellEggCrate = (EditText) popUpView_SellEgg.findViewById(R.id.enter_number_of_crates_selling);
        sellEggPieces = (EditText)popUpView_SellEgg.findViewById(R.id.enter_number_of_eggs_selling);
        sellEggAmountDue = (EditText) popUpView_SellEgg.findViewById(R.id.enter_amount_due_eggSale);
        sellEggAmountPaid = (EditText)popUpView_SellEgg.findViewById(R.id.enter_amount_collected_eggSale);
        sellEggBuyerName = (EditText)popUpView_SellEgg.findViewById(R.id.enter_buyer_name_eggSale);
        okSellEgg = (Button)popUpView_SellEgg.findViewById(R.id.ok_sell_eggs);
        cancelSellEgg = (ImageButton)popUpView_SellEgg.findViewById(R.id.close_egg_sale);
        UpdateEggInfo();
        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    currentDateEntryView = selectedCountDate_DateRange;
                    Calendar calendar = Calendar.getInstance();
                    new DatePickerDialog(getActivity(),dateSetListener, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();
                }catch(Exception e){
                    Snackbar.make(v,"Application Error",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                }
            }
        });
        selectDateRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow_DateRange.setAnimationStyle(R.style.popup_transit);
                popupWindow_DateRange.showAtLocation(returnView, Gravity.CENTER,0,0);
            }
        });
        cancelSelectRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow_DateRange.dismiss();
            }
        });
        startDateEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    currentDateEntryView = startDateEntry;
                    Calendar calendar = Calendar.getInstance();
                    new DatePickerDialog(getActivity(),dateSetListener, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();
                }catch(Exception e){
                    Snackbar.make(v,"Application Error",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                }
            }
        });
        endDateEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    currentDateEntryView = endDateEntry;
                    Calendar calendar = Calendar.getInstance();
                    new DatePickerDialog(getActivity(),dateSetListener, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();
                }catch(Exception e){
                    Snackbar.make(v,"Application Error",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                }
            }
        });
        okSelectRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String start = startDateEntry.getText().toString();
                String end = endDateEntry.getText().toString();
                Calendar startCal = Calendar.getInstance();
                startCal.set(Calendar.HOUR,0);
                Calendar endCal = Calendar.getInstance();
                try{
                    startCal.setTime(new SimpleDateFormat("dd/MM/yy").parse(start));
                    startCal.set(Calendar.HOUR,0);
                    endCal.setTime(new SimpleDateFormat("dd/MM/yy").parse(end));
                    endCal.set(Calendar.HOUR,5);
                    if(startCal.after(endCal)){
                        throw new ParseException("Start Date After End Date",0);
                    }
                }
                catch(ParseException e){
                    endCal.add(Calendar.DATE,1);//set end to tomorrow
                }
                finally {
                    String range = new SimpleDateFormat("dd/MM/yy").format(startCal.getTime())
                    + " - " + new SimpleDateFormat("dd/MM/yy").format(endCal.getTime());
                    selectedCountDate_DateRange.setText(range);
                    popupWindow_DateRange.dismiss();
                }
            }
        });
        selectedCountDate_DateRange.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                downloadSelected();
            }
        });
        addNewEggButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RealmController.with(EggFragment.this).getAllLayers().isEmpty()){
                    Snackbar.make(v,"You have no Layer, Add New Flock in Birds",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                }
                else {
                    popupWindow_EggEntry.setAnimationStyle(android.R.style.Animation_Dialog);
                    popupWindow_EggEntry.showAtLocation(returnView, Gravity.CENTER, 0, 0);
                    Calendar cal = Calendar.getInstance();
                    String today = new SimpleDateFormat("dd/MM/yy").format(cal.getTime());
                    eggDateEntry.setText(today);
                }
            }
        });
        eggDateEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    currentDateEntryView = eggDateEntry;
                    Calendar calendar = Calendar.getInstance();
                    new DatePickerDialog(getActivity(),dateSetListener, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();
                }catch(Exception e){
                    Snackbar.make(v,"Application Error",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                }

            }
        });
        selectLayerOwner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                downloadDateEntryPreviousRecord();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        eggDateEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                //show current amount of eggs on stated date
                downloadDateEntryPreviousRecord();
            }
        });
        okNewEggEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//store here before closing
                int crates = 0,eggs = 0,cracked = 0;
                String s_crates = numberOfCratesEntry.getText().toString().trim(),
                        s_eggs = numberOfEggsEntry.getText().toString().trim(),
                        s_cracked = numberOfCrackedEntry.getText().toString().trim(),
                        s_date = eggDateEntry.getText().toString().trim();
                try{
                    if(!s_crates.isEmpty())
                        crates = Integer.parseInt(s_crates);
                    if(!s_cracked.isEmpty())
                        cracked = Integer.parseInt(s_cracked);
                    if(!s_eggs.isEmpty())
                        eggs = Integer.parseInt(s_eggs);
                    int total_eggs = (crates*30)+eggs;
                    clsBirds bird = (clsBirds)selectLayerOwner.getSelectedItem();
                    bird.addEggRecord(s_date,total_eggs,cracked);
                    long es = eggSummary.getTotalEggs() - prevEggs;//remove previous record of date
                    es+=total_eggs;//add new record
                    eggSummary.setTotalEggs(es);//update eggSummary
                    es = eggSummary.getTotalCracked() - prevCracked;//do same as above three lines for cracked
                    es+=cracked;
                    eggSummary.setTotalCracked(es);
                    eggSummary.setUpdated(s_date);
                    DatabaseReference summaryRef = FirebaseDatabase.getInstance().getReference()
                            .child(PoultryApplication.CURRENT_FARM_CODE+"/Eggs/Summary");
                    summaryRef.setValue(eggSummary);
                    popupWindow_EggEntry.dismiss();

                    //check if today and update todayCountInfo
                    Calendar cal = Calendar.getInstance();
                    if(s_date.equalsIgnoreCase(new SimpleDateFormat("dd/MM/yy").format(cal.getTime()))){
                        todayEgg.setEggCount(total_eggs);
                        todayEgg.setCracked(cracked);
                    }//same for yesterday
                    cal.add(Calendar.DATE,-1);
                    if(s_date.equalsIgnoreCase(new SimpleDateFormat("dd/MM/yy").format(cal.getTime()))){
                        yesterdayEgg.setEggCount(total_eggs);
                        yesterdayEgg.setCracked(cracked);
                    }
                    ShowUpdatedEggInfo();
                }
                catch(NumberFormatException e){
                    Snackbar.make(okNewEggEntry,"Invalid Input Detected",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                }
                catch(ParseException e){

                }
            }
        });
        cancelNewEggEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow_EggEntry.dismiss();
            }
        });
        sellEggBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eggSummary.getTotalSold()>=eggSummary.getTotalEggs()){
                    Snackbar.make(v,"You do not have eggs, Record for today first",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                }
                else {
                    popupWindow_SellEgg.setAnimationStyle(android.R.style.Animation_Dialog);
                    popupWindow_SellEgg.showAtLocation(returnView, Gravity.CENTER, 0, 0);
                    Calendar cal = Calendar.getInstance();
                    String today = new SimpleDateFormat("dd/MM/yy").format(cal.getTime());
                    sellEggDate.setText(today);
                }
            }
        });
        sellEggDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    currentDateEntryView = sellEggDate;
                    Calendar calendar = Calendar.getInstance();
                    new DatePickerDialog(getActivity(),dateSetListener, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();
                }catch(Exception e){
                    Snackbar.make(v,"Application Error",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                }
            }
        });
        okSellEgg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int crates = 0,pieces = 0,amountDue = 0,amountPaid = 0;
                String s_crates = sellEggCrate.getText().toString().trim(),
                        s_pieces = sellEggPieces.getText().toString().trim(),
                        s_amountDue = sellEggAmountDue.getText().toString().trim(),
                        s_amountPaid = sellEggAmountPaid.getText().toString().trim(),
                        s_date = sellEggDate.getText().toString().trim(),
                        s_buyer = sellEggBuyerName.getText().toString().trim();
                try{
                    if(!s_crates.isEmpty())
                        crates = Integer.parseInt(s_crates);
                    if(!s_pieces.isEmpty())
                        pieces = Integer.parseInt(s_pieces);
                    if(!s_amountDue.isEmpty())
                        amountDue = Integer.parseInt(s_amountDue);
                    if(!s_amountPaid.isEmpty())
                        amountPaid = Integer.parseInt(s_amountPaid);
                    Date d = new SimpleDateFormat("dd/MM/yy").parse(s_date);
                    Calendar cal = Calendar.getInstance();cal.setTime(d);
                    FinanceSummary f = clsFinance.createSale(s_buyer+"-Egg Sale",s_buyer+" buys "+crates+" crates "+pieces+" pieces",
                            crates,amountDue,amountPaid,clsFinance.EGG_SALE_TYPE, cal);
                    RealmController.getInstance().updateFinance(f);
                    int totalEgs = (crates*30)+pieces;
                    eggSummary.setTotalSold(eggSummary.getTotalSold()+totalEgs);
                    eggSummary.setUpdated(s_date);
                    DatabaseReference summaryRef = FirebaseDatabase.getInstance().getReference()
                            .child(PoultryApplication.CURRENT_FARM_CODE+"/Eggs/Summary");
                    summaryRef.setValue(eggSummary);
                    sellEggBuyerName.setText("");
                    sellEggPieces.setText("");
                    sellEggCrate.setText("");
                    sellEggAmountDue.setText("");
                    sellEggAmountPaid.setText("");
                    popupWindow_SellEgg.dismiss();
                    ShowUpdatedEggInfo();
                }
                catch(NumberFormatException e){
                    Snackbar.make(v,"Invalid Number detected",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                }
                catch(ParseException e){
                    Snackbar.make(v,"Invalid Date detected",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                }
            }
        });
        cancelSellEgg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow_SellEgg.dismiss();
            }
        });
        return returnView;
    }

    private TextView currentDateEntryView = null;//to hold the textview to set the selected date;
    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar date = Calendar.getInstance();
            date.set(Calendar.YEAR, year);
            date.set(Calendar.MONTH, month);
            date.set(Calendar.DAY_OF_MONTH, day);
            currentDateEntryView.setText(new SimpleDateFormat("dd/MM/yy").format(date.getTime()));
        }
    };


    private void downloadSelected(){//download selected date or date range info for egg
        String dateRange = selectedCountDate_DateRange.getText().toString();
        selectedCountView.setText("");
        dialog.setMessage("Getting Info");
        if(dateRange.length()==8){//one date selected
            try {
                Calendar cal = Calendar.getInstance();
                cal.setTime(new SimpleDateFormat("dd/MM/yy").parse(dateRange));
                dateRange = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
                dialog.show();
                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                DatabaseReference eggDateNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Eggs/"+dateRange);
                eggDateNode.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        clsEggs total = new clsEggs();
                        for(DataSnapshot d : dataSnapshot.getChildren()){
                            total.setEggCount(total.getEggCount()+d.getValue(clsEggs.class).getEggCount());
                            total.setCracked(total.getCracked()+d.getValue(clsEggs.class).getCracked());
                        }
                        selectedCountView.setText(total.displayEggInfo());
                        dialog.dismiss();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        dialog.dismiss();
                        Snackbar.make(selectDate,"Failed to Get Info",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                    }
                });
            }
            catch (Exception e){
                Snackbar.make(selectedCountDate_DateRange,"Failed to Get Egg",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
            }
        }
        else{//range of date selected
            try {
                String sd = dateRange.split("-")[0].trim();
                String ed = dateRange.split("-")[1].trim();
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(new SimpleDateFormat("dd/MM/yy").parse(sd));
                sd = new SimpleDateFormat("yyyyMMdd").format(cal1.getTime());
                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(new SimpleDateFormat("dd/MM/yy").parse(ed));
                ed = new SimpleDateFormat("yyyyMMdd").format(cal2.getTime());
                final int sDateId = Integer.parseInt(sd), eDateId = Integer.parseInt(ed);
                dialog.show();
                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                DatabaseReference eggDateNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Eggs");
                eggDateNode.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        clsEggs total = new clsEggs();
                        for(DataSnapshot d : dataSnapshot.getChildren()){
                            int date = Integer.parseInt(d.getKey());
                            if(date>=sDateId&&date<=eDateId){
                                for(DataSnapshot dd : d.getChildren()){
                                    total.setEggCount(total.getEggCount()+dd.getValue(clsEggs.class).getEggCount());
                                    total.setCracked(total.getCracked()+dd.getValue(clsEggs.class).getCracked());
                                }
                            }
                        }
                        selectedCountView.setText(total.displayEggInfo());
                        dialog.dismiss();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        dialog.dismiss();
                        Snackbar.make(selectDateRange,"Failed to Get Info",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                    }
                });
            }
            catch (Exception e){
                Snackbar.make(selectedCountDate_DateRange,"Failed to Get Egg",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
            }
        }
    }
    private void downloadDateEntryPreviousRecord(){
        try{
            clsBirds bird = (clsBirds) selectLayerOwner.getSelectedItem();
            dialog.setMessage("Getting Previous Value");
            String date = eggDateEntry.getText().toString().trim();
            Calendar cal = Calendar.getInstance();
            cal.setTime(new SimpleDateFormat("dd/MM/yy").parse(date));
            date = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
            dialog.show();
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            DatabaseReference eggDateNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Eggs/"+date+"/"+bird.getId());
            eggDateNode.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    clsEggs e = dataSnapshot.getValue(clsEggs.class);
                    if(e!=null){
                        prevCracked = e.getCracked();
                        prevEggs = e.getEggCount();
                        numberOfCratesEntry.setText((e.getEggCount()/30)+"");
                        numberOfEggsEntry.setText((e.getEggCount()%30)+"");
                        numberOfCrackedEntry.setText(e.getCracked()+"");
                    }
                    else{
                        prevCracked = 0;
                        prevEggs = 0;
                        numberOfCratesEntry.setText("0");
                        numberOfEggsEntry.setText("0");
                        numberOfCrackedEntry.setText("0");
                    }
                    dialog.dismiss();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    dialog.dismiss();
                    Snackbar.make(selectDate,"Failed to Previous Values",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                }
            });
        }
        catch(Exception e){
        }
    }
    private EggSummary eggSummary = new EggSummary();
    private clsEggs todayEgg = new clsEggs(),yesterdayEgg = new clsEggs();
    private long prevEggs, prevCracked;//to hold the value of egg being worked on so as to update eggSummary
    private void ShowUpdatedEggInfo(){
        long left = eggSummary.getTotalEggs() - eggSummary.getTotalSold();
        clsEggs eg = new clsEggs();
        eg.setEggCount(left);
        eggsBalanceCount.setText(eg.displayEggInfo());

        todayCount.setText(todayEgg.displayEggInfo());
        yesterdayCount.setText(yesterdayEgg.displayEggInfo());

        RealmEggSummary es = new RealmEggSummary(new SimpleDateFormat("dd/MM/yy").format(Calendar.getInstance().getTime()),
                todayEgg.getEggCount(),yesterdayEgg.getEggCount(),left);
        RealmController.with(this).updateEggSummary(es);
    }
    private void UpdateEggInfo(){
        String format = "yyyyMMdd";
        Calendar cal = Calendar.getInstance();
        String today = new SimpleDateFormat(format).format(cal.getTime());
        cal.add(Calendar.DATE,-1);
        String ysday = new SimpleDateFormat(format).format(cal.getTime());
        dialog.setMessage("Getting Informations");
        dialog.show();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference todayRef = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Eggs/"+today);
        DatabaseReference ysdayRef = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Eggs/"+ysday);
        DatabaseReference eggSum = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Eggs/Summary");
        eggSum.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eggSummary = new EggSummary();
                if(dataSnapshot.getValue()!=null){
                    eggSummary = dataSnapshot.getValue(EggSummary.class);
                }
                ShowUpdatedEggInfo();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Snackbar.make(selectDate,"Failed to Get Egg Balance Info",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
            }
        });
        todayRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                todayEgg = new clsEggs();
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    todayEgg.setEggCount(todayEgg.getEggCount()+d.getValue(clsEggs.class).getEggCount());
                    todayEgg.setCracked(todayEgg.getCracked()+d.getValue(clsEggs.class).getCracked());
                }
                ShowUpdatedEggInfo();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Snackbar.make(selectDate,"Failed to Get Today Info",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
            }
        });
        ysdayRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                yesterdayEgg = new clsEggs();
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    yesterdayEgg.setEggCount(yesterdayEgg.getEggCount()+d.getValue(clsEggs.class).getEggCount());
                    yesterdayEgg.setCracked(yesterdayEgg.getCracked()+d.getValue(clsEggs.class).getCracked());
                }
                ShowUpdatedEggInfo();
                dialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialog.dismiss();
                Snackbar.make(selectDate,"Failed to Get Yesterday Info",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
            }
        });

    }
}
