package sample;

import java.io.IOException;

import ai.handler.Handler;
import game.constants.GameSettings;
import gui.GameDrawer;
import gui.OffsetHolder;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import launcher.Main;
import logic.GameLogic;
import logic.GameLoop;

/**
 * Created by Gerta on 24/02/2017.
 */
public class GameScreen extends AnchorPane{

    public BorderPane gameScreen;
    private HBox gameControls;
    private GameLogic logic;
    private GameDrawer drawer;
    private Main launcherMain;
    public Pane pane;
    public Pane base;
    public Handler aiHandler;


    /**
     * Create a new instance of the GameScreen
     * @param _main
     * @param base
     * @throws IOException
     */
    public GameScreen(Main _main, Pane base) throws IOException {
        this.gameScreen = new BorderPane();
        this.gameControls = new HBox();
        this.launcherMain = _main;
        this.base = base;
        this.drawScene();
    }

    /**
     * Draw a new game
     */
    public void drawGame() {
        pane = new Pane();
        OffsetHolder offsetHolder = new OffsetHolder();

        logic = new GameLogic(launcherMain, base, offsetHolder);
        drawer = new GameDrawer(launcherMain, pane, offsetHolder);

        aiHandler = new Handler(launcherMain.gameData);
        aiHandler.addPlayers(0, 0);
        aiHandler.start();
        
        Thread drawerThread = new Thread(new GameLoop(drawer, logic, aiHandler));
        drawerThread.setDaemon(true);
        drawerThread.start();
        gameScreen.setCenter(pane);
    }


    /**
     * Get the gameScreen
     * @return the gameScreen
     */
    public BorderPane getGameScreen() {
        return gameScreen;
    }


    /**
     * Get the gameControls
     * @return the gameControls
     */
    public HBox getGameControls() {
        return gameControls;
    }


    /**
     * Draw the scene
     */
    public void drawScene() {

        this.getStylesheets().add("styles/gameLayer.css");

       // gameControls.setPrefHeight(40);
        this.setPrefWidth(GameSettings.Arena.outerSize.getWidth());
        this.setPrefHeight(GameSettings.Arena.outerSize.getHeight());
        this.getChildren().addAll(gameScreen, gameControls);

        AnchorPane.setBottomAnchor(gameControls, 0.0);
        AnchorPane.setRightAnchor(gameControls, 0.0);
        AnchorPane.setLeftAnchor(gameControls, 0.0);


        this.getStylesheets().add("gameLayer.css");
        this.setId("gameLayer");
        gameScreen.setId("gameScreen");
        gameControls.setId("gameControls");

    }

}
