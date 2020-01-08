package com.nullpointerexception.retrogames.Fragments;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nullpointerexception.retrogames.Activities.ProfileDisplayActivity;
import com.nullpointerexception.retrogames.App;
import com.nullpointerexception.retrogames.Components.AuthenticationManager;
import com.nullpointerexception.retrogames.Components.BackEndInterface;
import com.nullpointerexception.retrogames.Components.OnTouchAnimatedListener;
import com.nullpointerexception.retrogames.Components.ProfileImageGenerator;
import com.nullpointerexception.retrogames.Components.Scoreboard;
import com.nullpointerexception.retrogames.Components.UserScoreView;
import com.nullpointerexception.retrogames.R;

import java.util.List;
import java.util.Vector;

public class LeaderboardFragment extends Fragment
{
    //  Ultimo chip selezionato
    private View selectedView;

    /*
          UI Components
     */
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerViewAdapter adapter;
    private ViewGroup chipsContainer;
    private ImageView profileImage;
    private TextView profileName, positionTextview, userScoreTextview;
    private CardView cardProfile;
    private View progressView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        //  Assegnazione componenti UI
        profileImage = view.findViewById(R.id.profileImageView);
        profileName = view.findViewById(R.id.textView_profile_name);
        chipsContainer = view.findViewById(R.id.chipsContainer);
        recyclerView = view.findViewById(R.id.recyclerView);
        positionTextview = view.findViewById(R.id.positionTextview);
        userScoreTextview = view.findViewById(R.id.scoreTextView);
        cardProfile = view.findViewById(R.id.cardProfileLeaderboard);
        progressView = view.findViewById(R.id.progressView);

        /*
                Inizializzazione utente
         */
        if(AuthenticationManager.get().isUserLogged())
            BackEndInterface.get().readUser(AuthenticationManager.get().getUserLogged().getEmail(), (success, value) ->
            {
                if(success)
                {
                    profileName.setText( AuthenticationManager.get()
                                            .getUserLogged().getNickname());

                    if(getContext() != null)
                        new ProfileImageGenerator(getContext())
                                .fetchImageOf(AuthenticationManager.get().getUserLogged().getNickname(),
                                        drawable -> profileImage.setImageDrawable(drawable));
                }
                else
                    cardProfile.setVisibility(View.GONE);
            });
        else
            cardProfile.setVisibility(View.GONE);

