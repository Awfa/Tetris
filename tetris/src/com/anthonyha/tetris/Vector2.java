package com.anthonyha.tetris;

public class Vector2 {
	public int x, y;

	public Vector2(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Vector2(Vector2 v) {
		x = v.x;
		y = v.y;
	}

	public Vector2 add(Vector2 v) {
		x += v.x;
		y += v.y;

		return this;
	}

	public Vector2 sub(Vector2 v) {
		x -= v.x;
		y -= v.y;

		return this;
	}
}
