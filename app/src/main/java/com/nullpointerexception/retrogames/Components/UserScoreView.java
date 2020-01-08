package com.nullpointerexception.retrogames.Components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nullpointerexception.retrogames.R;

/**
 *      View che mostra una riga della lista della classifica
 */
public class UserScoreView extends FrameLayout
{
    private ImageView profileImageView;

    public UserScoreView(Context context)
    {
        super(context);
        initialize(context);
    }

    public UserScoreView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize(context);
    }

    public UserScoreView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    /**
            Inizializzazione della view, viene creato il layout interno
            @param context Context della view
     */
    public void initialize(Context context)
    {
        setLayoutParams( new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView( inflate(context, R.layout.user_score_view_layout, null));
        profileImageView = findViewById(R.id.profileImageView);
    }

    /**
     *      Imposta tutte le view dell'interfaccia grafica con i dati ricevuti come parametro
     *      @param score Record del database con i dati da visualizzare nella view
     */
    public void setViewWithScore(Scoreboard score)
    {
        TextView nicknameTextview = findViewById(R.id.textView_profile_name);
        TextView scoreTextview = findViewById(R.id.scoreTextview);

        nicknameTextview.setText(score.getNickname());
        scoreTextview.setText(score.getFormattedScore());

        new ProfileImageGenerator(getContext())
                .fetchImageOf(score.getNickname(), profileImageView::setImageDrawable);
    }

    /**
     *      Imposta la textview con la posizione passata come parametro
     *      @param position Posizione da mostrare
     */
    public void setPosition(int position)
    {
        ((TextView) findViewById(R.id.positionTextView)).setText(String.format("#%d", position));
    }

    public ImageView getProfileImageView()
    {
        return profileImageView;
    }
}
