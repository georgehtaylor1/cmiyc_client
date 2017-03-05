package gui;

import java.util.ArrayList;
import java.util.Map;

import constants.Colors;
import game.Faction;
import game.Obstacle;
import game.Treasure;
import game.constants.GameSettings;
import gui.util.FxUtils;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import launcher.Main;

/**
 * Draws the state of the game to a Pane.
 */
public class GameDrawer {

	private Main main;
	private Pane pane;

	private Rectangle outerArena;
	private Rectangle innerArena;
	private Shape treasures;
	private Shape enemies;
	private Shape obstacles;
	private Shape darkness;
	private Shape camera;
	private Shape treasureShadow;
	private Shape allies;
	private Shape player;

	/**
	 * Constructs a new GameDrawer. Default graphics settings are set on the
	 * Pane.
	 */
	public GameDrawer(Main main, Pane pane) {
		this.pane = pane;
		this.main = main;
		pane.setStyle("-fx-background-color: " + FxUtils.toRGBCode(Colors.black) + ";");
		pane.setPrefSize(GraphicsSettings.initialPaneWidth, GraphicsSettings.initalPaneHeight);

	}

	/**
	 * Draws the current state of the game to the Pane.
	 */
	public void draw() {
		pane.getChildren().clear();
		this.outerArena = new Rectangle(0, 0, 840, 530);
		this.innerArena = new Rectangle(20, 20, 800, 450);
		this.outerArena.setFill(Colors.outerArena);
		this.pane.getChildren().addAll(outerArena, innerArena);

		ArrayList<Shape> treasureShapes = new ArrayList<>();
		for (Treasure t : main.gameData.treasures) {
			Circle c = new Circle(GameSettings.Treasure.radius, Color.LIGHTYELLOW);
			c.setCenterX(t.position.x);
			c.setCenterY(t.position.y);
			treasureShapes.add(c);
		}

		// Assume security

		this.darkness = new Rectangle(20, 20, GameSettings.Arena.size.width, GameSettings.Arena.size.height);
		this.darkness.setFill(Colors.darker);
		// Make flashlight shapes
		ArrayList<Shape> flashlightShapes = new ArrayList<>();

		// Client player
		Arc clientFlashlightArc = new Arc(main.player.position.x, main.player.position.y,
				GameSettings.Security.lightRadius, GameSettings.Security.lightRadius,
				-Math.toDegrees(main.player.direction) - GameSettings.Security.lightRadius / 2,
				GameSettings.Security.lightArcPercentage * 360 / 100);

		clientFlashlightArc.setType(ArcType.ROUND);
		 RadialGradient rg = new
		 RadialGradient(0,0.1,100,100,GameSettings.Security.lightRadius,false,CycleMethod.NO_CYCLE,new
		 Stop[] {new Stop(0,Color.TRANSPARENT), new Stop(1,Color.WHITE)});

		clientFlashlightArc.setFill(Color.YELLOW);
		Shape.subtract(darkness, clientFlashlightArc);
		flashlightShapes.add(clientFlashlightArc);

		// Make obstacle shapes
		ArrayList<Shape> obstacleShapes = new ArrayList<>();
		for (Obstacle o : main.gameData.obstacles) {
			Rectangle r = new Rectangle(o.width, o.height, Color.LIGHTBLUE);
			r.setX(o.topLeft.x);
			r.setY(o.topLeft.y);
			obstacleShapes.add(r);
		}

		// Make treasure shapes

		// Make player shapes
		ArrayList<Shape> players = new ArrayList<>();
		for (Map.Entry<String, game.Player> entry : main.gameData.players.entrySet()) {

			Arc f = new Arc(entry.getValue().position.x, entry.getValue().position.y, GameSettings.Security.lightRadius,
					GameSettings.Security.lightRadius,
					-Math.toDegrees(entry.getValue().direction) - GameSettings.Security.lightRadius / 2,
					GameSettings.Security.lightArcPercentage * 360 / 100);

			f.setType(ArcType.ROUND);
			Shape.subtract(darkness, f);
			f.setFill(Color.YELLOW);
			flashlightShapes.add(f);
			Circle c = new Circle(entry.getValue().position.x, entry.getValue().position.y, GameSettings.Player.radius);
			if (entry.getValue().faction == Faction.SECURITY)
				c.setFill(Colors.activeSecurity);
			else
				c.setFill(Colors.activeThief);
			players.add(c);
		}

		// Client player
		Circle clientPlayerShape = new Circle(GameSettings.Player.radius, Color.GREEN);
		clientPlayerShape.setCenterX(main.player.position.x);
		clientPlayerShape.setCenterY(main.player.position.y);

		// Draw
		pane.getChildren().addAll(obstacleShapes);
//		pane.getChildren().add(darkness);
		pane.getChildren().addAll(treasureShapes);
		pane.getChildren().addAll(flashlightShapes);
		pane.getChildren().addAll(players);
		pane.getChildren().add(clientPlayerShape);
	}
}
