package com.softobt.poultrymanager;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.softobt.adapters.BirdArrayAdapter;
import com.softobt.adapters.FinanceArrayAdapter;
import com.softobt.adapters.MedicArrayAdapter;
import com.softobt.mainapplication.PoultryApplication;
import com.softobt.mainapplication.RealmController;
import com.softobt.models.FinanceSummary;
import com.softobt.models.clsBirds;
import com.softobt.models.clsFinance;
import com.softobt.models.clsMedic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Abdulgafar Obeitor on 6/13/2017.
 */
public class ItemListFragment extends Fragment {
    private int itemType;
    private FloatingActionButton addItem;
    private ListView listView;
    private ProgressDialog dialog;
    private SwipeRefreshLayout swipeRefresh;

    private BirdArrayAdapter birdArrayAdapter;
    private ArrayList<clsBirds> birdSummaries;
    private FinanceArrayAdapter financeArrayAdapter;
    private ArrayList<FinanceSummary> financeSummaries;
    private ArrayList<clsMedic> medicSummaries;
    private MedicArrayAdapter medicArrayAdapter;

    private LayoutInflater layoutInflater;

    //Bird
    private View birdPopupView;
    private PopupWindow birdPopup;
    private Spinner birdTypeSelect,ageTypeSelect;
    private EditText birdNameEntry, birdAgeEntry,birdAmountPaidEntry, birdNumberEntry;
    private TextView birdArrivalDate;
    private Button birdSaveNewFlock;
    private ImageButton birdClosePopup;
    private CheckBox birdIsLayer;

    //Finance
    private View financePopupView;
    private PopupWindow financePopup;
    private Spinner financeTypeSelect;
    private EditText financeNameEntry,financeDescEntry,financeAmountEntry,financeAmountPaidEntry,financeItemCountEntry;
    private TextView financeTransactionDate;
    private Button financeSaveNewFinance;
    private ImageButton financeClosePopup;

