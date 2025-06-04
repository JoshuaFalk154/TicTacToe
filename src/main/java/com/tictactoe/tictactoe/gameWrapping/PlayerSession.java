package com.tictactoe.tictactoe.gameWrapping;

public record PlayerSession(String sessionId, String name, Assignment assignment) {
    public PlayerSession withAssignment(Assignment assignment) {
        return new PlayerSession(sessionId(), name(), assignment);
    }
}
