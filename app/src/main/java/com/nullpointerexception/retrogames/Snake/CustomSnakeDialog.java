package com.nullpointerexception.retrogames.Snake;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.nullpointerexception.retrogames.R;

public class CustomSnakeDialog extends AppCompatDialogFragment {
    private TextView mEasy, mMedium, mHard;
    private int mDifficulty;

    private CustomSnakeDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_snake_dialog, null);

        mEasy   = view.findViewById(R.id.easy_diff);
        mMedium = view.findViewById(R.id.medium_diff);
        mHard   = view.findViewById(R.id.hard_diff);

        builder.setView(view);

        mEasy.setOnClickListener(easyView -> {
            mDifficulty = GameType.EASY;
            listener.applyDifficult(mDifficulty);
            dismiss();
        });

        mMedium.setOnClickListener(mediumView -> {
            mDifficulty = GameType.MEDIUM;
            listener.applyDifficult(mDifficulty);
            dismiss();
        });

        mHard.setOnClickListener(hardView -> {
            mDifficulty = GameType.HARD;
            listener.applyDifficult(mDifficulty);
            dismiss();
        });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (CustomSnakeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement CustomSnakeDialogListener");
        }
    }

    public interface CustomSnakeDialogListener {
        void applyDifficult(int difficulty);
    }
}


