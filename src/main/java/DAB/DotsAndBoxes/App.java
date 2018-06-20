package DAB.DotsAndBoxes;

import DAB.DotsAndBoxes.model.*;
import DAB.DotsAndBoxes.model.exceptions.WrongParametersException;
import DAB.DotsAndBoxes.view.BoardPane;
import DAB.DotsAndBoxes.view.GameScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {
    private static Game game;

    public static void main(String[] args) {
        boolean correct = true;
        try {
            game = parseArguments(args);
        } catch (WrongParametersException e) {
            correct = false;
            System.out.println("Wrong arguments, please try again.");
        } catch (Exception e) {
            correct = false;
            System.out.println("Error while loading from file. File may be corrupt.");
        }
        if(correct){
            launch(args);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new GameScene(new BoardPane(game), game);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static Game parseArguments(String[] arguments) throws WrongParametersException, IOException, ClassNotFoundException {

        int size = -1, aiType = -1, aiMode = -1, aiModeParam = -1;
        boolean prune = false, argumentFail = false, loadFromFile = false;
        String fileName = null;
        Game result;

        if (arguments.length == 4 || arguments.length == 6 || arguments.length == 10 || arguments.length == 12) {
            if (arguments[0].equals("-size")) {
                size = Integer.parseInt(arguments[1]);
                if (size < 2 && size > 20) {
                    argumentFail = true;
                }
            } else {
                argumentFail = true;
            }
            if (arguments[2].equals("-ai")) {
                aiType = Integer.parseInt(arguments[3]);
                if (aiType < 0 && aiType > 3) {
                    argumentFail = true;
                }
            } else {
                argumentFail = true;
            }
            if (arguments.length > 4) {
                if (arguments.length == 6) {
                    if (arguments[4].equals("-load")) {
                        loadFromFile = true;
                        fileName = arguments[5];
                    } else {
                        argumentFail = true;
                    }
                } else {
                    if (arguments[4].equals("-mode")) {
                        if (arguments[5].equals("time")) {
                            aiMode = 0;
                        } else if (arguments[5].equals("depth")) {
                            aiMode = 1;
                        } else {
                            argumentFail = true;
                        }
                    } else {
                        argumentFail = true;
                    }
                    if (arguments[6].equals("-param")) {
                        aiModeParam = Integer.parseInt(arguments[7]);
                        if (aiType < 0) {
                            argumentFail = true;
                        }
                    } else {
                        argumentFail = true;
                    }
                    if (arguments[8].equals("-prune")) {
                        if (arguments[9].equals("on")) {
                            prune = true;
                        } else if (arguments[9].equals("off")) {
                            prune = false;
                        } else {
                            argumentFail = true;
                        }
                    } else {
                        argumentFail = true;
                    }
                }
            }
            if (arguments.length > 10) {
                if (arguments[10].equals("-load")) {
                    loadFromFile = true;
                    fileName = arguments[11];
                } else {
                    argumentFail = true;
                }
            }
        } else {
            argumentFail = true;
        }

        if(argumentFail){
            throw new WrongParametersException();
        }

        if (!loadFromFile) {
            result = new Game(size, aiType, aiMode, aiModeParam, prune);
        } else {
            result = Game.loadGameFromFile(size, aiType, aiMode, aiModeParam, prune, fileName);
        }
        return result;
    }

}
