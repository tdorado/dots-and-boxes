package DAB.DotsAndBoxes.model;

import DAB.DotsAndBoxes.App;
import java.io.*;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class AIPlayer extends Player implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int alpha = 10000;
    private static final int beta = -10000;

    private int aiMode;
    private int aiModeParam;
    private boolean prune;
    private transient MoveState lastMoveState;

    AIPlayer(int aiMode, int aiModeParam, boolean prune, int points) {
        super(points);
        this.aiMode = aiMode;
        this.aiModeParam = aiModeParam;
        this.prune = prune;
        this.lastMoveState = null;
    }

    AIPlayer(int aiMode, int aiModeParam, boolean prune) {
        this(aiMode, aiModeParam, prune, 0);
    }

    @Override
    public boolean isAI() {
        return true;
    }

    void setAiMode(int aiMode) {
        this.aiMode = aiMode;
    }

    void setAiModeParam(int aiModeParam) {
        this.aiModeParam = aiModeParam;
    }

    void setPrune(boolean prune) {
        this.prune = prune;
    }

    /**
     * Method that calculates the move using minimax algorithm with the parameters already saved
     */
    public void calculateAndMakeMove() {
        LinkedList<Move> moves = minimax();
        for (Move move : moves) {
            this.makeMove(move);
        }
    }

    /**
     * Method that gets the moves that the AI has to make
     *
     * @return List of moves to be made by de AIPlayer
     */
    private LinkedList<Move> minimax() {
        lastMoveState = new MoveState(false);
        if (aiMode == 0) {
            long timeSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
            minimaxTimeRec(0, lastMoveState, false, true, timeSeconds + (long) aiModeParam + 1);
        } else {
            minimaxDepthRec(0, lastMoveState, 0, false, true);
        }
        System.out.println(lastMoveState.chosen);
        if (lastMoveState.chosen != null) {
            return lastMoveState.chosen.moves;
        }
        return null;
    }

    /**
     * Method for the minimax recurrence in depth
     *
     * @param movesCount        int of how many moves were done in this recurrence
     * @param previousMoveState the MoveState that needs this recurrence
     * @param depth             int of the current depth
     * @param sameLevel         boolean if the play has more than one move
     * @param maxOrMin          boolean if max or min of the minimax
     * @return int minimax value
     */
    private int minimaxDepthRec(int movesCount, MoveState previousMoveState, int depth, boolean sameLevel, boolean maxOrMin) {
        if (maxOrMin) {  // MAX
            if (!App.getInstance().getGameBoard().getPossibleMoves().isEmpty()) {
                if (sameLevel) { // MAX CON VARIOS MOVS
                    boolean firstEntry = true;
                    LinkedHashSet<Move> possibleMoves = (LinkedHashSet<Move>) App.getInstance().getGameBoard().getPossibleMoves().clone();
                    for (Move move : possibleMoves) {
                        int auxMovesCount = movesCount;
                        movesCount++;
                        App.getInstance().getCurrentPlayer().makeMove(move);
                        MoveState currentMoveState;
                        if (firstEntry) {
                            currentMoveState = previousMoveState.children.getLast();
                            firstEntry = false;
                        } else {
                            currentMoveState = new MoveState(true);
                            LinkedList<Move> moves = previousMoveState.children.getLast().moves;
                            for (int i = 0; i < moves.size() - 1; i++) {
                                currentMoveState.moves.add(moves.get(i));
                            }
                            previousMoveState.children.add(currentMoveState);
                        }
                        currentMoveState.moves.add(move);
                        if (this == App.getInstance().getCurrentPlayer()) {
                            minimaxDepthRec(movesCount, previousMoveState, depth, true, true);
                        } else {
                            if (previousMoveState.chosen == null) {
                                previousMoveState.chosen = currentMoveState;
                            }
                            if (depth < aiModeParam) {
                                currentMoveState.value = minimaxDepthRec(0, currentMoveState, depth + 1, false, false);
                            } else {
                                currentMoveState.value = ((AIPlayer) App.getInstance().getNotCurrentPlayer()).heuristicValue();
                            }
                            if (previousMoveState.chosen.value < currentMoveState.value) {
                                previousMoveState.chosen = currentMoveState;
                            }
                        }
                        while (movesCount > auxMovesCount) {
                            App.getInstance().getGameBoard().undoLastMove();
                            movesCount--;
                        }
                    }
                } else { // MAX CON UN SOLO MOV
                    LinkedHashSet<Move> possibleMoves = (LinkedHashSet<Move>) App.getInstance().getGameBoard().getPossibleMoves().clone();
                    for (Move move : possibleMoves) {
                        movesCount++;
                        App.getInstance().getCurrentPlayer().makeMove(move);
                        MoveState currentMoveState = new MoveState(true);
                        currentMoveState.moves.add(move);
                        previousMoveState.children.add(currentMoveState);
                        if (this == App.getInstance().getCurrentPlayer()) {
                            minimaxDepthRec(movesCount, previousMoveState, depth, true, true);
                        } else {
                            if (previousMoveState.chosen == null) {
                                previousMoveState.chosen = currentMoveState;
                            }
                            if (depth < aiModeParam) {
                                currentMoveState.value = minimaxDepthRec(0, currentMoveState, depth + 1, false, false);
                            } else
                                currentMoveState.value = ((AIPlayer) App.getInstance().getNotCurrentPlayer()).heuristicValue();
                            if (previousMoveState.chosen.value < currentMoveState.value) {
                                previousMoveState.chosen = currentMoveState;
                            }
                        }
                        while (movesCount > 0) {
                            App.getInstance().getGameBoard().undoLastMove();
                            movesCount--;
                        }
                    }
                }
            } else {
                MoveState currentMoveState = previousMoveState.children.getLast();
                if (previousMoveState.chosen == null)
                    previousMoveState.chosen = currentMoveState;
                currentMoveState.value = ((AIPlayer) App.getInstance().getCurrentPlayer()).heuristicValue();
                if (previousMoveState.chosen.value < currentMoveState.value)
                    previousMoveState.chosen = currentMoveState;
            }
        } else { // MIN
            if (!App.getInstance().getGameBoard().getPossibleMoves().isEmpty()) {
                if (sameLevel) { //MIN CON VARIOS MOVS
                    boolean firstEntry = true;
                    LinkedHashSet<Move> possibleMoves = (LinkedHashSet<Move>) App.getInstance().getGameBoard().getPossibleMoves().clone();
                    for (Move move : possibleMoves) {
                        int auxMovesCount = movesCount;
                        movesCount++;
                        App.getInstance().getCurrentPlayer().makeMove(move);
                        MoveState currentMoveState;
                        if (firstEntry) {
                            currentMoveState = previousMoveState.children.getLast();
                            firstEntry = false;
                        } else {
                            currentMoveState = new MoveState(false);
                            LinkedList<Move> moves = previousMoveState.children.getLast().moves;
                            for (int i = 0; i < moves.size() - 1; i++)
                                currentMoveState.moves.add(moves.get(i));
                            previousMoveState.children.add(currentMoveState);
                        }
                        currentMoveState.moves.add(move);
                        if (this != App.getInstance().getCurrentPlayer()) {
                            minimaxDepthRec(movesCount, previousMoveState, depth, true, false);
                        } else {
                            if (previousMoveState.chosen == null) {
                                previousMoveState.chosen = currentMoveState;
                            }
                            if (prune) {
                                currentMoveState.value = ((AIPlayer) App.getInstance().getCurrentPlayer()).heuristicValue();
                                if (previousMoveState.chosen != currentMoveState && previousMoveState.chosen.value <= currentMoveState.value) {
                                    currentMoveState.pruned = true;
                                }
                                if (!currentMoveState.pruned) {
                                    if (depth < aiModeParam) {
                                        currentMoveState.value = minimaxDepthRec(0, currentMoveState, depth + 1, false, true);
                                    }
                                    if (previousMoveState.chosen.value > currentMoveState.value) {
                                        previousMoveState.chosen = currentMoveState;
                                    }
                                }
                            } else {
                                if (depth < aiModeParam) {
                                    currentMoveState.value = minimaxDepthRec(0, currentMoveState, depth + 1, false, true);
                                } else {
                                    currentMoveState.value = ((AIPlayer) App.getInstance().getCurrentPlayer()).heuristicValue();
                                }
                                if (previousMoveState.chosen.value > currentMoveState.value) {
                                    previousMoveState.chosen = currentMoveState;
                                }
                            }
                        }
                        while (movesCount > auxMovesCount) {
                            App.getInstance().getGameBoard().undoLastMove();
                            movesCount--;
                        }
                    }
                } else { //MIN CON UN SOLO MOV
                    LinkedHashSet<Move> possibleMoves = (LinkedHashSet<Move>) App.getInstance().getGameBoard().getPossibleMoves().clone();
                    for (Move move : possibleMoves) {
                        movesCount++;
                        App.getInstance().getCurrentPlayer().makeMove(move);
                        MoveState currentMoveState = new MoveState(false);
                        currentMoveState.moves.add(move);
                        previousMoveState.children.add(currentMoveState);
                        if (this != App.getInstance().getCurrentPlayer()) {
                            minimaxDepthRec(movesCount, previousMoveState, depth, true, false);
                        } else {
                            if (previousMoveState.chosen == null) {
                                previousMoveState.chosen = currentMoveState;
                            }
                            if (prune) {
                                currentMoveState.value = ((AIPlayer) App.getInstance().getCurrentPlayer()).heuristicValue();
                                if (previousMoveState.chosen != currentMoveState && previousMoveState.chosen.value <= currentMoveState.value) {
                                    currentMoveState.pruned = true;
                                }
                                if (!currentMoveState.pruned) {
                                    if (depth < aiModeParam) {
                                        currentMoveState.value = minimaxDepthRec(0, currentMoveState, depth + 1, false, true);
                                    }
                                    if (previousMoveState.chosen.value > currentMoveState.value)
                                        previousMoveState.chosen = currentMoveState;
                                }
                            } else {
                                if (depth < aiModeParam) {
                                    currentMoveState.value = minimaxDepthRec(0, currentMoveState, depth + 1, false, true);
                                } else {
                                    currentMoveState.value = ((AIPlayer) App.getInstance().getCurrentPlayer()).heuristicValue();
                                }
                                if (previousMoveState.chosen.value > currentMoveState.value) {
                                    previousMoveState.chosen = currentMoveState;
                                }
                            }
                        }
                        while (movesCount > 0) {
                            App.getInstance().getGameBoard().undoLastMove();
                            movesCount--;
                        }
                    }
                }
            } else {
                MoveState currentMoveState = previousMoveState.children.getLast();
                if (previousMoveState.chosen == null)
                    previousMoveState.chosen = currentMoveState;
                currentMoveState.value = ((AIPlayer) App.getInstance().getCurrentPlayer()).heuristicValue();
                if (previousMoveState.chosen.value > currentMoveState.value)
                    previousMoveState.chosen = currentMoveState;
            }
        }
        return previousMoveState.chosen.value;
    }

    /**
     * Method for the minimax recurrence in time
     *
     * @param movesCount        int of how many moves were done in this recurrence
     * @param previousMoveState the MoveState that needs this recurrence
     * @param sameLevel         boolean if the play has more than one move
     * @param maxOrMin          boolean if max or min of the minimax
     * @param secondsMax        long for the maximum of seconds the method has to be done for
     * @return int minimax value
     */
    private int minimaxTimeRec(int movesCount, MoveState previousMoveState, boolean sameLevel, boolean maxOrMin, long secondsMax) {
        if (maxOrMin) {  // MAX
            if (!App.getInstance().getGameBoard().getPossibleMoves().isEmpty()) {
                if (sameLevel) { // MAX CON VARIOS MOVS
                    boolean firstEntry = true;
                    LinkedHashSet<Move> possibleMoves = (LinkedHashSet<Move>) App.getInstance().getGameBoard().getPossibleMoves().clone();
                    for (Move move : possibleMoves) {
                        boolean timeFlag = false;
                        int auxMovesCount = movesCount;
                        movesCount++;
                        App.getInstance().getCurrentPlayer().makeMove(move);
                        MoveState currentMoveState;
                        if (firstEntry) {
                            currentMoveState = previousMoveState.children.getLast();
                            firstEntry = false;
                        } else {
                            currentMoveState = new MoveState(true);
                            LinkedList<Move> moves = previousMoveState.children.getLast().moves;
                            for (int i = 0; i < moves.size() - 1; i++) {
                                currentMoveState.moves.add(moves.get(i));
                            }
                            previousMoveState.children.add(currentMoveState);
                        }
                        currentMoveState.moves.add(move);
                        if (this == App.getInstance().getCurrentPlayer()) {
                            minimaxTimeRec(movesCount, previousMoveState, true, true, secondsMax);
                        } else {
                            if (previousMoveState.chosen == null) {
                                previousMoveState.chosen = currentMoveState;
                            }

                            long timeSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                            if (timeSeconds < secondsMax) {
                                currentMoveState.value = minimaxTimeRec(0, currentMoveState, false, false, secondsMax);
                            } else {
                                timeFlag = true;
                                currentMoveState.value = ((AIPlayer) App.getInstance().getNotCurrentPlayer()).heuristicValue();
                            }
                            if (previousMoveState.chosen.value < currentMoveState.value) {
                                previousMoveState.chosen = currentMoveState;
                            }
                        }
                        while (movesCount > auxMovesCount) {
                            App.getInstance().getGameBoard().undoLastMove();
                            movesCount--;
                        }
                        if (timeFlag) {
                            return previousMoveState.chosen.value;
                        }
                    }
                } else { // MAX CON UN SOLO MOV
                    LinkedHashSet<Move> possibleMoves = (LinkedHashSet<Move>) App.getInstance().getGameBoard().getPossibleMoves().clone();
                    for (Move move : possibleMoves) {
                        boolean timeFlag = false;
                        movesCount++;
                        App.getInstance().getCurrentPlayer().makeMove(move);
                        MoveState currentMoveState = new MoveState(true);
                        currentMoveState.moves.add(move);
                        previousMoveState.children.add(currentMoveState);
                        if (this == App.getInstance().getCurrentPlayer()) {
                            minimaxTimeRec(movesCount, previousMoveState, true, true, secondsMax);
                        } else {
                            if (previousMoveState.chosen == null) {
                                previousMoveState.chosen = currentMoveState;
                            }
                            long timeSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                            if (timeSeconds < secondsMax) {
                                currentMoveState.value = minimaxTimeRec(0, currentMoveState, false, false, secondsMax);
                            } else {
                                timeFlag = true;
                                currentMoveState.value = ((AIPlayer) App.getInstance().getNotCurrentPlayer()).heuristicValue();
                            }
                            if (previousMoveState.chosen.value < currentMoveState.value) {
                                previousMoveState.chosen = currentMoveState;
                            }
                        }
                        while (movesCount > 0) {
                            App.getInstance().getGameBoard().undoLastMove();
                            movesCount--;
                        }
                        if (timeFlag) {
                            return previousMoveState.chosen.value;
                        }
                    }
                }
            } else {
                MoveState currentMoveState = previousMoveState.children.getLast();
                if (previousMoveState.chosen == null)
                    previousMoveState.chosen = currentMoveState;
                currentMoveState.value = ((AIPlayer) App.getInstance().getCurrentPlayer()).heuristicValue();
                if (previousMoveState.chosen.value < currentMoveState.value)
                    previousMoveState.chosen = currentMoveState;
            }
        } else { // MIN
            if (!App.getInstance().getGameBoard().getPossibleMoves().isEmpty()) {
                if (sameLevel) { //MIN CON VARIOS MOVS
                    boolean firstEntry = true;
                    LinkedHashSet<Move> possibleMoves = (LinkedHashSet<Move>) App.getInstance().getGameBoard().getPossibleMoves().clone();
                    for (Move move : possibleMoves) {
                        boolean timeFlag = false;
                        int auxMovesCount = movesCount;
                        movesCount++;
                        App.getInstance().getCurrentPlayer().makeMove(move);
                        MoveState currentMoveState;
                        if (firstEntry) {
                            currentMoveState = previousMoveState.children.getLast();
                            firstEntry = false;
                        } else {
                            currentMoveState = new MoveState(false);
                            LinkedList<Move> moves = previousMoveState.children.getLast().moves;
                            for (int i = 0; i < moves.size() - 1; i++)
                                currentMoveState.moves.add(moves.get(i));
                            previousMoveState.children.add(currentMoveState);
                        }
                        currentMoveState.moves.add(move);
                        if (this != App.getInstance().getCurrentPlayer()) {
                            minimaxTimeRec(movesCount, previousMoveState, true, false, secondsMax);
                        } else {
                            if (previousMoveState.chosen == null) {
                                previousMoveState.chosen = currentMoveState;
                            }
                            if (prune) {
                                currentMoveState.value = ((AIPlayer) App.getInstance().getCurrentPlayer()).heuristicValue();
                                if (previousMoveState.chosen != currentMoveState && previousMoveState.chosen.value <= currentMoveState.value) {
                                    currentMoveState.pruned = true;
                                }
                                if (!currentMoveState.pruned) {
                                    long timeSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                                    if (timeSeconds < secondsMax) {
                                        currentMoveState.value = minimaxTimeRec(0, currentMoveState, false, true, secondsMax);
                                    } else {
                                        timeFlag = true;
                                    }
                                    if (previousMoveState.chosen.value > currentMoveState.value) {
                                        previousMoveState.chosen = currentMoveState;
                                    }
                                }
                            } else {
                                long timeSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                                if (timeSeconds < secondsMax) {
                                    currentMoveState.value = minimaxTimeRec(0, currentMoveState, false, true, secondsMax);
                                } else {
                                    timeFlag = true;
                                    currentMoveState.value = ((AIPlayer) App.getInstance().getCurrentPlayer()).heuristicValue();
                                }
                                if (previousMoveState.chosen.value > currentMoveState.value) {
                                    previousMoveState.chosen = currentMoveState;
                                }
                            }
                        }
                        while (movesCount > auxMovesCount) {
                            App.getInstance().getGameBoard().undoLastMove();
                            movesCount--;
                        }
                        if (timeFlag) {
                            return previousMoveState.chosen.value;
                        }
                    }
                } else { //MIN CON UN SOLO MOV
                    LinkedHashSet<Move> possibleMoves = (LinkedHashSet<Move>) App.getInstance().getGameBoard().getPossibleMoves().clone();
                    for (Move move : possibleMoves) {
                        boolean timeFlag = false;
                        movesCount++;
                        App.getInstance().getCurrentPlayer().makeMove(move);
                        MoveState currentMoveState = new MoveState(false);
                        currentMoveState.moves.add(move);
                        previousMoveState.children.add(currentMoveState);
                        if (this != App.getInstance().getCurrentPlayer()) {
                            minimaxTimeRec(movesCount, previousMoveState, true, false, secondsMax);
                        } else {
                            if (previousMoveState.chosen == null) {
                                previousMoveState.chosen = currentMoveState;
                            }
                            if (prune) {
                                currentMoveState.value = ((AIPlayer) App.getInstance().getCurrentPlayer()).heuristicValue();
                                if (previousMoveState.chosen != currentMoveState && previousMoveState.chosen.value <= currentMoveState.value) {
                                    currentMoveState.pruned = true;
                                }
                                if (!currentMoveState.pruned) {
                                    long timeSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                                    if (timeSeconds < secondsMax) {
                                        currentMoveState.value = minimaxTimeRec(0, currentMoveState, false, true, secondsMax);
                                    }
                                    if (previousMoveState.chosen.value > currentMoveState.value)
                                        previousMoveState.chosen = currentMoveState;
                                }
                            } else {
                                long timeSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                                if (timeSeconds < secondsMax) {
                                    currentMoveState.value = minimaxTimeRec(0, currentMoveState, false, true, secondsMax);
                                } else {
                                    timeFlag = true;
                                    currentMoveState.value = ((AIPlayer) App.getInstance().getCurrentPlayer()).heuristicValue();
                                }
                                if (previousMoveState.chosen.value > currentMoveState.value) {
                                    previousMoveState.chosen = currentMoveState;
                                }
                            }
                        }
                        while (movesCount > 0) {
                            App.getInstance().getGameBoard().undoLastMove();
                            movesCount--;
                        }
                        if (timeFlag) {
                            return previousMoveState.chosen.value;
                        }
                    }
                }
            } else {
                MoveState currentMoveState = previousMoveState.children.getLast();
                if (previousMoveState.chosen == null)
                    previousMoveState.chosen = currentMoveState;
                currentMoveState.value = ((AIPlayer) App.getInstance().getCurrentPlayer()).heuristicValue();
                if (previousMoveState.chosen.value > currentMoveState.value)
                    previousMoveState.chosen = currentMoveState;
            }
        }
        return previousMoveState.chosen.value;
    }

    /**
     * Heuristic for the minimax
     *
     * @return int heuristic value
     */
    private int heuristicValue() {
        if (App.getInstance().getGameBoard().isOver()) {
            if (getOtherPlayer().getPoints() > points) {
                return beta;
            } else if (getOtherPlayer().getPoints() < points) {
                return alpha;
            } else {
                return 0;
            }
        }
        return points - getOtherPlayer().getPoints();
    }

    /**
     * Method used for the heuristic value that returns the player that currently does not have to play
     *
     * @return Player
     */
    private Player getOtherPlayer() {
        if (App.getInstance().getCurrentPlayer() == this) {
            return App.getInstance().getNotCurrentPlayer();
        }
        return App.getInstance().getCurrentPlayer();
    }

    /**
     * Method to create the last minimax in .dot format
     *
     * @param fileName name of the file
     * @return true if create, false if not
     */
    public boolean makeDotFile(String fileName) {
        if (lastMoveState == null) {
            return false;
        }
        PrintWriter writer;
        File file = new File(System.getProperty("user.dir") + "/target/" + fileName + ".dot");
        try {
            writer = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            return false;
        }
        if (writer == null) {
            return false;
        }

        int nodeNumber = 1;

        writer.println("digraph {");
        writer.println();

        writer.println("START [shape=box, style=filled, fillcolor=coral1]");
        for (MoveState moveState : lastMoveState.children) {
            String moveString = moveState.toString();
            if (moveState == lastMoveState.chosen) {
                writer.println(nodeNumber + "[label=\"" + moveString + "\" , shape=oval, style=filled, fillcolor=coral1]");
                writer.println("START -> " + nodeNumber);
            } else {
                writer.println(nodeNumber + "[label=\"" + moveString + "\" , shape=oval, style=filled, fillcolor=white]");
                writer.println("START -> " + nodeNumber);
            }
            nodeNumber = makeDotFileRec(writer, moveState, nodeNumber);
        }

        writer.println();
        writer.println("}");
        writer.close();
        return true;
    }

    /**
     * Recurrence for the dot file
     *
     * @param writer     writer
     * @param moveState  current MoveState
     * @param nodeNumber current node index
     * @return int of how many nodes were printed
     */
    private int makeDotFileRec(PrintWriter writer, MoveState moveState, int nodeNumber) {
        int nodeNumberFrom = nodeNumber++;
        for (MoveState moveStateChild : moveState.children) {
            String moveStringTo = moveStateChild.toString();
            if (moveStateChild == moveState.chosen) {
                if (moveStateChild.isMax) {
                    writer.println(nodeNumber + "[label=\"" + moveStringTo + "\" ,shape=oval, style=filled, fillcolor=coral1]");
                    writer.println(nodeNumberFrom + " -> " + nodeNumber);
                } else {
                    writer.println(nodeNumber + "[label=\"" + moveStringTo + "\" ,shape=box, style=filled, fillcolor=coral1]");
                    writer.println(nodeNumberFrom + " -> " + nodeNumber);
                }
            } else {
                if (moveStateChild.isMax) {
                    writer.println(nodeNumber + "[label=\"" + moveStringTo + "\" , shape=oval, style=filled, fillcolor=white]");
                    writer.println(nodeNumberFrom + " -> " + nodeNumber);
                } else {
                    if (moveStateChild.pruned) {
                        writer.println(nodeNumber + "[label=\"" + moveStringTo + "\" , shape=box, style=filled, fillcolor=gray76]");
                        writer.println(nodeNumberFrom + " -> " + nodeNumber);
                    } else {
                        writer.println(nodeNumber + "[label=\"" + moveStringTo + "\" , shape=box, style=filled, fillcolor=white]");
                        writer.println(nodeNumberFrom + " -> " + nodeNumber);
                    }
                }
            }
            nodeNumber = makeDotFileRec(writer, moveStateChild, nodeNumber);
        }
        return nodeNumber;
    }

    /**
     * Class used to save all the recurrences of the minimax search, used to create dot file
     */
    private class MoveState {
        private LinkedList<Move> moves;
        private Integer value;
        private boolean pruned;
        private boolean isMax;
        private MoveState chosen;
        private LinkedList<MoveState> children;

        public MoveState(boolean isMax) {
            this.moves = new LinkedList<>();
            this.value = null;
            this.pruned = false;
            this.chosen = null;
            this.isMax = isMax;
            this.children = new LinkedList<>();
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            result.append("{");
            for (Move move : moves) {
                result.append("[" + move.toString() + "] ");
            }
            result.append(value + "}");
            return result.toString();
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(points);
        out.writeInt(aiMode);
        out.writeInt(aiModeParam);
        out.writeBoolean(prune);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        points = ois.readInt();
        aiMode = ois.readInt();
        aiModeParam = ois.readInt();
        prune = ois.readBoolean();
    }
}