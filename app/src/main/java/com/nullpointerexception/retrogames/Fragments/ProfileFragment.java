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

import com.nullpointerexception.retrogames.Activities.HomeActivity;
import com.nullpointerexception.retrogames.App;
import com.nullpointerexception.retrogames.Components.AuthenticationManager;
import com.nullpointerexception.retrogames.Components.BackEndInterface;
import com.nullpointerexception.retrogames.Components.Blocker;
import com.nullpointerexception.retrogames.Components.OnTouchAnimatedListener;
import com.nullpointerexception.retrogames.Components.ProfileImageGenerator;
import com.nullpointerexception.retrogames.Components.Scoreboard;
import com.nullpointerexception.retrogames.R;

public class ProfileFragment extends Fragment
{
    private String nickname;
    private ImageView profileImage, logoutButton;
    private TextView profileName, totalScore, scoreTetris, scorePong, scoreBreakout, scoreSnake, scoreHole,
                    totalscorePosition, positionTetris, positionPong, positionBreakout, positionSnake, positionHole;

    /**
     * Imposta il nickname prendedolo dall'utente loggato su AuthenticationManager
     */
    public ProfileFragment() {
        nickname = AuthenticationManager.get().getUserLogged().getNickname();
    }

    /**
     * Imposta il nickname  prendendolo dalla stringa passata come parametro
     * @param nickname stringa contenente  il nickname dell'utente
     */
    public ProfileFragment(String nickname) {
        this.nickname = nickname;
    }

    @SuppressLint({"ClickableViewAccessibility", "DefaultLocale"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

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


        //Assegnazione contenuti grafici


        //  Generazione immagine profilo
        if(getContext() != null)
            new ProfileImageGenerator(getContext())
                    .fetchImageOf(nickname, drawable -> profileImage.setImageDrawable(drawable));

        //  Assegnazione testi
        profileName.setText(nickname);

        if( ! nickname.equals(AuthenticationManager.get().getUserLogged().getNickname()))
        {
            view.findViewById(R.id.textView3).setVisibility(View.GONE);
            logoutButton.setVisibility(View.GONE);
        }
        else
        {
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
        }


        logoutButton.setOnTouchListener(new OnTouchAnimatedListener()
        {
            Blocker mBlocker = new Blocker();

            @Override
            public void onClick(View view)
            {
                if ( ! mBlocker.block())
                {
                    App.scoreboardDao.delete(App.scoreboardDao.getAll());
                    SharedPreferences prefs = getContext()
                            .getSharedPreferences(App.USER, Context.MODE_PRIVATE);
                    prefs.edit().clear().apply();

                    SharedPreferences prefs2 =getContext().
                            getSharedPreferences(App.APP_VARIABLES, Context.MODE_PRIVATE);
                    prefs2.edit().putBoolean(App.PREFS_INVALIDATE_FIREBASE_SCORES, false).apply();

                    AuthenticationManager.get().logout();
                    startActivity(new Intent(getContext(), HomeActivity.class));
                    if (getActivity() != null)
                        getActivity().finish();
                }
            }
        });

        updatePositions();

        return view;
    }

    @SuppressLint("DefaultLocale")
    private void updatePositions() {
        //  Total score
        BackEndInterface.get().readAllScoresFirebase(App.TOTALSCORE,
        (success, scoreboardList) ->
        {
            for(int i = 0; i < scoreboardList.size(); i++)
            {
                Scoreboard scoreboard = scoreboardList.get(i);
                if(scoreboard.getNickname().equals( nickname ))
                {
                    if(getActivity() != null)
                    {
                        int finalI = i+1;
                        getActivity().runOnUiThread(() ->
                        {
                            totalscorePosition.setText(String.format("#%d %s", finalI,
                                    getResources().getString(R.string.global_ranking)));
                            totalScore.setText( String.valueOf(scoreboard.getScore()));
                        });

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
                        if(scoreboard.getNickname().equals( nickname ))
                        {
                            if(getActivity() != null)
                            {
                                int finalI = i+1;
                                getActivity().runOnUiThread(() ->
                                {
                                    positionSnake.setText(String.format("#%d", finalI));
                                    scoreSnake.setText( String.valueOf(scoreboard.getScore()));
                                });

                                //  Update database
                                updateDatabase(App.SNAKE, finalI);
                            }
                            break;
                        }
                    }
                });

        //  MainActivityTetris
        BackEndInterface.get().readAllScoresFirebase(App.TETRIS,
                (success, scoreboardList) -> {
                    for(int i = 0; i < scoreboardList.size(); i++)
                    {
                        Scoreboard scoreboard = scoreboardList.get(i);
                        if(scoreboard.getNickname().equals( nickname ))
                        {
                            if(getActivity() != null)
                            {
                                int finalI = i+1;
                                getActivity().runOnUiThread(() ->
                                {
                                    positionTetris.setText(String.format("#%d", finalI));
                                    scoreTetris.setText( String.valueOf(scoreboard.getScore()));
                                });

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
                        if(scoreboard.getNickname().equals( nickname ))
                        {
                            if(getActivity() != null)
                            {
                                int finalI = i+1;
                                getActivity().runOnUiThread(() ->
                                {
                                    positionPong.setText(String.format("#%d", finalI));
                                    scorePong.setText( String.valueOf(scoreboard.getScore()));
                                });

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
                        if(scoreboard.getNickname().equals( nickname ))
                        {
                            if(getActivity() != null)
                            {
                                int finalI = i+1;
                                getActivity().runOnUiThread(() ->
                                {
                                    positionBreakout.setText(String.format("#%d", finalI));
                                    scoreBreakout.setText( String.valueOf(scoreboard.getScore()));
                                });

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
                        if(scoreboard.getNickname().equals( nickname ))
                        {
                            if(getActivity() != null)
                            {
                                int finalI = i+1;
                                getActivity().runOnUiThread(() ->
                                {
                                    positionHole.setText(String.format("#%d", finalI));
                                    scoreHole.setText( String.valueOf(scoreboard.getScore()));
                                });

                                //  Update database
                                updateDatabase(App.HOLE, finalI);
                            }
                            break;
                        }
                    }
                });
    }

    /**
     * Aggiorna il database locale con la nuova posizione
     * @param game stringa contenente il nome del gioco
     * @param position intero contenente la nuova posizione ottenuta dal giocatore
     */
    private void updateDatabase(String game, int position) {
        //  Controlla se il profilo mostrato è quello dell'utente loggato
        if(AuthenticationManager.get().isUserLogged())
        if( ! nickname.equals(AuthenticationManager.get().getUserLogged().getNickname()))
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
