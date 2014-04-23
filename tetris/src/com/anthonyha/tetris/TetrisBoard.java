package com.anthonyha.tetris;

import com.anthonyha.tetris.MessageSystem.Message;
import com.anthonyha.tetris.Tetromino.TetrominoNames;
import com.badlogic.gdx.utils.Array;

public class TetrisBoard extends AbstractMessageListener {
	private static final int BOARD_WIDTH = 12;
	private static final int BOARD_HEIGHT = 24;
	private static final int BOARD_TOP_MARGIN = 2;
	private static final int QUEUE_LENGTH = 3;
	private static final int MAX_LOCK_RESETS = 5;
	
	//Score multipliers for no lines cleared, single, double, triple, and quad respectively.
	private static final int[] scoreMultipliers = {0, 100, 300, 500, 800};
	
	private TetrominoFactory factory;
	private MessageSystem messageSystem;
	
	private float fallTimer;
	private float lockTimer;
	private float moveTimer;

	private boolean loss;
	
	private boolean left, right, down, held;
	
	private int lockResets = 0;
	private int lastClear = 0;
	private int score = 0;
	private int level;
	private int goal;
	
	private float softDropTime = 0.06818181818f;
	private float fallTime = 1f;
	private float lockTime = 0.5f;
	private float dasTime = 0.2666666f;
	private float amTime = 0.0388888f;
	
	private boolean isPaused;
	
	private BlockGrid spawnField;
	
	public enum TetrisScores {
		NONE, SINGLE, DOUBLE, TRIPLE, TETRIS, BACKTOBACK, TSPIN
	}
	
	public BlockGrid gameGrid;
	public Tetromino activeTetromino;
	public Tetromino heldTetromino;
	public Tetromino ghostTetromino;
	public Array<Tetromino> tetrominoQueue;

	public Vector2 tetrominoPos;
	

	public TetrisBoard(long seed, int level, MessageSystem messageSystem) {
		this.messageSystem = messageSystem;
		
		messageSystem.add(this, Message.LEFT);
		messageSystem.add(this, Message.RIGHT);
		messageSystem.add(this, Message.ROTATE_LEFT);
		messageSystem.add(this, Message.ROTATE_RIGHT);
		messageSystem.add(this, Message.SOFT_DROP);
		messageSystem.add(this, Message.HARD_DROP);
		messageSystem.add(this, Message.HOLD);
		messageSystem.add(this, Message.PAUSE);
		messageSystem.add(this, Message.UNPAUSE);
		messageSystem.add(this, Message.RESTART_GAME);
		
		tetrominoPos = new Vector2(0, 0);
		
		factory = new RandomTetrominoFactory();
		factory.setSeed(seed);
		
		setGameUp(level);
	}

	public void update(float deltaTime) {
		if (!loss && !isPaused) {
			// Process DAS movement
			while (moveTimer >= dasTime) {
				if (left) {
					moveLeft();
				} else if (right) {
					moveRight();
				}
				
				moveTimer -= amTime;
			}
			
			if (left ^ right) {
				moveTimer += deltaTime;
			} else {
				moveTimer = 0f;
			}
	
			// Check for intersection downwards and adds to the lock timer if there is one
			if (gameGrid.intersects(activeTetromino.blockGrid, tetrominoPos.x, tetrominoPos.y - 1)) {
				lockTimer += deltaTime;
			} else {
				lockTimer = 0f;
			}
	
			// If lock time has been exceeded, lock and spawn a new tetromino
			if (lockTimer >= lockTime) {
				if (lockTetromino()) {
					lockTimer -= lockTime;
				}
			} else {
				fallTimer += deltaTime;
			}
			
			// Make the piece fall
			if (down) {
				while (fallTimer >= softDropTime) {
					if (!gameGrid.intersects(activeTetromino.blockGrid, tetrominoPos.x, tetrominoPos.y - 1)) {
						--tetrominoPos.y;
						lockResets = 0;
						score += 1;
						
						messageSystem.postMessage(MessageSystem.Message.SOFT_DROPPED);
					}
					fallTimer -= softDropTime;
					
				}
			} else {
				while (fallTimer >= fallTime) {
					if (!gameGrid.intersects(activeTetromino.blockGrid, tetrominoPos.x, tetrominoPos.y - 1)) {
						--tetrominoPos.y;
						lockResets = 0;
					}
					fallTimer -= fallTime;
					
				}
			}
			
		}
	}
	
