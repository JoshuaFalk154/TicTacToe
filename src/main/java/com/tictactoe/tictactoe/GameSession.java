package com.tictactoe.tictactoe;

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

    public GameSession(String lobbyId) {
        this.id = lobbyId;
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

    public Game startNewGame() {
        if (!canStartGame()) {
            throw new IllegalStateException("lobby not ready to start game");
        }

        List<Player> players = playerSessionIdToPlayer.values().stream().toList();
        this.game = new Game(players.get(0), players.get(1));
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
    }

    public boolean isAvailable() {
        return playerSessionIdToPlayer.size() != 2;
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
