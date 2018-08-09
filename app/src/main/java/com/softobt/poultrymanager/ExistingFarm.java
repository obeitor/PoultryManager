package com.softobt.poultrymanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.softobt.bcrypt.BCrypt;
import com.softobt.mainapplication.PoultryApplication;
import com.softobt.models.Farm;
import com.softobt.simplehash.SHA;
import com.softobt.simplehash.SimpleMD5;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Abdulgafar Obeitor on 6/5/2017.
 */

public class ExistingFarm extends Fragment {
    private Button SIGN_IN;
    private Button NOT_EXIST;
    private EditText FARM_CODE;
    private EditText FARM_PASS;
    private ProgressDialog loginDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.existing_poultry_fragment,container,false);
        SIGN_IN = (Button)returnView.findViewById(R.id.signin);
        NOT_EXIST = (Button)returnView.findViewById(R.id.notexisting);
        FARM_CODE = (EditText)returnView.findViewById(R.id.farmcodeEntry);
        FARM_PASS = (EditText)returnView.findViewById(R.id.loginpassEntry);
        SIGN_IN.setOnClickListener(signinClick);
        NOT_EXIST.setOnClickListener(notExistClick);
        loginDialog = new ProgressDialog(getActivity());
        return returnView;
    }

    View.OnClickListener signinClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String farmcode = FARM_CODE.getText().toString();
            String pin  = FARM_PASS.getText().toString();
            if(farmcode.length()!=8){
                Snackbar.make(v, "Farm Code Invalid", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                return;
            }
            if(pin.length()<4){
                Snackbar.make(v, "Passcode Invalid", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                return;
            }
            loginToFarm();


        }
    };
    private void loginToFarm(){
        loginDialog.setMessage("Logging In");
        loginDialog.show();
        String farmcode = FARM_CODE.getText().toString().toUpperCase();
        final String pin = SimpleMD5.generatePassword(FARM_PASS.getText().toString());
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference farmRef = db.child("farms/"+farmcode);
        farmRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Farm farm = dataSnapshot.getValue(Farm.class);
                loginDialog.dismiss();
                if(farm == null){
                    Snackbar.make(SIGN_IN, "Farm Doesn't Exist", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
                else{
                    if(farm.getPin().equalsIgnoreCase(pin)){
                        SharedPreferences preferences = getActivity().getSharedPreferences(PoultryApplication.FARM_PREF, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(PoultryApplication.FARM_CODE, farm.getFarmCode());
                        editor.putString(PoultryApplication.FARM_NAME, farm.getName());
                        editor.commit();
                        PoultryApplication.CURRENT_FARM_CODE = farm.getFarmCode();
                        ((Launch)getActivity()).successfulLogin(farm.getName());
                    }
                    else{
                        Snackbar.make(SIGN_IN, "Incorrect Pin, Try Again", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Snackbar.make(SIGN_IN, "Failed, Try Again", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });
    }
    View.OnClickListener notExistClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_bottom,R.anim.exit_to_top)
                    .replace(R.id.container,new CreateFarm()).commit();
        }
    };
}
