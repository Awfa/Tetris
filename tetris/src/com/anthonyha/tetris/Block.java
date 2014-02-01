package com.anthonyha.tetris;

import com.anthonyha.tetris.Tetromino.TetrominoNames;

public class Block {
	public static final Block EMPTY_BLOCK = new Block();
	public TetrominoNames name;
	public boolean state;

	public Block() {
		name = TetrominoNames.GHOST;
		state = false;
	}
	
	public Block(TetrominoNames name) {
		this.name = name;
		state = false;
	}
}
