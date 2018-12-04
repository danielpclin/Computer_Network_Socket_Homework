package com.danielpclin;

import com.danielpclin.helpers.Point;
import com.danielpclin.helpers.Vector;
import com.danielpclin.tetromino.Block;
import com.danielpclin.tetromino.Tetromino;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Arrays;

public class Tetris extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/gameLayout.fxml"));
        BorderPane root = loader.load();
        primaryStage.setTitle("Tetris");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/game.css").toExternalForm());
        GameController gameController = loader.getController();
        gameController.initializeStageEventListener(scene);
        gameController.startGame();
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
//        Tetromino tetromino1 = new Tetromino(Block.O, new Point(0, 0));
//        System.out.println(tetromino1);
//        Tetromino tetromino2 = tetromino1.rotateClockwise();
//        System.out.println(tetromino2);
//        System.out.println(Arrays.toString(tetromino2.getPoints()));
//        Board board = new Board();
//        board.printBoard();
//        for(Vector[][] tetromino : Tetromino.tetrominoShapeVector){
//            for(Vector[] vectors : tetromino){
//                int arr[][] = new int[5][5];
//                for (Vector vector : vectors){
//                    arr[2 - vector.getY()][vector.getX() + 2] = 5;
//                }
//                for (int[] i : arr) {
//                    for (int j : i){
//                        System.out.print(j + " ");
//                    }
//                    System.out.println();
//                }
//                System.out.println();
//            }
//        }
        launch(args);
    }
}
