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

	private final double turnSpeedFast = 0.1;
	private final double turnSpeedMid = 0.07;
	private final double turnSpeedSlow = 0.04;
	private final double moveSpeedFast = 1;
	private final double moveSpeedMid = 0.75;
	private final double moveSpeedSlow = 0.4;

	private Position nextWaypoint;
	private Position previousWaypoint;

	private double leftVol;
	private double rightVol;
	private Player chasingPlayer;

	private final int scanTime = 200;
	private int currentScanStep;
	private final double scanAngle = Math.PI - 0.1;
	private double startingScanAngle;
	private boolean scanningLeft;

	/**
	 * Create a new security AI
	 * 
	 * @param handler
	 *            The AI handler for this AI
	 */
	public Security(Handler handler) {
		super(handler, Faction.SECURITY);
		this.faction = Faction.SECURITY;
		setState(SecurityState.MOVING);
		this.position = new Position(100, 200);
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
				updateMovingPosition(chasingPlayer.position, turnSpeedMid, moveSpeedFast);
				break;
			case LISTENING:
				updateListeningPosition();
				break;
			case MOVING:
				updateMovingPosition(nextWaypoint, turnSpeedMid, moveSpeedMid);
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
		
		if(nextWaypoint == null){
			nextWaypoint = Helper.getRandomFreePosition(getHandler().gameData);
		}
		
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
		boolean inRange = false;
		if (closestPlayer != null) {
			double closestDist = Maths.dist(this.position, closestPlayer.position);
			if (closestDist < GameSettings.Security.lightRadius) {
				double angle = Maths.angle(super.position, closestPlayer.position);
				if (angle >= super.direction - GameSettings.Security.lightArcPercentage * 2 * Math.PI
						&& angle <= super.direction + GameSettings.Security.lightArcPercentage * 2 * Math.PI) {
					setState(SecurityState.CHASING);
					chasingPlayer = closestPlayer;
					inRange = true;
				}
			}
		}

		if(chasingPlayer != null && this.position.at(chasingPlayer.position, GameSettings.Security.catchRadius)){
			//chasingPlayer.
		}
		
		if (!inRange && chasingPlayer != null) {
			setState(SecurityState.MOVING);
			chasingPlayer = null;
		}

		// Are we currently scanning
		//Debug.say("Checking scan " + currentScanStep + " " + this.state);
		if (this.getState() == SecurityState.SCANNING) {
			currentScanStep++;
			//Debug.say("Scanning");
			if (currentScanStep > scanTime) {
				currentScanStep = 0;
				setState(SecurityState.MOVING);
				Position newWaypoint = Helper.getNextWayPoint(nextWaypoint, getHandler().gameData.treasures,
						this.previousWaypoint, 0.5, getHandler().gameData.rand);
				previousWaypoint = nextWaypoint;
				nextWaypoint = newWaypoint;
			}
		}

		// Are we at the waypoint but not yet scanning
		if (this.position.at(nextWaypoint, 10) && getState() == SecurityState.MOVING) {
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
		Debug.say("Set state: " + state);
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
			turn(true, turnSpeedSlow);
		} else if (!scanningLeft && this.direction > Maths.normalizeAngle(this.startingScanAngle - this.scanAngle)) {
			turn(false, turnSpeedSlow);
		} else {
			scanningLeft = !scanningLeft;
		}

	}

	/**
	 * Update the position of the security.
	 * 
	 * Assume that the goal and any obstacles exert a force on the AI, the goal exerts a constant force of 1 and the obstacles exert a force proportional to the
	 * distance (or some exponent of it) to the nearest obstacle. The resultant of these forces is then calculated and the AI moves in the direction of this
	 * resultant. by ensuring that the force exerted by obstacles is strictly less than the force exerted by the goal it is possible to ensure that all objects
	 * that lie directly on walls are reachable
	 * 
	 * @param goal
	 *            The goal for the AI to reach
	 * @param turnSpeed
	 *            The speed at which the AI can turn for this movement
	 * @param moveSpeed
	 *            The speed at which the AI can move for this movement
	 */
	private void updateMovingPosition(Position goal, double turnSpeed, double moveSpeed) {
		double goalForce = 1;

		Obstacle obstruction = null;
		double dist = GameSettings.Arena.outerSize.getHeight() + GameSettings.Arena.outerSize.getWidth();

		for (Obstacle o : getHandler().gameData.obstacles) {
			Position collisionPoint = Helper.getCollisionPoint(this.position, o);
			double currentDist = Maths.dist(this.position, collisionPoint);
			if (currentDist < GameSettings.Security.lightRadius && currentDist < dist) {
				obstruction = o;
				dist = currentDist;
			}
		}

		double goalAngle = Maths.angle(this.position, goal);
		double targetAngle = goalAngle;

		if (obstruction != null) {
			double goalX = goalForce * Math.cos(goalAngle);
			double goalY = goalForce * Math.sin(goalAngle);

			Position collisionPoint = Helper.getCollisionPoint(this.position, obstruction);
			double collisionDist = Maths.dist(this.position, collisionPoint);
			double obstacleMultiplier = 1.1;
			double obstacleForce = obstacleMultiplier * (GameSettings.Security.lightRadius - collisionDist)
					/ GameSettings.Security.lightRadius;
			double obstacleAngle = Maths.angle(collisionPoint, this.position);
			double obstacleX = obstacleForce * Math.cos(obstacleAngle);
			double obstacleY = obstacleForce * Math.sin(obstacleAngle);

			double y = goalY + obstacleY;
			double x = goalX + obstacleX;

			targetAngle = Math.atan2(y, x);
		}

		Position resultantProjection = Maths.project(this.position, 5, targetAngle);
		turnTowards(resultantProjection, 0.04, turnSpeedMid);
		Helper.move(this, getHandler().gameData.obstacles, moveSpeedMid);

	}

	/**
	 * Turn the AI towards the specified position
	 * 
	 * @param p
	 *            The position to turn towards
	 * @param threshold
	 *            The threshold to limit eratic motion in the AI
	 * @param speed
	 *            The turn speed for the AI
	 */
	private void turnTowards(Position p, double threshold, double speed) {
		double targetAngle = Maths.angle(this.position, p);
		double deltaAngle = targetAngle - this.direction;
		deltaAngle = Maths.normalizeAngle(deltaAngle);
		if (Math.abs(deltaAngle) > threshold)
			turn(deltaAngle > 0 && deltaAngle < Math.PI, speed);
	}

	/**
	 * Update the position of the AI based on the fact that it can hear a villain
	 */
	private void updateListeningPosition() {
		turn(leftVol > rightVol, moveSpeedFast);
		Helper.move(this, getHandler().gameData.obstacles, moveSpeedSlow);
	}

	/**
	 * Turn the AI
	 * 
	 * @param left
	 *            Should the AI turn left or right?
	 * @param speed
	 *            At what speed should the AI move
	 */
	private void turn(boolean left, double speed) {
		this.direction += left ? speed : -speed;
		this.direction = Maths.normalizeAngle(this.direction);
	}

}
