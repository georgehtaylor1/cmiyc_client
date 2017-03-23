package ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import game.Faction;
import game.GameData;
import game.Obstacle;
import game.Player;
import game.Treasure;
import game.constants.GameSettings;
import game.states.TreasureState;
import game.util.Position;
import util.Maths;

public class Helper {

	/**
	 * Get a random valid position in the arena
	 * 
	 * @param gameData
	 *            The game data containing obstacles, treasures and players to be avoided
	 * @return A valid random position
	 */
	public static Position getRandomFreePosition(GameData gameData) {

		Position p = new Position();
		p.x = gameData.rand.nextInt(GameSettings.Arena.size.width - 40) + 20;
		p.y = gameData.rand.nextInt(GameSettings.Arena.size.height - 40) + 20;

		while (!validPos(p, gameData.treasures, gameData.obstacles, new ArrayList<Player>(gameData.players.values()),
				5)) {
			p.x = gameData.rand.nextInt(GameSettings.Arena.size.width - 40) + 20;
			p.y = gameData.rand.nextInt(GameSettings.Arena.size.height - 40) + 20;
		}
		return p;
	}

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
		Treasure minTreasure = null;
		double minDist = GameSettings.Arena.outerSize.getHeight() + GameSettings.Arena.outerSize.getWidth();

		for (Treasure t : treasures) {
			if (t.state == TreasureState.UNPICKED) {
				double currDist = Maths.dist(p, t.position);
				if (currDist < minDist) {
					minDist = currDist;
					minTreasure = t;
				}
			}
		}

