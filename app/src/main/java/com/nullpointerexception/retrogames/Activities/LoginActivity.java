package com.nullpointerexception.retrogames.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nullpointerexception.retrogames.App;
import com.nullpointerexception.retrogames.Components.AuthenticationManager;
import com.nullpointerexception.retrogames.Components.BackEndInterface;
import com.nullpointerexception.retrogames.Components.Blocker;
import com.nullpointerexception.retrogames.Components.OnTouchAnimatedListener;
import com.nullpointerexception.retrogames.Components.User;
import com.nullpointerexception.retrogames.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity
{

    private EditText mEmail, mPassword;
    private Button mLogin;
    private TextView mSignin;
    private View googleSignInButton;
    private AlertDialog mDlgMsg;

    private User currentUser;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Inizializzo l'UI
        initUI();

        //Listener login
        mLogin.setOnTouchListener(new OnTouchAnimatedListener()
        {
            private Blocker mBlocker = new Blocker();

            @Override
            public void onClick(View view)
            {
                if ( ! mBlocker.block())
                {
                    if (checkFields())
                    {
                        /*
                                Crea il layout con la progressbar e la mostra
                         */
                        FrameLayout frameLayout = new FrameLayout(LoginActivity.this);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        params.gravity = Gravity.CENTER;
                        frameLayout.setLayoutParams(params);
                        frameLayout.setFocusable(true);
                        frameLayout.setClickable(true);
                        frameLayout.setBackgroundColor(Color.parseColor("#80000000"));
                        ProgressBar progressBar = new ProgressBar(LoginActivity.this);
                        FrameLayout.LayoutParams progParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        progParams.gravity = Gravity.CENTER;
                        progressBar.setLayoutParams(progParams);
                        frameLayout.addView(progressBar);
                        ((ViewGroup) mLogin.getRootView()).addView(frameLayout);

                        //Chiama l'AuthenticationManager per gestire il login con firebase
                        AuthenticationManager.get().login(mEmail.getText().toString(), mPassword.getText().toString())
                                .addOnLoginResultListener(result ->
                                {
                                    //  Rimuove la progressbar
                                    ((ViewGroup) mLogin.getRootView()).removeView(frameLayout);

                                    //Se il result è vero allora l'utente ha loggato con successo
                                    if (result)
                                    {
                                        Log.i("claudio", "Loggato con successo");

                                        //currenUser si riempie con i dati dell'utente loggato
                                        currentUser = AuthenticationManager.get().getUserLogged();

                                        //Inserisce l'email nell'utente in quanto non salvata su firebase
                                        BackEndInterface
                                                .get()
                                                .readUser(currentUser.getEmail(), new BackEndInterface.OnDataReceivedListener() {
                                                    @Override
                                                    public void onDataReceived(boolean success, String value)
                                                    {
                                                        runOnUiThread(() ->
                                                        {
                                                            currentUser.setNickname(value);

                                                            //Salva il nickname nelle SharedPreferences
                                                            SharedPreferences preferences = getSharedPreferences(App.USER, MODE_PRIVATE);
                                                            preferences.edit().putString(App.NICKNAME, currentUser.getNickname()).apply();

                                                            loginProcedures();
                                                        });
                                                    }
                                                });
                                    }
                                    else
                                    {   //Il login non ha avuto successo in quanto l'utente ha sbagliato le credenziali
                                        Log.i("claudio", "errore nel log in");
                                        mDlgMsg = new AlertDialog.Builder(LoginActivity.this)
                                                .setTitle(getResources().getString(R.string.error_dialog_title))
                                                .setMessage(getResources().getString(R.string.error_dialog_content))
                                                .setPositiveButton(getResources().getString(R.string.again), (dialog, which) -> mDlgMsg.dismiss())
                                                .show();
                                    }
                                });
                    }
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

                                    loginProcedures();

                                } else { //Se l'accesso con Google non è andato a buon fine
                                    Log.i("claudio", "errore nel log in");
                                    mDlgMsg = new AlertDialog.Builder(LoginActivity.this)
                                            .setTitle(getResources().getString(R.string.error_dialog_title))
                                            .setMessage(getResources().getString(R.string.error_google_dialog_content))
                                            .setPositiveButton(getResources().getString(R.string.again), (dialog, which) -> mDlgMsg.dismiss())
                                            .show();
                                }
                            });
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
    private boolean emailCheck(String email)
    {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(email);

        return mat.matches();
    }

    /**
     *      Inizializza l'interfaccia grafica
     */
    private void initUI()
    {
        mEmail             = findViewById(R.id.nicknameTextField);
        mPassword          = findViewById(R.id.emailTextField);
        mLogin             = findViewById(R.id.registration_btn);
        mSignin            = findViewById(R.id.signIn_tv);
        googleSignInButton = findViewById(R.id.googleSignInButton);
    }

    private void loginProcedures()
    {
        //Fa partire l'Homeactivity e disalloca LoginActivity
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.putExtra("newLogin", true);
        startActivity(intent);
        finish();
    }

    /**
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
