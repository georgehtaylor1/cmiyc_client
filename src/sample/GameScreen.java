package sample;

import ai.handler.Handler;
import game.Obstacle;
import game.constants.GameSettings;
import gui.GameDrawer;
import javafx.scene.Scene;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import launcher.Main;
import logic.GameLogic;
import logic.GameLoop;

import java.io.IOException;

/**
 * Created by Gerta on 24/02/2017.
 */
public class GameScreen extends AnchorPane{

    private BorderPane gameScreen;
    private ToolBar gameControls;
    private GameLogic logic;
    private GameDrawer drawer;
    private Main launcherMain;
    public Pane pane;
    public Pane base;
    private Scene scene;
    private Stage stage;

    public GameScreen(Main _main, Pane base) throws IOException {
        this.gameScreen = new BorderPane();
        this.gameControls = new ToolBar();
        this.launcherMain = _main;
        this.base = base;
        this.drawScene();
    }

    public void drawGame() {
        pane = new Pane();
        logic = new GameLogic(launcherMain, base);
        drawer = new GameDrawer(launcherMain, pane);

        /*
        Handler h = new Handler(launcherMain.gameData);
        h.addPlayers(1, 0);
        h.start();
        */
        Thread drawerThread = new Thread(new GameLoop(drawer, logic));
        drawerThread.setDaemon(true);
        drawerThread.start();
        gameScreen.getChildren().add(pane);
    }


    public void drawScene() {

        this.getStylesheets().add("styles/welcomeLayer.css");

        gameControls.setPrefHeight(40);
        this.setPrefWidth(GameSettings.Arena.size.getWidth());
        this.setPrefHeight(GameSettings.Arena.size.getHeight());
        this.getChildren().addAll(gameScreen, gameControls);

        gameScreen.setPrefWidth(500);
        gameScreen.setPrefHeight(300);

        AnchorPane.setBottomAnchor(gameControls, 0.0);
        AnchorPane.setRightAnchor(gameControls, 0.0);
        AnchorPane.setLeftAnchor(gameControls, 0.0);
        AnchorPane.setTopAnchor(gameScreen, 40.0);
        AnchorPane.setLeftAnchor(gameScreen, 40.0);
        AnchorPane.setRightAnchor(gameScreen, 40.0);

        this.getStylesheets().add("styles/gameLayer.css");
        this.setId("gameLayer");
        gameScreen.setId("gameScreen");
        gameControls.setId("gameControls");

    }

}
