package server;

import dataaccess.*;
import io.javalin.*;
import service.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        try {
            UserDAO userDAO = new SQLUserDAO();
            AuthDAO authDAO = new SQLAuthDAO();
            GameDAO gameDAO = new SQLGameDAO();

            ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
            UserService userService = new UserService(userDAO, authDAO);
            GameService gameService = new GameService(gameDAO, authDAO);

            ClearHandler clearHandler = new ClearHandler(clearService);
            UserHandler userHandler = new UserHandler(userService);
            GameHandler gameHandler = new GameHandler(gameService);

            javalin = Javalin.create(config -> config.staticFiles.add("web"));

            javalin.delete("/db", clearHandler::clear);
            javalin.post("/user", userHandler::register);
            javalin.post("/session", userHandler::login);
            javalin.delete("/session", userHandler::logout);
            javalin.get("/game", gameHandler::listGames);
            javalin.post("/game", gameHandler::createGame);
            javalin.put("/game", gameHandler::joinGame);

        } catch (DataAccessException e) {
            throw new RuntimeException("Unable to start server", e);
        }
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
