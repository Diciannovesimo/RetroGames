package com.nullpointerexception.retrogames.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nullpointerexception.retrogames.Components.App;
import com.nullpointerexception.retrogames.Components.BackEndInterface;
import com.nullpointerexception.retrogames.Components.OnTouchAnimatedListener;
import com.nullpointerexception.retrogames.Components.ProfileImageFetcher;
import com.nullpointerexception.retrogames.Components.Scoreboard;
import com.nullpointerexception.retrogames.Components.User;
import com.nullpointerexception.retrogames.Components.UserScoreView;
import com.nullpointerexception.retrogames.R;

import java.util.List;
import java.util.Vector;

public class LeaderboardFragment extends Fragment
{
    private View selectedView;
    private User user;

    /*
          UI Components
     */
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerViewAdapter adapter;
    private ViewGroup chipsContainer;
    private ImageView profileImage;
    private TextView profileName, positionTextview, userScoreTextview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        profileImage = view.findViewById(R.id.profileImageView);
        profileName = view.findViewById(R.id.textView_profile_name);
        chipsContainer = view.findViewById(R.id.chipsContainer);
        recyclerView = view.findViewById(R.id.recyclerView);
        positionTextview = view.findViewById(R.id.positionTextview);
        userScoreTextview = view.findViewById(R.id.scoreTextView);

        /*
                Inizializzazione utente
         */
        /*
                TODO Dopo il testing, prendere l'utente loggato
         */
        user = new User();
        user.setNickname("Sgrulu");

        /*      Operazioni da fare se l'utente è loggato
        BackEndInterface.get().readUser(user.getEmail(), (success, value) ->
        {
            if(success)
            {
                profileName.setText(user.getNickname());

                if(getContext() != null)
                    new ProfileImageFetcher(getContext())
                            .fetchImageOf(user, drawable -> profileImage.setImageDrawable(drawable));
            }
        });*/

        profileName.setText(user.getNickname());

        if(getContext() != null)
            new ProfileImageFetcher(getContext())
                    .fetchImageOf(user, drawable -> profileImage.setImageDrawable(drawable));

        /*
                Inizializzazione recyclerView
         */
        //  TODO Remove after tests
        /*
        List<Scoreboard> fakeList = new Vector<>();
        for(int i = 0; i < 20; i++)
        {
            Random random = new Random();
            Scoreboard scoreboard = new Scoreboard();
            scoreboard.setNickname("Player #" + random.nextInt(1000));
            scoreboard.setScore(random.nextInt(3999999));
            fakeList.add(scoreboard);
        }*/

        layoutManager = new LinearLayoutManager(container.getContext());
        recyclerView.setLayoutManager(layoutManager);

        loadScoreboard(App.TOTALSCORE);

        /*
                Assegnazione listeners alle view
         */
        for(int i = 0; i < chipsContainer.getChildCount(); i++)
            chipsContainer.getChildAt(i).setOnTouchListener(new OnTouchAnimatedListener()
            {
                @Override
                public void onClick(View view)
                {
                    selectChip(view);

                    if(view instanceof TextView)
                    {
                        String text = ((TextView) view).getText().toString();

                        //  Switch content text
                        if(text.equals( getResources().getString(R.string.total_score)))
                            loadScoreboard(App.TOTALSCORE);
                        else if(text.equals( getResources().getString(R.string.pac_man)))
                            loadScoreboard(App.PACMAN);
                        else if(text.equals( getResources().getString(R.string.tetris)))
                            loadScoreboard(App.TETRIS);
                        else if(text.equals( getResources().getString(R.string.breakout)))
                            loadScoreboard(App.BREAKOUT);
                        else if(text.equals( getResources().getString(R.string.pong)))
                            loadScoreboard(App.PONG);
                        else if(text.equals( getResources().getString(R.string.space_invaders)))
                            loadScoreboard(App.SPACEINVADERS);
                    }
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

    private void loadScoreboard(String game)
    {
        List<Scoreboard> scoresList = new Vector<>();
        BackEndInterface.get().readAllScoresFirebase(game, (success, scoreboard) ->
        {
            if(success && getActivity() != null)
                getActivity().runOnUiThread(() ->
                {
                    scoresList.add(0, scoreboard);
                    adapter.notifyItemInserted(0);
                });
        });
        adapter = new RecyclerViewAdapter(scoresList);
        recyclerView.setAdapter(adapter);

        BackEndInterface.get().readScoreFirebase(game, user.getNickname(), (success, value) ->
        {
            if(success && getActivity() != null)
                getActivity().runOnUiThread(() ->
                {
                    //positionTextview.setText("#" + ); TODO
                    userScoreTextview.setText( Scoreboard.formatScore(value));
                });
        });
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<UserScoreViewItem>
    {
        private List<Scoreboard> dataSet = new Vector<>();

        public RecyclerViewAdapter(List<Scoreboard> scoreboardList)
        {
            dataSet = scoreboardList;
        }

        @NonNull
        @Override
        public UserScoreViewItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            return new UserScoreViewItem( new UserScoreView(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(@NonNull UserScoreViewItem holder, int position)
        {
            holder.userScoreView.setPosition(position+1);
            holder.userScoreView.setViewWithScore( dataSet.get(position));
        }

        @Override
        public int getItemCount()
        {
            return dataSet.size();
        }
    }

    public class UserScoreViewItem extends RecyclerView.ViewHolder
    {
        UserScoreView userScoreView;

        public UserScoreViewItem(@NonNull View itemView)
        {
            super(itemView);
            userScoreView = (UserScoreView) itemView;
        }
    }
}
