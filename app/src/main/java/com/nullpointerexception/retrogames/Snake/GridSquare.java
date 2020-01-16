package com.nullpointerexception.retrogames.Snake;

import android.graphics.Color;


public class GridSquare {
  private int mType;      //tipo elemento

  public GridSquare(int type) {
    mType = type;
  }

  public int getColor() {
    switch (mType) {
      case GameType.GRID:       //Posto vuoto
        return Color.WHITE;
      case GameType.FOOD:       //Cibo
        return Color.parseColor("#F36D6D");
      case GameType.SNAKE:      //Serpente
        return Color.parseColor("#0E630E");
    }
    return Color.WHITE;
  }

  public void setType(int type) {
    mType = type;
  }
}
