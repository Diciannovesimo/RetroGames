package com.nullpointerexception.retrogames.Components;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * Clasee che rappresenta il database dello Scoreboard
 */
@Database(entities = {Scoreboard.class}, version = 1, exportSchema = false)
public abstract class DatabaseManager extends RoomDatabase {

    public abstract ScoreboardDao scoreboardDao();

}
