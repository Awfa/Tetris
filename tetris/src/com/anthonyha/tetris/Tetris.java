package com.anthonyha.tetris;

import com.anthonyha.tetris.MessageSystem.Message;
import com.anthonyha.tetris.Tetromino.TetrominoNames;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
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
import com.badlogic.gdx.utils.ObjectMap;

public class Tetris extends AbstractMessageListener implements ApplicationListener {
	private static final int scale = 32;
	
	private OrthographicCamera camera;
	private SpriteBatch spriteBatch;
	
	private BitmapFont quantico42;
	private BitmapFont quantico48;
	private BitmapFont quantico64;
	private BitmapFont quantico72;
	
	private TextureAtlas gameTextures;
	
	private Sprite background;
	private Sprite board;
	private Sprite dropShadow;
	private ObjectMap<TetrominoNames, Sprite> blockSprites;
	private ObjectMap<TetrominoNames, Sprite> queueOverlaySprites;
	
	private static final ObjectMap<TetrominoNames, Vector2> tetrominoRenderOffsets;
	static {
		tetrominoRenderOffsets = new ObjectMap<TetrominoNames, Vector2>();
		tetrominoRenderOffsets.put(TetrominoNames.I, new Vector2(8, 100));
		tetrominoRenderOffsets.put(TetrominoNames.O, new Vector2(40, 84));
		tetrominoRenderOffsets.put(TetrominoNames.T, new Vector2(24, 84));
		tetrominoRenderOffsets.put(TetrominoNames.S, new Vector2(24, 84));
		tetrominoRenderOffsets.put(TetrominoNames.Z, new Vector2(24, 84));
		tetrominoRenderOffsets.put(TetrominoNames.J, new Vector2(24, 84));
		tetrominoRenderOffsets.put(TetrominoNames.L, new Vector2(24, 84));
	}
	
	private static final int[] queueYOffsets = { 0, 228, 372 };
	
	private TetrisBoard gameBoard;
	
	private MessageSystem messageSystem;
	private TetrisSoundSystem tetrisSoundSystem;
	
	ParticleEffectPool tetrisExplosionEffectPool;
	Array<PooledEffect> effects;
	
