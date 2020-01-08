package com.nullpointerexception.retrogames.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.nullpointerexception.retrogames.Components.AuthenticationManager;
import com.nullpointerexception.retrogames.Components.BackEndInterface;
import com.nullpointerexception.retrogames.Components.Blocker;
import com.nullpointerexception.retrogames.Components.OnTouchAnimatedListener;
import com.nullpointerexception.retrogames.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    private EditText mNickname, mEmail, mPassword, mConfirmPassword;
    private Button mRegister;
    private String email, password, confirmPassword, nickName;
    private List<String> mNicknames = new ArrayList<>();
    private AlertDialog mDlgMsg;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Rende l'activity transparente e a schermo intero
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        //Inizializza l'interfaccia grafica
        initUI();

        runOnUiThread(() -> BackEndInterface
                .get()
                .readAllNickname((success, nicknames) -> mNicknames.addAll(nicknames)));

        //Listener pulsante registrazione
        mRegister.setOnTouchListener(new OnTouchAnimatedListener()
        {
            Blocker mBlocker = new Blocker();

            @Override
            public void onClick(View view) {
                if (!mBlocker.block()) {
                    if(checkFields())
                        startRegistration();
                }
            }
        });
    }

    /**
     * Inizializza l'interfaccia grafica
     */
    private void initUI() {
        mNickname = findViewById(R.id.nicknameTextField);
        mEmail = findViewById(R.id.emailTextField);
        mPassword = findViewById(R.id.passwordTextField);
        mConfirmPassword = findViewById(R.id.confPasswordTextField3);
        mRegister = findViewById(R.id.registration_btn);
    }

    /**
     * Controlla se i campi sono corretti
     */
    public boolean checkFields() {
        boolean alright = true;
        email = mEmail.getText().toString();
        if(!emailCheck(email)) {
            alright = false;
            mEmail.setError(getResources().getString(R.string.error_wrong_email));
        }

        nickName = mNickname.getText().toString();
        if(nickName.length() <= 3) {
            alright = false;
            mNickname.setError(getResources().getString(R.string.error_short_username));
        } else {
            for(int i = 0; i < mNicknames.size(); i++) {
                if(nickName.equals(mNicknames.get(i))) {
                    alright = false;

                    mDlgMsg = new AlertDialog.Builder(RegistrationActivity.this)
                            .setTitle(getResources().getString(R.string.error_dialog_title))
                            .setMessage(getResources().getString(R.string.error_edittext_username))
                            .setPositiveButton(getResources().getString(R.string.dialog_ok), (dialog, which) -> mDlgMsg.dismiss())
                            .show();
                }
            }
        }

        password = mPassword.getText().toString();
        confirmPassword = mConfirmPassword.getText().toString();
        if(password.length() < 8) {
            alright = false;
            mPassword.setError(getResources().getString(R.string.error_short_username));
        }

        if(!confirmPassword.equals(password)) {
            alright = false;
            mConfirmPassword.setError(getResources().getString(R.string.error_match_password));
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
     * Gestisce i metodi per la registrazione
     */
    private void startRegistration() {
        //Chiama AuthenticationManager per iniziare la registrazione
        AuthenticationManager.get().createFirebaseUser(email, password, task -> {
            //Se il task ha successo...
            if(task.isSuccessful()){
                AuthenticationManager.get().sendVerificationEmail(task1 -> {
                    if(task1.isSuccessful()) {
                        //effettua il login
                        AuthenticationManager.get().getUIdOf(email, password)
                                .addOnUidListener(new AuthenticationManager.LoginAttempt.OnUIDListener() {
                                    @Override
                                    public void onIdObtained(String uid) {  //Quando ottiene l'id...
                                        //Scrive l'username dell'utente sul database
                                        BackEndInterface.get().writeUser(email, mNickname.getText().toString());
                                        //Show dialog
                                        RegistrationActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mDlgMsg = new AlertDialog.Builder(RegistrationActivity.this)
                                                        .setMessage(getResources().getString(R.string.dialog_registration_succes))
                                                        .setPositiveButton(getResources().getString(R.string.dialog_ok), (dialog, which) -> mDlgMsg.dismiss())
                                                        .show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError() {
                                        RegistrationActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                mDlgMsg = new AlertDialog.Builder(RegistrationActivity.this)
                                                        .setTitle(getResources().getString(R.string.error_dialog_title))
                                                        .setMessage(getResources().getString(R.string.dialog_registrazion_error))
                                                        .setPositiveButton(getResources().getString(R.string.dialog_ok), (dialog, which) -> mDlgMsg.dismiss())
                                                        .show();
                                            }
                                        });
                                    }
                                });
                    }
                });
            }else{
                RegistrationActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDlgMsg = new AlertDialog.Builder(RegistrationActivity.this)
                                .setTitle(getResources().getString(R.string.error_dialog_title))
                                .setMessage(getResources().getString(R.string.dialog_registrazion_error))
                                .setPositiveButton(getResources().getString(R.string.dialog_ok), (dialog, which) -> mDlgMsg.dismiss())
                                .show();
                    }
                });
            }
        });
    }
}
