package com.anthonyha.tetris;

import java.util.ArrayDeque;

import com.anthonyha.tetris.Tetromino.TetrominoNames;
import com.badlogic.gdx.graphics.Color;

public class TetrisBoard {
	private static final int BOARD_WIDTH = 12;
	private static final int BOARD_HEIGHT = 24;
	private static final int BOARD_TOP_MARGIN = 2;
	private static final int QUEUE_LENGTH = 3;
	private static final float LOCK_TIME = 0.5f;
	private static final float FALL_TIME = 1f;
	private static final float SOFT_DROP_MULTIPLIER = 5f;
	private static final float DELAYED_AUTO_SHIFT_TIME = 0.3f;
	private static final float AUTO_MOVEMENT_DELAY = 0.05f;
	
	//Score multipliers for no lines cleared, single, double, triple, and quad respectively.
	private static final int[] scoreMultipliers = {0, 100, 300, 500, 800};
	
	private TetrominoFactory factory;
	private float fallTimer = 0f;
	private float lockTimer = 0f;
	private float moveTimer = 0f;

	private boolean loss = false;
	private boolean lineCleared;
	
	private int score = 0;
	private int level = 1;
	
	private BlockGrid spawnField;
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
		
		spawnField = new BlockGrid(BOARD_WIDTH-2, BOARD_TOP_MARGIN);
		for (int x = 0; x < BOARD_WIDTH-2; ++x) {
			for (int y = 0; y < BOARD_TOP_MARGIN; ++y) {
				spawnField.setValue(x, y, true, Color.BLACK);
			}
		}
		
		tetrominoQueue = new ArrayDeque<Tetromino>(QUEUE_LENGTH);

		for (int i = 0; i < QUEUE_LENGTH; ++i) {
			tetrominoQueue.add(factory.getPiece());
		}

		spawnTetromino();
	}

	public void update(float deltaTime) {
		if (!loss) {
			// Process DAS movement
			if (moveTimer >= DELAYED_AUTO_SHIFT_TIME) {
				if (left) {
					moveLeft();
				} else if (right) {
					moveRight();
				}
				moveTimer -= AUTO_MOVEMENT_DELAY;
			} else if (left ^ right) {
				moveTimer += deltaTime;
			} else {
				moveTimer = 0f;
			}
	
			// Check for intersection downwards and adds to the lock timer if there is one
			if (gameGrid.intersects(activeTetromino.blockGrid, tetrominoX, tetrominoY - 1)) {
				lockTimer += deltaTime;
			} else {
				lockTimer = 0f;
			}
	
			// If lock time has been exceeded, lock and spawn a new tetromino
			if (lockTimer >= LOCK_TIME) {
				if (lockTetromino()) {
					spawnTetromino();
					lockTimer -= LOCK_TIME;
				}
			} else {
				fallTimer += deltaTime;
			}
			
			// TODO Fix up soft drop, and add scoring for soft drop
			// Make the piece fall
			if (fallTimer >= FALL_TIME / (down ? SOFT_DROP_MULTIPLIER : 1)) {
				if (!gameGrid.intersects(activeTetromino.blockGrid, tetrominoX, tetrominoY - 1)) {
					--tetrominoY;
				}
	
				fallTimer -= FALL_TIME / (down ? SOFT_DROP_MULTIPLIER : 1);
			}
		}
	}
	
	public void hardDrop() {
		if (!loss) {
			while(!gameGrid.intersects(activeTetromino.blockGrid, tetrominoX, tetrominoY-1)) {
				--tetrominoY;
				score += 2;
			}
		}
	}

	public void moveRight() {
		if (!loss && !gameGrid.intersects(activeTetromino.blockGrid, tetrominoX + 1, tetrominoY)) {
			++tetrominoX;
			lockTimer = 0f;
		}
	}

	public void moveLeft() {
		if (!loss && !gameGrid.intersects(activeTetromino.blockGrid, tetrominoX - 1, tetrominoY)) {
			--tetrominoX;
			lockTimer = 0f;
		}
	}
	
	public void holdPiece() {
		if (!loss && !held) {
			TetrominoNames activeName = activeTetromino.getName();
			
			if (heldTetromino == null) {
				spawnTetromino();
			} else {
				spawnTetromino(heldTetromino);
			}
			
			heldTetromino = factory.getPiece(activeName);
			held = true;
		}
	}

	public void rotateClockwise() {
		if (!loss) {
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
	}

	public void rotateCounterClockwise() {
		if (!loss) {
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
	}

	public int getScore() {
		return score;
	}
	
	public boolean isLoss() {
		return loss;
	}
	
	private boolean lockTetromino() {
		// Check for loss conditions
		if (gameGrid.intersects(activeTetromino.blockGrid, tetrominoX, tetrominoY) ||
				spawnField.intersects(activeTetromino.blockGrid, tetrominoX-1, tetrominoY + BOARD_HEIGHT - 2 - BOARD_TOP_MARGIN) ) {
			loss = true;
			return false;
		} else {
			// Check if the piece is above the visible portion of the playing field
			// Set all game grid blocks to be the same as the active tetromino's
			for (int x = 0; x < activeTetromino.blockGrid.getWidth(); ++x) {
				for (int y = 0; y < activeTetromino.blockGrid.getHeight(); ++y) {
					if (activeTetromino.blockGrid.getValue(x, y)) {
						gameGrid.setBlock(x + tetrominoX, y + tetrominoY, activeTetromino.blockGrid.getBlock(x, y));
					}
				}
			}
	
			// Process for line clears
			int lines = 0;
			for (int y = 1; y < BOARD_HEIGHT - 1; ++y) { // From the bottom of the board, to the top.
				int blocksInALine = 0; // Keep track of blocks in a line to see if a line is full
				for (int x = 1; x < BOARD_WIDTH - 1; ++x) {
					if (gameGrid.getValue(x, y)) {
						++blocksInALine;
					} else {
						break; // No need to check if there is a single hole
					}
				}
	
				if (blocksInALine == BOARD_WIDTH - 2) {
					clearLine(y);
					++lines;
					--y; // Decrement y so that it will check again
				}
			}
			
			// Process score normally, or back-to-back circumstances
			if (lineCleared) {
				score += scoreMultipliers[lines] * level * 1.5f;
			} else {
				score += scoreMultipliers[lines] * level;
			}
			
			// Set lineCleared flag correspondingly
			if (lines > 0) {
				lineCleared = true;
			} else {
				lineCleared = false;
			}
			
			held = false;
			return true;
		}
	}

	private void spawnTetromino() {
		activeTetromino = tetrominoQueue.remove();
		tetrominoQueue.add(factory.getPiece());

		tetrominoX = BOARD_WIDTH / 2 - (activeTetromino.blockGrid.getWidth() + 1) / 2;
		tetrominoY = BOARD_HEIGHT - 1 - activeTetromino.blockGrid.getHeight();

		moveTimer = 0f;
	}
	
	private void spawnTetromino(Tetromino t) {
		activeTetromino = t;

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
