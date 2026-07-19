package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class SQLUserDAOTest {

  private SQLUserDAO userDAO;

  @BeforeEach
  public void setup() throws DataAccessException {
    userDAO = new SQLUserDAO();
    userDAO.clear();
  }

  @Test
  public void createUserPositive() throws DataAccessException {
    UserData user = new UserData("bob", "password", "bob@email.com");

    userDAO.createUser(user);

    UserData result = userDAO.getUser("bob");
    assertNotNull(result);
    assertEquals("bob", result.username());
    assertEquals("bob@email.com", result.email());
  }

  @Test
  public void createUserNegative() throws DataAccessException {
    UserData user = new UserData("bob", "password", "bob@email.com");

    userDAO.createUser(user);

    assertThrows(DataAccessException.class, () -> userDAO.createUser(user));
  }

  @Test
  public void getUserPositive() throws DataAccessException {
    UserData user = new UserData("alice", "password", "alice@email.com");
    userDAO.createUser(user);

    UserData result = userDAO.getUser("alice");

    assertNotNull(result);
    assertEquals("alice", result.username());
  }

  @Test
  public void getUserNegative() throws DataAccessException {
    assertNull(userDAO.getUser("doesNotExist"));
  }

  @Test
  public void clearPositive() throws DataAccessException {
    userDAO.createUser(new UserData("bob", "pass", "email"));

    userDAO.clear();

    assertNull(userDAO.getUser("bob"));
  }
}