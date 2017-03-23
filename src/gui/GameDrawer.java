package gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import constants.Colors;

import game.Camera;
import game.Faction;
import game.Obstacle;
import game.Player;
import game.Treasure;
import game.constants.GameSettings;
import game.states.TreasureState;

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
import javafx.scene.text.Text;

import util.Client;

/**
 * Draws the state of the game to a Pane.
 */
public class GameDrawer {

    private Client main;
    private Pane pane;

    /**
     * Constructs a new GameDrawer. Default graphics settings are set on the
     * Pane.
     */
    public GameDrawer(Client main, Pane pane) {
        this.pane = pane;
        this.main = main;
        pane.setStyle(
                "-fx-background-color: " + FxUtils.toRGBCode(Color.GREY) + ";");
    }

    /**
     * Draws the current state of the game to the Pane.
     */
    public void draw() {

        DrawValues vals = new DrawValues(this.pane);

        // Make obstacle shapes
        ArrayList<Rectangle> obstacleRects = new ArrayList<>();
        for (Obstacle o : main.gameData.obstacles) {
            Rectangle r = new Rectangle(o.width * vals.scaleFactor,
                    o.height * vals.scaleFactor);
            r.setX(o.topLeft.x * vals.scaleFactor + vals.xOffset);
            r.setY(o.topLeft.y * vals.scaleFactor + vals.yOffset);
            obstacleRects.add(r);
        }

        // Make treasure shapes
        ArrayList<TreasureShape> treasureShapes = new ArrayList<>();
        for (Treasure t : main.gameData.treasures) {
            treasureShapes.add(new TreasureShape(t, vals.scaleFactor,
                    vals.xOffset, vals.yOffset));
        }

        final double arcAngle = (GameSettings.Security.lightArcPercentage
                / 100.0) * 360;
        final double cameraBoxLength = 10 * vals.scaleFactor;

        // Make camera shapes
        ArrayList<Rectangle> cameraShapes = new ArrayList<>();
        ArrayList<CenteredShape> cameraVisionShapes = new ArrayList<>();
        for (Camera c : main.gameData.cameras) {

            Rectangle box = new Rectangle(cameraBoxLength, cameraBoxLength);
            box.setX((c.position.x * vals.scaleFactor - cameraBoxLength / 2.0)
                    + vals.xOffset);
            box.setY((c.position.y * vals.scaleFactor - cameraBoxLength / 2.0)
                    + vals.yOffset);
            box.setRotate(-Math.toDegrees(c.direction));

            box.setStroke(Color.WHITE);
            box.setStrokeWidth(2 * vals.scaleFactor);
            cameraShapes.add(box);

            Arc cameraLight = new Arc(
                    c.position.x * vals.scaleFactor + vals.xOffset,
                    c.position.y * vals.scaleFactor + vals.yOffset,
                    GameSettings.Security.lightRadius * vals.scaleFactor,
                    GameSettings.Security.lightRadius * vals.scaleFactor,
                    Math.toDegrees(c.direction) - arcAngle / 2.0, arcAngle);
            cameraLight.setType(ArcType.ROUND);

            cameraVisionShapes.add(new CenteredShape(cameraLight));
        }

        // Make player shapes
        ArrayList<CenteredShape> securityLightShapes = new ArrayList<>();
        ArrayList<CenteredShape> thiefVisionShapes = new ArrayList<>();
        ArrayList<Circle> allyShapes = new ArrayList<>();
        ArrayList<Circle> enemyShapes = new ArrayList<>();

        for (Map.Entry<String, Player> entry : main.gameData.players
                .entrySet()) {

            Player player = entry.getValue();
            Circle c = new Circle(
                    player.position.x * vals.scaleFactor + vals.xOffset,
                    player.position.y * vals.scaleFactor + vals.yOffset,
                    GameSettings.Player.radius * vals.scaleFactor);

            // Vision
            if (player.faction == Faction.SECURITY) {
                // Security
                Arc light = new Arc(
                        player.position.x * vals.scaleFactor + vals.xOffset,
                        player.position.y * vals.scaleFactor + vals.yOffset,
                        GameSettings.Security.lightRadius * vals.scaleFactor,
                        GameSettings.Security.lightRadius * vals.scaleFactor,
                        -Math.toDegrees(player.direction) - arcAngle / 2.0,
                        arcAngle);
                light.setType(ArcType.ROUND);
                securityLightShapes.add(new CenteredShape(light));
            } else {
                // Thief
                Circle vision = new Circle(
                        GameSettings.Thief.visionRadius * vals.scaleFactor);
                vision.setCenterX(
                        player.position.x * vals.scaleFactor + vals.xOffset);
                vision.setCenterY(
                        player.position.y * vals.scaleFactor + vals.yOffset);
                thiefVisionShapes.add(new CenteredShape(vision));
            }

            if (player.clientID.equals(main.player.clientID)) {
                // Active player
                c.setStroke(Color.WHITE);
                c.setStrokeWidth(1.5 * vals.scaleFactor);
            }

            if (main.player.faction == player.faction) {
                c.setFill(Color.GREEN);
                allyShapes.add(c);
            } else {
                enemyShapes.add(c);
            }
        }

        final double outlineWidth = 2 * vals.scaleFactor;

        // Collect all edges
        ArrayList<Line> obstacleEdges = new ArrayList<>();
        for (Rectangle r : obstacleRects) {
            double leftX = r.getX() + outlineWidth;
            double rightX = r.getX() + r.getWidth() - outlineWidth;
            double topY = r.getY() + outlineWidth;
            double bottomY = r.getY() + r.getHeight() - outlineWidth;
            obstacleEdges.add(new Line(leftX, topY, leftX, bottomY));
            obstacleEdges.add(new Line(leftX, bottomY, rightX, bottomY));
            obstacleEdges.add(new Line(leftX, topY, rightX, topY));
            obstacleEdges.add(new Line(rightX, topY, rightX, bottomY));
        }

        // Occlude security lights
        ArrayList<CenteredShape> occSecurityLightShapes = new ArrayList<>();
        for (CenteredShape light : securityLightShapes) {

            CenteredShape occLight = new CenteredShape(light.shape,
                    light.getCenterX(), light.getCenterY());

            for (Line edge : obstacleEdges) {
                Polygon occlusion = calcOcclusion(light.getCenterX(),
                        light.getCenterY(), edge);
                occLight.shape = Shape.subtract(occLight.shape, occlusion);
            }

            RadialGradient lightGrad = makeRadialGradient(light.getCenterX(),
                    light.getCenterY(),
                    GameSettings.Security.lightRadius * vals.scaleFactor,
                    Colors.flashlight, Color.TRANSPARENT);

            occLight.shape.setFill(lightGrad);
            occSecurityLightShapes.add(occLight);
        }

        // Occlude camera vision
        ArrayList<CenteredShape> occCameraVisionShapes = new ArrayList<>();
        for (CenteredShape vision : cameraVisionShapes) {

            CenteredShape occVision = new CenteredShape(vision.shape,
                    vision.getCenterX(), vision.getCenterY());

            for (Line edge : obstacleEdges) {
                Polygon occlusion = calcOcclusion(vision.getCenterX(),
                        vision.getCenterY(), edge);
                occVision.shape = Shape.subtract(occVision.shape, occlusion);
            }

            RadialGradient visionGrad = makeRadialGradient(vision.getCenterX(),
                    vision.getCenterY(),
                    GameSettings.Security.lightRadius * vals.scaleFactor,
                    Colors.cameraVision, Color.TRANSPARENT);

            occVision.shape.setFill(visionGrad);
            occCameraVisionShapes.add(occVision);
        }

        // Occlude thief vision
        ArrayList<CenteredShape> occThiefVisionShapes = new ArrayList<>();
        for (CenteredShape vision : thiefVisionShapes) {

            CenteredShape occVision = new CenteredShape(vision.shape,
                    vision.getCenterX(), vision.getCenterY());

            for (Line edge : obstacleEdges) {
                Polygon occlusion = calcOcclusion(vision.getCenterX(),
                        vision.getCenterY(), edge);
                occVision.shape = Shape.subtract(occVision.shape, occlusion);
            }

            RadialGradient visionGrad = makeRadialGradient(vision.getCenterX(),
                    vision.getCenterY(),
                    GameSettings.Thief.visionRadius * vals.scaleFactor,
                    Colors.thiefVision, Color.TRANSPARENT);

            occVision.shape.setFill(visionGrad);
            occThiefVisionShapes.add(occVision);
        }

        // Occlude hidden shapes
        ArrayList<Shape> occObstacleShapes = new ArrayList<>();
        ArrayList<Shape> occTreasureShapes = new ArrayList<>();
        ArrayList<Shape> occShadowTreasureShapes = new ArrayList<>();
        ArrayList<Shape> occEnemyShapes = new ArrayList<>();
        ArrayList<Shape> occHiddenSecurityLightShapes = new ArrayList<>();

        if (main.player.faction == Faction.SECURITY) {
            // Security

            for (CenteredShape light : occSecurityLightShapes) {

                // Obstacles
                for (Rectangle r : obstacleRects) {

                    Rectangle inside = new Rectangle(r.getX() + outlineWidth,
                            r.getY() + outlineWidth,
                            r.getWidth() - outlineWidth * 2,
                            r.getHeight() - outlineWidth * 2);

                    Shape occObstacle = Shape.subtract(r, inside);
                    occObstacle = Shape.intersect(occObstacle, light.shape);

                    RadialGradient obstacleGrad = makeRadialGradient(
                            light.getCenterX(), light.getCenterY(),
                            GameSettings.Security.lightRadius
                                    * vals.scaleFactor,
                            Color.WHITE, Color.TRANSPARENT);

                    occObstacle.setFill(obstacleGrad);
                    occObstacleShapes.add(occObstacle);
                }

                // Treasures
                for (TreasureShape t : treasureShapes) {
                    if (t.treasure.state == TreasureState.UNPICKED) {
                        Shape occTreasure = Shape.intersect(t.circle,
                                light.shape);

                        RadialGradient treasureGrad = makeRadialGradient(
                                light.getCenterX(), light.getCenterY(),
                                GameSettings.Security.lightRadius
                                        * vals.scaleFactor,
                                Colors.treasure, Color.TRANSPARENT);

                        occTreasure.setFill(treasureGrad);
                        occTreasureShapes.add(occTreasure);
                    }
                }

                // Thieves
                for (Circle c : enemyShapes) {
                    Shape occEnemy = Shape.intersect(c, light.shape);

                    RadialGradient enemyGrad = makeRadialGradient(
                            light.getCenterX(), light.getCenterY(),
                            GameSettings.Security.lightRadius
                                    * vals.scaleFactor,
                            Color.RED, Color.TRANSPARENT);

                    occEnemy.setFill(enemyGrad);
                    occEnemyShapes.add(occEnemy);
                }
            }

            // Treasure shadows
            for (TreasureShape t : treasureShapes) {
                if (t.treasure.state != TreasureState.PICKED_AND_SEEN) {

                    Shape occShadowTreasure = t.circle;

                    for (CenteredShape light : occSecurityLightShapes) {
                        occShadowTreasure = Shape.subtract(occShadowTreasure,
                                light.shape);
                    }
                    for (CenteredShape vision : occCameraVisionShapes) {
                        occShadowTreasure = Shape.subtract(occShadowTreasure,
                                vision.shape);
                    }

                    occShadowTreasure.setFill(Colors.treasureShadow);
                    occShadowTreasureShapes.add(occShadowTreasure);
                }
            }
        } else {
            // Thief

            for (CenteredShape vision : occThiefVisionShapes) {

                // Security lights
                for (CenteredShape light : occSecurityLightShapes) {
                    Shape occLight = Shape.intersect(light.shape, vision.shape);

                    RadialGradient lightGrad = makeRadialGradient(
                            light.getCenterX(), light.getCenterY(),
                            GameSettings.Security.lightRadius
                                    * vals.scaleFactor,
                            Colors.flashlight, Color.TRANSPARENT);

                    occLight.setFill(lightGrad);
                    occHiddenSecurityLightShapes.add(occLight);
                }

                // Obstacles
                for (Rectangle r : obstacleRects) {

                    Rectangle inside = new Rectangle(r.getX() + outlineWidth,
                            r.getY() + outlineWidth,
                            r.getWidth() - outlineWidth * 2,
                            r.getHeight() - outlineWidth * 2);

                    Shape occObstacle = Shape.subtract(r, inside);
                    occObstacle = Shape.intersect(occObstacle, vision.shape);

                    RadialGradient obstacleGrad = makeRadialGradient(
                            vision.getCenterX(), vision.getCenterY(),
                            GameSettings.Thief.visionRadius * vals.scaleFactor,
                            Color.WHITE, Color.TRANSPARENT);

                    occObstacle.setFill(obstacleGrad);
                    occObstacleShapes.add(occObstacle);
                }

                // Treasures
                for (TreasureShape t : treasureShapes) {
                    if (t.treasure.state == TreasureState.UNPICKED) {
                        Shape occTreasure = Shape.intersect(t.circle,
                                vision.shape);

                        RadialGradient treasureGrad = makeRadialGradient(
                                vision.getCenterX(), vision.getCenterY(),
                                GameSettings.Thief.visionRadius,
                                Colors.treasure, Color.TRANSPARENT);

                        occTreasure.setFill(treasureGrad);
                        occTreasureShapes.add(occTreasure);
                    }
                }

                // Security
                for (Circle c : enemyShapes) {
                    Shape occEnemy = Shape.intersect(c, vision.shape);

                    RadialGradient enemyGrad = makeRadialGradient(
                            vision.getCenterX(), vision.getCenterY(),
                            GameSettings.Security.lightRadius
                                    * vals.scaleFactor,
                            Color.RED, Color.TRANSPARENT);

                    occEnemy.setFill(enemyGrad);
                    occEnemyShapes.add(occEnemy);
                }
            }
        }

        Rectangle outerArena = new Rectangle(0, 0, pane.getWidth(),
                pane.getHeight());
        outerArena.setFill(Colors.outerArena);

        final double arenaBufferLength = 20 * vals.scaleFactor;

        Rectangle innerArena = new Rectangle(arenaBufferLength + vals.xOffset,
                arenaBufferLength + vals.yOffset,
                vals.width - arenaBufferLength * 2,
                vals.height - arenaBufferLength * 2);
        innerArena.setFill(Colors.fog);

        // Draw
        List<Node> ch = pane.getChildren();
        ch.clear();
        ch.add(outerArena);
        ch.add(innerArena);
        ch.addAll(occObstacleShapes);
        ch.addAll(occTreasureShapes);

        if (main.player.faction == Faction.SECURITY) {
            // Security
            ch.addAll(occShadowTreasureShapes);
        }

        ch.addAll(occEnemyShapes);

        if (main.player.faction == Faction.SECURITY) {
            // Security
            for (CenteredShape s : occSecurityLightShapes) {
                ch.add(s.shape);
            }
            for (CenteredShape s : occCameraVisionShapes) {
                ch.add(s.shape);
            }
            ch.addAll(cameraShapes);
        } else {
            // Thief
            for (Shape s : occHiddenSecurityLightShapes) {
                ch.add(s);
            }
            for (CenteredShape s : occThiefVisionShapes) {
                ch.add(s.shape);
            }
        }

        ch.addAll(allyShapes);

        Text battery = new Text(
                "Battery : " + String.valueOf(main.player.battery));
        battery.setId("fancytext");
        battery.setX(0.0);
        battery.setY(0.0);
        pane.getChildren().add(battery);
    }

    /**
     * Helper method for constructing a RadialGradient.
     */
    private static RadialGradient makeRadialGradient(double centerX,
            double centerY, double radius, Color start, Color end) {

        return new RadialGradient(0, 0, centerX, centerY, radius, false,
                CycleMethod.NO_CYCLE, new Stop(0, start), new Stop(1, end));
    }

    /**
     * Calculate the occlusion caused by the given edge.
     */
    private Polygon calcOcclusion(double originX, double originY, Line edge) {
        final double OCCLUSION_LENGTH = pane.getWidth() * 1000;

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
