package com.anthonyha.tetris;

import java.util.ArrayDeque;

import com.badlogic.gdx.graphics.Color;

public class TetrisBoard {
	private static final int BOARD_WIDTH = 12;
	private static final int BOARD_HEIGHT = 24;
	private static final int QUEUE_LENGTH = 3;
	private static final float LOCK_TIME = 0.1f;
	private static final float FALL_TIME = 1f;
	private static final float DELAYED_AUTO_SHIFT_TIME = 0.133f;
	
	private TetrominoFactory factory;
	private float fallTimer = 0;
	private float lockTimer = 0;
	private float moveTimer = 0;
	
	public BlockGrid gameGrid;
	public Tetromino activeTetromino;
	public ArrayDeque<Tetromino> tetrominoQueue;
	
	public int tetrominoX, tetrominoY;
	public boolean left, right;
	
	public TetrisBoard(long seed) {
		Color borderColor = Color.BLACK;
		left = false;
		right = false;
		
		factory = new RandomTetrominoFactory();
		factory.setSeed(seed);
		
		//Make game grid
		gameGrid = new BlockGrid(BOARD_WIDTH, BOARD_HEIGHT);
		
		for(int x = 0; x < BOARD_WIDTH; ++x) {
			gameGrid.setValue(x, 0, true, Color.BLACK);
			gameGrid.setValue(x, BOARD_HEIGHT-1, true, borderColor);
		}
		
		for(int y = 0; y < BOARD_HEIGHT; ++y) {
			gameGrid.setValue(0, y, true, Color.BLACK);
			gameGrid.setValue(BOARD_WIDTH-1, y, true, borderColor);
		}
		
		tetrominoQueue = new ArrayDeque<Tetromino>(QUEUE_LENGTH);
		
		for (int i = 0; i < QUEUE_LENGTH; ++i) {
			tetrominoQueue.add(factory.getPiece());
		}
		
		spawnTetromino();
	}
	
	public void update(float deltaTime) {
		//Process DAS movement
		if (moveTimer >= DELAYED_AUTO_SHIFT_TIME) {
			if (left) {
				moveLeft();
			} else if (right) {
				moveRight();
			} else {
				moveTimer = 0f;
			}
		} else if (left ^ right) { // If left xor right, add dt to the move timer
			moveTimer += deltaTime;
		} else {
			moveTimer = 0f;
		}
		
		//Lock and spawn new piece
		if (gameGrid.intersects(activeTetromino.blockGrid, tetrominoX, tetrominoY-1)) {
			lockTimer += deltaTime;
		} else {
			lockTimer = 0;
		}
		
		if (lockTimer >= LOCK_TIME) {
			lockTetromino();
			spawnTetromino();
			lockTimer -= LOCK_TIME;
		} else {
			fallTimer += deltaTime;
		}
		
		//Make the piece fall
		if (fallTimer >= FALL_TIME) {
			if (!gameGrid.intersects(activeTetromino.blockGrid, tetrominoX, tetrominoY-1)) {
				--tetrominoY;
			}
			
			fallTimer -= FALL_TIME;
		}
	}
	
	public void moveRight() {
		if (!gameGrid.intersects(activeTetromino.blockGrid, tetrominoX+1, tetrominoY)) {
			++tetrominoX;
		}
	}
	
	public void moveLeft() {
		if (!gameGrid.intersects(activeTetromino.blockGrid, tetrominoX-1, tetrominoY)) {
			--tetrominoX;
		}
	}
	
	private void lockTetromino() {
		for (int x = 0; x < activeTetromino.blockGrid.getWidth(); ++x) {
			for (int y = 0; y < activeTetromino.blockGrid.getHeight(); ++y) {
				if (activeTetromino.blockGrid.getValue(x, y)) {
					gameGrid.setBlock(x+tetrominoX, y+tetrominoY, activeTetromino.blockGrid.getBlock(x, y));
				}
			}
		}
	}
	
	private void spawnTetromino() {
		activeTetromino = tetrominoQueue.remove();
		tetrominoQueue.add(factory.getPiece());
		
		tetrominoX = BOARD_WIDTH/2 - (activeTetromino.blockGrid.getWidth() + 1)/2;
		tetrominoY = BOARD_HEIGHT - 1 - activeTetromino.blockGrid.getHeight();
	}
}