	public int getScore() {
		return score;
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getGoal() {
		return goal;
	}
	
	public Vector2 getGhostVector() {
		Vector2 projectedDownPos = new Vector2(tetrominoPos);
		while(!gameGrid.intersects(activeTetromino.blockGrid, projectedDownPos.x, projectedDownPos.y-1)) {
			--projectedDownPos.y;
		}
		return projectedDownPos;
	}
	
	public boolean isLoss() {
		return loss;
	}
	
	public int getLinesCleared() {
		return lastClear;
	}
	
	@Override
	public void recieveMessage(Message message) {
		if (!loss) {
			switch(message) {
			case HARD_DROP:
				hardDrop();
				break;
				
			case ROTATE_RIGHT:
				rotateClockwise();
				break;
				
			case ROTATE_LEFT:
				rotateCounterClockwise();
				break;
				
			case HOLD:
				holdPiece();
				break;
				
			case PAUSE:
				isPaused = true;
				messageSystem.postMessage(Message.GAME_PAUSED);
				break;
				
			case UNPAUSE:
				isPaused = false;
				messageSystem.postMessage(Message.GAME_RESUMED);
				break;
				
			case RESTART_GAME:
				setGameUp(1);
				messageSystem.postMessage(Message.GAME_RESUMED);
				break;
				
			default:
				break;
			}
		}
	}

	@Override
	public void recieveMessage(Message message, boolean extra) {
		if (!loss) {
			switch(message) {
			case LEFT:
				if (extra) {
					moveLeft();
				} 
				left = extra;
				break;
				
			case RIGHT:
				if (extra) {
					moveRight();
				}
				right = extra;
				break;
				
			case SOFT_DROP:
				down = extra;
				fallTimer = 0;
				
			default:
				break;
			}
		}
	}
	
	private void hardDrop() {
		if (!isPaused) {
			while(!gameGrid.intersects(activeTetromino.blockGrid, tetrominoPos.x, tetrominoPos.y-1)) {
				--tetrominoPos.y;
				score += 2;
			}
			
			messageSystem.postMessage(MessageSystem.Message.HARD_DROPPED);
			lockTetromino();
		}
	}

	private void moveRight() {
		if (!gameGrid.intersects(activeTetromino.blockGrid, tetrominoPos.x + 1, tetrominoPos.y) && !isPaused) {
			++tetrominoPos.x;
			
			resetLock();
			messageSystem.postMessage(MessageSystem.Message.SHIFTED);
		}
	}

	private void moveLeft() {
		if (!gameGrid.intersects(activeTetromino.blockGrid, tetrominoPos.x - 1, tetrominoPos.y) && !isPaused) {
			--tetrominoPos.x;
			
			resetLock();
			messageSystem.postMessage(MessageSystem.Message.SHIFTED);
		}
	}
	
	private void holdPiece() {
		if (!held && !isPaused) {
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

	private void rotateClockwise() {
		if (!isPaused) {
			Tetromino temp = new Tetromino(activeTetromino);
			temp.rotateClockwise();
	
			for (int offset = 0; offset < activeTetromino.offsetData[0].length; ++offset) {
				// Derive kick translations from offsets
				Vector2 offsetPreRot = new Vector2(activeTetromino.offsetData[activeTetromino.getRotationState().ordinal()][offset]);
				Vector2 offsetPostRot = activeTetromino.offsetData[temp.getRotationState().ordinal()][offset];
				Vector2 kickTranslation = offsetPreRot.sub(offsetPostRot);
	
				// If there isn't an intersection, then apply offset and be done
				if (!gameGrid.intersects(temp.blockGrid, tetrominoPos.x + kickTranslation.x, tetrominoPos.y + kickTranslation.y)) {
					tetrominoPos.add(kickTranslation);
	
					activeTetromino.rotateClockwise();
					ghostTetromino.rotateClockwise();
					resetLock();
					break;
				}
			}
		}
	}

	private void rotateCounterClockwise() {
		if (!isPaused) {
			Tetromino temp = new Tetromino(activeTetromino);
			temp.rotateCounterClockwise();
	
			for (int offset = 0; offset < activeTetromino.offsetData[0].length; ++offset) {
				// Derive kick translations from offsets
				Vector2 offsetPreRot = new Vector2(activeTetromino.offsetData[activeTetromino.getRotationState().ordinal()][offset]);
				Vector2 offsetPostRot = activeTetromino.offsetData[temp.getRotationState().ordinal()][offset];
				Vector2 kickTranslation = offsetPreRot.sub(offsetPostRot);
	
				// If there isn't an intersection, then apply offset and be done
				if (!gameGrid.intersects(temp.blockGrid, tetrominoPos.x + kickTranslation.x, tetrominoPos.y + kickTranslation.y)) {
					tetrominoPos.add(kickTranslation);
	
					activeTetromino.rotateCounterClockwise();
					ghostTetromino.rotateCounterClockwise();
					resetLock();
					break;
				}
			}
		}
	}
	
	private boolean lockTetromino() {
		// Check for loss conditions
		if (gameGrid.intersects(activeTetromino.blockGrid, tetrominoPos.x, tetrominoPos.y) ||
				spawnField.intersects(activeTetromino.blockGrid, tetrominoPos.x-1, tetrominoPos.y + BOARD_HEIGHT - 2 - BOARD_TOP_MARGIN) ) {
			loss = true;
			
			messageSystem.postMessage(MessageSystem.Message.GAME_OVER);
			return false;
		} else {
			// Check if the piece is above the visible portion of the playing field
			// Set all game grid blocks to be the same as the active tetromino's
			for (int x = 0; x < activeTetromino.blockGrid.getWidth(); ++x) {
				for (int y = 0; y < activeTetromino.blockGrid.getHeight(); ++y) {
					if (activeTetromino.blockGrid.getValue(x, y)) {
						gameGrid.setBlock(x + tetrominoPos.x, y + tetrominoPos.y, activeTetromino.blockGrid.getBlock(x, y));
					}
				}
			}
			
			clearFullLines();
			spawnTetromino();
			
			// Make it able for players to hold the piece again
			held = false;
			
			// Make lock resets available again
			lockResets = 0;
			
			return true;
		}
	}

	private void spawnTetromino() {
		spawnTetromino(tetrominoQueue.removeIndex(0));
		tetrominoQueue.add(factory.getPiece());
	}
	
	private void spawnTetromino(Tetromino t) {
		activeTetromino = t;

		tetrominoPos.x = BOARD_WIDTH / 2 - (activeTetromino.blockGrid.getWidth() + 1) / 2;
		tetrominoPos.y = BOARD_HEIGHT - 1 - activeTetromino.blockGrid.getHeight();
		
		ghostTetromino = factory.getPiece(activeTetromino.getName());
		ghostTetromino.blockGrid.setName(TetrominoNames.GHOST);
		
		tetrominoPos.add(Tetromino.spawnOffsets.get(activeTetromino.getName()));
	}
	
	private int clearFullLines() {
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
				messageSystem.postMessage(Message.ROW_CLEARED, y+lines-1);
				++lines;
				--y; // Decrement y so that it will check again
			}
		}
		
		if (lines == 4 && lastClear == 4) {
			score += scoreMultipliers[lines] * level * 1.5f;
			messageSystem.postMessage(MessageSystem.Message.ROWS_SCORED, MessageSystem.Extra.BACKTOBACK_SCORED);
			lastClear = lines;
			
			goal -= 12;
		} else if (lines > 0) {
			score += scoreMultipliers[lines] * level;
			lastClear = lines;
			
			switch (lines) {
			case 1:
				messageSystem.postMessage(MessageSystem.Message.ROWS_SCORED, MessageSystem.Extra.SINGLE_SCORED);
				goal -= 1;
				break;
				
			case 2:
				messageSystem.postMessage(MessageSystem.Message.ROWS_SCORED, MessageSystem.Extra.DOUBLE_SCORED);
				goal -= 3;
				break;
				
			case 3:
				messageSystem.postMessage(MessageSystem.Message.ROWS_SCORED, MessageSystem.Extra.TRIPLE_SCORED);
				goal -= 5;
				break;
				
			case 4:
				messageSystem.postMessage(MessageSystem.Message.ROWS_SCORED, MessageSystem.Extra.TETRIS_SCORED);
				goal -= 8;
				break;
				
			}
		}
		
		if (goal <= 0) {
			++level;
			calculateConstantsFromLevel();
			
			messageSystem.postMessage(Message.LEVEL_UP);
		}
		
		return lines;
	}

	private void clearLine(int y) {
		for (; y < BOARD_HEIGHT - 2; ++y) {
			for (int x = 1; x < BOARD_WIDTH; ++x) {
				gameGrid.setBlock(x, y, gameGrid.getBlock(x, y + 1));
			}
		}
	}
	
	private void calculateConstantsFromLevel() {		
		goal = level * 5;
		fallTime = (float) (1/(Math.pow(1.3, level-1)));
		softDropTime = (float) (0.068/Math.pow(1.15, level-1));
		lockTime = fallTime/2;
	}
	
	private void resetLock() {
		if (getGhostVector().y == tetrominoPos.y) {
			++lockResets;
			
			if (lockResets < MAX_LOCK_RESETS) {
				lockTimer = 0f;
			}
		}
	}
	
	private void setGameUp(int level) {
		heldTetromino = null;
		activeTetromino = null;
		
		loss = false;
		left = false;
		right = false;
		down = false;
		isPaused = false;
		
		fallTimer = 0f;
		lockTimer = 0f;
		moveTimer = 0f;
		
		lockResets = 0;
		lastClear = 0;
		score = 0;
		
		this.level = level;
		calculateConstantsFromLevel();
		
		factory.reset();
		
		// Make game grid
		gameGrid = new BlockGrid(BOARD_WIDTH, BOARD_HEIGHT);

		for (int x = 0; x < BOARD_WIDTH; ++x) {
			gameGrid.setValue(x, 0, true);
			gameGrid.setValue(x, BOARD_HEIGHT - 1, true);
		}

		for (int y = 0; y < BOARD_HEIGHT; ++y) {
			gameGrid.setValue(0, y, true);
			gameGrid.setValue(BOARD_WIDTH - 1, y, true);
		}
		
		spawnField = new BlockGrid(BOARD_WIDTH-2, BOARD_TOP_MARGIN);
		for (int x = 0; x < BOARD_WIDTH-2; ++x) {
			for (int y = 0; y < BOARD_TOP_MARGIN; ++y) {
				spawnField.setValue(x, y, true);
			}
		}
		
		tetrominoQueue = new Array<Tetromino>(QUEUE_LENGTH);

		for (int i = 0; i < QUEUE_LENGTH; ++i) {
			tetrominoQueue.add(factory.getPiece());
		}

		spawnTetromino();
	}
	
}
