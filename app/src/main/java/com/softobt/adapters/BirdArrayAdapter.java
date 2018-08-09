package com.softobt.adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.media.Image;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.softobt.mainapplication.PoultryApplication;
import com.softobt.mainapplication.RealmController;
import com.softobt.models.Death;
import com.softobt.models.FinanceSummary;
import com.softobt.models.clsBirds;
import com.softobt.models.clsFinance;
import com.softobt.poultrymanager.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Abdulgafar Obeitor on 8/4/2017.
 */
public class BirdArrayAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<clsBirds> birds;
    public static final int homePageSummary = 1;
    public static final int mainPageSummary = 2;
    public static final int spinnerSummary = 3;
    private int whichSummary;

    /**
     *
     * @param context
     * @param birds
     * @param whichSummary
     */
    public BirdArrayAdapter(Context context, ArrayList<clsBirds> birds, int whichSummary){
        this.whichSummary = whichSummary;
        this.context = context;
        this.birds = birds;
    }
    @Override
    public int getCount() {
        return birds.size();
    }

    @Override
    public Object getItem(int position) {
        return birds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        final clsBirds bird = birds.get(position);
        if(this.whichSummary==mainPageSummary) {
            if(convertView==null)
                convertView = inflater.inflate(R.layout.list_bird_summaries, null);
            TextView name = (TextView) convertView.findViewById(R.id.bird_name_inList);
            TextView age = (TextView) convertView.findViewById(R.id.birds_age_inList);
            TextView type = (TextView) convertView.findViewById(R.id.birdType_inList);
            TextView active = (TextView) convertView.findViewById(R.id.no_active_birds_inList);
            TextView sold = (TextView) convertView.findViewById(R.id.no_sold_birds_inList);
            TextView sick = (TextView) convertView.findViewById(R.id.no_sick_birds_inList);
            TextView dead = (TextView) convertView.findViewById(R.id.no_dead_birds_inList);
            ImageButton feed = (ImageButton)convertView.findViewById(R.id.feedBird);
            ImageButton sell = (ImageButton) convertView.findViewById(R.id.recordBirdSale);
            ImageButton recSick = (ImageButton) convertView.findViewById(R.id.recordBirdSick);
            ImageButton recDeath = (ImageButton) convertView.findViewById(R.id.recordBirdDeath);
            name.setText(bird.getName());
            age.setText(bird.getAge());
            type.setText(bird.getType());
            active.setText(bird.getActive() + " Active");
            sold.setText(bird.getSold() + " Sold");
            sick.setText(bird.getSick() + " Sick");
            dead.setText(bird.getDead() + " Dead");

            View editSickView = inflater.inflate(R.layout.edit_sick_poultry,null);
            final PopupWindow editSickPopup = new PopupWindow(editSickView,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,
                    true);
            final EditText sickEntry = (EditText) editSickView.findViewById(R.id.edit_number_of_sick);
            Button okSick = (Button)editSickView.findViewById(R.id.ok_update_number_of_sick);
            ImageButton closeSick = (ImageButton)editSickView.findViewById(R.id.close_edit_sick);

            View birdSaleView = inflater.inflate(R.layout.sell_poultry_bird,null);
            final PopupWindow birdSalePopup = new PopupWindow(birdSaleView,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,
                    true);
            final EditText birdSaleNumberOfBirds = (EditText)birdSaleView.findViewById(R.id.enter_number_of_birds_selling);
            final EditText birdSaleAmountDue = (EditText)birdSaleView.findViewById(R.id.enter_amount_due_birdSale);
            final EditText birdSaleAmountCollected = (EditText)birdSaleView.findViewById(R.id.enter_amount_collected_birdSale);
            Button okSell = (Button)birdSaleView.findViewById(R.id.ok_sell_birds);
            ImageButton closeSale = (ImageButton)birdSaleView.findViewById(R.id.close_bird_sale);

            View recordDeathView = inflater.inflate(R.layout.record_death_bird,null);
            final PopupWindow recordDeathPopup = new PopupWindow(recordDeathView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,
                    true);
            final EditText recordNumberOfDeaths = (EditText)recordDeathView.findViewById(R.id.edit_number_of_dead);
            final TextView recordDeathDate = (TextView)recordDeathView.findViewById(R.id.edit_number_of_dead_date);
            Button okDead = (Button)recordDeathView.findViewById(R.id.ok_update_number_of_death);
            ImageButton closeDeath = (ImageButton)recordDeathView.findViewById(R.id.close_record_death);

            sell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    birdSalePopup.setAnimationStyle(R.style.popup_transit);
                    birdSalePopup.showAtLocation(((Activity)BirdArrayAdapter.this.context).getWindow().getDecorView(), Gravity.CENTER,0,0);
                }
            });
            recDeath.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recordDeathPopup.setAnimationStyle(R.style.popup_transit);
                    recordDeathPopup.showAtLocation(((Activity)BirdArrayAdapter.this.context).getWindow().getDecorView(), Gravity.CENTER,0,0);
                    recordDeathDate.setText(dateFormat.format(Calendar.getInstance().getTime()));
                    int dateInt = Integer.parseInt(intDateFormat.format(Calendar.getInstance().getTime()));
                    for(Death death : bird.getDeaths()){
                        if(death.getDate()==dateInt){
                            recordNumberOfDeaths.setText(""+death.getAmount());
                        }
                    }
                }
            });
            recSick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editSickPopup.setAnimationStyle(R.style.popup_transit);
                    editSickPopup.showAtLocation(((Activity)BirdArrayAdapter.this.context).getWindow().getDecorView(), Gravity.CENTER,0,0);
                    sickEntry.setText(bird.getSick()+"");
                }
            });
            okSick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int val = sickEntry.getText().toString().isEmpty()?0:Integer.parseInt(sickEntry.getText().toString().trim());
                    bird.setSick(val);
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference birdRecordNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Birds/"+bird.getId());
                    birdRecordNode.setValue(bird);
                    editSickPopup.dismiss();
                    BirdArrayAdapter.this.notifyDataSetChanged();
                }
            });
            closeSick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editSickPopup.dismiss();
                }
            });
            okSell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int numberOfBirds = birdSaleNumberOfBirds.getText().toString().isEmpty()?0:
                            Integer.parseInt(birdSaleNumberOfBirds.getText().toString().trim());
                    double amountPaid = birdSaleAmountCollected.getText().toString().isEmpty()?0:
                            Double.parseDouble(birdSaleAmountCollected.getText().toString().trim());
                    double amountDue = birdSaleAmountDue.getText().toString().isEmpty()?0:
                            Double.parseDouble(birdSaleAmountDue.getText().toString().trim());
                    if(numberOfBirds > bird.getActive()){
                        Snackbar.make(((Activity)BirdArrayAdapter.this.context).getWindow().getDecorView(),"Number of Birds more than" +
                                "Available Birds",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                    }
                    else{
                        bird.setSold(bird.getSold()+numberOfBirds);
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference birdRecordNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Birds/"+bird.getId()+"/sold");
                        birdRecordNode.setValue(bird.getSold());
                        FinanceSummary f =clsFinance.createSale("Bird Sale","Bird Sale "+bird.getName()+" "+bird.getType(),numberOfBirds,amountDue
                        ,amountPaid,clsFinance.BIRD_SALE_TYPE, Calendar.getInstance());
                        RealmController.getInstance().updateBird(bird);
                        RealmController.getInstance().updateFinance(f);
                        birdSalePopup.dismiss();
                        BirdArrayAdapter.this.notifyDataSetChanged();
                    }
                }
            });
            closeSale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    birdSalePopup.dismiss();
                }
            });
            recordDeathDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentDateEntryView = recordDeathDate;
                    BirdArrayAdapter.this.datebird = bird;
                    BirdArrayAdapter.this.currentDeathView = recordNumberOfDeaths;
                    Calendar calendar = Calendar.getInstance();
                    new DatePickerDialog((Activity)BirdArrayAdapter.this.context,dateSetListener, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
            okDead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int deadChickens = recordNumberOfDeaths.getText().toString().isEmpty()?0:
                            Integer.parseInt(recordNumberOfDeaths.getText().toString().trim());
                    String dateDeath = recordDeathDate.getText().toString().trim();
                    int dateDeathInt = 0;
                    try {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(dateFormat.parse(dateDeath));
                        dateDeathInt = Integer.parseInt(intDateFormat.format(cal.getTime()));
                    }catch(ParseException e){}
                    Death death = null;
                    for(Death d : bird.getDeaths()){
                        if(d.getDate()==dateDeathInt){
                            death = d;
                            d.setAmount(deadChickens);
                        }
                    }
                    if(death==null){
                        bird.getDeaths().add(new Death(dateDeathInt,deadChickens));
                    }
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference birdRecordNode = db.child(PoultryApplication.CURRENT_FARM_CODE+"/Birds/"+bird.getId()+"/deaths");
                    birdRecordNode.setValue(bird.getDeaths());
                    RealmController.getInstance().updateBird(bird);
                    recordDeathPopup.dismiss();
                }
            });
            closeDeath.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recordDeathPopup.dismiss();
                }
            });
        }
        else if(this.whichSummary==homePageSummary){
            if(convertView==null)
                convertView = inflater.inflate(R.layout.poultry_list_homepage_item,null);
            TextView name = (TextView)convertView.findViewById(R.id.poultry_name);
            TextView active = (TextView)convertView.findViewById(R.id.amt_avail);
            TextView dead = (TextView)convertView.findViewById(R.id.amt_dead);
            TextView sold = (TextView)convertView.findViewById(R.id.amt_sold);
            name.setText(bird.getName());
            active.setText(bird.getActive()+"");
            dead.setText(bird.getDead()+"");
            sold.setText(bird.getSold()+"");
        }
        else if(this.whichSummary==spinnerSummary){
            if(convertView==null)
                convertView = inflater.inflate(R.layout.spinner_item_custom,null);
            TextView textView = (TextView)convertView.findViewById(R.id.spinner_text);
            textView.setText(bird.getName()+" "+bird.getType());
        }
        return convertView;
    }

    private SimpleDateFormat intDateFormat = new SimpleDateFormat("yyyyMMdd");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private TextView currentDateEntryView = null;//to hold the textview to set the selected date;
    private EditText currentDeathView = null;
    private clsBirds datebird = null;
    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar date = Calendar.getInstance();
            date.set(Calendar.YEAR, year);
            date.set(Calendar.MONTH, month);
            date.set(Calendar.DAY_OF_MONTH, day);
            currentDateEntryView.setText(dateFormat.format(date.getTime()));
            int dateInt = Integer.parseInt(intDateFormat.format(date.getTime()));
            currentDeathView.setText("");
            for(Death death : datebird.getDeaths()){
                if(death.getDate()==dateInt){
                    currentDeathView.setText(""+death.getAmount());
                }
            }
        }
    };
}
