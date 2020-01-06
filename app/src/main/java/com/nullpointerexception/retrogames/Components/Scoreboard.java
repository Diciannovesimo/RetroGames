package com.nullpointerexception.retrogames.Components;


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

    private String nickname;

    public Scoreboard() { }

    public Scoreboard(String game, int score) {
        this.game = game;
        this.score = score;
    }

    public String getNickname()
    {
        return nickname;
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
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
