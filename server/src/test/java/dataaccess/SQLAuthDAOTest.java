package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class SQLAuthDAOTest {

  private SQLAuthDAO authDAO;

  @BeforeEach
  public void setup() throws DataAccessException {
    authDAO = new SQLAuthDAO();
    authDAO.clear();
  }

  @Test
  public void createAuthPositive() throws DataAccessException {
    AuthData auth = new AuthData("token", "bob");

    authDAO.createAuth(auth);

    assertEquals(auth, authDAO.getAuth("token"));
  }

  @Test
  public void createAuthNegative() throws DataAccessException {
    AuthData auth = new AuthData("token", "bob");

    authDAO.createAuth(auth);

    assertThrows(DataAccessException.class, () -> authDAO.createAuth(auth));
  }

  @Test
  public void getAuthPositive() throws DataAccessException {
    AuthData auth = new AuthData("abc", "alice");

    authDAO.createAuth(auth);

    assertEquals(auth, authDAO.getAuth("abc"));
  }

  @Test
  public void getAuthNegative() throws DataAccessException {
    assertNull(authDAO.getAuth("fake"));
  }

  @Test
  public void deleteAuthPositive() throws DataAccessException {
    AuthData auth = new AuthData("abc", "alice");

    authDAO.createAuth(auth);
    authDAO.deleteAuth("abc");

    assertNull(authDAO.getAuth("abc"));
  }

  @Test
  public void deleteAuthNegative() {
    assertDoesNotThrow(() -> authDAO.deleteAuth("fake"));
  }

  @Test
  public void clearPositive() throws DataAccessException {
    authDAO.createAuth(new AuthData("abc", "alice"));

    authDAO.clear();

    assertNull(authDAO.getAuth("abc"));
  }
}