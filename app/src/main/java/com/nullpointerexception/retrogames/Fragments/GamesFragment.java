package com.nullpointerexception.retrogames.Fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nullpointerexception.retrogames.App;
import com.nullpointerexception.retrogames.Breakout.MainActivityBreakout;
import com.nullpointerexception.retrogames.Components.Blocker;
import com.nullpointerexception.retrogames.Components.OnTouchAnimatedListener;
import com.nullpointerexception.retrogames.Hole.MainActivityHole;
import com.nullpointerexception.retrogames.Pong.MainActivityPong;
import com.nullpointerexception.retrogames.R;
import com.nullpointerexception.retrogames.Snake.MainActivitySnake;
import com.nullpointerexception.retrogames.Tetris.MainActivityTetris;

public class GamesFragment extends Fragment
{
    //Costanti
    private final int TETRIS = 0;
    private final int SNAKE = 1;
    private final int PONG = 2;
    private final int HOLE = 3;
    private final int BREAKOUT = 4;

    //UI Components Portrait
    private View lastViewSelected1, lastViewSelected2;
    private View playButtonTetris, playButtonSnake, playButtonPong, playButtonHole, playButtonBreakout;
    private TextView tetrisText, snakeText, pongText, holeText, breakoutText,
            tetrisHighscoreText, snakeHighscoreText,
            pongHighscoreText, holeHighscoreText, breakoutHighscoreText;
    private ViewGroup clickViewTetris, clickViewSnake, clickViewPong, clickViewHole, clickViewBreakout;

    //UI Components Portrait / Landscape
    private ViewGroup tetrisCard, snakeCard, pongCard, holeCard, breakoutCard;

    //UI Components Landscape
    private ViewGroup gamesCard, playButton;
    private TextView gamesTitleTextview, gamesHighscoreTextview;

