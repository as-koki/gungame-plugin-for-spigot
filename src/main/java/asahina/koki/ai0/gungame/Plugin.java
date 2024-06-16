package asahina.koki.ai0.gungame;
import org.bukkit.event.Listener;
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
          if (args[0].equalsIgnoreCase("team")) {
            if (args.length < 2) {
              sender.sendMessage("team <create/add/remove/bots> <player/num bots>");
              return true;
            }
            else if (args[1].equalsIgnoreCase("make")) {
              if (args.length < 3) {
                sender.sendMessage("team make <team>");
                return true;
              }
              else {
                Player player = (Player)sender;
                Location location1 = player.getLocation();
                team1 = new Gteam(args[2], "§c", false, location1);
                return true;
              }
            }
            else if (args[1].equalsIgnoreCase("check")) {
              Player player = (Player)sender;
              team1.checkteam(player);
              
            }

              // TODO: retreat info from conf(name of team, allow ff, ...)
            
            else if (args[1].equalsIgnoreCase("add")) {
              sender.sendMessage("add");
              // TODO: something add impl
              Player player = Bukkit.getPlayerExact(args[2]);
              if (args.length < 3 || player == null) {
                sender.sendMessage("team add <オンラインのプレイヤー>");
                return true;
              }
              else if (player.isOnline()) {
                cs.giveWeapon(player, "DEAGLE", 1);
                return true;
              }
            }
            else if (args[1].equalsIgnoreCase("remove")) {
              sender.sendMessage("remove");
              // TODO: something remove impl
              return true;
            }
            else {
              sender.sendMessage("team <add/remove/bots> <player/num bots>");
              return false;
            }
          }
        }
      }
      return true;
  }
}