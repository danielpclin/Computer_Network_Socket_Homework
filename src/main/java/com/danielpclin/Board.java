package com.danielpclin;

import com.danielpclin.tetromino.Block;
import com.danielpclin.tetromino.Tetromino;

import java.util.Arrays;

class Board {

    private static final int BOARD_HEIGHT = 22;
    private static final int BOARD_WIDTH = 10;

    private Tetromino tetromino = new Tetromino();
    private Block[][] boardMap;
    private Block hold = Block.NONE;



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

    private void doGameCycle(){

    }


}
