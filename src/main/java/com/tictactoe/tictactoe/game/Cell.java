package com.tictactoe.tictactoe.game;

public enum Cell {
    X("X"),
    O("O"),
    EMPTY("_");

    private String name;

    private Cell(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
