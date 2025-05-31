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



    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greet(HelloMessage message, @Header("simpSessionId") String sessionId) {
        return new Greeting("Hello, " +
                HtmlUtils.htmlEscape(message.getName()) + sessionId);
    }

    //
//    @MessageMapping("/start-game")
//    @SendTo("/topic/game")
//    public GameState startGame(@Header("simpSessionId") String sessionId) {
//
//
//        gameService.addPlayers(sessionId);
//
//        if (gameService.playersReady()) {
//            gameService.initializeGame();
//            Cell[][] cellBoard = gameService.getGame().getBoard();
//            int[][] intBoard = new int[3][3];
//
//            for (int row = 0; row < cellBoard.length; row++) {
//                for (int col = 0; col < cellBoard[row].length; col++) {
//                    Cell cell = cellBoard[row][col];
//                    int cellInt = 0;
//                    if (cell.equals(Cell.X)) cellInt = 1;
//                    if (cell.equals(Cell.O)) cellInt = 2;
//
//                    intBoard[row][col] = cellInt;
//                }
//            }
//            return new GameState(gameService.getGame().isOver(), intBoard);
//        }
//
//        return new GameState(false, new int[3][3]);
//    }

    @MessageMapping("/start-game")
    @SendTo("/topic/game")
    public GameState startGame(@Header("simpSessionId") String sessionId) {
        GameSession currentGameSession = gameServiceNew.getGameSession();
        currentGameSession.addPlayer(sessionId);

        if (currentGameSession.canStartGame()) {
            Game newGame = currentGameSession.startNewGame();
            Cell[][] cellBoard = newGame.getBoard();
            int[][] intBoard = new int[3][3];

            for (int row = 0; row < cellBoard.length; row++) {
                for (int col = 0; col < cellBoard[row].length; col++) {
                    Cell cell = cellBoard[row][col];
                    int cellInt = 0;
                    if (cell.equals(Cell.X)) cellInt = 1;
                    if (cell.equals(Cell.O)) cellInt = 2;

                    intBoard[row][col] = cellInt;
                }
            }
            return new GameState(newGame.isOver(), intBoard);
        }

        return new GameState(false, new int[3][3]);
    }


//    @MessageMapping("/take-move")
//    @SendTo("/topic/game")
//    public GameState takeMove(Move move, @Header("simpSessionId") String sessionId) {
//        gameService.makeMove(sessionId, move.row(), move.col());
//
//        Cell[][] cellBoard = gameService.getGame().getBoard();
//        int[][] intBoard = new int[3][3];
//
//        for (int row = 0; row < cellBoard.length; row++) {
//            for (int col = 0; col < cellBoard[row].length; col++) {
//                Cell cell = cellBoard[row][col];
//                int cellInt = 0;
//                if (cell.equals(Cell.X)) cellInt = 1;
//                if (cell.equals(Cell.O)) cellInt = 2;
//
//                intBoard[row][col] = cellInt;
//            }
//        }
//
//        return new GameState(gameService.getGame().isOver(), intBoard);
//    }

    @MessageMapping("/take-move")
    @SendTo("/topic/game")
    public GameState takeMove(Move move, @Header("simpSessionId") String sessionId) {
        GameSession currentGameSession = gameServiceNew.getGameSession();
        currentGameSession.makeMove(sessionId, move.row(), move.col());

        Cell[][] cellBoard = currentGameSession.getGame().getBoard();
        int[][] intBoard = new int[3][3];

        for (int row = 0; row < cellBoard.length; row++) {
            for (int col = 0; col < cellBoard[row].length; col++) {
                Cell cell = cellBoard[row][col];
                int cellInt = 0;
                if (cell.equals(Cell.X)) cellInt = 1;
                if (cell.equals(Cell.O)) cellInt = 2;

                intBoard[row][col] = cellInt;
            }
        }

        return new GameState(currentGameSession.getGame().isOver(), intBoard);
    }
}
