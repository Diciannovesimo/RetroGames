package com.nullpointerexception.retrogames.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.nullpointerexception.retrogames.Components.OnTouchAnimatedListener;
import com.nullpointerexception.retrogames.Fragments.GamesFragment;
import com.nullpointerexception.retrogames.Fragments.LeaderboardFragment;
import com.nullpointerexception.retrogames.Fragments.ProfileFragment;
import com.nullpointerexception.retrogames.R;

public class HomeActivity extends AppCompatActivity
{

    private static final int DEFAULT_ICON_COLORS = Color.parseColor("#707070");

    /*
            UI Components
     */
    private Fragment currentFragment;
    private ViewGroup gamesButton, leaderboardButton, profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        gamesButton = findViewById(R.id.buttonGames);
        leaderboardButton = findViewById(R.id.buttonLeaderBoard);
        profileButton = findViewById(R.id.buttonProfile);

        gamesButton.setOnTouchListener(new OnTouchAnimatedListener()
        {
            @Override
            public void onClick(View view)
            {
                placeFragment(new GamesFragment());
            }
        });

        leaderboardButton.setOnTouchListener(new OnTouchAnimatedListener()
        {
            @Override
            public void onClick(View view)
            {
                placeFragment(new LeaderboardFragment());
            }
        });

        profileButton.setOnTouchListener(new OnTouchAnimatedListener()
        {
            @Override
            public void onClick(View view)
            {
                //  TODO Controllare se si è loggati, altrimenti piazzare quella di login
                //placeFragment(new LoginFragment());
                placeFragment(new ProfileFragment());
            }
        });

        placeFragment(new GamesFragment());
    }

    private void placeFragment(Fragment newFragment)
    {
        if(currentFragment != null && newFragment.getClass().getSimpleName().equals(
                currentFragment.getClass().getSimpleName() ))
            return;

        if(currentFragment instanceof GamesFragment)
            resetSectionViewColor(gamesButton);
        else if(currentFragment instanceof LeaderboardFragment)
            resetSectionViewColor(leaderboardButton);
        else if(currentFragment instanceof ProfileFragment)
            resetSectionViewColor(profileButton);

        if(newFragment instanceof GamesFragment)
            setCurrentSectionView(gamesButton);
        else if(newFragment instanceof LeaderboardFragment)
            setCurrentSectionView(leaderboardButton);
        else if(newFragment instanceof ProfileFragment)
            setCurrentSectionView(profileButton);

        currentFragment = newFragment;

        runOnUiThread(() ->
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fragment_in, R.anim.fragment_out)
                        .replace(R.id.fragmentDisplay, currentFragment)
                        .commit());
    }

    private void setCurrentSectionView(ViewGroup buttonView)
    {
        for(int i = 0; i < buttonView.getChildCount(); i++)
        {
            View currentView = buttonView.getChildAt(i);

            if(currentView instanceof TextView)
                ((TextView) currentView).setTextColor( getResources().getColor(R.color.colorPrimaryDark));
            else if(currentView instanceof ImageView)
                ((ImageView) currentView).getDrawable().setTint( getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    private void resetSectionViewColor(ViewGroup buttonView)
    {
        for(int i = 0; i < buttonView.getChildCount(); i++)
        {
            View currentView = buttonView.getChildAt(i);

            if(currentView instanceof TextView)
                ((TextView) currentView).setTextColor(DEFAULT_ICON_COLORS);
            else if(currentView instanceof ImageView)
                ((ImageView) currentView).getDrawable().setTint(DEFAULT_ICON_COLORS);
        }
    }
}
