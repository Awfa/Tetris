package com.anthonyha.tetris;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Tetris implements ApplicationListener, InputProcessor {
	private OrthographicCamera camera;
	private ShapeRenderer shapeRenderer;
	private SpriteBatch spriteBatch;
	private BitmapFont bitmapFont;

	private TetrisBoard gameBoard;

	@Override
	public void create() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		int scale = 30;
		gameBoard = new TetrisBoard(scale);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, w, h);

		shapeRenderer = new ShapeRenderer();
		shapeRenderer.translate(w / 2 - scale * (gameBoard.gameGrid.getWidth() / 2), h / 2 - scale * (gameBoard.gameGrid.getHeight() / 2), 0);
		shapeRenderer.scale(scale, scale, 1);

		spriteBatch = new SpriteBatch();

		bitmapFont = new BitmapFont();
		bitmapFont.setColor(Color.BLACK);

		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void dispose() {
		shapeRenderer.dispose();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		gameBoard.update(Gdx.graphics.getDeltaTime());

		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();

		// Render score
		bitmapFont.draw(spriteBatch, "Score: " + gameBoard.getScore(),
				350, Gdx.graphics.getHeight() - 10);

		spriteBatch.end();

		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		
		// Render background
		shapeRenderer.setColor(Color.GRAY);
		shapeRenderer.rect(1, 1, gameBoard.gameGrid.getWidth()-2, gameBoard.gameGrid.getHeight()-4);
		
		
		// Render GameBoard
		for (int x = 1; x < gameBoard.gameGrid.getWidth()-1; ++x) {
			for (int y = 1; y < gameBoard.gameGrid.getHeight()-4; ++y) {
				if (gameBoard.gameGrid.getValue(x, y)) {
					shapeRenderer.setColor(gameBoard.gameGrid.getBlock(x, y).color);
					shapeRenderer.rect(x, y, 1, 1);
				}
			}
		}

		// Render Active Tetromino
		for (int x = 0; x < gameBoard.activeTetromino.blockGrid.getWidth(); ++x) {
			for (int y = 0; y < gameBoard.activeTetromino.blockGrid.getHeight(); ++y) {
				if (gameBoard.activeTetromino.blockGrid.getValue(x, y)) {
					shapeRenderer.setColor(gameBoard.activeTetromino.blockGrid.getBlock(x, y).color);
					shapeRenderer.rect(x + gameBoard.tetrominoX, y + gameBoard.tetrominoY, 1, 1);
				}
			}
		}
		
		// Render Held Piece
		if (gameBoard.heldTetromino != null) {
			for (int x = 0; x < gameBoard.heldTetromino.blockGrid.getWidth(); ++x) {
				for (int y = 0; y < gameBoard.heldTetromino.blockGrid.getHeight(); ++y) {
					if (gameBoard.heldTetromino.blockGrid.getValue(x, y)) {
						shapeRenderer.setColor(gameBoard.heldTetromino.blockGrid.getBlock(x, y).color);
						shapeRenderer.rect(x-6, y+19, 1, 1);
					}
				}
			}
		}
		
		// Render Queue
		int queueDisplacement = 0;
		for (Iterator<Tetromino> iter = gameBoard.tetrominoQueue.iterator(); iter.hasNext(); ++queueDisplacement) {
			Tetromino t = iter.next();
			for (int x = 0; x < t.blockGrid.getWidth(); ++x) {
				for (int y = 0; y < t.blockGrid.getHeight(); ++y) {
					if (t.blockGrid.getValue(x, y)) {
						shapeRenderer.setColor(t.blockGrid.getBlock(x, y).color);
						shapeRenderer.rect(x+13, y+19 - (queueDisplacement*4), 1, 1);
					}
				}
			}
		}
		
		shapeRenderer.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	// Input Processing
	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.A) {
			gameBoard.left = true;
			gameBoard.moveLeft();
			return true;
		} else if (keycode == Keys.D) {
			gameBoard.right = true;
			gameBoard.moveRight();
			return true;
		} else if (keycode == Keys.S) {
			gameBoard.down = true;
		} else if (keycode == Keys.Q) {
			gameBoard.rotateCounterClockwise();
		} else if (keycode == Keys.E) {
			gameBoard.rotateClockwise();
		} else if (keycode == Keys.W) {
			gameBoard.hardDrop();
		} else if (keycode == Keys.SHIFT_LEFT) {
			gameBoard.holdPiece();
		}

		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.A) {
			gameBoard.left = false;
			return true;
		} else if (keycode == Keys.D) {
			gameBoard.right = false;
			return true;
		} else if (keycode == Keys.S) {
			gameBoard.down = false;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}
