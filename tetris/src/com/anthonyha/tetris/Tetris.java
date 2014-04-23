package com.anthonyha.tetris;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Tetris extends Game {

	public SpriteBatch batch;
	
	public TetrisSoundSystem tetrisSoundSystem;
	public TetrisInputSystem tetrisInputSystem;
	public MessageSystem messageSystem;
	
	public TextureAtlas gameAtlas;
	public TextureAtlas uiAtlas;
	
	// Quantico font sizes
	public BitmapFont quantico72;
	public BitmapFont quantico64;
	public BitmapFont quantico48;
	
	public TetrisUI tetrisUI;
	
	// Screens
	public TetrisMainMenu mainMenu;
	public TetrisGameScreen gameScreen;
	
	@Override
	public void create() {
		batch = new SpriteBatch();
		
		messageSystem = new MessageSystem();
		tetrisSoundSystem = new TetrisSoundSystem(messageSystem);
		tetrisInputSystem = new TetrisInputSystem(this);
		
		// Load up textures used by the game
		gameAtlas = new TextureAtlas(Gdx.files.internal("textures/TetrisGame.pack"));
		uiAtlas = new TextureAtlas(Gdx.files.internal("textures/UI.pack"));
		
		// Create fonts used by the game
		FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/quantico/Quantico-Regular.otf"));
		quantico72 = fontGenerator.generateFont(72);
		quantico64 = fontGenerator.generateFont(64);
		quantico48 = fontGenerator.generateFont(48);
		fontGenerator.dispose();
		
		tetrisUI = new TetrisUI(this);
		
		mainMenu = new TetrisMainMenu(this);
		gameScreen = new TetrisGameScreen(this);
		this.setScreen(mainMenu);
	}
	
	@Override
	public void render() {
		super.render();
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		tetrisSoundSystem.dispose();
		
		gameAtlas.dispose();
		uiAtlas.dispose();
		
		quantico72.dispose();
		quantico64.dispose();
		quantico48.dispose();
		
		mainMenu.dispose();
		gameScreen.dispose();
	}
}
