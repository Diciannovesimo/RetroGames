package com.nullpointerexception.retrogames.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.nullpointerexception.retrogames.Breakout.MainActivityBreakout;
import com.nullpointerexception.retrogames.Components.Blocker;
import com.nullpointerexception.retrogames.Components.OnTouchAnimatedListener;
import com.nullpointerexception.retrogames.Hole.FullscreenActivity;
import com.nullpointerexception.retrogames.Pong.MainActivityPong;
import com.nullpointerexception.retrogames.R;
import com.nullpointerexception.retrogames.Snake.MainActivitySnake;
import com.nullpointerexception.retrogames.Tetris.MainActivityTetris;

public class GamesFragment extends Fragment
{

    /*
           UI Components
     */
    private ViewGroup tetrisCard, snakeCard, pongCard, holeCard, breakoutCard;
    private View playButtonTetris, playButtonSnake, playButtonPong, playButtonHole, playButtonBreakout;
    private TextView tetrisText, snakeText, pongText, holeText, breakoutText;
    private ViewGroup clickViewTetris, clickViewSnake, clickViewPong, clickViewHole, clickViewBreakout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //  Assegna UI
        initUI(view);

        //  Imposta i listener
        setOnCardClickListeners();
        setOnPlayButtonClickListener();

        return view;
    }

    private void initUI(View view)
    {
        tetrisCard = view.findViewById(R.id.CardView_tetris);
        snakeCard = view.findViewById(R.id.CardView_snake);
        pongCard = view.findViewById(R.id.CardView_pong);
        holeCard = view.findViewById(R.id.CardView_hole);
        breakoutCard = view.findViewById(R.id.CardView_brick_break);
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
    }

    private void setOnPlayButtonClickListener()
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
                    startActivity(intent);
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
                    startActivity(intent);
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
                    startActivity(intent);
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
                    Intent intent = new Intent(getContext(), FullscreenActivity.class);
                    startActivity(intent);
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
                    startActivity(intent);
                }
            }
        });
    }

    private void setOnCardClickListeners()
    {
        tetrisCard.setOnTouchListener(new OnTouchAnimatedListener()
        {
            boolean clicked = false;

            @Override
            public void onClick(View view)
            {
                if( ! clicked)
                {
                    clickViewTetris.setVisibility(View.VISIBLE);
                    tetrisText.setVisibility(View.GONE);
                }
                else
                {
                    clickViewTetris.setVisibility(View.GONE);
                    tetrisText.setVisibility(View.VISIBLE);
                }
                clicked = ! clicked;
            }
        });

        snakeCard.setOnTouchListener(new OnTouchAnimatedListener()
        {
            boolean clicked = false;

            @Override
            public void onClick(View view)
            {
                if( ! clicked)
                {
                    clickViewSnake.setVisibility(View.VISIBLE);
                    snakeText.setVisibility(View.GONE);
                }
                else
                {
                    clickViewSnake.setVisibility(View.GONE);
                    snakeText.setVisibility(View.VISIBLE);
                }
                clicked = ! clicked;
            }
        });

        pongCard.setOnTouchListener(new OnTouchAnimatedListener()
        {
            boolean clicked = false;

            @Override
            public void onClick(View view)
            {
                if( ! clicked)
                {
                    clickViewPong.setVisibility(View.VISIBLE);
                    pongText.setVisibility(View.GONE);
                }
                else
                {
                    clickViewPong.setVisibility(View.GONE);
                    pongText.setVisibility(View.VISIBLE);
                }
                clicked = ! clicked;
            }
        });

        holeCard.setOnTouchListener(new OnTouchAnimatedListener()
        {
            boolean clicked = false;

            @Override
            public void onClick(View view)
            {
                if( ! clicked)
                {
                    clickViewHole.setVisibility(View.VISIBLE);
                    holeText.setVisibility(View.GONE);
                }
                else
                {
                    clickViewHole.setVisibility(View.GONE);
                    holeText.setVisibility(View.VISIBLE);
                }
                clicked = ! clicked;
            }
        });

        breakoutCard.setOnTouchListener(new OnTouchAnimatedListener()
        {
            boolean clicked = false;

            @Override
            public void onClick(View view)
            {
                if( ! clicked)
                {
                    clickViewBreakout.setVisibility(View.VISIBLE);
                    breakoutText.setVisibility(View.GONE);
                }
                else
                {
                    clickViewBreakout.setVisibility(View.GONE);
                    breakoutText.setVisibility(View.VISIBLE);
                }
                clicked = ! clicked;
            }
        });
    }

}
