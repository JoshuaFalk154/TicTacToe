package com.tictactoe.tictactoe;

import com.tictactoe.tictactoe.game.Cell;
import com.tictactoe.tictactoe.game.Game;
import com.tictactoe.tictactoe.game.Player;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Getter
public class GameService {

    Game game;
    Map<String, Player> userNameToPlayer = new HashMap<>();

    public void initializeGame() {
        List<Player> players = userNameToPlayer.values().stream().toList();

        this.game = new Game(players.get(0), players.get(1));
    }

    public void addPlayers(String playerName) {
        if (userNameToPlayer.isEmpty()) {
            userNameToPlayer.put(playerName, new Player(playerName, Cell.X));
        } else if (userNameToPlayer.size() == 1) {
            userNameToPlayer.put(playerName, new Player(playerName, Cell.O));
        } else {
            throw new IllegalStateException("already two players ready");
        }
    }

    public boolean playersReady() {
        return (userNameToPlayer.size() == 2);
    }

    public void makeMove(String playerName, int row, int col) {
        game.makeMove(userNameToPlayer.get(playerName), row, col);
    }

}
