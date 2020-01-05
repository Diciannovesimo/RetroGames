package com.nullpointerexception.retrogames.Components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nullpointerexception.retrogames.R;

public class UserScoreView extends FrameLayout
{
    public UserScoreView(Context context)
    {
        super(context);
        addView( inflate(context, R.layout.user_score_view_layout, null));
    }

    public UserScoreView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        addView( inflate(context, R.layout.user_score_view_layout, null));
    }

    public UserScoreView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        addView( inflate(context, R.layout.user_score_view_layout, null));
    }

    public void setViewWithScore(Scoreboard score)
    {
        TextView nicknameTextview = findViewById(R.id.textView_profile_name);
        TextView scoreTextview = findViewById(R.id.scoreTextview);
        ImageView imageView = findViewById(R.id.profileImageView);

        nicknameTextview.setText(score.getNickname());
        scoreTextview.setText("" + score.getScore());

        User user = new User();
        user.setNickname(score.getNickname());

        new ProfileImageFetcher(getContext())
                .fetchImageOf(user, imageView::setImageDrawable);
    }

    public void setPosition(int position)
    {
        ((TextView) findViewById(R.id.positionTextView)).setText(String.format("#%d", position));
    }
}
