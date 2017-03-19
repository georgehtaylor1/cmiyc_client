package test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ai.Helper;
import game.Faction;
import game.Obstacle;
import game.Player;
import game.Treasure;
import game.util.Position;

public class AITest {

	ArrayList<Treasure> sampleTreasures = new ArrayList<Treasure>();
	ConcurrentHashMap<String, Player> samplePlayers = new ConcurrentHashMap<String, Player>();

	/**
	 * Setup the data for the tests
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {

		sampleTreasures.add(new Treasure(0, 0));
		sampleTreasures.add(new Treasure(10, 0));
		sampleTreasures.add(new Treasure(0, 10));
		sampleTreasures.add(new Treasure(10, 10));
		sampleTreasures.add(new Treasure(20, 20));

		Player sample = new Player("s1");
		sample.position = new Position(0, 0);
		sample.faction = Faction.SECURITY;
		samplePlayers.put("s1", sample);

		sample = new Player("s2");
		sample.position = new Position(20, 0);
		sample.faction = Faction.SECURITY;
		samplePlayers.put("s2", sample);

		sample = new Player("s3");
		sample.position = new Position(30, 20);
		sample.faction = Faction.SECURITY;
		samplePlayers.put("s3", sample);

		sample = new Player("s4");
		sample.position = new Position(50, 10);
		sample.faction = Faction.SECURITY;
		samplePlayers.put("s4", sample);

		sample = new Player("t1");
		sample.position = new Position(10, 10);
		sample.faction = Faction.THIEF;
		samplePlayers.put("t1", sample);

		sample = new Player("t2");
		sample.position = new Position(25, 25);
		sample.faction = Faction.THIEF;
		samplePlayers.put("t2", sample);

		sample = new Player("t3");
		sample.position = new Position(15, 30);
		sample.faction = Faction.THIEF;
		samplePlayers.put("t3", sample);

		sample = new Player("t4");
		sample.position = new Position(60, 10);
		sample.faction = Faction.THIEF;
		samplePlayers.put("t4", sample);
	}

	/**
	 * Reset the tests
	 * 
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		sampleTreasures = null;
		samplePlayers = null;
	}

	/**
	 * Test the function Helper.getClosestTreasure()
	 */
	@Test
	public void testGetClosestTreasure() {
		assertTrue(sampleTreasures.get(0).equals(Helper.getClosestTreasure(new Position(2, 2), sampleTreasures)));
		assertTrue(sampleTreasures.get(2).equals(Helper.getClosestTreasure(new Position(0, 7), sampleTreasures)));
		assertTrue(sampleTreasures.get(1).equals(Helper.getClosestTreasure(new Position(6, 0), sampleTreasures)));
		assertTrue(sampleTreasures.get(3).equals(Helper.getClosestTreasure(new Position(7, 7), sampleTreasures)));
		assertTrue(sampleTreasures.get(4).equals(Helper.getClosestTreasure(new Position(15, 20), sampleTreasures)));
	}

	/**
	 * Test the function Helper.getClosestThief()
	 */
	@Test
	public void testGetClosestThief() {
		assertTrue(samplePlayers.get("t1")
				.equals(Helper.getClosestThief(new Position(2, 2), "", new ArrayList<Player>(samplePlayers.values()))));
		assertTrue(samplePlayers.get("t2").equals(
				Helper.getClosestThief(new Position(20, 20), "", new ArrayList<Player>(samplePlayers.values()))));
		assertTrue(samplePlayers.get("t3").equals(
				Helper.getClosestThief(new Position(30, 30), "", new ArrayList<Player>(samplePlayers.values()))));
		assertTrue(samplePlayers.get("t4").equals(
				Helper.getClosestThief(new Position(70, 5), "", new ArrayList<Player>(samplePlayers.values()))));
	}

	/**
	 * Test the function Helper.getNextWaypoint()
	 */
	@Test
	public void testGetNextWaypoint() {
		assertTrue(sampleTreasures.get(1).equals(Helper.getNextWayPoint(new Position(1, 0), sampleTreasures,
				sampleTreasures.get(0).position, 0, new Random())));
		assertTrue(sampleTreasures.get(3).equals(Helper.getNextWayPoint(new Position(30, 30), sampleTreasures,
				sampleTreasures.get(4).position, 0, new Random())));
		assertTrue(sampleTreasures.get(3).equals(Helper.getNextWayPoint(new Position(0, 15), sampleTreasures,
				sampleTreasures.get(2).position, 0, new Random())));
	}

	/**
	 * Test the function Helper.closestCorner()
	 */
	@Test
	public void testClosestCorner() {
		Obstacle obs = new Obstacle(20, 20, 50, 50);
		try{
		assertTrue(
				(new Position(20, 20)).at(Helper.closestCorner(obs, new Position(10, 10), new Position(100, 100)), 1));
		assertTrue(
				(new Position(70, 70)).at(Helper.closestCorner(obs, new Position(60, 80), new Position(100, 100)), 1));
		assertTrue((new Position(20, 70)).at(Helper.closestCorner(obs, new Position(0, 80), new Position(10, 75)), 1));
		}catch(Exception ex){
			System.out.println("Unexpected error occured");
			assert(false);
		}
	}

	/**
	 * Test the function that gets the collision point on an obstacle
	 */
	@Test
	public void testGetCollisionPoint() {
		Obstacle obs = new Obstacle(20, 20, 50, 50);
		assertTrue((new Position(20, 20)).at(Helper.getCollisionPoint(new Position(10, 10), obs), 0.1));
		assertTrue((new Position(50, 20)).at(Helper.getCollisionPoint(new Position(50, 10), obs), 0.1));
		assertTrue((new Position(70, 20)).at(Helper.getCollisionPoint(new Position(90, 10), obs), 0.1));
		assertTrue((new Position(70, 60)).at(Helper.getCollisionPoint(new Position(90, 60), obs), 0.1));
		assertTrue((new Position(50, 70)).at(Helper.getCollisionPoint(new Position(50, 90), obs), 0.1));
	}

}
