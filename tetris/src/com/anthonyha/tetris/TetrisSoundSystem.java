package com.anthonyha.tetris;

import com.anthonyha.tetris.MessageSystem.Message;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

public class TetrisSoundSystem extends AbstractMessageListener implements Disposable {
	
	private float musicVolume;
	private float sfxVolume;
	
	Sound shiftSound, hardDropSound;
	
	public TetrisSoundSystem(MessageSystem m) {
		// Register for messages
		m.add(this, Message.SOFT_DROPPED);
		m.add(this, Message.SHIFTED);
		m.add(this, Message.HARD_DROPPED);
		m.add(this, Message.ROWS_SCORED);
		
		// Load sfx
		shiftSound = Gdx.audio.newSound(Gdx.files.internal("sfx/shiftSound.wav"));
		hardDropSound = Gdx.audio.newSound(Gdx.files.internal("sfx/hardDropSound.wav"));
		
		// Default volumes
		musicVolume = 1.0f;
		sfxVolume = 1.0f;
	}
	
	@Override
	public void recieveMessage(MessageSystem.Message message) {
		switch(message) {
		case SOFT_DROPPED:
		case SHIFTED:
			shiftSound.play(sfxVolume);
			break;
			
		case HARD_DROPPED:
			hardDropSound.play(sfxVolume);
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

	public float getMusicVolume() {
		return musicVolume;
	}

	public void setMusicVolume(float musicVolume) {
		this.musicVolume = musicVolume;
	}

	public float getSfxVolume() {
		return sfxVolume;
	}

	public void setSfxVolume(float sfxVolume) {
		this.sfxVolume = sfxVolume;
	}

	@Override
	public void dispose() {
		shiftSound.dispose();
		hardDropSound.dispose();
	}
	
}
