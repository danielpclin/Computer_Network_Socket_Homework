package com.danielpclin;

import com.danielpclin.helpers.Point;
import com.danielpclin.tetromino.Block;
import com.danielpclin.tetromino.Tetromino;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Arrays;

public class Tetris extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/gameLayout.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Tetris");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Tetromino tetromino1 = new Tetromino(Block.O, new Point(0, 0));
        System.out.println(tetromino1);
        Tetromino tetromino2 = tetromino1.rotateClockwise();
        System.out.println(tetromino2);
        System.out.println(Arrays.toString(tetromino2.getPoints()));
        Board board = new Board();
        board.printBoard();
        launch(args);
    }
}
