package DAB.DotsAndBoxes.view;

import DAB.DotsAndBoxes.model.*;
import DAB.DotsAndBoxes.model.exceptions.MinimaxException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.IOException;

public class BoardPane extends Pane {

    private static final int DISTANCE = 30;
    private static final int RADIUS = 5;
    private static final int LINE_EXTEND = 4;
    private static final int LINE_DISTANCE = 20;

    private Game game;
    private Text points1 = new Text(600, 200, "0");
    private Text points2 = new Text(600, 250, "0");
    private Text firstClick = new Text(600, 300, "First click detected.");
    private Text secondClick = new Text(600, 350, "Second click detected.");
    private Text invalidMove = new Text(600, 400, "Invalid Move.");
    private Button nextTurn = new Button("NEXT TURN");
    private Button undoMove = new Button("UNDO MOVE");
    private Button saveGame = new Button("SAVE GAME");

    public BoardPane(Game game) {
        this.game = game;
        initializeBoard();
    }

    public void setTexts() {
        points1.setText("Player 1 point: " + game.getPlayer1().getPoints());
        points2.setText("Player 2 point: " + game.getPlayer2().getPoints());
    }

    private void initializeBoard() {
        setTexts();

        undoMove.setDefaultButton(true);
        undoMove.setPrefSize(100, 25);
        undoMove.setLayoutX(600);
        undoMove.setLayoutY(25);
        this.getChildren().add(undoMove);
        undoMove.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Move lastMoveDone = game.getGameBoard().undoLastMove();
                if (lastMoveDone != null) {
                    undoLastMove(lastMoveDone);
                }
            }
        });

        saveGame.setDefaultButton(true);
        saveGame.setPrefSize(100, 25);
        saveGame.setLayoutX(600);
        saveGame.setLayoutY(75);
        this.getChildren().add(saveGame);

        saveGame.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    game.saveGame("partida");
                } catch (IOException el) {
                    el.printStackTrace();
                }
            }
        });


        nextTurn.setDefaultButton(true);
        nextTurn.setPrefSize(100, 25);
        nextTurn.setLayoutX(600);
        nextTurn.setLayoutY(125);
        this.getChildren().add(nextTurn);
        nextTurn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                playNextTurnAI();
            }
        });

        for (int i = 0; i < game.getGameBoard().getSize(); i++) {
            for (int j = 0; j < game.getGameBoard().getSize(); j++) {
                Circle c = new Circle(RADIUS, Color.BLACK);
                c.relocate(i * DISTANCE, j * DISTANCE);
                getChildren().add(c);
            }
        }
        getChildren().add(points1);
        getChildren().add(points2);
        firstClick.setVisible(false);
        secondClick.setVisible(false);
        invalidMove.setVisible(false);
        getChildren().add(firstClick);
        getChildren().add(secondClick);
        getChildren().add(invalidMove);
    }

    private void playNextTurnAI() {
        if (!game.getGameBoard().isOver()) {
            Player actualPlayer = game.getCurrentPlayer();
            if (actualPlayer.isAI()) {
                try {
                    ((AIPlayer) actualPlayer).calculateAndMakeMove();

                } catch (MinimaxException ex) {
                    System.out.println(ex);
                }
                refreshBoard();
            }
        }
    }

    public Text getFirstClick() {
        return firstClick;
    }

    public Text getSecondClick() {
        return secondClick;
    }

    public Text getInvalidMoveText() {
        return invalidMove;
    }

    public void refreshBoard() {
        setTexts();

        for (Move eachMoveDone : game.getGameBoard().getLastMoves()) {
            Rectangle arc = createLine(eachMoveDone);

            if (eachMoveDone.getPlayer() == game.getPlayer1()) {
                arc.setFill(Color.RED);
            } else {
                arc.setFill(Color.BLUE);
            }

            getChildren().add(arc);
        }
    }

    public void undoLastMove(Move lastMoveDone) {
        Rectangle arc = createLine(lastMoveDone);

        arc.setFill(Color.WHITE);
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
            arc.setX((moveDone.getColFrom() * DISTANCE) + RADIUS * 2);
            arc.setY((moveDone.getRowFrom() * DISTANCE) + (RADIUS * 2 - LINE_EXTEND) / 2);

        } else {
            arc.setWidth(LINE_EXTEND);
            arc.setHeight(LINE_DISTANCE);
            arc.setX((moveDone.getColFrom() * DISTANCE) + (RADIUS * 2 - LINE_EXTEND) / 2);
            arc.setY((moveDone.getRowFrom() * DISTANCE) + RADIUS * 2);

        }

        return arc;
    }

    public boolean isCircle(int x, int y) {
        if (x >= 0 && x <= DISTANCE * game.getGameBoard().getSize() && y >= 0 && y <= DISTANCE * game.getGameBoard().getSize()) {
            if (x % DISTANCE <= RADIUS && y % DISTANCE <= RADIUS) {
                return true;
            }
        }
        return false;
    }

}
