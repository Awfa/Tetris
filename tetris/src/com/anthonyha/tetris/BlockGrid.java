package com.anthonyha.tetris;

import java.util.ArrayList;

import com.anthonyha.tetris.Tetromino.TetrominoNames;

//Contains a collection of blocks in an x and y plane
public class BlockGrid {

	private int width, height;

	public ArrayList<ArrayList<Block>> blockGrid;

	// Initialize empty BlockGrid
	public BlockGrid(int width, int height) {
		blockGrid = new ArrayList<ArrayList<Block>>(width);
		this.width = width;
		this.height = height;

		for (int x = 0; x < width; ++x) {
			blockGrid.add(new ArrayList<Block>(height));
			for (int y = 0; y < height; ++y) {
				blockGrid.get(x).add(new Block());
			}
		}
	}
	
	public BlockGrid(int width, int height, TetrominoNames name) {
		blockGrid = new ArrayList<ArrayList<Block>>(width);
		this.width = width;
		this.height = height;

		for (int x = 0; x < width; ++x) {
			blockGrid.add(new ArrayList<Block>(height));
			for (int y = 0; y < height; ++y) {
				blockGrid.get(x).add(new Block());
				blockGrid.get(x).get(y).name = name;
			}
		}
	}

	// Getters for dimension
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	// Retrieve block value at coordinates (x,y)
	public boolean getValue(int x, int y) {
		// Check for boundaries
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return false;
		}

		return blockGrid.get(x).get(y).state;
	}

	// Retrieve block at coordinates (x,y)
	public Block getBlock(int x, int y) {
		// Check for boundaries
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return Block.EMPTY_BLOCK;
		}

		return blockGrid.get(x).get(y);
	}
	
	// Set block value at coordinates (x,y)
	public boolean setValue(int x, int y, boolean value) {
		if (x >= 0 && y >= 0 && x < width && y < height) {
			blockGrid.get(x).get(y).state = value;
			return true;
		}

		return false;
	}
	
	// Set block value at coordinates (x,y) and also the tetromino name
	public boolean setValue(int x, int y, boolean value, TetrominoNames name) {
		if (x >= 0 && y >= 0 && x < width && y < height) {
			blockGrid.get(x).get(y).state = value;
			blockGrid.get(x).get(y).name = name;
			return true;
		}

		return false;
	}

	// Set block at coordinates (x,y)
	public boolean setBlock(int x, int y, Block block) {
		if (x >= 0 && y >= 0 && x < width && y < height) {
			blockGrid.get(x).set(y, block);
			return true;
		}

		return false;
	}
	
	// Set name of all the blocks
	public void setName(TetrominoNames name) {
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				getBlock(x, y).name = name;
			}
		}
	}

	// Tests for intersection with anther block grid in relation to itself
	public boolean intersects(BlockGrid otherBlock, int relX, int relY) {
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				if (getValue(x, y) == true && otherBlock.getValue(x - relX, y - relY) == true) {
					return true;
				}
			}
		}

		return false;
	}

}
