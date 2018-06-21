package DAB.DotsAndBoxes.model;

import DAB.DotsAndBoxes.App;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

public class Board implements Serializable {

    private static final long serialVersionUID = 1L;

    private int size;
    private int squares[][];
    private LinkedList<Move> doneMoves;
    private LinkedHashSet<Move> possibleMoves;

    Board(int size) {
        this.size = size;
        this.squares = new int[size - 1][size -1];
        this.doneMoves = new LinkedList<>();
        this.possibleMoves = new LinkedHashSet<>();
        initializeGameBoard();
    }

    LinkedHashSet<Move> getPossibleMoves() {
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

    public Move undoLastMove() {
        if (doneMoves.isEmpty())
            return null;

        Move move = doneMoves.removeLast();
        move.getPlayer().setPoints(move.getPlayer().getPoints() - move.getPointsDone());
        possibleMoves.add(move);

        int x = move.getRowFrom();
        int y = move.getColFrom();

        if (move.isHorizontal()) {
            if(x == 0){
                squares[x][y]++;
            }
            else if(x == size - 1){
                squares[x - 1][y]++;
            }
            else{
                squares[x][y]++;
                squares[x - 1][y]++;
            }
        } else {
            if(y == 0){
                squares[x][y]++;
            }
            else if(y == size - 1){
                squares[x][y - 1]++;
            }
            else{
                squares[x][y]++;
                squares[x][y - 1]++;
            }
        }

        Player currentPlayer = App.getInstance().getCurrentPlayer();
        if (move.getPlayer() != currentPlayer) {
            App.getInstance().changeCurrentPlayerTurn();
        }

        return move;
    }

    public List<Move> getLastDoneMoves(){
        if (doneMoves.isEmpty())
            return null;

        LinkedList<Move> result = new LinkedList<>();
        boolean stop = false;
        result.add(doneMoves.getLast());
        for (int i = doneMoves.size() - 2; i >= 0 && !stop; i--) {
            if (doneMoves.get(i).getPlayer() == result.getFirst().getPlayer()) {
                result.add(doneMoves.get(i));
            } else {
                stop = true;
            }
        }
        return result;
    }

    public List<Move> getAllDoneMoves(){
        return doneMoves;
    }

    void makeMove(Move move) {
        if (possibleMoves.contains(move)) {
            doneMoves.addLast(move);
            possibleMoves.remove(move);

            int pointsDone = 0;
            int x = move.getRowFrom();
            int y = move.getColFrom();

            if (move.isHorizontal()) {
                if(x == 0){
                    squares[x][y]--;
                    if(squares[x][y] == 0){
                        pointsDone++;
                    }
                }
                else if(x == size - 1){
                    squares[x - 1][y]--;
                    if(squares[x - 1][y] == 0){
                        pointsDone++;
                    }
                }
                else{
                    squares[x][y]--;
                    if(squares[x][y] == 0){
                        pointsDone++;
                    }
                    squares[x - 1][y]--;
                    if(squares[x - 1][y] == 0){
                        pointsDone++;
                    }
                }
            } else {
                if(y == 0){
                    squares[x][y]--;
                    if(squares[x][y] == 0){
                        pointsDone++;
                    }
                }
                else if(y == size - 1){
                    squares[x][y -1]--;
                    if(squares[x][y - 1] == 0){
                        pointsDone++;
                    }
                }
                else{
                    squares[x][y]--;
                    if(squares[x][y] == 0){
                        pointsDone++;
                    }
                    squares[x][y - 1]--;
                    if(squares[x][y - 1] == 0){
                        pointsDone++;
                    }
                }
            }
            if(pointsDone == 0){
                App.getInstance().changeCurrentPlayerTurn();
            }
            else{
                move.setPointsDone(pointsDone);
                move.getPlayer().setPoints(move.getPlayer().getPoints() + pointsDone);
            }
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(size);
        out.writeObject(squares);
        out.writeObject(doneMoves);
        out.writeObject(possibleMoves);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        size = ois.readInt();
        squares = (int[][]) ois.readObject();
        doneMoves = (LinkedList<Move>) ois.readObject();
        possibleMoves = (LinkedHashSet<Move>) ois.readObject();
    }
}
