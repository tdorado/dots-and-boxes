package DAB.DotsAndBoxes.view;

import DAB.DotsAndBoxes.App;
import DAB.DotsAndBoxes.model.*;
import DAB.DotsAndBoxes.model.exceptions.DotCreationException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import java.io.IOException;
import java.util.List;

public class BoardPane extends Pane {

    static final int START_POSITION = 10;
    static final int DISTANCE = 30;
    private static final int RADIUS = 6;
    private static final int LINE_EXTEND = 4;
    private static final int LINE_DISTANCE = 18;
    private TextField textField = new TextField("Insert name for the file");
    private Button saveGameButton = new Button("SAVE GAME");
    private Button createDotFileButton = new Button("DOT FILE");
    private Button undoMoveButton = new Button("UNDO MOVE");
    private Button nextTurnButton = new Button("NEXT TURN(ONLY AI)");
    private Text currentTurnText = new Text(650, 275, "0");
    private Text pointsText = new Text(650, 300, "Points:");
    private Text player1PointsText = new Text(650, 325, "0");
    private Text player2PointsText = new Text(650, 350, "0");
    private Text errorFileText = new Text(650, 375, "Error while creating file");
    private Text gameEndedText = new Text(650, 400, "Game finished");
    private Text aiMoveText = new Text(650, 400, "AI calculating next moves");

    public BoardPane() {
        initializeBoard();
    }

    private void setTexts() {
        player1PointsText.setText("Player 1: " + App.getInstance().getPlayer1().getPoints());
        player2PointsText.setText("Player 2: " + App.getInstance().getPlayer2().getPoints());
        if(App.getInstance().getCurrentPlayer() == App.getInstance().getPlayer1()) {
            currentTurnText.setFill(Color.RED);
            if(App.getInstance().getPlayer1().isAI()) {
                currentTurnText.setText("Turn: Player 1(AI)");
            }
            else{
                currentTurnText.setText("Turn: Player 1");
            }
        }
        else{
            currentTurnText.setFill(Color.BLUE);
            if(App.getInstance().getPlayer2().isAI()) {
                currentTurnText.setText("Turn: Player 2(AI)");
            }
            else{
                currentTurnText.setText("Turn: Player 2");
            }
        }
        if(!App.getInstance().getBoard().isOver()){
            gameEndedText.setVisible(false);
        }
        else{
            gameEndedText.setVisible(true);
        }
        errorFileText.setVisible(false);
        aiMoveText.setVisible(false);
    }

