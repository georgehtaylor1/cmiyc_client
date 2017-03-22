<<<<<<< HEAD
package sample;

import javafx.application.Application;
import javafx.stage.Stage;
=======
/*
package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
>>>>>>> rinaldy2


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{


<<<<<<< HEAD
        /*StackPane base = new StackPane();
        Scene scene = new Scene(base);
        SlideScreen slideScreen = new SlideScreen();
       // primaryStage.initStyle(StageStyle.TRANSPARENT);
        WelcomeScreen welcomeScreen = new WelcomeScreen();

        GameScreen gameScreen = new GameScreen();
        base.getChildren().addAll(gameScreen, welcomeScreen, slideScreen);
=======
        StackPane base = new StackPane();
        Scene scene = new Scene(base);
         // SlideScreen slideScreen = new SlideScreen();
       // primaryStage.initStyle(StageStyle.TRANSPARENT);
        WelcomeScreen welcomeScreen = new WelcomeScreen();

        //GameScreen gameScreen = new GameScreen(this);
        SlideScreen slideScreen = new SlideScreen(gameScreen);

        base.getChildren().addAll(gameScreen, slideScreen);
>>>>>>> rinaldy2

        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> {
            welcomeScreen.setAnchor(newValue.doubleValue());
        });

        primaryStage.setScene(scene);
<<<<<<< HEAD
        primaryStage.show();*/
=======
        primaryStage.show();
>>>>>>> rinaldy2

    }

    public static void main(String[] args) {
        launch(args);
    }
}
<<<<<<< HEAD
=======
*/
>>>>>>> rinaldy2
