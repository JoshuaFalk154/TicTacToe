package com.tictactoe.tictactoe;

import com.tictactoe.tictactoe.game.Cell;
import com.tictactoe.tictactoe.game.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

@RestController
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final GameServiceNew gameServiceNew;
    private final Mapper mapper;
    private final SimpMessagingTemplate simpMessagingTemplate;


    @GetMapping("/join-game")
    public String gameId(@RequestParam("simpSessionId") String sessionId) {
        GameSession gameSession = gameService.getAvailableGameSession();
        gameSession.addPlayer(sessionId, sessionId);
        if (gameSession.canStartGame()) {
            gameSession.startNewGame();
        }
        return gameSession.getId();
    }

    @MessageMapping("/trigger-game-update/{gameSessionId}")
    @SendTo("/topic/current-game/{gameSessionId}")
    public GameStateExtra gameUpdate(@Header("simpSessionId") String sessionId, @DestinationVariable String gameSessionId) {
        GameSession currentGameSession = gameService.getGameSessionByGameSessionId(gameSessionId);

        if (currentGameSession.canStartGame()) {
            Game newGame = currentGameSession.startNewGame();
            int[][] intBoard = mapper.cellBoardToIntBoard(newGame.getBoard());
            return new GameStateExtra(newGame.isOver(), intBoard, "", false, true);
        }

        return new GameStateExtra(false, new int[3][3], "", true, false);
    }


    @MessageMapping("/start-game/{gameSessionId}")
    @SendTo("/topic/current-game/{gameSessionId}")
    public GameState startGame(@DestinationVariable String gameSessionId) {
        GameSession currentGameSession = gameService.getGameSessionByGameSessionId(gameSessionId);

        if (currentGameSession.canStartGame()) {
            Game newGame = currentGameSession.startNewGame();
            int[][] intBoard = mapper.cellBoardToIntBoard(newGame.getBoard());
            return new GameState(newGame.isOver(), intBoard, "");
        }

        return new GameState(false, new int[3][3], "");
    }

    @MessageMapping("/init-game/{gameSessionId}")
    @SendTo("/topic/current-game/{gameSessionId}")
    public GameState joinGame(@Header("simpSessionId") String sessionId, @DestinationVariable String gameSessionId) {
        GameSession currentGameSession = gameService.getGameSessionByGameSessionId(gameSessionId);

        if (currentGameSession.canStartGame()) {
            Game newGame = currentGameSession.startNewGame();
            int[][] intBoard = mapper.cellBoardToIntBoard(newGame.getBoard());
            return new GameState(newGame.isOver(), intBoard, "");
        }

        return new GameState(false, new int[3][3], "");
    }



    @MessageMapping("/current-game/{gameSessionId}")
    @SendTo("/topic/current-game/{gameSessionId}")
    public GameState takeMove(@DestinationVariable String gameSessionId, Move move, @Header("simpSessionId") String sessionId) {
        GameSession currentGameSession = gameService.getGameSessionByGameSessionId(gameSessionId);

        currentGameSession.makeMove(sessionId, move.row(), move.col());

        int[][] intBoard = mapper.cellBoardToIntBoard(currentGameSession.getGame().getBoard());

        String winner = "";
        if (currentGameSession.getGame().isOver()) {
            winner = currentGameSession.getGame().getWinner().get().cell().toString();
        }

        return new GameState(currentGameSession.getGame().isOver(), intBoard, winner);
    }


    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greet(HelloMessage message, @Header("simpSessionId") String sessionId) {
        return new Greeting("Hello, " +
                HtmlUtils.htmlEscape(message.getName()) + sessionId);
    }


    @MessageMapping("/join-game")
    @SendTo("/topic/game")
    public GameState joinGame(@Header("simpSessionId") String sessionId) {
        GameSession currentGameSession = gameServiceNew.getGameSession();
        currentGameSession.addPlayer(sessionId, sessionId);

        if (currentGameSession.canStartGame()) {
            Game newGame = currentGameSession.startNewGame();
            int[][] intBoard = mapper.cellBoardToIntBoard(newGame.getBoard());
            return new GameState(newGame.isOver(), intBoard, "");
        }

        return new GameState(false, new int[3][3], "");
    }

    @MessageMapping("/restart-game")
    @SendTo("/topic/game")
    public GameState restartGame(@Header("simpSessionId") String sessionId) {
        GameSession currentGameSession = gameServiceNew.getGameSession();
        currentGameSession.restartGame();

        if (currentGameSession.canStartGame()) {
            Game newGame = currentGameSession.startNewGame();
            int[][] intBoard = mapper.cellBoardToIntBoard(newGame.getBoard());
            return new GameState(newGame.isOver(), intBoard, "");
        }

        return new GameState(false, new int[3][3], "");
    }


    @MessageMapping("/take-move")
    @SendTo("/topic/game")
    public GameState takeMove1(Move move, @Header("simpSessionId") String sessionId) {
        GameSession currentGameSession = gameServiceNew.getGameSession();
        currentGameSession.makeMove(sessionId, move.row(), move.col());

        int[][] intBoard = mapper.cellBoardToIntBoard(currentGameSession.getGame().getBoard());

        String winner = "";
        if (currentGameSession.getGame().isOver()) {
            winner = currentGameSession.getGame().getWinner().get().cell().toString();
        }

        return new GameState(currentGameSession.getGame().isOver(), intBoard, winner);
    }


}
