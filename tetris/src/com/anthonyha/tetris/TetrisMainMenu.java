package com.anthonyha.tetris;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class TetrisMainMenu implements Screen {
	private final Tetris game;
	private Stage stage;
	private TextureAtlas atlas;
	private BitmapFont quantico;
	
	public TetrisMainMenu(final Tetris game) {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		float buttonWidth = 432.f;
		
		this.game = game;
		
		stage = new Stage();
		
		Gdx.input.setInputProcessor(stage);
		
		FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/quantico/Quantico-Bold.otf"));
		quantico = fontGenerator.generateFont(48);
		fontGenerator.dispose();
		
		atlas = new TextureAtlas(Gdx.files.internal("textures/MainMenu.pack"));
		
		Table table = new Table();
		table.setFillParent(true);
		stage.addActor(table);
		
		TextButtonStyle normalButtonStyle = new TextButtonStyle();
		normalButtonStyle.up = new NinePatchDrawable(atlas.createPatch("grayButtonUp"));
		normalButtonStyle.down = new NinePatchDrawable(atlas.createPatch("grayButtonDown"));
		normalButtonStyle.font = quantico;
		normalButtonStyle.fontColor = new Color(0.28f, 0.28f, 0.28f, 1.f);
		normalButtonStyle.pressedOffsetY = -8.f;
		
		TextButtonStyle playButtonStyle = new TextButtonStyle(normalButtonStyle);
		playButtonStyle.fontColor = Color.BLACK;
		
		TextButton playButton = new TextButton("PLAY GAME", playButtonStyle);
		TextButton optionButton = new TextButton("Options", normalButtonStyle);
		TextButton exitButton = new TextButton("Exit", normalButtonStyle);
		
		table.bottom();
		table.add(playButton).width(buttonWidth).spaceBottom(32f);
		table.row();
		table.add(optionButton).width(buttonWidth).spaceBottom(32f);
		table.row();
		table.add(exitButton).width(buttonWidth).padBottom(32f);
		
		playButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(new TetrisGameScreen(game));
				dispose();
			}
		});
		
		optionButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				
			}
		});
		
		exitButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.exit();
			}
		});
		
		
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.78f, 0.78f, 0.78f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height);
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		stage.dispose();
		quantico.dispose();
		atlas.dispose();
	}
	
}
