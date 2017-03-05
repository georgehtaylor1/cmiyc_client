package audio;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;

/**
 * AudioMidi
 * @author harvey
 * Audio player for MIDI files
 */
public class AudioMidi implements AudioPlayer {
	
	private Sequencer sequencer;
	private File audioFile;
	private boolean running;
	private boolean firstRun;
	
	/**
	 * Creates a new MIDI file player
	 * @param audioFile The file to be played
	 */
	public AudioMidi(File audioFile) {
		running = false;
		this.audioFile = audioFile;
		this.firstRun = true;
	}
	
	/**
	 * Sets up the sequencer
	 */
	private void setup() {
		running = true;
		try {
			sequencer = MidiSystem.getSequencer();
			sequencer.open();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Play the sound
	 * @param loop Whether to loop or not
	 */
	public void play(boolean loop) {
		if (!firstRun) stop();
		setup();
		InputStream is;
		try {
			is = new BufferedInputStream(new FileInputStream(audioFile));
			sequencer.setSequence(is);
	    	sequencer.start();
			if (loop) sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
		} catch (IOException | InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Stops playback
	 */
	public void stop() {
		running = false;
		sequencer.stop();
		sequencer.close();
	}
	
	/**
	 * Returns whether the player is running or not
	 * @return bool for whether the file is playing or not
	 */
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * Required by interface
	 */
	public void setVol(float vol, float pan) {};
}
