package com.nullpointerexception.retrogames.Components;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ScoreboardDao {

    /**
     * Restituisce tutto il database
     * @return lista di scorboard con tutti i giochi
     */
    @Query("SELECT * FROM scoreboard")
    List<Scoreboard> getAll();

    /**
     *  Restituisce una stringa conente il nome del gioco, se presente
     * @param game stringa contenente il nome da cercare
     * @return il nome del gioco
     */
    @Query("SELECT game FROM scoreboard WHERE game = :game")
    String getGame(String game);

    /**
     *  Restituisce un intero contenente il punteggio ottenuto nel gioco, se presente
     * @param game stringa contenente il nome del gioco
     * @return il punteggio del gioco
     */
    @Query("SELECT score FROM scoreboard WHERE game = :game")
    int getScore(String game);

    /**
     * Inserisce nel database il nome del gioco o il totalscore e il suo relativo punteggio
     * @param scoreboards oggetto contenente il nome del gioco e il suo relativo punteggio
     */
    @Insert
    void insertAll(Scoreboard... scoreboards);

    /**
     * Modifica nel database il nome del gioco e il suo relativo punteggio
     * @param scoreboards oggetto contenente il nome del gioco e il suo relativo punteggio
     */
    @Update
    void update(Scoreboard... scoreboards);

    @Delete
    void delete(Scoreboard user);
}
