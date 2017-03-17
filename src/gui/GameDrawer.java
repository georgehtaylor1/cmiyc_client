package gui;

import constants.Colors;

import java.util.ArrayList;

import game.Obstacle;
import game.Treasure;
import game.constants.GameSettings;
import game.states.PlayerState;
import gui.util.FxUtils;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
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

        // Make flashlight shapes
        ArrayList<Shape> flashlightShapes = new ArrayList<>();
        
        // Client player
        Arc clientFlashlightArc;
        // Check that player has not run out of battery
        if (main.player.state != PlayerState.STUCK) {	
	        clientFlashlightArc = new Arc(main.player.position.x,
	                main.player.position.y, GameSettings.Security.lightRadius,
	                GameSettings.Security.lightRadius,
	                -Math.toDegrees(main.player.direction)
	                        - GameSettings.Security.lightRadius / 2,
	                GameSettings.Security.lightArcPercentage * 360 / 100);
        }
        else {
        	clientFlashlightArc = new Arc(main.player.position.x,
	                main.player.position.y, GameSettings.Security.lightRadius/2,
	                GameSettings.Security.lightRadius/2,
	                -Math.toDegrees(main.player.direction)
	                        - GameSettings.Security.lightRadius / 2,
	                GameSettings.Security.lightArcPercentage * 360 / 100);
        }

        clientFlashlightArc.setType(ArcType.ROUND);
        clientFlashlightArc.setFill(Color.YELLOW);

        flashlightShapes.add(clientFlashlightArc);

        // Make obstacle shapes
        ArrayList<Shape> obstacleShapes = new ArrayList<>();
        for (Obstacle o : main.gameData.obstacles) {
            Rectangle r = new Rectangle(o.width, o.height, Color.LIGHTBLUE);
            r.setX(o.topLeft.x);
            r.setY(o.topLeft.y);
            obstacleShapes.add(r);
        }
        
        Rectangle secHome = new Rectangle(GameSettings.Arena.secHomeSize.getWidth(),GameSettings.Arena.secHomeSize.getHeight(), Color.LIGHTGREEN);
        secHome.setX(0);
        secHome.setY(0);

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

        TextField text = new TextField(String.valueOf(main.player.battery));
        
        // Draw
        pane.getChildren().add(secHome);
        pane.getChildren().add(text);
        pane.getChildren().addAll(obstacleShapes);
        pane.getChildren().addAll(treasureShapes);
    	pane.getChildren().addAll(flashlightShapes);
        pane.getChildren().add(clientPlayerShape);
    }
}
