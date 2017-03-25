package logic;

import java.util.HashMap;
import java.util.Map;

import audio.AudioPlayer;
import audio.AudioWav;
import constants.Commands.Action;
import constants.Commands.Key;
import game.Camera;
import game.Faction;
import game.GameMode;
import game.Obstacle;
import game.Player;
import game.Treasure;
import game.constants.GameSettings;
import game.states.GameState;
import game.states.PlayerState;
import game.states.TreasureState;
import game.util.Position;
import gui.DrawValues;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import states.ClientState;
import util.Client;
import util.Debug;
import util.Maths;
import util.Transferable;

/**
 * The main logic of the game.
 */
public class GameLogic {

	private HashMap<KeyCode, Boolean> keys = new HashMap<KeyCode, Boolean>();

	private Client client;
	private Pane pane;
	private Faction faction; // Client's faction
	private double mouseX;
	private double mouseY;

	private AudioPlayer footsteps;
	private boolean playSound;

	private Rectangle fullMap;
	private Shape walkableArea;
	private Rectangle chargingArea;

	private boolean gameEnd;

	public GameLogic(Client client, Pane pane, AudioPlayer footsteps) {
		this.footsteps = footsteps;
		this.client = client;
		this.pane = pane;
		this.faction = client.player.faction;
		this.gameEnd = false;
		// Adds listeners
		pane.requestFocus();
		pane.setOnKeyPressed(e -> {
			playSound = true;
			keys.put(e.getCode(), true);
		});
		pane.setOnKeyReleased(e -> {
			playSound = false;
			keys.put(e.getCode(), false);
		});
		pane.setOnMouseMoved(e -> {
			mouseX = e.getSceneX();
			mouseY = e.getSceneY();
		});
	}

