package com.nullpointerexception.retrogames.Welcome;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.nullpointerexception.retrogames.Activities.LoginActivity;
import com.nullpointerexception.retrogames.Components.OnTouchAnimatedListener;
import com.nullpointerexception.retrogames.HomeActivity;
import com.nullpointerexception.retrogames.R;

public class FragmentFirstAccessActivity extends Fragment
{

    private Button login;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_first_access, container, false);

        login = view.findViewById(R.id.button_login_first);
        login.setOnTouchListener(new OnTouchAnimatedListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(v.getContext(), LoginActivity.class);
                startActivity(intent);

                if(getActivity() != null)
                    getActivity().finish();
            }
        });

        view.findViewById(R.id.textView_dont_log_first)
                .setOnTouchListener(new OnTouchAnimatedListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(v.getContext(), HomeActivity.class);
                startActivity(intent);

                if(getActivity() != null)
                    getActivity().finish();
            }
        });

        return view;
    }

}
