package sample;

import javafx.scene.control.ToolBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;

import static java.awt.SystemColor.text;

/**
 * Created by Gerta on 04/03/2017.
 */
public class WelcomeScreen extends AnchorPane{

    private AnchorPane welcomeScreen;
    private ToolBar welcomeControls;

    private Arc arc1;
    private Arc arc2;
    private Arc arc3;
    private Arc arc4;
    private Arc arc5;
    private Arc arc6;
    private Arc arc7;

    private Ellipse ellipse2;
    private Ellipse ellipse1;
    private Ellipse ellipse3;
    private Ellipse ellipse4;
    private Ellipse ellipse4b;
    private Ellipse ellipse5;
    private Ellipse ellipse7;

    private Text text;


    public WelcomeScreen() throws IOException {

        this.welcomeScreen = new AnchorPane();
        this.welcomeControls = new ToolBar();
        this.drawScene();

    }

    public void drawScene() {

        this.getStylesheets().add("styles/welcomeLayer.css");

        welcomeControls.setPrefHeight(40);
        this.setPrefWidth(Constants.ScreenWidth);
        this.setPrefHeight(Constants.ScreenHeight);
        this.getChildren().addAll(welcomeScreen, welcomeControls);

        welcomeScreen.setPrefWidth(this.getPrefWidth());
        welcomeScreen.setPrefHeight(350);

        AnchorPane.setBottomAnchor(welcomeControls, 0.0);
        AnchorPane.setRightAnchor(welcomeControls, 0.0);
        AnchorPane.setLeftAnchor(welcomeControls, 0.0);
        AnchorPane.setTopAnchor(welcomeScreen,  50.0);
        AnchorPane.setLeftAnchor(welcomeScreen, 0.0);
        AnchorPane.setRightAnchor(welcomeScreen, 0.0);

        arc1 = new Arc();
        arc1.setFill(Paint.valueOf("#606060"));
        arc1.setRadiusX(40.0f);
        arc1.setRadiusY(52.0f);
        arc1.setStartAngle(-25.0f);
        arc1.setLength(50.0f);
        arc1.setType(ArcType.ROUND);

        ellipse1 = new Ellipse();
        ellipse1.setFill(Paint.valueOf("#3CB371"));
        ellipse1.setRadiusX(8.0f);
        ellipse1.setRadiusY(8.0f);

        arc2 = new Arc();
        arc2.setFill(Paint.valueOf("#606060"));
        arc2.setCenterX(140.0f);
        arc2.setCenterY(45.0f);
        arc2.setRadiusX(40.0f);
        arc2.setRadiusY(40.0f);
        arc2.setStartAngle(-115.0f);
        arc2.setLength(50.0f);
        arc2.setType(ArcType.ROUND);

        ellipse2 = new Ellipse();
        ellipse2.setFill(Paint.valueOf("#3CB371"));
        ellipse2.setCenterX(140.0f);
        ellipse2.setCenterY(53.0f);
        ellipse2.setRadiusX(8.0f);
        ellipse2.setRadiusY(8.0f);


        arc3 = new Arc();
        arc3.setFill(Paint.valueOf("#606060"));
        arc3.setCenterX(165.0f);
        arc3.setCenterY(45.0f);
        arc3.setRadiusX(40.0f);
        arc3.setRadiusY(40.0f);
        arc3.setStartAngle(-115.0f);
        arc3.setLength(50.0f);
        arc3.setType(ArcType.ROUND);

        ellipse3 = new Ellipse();
        ellipse3.setFill(Paint.valueOf("#3CB371"));
        ellipse3.setCenterX(165.0f);
        ellipse3.setCenterY(53.0f);
        ellipse3.setRadiusX(8.0f);
        ellipse3.setRadiusY(8.0f);

        arc4 = new Arc();
        arc4.setFill(Paint.valueOf("#606060"));
        arc4.setCenterX(210.0f);
        arc4.setCenterY(78.0f);
        arc4.setRadiusX(25.0f);
        arc4.setRadiusY(32.0f);
        arc4.setStartAngle(65.0f);
        arc4.setLength(50.0f);
        arc4.setType(ArcType.ROUND);

        ellipse4 = new Ellipse();
        ellipse4.setFill(Paint.valueOf("#3CB371"));
        ellipse4.setCenterX(210.0f);
        ellipse4.setCenterY(78.0f);
        ellipse4.setRadiusX(8.0f);
        ellipse4.setRadiusY(8.0f);

        ellipse4b = new Ellipse();
        ellipse4b.setFill(Paint.valueOf("#FF0000"));
        ellipse4b.setCenterX(210.0f);
        ellipse4b.setCenterY(30.0f);
        ellipse4b.setRadiusX(8.0f);
        ellipse4b.setRadiusY(8.0f);

        arc5 = new Arc();
        arc5.setFill(Paint.valueOf("#606060"));
        arc5.setCenterX(270.0f);
        arc5.setCenterY(85.0f);
        arc5.setRadiusX(50.0f);
        arc5.setRadiusY(40.0f);
        arc5.setStartAngle(62.0f);
        arc5.setLength(60.0f);
        arc5.setType(ArcType.ROUND);

        ellipse5 = new Ellipse();
        ellipse5.setFill(Paint.valueOf("#3CB371"));
        ellipse5.setCenterX(270.0f);
        ellipse5.setCenterY(80.0f);
        ellipse5.setRadiusX(8.0f);
        ellipse5.setRadiusY(8.0f);

        arc6 = new Arc();
        arc6.setFill(Paint.valueOf("#606060"));
        arc6.setCenterX(272.0f);
        arc6.setCenterY(80.0f);
        arc6.setRadiusX(35.0f);
        arc6.setRadiusY(20.0f);
        arc6.setStartAngle(190.0f);
        arc6.setLength(40.0f);
        arc6.setType(ArcType.ROUND);

        arc7 = new Arc();
        arc7.setFill(Paint.valueOf("#606060"));
        arc7.setRadiusX(40.0f);
        arc7.setRadiusY(52.0f);
        arc7.setStartAngle(-25.0f);
        arc7.setLength(50.0f);
        arc7.setType(ArcType.ROUND);

        ellipse7 = new Ellipse();
        ellipse7.setFill(Paint.valueOf("#3CB371"));
        ellipse7.setCenterX(315.0f);
        ellipse7.setCenterY(65.0f);
        ellipse7.setRadiusX(8.0f);
        ellipse7.setRadiusY(8.0f);

        text = new Text();
        text.setText("Catch Me If You Can");
        text.setFont(Font.font ("Verdana", 22));
        text.setFill(Color.valueOf("#606060"));


        welcomeScreen.getChildren().addAll(arc1, ellipse1, arc2, ellipse2, arc3, ellipse3, arc4, ellipse4, ellipse4b, arc5, arc6, ellipse5, arc7, ellipse7, text);
        setAnchor(welcomeScreen.getPrefWidth());


        this.setId("welcomeLayer");
        welcomeScreen.setId("welcomeScreen");
        welcomeControls.setId("welcomeControls");


    }

