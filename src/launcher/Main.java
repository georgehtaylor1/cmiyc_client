package launcher;

import game.Obstacle;
import game.Treasure;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import sample.GameScreen;
import sample.SlideScreen;
import sample.SlideScreenData;
import sample.WelcomeScreen;
import util.Client;

public class Main extends Application {

	public Client client;
	private SlideScreenData obData;

	public Main() {
		this.obData = new SlideScreenData();
		this.client = new Client("u-n-owen", obData);

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

		GameScreen gameScreen = new GameScreen( this, base );
		SlideScreen slideScreen = new SlideScreen( gameScreen );
		obData.addObserver(slideScreen);

		gameScreen.requestFocus();
		this.client.gameData.players.put( this.client.player.clientID, this.client.player );
		base.getChildren().addAll( gameScreen, slideScreen );

		primaryStage.setFullScreenExitKeyCombination( KeyCombination.NO_MATCH );
		primaryStage.setFullScreen( true );
		primaryStage.setScene( scene );
		primaryStage.show();

		slideScreen.setPickOnBounds( false );
	}

}
