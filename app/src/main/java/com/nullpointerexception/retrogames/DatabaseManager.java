package com.nullpointerexception.retrogames;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.nullpointerexception.retrogames.Welcome.ScoreboardDao;

@Database(entities = {Scoreboard.class}, version = 1, exportSchema = false)
public abstract class DatabaseManager extends RoomDatabase {


    public abstract ScoreboardDao scoreboardDao();


}
