package com.nullpointerexception.retrogames.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.nullpointerexception.retrogames.Components.OnTouchAnimatedListener;
import com.nullpointerexception.retrogames.R;

public class WelcomeFragment extends Fragment
{

    private Button nextButton;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        nextButton = view.findViewById(R.id.button_welcome);
        nextButton.setOnTouchListener(new OnTouchAnimatedListener()
        {
            @Override
            public void onClick(View view)
            {
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_from_right,
                                R.anim.slide_out_to_left)
                        .replace(R.id.fragment, new LoginFragment())
                        .commit();
            }
        });

        return view;
    }

}
