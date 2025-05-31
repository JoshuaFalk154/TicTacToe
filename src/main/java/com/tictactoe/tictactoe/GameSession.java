package com.tictactoe.tictactoe;

import com.tictactoe.tictactoe.game.Cell;
import com.tictactoe.tictactoe.game.Game;
import com.tictactoe.tictactoe.game.Player;
import lombok.Getter;

import java.util.Objects;

@Getter
public class GameSession {

    String lobbyId;
    String playerOne;
    String playerTwo;
    Game game;

    public GameSession(String lobbyId) {
        this.lobbyId = lobbyId;
    }

    public void addPlayer(String player) {
        if (playerOne == null) {
            playerOne = player;
        } else if (playerTwo == null) {
            playerTwo = player;
        } else {
            throw new IllegalStateException("already two players!");
        }
    }

    public Game startNewGame() {
        if (!canStartGame()) {
            throw new IllegalStateException("lobby not ready to start game");
        }

        this.game = new Game(new Player(playerOne, Cell.X), new Player(playerTwo, Cell.O));
        return this.game;
    }

    public boolean canStartGame() {
        return (playerOne != null && playerTwo != null);
    }

    public void makeMove(String player, int row, int col) {
        if (!Objects.equals(player, playerOne) && !Objects.equals(player, playerTwo)) {
            throw new IllegalArgumentException("invalid player");
        }

        if (player.equals(playerOne)) {
            this.game.makeMove(new Player(playerOne, Cell.X), row, col);
        }

        if (player.equals(playerTwo)) {
            this.game.makeMove(new Player(playerTwo, Cell.O), row, col);
        }
    }

}