    public GamesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //  Assegna UI
        initUI(view);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            showCardOfGame(App.TETRIS);
            setOnCardClickListenersLandscape();
        }
        else
        {
            setPortraitHighscores();

            //  Imposta i listener
            setOnCardClickListenersPortrait();
            setOnPlayButtonClickListenerPortrait();
        }

        return view;
    }

    /**
     * Inizializza gli elementi dell'interfaccia grafica
     * @param view
     */
    private void initUI(View view)
    {
        tetrisCard = view.findViewById(R.id.CardView_tetris);
        snakeCard = view.findViewById(R.id.CardView_snake);
        pongCard = view.findViewById(R.id.CardView_pong);
        holeCard = view.findViewById(R.id.CardView_hole);
        breakoutCard = view.findViewById(R.id.CardView_brick_break);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            gamesCard = view.findViewById(R.id.games_info);
            gamesTitleTextview = view.findViewById(R.id.textView_games_onclick);
            gamesHighscoreTextview = view.findViewById(R.id.textView_games_highscore);
            playButton = view.findViewById(R.id.onclick_play);
        }
        else
        {
            playButtonTetris = view.findViewById(R.id.imageView_play_tetris);
            playButtonSnake = view.findViewById(R.id.imageView_play_snake);
            playButtonPong = view.findViewById(R.id.imageView_play_pong);
            playButtonHole = view.findViewById(R.id.imageView_play_hole);
            playButtonBreakout = view.findViewById(R.id.imageView_play_brick_break);
            tetrisText = view.findViewById(R.id.textView_tetris);
            snakeText = view.findViewById(R.id.textView_snake);
            pongText = view.findViewById(R.id.textView_pong);
            holeText = view.findViewById(R.id.textView_hole);
            breakoutText = view.findViewById(R.id.textView_brick_break);
            clickViewTetris = view.findViewById(R.id.Constraint_onclick_tetris);
            clickViewSnake = view.findViewById(R.id.Constraint_onclick_snake);
            clickViewPong = view.findViewById(R.id.Constraint_onclick_pong);
            clickViewHole = view.findViewById(R.id.Constraint_onclick_hole);
            clickViewBreakout = view.findViewById(R.id.Constraint_onclick_brick_break);
            tetrisHighscoreText = view.findViewById(R.id.textView_tetris_highscore);
            snakeHighscoreText = view.findViewById(R.id.textView_snake_highscore);
            pongHighscoreText = view.findViewById(R.id.textView_pong_highscore);
            holeHighscoreText = view.findViewById(R.id.textView_hole_highscore);
            breakoutHighscoreText = view.findViewById(R.id.textView_brick_break_highscore);
        }
    }

    /**
     * Imposta gli highscores quando la schermata è in portait
     */
    private void setPortraitHighscores()
    {
        tetrisHighscoreText.setText( String.valueOf(App.scoreboardDao.getScore(App.TETRIS) ));
        snakeHighscoreText.setText( String.valueOf(App.scoreboardDao.getScore(App.SNAKE) ));
        pongHighscoreText.setText( String.valueOf(App.scoreboardDao.getScore(App.PONG) ));
        holeHighscoreText.setText( String.valueOf(App.scoreboardDao.getScore(App.HOLE) ));
        breakoutHighscoreText.setText( String.valueOf(App.scoreboardDao.getScore(App.BREAKOUT) ));
    }

    /**
     * Imposta il listener sui pulsanti play quando è in portait
     */
    private void setOnPlayButtonClickListenerPortrait()
    {
        playButtonTetris.setOnTouchListener(new OnTouchAnimatedListener()
        {
            Blocker blocker = new Blocker();

            @Override
            public void onClick(View view)
            {
                if( ! blocker.block())
                {
                    Intent intent = new Intent(getContext(), MainActivityTetris.class);
                    startActivityForResult(intent, TETRIS);
                }
            }
        });

        playButtonSnake.setOnTouchListener(new OnTouchAnimatedListener()
        {
            Blocker blocker = new Blocker();
            @Override
            public void onClick(View view)
            {
                if(!blocker.block()) {
                    Intent intent = new Intent(getContext(), MainActivitySnake.class);
                    startActivityForResult(intent, SNAKE);
                }
            }
        });

        playButtonPong.setOnTouchListener(new OnTouchAnimatedListener()
        {
            Blocker blocker = new Blocker();
            @Override
            public void onClick(View view)
            {
                if(!blocker.block()) {
                    Intent intent = new Intent(getContext(), MainActivityPong.class);
                    startActivityForResult(intent, PONG);
                }
            }
        });

        playButtonHole.setOnTouchListener(new OnTouchAnimatedListener()
        {
            Blocker blocker = new Blocker();
            @Override
            public void onClick(View view)
            {
                if(!blocker.block()) {
                    Intent intent = new Intent(getContext(), MainActivityHole.class);
                    startActivityForResult(intent, HOLE);
                }
            }
        });

        playButtonBreakout.setOnTouchListener(new OnTouchAnimatedListener()
        {
            Blocker blocker = new Blocker();
            @Override
            public void onClick(View view)
            {
                if(!blocker.block()) {
                    Intent intent = new Intent(getContext(), MainActivityBreakout.class);
                    startActivityForResult(intent, BREAKOUT);
                }
            }
        });
    }

    /**
     * Imposta il listener sulla cardview quando sono in portait
     */
    private void setOnCardClickListenersPortrait()
    {
        tetrisCard.setOnTouchListener(new OnTouchAnimatedListener()
        {
            @Override
            public void onClick(View view)
            {
                if(clickViewTetris.getVisibility() == View.GONE)
                {
                    if(lastViewSelected1 != null && lastViewSelected2 != null)
                    {
                        lastViewSelected1.setVisibility(View.GONE);
                        lastViewSelected2.setVisibility(View.VISIBLE);
                    }

                    clickViewTetris.setVisibility(View.VISIBLE);
                    tetrisText.setVisibility(View.GONE);

                    lastViewSelected1 = clickViewTetris;
                    lastViewSelected2 = tetrisText;
                }
                else
                {
                    clickViewTetris.setVisibility(View.GONE);
                    tetrisText.setVisibility(View.VISIBLE);
                }
            }
        });

        snakeCard.setOnTouchListener(new OnTouchAnimatedListener()
        {
            @Override
            public void onClick(View view)
            {
                if(clickViewSnake.getVisibility() == View.GONE)
                {
                    if(lastViewSelected1 != null && lastViewSelected2 != null)
                    {
                        lastViewSelected1.setVisibility(View.GONE);
                        lastViewSelected2.setVisibility(View.VISIBLE);
                    }

                    clickViewSnake.setVisibility(View.VISIBLE);
                    snakeText.setVisibility(View.GONE);

                    lastViewSelected1 = clickViewSnake;
                    lastViewSelected2 = snakeText;
                }
                else
                {
                    clickViewSnake.setVisibility(View.GONE);
                    snakeText.setVisibility(View.VISIBLE);
                }
            }
        });

        pongCard.setOnTouchListener(new OnTouchAnimatedListener()
        {
            @Override
            public void onClick(View view)
            {
                if(clickViewPong.getVisibility() == View.GONE)
                {
                    if(lastViewSelected1 != null && lastViewSelected2 != null)
                    {
                        lastViewSelected1.setVisibility(View.GONE);
                        lastViewSelected2.setVisibility(View.VISIBLE);
                    }

                    clickViewPong.setVisibility(View.VISIBLE);
                    pongText.setVisibility(View.GONE);

                    lastViewSelected1 = clickViewPong;
                    lastViewSelected2 = pongText;
                }
                else
                {
                    clickViewPong.setVisibility(View.GONE);
                    pongText.setVisibility(View.VISIBLE);
                }
            }
        });

        holeCard.setOnTouchListener(new OnTouchAnimatedListener()
        {
            @Override
            public void onClick(View view)
            {
                if(clickViewHole.getVisibility() == View.GONE)
                {
                    if(lastViewSelected1 != null && lastViewSelected2 != null)
                    {
                        lastViewSelected1.setVisibility(View.GONE);
                        lastViewSelected2.setVisibility(View.VISIBLE);
                    }

                    clickViewHole.setVisibility(View.VISIBLE);
                    holeText.setVisibility(View.GONE);

                    lastViewSelected1 = clickViewHole;
                    lastViewSelected2 = holeText;
                }
                else
                {
                    clickViewHole.setVisibility(View.GONE);
                    holeText.setVisibility(View.VISIBLE);
                }
            }
        });

        breakoutCard.setOnTouchListener(new OnTouchAnimatedListener()
        {
            @Override
            public void onClick(View view)
            {
                if(clickViewBreakout.getVisibility() == View.GONE)
                {
                    if(lastViewSelected1 != null && lastViewSelected2 != null)
                    {
                        lastViewSelected1.setVisibility(View.GONE);
                        lastViewSelected2.setVisibility(View.VISIBLE);
                    }

                    clickViewBreakout.setVisibility(View.VISIBLE);
                    breakoutText.setVisibility(View.GONE);

                    lastViewSelected1 = clickViewBreakout;
                    lastViewSelected2 = breakoutText;
                }
                else
                {
                    clickViewBreakout.setVisibility(View.GONE);
                    breakoutText.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * Imposta il listener sulla cardview quando sono in landscape
     */
    private void setOnCardClickListenersLandscape()
    {
        tetrisCard.setOnTouchListener(new OnTouchAnimatedListener()
        {
            @Override
            public void onClick(View view)
            {
                showCardOfGame(App.TETRIS);
            }
        });

        snakeCard.setOnTouchListener(new OnTouchAnimatedListener()
        {
            @Override
            public void onClick(View view)
            {
                showCardOfGame(App.SNAKE);
            }
        });

        pongCard.setOnTouchListener(new OnTouchAnimatedListener()
        {
            @Override
            public void onClick(View view)
            {
                showCardOfGame(App.PONG);
            }
        });

        holeCard.setOnTouchListener(new OnTouchAnimatedListener()
        {
            @Override
            public void onClick(View view)
            {
                showCardOfGame(App.HOLE);
            }
        });

        breakoutCard.setOnTouchListener(new OnTouchAnimatedListener()
        {
            @Override
            public void onClick(View view)
            {
                showCardOfGame(App.BREAKOUT);
            }
        });
    }

    /**
     * Mostra le CardView personalizzate per ogni gioco
     * @param game Una stirnga contenente il nome del gioco
     */
    private void showCardOfGame(String game)
    {
        if(gamesCard == null || game == null)
            return;

        TranslateAnimation animation =
                new TranslateAnimation(0, 0,
                                       gamesCard.getY() + gamesCard.getHeight(),
                                                 0);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setDuration(300);
        gamesCard.startAnimation(animation);

        gamesHighscoreTextview.setText( String.valueOf(App.scoreboardDao.getScore(game)));
        if(game.equals(App.TETRIS))
        {
            gamesTitleTextview.setText( getResources().getString(R.string.tetris));
            playButton.setOnTouchListener(new OnTouchAnimatedListener()
            {
                Blocker blocker = new Blocker();

                @Override
                public void onClick(View view)
                {
                    if( ! blocker.block())
                    {
                        Intent intent = new Intent(getContext(), MainActivityTetris.class);
                        startActivityForResult(intent, TETRIS);
                    }
                }
            });
        }
        else if(game.equals(App.SNAKE))
        {
            gamesTitleTextview.setText( getResources().getString(R.string.snake));
            playButton.setOnTouchListener(new OnTouchAnimatedListener()
            {
                Blocker blocker = new Blocker();

                @Override
                public void onClick(View view)
                {
                    if( ! blocker.block())
                    {
                        Intent intent = new Intent(getContext(), MainActivitySnake.class);
                        startActivityForResult(intent, SNAKE);
                    }
                }
            });
        }
        else if(game.equals(App.PONG))
        {
            gamesTitleTextview.setText( getResources().getString(R.string.pong));
            playButton.setOnTouchListener(new OnTouchAnimatedListener()
            {
                Blocker blocker = new Blocker();

                @Override
                public void onClick(View view)
                {
                    if( ! blocker.block())
                    {
                        Intent intent = new Intent(getContext(), MainActivityPong.class);
                        startActivityForResult(intent, PONG);
                    }
                }
            });
        }
        else if(game.equals(App.HOLE))
        {
            gamesTitleTextview.setText( getResources().getString(R.string.hole));
            playButton.setOnTouchListener(new OnTouchAnimatedListener()
            {
                Blocker blocker = new Blocker();

                @Override
                public void onClick(View view)
                {
                    if( ! blocker.block())
                    {
                        Intent intent = new Intent(getContext(), MainActivityHole.class);
                        startActivityForResult(intent, HOLE);
                    }
                }
            });
        }
        else if(game.equals(App.BREAKOUT))
        {
            gamesTitleTextview.setText( getResources().getString(R.string.breakout));
            playButton.setOnTouchListener(new OnTouchAnimatedListener()
            {
                Blocker blocker = new Blocker();

                @Override
                public void onClick(View view)
                {
                    if( ! blocker.block())
                    {
                        Intent intent = new Intent(getContext(), MainActivityBreakout.class);
                        startActivityForResult(intent, BREAKOUT);
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        switch (requestCode)
        {
            case TETRIS:
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                    gamesHighscoreTextview.setText( String.valueOf(App.scoreboardDao.getScore(App.TETRIS) ));
                else
                    tetrisHighscoreText.setText( String.valueOf(App.scoreboardDao.getScore(App.TETRIS) ));
                break;
            case SNAKE:
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                    gamesHighscoreTextview.setText( String.valueOf(App.scoreboardDao.getScore(App.SNAKE) ));
                else
                    snakeHighscoreText.setText( String.valueOf(App.scoreboardDao.getScore(App.SNAKE) ));
                break;
            case PONG:
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                    gamesHighscoreTextview.setText( String.valueOf(App.scoreboardDao.getScore(App.PONG) ));
                else
                    pongHighscoreText.setText( String.valueOf(App.scoreboardDao.getScore(App.PONG) ));
                break;
            case HOLE:
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                    gamesHighscoreTextview.setText( String.valueOf(App.scoreboardDao.getScore(App.HOLE) ));
                else
                    holeHighscoreText.setText( String.valueOf(App.scoreboardDao.getScore(App.HOLE) ));
                break;
            case BREAKOUT:
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                    gamesHighscoreTextview.setText( String.valueOf(App.scoreboardDao.getScore(App.BREAKOUT) ));
                else
                    breakoutHighscoreText.setText( String.valueOf(App.scoreboardDao.getScore(App.BREAKOUT) ));
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
