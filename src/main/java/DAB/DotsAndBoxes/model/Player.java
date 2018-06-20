package DAB.DotsAndBoxes.model;

import DAB.DotsAndBoxes.model.exceptions.InvalidMoveException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    int points;
    Game game;

    Player(int points, Game game) {
        this.points = points;
        this.game = game;
    }

    Player(Game game) {
        this(0, game);
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

    public boolean makePlayerMove(Move move) throws InvalidMoveException {
        if(!game.getGameBoard().makeMove(new Move(move, this))){
            throw new InvalidMoveException();
        }
        return true;
    }

    public boolean makeMove(Move move){
        return game.getGameBoard().makeMove(new Move(move, this));
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(points);
        out.writeObject(game);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        points = ois.readInt();
        game = (Game) ois.readObject();
    }
}