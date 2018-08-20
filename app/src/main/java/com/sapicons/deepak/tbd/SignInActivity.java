package com.sapicons.deepak.tbd;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Arrays;
import java.util.List;

public class SignInActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    String email, password;
    Button signInBtn;
    CheckBox rememberMeCB;
    TextView registerTV, forgotPasswordTV;

    private  FirebaseAuth mAuth;

    ProgressDialog progressDialog;
    InputMethodManager inputManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_in);

        mAuth= FirebaseAuth.getInstance();

        initialiseUI();
    }
    private void initialiseUI(){
        inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait ...");

        signInBtn = findViewById(R.id.sign_in_btn);
        rememberMeCB = findViewById(R.id.remember_me_cb);
        registerTV = findViewById(R.id.register_tv);
        forgotPasswordTV = findViewById(R.id.forgot_password_tv);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //hide keyboard
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                //show progress dialog
                progressDialog.show();

                getEmailAndPassword();
                signInWithEmailPassword(email,password);
            }
        });

        registerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inflateRegistrationForm();
            }
        });

        forgotPasswordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });

        if(checkForPINSignIN())
           rememberMeCB.setChecked(true);
        else
            rememberMeCB.setChecked(false);
    }

    private  boolean checkForPINSignIN(){

        SharedPreferences pref = this.getSharedPreferences("remember_me",0);
        boolean isChecked = pref.getBoolean("is_checked",false);
        return isChecked;
    }

    private void getEmailAndPassword(){
        //get Password
        EditText emailET= findViewById(R.id.email_et),
                passET= findViewById(R.id.password_et);
        email = emailET.getText().toString();
        password = passET.getText().toString();
    }

    private void signInWithEmailPassword(String email, String password){

        if(email=="" || password==""){
            Snackbar.make(findViewById(R.id.sign_in_activity_rel_layout),"Enter Email/Password",Snackbar.LENGTH_LONG)
                    .show();
        }
        else{
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "signInUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                updateUI(user);
                            } else if(!task.isSuccessful()){
                                try{
                                    Log.w("TAG", "signInUserWithEmail:failure", task.getException());
                                    throw task.getException();
                                }catch (FirebaseAuthInvalidCredentialsException e){
                                    Snackbar.make(findViewById(R.id.sign_in_activity_rel_layout),"Invalid Email/Password",Snackbar.LENGTH_LONG)
                                            .show();
                                    progressDialog.dismiss();
                                }catch (FirebaseAuthInvalidUserException e){
                                    Snackbar.make(findViewById(R.id.sign_in_activity_rel_layout),"Email Doesn't Exist. Register Now.",Snackbar.LENGTH_LONG)
                                            .show();
                                    progressDialog.dismiss();
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                                //updateUI(null);
                            }

                            // ...
                        }
                    });
        }

    }

    private void inflateRegistrationForm(){

        AlertDialog.Builder d = new AlertDialog.Builder(this);
        final View customView = View.inflate(this,R.layout.register_form,null);
        final EditText emailEt=customView.findViewById(R.id.reg_email_et),
                nameEt=customView.findViewById(R.id.reg_full_name_et),
                passwordEt=customView.findViewById(R.id.reg_password_et),
                phoneEt = customView.findViewById(R.id.reg_phone_et);
        final RadioGroup radioGroup = customView.findViewById(R.id.reg_gender_rg);

        d.setView(customView)
                .setPositiveButton("Register", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String email = emailEt.getText().toString(),
                        password = passwordEt.getText().toString(),
                        name = nameEt.getText().toString(),
                        phone = phoneEt.getText().toString();
                int selectedRId = radioGroup.getCheckedRadioButtonId();
                RadioButton selectedGender = customView.findViewById(selectedRId);
                String gender = selectedGender.getText().toString();


                registerWithEmailAndPassword(email,password,name);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        d.create().show();
    }

    private void registerWithEmailAndPassword(String regEmail, String regPassword, final String regName){
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(regEmail, regPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user!=null){
                                UserProfileChangeRequest profileUpdate= new UserProfileChangeRequest.Builder()
                                        .setDisplayName(regName)
                                        .build();
                                user.updateProfile(profileUpdate)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                    Log.d("TAG","User profile updated");
                                            }
                                        });

                                progressDialog.dismiss();
                                updateUI(user);
                            }

                            //updateUI(user);
                        } else if(!task.isSuccessful()){
                            try{
                                Log.w("TAG", "crateUserWithEmail:failure", task.getException());
                                throw task.getException();
                            }catch (FirebaseAuthUserCollisionException e){
                                Snackbar.make(findViewById(R.id.sign_in_activity_rel_layout),"Email Already Exists!",Snackbar.LENGTH_LONG)
                                        .show();
                            }catch (FirebaseAuthWeakPasswordException e){
                                Snackbar.make(findViewById(R.id.sign_in_activity_rel_layout),"Weak Password. Use Numbers and Letters!",Snackbar.LENGTH_LONG)
                                        .show();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void resetPassword(){
        AlertDialog.Builder d = new AlertDialog.Builder(this);
        final View customView = View.inflate(this,R.layout.forgot_password_form,null);

        final EditText resetEmailET = customView.findViewById(R.id.reset_email_et);
        d.setView(customView)
                .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String resetEmail = resetEmailET.getText().toString();
                        FirebaseAuth.getInstance().sendPasswordResetEmail(resetEmail)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("TAG", "Email sent.");
                                            Toast.makeText(SignInActivity.this, "Email Sent!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        d.create().show();
    }

    private void updateUI(FirebaseUser user){
        progressDialog.dismiss();
        SharedPreferences pref = this.getSharedPreferences("remember_me",0);
        SharedPreferences.Editor editor = pref.edit();

        //update remember as according to user's choice
        if(rememberMeCB.isChecked()){
            editor.putBoolean("is_checked",true);
            editor.apply();
            editor.commit();
        }else{
            editor.putBoolean("is_checked",false);
            editor.apply();
            editor.commit();
        }

        // user is signed in
        if(user!=null) {
            Toast.makeText(this, " Welocme! " , Toast.LENGTH_SHORT).show();

            //if remember me is checked, direct user to PIN activity
            if(checkForPINSignIN())
                startActivity(new Intent(SignInActivity.this, PinLockActivity.class));

            //take him to main activity
            else
                startActivity(new Intent(SignInActivity.this, Main2Activity.class));
            finish();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser!=null) {
            updateUI(currentUser);

        }

    }


}
