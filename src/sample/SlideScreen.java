package sample;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import ai.handler.Handler;
import game.Faction;
import game.GameMode;
import game.constants.GameSettings;
import gui.GraphicsSettings;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import states.ClientState;

/**
 * Created by Gerta on 24/02/2017.
 */

public class SlideScreen extends AnchorPane implements Observer {

	private StackPane base;
	private AnchorPane slider;
	private HBox sliderControls;
	private BorderPane together;
	private TextField username;
	private TextField host;
	private Button connect;
	private Button disconnect;
	private Button exit;
	private ToggleButton ready;
	private Button mainButton;
	private Button singlePlayer;
	private Text text;
	private Text text2;
	private VBox connection;
	private VBox settings;

	private State state;

	private ToggleButton toggleButton2vs3;
	private ToggleButton toggleButton1vs2;
	private ToggleGroup group;

	private ToggleButton security;
	private ToggleButton thief;
	private ToggleGroup group2;
	private GameScreen gameScreen;
	private WelcomeScreen welcomeScreen;

	private boolean gameRendering;

	private TranslateTransition sliderTranslation;

	public SlideScreen(StackPane base, GameScreen gameScreen, WelcomeScreen welcomeScreen) throws IOException {
		this.base = base;
		this.slider = new AnchorPane();
		this.together = new BorderPane();
		this.mainButton = new Button("Start");
		this.sliderControls = new HBox();
		this.exit = new Button("Exit");
		this.username = new TextField();
		this.host = new TextField();
		this.connect = new Button("Connect");
		this.disconnect = new Button("Disconnect");
		this.singlePlayer = new Button("Single Player");
		this.ready = new ToggleButton("Ready");
		this.text = new Text();
		text.setId("fancytext");
		this.text2 = new Text();
		text2.setId("fancytext");
		this.connection = new VBox();
		this.settings = new VBox();
		this.gameRendering = false;

		//toggleButton for number of players
		this.toggleButton1vs2 = new ToggleButton("1vs2");
		this.toggleButton2vs3 = new ToggleButton("2vs3");
		this.group = new ToggleGroup();

		this.security = new ToggleButton("Security");
		this.thief = new ToggleButton("Thief");
		this.group2 = new ToggleGroup();

		this.gameScreen = gameScreen;
		this.welcomeScreen = welcomeScreen;
		this.drawScene();
		this.state = State.START;
	}

