package gui;

import java.util.ArrayList;

import constants.Colors;

import game.Obstacle;
import game.Treasure;
import game.constants.GameSettings;

import gui.util.FxUtils;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
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
        pane.setStyle("-fx-background-color: " + FxUtils.toRGBCode(Colors.black)
                + ";");
        pane.setPrefSize(GraphicsSettings.initialPaneWidth,
                GraphicsSettings.initalPaneHeight);
    }

    /**
     * Draws the current state of the game to the Pane.
     */
    public void draw() {
        pane.getChildren().clear();

        // Assume security

        ArrayList<Arc> lightArcs = new ArrayList<>();

        // Client player
        Arc clientLightArc = new Arc(main.player.position.x,
                main.player.position.y, GameSettings.Security.lightRadius,
                GameSettings.Security.lightRadius,
                -Math.toDegrees(main.player.direction)
                        - GameSettings.Security.lightRadius / 2,
                GameSettings.Security.lightArcPercentage * 360 / 100);

        clientLightArc.setType(ArcType.ROUND);

        lightArcs.add(clientLightArc);

        ArrayList<Rectangle> obstacleShapes = new ArrayList<>();
        for (Obstacle o : main.gameData.obstacles) {
            Rectangle r = new Rectangle(o.width, o.height, Color.LIGHTBLUE);
            r.setX(o.topLeft.x);
            r.setY(o.topLeft.y);
            obstacleShapes.add(r);
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
        double occlusionLength = pane.getWidth() * 1000;
        for (Arc arc : lightArcs) {
            Shape occludedArc = arc;

            for (Line edge : obstacleEdges) {
                double v2x = edge.getEndX() - arc.getCenterX();
                double v2y = edge.getEndY() - arc.getCenterY();
                double v2l = Math.sqrt(v2x * v2x + v2y * v2y);
                double v3x = (occlusionLength * v2x) / v2l;
                double v3y = (occlusionLength * v2y) / v2l;
                double x3 = arc.getCenterX() + v3x;
                double y3 = arc.getCenterY() + v3y;

                double v1x = edge.getStartX() - arc.getCenterX();
                double v1y = edge.getStartY() - arc.getCenterY();
                double v1l = Math.sqrt(v1x * v1x + v1y * v1y);
                double v4x = (occlusionLength * v1x) / v1l;
                double v4y = (occlusionLength * v1y) / v1l;
                double x4 = arc.getCenterX() + v4x;
                double y4 = arc.getCenterY() + v4y;

                Polygon p = new Polygon(edge.getStartX(), edge.getStartY(),
                        edge.getEndX(), edge.getEndY(), x3, y3, x4, y4);

                occludedArc = Shape.subtract(occludedArc, p);
            }

            occludedArc.setFill(Color.YELLOW);
            occludedLightArcs.add(occludedArc);
        }

        // Make treasure shapes
        ArrayList<Shape> treasureShapes = new ArrayList<>();
        for (Treasure t : main.gameData.treasures) {
            Circle c = new Circle(GameSettings.Treasure.radius,
                    Color.LIGHTYELLOW);
            c.setCenterX(t.position.x);
            c.setCenterY(t.position.y);
            treasureShapes.add(c);
        }

        // Make player shapes

        // Client player
        Circle clientPlayerShape = new Circle(GameSettings.Player.radius,
                Color.GREEN);
        clientPlayerShape.setCenterX(main.player.position.x);
        clientPlayerShape.setCenterY(main.player.position.y);

        // Draw
        pane.getChildren().addAll(obstacleShapes);
        pane.getChildren().addAll(treasureShapes);
        pane.getChildren().addAll(occludedLightArcs);
        pane.getChildren().add(clientPlayerShape);
    }
}
