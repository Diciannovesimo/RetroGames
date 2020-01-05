package com.nullpointerexception.retrogames.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.nullpointerexception.retrogames.Activities.LoginActivity;
import com.nullpointerexception.retrogames.Components.OnTouchAnimatedListener;
import com.nullpointerexception.retrogames.Components.ProfileImageFetcher;
import com.nullpointerexception.retrogames.Components.User;
import com.nullpointerexception.retrogames.MainActivity;
import com.nullpointerexception.retrogames.R;

public class ProfileFragment extends Fragment
{
    /*
            UI Components
     */
    private ImageView profileImage, logoutButton;
    private TextView profileName;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImage = view.findViewById(R.id.imageView_profile);
        profileName = view.findViewById(R.id.textView_profile_name);
        logoutButton = view.findViewById(R.id.imageView_logout);

        /*
                TODO Dopo il testing, prendere l'utente loggato
         */
        User user = new User();
        user.setNickname("Sgrulu");

        profileName.setText(user.getNickname());

        if(getContext() != null)
        new ProfileImageFetcher(getContext())
                .fetchImageOf(user, drawable -> profileImage.setImageDrawable(drawable));

        profileImage.setOnClickListener(v ->
                startActivity(new Intent(getContext(), MainActivity.class)));

        logoutButton.setOnTouchListener(new OnTouchAnimatedListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(getContext(), LoginActivity.class));
                if(getActivity() != null)
                    getActivity().finish();
            }
        });

        return view;
    }

}
