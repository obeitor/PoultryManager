package com.softobt.extraactivities;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.softobt.mainapplication.PoultryApplication;
import com.softobt.models.Farm;
import com.softobt.poultrymanager.Launch;
import com.softobt.poultrymanager.R;
import com.softobt.simplehash.SimpleMD5;

import org.w3c.dom.Text;

public class InfoActivity extends AppCompatActivity {

    private TextView farmName, farmCode, emailAddress;
    private Button changePin;
    private ProgressDialog dialog;

    private EditText editFarmName;
    private Button changeFarmName;
    private ImageButton closeFarmName;
    private View changeFarmNameView;
    private PopupWindow changeFarmNamePopup;

    private EditText enterOldPin, enterNewPin, confirmNewPin;
    private Button okChangePin;
    private ImageButton closeChangePin;
    private View changePinView;
    private PopupWindow changePinPopup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ImageButton close = (ImageButton)findViewById(R.id.close_info_page);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dialog = new ProgressDialog(this);
        farmName = (TextView)findViewById(R.id.farmNameView);
        farmCode = (TextView)findViewById(R.id.farmCodeView);
        emailAddress = (TextView)findViewById(R.id.emailAddressView);
        changePin = (Button)findViewById(R.id.changePin);

        changeFarmNameView = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.change_farm_name,null);
        changeFarmNamePopup = new PopupWindow(changeFarmNameView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,
                true);
        editFarmName = (EditText) changeFarmNameView.findViewById(R.id.change_farm_name_entry);
        changeFarmName = (Button)changeFarmNameView.findViewById(R.id.ok_change_farm_name);
        closeFarmName = (ImageButton)changeFarmNameView.findViewById(R.id.close_change_farm_name);

        changePinView = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.change_pin,null);
        changePinPopup = new PopupWindow(changePinView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,
                true);
        enterOldPin = (EditText)changePinView.findViewById(R.id.enter_old_pin);
        enterNewPin = (EditText)changePinView.findViewById(R.id.enter_new_pin);
        confirmNewPin = (EditText)changePinView.findViewById(R.id.reenter_new_pin);
        okChangePin = (Button)changePinView.findViewById(R.id.ok_change_pin);
        closeChangePin = (ImageButton)changePinView.findViewById(R.id.close_change_pin);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){
            String email = auth.getCurrentUser().getEmail();
            emailAddress.setText(email);
        }
        final SharedPreferences preferences = getSharedPreferences(PoultryApplication.FARM_PREF,MODE_PRIVATE);
        String name = preferences.getString(PoultryApplication.FARM_NAME,"No Name");
        final String code = preferences.getString(PoultryApplication.FARM_CODE,"DEFAULT");
        farmCode.setText(code);
        farmName.setText(name);
        farmCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("FarmCode",code);
                clipboard.setPrimaryClip(clip);
                Snackbar.make(getWindow().getDecorView(), "Copied the farm Code to Clipboard!", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

            }
        });
        farmName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFarmNamePopup.setAnimationStyle(R.style.popup_transit);
                changeFarmNamePopup.showAtLocation(getWindow().getDecorView(), Gravity.CENTER,0,0);
                editFarmName.setText(farmName.getText().toString());
            }
        });
        changePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePinPopup.setAnimationStyle(R.style.popup_transit);
                changePinPopup.showAtLocation(getWindow().getDecorView(),Gravity.CENTER,0,0);
                confirmNewPin.setText("");enterNewPin.setText("");enterOldPin.setText("");
            }
        });
        changeFarmName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String farmname = editFarmName.getText().toString();
                if(farmname.length()<4){
                    Snackbar.make(getWindow().getDecorView(), "Farm name too short!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
                else{
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(PoultryApplication.FARM_NAME,farmname);
                    editor.commit();
                    Farm.updateFarmName(code,farmname);
                    changeFarmNamePopup.dismiss();
                    farmName.setText(farmname);
                    Snackbar.make(getWindow().getDecorView(), "Updated Farm Name!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }
        });
        closeFarmName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFarmNamePopup.dismiss();
            }
        });
        okChangePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String oldpin = enterOldPin.getText().toString();
                final String newpin = enterNewPin.getText().toString();
                final String confirmpin = confirmNewPin.getText().toString();
                if(oldpin.length()<4){
                    Snackbar.make(getWindow().getDecorView(),"Old Pin Incorrect",Snackbar.LENGTH_SHORT)
                            .setAction("Action",null).show();
                }
                else if(newpin.length()<4){
                    Snackbar.make(getWindow().getDecorView(),"New Pin Must be 4 - 6 digits",Snackbar.LENGTH_SHORT)
                            .setAction("Action",null).show();
                }
                else if(!newpin.equals(confirmpin)){
                    Snackbar.make(getWindow().getDecorView(),"New Pin and Confirm doesn't match",Snackbar.LENGTH_SHORT)
                            .setAction("Action",null).show();
                }
                else{
                    final String encodedpin = SimpleMD5.generatePassword(oldpin);
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference farmRef = db.child("farms/"+code);
                    dialog.setMessage("Confirming Pin Change...");
                    dialog.show();
                    farmRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Farm farm = dataSnapshot.getValue(Farm.class);
                            dialog.dismiss();
                            if(farm == null){
                                Snackbar.make(getWindow().getDecorView(), "Farm Doesn't Exist", Snackbar.LENGTH_SHORT)
                                        .setAction("Action", null).show();
                            }
                            else{
                                if(farm.getPin().equalsIgnoreCase(encodedpin)){
                                    Farm.updateFarmPin(code,newpin);
                                    changePinPopup.dismiss();
                                    Snackbar.make(getWindow().getDecorView(),"Pin Changed Successfully",Snackbar.LENGTH_SHORT)
                                            .setAction("Action",null).show();
                                }
                                else{
                                    Snackbar.make(getWindow().getDecorView(), "Incorrect Pin, Try Again", Snackbar.LENGTH_SHORT)
                                            .setAction("Action", null).show();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Snackbar.make(getWindow().getDecorView(), "Failed, Try Again", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                        }
                    });
                }
            }
        });
        closeChangePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePinPopup.dismiss();
            }
        });
    }
}
