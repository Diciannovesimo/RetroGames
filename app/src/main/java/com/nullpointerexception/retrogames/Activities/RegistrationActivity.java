package com.nullpointerexception.retrogames.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.developer.kalert.KAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.nullpointerexception.retrogames.BackEndInterface;
import com.nullpointerexception.retrogames.Components.AuthenticationManager;
import com.nullpointerexception.retrogames.Components.Blocker;
import com.nullpointerexception.retrogames.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    private EditText mNickname, mEmail, mPassword, mConfirmPassword;
    private Button mRegister;
    private String email, password, confirmPassword, nickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Rende l'activity transparente e a schermo intero
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        initUI();

        mRegister.setOnClickListener(new View.OnClickListener() {
            private Blocker mBlocker = new Blocker();

            @Override
            public void onClick(View view) {
                if (!mBlocker.block()) {
                    if(checkFields())
                        startRegistration();
                }
            }
        });
    }

    private void initUI() {
        mNickname = findViewById(R.id.nicknameTextField);
        mEmail = findViewById(R.id.emailTextField);
        mPassword = findViewById(R.id.passwordTextField);
        mConfirmPassword = findViewById(R.id.confPasswordTextField3);
        mRegister = findViewById(R.id.registration_btn);
    }

    public boolean checkFields() {
        boolean alright = true;
        email = mEmail.getText().toString();

        if(!emailCheck(email)) {
            alright = false;
            mEmail.setError("Email wrong");
        }

        nickName = mNickname.getText().toString();
        if(nickName.length() <= 2) {
            alright = false;
            mNickname.setError("Nickname troppo corto");
        }

        password = mPassword.getText().toString();
        confirmPassword = mConfirmPassword.getText().toString();
        if(password.length() < 8) {
            alright = false;
            mPassword.setError("La password non rispetta i criteri");
        }

        if(!confirmPassword.equals(password)) {
            alright = false;
            mConfirmPassword.setError("La password non corrisponde");
        }

        return alright;
    }

    private boolean emailCheck(String email) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(email);

        if(mat.matches())
            return true;
        else
            return false;
    }

    private void startRegistration() {

        AuthenticationManager.get().createFirebaseUser(email, password, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    AuthenticationManager.get().sendVerificationEmail(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task1) {
                            if(task1.isSuccessful()) {
                                AuthenticationManager.get().getUIdOf(email, password)
                                        .addOnUidListener(new AuthenticationManager.LoginAttempt.OnUIDListener() {
                                            @Override
                                            public void onIdObtained(String uid) {
                                                BackEndInterface.get().writeUser(email, mNickname.getText().toString());
                                                //Show dialog
                                                RegistrationActivity.this.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        new KAlertDialog(RegistrationActivity.this, KAlertDialog.SUCCESS_TYPE)
                                                                .setContentText("Registrazione effettuata con successo")
                                                                .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                                                                    @Override
                                                                    public void onClick(KAlertDialog kAlertDialog) {
                                                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                                                        finish();
                                                                    }
                                                                })
                                                                .show();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onError() {
                                                RegistrationActivity.this.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        new KAlertDialog(RegistrationActivity.this, KAlertDialog.ERROR_TYPE)
                                                                .setContentText("Errore durante la registrazione")
                                                                .show();
                                                    }
                                                });
                                            }
                                        });
                            }
                        }
                    });
                }else{
                    RegistrationActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new KAlertDialog(RegistrationActivity.this, KAlertDialog.ERROR_TYPE)
                                    .setContentText("Errore durante la registrazione")
                                    .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                                        @Override
                                        public void onClick(KAlertDialog kAlertDialog) {
                                            kAlertDialog.dismiss();
                                        }
                                    })
                                    .show();
                        }
                    });
                }
            }
        });
    }
}
