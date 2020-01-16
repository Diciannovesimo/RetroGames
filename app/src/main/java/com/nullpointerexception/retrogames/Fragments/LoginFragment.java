package com.nullpointerexception.retrogames.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.nullpointerexception.retrogames.Activities.HomeActivity;
import com.nullpointerexception.retrogames.Activities.LoginActivity;
import com.nullpointerexception.retrogames.Components.OnTouchAnimatedListener;
import com.nullpointerexception.retrogames.R;

public class LoginFragment extends Fragment
{

    private Button login;
    private boolean isFirstAccess = true;

    public LoginFragment() {}

    public LoginFragment(boolean isFirstAccess)
    {
        this.isFirstAccess = isFirstAccess;
    }

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

        TextView dontLogin = view.findViewById(R.id.textView_dont_log_first);

        //se è il primo accesso
        if(isFirstAccess)
            dontLogin.setOnTouchListener(new OnTouchAnimatedListener()
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
        else
        {
            int color = Color.parseColor("#DE000000");
            ((TextView) view.findViewById(R.id.textView_info_first))
                    .setTextColor(color);
            ((TextView) view.findViewById(R.id.textView_log_first))
                    .setTextColor(color);
            login.setBackground( getResources()
                    .getDrawable(R.drawable.selected_chip_background));
            dontLogin.setVisibility(View.GONE);
        }

        return view;
    }

}
