package com.nullpointerexception.retrogames.Components;

import android.content.Context;

import androidx.room.Room;

public class App
{
    /*
            Firebase string keys
     */
    public static final String PACMAN = "pacman";
    public static final String BREAKOUT = "breakout";
    public static final String PONG = "pong";
    public static final String SPACEINVADERS = "spaceInvaders";
    public static final String TETRIS = "tetris";
    public static final String TOTALSCORE = "totalscore";
    public static final String USER = "user";

    /*
            Shared Preferences
     */
    public static final String APP_VARIABLES = "app_variables";
    public static final String APP_FIRST_OPENING = "app_first_opening";
    public static final String APP_NO_USER_FINDED = "app_no_user_finded";


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
