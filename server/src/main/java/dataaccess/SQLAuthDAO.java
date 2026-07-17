package dataaccess;

import model.AuthData;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() throws DataAccessException {
        DatabaseManager.createDatabase();
        configureDatabase();
    }

    private void configureDatabase() throws DataAccessException {

        String sql = """
                CREATE TABLE IF NOT EXISTS auths (
                authToken VARCHAR(255) PRIMARY KEY,
                username VARCHAR(255) NOT NULL
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
    public void createAuth(AuthData authData) throws DataAccessException {
        String sql = """
        INSERT INTO users (authToken, username)
        VALUES (?, ?)
        """;

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {
            statement.setString(1, authData.authToken());
            statement.setString(2, authData.username());

            statement.executeUpdate();
        }  catch (Exception e) {
            throw new DataAccessException("Unable to create user", e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}