package com.tictactoe.tictactoe;

import com.tictactoe.tictactoe.game.Cell;
import com.tictactoe.tictactoe.game.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final GameServiceNew gameServiceNew;
    private final Mapper mapper;


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
    public GameState takeMove(Move move, @Header("simpSessionId") String sessionId) {
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
