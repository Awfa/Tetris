package com.anthonyha.tetris;

import java.util.ArrayDeque;

import com.badlogic.gdx.graphics.Color;

public class TetrisBoard {
	private static final int BOARD_WIDTH = 12;
	private static final int BOARD_HEIGHT = 24;
	private static final int QUEUE_LENGTH = 3;
	private static final float LOCK_TIME = 0.1f;
	private static final float FALL_TIME = 0f;
	
	private TetrominoFactory factory;
	private float fallTimer = 0;
	private float lockTimer = 0;
	
	public BlockGrid gameGrid;
	public Tetromino activeTetromino;
	public ArrayDeque<Tetromino> tetrominoQueue;
	
	public int tetrominoX, tetrominoY;
	
	public TetrisBoard(long seed) {
		Color borderColor = Color.BLACK;
		
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
		//Lock and spawn new piece
		if (gameGrid.intersects(activeTetromino.blockGrid, tetrominoX, tetrominoY-1)) {
			lockTimer += deltaTime;
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
