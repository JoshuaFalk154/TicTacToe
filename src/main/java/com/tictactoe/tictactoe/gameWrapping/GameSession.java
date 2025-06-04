package com.tictactoe.tictactoe.gameWrapping;

import com.tictactoe.tictactoe.game.Cell;
import com.tictactoe.tictactoe.game.Game;
import com.tictactoe.tictactoe.game.Player;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class GameSession {

    String id;
    Map<String, PlayerSession> playerSessionIdToPlayerSession = new HashMap<>();
    Game game;
    GamePhase gamePhase;


    public GameSession(String lobbyId) {
        this.id = lobbyId;
        this.gamePhase = GamePhase.WAITING_FOR_PLAYERS;
    }

    public void addPlayer(String playerSessionId, String playerName) {
        if (playerSessionIdToPlayerSession.isEmpty()) {
            playerSessionIdToPlayerSession.put(playerSessionId, new PlayerSession(playerSessionId, playerName, null));
        } else if (playerSessionIdToPlayerSession.size() == 1) {
            if (playerSessionIdToPlayerSession.get(playerSessionId) != null) {
                throw new IllegalArgumentException("player with the following id already exists: " + playerSessionId);
            }

            Set<String> playerNames = playerSessionIdToPlayerSession.values().stream().map(PlayerSession::name).collect(Collectors.toSet());
            if (playerNames.contains(playerName)) {
                throw new IllegalArgumentException("player with the following name already exists: " + playerName);
            }

            playerSessionIdToPlayerSession.put(playerSessionId, new PlayerSession(playerSessionId, playerName, null));
        } else {
            throw new IllegalStateException("already two players exists");
        }

    }


    public void removePlayer(String playerSessionId) {
        if (gamePhase.equals(GamePhase.GAME_OVER)) {
            throw new IllegalStateException("try to remove player in invalid game-phase: " + gamePhase);
        }

        playerSessionIdToPlayerSession.remove(playerSessionId);
    }

    public Game startNewGame() {
        if (!canStartGame()) {
            throw new IllegalStateException("lobby not ready to start game");
        }

        this.gamePhase = GamePhase.IN_PROGRESS;

        List<PlayerSession> playerSessions = playerSessionIdToPlayerSession.values().stream().toList();
        PlayerSession player1 = playerSessions.get(0);
        PlayerSession player2 = playerSessions.get(1);

        player1 = player1.withAssignment(Assignment.X);
        player2 = player2.withAssignment(Assignment.O);
        playerSessionIdToPlayerSession.put(player1.sessionId(), player1);
        playerSessionIdToPlayerSession.put(player2.sessionId(), player2);


        this.game = new Game(playerSessionToPlayer(player1), playerSessionToPlayer(player2));
        return this.game;
    }

    public Game restartGame() {
        return startNewGame();
    }

    public boolean canStartGame() {
        return playerSessionIdToPlayerSession.size() == 2;
    }

    public void makeMove(String playerSessionId, int row, int col) {
        if (playerSessionIdToPlayerSession.get(playerSessionId) == null) {
            throw new IllegalArgumentException("No player in this game with the following player-session-id " + playerSessionId);
        }

        this.game.makeMove(playerSessionToPlayer(playerSessionIdToPlayerSession.get(playerSessionId)), row, col);
        if (this.game.isOver()) {
            this.gamePhase = GamePhase.GAME_OVER;
        }
    }

    public boolean isAvailable() {
        return playerSessionIdToPlayerSession.size() != 2;
    }

    public void resetGameSession() {
        gamePhase = GamePhase.WAITING_FOR_PLAYERS;
        game = null;
    }

    public PlayerSession getPlayerTurn() {
        if (!gamePhase.equals(GamePhase.IN_PROGRESS)) {
            throw new IllegalStateException("Game not in progress. No players turn!");
        }

        return playerSessionIdToPlayerSession.get(getGame().getPlayerToMove().name());
    }

    /**
     * returns winner if exists, else return null (tie)
     *
     * @return
     */
    public PlayerSession getWinner() {
        if (!gamePhase.equals(GamePhase.GAME_OVER)) {
            throw new IllegalStateException("Game not over. No winner!");
        }

        if (game.getWinner().isEmpty()) {
            return null;
        }

        return playerSessionIdToPlayerSession.get(game.getWinner().get().name());
    }

    public boolean isTie() {
        if (!gamePhase.equals(GamePhase.GAME_OVER)) {
            throw new IllegalStateException("Game not over. No winner!");
        }

        return game.isTie();
    }

    public List<PlayerSession> getPlayers() {
        return Collections.unmodifiableList(playerSessionIdToPlayerSession.values().stream().toList());
    }

    public Player playerSessionToPlayer(PlayerSession playerSession) {
        Cell cell = playerSession.assignment().equals(Assignment.X) ? Cell.X : Cell.O;
        return new Player(playerSession.sessionId(), cell);
    }
}


//package com.tictactoe.tictactoe;
//
//import com.tictactoe.tictactoe.game.Cell;
//import com.tictactoe.tictactoe.game.Game;
//import com.tictactoe.tictactoe.game.Player;
//import lombok.Getter;
//
//import java.util.Objects;
//
//@Getter
//public class GameSession {
//
//    String lobbyId;
//    String playerOne;
//    String playerTwo;
//    Game game;
//
//    public GameSession(String lobbyId) {
//        this.lobbyId = lobbyId;
//    }
//
//    public void addPlayer(String player) {
//        if (playerOne == null) {
//            playerOne = player;
//        } else if (playerTwo == null) {
//            playerTwo = player;
//        } else {
//            throw new IllegalStateException("already two players!");
//        }
//    }
//
//    public Game startNewGame() {
//        if (!canStartGame()) {
//            throw new IllegalStateException("lobby not ready to start game");
//        }
//
//        this.game = new Game(new Player(playerOne, Cell.X), new Player(playerTwo, Cell.O));
//        return this.game;
//    }
//
//    public boolean canStartGame() {
//        return (playerOne != null && playerTwo != null);
//    }
//
//    public void makeMove(String player, int row, int col) {
//        if (!Objects.equals(player, playerOne) && !Objects.equals(player, playerTwo)) {
//            throw new IllegalArgumentException("invalid player");
//        }
//
//        if (player.equals(playerOne)) {
//            this.game.makeMove(new Player(playerOne, Cell.X), row, col);
//        }
//
//        if (player.equals(playerTwo)) {
//            this.game.makeMove(new Player(playerTwo, Cell.O), row, col);
//        }
//    }
//
//}
