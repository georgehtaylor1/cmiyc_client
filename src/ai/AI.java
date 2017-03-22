package ai;

import java.util.UUID;

import ai.handler.Handler;
import game.Faction;
import game.Player;

public abstract class AI extends Player implements Runnable {

	private Handler handler;
	private boolean running;
	private boolean update = false;

	/**
	 * Create a new AI player
	 * 
	 * @param handler
	 *            The AI handler that the AI should use
	 */
	public AI(Handler handler, Faction faction) {
		super(UUID.randomUUID().toString());
		this.setHandler(handler);
		super.faction = faction;
		setRunning(false);
	}

	/**
	 * Start the AI thread
	 */
	public void start() {
		Thread t = new Thread(this);
		setRunning(true);
		t.start();
	}

	/**
	 * Stop the AI from running
	 */
	public void end() {
		setRunning(false);
	}

	/**
	 * Determine whether the AI is running
	 * 
	 * @return Whether or not the AI is running
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Set the AI to be running or not
	 * 
	 * @param running
	 *            Whether or not the AI should be running
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * Determine whether the AI needs updating
	 * 
	 * @return Whether or not the AI needs to be updated
	 */
	public boolean needsUpdate() {
		return update;
	}

	/**
	 * Set whether the AI needs to update itself
	 * 
	 * @param update
	 */
	public void setUpdate(boolean update) {
		this.update = update;
	}

	/**
	 * Get the handler for the AI
	 * 
	 * @return The AI handler being used
	 */
	public Handler getHandler() {
		return handler;
	}

	/**
	 * Set the AI handler for this AI
	 * 
	 * @param handler
	 *            The AI handler to be used
	 */
	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	/**
	 * Update the state of the AI
	 */
	protected abstract void updateState();

}
