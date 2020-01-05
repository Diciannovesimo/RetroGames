package com.nullpointerexception.retrogames.Breakout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.nullpointerexception.retrogames.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Scoreboard extends AppCompatActivity {
    Button AddScoreButton, RemScoreButton;
    TextView LastScore, ScoreBoard, ScoreBoard2, ScoreBoard3, WinLose;
    EditText PlayerName, Scoreposition;
    List<String> PlayerList;
    String currentDate;
    SharedPreferences pref, prefs, prefsPlayer, prefsDate;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor date_editor;
    SharedPreferences.Editor player_editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard_breakout);
        PlayerList = new ArrayList<String>();
        prefs = getSharedPreferences("PlayerScore", MODE_PRIVATE);
        prefsPlayer = getSharedPreferences("PlayerNames", MODE_PRIVATE);
        prefsDate = getSharedPreferences("ScoreDate", MODE_PRIVATE);
        pref = getSharedPreferences("ShowButton", MODE_PRIVATE);
        editor = prefs.edit();
        date_editor = prefsDate.edit();
        player_editor = prefsPlayer.edit();

        Calendar calendar = Calendar.getInstance();
        currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        //Config des Widgets

        AddScoreButton =  findViewById(R.id.activity_score_addScore);
        RemScoreButton =  findViewById(R.id.activity_score_removescore);
        LastScore = (TextView) findViewById(R.id.activity_score_lastscore);
        WinLose = (TextView) findViewById(R.id.activity_score_winlose);
        ScoreBoard = (TextView) findViewById(R.id.activity_score_scoreboard);
        ScoreBoard2 = (TextView) findViewById(R.id.activity_score_scoreboard2);
        ScoreBoard3 = (TextView) findViewById(R.id.activity_score_scoreboard3);
        PlayerName = (EditText) findViewById(R.id.activity_score_playername);
        Scoreposition = (EditText) findViewById(R.id.activity_score_scoreposition);

        if(getIntent().getBooleanExtra("Lose", false)){
            WinLose.setText("Vous avez perdu...");
        }else{
            WinLose.setText("Vous avez gagné !!");
        }
        AddScoreButton.setVisibility(View.VISIBLE);
        PlayerName.setVisibility(View.VISIBLE);
        LastScore.setVisibility(View.VISIBLE);
        WinLose.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        if(!(intent.getBooleanExtra("ShowButton",true))){
            AddScoreButton.setVisibility(View.INVISIBLE);
            PlayerName.setVisibility(View.INVISIBLE);
            LastScore.setVisibility(View.INVISIBLE);
            WinLose.setVisibility(View.INVISIBLE);
        }
        RecupList();
        DisplayScore();
        LastScore.setText("Votre score : "+Integer.toString(prefs.getInt("LatestScore", 0)));
        //Sauvegarde du score actuel
        AddScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!PlayerName.getText().toString().isEmpty()){
                    if(PlayerList.size()==5){
                        addPlayer(PlayerName.getText().toString());
                        SortList();
                        removeLastPlayer();
                    }else{
                        addPlayer(PlayerName.getText().toString());
                        SortList();
                    }
                }
                DisplayScore();
                SaveList();
            }
        });
        RemScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = Scoreposition.getText().toString();
                if(s.charAt(0)>='1' && s.charAt(0)<='9') {
                    removePlayer(Integer.valueOf(s));
                    SortList();
                    DisplayScore();
                    SaveList();
                }
            }
        });

    }
    public void addPlayer(String n){
        int LatestScore = prefs.getInt("LatestScore", 0);
        boolean replace = false;
        for(int i =0; i<PlayerList.size(); i++){
            if(PlayerList.get(i).equals(n)){
                replace = true;
                if(prefs.getInt(PlayerList.get(i), 0) < LatestScore){
                    editor.putInt(n, LatestScore);
                    date_editor.putString(PlayerName.getText().toString(), currentDate);
                }
            }
        }
        if(replace==false){
            PlayerList.add(n);
            editor.putInt(n, LatestScore);
            date_editor.putString(PlayerName.getText().toString(), currentDate);
        }
        editor.apply(); date_editor.apply();

    }
    public void SortList(){
        boolean sorted = false;
        int taille = PlayerList.size();
        while(!sorted){
            sorted=true;
            for(int i=0; i<taille-1; i++){
                int val1=prefs.getInt(PlayerList.get(i),0);
                int val2=prefs.getInt(PlayerList.get(i+1),0);
                if(val1< val2){
                    String temp = PlayerList.get(i);
                    PlayerList.set(i, PlayerList.get(i+1));
                    PlayerList.set(i+1, temp);
                    sorted = false;
                }
            }
            taille--;
        }
    }
    public void removeLastPlayer(){
        PlayerList.remove(PlayerList.size()-1);
    }
    public void removePlayer(int i){
        if(i<=PlayerList.size()) {
            PlayerList.remove(i - 1);
        }
    }
    public void DisplayScore(){
        String chaine = "",chaine2 = "",chaine3 = "";
        String c,c2,c3;
        ScoreBoard.setText("");
        for(int i=0; i<PlayerList.size(); i++){
            if(PlayerList.get(i).isEmpty()){
                c = "---"+"\n";
                c2 = "---"+"\n";
                c3 = "---"+"\n";
            }else{
                c = PlayerList.get(i)+"\t\t\t\n";
                c2 = prefsDate.getString(PlayerList.get(i), "no_date")+"\t\t\t\n";
                c3 = prefs.getInt(PlayerList.get(i), 0)+"\n";
            }
            chaine += c;
            chaine2 += c2;
            chaine3 += c3;
        }
        ScoreBoard.setText(chaine);
        ScoreBoard2.setText(chaine2);
        ScoreBoard3.setText(chaine3);
    }
    public void SaveList(){
        player_editor.clear();
        player_editor.apply();
        for(int i=0; i<PlayerList.size(); i++){
            String key ="best"+Integer.toString(i+1);
            player_editor.putString(key, PlayerList.get(i));
            player_editor.apply();
        }
    }
    public void RecupList(){
        for(int i=0; i<5; i++){
            String key = "best"+Integer.toString(i+1);
            String Player = prefsPlayer.getString(key, "");
            if(!Player.isEmpty()) {
                PlayerList.add(Player);
            }
        }
    }
    public void Nettoyer(){
        editor.clear();date_editor.clear(); player_editor.clear();
        editor.apply();date_editor.apply(); player_editor.apply();
    }
}
