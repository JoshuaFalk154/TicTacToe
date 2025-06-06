package com.tictactoe.tictactoe.gameSession;

import com.tictactoe.tictactoe.game.Assignment;

public record PlayerSession(String id, String name, Assignment assignment) {

    public PlayerSession withAssignment(Assignment assignment) {
        return new PlayerSession(id(), name(), assignment);
    }
}
