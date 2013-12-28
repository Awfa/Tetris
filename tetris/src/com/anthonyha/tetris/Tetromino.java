package com.anthonyha.tetris;

public class Tetromino {

	private RotationState rotationState;

	public enum RotationState {
		UP, RIGHT, DOWN, LEFT
	}

	public BlockGrid blockGrid;
	public Vector2[][] offsetData;

	public Tetromino(BlockGrid contents, Vector2[][] offsetData) {
		blockGrid = contents;
		this.offsetData = offsetData;
		rotationState = RotationState.UP;
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

}
