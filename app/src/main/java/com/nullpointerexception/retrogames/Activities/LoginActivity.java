package com.nullpointerexception.retrogames.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.developer.kalert.KAlertDialog;
import com.nullpointerexception.retrogames.Components.App;
import com.nullpointerexception.retrogames.Components.AuthenticationManager;
import com.nullpointerexception.retrogames.Components.BackEndInterface;
import com.nullpointerexception.retrogames.Components.Blocker;
import com.nullpointerexception.retrogames.Components.OnTouchAnimatedListener;
import com.nullpointerexception.retrogames.Components.User;
import com.nullpointerexception.retrogames.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail, mPassword;
    private Button mLogin;
    private TextView mSignin;
    private View googleSignInButton;

    private User currentUser;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Inizializzo l'UI
        initUI();

        //Rende l'activity transparente e a schermo intero
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        //Listener login
        mLogin.setOnTouchListener(new OnTouchAnimatedListener() {
            private Blocker mBlocker = new Blocker();

            @Override
            public void onClick(View view) {
                if (!mBlocker.block()) {
                    if (checkFields()) {
                        AuthenticationManager.get().login(mEmail.getText().toString()
                                , mPassword.getText().toString())
                                .addOnLoginResultListener(result -> {

                                    if (result) {
                                        Log.i("claudio", "Loggato con successo");
                                        currentUser = AuthenticationManager.get().getUserLogged();
                                        BackEndInterface
                                                .get()
                                                .readUser(currentUser.getEmail(), new BackEndInterface.OnDataReceivedListener() {
                                                    @Override
                                                    public void onDataReceived(boolean success, String value) {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                //TODO: Gestire salvataggio sharedPreferences
                                                                currentUser.setNickname(value);
                                                                SharedPreferences preferences = getSharedPreferences(App.USER, MODE_PRIVATE);
                                                                preferences.edit().putString(currentUser.getNickname(),"lol").apply();
                                                            }
                                                        });
                                                    }
                                                });

                                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                        finish();
                                    } else {
                                        Log.i("claudio", "errore nel log in");
                                        new KAlertDialog(LoginActivity.this, KAlertDialog.ERROR_TYPE)
                                                .setTitleText("Errore")
                                                .setContentText("Nome utente o password errati")
                                                .show();
                                    }
                                });
                    } else
                        Toast.makeText(getApplicationContext(), "Campi errati", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Listener registrazione
        mSignin.setOnTouchListener(new OnTouchAnimatedListener() {
            private Blocker mBlocker = new Blocker();

            @Override
            public void onClick(View view) {
                if (!mBlocker.block()) {
                    startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
                }
            }
        });

        //Listener log in con Google
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            private Blocker mBlocker = new Blocker();

            @Override
            public void onClick(View view) {
                if (!mBlocker.block()) {
                    AuthenticationManager.get().requestLoginWithGoogle(LoginActivity.this)
                            .addOnLoginResultListener(result -> {
                                if (result) {
                                    Log.i("claudio", "Loggato con successo");
                                    currentUser = AuthenticationManager.get().getUserLogged();
                                    BackEndInterface.get().writeUser(currentUser.getEmail(), currentUser.getNickname());
                                } else
                                    Log.i("claudio", "errore nel log in");
                                    new KAlertDialog(LoginActivity.this, KAlertDialog.ERROR_TYPE)
                                            .setTitleText("Errore")
                                            .setContentText("Accesso con Google non riuscito")
                                            .show();
                            });
                }
            }
        });
    }

    private boolean checkFields() {
        boolean allRight = true;

        String email = mEmail.getText().toString();
        if (!emailCheck(email) || email.isEmpty())
            allRight = false;

        String psw = mPassword.getText().toString();
        if (psw.isEmpty() || psw.length() > 8)
            allRight = false;

        return allRight;
    }

    private boolean emailCheck(String email) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(email);

        if (mat.matches())
            return true;
        else
            return false;
    }

    private void initUI() {
        mEmail             = findViewById(R.id.nicknameTextField);
        mPassword          = findViewById(R.id.emailTextField);
        mLogin             = findViewById(R.id.registration_btn);
        mSignin            = findViewById(R.id.signIn_tv);
        googleSignInButton = findViewById(R.id.googleSignInButton);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AuthenticationManager.get().loginWithGoogle(requestCode, data);
    }
}
