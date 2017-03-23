package sample;

import java.io.IOException;

import game.Faction;
import game.GameMode;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Created by Gerta on 24/02/2017.
 */

public class SlideScreen extends AnchorPane {

    private AnchorPane slider;
    private ToolBar toolBar;
    private BorderPane together;
    private TextField username;
    private TextField host;
    private Button connect;
    private Button disconnect;
    private ToggleButton ready;
    private Button mainButton;
    private Text text;
    private Text text2;
    private VBox connection;
    private VBox settings;

    private ToggleButton toggleButton2vs3;
    private ToggleButton toggleButton1vs2;
    private ToggleGroup group;

    private ToggleButton security;
    private ToggleButton thief;
    private ToggleGroup group2;
    private GameScreen gameScreen;
    
    private boolean gameRendering;

    private TranslateTransition sliderTranslation;

    public SlideScreen(GameScreen gameScreen) throws IOException {
        this.slider = new AnchorPane();
        this.together = new BorderPane();
        this.mainButton = new Button("Start");
        this.toolBar = new ToolBar(mainButton);
        this.username = new TextField();
        this.host = new TextField();
        this.connect = new Button("Connect");
        this.disconnect = new Button("Disconnect");
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
        this.drawScene();
    }


    public void drawScene() {
        slider.getStylesheets().add("styles/slider.css");
        this.getStylesheets().add("styles/sliderLayer.css");

        toolBar.setPrefHeight(40);
        this.setPrefWidth(Constants.ScreenWidth);
        this.setPrefHeight(Constants.ScreenHeight);
        this.getChildren().addAll(slider, toolBar);

        slider.setPrefWidth(250);
        slider.setPrefHeight(Constants.ScreenHeight);
        username.setPromptText("username");
        host.setPromptText("host");


        connection.getChildren().addAll(username, host, connect);
        connection.setDisable(false);
        HBox hBox1 = new HBox();
        hBox1.getChildren().addAll(toggleButton1vs2, toggleButton2vs3);
        HBox hBox2 = new HBox();
        hBox2.getChildren().addAll(security, thief);
        settings.getChildren().addAll(hBox1, hBox2);
        together.setTop(connection);
        together.setBottom(settings);
        slider.getChildren().addAll(together);


        AnchorPane.setBottomAnchor(toolBar, 0.0);
        AnchorPane.setRightAnchor(toolBar, 0.0);
        AnchorPane.setLeftAnchor(toolBar, 0.0);
        AnchorPane.setTopAnchor(slider, 0.0);
        AnchorPane.setRightAnchor(slider, 0.0);
        AnchorPane.setBottomAnchor(slider, toolBar.getPrefHeight());

        AnchorPane.setTopAnchor(together, 0.0);
        AnchorPane.setBottomAnchor(together, 0.0);
        AnchorPane.setLeftAnchor(together, 0.0);
        AnchorPane.setRightAnchor(together, 0.0);

        toolBar.setPrefWidth(Constants.ScreenWidth);
        slider.setId("slider");
        this.setId("sliderLayer");
        toolBar.setId("toolbar");
        mainButton.setId("mainButton");
        username.setId("username");
        host.setId("host");
        connect.setId("connect");
        disconnect.setId("connect");
        ready.setId("connect");
        toggleButton1vs2.setId("1vs2");
        toggleButton2vs3.setId("2vs3");
        security.setId("security");
        thief.setId("thief");

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

        this.mainButton.setOnAction(e -> {
            if(this.sliderTranslation.getRate() == 1) {
                slideIn();
            }
        });

        this.setOnMouseClicked(event -> {
            if(this.sliderTranslation.getRate() == -1) {
                slideOut();
            }
        });

    }

    public void slideIn(){
        sliderTranslation.setRate(-1);
        sliderTranslation.play();
        this.setPrefWidth(slider.getPrefWidth());
    }

    public void slideOut() {
        sliderTranslation.setRate(1);
        sliderTranslation.play();
    }

    public enum State {
        START,
        FIND,
        LOBBY,
        INGAME
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
               text.setText("Players found: 0");
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
}



