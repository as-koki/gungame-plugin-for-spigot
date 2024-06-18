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
import java.util.ArrayList;

/*
 * gungame java plugin
 */
public class Plugin extends JavaPlugin implements Listener, CommandExecutor
{
  private static final Logger LOGGER=Logger.getLogger("gungame");
  public ScoreboardManager manager;
  public static Scoreboard board;
  CSUtility cs = new CSUtility();
  BombGameManager games;
  public void onEnable()
  {
    getCommand("gg").setExecutor(this);
    manager = Bukkit.getScoreboardManager();
    board = manager.getNewScoreboard();
    games = new BombGameManager();
    // Bukkit.getPluginManager().registerEvents(bomb, this);
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
              if (games.createGame(args[2])) {
                sender.sendMessage("爆破ゲームを作成しました!");
                return true;
              }
              else {
                sender.sendMessage("爆破ゲームの作成に失敗しました");
                return true;
              }
            }
            if (args[1].equalsIgnoreCase("join")) { 
              if (!games.hasGame(args[2])) {// name of game
                sender.sendMessage("そのゲームは存在しません");
                return true;
              }
              Player player = Bukkit.getServer().getPlayer(args[3]); // name of player who join
              if (games.getBomb(args[2]).addPlayer(player, args[4])){ // terrorist: "t", counter terrorist: "ct", random: null
                sender.sendMessage(player.getName() + "がゲームに追加されました");
                return true;
              }
              else {
                sender.sendMessage("プレイヤ―の追加に失敗しました");
              }
              return true;
            }
            else if (args[1].equalsIgnoreCase("leave")) {
              if (!games.hasGame(args[2])) {// name of game
                sender.sendMessage("そのゲームは存在しません");
                return true;
              }
              Player player = Bukkit.getServer().getPlayer(args[3]); // name of player who join
              games.getBomb(args[2]).removePlayer(player, args[4]); // terrorist: "terro", counter terrorist: dont care
              return true;
            }
            else if (args[1].equalsIgnoreCase("start")) {
              if (!games.hasGame(args[2])) {// name of game
                sender.sendMessage("そのゲームは存在しません");
                return true;
              }
              games.getBomb(args[2]).start();
              return true;
            }
            else if (args[1].equalsIgnoreCase("list")) {
              ArrayList<String> bombs = games.gameList();
              for (String a : bombs) {
                sender.sendMessage(a);
              }
            }
          }

        }
      }
      return true;
  }
}