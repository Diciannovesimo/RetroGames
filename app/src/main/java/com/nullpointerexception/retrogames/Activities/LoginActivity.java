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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.developer.kalert.KAlertDialog;
import com.nullpointerexception.retrogames.App;
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
                                                                preferences.edit().putString(App.NICKNAME, currentUser.getNickname()).apply();
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
                    } else {
                        //Crea un vettore con il risultato dei controlli sui campi e lo riempe
                        //con il return di wrongFields
                        int[] wrongFields = wrongFields();
                        if(wrongFields[0] == 1 && wrongFields[1] == 1) { //se wrongFields[0]/[1] è 1 allora la mail e la password è errata
                            mEmail.setError("La mail è sbagliata");
                            mPassword.setError("La password è sbagliata");
                        } else if(wrongFields[0] == 1) {                 //se wrongFields[0] è 1 allora la mail è errata
                            mEmail.setError("La mail è sbagliata");
                        } else if(wrongFields[1] == 1) {                 //se wrongFields[1] è 1 allora la password è errata
                            mPassword.setError("La password è sbagliata");
                        }
                    }
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
                                    SharedPreferences preferences = getSharedPreferences(App.USER, MODE_PRIVATE);
                                    preferences.edit().putString(App.NICKNAME, currentUser.getNickname()).apply();
                                    BackEndInterface.get().writeUser(currentUser.getEmail(), currentUser.getNickname());
                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                    finish();
                                } else {
                                    Log.i("claudio", "errore nel log in");
                                    new KAlertDialog(LoginActivity.this, KAlertDialog.ERROR_TYPE)
                                            .setTitleText("Errore")
                                            .setContentText("Accesso con Google non riuscito")
                                            .show();
                                }
                            });
                }
            }
        });
    }

    private boolean checkFields() {
        boolean alright = true;

        String email = mEmail.getText().toString();
        if (!emailCheck(email.trim()) || email.isEmpty())
            alright = false;

        String psw = mPassword.getText().toString();
        if (psw.isEmpty() || psw.length() < 8)
            alright = false;

        return alright;
    }

    private int[] wrongFields() {
        int[] wrongField = new int[2];

        String email = mEmail.getText().toString();
        if (!emailCheck(email) || email.isEmpty())
            wrongField[0] = 1;

        String psw = mPassword.getText().toString();
        if (psw.isEmpty() || psw.length() < 8)
            wrongField[1] = 1;

        return wrongField;
    }

    private boolean emailCheck(String email) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(email);

        return mat.matches();
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
