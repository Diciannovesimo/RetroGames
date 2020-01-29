package com.nullpointerexception.retrogames.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.nullpointerexception.retrogames.App;
import com.nullpointerexception.retrogames.Components.AuthenticationManager;
import com.nullpointerexception.retrogames.Components.BackEndInterface;
import com.nullpointerexception.retrogames.Components.OnTouchAnimatedListener;
import com.nullpointerexception.retrogames.Components.Scoreboard;
import com.nullpointerexception.retrogames.Fragments.GamesFragment;
import com.nullpointerexception.retrogames.Fragments.LeaderboardFragment;
import com.nullpointerexception.retrogames.Fragments.LoginFragment;
import com.nullpointerexception.retrogames.Fragments.ProfileFragment;
import com.nullpointerexception.retrogames.R;

import java.util.ArrayList;
import java.util.List;

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
            Variables
     */
    private boolean newLogin = false;
    private boolean isUserLogged = false;

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

        isUserLogged = AuthenticationManager.get().isUserLogged();

        if(getIntent() != null && getIntent().hasExtra("newLogin"))
            newLogin = true;

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
                if(isUserLogged)
                    placeFragment(new ProfileFragment());
                else
                    placeFragment(new LoginFragment(false));
            }
        });

        //  Ripiazza il fragment precedente salvato
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
                    placeFragment(new LoginFragment(isUserLogged));
                    break;
            }
        }
        else
        {
            if(newLogin)
                restoreScore(() -> placeFragment(new GamesFragment()));
            else
                placeFragment(new GamesFragment());
        }

    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    /**
     *      Piazza il fragment della sezione da mostrare
     *      @param newFragment Fragment della sezione da mostrare
     */
    private void placeFragment(Fragment newFragment)
    {
        //  Controlla che non sia il fragment della sezione attualmente mostrata
        if(currentFragment != null && newFragment.getClass().getSimpleName().equals(
                currentFragment.getClass().getSimpleName() ))
            return;

        //  Controlla di quale tasto della navigationView reipostare il colore di default
        if(currentFragment instanceof GamesFragment)
            resetSectionViewColor(gamesButton);
        else if(currentFragment instanceof LeaderboardFragment)
            resetSectionViewColor(leaderboardButton);
        else if(currentFragment instanceof ProfileFragment ||
                currentFragment instanceof LoginFragment)
            resetSectionViewColor(profileButton);

        //  Controlla di quale tasto della navigationView impostare il colore che indica l'attuale sezione
        if(newFragment instanceof GamesFragment)
            setCurrentSectionView(gamesButton);
        else if(newFragment instanceof LeaderboardFragment)
            setCurrentSectionView(leaderboardButton);
        else if(newFragment instanceof ProfileFragment ||
                newFragment instanceof LoginFragment)
            setCurrentSectionView(profileButton);

        currentFragment = newFragment;

        //  Piazza il fragment
        runOnUiThread(() ->
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fragment_in, R.anim.fragment_out)
                        .replace(R.id.fragmentDisplay, currentFragment)
                        .commit());
    }

    /**
            Colora il pulsante nel colore che indica la sezione corrente
     */
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

    /**
     *      Reimposta il colore di default al pulsante della navigationView
     *      @param buttonView Bottone da reimpostare.
     */
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
        /*
              Salva il fragment della sezione corrente, per ripristinarlo in seguito
         */

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }

    boolean[] dataReceived;

    /**
     * Sincronizza il database locale con quello di firebase
     * @param nickname nome dell'utente loggato
     */
    private void syncDatabase(String nickname, Runnable runnable)
    {
        List<String> games = new ArrayList<>();
        games.add(App.SNAKE);
        games.add(App.TETRIS);
        games.add(App.PONG);
        games.add(App.HOLE);
        games.add(App.BREAKOUT);

        dataReceived = new boolean[5];

        for(int i = 0; i < dataReceived.length; i++)
            dataReceived[i] = false;

        for(int i = 0; i < games.size(); i++)
        {
            //Recupero l'eventuale score
            long localscore = 0;
            if(App.scoreboardDao.getGame(games.get(i)) != null) //Recupero l'eventuale valore dal database locale
                localscore = App.scoreboardDao.getScore(games.get(i));

            long finalLocalScore = localscore;
            String stringGame = games.get(i);

            int finalI = i;
            BackEndInterface.get().readScoreFirebase(stringGame, nickname, (success, value) ->
            {
                if(success)
                {
                    //Il valore sta su Firebase
                    long scoreFirebase = Long.parseLong(value);

                    if(finalLocalScore < scoreFirebase)
                    {
                        //Scrivo sul database locale il punteggio di firebase
                        if(App.scoreboardDao.getGame(stringGame) == null)
                            App.scoreboardDao.insertAll(new Scoreboard(stringGame, scoreFirebase));
                        else
                            App.scoreboardDao.update(new Scoreboard(stringGame, scoreFirebase));
                    }
                }
                else    //Il valore non sta su firebase
                {
                    if(finalLocalScore != 0)
                    {
                        //Scrivo sul database di firebase il punteggio del database locale
                        BackEndInterface.get().writeScoreFirebase(stringGame, nickname, finalLocalScore);
                    }
                }

                dataReceived[finalI] = true;
                boolean proceed = true;
                for(boolean b : dataReceived)
                    proceed = proceed && b;

                if(proceed)
                    runnable.run();
            });
        }
    }

    /**
     * Risincronizza i punteggi tra quelli presenti nel database locale e quelli presenti su firebase
     * Per sincronizzare i punteggi scrivi su entrambi i database il punteggio maggiore che recupera
     * tra il database locale e quello di Firebase
     * e riempie il database locale
     */
    private void restoreScore(Runnable runnable)
    {
        SharedPreferences nicknameShared = getSharedPreferences(App.USER, MODE_PRIVATE);
        String nickname = nicknameShared.getString(App.NICKNAME, "-");

        syncDatabase(nickname, () ->
        {
            //Genero il nuovo totalscore
            long newTotalscore = 0;

            if(App.scoreboardDao.getGame(App.TETRIS) != null)
            {
                //Recupero l'eventuale valore dal database locale
                newTotalscore = newTotalscore + App.scoreboardDao.getScore(App.TETRIS);
            }

            if(App.scoreboardDao.getGame(App.SNAKE) != null)
            {
                //Recupero l'eventuale valore dal database locale
                newTotalscore = newTotalscore + App.scoreboardDao.getScore(App.SNAKE);
            }

            if(App.scoreboardDao.getGame(App.HOLE) != null)
            {
                //Recupero l'eventuale valore dal database locale
                newTotalscore = newTotalscore + App.scoreboardDao.getScore(App.HOLE);
            }

            if(App.scoreboardDao.getGame(App.BREAKOUT) != null)
            {
                //Recupero l'eventuale valore dal database locale
                newTotalscore = newTotalscore + App.scoreboardDao.getScore(App.BREAKOUT);
            }

            if(App.scoreboardDao.getGame(App.PONG) != null)
            {
                //Recupero l'eventuale valore dal database locale
                newTotalscore = newTotalscore + App.scoreboardDao.getScore(App.PONG);
            }

            //Scrivo il nuovo totalscore su firebase e sul database locale
            if(App.scoreboardDao.getGame(App.TOTALSCORE) != null)   //Recupero l'eventuale valore dal database locale
                App.scoreboardDao.update(new Scoreboard(App.TOTALSCORE, newTotalscore));
            else
                App.scoreboardDao.insertAll(new Scoreboard(App.TOTALSCORE, newTotalscore));

            if( ! nickname.equals("-"))
            {
                BackEndInterface.get().writeScoreFirebase(nickname);
                BackEndInterface.get().writeScoreFirebase(App.TOTALSCORE, nickname, newTotalscore);
            }

            runnable.run();
        });
    }
}
