package sample;

import java.io.IOException;

import ai.handler.Handler;
import game.constants.GameSettings;
import gui.GameDrawer;
import gui.OffsetHolder;
import javafx.scene.Scene;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import launcher.Main;
import logic.GameLogic;
import logic.GameLoop;

/**
 * Created by Gerta on 24/02/2017.
 */
public class GameScreen extends AnchorPane{

    public BorderPane gameScreen;
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
        OffsetHolder offsetHolder = new OffsetHolder();
        
        
        logic = new GameLogic(launcherMain, base, offsetHolder);
        drawer = new GameDrawer(launcherMain, pane, offsetHolder);

        
        Handler h = new Handler(launcherMain.gameData);
        h.addPlayers(0, 0);
        h.start();
        
        Thread drawerThread = new Thread(new GameLoop(drawer, logic, h));
        drawerThread.setDaemon(true);
        drawerThread.start();
        gameScreen.setCenter(pane);
    }


    public void drawScene() {

        this.getStylesheets().add("styles/gameLayer.css");

        gameControls.setPrefHeight(40);
        this.setPrefWidth(GameSettings.Arena.outerSize.getWidth());
        this.setPrefHeight(GameSettings.Arena.outerSize.getWidth());
        this.getChildren().addAll(gameScreen, gameControls);

        gameScreen.setPrefWidth(1900);
        gameScreen.setPrefHeight(1000);

        System.out.println(gameScreen.getHeight());
        AnchorPane.setBottomAnchor(gameControls, 0.0);
        AnchorPane.setRightAnchor(gameControls, 0.0);
        AnchorPane.setLeftAnchor(gameControls, 0.0);
//        AnchorPane.setTopAnchor(gameScreen, 40.0);
//        AnchorPane.setLeftAnchor(gameScreen, 40.0);
//        AnchorPane.setRightAnchor(gameScreen, 40.0);

        this.getStylesheets().add("styles/gameLayer.css");
        this.setId("gameLayer");
        gameScreen.setId("gameScreen");
        gameControls.setId("gameControls");

    }

}
