package audio;

import java.io.File;

/**
 * Stores location of audio files and provides an interface to play them
 * 
 * @author harvey
 */
public enum Sound {
	MUSIC_MAIN ("the_environment.wav"), FOOTSTEPS ("footsteps.wav");
	
	private File audiofile;
	private AudioPlayer player;
	private AudioType type;

	/**
	 * Creates new interface for an Wav file
	 * @param filename The path of the audio file relative to /resources/sound/
	 */
	private Sound(String filename) {
		this.audiofile = new File("resources/sound/" + filename);
		this.player = new AudioWav(this.audiofile);
		this.type = AudioType.WAV;
	}
	
	/**
	 * Creates new interface for an audio file
	 * @param filename The path of the audio file relative to the location of the .class file
	 * @param type MIDI or Wav
	 */
	private Sound(String filename, AudioType type) {
		this.audiofile = new File("resources/sound/" + filename);
		switch(type) {
		case MIDI:
			this.player = new AudioMidi(this.audiofile);
			this.type = AudioType.MIDI;
		case WAV:
			this.player = new AudioWav(this.audiofile);
			this.type = AudioType.WAV;
		}
	}

	/**
	 * Plays sound once
	 */
	public void play() {
		this.player.play(false);
	}
	
	/**
	 * Plays sound in a loop until manually stopped
	 */
	public void playLoop() {
		this.player.play(true);;
	}
	
	/**
	 * Stop sound
	 */
	public void stop() {
		this.player.stop();
	}
	
	/**
	 * Checks if player is running or not
	 * @return True if player is running, false otherwise
	 */
	public boolean isRunning() {
		return this.player.isRunning();
	}
	
	/**
	 * Sets volume of a Wav player
	 * @param vol New volume
	 * @param pan New L/R balance
	 */
	public void setVol(double vol, double pan) {
		if (this.type == AudioType.WAV) this.player.setVol((float) vol, (float) pan);
	}
	
	/**
	 * Sets volume of a Wav player
	 * @param vol New global volume
	 * @param left Left volume
	 * @param right Right volume
	 */
	public void setVol(double vol, double left, double right) {
		if (this.type == AudioType.WAV) {
			float pan;
			if (left > right) pan = (float)(left/right);
			else pan = (float)(right/left);
			this.player.setVol((float)vol, pan);
		}
	}
	
	/**
	 * Sets volume of a Wav player
	 * @param vol New Volume
	 */
	public void setVol(double vol) {
		if (this.type == AudioType.WAV) this.player.setVol((float) vol, (float) 0.5);
	}
	
	/**
	 * Gets volume of a Wav player
	 * @return Volume as float
	 */
	public float getVol() {
		return this.player.getVol();
	}
	
	/**
	 * Gets pan of a Wav player
	 * @return Pan as float
	 */
	public float getPan() {
		return this.player.getPan();
	}
}
