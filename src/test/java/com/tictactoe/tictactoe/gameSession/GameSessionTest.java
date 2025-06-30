package com.tictactoe.tictactoe.gameSession;

import com.tictactoe.tictactoe.game.Assignment;
import com.tictactoe.tictactoe.game.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameSessionTest {

    @Test
    public void GameSession_joinGame_AllGood() {
        PlayerSession playerSession = new PlayerSession("idsome", "p1", Assignment.X);

        GameSession gameSession = new GameSession(UUID.randomUUID().toString());
        gameSession.joinGame(playerSession);

        List<PlayerSession> expected = List.of(playerSession);

        assertEquals(gameSession.getPlayers(), expected);
    }

    @Test
    public void GameSession_joinGame_IllegalStateException() {
        PlayerSession playerSession1 = new PlayerSession("idsome1", "p1", Assignment.X);
        PlayerSession playerSession2 = new PlayerSession("idsome2", "p2", Assignment.O);
        PlayerSession playerSession3 = new PlayerSession("idsome3", "p3", Assignment.O);

        GameSession gameSession = new GameSession(UUID.randomUUID().toString());
        gameSession.joinGame(playerSession1);
        gameSession.joinGame(playerSession2);

        assertThrows(IllegalStateException.class, () -> gameSession.joinGame(playerSession3));
    }

    @ParameterizedTest
    @MethodSource("provideAlreadyExistingUser")
    public void GameSession_joinGame_IllegalArgumentException_PlayerAlreadyExists(PlayerSession playerSession) {
        PlayerSession playerSession1 = new PlayerSession("idsome1", "p1", Assignment.X);
        GameSession gameSession = new GameSession(UUID.randomUUID().toString());
        gameSession.joinGame(playerSession1);

        assertThrows(IllegalArgumentException.class, () -> gameSession.joinGame(playerSession));
    }

    private static Stream<Arguments> provideAlreadyExistingUser() {
        return Stream.of(
                Arguments.of(new PlayerSession("idsome1", "p2", Assignment.O)),
                Arguments.of(new PlayerSession("idsome2", "p1", Assignment.O)),
                Arguments.of(new PlayerSession("idsome2", "p2", Assignment.X))
        );
    }

    @Test
    public void GameSession_takeMove_AllGood() {
        PlayerSession playerSession1 = new PlayerSession("idsome1", "p1", Assignment.X);
        PlayerSession playerSession2 = new PlayerSession("idsome2", "p2", Assignment.O);

        GameSession gameSession = new GameSession(UUID.randomUUID().toString());
        gameSession.joinGame(playerSession1);
        gameSession.joinGame(playerSession2);

        gameSession.takeMove(gameSession.getPlayerTurn().id(), 0, 0);
    }

    @Test
    public void GameSession_takeMove_IllegalStateException() {
        PlayerSession playerSession1 = new PlayerSession("idsome1", "p1", Assignment.X);
        GameSession gameSession = new GameSession(UUID.randomUUID().toString());
        gameSession.joinGame(playerSession1);

        assertThrows(IllegalStateException.class, () -> gameSession.takeMove(playerSession1.id(), 0, 0));
    }

    @Test
    public void GameSession_leaveGame_AllGood() {
        PlayerSession playerSession1 = new PlayerSession("idsome1", "p1", Assignment.X);
        PlayerSession playerSession2 = new PlayerSession("idsome2", "p2", Assignment.O);

        GameSession gameSession = new GameSession(UUID.randomUUID().toString());
        gameSession.joinGame(playerSession1);
        gameSession.joinGame(playerSession2);

        gameSession.leaveGame(playerSession1.id());
    }

    @Test
    public void GameSession_leaveGame_IllegalArgumentException() {
        PlayerSession playerSession1 = new PlayerSession("idsome1", "p1", Assignment.X);
        PlayerSession playerSession2 = new PlayerSession("idsome2", "p2", Assignment.O);

        GameSession gameSession = new GameSession(UUID.randomUUID().toString());
        gameSession.joinGame(playerSession1);

        assertThrows(IllegalArgumentException.class, () -> gameSession.leaveGame(playerSession2.id()));
    }

    @Test
    public void GameSession_restartGame_AllGood() {
        PlayerSession playerSession1 = new PlayerSession("idsome1", "p1", Assignment.X);
        PlayerSession playerSession2 = new PlayerSession("idsome2", "p2", Assignment.O);


        GameSession gameSession = new GameSession(UUID.randomUUID().toString());
        gameSession.joinGame(playerSession1);
        gameSession.joinGame(playerSession2);

        String firstPlayerId = gameSession.getPlayerTurn().id();
        String secondPlayerId = firstPlayerId.equals(playerSession1.id()) ? playerSession2.id() : playerSession1.id();

        gameSession.takeMove(firstPlayerId, 0, 0);
        gameSession.takeMove(secondPlayerId, 0, 1);

        gameSession.takeMove(firstPlayerId, 1, 0);
        gameSession.takeMove(secondPlayerId, 1, 1);

        gameSession.takeMove(firstPlayerId, 2, 0);
    }

    @Test
    public void GameSession_restartGame_IllegalStateException() {
        PlayerSession playerSession1 = new PlayerSession("idsome1", "p1", Assignment.X);
        PlayerSession playerSession2 = new PlayerSession("idsome2", "p2", Assignment.O);


        GameSession gameSession = new GameSession(UUID.randomUUID().toString());
        gameSession.joinGame(playerSession1);
        gameSession.joinGame(playerSession2);

        assertThrows(IllegalStateException.class, gameSession::restartGame);
    }
}
