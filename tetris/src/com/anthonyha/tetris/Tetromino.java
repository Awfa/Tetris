package com.anthonyha.tetris;

public class Tetromino {
	public BlockGrid blockGrid;
	
	public Tetromino(BlockGrid contents) {
		blockGrid = contents;
	}
	
	public void rotateClockwise() {
		BlockGrid rotatedGrid = new BlockGrid(blockGrid.getHeight(), blockGrid.getWidth());
		
		for (int x = 0; x < blockGrid.getWidth(); ++x) {
			for (int y = 0; y < blockGrid.getHeight(); ++y) {
				rotatedGrid.setValue(y, x, blockGrid.getValue(blockGrid.getWidth()-x-1, y));
			}
		}
		blockGrid = rotatedGrid;
	}
	
	public void rotateCounterClockwise() {
		BlockGrid rotatedGrid = new BlockGrid(blockGrid.getHeight(), blockGrid.getWidth());
		
		for (int x = 0; x < blockGrid.getWidth(); ++x) {
			for (int y = 0; y < blockGrid.getHeight(); ++y) {
				rotatedGrid.setValue(y, x, blockGrid.getValue(x, blockGrid.getHeight()-y-1));
			}
		}
		blockGrid = rotatedGrid;
	}
	
}
