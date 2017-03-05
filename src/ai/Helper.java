package ai;

import java.util.ArrayList;

import game.Obstacle;
import game.Player;
import game.Treasure;
import game.util.Position;
import util.Maths;

public class Helper {

	/**
	 * Get the treasure that is closest to the given point
	 * 
	 * @param p
	 *            The position to compare to
	 * @param treasures
	 *            The treasures to select the closest from
	 * @return The closest treasure to the given point
	 */
	public static Treasure getClosestTreasure(Position p, ArrayList<Treasure> treasures) {
		return treasures.stream()
				.min((t1, t2) -> Double.compare(Maths.dist(p, t1.position), Maths.dist(p, t2.position))).get();

	}

	/**
	 * Get the player that is closest to the given point
	 * 
	 * @param p
	 *            The position to compare to
	 * @param players
	 *            The players to check
	 * @return The closest player
	 */
	public static Player getClosestPlayer(Position p, ArrayList<Player> players) {
		return players.stream().min((p1, p2) -> Double.compare(Maths.dist(p, p1.position), Maths.dist(p, p2.position)))
				.get();

	}

	/**
	 * Get the position of the next waypoint
	 * 
	 * @param p
	 *            The posiiton of the AI
	 * @param treasures
	 *            The list of treasures
	 * @param currentWaypoint
	 *            The current waypoint so that it can be ignored
	 * @return The next waypoint to go to
	 */
	public static Position getNextWayPoint(Position p, ArrayList<Treasure> treasures, Position currentWaypoint) {
		double minDist = Maths.dist(p, treasures.get(0).position);
		Position minPos = treasures.get(0).position;
		for (Treasure t : treasures) {
			if (!t.position.at(p)) {
				double d = Maths.dist(p, t.position);
				if (d < minDist) {
					minDist = d;
					minPos = t.position;
				}
			}
		}
		return minPos;
	}

	/**
	 * Get the position of the closest corner of the obstacle to the given position, offset by 45 degrees in the specified distance
	 * 
	 * @param o
	 *            The obstacle to be checked
	 * @param p
	 *            The position to be compared to
	 * @param wayPoint
	 *            The way point that the player is attempting to move to
	 * @param offset
	 *            The offset to be added to the position of the corner
	 * @return The position of the offset from the closest corner
	 */
	public static Position closestCornerOffset(Obstacle o, Position p, Position wayPoint, double offset) {
		Position minPos = closestCorner(o, p, wayPoint);

		if (minPos == null)
			return null;

		if (minPos.equals(o.topLeft)) {
			return new Position(minPos.x - (offset * Math.cos(Math.PI / 4)),
					minPos.y - (offset * Math.sin(Math.PI / 4)));
		}

		if (minPos.equals(o.bottomLeft)) {
			return new Position(minPos.x - (offset * Math.cos(Math.PI / 4)),
					minPos.y + (offset * Math.sin(Math.PI / 4)));
		}

		if (minPos.equals(o.topRight)) {
			return new Position(minPos.x + (offset * Math.cos(Math.PI / 4)),
					minPos.y - (offset * Math.sin(Math.PI / 4)));
		}

		return new Position(minPos.x + (offset * Math.cos(Math.PI / 4)), minPos.y + (offset * Math.sin(Math.PI / 4)));

	}

	/**
	 * Get the closest corner on the obstacle to the given point, ensuring that the distance from the corner to the waypoint isn't greater than the specified
	 * max distance
	 * 
	 * @param o
	 *            The obstacle to be checked
	 * @param p
	 *            The position of the player
	 * @param wayPoint
	 *            The way point to be compared to
	 * @return The closest corner to the player
	 */
	public static Position closestCorner(Obstacle o, Position p, Position wayPoint) {
		Position minPos = o.topLeft;
		double minDist = Maths.dist(p, o.topLeft);
		double maxDist = Maths.dist(p, wayPoint);

		double blDist = Maths.dist(p, o.bottomLeft);
		double trDist = Maths.dist(p, o.topRight);
		double brDist = Maths.dist(p, o.bottomRight);

		if (Maths.dist(o.bottomLeft, wayPoint) < maxDist && blDist < minDist) {
			minDist = blDist;
			minPos = o.bottomLeft;
		}

		if (Maths.dist(o.topRight, wayPoint) < maxDist && trDist < minDist) {
			minDist = trDist;
			minPos = o.topRight;
		}

		if (Maths.dist(o.bottomRight, wayPoint) < maxDist && brDist < minDist) {
			minDist = brDist;
			minPos = o.bottomRight;
		}

		if (Maths.dist(o.bottomLeft, minPos) < maxDist)
			return minPos;
		else
			return null;
	}

}
