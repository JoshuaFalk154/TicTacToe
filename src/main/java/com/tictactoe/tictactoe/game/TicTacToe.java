package com.tictactoe.tictactoe.game;

import java.util.*;

public class TicTacToe {

    private Map<String, Assignment> playerNameToAssignment = new HashMap<>();
    private Cell[][] board;
    private String playerNameToMove;
    private String winner;
    private GameState gameState;

    public TicTacToe(Player playerOne, Player playerTwo) {
        setPlayers(playerOne, playerTwo);
        playerNameToMove = playerOne.name();
        initializeBoard();
        gameState = GameState.RUNNING;
        winner = null;
    }


    /**
     * @param playerName The name of the player to move.
     * @param row
     * @param col
     * @throws java.lang.IllegalArgumentException If row or col is out of index.
     * @throws java.lang.IllegalStateException    If the method called in wrong state.
     * @throws java.lang.IllegalStateException    If wrong player is making a move.
     * @throws java.lang.IllegalStateException    If the cell is already used.
     */
    public void makeMove(String playerName, int row, int col) {
        Objects.requireNonNull(playerName);
        if (row >= board.length || col >= board[0].length) {
            throw new IllegalArgumentException("Row or col out of index");
        }

        if (!gameState.equals(GameState.RUNNING)) {
            throw new IllegalStateException("Can only make move during running game");
        }

        if (!playerName.equals(playerNameToMove)) {
            throw new IllegalStateException("Wrong player is making a move");
        }


        if (!board[row][col].equals(Cell.EMPTY)) {
            throw new IllegalStateException("cell already used");
        }


        board[row][col] = assignmentToCell(playerNameToAssignment.get(playerName));
        playerNameToMove = getPlayerNames().stream()
                .filter(p -> !p.equals(playerNameToMove))
                .toList()
                .getFirst();

        if (atLeastOnePlayerHasLine() || boardIsFull()) {
            gameState = GameState.GAME_OVER;
        }

        if (atLeastOnePlayerHasLine()) {
            List<String> playerNames = getPlayerNames();
            winner = playerHasLine(playerNames.getFirst()) ? playerNames.getFirst() : playerNames.get(1);
        }
    }

    /**
     * Sets players
     *
     * @param p1
     * @param p2
     * @throws IllegalArgumentException If p1 and p2 have same name or assignment (X/O)
     */
    private void setPlayers(Player p1, Player p2) {
        Objects.requireNonNull(p1);
        Objects.requireNonNull(p2);

        if (p1.name().equals(p2.name()) || p1.assignment().equals(p2.assignment())) {
            throw new IllegalArgumentException("Players are not allowed to have same name or same assignment(X/O)");
        }

        playerNameToAssignment.put(p1.name(), p1.assignment());
        playerNameToAssignment.put(p2.name(), p2.assignment());
    }

    private void initializeBoard() {
        this.board = new Cell[3][3];
        for (Cell[] cells : board) {
            Arrays.fill(cells, Cell.EMPTY);
        }
    }

    private boolean boardIsFull() {
        for (Cell[] cells : board) {
            for (Cell cell : cells) {
                if (cell.equals(Cell.EMPTY)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean atLeastOnePlayerHasLine() {
        List<String> playerNames = playerNameToAssignment.keySet().stream().toList();
        return (playerHasLine(playerNames.get(0)) || playerHasLine(playerNames.get(1)));
    }

    private boolean playerHasLine(String player) {
        if (playerNameToAssignment.get(player) == null) {
            throw new IllegalArgumentException("Player is invalid");
        }

        Cell playerCell = assignmentToCell(playerNameToAssignment.get(player));

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

//    private boolean playerHasLine(String player) {
//        if (playerNameToAssignment.get(player) == null) {
//            throw new IllegalArgumentException("Player is invalid");
//        }
//
//        Cell playerCell = assignmentToCell(playerNameToAssignment.get(player));
//
//        for (Cell[] cells : board) {
//            int count = 0;
//            for (int col = 0; col < board[0].length; col++) {
//                if (cells[col].equals(playerCell)) {
//                    count++;
//                }
//
//                if (col == board[0].length - 1 && count == board[0].length) {
//                    return true;
//                }
//            }
//        }
//
//        for (int col = 0; col < board[0].length; col++) {
//            int count = 0;
//            for (int row = 0; row < board.length; row++) {
//                if (board[row][col].equals(playerCell)) {
//                    count++;
//                }
//
//                if (row == board[0].length - 1 && count == board[0].length) {
//                    return true;
//                }
//            }
//        }
//
//        for (int row = 0; row < board.length; row++) {
//            int count = 0;
//            if (board[row][row].equals(playerCell)) {
//                count++;
//            }
//
//            if (row == board.length - 1 && count == board.length) {
//                return true;
//            }
//        }
//
//        int col = board.length-1;
//        int count = 0;
//        for (int row = 0; row < board.length; row++) {
//            if (board[row][col].equals(playerCell)) {
//                count++;
//            }
//
//            if (row == board.length-1 && count == board.length) {
//                return true;
//            }
//            col--;
//        }
//
//        return false;
//
//
//
////        if (board[0][0].equals(playerCell) & board[0][1].equals(playerCell) & board[0][2].equals(playerCell))
////            return true;
////        if (board[1][0].equals(playerCell) & board[1][1].equals(playerCell) & board[1][2].equals(playerCell))
////            return true;
////        if (board[2][0].equals(playerCell) & board[2][1].equals(playerCell) & board[2][2].equals(playerCell))
////            return true;
////
////        if (board[0][0].equals(playerCell) & board[1][0].equals(playerCell) & board[2][0].equals(playerCell))
////            return true;
////        if (board[0][1].equals(playerCell) & board[1][1].equals(playerCell) & board[2][1].equals(playerCell))
////            return true;
////        if (board[0][2].equals(playerCell) & board[1][2].equals(playerCell) & board[2][2].equals(playerCell))
////            return true;
////
////        if (board[0][0].equals(playerCell) & board[1][1].equals(playerCell) & board[2][2].equals(playerCell))
////            return true;
////        if (board[0][2].equals(playerCell) & board[1][1].equals(playerCell) & board[2][0].equals(playerCell))
////            return true;
////        return false;
//    }

    public List<String> getPlayerNames() {
        return playerNameToAssignment.keySet().stream().toList();
    }

    private Cell assignmentToCell(Assignment assignment) {
        return assignment.equals(Assignment.X) ? Cell.X : Cell.O;
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

    public String getWinner() {
        if (!gameState.equals(GameState.GAME_OVER)) {
            throw new IllegalStateException("Game not over yet");
        }
        return winner;
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean isOver() {
        return gameState.equals(GameState.GAME_OVER);
    }

    public boolean isTie() {
        if (!gameState.equals(GameState.GAME_OVER)) {
            throw new IllegalStateException("Game not over yet");
        }
        return winner == null;
    }


    public String getPlayerNameToMove() {
        if (!gameState.equals(GameState.RUNNING)) {
            throw new IllegalStateException("Game not running");
        }

        return playerNameToMove;
    }
}
