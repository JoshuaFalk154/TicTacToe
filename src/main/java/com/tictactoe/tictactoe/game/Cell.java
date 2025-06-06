package com.tictactoe.tictactoe.game;

public enum Cell {
    EMPTY("_"),
    X("X"),
    O("O");

    private String name;

    private Cell(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
