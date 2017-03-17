package sample;

import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{


        /*StackPane base = new StackPane();
        Scene scene = new Scene(base);
        SlideScreen slideScreen = new SlideScreen();
       // primaryStage.initStyle(StageStyle.TRANSPARENT);
        WelcomeScreen welcomeScreen = new WelcomeScreen();

        GameScreen gameScreen = new GameScreen();
        base.getChildren().addAll(gameScreen, welcomeScreen, slideScreen);

        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> {
            welcomeScreen.setAnchor(newValue.doubleValue());
        });

        primaryStage.setScene(scene);
        primaryStage.show();*/

    }

    public static void main(String[] args) {
        launch(args);
    }
}
