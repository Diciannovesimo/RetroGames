package com.nullpointerexception.retrogames.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
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
import com.nullpointerexception.retrogames.App;
import com.nullpointerexception.retrogames.Components.AuthenticationManager;
import com.nullpointerexception.retrogames.Components.BackEndInterface;
import com.nullpointerexception.retrogames.Components.Blocker;
import com.nullpointerexception.retrogames.Components.OnTouchAnimatedListener;
import com.nullpointerexception.retrogames.Components.Scoreboard;
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
                        //Chiama l'AuthenticationManager per gestire il login con firebase
                        AuthenticationManager.get().login(mEmail.getText().toString()
                                , mPassword.getText().toString())
                                .addOnLoginResultListener(result -> {
                                    //Se il result è vero allora l'utente ha loggato con successo
                                    if (result) {
                                        Log.i("claudio", "Loggato con successo");
                                        //currenUser si riempie con i dati dell'utente loggato
                                        currentUser = AuthenticationManager.get().getUserLogged();
                                        //Inserisce l'email nell'utente in quanto non salvata su firebase
                                        BackEndInterface
                                                .get()
                                                .readUser(currentUser.getEmail(), new BackEndInterface.OnDataReceivedListener() {
                                                    @Override
                                                    public void onDataReceived(boolean success, String value) {
                                                        runOnUiThread(() -> {
                                                            currentUser.setNickname(value);
                                                            //Salva il nickname nelle SharedPreferences
                                                            SharedPreferences preferences = getSharedPreferences(App.USER, MODE_PRIVATE);
                                                            preferences.edit().putString(App.NICKNAME, currentUser.getNickname()).apply();
                                                        });
                                                    }
                                                });
                                        //Salva il nickname nelle SharedPreferences
                                        SharedPreferences preferences = getSharedPreferences(App.USER, MODE_PRIVATE);
                                        preferences.edit().putString(App.NICKNAME, currentUser.getNickname()).apply();
                                        Log.d("nickname", preferences.getString(App.NICKNAME,"nulla"));
                                        //Restore dei punteggi sul server firebase
                                        new restoreScoreboard().execute();
                                        //Dopo il login parte l'Home activity e LoginActivity viene disallocata
                                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                        finish();
                                    } else {   //Il login non ha avuto successo in quanto l'utente ha sbagliato le credenziali
                                        Log.i("claudio", "errore nel log in");
                                        new KAlertDialog(LoginActivity.this, KAlertDialog.ERROR_TYPE)
                                                .setTitleText(getResources().getString(R.string.error_dialog_title))
                                                .setContentText(getResources().getString(R.string.error_dialog_content))
                                                .show();
                                    }
                                });
                    }
                }
            }
        });

        //Listener pulsante registrazione
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
                    //Fa partire il dialog per la scelta della mail di Google
                    AuthenticationManager.get().requestLoginWithGoogle(LoginActivity.this)
                            .addOnLoginResultListener(result -> {
                                if (result) { //Luetente si è loggato con successo
                                    Log.i("claudio", "Loggato con successo");
                                    //currentUser si riempie con le informazioni
                                    currentUser = AuthenticationManager.get().getUserLogged();
                                    //Salva il nickname nelle SharedPreferences
                                    SharedPreferences preferences = getSharedPreferences(App.USER, MODE_PRIVATE);
                                    preferences.edit().putString(App.NICKNAME, currentUser.getNickname()).apply();
                                    //Salva email e nickname nel database
                                    BackEndInterface.get().writeUser(currentUser.getEmail(), currentUser.getNickname());
                                    //Restore dei punteggi sul server firebase
                                    new restoreScoreboard().execute();
                                    //Fa partire l'Homeactivity e disalloca LoginActivity
                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                    finish();
                                } else { //Se l'accesso con Google non è andato a buon fine
                                    Log.i("claudio", "errore nel log in");
                                    new KAlertDialog(LoginActivity.this, KAlertDialog.ERROR_TYPE)
                                            .setTitleText(getResources().getString(R.string.error_dialog_title))
                                            .setContentText(getResources().getString(R.string.error_google_dialog_content))
                                            .show();
                                }
                            });
                }
            }
        });
    }


    /**
     * Controlla se qualche campo non rispetta i criteri
     *
     * @return vero se tutti i campi sono esatti, falso se esiste un errore
     */
    private boolean checkFields() {
        boolean alright = true;

        String email = mEmail.getText().toString();
        if (!emailCheck(email.trim()) || email.isEmpty()) {
            alright = false;
            mEmail.setError(getResources().getString(R.string.error_edittext_mail));
        }

        String psw = mPassword.getText().toString();
        if (psw.isEmpty() || psw.length() < 8) {
            alright = false;
            mPassword.setError(getResources().getString(R.string.error_edittext_password));
        }

        return alright;
    }

    /**
     * Verifa se l'email è corretta
     * @param email Riceve una String email come parametro
     * @return Un boolean per capire se la mail è formattata correttamente
     */
    private boolean emailCheck(String email) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(email);

        return mat.matches();
    }

    /**
     * Inizializza l'interfaccia grafica
     */
    private void initUI() {
        mEmail             = findViewById(R.id.nicknameTextField);
        mPassword          = findViewById(R.id.emailTextField);
        mLogin             = findViewById(R.id.registration_btn);
        mSignin            = findViewById(R.id.signIn_tv);
        googleSignInButton = findViewById(R.id.googleSignInButton);
    }

    /**
     * Recupera gli eventuali punteggi salvati sul Database di Firebase
     * e riempie il database locale
     */
    private class restoreScoreboard extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            SharedPreferences nicknameShared = getSharedPreferences(App.USER, MODE_PRIVATE);
            String nickname = nicknameShared.getString(App.NICKNAME, "-");
            Log.d("nicknameAsynkTask", nicknameShared.getString(App.NICKNAME,"nulla"));

            //Recupero l'eventuale Totalscore
            BackEndInterface.get().readScoreFirebase(App.TOTALSCORE, nickname, new BackEndInterface.OnDataReceivedListener() {
                @Override
                public void onDataReceived(boolean success, String value) {
                    if(success) {
                        long score = Long.parseLong(value);
                        App.scoreboardDao.insertAll(new Scoreboard(App.TOTALSCORE, score));
                    }
                }
            });


            //Recupero l'eventuale score di Snake
            BackEndInterface.get().readScoreFirebase(App.SNAKE, nickname, new BackEndInterface.OnDataReceivedListener() {
                @Override
                public void onDataReceived(boolean success, String value) {
                    if(success){
                        long score = Long.parseLong(value);
                        App.scoreboardDao.insertAll(new Scoreboard(App.SNAKE, score));
                    }
                }
            });


            //Recupero l'eventuale score di Tetris
            BackEndInterface.get().readScoreFirebase(App.TETRIS, nickname, new BackEndInterface.OnDataReceivedListener() {
                @Override
                public void onDataReceived(boolean success, String value) {
                    if(success) {
                        long score = Long.parseLong(value);
                        App.scoreboardDao.insertAll(new Scoreboard(App.TETRIS, score));
                    }
                }
            });

            //Recupero l'eventuale score di Breakout
            BackEndInterface.get().readScoreFirebase(App.BREAKOUT, nickname, new BackEndInterface.OnDataReceivedListener() {
                @Override
                public void onDataReceived(boolean success, String value) {
                    if(success) {
                        long score = Long.parseLong(value);
                        App.scoreboardDao.insertAll(new Scoreboard(App.BREAKOUT, score));
                    }
                }
            });

            //Recupero l'eventuale score di Hole
            BackEndInterface.get().readScoreFirebase(App.HOLE, nickname, new BackEndInterface.OnDataReceivedListener() {
                @Override
                public void onDataReceived(boolean success, String value) {
                    if(success){
                        long score = Long.parseLong(value);
                        App.scoreboardDao.insertAll(new Scoreboard(App.HOLE, score));
                    }
                }
            });

            //Recupero l'eventuale score di Pong
            BackEndInterface.get().readScoreFirebase(App.PONG, nickname, new BackEndInterface.OnDataReceivedListener() {
                @Override
                public void onDataReceived(boolean success, String value) {
                    if(success){
                        long score = Long.parseLong(value);
                        App.scoreboardDao.insertAll(new Scoreboard(App.PONG, score));
                    }
                }
            });
            return null;
        }

        protected void onProgressUpdate(Void... voids) {
        }

        protected void onPostExecute() {
            Toast.makeText(getApplicationContext(), "Informazioni ripristinate con successo", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     *
     * @param requestCode Il codice di richiesta dello startActivityForResult
     * @param resultCode  Il codice ottenuto come risultato della richiesta
     * @param data        I dati scambiati tra le activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Fa partire l/accesso con Google
        AuthenticationManager.get().loginWithGoogle(requestCode, data);
    }
}
