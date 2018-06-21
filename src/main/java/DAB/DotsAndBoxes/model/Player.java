package DAB.DotsAndBoxes.model;

import DAB.DotsAndBoxes.App;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    int points;

    Player(int points) {
        this.points = points;
    }

    Player() {
        this(0);
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

    public boolean makeMove(Move move){
        return App.getInstance().getGameBoard().makeMove(new Move(move, this));
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(points);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        points = ois.readInt();
    }
}