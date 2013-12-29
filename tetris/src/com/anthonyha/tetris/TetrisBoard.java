package com.anthonyha.tetris;

import java.util.ArrayDeque;

import com.anthonyha.tetris.Tetromino.TetrominoNames;
import com.badlogic.gdx.graphics.Color;

public class TetrisBoard {
	private static final int BOARD_WIDTH = 12;
	private static final int BOARD_HEIGHT = 24;
	private static final int QUEUE_LENGTH = 4;
	private static final float LOCK_TIME = 0.5f;
	private static final float FALL_TIME = 1f;
	private static final float SOFT_DROP_MULTIPLIER = 5f;
	private static final float DELAYED_AUTO_SHIFT_TIME = 0.3f;
	private static final float AUTO_MOVEMENT_DELAY = 0.05f;

	private TetrominoFactory factory;
	private float fallTimer = 0f;
	private float lockTimer = 0f;
	private float moveTimer = 0f;

	private int linesCleared = 0;

	public BlockGrid gameGrid;
	public Tetromino activeTetromino;
	public Tetromino heldTetromino;
	public ArrayDeque<Tetromino> tetrominoQueue;

	public int tetrominoX, tetrominoY;
	public boolean left, right, down, held;

	public TetrisBoard(long seed) {
		Color borderColor = Color.BLACK;
		left = false;
		right = false;
		down = false;

		factory = new RandomTetrominoFactory();
		factory.setSeed(seed);

		// Make game grid
		gameGrid = new BlockGrid(BOARD_WIDTH, BOARD_HEIGHT);

		for (int x = 0; x < BOARD_WIDTH; ++x) {
			gameGrid.setValue(x, 0, true, Color.BLACK);
			gameGrid.setValue(x, BOARD_HEIGHT - 1, true, borderColor);
		}

		for (int y = 0; y < BOARD_HEIGHT; ++y) {
			gameGrid.setValue(0, y, true, Color.BLACK);
			gameGrid.setValue(BOARD_WIDTH - 1, y, true, borderColor);
		}

		tetrominoQueue = new ArrayDeque<Tetromino>(QUEUE_LENGTH);

		for (int i = 0; i < QUEUE_LENGTH; ++i) {
			tetrominoQueue.add(factory.getPiece());
		}

		spawnTetromino();
	}

	public void update(float deltaTime) {
		// Process DAS movement
		if (moveTimer >= DELAYED_AUTO_SHIFT_TIME) {
			if (left) {
				moveLeft();
			} else if (right) {
				moveRight();
			}
			moveTimer -= AUTO_MOVEMENT_DELAY;
		} else if (left ^ right) { // If left xor right, add dt to the move
									// timer
			moveTimer += deltaTime;
		} else {
			moveTimer = 0f;
		}

		// Check for intersection downwards and adds to the lock timer if there
		// is one
		if (gameGrid.intersects(activeTetromino.blockGrid, tetrominoX, tetrominoY - 1)) {
			lockTimer += deltaTime;
		} else {
			lockTimer = 0f;
		}

		// If lock time has been exceeded, lock and spawn a new tetromino
		if (lockTimer >= LOCK_TIME) {
			lockTetromino();
			spawnTetromino();
			lockTimer -= LOCK_TIME;
		} else {
			fallTimer += deltaTime;
		}

		// Make the piece fall
		if (fallTimer >= FALL_TIME / (down ? SOFT_DROP_MULTIPLIER : 1)) {
			if (!gameGrid.intersects(activeTetromino.blockGrid, tetrominoX, tetrominoY - 1)) {
				--tetrominoY;
			}

			fallTimer -= FALL_TIME / (down ? SOFT_DROP_MULTIPLIER : 1);
		}
	}
	
	public void hardDrop() {
		while(!gameGrid.intersects(activeTetromino.blockGrid, tetrominoX, tetrominoY-1)) {
			--tetrominoY;
		}
	}

	public void moveRight() {
		if (!gameGrid.intersects(activeTetromino.blockGrid, tetrominoX + 1, tetrominoY)) {
			++tetrominoX;
			lockTimer = 0f;
		}
	}

	public void moveLeft() {
		if (!gameGrid.intersects(activeTetromino.blockGrid, tetrominoX - 1, tetrominoY)) {
			--tetrominoX;
			lockTimer = 0f;
		}
	}
	
