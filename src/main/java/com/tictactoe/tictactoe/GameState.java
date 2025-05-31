package com.tictactoe.tictactoe;



public class GameState {

    boolean gameOver;
    int [][] board;

    public GameState(boolean gameOver, int[][] board) {
        this.gameOver = gameOver;
        this.board = board;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }
}
