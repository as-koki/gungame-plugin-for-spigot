package asahina.koki.ai0.gungame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.Color;
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
    File configFile;
    YamlConfiguration config;

    public Reader(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        plugin.saveDefaultConfig();
        configFile = new File(plugin.getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public gameInfo readGameSetting(String name) {
        gameInfo info = new gameInfo();

        if (!configFile.exists()) {
            plugin.getLogger().warning("config.ymlが見つかりませんでした。デフォルトの設定を使用します。");
            plugin.saveResource("config.yml", false); // プラグインのjar内にあるデフォルトのconfig.ymlを保存
            config = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("config.yml")));
        }

        info.setName(name);
        info.setMaxGameCount(config.getInt(info.getName() + ".matchpoint"));
        info.setFf(config.getBoolean(info.getName() + "ff"));
        info.setRoundTime(config.getInt(info.getName() + ".roundtime"));
        info.setPlantTime(config.getInt(info.getName() + ".planttime"));     
        info.setDefuseTime(config.getInt(info.getName() + ".defusetime"));
        info.setExploreTime(config.getInt(info.getName() + ".exploretime"));

        List<Double> t_xyz = config.getDoubleList(info.getName() + ".t");
        info.setTspawn(new Location(Bukkit.getWorld(info.getName()), t_xyz.get(0), t_xyz.get(1), t_xyz.get(2)));
        
        List<Double> ct_xyz = config.getDoubleList(info.getName() + ".ct");
        info.setCTspawn(new Location(Bukkit.getWorld(info.getName()), ct_xyz.get(0), ct_xyz.get(1), ct_xyz.get(2)));

        List<Double> A_xyz = config.getDoubleList(info.getName() + ".bombsiteA");
        info.setA(new Location(Bukkit.getWorld(info.getName()), A_xyz.get(0), A_xyz.get(1), A_xyz.get(2)));

        List<Double> B_xyz = config.getDoubleList(info.getName() + ".bombsiteB");
        info.setB(new Location(Bukkit.getWorld(info.getName()), B_xyz.get(0), B_xyz.get(1), B_xyz.get(2)));
        printGameInfo(info);
        return info;
  }


  private void printGameInfo(gameInfo info) {
    logger.info("Game Name: " + info.getName());
    logger.info("Match Point: " + info.getMaxGameCount());
    logger.info("Friendly Fire: " + info.getFf());
    logger.info("Round Time: " + info.getRoundTime());
    logger.info("Plant Time: " + info.getPlantTime());
    logger.info("Defuse Time: " + info.getDefuseTime());
    logger.info("Explore Time: " + info.getExploreTime());
    logger.info("T Spawn: " + info.getTspawn());
    logger.info("CT Spawn: " + info.getCTspawn());
    logger.info("Bombsite A: " + info.getA());
    logger.info("Bombsite B: " + info.getB());
    logger.info("T Team: " + info.getT().getName());
    logger.info("CT Team: " + info.getCT().getName());
}
}

class gameInfo {
    private String name;
    private boolean ff;
    private Gteam t = new Gteam(name+"t", "§4", ff);
    private Gteam ct = new Gteam(name+"ct", "§3", ff);
    private int roundtime;//
    private int planttime;//
    private int defusetime;//
    private int exploretime;//
    private Location tSpawn;//
    private Location ctSpawn;//
    private Location A;//
    private Location B;//
    private int status;//
    private int gameCount;//
    private int maxGameCount;
    private int bombStatus;
    private int tScore;
    private Scores scores = new Scores();
    private int ctScore;
    private List<Player> inGamePlayers = new ArrayList<>();
    private String bombSite = null;

    public List<Player> getInGamePlayers() {
        return this.inGamePlayers;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBombSite() {
        return this.bombSite;
    }

    public void setBombSite(String site) {
        this.bombSite = site;
    } 

    public String getName() {
        return this.name;
    }

    public Scores getScores() {
        return this.scores;
    }

    public void setFf(boolean ff) {
        this.ff = ff;
    }

    public boolean getFf() {
        return this.ff;
    }

    public void setT(Gteam t) {
        this.t = t;
    }

    public Gteam getT() {
        return this.t;
    }

    public void setCT(Gteam ct) {
        this.ct = ct;
    }

    public Gteam getCT() {
        return this.ct;
    }

    public void setMaxGameCount(int count) {
        this.maxGameCount = count;
    }

    public int getMaxGameCount() {
        return this.maxGameCount;
    }

    public void setTScore(int score) {
        this.tScore = score;
    }
    public int getTScore() {
        return this.tScore;
    }

    public void setCTScore(int score) {
        this.ctScore = score;
    }

    public int getCTScore() {
        return this.ctScore;
    }

    public void setBombStatus(int status) {
        this.bombStatus = status;
    }

    public int getBombStatus() {
        return this.bombStatus;
    }
    
    public void setRoundTime(int roundtime) {
        this.roundtime = roundtime;
    }

    public int getRoundTime() {
        return this.roundtime;
    }

    public void setPlantTime(int planttime) {
        this.planttime = planttime;
    }

    public int getPlantTime() {
        return this.planttime;
    }

    public void setDefuseTime(int time) {
        this.defusetime = time;
    }

    public int getDefuseTime() {
        return this.defusetime;
    }

    public void setExploreTime(int time) {
        this.exploretime = time;
    }

    public int getExploreTime() {
        return this.exploretime;
    }

    public void setTspawn(Location location) {
        this.tSpawn = location;
    }

    public Location getTspawn() {
        return this.tSpawn;
    }

    public void setCTspawn(Location location) {
        this.ctSpawn = location;
    }

    public Location getCTspawn() {
        return this.ctSpawn;
    }

    public void setA(Location location) {
        this.A = location;
    }

    public Location getA() {
        return this.A;
    }

    public void setB(Location location) {
        this.B = location;
    }

    public Location getB() {
        return this.B;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public int getGameCount() {
        return this.gameCount;
    }

    public void setGameCount(int count) {
        this.gameCount = count;
    }
}
