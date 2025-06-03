package com.tictactoe.tictactoe;

import com.tictactoe.tictactoe.game.Cell;
import com.tictactoe.tictactoe.game.Game;
import com.tictactoe.tictactoe.game.Player;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Getter
public class GameService {

    Map<String, GameSession> gameSessionIdToGameSession = new HashMap<>();

    public GameSession getGameSessionByGameSessionId(String gameSessionId) {
        return gameSessionIdToGameSession.get(gameSessionId);
    }

    public GameSession getAvailableGameSession() {
        Optional<GameSession> availableSession = gameSessionIdToGameSession.values().stream()
                .filter(GameSession::isAvailable)
                .findFirst();

        if (availableSession.isPresent()) {
            return availableSession.get();
        }

        String gameSessionId = UUID.randomUUID().toString();
        GameSession newGameSession = new GameSession(gameSessionId);
        gameSessionIdToGameSession.put(gameSessionId, newGameSession);
        return newGameSession;
    }


}
