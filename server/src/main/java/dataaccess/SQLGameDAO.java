package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SQLGameDAO implements GameDAO {
    private final Gson gson = new Gson();

    public SQLGameDAO() throws DataAccessException {
        DatabaseManager.createDatabase();
        configureDatabase();
    }

    private void configureDatabase() throws DataAccessException {

        String sql = """
                CREATE TABLE IF NOT EXISTS games (
                gameID int AUTO_INCREMENT PRIMARY KEY,
                whiteUsername VARCHAR(255),
                blackUsername VARCHAR(255),
                gameName VARCHAR(255),
                game text
                            )
            """;

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.executeUpdate();

        } catch (Exception e) {
            throw new DataAccessException("Unable to configure database", e);
        }
    }


    @Override
    public int createGame(GameData game) throws DataAccessException {
        String sql = """
        INSERT INTO games (whiteUsername, blackUsername, gameName, game)
        VALUES (?, ?, ?, ?)
        """;

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, game.whiteUsername());
            statement.setString(2, game.blackUsername());
            statement.setString(3, game.gameName());
            statement.setString(4, gson.toJson(game.game()));

            statement.executeUpdate();

            try (var keys = statement.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }  catch (Exception e) {
            throw new DataAccessException("Unable to create game", e);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = """
        SELECT gameID, whiteUsername, blackUsername, gameName, game
        FROM games
        WHERE gameID = ?;
        """;

        try (var conn= DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {
            statement.setInt(1, gameID);

            var resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String gameJson = resultSet.getString("game");
                ChessGame chessGame = gson.fromJson(gameJson, ChessGame.class);

                return new GameData(
                        resultSet.getInt("gameID"),
                        resultSet.getString("whiteUsername"),
                        resultSet.getString("blackUsername"),
                        resultSet.getString("gameName"),
                        chessGame
                );
            }
            return null;

        } catch (Exception e) {
            throw new DataAccessException("Unable to get game", e);
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> gamesList = new ArrayList<>();

        String sql = """
        SELECT gameID, whiteUsername, blackUsername, gameName, game
        FROM games
        """;

        try (var conn= DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            var resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String gameJson = resultSet.getString("game");
                ChessGame chessGame = gson.fromJson(gameJson, ChessGame.class);
                gamesList.add(new GameData(resultSet.getInt("gameID"),
                            resultSet.getString("whiteUsername"),
                            resultSet.getString("blackUsername"),
                            resultSet.getString("gameName"),
                            chessGame)
                );
            }
            return gamesList;

        } catch (Exception e) {
            throw new DataAccessException("Unable to get list of games", e);
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String sql = """
        UPDATE games
        SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ?
        WHERE gameID = ?;
        """;

        try (var conn= DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {
            statement.setString(1, game.whiteUsername());
            statement.setString(2, game.blackUsername());
            statement.setString(3, game.gameName());
            statement.setString(4, gson.toJson(game.game()));
            statement.setInt(5, game.gameID());

            int rows = statement.executeUpdate();

            if (rows == 0) {
                throw new DataAccessException("Game does not exist");
            }

        } catch (Exception e) {
            throw new DataAccessException("Unable to update game", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = """
        DELETE FROM games
        WHERE gameID = ?;
        """;


    }
}
