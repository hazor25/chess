package server;

import com.google.gson.Gson;

import dataaccess.DataAccessException;
import io.javalin.http.Context;

import java.util.Map;

public class ExceptionHandler {

    private final Gson gson = new Gson();

    public void handleException(Context ctx, Exception ex) {
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
