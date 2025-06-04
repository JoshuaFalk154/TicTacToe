package com.tictactoe.tictactoe;

import com.tictactoe.tictactoe.game.Cell;
import com.tictactoe.tictactoe.game.Player;
import com.tictactoe.tictactoe.gameWrapping.Assignment;
import com.tictactoe.tictactoe.gameWrapping.PlayerSession;
import org.springframework.stereotype.Service;

@Service
public class Mapper {

    public int[][] cellBoardToIntBoard(Cell[][] cellBoard) {
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

        return intBoard;
    }

    public Player playerSessionToPlayer(PlayerSession playerSession) {
        Cell cell = playerSession.assignment().equals(Assignment.X) ? Cell.X : Cell.O;
        return new Player(playerSession.sessionId(), cell);
    }

}
