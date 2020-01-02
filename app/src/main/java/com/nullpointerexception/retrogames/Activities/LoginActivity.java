package com.nullpointerexception.retrogames.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.nullpointerexception.retrogames.Components.Blocker;
import com.nullpointerexception.retrogames.R;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail, mPassword;
    private Button mLogin;
    private TextView mSignin;
    private View googleSignInButton;

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
        mLogin.setOnClickListener(new View.OnClickListener() {
            private Blocker mBlocker = new Blocker();

            @Override
            public void onClick(View view) {
                if (!mBlocker.block()) {

                }
            }
        });

        //Listener registrazione
        mSignin.setOnClickListener(new View.OnClickListener() {
            private Blocker mBlocker = new Blocker();

            @Override
            public void onClick(View view) {
                if (!mBlocker.block()) {

                }
            }
        });
    }

    private void initUI() {
        mEmail = findViewById(R.id.emailTextField);
        mPassword = findViewById(R.id.passwordTextField);
        mLogin = findViewById(R.id.login_btn);
        mSignin = findViewById(R.id.signIn_tv);
        googleSignInButton = findViewById(R.id.googleSignInButton);
    }
}
