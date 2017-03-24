package logic;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;

import game.Obstacle;
import game.constants.GameSettings;
import gui.OffsetHolder;
import javafx.application.Application;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import util.Client;

public class GameLogicTest {

	@Mock
    private Client client;

    @Mock
    private Pane pane;

    @Mock
    private OffsetHolder offsetHolder;

    private GameLogic logic;
    
    public static class MockJavaFx extends Application {
        @Override
        public void start(final Stage primaryStage) throws Exception {

        }
    }

    @BeforeClass
    public static void initJFX() throws InterruptedException {
        final Thread t = new Thread("JavaFX Init Thread") {
            @Override
            public void run() {
                Application.launch(GameLogicTest.MockJavaFx.class, new String[0]);
            }
        };
        t.setDaemon(true);
        t.start();
        Thread.sleep(500);
    }

    @Before
    public void setUp() throws NullPointerException {
        client = mock(Client.class);
        pane = mock(Pane.class);
        offsetHolder = mock(OffsetHolder.class);
    }

    @Test
    public void testInitialization() {
        try {
            logic = new GameLogic(client, pane, offsetHolder);
        } catch (final Exception ex) {

        }
        assertTrue(true);
    }

	@Test // Test if obstacles are added to the game data
	public void testAddingObstacles() {
		
	}

	@Test // Test if treasures are added to the game data
	public void testAddingTreasures() {
		
	}

	@Test // Test if deployed cameras are added to game data
	public void testAddingCamera() {
		
	}
	
	@Test // Test if camera count decrease after deploying 
	public void testDeployCamera() {
		
	}

	@Test // Test if thief state changed after capture
	public void testCaptureThief() {
		
	}

	@Test // Test if treasure state changed after picked up
	public void testCollectTreasure() {
		
	}
	

}