        /*
                Inizializzazione recyclerView
         */
        layoutManager = new LinearLayoutManager(container.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration( new ItemDivider(getContext()));

        //  Carica la recyclerView
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
                        else if(text.equals( getResources().getString(R.string.hole)))
                            loadScoreboard(App.HOLE);
                        else if(text.equals( getResources().getString(R.string.tetris)))
                            loadScoreboard(App.TETRIS);
                        else if(text.equals( getResources().getString(R.string.breakout)))
                            loadScoreboard(App.BREAKOUT);
                        else if(text.equals( getResources().getString(R.string.pong)))
                            loadScoreboard(App.PONG);
                        else if(text.equals( getResources().getString(R.string.snake)))
                            loadScoreboard(App.SNAKE);
                    }
                }
            });

        //  Seleziona la view Totalscore, situata alla prima posizione nel layout
        selectChip( chipsContainer.getChildAt(0));

        return view;
    }

    /**
     *      Cambia lo sfondo della view in modo da sembrare selezionato.
     *      @param view View da selezionare
     */
    private void selectChip(View view)
    {
        if(selectedView != null)
            selectedView.setBackground( getResources().getDrawable(R.drawable.roundedbutton_forleaderboard));

       view.setBackground( getResources().getDrawable(R.drawable.selected_chip_background));
       selectedView = view;
    }

    /**
     *      Carica la recyclerview con i punteggi relativi al gioco passato come parametro
     *      @param game Gioco di cui visualizzare la classifica
     */
    private void loadScoreboard(String game)
    {
        recyclerView.setAdapter(null);
        progressView.setVisibility(View.VISIBLE);
        positionTextview.setText("#");
        userScoreTextview.setText("-");

        /*
                Lettura degli score dal database
         */
        BackEndInterface.get().readAllScoresFirebase(game, (success, scoreboardList) ->
        {
            progressView.setVisibility(View.GONE);

            if(success && getActivity() != null)
                getActivity().runOnUiThread(() ->
                {
                    //  Aggiornamento recyclerview
                    adapter = new RecyclerViewAdapter(scoreboardList);
                    recyclerView.setAdapter(adapter);

                    if(AuthenticationManager.get().isUserLogged())
                        for(int i = 0; i < scoreboardList.size(); i++)
                            if(scoreboardList.get(i).getNickname().equals(
                                    AuthenticationManager.get().getUserLogged().getNickname()))
                            {
                                int position = i+1;
                                positionTextview.setText(String.format("#%d", position));

                                //  Update database
                                Scoreboard scoreboard = App.scoreboardDao.getScoreboard(game);
                                if(scoreboard == null)
                                {
                                    scoreboard = new Scoreboard(game, 0);
                                    scoreboard.setPosition(position);
                                    App.scoreboardDao.insertAll(scoreboard);
                                }
                                else
                                {
                                    scoreboard.setPosition(position);
                                    App.scoreboardDao.update(scoreboard);
                                }
                            }
                });
        });

        //  Legge lo score dell'utente dal database se è loggato
        if(AuthenticationManager.get().isUserLogged())
        {
            BackEndInterface.get().readScoreFirebase(game,
                    AuthenticationManager.get().getUserLogged().getNickname(),
                    (success, value) ->
                    {
                        if(success)
                        {
                            if(getActivity() != null)
                                getActivity().runOnUiThread(() ->
                                {
                                    //  Setta il testo della textview
                                    userScoreTextview.setText( Scoreboard.formatScore(value));

                                    //  Mostra lo score dell'utente se è nascosto
                                    if(cardProfile.getVisibility() == View.GONE)
                                    {
                                        cardProfile.setVisibility(View.VISIBLE);
                                        cardProfile.animate()
                                                .yBy(-cardProfile.getHeight())
                                                .setDuration(200)
                                                .setInterpolator(new DecelerateInterpolator())
                                                .setListener(null);
                                    }
                                });
                        }
                        else
                        {
                            //  Nasconde lo score dell'utente se non c'è in classifica
                            hideUserCard();
                        }
                    });
        }
        else
            hideUserCard(); //  Nasconde lo score dell'utente se non c'è in classifica
    }

    /**
     *      Nasconde la view con lo score dell'utente in basso nello schermo
     *      compiendo un'animazione.
     */
    private void hideUserCard()
    {
        if(cardProfile.getVisibility() == View.VISIBLE)
            if(getActivity() != null)
                getActivity().runOnUiThread(() ->
                        cardProfile.animate()
                                .yBy(cardProfile.getHeight())
                                .setDuration(200)
                                .setInterpolator(new DecelerateInterpolator())
                                .setListener(new Animator.AnimatorListener()
                                {
                                    public void onAnimationStart(Animator animator) { }
                                    @Override
                                    public void onAnimationEnd(Animator animator)
                                    {
                                        cardProfile.setVisibility(View.GONE);
                                    }
                                    public void onAnimationCancel(Animator animator) { }
                                    public void onAnimationRepeat(Animator animator) { }
                                }));
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

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(@NonNull UserScoreViewItem holder, int position)
        {
            holder.userScoreView.setPosition(position+1);
            holder.userScoreView.setViewWithScore( dataSet.get(position));

            //  Imposta il colore di sfondo a verde se questa riga è quella dell'utente loggato
            if(AuthenticationManager.get().isUserLogged())
                if(dataSet.get(position).getNickname().equals(
                        AuthenticationManager.get().getUserLogged().getNickname()))
                    holder.userScoreView.setBackgroundColor(Color.parseColor("#4c00aa00"));

            holder.userScoreView
                    .getProfileImageView()
                    .setOnTouchListener(new OnTouchAnimatedListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            //  Se non è stato cliccato l'utente loggato, apre la schermata profilo del giocatore
                            if( ! dataSet.get(position).getNickname().equals(
                                    AuthenticationManager.get().getUserLogged().getNickname()))
                            {
                                Intent intent = new Intent(getContext(), ProfileDisplayActivity.class);
                                intent.putExtra(ProfileDisplayActivity.USER_EXTRA, dataSet.get(position).getNickname());
                                startActivity(intent);
                            }
                        }
                    });
        }

        @Override
        public int getItemCount()
        {
            return dataSet.size();
        }
    }

    /**
     *      Adattamento della UserScoreView per la recyclerView
     */
    public class UserScoreViewItem extends RecyclerView.ViewHolder
    {
        UserScoreView userScoreView;

        public UserScoreViewItem(@NonNull View itemView)
        {
            super(itemView);
            userScoreView = (UserScoreView) itemView;
        }
    }

    /**
     *      Implementazione custom di {@link androidx.recyclerview.widget.RecyclerView.ItemDecoration}
     *      Disegna un drawable come divisore di items nella recyclerView
     */
    private class ItemDivider extends RecyclerView.ItemDecoration
    {
        private Drawable drawable;

        ItemDivider(Context context)
        {
            drawable = context.getResources().getDrawable(R.drawable.recyclerview_divider);
        }

        @Override
        public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state)
        {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++)
            {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + drawable.getIntrinsicHeight();

                drawable.setBounds(left, top, right, bottom);
                drawable.draw(c);
            }
        }
    }
}
