package com.anthonyha.tetris;

import com.badlogic.gdx.graphics.Color;

public class Block {
	public static final Block EMPTY_BLOCK = new Block();
	public Color color;
	public boolean state;

	public Block() {
		color = Color.BLACK;
		state = false;
	}
}
