package gui;

import ai.handler.Handler;
import game.Camera;
import game.Faction;
import game.Obstacle;
import game.Player;
import game.Treasure;
import game.util.Position;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import launcher.Main;
import logic.GameLogic;
import logic.GameLoop;

/**
 * Main class that tests the graphics module.
 */
public class GraphicsTest extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage stage) {
        Main main = new Main();

        main.gameData.obstacles.add(new Obstacle(400, 340, 120, 80));
        main.gameData.obstacles.add(new Obstacle(200, 180, 60, 120));
        main.gameData.obstacles.add(new Obstacle(600, 50, 100, 100));

        main.gameData.treasures.add(new Treasure(300, 300));
        main.gameData.treasures.add(new Treasure(310, 310));
        main.gameData.treasures.add(new Treasure(390, 400));

        Handler h = new Handler(main.gameData);
        h.addPlayers(0, 1);
        h.start();

        Player tom = new Player("tom");
        tom.faction = Faction.THIEF;
        tom.position = new Position(100, 150);
        main.gameData.players.put("tom", tom);
        main.player.faction = Faction.THIEF;
        main.gameData.players.put(main.player.clientID, main.player);

        Player bob = new Player("bob");
        main.gameData.players.put("bob", bob);

        main.gameData.cameras.add(new Camera(500, 300, Math.PI / 6.0, 50));

        Pane pane = new Pane();

        GameLogic logic = new GameLogic(main, pane);
        GameDrawer drawer = new GameDrawer(main, pane);

        Scene scene = new Scene(pane);
       
        scene.setCursor(Cursor.CROSSHAIR); // TODO We could add our own cursor
                                           // later.
        stage.setScene(scene);
        stage.sizeToScene();
        stage.setTitle("Graphics test");

        stage.setOnCloseRequest(e -> {
            e.consume();
            h.end();
            Platform.exit();
        });

        stage.show();
        // requestFocus() only works after stage.show().
        pane.requestFocus();

        Thread drawerThread = new Thread(new GameLoop(drawer, logic, h));
        drawerThread.setDaemon(true);
        drawerThread.start();

    }

}
