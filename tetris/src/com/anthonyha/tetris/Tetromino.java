package com.anthonyha.tetris;

import java.util.*;

public class Tetromino {

	private RotationState rotationState;
	private TetrominoNames name;
	
	public enum TetrominoNames {
		I, O, T, S, Z, J, L, NOTHING
	}
	
	public static final Map<TetrominoNames, Vector2> spawnOffsets;
	static {
		spawnOffsets = new HashMap<TetrominoNames, Vector2>();
		spawnOffsets.put(TetrominoNames.I, new Vector2(0, 0));
		spawnOffsets.put(TetrominoNames.O, new Vector2(0, -1));
		spawnOffsets.put(TetrominoNames.T, new Vector2(0, -1));
		spawnOffsets.put(TetrominoNames.S, new Vector2(0, -1));
		spawnOffsets.put(TetrominoNames.Z, new Vector2(0, -1));
		spawnOffsets.put(TetrominoNames.J, new Vector2(0, -1));
		spawnOffsets.put(TetrominoNames.L, new Vector2(0, -1));
	}
	
	public static final Map<TetrominoNames, Vector2> origins;
	static {
		origins = new HashMap<TetrominoNames, Vector2>();
		origins.put(TetrominoNames.I, new Vector2(1, 2));
		origins.put(TetrominoNames.O, new Vector2(1, 1));
		origins.put(TetrominoNames.T, new Vector2(0, 1));
		origins.put(TetrominoNames.S, new Vector2(0, 1));
		origins.put(TetrominoNames.Z, new Vector2(0, 1));
		origins.put(TetrominoNames.J, new Vector2(0, 1));
		origins.put(TetrominoNames.L, new Vector2(0, 1));
	}
	
	public enum RotationState {
		UP, RIGHT, DOWN, LEFT
	}

	public BlockGrid blockGrid;
	public Vector2[][] offsetData;

	public Tetromino(BlockGrid contents, Vector2[][] offsetData, TetrominoNames name) {
		blockGrid = contents;
		this.offsetData = offsetData;
		rotationState = RotationState.UP;
		this.name = name;
	}

	public Tetromino(Tetromino t) {
		blockGrid = t.blockGrid;
		offsetData = t.offsetData;
		rotationState = t.getRotationState();
	}

	public Tetromino rotateClockwise() {
		BlockGrid rotatedGrid = new BlockGrid(blockGrid.getHeight(), blockGrid.getWidth());

		for (int x = 0; x < blockGrid.getWidth(); ++x) {
			for (int y = 0; y < blockGrid.getHeight(); ++y) {
				rotatedGrid.setBlock(y, x, blockGrid.getBlock(blockGrid.getWidth() - x - 1, y));
			}
		}
		rotationState = RotationState.values()[(rotationState.ordinal() + 1) % RotationState.values().length];
		blockGrid = rotatedGrid;

		return this;
	}

	public Tetromino rotateCounterClockwise() {
		BlockGrid rotatedGrid = new BlockGrid(blockGrid.getHeight(), blockGrid.getWidth());

		for (int x = 0; x < blockGrid.getWidth(); ++x) {
			for (int y = 0; y < blockGrid.getHeight(); ++y) {
				rotatedGrid.setBlock(y, x, blockGrid.getBlock(x, blockGrid.getHeight() - y - 1));
			}
		}
		rotationState = RotationState.values()[(rotationState.ordinal() + 3) % RotationState.values().length];
		blockGrid = rotatedGrid;

		return this;
	}

	public RotationState getRotationState() {
		return rotationState;
	}
	
	public TetrominoNames getName() {
		return name;
	}

}
