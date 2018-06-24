package DAB.DotsAndBoxes.model;

import java.io.*;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class AIPlayer extends Player implements Serializable {

    private static final long serialVersionUID = 1L;

    private int aiMode;
    private long aiModeParam;
    private boolean prune;

    private transient MoveState lastMoveState;
    private transient long maxTime;

    AIPlayer(Game game, int aiMode, int aiModeParam, boolean prune, int points) {
        super(game, points);
        this.aiMode = aiMode;
        if(aiMode == 0) {
            this.aiModeParam = (long) aiModeParam * 1000; // saved in milliseconds
        }
        else{
            this.aiModeParam = (long) aiModeParam;
        }
        this.prune = prune;
        this.lastMoveState = null;
    }

    AIPlayer(Game game, int aiMode, int aiModeParam, boolean prune) {
        this(game, aiMode, aiModeParam, prune, 0);
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
        lastMoveState.gamePhase = game.deepClone();
        if (aiMode == 0) {
            maxTime = System.currentTimeMillis() + aiModeParam;
            minimaxTimeRec(lastMoveState);
        } else {
            minimaxDepthRec(lastMoveState, 0, (int)aiModeParam, true);
        }
        System.out.println(lastMoveState.chosenChild);
        return lastMoveState.chosenChild.moves;
    }

    private int minimaxDepthRec(MoveState previousMoveState, int depth, int maxDepth, boolean maxOrMin) {
        Game game = previousMoveState.gamePhase;
        Iterator<Move> iterator = ((LinkedHashSet<Move>) game.getBoard().getPossibleMoves().clone()).iterator();
        Player player;
        if (maxOrMin) {
            player = game.getCurrentPlayer();
            while(iterator.hasNext() && canKeepGoing()) {
                Player maxPlayer = game.getCurrentPlayer();
                Move move = iterator.next();
                game.getCurrentPlayer().makeMove(move);
                MoveState currentMoveState = new MoveState(true);
                currentMoveState.moves.add(move);
                previousMoveState.children.add(currentMoveState);
                if (maxPlayer == game.getCurrentPlayer() && !game.getBoard().isOver()) {
                    minimaxDepthMultipleMovesRec(previousMoveState, depth, maxDepth, true);
                } else {
                    currentMoveState.gamePhase = game.deepClone();
                    currentMoveState.value = maxPlayer.heuristicValue();
                    if (previousMoveState.chosenChild == null) {
                        previousMoveState.chosenChild = currentMoveState;
                    }
                    if (depth < maxDepth && canKeepGoing() && !game.getBoard().isOver()) {
                        currentMoveState.value = minimaxDepthRec(currentMoveState, depth + 1, maxDepth, false);
                    }
                    if (previousMoveState.chosenChild.value < currentMoveState.value)
                        previousMoveState.chosenChild = currentMoveState;
                }
                game.getBoard().undoLastMove();
            }
        }
        else {
            player = game.getCurrentPlayer();
            while(iterator.hasNext() && canKeepGoing()) {
                Player minPlayer = game.getCurrentPlayer();
                Move move = iterator.next();
                game.getCurrentPlayer().makeMove(move);
                MoveState currentMoveState = new MoveState(false);
                currentMoveState.moves.add(move);
                previousMoveState.children.add(currentMoveState);
                if (minPlayer == game.getCurrentPlayer() && !game.getBoard().isOver()) {
                    minimaxDepthMultipleMovesRec(previousMoveState, depth, maxDepth, false);
                } else {
                    if (previousMoveState.chosenChild == null) {
                        previousMoveState.chosenChild = currentMoveState;
                    }
                    currentMoveState.gamePhase = game.deepClone();
                    currentMoveState.value = minPlayer.getOpposingPlayer().heuristicValue();
                    if (prune) {
                        if (previousMoveState.chosenChild != currentMoveState && previousMoveState.chosenChild.value < currentMoveState.value) {
                            currentMoveState.pruned = true;
                        }
                        if (!currentMoveState.pruned) {
                            if (depth < maxDepth && canKeepGoing() && !game.getBoard().isOver()) {
                                currentMoveState.value = minimaxDepthRec(currentMoveState, depth + 1, maxDepth, true);
                            }
                            if (previousMoveState.chosenChild.value > currentMoveState.value)
                                previousMoveState.chosenChild = currentMoveState;
                        }
                    } else {
                        if (depth < maxDepth && canKeepGoing() && !game.getBoard().isOver()) {
                            currentMoveState.value = minimaxDepthRec(currentMoveState, depth + 1, maxDepth, true);
                        }
                        if (previousMoveState.chosenChild.value > currentMoveState.value) {
                            previousMoveState.chosenChild = currentMoveState;
                        }
                    }
                }
                game.getBoard().undoLastMove();
            }
        }
        if(previousMoveState.chosenChild != null)
            return previousMoveState.chosenChild.value;
        else
            return player.heuristicValue();
    }

    private void minimaxDepthMultipleMovesRec(MoveState previousMoveState, int depth, int maxDepth, boolean maxOrMin){
        Game game = previousMoveState.gamePhase;
        Iterator<Move> iterator = ((LinkedHashSet<Move>) game.getBoard().getPossibleMoves().clone()).iterator();
        boolean firstEntry = true;
        if (maxOrMin) {
            while(iterator.hasNext() && canKeepGoing()) {
                Player maxPlayer = game.getCurrentPlayer();
                Move move = iterator.next();
                game.getCurrentPlayer().makeMove(move);
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
                if (maxPlayer == game.getCurrentPlayer() && !game.getBoard().isOver()) {
                    minimaxDepthMultipleMovesRec(previousMoveState, depth, maxDepth, true);
                } else {
                    currentMoveState.gamePhase = game.deepClone();
                    currentMoveState.value = maxPlayer.heuristicValue();
                    if (previousMoveState.chosenChild == null) {
                        previousMoveState.chosenChild = currentMoveState;
                    }
                    if (depth < maxDepth && canKeepGoing() && !game.getBoard().isOver()) {
                        currentMoveState.value = minimaxDepthRec(currentMoveState, depth + 1, maxDepth, false);
                    }
                    if (previousMoveState.chosenChild.value < currentMoveState.value) {
                        previousMoveState.chosenChild = currentMoveState;
                    }
                }
                game.getBoard().undoLastMove();
            }
        }
        else {
            while(iterator.hasNext() && canKeepGoing()) {
                Player minPlayer = game.getCurrentPlayer();
                Move move = iterator.next();
                game.getCurrentPlayer().makeMove(move);
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
                if (minPlayer == game.getCurrentPlayer() && !game.getBoard().isOver()) {
                    minimaxDepthMultipleMovesRec(previousMoveState, depth, maxDepth, false);
                } else {
                    if (previousMoveState.chosenChild == null) {
                        previousMoveState.chosenChild = currentMoveState;
                    }
                    currentMoveState.gamePhase = game.deepClone();
                    currentMoveState.value = minPlayer.getOpposingPlayer().heuristicValue();
                    if (prune) {
                        if (previousMoveState.chosenChild != currentMoveState && previousMoveState.chosenChild.value < currentMoveState.value) {
                            currentMoveState.pruned = true;
                        }
                        if (!currentMoveState.pruned) {
                            if (depth < maxDepth && canKeepGoing() && !game.getBoard().isOver()) {
                                currentMoveState.value = minimaxDepthRec(currentMoveState, depth + 1, maxDepth, true);
                            }
                            if (previousMoveState.chosenChild.value > currentMoveState.value) {
                                previousMoveState.chosenChild = currentMoveState;
                            }
                        }
                    } else {
                        if (depth < maxDepth && canKeepGoing() && !game.getBoard().isOver()) {
                            currentMoveState.value = minimaxDepthRec(currentMoveState, depth + 1, maxDepth, true);
                        }
                        if (previousMoveState.chosenChild.value > currentMoveState.value) {
                            previousMoveState.chosenChild = currentMoveState;
                        }
                    }
                }
                game.getBoard().undoLastMove();
            }
        }
    }

    private void minimaxTimeRec(MoveState rootMoveState) {
        Deque<MoveState> deque = new LinkedList<>();
        if(!rootMoveState.isMax)
            minimaxDepthRec(rootMoveState, 0, 0, true);
        else
            minimaxDepthRec(rootMoveState, 0, 0, false);
        deque.addAll(rootMoveState.children);
        while(canKeepGoing() && !deque.isEmpty()){
            MoveState currentMoveState = deque.getFirst();
            minimaxTimeRec(currentMoveState);
        }
    }

    private boolean canKeepGoing(){
        if(aiMode == 0){ //time
            return System.currentTimeMillis() < maxTime;
        }
        else{ //depth
            return true;
        }
    }

    /**
     * Method to create the last minimax in .dot format
     *
     * @param fileName name of the file
     * @return true if create, false if not
     */
    boolean makeDotFile(String fileName) {
        if (lastMoveState == null) {
            return false;
        }
        PrintWriter writer;
        try {
            writer = new PrintWriter(new File(System.getProperty("user.dir") + "/target/" + fileName + ".dot"));
        } catch (FileNotFoundException e) {
            return false;
        }

        int nodeNumber = 1;

        writer.println("digraph {");
        writer.println();

        writer.println("START [shape=box, style=filled, fillcolor=coral1]");
        for (MoveState moveState : lastMoveState.children) {
            String moveString = moveState.toString();
            if (moveState == lastMoveState.chosenChild) {
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
            if (moveStateChild == moveState.chosenChild) {
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
        private Game gamePhase;
        private MoveState chosenChild;
        private LinkedList<MoveState> children;

        MoveState(boolean isMax) {
            this.moves = new LinkedList<>();
            this.value = null;
            this.pruned = false;
            this.isMax = isMax;
            this.gamePhase = null;
            this.chosenChild = null;
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
        out.writeObject(game);
        out.writeInt(points);
        out.writeInt(aiMode);
        out.writeLong(aiModeParam);
        out.writeBoolean(prune);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        game = (Game) ois.readObject();
        points = ois.readInt();
        aiMode = ois.readInt();
        aiModeParam = ois.readLong();
        prune = ois.readBoolean();
    }
}