	/**
	 * Updates the game periodically
	 */
	public void update() {

		DrawValues vals = new DrawValues(this.pane);

		if(!footsteps.isRunning() && playSound){
			playSound = true;
			footsteps.play(false);
		}
			
		if(!playSound)
			footsteps.stop();
		
		if (this.gameEnd) {
			return;
		}

		this.fullMap = new Rectangle(20, 20, 800, 490); // Must change this to

		// inner arena size
		this.walkableArea = this.fullMap;
		this.chargingArea = new Rectangle(20, 20, 50, 50); // FIXME: This is
															// still hardcoded.
															// The security's
															// charging area

		// Makes the walkable area
		for (Obstacle o : client.gameData.obstacles) {
			Rectangle object = new Rectangle(o.topLeft.x, o.topLeft.y, o.width, o.height);
			Shape s = Rectangle.subtract(walkableArea, object);
			walkableArea = s;
		}

		// First we find the angle from the mouse to the player
		double angle = Maths.angle(client.player.position.x * vals.scaleFactor + vals.xOffset,
				client.player.position.y * vals.scaleFactor + vals.yOffset, mouseX, mouseY);
		client.player.direction = Maths.normalizeAngle(angle); // Updates
																// client's
																// direction
																// (currently in radians)

		// Movement
		if (keys.containsKey(KeyCode.W) && keys.get(KeyCode.W) /* && client.player.state == PlayerState.NORMAL */) {
			if (Math.pow(client.player.position.x * vals.scaleFactor + vals.xOffset - mouseX, 2)
					+ Math.pow(client.player.position.y * vals.scaleFactor + vals.yOffset - mouseY, 2) <= Math
							.pow(GameSettings.Player.radius * vals.scaleFactor, 2)) {
				return; // This prevents spinning about mouse
			}

			double tempX = client.player.position.x, tempY = client.player.position.y;
			tempX += client.player.speed * Math.cos(angle);
			tempY += client.player.speed * Math.sin(angle);

			if (walkableArea.contains(tempX, client.player.position.y))
				client.player.position.x = tempX;

			if (walkableArea.contains(client.player.position.x, tempY))
				client.player.position.y = tempY;
		}
		if (keys.containsKey(KeyCode.S) && keys.get(KeyCode.S) && client.player.state == PlayerState.NORMAL) {
			double tempX = client.player.position.x, tempY = client.player.position.y;
			tempX -= client.player.speed * Math.cos(angle);
			tempY -= client.player.speed * Math.sin(angle);
			if (walkableArea.contains(tempX, client.player.position.y))
				client.player.position.x = tempX;

			if (walkableArea.contains(client.player.position.x, tempY))
				client.player.position.y = tempY;
		}
		if (keys.containsKey(KeyCode.A) && keys.get(KeyCode.A) && client.player.state == PlayerState.NORMAL) {
			double tempX = client.player.position.x, tempY = client.player.position.y;
			tempX += client.player.speed * Math.cos(angle - Math.PI / 2);
			tempY += client.player.speed * Math.sin(angle - Math.PI / 2);
			if (walkableArea.contains(tempX, client.player.position.y))
				client.player.position.x = tempX;

			if (walkableArea.contains(client.player.position.x, tempY))
				client.player.position.y = tempY;
		}
		if (keys.containsKey(KeyCode.D) && keys.get(KeyCode.D) && client.player.state == PlayerState.NORMAL) {
			double tempX = client.player.position.x, tempY = client.player.position.y;
			tempX += client.player.speed * Math.cos(angle + Math.PI / 2);
			tempY += client.player.speed * Math.sin(angle + Math.PI / 2);
			if (walkableArea.contains(tempX, client.player.position.y))
				client.player.position.x = tempX;

			if (walkableArea.contains(client.player.position.x, tempY))
				client.player.position.y = tempY;
		}

		if (faction == Faction.THIEF) { // Thief functions

			if (keys.containsKey(KeyCode.SPACE) && keys.get(KeyCode.SPACE)
					&& client.player.state == PlayerState.NORMAL) {
				// Action button to collect
				// treasures (FOR THIEVES)
				if (GameSettings.Arena.exit.at(client.player.position, 20)) {
					client.player.state = PlayerState.ESCAPED;
					HashMap<Key, Object> map = new HashMap<Key, Object>();
					map.put(Key.PLAYER_STATE, client.player.state);
					client.send(new Transferable(Action.UPDATE_CLIENT_STATE, map));
				} else {
					double px = client.player.position.x;
					double py = client.player.position.y;

					Treasure tempT = null; // Saves a treasures to be collected
					for (Treasure t : client.gameData.treasures) {
						if (t.state == TreasureState.UNPICKED) {
							double tx = t.position.x;
							double ty = t.position.y;

							if (Math.pow(px - tx, 2) + Math.pow(py - ty, 2) <= Math.pow(GameSettings.Thief.stealRadius,
									2)) { // Treasure
																																	// is in
																																		// catch
																																		// range.
								tempT = t; // This is the treasure to delete
								break;
							}
						}
					}
					collectTreasure(tempT);
				}
			}
		} else { // Security functions

			if (keys.containsKey(KeyCode.SPACE) && keys.get(KeyCode.SPACE)) {
				double px = client.player.position.x;
				double py = client.player.position.y;
				String nameToBeRemoved = null;
				Player thiefToBeRemoved = null;
				for (Map.Entry<String, Player> p : client.gameData.players.entrySet()) {
					Player tempP = p.getValue();

					if (tempP.faction == Faction.SECURITY) {
						continue;
					}

					double tx = tempP.position.x;
					double ty = tempP.position.y;
					if (Math.pow(px - tx, 2) + Math.pow(py - ty, 2) <= Math.pow(GameSettings.Security.catchRadius, 2)) {
						nameToBeRemoved = p.getKey();
						thiefToBeRemoved = tempP;
						break;
					}
				}
				captureThief(nameToBeRemoved, thiefToBeRemoved);
			}

			// Deploy camera
			if (keys.containsKey(KeyCode.C) && keys.get(KeyCode.C)) {
				deployCamera(client.player.position, angle);
			}

			// If on charging area, then increase battery, else decrease it
			double current = client.player.battery;
			if (chargingArea.contains(client.player.position.x, client.player.position.y)) {
				if (current < GameSettings.Security.fullBattery) {
					client.player.battery += GameSettings.Security.chargeValue;
				}
			} else {
				if (current > GameSettings.Security.noBattery)
					client.player.battery -= GameSettings.Security.drainValue;
			}

			client.gameData.secScore += GameSettings.Score.scorePerSecond / 60.0;
		}

		// winning condition TODO
		int oog = 0;
		for (Map.Entry<String, Player> e : client.gameData.players.entrySet()) {
			Player p = e.getValue();
			if (p.faction == Faction.THIEF && (p.state != PlayerState.CAUGHT && p.state != PlayerState.ESCAPED)) {
				break;
			} else if (p.faction == Faction.THIEF)
				oog++;
		}
		if ((this.client.gameData.mode == GameMode.SHORT && oog == 1)
				|| (this.client.gameData.mode == GameMode.LONG && oog == 2)) {
			Debug.say("Security: " + Math.round(client.gameData.secScore));
			Debug.say("Thief: " + Math.round(client.gameData.thiefScore));

			if (client.gameData.secScore > client.gameData.thiefScore) {
				Debug.say("Security wins!");
			} else if (client.gameData.secScore < client.gameData.thiefScore) {
				Debug.say("Thief wins!");
			} else {
				Debug.say("It's a draw!");
			}
			this.gameEnd = true;
			client.gameData.state = GameState.POSTGAME;
			client.obData.setState(ClientState.POSTGAME);
		}

	}

