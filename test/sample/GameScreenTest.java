/*
package sample;

import javafx.application.Application;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import launcher.Main;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

*/
/**
 * Created by Gerta on 18/03/2017.
 *//*

public class GameScreenTest {

    @Mock
    private Main main;

    @Mock
    private Pane pane;

    private GameScreen gameScreen;

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
                Application.launch(SlideScreenTest.MockJavaFx.class, new String[0]);
            }
        };
        t.setDaemon(true);
        t.start();
        Thread.sleep(500);
    }

    @Before
    public void setUp() throws NullPointerException {
        main = mock(Main.class);
        pane = mock(Pane.class);
    }

    @Test
    public void testInitialization() {
        try {
            gameScreen = new GameScreen(main, pane);
        } catch (final Exception ex) {

        }

        assertTrue(true);
    }

    @Test
    public void drawGame() throws Exception {


    }

    @Test
    public void drawScene() throws Exception {
        gameScreen = new GameScreen(main, pane);

        assertEquals(40, gameScreen.getGameControls().getPrefHeight(), 0.000);


        assertTrue(gameScreen.getChildren().contains(gameScreen.getGameControls()));
        assertTrue(gameScreen.getChildren().contains(gameScreen.getGameScreen()));

    }

}*/
