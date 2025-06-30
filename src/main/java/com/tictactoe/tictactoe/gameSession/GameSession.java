package com.tictactoe.tictactoe.gameSession;

import com.tictactoe.tictactoe.game.Assignment;
import com.tictactoe.tictactoe.game.Cell;
import com.tictactoe.tictactoe.game.Player;
import com.tictactoe.tictactoe.game.TicTacToe;

import java.util.*;

/**
 * Game Starts automatically if ready
 */
public class GameSession {

    private final String id;
    private final Map<String, PlayerSession> playerSessionIdToPlayerSession = new HashMap<>();
    private TicTacToe ticTacToe;
    GameSessionState gameSessionState;

    public GameSession(String id) {
        this.id = id;
        gameSessionState = GameSessionState.WAITING_ON_PLAYERS;
    }

    /**
     * Adds playerSession to the game session.
     * @param playerSession
     * @throws IllegalStateException If method called in wrong state.
     * @throws IllegalArgumentException If player already exists.
     */
    public void joinGame(PlayerSession playerSession) {
        if (!gameSessionState.equals(GameSessionState.WAITING_ON_PLAYERS)) {
            throw new IllegalStateException("You can only join during waiting on players");
        }

        playerSessionIdToPlayerSession.values().stream()
                .filter(ps -> ps.id().equals(playerSession.id()) || ps.name().equals(playerSession.name()))
                .findAny()
                .ifPresent(p -> {
                    throw new IllegalArgumentException("Player already exists");
                });

        playerSessionIdToPlayerSession.put(playerSession.id(), playerSession);

        if (playerSessionIdToPlayerSession.size() == 2) {
            startNewGame();
            gameSessionState = GameSessionState.RUNNING;
        }
    }

    /**
     * Takes move.
     * @param playerSessionId
     * @param row
     * @param col
     * @throws IllegalStateException If called in wrong state.
     * @throws java.lang.IllegalArgumentException If row or col is out of index.
     * @throws java.lang.IllegalStateException    If wrong player is making a move.
     * @throws java.lang.IllegalStateException    If the cell is already used.
     */
    public void takeMove(String playerSessionId, int row, int col) {
        if (!gameSessionState.equals(GameSessionState.RUNNING)) {
            throw new IllegalStateException("Can only take move during running game");
        }

        ticTacToe.makeMove(playerSessionId, row, col);
        if (ticTacToe.isOver()) {
            gameSessionState = GameSessionState.GAME_OVER;
        }
    }

    /**
     * Removes player from session
     * @param playerSessionId
     * @throws IllegalArgumentException If no player with playerSessionId in game session.
     */
    public void leaveGame(String playerSessionId) {
        if (playerSessionIdToPlayerSession.get(playerSessionId) == null) {
            throw new IllegalArgumentException("Player with the following session id is not part of the game: " + playerSessionId);
        }

        playerSessionIdToPlayerSession.remove(playerSessionId);
        if (gameSessionState.equals(GameSessionState.RUNNING) || gameSessionState.equals(GameSessionState.GAME_OVER)) {
            gameSessionState = GameSessionState.WAITING_ON_PLAYERS;
            resetGame();
        }
    }

    /**
     * Restarts game, if running.
     * @throws IllegalStateException If game is not over.
     */
    public void restartGame() {
        if (!gameSessionState.equals(GameSessionState.GAME_OVER)) {
            throw new IllegalStateException("Can only restart game in GAME_OVER state");
        }
        

        if (playerSessionIdToPlayerSession.size() != 2) {
            gameSessionState = GameSessionState.WAITING_ON_PLAYERS;
            resetGame();
        } else {
            startNewGame();
            gameSessionState = GameSessionState.RUNNING;
        }
    }

    private void startNewGame() {
        if (playerSessionIdToPlayerSession.size() != 2 && gameSessionState.equals(GameSessionState.WAITING_ON_PLAYERS)) {
            throw new IllegalStateException("Can not start new game");
        }

        List<PlayerSession> playerSessions = playerSessionIdToPlayerSession.values().stream().toList();

        PlayerSession playerSession1 = playerSessions.get(0);
        PlayerSession playerSession2 = playerSessions.get(1);

        playerSession1 = playerSession1.withAssignment(Assignment.X);
        playerSession2 = playerSession2.withAssignment(Assignment.O);
        playerSessionIdToPlayerSession.put(playerSession1.id(), playerSession1);
        playerSessionIdToPlayerSession.put(playerSession2.id(), playerSession2);

        ticTacToe = new TicTacToe(new Player(playerSession1.id(), playerSession1.assignment()), new Player(playerSession2.id(), playerSession2.assignment()));
    }

    private void resetGame() {
        ticTacToe = null;
    }

    public GameSessionState getGameSessionState() {
        return gameSessionState;
    }

    public boolean isTie() {
        return ticTacToe.isTie();
    }

    public List<PlayerSession> getPlayers() {
        return playerSessionIdToPlayerSession.values().stream().toList();
    }

    public PlayerSession getPlayerTurn() {
        if (!gameSessionState.equals(GameSessionState.RUNNING)) {
            throw new IllegalStateException("No players turn because no game is running");
        }
        return playerSessionIdToPlayerSession.get(ticTacToe.getPlayerNameToMove());
    }

    public PlayerSession getWinner() {
        if (!gameSessionState.equals(GameSessionState.GAME_OVER)) {
            throw new IllegalStateException("No winner if game not over");
        }

        return isTie() ? null : playerSessionIdToPlayerSession.get(ticTacToe.getWinner());
    }

    public String getId() {
        return id;
    }

    public Cell[][] getBoard() {
        if (gameSessionState.equals(GameSessionState.WAITING_ON_PLAYERS)) {
            throw new IllegalStateException("No board before game started");
        }

        return ticTacToe.getBoard();
    }
}
