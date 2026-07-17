package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.JoinGameResult;
import result.ListGamesResult;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private GameDAO gameDAO;
    private GameService gameService;

    @BeforeEach
    void setUp() throws Exception {
        gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        gameService = new GameService(gameDAO, authDAO);

        authDAO.createAuth(new AuthData("validToken", "hazor25"));
    }

    @Test
    void createGamePositive() throws Exception {
        CreateGameResult result = gameService.createGame("validToken", new CreateGameRequest("Test Game"));

        assertNotNull(result);
        GameData game = gameDAO.getGame(result.gameID());
        assertNotNull(game);
        assertEquals("Test Game", game.gameName());
    }

    @Test
    void createGameNegative() {
        DataAccessException ex = assertThrows(DataAccessException.class, () ->
                gameService.createGame("badToken", new CreateGameRequest("Test Game"))
        );

        assertEquals("unauthorized", ex.getMessage());
    }

    @Test
    void listGamesPositive() throws Exception {
        gameDAO.createGame(new GameData(0, null, null, "Test Game", new ChessGame()));

        ListGamesResult result = gameService.listGames("validToken");

        assertNotNull(result);
        assertFalse(result.games().isEmpty());
    }

    @Test
    void listGamesNegative() {
        DataAccessException ex = assertThrows(DataAccessException.class, () ->
                gameService.listGames("badToken")
        );

        assertEquals("unauthorized", ex.getMessage());
    }

    @Test
    void joinGamePositive() throws Exception {
        int gameID = gameDAO.createGame(new GameData(0, null, null, "Test Game", new ChessGame()));

        JoinGameResult result = gameService.joinGame("validToken", new JoinGameRequest("WHITE", gameID));

        assertNotNull(result);
        GameData updated = gameDAO.getGame(gameID);
        assertEquals("hazor25", updated.whiteUsername());
    }

    @Test
    void joinGameNegative() throws Exception {
        int gameID = gameDAO.createGame(new GameData(0, "bob", null, "Test Game", new ChessGame()));

        DataAccessException ex = assertThrows(DataAccessException.class, () ->
                gameService.joinGame("validToken", new JoinGameRequest("WHITE", gameID))
        );

        assertEquals("already taken", ex.getMessage());
    }
}