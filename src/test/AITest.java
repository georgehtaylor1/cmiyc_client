package test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ai.Helper;
import game.Faction;
import game.Player;
import game.Treasure;
import game.util.Position;

public class AITest {

	ArrayList<Treasure> sampleTreasures = new ArrayList<Treasure>();
	ConcurrentHashMap<String, Player> samplePlayers = new ConcurrentHashMap<String, Player>();

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

	@After
	public void tearDown() throws Exception {
		sampleTreasures = null;
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetClosestTreasure() {
		assertTrue(sampleTreasures.get(0).equals(Helper.getClosestTreasure(new Position(2, 2), sampleTreasures)));
		assertTrue(sampleTreasures.get(2).equals(Helper.getClosestTreasure(new Position(0, 7), sampleTreasures)));
		assertTrue(sampleTreasures.get(1).equals(Helper.getClosestTreasure(new Position(6, 0), sampleTreasures)));
		assertTrue(sampleTreasures.get(3).equals(Helper.getClosestTreasure(new Position(7, 7), sampleTreasures)));
		assertTrue(sampleTreasures.get(4).equals(Helper.getClosestTreasure(new Position(15, 20), sampleTreasures)));
	}

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

	@Test
	public void testGetNextWaypoint() {

	}

	@Test
	public void testClosestCorner() {

	}

	@Test
	public void testGetCollisionPoint() {

	}

}
