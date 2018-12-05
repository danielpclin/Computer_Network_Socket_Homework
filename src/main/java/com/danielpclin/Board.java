package com.danielpclin;

import com.danielpclin.helpers.Point;
import com.danielpclin.tetromino.Block;
import com.danielpclin.tetromino.Tetromino;

import java.util.Arrays;

class Board {

    public static final int BOARD_HEIGHT = 20;
    public static final int BOARD_WIDTH = 10;

    private boolean paused = false;

    private Block[][] boardMap;

    Board(){
        boardMap = new Block[BOARD_WIDTH][BOARD_HEIGHT];
        for (Block[] column: boardMap) {
            Arrays.fill(column, Block.NONE);
        }
    }

    void printBoard(){
        for(Block[] column : boardMap){
            for(Block cell : column){
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void placeTetromino(Tetromino tetromino){
        for (Point point : tetromino.getPoints()){
            if (boardMap[point.getX()-1][point.getY()-1].equals(Block.NONE)){
                boardMap[point.getX()-1][point.getY()-1] = tetromino.getBlock();
            } else {
                throw new IllegalArgumentException("Tried to place tetromino on existing block");
            }
        }
    }

    public Block[][] getBoardMap() {
        return boardMap;
    }

    public Boolean testValidMove(Point[] points){
        for (Point point : points){
            if (point.getX() > Board.BOARD_WIDTH || point.getX() < 1 || point.getY() < 1){
                return false;
            } else if (point.getY() > Board.BOARD_HEIGHT) {
                // Don't check board if block is above playing field
                continue;
            }
            if(!boardMap[point.getX()-1][point.getY()-1].equals(Block.NONE)){
                return false;
            }
        }
        return true;
    }
}
