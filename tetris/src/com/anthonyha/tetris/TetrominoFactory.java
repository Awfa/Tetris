package com.anthonyha.tetris;

public interface TetrominoFactory {
	
	public enum TetrominoNames {
		I, O, T, S, Z, J, L
	}
	
	public Tetromino getPiece();
	public Tetromino getPiece(TetrominoNames tetrominoName);
	public void setSeed(long seed);
	
}
