package com.tictactoe.tictactoe;

public record GameStateNew(GamePhase gamePhase, int[][] board, String winner) {
}
