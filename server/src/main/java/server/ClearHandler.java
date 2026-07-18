package server;

import com.google.gson.Gson;
import io.javalin.http.Context;

import dataaccess.DataAccessException;
import result.ClearResult;
import service.ClearService;


public class ClearHandler {
    private final ClearService clearService;
    private final Gson gson = new Gson();

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public void clear(Context ctx) throws DataAccessException {
        try {
            clearService.clear();

            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(new ClearResult()));

        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(new ClearResult("Error: " + e.getMessage())));
        }
    }
}