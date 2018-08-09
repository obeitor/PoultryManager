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
import com.google.firebase.auth.*;
import com.softobt.mainapplication.PoultryApplication;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Abdulgafar Obeitor on 6/14/2017.
 */
public class LoginFragment extends Fragment {
    private EditText emailAddress, password;
    private FirebaseAuth authentication;
    private ProgressDialog signInProgress;
    private Button signin;
    private TextView forgotPassword, notMember;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.signin_user_fragment,container,false);
        emailAddress = (EditText)returnView.findViewById(R.id.emailEntry);
        password = (EditText)returnView.findViewById(R.id.loginpassEntry);
        signin = (Button)returnView.findViewById(R.id.login);
        notMember = (TextView) returnView.findViewById(R.id.notamember);
        forgotPassword = (TextView) returnView.findViewById(R.id.forgotpassword);
        signInProgress = new ProgressDialog(getActivity());
        authentication = FirebaseAuth.getInstance();
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailAddress.getText().toString().trim();
                final String pass = password.getText().toString().trim();
                if(email.trim().isEmpty()){
                    Snackbar.make(v, "Email Empty!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    return;
                }
                if(pass.trim().isEmpty()){
                    Snackbar.make(v, "Password Empty!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    return;
                }
                signInProgress.setMessage("Signing In...");
                signInProgress.show();
                authentication.signInWithEmailAndPassword(email,pass)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                signInProgress.dismiss();
                                if (!task.isSuccessful()) {
                                    Snackbar.make(signin, "Failed To Login!", Snackbar.LENGTH_SHORT)
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
        notMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_bottom,R.anim.exit_to_top)
                        .replace(R.id.container,new RegisterFragment()).commit();
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailAddress.getText().toString().trim();
                if(email.isEmpty() || !PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()){
                    Snackbar.make(v, "Enter Your Registered Email", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    return;
                }
                signInProgress.setMessage("Sending Reset Email!");
                signInProgress.show();
                authentication.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Snackbar.make(forgotPassword,"Successfully Sent Reset Email", Snackbar.LENGTH_SHORT)
                                            .setAction("Action", null).show();
                                } else {
                                    Snackbar.make(forgotPassword,"Failed To Send Email", Snackbar.LENGTH_SHORT)
                                            .setAction("Action", null).show();
                                }
                                signInProgress.dismiss();
                            }
                        });
            }
        });
        return returnView;
    }
}
