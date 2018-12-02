package com.danielpclin.tetromino;

import com.danielpclin.helpers.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Tetromino {

    private Point point = new Point();
    private Rotation rotation = Rotation.ZERO;
    private Block block = Block.NONE;

    public static final Vector[][][] tetrominoOffset = { // SRS Rotation System
            { // I Tetromino Offset Data
                    { new Vector( 0, 0), new Vector(-1, 0), new Vector( 2, 0), new Vector(-1, 0), new Vector( 2, 0) },
                    { new Vector(-1, 0), new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 1), new Vector( 0,-2) },
                    { new Vector(-1, 1), new Vector( 1, 1), new Vector(-2, 1), new Vector( 1, 0), new Vector(-2, 0) },
                    { new Vector( 0, 1), new Vector( 0, 1), new Vector( 0, 1), new Vector( 0,-1), new Vector( 0, 2) }
            },
            { // O Tetromino Offset Data
                    { new Vector( 0, 0) },
                    { new Vector( 0,-1) },
                    { new Vector(-1,-1) },
                    { new Vector(-1, 0) }
            },
            { // J, L, S, T, Z Tetromino Offset Data
                    { new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 0) },
                    { new Vector( 0, 0), new Vector( 1, 0), new Vector( 1,-1), new Vector( 0, 2), new Vector( 1, 2) },
                    { new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 0) },
                    { new Vector( 0, 0), new Vector(-1, 0), new Vector(-1,-1), new Vector( 0, 2), new Vector(-1, 2) }
            }
    };

    public Tetromino() {

    }

    public Tetromino(Block block, Point point) {
        this.block = block;
        this.point = point;
    }

    private Tetromino(Block block, Point point, Rotation rotation) {
        this.block = block;
        this.point = point;
        this.rotation = rotation;
    }

    public Tetromino rotateClockwise(){
        return rotateClockwise(0);
    }

    public Tetromino rotateClockwise(int offset){
        if(offset <0 || offset > 4) {
            throw new IllegalArgumentException("Invalid offset number");
        }
        Vector pointOffset;
        switch (this.block){
            case NONE:
                return null;
            case J:
            case L:
            case S:
            case T:
            case Z:
                pointOffset = tetrominoOffset[2][rotation.ordinal()][offset].subtract(tetrominoOffset[2][rotation.clockwiseRotation().ordinal()][offset]);
                return new Tetromino(block, point.add(pointOffset), rotation);
            case I:
                pointOffset = tetrominoOffset[0][rotation.ordinal()][offset].subtract(tetrominoOffset[0][rotation.clockwiseRotation().ordinal()][offset]);
                return new Tetromino(block, point.add(pointOffset), rotation);
            case O:
                if(offset != 0) {
                    throw new IllegalArgumentException("Invalid offset number");
                }
                pointOffset = tetrominoOffset[1][rotation.ordinal()][offset].subtract(tetrominoOffset[1][rotation.clockwiseRotation().ordinal()][offset]);
                return new Tetromino(block, point.add(pointOffset), rotation);
            default:
                return null;
        }
    }

    public Tetromino rotateCounterClockwise(){
        return rotateCounterClockwise(0);
    }

    public Tetromino rotateCounterClockwise(int offset){
        if(offset <0 || offset > 4) {
            throw new IllegalArgumentException("Invalid offset number");
        }
        Vector pointOffset;
        switch (this.block){
            case NONE:
                return null;
            case J:
            case L:
            case S:
            case T:
            case Z:
                pointOffset = tetrominoOffset[2][rotation.ordinal()][offset].subtract(tetrominoOffset[2][rotation.counterClockwiseRotation().ordinal()][offset]);
                return new Tetromino(block, point.add(pointOffset), rotation);
            case I:
                pointOffset = tetrominoOffset[0][rotation.ordinal()][offset].subtract(tetrominoOffset[0][rotation.counterClockwiseRotation().ordinal()][offset]);
                return new Tetromino(block, point.add(pointOffset), rotation);
            case O:
                if(offset != 0) {
                    throw new IllegalArgumentException("Invalid offset number");
                }
                pointOffset = tetrominoOffset[1][rotation.ordinal()][offset].subtract(tetrominoOffset[1][rotation.counterClockwiseRotation().ordinal()][offset]);
                return new Tetromino(block, point.add(pointOffset), rotation);
            default:
                return null;
        }
    }

    // TODO implement block vectors with rotation in mind
    private Vector[] getVectorsFromCenter(){
        return new Vector[]{ new Vector(0, 0), new Vector(1, 0), new Vector(2, 0), new Vector(-1, 0) };
    }

    public Point[] getPoints(){
        return Arrays.stream(getVectorsFromCenter())
                .map(vector -> vector.add(point))
                .toArray(Point[]::new);
    }

    @Override
    public String toString() {
        return "Tetromino{" +
                "point.x=" + point.getX() + ", point.y=" + point.getY() +
                ", rotation=" + rotation + " (" + rotation.ordinal() + ")" +
                ", block=" + block + " (" + block.ordinal() + ")" +
                '}';
    }
}