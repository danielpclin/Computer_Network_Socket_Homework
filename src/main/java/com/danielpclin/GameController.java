package com.danielpclin;

import com.danielpclin.helpers.Point;
import com.danielpclin.helpers.Vector;
import com.danielpclin.tetromino.Block;
import com.danielpclin.tetromino.Tetromino;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.*;

public class GameController {

    @FXML
    BorderPane boarderPane;

    @FXML
    Canvas holdTetrominoCanvas;

    @FXML
    Canvas gameBoardCanvas;

    @FXML
    Canvas tetrominoCanvas;

    @FXML
    Canvas gameBoardGridCanvas;

    @FXML
    Canvas nextTetrominoCanvas;

    private static final int BLOCK_PIXEL_LENGTH = 30;

    private Scene scene;
    private Board gameBoard = new Board();
    private Tetromino tetromino = new Tetromino();
    private Block holdTetromino = Block.NONE;
    private Block nextTetromino = Block.NONE;
    private ArrayList<Block> tetrominoPickQueue = new ArrayList<>();
    private GraphicsContext gameGraphicsContent;
    private GraphicsContext gameGridGraphicsContent;
    private Timer gameTimer = new Timer();
    private Timer delayLockTimer = new Timer();
    private Random random = new Random();
    private boolean updatingGame = false;

    public void initialize() {
        gameGraphicsContent = gameBoardCanvas.getGraphicsContext2D();

        // Draw grid on gameGridCanvas
        gameGridGraphicsContent = gameBoardGridCanvas.getGraphicsContext2D();
        gameGridGraphicsContent.setStroke(Color.BLACK);
        drawGrid();
    }

    public void startGame(){
        gameStart();
    }

    public void initializeSceneEventListener(Scene scene){
        this.scene = scene;
        this.scene.setOnKeyPressed(e-> {
            if (!updatingGame) {
                switch (e.getCode()) {
                    case DOWN:
                    case S:
                        tetrominoTryMoveDown(tetromino);
                        break;
                    case LEFT:
                    case A:
                        tetrominoTryMoveLeft(tetromino);
                        break;
                    case RIGHT:
                    case D:
                        tetrominoTryMoveRight(tetromino);
                        break;
                    case SPACE:
                        // Drop tetromino to bottom
                        break;
                    case C:
                        // HOLD
                        break;
                    case Z:
                        // Left Turn
                        break;
                    case X:
                        // Right Turn
                        break;
                    case P:
                        gameBoard.setPaused(!gameBoard.isPaused());
                        break;
                    default:
                }
                renderGame();
            }
        });
    }

    public void initilizeStageEventListener(Stage stage){
        stage.setOnCloseRequest(event -> {
            gameTimer.cancel();
            delayLockTimer.cancel();
        });
    }

    public void setMinimumConstraints(Stage stage){
        stage.setMinWidth(800);
        stage.setMinHeight(stage.getHeight());
    }

    private void gameStart(){
        gameTimer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                doGameCycle();
            }
        }, 0, 1000);
    }

    private void doGameCycle(){
        if (!gameBoard.isPaused()){
            updateGame();
            renderGame();
        }
    }

    private void clearCanvas(GraphicsContext gc){
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }

    private void drawGrid(){
        for(double x = 0.5; x < BLOCK_PIXEL_LENGTH * Board.BOARD_WIDTH + 1; x += BLOCK_PIXEL_LENGTH){
            gameGridGraphicsContent.strokeLine(x, 0, x, BLOCK_PIXEL_LENGTH * Board.BOARD_HEIGHT);
        }
        for(double y = 0.5; y < BLOCK_PIXEL_LENGTH * Board.BOARD_HEIGHT + 1; y += BLOCK_PIXEL_LENGTH){
            gameGridGraphicsContent.strokeLine(0, y, BLOCK_PIXEL_LENGTH * Board.BOARD_WIDTH, y);
        }
    }

    private void drawBlock(Point point, Color color, int block_length, GraphicsContext gc){
        if (!color.equals(Color.TRANSPARENT)) {
            Platform.runLater(() -> {
                gc.setFill(color);
                Point canvasPoint = convertPointToDraw(point);
                gc.fillRect(canvasPoint.getX(), canvasPoint.getY(), block_length, block_length);
            });
        }
    }

    private void drawBlock(Point point, Color color, GraphicsContext gc){
        drawBlock(point, color, BLOCK_PIXEL_LENGTH, gc);
    }

    private void drawBlock(Point point, Color color){
        drawBlock(point, color, gameGraphicsContent);
    }

    private Point convertPointToDraw(Point point, int block_length){
        return new Point((point.getX() - 1) * block_length, (Board.BOARD_HEIGHT - point.getY()) * block_length);
    }

    private Point convertPointToDraw(Point point){
        return convertPointToDraw(point, BLOCK_PIXEL_LENGTH);
    }

    private void drawTetromino(Tetromino tetromino){
        for (Point point : tetromino.getPoints()){
            drawBlock(point, tetromino.getBlock().getColor());
        }
    }

    private void drawBoard(Board gameBoard){
        Block[][] boardMap = gameBoard.getBoardMap();
        for (int width = 0; width < boardMap.length; width++){
            for (int height = 0; height < boardMap[width].length; height++){
                drawBlock(new Point(width + 1, height + 1), boardMap[width][height].getColor());
            }
        }
    }

    private void pickTetromino(){
        if (tetrominoPickQueue.size() == 0){
            tetrominoPickQueue.addAll(Block.PLACEABLE_BLOCKS);
        }
        if (nextTetromino.equals(Block.NONE)){
            nextTetromino = tetrominoPickQueue.remove(random.nextInt(tetrominoPickQueue.size()));
        }
        tetromino.setBlock(nextTetromino);
        nextTetromino = tetrominoPickQueue.remove(random.nextInt(tetrominoPickQueue.size()));
    }

    private void tetrominoTryMoveDown(Tetromino tetromino){
        if (gameBoard.testValidMove(tetromino.getDownPoints())){
            tetromino.moveDown();
        } else {
            delayLockTimer.schedule(new TimerTask(){
                            @Override
                            public void run() {
                                if (!gameBoard.testValidMove(tetromino.getDownPoints())){
                                    gameBoard.placeTetromino(tetromino);
                                    pickTetromino();
                                }
                            }
                        }, 750);
        }
    }

    private void tetrominoTryMoveRight(Tetromino tetromino){
        if (gameBoard.testValidMove(tetromino.getRightPoints())){
            tetromino.moveRight();
        }
    }

    private void tetrominoTryMoveLeft(Tetromino tetromino){
        if (gameBoard.testValidMove(tetromino.getLeftPoints())){
            tetromino.moveLeft();
        }
    }

    private void updateGame(){
        if(gameBoard.isPaused()){
            return;
        }
        updatingGame = true;
        if (tetromino.getBlock().equals(Block.NONE)){
            pickTetromino();
        }
        tetrominoTryMoveDown(tetromino);
        System.out.println(tetromino);
        updatingGame = false;
    }

    private void renderGame(){
        clearCanvas(gameGraphicsContent);
        drawBoard(gameBoard);
        drawTetromino(tetromino);
    }
}
