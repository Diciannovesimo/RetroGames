package com.nullpointerexception.retrogames.Components;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class BackEndInterface
{

    private static BackEndInterface instance = null;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;

    public BackEndInterface() { }

    public static synchronized BackEndInterface get() {
        if(instance==null)
            instance = new BackEndInterface();
        return instance;
    }

    /**
     * Scrive sul database di Firebase lo score ottenuto dall'utente in un determinato gioco
     * @param game stringa conentente il nome del gioco
     * @param nickname stringa contenente il nome del giocatore
     * @param score intero contenente il punteggio ottenuto dal giocatore
     * @param totalscore intero conente il punteggio complessivo ottenuto dal giocatore
     */
    public void writeScoreFirebase(String game, String nickname, int score, int totalscore) {

        //TODO Chiedere a Luca come fare per l'ordinamento su Firebase
        //Scrittura dello score di un singolo gioco
        myRef = database.getReference(game).child(nickname);
        myRef.setValue(score)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                    }
                 })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Log.d("writeScoreFirebase", "Elemento non scritto");
                    }
                });

        //Scrittura del nuovo totalscore
        myRef = database.getReference(App.TOTALSCORE).child(nickname);
        myRef.setValue(totalscore)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Log.d("writeScoreFirebase", "Elemento non scritto");
                    }
                });
    }

    /**
     * Legge dal database lo score ottenuto dall'utente in un determinato gioco o nella classifica globale
     * @param child stringa contenente il nodo dell'albero a cui si fa riferimento
     * @param nickname stringa contente il nome dell'utente da cui si vuole ricavare lo score
     * @param listener definizione delle operazioni da compiere una volta ricevuto il dato
     */
    public void readScoreFirebase(String child, String nickname, final OnDataReceivedListener listener) {
        myRef = database.getReference(child).child(nickname);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null)
                {
                    long value = dataSnapshot.getValue(Long.class);
                    if(listener != null)
                        listener.onDataReceived(true, String.valueOf(value));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                if(listener != null)
                    listener.onDataReceived(false, String.valueOf(-1));
            }
        });

    }

    /**
     * Legge dal database lo score ottenuto dall'utente in un determinato gioco o nella classifica globale
     * @param child stringa contenente il nodo dell'albero a cui si fa riferimento
     * @param listener definizione delle operazioni da compiere una volta ricevuto il dato
     */
    public void readAllScoresFirebase(String child, final OnQueryResultListener listener)
    {
        Query query = database.getReference(child).orderByValue();

        query.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot childSnapshot : dataSnapshot.getChildren())
                {
                    Scoreboard scoreboard = new Scoreboard();
                    scoreboard.setGame(child);
                    scoreboard.setNickname(childSnapshot.getKey());

                    long value = childSnapshot.getValue(Long.class);
                    scoreboard.setScore(value);

                    if(listener != null)
                        listener.onQueryResult(true, scoreboard);
                }
            }

            @Override
            public void onCancelled(DatabaseError error)
            {
                // Failed to read value
                if(listener != null)
                    listener.onQueryResult(false, null);
            }
        });

    }

    /**
     * Scrivi sul database il nickname dell'utente
     * @param email stringa contenente l'email che l'utente ha utilizzato per la registrazione
     * @param nickname stringa contenente il nickname scelto dall'utente durante la registrazione
     */
    public void writeUser(String email, String nickname) {
        String newEmail = changeChars(email,'.', '%');
        myRef = database.getReference(App.USER).child(newEmail);
        myRef.setValue(nickname)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Log.d("writeUserFirebase", "Elemento non scritto");
                    }
                });
    }

    /**
     * Legge sul database il nickname di un giocatore
     * @param email stringa contenente l'email dell'utente da cui si vuole recuperare la stringa
     * @param listener definizione delle operazioni da compiere una volta ricevuto il dato
     */
    public void readUser(String email, final OnDataReceivedListener listener) {
        String newEmail = changeChars(email,'.', '%');
        myRef = database.getReference(App.USER).child(newEmail);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nickname = dataSnapshot.getValue(String.class);
                if(listener != null)
                    listener.onDataReceived(true, nickname);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                if(listener != null)
                    listener.onDataReceived(true, "");
            }
        });
    }

    /**
     * Sostituisce un carattere della stringa con uno nuovo
     * @param input stringa in input da modificare
     * @param toRemove carattere da togliere
     * @param nuovo carattere nuovo da inserire al posto di quello da rimuovere
     * @return restiruisce la nuova stringa con i caratteri sostituiti
     */
    private String changeChars(String input, char toRemove, char nuovo) {
        String output = "";
        for(int i = 0; i < input.length(); i++){
            if(input.charAt(i) == toRemove)
                output += nuovo;
            else
                output += input.charAt(i);
        }
        return output;
    }

    /**
     * Interfaccia usata per gestire le azioni da compiere
     * una volta ricevuto il dato da Firebase
     */
    public interface OnDataReceivedListener {
        void onDataReceived(boolean success, String value);
    }

    /**
     * Interfaccia usata per gestire le azioni da compiere
     * una volta ricevuto il risultato di una query assegnata a Firebase
     */
    public interface OnQueryResultListener {
        void onQueryResult(boolean success, Scoreboard scoreboard);
    }

}
