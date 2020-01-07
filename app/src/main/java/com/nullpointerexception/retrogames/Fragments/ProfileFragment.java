package com.nullpointerexception.retrogames.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.nullpointerexception.retrogames.Activities.LoginActivity;
import com.nullpointerexception.retrogames.App;
import com.nullpointerexception.retrogames.Components.AuthenticationManager;
import com.nullpointerexception.retrogames.Components.BackEndInterface;
import com.nullpointerexception.retrogames.Components.OnTouchAnimatedListener;
import com.nullpointerexception.retrogames.Components.ProfileImageGenerator;
import com.nullpointerexception.retrogames.Components.Scoreboard;
import com.nullpointerexception.retrogames.Components.User;
import com.nullpointerexception.retrogames.R;

public class ProfileFragment extends Fragment
{
    private User user;

    /*
            UI Components
     */
    private ImageView profileImage, logoutButton;
    private TextView profileName,
                    totalScore,
                    scoreTetris,
                    scorePong,
                    scoreBreakout,
                    scoreSnake,
                    scoreHole,
                    totalscorePosition,
                    positionTetris,
                    positionPong,
                    positionBreakout,
                    positionSnake,
                    positionHole;

    public ProfileFragment()
    {
        user = AuthenticationManager.get().getUserLogged();
    }

    public ProfileFragment(User user)
    {
        this.user = user;
    }

