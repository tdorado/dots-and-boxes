package DAB.DotsAndBoxes.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;

public class Move implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    private int rowFrom;
    private int colFrom;
    private int rowTo;
    private int colTo;
    private int pointsDone;
    private Player player;

    Move(Move move, Player player){
        this(move.rowFrom, move.colFrom, move.rowTo, move.colTo);
        this.player = player;
    }

    public Move(int rowFrom, int colFrom, int rowTo, int colTo) {
        this.rowFrom = rowFrom;
        this.colFrom = colFrom;
        this.rowTo = rowTo;
        this.colTo = colTo;
        this.pointsDone = 0;
        this.player = null;
    }

    public int getRowFrom() {
        return rowFrom;
    }

    public int getColFrom() {
        return colFrom;
    }

    int getPointsDone() {
        return pointsDone;
    }

    void setPointsDone(int pointsDone){
        this.pointsDone = pointsDone;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isHorizontal() {
        return rowFrom == rowTo;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;

        if (object == null || getClass() != object.getClass())
            return false;

        Move move = (Move) object;
        return rowFrom == move.rowFrom && colFrom == move.colFrom && rowTo == move.rowTo && colTo == move.colTo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowFrom, colFrom, rowTo, colTo);
    }

    public Move clone() throws CloneNotSupportedException {
        return (Move) super.clone();
    }

    @Override
    public String toString() {
        return "(" + rowFrom + ", " + colFrom + ")( " + rowTo + ", " + colTo + ')';
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(rowFrom);
        out.writeInt(colFrom);
        out.writeInt(rowTo);
        out.writeInt(colTo);
        out.writeInt(pointsDone);
        out.writeObject(player);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        rowFrom = ois.readInt();
        colFrom = ois.readInt();
        rowTo = ois.readInt();
        colTo = ois.readInt();
        pointsDone = ois.readInt();
        player = (Player) ois.readObject();
    }
}