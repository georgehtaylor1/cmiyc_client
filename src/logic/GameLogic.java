package logic;

import java.util.HashMap;

import constants.Commands.Action;
import constants.Commands.Key;
import game.Faction;
import game.Obstacle;
import game.Player;
import game.Treasure;
import game.constants.GameSettings;
import game.states.PlayerState;
import game.states.TreasureState;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import launcher.Main;

import util.Maths;
import util.Transferable;

/**
 * The main logic of the game.
 */
public class GameLogic {

    private HashMap<KeyCode, Boolean> keys = new HashMap<KeyCode, Boolean>();

    private Main client;
    private Faction faction; // Client's faction
    private double mouseX;
    private double mouseY;

    private Rectangle fullMap;
    private Shape walkableArea;

    public GameLogic(Main client, Pane pane) {
        this.client = client;
        this.faction = client.player.faction;
        this.fullMap = new Rectangle(0, 0, 800, 450); // Must change this to
                                                      // inner arena size
        this.walkableArea = this.fullMap;

        // Makes the walkable area
        for (Obstacle o : client.gameData.obstacles) {
            Rectangle object = new Rectangle(o.topLeft.x, o.topLeft.y, o.width,
                    o.height);
            Shape s = Rectangle.subtract(walkableArea, object);
            walkableArea = s;
        }

        // Adds listeners
        pane.setFocusTraversable(true);
        pane.setOnKeyPressed(e -> {
            keys.put(e.getCode(), true);
        });
        pane.setOnKeyReleased(e -> {
            keys.put(e.getCode(), false);
        });
        pane.setOnMouseMoved(e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });
    }

    public void update() {
        // First we find the angle from the mouse to the player
        double angle = Maths.angle(client.player.position.x,
                client.player.position.y, mouseX, mouseY);
        client.player.direction = angle; // Updates client's direction
                                         // (currently in radians)

        if (keys.containsKey(KeyCode.W) && keys.get(KeyCode.W)) {
            double tempX = client.player.position.x,
                    tempY = client.player.position.y;
            tempX += client.player.speed * Math.cos(angle);
            tempY += client.player.speed * Math.sin(angle);
            if (walkableArea.contains(tempX, tempY)) { // Only update movement
                                                       // when the area is still
                                                       // walkable
                client.player.position.x = tempX;
                client.player.position.y = tempY;
                String drag = client.player.dragging;
                if (drag != null) {
                	client.gameData.players.get(drag).position.x = tempX;
                	client.gameData.players.get(drag).position.y = tempY;
                }
            }
        }
        if (keys.containsKey(KeyCode.S) && keys.get(KeyCode.S)) {
            double tempX = client.player.position.x,
                    tempY = client.player.position.y;
            tempX -= client.player.speed * Math.cos(angle);
            tempY -= client.player.speed * Math.sin(angle);
            if (walkableArea.contains(tempX, tempY)) { // Only update movement
                                                       // when the area is still
                                                       // walkable
                client.player.position.x = tempX;
                client.player.position.y = tempY;
                String drag = client.player.dragging;
                if (drag != null) {
                	client.gameData.players.get(drag).position.x = tempX;
                	client.gameData.players.get(drag).position.y = tempY;
                }
            }
        }
        if (keys.containsKey(KeyCode.A) && keys.get(KeyCode.A)) {
            double tempX = client.player.position.x,
                    tempY = client.player.position.y;
            tempX += client.player.speed * Math.cos(angle - Math.PI / 2);
            tempY += client.player.speed * Math.sin(angle - Math.PI / 2);
            if (walkableArea.contains(tempX, tempY)) { // Only update movement
                                                       // when the area is still
                                                       // walkable
                client.player.position.x = tempX;
                client.player.position.y = tempY;
                String drag = client.player.dragging;
                if (drag != null) {
                	client.gameData.players.get(drag).position.x = tempX;
                	client.gameData.players.get(drag).position.y = tempY;
                }
            }
        }
        if (keys.containsKey(KeyCode.D) && keys.get(KeyCode.D)) {
            double tempX = client.player.position.x,
                    tempY = client.player.position.y;
            tempX += client.player.speed * Math.cos(angle + Math.PI / 2);
            tempY += client.player.speed * Math.sin(angle + Math.PI / 2);
            if (walkableArea.contains(tempX, tempY)) { // Only update movement
                                                       // when the area is still
                                                       // walkable
                client.player.position.x = tempX;
                client.player.position.y = tempY;
                String drag = client.player.dragging;
                if (drag != null) {
                	client.gameData.players.get(drag).position.x = tempX;
                	client.gameData.players.get(drag).position.y = tempY;
                }
            }
        }
        if (Faction.THIEF == faction && keys.containsKey(KeyCode.SPACE)
                && keys.get(KeyCode.SPACE)) { // Action button to collect
                                              // treasures (FOR THIEVES)
            Treasure tempT = null; // Saves a treasures to be collected
            for (Treasure t : client.gameData.treasures) {
                double tx = t.position.x;
                double ty = t.position.y;
                double px = client.player.position.x;
                double py = client.player.position.y;
                if (Math.pow(px - tx, 2) + Math.pow(py - ty, 2) < Math
                        .pow(GameSettings.Thief.stealRadius, 2)) { // Treasure
                                                                   // is in
                                                                   // catch
                                                                   // range.
                    tempT = t; // This is the treasure to delete
                }
            }
            if (tempT != null) { // We can't remove the treasure in the for loop
                                 // because it will cause
                                 // concurrentModificationException.
                System.out.println("Score! Add: " + tempT.value);
                client.gameData.treasures.remove(tempT); // So we remove it here
                HashMap<Key, Object> map = new HashMap<Key, Object>();
                map.put(Key.TREASURE_ID, tempT.id);
                map.put(Key.TREASURE_STATE, TreasureState.PICKED);
                client.send(new Transferable(Action.UPDATE_TREASURE_STATE, new HashMap<Key, Object>()));
            }

            try { // Adds a little delay so villains won't spam action button.
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (Faction.SECURITY == faction && keys.containsKey(KeyCode.SPACE)
                && keys.get(KeyCode.SPACE)) { // Action button to drag
                                              // guards (FOR SECURITY)
        	if (client.player.dragging == null) {
	            String id = null; // initialisation
	            for (String k : client.gameData.players.keySet()) {
	            	Player p = client.gameData.players.get(k);
	                double tx = p.position.x;
	                double ty = p.position.y;
	                double px = client.player.position.x;
	                double py = client.player.position.y;
	                if (Math.pow(px - tx, 2) + Math.pow(py - ty, 2) < Math
	                        .pow(GameSettings.Security.dragRadius, 2)) { // Player
	                                                                   // is in
	                                                                   // drag
	                                                                   // range.
	                	if (client.gameData.players.get(k).state == PlayerState.STUCK) { // Check if player is out of battery
	                		id = p.clientID;
	                	}
	                }
	            }
	            if (id != null) {
	                System.out.println("Dragging Player");
	                client.player.dragging = id;
	                HashMap<Key, Object> map = new HashMap<Key, Object>();
	                map.put(Key.DRAGGED_PLAYER, id);
	                client.send(new Transferable(Action.START_DRAG, new HashMap<Key, Object>()));
	            }
	
	            try { // Adds a little delay so guards won't spam action button.
	                Thread.sleep(100);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
        	}
        	else {
        		System.out.println("Stopped dragging");
        		client.player.dragging = null;
                client.send(new Transferable(Action.STOP_DRAG));
        	}
        }

        // TODO Catch thieves for security
        // TODO Catch thieves for camera
    }
}
