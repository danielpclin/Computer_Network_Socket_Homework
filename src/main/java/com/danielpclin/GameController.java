package com.danielpclin;

import com.danielpclin.helpers.Point;
import com.danielpclin.tetromino.Block;
import com.danielpclin.tetromino.Tetromino;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import java.util.*;

public class GameController {

    @FXML
    BorderPane boarderPane;

    @FXML
    Canvas holdTetrominoCanvas;

    @FXML
    Canvas gameBoardCanvas;

    @FXML
    Canvas gameBoardGridCanvas;

    @FXML
    Canvas nextTetrominoCanvas;

    private static final int BLOCK_PIXEL_LENGTH = 30;

    private Scene scene;
    private Board gameBoard = new Board();
    private Tetromino tetromino = new Tetromino();
    private Block holdTetromino = Block.NONE;
    private ArrayList<Block> tetrominoPickQueue = new ArrayList<>();
    private GraphicsContext gameGraphicsContent;
    private GraphicsContext gameGridGraphicsContent;

    public void initialize() {
        gameGraphicsContent = gameBoardCanvas.getGraphicsContext2D();
        gameGraphicsContent.setFill(Color.GREEN);
        gameGraphicsContent.fillRect(20,20,40,40);

        // Draw grid on gameGridCanvas
        gameGridGraphicsContent = gameBoardGridCanvas.getGraphicsContext2D();
        gameGridGraphicsContent.setStroke(Color.BLACK);
        drawGrid();
    }

    public void startGame(){
        gameStart();
    }

    public void initializeStageEventListener(Scene scene){
        this.scene = scene;
        this.scene.setOnKeyReleased(e->{
//            System.out.println(e);
            switch (e.getCode()){
                case DOWN:
                case S:
                    // Move tetromino down
                    break;
                case LEFT:
                case A:
                    // Move tetromino left
                    break;
                case RIGHT:
                case D:
                    // Move tetromino right
                    break;
                case SPACE:
                    // Drop tetromino to bottom
                    break;
                case P:
                    gameBoard.setPaused(!gameBoard.isPaused());
                    break;
                default:
            }
            renderGame();
        });
    }

    private void gameStart(){
        Timer gameTimer = new Timer();
        gameTimer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                doGameCycle();
            }
        }, 0, 1000);
    }

    private void doGameCycle(){
        updateGame();
        renderGame();
//        Random random = new Random();
//        drawBlock(new Point(random.nextInt(10), random.nextInt(20)), Color.CYAN);//random.nextInt(10), random.nextInt(20)
    }

    private void drawGrid(){
        for(double x = 0.5; x < BLOCK_PIXEL_LENGTH * Board.BOARD_WIDTH + 1; x += BLOCK_PIXEL_LENGTH){
            gameGridGraphicsContent.strokeLine(x, 0, x, BLOCK_PIXEL_LENGTH * Board.BOARD_HEIGHT);
        }
        for(double y = 0.5; y < BLOCK_PIXEL_LENGTH * Board.BOARD_HEIGHT + 1; y += BLOCK_PIXEL_LENGTH){
            gameGridGraphicsContent.strokeLine(0, y, BLOCK_PIXEL_LENGTH * Board.BOARD_WIDTH, y);
        }
    }

    private void drawBlock(Point point, Color color){
        if (!color.equals(Color.TRANSPARENT)) {
            Platform.runLater(() -> {
                gameGraphicsContent.setFill(color);
                Point canvasPoint = convertPointToDraw(point);
                gameGraphicsContent.fillRect(canvasPoint.getX(), canvasPoint.getY(), BLOCK_PIXEL_LENGTH, BLOCK_PIXEL_LENGTH);
            });
        }

    }

    private Point convertPointToDraw(Point point){
        return new Point(point.getX() * BLOCK_PIXEL_LENGTH, (Board.BOARD_HEIGHT - (point.getY() + 1)) * BLOCK_PIXEL_LENGTH);
    }

    private void updateGame(){
        if(gameBoard.isPaused()){
            return;
        }
        oneLineDown();
    }

    private void renderGame(){
        // draw board
        Block[][] boardMap = gameBoard.getBoardMap();
        for (int height = 0; height < boardMap.length; height++){
            for (int width = 0; width < boardMap[height].length; width++){
                drawBlock(new Point(width, height), boardMap[height][width].getColor());
            }
        }
        // draw tetromino
        // TODO
    }

    public void oneLineDown(){

    }
}
