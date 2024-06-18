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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.Location;
import com.shampaggon.crackshot.CSUtility;
import org.bukkit.command.Command;

/*
 * gungame java plugin
 */
public class Plugin extends JavaPlugin implements Listener, CommandExecutor
{
  private static final Logger LOGGER=Logger.getLogger("gungame");
  public ScoreboardManager manager;
  public static Scoreboard board;
  public Gteam team1;
  CSUtility cs = new CSUtility();



  public void onEnable()
  {
    getCommand("gg").setExecutor(this);
    manager = Bukkit.getScoreboardManager();
    board = manager.getNewScoreboard();
  }

  public void onDisable()
  {
    LOGGER.info("gungame disabled");
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (command.getName().equalsIgnoreCase("gg")) { //親コマンドの判定
        if (args.length == 0) {
          sender.sendMessage("使い方: /gg <arg0> <arg1> <arg2> ...");
          return true;
        }  
        else {
          if (args[0].equalsIgnoreCase("bomb")) {
            if (args[1].equalsIgnoreCase("create")) {
              Bomb bomb1 = new Bomb(args[2]);
              Bukkit.getPluginManager().registerEvents(bomb1, this);
              return true;
            }
            if (args[1].equalsIgnoreCase("join")) {
              Bomb game = Bomb.getBomb(args[2]); // name of game
              if (game == null) {
                sender.sendMessage("そのゲームは存在しません");
                return true;
              }
              Player player = Bukkit.getServer().getPlayer(args[3]); // name of player who join
              game.addPlayer(player, args[4]); // terrorist: "terro", counter terrorist: dont care
              return true;
            }
            else if (args[1].equalsIgnoreCase("leave")) {
              Bomb game = Bomb.getBomb(args[2]); // name of game
              if (game == null) {
                sender.sendMessage("そのゲームは存在しません");
                return true;
              }
              Player player = Bukkit.getServer().getPlayer(args[3]); // name of player who join
              game.removePlayer(player, args[4]); // terrorist: "terro", counter terrorist: dont care
              return true;
            }
            else if (args[1].equalsIgnoreCase("start")) {
              Bomb game = Bomb.getBomb(args[2]);
              if (game == null) {
                sender.sendMessage("そのゲームは存在しません");
                return true;
              }
              game.start();
              return true;

            }
            return true;
          }

        }
      }
      return true;
  }
}