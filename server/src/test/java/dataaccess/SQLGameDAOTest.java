package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.*;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameDAOTest {

  private SQLGameDAO gameDAO;

  @BeforeEach
  public void setup() throws DataAccessException {
    gameDAO = new SQLGameDAO();
    gameDAO.clear();
  }

  @Test
  public void createGamePositive() throws DataAccessException {
    GameData game = new GameData(0, null, null, "Game", new ChessGame());

    int id = gameDAO.createGame(game);

    assertNotEquals(0, id);
  }

  @Test
  public void createGameNegative() {
    assertThrows(DataAccessException.class, () -> gameDAO.createGame(null));
  }

  @Test
  public void getGamePositive() throws DataAccessException {
    GameData game = new GameData(0, null, null, "Game", new ChessGame());

    int id = gameDAO.createGame(game);

    GameData result = gameDAO.getGame(id);

    assertNotNull(result);
    assertEquals("Game", result.gameName());
  }

  @Test
  public void getGameNegative() throws DataAccessException {
    assertNull(gameDAO.getGame(99999));
  }

  @Test
  public void listGamesPositive() throws DataAccessException {
    gameDAO.createGame(new GameData(0, null, null, "One", new ChessGame()));
    gameDAO.createGame(new GameData(0, null, null, "Two", new ChessGame()));

    Collection<GameData> games = gameDAO.listGames();

    assertEquals(2, games.size());
  }

  @Test
  public void listGamesNegative() throws DataAccessException {
    Collection<GameData> games = gameDAO.listGames();

    assertTrue(games.isEmpty());
  }

  @Test
  public void updateGamePositive() throws DataAccessException {
    int id = gameDAO.createGame(new GameData(0, null, null, "Old", new ChessGame()));

    GameData updated = new GameData(id, "white", "black", "New", new ChessGame());

    gameDAO.updateGame(updated);

    GameData result = gameDAO.getGame(id);

    assertEquals("white", result.whiteUsername());
    assertEquals("black", result.blackUsername());
    assertEquals("New", result.gameName());
  }

  @Test
  public void updateGameNegative() {
    GameData fake = new GameData(9999, null, null, "Fake", new ChessGame());

    assertThrows(DataAccessException.class, () -> gameDAO.updateGame(fake));
  }

  @Test
  public void clearPositive() throws DataAccessException {
    gameDAO.createGame(new GameData(0, null, null, "Game", new ChessGame()));

    gameDAO.clear();

    assertTrue(gameDAO.listGames().isEmpty());
  }
}