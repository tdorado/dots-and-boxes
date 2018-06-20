package DAB.DotsAndBoxes.view;

import DAB.DotsAndBoxes.model.Game;
import DAB.DotsAndBoxes.model.Move;
import DAB.DotsAndBoxes.model.Player;
import DAB.DotsAndBoxes.model.exceptions.InvalidMoveException;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

public class GameScene extends Scene {
    private Game game;
    private BoardPane boardPane;
    private Coordinates cord1 = new Coordinates(0, 0);
    private Coordinates cord2 = new Coordinates(0, 0);
    private int cont = 0;
    private Move lastMoveClicked = null;

    public GameScene(BoardPane boardPane, Game game) {
        super(boardPane, 800, 600);
        this.boardPane = boardPane;
        this.game = game;

        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (boardPane.isCircle(x, y)) {
                    if (cont == 0) {
                        cord1.x = y / 30;
                        cord1.y = x / 30;
                        cont = 1;
                        boardPane.getFirstClick().setVisible(true);
                        boardPane.getInvalidMoveText().setVisible(false);
                    } else if (cont == 1) {
                        cord2.x = y / 30;
                        cord2.y = x / 30;
                        cont = 2;
                        boardPane.getSecondClick().setVisible(true);
                        lastMoveClicked = new Move(cord1.x, cord1.y, cord2.x, cord2.y);
                        playNextTurnPlayer();
                    }
                }
            }
        });

    }

    private void playNextTurnPlayer() {
        if (!game.getGameBoard().isOver()) {
            Player actualPlayer = game.getCurrentPlayer();
            if (!actualPlayer.isAI()){
                try {
                    actualPlayer.makePlayerMove(lastMoveClicked);
                } catch (InvalidMoveException ex) {
                    boardPane.getInvalidMoveText().setVisible(true);
                    System.out.println(ex);
                }
                boardPane.getFirstClick().setVisible(false);
                boardPane.getSecondClick().setVisible(false);
                lastMoveClicked = null;
                cont = 0;
                boardPane.refreshBoard();
            }
        }
    }


    private class Coordinates {
        private int x;
        private int y;

        Coordinates(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
