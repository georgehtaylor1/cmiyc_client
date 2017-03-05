package audio;

/**
 * AudioPlayer
 * @author harvey
 * General interface for Audio players
 */
public interface AudioPlayer {
	public void play(boolean loop);
	public void stop();
	public void setVol(float vol, float pan);
}
