package com.tictactoe.tictactoe.game;

import java.util.Objects;

public record Player(String name, Cell cell) {
    public Player {
        Objects.requireNonNull(name);
        Objects.requireNonNull(cell);

    }
}
