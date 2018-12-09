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
import javafx.scene.image.Image;
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
    private boolean canHold = true;
    private Block nextTetromino = Block.NONE;
    private ArrayList<Block> tetrominoPickQueue = new ArrayList<>();
    private GraphicsContext gameGraphicsContent;
    private GraphicsContext gameGridGraphicsContent;
    private GraphicsContext nextGraphicsContent;
    private GraphicsContext holdGraphicsContent;
    private Timer gameTimer = new Timer();
    private Random random = new Random();
    private boolean isPaused = false;

    public void initialize() {
        gameGraphicsContent = gameBoardCanvas.getGraphicsContext2D();
        holdGraphicsContent = holdTetrominoCanvas.getGraphicsContext2D();
        nextGraphicsContent = nextTetrominoCanvas.getGraphicsContext2D();

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
            switch (e.getCode()) {
                case DOWN:
                case S:
                    tetrominoTryMoveDown();
                    break;
                case LEFT:
                case A:
                    tetrominoTryMoveLeft();
                    break;
                case RIGHT:
                case D:
                    tetrominoTryMoveRight();
                    break;
                case SPACE:
                    tetrominoHardDrop();
                    break;
                case SHIFT:
                case C:
                    tetrominoTryHold();
                    break;
                case Z:
                    tetrominoTryCounterClockwise();
                    break;
                case X:
                case UP:
                    tetrominoTryClockwise();
                    break;
                case P:
                    isPaused = !isPaused;
                    break;
                default:
            }
            renderGame();

        });
    }

    public void initilizeStageEventListener(Stage stage){
        stage.setOnCloseRequest(event -> {
            gameTimer.cancel();
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
        if (!isPaused){
            updateGame();
            renderGame();
        }
    }

    private void clearCanvas(GraphicsContext gc){
        Platform.runLater(() -> {
            gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        });
    }

    private void drawGrid(){
        for(double x = 0.5; x < BLOCK_PIXEL_LENGTH * Board.BOARD_WIDTH + 1; x += BLOCK_PIXEL_LENGTH){
            gameGridGraphicsContent.strokeLine(x, 0, x, BLOCK_PIXEL_LENGTH * Board.BOARD_HEIGHT);
        }
        for(double y = 0.5; y < BLOCK_PIXEL_LENGTH * Board.BOARD_HEIGHT + 1; y += BLOCK_PIXEL_LENGTH){
            gameGridGraphicsContent.strokeLine(0, y, BLOCK_PIXEL_LENGTH * Board.BOARD_WIDTH, y);
        }
    }

    private void drawBlock(Point point, Image image, int block_length, GraphicsContext gc){
        if (image!=null) {
            Platform.runLater(() -> {
                Point canvasPoint = convertPointToDraw(point);
                gc.drawImage(image, canvasPoint.getX(), canvasPoint.getY());
            });
        }
    }

    private void drawBlock(Point point, Image image, GraphicsContext gc){
        drawBlock(point, image, BLOCK_PIXEL_LENGTH, gc);
    }

    private void drawBlock(Point point, Image image){
        drawBlock(point, image, gameGraphicsContent);
    }

    private Point convertPointToDraw(Point point, int block_length){
        return new Point((point.getX() - 1) * block_length, (Board.BOARD_HEIGHT - point.getY()) * block_length);
    }

    private Point convertPointToDraw(Point point){
        return convertPointToDraw(point, BLOCK_PIXEL_LENGTH);
    }

    private void drawTetromino(Tetromino tetromino){
        for (Point point : tetromino.getPoints()){
            drawBlock(point, tetromino.getBlock().getImage());
        }
    }

    private void drawBoard(Board gameBoard){
        Block[][] boardMap = gameBoard.getBoardMap();
        for (int width = 0; width < boardMap.length; width++){
            for (int height = 0; height < boardMap[width].length; height++){
                drawBlock(new Point(width + 1, height + 1), boardMap[width][height].getImage());
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
        canHold = true;
        //TODO gameover
        drawNext();
    }

    private void tetrominoHardDrop(){
        while (gameBoard.testValidMove(tetromino.getDownPoints())){
            tetromino.moveDown();
        }
        tetrominoLock();
        clearFullLines();
    }

    private void tetrominoTryHold(){
        if (canHold) {
            if (holdTetromino.equals(Block.NONE)){
                holdTetromino = tetromino.getBlock();
                pickTetromino();
            } else {
                Block block = holdTetromino;
                holdTetromino = tetromino.getBlock();
                tetromino.setBlock(block);
            }
            canHold = false;
        }
        drawHold();
    }

    private void tetrominoTryMoveDown(){
        if (gameBoard.testValidMove(tetromino.getDownPoints())){
            tetromino.moveDown();
            tetrominoDelayLock();
        }
    }

    private void tetrominoDelayLock(){
        if (!gameBoard.testValidMove(tetromino.getDownPoints())){
            Timer timer = new Timer(true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    tetrominoLock();
                    renderGame();
                }
            }, 750);
        }
    }

    private void tetrominoLock(){
        if (!gameBoard.testValidMove(tetromino.getDownPoints())){
            gameBoard.placeTetromino(tetromino);
            pickTetromino();
        }
    }

    private void tetrominoTryMoveRight(){
        if (gameBoard.testValidMove(tetromino.getRightPoints())){
            tetromino.moveRight();
        }
    }

    private void tetrominoTryMoveLeft(){
        if (gameBoard.testValidMove(tetromino.getLeftPoints())){
            tetromino.moveLeft();
        }
    }

    private void tetrominoTryClockwise(){
        for (int i = 0; i < 5; i++){
            if (gameBoard.testValidMove(tetromino.getClockwisePoints(i))){
                tetromino.rotateClockwise(i);
                break;
            }
        }
    }

    private void tetrominoTryCounterClockwise(){
        for (int i = 0; i < 5; i++){
            if (gameBoard.testValidMove(tetromino.getCounterClockwisePoints(i))){
                tetromino.rotateCounterClockwise(i);
                break;
            }
        }
    }

    private void clearFullLines(){
        gameBoard.clearFullLines();
    }

    private void updateGame(){
        if(isPaused){
            return;
        }
        if (tetromino.getBlock().equals(Block.NONE)){
            pickTetromino();
        }
        tetrominoTryMoveDown();
        clearFullLines();
        System.out.println(tetromino);
    }

    private void renderGame(){
        clearCanvas(gameGraphicsContent);
        drawTetromino(tetromino);
        drawBoard(gameBoard);
    }

    private void drawNext() {
        drawSideCanvas(nextTetromino, nextGraphicsContent, nextTetrominoCanvas);
    }

    private void drawSideCanvas(Block nextTetromino, GraphicsContext nextGraphicsContent, Canvas nextTetrominoCanvas) {
        final Vector drawOffset;
        switch (nextTetromino){
            case I:
                drawOffset = new Vector(15 + BLOCK_PIXEL_LENGTH, 20 - BLOCK_PIXEL_LENGTH/2);
                break;
            case O:
                drawOffset = new Vector(15 + BLOCK_PIXEL_LENGTH, 20);
                break;
            case J:
            case L:
            case S:
            case T:
            case Z:
                drawOffset = new Vector(30 + BLOCK_PIXEL_LENGTH, 20);
                break;
            default:
                drawOffset = null;
        }
        if (drawOffset != null) {
            Platform.runLater(() -> {
                nextGraphicsContent.clearRect(0, 0, nextTetrominoCanvas.getWidth(), nextTetrominoCanvas.getHeight());
                for (Vector vector : Tetromino.TETROMINO_SHAPE_VECTOR[nextTetromino.ordinal()][0]) {
                    Point point = vector.asPoint();
                    nextGraphicsContent.drawImage(nextTetromino.getImage(), point.getX() * BLOCK_PIXEL_LENGTH + drawOffset.getX(), (1 - point.getY()) * BLOCK_PIXEL_LENGTH + drawOffset.getY());
                }
            });
        }
    }

    private void drawHold() {
        if (!holdTetromino.equals(Block.NONE)) {
            drawSideCanvas(holdTetromino, holdGraphicsContent, holdTetrominoCanvas);
        }
    }
}