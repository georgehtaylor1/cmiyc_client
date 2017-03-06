package gui;

import java.util.ArrayList;
import java.util.Map;

import constants.Colors;
import game.Camera;
import game.Faction;
import game.Obstacle;
import game.Player;
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
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import launcher.Main;

/**
 * Draws the state of the game to a Pane.
 */
public class GameDrawer {

	private Main main;
	private Pane pane;

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

		Shape darkness = new Rectangle(20, 20, GameSettings.Arena.size.width, GameSettings.Arena.size.height);

		ArrayList<Shape> treasureShapes = new ArrayList<>();
		for (Treasure t : main.gameData.treasures) {
			Circle c = new Circle(GameSettings.Treasure.radius, Colors.treasure);
			c.setCenterX(t.position.x);
			c.setCenterY(t.position.y);
			treasureShapes.add(c);
		}

		// Make obstacle shapes
		ArrayList<Rectangle> obstacleShapes = new ArrayList<>();
		for (Obstacle o : main.gameData.obstacles) {
			Rectangle r = new Rectangle(o.width, o.height, Color.TRANSPARENT);
			r.setStroke(Color.WHITE);
			r.setStrokeWidth(4);
			r.setX(o.topLeft.x);
			r.setY(o.topLeft.y);
			obstacleShapes.add(r);
		}
		ArrayList<Circle> thiefVision = new ArrayList<>();
		ArrayList<Arc> lightArcs = new ArrayList<>();
		ArrayList<Shape> allies = new ArrayList<>();
		ArrayList<Shape> enemies = new ArrayList<>();

		// Make camera shapes
		/*
		ArrayList<Rectangle> camera = new ArrayList<>();
		for (Camera c : main.gameData.cameras) {
			Rectangle box = new Rectangle(c.position.x, c.position.y, )
		}
		*/
		
		double arcAngle = (GameSettings.Security.lightArcPercentage / 100) * 360;

		for (Map.Entry<String, Player> entry : main.gameData.players.entrySet()) {

			Player player = entry.getValue();
			Circle c = new Circle(player.position.x, player.position.y, GameSettings.Player.radius);

			// Flashlight
			if (player.faction == Faction.SECURITY) {
				Arc base = new Arc(player.position.x, player.position.y, GameSettings.Security.lightRadius,
						GameSettings.Security.lightRadius, -Math.toDegrees(player.direction) - arcAngle / 2, arcAngle);

				base.setType(ArcType.ROUND);
				lightArcs.add(base);
				c.setFill(Colors.activeSecurity);
			} else {
				// TODO occlusion for thief vision
				Circle vision = new Circle(player.position.x, player.position.y, GameSettings.Thief.visionRadius);
				RadialGradient rg = new RadialGradient(0, 0.1, player.position.x, player.position.y,
						GameSettings.Security.lightRadius, false, CycleMethod.NO_CYCLE,
						new Stop[] { new Stop(0, Color.TRANSPARENT), new Stop(1, Colors.thiefVision) });
				vision.setFill(rg);
				Circle extra = new Circle(player.position.x, player.position.y, GameSettings.Thief.visionRadius-2);
				
				if (main.player.faction == Faction.THIEF) {
					darkness = Shape.subtract(darkness, extra);
				}
				
				thiefVision.add(0,vision);
				c.setFill(Colors.activeThief);
			}
			
			if (main.player.faction == player.faction) {
				allies.add(c);
			} else {
				enemies.add(c);
			}
		}

		// Calculate occlusion
		ArrayList<Line> obstacleEdges = new ArrayList<>();
		for (Rectangle r : obstacleShapes) {
			double leftX = r.getX();
			double rightX = r.getX() + r.getWidth();
			double topY = r.getY();
			double bottomY = r.getY() + r.getHeight();
			obstacleEdges.add(new Line(leftX, topY, leftX, bottomY));
			obstacleEdges.add(new Line(leftX, bottomY, rightX, bottomY));
			obstacleEdges.add(new Line(leftX, topY, rightX, topY));
			obstacleEdges.add(new Line(rightX, topY, rightX, bottomY));
		}

