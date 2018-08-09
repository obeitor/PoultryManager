package com.softobt.poultrymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.softobt.mainapplication.PoultryApplication;

public class Launch extends AppCompatActivity {

FirebaseAuth authentication;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        SharedPreferences preferences = getSharedPreferences(PoultryApplication.FARM_PREF,MODE_PRIVATE);
        authentication = FirebaseAuth.getInstance();
        Boolean firstOpen = preferences.getBoolean(PoultryApplication.FIRST_TIME,true);
        String farmcode = preferences.getString(PoultryApplication.FARM_CODE,"DEFAULT");
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PoultryApplication.FIRST_TIME, false);
        editor.commit();
        if(authentication.getCurrentUser()==null){//if no user logged in, come here
            if(firstOpen)
                getSupportFragmentManager().beginTransaction().add(R.id.container, new RegisterFragment()).commit();
            else
                getSupportFragmentManager().beginTransaction().add(R.id.container, new LoginFragment()).commit();
        }
        else {//if user logged in already
            if (farmcode.equalsIgnoreCase("DEFAULT")) {//if not linked to any farm
                getSupportFragmentManager().beginTransaction().add(R.id.container, new Start()).commit();
            } else {
                PoultryApplication.CURRENT_FARM_CODE = farmcode;
                Intent i = new Intent(this, MainActivity.class);
                i.putExtra(PoultryApplication.FARM_NAME, preferences.getString(PoultryApplication.FARM_NAME, "No Name"));
                startActivity(i);
                finish();
            }
        }
    }
    private Intent intent;
    protected void successfulLogin(String name){
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_bottom,R.anim.exit_to_bottom)
                .replace(R.id.container,new SuccessfulLogin()).commit();
        Snackbar.make(getWindow().getDecorView(),"Successful",Snackbar.LENGTH_LONG).setAction("Action",null).show();
        intent = new Intent(this,MainActivity.class);
        intent.putExtra(PoultryApplication.FARM_NAME,name);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Launch.this.startActivity(intent);
                Launch.this.finish();
            }
        },1000);
    }
}
