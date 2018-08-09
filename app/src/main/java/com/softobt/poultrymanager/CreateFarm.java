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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.softobt.mainapplication.PoultryApplication;
import com.softobt.bcrypt.BCrypt;
import com.softobt.models.Farm;
import com.softobt.simplehash.SHA;
import com.softobt.simplehash.SimpleMD5;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Abdulgafar Obeitor on 6/5/2017.
 */

public class CreateFarm extends Fragment {
    private Button CREATE;
    private Button ALREADY_EXISTS;
    private EditText FARM_NAME;
    private EditText FARM_PASS;
    private EditText CONFIRM_PASS;
    private ProgressDialog creatingDialog;
    private String NEW_FARM_CODE = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.new_poultry_fragment,container,false);
        CREATE = (Button)returnView.findViewById(R.id.create);
        ALREADY_EXISTS = (Button)returnView.findViewById(R.id.alreadyexist);
        FARM_NAME = (EditText)returnView.findViewById(R.id.farmnameEntry);
        FARM_PASS = (EditText)returnView.findViewById(R.id.joinpassEntry);
        CONFIRM_PASS = (EditText)returnView.findViewById(R.id.confirmpassEntry);
        CREATE.setOnClickListener(createClick);
        ALREADY_EXISTS.setOnClickListener(alreadyExistClick);
        creatingDialog = new ProgressDialog(getActivity());
        return returnView;
    }
    View.OnClickListener createClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String farmname = FARM_NAME.getText().toString();
            String farmpass = FARM_PASS.getText().toString();
            String confirmpass  = CONFIRM_PASS.getText().toString();
            if(farmname.length()<4){
                Snackbar.make(getActivity().getWindow().getDecorView(), "Farm name too short!", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                return;
            }
            if(farmpass.length()<4){
                Snackbar.make(getActivity().getWindow().getDecorView(), "Must be 4 - 6 digits", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                return;
            }
            if(!confirmpass.equals(farmpass)){
                Snackbar.make(getActivity().getWindow().getDecorView(), "Please confirm passcode", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                return;
            }
            createFarm();
        }
    };
    private void createFarm(){
        creatingDialog.setMessage("Creating Farm...");
        creatingDialog.show();
        String password = FARM_PASS.getText().toString();
        String name = FARM_NAME.getText().toString();
        NEW_FARM_CODE = Farm.createFarm(name,password);
        if(NEW_FARM_CODE == null || NEW_FARM_CODE.isEmpty()){
            Snackbar.make(getActivity().getWindow().getDecorView(), "Failed, Please Try Again", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            creatingDialog.dismiss();
        }
        else{
            SharedPreferences preferences = getActivity().getSharedPreferences(PoultryApplication.FARM_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(PoultryApplication.FARM_CODE, NEW_FARM_CODE);
            editor.putString(PoultryApplication.FARM_NAME, name);
            editor.commit();
            creatingDialog.dismiss();
            PoultryApplication.CURRENT_FARM_CODE = NEW_FARM_CODE;
            ((Launch)getActivity()).successfulLogin(name);
        }
    }
    View.OnClickListener alreadyExistClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_bottom,R.anim.exit_to_top)
                    .replace(R.id.container,new ExistingFarm()).commit();
        }
    };
}
