package com.tictactoe.tictactoe;

import com.tictactoe.tictactoe.gameWrapping.GamePhase;

public record GameState(GamePhase gamePhase, int[][] board, String winner, String playersTurnPlayerSessionId) {
}
