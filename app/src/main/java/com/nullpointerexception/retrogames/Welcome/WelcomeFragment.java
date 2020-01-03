package com.nullpointerexception.retrogames.Welcome;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nullpointerexception.retrogames.R;


public class WelcomeFragment extends Fragment {

    Button next;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        next = view.findViewById(R.id.button_welcome);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment, new FragmentFirstAccessActivity(), "FragmentFirstAccessActivity");
                ft.commit();
            }
        });

        return view;
    }

}
