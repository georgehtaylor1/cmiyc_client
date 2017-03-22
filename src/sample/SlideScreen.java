package sample;

import java.io.IOException;

import gui.GameDrawer;
import gui.OffsetHolder;
import javafx.animation.TranslateTransition;
import javafx.scene.Scene;
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
import javafx.stage.Stage;
import javafx.util.Duration;
import launcher.Main;
import logic.GameLogic;

/**
 * Created by Gerta on 24/02/2017.
 */

public class SlideScreen extends AnchorPane {

    private AnchorPane slider;
    private ToolBar toolBar;
    private BorderPane together;
    private Pane pane;

    private TextField username;
    private TextField host;
    private Button connect;
    private Button mainButton;

    private VBox vBox1;
    private VBox vBox2;
    private HBox hBox1;
    private HBox hBox2;

    private ToggleButton toggleButton2vs3;
    private ToggleButton toggleButton1vs2;
    private ToggleGroup group;

    private ToggleButton security;
    private ToggleButton thief;
    private ToggleGroup group2;

    private GameLogic gameLogic;
    private GameDrawer gameDrawer;

    private Main launcherMain;
    private GameScreen gameScreen;
    private TranslateTransition sliderTranslation;


    /**
     * Create a new instance of the SlideScreen
     * @param gameScreen
     * @throws IOException
     */
    public SlideScreen(GameScreen gameScreen) throws IOException {
        this.slider = new AnchorPane();
        this.together = new BorderPane();
        this.mainButton = new Button("Find Game");
        this.toolBar = new ToolBar(mainButton);
        this.username = new TextField();
        this.host = new TextField();
        this.connect = new Button("Connect");
        this.vBox1 = new VBox();
        this.vBox2 = new VBox();
        this.hBox1 = new HBox();
        this.hBox2 = new HBox();

        //toggleButton for number of players
        this.toggleButton1vs2 = new ToggleButton("1vs2");
        this.toggleButton2vs3 = new ToggleButton("2vs3");
        this.group = new ToggleGroup();

        this.security = new ToggleButton("Security");
        this.thief = new ToggleButton("Thief");
        this.group2 = new ToggleGroup();
        
        OffsetHolder offsetHolder = new OffsetHolder();

        this.launcherMain = new Main();
        this.pane = new Pane();
        this.gameLogic = new GameLogic(launcherMain, pane, offsetHolder);
        this.gameDrawer = new GameDrawer(launcherMain, pane, offsetHolder);
        this.gameScreen = gameScreen;
        this.drawScene();
    }


    /**
     * Draw the scene
     */
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

    /**
     * Make the 1vs2 toggleButton selected
     */
    public void oneVsTwoSetActive() {
        toggleButton1vs2.setSelected(true);
    }

    /**
     * Make the 2vs3 toggleButton selected
     */
    public void twoVsThreeSetActive() {
        toggleButton2vs3.setSelected(true);
    }

    /**
     * Make the security toggleButton selected
     */
    public void securitySetActive () {
        security.setSelected(true);
    }


    /**
     * Make the thief toggleButton selected
     */
    public void thiefSetActive () {
        thief.setSelected(true);
    }


    /**
     * Show the slider
     */
    public void slideIn(){
        sliderTranslation.setRate(-1);
        sliderTranslation.play();
        this.setPrefWidth(slider.getPrefWidth());
    }

    /**
     * Hide the slider
     */
    public void slideOut() {
        sliderTranslation.setRate(1);
        sliderTranslation.play();
    }

    /**
     * Get the vBox1 from the slider
     * @return vBox1 from the slider
     */
    public VBox getvBox1() {
        return vBox1;
    }

    /**
     * Get the vBox2 from the slider
     * @return vBox2 from the slider
     */
    public VBox getvBox2() {
        return vBox2;
    }

    /**
     * Get the hBox1  with the toggleButtons from the slider
     * @return hBox1  with the toggleButtons from the slider
     */
    public HBox gethBox1() {
        return hBox1;
    }

    /**
     * Get the hBox2 with the toggleButtons from the slider
     * @return hBox2 with the toggleButtons from the slider
     */
    public HBox gethBox2() {
        return hBox2;
    }

    /**
     * Get the slider
     * @return the slider which is an AnchorPane
     */
    public AnchorPane getSlider() {
        return slider;
    }

    /**
     * Get the toolBar
     * @return the toolBar which contains the mainButton
     */
    public ToolBar getToolBar() {
        return toolBar;
    }

    /**
     * Get the borderPane together
     * @return together which contains vBox1 and vBox2
     */
    public BorderPane getTogether() {
        return together;
    }

    /**
     * Get the mainButton
     * @return the mainButton
     */
    public Button getMainButton() {
        return mainButton;
    }

    /**
     * Get the toggleButton 2vs3
     * @return 2vs3
     */
    public ToggleButton getToggleButton2vs3() {
        return toggleButton2vs3;
    }

    /**
     * Get the toggleButton 1vs2
     * @return 2vs3
     */
    public ToggleButton getToggleButton1vs2() {
        return toggleButton1vs2;
    }

    /**
     * Get the toggleButton security
     * @return security
     */
    public ToggleButton getSecurity() {
        return security;
    }

    /**
     * Get the toggleButton thief
     * @return thief
     */
    public ToggleButton getThief() {
        return thief;
    }

    /**
     * Get the sliderTranslation
     * @return sliderTranslation which makes the movement of the slider
     */
    public TranslateTransition getSliderTranslation() {
        return sliderTranslation;
    }



    public enum State {
        START,
        FIND,
        STOP_FIND,
        ENTER,
        LEAVE
    }

    /**
     * Set the state of the mainButton
     * @param state
     */
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



