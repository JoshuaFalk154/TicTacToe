package com.tictactoe.tictactoe.game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TicTacToeTest {

    @Test
    public void TicTacToe_makeMove_allGood() {
        TicTacToe ticTacToe = new TicTacToe(new Player("p1", Assignment.X), new Player("p2", Assignment.O));

        ticTacToe.makeMove("p1", 1, 1);
    }

    @Test
    public void TicTacToe_makeMove_IllegalArgumentException() {
        TicTacToe ticTacToe = new TicTacToe(new Player("p1", Assignment.X), new Player("p2", Assignment.O));

        assertThrows(IllegalArgumentException.class, () -> {
            ticTacToe.makeMove("p1", 1, 4);
        });
    }

    @Test
    public void TicTacToe_makeMove_IllegalStateException_WrongState() {
        TicTacToe ticTacToe = new TicTacToe(new Player("p1", Assignment.X), new Player("p2", Assignment.O));

        ticTacToe.makeMove("p1", 0, 0);
        ticTacToe.makeMove("p2", 0, 1);

        ticTacToe.makeMove("p1", 1, 0);
        ticTacToe.makeMove("p2", 1, 1);

        ticTacToe.makeMove("p1", 2, 0);


        assertThrows(IllegalStateException.class, () -> {
            ticTacToe.makeMove("p1", 1, 1);
        });
    }

    @Test
    public void TicTacToe_makeMove_IllegalStateException_WrongPlayer() {
        TicTacToe ticTacToe = new TicTacToe(new Player("p1", Assignment.X), new Player("p2", Assignment.O));

        ticTacToe.makeMove("p1", 0, 0);

        assertThrows(IllegalStateException.class, () -> {
            ticTacToe.makeMove("p1", 1, 1);
        });
    }

    @Test
    public void TicTacToe_makeMove_IllegalStateException_CellUsed() {
        TicTacToe ticTacToe = new TicTacToe(new Player("p1", Assignment.X), new Player("p2", Assignment.O));

        ticTacToe.makeMove("p1", 0, 0);

        assertThrows(IllegalStateException.class, () -> {
            ticTacToe.makeMove("p1", 0, 0);
        });
    }

    @Test
    public void TicTacToe_isOver_true() {
        TicTacToe ticTacToe = new TicTacToe(new Player("p1", Assignment.X), new Player("p2", Assignment.O));

        ticTacToe.makeMove("p1", 0, 0);
        ticTacToe.makeMove("p2", 0, 1);

        ticTacToe.makeMove("p1", 1, 0);
        ticTacToe.makeMove("p2", 1, 1);

        ticTacToe.makeMove("p1", 2, 0);

        assertTrue(ticTacToe.isOver());
    }

    @Test
    public void TicTacToe_isOver_false() {
        TicTacToe ticTacToe = new TicTacToe(new Player("p1", Assignment.X), new Player("p2", Assignment.O));
        ticTacToe.makeMove("p1", 0, 0);

        assertTrue(!ticTacToe.isOver());
    }

    @Test
    public void TicTacToe_isTie_true() {
        TicTacToe ticTacToe = new TicTacToe(new Player("p1", Assignment.X), new Player("p2", Assignment.O));

        ticTacToe.makeMove("p1", 0, 2);
        ticTacToe.makeMove("p2",0 , 0);

        ticTacToe.makeMove("p1", 1, 0);
        ticTacToe.makeMove("p2", 0, 1);

        ticTacToe.makeMove("p1", 1, 1);
        ticTacToe.makeMove("p2", 1, 2);

        ticTacToe.makeMove("p1", 2, 1);
        ticTacToe.makeMove("p2", 2, 0);

        ticTacToe.makeMove("p1", 2, 2);

        assertTrue(ticTacToe.isTie());
    }

    @Test
    public void TicTacToe_getPlayerNameToMove_RightName() {
        TicTacToe ticTacToe = new TicTacToe(new Player("p1", Assignment.X), new Player("p2", Assignment.O));

        String expected = "p1";
        String result = ticTacToe.getPlayerNameToMove();
        assertEquals(expected, result);
    }

    @Test
    public void TicTacToe_getPlayerNameToMove_RightName_Moved() {
        TicTacToe ticTacToe = new TicTacToe(new Player("p1", Assignment.X), new Player("p2", Assignment.O));

        ticTacToe.makeMove("p1", 0, 2);

        String expected = "p2";
        String result = ticTacToe.getPlayerNameToMove();
        assertEquals(expected, result);
    }












}
