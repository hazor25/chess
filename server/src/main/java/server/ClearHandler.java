package server;

import io.javalin.http.Context;

import dataaccess.DataAccessException;
import result.ClearResult;
import service.ClearService;


public class ClearHandler {
    private final ClearService clearService;

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public void clear(Context ctx) throws DataAccessException {
        clearService.clear();
        ctx.status(200);
        ctx.json(new ClearResult());
    }
}