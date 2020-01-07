package com.nullpointerexception.retrogames.Snake;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.nullpointerexception.retrogames.R;

public class MainActivitySnake extends AppCompatActivity implements View.OnClickListener {

  private SnakePanelView mSnakePanelView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_snake);

    //Inizializza l'interfaccia grafica
    initUI();
  }

  /**
   * Inizializza l'interfaccia grafica impostando un listener al click su ongi pulsante
   */
  public void initUI() {
    mSnakePanelView = findViewById(R.id.snake_view);
    findViewById(R.id.left_btn).setOnClickListener(this);
    findViewById(R.id.right_btn).setOnClickListener(this);
    findViewById(R.id.top_btn).setOnClickListener(this);
    findViewById(R.id.bottom_btn).setOnClickListener(this);
    findViewById(R.id.start_btn).setOnClickListener(this);
  }

  /**
   * Quando un pulsante viene premuto, lo individua e cambia la direzione del serpente
   * @param v View di ogni bottone dell'interfaccia
   */
  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.left_btn:
        mSnakePanelView.setSnakeDirection(GameType.LEFT);
        break;
      case R.id.right_btn:
        mSnakePanelView.setSnakeDirection(GameType.RIGHT);
        break;
      case R.id.top_btn:
        mSnakePanelView.setSnakeDirection(GameType.TOP);
        break;
      case R.id.bottom_btn:
        mSnakePanelView.setSnakeDirection(GameType.BOTTOM);
        break;
      case R.id.start_btn:
        mSnakePanelView.reStartGame();
        break;
    }
  }
}
