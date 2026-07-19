package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService userService;

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO = new SQLUserDAO();
        authDAO = new SQLAuthDAO();
        userDAO.clear();
        authDAO.clear();

        userService = new UserService(userDAO, authDAO);
    }

    @Test
    void registerPositive() throws Exception {
        RegisterRequest request = new RegisterRequest("luis", "password", "luis@email.com");

        RegisterResult result = userService.register(request);

        assertNotNull(result);
        assertEquals("luis", result.username());
        assertNotNull(result.authToken());

        UserData user = userDAO.getUser("luis");
        assertNotNull(user);
        assertEquals("luis", user.username());

        AuthData auth = authDAO.getAuth(result.authToken());
        assertNotNull(auth);
        assertEquals("luis", auth.username());
    }

    @Test
    void registerNegative() throws Exception {
        userService.register(new RegisterRequest("luis", "password", "luis@email.com"));

        DataAccessException ex = assertThrows(DataAccessException.class, () ->
                userService.register(new RegisterRequest("luis", "otherpass", "other@email.com"))
        );

        assertEquals("already taken", ex.getMessage());
    }

    @Test
    void loginPositive() throws Exception {
        userService.register(new RegisterRequest("luis", "password", "luis@email.com"));

        LoginResult result = userService.login(new LoginRequest("luis", "password"));

        assertNotNull(result);
        assertEquals("luis", result.username());
        assertNotNull(result.authToken());

        assertNotNull(authDAO.getAuth(result.authToken()));
    }

    @Test
    void loginNegative() throws Exception {
        userService.register(new RegisterRequest("luis", "password", "luis@email.com"));

        DataAccessException ex = assertThrows(DataAccessException.class, () ->
                userService.login(new LoginRequest("luis", "wrongPassword"))
        );

        assertEquals("unauthorized", ex.getMessage());
    }

    @Test
    void logoutPositive() throws Exception {
        RegisterResult registerResult = userService.register(
                new RegisterRequest("luis", "password", "luis@email.com")
        );

        LogoutResult result = userService.logout(registerResult.authToken());

        assertNotNull(result);
        assertNull(authDAO.getAuth(registerResult.authToken()));
    }

    @Test
    void logoutNegative() {
        DataAccessException ex = assertThrows(DataAccessException.class, () ->
                userService.logout("fakeToken")
        );

        assertEquals("unauthorized", ex.getMessage());
    }
}