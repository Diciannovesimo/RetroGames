package com.nullpointerexception.retrogames.Components;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Scoreboard.class}, version = 1, exportSchema = false)
public abstract class DatabaseManager extends RoomDatabase {


    public abstract ScoreboardDao scoreboardDao();


}
