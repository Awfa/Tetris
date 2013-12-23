package com.anthonyha.tetris;

import java.util.Collections;
import java.util.Random;
import java.util.ArrayList;

public class RandomTetrominoFactory implements TetrominoFactory {
	
	private Random generator = new Random();
	private ArrayList<TetrominoNames> bag = new ArrayList<TetrominoNames>(7);
	
	public enum TetrominoNames {
		I, O, T, S, Z, J, L
	}
	
	public RandomTetrominoFactory() {
		generateGrabBag();
	}
	
	@Override
	public Tetromino getPiece() {
		TetrominoNames tetrominoName;
		BlockGrid grid;
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
				grid = new BlockGrid(4, 4);
				grid.setValue(0, 2, true);
				grid.setValue(1, 2, true);
				grid.setValue(2, 2, true);
				grid.setValue(3, 2, true);
				break;
			case O:
				grid = new BlockGrid(2, 2);
				grid.setValue(0, 0, true);
				grid.setValue(0, 1, true);
				grid.setValue(1, 0, true);
				grid.setValue(1, 1, true);
				break;
			case T:
				grid = new BlockGrid(3, 3);
				grid.setValue(0, 1, true);
				grid.setValue(1, 1, true);
				grid.setValue(1, 2, true);
				grid.setValue(2, 1, true);
				break;
			case S:
				grid = new BlockGrid(3, 3);
				grid.setValue(0, 1, true);
				grid.setValue(1, 1, true);
				grid.setValue(1, 2, true);
				grid.setValue(2, 2, true);
				break;
			case Z:
				grid = new BlockGrid(3, 3);
				grid.setValue(0, 2, true);
				grid.setValue(1, 1, true);
				grid.setValue(1, 2, true);
				grid.setValue(2, 1, true);
				break;
			case J:
				grid = new BlockGrid(3, 3);
				grid.setValue(0, 1, true);
				grid.setValue(0, 2, true);
				grid.setValue(1, 1, true);
				grid.setValue(2, 1, true);
				break;
			case L:
				grid = new BlockGrid(3, 3);
				grid.setValue(0, 1, true);
				grid.setValue(1, 1, true);
				grid.setValue(2, 1, true);
				grid.setValue(2, 2, true);
				break;
			default: //Debug block
				grid = new BlockGrid(1, 1);
				grid.setValue(0, 0, true);
				break;
		}
		return new Tetromino(grid);
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
