package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private ClearService clearService;

    @BeforeEach
    void setUp() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        clearService = new ClearService(userDAO, authDAO, gameDAO);
    }

    @Test
    void clearPositive() throws Exception {
        userDAO.createUser(new UserData("cass", "password", "cass21@email.com"));
        authDAO.createAuth(new AuthData("token", "cass"));
        GameData game = new GameData(0, null, null, "Test Game", new ChessGame());
        gameDAO.createGame(game);

        clearService.clear();

        assertNull(userDAO.getUser("cass"));
        assertNull(authDAO.getAuth("token"));
        assertTrue(gameDAO.listGames().isEmpty());
    }
}