	@Override
	public void create() {
		float w = 1920f;
		float h = 1080f;
		
		messageSystem = new MessageSystem();
		messageSystem.add(this, Message.SOFT_DROPPED);
		messageSystem.add(this, Message.HARD_DROPPED);
		messageSystem.add(this, Message.SHIFTED);
		messageSystem.add(this, Message.ROW_CLEARED);
		messageSystem.add(this, Message.ROWS_SCORED);
		
		spriteBatch = new SpriteBatch();
		
		// Texture loading
		gameTextures = new TextureAtlas(Gdx.files.internal("textures/TetrisGame.pack"));
		
		// Mapping each tetromino name to its respective sprite
		blockSprites = new ObjectMap<TetrominoNames, Sprite>();
		blockSprites.put(TetrominoNames.I, gameTextures.createSprite("I"));
		blockSprites.put(TetrominoNames.O, gameTextures.createSprite("O"));
		blockSprites.put(TetrominoNames.T, gameTextures.createSprite("T"));
		blockSprites.put(TetrominoNames.S, gameTextures.createSprite("S"));
		blockSprites.put(TetrominoNames.Z, gameTextures.createSprite("Z"));
		blockSprites.put(TetrominoNames.J, gameTextures.createSprite("J"));
		blockSprites.put(TetrominoNames.L, gameTextures.createSprite("L"));
		blockSprites.put(TetrominoNames.GHOST, gameTextures.createSprite("Ghost"));
		
		queueOverlaySprites = new ObjectMap<TetrominoNames, Sprite>();
		queueOverlaySprites.put(TetrominoNames.I, gameTextures.createSprite("IOverlay"));
		queueOverlaySprites.put(TetrominoNames.O, gameTextures.createSprite("OOverlay"));
		queueOverlaySprites.put(TetrominoNames.T, gameTextures.createSprite("TOverlay"));
		queueOverlaySprites.put(TetrominoNames.S, gameTextures.createSprite("SOverlay"));
		queueOverlaySprites.put(TetrominoNames.Z, gameTextures.createSprite("ZOverlay"));
		queueOverlaySprites.put(TetrominoNames.J, gameTextures.createSprite("JOverlay"));
		queueOverlaySprites.put(TetrominoNames.L, gameTextures.createSprite("LOverlay"));
		queueOverlaySprites.put(TetrominoNames.GHOST, gameTextures.createSprite("GhostOverlay"));
		
		for (Sprite overlay : queueOverlaySprites.values()) {
			overlay.setPosition(1136, 1080-468);
		}
		
		dropShadow = gameTextures.createSprite("Shadow");
		
		// Create background and board sprites
		background = gameTextures.createSprite("Background");
		board = gameTextures.createSprite("Board");
		
		// Create game model
		gameBoard = new TetrisBoard(13, 1, messageSystem);

		// Create camera and center it
		camera = new OrthographicCamera();
		camera.setToOrtho(false, w, h);
		camera.translate(-(w - background.getRegionWidth()) / 2f, -(h - background.getRegionHeight()) / 2f);
		camera.update();

		// Create fonts used by the game
		FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/quantico/Quantico-Regular.otf"));
		quantico42 = fontGenerator.generateFont(42);
		quantico48 = fontGenerator.generateFont(48);
		quantico64 = fontGenerator.generateFont(64);
		quantico72 = fontGenerator.generateFont(72);
		fontGenerator.dispose();
		
		// Create particle effects
		ParticleEffect tetrisExplosionEffect = new ParticleEffect();
		tetrisExplosionEffect.load(Gdx.files.internal("effects/BlockExplosion.p"), gameTextures);
		tetrisExplosionEffectPool = new ParticleEffectPool(tetrisExplosionEffect, 1, 2);
		effects = new Array<PooledEffect>();
		
		tetrisSoundSystem = new TetrisSoundSystem(messageSystem);
		tetrisSoundSystem.setSfxVolume(0.5f);
		Gdx.input.setInputProcessor(new TetrisInputSystem(messageSystem));
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		
		quantico48.dispose();
		quantico72.dispose();
		
		gameTextures.dispose();
		tetrisSoundSystem.dispose();
	}