	/**
	 * Draw the slider screen
	 */
	public void drawScene() {
		slider.getStylesheets().add("styles/slider.css");
		this.getStylesheets().add("styles/sliderLayer.css");

		sliderControls.setPrefHeight(40);
		this.setPrefWidth(GameSettings.Arena.outerSize.getWidth());
		this.setPrefHeight(GameSettings.Arena.outerSize.getHeight());
		this.getChildren().addAll(slider, sliderControls);
		sliderControls.getChildren().addAll(exit, mainButton);
		sliderControls.setSpacing(1080);

		slider.setPrefWidth(250);
		slider.setPrefHeight(GameSettings.Arena.outerSize.getHeight());
		username.setPromptText("username");
		host.setPromptText("host");

		connection.getChildren().addAll(username, host, connect, singlePlayer);
		connection.setDisable(false);
		HBox hBox1 = new HBox();
		hBox1.getChildren().addAll(toggleButton1vs2, toggleButton2vs3);
		HBox hBox2 = new HBox();
		hBox2.getChildren().addAll(security, thief);
		settings.getChildren().addAll(hBox1, hBox2);
		together.setTop(connection);
		together.setBottom(settings);
		slider.getChildren().addAll(together);
		settings.setSpacing(3.0);
		connection.setSpacing(3.0);

		AnchorPane.setBottomAnchor(sliderControls, 0.0);
		AnchorPane.setRightAnchor(sliderControls, 0.0);
		AnchorPane.setLeftAnchor(sliderControls, 0.0);
		AnchorPane.setTopAnchor(slider, 0.0);
		AnchorPane.setRightAnchor(slider, 0.0);
		AnchorPane.setBottomAnchor(slider, sliderControls.getPrefHeight());

		AnchorPane.setTopAnchor(together, 0.0);
		AnchorPane.setBottomAnchor(together, 0.0);
		AnchorPane.setLeftAnchor(together, 0.0);
		AnchorPane.setRightAnchor(together, 0.0);

		sliderControls.setPrefWidth(GameSettings.Arena.outerSize.getWidth());
		slider.setId("slider");
		this.setId("sliderLayer");
		sliderControls.setId("sliderControls");
		mainButton.setId("mainButton");
		exit.setId("exit");
		username.setId("username");
		host.setId("host");
		connect.setId("connect");
		disconnect.setId("connect");
		ready.setId("connect");
		singlePlayer.setId("connect");
		toggleButton1vs2.setId("leftToggle");
		toggleButton2vs3.setId("rightToggle");
		security.setId("leftToggle");
		thief.setId("rightToggle");

		toggleButton2vs3.setToggleGroup(group);
		toggleButton1vs2.setToggleGroup(group);
		toggleButton1vs2.setSelected(true);
		security.setToggleGroup(group2);
		security.setSelected(true);
		thief.setToggleGroup(group2);

		username.setEditable(true);

		slider.setTranslateX(0);
		sliderTranslation = new TranslateTransition(Duration.millis(400), slider);

		sliderTranslation.setFromX(0);
		sliderTranslation.setToX(this.getPrefWidth() - slider.getPrefWidth());
		sliderTranslation.setRate(1);
		sliderTranslation.play();

		connect.setOnAction(e -> {
			if (this.sliderTranslation.getRate() == -1)
				slideOut();
			String[] _data = host.getText().split(":");
			String _ip = _data[0];
			int _port = Integer.parseInt(_data[1]);
			String _name = username.getText();
			gameScreen.client.connect(_port, _ip, _name);
			if (!gameRendering) {
				gameScreen.drawGame();
				gameRendering = true;
			}
			setState(State.FIND);
		});

		disconnect.setOnAction(e -> {
			if (this.sliderTranslation.getRate() == -1)
				slideOut();
			gameScreen.client.disconnect();
			setState(State.START);
		});

		security.setOnAction(e -> {
			gameScreen.client.player.faction = Faction.SECURITY;
		});

		thief.setOnAction(e -> {
			gameScreen.client.player.faction = Faction.THIEF;
		});

		toggleButton1vs2.setOnAction(e -> {
			gameScreen.client.player.mode = GameMode.SHORT;
		});

		toggleButton2vs3.setOnAction(e -> {
			gameScreen.client.player.mode = GameMode.LONG;
		});

		singlePlayer.setOnAction(e -> {
			if (this.sliderTranslation.getRate() == -1)
				slideOut();
			gameScreen.aiHandler.addPlayers(1, 1);
			gameScreen.aiHandler.start();
			gameScreen.drawGame();
			this.base.getChildren().clear();
			this.base.getChildren().addAll(this.gameScreen, this);
		});

		this.mainButton.setOnAction(e -> {
			if (this.sliderTranslation.getRate() == 1) {
				slideIn();
			}
		});

		this.exit.setOnAction(e -> {
			gameScreen.aiHandler.end();
			System.exit(0);
		});

		this.setOnMouseClicked(event -> {
			if (this.sliderTranslation.getRate() == -1)
				slideOut();
		});

	}

	public void slideIn() {
		sliderTranslation.setRate(-1);
		sliderTranslation.play();
		this.setPrefWidth(slider.getPrefWidth());
	}

	public void slideOut() {
		sliderTranslation.setRate(1);
		sliderTranslation.play();
	}

	public enum State {
		START, FIND, LOBBY, INGAME
	}

	public void setState(State state) {
		switch (state) {
		case START:
			slider.getChildren().clear();
			together.getChildren().clear();
			together.setTop(connection);
			together.setBottom(settings);
			slider.getChildren().addAll(together);
			mainButton.setText("Start");
			base.getChildren().clear();
			base.getChildren().addAll(this.gameScreen, this.welcomeScreen, this);
			break;
		case FIND:
			slider.getChildren().clear();
			text.setText("Players found: 0");
			VBox vbox1 = new VBox();
			vbox1.getChildren().add(text);
			vbox1.getChildren().add(disconnect);
			together.getChildren().clear();
			together.setTop(vbox1);
			slider.getChildren().add(together);
			mainButton.setText("Finding...");
			break;
		case LOBBY:
			mainButton.setText("Menu");
			slider.getChildren().clear();
			text.setText("Players found: " + this.gameScreen.client.obData.getPlayers());
			slider.getChildren().add(text);
			slider.getChildren().add(ready);
			break;
		case INGAME:
			mainButton.setText("Menu");
			slider.getChildren().clear();
			text.setText("Thieves Captured: 0");
			text2.setText("Thieves Escaped: 0");
			slider.getChildren().add(text);
			slider.getChildren().add(text2);
			slider.getChildren().add(disconnect);
			break;
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		if (this.state == State.FIND) {
			if (this.gameScreen.client.obData.getState() == ClientState.PLAYING)
				this.setState(State.INGAME);
			else
				this.setState(State.LOBBY);
		} else if (this.state == State.INGAME) {
			if (this.gameScreen.client.obData.getState() == ClientState.POSTGAME)
				this.state = State.START;
		}
	}
}
