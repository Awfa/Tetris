package com.anthonyha.tetris;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class TetrisMainMenu implements Screen {
	private final Tetris game;
	private Stage stage;
	
	public TetrisMainMenu(final Tetris game) {
		final Table mainMenu = new Table();
		final Table optionsMenu = new Table();
		
		this.game = game;
		
		stage = new Stage();
		
		mainMenu.setFillParent(true);
		optionsMenu.setFillParent(true);
		
		stage.addActor(mainMenu);
		stage.addActor(optionsMenu);
		
		// Filling the main menu
		Image mainMenuTitle = new Image(game.uiAtlas.findRegion("Title"));
		TextButton playButton = new TextButton("Play Game", game.tetrisUI.playButtonStyle);
		TextButton optionButton = new TextButton("Options", game.tetrisUI.normalButtonStyle);
		TextButton exitButton = new TextButton("Exit", game.tetrisUI.normalButtonStyle);
		
		mainMenu.top();
		mainMenu.add(mainMenuTitle).padTop(TetrisUI.spacingTop).spaceBottom(TetrisUI.spacing);
		mainMenu.row();
		mainMenu.add(playButton).width(TetrisUI.buttonWidth).spaceBottom(TetrisUI.spacing);
		mainMenu.row();
		mainMenu.add(optionButton).width(TetrisUI.buttonWidth).spaceBottom(TetrisUI.spacing);
		mainMenu.row();
		mainMenu.add(exitButton).width(TetrisUI.buttonWidth).padBottom(TetrisUI.spacing);
		
		// Filling the options menu
		Label optionsTitle = new Label("Options", game.tetrisUI.optionsHeaderStyle);
		Label musicVolume = new Label("Music Vol: " + game.tetrisSoundSystem.getMusicVolume() * 10, game.tetrisUI.optionsLabelStyle);
		Label sfxVolume = new Label("SFX Vol: " + game.tetrisSoundSystem.getSfxVolume() * 10, game.tetrisUI.optionsLabelStyle);
		Slider musicSlider = new Slider(0.f, 10.f, 0.5f, false, game.tetrisUI.optionsSliderStyle);
		Slider sfxSlider = new Slider(0.f, 10.f, 0.5f, false, game.tetrisUI.optionsSliderStyle);
		TextButton backButton = new TextButton("Back", game.tetrisUI.normalButtonStyle);
		
		musicSlider.setValue(game.tetrisSoundSystem.getMusicVolume() * 10);
		musicVolume.setAlignment(Align.right);
		musicVolume.setName("musicVolumeLabel");
		
		sfxSlider.setValue(game.tetrisSoundSystem.getSfxVolume() * 10);
		sfxVolume.setAlignment(Align.right);
		sfxVolume.setName("sfxVolumeLabel");
		
		optionsMenu.top();
		optionsMenu.add(optionsTitle).width(TetrisUI.buttonWidth).padTop(TetrisUI.spacingTop).spaceBottom(TetrisUI.spacing);
		optionsMenu.row();
		optionsMenu.add(musicVolume).width(TetrisUI.buttonWidth).right();
		optionsMenu.add(musicSlider).width(TetrisUI.buttonWidth);
		optionsMenu.row();
		optionsMenu.add(sfxVolume).width(TetrisUI.buttonWidth).right().spaceBottom(TetrisUI.spacing);
		optionsMenu.add(sfxSlider).width(TetrisUI.buttonWidth);
		optionsMenu.row();
		optionsMenu.add(backButton).width(TetrisUI.buttonWidth).padBottom(TetrisUI.spacing);
		optionsMenu.validate();
		
		// Move options menu off screen
		optionsMenu.setX(optionsMenu.getWidth());
		
		// Main menu listeners
		playButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(game.gameScreen);
			}
		});
		
		exitButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.exit();
			}
		});
		
		// Option menu listeners
		optionButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				mainMenu.addAction(Actions.moveTo(-mainMenu.getWidth(), 0f, 0.5f, Interpolation.swingOut));
				optionsMenu.addAction(Actions.moveTo(0f, 0f, 0.5f, Interpolation.swingIn));
			}
		});
		
		musicSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.tetrisSoundSystem.setMusicVolume(((Slider) actor).getValue() / 10.f);
				((Label) optionsMenu.findActor("musicVolumeLabel")).setText("Music Vol: " + ((Slider) actor).getValue());
			}
		});
		
		sfxSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.tetrisSoundSystem.setSfxVolume(((Slider) actor).getValue() / 10.f);
				((Label) optionsMenu.findActor("sfxVolumeLabel")).setText("SFX Vol: " + ((Slider) actor).getValue());
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
		Gdx.input.setInputProcessor(stage);
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
