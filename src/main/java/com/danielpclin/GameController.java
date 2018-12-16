package com.danielpclin;

import com.danielpclin.helpers.Broadcastable;
import com.danielpclin.helpers.Point;
import com.danielpclin.helpers.Vector;
import com.danielpclin.tetromino.Block;
import com.danielpclin.tetromino.Tetromino;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.*;

public class GameController {

    @FXML
    private BorderPane boarderPane;

    @FXML
    private Canvas holdTetrominoCanvas;

    @FXML
    private Canvas gameBoardCanvas;

    @FXML
    private Canvas gameBoardGridCanvas;

    @FXML
    private Canvas nextTetrominoCanvas;

    @FXML
    private Label gameoverLabel;

    @FXML
    private Button startBtn;

    private static final int BLOCK_PIXEL_LENGTH = 30;

    private GraphicsContext gameGraphicsContent;
    private GraphicsContext gameGridGraphicsContent;
    private GraphicsContext nextGraphicsContent;
    private GraphicsContext holdGraphicsContent;
    private Timer gameTimer;
    private Tetris tetris;
    private Broadcastable broadcastable;
    private EventHandler<KeyEvent> gameEventHandler = e-> {
        e.consume();
        if (!tetris.isGameOver()){
            switch (e.getCode()) {
                case DOWN:
                case S:
                    if (tetris.tetrominoTryMoveDown()){
                        tetrominoDelayLock();
                    }
                    break;
                case LEFT:
                case A:
                    tetris.tetrominoTryMoveLeft();
                    break;
                case RIGHT:
                case D:
                    tetris.tetrominoTryMoveRight();
                    break;
                case SPACE:
                    tetris.tetrominoHardDrop();
                    drawNext();
                    break;
                case SHIFT:
                case C:
                    if (tetris.tetrominoTryHold()) {
                        drawHold();
                        drawNext();
                    }
                    break;
                case Z:
                    tetris.tetrominoTryCounterClockwise();
                    break;
                case X:
                case UP:
                    tetris.tetrominoTryClockwise();
                    break;
                case P:
                    tetris.togglePause();
                    break;
                default:
            }
        }
        renderGame();
    };

    @FXML
    private void initialize() {
        gameGraphicsContent = gameBoardCanvas.getGraphicsContext2D();
        holdGraphicsContent = holdTetrominoCanvas.getGraphicsContext2D();
        nextGraphicsContent = nextTetrominoCanvas.getGraphicsContext2D();

        // Draw grid on gameGridCanvas
        gameGridGraphicsContent = gameBoardGridCanvas.getGraphicsContext2D();
        gameGridGraphicsContent.setStroke(Color.BLACK);
        drawGrid();
    }

    @FXML
    private void startGame(){
        if (!(gameTimer == null)){
            gameOver();
        }
        initializeSceneEventListener();
        gameTimer = new Timer(true);
        tetris = new Tetris();
        startBtn.setVisible(false);
        gameStart();
    }

    private void initializeSceneEventListener(){
        boarderPane.getScene().addEventFilter(KeyEvent.KEY_PRESSED, gameEventHandler);
    }

    private void gameStart(){
        tetris.initializeGame();
        drawNext();
        gameTimer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                if (!tetris.isGameOver()){
                    doGameCycle();
                } else {
                    gameOver();
                }
            }
        }, 0, 1000);
    }

    private void gameOver(){
        boarderPane.getScene().removeEventFilter(KeyEvent.KEY_PRESSED, gameEventHandler);
        gameTimer.cancel();
        startBtn.setVisible(true);
        gameoverLabel.setVisible(true);
    }

    private void doGameCycle(){
        if (!tetris.isPaused()){
            tetris.updateGame();
            tetrominoDelayLock();
            renderGame();
        }
    }

    private void clearCanvas(GraphicsContext gc){
        Platform.runLater(() -> {
            gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        });
    }

    private void drawGrid(){
        Image background1 = new Image(getClass().getResource("/img/background1.png").toExternalForm());
        Image background2 = new Image(getClass().getResource("/img/background2.png").toExternalForm());
        for (int x = 0; x < BLOCK_PIXEL_LENGTH * Board.BOARD_WIDTH; x += BLOCK_PIXEL_LENGTH){
            for (int y = 0; y < BLOCK_PIXEL_LENGTH * Board.BOARD_HEIGHT; y += BLOCK_PIXEL_LENGTH){
                if ((x+y) % (2 * BLOCK_PIXEL_LENGTH) == 0) {
                    gameGridGraphicsContent.drawImage(background1, x, y);
                } else {
                    gameGridGraphicsContent.drawImage(background2, x, y);
                }
            }
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

    private void renderGame(){
        clearCanvas(gameGraphicsContent);
        drawBoard(tetris.getGameBoard());
        drawTetromino(tetris.getTetromino());
        broadcastMessage(prepareBroadcast(tetris.getGameBoard(), tetris.getTetromino()));
    }

    private void drawNext() {
        drawSideCanvas(tetris.getNextTetromino(), nextGraphicsContent, nextTetrominoCanvas);
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
                    nextGraphicsContent.drawImage(nextTetromino.getImage(),
                            point.getX() * BLOCK_PIXEL_LENGTH + drawOffset.getX(),
                            (1 - point.getY()) * BLOCK_PIXEL_LENGTH + drawOffset.getY());
                }
            });
        }
    }

    private void drawHold() {
        if (!tetris.getHoldTetromino().equals(Block.NONE)) {
            drawSideCanvas(tetris.getHoldTetromino(), holdGraphicsContent, holdTetrominoCanvas);
        }
    }

    private void tetrominoDelayLock(){
        if (!tetris.tetrominoCanMoveDown()) {
            Timer timer = new Timer(true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (tetris.tetrominoLock()){
                        renderGame();
                        drawNext();
                    }
                }
            }, 750);
        }
    }

    private void receiveMessage(String message){

    }

    private String prepareBroadcast(Board board, Tetromino tetromino){
        StringBuilder stringBuilder = new StringBuilder(0);
        for( Block[] col: board.getBoardMap() ){
            for( Block block : col ){
                stringBuilder.append(block.toChar());
            }
        }
        for ( Point point: tetromino.getPoints()){
            stringBuilder.replace((point.getX()-1)*Board.BOARD_HEIGHT+point.getY()-1,
                    (point.getX()-1)*Board.BOARD_HEIGHT+point.getY(),
                    String.valueOf(tetromino.getBlock().toChar()));
        }
        return stringBuilder.toString();
    }

    private void broadcastMessage(String msg) {
        try {
            broadcastable.broadcast(msg);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startServer() throws IOException{
        Server server = new Server((message)->{
            receiveMessage(message);
            return message;
        });
        broadcastable = server;
        Thread thread = new Thread(server);
        thread.setDaemon(true);
        thread.start();
    }

    public void startClient() throws IOException{
        Client client = new Client(this::receiveMessage);
        broadcastable = client;
        Thread thread = new Thread(client);
        thread.setDaemon(true);
        thread.start();
    }


}