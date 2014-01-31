package com.anthonyha.tetris;

import java.util.HashMap;
import java.util.Map;

import com.anthonyha.tetris.MessageSystem.Message;
import com.anthonyha.tetris.Tetromino.TetrominoNames;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Array;

public class Tetris implements ApplicationListener, InputProcessor, MessageListener {
	private OrthographicCamera camera;
	private SpriteBatch spriteBatch;
	
	private BitmapFont quantico48;
	private BitmapFont quantico72;
	
	private TextureAtlas gameTextures;
	
	private Sprite background;
	private Sprite board;
	private Map<TetrominoNames, Sprite> blockSprites;
	
	private TetrisBoard gameBoard;
	private MessageSystem messageSystem;
	
	ParticleEffectPool tetrisExplosionEffectPool;
	Array<PooledEffect> effects;
	
	@Override
	public void create() {
		float w = 1920f;
		float h = 1080f;

		int scale = 32;
		
		messageSystem = new MessageSystem();
		messageSystem.add(this, Message.ROW_CLEARED);
		
		spriteBatch = new SpriteBatch();
		
		// Texture loading
		gameTextures = new TextureAtlas(Gdx.files.internal("textures/TetrisGame.pack"));
		
		// Mapping each tetromino name to its respective sprite
		blockSprites = new HashMap<TetrominoNames, Sprite>();
		blockSprites.put(TetrominoNames.I, gameTextures.createSprite("I"));
		blockSprites.put(TetrominoNames.O, gameTextures.createSprite("O"));
		blockSprites.put(TetrominoNames.T, gameTextures.createSprite("T"));
		blockSprites.put(TetrominoNames.S, gameTextures.createSprite("S"));
		blockSprites.put(TetrominoNames.Z, gameTextures.createSprite("Z"));
		blockSprites.put(TetrominoNames.J, gameTextures.createSprite("J"));
		blockSprites.put(TetrominoNames.L, gameTextures.createSprite("L"));
		
		// Create background and board sprites
		background = gameTextures.createSprite("Background");
		board = gameTextures.createSprite("Board");
		
		// Create game model
		gameBoard = new TetrisBoard(scale, messageSystem);

		// Create camera and center it
		camera = new OrthographicCamera();
		camera.setToOrtho(false, w, h);
		camera.translate(-(w - background.getRegionWidth()) / 2f, -(h - background.getRegionHeight()) / 2f);
		camera.update();

		// Create fonts used by the game
		FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/quantico/Quantico-Bold.otf"));
		quantico48 = fontGenerator.generateFont(48);
		quantico72 = fontGenerator.generateFont(72);
		fontGenerator.dispose();
		
		// Create particle effects
		ParticleEffect tetrisExplosionEffect = new ParticleEffect();
		tetrisExplosionEffect.load(Gdx.files.internal("effects/BlockExplosion.p"), gameTextures);
		tetrisExplosionEffectPool = new ParticleEffectPool(tetrisExplosionEffect, 1, 2);
		effects = new Array<PooledEffect>();
		
		Gdx.input.setInputProcessor(this);
		
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		
		quantico48.dispose();
		quantico72.dispose();
		
		gameTextures.dispose();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1f, 1f, 1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		gameBoard.update(Gdx.graphics.getDeltaTime());
		
		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();
		
		// Render Board
		spriteBatch.draw(board, 800, 1080-860);
		
		// Render GameBoard
		for (int x = 1; x < gameBoard.gameGrid.getWidth()-1; ++x) {
			for (int y = 1; y < gameBoard.gameGrid.getHeight()-4; ++y) {
				if (gameBoard.gameGrid.getValue(x, y)) {
					Sprite block = blockSprites.get(gameBoard.gameGrid.getBlock(x, y).name);
					
					block.setPosition((x - 1) * block.getWidth() + 800, (y - 1) * block.getHeight() + 1080-860);
					block.draw(spriteBatch);
				}
			}
		}
		
		// Render Active Tetromino
		Sprite activeBlockSprite = blockSprites.get(gameBoard.activeTetromino.getName());
		for (int x = 0; x < gameBoard.activeTetromino.blockGrid.getWidth(); ++x) {
			for (int y = 0; y < gameBoard.activeTetromino.blockGrid.getHeight(); ++y) {
				if (gameBoard.activeTetromino.blockGrid.getValue(x, y)) {
					activeBlockSprite.setPosition((gameBoard.tetrominoPos.x + x - 1) * activeBlockSprite.getWidth() + 800, (gameBoard.tetrominoPos.y + y - 1) * activeBlockSprite.getHeight() + 1080-860);
					activeBlockSprite.draw(spriteBatch);
				}
			}
		}
		
		background.draw(spriteBatch);
		
		// Render Score
		quantico48.draw(spriteBatch, "SCORE:", 874, 1080-30);
		
		// Format the score to take 6 digits and a comma in between
		String score = String.valueOf(gameBoard.getScore());
		StringBuilder scoreBuilder = new StringBuilder();
		for (int i = 0; i < 6 - score.length(); ++i) {
			scoreBuilder.append(0);
		}
		scoreBuilder.append(score);
		//scoreBuilder.insert(3, ',');
		
		quantico72.draw(spriteBatch, scoreBuilder.toString(), 818, 1080-100);
		
		for (int i = effects.size - 1; i >= 0; i--) {
		    PooledEffect effect = effects.get(i);
		    effect.draw(spriteBatch, Gdx.graphics.getDeltaTime());
		    if (effect.isComplete()) {
		        effect.free();
		        effects.removeIndex(i);
		    }
		}
		
		spriteBatch.end();
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
		} else if (keycode == Keys.ESCAPE) {
			Gdx.graphics.setDisplayMode(1366, 768, false);
		} else if (keycode == Keys.BACKSPACE) {
			Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width, Gdx.graphics.getDesktopDisplayMode().height, true);
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
	
	@Override
	public void recieveMessage(MessageSystem.Message message) {
		
	}
	
	@Override
	public void recieveMessage(MessageSystem.Message message, int extra) {
		switch(message) {
		case ROW_CLEARED:
			PooledEffect effect = tetrisExplosionEffectPool.obtain();
			effect.setPosition(800+160, 1080-860+extra*32);
			effects.add(effect);
			break;
		default:
			break;
		}
	}
}
