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

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameController {

    @FXML private BorderPane boarderPane;
    @FXML private Canvas holdTetrominoCanvas, gameBoardCanvas, gameBoardGridCanvas, nextTetrominoCanvas;
    @FXML private ArrayList<Canvas> sideGameCanvas, sideGridCanvas;
    @FXML private Label gameoverLabel;
    @FXML private Button startBtn;

    private static final int BLOCK_PIXEL_LENGTH = 30;

    private GraphicsContext gameGraphicsContent, gameGridGraphicsContent,
            nextGraphicsContent, holdGraphicsContent;
    private ArrayList<GraphicsContext> sideGameGraphicsContext = new ArrayList<>(0),
            sideGridGraphicsContext = new ArrayList<>(0);
    private Timer gameTimer;
    private Tetris tetris;
    private Broadcastable broadcastable;
    private ArrayList<String> sideClients = new ArrayList<>(0);
    private Pattern socketAddressPattern = Pattern.compile("^((?:[0-9]{1,3}\\.){3}[0-9]{1,3}:\\d*): (.*)");

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
        gameGridGraphicsContent = gameBoardGridCanvas.getGraphicsContext2D();
        holdGraphicsContent = holdTetrominoCanvas.getGraphicsContext2D();
        nextGraphicsContent = nextTetrominoCanvas.getGraphicsContext2D();
        sideGameCanvas.forEach(canvas -> sideGameGraphicsContext.add(canvas.getGraphicsContext2D()));
        sideGridCanvas.forEach(canvas -> sideGridGraphicsContext.add(canvas.getGraphicsContext2D()));
        drawGrid(gameGridGraphicsContent, BLOCK_PIXEL_LENGTH);
        sideGridGraphicsContext.forEach(graphicsContext -> drawGrid(graphicsContext, BLOCK_PIXEL_LENGTH / 2));
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
        gameoverLabel.setVisible(false);
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

    private void drawGrid(GraphicsContext graphicsContext, int pixelWidth){
        Image background1 = new Image(getClass().getResource("/img/background1.png").toExternalForm());
        Image background2 = new Image(getClass().getResource("/img/background2.png").toExternalForm());
        for (int x = 0; x < pixelWidth * Board.BOARD_WIDTH; x += pixelWidth){
            for (int y = 0; y < pixelWidth * Board.BOARD_HEIGHT; y += pixelWidth){
                if ((x+y) % (2 * pixelWidth) == 0) {
                    graphicsContext.drawImage(background1, x, y, pixelWidth, pixelWidth);
                } else {
                    graphicsContext.drawImage(background2, x, y, pixelWidth, pixelWidth);
                }
            }
        }
    }

    private void drawBlock(Point point, Image image, GraphicsContext gc, int block_length){
        if (image!=null) {
            Platform.runLater(() -> {
                Point canvasPoint = convertPointToDraw(point, block_length);
                gc.drawImage(image, canvasPoint.getX(), canvasPoint.getY(), block_length, block_length);
            });
        }
    }

    private void drawBlock(Point point, Image image, GraphicsContext gc){
        drawBlock(point, image, gc, BLOCK_PIXEL_LENGTH);
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

    private void drawBoard(Board gameBoard, GraphicsContext graphicsContext, int blockLength){
        Block[][] boardMap = gameBoard.getBoardMap();
        for (int width = 0; width < boardMap.length; width++){
            for (int height = 0; height < boardMap[width].length; height++){
                drawBlock(new Point(width + 1, height + 1), boardMap[width][height].getImage(), graphicsContext, blockLength);
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

    //TODO
    private void receiveMessage(String message){
        Matcher matcher = socketAddressPattern.matcher(message);
        if (matcher.find()) {
            if (sideClients.size()==0){
                sideClients.add(matcher.group(1));
            }
            sideClients.indexOf(matcher.group(1));

            Board board = Board.valueOf(matcher.group(2));
            clearCanvas(sideGameGraphicsContext.get(0));
            drawBoard(board, sideGameGraphicsContext.get(0), BLOCK_PIXEL_LENGTH/2);
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
        }
    }

    private String prepareBroadcast(Board board, Tetromino tetromino){
        StringBuilder stringBuilder = board.toStringBuilder();
        for ( Point point: tetromino.getPoints()){
            if (point.getX() > Board.BOARD_WIDTH || point.getY() > Board.BOARD_HEIGHT){
                continue;
            }
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