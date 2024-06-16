package asahina.koki.ai0.gungame;

import org.bukkit.Bukkit;
import org.bukkit.Location;
public class Bomb {
    Location location_t = new Location(Bukkit.getWorld("world"), 1, 1, 1, 0, 0); // tmp
    Location location_ct = new Location(Bukkit.getWorld("world"), 1, 1, 1, 0, 0); // tmp

    private Gteam t = new Gteam("テロリスト", "§c", false, location_t);
    private Gteam ct = new Gteam("カウンターテロリスト", "§1", false, location_ct);

    private String name;
    private int matches;
    private boolean ff;
    private int planttime;
    private int defusetime;
    private int exploretime;

    public Bomb(String name) {
        this.name = name;
        this.matches = 15; // tmp
        this.ff = false;
        this.planttime = 3; // tmp
        this.defusetime = 10; // tmp
        this.exploretime = 40; // tmp
    }
}