    public void setAnchor(double size){
        AnchorPane.setTopAnchor(arc1, 65.0);
        AnchorPane.setLeftAnchor(arc1, size/2-145);
        AnchorPane.setTopAnchor(ellipse1, 78.0);
        AnchorPane.setLeftAnchor(ellipse1, size/2-145);

        AnchorPane.setTopAnchor(arc2, 65.0);
        AnchorPane.setLeftAnchor(arc2, size/2 - 85);
        AnchorPane.setTopAnchor(ellipse2, 63.0);
        AnchorPane.setLeftAnchor(ellipse2, size/2-76);

        AnchorPane.setTopAnchor(arc3, 65.0);
        AnchorPane.setLeftAnchor(arc3, size/2 - 62);
        AnchorPane.setTopAnchor(ellipse3, 63.0);
        AnchorPane.setLeftAnchor(ellipse3, size/2-53);

        AnchorPane.setTopAnchor(arc4, 65.0);
        AnchorPane.setLeftAnchor(arc4, size/2-7);
        AnchorPane.setTopAnchor(ellipse4, 85.0);
        AnchorPane.setLeftAnchor(ellipse4, size/2 -5);
        AnchorPane.setTopAnchor(ellipse4b, 43.0);
        AnchorPane.setLeftAnchor(ellipse4b, size/2 -5);


        AnchorPane.setTopAnchor(arc5, 65.0);
        AnchorPane.setLeftAnchor(arc5, size/2 + 30);
        AnchorPane.setTopAnchor(arc6, 95.0);
        AnchorPane.setLeftAnchor(arc6, size/2 + 28);
        AnchorPane.setTopAnchor(ellipse5, 90.0);
        AnchorPane.setLeftAnchor(ellipse5, size/2 + 47);

        AnchorPane.setTopAnchor(arc7, 65.0);
        AnchorPane.setLeftAnchor(arc7, size/2 + 93);
        AnchorPane.setTopAnchor(ellipse7, 78.0);
        AnchorPane.setLeftAnchor(ellipse7, size/2 + 93);

        AnchorPane.setTopAnchor(text, 130.0);
        AnchorPane.setLeftAnchor(text, size/2 -113);

    }

}
