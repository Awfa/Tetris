package com.anthonyha.tetris;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class TetrisUI {
	public TextButtonStyle playButtonStyle;
	public TextButtonStyle normalButtonStyle;
	public TextButtonStyle darkButtonStyle;
	public SliderStyle optionsSliderStyle;
	public LabelStyle optionsLabelStyle;
	public LabelStyle optionsHeaderStyle;
	public LabelStyle pauseHeaderStyle;
	
	static float buttonWidth = 432f;
	static float spacing = 32f;
	static float spacingTop = 64f;
	
	TetrisUI(final Tetris game) {
		normalButtonStyle = new TextButtonStyle();
		normalButtonStyle.up = new NinePatchDrawable(game.uiAtlas.createPatch("ButtonUp"));
		normalButtonStyle.down = new NinePatchDrawable(game.uiAtlas.createPatch("ButtonDown"));
		normalButtonStyle.font = game.quantico48;
		normalButtonStyle.fontColor = new Color(0.28f, 0.28f, 0.28f, 1.f);
		
		playButtonStyle = new TextButtonStyle(normalButtonStyle);
		playButtonStyle.fontColor = Color.BLACK;
		
		darkButtonStyle = new TextButtonStyle(normalButtonStyle);
		darkButtonStyle.up = new NinePatchDrawable(game.uiAtlas.createPatch("ButtonBlueUp"));
		darkButtonStyle.fontColor = Color.WHITE;
		
		optionsSliderStyle = new SliderStyle();
		optionsSliderStyle.background = new NinePatchDrawable(game.uiAtlas.createPatch("Slider"));
		optionsSliderStyle.knob = new TextureRegionDrawable(game.uiAtlas.findRegion("SliderKnob"));
		
		optionsLabelStyle = new LabelStyle();
		optionsLabelStyle.background = new NinePatchDrawable(game.uiAtlas.createPatch("ButtonUp"));
		optionsLabelStyle.font = game.quantico48;
		optionsLabelStyle.fontColor = new Color(0.28f, 0.28f, 0.28f, 1.f);
		
		optionsHeaderStyle = new LabelStyle(optionsLabelStyle);
		optionsHeaderStyle.font = game.quantico64;
		optionsHeaderStyle.fontColor = Color.BLACK;
		
		pauseHeaderStyle = new LabelStyle();
		pauseHeaderStyle.background = new NinePatchDrawable(game.uiAtlas.createPatch("LabelHeader"));
		pauseHeaderStyle.font = game.quantico48;
		pauseHeaderStyle.fontColor = Color.WHITE;
	}
}
