package com.anthonyha.tetris;

import com.anthonyha.tetris.MessageSystem.Message;
import com.anthonyha.tetris.Tetromino.TetrominoNames;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class Tetris extends AbstractMessageListener implements ApplicationListener {
	private static final int scale = 32;
	
	private OrthographicCamera camera;
	private SpriteBatch spriteBatch;
	
	private BitmapFont quantico64;
	private BitmapFont quantico72;
	
	private TextureAtlas gameTextures;
	
	private Sprite background;
	private Sprite board;
	private Sprite dropShadow;
	private ObjectMap<TetrominoNames, Sprite> blockSprites;
	private ObjectMap<TetrominoNames, Sprite> queueOverlaySprites;
	private ObjectMap<MessageSystem.Extra, Sprite> messageSprites;
	
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
	private static final int xBoardOffset = 800;
	private static final int yBoardOffset = 1080-860;
	private static final int essentialWidth = 700;
	private static final int essentialHeight = 900;
	
	private TetrisBoard gameBoard;
	
	private MessageSystem messageSystem;
	private TetrisSoundSystem tetrisSoundSystem;
	
	private Stage stage;
	
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
		
		messageSprites = new ObjectMap<MessageSystem.Extra, Sprite>();
		messageSprites.put(MessageSystem.Extra.DOUBLE_SCORED, gameTextures.createSprite("double"));
		messageSprites.put(MessageSystem.Extra.TRIPLE_SCORED, gameTextures.createSprite("triple"));
		messageSprites.put(MessageSystem.Extra.TETRIS_SCORED, gameTextures.createSprite("tetris"));
		messageSprites.put(MessageSystem.Extra.BACKTOBACK_SCORED, gameTextures.createSprite("backtoback"));
		
		
		// Create sprites
		background = gameTextures.createSprite("Background");
		board = gameTextures.createSprite("Board");
		dropShadow = gameTextures.createSprite("Shadow");
		
		// Create game model
		gameBoard = new TetrisBoard(13, 1, messageSystem);

		// Create camera and center it
		camera = new OrthographicCamera();
		camera.setToOrtho(false, w, h);
		camera.translate(-(w - background.getRegionWidth()) / 2f, -(h - background.getRegionHeight()) / 2f);
		camera.update();

		// Create fonts used by the game
		FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/quantico/Quantico-Regular.otf"));
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
		
		// Set the stage
		stage = new Stage(w, h, true);
		stage.setCamera(camera);
		
		Gdx.input.setInputProcessor(new TetrisInputSystem(messageSystem));
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		
		quantico64.dispose();
		quantico72.dispose();
		
		gameTextures.dispose();
		tetrisSoundSystem.dispose();
		stage.dispose();
	}

	@Override
	public void render() {
		Tetromino tetromino;
		TetrominoNames blockName;
		Sprite overlaySprite = queueOverlaySprites.get(gameBoard.tetrominoQueue.get(0).getName());
		
		Gdx.gl.glClearColor(1f, 1f, 1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		gameBoard.update(Gdx.graphics.getDeltaTime());
		
		
		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();
		
		spriteBatch.draw(board, xBoardOffset, yBoardOffset); // Render board
		
		drawBlockGrid(gameBoard.gameGrid, 0, 0); // Render game grid
		drawBlockGrid(gameBoard.ghostTetromino.blockGrid, gameBoard.getGhostVector().x, gameBoard.getGhostVector().y); // Render ghost piece		
		drawBlockGrid(gameBoard.activeTetromino.blockGrid, gameBoard.tetrominoPos.x, gameBoard.tetrominoPos.y); // Render active tetromino
		
		background.draw(spriteBatch);
		
		// Render hold		
		if (gameBoard.heldTetromino != null) {
			tetromino = gameBoard.heldTetromino;
			blockName = tetromino.getName();
			
			overlaySprite = queueOverlaySprites.get(blockName);
			overlaySprite.setPosition(620, 1080-444);
			overlaySprite.draw(spriteBatch);

			drawBlockGrid(tetromino.blockGrid, -Tetromino.origins.get(blockName).x + 1, -Tetromino.origins.get(blockName).y + 1, 632 + tetrominoRenderOffsets.get(blockName).x, 1080-452 + tetrominoRenderOffsets.get(blockName).y);
			
		} else {
			overlaySprite = queueOverlaySprites.get(TetrominoNames.GHOST);
			overlaySprite.setPosition(620, 1080-444);
			overlaySprite.draw(spriteBatch);
		}
		
		
		// Render queue
		overlaySprite = queueOverlaySprites.get(gameBoard.tetrominoQueue.get(0).getName());
		overlaySprite.setPosition(1132, 1080-444);
		overlaySprite.draw(spriteBatch);

		for (int i = 0; i < gameBoard.tetrominoQueue.size; ++i) {
			tetromino = gameBoard.tetrominoQueue.get(i);
			blockName = tetromino.getName();
			
			drawBlockGrid(tetromino.blockGrid, -Tetromino.origins.get(blockName).x + 1, -Tetromino.origins.get(blockName).y + 1, 1144 + tetrominoRenderOffsets.get(blockName).x, 1080-452 + tetrominoRenderOffsets.get(blockName).y - queueYOffsets[i]);
		}
		
		// Render particle effects
		for (int i = effects.size - 1; i >= 0; i--) {
		    PooledEffect effect = effects.get(i);
		    effect.draw(spriteBatch, Gdx.graphics.getDeltaTime());
		    if (effect.isComplete()) {
		        effect.free();
		        effects.removeIndex(i);
		    }
		}

		quantico72.draw(spriteBatch, padNumber(gameBoard.getScore(), 6), 818, 1080-125);
		quantico64.draw(spriteBatch, padNumber(gameBoard.getLevel(), 2), 665, 1080-598);
		quantico64.draw(spriteBatch, padNumber(gameBoard.getGoal(), 2), 660, 1080-814);
		
		spriteBatch.end();
		
		stage.act();
		stage.draw();
	}
	
	@Override
	public void resize(int width, int height) {
		double aspectRatio = (double) width / height;

		if (width < essentialWidth) {
			height = (int) (essentialWidth / aspectRatio);
			width = essentialWidth;
		}
		
		if (height < essentialHeight) {
			width = (int) (essentialHeight * aspectRatio);
			height = essentialHeight;
		}
		
		camera.setToOrtho(false, width, height);
		camera.translate((background.getRegionWidth() - width) / 2f, background.getRegionHeight() - height);
		camera.update();
		
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
			float halfPoint = (xBoardOffset + 5.f * scale);
			Actor actor;
			
			switch(extra) {
			case SINGLE_SCORED:
				
				break;
			case DOUBLE_SCORED:
			case TRIPLE_SCORED:
			case TETRIS_SCORED:
			case BACKTOBACK_SCORED:
				actor = new Image(messageSprites.get(extra));
				stage.addActor(actor);
				actor.addAction(Actions.sequence(Actions.moveTo(-actor.getWidth(), yBoardOffset + 500f),
						Actions.moveTo(halfPoint - actor.getWidth() / 2 - 10, yBoardOffset + 500, 0.2f, Interpolation.swingIn),
						Actions.moveTo(halfPoint - actor.getWidth() / 2 + 10, yBoardOffset + 500, 0.5f, Interpolation.linear),
						Actions.moveTo(stage.getWidth(), yBoardOffset + 500, 0.2f, Interpolation.swingOut),
						Actions.removeActor()));
				break;
			case TSPIN_SCORED:
				
				break;
			}
			break;
			
		default:
			break;
		}
	}
	
	private String padNumber(int num, int pad) {
		StringBuilder stringBuilder = new StringBuilder();
		String numberString = String.valueOf(num);
		for (int i = 0; i < pad - numberString.length(); ++i) {
			stringBuilder.append(0);
		}
		stringBuilder.append(numberString);
		return stringBuilder.toString();
	}
	
	private void drawBlock(Sprite blockSprite, int x, int y) {
		blockSprite.setPosition(x, y);
		blockSprite.draw(spriteBatch);
	}
	
	private void drawBlockGrid(BlockGrid grid, int xGrid, int yGrid) {
		drawBlockGrid(grid, xGrid, yGrid, xBoardOffset, yBoardOffset);
	}
	
	private void drawBlockGrid(BlockGrid grid, int xGrid, int yGrid, int xOrigin, int yOrigin) {
		Sprite blockSprite;
		
		for (int x = 0; x < grid.getWidth(); ++x) {
			for (int y = 0; y < grid.getHeight(); ++y) {
				if (grid.getValue(x, y) && grid.getBlock(x, y).name != TetrominoNames.GHOST) {
					drawBlock(dropShadow, (x + xGrid - 1) * scale + xOrigin - 15, (y + yGrid - 1) * scale + yOrigin - 15);
				}
			}
		}
		
		for (int x = 0; x < grid.getWidth(); ++x) {
			for (int y = 0; y < grid.getHeight(); ++y) {
				if (grid.getValue(x, y)) {
					blockSprite = blockSprites.get(grid.getBlock(x, y).name);
					drawBlock(blockSprite, (x + xGrid - 1) * scale + xOrigin, (y + yGrid - 1) * scale + yOrigin);
				}
			}
		}
	}
	
}
