package DAB.DotsAndBoxes.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

public class GameBoard implements Serializable {

    private static final long serialVersionUID = 1L;

    private int size;
    private int squares[][];
    private HashSet<Move> movesDone;
    private HashSet<Move> possibleMoves;
    private Stack<Move> undoStack;
    private Game game;

    public GameBoard(int size) {
        this.size = size;
        this.squares = new int[size - 1][size -1];
        this.movesDone = new HashSet<>();
        this.possibleMoves = new HashSet<>();
        this.undoStack = new Stack<>();
        initializeGameBoard();
    }

    public HashSet<Move> getMovesDone() {
        return movesDone;
    }

    public HashSet<Move> getPossibleMoves() {
        return possibleMoves;
    }

    public boolean isOver() {
        return possibleMoves.isEmpty();
    }

    public int getSize() {
        return size;
    }

    private void initializeGameBoard() {
        for (int i = 0; i < size - 1; i++) {
            for(int j = 0; j < size - 1; j++){
                squares[i][j] = 4;
            }
        }
        List<Move> aux = new ArrayList<>();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (row == size - 1 && col != size - 1) {
                    aux.add(new Move(row, col, row, col + 1));
                } else if (col == size - 1 && row != size - 1) {
                    aux.add(new Move(row, col, row + 1, col));
                } else if (row != size - 1 && col != size - 1) {
                    aux.add(new Move(row, col, row, col + 1));
                    aux.add(new Move(row, col, row + 1, col));
                }
            }
        }
        Collections.shuffle(aux);
        possibleMoves.addAll(aux);
    }

    /**
     * Method that undoes the last move and returns the undone move
     *
     * @return MoveDone undone
     */
    public Move undoLastMove() {
        if (undoStack.isEmpty())
            return null;

        Player currentPlayer = game.getCurrentPlayer();
        Move move = undoStack.pop();
        if (move.getPlayer() != currentPlayer) {
            game.changeCurrentPlayerTurn();
        }
        undoMove(move);

        return move;
    }

    public List<Move> getLastMoves(){
        if (undoStack.isEmpty())
            return null;

        LinkedList<Move> result = new LinkedList<>();
        result.add(undoStack.peek());
        boolean flag = false;
        for (int i = undoStack.size() - 2; i >= 0 && !flag; i--) {
            if (undoStack.get(i).getPlayer() == result.getFirst().getPlayer()) {
                result.add(undoStack.get(i));
            } else {
                flag = true;
            }
        }
        return result;
    }

    public int undoMove(Move move) {
        return 0;
    }

    public boolean makeMove(Move move) {

        return false;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(size);
        out.writeObject(squares);
        out.writeObject(movesDone);
        out.writeObject(possibleMoves);
        out.writeObject(undoStack);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        size = ois.readInt();
        squares = (int[][]) ois.readObject();
        movesDone = (HashSet<Move>) ois.readObject();
        possibleMoves = (HashSet<Move>) ois.readObject();
        undoStack = (Stack<Move>) ois.readObject();
    }
}
