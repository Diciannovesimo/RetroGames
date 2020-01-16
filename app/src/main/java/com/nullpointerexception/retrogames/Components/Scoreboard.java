package com.nullpointerexception.retrogames.Components;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "scoreboard")
public class Scoreboard {

    @PrimaryKey @NonNull
    @ColumnInfo(name = "game")
    private String game;    //Nome del gioco

    @ColumnInfo(name = "score")
    private long score;     //Punteggio oggenuto nel gioco

    @ColumnInfo(name = "totalscore")
    private long totalscore;    //Totalscore

    @ColumnInfo(name = "position")
    private int position;   //Posizione dell'utente

    private String nickname;    //nickname dell'utente

    @Ignore
    public Scoreboard() { }

    public Scoreboard(String game, long score) {
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

    public long getTotalscore() {
        return totalscore;
    }

    public void setTotalscore(long totalscore) {
        this.totalscore = totalscore;
    }

    public String getFormattedScore()
    {
        return formatScore( String.valueOf(score));
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public static String formatScore(String s)
    {
        if(s.length() > 3)
            return formatScore( s.substring(0, s.length() - 3)) + "," + s.substring(s.length() - 3);
        else
            return s;
    }
}
