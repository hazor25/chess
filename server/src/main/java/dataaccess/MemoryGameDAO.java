package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public int createGame(GameData game) throws DataAccessException {
        if (games.containsKey(game.gameID())) {
            throw new DataAccessException("already exists");
        }
        games.put(game.gameID(), game);
        return game.gameID();
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        if(games.containsKey(gameID)){
            return games.get(gameID);
        }
        throw new DataAccessException("bad request");
    }

    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (!games.containsKey(game.gameID())) {
            throw new DataAccessException("bad request");
        }
        games.put(game.gameID(), game);
    }

    @Override
    public void clear() {
        games.clear();
    }
}