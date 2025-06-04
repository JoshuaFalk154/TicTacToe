package com.tictactoe.tictactoe.game;

import java.util.Arrays;
import java.util.Optional;

public class Game {

    Cell[][] board;
    Player playerOne;
    Player playerTwo;
    Player playerToMove;


    public Game(Player playerOne, Player playerTwo) {
        setPlayers(playerOne, playerTwo);
        playerToMove = playerOne;
        initializeBoard();
    }

    private void setPlayers(Player p1, Player p2) {
        if (p1 == null || p2 == null || p1.equals(p2)) {
            throw new IllegalArgumentException("provide valid players");
        }

        if (p1.cell().equals(Cell.EMPTY) || p2.cell().equals(Cell.EMPTY)) {
            throw new IllegalArgumentException("players have invalid cell. Either X or O, not EMPTY");
        }

        if (p1.cell().equals(p2.cell())) {
            throw new IllegalArgumentException("players need different cells");
        }

        this.playerOne = p1;
        this.playerTwo = p2;
    }

    private void initializeBoard() {
        this.board = new Cell[3][3];
        for (Cell[] cells : board) {
            Arrays.fill(cells, Cell.EMPTY);
        }
    }

    public void makeMove(Player player, int row, int col) {
        if (!player.equals(playerToMove)) {
            throw new IllegalStateException("wrong player is making a move");
        }

        if (isOver()) {
            throw new IllegalStateException("game already over");
        }

        if (!board[row][col].equals(Cell.EMPTY)) {
            throw new IllegalStateException("cell already used");
        }

        board[row][col] = player.cell();
        updatePlayerToMove();
    }

    public Optional<Player> getWinner() {
        if (playerHasLine(playerOne)) return Optional.of(playerOne);
        if (playerHasLine(playerTwo)) return Optional.of(playerTwo);
        return Optional.empty();
    }

    public boolean isTie() {
        return getWinner().isEmpty() && boardIsFull();
    }


    public void updatePlayerToMove() {
        if (playerToMove.equals(playerOne)) {
            playerToMove = playerTwo;
        } else {
            playerToMove = playerOne;
        }
    }


    public boolean isOver() {
        return (boardIsFull() || atLeastOnePlayerHasLIne());
    }

    public boolean boardIsFull() {
        for (Cell[] cells : board) {
            for (Cell cell : cells) {
                if (cell.equals(Cell.EMPTY)) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean atLeastOnePlayerHasLIne() {
        return (playerHasLine(playerOne) || playerHasLine(playerTwo));
    }

    private boolean playerHasLine(Player player) {
        if (!player.equals(playerOne) & !player.equals(playerTwo)) {
            throw new IllegalArgumentException("Player is invalid");
        }

        Cell playerCell = player.cell();

        if (board[0][0].equals(playerCell) & board[0][1].equals(playerCell) & board[0][2].equals(playerCell))
            return true;
        if (board[1][0].equals(playerCell) & board[1][1].equals(playerCell) & board[1][2].equals(playerCell))
            return true;
        if (board[2][0].equals(playerCell) & board[2][1].equals(playerCell) & board[2][2].equals(playerCell))
            return true;

        if (board[0][0].equals(playerCell) & board[1][0].equals(playerCell) & board[2][0].equals(playerCell))
            return true;
        if (board[0][1].equals(playerCell) & board[1][1].equals(playerCell) & board[2][1].equals(playerCell))
            return true;
        if (board[0][2].equals(playerCell) & board[1][2].equals(playerCell) & board[2][2].equals(playerCell))
            return true;

        if (board[0][0].equals(playerCell) & board[1][1].equals(playerCell) & board[2][2].equals(playerCell))
            return true;
        if (board[0][2].equals(playerCell) & board[1][1].equals(playerCell) & board[2][0].equals(playerCell))
            return true;

        return false;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Cell[] cells : board) {
            for (int col = 0; col < cells.length; col++) {
                stringBuilder.append(cells[col].toString());
                if (col == cells.length - 1) {
                    stringBuilder.append("\n");
                }
            }
        }
        return stringBuilder.toString();
    }

    public Cell[][] getBoard() {
        return board;
    }

    public void setBoard(Cell[][] board) {
        this.board = board;
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(Player playerOne) {
        this.playerOne = playerOne;
    }

    public Player getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(Player playerTwo) {
        this.playerTwo = playerTwo;
    }

    public Player getPlayerToMove() {
        return playerToMove;
    }

    public void setPlayerToMove(Player playerToMove) {
        this.playerToMove = playerToMove;
    }
}
