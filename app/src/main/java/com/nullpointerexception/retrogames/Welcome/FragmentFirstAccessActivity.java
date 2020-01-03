package com.nullpointerexception.retrogames.Welcome;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nullpointerexception.retrogames.Activities.LoginActivity;
import com.nullpointerexception.retrogames.R;


public class FragmentFirstAccessActivity extends Fragment {

    Button login;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_first_access, container, false);

        login = view.findViewById(R.id.button_login_first);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

}
