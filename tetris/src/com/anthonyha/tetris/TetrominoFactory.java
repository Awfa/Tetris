package com.anthonyha.tetris;

public interface TetrominoFactory {
	
	public Tetromino getPiece();
	public void setSeed(long seed);
	
}
