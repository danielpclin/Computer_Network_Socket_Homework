package com.danielpclin.tetromino;

import javafx.scene.paint.Color;

public enum Block {
    NONE, I, O, J, L, S, T, Z;

    private Color color;

    static {
        NONE.color = Color.TRANSPARENT; // Color.WHITE;
        I.color = Color.CYAN;
        O.color = Color.YELLOW;
        J.color = Color.BLUE;
        L.color = Color.ORANGE;
        S.color = Color.GREEN;
        T.color = Color.PURPLE;
        Z.color = Color.RED;
    }

    public Color getColor() {
        return color;
    }
}
