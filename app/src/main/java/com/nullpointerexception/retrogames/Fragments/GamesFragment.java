package com.nullpointerexception.retrogames.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.nullpointerexception.retrogames.Breakout.MainActivityBreakout;
import com.nullpointerexception.retrogames.Components.Blocker;
import com.nullpointerexception.retrogames.Components.OnTouchAnimatedListener;
import com.nullpointerexception.retrogames.Hole.FullscreenActivity;
import com.nullpointerexception.retrogames.Pong.MainActivityPong;
import com.nullpointerexception.retrogames.R;
import com.nullpointerexception.retrogames.Snake.MainActivitySnake;
import com.nullpointerexception.retrogames.Tetris.Tetris;

public class GamesFragment extends Fragment
{

    /*
           UI Components
     */
    private ViewGroup tetrisCard, snakeCard, pongCard, spaceInvadersCard, breakoutCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tetrisCard = view.findViewById(R.id.CardView_tetris);
        snakeCard = view.findViewById(R.id.CardView_snake);
        pongCard = view.findViewById(R.id.CardView_pong);
        spaceInvadersCard = view.findViewById(R.id.CardView_hole);
        breakoutCard = view.findViewById(R.id.CardView_brick_break);

        tetrisCard.setOnTouchListener(new OnTouchAnimatedListener()
        {
            Blocker blocker = new Blocker();
            @Override
            public void onClick(View view)
            {
                if(!blocker.block()) {
                    Intent intent = new Intent(getContext(), Tetris.class);
                    startActivity(intent);
                }
            }
        });

        snakeCard.setOnTouchListener(new OnTouchAnimatedListener()
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

        pongCard.setOnTouchListener(new OnTouchAnimatedListener()
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

        spaceInvadersCard.setOnTouchListener(new OnTouchAnimatedListener()
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

        breakoutCard.setOnTouchListener(new OnTouchAnimatedListener()
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

        return view;
    }

}