    //Medics
    private View medicPopupView;
    private PopupWindow medicPopup;
    private Spinner medicBirdSelect,medicAgeUnitSelect;
    private ToggleButton medicTypeToggle;
    private EditText medicName, medicDiseaseName, medicAgeEntry,medicDaysCount;
    private TextView medicDueDate;
    private Button medicSaveMedication;
    private ImageButton medicClosePopup;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.item_list_fragment,container,false);
        addItem = (FloatingActionButton)returnView.findViewById(R.id.add_to_list_of_item);
        dialog = new ProgressDialog(getActivity());
        listView = (ListView) returnView.findViewById(R.id.list_of_item);
        swipeRefresh = (SwipeRefreshLayout)returnView.findViewById(R.id.refreshList);

        birdSummaries = new ArrayList<>();
        birdArrayAdapter = new BirdArrayAdapter(getActivity(),birdSummaries,BirdArrayAdapter.mainPageSummary);
        financeSummaries = new ArrayList<>();
        financeArrayAdapter = new FinanceArrayAdapter(getActivity(),financeSummaries);
        medicSummaries = new ArrayList<>();
        medicArrayAdapter = new MedicArrayAdapter(getActivity(),medicSummaries);

        layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(itemType== PoultryApplication.BIRDS) {
            addItem.setImageResource(R.drawable.add_chicken);
            listView.setAdapter(birdArrayAdapter);
            loadBirds();
            initializeBirdPopups();
        }
        else if(itemType==PoultryApplication.MEDICATION) {
            addItem.setImageResource(R.drawable.add_med);
            listView.setAdapter(medicArrayAdapter);
            loadMedications();
            initializeMedicPopups();
        }
        else if(itemType==PoultryApplication.FINANCE) {
            addItem.setImageResource(R.drawable.add_finance);
            listView.setAdapter(financeArrayAdapter);
            loadFinances();
            initializeFinancePopups();
        }
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMainButtonAddClick();
            }
        });
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(itemType==PoultryApplication.BIRDS){
                    loadBirdsFromServer();
                }
                else if(itemType==PoultryApplication.FINANCE){
                    loadFinancesFromServer();
                }
                else if(itemType==PoultryApplication.MEDICATION){
                    loadMedicationsFromServer();
                }
            }
        });
        return returnView;
    }
    public ItemListFragment(){

    }
    public void setItemType(int itemType){
        this.itemType = itemType;
    }
    private void initializeBirdPopups(){
        //BIRDS
        birdPopupView = layoutInflater.inflate(R.layout.add_bird_record,null);
        birdPopup = new PopupWindow(birdPopupView,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,
                true);
        birdNameEntry = (EditText)birdPopupView.findViewById(R.id.bird_enter_name);
        birdAgeEntry = (EditText)birdPopupView.findViewById(R.id.bird_age_onArrival);
        birdIsLayer = (CheckBox)birdPopupView.findViewById(R.id.bird_if_LaysEgg);
        birdTypeSelect = (Spinner)birdPopupView.findViewById(R.id.select_bird_type);
        ageTypeSelect = (Spinner)birdPopupView.findViewById(R.id.select_age_type);
        birdAmountPaidEntry = (EditText)birdPopupView.findViewById(R.id.bird_amount_paid);
        birdNumberEntry = (EditText)birdPopupView.findViewById(R.id.bird_enter_noOfBirds);
        birdArrivalDate = (TextView)birdPopupView.findViewById(R.id.bird_arrival_date);
        birdClosePopup = (ImageButton)birdPopupView.findViewById(R.id.close_new_flock_pop);
        birdSaveNewFlock = (Button)birdPopupView.findViewById(R.id.bird_add_btn);
        birdArrivalDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    currentDateEntryView = birdArrivalDate;
                    Calendar calendar = Calendar.getInstance();
                    birdArrivalDate.setText(dateFormat.format(calendar.getTime()));
                    new DatePickerDialog(getActivity(),dateSetListener, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();
                }catch(Exception e){
                    Snackbar.make(v,"Application Error",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                }
            }
        });
        birdClosePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                birdPopup.dismiss();
            }
        });
        birdTypeSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==1){
                    birdIsLayer.setChecked(true);
                }
                else{
                    birdIsLayer.setChecked(false);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        birdSaveNewFlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = birdNameEntry.getText().toString();
                final String type = birdTypeSelect.getSelectedItem().toString();
                final String age = birdAgeEntry.getText().toString().isEmpty()?"1":birdAgeEntry.getText().toString().trim()
                    +"-"+ageTypeSelect.getSelectedItem().toString();
                final String date = birdArrivalDate.getText().toString();
                final long amount = birdAmountPaidEntry.getText().toString().isEmpty()?0:Long.parseLong(birdAmountPaidEntry.getText().toString());
                final int number = birdNumberEntry.getText().toString().isEmpty()?0:Integer.parseInt(birdNumberEntry.getText().toString());
                if(amount<1){
                    Snackbar.make(getActivity().getWindow().getDecorView(),"Amount is 0",Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }
                else if(date.isEmpty()){
                    Snackbar.make(getActivity().getWindow().getDecorView(),"No Date Selected",Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }
                else if(number<1){
                    Snackbar.make(getActivity().getWindow().getDecorView(),"Number of Birds is 0",Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }
                else if(name.isEmpty()){
                    Snackbar.make(getActivity().getWindow().getDecorView(),"Enter Name for Flock",Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }
                else{
                    final Calendar dateCal = Calendar.getInstance();
                    try{
                        dateCal.setTime(dateFormat.parse(date));
                    }
                    catch(ParseException e){

                    }
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference bulkCount = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Bulk-count");
                    bulkCount.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int count = dataSnapshot.getValue(Integer.class)==null?0:dataSnapshot.getValue(Integer.class);
                            clsBirds bird = clsBirds.addBirdFlock((count+1),name,type,birdIsLayer.isChecked(),number,age,dateCal);
                            clsFinance.createExpenditure("Bird Purchase","Bought new "+type,number,amount,amount,clsFinance.BIRD_PURCHASE,dateCal);
                            birdSummaries.add(bird);
                            RealmController.with(ItemListFragment.this).updateBirds(birdSummaries);
                            birdArrayAdapter.notifyDataSetChanged();
                            birdPopup.dismiss();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Snackbar.make(getActivity().getWindow().getDecorView(),"Failed To Save online",Snackbar.LENGTH_LONG)
                                    .setAction("Action",null).show();
                        }
                    });

                }
            }
        });
    }
    private void initializeFinancePopups(){
        //FINANCE
        financePopupView = layoutInflater.inflate(R.layout.add_finance_record,null);
        financePopup = new PopupWindow(financePopupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,
                true);
        financeAmountEntry = (EditText)financePopupView.findViewById(R.id.finance_amount);
        financeAmountPaidEntry = (EditText)financePopupView.findViewById(R.id.finance_amount_paid);
        financeDescEntry = (EditText)financePopupView.findViewById(R.id.finance_desc);
        financeItemCountEntry = (EditText)financePopupView.findViewById(R.id.finance_no_items);
        financeNameEntry = (EditText)financePopupView.findViewById(R.id.finance_name);
        financeSaveNewFinance = (Button)financePopupView.findViewById(R.id.credit_debit_btn);
        financeClosePopup = (ImageButton)financePopupView.findViewById(R.id.close_credit_debit_pop);
        financeTypeSelect = (Spinner)financePopupView.findViewById(R.id.select_fin_type);
        financeTransactionDate = (TextView)financePopupView.findViewById(R.id.finance_trans_date);
        financeTransactionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    currentDateEntryView = financeTransactionDate;
                    Calendar calendar = Calendar.getInstance();
                    financeTransactionDate.setText(dateFormat.format(calendar.getTime()));
                    new DatePickerDialog(getActivity(),dateSetListener, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();
                }catch(Exception e){
                    Snackbar.make(v,"Application Error",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                }
            }
        });
        financeTypeSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0://add investment
                        financeSaveNewFinance.setEnabled(true);
                        financeSaveNewFinance.setBackgroundResource(R.drawable.green_btn);
                        financeSaveNewFinance.setText("CREDIT");
                        financeNameEntry.setText("Investment");
                        financeNameEntry.setEnabled(false);
                        financeAmountPaidEntry.setEnabled(false);financeAmountPaidEntry.setText(financeAmountEntry.getText().toString());
                        financeItemCountEntry.setText("1");financeItemCountEntry.setEnabled(false);
                        break;
                    case 1://purchase fixed asset
                        financeSaveNewFinance.setEnabled(true);
                        financeSaveNewFinance.setBackgroundResource(R.drawable.red_btn);
                        financeSaveNewFinance.setText("DEBIT");
                        financeNameEntry.setText("");
                        financeNameEntry.setEnabled(true);
                        financeAmountPaidEntry.setEnabled(true);financeAmountPaidEntry.setText("0");
                        financeItemCountEntry.setText("0");financeItemCountEntry.setEnabled(true);
                        break;
                    case 2://pay for service
                        financeSaveNewFinance.setEnabled(true);
                        financeSaveNewFinance.setBackgroundResource(R.drawable.red_btn);
                        financeSaveNewFinance.setText("DEBIT");
                        financeNameEntry.setText("");
                        financeNameEntry.setEnabled(true);
                        financeAmountPaidEntry.setEnabled(true);financeAmountPaidEntry.setText("0");
                        financeItemCountEntry.setText("1");financeItemCountEntry.setEnabled(false);
                        break;
                    case 3://loan repayment
                        financeSaveNewFinance.setEnabled(true);
                        financeSaveNewFinance.setBackgroundResource(R.drawable.red_btn);
                        financeSaveNewFinance.setText("DEBIT");
                        financeNameEntry.setText("Loan Payment");
                        financeNameEntry.setEnabled(false);
                        financeAmountPaidEntry.setEnabled(false);financeAmountPaidEntry.setText(financeAmountEntry.getText().toString());
                        financeItemCountEntry.setText("1");financeItemCountEntry.setEnabled(false);
                        break;
                    default:
                        financeSaveNewFinance.setEnabled(false);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                financeSaveNewFinance.setEnabled(false);
            }
        });

        financeClosePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                financePopup.dismiss();
            }
        });
        financeSaveNewFinance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String desc = financeDescEntry.getText().toString();
                String name = financeNameEntry.getText().toString();
                String date = financeTransactionDate.getText().toString();
                double amount = financeAmountEntry.getText().toString().isEmpty()?0:Double.parseDouble(financeAmountEntry.getText().toString());
                double amountPaid = financeAmountPaidEntry.getText().toString().isEmpty()?0:Double.parseDouble(financeAmountPaidEntry.getText().toString());
                int items = financeItemCountEntry.getText().toString().isEmpty()?0:Integer.parseInt(financeItemCountEntry.getText().toString());
                if(amount<1){
                    Snackbar.make(getActivity().getWindow().getDecorView(),"Amount is 0",Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }
                else if(date.isEmpty()){
                    Snackbar.make(getActivity().getWindow().getDecorView(),"No Date Selected",Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }
                else if(name.isEmpty()){
                    Snackbar.make(getActivity().getWindow().getDecorView(),"Enter Transaction Name",Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }
                else {
                    Calendar dateCal = Calendar.getInstance();
                    try{
                        dateCal.setTime(dateFormat.parse(date));
                    }
                    catch(ParseException e){

                    }
                    FinanceSummary summary = null;
                    switch (financeTypeSelect.getSelectedItemPosition()) {
                        case 0:
                            summary = clsFinance.createInvestment(amount, desc, dateCal);
                            break;
                        case 1:
                            summary = clsFinance.createExpenditure(name, desc, items, amountPaid, amount, clsFinance.FIXED_ITEM_PURCHASE, dateCal);
                            break;
                        case 2:
                            summary = clsFinance.createServicePayment(amount, amountPaid, desc, name, dateCal);
                            break;
                        case 3:
                            summary = clsFinance.createLoanRepay(amount, desc, dateCal);
                            break;
                        default:
                            break;
                    }
                    if(summary!=null){
                        RealmController.with(ItemListFragment.this).updateFinance(summary);
                        financeSummaries.clear();financeSummaries.addAll(RealmController.getInstance().getSomeFinancesToView());
                    }
                }
                financeArrayAdapter.notifyDataSetChanged();
                financePopup.dismiss();
            }
        });
    }

    private void initializeMedicPopups(){
        medicPopupView = layoutInflater.inflate(R.layout.add_medic_record,null);
        medicPopup = new PopupWindow(medicPopupView,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,
                true);
        medicAgeEntry = (EditText) medicPopupView.findViewById(R.id.medic_bird_age);
        medicAgeUnitSelect = (Spinner)medicPopupView.findViewById(R.id.medic_sel_age_type);
        medicBirdSelect = (Spinner)medicPopupView.findViewById(R.id.medic_select_bird);
        medicBirdSelect.setAdapter(new BirdArrayAdapter(getActivity(), RealmController.with(this).getActiveFlocks(),BirdArrayAdapter.spinnerSummary));
        medicClosePopup = (ImageButton)medicPopupView.findViewById(R.id.close_new_medic_pop);
        medicDiseaseName = (EditText)medicPopupView.findViewById(R.id.medic_disease);
        medicDueDate = (TextView)medicPopupView.findViewById(R.id.medic_due_date);
        medicName = (EditText)medicPopupView.findViewById(R.id.medic_drug_name);
        medicSaveMedication = (Button)medicPopupView.findViewById(R.id.medic_add_btn);
        medicTypeToggle = (ToggleButton)medicPopupView.findViewById(R.id.medic_type_toggle);
        medicDaysCount = (EditText)medicPopupView.findViewById(R.id.medic_days_count);
        medicDaysCount.setText("1");
        medicClosePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medicPopup.dismiss();
            }
        });
        medicDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    currentDateEntryView = medicDueDate;
                    Calendar calendar = Calendar.getInstance();
                    medicDueDate.setText(dateFormat.format(calendar.getTime()));
                    new DatePickerDialog(getActivity(),dateSetListener, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();
                }catch(Exception e){
                    Snackbar.make(v,"Application Error",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                }
            }
        });
        medicTypeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    medicDueDate.setEnabled(false);
                    medicDueDate.setText("");
                    medicAgeEntry.setEnabled(true);
                }
                else{
                    medicDueDate.setEnabled(true);
                    medicAgeEntry.setEnabled(false);
                    medicAgeEntry.setText("0");
                }
            }
        });
        medicSaveMedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clsBirds bird = (clsBirds) medicBirdSelect.getSelectedItem();
                String name = medicName.getText().toString();
                String date = medicDueDate.getText().toString();
                String disease = medicDiseaseName.getText().toString();
                int age = medicAgeEntry.getText().toString().isEmpty()?0:Integer.parseInt(medicAgeEntry.getText().toString());
                int daysCount = medicDaysCount.getText().toString().isEmpty()?1:Integer.parseInt(medicDaysCount.getText().toString());
                String ageUnit = medicAgeUnitSelect.getSelectedItem().toString();
                Calendar dueDateCal = null;
                if(bird==null){
                    Snackbar.make(getActivity().getWindow().getDecorView(),"Please Select a Flock",Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }
                if(name.isEmpty()){
                    Snackbar.make(getActivity().getWindow().getDecorView(),"Please Enter Medication Name",Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }
                else if(disease.isEmpty()){
                    Snackbar.make(getActivity().getWindow().getDecorView(),"Please enter Disease or Unknown",Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }
                else if(date.isEmpty()&&!medicTypeToggle.isChecked()) {
                    Snackbar.make(getActivity().getWindow().getDecorView(), "No Date Selected", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else if(age==0&&medicTypeToggle.isChecked()) {
                    Snackbar.make(getActivity().getWindow().getDecorView(), "Age not entered", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else{
                    if(!date.isEmpty()){
                        dueDateCal = Calendar.getInstance();
                        try{dueDateCal.setTime(dateFormat.parse(date));}catch(ParseException e){}
                    }
                    ArrayList<clsMedic> medics = clsMedic.addMedication(medicTypeToggle.isChecked(),bird,age,ageUnit,dueDateCal,name,disease,daysCount);
                    for(clsMedic medic : medics)
                        RealmController.with(ItemListFragment.this).updateMedic(medic);
                    medicSummaries.clear();
                    medicSummaries.addAll(RealmController.getInstance().getSomeMedicsToView());
                    medicArrayAdapter.notifyDataSetChanged();
                    medicPopup.dismiss();
                }
            }
        });
    }
    private void onMainButtonAddClick(){
        if(itemType==PoultryApplication.BIRDS){
            birdPopup.setAnimationStyle(R.style.popup_transit);
            birdPopup.showAtLocation(getActivity().getWindow().getDecorView(),Gravity.CENTER,0,0);
        }
        else if(itemType==PoultryApplication.MEDICATION){
            medicPopup.setAnimationStyle(R.style.popup_transit);
            medicPopup.showAtLocation(getActivity().getWindow().getDecorView(),Gravity.CENTER,0,0);
        }
        else if(itemType == PoultryApplication.FINANCE){
            financePopup.setAnimationStyle(R.style.popup_transit);
            financePopup.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER,0,0);
        }
    }



    private void loadFinancesFromServer(){
        swipeRefresh.setRefreshing(true);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference financeSummaryNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/FinanceSummary");
        financeSummaryNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<FinanceSummary> sums = new ArrayList<>();
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    for(DataSnapshot dd : d.getChildren()) {
                        FinanceSummary summary = dd.getValue(FinanceSummary.class);
                        sums.add(summary);
                    }
                }
                RealmController.getInstance().updateFinances(sums);
                financeSummaries.clear();
                financeSummaries.addAll(RealmController.getInstance().getSomeFinancesToView());
                financeArrayAdapter.notifyDataSetChanged();
                Snackbar.make(getActivity().getWindow().getDecorView(),"Showing from a Week ago",Snackbar.LENGTH_LONG)
                        .setAction("Action",null).show();
                swipeRefresh.setRefreshing(false);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                swipeRefresh.setRefreshing(false);
                Snackbar.make(getActivity().getWindow().getDecorView(),"Failed To Get Finances",Snackbar.LENGTH_LONG)
                        .setAction("Action",null).show();
            }
        });
    }
    private void loadFinances(){
        financeSummaries.clear();
        financeSummaries.addAll(RealmController.with(ItemListFragment.this).getSomeFinancesToView());
        financeArrayAdapter.notifyDataSetChanged();
        Snackbar.make(getActivity().getWindow().getDecorView(),"Showing from a Week ago, Pull-down to Reload",Snackbar.LENGTH_LONG)
                .setAction("Action",null).show();
    }
    private void loadMedications(){
        medicSummaries.clear();
        medicSummaries.addAll(RealmController.with(ItemListFragment.this).getSomeMedicsToView());
        medicArrayAdapter.notifyDataSetChanged();
        Snackbar.make(getActivity().getWindow().getDecorView(),"Showing from a Week ago, Pull-down to Reload",Snackbar.LENGTH_LONG)
                .setAction("Action",null).show();
    }
    private void loadMedicationsFromServer(){
        swipeRefresh.setRefreshing(true);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference medicSummaryNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Medic");
        medicSummaryNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<clsMedic> sums = new ArrayList<>();
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    for(DataSnapshot dd : d.getChildren()) {
                        clsMedic summary = dd.getValue(clsMedic.class);
                        sums.add(summary);
                    }
                }
                RealmController.getInstance().updateMedics(sums);
                medicSummaries.clear();
                medicSummaries.addAll(RealmController.getInstance().getSomeMedicsToView());
                medicArrayAdapter.notifyDataSetChanged();
                Snackbar.make(getActivity().getWindow().getDecorView(),"Showing from a Week ago",Snackbar.LENGTH_LONG)
                        .setAction("Action",null).show();
                swipeRefresh.setRefreshing(false);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                swipeRefresh.setRefreshing(false);
                Snackbar.make(getActivity().getWindow().getDecorView(),"Failed To Get Medications",Snackbar.LENGTH_LONG)
                        .setAction("Action",null).show();
            }
        });
    }

    private void loadBirdsFromServer(){
        swipeRefresh.setRefreshing(true);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference birdSummaryNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Birds");
        birdSummaryNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<clsBirds> birds = new ArrayList<>();
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    clsBirds bird = d.getValue(clsBirds.class);
                    bird.setId(d.getKey());
                    birds.add(bird);
                }
                RealmController.with(ItemListFragment.this).updateBirds(birds);
                birdSummaries.clear();
                birdSummaries.addAll(RealmController.with(ItemListFragment.this).getActiveFlocks());
                birdArrayAdapter.notifyDataSetChanged();
                Snackbar.make(getActivity().getWindow().getDecorView(),"Showing all Flocks",Snackbar.LENGTH_LONG)
                        .setAction("Action",null).show();
                swipeRefresh.setRefreshing(false);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                swipeRefresh.setRefreshing(false);
                birdSummaries.clear();
                birdSummaries.addAll(RealmController.with(ItemListFragment.this).getActiveFlocks());
                birdArrayAdapter.notifyDataSetChanged();
                Snackbar.make(getActivity().getWindow().getDecorView(),"Couldn't load from server",Snackbar.LENGTH_LONG)
                        .setAction("Action",null).show();
            }
        });
    }
    private void loadBirds(){
        swipeRefresh.setRefreshing(true);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference birdSummaryNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Bulk-count");
        birdSummaryNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = dataSnapshot.getValue(Integer.class)==null?0:dataSnapshot.getValue(Integer.class);
                if(count>RealmController.with(ItemListFragment.this).getActiveFlocks().size()){
                    loadBirdsFromServer();
                }
                else{
                    birdSummaries.clear();
                    birdSummaries.addAll(RealmController.with(ItemListFragment.this).getActiveFlocks());
                    birdArrayAdapter.notifyDataSetChanged();
                    swipeRefresh.setRefreshing(false);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                swipeRefresh.setRefreshing(false);
                birdSummaries.clear();
                birdSummaries.addAll(RealmController.with(ItemListFragment.this).getActiveFlocks());
                birdArrayAdapter.notifyDataSetChanged();
                Snackbar.make(getActivity().getWindow().getDecorView(),"Couldn't load from server",Snackbar.LENGTH_LONG)
                        .setAction("Action",null).show();
            }
        });
    }



    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
    private TextView currentDateEntryView = null;//to hold the textview to set the selected date;
    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar date = Calendar.getInstance();
            date.set(Calendar.YEAR, year);
            date.set(Calendar.MONTH, month);
            date.set(Calendar.DAY_OF_MONTH, day);
            currentDateEntryView.setText(dateFormat.format(date.getTime()));
        }
    };
}