	@Override
	public void render() {
		Tetromino tetromino;
		TetrominoNames blockName;
		Sprite blockSprite;
		Sprite overlaySprite = queueOverlaySprites.get(gameBoard.tetrominoQueue.get(0).getName());
		
		StringBuilder stringBuilder = new StringBuilder();
		
		Gdx.gl.glClearColor(1f, 1f, 1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		gameBoard.update(Gdx.graphics.getDeltaTime());
		
		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();
		
		// Render board
		spriteBatch.draw(board, 800, 1080-860);
		
		// Render drop shadows for game grid
		for (int x = 1; x < gameBoard.gameGrid.getWidth()-1; ++x) {
			for (int y = 1; y < gameBoard.gameGrid.getHeight()-4; ++y) {
				if (gameBoard.gameGrid.getValue(x, y)) {
					dropShadow.setPosition((x - 1) * scale + 800 - 15, (y - 1) * scale + 1080-860 - 15);
					dropShadow.draw(spriteBatch);
				}
			}
		}
		
		// Render drop shadows for active tetromino
		tetromino = gameBoard.activeTetromino;
		blockName = tetromino.getName();
		blockSprite = blockSprites.get(blockName);
		for (int x = 0; x < tetromino.blockGrid.getWidth(); ++x) {
			for (int y = 0; y < tetromino.blockGrid.getHeight(); ++y) {
				if (tetromino.blockGrid.getValue(x, y)) {
					dropShadow.setPosition((gameBoard.tetrominoPos.x + x - 1) * scale + 800 - 15,
							(gameBoard.tetrominoPos.y + y - 1) * scale + 1080-860 - 15);
					dropShadow.draw(spriteBatch);
				}
			}
		}
		
		// Render game grid
		for (int x = 1; x < gameBoard.gameGrid.getWidth()-1; ++x) {
			for (int y = 1; y < gameBoard.gameGrid.getHeight()-4; ++y) {
				if (gameBoard.gameGrid.getValue(x, y)) {
					blockName = gameBoard.gameGrid.getBlock(x, y).name;
					blockSprite = blockSprites.get(blockName);
					
					blockSprite.setPosition((x - 1) * scale + 800, (y - 1) * scale + 1080-860);
					blockSprite.draw(spriteBatch);
				}
			}
		}
		
		// Render ghost piece
		blockSprite = blockSprites.get(TetrominoNames.GHOST);
		for (int x = 0; x < tetromino.blockGrid.getWidth(); ++x) {
			for (int y = 0; y < tetromino.blockGrid.getHeight(); ++y) {
				if (tetromino.blockGrid.getValue(x, y)) {
					blockSprite.setPosition((gameBoard.getGhostVector().x + x - 1) * scale + 800,
							(gameBoard.getGhostVector().y + y - 1) * scale + 1080-860);
					blockSprite.draw(spriteBatch);
				}
			}
		}
		
		// Render active tetromino
		blockName = tetromino.getName();
		blockSprite = blockSprites.get(blockName);
		for (int x = 0; x < tetromino.blockGrid.getWidth(); ++x) {
			for (int y = 0; y < tetromino.blockGrid.getHeight(); ++y) {
				if (tetromino.blockGrid.getValue(x, y)) {
					blockSprite.setPosition((gameBoard.tetrominoPos.x + x - 1) * scale + 800,
							(gameBoard.tetrominoPos.y + y - 1) * scale + 1080-860);
					blockSprite.draw(spriteBatch);
				}
			}
		}
		
		// Draw the background over the board so it cuts off the piece when it spawns above the game board area
		// This is why we draw the background after the board, grid, and active tetromino
		background.draw(spriteBatch);
		
		// Render score
		quantico48.draw(spriteBatch, "SCORE:", 874, 1080-30);
		
		// Format the score to take 6 digits and a comma in between
		String score = String.valueOf(gameBoard.getScore());
		for (int i = 0; i < 6 - score.length(); ++i) {
			stringBuilder.append(0);
		}
		stringBuilder.append(score);
		quantico72.draw(spriteBatch, stringBuilder.toString(), 818, 1080-100);
		
		// Render particle effects
		for (int i = effects.size - 1; i >= 0; i--) {
		    PooledEffect effect = effects.get(i);
		    effect.draw(spriteBatch, Gdx.graphics.getDeltaTime());
		    if (effect.isComplete()) {
		        effect.free();
		        effects.removeIndex(i);
		    }
		}
		
		// Render hold
		quantico42.draw(spriteBatch, "Hold", 658, 1080-154);
		
		if (gameBoard.heldTetromino != null) {
			tetromino = gameBoard.heldTetromino;
			blockName = tetromino.getName();
			blockSprite = blockSprites.get(blockName);
			
			overlaySprite = queueOverlaySprites.get(blockName);
			overlaySprite.setPosition(624, 1080-452);
			overlaySprite.draw(spriteBatch);
			
			// Render shadow
			for (int x = 0; x < tetromino.blockGrid.getWidth(); ++x) {
				for (int y = 0; y < tetromino.blockGrid.getHeight(); ++y) {
					if (tetromino.blockGrid.getValue(x, y)) {
						int posX = (x - Tetromino.origins.get(blockName).x) * scale + 632 + tetrominoRenderOffsets.get(blockName).x;
						int posY = (y - Tetromino.origins.get(blockName).y) * scale + 1080-452 + tetrominoRenderOffsets.get(blockName).y;
						
						dropShadow.setPosition(posX - 15, posY - 15);
						dropShadow.draw(spriteBatch);
					}
				}
			}
			
			// Render tetromino
			for (int x = 0; x < tetromino.blockGrid.getWidth(); ++x) {
				for (int y = 0; y < tetromino.blockGrid.getHeight(); ++y) {
					if (tetromino.blockGrid.getValue(x, y)) {
						int posX = (x - Tetromino.origins.get(blockName).x) * scale + 632 + tetrominoRenderOffsets.get(blockName).x;
						int posY = (y - Tetromino.origins.get(blockName).y) * scale + 1080-452 + tetrominoRenderOffsets.get(blockName).y;
						
						blockSprite.setPosition(posX, posY);
						blockSprite.draw(spriteBatch);
					}
				}
			}
		} else {
			overlaySprite = queueOverlaySprites.get(TetrominoNames.GHOST);
			overlaySprite.setPosition(624, 1080-452);
			overlaySprite.draw(spriteBatch);
		}
		
		
		// Render queue
		quantico42.draw(spriteBatch,  "Queue", 1153, 1080-154);
		
		overlaySprite = queueOverlaySprites.get(gameBoard.tetrominoQueue.get(0).getName());
		overlaySprite.setPosition(1136, 1080-452);
		overlaySprite.draw(spriteBatch);
		// Render shadows
		for (int i = 0; i < gameBoard.tetrominoQueue.size; ++i) {
			tetromino = gameBoard.tetrominoQueue.get(i);
			blockName = tetromino.getName();
			blockSprite = blockSprites.get(blockName);
			
			for (int x = 0; x < tetromino.blockGrid.getWidth(); ++x) {
				for (int y = 0; y < tetromino.blockGrid.getHeight(); ++y) {
					if (tetromino.blockGrid.getValue(x, y)) {
						dropShadow.setPosition((x - Tetromino.origins.get(blockName).x) * scale + 1144 + tetrominoRenderOffsets.get(blockName).x - 15,
								(y - Tetromino.origins.get(blockName).y) * scale + 1080-452 + tetrominoRenderOffsets.get(blockName).y - queueYOffsets[i] - 15);
						dropShadow.draw(spriteBatch);
					}
				}
			}
		}
		
		// Render tetrominos
		for (int i = 0; i < gameBoard.tetrominoQueue.size; ++i) {
			tetromino = gameBoard.tetrominoQueue.get(i);
			blockName = tetromino.getName();
			blockSprite = blockSprites.get(blockName);
			
			for (int x = 0; x < tetromino.blockGrid.getWidth(); ++x) {
				for (int y = 0; y < tetromino.blockGrid.getHeight(); ++y) {
					if (tetromino.blockGrid.getValue(x, y)) {
						blockSprite.setPosition((x - Tetromino.origins.get(blockName).x) * scale + 1144 + tetrominoRenderOffsets.get(blockName).x,
								(y - Tetromino.origins.get(blockName).y) * scale + 1080-452 + tetrominoRenderOffsets.get(blockName).y - queueYOffsets[i]);
						blockSprite.draw(spriteBatch);
					}
				}
			}
		}
		
		// Render score
		String level = String.valueOf(gameBoard.getLevel());
		stringBuilder.delete(0, stringBuilder.length());
		for (int i = 0; i < 2 - level.length(); ++i) {
			stringBuilder.append(0);
		}
		stringBuilder.append(level);
		
		quantico42.draw(spriteBatch, "Level", 654, 1080-506);
		quantico64.draw(spriteBatch, stringBuilder.toString(), 665,1080-598);
		
		// Render goal
		String goal = String.valueOf(gameBoard.getGoal());
		stringBuilder.delete(0, stringBuilder.length());
		for (int i = 0; i < 2 - goal.length(); ++i) {
			stringBuilder.append(0);
		}
		stringBuilder.append(goal);
		quantico42.draw(spriteBatch, "Goal", 658, 1080-720);
		quantico64.draw(spriteBatch, stringBuilder.toString(), 660,1080-814);
		
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
	
	@Override
	public void recieveMessage(MessageSystem.Message message, MessageSystem.Extra extra) {
		switch(message) {
		case ROWS_SCORED:
			switch(extra) {
			case SINGLE_SCORED:
				
				break;
			case DOUBLE_SCORED:
				
				break;
			case TRIPLE_SCORED:
				
				break;
			case TETRIS_SCORED:
				
				break;
			case BACKTOBACK_SCORED:
				
				break;
			case TSPIN_SCORED:
				
				break;
			}
			break;
			
		default:
			break;
		}
	}
	
}
