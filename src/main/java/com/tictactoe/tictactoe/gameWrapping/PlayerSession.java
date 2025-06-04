package com.tictactoe.tictactoe.gameWrapping;

public record PlayerSession(String playerSessionId, String playerName, Assignment assignment) {
    public PlayerSession withAssignment(Assignment assignment) {
        return new PlayerSession(playerSessionId(), playerName(), assignment);
    }
}
