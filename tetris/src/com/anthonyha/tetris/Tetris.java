package com.anthonyha.tetris;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Tetris implements ApplicationListener {
	private OrthographicCamera camera;
	private ShapeRenderer shapeRenderer;
	
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
		shapeRenderer.translate(w/2-scale*(gameBoard.gameGrid.getWidth()/2), h/2-scale*(gameBoard.gameGrid.getHeight()/2), 0);
		shapeRenderer.scale(scale, scale, 1);
		
		

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
		
		shapeRenderer.setProjectionMatrix(camera.combined);
		
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		
		//Render GameBoard
		
		
		for (int x = 0; x < gameBoard.gameGrid.getWidth(); ++x) {
			for (int y = 0; y < gameBoard.gameGrid.getHeight(); ++y) {
				if (gameBoard.gameGrid.getValue(x, y)) {
					shapeRenderer.setColor(gameBoard.gameGrid.getBlock(x, y).color);
					shapeRenderer.rect(x, y, 1, 1);
				}
			}
		}
		
		//Render Active Tetromino
		for (int x = 0; x < gameBoard.activeTetromino.blockGrid.getWidth(); ++x) {
			for (int y = 0; y < gameBoard.activeTetromino.blockGrid.getHeight(); ++y) {
				if (gameBoard.activeTetromino.blockGrid.getValue(x, y)) {
					shapeRenderer.setColor(gameBoard.activeTetromino.blockGrid.getBlock(x, y).color);
					shapeRenderer.rect(x+gameBoard.tetrominoX, y+gameBoard.tetrominoY, 1, 1);
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
}
