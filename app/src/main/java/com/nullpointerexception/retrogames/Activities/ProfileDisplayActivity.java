package com.nullpointerexception.retrogames.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nullpointerexception.retrogames.Fragments.ProfileFragment;
import com.nullpointerexception.retrogames.R;

public class ProfileDisplayActivity extends AppCompatActivity
{
    /*
            Constants
     */
    public static final String USER_EXTRA = "user";
    public static final String ACTIVITY_TITLE_EXTRA = "activity_title";

    /*
            UI Components
     */
    private TextView activityTitleTextview;
    private View backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_display_activity);

        backButton = findViewById(R.id.back_button);
        activityTitleTextview = findViewById(R.id.title_activity_textview);

        if(getIntent() != null && getIntent().hasExtra(ACTIVITY_TITLE_EXTRA))
            activityTitleTextview.setText( getIntent().getStringExtra(ACTIVITY_TITLE_EXTRA));
        else
            activityTitleTextview.setText( getResources().getString(R.string.profile));
        backButton.setOnClickListener(v -> finish());

        if(getIntent() != null && getIntent().hasExtra(USER_EXTRA))
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentDisplay,
                            new ProfileFragment( getIntent().getStringExtra(USER_EXTRA)))
                    .commit();
        }
        else
        {
            Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
