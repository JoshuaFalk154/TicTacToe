package com.tictactoe.tictactoe.gameSession;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GameServiceTest {

    @Autowired


    @Test
    public void GameService_getAvailableGameSession_availableSession() {
        GameService gameService = new GameService();
        GameSession session = gameService.getAvailableGameSession();

        assertNotNull(session);
    }

}
