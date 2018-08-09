package com.softobt.poultrymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.*;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.softobt.extraactivities.InfoActivity;
import com.softobt.extraactivities.Settings;
import com.softobt.mainapplication.PoultryApplication;
import com.softobt.models.Farm;

public class MainActivity extends AppCompatActivity {

    private Toolbar topToolbar;
    private BottomNavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        topToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(topToolbar);
        startFarm();
        navigationView = (BottomNavigationView)findViewById(R.id.navbar);
        getSupportFragmentManager().beginTransaction().add(R.id.maincontainer,new HomeFragment()).commit();
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment replacementFrag = null;
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                int replacement = 0;
                switch (item.getItemId()){
                    case R.id.nav_home:
                        replacementFrag = new HomeFragment();
                        setBarTitle("");replacement = PoultryApplication.HOME;
                        break;
                    case R.id.nav_birds:
                        setBarTitle(R.string.birds);
                        replacementFrag = new ItemListFragment();
                        replacement = PoultryApplication.BIRDS;
                        ((ItemListFragment)replacementFrag).setItemType(PoultryApplication.BIRDS);
                        break;
                    case R.id.nav_egg:
                        setBarTitle(R.string.eggs);
                        replacementFrag = new EggFragment();
                        replacement = PoultryApplication.EGGS;
                        break;
                    case R.id.nav_finance:
                        setBarTitle(R.string.finance);
                        replacementFrag = new ItemListFragment();
                        replacement = PoultryApplication.FINANCE;
                        ((ItemListFragment)replacementFrag).setItemType(PoultryApplication.FINANCE);
                        break;
                    case R.id.nav_medic:
                        setBarTitle(R.string.medic);
                        replacementFrag = new ItemListFragment();
                        replacement = PoultryApplication.MEDICATION;
                        ((ItemListFragment)replacementFrag).setItemType(PoultryApplication.MEDICATION);
                        break;
                }
                if(replacement>PoultryApplication.CURRENT_PAGE){
                    transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                }
                else if(replacement<PoultryApplication.CURRENT_PAGE){
                    transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                }
                PoultryApplication.CURRENT_PAGE = replacement;
                transaction.replace(R.id.maincontainer,replacementFrag).commit();
                return true;
            }
        });
    }
    private void startFarm(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference farmRef = db.child("farms/"+PoultryApplication.CURRENT_FARM_CODE);
        farmRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Farm farm = dataSnapshot.getValue(Farm.class);
                if(farm == null){
                    Snackbar.make(getWindow().getDecorView(), "Farm Doesn't Exist", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else{
                    SharedPreferences preferences = getSharedPreferences(PoultryApplication.FARM_PREF,MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(PoultryApplication.FARM_NAME,farm.getName());
                    editor.commit();
                    getSupportActionBar().setTitle(farm.getName().toUpperCase());
                    PoultryApplication.currency = farm.getCurrency();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.poultry,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()){
            case R.id.menu_farm_info:
                intent = new Intent(this, InfoActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_settings:
                intent = new Intent(this,Settings.class);
                startActivity(intent);
                break;
            case R.id.menu_report:
                break;
        }
        return true;
    }

    protected void setBarTitle(String title){
        getSupportActionBar().setSubtitle(title);
    }
    protected void setBarTitle(int stringId){
        String title = "None";
        try {
            title = getResources().getString(stringId);
        }catch(NullPointerException e){
            title = "None";
        }
        getSupportActionBar().setSubtitle(title);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(PoultryApplication.FARM_PREF,MODE_PRIVATE);
        String fname = preferences.getString(PoultryApplication.FARM_NAME,"No Name");
        getSupportActionBar().setTitle(fname.toUpperCase());
    }
}
