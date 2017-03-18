package sample;

import game.constants.GameSettings;
import gui.GameDrawer;
import javafx.animation.TranslateTransition;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.GameLogic;
import launcher.Main;

import java.io.IOException;

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
    private Main launcherMain;
    private GameScreen gameScreen;


    private VBox vBox1;
    private VBox vBox2;
    private HBox hBox1;
    private HBox hBox2;


    private TranslateTransition sliderTranslation;

    public AnchorPane getSlider() {
        return slider;
    }

    public ToolBar getToolBar() {
        return toolBar;
    }

    public BorderPane getTogether() {
        return together;
    }

    public Button getConnect() {
        return connect;
    }

    public Button getMainButton() {
        return mainButton;
    }

    public ToggleButton getToggleButton2vs3() {
        return toggleButton2vs3;
    }

    public ToggleButton getToggleButton1vs2() {
        return toggleButton1vs2;
    }


    public ToggleButton getSecurity() {
        return security;
    }

    public ToggleButton getThief() {
        return thief;
    }


    public HBox gethBox1() {
        return hBox1;
    }

    public HBox gethBox2() {
        return hBox2;
    }

    public VBox getvBox1() {
        return vBox1;
    }

    public VBox getvBox2() {
        return vBox2;
    }


    public TranslateTransition getSliderTranslation() {
        return sliderTranslation;
    }

    public SlideScreen(GameScreen gameScreen, Stage stage) throws IOException {
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

        this.launcherMain = new Main();
        this.pane = new Pane();
        this.gameLogic = new GameLogic(launcherMain, pane);
        this.gameDrawer = new GameDrawer(launcherMain, pane, stage);
        this.gameScreen = gameScreen;
        this.drawScene();
    }


    public void drawScene() {
        slider.getStylesheets().add("styles/slider.css");
        this.getStylesheets().add("styles/sliderLayer.css");

        toolBar.setPrefHeight(40);
        this.setPrefWidth(GameSettings.Arena.size.getWidth());
        this.setPrefHeight(GameSettings.Arena.size.getHeight());
        this.getChildren().addAll(slider, toolBar);

        slider.setPrefWidth(250);
        slider.setPrefHeight(GameSettings.Arena.size.getHeight());
        username.setPromptText("username");
        host.setPromptText("host");

        vBox1 = new VBox();
        vBox1.getChildren().addAll(username, host, connect);
        vBox1.setDisable(false);
        vBox2 = new VBox();
        hBox1 = new HBox();
        hBox1.getChildren().addAll(toggleButton1vs2, toggleButton2vs3);
        hBox2 = new HBox();
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

        toolBar.setPrefWidth(GameSettings.Arena.size.getWidth());
        slider.setId("slider");
        this.setId("sliderLayer");
        toolBar.setId("toolbar");
        mainButton.setId("mainButton");
        username.setId("username");
        host.setId("host");
        connect.setId("connect");
        toggleButton1vs2.setId("1vs2");
        toggleButton2vs3.setId("2vs3");
        security.setId("security");
        thief.setId("thief");
        vBox2.setId("vBox2");


        username.setEditable(true);


        slider.setTranslateX(0);
        sliderTranslation = new TranslateTransition(Duration.millis(400), slider);

        sliderTranslation.setFromX(0);
        sliderTranslation.setToX(this.getPrefWidth() - slider.getPrefWidth());
        sliderTranslation.setRate(1);
        sliderTranslation.play();


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

        toggleButton1vs2.setToggleGroup(group);
        toggleButton2vs3.setToggleGroup(group);
        security.setToggleGroup(group2);
        thief.setToggleGroup(group2);
        toggleButton1vs2.setSelected(true);
        security.setSelected(true);

    }

    public void oneVsTwoSetActive() {
        toggleButton1vs2.setSelected(true);
    }

    public void twoVsThreeSetActive() {
        toggleButton2vs3.setSelected(true);
    }

    public void securitySetActive () {
        security.setSelected(true);
    }

    public void thiefSetActive () {
        thief.setSelected(true);
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



