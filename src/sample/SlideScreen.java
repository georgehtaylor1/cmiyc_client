package sample;

import java.io.IOException;

import gui.GameDrawer;
import gui.OffsetHolder;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import logic.GameLogic;
import util.Client;

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
    private Button mainButton;

    private ToggleButton toggleButton2vs3;
    private ToggleButton toggleButton1vs2;
    private ToggleGroup group;

    private ToggleButton security;
    private ToggleButton thief;
    private ToggleGroup group2;
    private Pane pane;
    private GameLogic gameLogic;
    private GameDrawer gameDrawer;
    private Client launcherMain;
    private GameScreen gameScreen;

    private TranslateTransition sliderTranslation;

    public SlideScreen(GameScreen gameScreen) throws IOException {
        this.slider = new AnchorPane();
        this.together = new BorderPane();
        this.mainButton = new Button("Find Game");
        this.toolBar = new ToolBar(mainButton);
        this.username = new TextField();
        this.host = new TextField();
        this.connect = new Button("Connect");

        //toggleButton for number of players
        this.toggleButton1vs2 = new ToggleButton("1vs2");
        this.toggleButton2vs3 = new ToggleButton("2vs3");
        this.group = new ToggleGroup();

        this.security = new ToggleButton("Security");
        this.thief = new ToggleButton("Thief");
        this.group2 = new ToggleGroup();
        
        OffsetHolder offsetHolder = new OffsetHolder();

        this.launcherMain = new Client();
        this.pane = new Pane();
        this.gameLogic = new GameLogic(launcherMain, pane, offsetHolder);
        this.gameDrawer = new GameDrawer(launcherMain, pane, offsetHolder);
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


        VBox vBox1 = new VBox();
        vBox1.getChildren().addAll(username, host, connect);
        vBox1.setDisable(false);
        VBox vBox2 = new VBox();
        HBox hBox1 = new HBox();
        hBox1.getChildren().addAll(toggleButton1vs2, toggleButton2vs3);
        HBox hBox2 = new HBox();
        hBox2.getChildren().addAll(security, thief);
        vBox2.getChildren().addAll(hBox1, hBox2);
        together.setTop(vBox1);
        together.setBottom(vBox2);
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
       // cancel.setId("cancel");
        toggleButton1vs2.setId("1vs2");
        toggleButton2vs3.setId("2vs3");
        security.setId("security");
        thief.setId("thief");


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

        /*this.cancel.setOnAction(e -> {
            sliderTranslation.setRate(1);
            sliderTranslation.play();
        });*/

        connect.setOnAction(e -> {
            gameScreen.drawGame();
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

    public void SetActive() {
        toggleButton1vs2.setToggleGroup(group);
        toggleButton1vs2.setSelected(true);
        toggleButton2vs3.setToggleGroup(group);
    }

    public void securitySetActive() {
        toggleButton1vs2.setToggleGroup(group);
        toggleButton1vs2.setSelected(true);
        toggleButton2vs3.setToggleGroup(group);
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
        STOP_FIND,
        ENTER,
        LEAVE
    }

    public void setState(State state) {
       switch (state) {
           case START:
               this.mainButton.setText("Start");
               break;
           case FIND:
               this.mainButton.setText("Find");
               break;
           case STOP_FIND:
                this.mainButton.setText("Stop Find");
               break;
           case ENTER:
               this.mainButton.setText("Enter");
               break;
           case LEAVE:
               this.mainButton.setText("Leave");
               break;
       }
    }
}



