package com.anthonyha.tetris;

import com.anthonyha.tetris.MessageSystem.Message;
import com.anthonyha.tetris.Tetromino.TetrominoNames;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class TetrisGameScreen extends AbstractMessageListener implements Screen {
	private static final int scale = 32;
	private final Tetris game;
	
	private OrthographicCamera camera;

	private Sprite background;
	private Sprite board;
	private Sprite dropShadow;
	private Image pauseOverlay;
	
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
	private static final float pauseTime = 0.3f;
	
	private TetrisBoard gameBoard;
	
	private Stage stage;
	private Table pauseMenu;
	
	private boolean isPaused = false;
	
	ParticleEffectPool tetrisExplosionEffectPool;
	Array<PooledEffect> effects;
	
	public TetrisGameScreen(final Tetris game) {
		this.game = game;
		
		float w = 1920f;
		float h = 1080f;
		
		game.messageSystem.add(this, Message.SOFT_DROPPED);
		game.messageSystem.add(this, Message.HARD_DROPPED);
		game.messageSystem.add(this, Message.SHIFTED);
		game.messageSystem.add(this, Message.ROW_CLEARED);
		game.messageSystem.add(this, Message.ROWS_SCORED);
		game.messageSystem.add(this, Message.GAME_PAUSED);
		game.messageSystem.add(this, Message.GAME_RESUMED);
		
		
		// Mapping each tetromino name to its respective sprite
		blockSprites = new ObjectMap<TetrominoNames, Sprite>();
		blockSprites.put(TetrominoNames.I, game.gameAtlas.createSprite("I"));
		blockSprites.put(TetrominoNames.O, game.gameAtlas.createSprite("O"));
		blockSprites.put(TetrominoNames.T, game.gameAtlas.createSprite("T"));
		blockSprites.put(TetrominoNames.S, game.gameAtlas.createSprite("S"));
		blockSprites.put(TetrominoNames.Z, game.gameAtlas.createSprite("Z"));
		blockSprites.put(TetrominoNames.J, game.gameAtlas.createSprite("J"));
		blockSprites.put(TetrominoNames.L, game.gameAtlas.createSprite("L"));
		blockSprites.put(TetrominoNames.GHOST, game.gameAtlas.createSprite("Ghost"));
		
		queueOverlaySprites = new ObjectMap<TetrominoNames, Sprite>();
		queueOverlaySprites.put(TetrominoNames.I, game.gameAtlas.createSprite("IOverlay"));
		queueOverlaySprites.put(TetrominoNames.O, game.gameAtlas.createSprite("OOverlay"));
		queueOverlaySprites.put(TetrominoNames.T, game.gameAtlas.createSprite("TOverlay"));
		queueOverlaySprites.put(TetrominoNames.S, game.gameAtlas.createSprite("SOverlay"));
		queueOverlaySprites.put(TetrominoNames.Z, game.gameAtlas.createSprite("ZOverlay"));
		queueOverlaySprites.put(TetrominoNames.J, game.gameAtlas.createSprite("JOverlay"));
		queueOverlaySprites.put(TetrominoNames.L, game.gameAtlas.createSprite("LOverlay"));
		queueOverlaySprites.put(TetrominoNames.GHOST, game.gameAtlas.createSprite("GhostOverlay"));
		
		messageSprites = new ObjectMap<MessageSystem.Extra, Sprite>();
		messageSprites.put(MessageSystem.Extra.DOUBLE_SCORED, game.gameAtlas.createSprite("Double"));
		messageSprites.put(MessageSystem.Extra.TRIPLE_SCORED, game.gameAtlas.createSprite("Triple"));
		messageSprites.put(MessageSystem.Extra.TETRIS_SCORED, game.gameAtlas.createSprite("Tetris"));
		messageSprites.put(MessageSystem.Extra.BACKTOBACK_SCORED, game.gameAtlas.createSprite("BackToBack"));
		
		
		// Create sprites
		background = game.gameAtlas.createSprite("Background");
		board = game.gameAtlas.createSprite("Board");
		dropShadow = game.gameAtlas.createSprite("Shadow");
		
		// Create game model
		gameBoard = new TetrisBoard(13, 1, game.messageSystem);

		// Create camera and center it
		camera = new OrthographicCamera();
		camera.setToOrtho(false, w, h);
		camera.translate(-(w - background.getRegionWidth()) / 2f, -(h - background.getRegionHeight()) / 2f);
		camera.update();
		
		// Create particle effects
		ParticleEffect tetrisExplosionEffect = new ParticleEffect();
		tetrisExplosionEffect.load(Gdx.files.internal("effects/BlockExplosion.p"), game.gameAtlas);
		tetrisExplosionEffectPool = new ParticleEffectPool(tetrisExplosionEffect, 1, 2);
		effects = new Array<PooledEffect>();
		
		// Set the stage
		stage = new Stage(w, h, true);
		stage.setCamera(camera);
		
		pauseOverlay = new Image(game.gameAtlas.findRegion("PauseOverlay"));
		stage.addActor(pauseOverlay);
		
		pauseOverlay.setBounds(0, 0, stage.getWidth(), stage.getHeight());
		pauseOverlay.setColor(Color.CLEAR);
		
		// Add pause menu
		pauseMenu = new Table();
		
		pauseMenu.setFillParent(true);
		
		Label pauseHeader = new Label("Pause", game.tetrisUI.pauseHeaderStyle);
		pauseHeader.setAlignment(Align.center);
		
		TextButton resumeButton = new TextButton("Resume", game.tetrisUI.darkButtonStyle);
		TextButton restartButton = new TextButton("Restart", game.tetrisUI.darkButtonStyle);
		TextButton optionsButton = new TextButton("Options", game.tetrisUI.darkButtonStyle);
		TextButton mainMenuButton = new TextButton("Back to Menu", game.tetrisUI.darkButtonStyle);
		
		resumeButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.messageSystem.postMessage(Message.UNPAUSE);
			}
		});
		
		restartButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.messageSystem.postMessage(Message.RESTART_GAME);
			}
		});
		
		mainMenuButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.messageSystem.postMessage(Message.GAME_PAUSED);
				game.setScreen(game.mainMenu);
			}
		});
		
		AtlasRegion pauseMenuFillerTexture = game.uiAtlas.findRegion("PauseMenuFiller"); // Texture to go inbetween each button
		pauseMenu.top();
		pauseMenu.add(pauseHeader).width(TetrisUI.buttonWidth).padTop(TetrisUI.spacingTop);
		pauseMenu.row(); pauseMenu.add(new Image(pauseMenuFillerTexture)).width(TetrisUI.buttonWidth - 16); pauseMenu.row();
		pauseMenu.add(resumeButton).width(TetrisUI.buttonWidth);
		pauseMenu.row(); pauseMenu.add(new Image(pauseMenuFillerTexture)).width(TetrisUI.buttonWidth - 16); pauseMenu.row();
		pauseMenu.add(restartButton).width(TetrisUI.buttonWidth);
		pauseMenu.row(); pauseMenu.add(new Image(pauseMenuFillerTexture)).width(TetrisUI.buttonWidth - 16); pauseMenu.row();
		//pauseMenu.add(optionsButton).width(TetrisUI.buttonWidth);
		//pauseMenu.row(); pauseMenu.add(new Image(pauseMenuFillerTexture)).width(TetrisUI.buttonWidth - 16); pauseMenu.row();
		pauseMenu.add(mainMenuButton).width(TetrisUI.buttonWidth).spaceBottom(TetrisUI.spacing);
		pauseMenu.setVisible(false);
		stage.addActor(pauseMenu);
		
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	@Override
	public void render(float delta) {
		Tetromino tetromino;
		TetrominoNames blockName;
		Sprite overlaySprite = queueOverlaySprites.get(gameBoard.tetrominoQueue.get(0).getName());
		
		Gdx.gl.glClearColor(1f, 1f, 1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		gameBoard.update(delta);
		
		
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		
		game.batch.draw(board, xBoardOffset, yBoardOffset); // Render board
		
		drawBlockGrid(gameBoard.ghostTetromino.blockGrid, gameBoard.getGhostVector().x, gameBoard.getGhostVector().y); // Render ghost piece
		drawBlockGrid(gameBoard.gameGrid, 0, 0); // Render game grid		
		drawBlockGrid(gameBoard.activeTetromino.blockGrid, gameBoard.tetrominoPos.x, gameBoard.tetrominoPos.y); // Render active tetromino
		
		background.draw(game.batch);
		
		// Render hold		
		if (gameBoard.heldTetromino != null) {
			tetromino = gameBoard.heldTetromino;
			blockName = tetromino.getName();
			
			overlaySprite = queueOverlaySprites.get(blockName);
			overlaySprite.setPosition(620, 1080-444);
			overlaySprite.draw(game.batch);

			drawBlockGrid(tetromino.blockGrid, -Tetromino.origins.get(blockName).x + 1, -Tetromino.origins.get(blockName).y + 1, 632 + tetrominoRenderOffsets.get(blockName).x, 1080-452 + tetrominoRenderOffsets.get(blockName).y);
			
		} else {
			overlaySprite = queueOverlaySprites.get(TetrominoNames.GHOST);
			overlaySprite.setPosition(620, 1080-444);
			overlaySprite.draw(game.batch);
		}
		
		
		// Render queue
		overlaySprite = queueOverlaySprites.get(gameBoard.tetrominoQueue.get(0).getName());
		overlaySprite.setPosition(1132, 1080-444);
		overlaySprite.draw(game.batch);

		for (int i = 0; i < gameBoard.tetrominoQueue.size; ++i) {
			tetromino = gameBoard.tetrominoQueue.get(i);
			blockName = tetromino.getName();
			
			drawBlockGrid(tetromino.blockGrid, -Tetromino.origins.get(blockName).x + 1, -Tetromino.origins.get(blockName).y + 1, 1144 + tetrominoRenderOffsets.get(blockName).x, 1080-452 + tetrominoRenderOffsets.get(blockName).y - queueYOffsets[i]);
		}
		
		// Render particle effects
		for (int i = effects.size - 1; i >= 0; i--) {
		    PooledEffect effect = effects.get(i);
		    effect.draw(game.batch, delta);
		    if (effect.isComplete()) {
		        effect.free();
		        effects.removeIndex(i);
		    }
		}

		game.quantico72.draw(game.batch, padNumber(gameBoard.getScore(), 6), 818, 1080-125);
		game.quantico64.draw(game.batch, padNumber(gameBoard.getLevel(), 2), 665, 1080-598);
		game.quantico64.draw(game.batch, padNumber(gameBoard.getGoal(), 2), 660, 1080-814);
		
		game.batch.end();
		
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
		
		stage.setViewport(width, height, true);
		pauseMenu.setPosition((background.getRegionWidth() - width) / 2f, background.getRegionHeight() - height);
		
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
	public void recieveMessage(Message message) {
		switch(message) {
		case GAME_PAUSED:
			isPaused = true;
			pauseOverlay.addAction(Actions.alpha(1f, pauseTime, Interpolation.linear));
			
			for (Action a : pauseMenu.getActions()) {
				pauseMenu.removeAction(a);
			}
			pauseMenu.addAction(Actions.sequence(Actions.show(), Actions.fadeIn(pauseTime)));
			
			break;
		case GAME_RESUMED:
			isPaused = false;
			
			pauseOverlay.addAction(Actions.alpha(0f, pauseTime, Interpolation.linear));
			
			for (Action a : pauseMenu.getActions()) {
				pauseMenu.removeAction(a);
			}
			pauseMenu.addAction(Actions.sequence(Actions.fadeOut(pauseTime), Actions.hide()));
			
			break;
			
		default:
			break;
		}
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
						Actions.moveTo(halfPoint - actor.getWidth() / 2 - 10, yBoardOffset + 500, 0.2f, Interpolation.exp5In),
						Actions.moveTo(halfPoint - actor.getWidth() / 2 + 10, yBoardOffset + 500, 0.5f, Interpolation.linear),
						Actions.moveTo(stage.getWidth() + actor.getWidth(), yBoardOffset + 500, 0.2f, Interpolation.exp5Out),
						Actions.removeActor()
						));
				
				break;
			case TSPIN_SCORED:
				
				break;
			}
			break;
			
		default:
			break;
		}
	}
	
	public boolean isPaused() {
		return isPaused;
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
		blockSprite.draw(game.batch);
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

	@Override
	public void show() {
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, game.tetrisInputSystem));
		game.messageSystem.postMessage(Message.UNPAUSE);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}
	
}