    private void initializeBoard() {
        setTexts();

        player1PointsText.setFill(Color.RED);
        player2PointsText.setFill(Color.BLUE);

        textField.setPrefSize(150, 25);
        textField.setLayoutX(650);
        textField.setLayoutY(25);

        saveGameButton.setDefaultButton(true);
        saveGameButton.setPrefSize(150, 25);
        saveGameButton.setLayoutX(650);
        saveGameButton.setLayoutY(75);
        saveGameButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String fileName = textField.getText();
                if(!isFileNameValid(fileName)){
                    return;
                }
                try {
                    App.getInstance().saveGame(fileName);
                } catch (IOException e) {
                    errorFileText.setVisible(true);
                }
            }
        });

        createDotFileButton.setDefaultButton(true);
        createDotFileButton.setPrefSize(150, 25);
        createDotFileButton.setLayoutX(650);
        createDotFileButton.setLayoutY(125);
        createDotFileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String fileName = textField.getText();
                if(!isFileNameValid(fileName)){
                    return;
                }
                try {
                    App.getInstance().generateDotFile(fileName);
                } catch (DotCreationException e) {
                    errorFileText.setVisible(true);
                }
            }
        });

        undoMoveButton.setDefaultButton(true);
        undoMoveButton.setPrefSize(150, 25);
        undoMoveButton.setLayoutX(650);
        undoMoveButton.setLayoutY(175);
        undoMoveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Move lastMoveDone = App.getInstance().getBoard().undoLastMove();
                if (lastMoveDone != null) {
                    undoLastMove(lastMoveDone);
                }
            }
        });

        nextTurnButton.setDefaultButton(true);
        nextTurnButton.setPrefSize(150, 25);
        nextTurnButton.setLayoutX(650);
        nextTurnButton.setLayoutY(225);
        nextTurnButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                playNextTurnAI();
            }
        });

        for (int i = 0; i < App.getInstance().getBoard().getSize(); i++) {
            for (int j = 0; j < App.getInstance().getBoard().getSize(); j++) {
                Circle c = new Circle(RADIUS, Color.BLACK);
                c.relocate(START_POSITION + (i  * DISTANCE), START_POSITION + (j * DISTANCE));
                getChildren().add(c);
            }
        }
        refreshBoardStart();
        getChildren().addAll(textField, undoMoveButton, saveGameButton, nextTurnButton, createDotFileButton, currentTurnText, pointsText, player1PointsText, player2PointsText, gameEndedText, errorFileText);
    }

    private void refreshBoardStart() {
        List<Move> lastMoves = App.getInstance().getBoard().getAllDoneMoves();
        if(lastMoves == null){
            return;
        }

        refreshBoardFromList(lastMoves);
        setTexts();
    }

    void refreshBoardMoves() {
        List<Move> lastMoves = App.getInstance().getBoard().getLastDoneMoves();
        if(lastMoves == null){
            return;
        }

        refreshBoardFromList(lastMoves);
        setTexts();
    }

    private void refreshBoardFromList(List<Move> moves) {
        for (Move eachMoveDone : moves) {
            Rectangle arc = createLine(eachMoveDone);

            if (eachMoveDone.getPlayer() == App.getInstance().getPlayer1()) {
                arc.setFill(Color.RED);
            } else {
                arc.setFill(Color.BLUE);
            }

            getChildren().add(arc);
        }
    }

    private void playNextTurnAI() {
        if (!App.getInstance().getBoard().isOver()) {
            Player actualPlayer = App.getInstance().getCurrentPlayer();
            if (actualPlayer.isAI()) {
                System.out.println("AI Calculating move:");
                aiMoveText.setVisible(true);
                ((AIPlayer)actualPlayer).calculateAndMakeMove();
                refreshBoardMoves();
            }
        }
    }

    public Text getAiMoveText(){
        return aiMoveText;
    }

    private void undoLastMove(Move lastMoveDone) {
        Rectangle arc = createLine(lastMoveDone);

        arc.setFill(Color.WHITESMOKE);
        getChildren().add(arc);
        setTexts();
    }

    private Rectangle createLine(Move moveDone) {
        Rectangle arc = new Rectangle();
        arc.setArcWidth(1);
        arc.setArcHeight(1);

        if (moveDone.isHorizontal()) {
            arc.setWidth(LINE_DISTANCE);
            arc.setHeight(LINE_EXTEND);
            arc.setX(START_POSITION + (moveDone.getColFrom() * DISTANCE) + RADIUS * 2);
            arc.setY(START_POSITION + (moveDone.getRowFrom() * DISTANCE) + (RADIUS * 2 - LINE_EXTEND) / 2);
        } else {
            arc.setWidth(LINE_EXTEND);
            arc.setHeight(LINE_DISTANCE);
            arc.setX(START_POSITION + (moveDone.getColFrom() * DISTANCE) + (RADIUS * 2 - LINE_EXTEND) / 2);
            arc.setY(START_POSITION + (moveDone.getRowFrom() * DISTANCE) + RADIUS * 2);
        }

        return arc;
    }

    int isArc(int x, int y) {
        if (x >= 0 && x <= START_POSITION + DISTANCE * App.getInstance().getBoard().getSize() && y >= 0 && y <= START_POSITION + DISTANCE * App.getInstance().getBoard().getSize()) {
            int width = RADIUS+RADIUS;
            int height = width + RADIUS + LINE_DISTANCE;
            int xPos = (x - START_POSITION) % DISTANCE;
            int yPos = (y - START_POSITION) % DISTANCE;
            if(xPos <= width && yPos >=width && yPos <= height){
                return 1;
            }
            else if(yPos <= width && xPos >=width && xPos <= height){
                return 2;
            }
        }
        return 0;
    }

    private boolean isFileNameValid(String str){
        if(str == null){
            return false;
        }
        if(str.isEmpty()){
            return false;
        }
        for(int i = 0; i < str.length(); i++){
            if(str.charAt(i) == ' '){
                return false;
            }
        }
        return true;
    }
}
