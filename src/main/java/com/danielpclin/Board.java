package com.danielpclin;

import com.danielpclin.tetromino.Block;
import com.danielpclin.tetromino.Tetromino;

import java.util.Arrays;

class Board {

    public static final int BOARD_HEIGHT = 20;
    public static final int BOARD_WIDTH = 10;

    private boolean paused = false;

    private Block[][] boardMap;




    Board(){
        boardMap = new Block[BOARD_HEIGHT][BOARD_WIDTH];
        for (Block[] row: boardMap) {
            Arrays.fill(row, Block.NONE);
        }
    }

    void printBoard(){
        for(Block[] row : boardMap){
            for(Block cell : row){
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

    }

    public Block[][] getBoardMap() {
        return boardMap;
    }
}
