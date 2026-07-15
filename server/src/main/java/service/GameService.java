package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.JoinGameResult;
import result.ListGamesResult;


public class GameService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }


    private AuthData validateAuth(String authToken) throws DataAccessException {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Unauthorized");
        }
        return auth;
    }


    public CreateGameResult createGame(CreateGameRequest request, String token) throws DataAccessException {
        validateAuth(token);
        if (request == null || request.gameName() == null || request.gameName().isBlank()) {
            throw new DataAccessException("Bad request");
        }

        int gameID = gameDAO.generateGameID();
        GameData newGame = new GameData(gameID, null, null, request.gameName(), new ChessGame());
        gameDAO.createGame(newGame);
        return new CreateGameResult(gameID);
    }

    public ListGamesResult listGames(String token) throws DataAccessException {
        validateAuth(token);
        return new ListGamesResult(gameDAO.listGames());
    }

    private Boolean colorAvailable(String color, GameData game) {
        if ("WHITE".equals(color)) {
            return game.whiteUsername() == null;
        }
        else if ("BLACK".equals(color)) {
            return game.blackUsername() == null;
        }
        return true;
    }

    public JoinGameResult joinGame(JoinGameRequest request, String token) throws DataAccessException{
        AuthData auth = validateAuth(token);
        String username = auth.username();
        String color = request.playerColor();

        if (request == null || color == null) {
            throw new DataAccessException("Bad request");
        }
        if (!"WHITE".equals(color) && !"BLACK".equals(color)) {
            throw new DataAccessException("Invalid color");
        }
        GameData game = gameDAO.getGame(request.gameID());

        if (colorAvailable(color, game)) {
            GameData updatedGame =  new GameData(game.gameID(), username,
                    game.blackUsername(), game.gameName(), game.game());
            gameDAO.updateGame(updatedGame);
        }
        else {
            throw new DataAccessException(color + "player already taken");
        }



        return new JoinGameResult();
    }
}