	public void holdPiece() {
		if (!held) {
			TetrominoNames activeName = activeTetromino.getName();
			
			if (heldTetromino == null) {
				spawnTetromino();
			} else {
				tetrominoQueue.addFirst(heldTetromino);
				spawnTetromino();
			}
			
			heldTetromino = factory.getPiece(activeName);
			held = true;
		}
	}

	public void rotateClockwise() {
		Tetromino temp = new Tetromino(activeTetromino);
		temp.rotateClockwise();

		for (int offset = 0; offset < activeTetromino.offsetData[0].length; ++offset) {
			// Derive kick translations from offsets
			Vector2 offsetPreRot = new Vector2(activeTetromino.offsetData[activeTetromino.getRotationState().ordinal()][offset]);
			Vector2 offsetPostRot = activeTetromino.offsetData[temp.getRotationState().ordinal()][offset];
			Vector2 kickTranslation = offsetPreRot.sub(offsetPostRot);

			// If there isn't an intersection, then apply offset and be done
			if (!gameGrid.intersects(temp.blockGrid, tetrominoX + kickTranslation.x, tetrominoY + kickTranslation.y)) {
				tetrominoX += kickTranslation.x;
				tetrominoY += kickTranslation.y;

				activeTetromino.rotateClockwise();
				lockTimer = 0f;
				break;
			}
		}
	}

	public void rotateCounterClockwise() {
		Tetromino temp = new Tetromino(activeTetromino);
		temp.rotateCounterClockwise();

		for (int offset = 0; offset < activeTetromino.offsetData[0].length; ++offset) {
			// Derive kick translations from offsets
			Vector2 offsetPreRot = new Vector2(activeTetromino.offsetData[activeTetromino.getRotationState().ordinal()][offset]);
			Vector2 offsetPostRot = activeTetromino.offsetData[temp.getRotationState().ordinal()][offset];
			Vector2 kickTranslation = offsetPreRot.sub(offsetPostRot);

			// If there isn't an intersection, then apply offset and be done
			if (!gameGrid.intersects(temp.blockGrid, tetrominoX + kickTranslation.x, tetrominoY + kickTranslation.y)) {
				tetrominoX += kickTranslation.x;
				tetrominoY += kickTranslation.y;

				activeTetromino.rotateCounterClockwise();
				lockTimer = 0f;
				break;
			}
		}
	}

	public int getLinesCleared() {
		return linesCleared;
	}

	private void lockTetromino() {
		// Set all game grid blocks to be the same as the active tetromino's
		for (int x = 0; x < activeTetromino.blockGrid.getWidth(); ++x) {
			for (int y = 0; y < activeTetromino.blockGrid.getHeight(); ++y) {
				if (activeTetromino.blockGrid.getValue(x, y)) {
					gameGrid.setBlock(x + tetrominoX, y + tetrominoY, activeTetromino.blockGrid.getBlock(x, y));
				}
			}
		}

		// Process for line clears
		for (int y = 1; y < BOARD_HEIGHT - 1; ++y) { // From the bottom of the
														// board, to the top.
														// Careful for borders!
			int blocksInALine = 0; // Keep track of blocks in a line to see if a
									// line is full
			for (int x = 1; x < BOARD_WIDTH - 1; ++x) {
				if (gameGrid.getValue(x, y)) {
					++blocksInALine;
				} else {
					break; // No need to check if there is a single hole
				}
			}

			if (blocksInALine == BOARD_WIDTH - 2) {
				clearLine(y);
				++linesCleared;
				--y; // Decrement y so that it will check again
			}
		}
		
		held = false;

	}

	private void spawnTetromino() {
		activeTetromino = tetrominoQueue.remove();
		tetrominoQueue.add(factory.getPiece());

		tetrominoX = BOARD_WIDTH / 2 - (activeTetromino.blockGrid.getWidth() + 1) / 2;
		tetrominoY = BOARD_HEIGHT - 1 - activeTetromino.blockGrid.getHeight();

		moveTimer = 0f;
	}

	private void clearLine(int y) {
		for (; y < BOARD_HEIGHT - 2; ++y) {
			for (int x = 1; x < BOARD_WIDTH; ++x) {
				gameGrid.setBlock(x, y, gameGrid.getBlock(x, y + 1));
			}
		}
	}
}
