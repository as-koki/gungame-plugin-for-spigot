package asahina.koki.ai0.gungame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.Location;
import com.shampaggon.crackshot.CSUtility;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import org.bukkit.command.Command;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Logger;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class Reader {
    private final JavaPlugin plugin;
    private final Logger logger;
    gameInfo info = new gameInfo();

    public Reader(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        plugin.saveDefaultConfig();
    }

    public void readLocation() {

    }

    public gameInfo readGameSetting(String name) {

        File configFile;
        YamlConfiguration config;
        configFile = new File(plugin.getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);

        if (!configFile.exists()) {
            plugin.getLogger().warning("config.ymlが見つかりませんでした。デフォルトの設定を使用します。");
            plugin.saveResource("config.yml", false); // プラグインのjar内にあるデフォルトのconfig.ymlを保存
            config = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("config.yml")));
        }

        info.name = name;
        info.matchpoint = config.getInt(info.name + ".matchpoint");
        info.ff = config.getBoolean(info.name + "ff");
        info.roundtime = config.getInt(info.name + ".roundtime");
        info.planttime = config.getInt(info.name + ".planttime");
        info.defusetime = config.getInt(info.name + ".defusetime");
        info.exploretime = config.getInt(info.name + ".exploretime");
        info.world = config.getString(info.name + ".world");

        List<Double> t_xyz = config.getDoubleList(info.name + ".t");
        info.t = new Location(Bukkit.getWorld(info.world), t_xyz.get(0), t_xyz.get(1), t_xyz.get(2));
        
        List<Double> ct_xyz = config.getDoubleList(info.name + ".ct");
        info.ct = new Location(Bukkit.getWorld(info.world), ct_xyz.get(0), ct_xyz.get(1), ct_xyz.get(2));
        
        List<Double> A_xyz = config.getDoubleList(info.name + ".bombsiteA");
        info.A = new Location(Bukkit.getWorld(info.world), A_xyz.get(0), A_xyz.get(1), A_xyz.get(2));

        List<Double> B_xyz = config.getDoubleList(info.name + ".bombsiteB");
        info.B = new Location(Bukkit.getWorld(info.world), B_xyz.get(0), B_xyz.get(1), B_xyz.get(2));

        return info;
  }
}

class gameInfo {
    public String name;
    public String world;
    public int matchpoint;
    public boolean ff;
    public int roundtime;
    public int planttime;
    public int defusetime;
    public int exploretime;
    public Location t;
    public Location ct;
    public Location A;
    public Location B;
}
