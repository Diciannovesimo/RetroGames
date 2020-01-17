package com.nullpointerexception.retrogames;

import android.content.Context;

import androidx.room.Room;

import com.nullpointerexception.retrogames.Components.DatabaseManager;
import com.nullpointerexception.retrogames.Components.ScoreboardDao;

public class App
{
    /*
            Firebase string keys
     */
    public static final String HOLE = "hole";
    public static final String BREAKOUT = "breakout";
    public static final String PONG = "pong";
    public static final String SNAKE = "snake";
    public static final String TETRIS = "tetris";
    public static final String TOTALSCORE = "totalscore";
    public static final String USER = "user";

    /*
            Shared Preferences
     */
    public static final String APP_VARIABLES = "app_variables";
    public static final String BREAKOUT_TUTORIAL = "breakout_tutorial";
    public static final String SNAKE_TUTORIAL = "snake_tutorial";
    public static final String APP_FIRST_OPENING = "app_first_opening";
    public static final String APP_NO_USER_FINDED = "app_no_user_finded";
    public static final String NICKNAME = "nickname";
    public static final String PREFS_INVALIDATE_FIREBASE_SCORES = "firebase_scores_invalidation";

    /*
            Room Database
     */
    public static DatabaseManager databaseManager;
    public static ScoreboardDao scoreboardDao;
    public static void initializeRoomDatabase(Context context) {
        databaseManager = Room.databaseBuilder(context,
                DatabaseManager.class, "scoreboard").allowMainThreadQueries().build();
        scoreboardDao = databaseManager.scoreboardDao();
    }
}
