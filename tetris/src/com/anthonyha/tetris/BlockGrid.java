package com.anthonyha.tetris;

import java.util.ArrayList;

//Contains a collection of blocks in an x and y plane
public class BlockGrid {
	
	public ArrayList<ArrayList<Boolean>> blockGrid;
	
	private int width, height;
	
	//Initialize empty BlockGrid
	public BlockGrid(int width, int height) {
		blockGrid = new ArrayList<ArrayList<Boolean>>(width);
		this.width = width;
		this.height = height;
		
		for(int x = 0; x < width; ++x) {
			blockGrid.add(new ArrayList<Boolean>(height));
			for (int y = 0; y < height; ++y) {
				blockGrid.get(x).add(new Boolean(false));
			}
		}
	}
	
	//Getters for dimension
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	//Retrieve block value at coordinates (x,y)
	public boolean getValue(int x, int y) {
		//Check for boundaries
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return false;
		}
		
		return blockGrid.get(x).get(y);
	}
	
	//Set block value at coordinates (x,y)
	public boolean setValue(int x, int y, boolean value) {
		if (x >= 0 && y >= 0 && x < width && y < height) {
			blockGrid.get(x).set(y, value);
			return true;
		}
		
		return false;
	}
	
	//Tests for intersection with anther block grid in relation to itself
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
