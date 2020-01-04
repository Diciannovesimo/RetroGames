package com.nullpointerexception.retrogames;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.nullpointerexception.retrogames.Components.User;

@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class DatabaseManager extends RoomDatabase {


    public abstract UserDao userDao();
    /*
    private static DatabaseManager INSTANCE;

    public abstract User userModel();


    public static DatabaseManager getInMemoryDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.inMemoryDatabaseBuilder(context.getApplicationContext(), DatabaseManager.class)
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }



    public static DatabaseManager getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DatabaseManager.class, "user_db")
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }



    public static void destroyInstance() {
        INSTANCE = null;
    }

     */


}
