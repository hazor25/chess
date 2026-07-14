package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO {
    private final HashMap<String, UserData> users = new HashMap<>();
}
