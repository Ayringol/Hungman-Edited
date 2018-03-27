package hangman;

import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Game {

	private String answer;
	private String tmpAnswer;
	private String[] letterAndPosArray;
	private String wrongAnswer = "";//mani
	private boolean newGame = false;//mani
	//private String[] words; Ayrin: add an ArrayList
	private int moves;
	private int index;
	private Label label1;
	private Label label2;
        private int wrongLettersCount = 0; //Ayrin: for wrong letters
        private String[] answerLettersPos; //Ayrin: for correct letters
	private final ReadOnlyObjectWrapper<GameStatus> gameStatus;
	private ObjectProperty<Boolean> gameState = new ReadOnlyObjectWrapper<Boolean>();
        public static boolean gameOver = false;
        
    private ArrayList <String> words = new ArrayList<>();//Ayrin: ArrayList
        
	public enum GameStatus {
		GAME_OVER {
			@Override
			public String toString() {
				return "Game over!";
			}
		},
		BAD_GUESS {
			@Override
			public String toString() { return "Bad guess..."; }
		},
		GOOD_GUESS {
			@Override
			public String toString() {
				return "Good guess!";
			}
		},
		WON {
			@Override
			public String toString() {
				return "You won!";
			}
		},
		OPEN {
			@Override
			public String toString() {
				return "Game on, let's go!";
			}
		}
	}

	public Game() {
            //Ayrin: added few words to play with
                words.add("pear");
                words.add("orange");
                words.add("peach");
                words.add("peach");
                words.add("dog");
                words.add("cat");
                words.add("bird");
                words.add("fish");
                System.out.println("Words list: " + words);
		gameStatus = new ReadOnlyObjectWrapper<GameStatus>(this, "gameStatus", GameStatus.OPEN);
		gameStatus.addListener(new ChangeListener<GameStatus>() {
			@Override
			public void changed(ObservableValue<? extends GameStatus> observable,
								GameStatus oldValue, GameStatus newValue) {
				if (gameStatus.get() != GameStatus.OPEN) {
					log("in Game: in changed");
					//currentPlayer.set(null);
				}
			}

		});
		setRandomWord();
		prepTmpAnswer();
		prepLetterAndPosArray();
		moves = 0;

		gameState.setValue(false); // initial state
		createGameStatusBinding();
	}

	private void createGameStatusBinding() {
		List<Observable> allObservableThings = new ArrayList<>();
		ObjectBinding<GameStatus> gameStatusBinding = new ObjectBinding<GameStatus>() {
			{
				super.bind(gameState);
			}
			@Override
			public GameStatus computeValue() {
				log("in computeValue");
				GameStatus check = checkForWinner(index);
				if(check != null ) {
					//mani
					//player should enter any key to start a new game
					if(newGame == true)
					{
						//reset();

					}
					else{
						newGame = true;
						return check;
					}
					//
				}

				//mani
				//starts the game if player press any key
				if(wrongAnswer.trim().length() == 0){
					log("new game");
					return GameStatus.OPEN;

				}
				//
				else if (index != -1){
					log("good guess");
					return GameStatus.GOOD_GUESS;
				}
				else {
					moves++;
					log("bad guess");
					return GameStatus.BAD_GUESS;
					//printHangman();
				}
			}
		};
		gameStatus.bind(gameStatusBinding);
	}

	public ReadOnlyObjectProperty<GameStatus> gameStatusProperty() {
		return gameStatus.getReadOnlyProperty();
	}
	public GameStatus getGameStatus() {
		return gameStatus.get();
	}

	private void setRandomWord() {
                // Ayrin: now the method it is actually setting a new random word from the array created above
                int random = (int)(Math.random() * ((words.size())));
		answer = words.get(random);
                System.out.println("the answer is: " + answer);
	}
        
  
        //Ayrin: "New Addition" this method sets the hints for the word to guess
        public  void setHints(GridPane goodLettersGrid){             
            int answerCount = answer.split("").length;
            answerLettersPos = answer.split("");
            for (int i = 0; i<answerCount;i++){
                goodLettersGrid.add(new Label("_ "), i, 0);
            }
        }
        
	private void prepTmpAnswer() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < answer.length(); i++) {
			sb.append(" ");
		}
		tmpAnswer = sb.toString();
	}

	private void prepLetterAndPosArray() {
		letterAndPosArray = new String[answer.length()];
		for(int i = 0; i < answer.length(); i++) {
			letterAndPosArray[i] = answer.substring(i,i+1);
		}
	}

	private int getValidIndex(String input) {
		int index = -1;
		wrongAnswer +=input;//mani
		for(int i = 0; i < letterAndPosArray.length; i++) {
			if(letterAndPosArray[i].equals(input)) {
				index = i;
				letterAndPosArray[i] = "";
				break;
			}
		}
		return index;
	}

	private int update(String input) {
		int index = getValidIndex(input);
		if(index != -1) {
			StringBuilder sb = new StringBuilder(tmpAnswer);
			sb.setCharAt(index, input.charAt(0));
                        
			tmpAnswer = sb.toString();
                        
		}
		return index;
	}

	private static void drawHangmanFrame() {}

    /*Ayrin: changed the method's functionality     
     * Now this method specifies what inputs are correct or wrong
     * And shows them in application*/
	public void makeMove(String letter,Label letterLabel,GridPane wrongLettersGrid,GridPane goodLettersGrid) {
		log("\nin makeMove: " + letter);
		index = update(letter);
		// this will toggle the state of the game
		gameState.setValue(!gameState.getValue());
                letterLabel.textProperty().set(letter);
                if(index==-1){
							label1 = new Label(letter);
                            label1.setFont(new Font("Comic Sans MS",56));
                            label1.setTextFill(Color.web("#ff0000"));
                wrongLettersGrid.add(label1, wrongLettersCount, 0);
                wrongLettersCount++;
                }
                else if(index !=-1){
                    for(int i=0;i<answerLettersPos.length;i++){
                        if(answerLettersPos[i].equals(letter)){
                            label2 = new Label(letter);
                            label2.setFont(new Font("Comic Sans MS",56));
                            label2.setTextFill(Color.web("#0000ff"));
                            goodLettersGrid.add(label2, i, 0);

                          System.out.println(tmpAnswer);
                        }
                    }                                  
                }
	
        }// end of makeMove()

	//mani
	//reset the game
	public void reset(GridPane wrongLettersGrid,GridPane goodLettersGrid) { 
		// Ayrin: reset METHOD NOW TAKES THE TWO GRIDS AS PARAMETERS IN ORDER TO MODIFY THEM.
            wrongLettersGrid.getChildren().clear(); //Ayrin: CLEARS wrongLettersGrid GRIDPANE.
            goodLettersGrid.getChildren().clear();  //Ayrin: CLEARS goodLettersGrid GRIDPANE.
            wrongAnswer = "";
            newGame = false;
            label1 = new Label();
            label2 = new Label();
            setRandomWord();
            prepTmpAnswer();
            prepLetterAndPosArray();
            moves = 0;
            gameState.setValue(false); // initial state
            createGameStatusBinding();
            gameOver = false; //Ayrin: SETS THE GAME OVER BOOLEAN TO FALSE SO THE makeMove METHOD CAN BE CALLED

        }
	//

	private int numOfTries() {
		return 5; // TODO, fix me
	}

	public static void log(String s) {
            
		System.out.println(s);
            
            }

	private GameStatus checkForWinner(int status) {
		log("in checkForWinner");
		if(tmpAnswer.equals(answer)) {
			log("won");
                        gameOver=true; /*Ayrin: SETS THE GAME OVER BOOLEAN 
                                        TO TRUE SO THE makeMove METHOD CAN'T BE 
                                        CALLED AND NO FURTHER GUESSES CAN BE MADE.*/
			return GameStatus.WON;
		}
		else if(moves == numOfTries()) {
			log("game over");
                        gameOver=true;  /*Ayrin: SETS THE GAME OVER BOOLEAN 
                                         TO TRUE SO THE makeMove METHOD CAN'T BE 
                                         CALLED AND NO FURTHER GUESSES CAN BE MADE.*/              
			return GameStatus.GAME_OVER;
		}
		else {
			return null;
		}
	}
}