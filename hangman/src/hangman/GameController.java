package hangman;

import static hangman.Game.gameOver;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class GameController {

	private final ExecutorService executorService;
	private final Game game;	
	
	public GameController(Game game) {
		this.game = game;
		executorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setDaemon(true);
				return thread;
			}
		});
	}

	@FXML
	private VBox board ;
	@FXML
	private Label statusLabel ;
	@FXML
	private Label enterALetterLabel ;
        @FXML
        private Label letterLabel; //Ayrin: This label is where the program shows the inputs
        @FXML
	private TextField textField ;
        @FXML
        private GridPane wrongLettersGrid; //Ayrin
        @FXML
        private GridPane goodLettersGrid; //Ayrin
        @FXML
        private MenuItem HangmanButton; /* Ayrin: INJECTED THE HangmanButton DEFINED IN 
                                           THE FXML FILE SO AN EVENTHANDLER CAN BE ADDED TO IT */
        
    public void initialize() throws IOException {
		System.out.println("in initialize");
		drawHangman();
		addTextBoxListener();
		setUpStatusLabelBindings();
                game.setHints(goodLettersGrid); /*Ayrin: call to set the hints of the word to guess
                                                 The layout is handled by the goodLettersGrid GRIDPANE*/
                HangmanButton.addEventHandler(ActionEvent.ACTION, (ActionEvent actionEvent) ->{ 
                	//Ayrin: AN EVENTHANDLER IS ADDED IN ORDER TO CALLE THE reset AND setHints METHODS.
                    game.reset(wrongLettersGrid,goodLettersGrid);
                    game.setHints(goodLettersGrid);
                });
	}

	private void addTextBoxListener() {
		textField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
				if(newValue.length() > 0) 
				{//Ayrin: added these 3 lines
  
                                    textField.clear();
   
                                        if(!gameOver){ 
                                        	//Ayrin: WITH THIS WE MAKE SURE THE makeMove METHOD IS CALLED ONLY IF THE GAME IS NOT OVER.
                                            System.out.print(newValue);                    
                                            game.makeMove(newValue.toLowerCase(),letterLabel,wrongLettersGrid,goodLettersGrid); 
                                            // Ayrin: ADDED THE toLowerCase METHOD IN ORDER TO HANDLE THE CASE OF A BLOQ MAYUS ACTIVATED
                                        }
				}
			}
		});
	}

	private void setUpStatusLabelBindings() {

		System.out.println("in setUpStatusLabelBindings");
		statusLabel.textProperty().bind(Bindings.format("%s", game.gameStatusProperty()));
		enterALetterLabel.textProperty().bind(Bindings.format("%s", "Enter a letter:"));
		/*	Bindings.when(
					game.currentPlayerProperty().isNotNull()
			).then(
				Bindings.format("To play: %s", game.currentPlayerProperty())
			).otherwise(
				""
			)
		);
		*/
	}

	private void drawHangman() {

		Line line = new Line();
		line.setStartX(25.0f);
		line.setStartY(0.0f);
		line.setEndX(25.0f);
		line.setEndY(25.0f);

		Circle c = new Circle();
		c.setRadius(10);

		board.getChildren().add(line);
		board.getChildren().add(c);

	}
		


	@FXML
	private void quit() {
		board.getScene().getWindow().hide();
	}

}