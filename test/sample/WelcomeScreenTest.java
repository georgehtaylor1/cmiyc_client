/*
package sample;

import javafx.application.Application;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

*/
/**
 * Created by Gerta on 18/03/2017.
 *//*

public class WelcomeScreenTest {

    private WelcomeScreen welcomeScreen;

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
    public void setUp() throws Exception {
        welcomeScreen = new WelcomeScreen();
    }


    @Test
    public void drawScene() throws Exception {
        welcomeScreen = new WelcomeScreen();
        assertTrue(welcomeScreen.getChildren().contains(welcomeScreen.getWelcomeControls()));
        assertTrue(welcomeScreen.getChildren().contains(welcomeScreen.getWelcomeScreen()));
        
    }

    @Test
    public void setAnchor() throws Exception {

    }

}*/
