package com.nullpointerexception.retrogames;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.nullpointerexception.retrogames.Components.User;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM scoreboard")
    List<User> getAll();

    @Query("SELECT * FROM scoreboard WHERE id = :id")
    public User getUserById(String id);

    @Insert
    void insertAll(User... users);

    @Update
    void update(User... users);

    @Delete
    void delete(User user);
}
