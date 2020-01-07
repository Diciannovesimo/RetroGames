package com.nullpointerexception.retrogames.Components;

import android.content.Context;
import android.content.SharedPreferences;

import com.nullpointerexception.retrogames.App;

public class SaveScore {

    private String nameGame;
    private long newScore;


    public SaveScore(){
    }


    /**
         * Salva lo score del gioco sia su database locale sia sul database di firebase
         * e aggiorna automaticamente il nuovo totalscore.
         * @param nameGame stringa contenente il nome del gioco
         * @param newScore intero contenente il punteggio del gioco
         * @param context contesto
     */
    public void save(String nameGame, int newScore, Context context){
        this.nameGame = nameGame;
        this.newScore = newScore;
        long newTotalscore;


        if(App.scoreboardDao.getGame(nameGame) != null) //Controllo se il gioco è presente nel database
        {
            int previousScore = App.scoreboardDao.getScore(nameGame); //Prendo il valore dal database locale

            if( newScore > previousScore ) //Controllo se il nuovo punteggio è maggiore rispetto a quello memorizzato
            {
                //Scrivo il nuovo punteggio sul database locale
                App.scoreboardDao.update(new Scoreboard(nameGame, newScore));


                if(App.scoreboardDao.getGame(App.TOTALSCORE) != null) //Controllo se già esiste un totalscore
                {
                    //Esiste già un totalscore
                    newTotalscore = App.scoreboardDao.getScore(App.TOTALSCORE); //Leggo il vecchio totalscore
                    newTotalscore = previousScore - newTotalscore + newScore;   //Determino il nuovo totalscore
                    newTotalscore = Math.abs(newTotalscore);
                    App.scoreboardDao.update(new Scoreboard(App.TOTALSCORE, newTotalscore));   //Scrivo il nuovo totalscore
                }
                else
                {
                    //Non esiste un totalscore
                    newTotalscore = 0;
                    newTotalscore = previousScore - newTotalscore + newScore;   //Determino il nuovo totalscore
                    newTotalscore = Math.abs(newTotalscore);
                    App.scoreboardDao.insertAll(new Scoreboard(App.TOTALSCORE, newTotalscore));   //Scrivo il nuovo totalscore
                }

                /**
                //Scrivo il nuovo totalscore sulle SharedPreferences
                SharedPreferences totalscoreShared = context.getSharedPreferences(App.TOTALSCORE, Context.MODE_PRIVATE);
                newTotalscore = totalscoreShared.getLong(App.TOTALSCORE, 0);  //Leggo il vecchio totalscore
                newTotalscore = previousScore - newTotalscore + newScore;    //Determino il nuovo totalscore
                totalscoreShared.edit().putLong(App.TOTALSCORE, newTotalscore).apply();   //Scrivo il nuovo totalscore
                 **/

                //Controllo se l'utente è loggato con firebase
                if(AuthenticationManager.get().isUserLogged())
                {
                    //Prendo il nickname dell'utente loggato
                    SharedPreferences nicknameShared = context.getSharedPreferences(App.USER, 0);
                    String nickname = nicknameShared.getString(App.NICKNAME, "error");

                    //Scrivo il nuovo punteggio e il nuovo totalscore sul database firebase
                    BackEndInterface.get().writeScoreFirebase(nameGame,nickname, newScore, newTotalscore);
                }
            }

        }
        else    //Non è presente uno score con questo gioco
        {
            //Scrivo il nuovo punteggio sul database locale
            App.scoreboardDao.insertAll(new Scoreboard(nameGame, newScore));


            if(App.scoreboardDao.getGame(App.TOTALSCORE) != null) //Controllo se già esiste un totalscore
            {
                //Esiste già un totalscore
                newTotalscore = App.scoreboardDao.getScore(App.TOTALSCORE); //Leggo il vecchio totalscore
                newTotalscore = newTotalscore + newScore;   //Determino il nuovo totalscore
                newTotalscore = Math.abs(newTotalscore);
                App.scoreboardDao.update(new Scoreboard(App.TOTALSCORE, newTotalscore));   //Scrivo il nuovo totalscore
            }
            else
            {
                newTotalscore = 0;
                newTotalscore = newTotalscore + newScore;   //Determino il nuovo totalscore
                newTotalscore = Math.abs(newTotalscore);
                App.scoreboardDao.insertAll(new Scoreboard(App.TOTALSCORE, newTotalscore));   //Scrivo il nuovo totalscore
            }


            /**
            //Scrivo il nuovo totalscore sulle SharedPreferences
            SharedPreferences totalscoreShared = context.getSharedPreferences(App.TOTALSCORE, Context.MODE_PRIVATE);
            newTotalscore = totalscoreShared.getLong(App.TOTALSCORE, 0);  //Leggo il vecchio totalscore
            newTotalscore = newTotalscore + newScore;    //Determino il nuovo totalscore
            totalscoreShared.edit().putLong(App.TOTALSCORE, newTotalscore).apply();   //Scrivo il nuovo totalscore
             **/

            //Controllo se l'utente è loggato con firebase
            if(AuthenticationManager.get().isUserLogged())
            {
                //Prendo il nickname dell'utente loggato
                SharedPreferences nicknameShared = context.getSharedPreferences(App.USER, 0);
                String nickname = nicknameShared.getString(App.NICKNAME, "error");

                //Scrivo il nuovo punteggio e il nuovo totalscore sul database firebase
                BackEndInterface.get().writeScoreFirebase(nameGame,nickname, newScore, newTotalscore);
            }
        }
    }

}
