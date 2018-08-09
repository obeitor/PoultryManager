package com.softobt.extraactivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.google.firebase.auth.FirebaseAuth;
import com.softobt.mainapplication.PoultryApplication;
import com.softobt.mainapplication.RealmController;
import com.softobt.poultrymanager.Launch;
import com.softobt.poultrymanager.R;

public class Settings extends AppCompatActivity {

    private Button closeFarmBtn,logOffBtn,confirmYesBtn,confirmNoBtn;
    private PopupWindow confirmDialogPopup;
    boolean shouldLogOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ImageButton close = (ImageButton)findViewById(R.id.close_settings_page);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        closeFarmBtn = (Button)findViewById(R.id.close_poultry);
        logOffBtn = (Button)findViewById(R.id.logOut_app);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View confirmDialogView = inflater.inflate(R.layout.confirm_dialog_layout,null);
        confirmDialogPopup = new PopupWindow(confirmDialogView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,
                true);
        confirmYesBtn = (Button)confirmDialogView.findViewById(R.id.yes_confirm);
        confirmNoBtn = (Button)confirmDialogView.findViewById(R.id.no_confirm);
        closeFarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shouldLogOff = false;
                showConfirmDialog();
            }
        });
        logOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shouldLogOff = true;
                showConfirmDialog();
            }
        });
        confirmYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialogPopup.dismiss();
                if(shouldLogOff)
                    FirebaseAuth.getInstance().signOut();
                closeFarm();
            }
        });
        confirmNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialogPopup.dismiss();
            }
        });
    }

    private void showConfirmDialog(){

        confirmDialogPopup.setAnimationStyle(R.style.popup_transit);
        confirmDialogPopup.showAtLocation(getWindow().getDecorView(), Gravity.CENTER,0,0);
    }
    private void closeFarm(){
        SharedPreferences preferences = getSharedPreferences(PoultryApplication.FARM_PREF,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PoultryApplication.FARM_CODE,"DEFAULT");
        editor.putString(PoultryApplication.FARM_NAME,"No name");
        editor.commit();
        RealmController.with(this).deleteAllRealmData();
        Intent i = new Intent(this,Launch.class);
        startActivity(i);
        finish();
    }
}
