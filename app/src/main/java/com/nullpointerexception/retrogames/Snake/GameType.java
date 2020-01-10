package com.nullpointerexception.retrogames.Snake;

public interface GameType {
  int GRID = 0;
  int FOOD = 1;
  int SNAKE = 2;

  int LEFT = 1;
  int TOP = 2;
  int RIGHT = 3;
  int BOTTOM = 4;

  int EASY = 1;
  int MEDIUM = 2;
  int HARD = 3;
  int GENERIC_DIFFICULTY = 4;

  int SNAKE_EAT = 0;
  int SNAKE_LOOSE = 1;
}
