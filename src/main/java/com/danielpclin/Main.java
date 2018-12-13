package com.danielpclin;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
//        FXMLLoader loader = new FXMLLoader();
//        loader.setLocation(getClass().getResource("/fxml/gameLayout.fxml"));
//        BorderPane root = loader.load();
//        primaryStage.setTitle("Tetris");
//        Scene scene = new Scene(root);
//        scene.getStylesheets().add(getClass().getResource("/css/game.css").toExternalForm());
//        primaryStage.setScene(scene);
//        GameController gameController = loader.getController();
//        gameController.initializeSceneEventListener(scene);
//        gameController.initilizeStageEventListener(primaryStage);
//        primaryStage.show();
//        gameController.setMinimumConstraints(primaryStage);
//        gameController.startGame();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/gameMenu.fxml"));
        Pane root = loader.load();
        primaryStage.setTitle("Tetris");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/game.css").toExternalForm());
        primaryStage.setScene(scene);
//        GameController gameController = loader.getController();
//        gameController.initializeSceneEventListener(scene);
//        gameController.initilizeStageEventListener(primaryStage);
        primaryStage.show();
//        gameController.setMinimumConstraints(primaryStage);
//        gameController.startGame();
    }

    public static void main(String[] args) throws Exception{
//        Server server = new Server(12000);
//        (new Thread(server)).start();
//        Timer timer = new Timer(true);
//        timer.schedule(new TimerTask(){
//            @Override
//            public void run() {
//                try {
//                    server.broadcast("test");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, 0, 1000);
        launch(args);
//        Platform.exit();
    }
}
