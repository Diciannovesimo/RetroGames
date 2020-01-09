package com.nullpointerexception.retrogames.Activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.nullpointerexception.retrogames.App;
import com.nullpointerexception.retrogames.Components.AuthenticationManager;
import com.nullpointerexception.retrogames.Components.BackEndInterface;
import com.nullpointerexception.retrogames.Components.OnTouchAnimatedListener;
import com.nullpointerexception.retrogames.Fragments.GamesFragment;
import com.nullpointerexception.retrogames.Fragments.LeaderboardFragment;
import com.nullpointerexception.retrogames.Fragments.LoginFragment;
import com.nullpointerexception.retrogames.Fragments.ProfileFragment;
import com.nullpointerexception.retrogames.R;

public class HomeActivity extends AppCompatActivity
{
    /*
            Constants
     */
    private static final int DEFAULT_ICON_COLORS = Color.parseColor("#707070");
    private static final int GAMES_FRAGMENT = 0;
    private static final int LEADERBOARD_FRAGMENT = 1;
    private static final int PROFILE_FRAGMENT = 2;
    private static final int ACCESS_FRAGMENT = 3;

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

        sendFirebase();

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
                if(AuthenticationManager.get().isUserLogged())
                    placeFragment(new ProfileFragment());
                else
                    placeFragment(new LoginFragment(false));
            }
        });

        if(savedInstanceState != null)
        {
            int fragment = savedInstanceState.getInt("fragmentPlaced");
            switch (fragment)
            {
                case GAMES_FRAGMENT:
                default:
                    placeFragment(new GamesFragment());
                    break;
                case LEADERBOARD_FRAGMENT:
                    placeFragment(new LeaderboardFragment());
                    break;
                case PROFILE_FRAGMENT:
                    placeFragment(new ProfileFragment());
                    break;
                case ACCESS_FRAGMENT:
                    placeFragment(new LoginFragment());
                    break;
            }
        }
        else
            placeFragment(new GamesFragment());
    }

    private void sendFirebase() {
        //Prendo il nickname dell'utente loggato
        SharedPreferences nicknameShared = getSharedPreferences(App.USER, 0);
        String nickname = nicknameShared.getString(App.NICKNAME, "error");
        long score = 0;
        long totalscore = App.scoreboardDao.getScore(App.TOTALSCORE);

        //Scrivo il punteggio di tetris
        score = App.scoreboardDao.getScore(App.TETRIS);
        if( score != 0)
            BackEndInterface.get().writeScoreFirebase(App.TETRIS, nickname,score,totalscore);

        //Scrivo il punteggio di snake
        score = App.scoreboardDao.getScore(App.SNAKE);
        if( score != 0)
            BackEndInterface.get().writeScoreFirebase(App.SNAKE, nickname,score,totalscore);

        //Scrivo il punteggio di hole
        score = App.scoreboardDao.getScore(App.HOLE);
        if( score != 0)
            BackEndInterface.get().writeScoreFirebase(App.HOLE, nickname,score,totalscore);

        //Scrivo il punteggio di breakout
        score = App.scoreboardDao.getScore(App.BREAKOUT);
        if( score != 0)
            BackEndInterface.get().writeScoreFirebase(App.BREAKOUT, nickname,score,totalscore);

        //Scrivo il punteggio di pong
        score = App.scoreboardDao.getScore(App.PONG);
        if( score != 0)
            BackEndInterface.get().writeScoreFirebase(App.PONG, nickname,score,totalscore);
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
        else if(currentFragment instanceof ProfileFragment ||
                currentFragment instanceof LoginFragment)
            resetSectionViewColor(profileButton);

        if(newFragment instanceof GamesFragment)
            setCurrentSectionView(gamesButton);
        else if(newFragment instanceof LeaderboardFragment)
            setCurrentSectionView(leaderboardButton);
        else if(newFragment instanceof ProfileFragment ||
                newFragment instanceof LoginFragment)
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
                ((ImageView) currentView).getDrawable().mutate().setTint( getResources().getColor(R.color.colorPrimaryDark));
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState)
    {
        int value = GAMES_FRAGMENT;

        if(currentFragment instanceof GamesFragment)
            value = GAMES_FRAGMENT;
        else if(currentFragment instanceof LeaderboardFragment)
            value = LEADERBOARD_FRAGMENT;
        else if(currentFragment instanceof ProfileFragment)
            value = PROFILE_FRAGMENT;
        else if(currentFragment instanceof LoginFragment)
            value = ACCESS_FRAGMENT;

        outState.putInt("fragmentPlaced", value);

        super.onSaveInstanceState(outState);
    }
}
