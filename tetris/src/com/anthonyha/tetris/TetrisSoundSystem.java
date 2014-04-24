package com.anthonyha.tetris;

import com.anthonyha.tetris.MessageSystem.Message;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

public class TetrisSoundSystem extends AbstractMessageListener implements Disposable {
	
	private float musicVolume;
	private float sfxVolume;
	
	private Sound shiftSound, hardDropSound, success;
	private Music theme;
	
	public TetrisSoundSystem(MessageSystem m) {
		// Register for messages
		m.add(this, Message.SOFT_DROPPED);
		m.add(this, Message.SHIFTED);
		m.add(this, Message.HARD_DROPPED);
		m.add(this, Message.ROWS_SCORED);
		m.add(this, Message.LEVEL_UP);
		m.add(this, Message.GAME_PAUSED);
		m.add(this, Message.GAME_RESUMED);
		m.add(this, Message.RESTART_GAME);
		m.add(this, Message.GAME_OVER);
		
		// Load sfx
		shiftSound = Gdx.audio.newSound(Gdx.files.internal("sfx/shiftSound.wav"));
		hardDropSound = Gdx.audio.newSound(Gdx.files.internal("sfx/hardDropSound.wav"));
		success = Gdx.audio.newSound(Gdx.files.internal("sfx/success.wav"));
		
		// Load music
		theme = Gdx.audio.newMusic(Gdx.files.internal("music/tetrisTheme.mp3"));
		
		// Default volumes
		musicVolume = 1.0f;
		sfxVolume = 1.0f;
		
		theme.setLooping(true);
		theme.setVolume(musicVolume * 0.25f); // Normalize the volume
	}
	
	@Override
	public void recieveMessage(MessageSystem.Message message) {
		switch(message) {
		case SOFT_DROPPED:
		case SHIFTED:
			shiftSound.play(sfxVolume * 0.5f);
			break;
			
		case HARD_DROPPED:
			hardDropSound.play(sfxVolume * 0.5f);
			break;
			
		case LEVEL_UP:
			success.play(sfxVolume * 0.5f);
			break;
		
		case GAME_OVER:
		case GAME_PAUSED:
			theme.pause();
			break;
			
		case GAME_RESUMED:
			theme.play();
			break;

		case RESTART_GAME:
			theme.stop();
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

	public float getMusicVolume() {
		return musicVolume;
	}

	public void setMusicVolume(float musicVolume) {
		this.musicVolume = musicVolume;
		theme.setVolume(musicVolume * 0.25f); // Normalize music volume
	}

	public float getSfxVolume() {
		return sfxVolume;
	}

	public void setSfxVolume(float sfxVolume) {
		this.sfxVolume = sfxVolume;
	}

	@Override
	public void dispose() {
		theme.dispose();
		shiftSound.dispose();
		hardDropSound.dispose();
		success.dispose();
	}
	
}