	/**
	 * Method to collect treasure for thief if in range
	 *
	 * @param t
	 *            to be removed
	 */
	public void collectTreasure(Treasure treasure) {
		if (treasure != null) {
			treasure.state = TreasureState.PICKED;
			client.player.treasureScore += GameSettings.Score.treasureScore;
			client.gameData.thiefScore += GameSettings.Score.treasureScore;
			// client.gameData.treasures.remove(treasure); TODO: DO WE NEED
			// THIS?
			HashMap<Key, Object> map = new HashMap<Key, Object>();
			map.put(Key.TREASURE_ID, treasure.id);
			map.put(Key.TREASURE_STATE, TreasureState.PICKED);
			client.send(new Transferable(Action.UPDATE_TREASURE_STATE, map));
			map = new HashMap<Key, Object>();
			map.put(Key.SCORE, client.gameData.thiefScore);
			client.send(new Transferable(Action.UPDATE_THIEF_SCORE, map));
			Debug.say("Collected! Score: " + client.player.treasureScore);
		}
	}

	/**
	 * Method to capture aka remove thief from gamedata
	 *
	 * @param name
	 *            of the thief
	 * @param thief
	 *            player to be removed
	 */
	public void captureThief(String name, Player thief) {
		if (thief != null && thief.state != PlayerState.ESCAPED) {
			thief.state = PlayerState.CAUGHT;
			// client.gameData.secScore +=
			// GameSettings.Score.thiefCaptureScore;//TODO: Is this necessary?
			client.gameData.thiefScore -= thief.treasureScore;
			client.gameData.players.remove(name);
			// Network
			HashMap<Key, Object> map = new HashMap<Key, Object>();
			map.put(Key.CLIENT_ID, thief.clientID);
			map.put(Key.PLAYER_STATE, PlayerState.CAUGHT);
			client.send(new Transferable(Action.UPDATE_PLAYER_STATE, map));
			map = new HashMap<Key, Object>();
			map.put(Key.SCORE, client.gameData.thiefScore);
			client.send(new Transferable(Action.UPDATE_THIEF_SCORE, map));
			Debug.say("Thief " + thief.clientID + " captured. Score " + Math.round(client.gameData.secScore));
		}
	}

	/**
	 * Method to deploy camera
	 *
	 * @param p
	 *            position to put camera
	 * @param angle
	 *            in radians of the direction
	 */
	public void deployCamera(Position p, double angle) {
		if (client.player.cameras > 0) {
			Camera deployed = new Camera(p.x, p.y, -angle, GameSettings.Security.lightRadius);
			client.gameData.cameras.add(deployed);
			client.player.cameras--;
			Debug.say("deployed camera. Left " + client.player.cameras + " cameras");

			HashMap<Key, Object> map = new HashMap<Key, Object>();
			map.put(Key.CAMERA, deployed);
			client.send(new Transferable(Action.DEPLOY_CAMERA, map));

			try {
				Thread.sleep(200);
			} catch (Exception e) {
				e.getMessage();
			}
		}
	}
}
