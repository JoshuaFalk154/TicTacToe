package com.tictactoe.tictactoe.gameSession;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Getter
public class GameService {

    Map<String, GameSession> gameSessionIdToGameSession = new HashMap<>();

    public GameSession getGameSessionByGameSessionId(String gameSessionId) {
        return gameSessionIdToGameSession.get(gameSessionId);
    }

    /**
     * Gets and available game session.
     * @return Available game session.
     */
    public GameSession getAvailableGameSession() {
        Optional<GameSession> availableSession = gameSessionIdToGameSession.values().stream()
                .filter(gameSession -> gameSession.getGameSessionState().equals(GameSessionState.WAITING_ON_PLAYERS))
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
