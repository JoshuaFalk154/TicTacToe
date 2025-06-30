package com.tictactoe.tictactoe.controller;

import com.tictactoe.tictactoe.gameSession.GameService;
import com.tictactoe.tictactoe.gameSession.GameSession;
import com.tictactoe.tictactoe.gameSession.GameSessionState;
import com.tictactoe.tictactoe.gameSession.PlayerSession;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class GameController {

    private final GameService gameService;
    private final Mapper mapper;


    public GameStatus buildGameStatus(GameSession gameSession) {
        Objects.requireNonNull(gameSession);

        GameSessionState gameSessionState = gameSession.getGameSessionState();
        Outcome outcome = null;
        if (gameSessionState.equals(GameSessionState.GAME_OVER)) {
            boolean isTie = gameSession.isTie();
            outcome = gameSession.isTie() ? Outcome.TIE : Outcome.WINNER;
        }

        PlayerSession playerOne = null;
        PlayerSession playerTwo = null;
        PlayerSession playerTurn = null;

        if (gameSessionState.equals(GameSessionState.RUNNING)) {
            List<PlayerSession> players = gameSession.getPlayers();
            playerOne = players.getFirst();
            playerTwo = players.getLast();
            playerTurn = gameSession.getPlayerTurn();
        }

        int[][] board = null;
        if (gameSessionState.equals(GameSessionState.RUNNING) || gameSessionState.equals(GameSessionState.GAME_OVER)) {
            board = mapper.cellBoardToIntBoard(gameSession.getBoard());
        }

        PlayerSession winner = null;
        if (gameSessionState.equals(GameSessionState.GAME_OVER) && !gameSession.isTie()) {
            winner = gameSession.getWinner();
        }

        return new GameStatus(gameSessionState, outcome, playerOne, playerTwo, playerTurn, winner, board);
    }

    @GetMapping("/join-game")
    public String gameId(@RequestParam("simpSessionId") String sessionId) {
        GameSession gameSession = gameService.getAvailableGameSession();
        GameSessionState gameSessionState = gameSession.getGameSessionState();

        gameSession.joinGame(new PlayerSession(sessionId, sessionId, null));
        return gameSession.getId();
    }

    @MessageMapping("/take-move/{gameSessionId}")
    @SendTo("/topic/current-game/{gameSessionId}")
    public GameStatus takeMove(@DestinationVariable String gameSessionId, Move move, @Header("simpSessionId") String sessionId) {
        GameSession gameSession = gameService.getGameSessionByGameSessionId(gameSessionId);

        try {
            gameSession.takeMove(sessionId, move.row(), move.col());
        } catch (IllegalStateException e) {
            return null;
        }

        return buildGameStatus(gameSession);
    }


    @MessageMapping("/leave-game/{gameSessionId}")
    @SendTo("/topic/current-game/{gameSessionId}")
    public GameStatus leaveGame(@Header("simpSessionId") String sessionId, @DestinationVariable String gameSessionId) {
        GameSession gameSession = gameService.getGameSessionByGameSessionId(gameSessionId);

        try {
            gameSession.leaveGame(sessionId);
        } catch (Exception e) {
            return null;
        }

        return buildGameStatus(gameSession);
    }

    @MessageMapping("/restart-game/{gameSessionId}")
    @SendTo("/topic/current-game/{gameSessionId}")
    public GameStatus restartGame(@Header("simpSessionId") String sessionId, @DestinationVariable String gameSessionId) {
        GameSession gameSession = gameService.getGameSessionByGameSessionId(gameSessionId);

        try {
            gameSession.restartGame();
        } catch (Exception e) {
            return null;
        }

        return buildGameStatus(gameSession);
    }


    @MessageMapping("/trigger-game-update/{gameSessionId}")
    @SendTo("/topic/current-game/{gameSessionId}")
    public GameStatus gameUpdate(@Header("simpSessionId") String sessionId, @DestinationVariable String gameSessionId) {
        GameSession gameSession = gameService.getGameSessionByGameSessionId(gameSessionId);

        return buildGameStatus(gameSession);
    }


}
