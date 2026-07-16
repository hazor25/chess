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

    private void validateRegisterRequest(RegisterRequest request) throws DataAccessException {
        if (request == null ||
                request.username() == null || request.username().isBlank() ||
                request.password() == null || request.password().isBlank() ||
                request.email() == null || request.email().isBlank()) {
            throw new DataAccessException("bad request");
        }
    }

    private void validateLoginRequest(LoginRequest request) throws DataAccessException {
        if (request == null ||
                request.username() == null || request.username().isBlank() ||
                request.password() == null || request.password().isBlank()) {
            throw new DataAccessException("bad request");
        }
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        validateRegisterRequest(request);

        UserData existingUser = userDAO.getUser(request.username());
        if (existingUser != null) {
            throw new DataAccessException("already taken");
        }
        UserData newUser = new UserData(request.username(), request.password(), request.email());
        userDAO.createUser(newUser);

        String token = UUID.randomUUID().toString();
        authDAO.createAuth(new AuthData(token, newUser.username()));
        return new RegisterResult(newUser.username(), token);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        validateLoginRequest(request);
        UserData inputUser = userDAO.getUser(request.username());

        if (inputUser == null) {
            throw new DataAccessException("unauthorized");
        }

        if (!inputUser.password().equals(request.password())) {
            throw new DataAccessException("unauthorized");
        }

        String token = UUID.randomUUID().toString();
        authDAO.createAuth(new AuthData(token, inputUser.username()));
        return new LoginResult(inputUser.username(), token);
    }

    public LogoutResult logout(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);

        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }

        authDAO.deleteAuth(authData.authToken());
        return new LogoutResult();
    }
}
