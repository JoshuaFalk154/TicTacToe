package com.tictactoe.tictactoe;

import com.tictactoe.tictactoe.gameWrapping.GamePhase;
import com.tictactoe.tictactoe.gameWrapping.Outcome;
import com.tictactoe.tictactoe.gameWrapping.PlayerSession;



public record GameState(GamePhase gamePhase, int[][] board, PlayerSession winner, PlayerSession playerTurn,
                        PlayerSession playerOne, PlayerSession playerTwo, Outcome outcome) {
}
