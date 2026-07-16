package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;

import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.JoinGameResult;
import result.ListGamesResult;
import service.GameService;

import java.util.Map;

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
            ctx.json(result);
        } catch (Exception ex) {
            handleException(ctx, ex);
        }
    }

    public void createGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            CreateGameRequest request = gson.fromJson(ctx.body(), CreateGameRequest.class);
            CreateGameResult result = gameService.createGame(authToken, request);
            ctx.status(200);
            ctx.json(result);
        } catch (Exception ex) {
            handleException(ctx, ex);
        }
    }

    public void joinGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            JoinGameRequest request = gson.fromJson(ctx.body(), JoinGameRequest.class);
            JoinGameResult result = gameService.joinGame(authToken, request);
            ctx.status(200);
            ctx.json(result);
        } catch (Exception ex) {
            handleException(ctx, ex);
        }
    }

    private void handleException(Context ctx, Exception ex) {
        if (ex instanceof DataAccessException dae) {
            String message = dae.getMessage();

            switch (message) {
                case "bad request" -> {
                    ctx.status(400);
                    ctx.json(Map.of("message", "Error: " + message));
                }
                case "unauthorized" -> {
                    ctx.status(401);
                    ctx.json(Map.of("message", "Error: " + message));
                }
                case "already exists" -> {
                    ctx.status(403);
                    ctx.json(Map.of("message", "Error: " + message));
                }
                default -> {
                    ctx.status(500);
                    ctx.json(Map.of("message", "Error: " + message));
                }
            }
        } else {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + ex.getMessage()));
        }
    }
}