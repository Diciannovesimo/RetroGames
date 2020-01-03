package com.nullpointerexception.retrogames;

import android.app.DatePickerDialog;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BackEndInterface{

    private static BackEndInterface instance = null;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;


    public BackEndInterface() {
    }


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
                        Log.d("writeFirebase", "Elemento non scritto");
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
                        Log.d("writeFirebase", "Elemento non scritto");
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
                long value = dataSnapshot.getValue(Long.class);
                if(listener != null)
                    listener.onDataReceived(true, value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                if(listener != null)
                    listener.onDataReceived(false, -1);
            }
        });

    }


    /**
     * Interfaccia usata per gestire le azioni da compiere
     * una volta ricevuto il dato da Firebase
     */
    public interface OnDataReceivedListener {
        void onDataReceived(boolean success, long value);
    }

}
