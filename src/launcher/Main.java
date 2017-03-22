package launcher;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import sample.GameScreen;
import sample.SlideScreen;
import sample.WelcomeScreen;
import util.Client;
import util.Debug;

public class Main extends Application {

	public Client client;
	
	private int port;
	private String host;

	private void connect() {

		Socket socket;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;

		try { socket = new Socket( this.host, this.port ); }
		catch( Exception _exception ) { return; } // TODO: Socket Error output

		try {
			out = new ObjectOutputStream( socket.getOutputStream() );
			out.flush();
		}
		catch( Exception _exception ) {
			Debug.say("Error Occured while trying to open new Output Stream.");
			return;
		}

		try { in = new ObjectInputStream( socket.getInputStream() ); }
		catch( Exception _exception ) {
			Debug.say("Error Occured while trying to open new Input Stream.");
			return;
		}

		this.client.connect( in, out );
	}


	public void disconnect() {
		this.client.disconnect();
	}

	public Main() {
		
		this.client = new Client();
		
		
		// HARDCODED!!!!!!!!!
		this.port = 1234;
		this.host = "localhost";

	}

	public static void main( String _arguments[] ) {
		new Main();
		launch( _arguments );
	}

	public void start( Stage primaryStage ) throws Exception {
		StackPane base = new StackPane();
        Scene scene = new Scene(base);
        SlideScreen slideScreen = new SlideScreen(this);
        WelcomeScreen welcomeScreen = new WelcomeScreen();

        GameScreen gameScreen = new GameScreen();
        base.getChildren().addAll(gameScreen, welcomeScreen, slideScreen);

        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> {
            welcomeScreen.setAnchor(newValue.doubleValue());
        });

        primaryStage.setScene(scene);
        primaryStage.show();
	}

}
