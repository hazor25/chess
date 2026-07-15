package service;

import dataaccess.*;

import model.AuthData;
import model.UserData;

import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;

import java.util.UUID;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        UserData existingUser = userDAO.getUser(request.username());
        if (existingUser != null) {
            throw new DataAccessException("Username is taken");
        }
        UserData newUser = new UserData(request.username(), request.password(), request.email());
        userDAO.createUser(newUser);

        String token = UUID.randomUUID().toString();
        authDAO.createAuth(new AuthData(token, newUser.username()));
        return new RegisterResult(newUser.username(), token);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        UserData inputUser = userDAO.getUser(request.username());

        if (inputUser == null) {
            throw new DataAccessException("Incorrect username or password");
        }

        if (!inputUser.password().equals(request.password())) {
            throw new DataAccessException("Incorrect username or password");
        }

        String token = UUID.randomUUID().toString();
        authDAO.createAuth(new AuthData(token, inputUser.username()));
        return new LoginResult(inputUser.username(), token);
    }

    public LogoutResult logout(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);

        if (authData == null) {
            throw new DataAccessException("Unauthorized");
        }

        authDAO.deleteAuth(authData.authToken());
        return new LogoutResult();
    }
}
