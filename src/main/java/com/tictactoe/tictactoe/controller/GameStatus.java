package com.tictactoe.tictactoe.controller;

import com.tictactoe.tictactoe.gameSession.GameSessionState;
import com.tictactoe.tictactoe.gameSession.PlayerSession;

public record GameStatus(GameSessionState gameSessionState, Outcome outcome, PlayerSession playerOne,
                         PlayerSession playerTwo, PlayerSession playerTurn, PlayerSession winner, int[][] board) {
}
