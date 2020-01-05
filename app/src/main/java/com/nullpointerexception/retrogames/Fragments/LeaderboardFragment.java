package com.nullpointerexception.retrogames.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.nullpointerexception.retrogames.Components.OnTouchAnimatedListener;
import com.nullpointerexception.retrogames.Components.ProfileImageFetcher;
import com.nullpointerexception.retrogames.Components.User;
import com.nullpointerexception.retrogames.R;

public class LeaderboardFragment extends Fragment
{
    private View selectedView;

    /*
          UI Components
     */
    private ViewGroup chipsContainer;
    private ImageView profileImage;
    private TextView profileName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        profileImage = view.findViewById(R.id.profileImageView);
        profileName = view.findViewById(R.id.textView_profile_name);
        chipsContainer = view.findViewById(R.id.chipsContainer);

        /*
                TODO Dopo il testing, prendere l'utente loggato
         */
        User user = new User();
        user.setNickname("Sgrulu");

        profileName.setText(user.getNickname());

        if(getContext() != null)
            new ProfileImageFetcher(getContext())
                    .fetchImageOf(user, drawable -> profileImage.setImageDrawable(drawable));

        for(int i = 0; i < chipsContainer.getChildCount(); i++)
            chipsContainer.getChildAt(i).setOnTouchListener(new OnTouchAnimatedListener()
            {
                @Override
                public void onClick(View view)
                {
                    selectChip(view);
                }
            });

        selectChip(chipsContainer.getChildAt(0));

        return view;
    }

    private void selectChip(View view)
    {
        if(selectedView != null)
            selectedView.setBackground( getResources().getDrawable(R.drawable.roundedbutton_forleaderboard));

        view.setBackground( getResources().getDrawable(R.drawable.selected_chip_background));
        selectedView = view;
    }
}
