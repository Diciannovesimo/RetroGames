package com.nullpointerexception.retrogames.Snake;

import android.graphics.Color;


public class GridSquare {
  private int mType;      //tipo elemento

  public GridSquare(int type) {
    mType = type;
  }

  public int getColor() {
    switch (mType) {
      case GameType.GRID:       //Posto vutoo
        return Color.WHITE;
      case GameType.FOOD:       //Cibo
        return Color.BLUE;
      case GameType.SNAKE:      //Serpente
        return Color.parseColor("#FF4081");
    }
    return Color.WHITE;
  }

  public void setType(int type) {
    mType = type;
  }
}
