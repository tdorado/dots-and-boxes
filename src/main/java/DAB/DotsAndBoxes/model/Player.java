package DAB.DotsAndBoxes.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    Game game;
    int points;

    Player(Game game, int points) {
        this.game = game;
        this.points = points;
    }

    Player(Game game) {
        this(game,0);
    }

    public boolean isAI() {
        return false;
    }

    public int getPoints() {
        return points;
    }

    void setPoints(int points) {
        this.points = points;
    }

    public void makeMove(Move move){
        game.getBoard().makeMove(new Move(move, this));
    }

    /**
     * Heuristic for the minimax
     *
     * @return int heuristic value
     */
    int heuristicValue() {
        return points - getOpposingPlayer().getPoints();
    }

    /**
     * Method used for the heuristic value that returns the player that currently does not have to play
     *
     * @return Player
     */
    Player getOpposingPlayer() {
        if (game.getCurrentPlayer() == this) {
            return game.getNotCurrentPlayer();
        }
        return game.getCurrentPlayer();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(game);
        out.writeInt(points);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        game = (Game) ois.readObject();
        points = ois.readInt();
    }
}