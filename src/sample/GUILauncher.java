package sample;

import javafx.application.Application;
import javafx.stage.Stage;
import launcher.Main;;

public class GUILauncher extends Application {

	public Main main;
	
	public GUILauncher(Main main) {
		this.main = main;
	}
	
    @Override
    public void start(Stage primaryStage) throws Exception{
        SlideScreen slideScreen = new SlideScreen(main);
       // primaryStage.initStyle(StageStyle.UNDECORATED);
        WelcomeScreen welcomeScreen = new WelcomeScreen();

        GameScreen gameScreen = new GameScreen();

        primaryStage.setScene(slideScreen.drawScene());
        primaryStage.show();

    }

    public void run() {
        launch();
    }
}
