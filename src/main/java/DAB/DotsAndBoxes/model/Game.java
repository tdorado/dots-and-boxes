package DAB.DotsAndBoxes.model;

import DAB.DotsAndBoxes.model.exceptions.DotCreationException;
import DAB.DotsAndBoxes.model.exceptions.WrongParametersException;
import java.io.*;
import java.util.Random;

public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    private GameBoard gameBoard;
    private int aiType;
    private Player player1;
    private Player player2;
    private int currentPlayerTurn;

    public Game(int size, int aiType, int aiMode, int aiModeParam, boolean prune) {
        this.aiType = aiType;
        this.gameBoard = new GameBoard(size);

        Random random = new Random();
        this.currentPlayerTurn = random.nextInt(3 - 1) + 1;

        if (aiType == 0) {
            this.player1 = new Player();
            this.player2 = new Player();
        } else if (aiType == 1) {
            this.currentPlayerTurn = 1;
            this.player1 = new AIPlayer(aiMode, aiModeParam, prune);
            this.player2 = new Player();
        } else if (aiType == 2) {
            this.currentPlayerTurn = 1;
            this.player1 = new Player();
            this.player2 = new AIPlayer(aiMode, aiModeParam, prune);
        } else {
            this.player1 = new AIPlayer(aiMode, aiModeParam, prune);
            this.player2 = new AIPlayer(aiMode, aiModeParam, prune);
        }
    }

    public int getAiType() {
        return aiType;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    /**
     * Returns the GameBoard containing information for the visual part.
     *
     * @return GameBoard of the Game
     */
    public GameBoard getGameBoard() {
        return gameBoard;
    }

    /**
     * Returns the Player that has to make a move at the moment of request.
     *
     * @return Player
     */
    public Player getCurrentPlayer() {
        if (currentPlayerTurn == 1) {
            return player1;
        }
        return player2;
    }

    /**
     * Returns the Player that does not have to make a move at the moment of request.
     *
     * @return Player
     */
    public Player getNotCurrentPlayer() {
        if (currentPlayerTurn == 1) {
            return player2;
        }
        return player1;
    }

    /**
     * Generates .dot file containing the process of the minimax algorithm that the AIPlayer made to chose it's move.
     *
     * @param fileName name of the .dot file
     * @return boolean, true if the file was created correctly and false if not
     * @throws DotCreationException
     */
    public boolean generateDotFile(String fileName) throws DotCreationException {
        if (aiType == 0) {
            return false;
        } else if (aiType == 1) {
            if (!((AIPlayer) player1).makeDotFile(fileName)) {
                throw new DotCreationException();
            }
        } else if (aiType == 2) {
            if (!((AIPlayer) player2).makeDotFile(fileName)) {
                throw new DotCreationException();
            }
        } else {
            if (!((AIPlayer) getNotCurrentPlayer()).makeDotFile(fileName)) {
                throw new DotCreationException();
            }
        }
        return true;
    }

    public void saveGame(String fileName) throws IOException {
        FileManager.writeToFile(this, fileName);
    }

    public static Game loadGameFromFile(int size, int aiType, int aiMode, int aiModeParam, boolean prune, String fileName) throws IOException, ClassNotFoundException, WrongParametersException {
        Game game;
        game = (Game) FileManager.readFromFile(fileName);
        if (game != null) {
            if (size != game.gameBoard.getSize()) {
                throw new WrongParametersException();
            }
            if (game.aiType == 0) {
                if (aiType == 1) {
                    game.player1 = new AIPlayer(aiMode, aiModeParam, prune, game.player1.getPoints());
                } else if (aiType == 2) {
                    game.player2 = new AIPlayer(aiMode, aiModeParam, prune, game.player2.getPoints());
                } else if (aiType == 3) {
                    game.player1 = new AIPlayer(aiMode, aiModeParam, prune, game.player1.getPoints());
                    game.player2 = new AIPlayer(aiMode, aiModeParam, prune, game.player2.getPoints());
                }
            } else if (game.aiType == 1) {
                if (aiType == 0) {
                    game.player1 = new Player(game.player1.getPoints());
                } else if (aiType == 1) {
                    ((AIPlayer) game.player1).setAiMode(aiMode);
                    ((AIPlayer) game.player1).setAiModeParam(aiModeParam);
                    ((AIPlayer) game.player1).setPrune(prune);
                } else if (aiType == 2) {
                    game.player1 = new Player(game.player1.getPoints());
                    game.player2 = new AIPlayer(aiMode, aiModeParam, prune, game.player2.getPoints());
                } else if (aiType == 3) {
                    game.player2 = new AIPlayer(aiMode, aiModeParam, prune, game.player2.getPoints());
                }
            } else if (game.aiType == 2) {
                if (aiType == 0) {
                    game.player2 = new Player(game.player2.getPoints());
                } else if (aiType == 1) {
                    game.player1 = new AIPlayer(aiMode, aiModeParam, prune, game.player1.getPoints());
                    game.player2 = new Player(game.player2.getPoints());
                } else if (aiType == 2) {
                    ((AIPlayer) game.player2).setAiMode(aiMode);
                    ((AIPlayer) game.player2).setAiModeParam(aiModeParam);
                    ((AIPlayer) game.player2).setPrune(prune);
                } else if (aiType == 3) {
                    game.player1 = new AIPlayer(aiMode, aiModeParam, prune, game.player1.getPoints());
                }
            } else if (game.aiType == 3) {
                if (aiType == 0) {
                    game.player1 = new Player(game.player1.getPoints());
                    game.player2 = new Player(game.player2.getPoints());
                } else if (aiType == 1) {
                    game.player2 = new Player(game.player2.getPoints());
                } else if (aiType == 2) {
                    game.player1 = new Player(game.player1.getPoints());
                } else if (aiType == 3) {
                    ((AIPlayer) game.player1).setAiMode(aiMode);
                    ((AIPlayer) game.player1).setAiModeParam(aiModeParam);
                    ((AIPlayer) game.player1).setPrune(prune);
                    ((AIPlayer) game.player2).setAiMode(aiMode);
                    ((AIPlayer) game.player2).setAiModeParam(aiModeParam);
                    ((AIPlayer) game.player2).setPrune(prune);
                }
            }
            game.aiType = aiType;
        }
        return game;
    }

    void changeCurrentPlayerTurn() {
        if (currentPlayerTurn == 1) {
            currentPlayerTurn = 2;
        } else {
            currentPlayerTurn = 1;
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(gameBoard);
        out.writeInt(aiType);
        out.writeObject(player1);
        out.writeObject(player2);
        out.writeInt(currentPlayerTurn);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        gameBoard = (GameBoard) ois.readObject();
        aiType = ois.readInt();
        player1 = (Player) ois.readObject();
        player2 = (Player) ois.readObject();
        currentPlayerTurn = ois.readInt();
    }
}
