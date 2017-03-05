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
import util.Debug;
import util.Maths;

public class Security extends AI {

	private SecurityState state;

	private final double turnSpeedHigh = 0.1;
	private final double turnSpeedMid = 0.07;
	private final double turnSpeedLow = 0.04;
	private final double moveSpeedHigh = 1;
	private final double moveSpeedMid = 0.7;
	private final double moveSpeedLow = 0.4;

	private Position nextWaypoint;
	private Position tempWaypoint;
	private Position previousWaypoint;
	private Obstacle obstruction;

	private double leftVol;
	private double rightVol;
	private double chasingAngle;
	private double chasingDistance;

	private final int scanTime = 160;
	private int currentScanStep;
	private final double scanAngle = Math.PI / 3;
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
		setState(SecurityState.MOVING);
		this.position = new Position(500, 500);
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		Debug.say("Security started");

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

		Debug.say("Security updating " + getState().toString() + " " + this.position.x + " " + this.position.y);

		// Get the volume for the players
		leftVol = Maths.getLeftVolume(this.position, this.clientID, this.getHandler().gameData.players);
		rightVol = Maths.getLeftVolume(this.position, this.clientID, this.getHandler().gameData.players);

		// Check if we can hear anyone
		if (leftVol > 0 || rightVol > 0)
			setState(SecurityState.LISTENING);

		// Check if there is anyone we can chase
		ConcurrentHashMap<String, Player> excHashMap = super.getHandler().gameData.players;
		excHashMap.remove(super.clientID);
		Player closestPlayer = Helper.getClosestThief(this.position, this.clientID,
				new ArrayList<Player>(excHashMap.values()));
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
				currentScanStep = 0;
				setState(SecurityState.MOVING);
				Position newWaypoint = Helper.getNextWayPoint(this.position, getHandler().gameData.treasures,
						this.previousWaypoint);
				previousWaypoint = nextWaypoint;
				nextWaypoint = newWaypoint;
			}
		}

		// Are we at the waypoint but not yet scanning
		if (this.position.at(nextWaypoint, 5) && getState() == SecurityState.MOVING) {
			setState(SecurityState.SCANNING);
		}

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
		if (scanningLeft && this.direction < Maths.normalizeAngle(this.startingScanAngle + this.scanAngle)) {
			turnSlow(true);
		} else if (!scanningLeft && this.direction > Maths.normalizeAngle(this.startingScanAngle - this.scanAngle)) {
			turnSlow(false);
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
		double goalForce = 1;

		Obstacle obstruction = null;
		double dist = GameSettings.Arena.outerSize.getHeight() + GameSettings.Arena.outerSize.getWidth();

		for (Obstacle o : getHandler().gameData.obstacles) {
			Position collisionPoint = getCollisionPoint(this.position, o);
			double currentDist = Maths.dist(this.position, collisionPoint);
			if (currentDist < GameSettings.Security.lightRadius && currentDist < dist) {
				obstruction = o;
				dist = currentDist;
			}
		}

		double goalAngle = Maths.angle(this.position, nextWaypoint);
		double targetAngle = goalAngle;

		if (obstruction != null) {
			double goalX = goalForce * Math.cos(goalAngle);
			double goalY = goalForce * Math.sin(goalAngle);

			Position collisionPoint = getCollisionPoint(this.position, obstruction);
			double collisionDist = Maths.dist(this.position, collisionPoint);
			double obstacleForce = (GameSettings.Security.lightRadius - collisionDist)
					/ GameSettings.Security.lightRadius;
			double obstacleAngle = Maths.angle(collisionPoint, this.position);
			double obstacleX = obstacleForce * Math.cos(obstacleAngle);
			double obstacleY = obstacleForce * Math.sin(obstacleAngle);

			double y = goalY + obstacleY;
			double x = goalX + obstacleX;

			targetAngle = Math.atan2(y, x);
		}

		Position resultantProjection = Maths.project(this.position, 5, targetAngle);
		turnTowards(resultantProjection, 0.04);
		moveMid();

	}

	/**
	 * Get the closest position on the obstacle to the given position
	 * 
	 * @param p
	 *            The position to compare to
	 * @param o
	 *            The obstacle to be examined
	 * @return The position on the border of the obstacle closest to the given position
	 */
	private Position getCollisionPoint(Position p, Obstacle o) {
		if (p.y > o.bottomRight.y) {
			if (p.x < o.topLeft.x)
				return o.bottomLeft;
			if (p.x > o.bottomRight.x)
				return o.bottomRight;
			return new Position(p.x, o.bottomRight.y);
		}
		if (p.y < o.topLeft.y) {
			if (p.x < o.topLeft.x)
				return o.topLeft;
			if (p.x > o.bottomRight.x)
				return o.topRight;
			return new Position(p.x, o.topLeft.y);
		}
		if (p.x < o.topLeft.x)
			return new Position(o.topLeft.x, p.y);
		if (p.x > o.bottomRight.x)
			return new Position(o.bottomRight.x, p.y);
		return p;

	}

	/**
	 * Turn the AI towards the specified position
	 * 
	 * @param p
	 *            The position to turn towards
	 * @param threshold
	 *            The threshold to limit eratic motion in the AI
	 */
	private void turnTowards(Position p, double threshold) {
		double targetAngle = Maths.angle(this.position, p);
		double deltaAngle = targetAngle - this.direction;
		deltaAngle = Maths.normalizeAngle(deltaAngle);
		if (Math.abs(deltaAngle) > threshold)
			turnFast(deltaAngle > 0 && deltaAngle < Math.PI);
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
		this.direction += left ? turnSpeedHigh : -turnSpeedHigh;
		this.direction = Maths.normalizeAngle(this.direction);
	}

	/**
	 * Turn the AI at a medium speed
	 * 
	 * @param left
	 *            Should the AI turn left or right?
	 */
	private void turnMid(boolean left) {
		this.direction += left ? turnSpeedMid : -turnSpeedMid;
		this.direction = Maths.normalizeAngle(this.direction);
	}

	/**
	 * Slowlu turn the AI
	 * 
	 * @param left
	 *            Should the AI turn left or right?
	 */
	private void turnSlow(boolean left) {
		this.direction += left ? turnSpeedLow : -turnSpeedLow;
		this.direction = Maths.normalizeAngle(this.direction);
	}

}
