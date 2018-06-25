package DAB.DotsAndBoxes;

import DAB.DotsAndBoxes.model.AIPlayer;
import DAB.DotsAndBoxes.model.Game;
import DAB.DotsAndBoxes.model.Player;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class GameTest extends TestCase {

    public GameTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(GameTest.class);
    }

    public void testApp() {

        Game game0 = new Game(5, 0, 1, 1, true);
        assertEquals(5, game0.getBoard().getSize());
        assertEquals(Player.class, game0.getPlayer1().getClass());
        assertEquals(Player.class, game0.getPlayer2().getClass());

        Game game1 = new Game(6, 1, 1, 1, true);
        assertEquals(6, game1.getBoard().getSize());
        assertEquals(AIPlayer.class, game1.getPlayer1().getClass());
        assertEquals(Player.class, game1.getPlayer2().getClass());

        Game game2 = new Game(8, 2, 1, 1, true);
        assertEquals(8, game2.getBoard().getSize());
        assertEquals(Player.class, game2.getPlayer1().getClass());
        assertEquals(AIPlayer.class, game2.getPlayer2().getClass());

        Game game3 = new Game(10, 3, 1, 1, true);
        assertEquals(10, game3.getBoard().getSize());
        assertEquals(AIPlayer.class, game3.getPlayer1().getClass());
        assertEquals(AIPlayer.class, game3.getPlayer2().getClass());
    }

}
