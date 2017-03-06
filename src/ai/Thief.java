package ai;

import java.util.ArrayList;

import ai.handler.Handler;
import ai.states.ThiefState;
import game.Faction;
import game.Obstacle;
import game.Player;
import game.Treasure;
import game.constants.GameSettings;
import game.states.TreasureState;
import game.util.Position;
import util.Maths;

public class Thief extends AI {

	private ThiefState state;

	private final double turnSpeedFast = 0.1;
	private final double turnSpeedMid = 0.07;
	private final double turnSpeedSlow = 0.04;
	private final double moveSpeedFast = 1;
	private final double moveSpeedMid = 0.75;
	private final double moveSpeedSlow = 0.4;

	private Treasure target;

	public Thief(Handler handler) {
		super(handler, Faction.THIEF);
		this.faction = Faction.THIEF;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {

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

			switch (getState()) {
			case MOVING:
				updateMovingPosition(target.position, turnSpeedMid, moveSpeedMid);
			case RUNNING:
				break;
			default:
				break;

			}
		}

	}

	@Override
	protected void updateState() {

		if (this.position.at(target.position, GameSettings.Thief.stealRadius)) {
			for (Treasure t : getHandler().gameData.treasures) {
				if (t.position.at(target.position, 2)) {
					t.state = TreasureState.PICKED;
					target = null;
				}
			}

			// Randomly select the next treasure
			for (Treasure t : getHandler().gameData.treasures) {
				if (t.state == TreasureState.UNPICKED) {
					target = t;
				}
			}

		}

	}

	/**
	 * Update the position of the thief.
	 * 
	 * Assume that the goal, any obstacles and any security exert a force on the AI, the goal and security exerts 1 and the obstacles exert a force proportional
	 * to the distance (or some exponent of it) to the nearest obstacle. If the security is within range then th force exerted by the goal will be 0 and the
	 * force exerted by the security will be 1. The resultant of these forces is then calculated and the AI moves in the direction of this resultant. By
	 * ensuring that the force exerted by obstacles is strictly less than the force exerted by the goal it is possible to ensure that all objects that lie
	 * directly on walls are reachable
	 * 
	 * @param goal
	 *            The goal for the AI to reach
	 * @param turnSpeed
	 *            The speed at which the AI can turn for this movement
	 * @param moveSpeed
	 *            The speed at which the AI can move for this movement
	 */
	private void updateMovingPosition(Position goal, double turnSpeed, double moveSpeed) {
		double goalSecForce = 1;

		//Check for any obstacles within range
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

		// Check for any security within range
		Position security = null;
		double secDist = GameSettings.Arena.outerSize.getHeight() + GameSettings.Arena.outerSize.getWidth();
		for (Player p : new ArrayList<Player>(getHandler().gameData.players.values())) {
			if (p.faction == Faction.SECURITY) {
				double currDist = Maths.dist(this.position, p.position);
				if (currDist < GameSettings.Thief.visionRadius && currDist < secDist) {
					security = p.position;
					secDist = currDist;
				}
			}
		}

		// Do we want to meve to the goal or away from the security
		double goalSecAngle = 0;
		if (security != null)
			goalSecAngle = Maths.angle(security, this.position);
		else
			goalSecAngle = Maths.angle(this.position, goal);

		double targetAngle = goalSecAngle;

		// Is there an obstacle we need to avoid
		if (obstruction != null) {
			double goalSecX = goalSecForce * Math.cos(goalSecAngle);
			double goalSecY = goalSecForce * Math.sin(goalSecAngle);

			Position collisionPoint = Helper.getCollisionPoint(this.position, obstruction);
			double collisionDist = Maths.dist(this.position, collisionPoint);
			double obstacleForce = (GameSettings.Security.lightRadius - collisionDist)
					/ GameSettings.Security.lightRadius;
			double obstacleAngle = Maths.angle(collisionPoint, this.position);
			double obstacleX = obstacleForce * Math.cos(obstacleAngle);
			double obstacleY = obstacleForce * Math.sin(obstacleAngle);

			double y = goalSecY + obstacleY;
			double x = goalSecX + obstacleX;

			targetAngle = Math.atan2(y, x);
		}

		Position resultantProjection = Maths.project(this.position, 5, targetAngle);

		if (security == null) {
			turnTowards(resultantProjection, 0.04, turnSpeedMid);
			Helper.move(this, getHandler().gameData.obstacles, moveSpeedMid);
		} else {
			turnTowards(resultantProjection, 0.04, turnSpeedFast);
			Helper.move(this, getHandler().gameData.obstacles, moveSpeedFast);
		}

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

	public ThiefState getState() {
		return state;
	}

	public void setState(ThiefState state) {
		this.state = state;
	}

}
