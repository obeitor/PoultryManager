package com.softobt.poultrymanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.util.PatternsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.softobt.mainapplication.PoultryApplication;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Abdulgafar Obeitor on 6/14/2017.
 */
public class RegisterFragment extends Fragment {
    private EditText emailEntry, passwordEntry, confirmPasswordEntry;
    private FirebaseAuth authentication;
    private Button regBtn;
    private TextView alreadyMemberBtn;
    private ProgressDialog registerProgress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.register_user_fragment,container,false);
        authentication = FirebaseAuth.getInstance();
        emailEntry = (EditText)returnView.findViewById(R.id.regemailEntry);
        passwordEntry = (EditText)returnView.findViewById(R.id.regpassEntry);
        confirmPasswordEntry = (EditText)returnView.findViewById(R.id.regconfirmpassEntry);
        regBtn = (Button)returnView.findViewById(R.id.signup);
        alreadyMemberBtn = (TextView) returnView.findViewById(R.id.alreadyreg);
        registerProgress = new ProgressDialog(getActivity());
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEntry.getText().toString().trim();
                String password = passwordEntry.getText().toString().trim();
                String confirm = confirmPasswordEntry.getText().toString().trim();
                if(email.isEmpty() || !PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()){
                    Snackbar.make(v, "Enter a Valid Email", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    return;
                }
                if(password.isEmpty() || password.length() < 8){
                    Snackbar.make(v, "Password should be 8 characters least", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    return;
                }
                if(!confirm.equals(password)){
                    Snackbar.make(v, "Passwords don't Match!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    return;
                }
                registerProgress.setMessage("Creating User...");registerProgress.show();
                authentication.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                registerProgress.dismiss();
                                if (!task.isSuccessful()) {
                                    Snackbar.make(regBtn, "Failed To Register!", Snackbar.LENGTH_SHORT)
                                            .setAction("Action", null).show();
                                }
                                else {

                                    SharedPreferences preferences = getActivity().getSharedPreferences(PoultryApplication.FARM_PREF,MODE_PRIVATE);
                                    String farmcode = preferences.getString(PoultryApplication.FARM_CODE,"DEFAULT");
                                    if(farmcode.equalsIgnoreCase("DEFAULT"))
                                        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_bottom,R.anim.exit_to_top)
                                                .replace(R.id.container, new Start()).commit();
                                    else{
                                        Intent i = new Intent(getActivity(), MainActivity.class);
                                        i.putExtra(PoultryApplication.FARM_NAME, preferences.getString(PoultryApplication.FARM_NAME, "No Name"));
                                        startActivity(i);
                                        getActivity().finish();
                                    }
                                }
                            }
                        });
            }
        });
        alreadyMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_bottom,R.anim.exit_to_top)
                        .replace(R.id.container,new LoginFragment()).commit();
            }
        });
        return returnView;
    }
}
