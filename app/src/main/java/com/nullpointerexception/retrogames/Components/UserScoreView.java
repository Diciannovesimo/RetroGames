package com.nullpointerexception.retrogames.Components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nullpointerexception.retrogames.R;

public class UserScoreView extends FrameLayout
{
    public UserScoreView(Context context)
    {
        super(context);
        setLayoutParams( new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView( inflate(context, R.layout.user_score_view_layout, null));
    }

    public UserScoreView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setLayoutParams( new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView( inflate(context, R.layout.user_score_view_layout, null));
    }

    public UserScoreView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        setLayoutParams( new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView( inflate(context, R.layout.user_score_view_layout, null));
    }

    public void setViewWithScore(Scoreboard score)
    {
        TextView nicknameTextview = findViewById(R.id.textView_profile_name);
        TextView scoreTextview = findViewById(R.id.scoreTextview);
        ImageView imageView = findViewById(R.id.profileImageView);

        nicknameTextview.setText(score.getNickname());
        scoreTextview.setText(score.getFormattedScore());

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
