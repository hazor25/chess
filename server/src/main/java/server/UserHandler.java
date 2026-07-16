package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;

import request.CreateGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.LoginResult;
import result.RegisterResult;
import service.UserService;

import java.util.Map;

public class UserHandler {

    private final UserService userService;
    private final Gson gson = new Gson();

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Context ctx) throws DataAccessException {
        try {
            String authToken = ctx.header("authorization");
            RegisterRequest request = gson.fromJson(ctx.body(), RegisterRequest.class);
            RegisterResult result = userService.register(request);
            ctx.status(200);
            ctx.json(result);
        } catch (Exception ex) {
            handleException(ctx, ex);
        }
    }

    public void login(Context ctx) throws DataAccessException {
        try {
            String authToken = ctx.header("authorization");
            LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);
            LoginResult result = userService.login(request);
            ctx.status(200);
            ctx.json(result);
        } catch (Exception ex) {
            handleException(ctx, ex);
        }
    }

    public void logout(Context ctx) throws DataAccessException {
        try {
            String authToken = ctx.header("authorization");

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
