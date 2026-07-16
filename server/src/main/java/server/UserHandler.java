package server;

import com.google.gson.Gson;
import io.javalin.http.Context;

import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;
import service.UserService;

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
            ExceptionHandler exHandler = new ExceptionHandler();
            exHandler.handleException(ctx, ex);
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
            ExceptionHandler exHandler = new ExceptionHandler();
            exHandler.handleException(ctx, ex);
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
            ExceptionHandler exHandler = new ExceptionHandler();
            exHandler.handleException(ctx, ex);
        }
    }
}
