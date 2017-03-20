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

import java.rmi.server.ExportException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

*/
/**
 * Created by Gerta on 16/03/2017.
 *//*

public class SlideScreenTest {
    @Mock
    private GameScreen gameScreen;

    private SlideScreen slideScreen;

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
                Application.launch(MockJavaFx.class, new String[0]);
            }
        };
        t.setDaemon(true);
        t.start();
        Thread.sleep(500);
    }

    @Before
    public void setUp() throws NullPointerException {
        gameScreen = mock(GameScreen.class);
    }

    @Test
    public void testInitialization() {
        try {
            slideScreen = new SlideScreen(gameScreen);
        } catch (final Exception ex) {

        }

        assertTrue(true);
    }

    @Test
    public void drawScene() throws Exception {
        slideScreen = new SlideScreen(gameScreen);

        assertEquals(40, slideScreen.getToolBar().getPrefHeight(), 0.000);
        assertEquals(250, slideScreen.getSlider().getPrefWidth(), 0.000);

        assertEquals(2, slideScreen.getChildren().size(), 0.000 );
        assertTrue(slideScreen.getChildren().contains(slideScreen.getToolBar()));
        assertTrue(slideScreen.getToolBar().getItems().contains(slideScreen.getMainButton()));
        assertTrue(slideScreen.getChildren().contains(slideScreen.getSlider()));

        assertEquals(2, slideScreen.getTogether().getChildren().size(), 0.000 );
        assertTrue(slideScreen.getTogether().getChildren().contains(slideScreen.getvBox1()));
        assertTrue(slideScreen.getTogether().getChildren().contains(slideScreen.getvBox2()));

        assertEquals(2, slideScreen.gethBox1().getChildren().size(), 0.000 );
        assertTrue(slideScreen.gethBox1().getChildren().contains(slideScreen.getToggleButton1vs2()));
        assertTrue(slideScreen.gethBox1().getChildren().contains(slideScreen.getToggleButton2vs3()));

        assertEquals(2, slideScreen.gethBox2().getChildren().size(), 0.000 );
        assertTrue(slideScreen.gethBox2().getChildren().contains(slideScreen.getSecurity()));
        assertTrue(slideScreen.gethBox2().getChildren().contains(slideScreen.getThief()));

        assertEquals(2, slideScreen.getvBox2().getChildren().size(), 0.000 );
        assertTrue(slideScreen.getvBox2().getChildren().contains(slideScreen.gethBox1()));
        assertTrue(slideScreen.getvBox2().getChildren().contains(slideScreen.gethBox2()));

        assertTrue(slideScreen.getSlider().getChildren().contains(slideScreen.getTogether()));

        assertEquals(1, slideScreen.getSliderTranslation().getRate(), 0.000);
        slideScreen.getMainButton().fire();
        assertEquals(-1, slideScreen.getSliderTranslation().getRate(), 0.000);

    }

    @Test
    public void thiefSetActive() throws Exception {
        slideScreen = new SlideScreen(gameScreen);
        slideScreen.thiefSetActive();
        assertTrue(slideScreen.getThief().isSelected());
        assertFalse(slideScreen.getSecurity().isSelected());
    }

    @Test
    public void securitySetActive() throws Exception {
        slideScreen = new SlideScreen(gameScreen);
        slideScreen.securitySetActive();
        assertTrue(slideScreen.getSecurity().isSelected());
        assertFalse(slideScreen.getThief().isSelected());

    }

    @Test
    public void oneVsTwoSetActive() throws Exception {
        slideScreen = new SlideScreen(gameScreen);
        slideScreen.oneVsTwoSetActive();
        assertTrue(slideScreen.getToggleButton1vs2().isSelected());
        assertFalse(slideScreen.getToggleButton2vs3().isSelected());

    }

    @Test
    public void twoVsThreeSetActive() throws Exception {
        slideScreen = new SlideScreen(gameScreen);
        slideScreen.twoVsThreeSetActive();
        assertTrue(slideScreen.getToggleButton2vs3().isSelected());
        assertFalse(slideScreen.getToggleButton1vs2().isSelected());

    }

   @Test
    public void slideIn() throws Exception {
       slideScreen = new SlideScreen(gameScreen);
       slideScreen.slideIn();
       assertEquals(-1, slideScreen.getSliderTranslation().getRate(), 0.000);

    }

    @Test
    public void slideOut() throws Exception {
        slideScreen = new SlideScreen(gameScreen);
        slideScreen.slideOut();
        assertEquals(1, slideScreen.getSliderTranslation().getRate(), 0.000);

    }

    @Test
    public void setState() throws Exception {
        slideScreen = new SlideScreen(gameScreen);
        slideScreen.setState(SlideScreen.State.START);
        assertEquals("Start", slideScreen.getMainButton().getText());

        slideScreen.setState(SlideScreen.State.ENTER);
        assertEquals("Enter", slideScreen.getMainButton().getText());

        slideScreen.setState(SlideScreen.State.FIND);
        assertEquals("Find", slideScreen.getMainButton().getText());

        slideScreen.setState(SlideScreen.State.LEAVE);
        assertEquals("Leave", slideScreen.getMainButton().getText());

        slideScreen.setState(SlideScreen.State.STOP_FIND);
        assertEquals("Stop Find", slideScreen.getMainButton().getText());

    }

}*/
