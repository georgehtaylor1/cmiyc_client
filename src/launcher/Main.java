package launcher;

import game.Faction;
import game.Obstacle;
import game.Player;
import game.Treasure;
import game.constants.GameSettings;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import sample.GameOverScreen;
import sample.GameScreen;
import sample.SlideScreen;
import sample.SlideScreenData;
import sample.WelcomeScreen;
import util.Client;

public class Main extends Application {

	public Client client;
	private SlideScreenData obData;

	public Main() {
		this.obData = new SlideScreenData();
		this.client = new Client("u-n-owen", obData);

	}

	public static void main(String _arguments[]) {
		//new Main();
		launch(_arguments);
	}

	public void start(Stage primaryStage) throws Exception {
		this.obData = new SlideScreenData();
		this.client = new Client("u-n-owen", obData);
		StackPane base = new StackPane();
		Scene scene = new Scene(base);

		WelcomeScreen welcomeScreen = new WelcomeScreen();

		primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> {
			welcomeScreen.setAnchor(newValue.doubleValue());
		});

		/// TODO: Remove later
		this.client.gameData.treasures.add(new Treasure(450, 450));
		this.client.gameData.obstacles.add(new Obstacle(400, 340, 120, 80));

		this.client.gameData.obstacles.add(new Obstacle(0, 0, 20, (int) GameSettings.Arena.size.getHeight() + 60));
		this.client.gameData.obstacles.add(
				new Obstacle(20, (int) GameSettings.Arena.size.getHeight() + 40, GameSettings.Arena.size.width, 20));
		this.client.gameData.obstacles.add(new Obstacle(GameSettings.Arena.size.width + 20, 0, 20,
				(int) GameSettings.Arena.size.getHeight() + 60));
		this.client.gameData.obstacles.add(new Obstacle(20, 0, GameSettings.Arena.size.width, 20));

		/// 

		GameOverScreen gameover = new GameOverScreen(this.client);
		GameScreen gameScreen = new GameScreen(this, base);
		SlideScreen slideScreen = new SlideScreen(base, gameScreen, welcomeScreen, gameover);
		obData.addObserver(slideScreen);

		welcomeScreen.requestFocus();
		//this.client.player.faction = Faction.THIEF;
		this.client.gameData.players.put(this.client.player.clientID, this.client.player);
		//base.getChildren().clear();
		base.getChildren().addAll(gameScreen, welcomeScreen, slideScreen);

		primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		primaryStage.setFullScreen(true);
		primaryStage.setScene(scene);
		primaryStage.show();

		slideScreen.setPickOnBounds(false);
	}

}
