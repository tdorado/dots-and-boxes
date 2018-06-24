package DAB.DotsAndBoxes.model;

import DAB.DotsAndBoxes.model.exceptions.DotCreationException;
import DAB.DotsAndBoxes.model.exceptions.WrongParametersException;
import java.io.*;
import java.util.LinkedHashSet;
import java.util.Random;

public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    private Board board;
    private int aiType;
    private Player player1;
    private Player player2;
    private int currentPlayerTurn;

    public Game(int size, int aiType, int aiMode, int aiModeParam, boolean prune) {
        this.aiType = aiType;
        this.board = new Board(this, size);

        Random random = new Random();
        this.currentPlayerTurn = random.nextInt(3 - 1) + 1;

        if (aiType == 0) {
            this.player1 = new Player(this);
            this.player2 = new Player(this);
        } else if (aiType == 1) {
            this.currentPlayerTurn = 1;
            this.player1 = new AIPlayer(this, aiMode, aiModeParam, prune);
            this.player2 = new Player(this);
        } else if (aiType == 2) {
            this.currentPlayerTurn = 1;
            this.player1 = new Player(this);
            this.player2 = new AIPlayer(this, aiMode, aiModeParam, prune);
        } else {
            this.player1 = new AIPlayer(this, aiMode, aiModeParam, prune);
            this.player2 = new AIPlayer(this, aiMode, aiModeParam, prune);
        }
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Board getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        if (currentPlayerTurn == 1) {
            return player1;
        }
        return player2;
    }

    Player getNotCurrentPlayer() {
        if (currentPlayerTurn == 1) {
            return player2;
        }
        return player1;
    }

    public void generateDotFile(String fileName) throws DotCreationException {
        if (aiType == 1) {
            if (!((AIPlayer) player1).makeDotFile(fileName)) {
                throw new DotCreationException();
            }
        } else if (aiType == 2) {
            if (!((AIPlayer) player2).makeDotFile(fileName)) {
                throw new DotCreationException();
            }
        } else if (aiType == 3) {
            if (!((AIPlayer) getNotCurrentPlayer()).makeDotFile(fileName)) {
                throw new DotCreationException();
            }
        }
    }

    Game deepClone(){
        Game result = new Game(board.getSize(), aiType, 0, 0, false);
        result.board.setSquares(board.getSquares().clone());
        result.board.setPossibleMoves(new LinkedHashSet<>(board.getPossibleMoves()));
        result.setCurrentPlayerTurn(currentPlayerTurn);
        result.player1 = new Player(result, player1.getPoints());
        result.player2 = new Player(result, player2.getPoints());
        return result;
    }

    private void setCurrentPlayerTurn(int currentPlayerTurn) {
        this.currentPlayerTurn = currentPlayerTurn;
    }

    public void saveGame(String fileName) throws IOException {
        ObjectOutputStream outStream;
        outStream = new ObjectOutputStream(new FileOutputStream(System.getProperty("user.dir") + "/target/" + fileName + ".game"));
        outStream.writeObject(this);
        outStream.close();
    }

    public static Game loadGameFromFile(int size, int aiType, int aiMode, int aiModeParam, boolean prune, String fileName) throws IOException, ClassNotFoundException, WrongParametersException {
        ObjectInputStream inputStream;
        inputStream = new ObjectInputStream(new FileInputStream(System.getProperty("user.dir") + "/target/" + fileName + ".game"));
        Game game = (Game)inputStream.readObject();
        inputStream.close();
        if (game != null) {
            if (size != game.board.getSize()) {
                throw new WrongParametersException();
            }
            if (game.aiType == 0) {
                if (aiType == 1) {
                    game.player1 = new AIPlayer(game, aiMode, aiModeParam, prune, game.player1.getPoints());
                } else if (aiType == 2) {
                    game.player2 = new AIPlayer(game, aiMode, aiModeParam, prune, game.player2.getPoints());
                } else if (aiType == 3) {
                    game.player1 = new AIPlayer(game, aiMode, aiModeParam, prune, game.player1.getPoints());
                    game.player2 = new AIPlayer(game, aiMode, aiModeParam, prune, game.player2.getPoints());
                }
            } else if (game.aiType == 1) {
                if (aiType == 0) {
                    game.player1 = new Player(game, game.player1.getPoints());
                } else if (aiType == 1) {
                    ((AIPlayer) game.player1).setAiMode(aiMode);
                    ((AIPlayer) game.player1).setAiModeParam(aiModeParam);
                    ((AIPlayer) game.player1).setPrune(prune);
                } else if (aiType == 2) {
                    game.player1 = new Player(game, game.player1.getPoints());
                    game.player2 = new AIPlayer(game, aiMode, aiModeParam, prune, game.player2.getPoints());
                } else if (aiType == 3) {
                    game.player2 = new AIPlayer(game, aiMode, aiModeParam, prune, game.player2.getPoints());
                }
            } else if (game.aiType == 2) {
                if (aiType == 0) {
                    game.player2 = new Player(game, game.player2.getPoints());
                } else if (aiType == 1) {
                    game.player1 = new AIPlayer(game, aiMode, aiModeParam, prune, game.player1.getPoints());
                    game.player2 = new Player(game, game.player2.getPoints());
                } else if (aiType == 2) {
                    ((AIPlayer) game.player2).setAiMode(aiMode);
                    ((AIPlayer) game.player2).setAiModeParam(aiModeParam);
                    ((AIPlayer) game.player2).setPrune(prune);
                } else if (aiType == 3) {
                    game.player1 = new AIPlayer(game, aiMode, aiModeParam, prune, game.player1.getPoints());
                }
            } else if (game.aiType == 3) {
                if (aiType == 0) {
                    game.player1 = new Player(game, game.player1.getPoints());
                    game.player2 = new Player(game, game.player2.getPoints());
                } else if (aiType == 1) {
                    game.player2 = new Player(game, game.player2.getPoints());
                } else if (aiType == 2) {
                    game.player1 = new Player(game, game.player1.getPoints());
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
        out.writeObject(board);
        out.writeInt(aiType);
        out.writeObject(player1);
        out.writeObject(player2);
        out.writeInt(currentPlayerTurn);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        board = (Board) ois.readObject();
        aiType = ois.readInt();
        player1 = (Player) ois.readObject();
        player2 = (Player) ois.readObject();
        currentPlayerTurn = ois.readInt();
    }
}
