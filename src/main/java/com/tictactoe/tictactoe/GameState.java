package com.tictactoe.tictactoe;


import java.util.Objects;

public record GameState(boolean gameOver, int [][] board, String winner) {
    public GameState{
        Objects.requireNonNull(board);
    }
}
