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
    Map<String, Player> playerSessionIdToPlayer = new HashMap<>();
    Game game;
    GamePhase gamePhase;

    public GameSession(String lobbyId) {
        this.id = lobbyId;
        this.gamePhase = GamePhase.WAITING_FOR_PLAYERS;
    }

    public void addPlayer(String playerSessionId, String playerName) {
        if (playerSessionIdToPlayer.isEmpty()) {
            playerSessionIdToPlayer.put(playerSessionId, new Player(playerName, Cell.X));
        } else if (playerSessionIdToPlayer.size() == 1) {
            if (playerSessionIdToPlayer.get(playerSessionId) != null) {
                throw new IllegalArgumentException("player with the following id already exists: " + playerSessionId);
            }
            Set<String> playerNames = playerSessionIdToPlayer.values().stream().map(Player::name).collect(Collectors.toSet());
            if (playerNames.contains(playerName)) {
                throw new IllegalArgumentException("player with the following name already exists: " + playerName);
            }

            playerSessionIdToPlayer.put(playerSessionId, new Player(playerName, Cell.O));
        } else {
            throw new IllegalStateException("already two players exists");
        }
    }


    public void removePlayer(String playerSessionId) {
        if (gamePhase.equals(GamePhase.GAME_OVER)) {
            throw new IllegalStateException("try to remove player in invalid game-phase: " + gamePhase);
        }

        playerSessionIdToPlayer.remove(playerSessionId);
    }

    public Game startNewGame() {
        if (!canStartGame()) {
            throw new IllegalStateException("lobby not ready to start game");
        }


        this.gamePhase = GamePhase.IN_PROGRESS;

        // assign cells new, in case new player joins
        List<Map.Entry<String, Player>> list = playerSessionIdToPlayer.entrySet().stream().toList();
        Player player1 = list.get(0).getValue();
        Player player2 = list.get(1).getValue();

        Player newPlayer1 = new Player(player1.name(), Cell.X);
        Player newPlayer2 = new Player(player2.name(), Cell.O);

        playerSessionIdToPlayer.put(list.getFirst().getKey(), newPlayer1);
        playerSessionIdToPlayer.put(list.get(1).getKey(), newPlayer2);

        this.game = new Game(newPlayer1, newPlayer2);
        return this.game;
    }

    public Game restartGame() {
        return startNewGame();
    }

    public boolean canStartGame() {
        return playerSessionIdToPlayer.size() == 2;
    }

    public void makeMove(String playerSessionId, int row, int col) {
        if (playerSessionIdToPlayer.get(playerSessionId) == null) {
            throw new IllegalArgumentException("No player in this game with the following player-session-id " + playerSessionId);
        }

        this.game.makeMove(playerSessionIdToPlayer.get(playerSessionId), row, col);
        if (this.game.isOver()) {
            this.gamePhase = GamePhase.GAME_OVER;
        }
    }

    public boolean isAvailable() {
        return playerSessionIdToPlayer.size() != 2;
    }

    public void resetGameSession() {
        gamePhase = GamePhase.WAITING_FOR_PLAYERS;
        game = null;
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
