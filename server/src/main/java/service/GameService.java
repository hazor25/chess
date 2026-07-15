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

        int gameID = Math.abs(new java.util.Random().nextInt());
        GameData newGame = new GameData(gameID, null, null, request.gameName(), new ChessGame());
        gameDAO.createGame(newGame);
        return new CreateGameResult(gameID);
    }

    public ListGamesResult listGames(String token) throws DataAccessException {
        validateAuth(token);
        return new ListGamesResult(gameDAO.listGames());
    }

    private Boolean colorAvailable(JoinGameRequest req, String color, GameData game) {
        if ("WHITE".equals(req.playerColor())) {
            if (game.whiteUsername() != null) {
                return false;
            }
        }
        else if ("BLACK".equals(req.playerColor())) {
            if (game.blackUsername() != null) {
                return false;
            }
        }
        return true;
    }

    public JoinGameResult joinGame(JoinGameRequest request, String token) throws DataAccessException{
        AuthData auth = validateAuth(token);
        String username = auth.username();

        if (request == null || request.playerColor() == null) {
            throw new DataAccessException("Bad request");
        }
        GameData game = gameDAO.getGame(request.gameID());

        if (colorAvailable(request,  request.playerColor(), game)) {
            GameData updatedGame =  new GameData(game.gameID(), username,
                    game.blackUsername(), game.gameName(), game.game());
            gameDAO.updateGame(game);
        } else {
            throw new DataAccessException(request.playerColor() + "player already taken");
        }



        return new JoinGameResult();
    }
}
