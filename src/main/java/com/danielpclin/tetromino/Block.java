package com.danielpclin.tetromino;

import java.awt.Color;

public enum Block {
    NONE, I, O, J, L, S, T, Z;

    private Color color;

    static {
        NONE.color = Color.WHITE; // new Color(255, 255, 255, 0);
        I.color = Color.CYAN;
        O.color = Color.YELLOW;
        J.color = Color.BLUE;
        L.color = Color.ORANGE;
        S.color = Color.GREEN;
        T.color = new Color(128, 255, 128);
        Z.color = Color.RED;
    }

    public Color getColor() {
        return color;
    }
}
