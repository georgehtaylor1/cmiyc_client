package sample;

import java.io.IOException;

import javafx.animation.TranslateTransition;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.util.Duration;
import launcher.Main;
import sun.launcher.resources.launcher;

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

    private final ToggleButton toggleButton2vs3;
    private final ToggleButton toggleButton1vs2;
    private final ToggleGroup group;

    private final ToggleButton security;
    private final ToggleButton thief;
    private final ToggleGroup group2;
    
    private Main main;

    private TranslateTransition sliderTranslation;

    public SlideScreen(Main main) throws IOException {
    	this.main = main;
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
        connect.setOnAction(e -> {
        	String[] splits = host.getText().split(":");
        	String user = username.getText();
        	String[] ret = new String[3];
        	ret[0] = splits[0];
        	ret[1] = splits[1];
        	ret[2] = user;
        	main.useInputs(ret);
        });

        toggleButton1vs2.setToggleGroup(group);
        toggleButton1vs2.setSelected(true);
        toggleButton2vs3.setToggleGroup(group);

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



