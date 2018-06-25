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
        if (aiMode == 0) {
            lastMoveState.gamePhase = game.deepClone();
            maxTime = System.currentTimeMillis() + aiModeParam;
            minimaxTimeRec(lastMoveState);
            refreshBestMove(lastMoveState);
        } else {
            minimaxDepthRec(lastMoveState, 0, (int)aiModeParam, new LinkedHashSet<>(game.getBoard().getPossibleMoves()));
        }
        System.out.println(lastMoveState.chosenChild);
        return lastMoveState.chosenChild.moves;
    }

    private void minimaxDepthRec(MoveState previousMoveState, int depth, int maxDepth, LinkedHashSet<Move> possibleMoves) {
        Player player = game.getCurrentPlayer();
        if (!previousMoveState.isMax) {
            for(Move move : possibleMoves){
                game.getCurrentPlayer().makeMove(move);
                MoveState currentMoveState = new MoveState(true);
                currentMoveState.moves.add(move);
                previousMoveState.children.add(currentMoveState);
                if (player == game.getCurrentPlayer() && !game.getBoard().isOver()) {
                    LinkedHashSet<Move> nextPossibleMoves = new LinkedHashSet<>(possibleMoves);
                    nextPossibleMoves.remove(move);
                    minimaxDepthMultipleMovesRec(previousMoveState, depth, maxDepth, nextPossibleMoves);
                } else {
                    currentMoveState.value = heuristicValue();
                    if (previousMoveState.chosenChild == null) {
                        previousMoveState.value = currentMoveState.value;
                        previousMoveState.chosenChild = currentMoveState;
                    }
                    if (depth < maxDepth && !game.getBoard().isOver()) {
                        LinkedHashSet<Move> nextPossibleMoves = new LinkedHashSet<>(possibleMoves);
                        nextPossibleMoves.removeAll(currentMoveState.moves);
                        minimaxDepthRec(currentMoveState, depth + 1, maxDepth, nextPossibleMoves);
                    }
                    if (previousMoveState.chosenChild.value < currentMoveState.value) {
                        previousMoveState.chosenChild = currentMoveState;
                        previousMoveState.value = currentMoveState.value;
                    }
                }
                game.getBoard().undoLastMove();
            }
        }
        else {
            for(Move move : possibleMoves){
                game.getCurrentPlayer().makeMove(move);
                MoveState currentMoveState = new MoveState(false);
                currentMoveState.moves.add(move);
                previousMoveState.children.add(currentMoveState);
                if (player == game.getCurrentPlayer() && !game.getBoard().isOver()) {
                    LinkedHashSet<Move> nextPossibleMoves = new LinkedHashSet<>(possibleMoves);
                    nextPossibleMoves.remove(move);
                    minimaxDepthMultipleMovesRec(previousMoveState, depth, maxDepth, nextPossibleMoves);
                } else {
                    currentMoveState.value = heuristicValue();
                    if (previousMoveState.chosenChild == null) {
                        previousMoveState.chosenChild = currentMoveState;
                        previousMoveState.value = currentMoveState.value;
                    }
                    if (prune) {
                        if (previousMoveState.chosenChild != currentMoveState && previousMoveState.chosenChild.value < currentMoveState.value) {
                            currentMoveState.pruned = true;
                        }
                        if (!currentMoveState.pruned) {
                            if (!game.getBoard().isOver() && depth < maxDepth) {
                                LinkedHashSet<Move> nextPossibleMoves = new LinkedHashSet<>(possibleMoves);
                                nextPossibleMoves.removeAll(currentMoveState.moves);
                                minimaxDepthRec(currentMoveState, depth + 1, maxDepth, nextPossibleMoves);
                            }
                            if (previousMoveState.chosenChild.value > currentMoveState.value){
                                previousMoveState.chosenChild = currentMoveState;
                                previousMoveState.value = currentMoveState.value;
                            }
                        }
                    } else {
                        if (depth < maxDepth && !game.getBoard().isOver()) {
                            LinkedHashSet<Move> nextPossibleMoves = new LinkedHashSet<>(possibleMoves);
                            nextPossibleMoves.removeAll(currentMoveState.moves);
                            minimaxDepthRec(currentMoveState, depth + 1, maxDepth, nextPossibleMoves);
                        }
                        if (previousMoveState.chosenChild.value > currentMoveState.value) {
                            previousMoveState.chosenChild = currentMoveState;
                            previousMoveState.value = currentMoveState.value;
                        }
                    }
                }
                game.getBoard().undoLastMove();
            }
        }
    }

    private void minimaxDepthMultipleMovesRec(MoveState previousMoveState, int depth, int maxDepth, LinkedHashSet<Move> possibleMoves){
        boolean firstEntry = true;
        Player player = game.getCurrentPlayer();
        if (!previousMoveState.isMax) {
            for(Move move : possibleMoves){
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
                if (player == game.getCurrentPlayer() && !game.getBoard().isOver()) {
                    LinkedHashSet<Move> nextPossibleMoves = new LinkedHashSet<>(possibleMoves);
                    nextPossibleMoves.remove(move);
                    minimaxDepthMultipleMovesRec(previousMoveState, depth, maxDepth, nextPossibleMoves);
                } else {
                    currentMoveState.value = heuristicValue();
                    if (previousMoveState.chosenChild == null) {
                        previousMoveState.chosenChild = currentMoveState;
                        previousMoveState.value = currentMoveState.value;
                    }
                    if (depth < maxDepth && !game.getBoard().isOver()) {
                        LinkedHashSet<Move> nextPossibleMoves = new LinkedHashSet<>(possibleMoves);
                        nextPossibleMoves.removeAll(currentMoveState.moves);
                        minimaxDepthRec(currentMoveState, depth + 1, maxDepth, nextPossibleMoves);
                    }
                    if (previousMoveState.chosenChild.value < currentMoveState.value) {
                        previousMoveState.chosenChild = currentMoveState;
                        previousMoveState.value = currentMoveState.value;
                    }
                }
                game.getBoard().undoLastMove();
            }
        }
        else {
            for(Move move : possibleMoves){
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
                if (player == game.getCurrentPlayer() && !game.getBoard().isOver()) {
                    LinkedHashSet<Move> nextPossibleMoves = new LinkedHashSet<>(possibleMoves);
                    nextPossibleMoves.remove(move);
                    minimaxDepthMultipleMovesRec(previousMoveState, depth, maxDepth, nextPossibleMoves);
                } else {
                    currentMoveState.value = heuristicValue();
                    if (previousMoveState.chosenChild == null) {
                        previousMoveState.chosenChild = currentMoveState;
                        previousMoveState.value = currentMoveState.value;
                    }
                    if (prune) {
                        if (previousMoveState.chosenChild != currentMoveState && previousMoveState.chosenChild.value < currentMoveState.value) {
                            currentMoveState.pruned = true;
                        }
                        if (!currentMoveState.pruned) {
                            if (depth < maxDepth && !game.getBoard().isOver()) {
                                LinkedHashSet<Move> nextPossibleMoves = new LinkedHashSet<>(possibleMoves);
                                nextPossibleMoves.removeAll(currentMoveState.moves);
                                minimaxDepthRec(currentMoveState, depth + 1, maxDepth, nextPossibleMoves);
                            }
                            if (previousMoveState.chosenChild.value > currentMoveState.value) {
                                previousMoveState.chosenChild = currentMoveState;
                                previousMoveState.value = currentMoveState.value;
                            }
                        }
                    } else {
                        if (depth < maxDepth && !game.getBoard().isOver()) {
                            LinkedHashSet<Move> nextPossibleMoves = new LinkedHashSet<>(possibleMoves);
                            nextPossibleMoves.removeAll(currentMoveState.moves);
                            minimaxDepthRec(currentMoveState, depth + 1, maxDepth, nextPossibleMoves);
                        }
                        if (previousMoveState.chosenChild.value > currentMoveState.value) {
                            previousMoveState.chosenChild = currentMoveState;
                            previousMoveState.value = currentMoveState.value;
                        }
                    }
                }
                game.getBoard().undoLastMove();
            }
        }
    }

    private void minimaxLevel(MoveState previousMoveState){
        Game gamePhase = previousMoveState.gamePhase;
        Iterator<Move> iterator = ((LinkedHashSet<Move>) gamePhase.getBoard().getPossibleMoves().clone()).iterator();
        Player player = gamePhase.getCurrentPlayer();
        if (!previousMoveState.isMax) {
            while(iterator.hasNext()){
                Move move = iterator.next();
                player.makeMove(move);
                MoveState currentMoveState = new MoveState(true);
                currentMoveState.moves.add(move);
                previousMoveState.children.add(currentMoveState);
                if (player == gamePhase.getCurrentPlayer() && !gamePhase.getBoard().isOver()) {
                    minimaxLevelMultipleMoves(previousMoveState);
                } else {
                    currentMoveState.gamePhase = gamePhase.deepClone();
                    currentMoveState.value = player.heuristicValue();
                }
                gamePhase.getBoard().undoLastMove();
            }
        }
        else {
            while(iterator.hasNext()){
                Move move = iterator.next();
                player.makeMove(move);
                MoveState currentMoveState = new MoveState(false);
                currentMoveState.moves.add(move);
                previousMoveState.children.add(currentMoveState);
                if (player == gamePhase.getCurrentPlayer() && !gamePhase.getBoard().isOver()) {
                    minimaxLevelMultipleMoves(previousMoveState);
                } else {
                    currentMoveState.gamePhase = gamePhase.deepClone();
                    currentMoveState.value = player.getOpposingPlayer().heuristicValue();
                }
                gamePhase.getBoard().undoLastMove();
            }
        }
    }

    private void minimaxLevelMultipleMoves(MoveState previousMoveState) {
        Game gamePhase = previousMoveState.gamePhase;
        Iterator<Move> iterator = ((LinkedHashSet<Move>) gamePhase.getBoard().getPossibleMoves().clone()).iterator();
        boolean firstEntry = true;
        Player player = gamePhase.getCurrentPlayer();
        if (!previousMoveState.isMax) {
            while(iterator.hasNext() && hasTime()){
                Move move = iterator.next();
                player.makeMove(move);
                MoveState currentMoveState;
                if (firstEntry) {
                    currentMoveState = previousMoveState.children.getLast();
                    firstEntry = false;
                } else {
                    currentMoveState = new MoveState(true);
                    LinkedList<Move> moves = previousMoveState.children.getLast().moves;
                    for (int i = 0; i < moves.size() - 1; i++)
                        currentMoveState.moves.add(moves.get(i));
                    previousMoveState.children.add(currentMoveState);
                }
                currentMoveState.moves.add(move);
                if (player == gamePhase.getCurrentPlayer() && !gamePhase.getBoard().isOver()) {
                    minimaxLevelMultipleMoves(previousMoveState);
                } else {
                    currentMoveState.gamePhase = gamePhase.deepClone();
                    currentMoveState.value = player.heuristicValue();
                }
                gamePhase.getBoard().undoLastMove();
            }
        }
        else {
            while(iterator.hasNext() && hasTime()){
                Move move = iterator.next();
                player.makeMove(move);
                MoveState currentMoveState;
                if (firstEntry) {
                    currentMoveState = previousMoveState.children.getLast();
                    firstEntry = false;
                } else {
                    currentMoveState = new MoveState(false);
                    LinkedList<Move> moves = previousMoveState.children.getLast().moves;
                    for (int i = 0; i < moves.size() - 1; i++) {
                        currentMoveState.moves.add(moves.get(i));
                    }
                    previousMoveState.children.add(currentMoveState);
                }
                currentMoveState.moves.add(move);
                if (player == gamePhase.getCurrentPlayer() && !gamePhase.getBoard().isOver()) {
                    minimaxLevelMultipleMoves(previousMoveState);
                } else {
                    currentMoveState.gamePhase = gamePhase.deepClone();
                    currentMoveState.value = player.getOpposingPlayer().heuristicValue();
                }
                gamePhase.getBoard().undoLastMove();
            }
        }
    }

    private void minimaxTimeRec(MoveState rootMoveState) {
        minimaxLevel(rootMoveState);
        Deque<MoveState> deque= new LinkedList<>();
        deque.addAll(rootMoveState.children);
        while(hasTime() && !deque.isEmpty()){
            MoveState currentMoveState = deque.poll();
            minimaxTimeRec(currentMoveState);
        }
    }

    private void refreshBestMove(MoveState rootMoveState) {
        if(rootMoveState.children.isEmpty()){
            return;
        }
        for(MoveState child : rootMoveState.children){
            if(!child.children.isEmpty()){
                refreshBestMove(child);
            }
            if(rootMoveState.chosenChild == null){
                rootMoveState.chosenChild = child;
                rootMoveState.value = child.value;
            }
            else{
                if(rootMoveState.isMax) {
                    if (rootMoveState.chosenChild.value > child.value) {
                        rootMoveState.chosenChild = child;
                        rootMoveState.value = child.value;
                    }
                }
                else{
                    if (rootMoveState.chosenChild.value < child.value) {
                        rootMoveState.chosenChild = child;
                        rootMoveState.value = child.value;
                    }
                }
            }
        }
    }

    private boolean hasTime(){
        return System.currentTimeMillis() < maxTime;
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

        MoveState( boolean isMax) {
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