package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() throws DataAccessException {
        DatabaseManager.createDatabase();
        configureDatabase();
    }

    private void configureDatabase() throws DataAccessException {

        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                username VARCHAR(255) PRIMARY KEY,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL
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
    public UserData getUser(String username) throws DataAccessException {
        String sql = """
        SELECT username, password, email
        FROM users
        WHERE username = ?
        """;

        try (var conn= DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {
            statement.setString(1, username);

            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new UserData(
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("email")
                );
            }
            return null;

        } catch (Exception e) {
            throw new DataAccessException("Unable to get user", e);
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String sql = """
        INSERT INTO users (username, password, email)
        VALUES (?, ?, ?)
        """;

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {
            statement.setString(1, user.username());
            String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
            statement.setString(2, hashedPassword);
            statement.setString(3, user.email());

            statement.executeUpdate();
        }  catch (Exception e) {
            throw new DataAccessException("Unable to create user", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = """
        DELETE FROM users
        """;

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException("Unable to clear users", e);
        }
    }
}
