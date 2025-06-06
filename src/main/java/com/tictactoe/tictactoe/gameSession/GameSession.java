package com.tictactoe.tictactoe.gameSession;

import com.tictactoe.tictactoe.game.Assignment;
import com.tictactoe.tictactoe.game.Cell;
import com.tictactoe.tictactoe.game.Player;
import com.tictactoe.tictactoe.game.TicTacToe;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Game Starts automatically if ready
 */
public class GameSession {

    private final String id;
    private Map<String, PlayerSession> playerSessionIdToPlayerSession = new HashMap<>();
    private TicTacToe ticTacToe;
    GameSessionState gameSessionState;

    public GameSession(String id) {
        this.id = id;
        gameSessionState = GameSessionState.WAITING_ON_PLAYERS;
    }

    public void joinGame(PlayerSession playerSession) {
        if (!gameSessionState.equals(GameSessionState.WAITING_ON_PLAYERS)) {
            throw new IllegalStateException("You can only join during waiting on players");
        }

        if (playerSessionIdToPlayerSession.size() >= 2) {
            throw new IllegalStateException("Already two players in session");
        }

        if (playerSessionIdToPlayerSession.get(playerSession.id()) != null) {
            throw new IllegalArgumentException("Player with session id already exists");
        }

        playerSessionIdToPlayerSession.values().stream()
                .map(PlayerSession::name)
                .filter(name -> name.equals(playerSession.id()))
                .findAny()
                .ifPresent(p -> {
                    throw new IllegalArgumentException("Player with name already exists");
                });


        playerSessionIdToPlayerSession.put(playerSession.id(), playerSession);

        if (playerSessionIdToPlayerSession.size() == 2) {
            startNewGame();
            gameSessionState = GameSessionState.RUNNING;
        }
    }

    public void takeMove(String playerSessionId, int row, int col) {
        if (!gameSessionState.equals(GameSessionState.RUNNING)) {
            throw new IllegalStateException("Can only take move during running game");
        }

        ticTacToe.makeMove(playerSessionId, row, col);
        if (ticTacToe.isOver()) {
            gameSessionState = GameSessionState.GAME_OVER;
        }
    }

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
        return Collections.unmodifiableList(playerSessionIdToPlayerSession.values().stream().toList());
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
