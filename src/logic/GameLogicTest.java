package logic;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import game.Faction;
import game.Obstacle;
import game.Player;
import game.Treasure;
import game.states.PlayerState;
import game.states.TreasureState;
import game.util.Position;
import gui.OffsetHolder;
import javafx.scene.layout.Pane;
import sample.SlideScreenData;
import util.Client;

public class GameLogicTest {
	
	Client c = new Client("test", new SlideScreenData());
	OffsetHolder offsetHolder;
    GameLogic logic = new GameLogic(c, new Pane(), offsetHolder);;
    
    @Before
    public void setUp() throws Exception {

		c.gameData.treasures.add(new Treasure(0, 0));
		c.gameData.treasures.add(new Treasure(10, 0));
		c.gameData.treasures.add(new Treasure(0, 10));
		c.gameData.treasures.add(new Treasure(10, 10));
		c.gameData.treasures.add(new Treasure(20, 20));

		Player sample = new Player("s1");
		sample.position = new Position(0, 0);
		sample.faction = Faction.SECURITY;
		c.gameData.players.put("s1", sample);

		sample = new Player("s2");
		sample.position = new Position(20, 0);
		sample.faction = Faction.SECURITY;
		c.gameData.players.put("s2", sample);

		sample = new Player("s3");
		sample.position = new Position(30, 20);
		sample.faction = Faction.SECURITY;
		c.gameData.players.put("s3", sample);

		sample = new Player("s4");
		sample.position = new Position(50, 10);
		sample.faction = Faction.SECURITY;
		c.gameData.players.put("s4", sample);

		sample = new Player("t1");
		sample.position = new Position(10, 10);
		sample.faction = Faction.THIEF;
		c.gameData.players.put("t1", sample);

		sample = new Player("t2");
		sample.position = new Position(25, 25);
		sample.faction = Faction.THIEF;
		c.gameData.players.put("t2", sample);

		sample = new Player("t3");
		sample.position = new Position(15, 30);
		sample.faction = Faction.THIEF;
		c.gameData.players.put("t3", sample);

		sample = new Player("t4");
		sample.position = new Position(60, 10);
		sample.faction = Faction.THIEF;
		c.gameData.players.put("t4", sample);
    }

    @Test
    public void testInitialization() {
        try {
            logic = new GameLogic(c, new Pane(), offsetHolder);
        } catch (final Exception ex) {

        }
        assertTrue(true);
    }

	@Test // Test if obstacles are added to the game data
	public void testAddingObstacles() {
		assertTrue(c.gameData.obstacles.size() == 0);
		c.gameData.obstacles.add(new Obstacle(0,0,100,100));
		assertTrue(c.gameData.obstacles.size() == 1);
	}

	@Test // Test if treasures are added to the game data
	public void testAddingTreasures() {
		assertTrue (c.gameData.treasures.size() == 5);
		c.gameData.treasures.add(new Treasure(10, 10));
		assertTrue (c.gameData.treasures.size() == 6);
	}

	@Test // Test if camera count decrease after deploying 
	public void testDeployCamera() {
		assertTrue (c.player.cameras == 2);
		logic.deployCamera(new Position (100, 200), 140);
		assertTrue (c.player.cameras == 1);
	}

	@Test // Test if deployed cameras are added to game data
	public void testAddingCamera() {
		assertTrue (c.gameData.cameras.size() == 0);
		logic.deployCamera(new Position(100,200), 140);
		assertTrue (c.gameData.cameras.size() == 1);		
	}

	@Test // Test if thief state changed after capture
	public void testCaptureThief() {
		assertTrue (logic != null);
		logic.captureThief("t1", c.gameData.players.get("t1"));
		assertTrue (c.gameData.players.get("t1").state == PlayerState.CAUGHT);
	}
	
	@Test // Test if treasure state changed after picked up
	public void testCollectTreasure() {
		assertTrue (c.gameData.treasures.get(1) != null);
		logic.collectTreasure(c.gameData.treasures.get(1));
		assertTrue (c.gameData.treasures.get(1).state == TreasureState.PICKED);
	}
	

}
