package gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import constants.Colors;

import game.Faction;
import game.Obstacle;
import game.Player;
import game.constants.GameSettings;

import gui.util.FxUtils;

import javafx.scene.Node;
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
        pane.setStyle("-fx-background-color: " + FxUtils.toRGBCode(Colors.black)
                + ";");
        pane.setPrefSize(GraphicsSettings.initialPaneWidth,
                GraphicsSettings.initalPaneHeight);
    }

    /**
     * Draws the current state of the game to the Pane.
     */
    public void draw() {

        // Make obstacle shapes
        ArrayList<Rectangle> obstacleRects = new ArrayList<>();
        for (Obstacle o : main.gameData.obstacles) {
            Rectangle r = new Rectangle(o.width, o.height, Color.LIGHTBLUE);
            r.setX(o.topLeft.x);
            r.setY(o.topLeft.y);
            obstacleRects.add(r);
        }

        ArrayList<Arc> lightArcs = new ArrayList<>();
        ArrayList<Shape> allies = new ArrayList<>();
        ArrayList<Shape> enemies = new ArrayList<>();

        double arcAngle = (GameSettings.Security.lightArcPercentage / 100)
                * 360;

        for (Map.Entry<String, Player> entry : main.gameData.players
                .entrySet()) {

            Player player = entry.getValue();
            Circle c = new Circle(player.position.x, player.position.y,
                    GameSettings.Player.radius);

            if (player.faction == Faction.SECURITY) {
                Arc light = new Arc(player.position.x, player.position.y,
                        GameSettings.Security.lightRadius,
                        GameSettings.Security.lightRadius,
                        -Math.toDegrees(player.direction) - arcAngle / 2,
                        arcAngle);

                light.setType(ArcType.ROUND);
                lightArcs.add(light);
            }

            if (main.player.faction == player.faction) {
                c.setFill(Color.GREEN);
                allies.add(c);
            } else {
                c.setFill(Color.RED);
                enemies.add(c);
            }
        }

        // Calculate occlusion
        ArrayList<Line> obstacleEdges = new ArrayList<>();
        for (Rectangle r : obstacleRects) {
            double leftX = r.getX();
            double rightX = r.getX() + r.getWidth();
            double topY = r.getY();
            double bottomY = r.getY() + r.getHeight();
            obstacleEdges.add(new Line(leftX, topY, leftX, bottomY));
            obstacleEdges.add(new Line(leftX, bottomY, rightX, bottomY));
            obstacleEdges.add(new Line(leftX, topY, rightX, topY));
            obstacleEdges.add(new Line(rightX, topY, rightX, bottomY));
        }

        ArrayList<OccludedLight> occludedLightShapes = new ArrayList<>();
        for (Arc light : lightArcs) {

            OccludedLight occludedLight = new OccludedLight(light,
                    light.getCenterX(), light.getCenterY());

            for (Line edge : obstacleEdges) {

                Polygon lightOcclusion = calcOcclusion(light.getCenterX(),
                        light.getCenterY(), edge);

                occludedLight.setShape(Shape.subtract(occludedLight.getShape(),
                        lightOcclusion));
            }

            RadialGradient lightGrad = new RadialGradient(0, 0.1,
                    light.getCenterX(), light.getCenterY(),
                    GameSettings.Security.lightRadius, false,
                    CycleMethod.NO_CYCLE,
                    new Stop[] { new Stop(0, Colors.flashlight),
                            new Stop(1, Color.TRANSPARENT) });

            occludedLight.getShape().setFill(lightGrad);
            occludedLightShapes.add(occludedLight);
        }

        ArrayList<Shape> occludedObstacleShapes = new ArrayList<>();
        for (OccludedLight light : occludedLightShapes) {
            for (Rectangle r : obstacleRects) {

                Rectangle outline = new Rectangle(r.getWidth() + 4,
                        r.getHeight() + 4);
                outline.setX(r.getX() - 2);
                outline.setY(r.getY() - 2);

                Shape occludedOutline = Shape.subtract(outline, r);
                occludedOutline = Shape.intersect(occludedOutline,
                        light.getShape());

                RadialGradient outlineGrad = new RadialGradient(0, 0.1,
                        light.getCenterX(), light.getCenterY(),
                        GameSettings.Security.lightRadius, false,
                        CycleMethod.NO_CYCLE,
                        new Stop[] { new Stop(0, Color.WHITE),
                                new Stop(1, Color.TRANSPARENT) });

                occludedOutline.setFill(outlineGrad);
                occludedObstacleShapes.add(occludedOutline);
            }
        }

        Shape outerArena = new Rectangle(0, 0, 840, 530);
        Rectangle innerArena = new Rectangle(20, 20, 800, 450);
        outerArena.setFill(Colors.outerArena);
        innerArena.setFill(Color.BLACK);

        // Draw
        List<Node> ch = pane.getChildren();
        ch.clear();
        ch.add(outerArena);
        ch.add(innerArena);
        ch.addAll(occludedObstacleShapes);

        for (OccludedLight light : occludedLightShapes) {
            ch.add(light.getShape());
        }

        ch.addAll(allies);
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

        return new Polygon(edge.getStartX(), edge.getStartY(), edge.getEndX(),
                edge.getEndY(), x3, y3, x4, y4);
    }
}
