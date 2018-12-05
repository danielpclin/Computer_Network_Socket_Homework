package com.danielpclin.tetromino;

import com.danielpclin.helpers.*;

import java.util.Arrays;

public class Tetromino {

    private Point point = new Point(5, 20);
    private Rotation rotation = Rotation.ZERO;
    private Block block = Block.NONE;

    private static final Vector[][][] tetrominoOffset = { // SRS Rotation System
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

    public static final Vector[][][] tetrominoShapeVector = { // SRS Rotation System
            { // Block.NONE
                    { new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 0) }, // Rotation.ZERO
                    { new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 0) }, // Rotation.RIGHT
                    { new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 0) }, // Rotation.TWO
                    { new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 0), new Vector( 0, 0) }  // Rotation.LEFT
            },
            { // Block.I
                    { new Vector( 0, 0), new Vector(-1, 0), new Vector( 1, 0), new Vector( 2, 0) }, // Rotation.ZERO
                    { new Vector( 0, 0), new Vector( 0, 1), new Vector( 0,-1), new Vector( 0,-2) }, // Rotation.RIGHT
                    { new Vector( 0, 0), new Vector(-2, 0), new Vector(-1, 0), new Vector( 1, 0) }, // Rotation.TWO
                    { new Vector( 0, 0), new Vector( 0, 2), new Vector( 0, 1), new Vector( 0,-1) }  // Rotation.LEFT
            },
            { // Block.O
                    { new Vector( 0, 0), new Vector( 1, 0), new Vector( 0, 1), new Vector( 1, 1) }, // Rotation.ZERO
                    { new Vector( 0, 0), new Vector( 1, 0), new Vector( 0,-1), new Vector( 1,-1) }, // Rotation.RIGHT
                    { new Vector( 0, 0), new Vector(-1, 0), new Vector( 0,-1), new Vector(-1,-1) }, // Rotation.TWO
                    { new Vector( 0, 0), new Vector(-1, 0), new Vector( 0, 1), new Vector(-1, 1) }  // Rotation.LEFT
            },
            { // Block.J
                    { new Vector( 0, 0), new Vector(-1, 0), new Vector( 1, 0), new Vector(-1, 1) }, // Rotation.ZERO
                    { new Vector( 0, 0), new Vector( 0, 1), new Vector( 0,-1), new Vector( 1, 1) }, // Rotation.RIGHT
                    { new Vector( 0, 0), new Vector(-1, 0), new Vector( 1, 0), new Vector( 1,-1) }, // Rotation.TWO
                    { new Vector( 0, 0), new Vector( 0, 1), new Vector( 0,-1), new Vector(-1,-1) }  // Rotation.LEFT
            },
            { // Block.L
                    { new Vector( 0, 0), new Vector(-1, 0), new Vector( 1, 0), new Vector( 1, 1) }, // Rotation.ZERO
                    { new Vector( 0, 0), new Vector( 0, 1), new Vector( 0,-1), new Vector( 1,-1) }, // Rotation.RIGHT
                    { new Vector( 0, 0), new Vector(-1, 0), new Vector( 1, 0), new Vector(-1,-1) }, // Rotation.TWO
                    { new Vector( 0, 0), new Vector( 0, 1), new Vector( 0,-1), new Vector(-1, 1) }  // Rotation.LEFT
            },
            { // Block.S
                    { new Vector( 0, 0), new Vector( 0, 1), new Vector( 1, 1), new Vector(-1, 0) }, // Rotation.ZERO
                    { new Vector( 0, 0), new Vector( 1, 0), new Vector( 1,-1), new Vector( 0, 1) }, // Rotation.RIGHT
                    { new Vector( 0, 0), new Vector( 0,-1), new Vector(-1,-1), new Vector( 1, 0) }, // Rotation.TWO
                    { new Vector( 0, 0), new Vector(-1, 0), new Vector(-1, 1), new Vector( 0,-1) }  // Rotation.LEFT
            },
            { // Block.T
                    { new Vector( 0, 0), new Vector(-1, 0), new Vector( 1, 0), new Vector( 0, 1) }, // Rotation.ZERO
                    { new Vector( 0, 0), new Vector( 0, 1), new Vector( 0,-1), new Vector( 1, 0) }, // Rotation.RIGHT
                    { new Vector( 0, 0), new Vector(-1, 0), new Vector( 1, 0), new Vector( 0,-1) }, // Rotation.TWO
                    { new Vector( 0, 0), new Vector( 0, 1), new Vector( 0,-1), new Vector(-1, 0) }  // Rotation.LEFT
            },

            { // Block.Z
                    { new Vector( 0, 0), new Vector( 0, 1), new Vector(-1, 1), new Vector( 1, 0) }, // Rotation.ZERO
                    { new Vector( 0, 0), new Vector( 1, 0), new Vector( 1, 1), new Vector( 0,-1) }, // Rotation.RIGHT
                    { new Vector( 0, 0), new Vector( 0,-1), new Vector( 1,-1), new Vector(-1, 0) }, // Rotation.TWO
                    { new Vector( 0, 0), new Vector(-1, 0), new Vector(-1,-1), new Vector( 0, 1) }  // Rotation.LEFT
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

    // Vectors representing tetromino
    private Vector[] getVectors(){
        return tetrominoShapeVector[block.ordinal()][rotation.ordinal()];
    }

    // Points representing tetromino
    public Point[] getPoints(){
        return Arrays.stream(getVectors())
                .map(vector -> point.add(vector))
                .toArray(Point[]::new);
    }

    public Point[] getDownPoints(){
        return Arrays.stream(getVectors())
                .map(vector -> {
                    return point.add(vector).add(new Vector(0, -1));
                })
                .toArray(Point[]::new);
    }

    public Point[] getRightPoints(){
        return Arrays.stream(getVectors())
                .map(vector -> {
                    return point.add(vector).add(new Vector(1, 0));
                })
                .toArray(Point[]::new);
    }

    public Point[] getLeftPoints(){
        return Arrays.stream(getVectors())
                .map(vector -> {
                    return point.add(vector).add(new Vector(-1, 0));
                })
                .toArray(Point[]::new);
    }

    public void moveDown(){
        point = point.add(new Vector(0, -1));
    }

    public void moveRight(){
        point = point.add(new Vector(1, 0));
    }

    public void moveLeft(){
        point = point.add(new Vector(-1, 0));
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        point = new Point(5, 20);
        this.block = block;
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
