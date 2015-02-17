package com.anthonyha.tetris;

public class Vector2 {
	/** Integers x and y represent the integer coordinates of the vector class **/
	public int x, y;

	/** Creates a Vector2 with the given x and y integers
	 * @param x the horizontal component of the coordinate
	 * @param y the vertical component of the coordinate
	 */
	public Vector2(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/** Constructs a Vector2 as a copy of the given vector
	 * @param v The vector to copy
	 * Postcondition:
	 *	-Vector v is unchanged
	 *	-This vector is updated with new values
	 */
	public Vector2(Vector2 v) {
		x = v.x;
		y = v.y;
	}

	/** Adds the vector v's components onto this vector
	 * @param v The vector to add onto this vector
	 * @return itself
	 * Postcondition:
	 * 	-Vector v is unchanged
	 * 	-This vector is updated with new values
	 */
	public Vector2 add(Vector2 v) {
		x += v.x;
		y += v.y;

		return this;
	}

	/** Subtracts the vector v's components onto this vector
	 * @param v The vector to subtract from this vector
	 * @return itself
	 * Postcondition:
	 * 	-Vector v is unchanged
	 * 	-This vector is updated with new values
	 */
	public Vector2 sub(Vector2 v) {
		x -= v.x;
		y -= v.y;

		return this;
	}
}
