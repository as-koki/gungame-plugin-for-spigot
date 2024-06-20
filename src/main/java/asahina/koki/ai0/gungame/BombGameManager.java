package asahina.koki.ai0.gungame;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
// import org.bukkit.plugin.Plugin;

public class BombGameManager {
    public ArrayList<Bomb> games = new ArrayList<Bomb>();
    public Plugin plugin;
    public BombGameManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean createGame (Plugin plugin, gameInfo info) {
        if (hasGame(info.name)) {
            Bukkit.getLogger().warning("Game with name " + info.name + " already exists.");

            return false;
        }
        Bomb bomb = new Bomb(plugin, info);
        games.add(bomb);
        return true;
    }

    public boolean hasGame (String str) {
        for (Bomb bomb : games) {
            if (bomb.getName().equals(str)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> gameList () {
        ArrayList<String> namelist = new ArrayList<String>();
        for (Bomb bomb : games) {
            String name = bomb.getName();
            namelist.add(name);
        }
        return namelist;
    }

    public Bomb getBomb(String game) {
        for (Bomb bomb : games) {
            if (bomb.getName().equals(game)) {
                return bomb;
            }
        }
        return null;
    }


}
