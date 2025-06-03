package com.tictactoe.tictactoe;

public record GameStateExtra(boolean gameOver, int [][] board, String winner, boolean waiting, boolean gameStarted) {
}
