package audio;

/**
 * General interface for Audio players
 * 
 * @author harvey
 */
public interface AudioPlayer {
	public void play(boolean loop);
	public void stop();
	public void setVol(float vol, float pan);
	public boolean isRunning();
}