    @SuppressLint({"ClickableViewAccessibility", "DefaultLocale"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        /*
                UI Assignment
         */
        profileImage = view.findViewById(R.id.imageView_profile);
        profileName = view.findViewById(R.id.textView_profile_name);
        logoutButton = view.findViewById(R.id.imageView_logout);
        totalScore = view.findViewById(R.id.textView_total_score);
        scoreTetris = view.findViewById(R.id.textView_score_tetris);
        scoreSnake = view.findViewById(R.id.textView_score_snake);
        scorePong = view.findViewById(R.id.textView_score_pong);
        scoreBreakout = view.findViewById(R.id.textView_score_brick_breaker);
        scoreHole = view.findViewById(R.id.textView_score_hole);
        totalscorePosition = view.findViewById(R.id.textview_totalscore_position);
        positionTetris = view.findViewById(R.id.textView_position_tetris);
        positionSnake = view.findViewById(R.id.textView_position_snake);
        positionPong = view.findViewById(R.id.textView_position_pong);
        positionBreakout = view.findViewById(R.id.textView_position_brick_breaker);
        positionHole = view.findViewById(R.id.textView_position_hole);

        /*
                Assegnazione contenuti grafici
         */

        //  Generazione immagine profilo
        if(getContext() != null)
            new ProfileImageGenerator(getContext())
                    .fetchImageOf(user, drawable -> profileImage.setImageDrawable(drawable));

        //  Assegnazione testi
        profileName.setText(user.getNickname());

        //  Assegnazione score
        totalScore.setText( String.valueOf(App.scoreboardDao.getScore(App.TOTALSCORE)) );
        scoreSnake.setText( String.valueOf(App.scoreboardDao.getScore(App.SNAKE)) );
        scoreTetris.setText( String.valueOf(App.scoreboardDao.getScore(App.TETRIS)) );
        scorePong.setText( String.valueOf(App.scoreboardDao.getScore(App.PONG)) );
        scoreBreakout.setText( String.valueOf(App.scoreboardDao.getScore(App.BREAKOUT)) );
        scoreHole.setText( String.valueOf(App.scoreboardDao.getScore(App.HOLE)) );

        //  Assegnazione posizioni
        if(App.scoreboardDao.getPosition(App.TOTALSCORE) > 0)
            totalscorePosition.setText(String.format("#%d %s", App.scoreboardDao.getPosition(App.TOTALSCORE),
                getResources().getString(R.string.global_ranking)));
        if(App.scoreboardDao.getPosition(App.SNAKE) > 0)
            positionSnake.setText( String.format("#%d", App.scoreboardDao.getPosition(App.SNAKE)));
        if(App.scoreboardDao.getPosition(App.TETRIS) > 0)
            positionTetris.setText( String.format("#%d", App.scoreboardDao.getPosition(App.TETRIS)));
        if(App.scoreboardDao.getPosition(App.PONG) > 0)
            positionPong.setText( String.format("#%d", App.scoreboardDao.getPosition(App.PONG)));
        if(App.scoreboardDao.getPosition(App.HOLE) > 0)
            positionHole.setText( String.format("#%d", App.scoreboardDao.getPosition(App.HOLE)));
        if(App.scoreboardDao.getPosition(App.BREAKOUT) > 0)
            positionBreakout.setText( String.format("#%d", App.scoreboardDao.getPosition(App.BREAKOUT)));

        logoutButton.setOnTouchListener(new OnTouchAnimatedListener()
        {
            @Override
            public void onClick(View view)
            {
                App.scoreboardDao.delete( App.scoreboardDao.getAll() );
                SharedPreferences prefs = getContext()
                        .getSharedPreferences(App.USER, Context.MODE_PRIVATE);
                prefs.edit().clear().apply();

                AuthenticationManager.get().logout();
                startActivity(new Intent(getContext(), LoginActivity.class));
                if(getActivity() != null)
                    getActivity().finish();
            }
        });

        updatePositions();

        return view;
    }

    @SuppressLint("DefaultLocale")
    private void updatePositions()
    {
        //  Total score
        BackEndInterface.get().readAllScoresFirebase(App.TOTALSCORE,
        (success, scoreboardList) ->
        {
            for(int i = 0; i < scoreboardList.size(); i++)
            {
                Scoreboard scoreboard = scoreboardList.get(i);
                if(scoreboard.getNickname().equals( user.getNickname() ))
                {
                    if(getActivity() != null)
                    {
                        int finalI = i;
                        getActivity().runOnUiThread(() ->
                                totalscorePosition.setText(String.format("#%d %s", finalI +1,
                                    getResources().getString(R.string.global_ranking))));

                        //  Update database
                        updateDatabase(App.TOTALSCORE, finalI);
                    }
                    break;
                }
            }
        });

        //  Snake
        BackEndInterface.get().readAllScoresFirebase(App.SNAKE,
                (success, scoreboardList) ->
                {
                    for(int i = 0; i < scoreboardList.size(); i++)
                    {
                        Scoreboard scoreboard = scoreboardList.get(i);
                        if(scoreboard.getNickname().equals( user.getNickname() ))
                        {
                            if(getActivity() != null)
                            {
                                int finalI = i+1;
                                getActivity().runOnUiThread(() ->
                                        positionSnake.setText( String.format("#%d", finalI)));

                                //  Update database
                                updateDatabase(App.SNAKE, finalI);
                            }
                            break;
                        }
                    }
                });

        //  Tetris
        BackEndInterface.get().readAllScoresFirebase(App.TETRIS,
                (success, scoreboardList) ->
                {
                    for(int i = 0; i < scoreboardList.size(); i++)
                    {
                        Scoreboard scoreboard = scoreboardList.get(i);
                        if(scoreboard.getNickname().equals( user.getNickname() ))
                        {
                            if(getActivity() != null)
                            {
                                int finalI = i+1;
                                getActivity().runOnUiThread(() ->
                                        positionTetris.setText( String.format("#%d", finalI)));

                                //  Update database
                                updateDatabase(App.TETRIS, finalI);
                            }
                            break;
                        }
                    }
                });

        //  Pong
        BackEndInterface.get().readAllScoresFirebase(App.PONG,
                (success, scoreboardList) ->
                {
                    for(int i = 0; i < scoreboardList.size(); i++)
                    {
                        Scoreboard scoreboard = scoreboardList.get(i);
                        if(scoreboard.getNickname().equals( user.getNickname() ))
                        {
                            if(getActivity() != null)
                            {
                                int finalI = i+1;
                                getActivity().runOnUiThread(() ->
                                        positionPong.setText( String.format("#%d", finalI)));

                                //  Update database
                                updateDatabase(App.PONG, finalI);
                            }
                            break;
                        }
                    }
                });

        //  Breakout
        BackEndInterface.get().readAllScoresFirebase(App.BREAKOUT,
                (success, scoreboardList) ->
                {
                    for(int i = 0; i < scoreboardList.size(); i++)
                    {
                        Scoreboard scoreboard = scoreboardList.get(i);
                        if(scoreboard.getNickname().equals( user.getNickname() ))
                        {
                            if(getActivity() != null)
                            {
                                int finalI = i+1;
                                getActivity().runOnUiThread(() ->
                                        positionBreakout.setText( String.format("#%d", finalI)));

                                //  Update database
                                updateDatabase(App.BREAKOUT, finalI);
                            }
                            break;
                        }
                    }
                });

        //  Hole
        BackEndInterface.get().readAllScoresFirebase(App.HOLE,
                (success, scoreboardList) ->
                {
                    for(int i = 0; i < scoreboardList.size(); i++)
                    {
                        Scoreboard scoreboard = scoreboardList.get(i);
                        if(scoreboard.getNickname().equals( user.getNickname() ))
                        {
                            if(getActivity() != null)
                            {
                                int finalI = i+1;
                                getActivity().runOnUiThread(() ->
                                        positionHole.setText( String.format("#%d", finalI)));

                                //  Update database
                                updateDatabase(App.HOLE, finalI);
                            }
                            break;
                        }
                    }
                });
    }

    /*
            Aggiorna il database locale con la nuova posizione per il gioco passato
            come parametro.
     */
    private void updateDatabase(String game, int position)
    {
        //  Controlla se il profilo mostrato è quello dell'utente loggato
        if( ! user.getNickname().equals(
                AuthenticationManager.get().getUserLogged().getNickname()))
            return;

        Scoreboard localScore = App.scoreboardDao.getScoreboard(game);
        if(localScore == null)
        {
            localScore = new Scoreboard(game, 0);
            localScore.setPosition(position);
            App.scoreboardDao.insertAll(localScore);
        }
        else
        {
            localScore.setPosition(position);
            App.scoreboardDao.update(localScore);
        }
    }

}