		return minTreasure;

	}

	/**
	 * Get a random start position that does not conflict with any existing players, treasures or obstacles
	 * 
	 * @param treasures
	 *            The list of treasures to avoid
	 * @param obstacles
	 *            The list of obstacles to avoid
	 * @param players
	 *            The list of players to avoid
	 * @param rand
	 *            The random generator
	 * @return A valid random position
	 */
	public static Position getStartPositon(ArrayList<Treasure> treasures, ArrayList<Obstacle> obstacles,
			ArrayList<Player> players, Random rand) {
		boolean notValid = true;
		Position p = new Position();
		while (notValid) {
			int x = rand.nextInt(GameSettings.Arena.outerSize.width);
			int y = rand.nextInt(GameSettings.Arena.outerSize.height);
			p = new Position(x, y);
			notValid = !validPos(p, treasures, obstacles, players, 10);
		}
		return p;
	}

	/**
	 * A helper for the previous version it indicates whether a given position is valid
	 * 
	 * @param p
	 *            The position to test
	 * @param treasures
	 *            The list of treasures to be avoided
	 * @param obstacles
	 *            The list of obstacles to be avoided
	 * @param players
	 *            The list of players to be avoided
	 * @param threshold
	 *            The threshold distance to avoid players and treasures
	 * @return A boolean indicating whether or not the given position is valid
	 */
	private static boolean validPos(Position p, ArrayList<Treasure> treasures, ArrayList<Obstacle> obstacles,
			ArrayList<Player> players, double threshold) {
		for (Treasure t : treasures)
			if (p.at(t.position, threshold))
				return false;

		for (Obstacle o : obstacles)
			if (o.contains(p))
				return false;

		for (Player player : players)
			if (player.position.at(p, threshold))
				return false;

		return true;
	}

	/**
	 * Get the player that is closest to the given point
	 * 
	 * @param p
	 *            The position to compare to
	 * @param myName
	 *            the clientID of the current player so that they can be ignored
	 * @param players
	 *            The players to check
	 * @return The closest player
	 */
	public static Player getClosestThief(Position p, String myName, ArrayList<Player> players) {
		Player minPlayer = null;
		// No player can be further away than this
		double minDist = GameSettings.Arena.outerSize.getHeight() + GameSettings.Arena.outerSize.getWidth();

		for (Player player : players) {
			if (!player.clientID.equals(myName) && player.faction == Faction.THIEF) {
				double dist = Maths.dist(player.position, p);
				if (dist < minDist) {
					minDist = dist;
					minPlayer = player;
				}
			}
		}
		return minPlayer;

	}

	/**
	 * Get the position of the next waypoint
	 * 
	 * @param p
	 *            The posiiton of the AI
	 * @param treasures
	 *            The list of treasures
	 * @param previousWaypoint
	 *            The current waypoint so that it can be ignored
	 * @param randomness
	 *            A double value representing the probability of a random waypoint being selected
	 * @return The next waypoint to go to
	 */
	public static Position getNextWayPoint(Position p, GameData gameData, Position previousWaypoint, double randomness,
			Random rand) {

		if (rand.nextDouble() < randomness) {

			int index = rand.nextInt(gameData.treasures.size());
			Position chosenPosition = gameData.treasures.get(index).position;
			
			ArrayList<Treasure> randTreasures = (ArrayList<Treasure>) gameData.treasures.clone();
			Collections.shuffle(randTreasures, gameData.rand);
			Iterator<Treasure> i = randTreasures.iterator();
			while(i.hasNext()){
				Treasure t = i.next();
				if(t.state == TreasureState.UNPICKED)
					return t.position;
			}
			return getRandomFreePosition(gameData);
		}

		double minDist = GameSettings.Arena.outerSize.getHeight() + GameSettings.Arena.outerSize.getWidth();
		Position minPos = null;
		for (Treasure t : gameData.treasures) {
			if (t.state != TreasureState.PICKED && !t.position.at(p, 2)
					&& (previousWaypoint != null ? !t.position.at(previousWaypoint, 2) : true)) {
				double d = Maths.dist(p, t.position);
				if (d < minDist) {
					minDist = d;
					minPos = t.position;
				}
			}
		}

		// If no treasure has been found then pick a random treasure instead
		if (minPos == null)
			return getRandomFreePosition(gameData);

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

		double tlDist = Maths.dist(p, o.topLeft);
		double blDist = Maths.dist(p, o.bottomLeft);
		double trDist = Maths.dist(p, o.topRight);
		double brDist = Maths.dist(p, o.bottomRight);

		Position minPos = null;
		double minDist = Maths.dist(p, wayPoint);

		if (tlDist < minDist) {
			minDist = tlDist;
			minPos = o.topLeft;
		}

		if (trDist < minDist) {
			minDist = trDist;
			minPos = o.topRight;
		}

		if (blDist < minDist) {
			minDist = blDist;
			minPos = o.bottomLeft;
		}

		if (brDist < minDist) {
			minDist = brDist;
			minPos = o.bottomRight;
		}

		return minPos;
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
	public static Position getCollisionPoint(Position p, Obstacle o) {
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
	 * Move the given player with the given speed without allowing them to pass through an obstacle
	 * 
	 * @param p
	 *            The player to move
	 * @param obstacles
	 *            The obstacles in the arena
	 * @param speed
	 *            The speed that the player should move at
	 */
	public static void move(Player p, ArrayList<Obstacle> obstacles, double speed) {
		boolean xFine = true;
		boolean yFine = true;
		boolean bothFine = true;

		Position testXY = new Position(p.position.x + (speed * Math.cos(p.direction)),
				p.position.y + (speed * Math.sin(p.direction)));
		Position testX = new Position(p.position.x + (speed * Math.cos(p.direction)), p.position.y);
		Position testY = new Position(p.position.x, p.position.y + (speed * Math.sin(p.direction)));

		if (testY.y <= 20 || testY.y >= GameSettings.Arena.size.getHeight()) {
			yFine = false;
			bothFine = false;
		}

		if (testX.x <= 20 || testX.x >= GameSettings.Arena.size.getWidth() - 20) {
			xFine = false;
			bothFine = false;
		}

		for (Obstacle o : obstacles) {
			if (o.contains(testXY)) {
				bothFine = false;
			}
			if (o.contains(testX))
				xFine = false;
			if (o.contains(testY))
				yFine = false;
		}
		if (bothFine) {
			p.position = testXY;
			return;
		}
		if (xFine && !yFine) {
			p.position = testX;
			return;
		}
		if (!xFine && yFine) {
			p.position = testY;
			return;
		}
	}

}
