package sample;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import util.Client;

public class GameOverScreen extends Pane {
	
	private Client client;
	
	public GameOverScreen(Client _c) {
		this.client = _c;
	}
	
	public void drawScene() {
		Text winText;
		if (client.gameData.secScore > client.gameData.thiefScore)
			winText = new Text("Security win!");
		else
			winText = new Text("Thieves win!");
	    winText.setId("fancytext");
	    winText.setFont(new Font(32));
	    winText.setX(30);
	    winText.setY(30);
	    winText.setFill(Color.WHITE);
	    this.getChildren().add(winText);
	    
	    Text secScore = new Text(Double.toString(client.gameData.secScore));
	    Text thiefScore = new Text(Double.toString(client.gameData.thiefScore));
	    secScore.setId("fancytext");
	    secScore.setFont(new Font(32));
	    secScore.setX(30);
	    secScore.setY(60);
	    secScore.setFill(Color.WHITE);
	    this.getChildren().add(secScore);
	    thiefScore.setId("fancytext");
	    thiefScore.setFont(new Font(32));
	    thiefScore.setX(30);
	    thiefScore.setY(90);
	    thiefScore.setFill(Color.WHITE);
	    this.getChildren().add(thiefScore);
	}
}
