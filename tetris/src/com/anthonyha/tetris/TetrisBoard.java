package com.anthonyha.tetris;

import java.util.ArrayDeque;

public class TetrisBoard {
	private static final int BOARD_WIDTH = 10;
	private static final int BOARD_HEIGHT = 22;
	private static final int QUEUE_LENGTH = 3;
	private static final float LOCK_TIME = 2f;
	private static final float FALL_TIME = 0.5f;
	
	private TetrominoFactory factory;
	private float fallTimer = 0;
	private float lockTimer = 0;
	
	public BlockGrid gameGrid;
	public Tetromino activeTetromino;
	public ArrayDeque<Tetromino> tetrominoQueue;
	
	public int tetrominoX, tetrominoY;
	
	public TetrisBoard(long seed) {
		factory = new RandomTetrominoFactory();
		factory.setSeed(seed);
		
		gameGrid = new BlockGrid(BOARD_WIDTH, BOARD_HEIGHT);
		tetrominoQueue = new ArrayDeque<Tetromino>(QUEUE_LENGTH);
		
		for (int i = 0; i < QUEUE_LENGTH; ++i) {
			tetrominoQueue.add(factory.getPiece());
		}
		
		activeTetromino = tetrominoQueue.remove();
		tetrominoQueue.add(factory.getPiece());
		
		tetrominoX = 5 - activeTetromino.blockGrid.getWidth()/2;
		tetrominoY = 22 - activeTetromino.blockGrid.getHeight();
		update(0);
	}
	
	public void update(float deltaTime) {
		//Lock and spawn new piece
		if (lockTimer >= LOCK_TIME || tetrominoY == 0) {
			lockTetromino();
			activeTetromino = tetrominoQueue.remove();
			tetrominoQueue.add(factory.getPiece());
			
			tetrominoX = 5 - activeTetromino.blockGrid.getWidth()/2;
			tetrominoY = 22 - activeTetromino.blockGrid.getHeight();
		}
		
		fallTimer += deltaTime;
		if (fallTimer >= FALL_TIME) {
			--tetrominoY;
			
			fallTimer -= FALL_TIME;
		}
	}
	
	private void lockTetromino() {
		for (int x = 0; x < activeTetromino.blockGrid.getWidth(); ++x) {
			for (int y = 0; y < activeTetromino.blockGrid.getHeight(); ++y) {
				if (activeTetromino.blockGrid.getValue(x, y)) {
					gameGrid.setValue(x+tetrominoX, y+tetrominoY, true);
				}
			}
		}
	}
	
}