		ArrayList<Shape> occludedLightArcs = new ArrayList<>();
		for (Arc cutout : lightArcs) {
			Shape occludedCutout = cutout;

			Arc occludedLightArc = new Arc(cutout.getCenterX(), cutout.getCenterY(), cutout.getRadiusX() + 2,
					cutout.getRadiusY() + 2, cutout.getStartAngle() - 2, cutout.getLength() + 4);
			occludedLightArc.setType(ArcType.ROUND);

			Shape occludedLight = occludedLightArc;

			for (Line edge : obstacleEdges) {

				double vx = edge.getEndX() - edge.getStartX();
				double vy = edge.getEndY() - edge.getStartY();
				double vl = Math.sqrt(vx * vx + vy * vy);
				double wl = vl - 2;
				double wx = (wl * vx) / vl;
				double wy = (wl * vy) / vl;
				double bx = edge.getStartX() + wx;
				double by = edge.getStartY() + wy;
				double ax = edge.getEndX() - wx;
				double ay = edge.getEndY() - wy;

				Line lightEdge = new Line(ax, ay, bx, by);

				Polygon cutoutOcclusion = calcOcclusion(cutout.getCenterX(), cutout.getCenterY(), edge);
				occludedCutout = Shape.subtract(occludedCutout, cutoutOcclusion);

				Polygon lightOcclusion = calcOcclusion(cutout.getCenterX(), cutout.getCenterY(), lightEdge);
				occludedLight = Shape.subtract(occludedLight, lightOcclusion);
			}
			if (main.player.faction == Faction.SECURITY) {
				darkness = Shape.subtract(darkness, occludedCutout);
			}
			RadialGradient rg1 = new RadialGradient(0, 0.1, cutout.getCenterX(), cutout.getCenterY(),
					GameSettings.Security.lightRadius, false, CycleMethod.NO_CYCLE,
					new Stop[] { new Stop(0, Colors.flashlight), new Stop(1, Colors.fog) });

			occludedLight.setFill(rg1);
			occludedLightArcs.add(occludedLight);
		}

		
		Shape outerArena = new Rectangle(0, 0, 840, 530);
		Rectangle innerArena = new Rectangle(20, 20, 800, 450);
		outerArena = Shape.subtract(outerArena, innerArena);
		outerArena.setFill(Colors.outerArena);
		darkness.setFill(Colors.fog);

		// Draw
		pane.getChildren().add(innerArena);
		pane.getChildren().addAll(treasureShapes);
		pane.getChildren().addAll(enemies);
		pane.getChildren().addAll(obstacleShapes);
		pane.getChildren().addAll(occludedLightArcs);
		pane.getChildren().addAll(thiefVision);
		pane.getChildren().add(darkness);
		// Fog

		pane.getChildren().addAll(allies);
		pane.getChildren().add(outerArena);
	}

	/**
	 * Calculate the occlusion caused by the given edge.
	 */
	private Polygon calcOcclusion(double originX, double originY, Line edge) {
		double OCCLUSION_LENGTH = pane.getWidth() * 1000;

		double v2x = edge.getEndX() - originX;
		double v2y = edge.getEndY() - originY;
		double v2l = Math.sqrt(v2x * v2x + v2y * v2y);
		double v3x = (OCCLUSION_LENGTH * v2x) / v2l;
		double v3y = (OCCLUSION_LENGTH * v2y) / v2l;
		double x3 = originX + v3x;
		double y3 = originY + v3y;

		double v1x = edge.getStartX() - originX;
		double v1y = edge.getStartY() - originY;
		double v1l = Math.sqrt(v1x * v1x + v1y * v1y);
		double v4x = (OCCLUSION_LENGTH * v1x) / v1l;
		double v4y = (OCCLUSION_LENGTH * v1y) / v1l;
		double x4 = originX + v4x;
		double y4 = originY + v4y;

		return new Polygon(edge.getStartX(), edge.getStartY(), edge.getEndX(), edge.getEndY(), x3, y3, x4, y4);
	}
}
