package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;

import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;
import service.UserService;

import java.util.Map;

public class UserHandler {

    private final UserService userService;
    private final Gson gson = new Gson();

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Context ctx) {
        try {
            RegisterRequest request = gson.fromJson(ctx.body(), RegisterRequest.class);
            RegisterResult result = userService.register(request);
            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(result));
        } catch (Exception ex) {
            handleException(ctx, ex);
        }
    }

    public void login(Context ctx) {
        try {
            LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);
            LoginResult result = userService.login(request);
            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(result));
        } catch (Exception ex) {
            handleException(ctx, ex);
        }
    }

    public void logout(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            LogoutResult result = userService.logout(authToken);
            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(result));
        } catch (Exception ex) {
            handleException(ctx, ex);
        }
    }

    private void handleException(Context ctx, Exception ex) {
        ctx.contentType("application/json");

        if (ex instanceof DataAccessException dae) {
            String message = dae.getMessage();

            switch (message) {
                case "bad request" -> {
                    ctx.status(400);
                    ctx.result(gson.toJson(Map.of("message", "Error: " + message)));
                }
                case "unauthorized" -> {
                    ctx.status(401);
                    ctx.result(gson.toJson(Map.of("message", "Error: " + message)));
                }
                case "already taken" -> {
                    ctx.status(403);
                    ctx.result(gson.toJson(Map.of("message", "Error: " + message)));
                }
                default -> {
                    ctx.status(500);
                    ctx.result(gson.toJson(Map.of("message", "Error: " + message)));
                }
            }
        } else {
            ctx.status(500);
            ctx.result(gson.toJson(Map.of("message", "Error: " + ex.getMessage())));
        }
    }
}
