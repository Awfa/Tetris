package com.anthonyha.tetris;

import com.anthonyha.tetris.Tetromino.TetrominoNames;

public interface TetrominoFactory {
	public Tetromino getPiece();
	public Tetromino getPiece(TetrominoNames tetrominoName);
	public void setSeed(long seed);
	public void reset();
	
}
