package launcher;

import java.awt.Event;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

import com.ClientReceiver;
import com.ClientSender;

import constants.Commands.Action;
import game.Faction;
import game.GameData;
import game.GameMode;
import game.Obstacle;
import game.Player;
import game.Treasure;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import sample.GameScreen;
import sample.SlideScreen;
import sample.WelcomeScreen;
import states.ClientState;
import util.Client;
import util.Debug;
import util.Transferable;

@SuppressWarnings( "serial" )
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
		Scene scene = new Scene( base );

		WelcomeScreen welcomeScreen = new WelcomeScreen();

		primaryStage.widthProperty().addListener( ( observable, oldValue, newValue ) -> {
			welcomeScreen.setAnchor( newValue.doubleValue() );
		} );
		this.client.gameData.treasures.add( new Treasure( 450, 450 ) );
		this.client.gameData.obstacles.add( new Obstacle( 400, 340, 120, 80 ) );

		GameScreen gameScreen = new GameScreen( this.client, base );
		SlideScreen slideScreen = new SlideScreen( gameScreen );

		gameScreen.requestFocus();
		this.client.gameData.players.put( this.client.player.clientID, this.client.player );
		base.getChildren().addAll( gameScreen, slideScreen );

		primaryStage.setFullScreenExitKeyCombination( KeyCombination.NO_MATCH );
		primaryStage.setFullScreen( true );
		primaryStage.setScene( scene );
		primaryStage.show();

		gameScreen.gameScreen.setPrefWidth( gameScreen.getWidth() );
		gameScreen.gameScreen.setPrefHeight( gameScreen.getHeight() - 40 );

		slideScreen.setPickOnBounds( false );
	}

}
