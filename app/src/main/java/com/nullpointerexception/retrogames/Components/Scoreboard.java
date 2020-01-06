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
    private long score;

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

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public String getFormattedScore()
    {
        return formatScore( String.valueOf(score));
    }

    public static String formatScore(String s)
    {
        if(s.length() > 3)
            return formatScore( s.substring(0, s.length() - 3)) + "," + s.substring(s.length() - 3);
        else
            return s;
    }
}
