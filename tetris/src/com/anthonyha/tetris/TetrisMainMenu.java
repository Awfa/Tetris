package com.anthonyha.tetris;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class TetrisMainMenu implements Screen {
	private final Tetris game;
	private Stage stage;
	
	public TetrisMainMenu(final Tetris game) {
		final Table mainMenu = new Table();
		final Table optionsMenu = new Table();
		float buttonWidth = 432f;
		float spacing = 32f;
		float spacingTop = 64f;
		
		this.game = game;
		
		stage = new Stage();
		
		Gdx.input.setInputProcessor(stage);
		
		mainMenu.setFillParent(true);
		optionsMenu.setFillParent(true);
		
		stage.addActor(mainMenu);
		stage.addActor(optionsMenu);		
		
		TextButtonStyle normalButtonStyle = new TextButtonStyle();
		normalButtonStyle.up = new NinePatchDrawable(game.uiAtlas.createPatch("ButtonUp"));
		normalButtonStyle.down = new NinePatchDrawable(game.uiAtlas.createPatch("ButtonDown"));
		normalButtonStyle.font = game.quantico48;
		normalButtonStyle.fontColor = new Color(0.28f, 0.28f, 0.28f, 1.f);
		
		TextButtonStyle playButtonStyle = new TextButtonStyle(normalButtonStyle);
		playButtonStyle.fontColor = Color.BLACK;
		
		SliderStyle optionsSliderStyle = new SliderStyle();
		optionsSliderStyle.background = new NinePatchDrawable(game.uiAtlas.createPatch("Slider"));
		optionsSliderStyle.knob = new TextureRegionDrawable(game.uiAtlas.findRegion("SliderKnob"));
		
		LabelStyle optionsLabelStyle = new LabelStyle();
		optionsLabelStyle.background = new NinePatchDrawable(game.uiAtlas.createPatch("ButtonUp"));
		optionsLabelStyle.font = game.quantico48;
		optionsLabelStyle.fontColor = new Color(0.28f, 0.28f, 0.28f, 1.f);
		
		LabelStyle optionsHeaderStyle = new LabelStyle(optionsLabelStyle);
		optionsHeaderStyle.font = game.quantico64;
		optionsHeaderStyle.fontColor = Color.BLACK;
		
		// Filling the main menu
		Image mainMenuTitle = new Image(game.uiAtlas.findRegion("title"));
		TextButton playButton = new TextButton("Play Game", playButtonStyle);
		TextButton optionButton = new TextButton("Options", normalButtonStyle);
		TextButton exitButton = new TextButton("Exit", normalButtonStyle);
		
		mainMenu.top();
		mainMenu.add(mainMenuTitle).padTop(spacingTop).spaceBottom(spacing);
		mainMenu.row();
		mainMenu.add(playButton).width(buttonWidth).spaceBottom(spacing);
		mainMenu.row();
		mainMenu.add(optionButton).width(buttonWidth).spaceBottom(spacing);
		mainMenu.row();
		mainMenu.add(exitButton).width(buttonWidth).padBottom(spacing);
		
		// Filling the options menu
		Label optionsTitle = new Label("Options", optionsHeaderStyle);
		Label musicVolume = new Label("Music Vol: 10", optionsLabelStyle);
		Label sfxVolume = new Label("SFX Vol: 10", optionsLabelStyle);
		
		optionsTitle.setAlignment(Align.center);
		musicVolume.setAlignment(Align.right);
		sfxVolume.setAlignment(Align.right);
		
		Slider musicSlider = new Slider(0.f, 10.f, 0.1f, false, optionsSliderStyle);
		Slider sfxSlider = new Slider(0.f, 10.f, 0.1f, false, optionsSliderStyle);
		
		TextButton backButton = new TextButton("Back", normalButtonStyle);
		
		musicSlider.setValue(10.f);
		sfxSlider.setValue(10.f);
		
		optionsMenu.top();
		optionsMenu.add(optionsTitle).width(buttonWidth).padTop(spacingTop).spaceBottom(spacing);
		optionsMenu.row();
		optionsMenu.add(musicVolume).width(buttonWidth).right();
		optionsMenu.add(musicSlider).width(buttonWidth);
		optionsMenu.row();
		optionsMenu.add(sfxVolume).width(buttonWidth).right().spaceBottom(spacing);
		optionsMenu.add(sfxSlider).width(buttonWidth);
		optionsMenu.row();
		optionsMenu.add(backButton).width(buttonWidth).padBottom(spacing);
		optionsMenu.validate();
		
		// Move options menu off screen
		optionsMenu.setX(optionsMenu.getWidth());
		
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
				mainMenu.addAction(Actions.moveTo(-mainMenu.getWidth(), 0f, 0.5f, Interpolation.swingOut));
				optionsMenu.addAction(Actions.moveTo(0f, 0f, 0.5f, Interpolation.swingIn));
			}
		});
		
		exitButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.exit();
			}
		});
		
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				mainMenu.addAction(Actions.moveTo(0f, 0f, 0.5f, Interpolation.swingIn));
				optionsMenu.addAction(Actions.moveTo(optionsMenu.getWidth(), 0f, 0.5f, Interpolation.swingOut));
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
	}
	
}
