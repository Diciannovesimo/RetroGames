package com.nullpointerexception.retrogames;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "scoreboard")
public class Scoreboard {

    @PrimaryKey @NonNull
    @ColumnInfo(name = "game")
    private String game;

    @ColumnInfo(name = "score")
    private int score;


    public Scoreboard(String game, int score) {
        this.game = game;
        this.score = score;
    }


    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
