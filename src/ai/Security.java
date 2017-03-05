package ai;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import ai.handler.Handler;
import ai.states.SecurityState;
import game.Faction;
import game.Obstacle;
import game.Player;
import game.constants.GameSettings;
import game.util.Position;
import util.Maths;

public class Security extends AI {

	private SecurityState state;

	private final double turnSpeedHigh = 0.12;
	private final double turnSpeedMid = 0.09;
	private final double turnSpeedLow = 0.05;
	private final double moveSpeedHigh = 1.2;
	private final double moveSpeedMid = 0.9;
	private final double moveSpeedLow = 0.5;

	private Position nextWaypoint;
	private Position previousWaypoint;

	private double leftVol;
	private double rightVol;
	private double chasingAngle;
	private double chasingDistance;

	private final int scanTime = 100;
	private int currentScanStep;
	private final double scanAngle = Math.PI / 4;
	private double startingScanAngle;
	private boolean scanningLeft;

	/**
	 * Create a new security AI
	 * 
	 * @param handler
	 *            The AI handler for this AI
	 */
	public Security(Handler handler) {
		super(handler);
		this.faction = Faction.SECURITY;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		if (nextWaypoint == null)
			nextWaypoint = Helper.getClosestTreasure(this.position, this.getHandler().gameData.treasures).position;

		while (isRunning()) {

			// Wait until the AI should be updated
			while (!needsUpdate()) {
				// Prevent thread races
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			updateState();

			switch (this.state) {
			case CHASING:
				updateChasePosition();
				break;
			case LISTENING:
				updateListeningPosition();
				break;
			case MOVING:
				updateMovingPosition();
				break;
			case SCANNING:
				updateScanningPosition();
				break;
			default:
				break;

			}

			setUpdate(false);

		}

	}

	/* (non-Javadoc)
	 * @see ai.AI#updateState()
	 */
	protected void updateState() {

		// Get the volume for the players
		leftVol = Maths.getLeftVolume(this.position, this.clientID, this.getHandler().gameData.players);
		rightVol = Maths.getLeftVolume(this.position, this.clientID, this.getHandler().gameData.players);

		// Check if we can hear anyone
		if (leftVol > 0 || rightVol > 0)
			setState(SecurityState.LISTENING);

		// Check if there is anyone we can chase
		ConcurrentHashMap<String, Player> excHashMap = super.getHandler().gameData.players;
		excHashMap.remove(super.clientID);
		Player closestPlayer = Helper.getClosestPlayer(this.position, (ArrayList<Player>) excHashMap.values());
		double closestDist = Maths.dist(this.position, closestPlayer.position);
		if (closestDist < GameSettings.Security.lightRadius) {
			double angle = Maths.angle(super.position, closestPlayer.position);
			if (angle >= super.direction - GameSettings.Security.lightArcPercentage * 2 * Math.PI
					&& angle <= super.direction + GameSettings.Security.lightArcPercentage * 2 * Math.PI) {
				setState(SecurityState.CHASING);
				chasingAngle = angle;
				chasingDistance = closestDist;
			}
		}

		// Are we currently scanning
		if (this.getState() == SecurityState.SCANNING) {
			currentScanStep++;
			if (currentScanStep > scanTime) {
				setState(SecurityState.MOVING);
				Position newWaypoint = Helper.getNextWayPoint(this.position, getHandler().gameData.treasures,
						this.previousWaypoint);
				previousWaypoint = nextWaypoint;
				nextWaypoint = newWaypoint;
			}
		}

		// Are we at the waypoint but not yet scanning
		if (this.position.at(nextWaypoint) && getState() != SecurityState.SCANNING)
			setState(SecurityState.SCANNING);

	}

	/**
	 * Set the state of the AI
	 * 
	 * @param state
	 *            The new state of the AI
	 */
	public void setState(SecurityState state) {
		this.state = state;
	}

	/**
	 * Get the state of the AI
	 * 
	 * @return The state of the AI
	 */
	public SecurityState getState() {
		return this.state;
	}

	/**
	 * update the scanning position so that the security swings from left to right
	 */
	private void updateScanningPosition() {
		if (scanningLeft && this.direction < this.startingScanAngle + this.scanAngle) {
			turnMid(true);
		} else if (!scanningLeft && this.direction > this.startingScanAngle - this.scanAngle) {
			turnMid(false);
		} else {
			scanningLeft = !scanningLeft;
		}

	}

	/**
	 * Update the position of the security:
	 * 
	 * If the security is about to come up against an obstacle then find the closest corner on the obstacle provided it is closer to the waypoint than the
	 * player and turn towards that
	 * 
	 * If there is no obstacle ahead of the security then turn towards the waypoint
	 */
	private void updateMovingPosition() {

		moveMid();
		Position projection = Maths.project(this.position, GameSettings.Security.lightRadius, this.direction);
		boolean noObstruction = true;
		for (Obstacle o : getHandler().gameData.obstacles) {
			if (o.contains(projection)) {
				noObstruction = false;
				Position targetCorner = Helper.closestCornerOffset(o, this.position, this.nextWaypoint, 5);
				double targetAngle = Maths.angle(this.position, targetCorner);
				double deltaAngle = targetAngle - this.direction;
				turnFast(deltaAngle > 0 && deltaAngle < 180);
			}
		}
		if (noObstruction) {
			double targetAngle = Maths.angle(this.position, this.nextWaypoint);
			double deltaAngle = targetAngle - this.direction;
			turnFast(deltaAngle > 0 && deltaAngle < 180);
		}
	}

	/**
	 * Update the position of the AI based on the fact that it can hear a villain
	 */
	private void updateListeningPosition() {
		turnFast(leftVol > rightVol);
		moveSlow();
	}

	private void updateChasePosition() {
		// TODO Auto-generated method stub

	}

	/**
	 * Move the AI forward at the faster speed
	 */
	private void moveFast() {
		this.position = Maths.project(this.position, moveSpeedHigh, this.direction);
	}

	/**
	 * Move the AI forward at the middle speed
	 */
	private void moveMid() {
		this.position = Maths.project(this.position, moveSpeedMid, this.direction);
	}

	/**
	 * Move the AI forward at the slower speed
	 */
	private void moveSlow() {
		this.position = Maths.project(this.position, moveSpeedLow, this.direction);
	}

	/**
	 * Quickly turn the AI
	 * 
	 * @param left
	 *            Should the AI turn left or right?
	 */
	private void turnFast(boolean left) {
		this.direction += left ? -turnSpeedHigh : turnSpeedHigh;
	}

	/**
	 * Turn the AI at a medium speed
	 * 
	 * @param left
	 *            Should the AI turn left or right?
	 */
	private void turnMid(boolean left) {
		this.direction += left ? -turnSpeedMid : turnSpeedMid;
	}

	/**
	 * Slowlu turn the AI
	 * 
	 * @param left
	 *            Should the AI turn left or right?
	 */
	private void turnSlow(boolean left) {
		this.direction += left ? -turnSpeedLow : turnSpeedLow;
	}

}
