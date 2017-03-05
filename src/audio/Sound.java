package audio;

import java.io.File;
import java.net.URL;

/**
 * Sound
 * @author harvey
 * Stores location of audio files and provides an interface to play them
 */
public enum Sound {
	//FOOTSTEP ("footsteps.wav"), MUSIC_MAIN ("output.midi", AudioType.MIDI), DOOR_OPEN ("open.wav"), BATTERY_LOW ("battery.wav"), WINDOW_BREAK ("window.wav");
	MUSIC_MAIN ("output.wav");
	
	private File audiofile;
	private AudioPlayer player;
	private AudioType type;

	/**
	 * Creates new interface for an Wav file
	 * @param filename The path of the audio file relative to the location of the .class file 
	 */
	private Sound(String filename) {
		this.audiofile = new File(getClass().getResource(filename).getPath());
		this.player = new AudioWav(this.audiofile);
		this.type = AudioType.WAV;
	}
	
	/**
	 * Creates new interface for an audio file
	 * @param filename The path of the audio file relative to the location of the .class file
	 * @param type MIDI or Wav
	 */
	private Sound(String filename, AudioType type) {
		URL url = getClass().getResource(filename);
		this.audiofile = new File(url.getPath());
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
}
