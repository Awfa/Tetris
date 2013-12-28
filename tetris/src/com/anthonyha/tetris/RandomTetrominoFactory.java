package com.anthonyha.tetris;

import java.util.Collections;
import java.util.Random;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;

public class RandomTetrominoFactory implements TetrominoFactory {
	
	public static final Vector2[][] jlstzOffsets =
		{
			{new Vector2(0, 0), new Vector2(0, 0), new Vector2(0, 0), new Vector2(0, 0), new Vector2(0, 0) },
			{new Vector2(0, 0), new Vector2(1, 0), new Vector2(1, -1), new Vector2(0, 2), new Vector2(1, 2) },
			{new Vector2(0, 0), new Vector2(0, 0), new Vector2(0, 0), new Vector2(0, 0), new Vector2(0, 0) },
			{new Vector2(0, 0), new Vector2(-1, 0), new Vector2(-1, -1), new Vector2(0, 2), new Vector2(-1, 2) }
		};
	
	public static final Vector2[][] iOffsets =
		{
			{new Vector2(0, 0), new Vector2(-1, 0), new Vector2(2, 0), new Vector2(-1, 0), new Vector2(2, 0) },
			{new Vector2(-1, 0), new Vector2(0, 0), new Vector2(0, 0), new Vector2(0, 1), new Vector2(0, -2) },
			{new Vector2(-1, 1), new Vector2(1, 1), new Vector2(-2, 1), new Vector2(1, 0), new Vector2(-2, 0) },
			{new Vector2(0, 1), new Vector2(0, 1), new Vector2(0, 1), new Vector2(0, -1), new Vector2(0, 2) }
		};
	public static final Vector2[][] oOffsets =
		{
			{new Vector2(0, 0) },
			{new Vector2(0, -1) },
			{new Vector2(-1, -1) },
			{new Vector2(-1, 0) }
		};
	
	private Random generator = new Random();
	private ArrayList<TetrominoNames> bag = new ArrayList<TetrominoNames>(7);
	
	public enum TetrominoNames {
		I, O, T, S, Z, J, L
	}
	
	@Override
	public Tetromino getPiece() {
		TetrominoNames tetrominoName;
		BlockGrid grid = null;
		
		//Refill the bag if it's empty
		if (bag.isEmpty()) {
			generateGrabBag();
		}
		
		//Grab a name and remove it from the bag
		tetrominoName = bag.get(0);
		bag.remove(0);
		
		//Generate piece from name
		switch (tetrominoName) {
		case I:
			grid = new BlockGrid(5, 5);
			grid.setValue(1, 2, true, Color.CYAN);
			grid.setValue(2, 2, true, Color.CYAN);
			grid.setValue(3, 2, true, Color.CYAN);
			grid.setValue(4, 2, true, Color.CYAN);
			break;
		case O:
			grid = new BlockGrid(3, 3);
			grid.setValue(1, 1, true, Color.YELLOW);
			grid.setValue(1, 2, true, Color.YELLOW);
			grid.setValue(2, 1, true, Color.YELLOW);
			grid.setValue(2, 2, true, Color.YELLOW);
			break;
		case T:
			grid = new BlockGrid(3, 3);
			grid.setValue(0, 1, true, Color.MAGENTA);
			grid.setValue(1, 1, true, Color.MAGENTA);
			grid.setValue(1, 2, true, Color.MAGENTA);
			grid.setValue(2, 1, true, Color.MAGENTA);
			break;
		case S:
			grid = new BlockGrid(3, 3);
			grid.setValue(0, 1, true, Color.GREEN);
			grid.setValue(1, 1, true, Color.GREEN);
			grid.setValue(1, 2, true, Color.GREEN);
			grid.setValue(2, 2, true, Color.GREEN);
			break;
		case Z:
			grid = new BlockGrid(3, 3);
			grid.setValue(0, 2, true, Color.RED);
			grid.setValue(1, 1, true, Color.RED);
			grid.setValue(1, 2, true, Color.RED);
			grid.setValue(2, 1, true, Color.RED);
			break;
		case J:
			grid = new BlockGrid(3, 3);
			grid.setValue(0, 1, true, Color.BLUE);
			grid.setValue(0, 2, true, Color.BLUE);
			grid.setValue(1, 1, true, Color.BLUE);
			grid.setValue(2, 1, true, Color.BLUE);
			break;
		case L:
			grid = new BlockGrid(3, 3);
			grid.setValue(0, 1, true, Color.ORANGE);
			grid.setValue(1, 1, true, Color.ORANGE);
			grid.setValue(2, 1, true, Color.ORANGE);
			grid.setValue(2, 2, true, Color.ORANGE);
			break;
		}
		
		//Generate offset data from name
		switch (tetrominoName) {
		case J:
		case L:
		case S:
		case T:
		case Z:
			return new Tetromino(grid, jlstzOffsets);
		case I:
			return new Tetromino(grid, iOffsets);
		case O:
			return new Tetromino(grid, oOffsets);
		}
		return null;
		
	}

	@Override
	public void setSeed(long seed) {
		generator.setSeed(seed);
	}
	
	private void generateGrabBag() {
		bag.add(TetrominoNames.I);
		bag.add(TetrominoNames.O);
		bag.add(TetrominoNames.T);
		bag.add(TetrominoNames.S);
		bag.add(TetrominoNames.Z);
		bag.add(TetrominoNames.J);
		bag.add(TetrominoNames.L);
		
		Collections.shuffle(bag, generator);
	}
	
}
