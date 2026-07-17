package dataaccess;

import com.google.gson.Gson;
import model.GameData;

import java.sql.Statement;
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
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}
