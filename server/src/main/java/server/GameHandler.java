package server;

import com.google.gson.Gson;
import io.javalin.http.Context;

import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.JoinGameResult;
import result.ListGamesResult;
import service.GameService;


public class GameHandler {

    private final GameService gameService;
    private final Gson gson = new Gson();

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void listGames(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            ListGamesResult result = gameService.listGames(authToken);
            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(result));
        } catch (Exception ex) {
            ExceptionHandler exHandler = new ExceptionHandler();
            exHandler.handleException(ctx, ex);
        }
    }

    public void createGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            CreateGameRequest request = gson.fromJson(ctx.body(), CreateGameRequest.class);
            CreateGameResult result = gameService.createGame(authToken, request);
            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(result));
        } catch (Exception ex) {
            ExceptionHandler exHandler = new ExceptionHandler();
            exHandler.handleException(ctx, ex);
        }
    }

    public void joinGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            JoinGameRequest request = gson.fromJson(ctx.body(), JoinGameRequest.class);
            JoinGameResult result = gameService.joinGame(authToken, request);
            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(result));
        } catch (Exception ex) {
            ExceptionHandler exHandler = new ExceptionHandler();
            exHandler.handleException(ctx, ex);
        }
    }
}