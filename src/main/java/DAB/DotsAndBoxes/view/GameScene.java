package DAB.DotsAndBoxes.view;

import DAB.DotsAndBoxes.App;
import DAB.DotsAndBoxes.model.AIPlayer;
import DAB.DotsAndBoxes.model.Move;
import DAB.DotsAndBoxes.model.Player;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import static DAB.DotsAndBoxes.view.BoardPane.DISTANCE;
import static DAB.DotsAndBoxes.view.BoardPane.START_POSITION;

public class GameScene extends Scene {

    private BoardPane boardPane;

    public GameScene(BoardPane boardPane) {
        super(boardPane, 800, 600);
        this.boardPane = boardPane;
        initializeScene();
    }

    private void initializeScene() {
        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!App.getInstance().getBoard().isOver()) {
                    Player currentPlayer = App.getInstance().getCurrentPlayer();
                    if (!currentPlayer.isAI()) {
                        playTurnPlayer(event);
                        currentPlayer = App.getInstance().getCurrentPlayer();
                        if (currentPlayer.isAI()) {
                            ((AIPlayer) currentPlayer).calculateAndMakeMove();
                            boardPane.refreshBoardMoves();
                        }
                    } else {
                        ((AIPlayer) currentPlayer).calculateAndMakeMove();
                        boardPane.refreshBoardMoves();
                    }
                }
            }
        });
    }

    private void playTurnPlayer(MouseEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int arcType = boardPane.isArc(x, y);
        if (arcType != 0) {
            Move move;
            x = (x - START_POSITION) / DISTANCE;
            y = (y - START_POSITION) / DISTANCE;
            if (arcType == 1) {
                move = new Move(y, x, y + 1, x);
            } else {
                move = new Move(y, x, y, x + 1);
            }
            Player currentPlayer = App.getInstance().getCurrentPlayer();
            currentPlayer.makeMove(move);
            boardPane.refreshBoardMoves();
        }
    }

}
