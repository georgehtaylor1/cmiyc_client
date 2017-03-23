package sample;

import java.io.IOException;

import ai.handler.Handler;

import gui.GameDrawer;

import javafx.geometry.Rectangle2D;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;

import launcher.Main;

import logic.GameLogic;
import logic.GameLoop;

import util.Client;

/**
 * Created by Gerta on 24/02/2017.
 */
public class GameScreen extends AnchorPane {

    public BorderPane gameScreen;
    private ToolBar gameControls;
    private GameLogic logic;
    private GameDrawer drawer;
    public Client client;
    public Pane pane;
    public Pane base;

    public GameScreen(Main _main, Pane base) throws IOException {
        this.gameScreen = new BorderPane();
        this.gameControls = new ToolBar();
        this.client = _main.client;
        this.base = base;
        this.drawScene();
    }

    public void drawGame() {
        pane = new Pane();
        pane.setPrefSize(base.getWidth(), base.getHeight());

        logic = new GameLogic(client, base);
        drawer = new GameDrawer(client, pane);

        Handler h = new Handler(client.gameData);
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
        this.getChildren().addAll(gameScreen, gameControls);

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        gameScreen.setPrefWidth(bounds.getWidth());
        gameScreen.setPrefHeight(bounds.getHeight() - 40);

        AnchorPane.setBottomAnchor(gameControls, 0.0);
        AnchorPane.setRightAnchor(gameControls, 0.0);
        AnchorPane.setLeftAnchor(gameControls, 0.0);
        AnchorPane.setTopAnchor(gameScreen, 0.0);
        AnchorPane.setLeftAnchor(gameScreen, 0.0);
        AnchorPane.setRightAnchor(gameScreen, 0.0);
        AnchorPane.setBottomAnchor(gameScreen, 0.0);

        this.getStylesheets().add("styles/gameLayer.css");
        this.setId("gameLayer");
        gameScreen.setId("gameScreen");
        gameControls.setId("gameControls");

    }

}
