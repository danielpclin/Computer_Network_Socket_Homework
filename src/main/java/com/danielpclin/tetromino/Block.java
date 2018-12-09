package com.danielpclin.tetromino;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

public enum Block {
    NONE, I, O, J, L, S, T, Z;

    private Color color;
    private String colorString;
    private Image image;

    static {
        NONE.color = Color.TRANSPARENT;
        I.color = Color.CYAN;
        O.color = Color.YELLOW;
        J.color = Color.BLUE;
        L.color = Color.ORANGE;
        S.color = Color.GREEN;
        T.color = Color.PURPLE;
        Z.color = Color.RED;
    }

    static {
        NONE.colorString = "transparent";
        I.colorString = "blue";
        O.colorString = "yellow";
        J.colorString = "deepblue";
        L.colorString = "orange";
        S.colorString = "green";
        T.colorString = "pink";
        Z.colorString = "red";
    }

    static {
        NONE.image = null;
        I.image = new Image(Block.class.getResource("/img/blue.png").toExternalForm());
        O.image = new Image(Block.class.getResource("/img/yellow.png").toExternalForm());
        J.image = new Image(Block.class.getResource("/img/deepblue.png").toExternalForm());
        L.image = new Image(Block.class.getResource("/img/orange.png").toExternalForm());
        S.image = new Image(Block.class.getResource("/img/green.png").toExternalForm());
        T.image = new Image(Block.class.getResource("/img/pink.png").toExternalForm());
        Z.image = new Image(Block.class.getResource("/img/red.png").toExternalForm());
    }

    public Color getColor() {
        return color;
    }

    public String getColorString() {
        return colorString;
    }

    public Image getImage() {
        return image;
    }

    public static final List<Block> PLACEABLE_BLOCKS = unmodifiableList(asList(I, O, J, L, S, T, Z));
}
