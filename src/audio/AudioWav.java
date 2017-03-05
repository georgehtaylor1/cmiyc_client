package audio;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import util.Debug;

/**
 * AudioWav
 * @author harvey
 * Player for a Wav file
 */
public class AudioWav implements AudioPlayer {
	private boolean running;
	private Clip player;
	private File audiofile;
	
	/**
	 * Creates a new Wav file player
	 * @param audiofile The file to be played
	 */
	public AudioWav(File audiofile) {
		running = false;
		this.audiofile = audiofile;
	}
	
	/**
	 * Play the sound
	 * @param loop Whether to loop or not
	 */
	public void play(boolean loop) {
		try {
			if(running) stop();
			player = AudioSystem.getClip();
			AudioInputStream ais = AudioSystem.getAudioInputStream(audiofile);
			player.open(ais);
			player.start();
			if (loop) player.loop(Clip.LOOP_CONTINUOUSLY);
		} catch (Exception e) {
		  System.err.println(e.getMessage());
		}
	}
	
	/**
	 * Stop playback
	 */
	public void stop() {
		running = false;
		player.stop();
		player.drain();
		player.close();
	}
	
	/**
	 * Returns whether the player is running or not
	 * @return bool for whether the file is playing or not
	 */
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * Sets playback volume
	 * @param vol New volume
	 * @param pan New L/R balance
	 */
	public void setVol(float vol, float pan) {
		FloatControl volControl = (FloatControl) player.getControl(FloatControl.Type.MASTER_GAIN);
		FloatControl panControl = (FloatControl) player.getControl(FloatControl.Type.PAN);
		if (vol <= volControl.getMaximum() && vol >= volControl.getMinimum()) volControl.setValue(vol);
		else Debug.say("Invalid Volume parameter");
		if (-1 <= pan && pan <= 1) panControl.setValue(pan);
		else Debug.say("Invalid pan parameter");
	}
}
