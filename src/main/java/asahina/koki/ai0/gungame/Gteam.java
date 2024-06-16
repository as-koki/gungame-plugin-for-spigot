package asahina.koki.ai0.gungame;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Gteam {
    private String name;
    private String color;
    private Location spawn;
    private List<Player> players;
    public static List<Team> teams = new ArrayList<Team>(); 

  public Gteam(String name, String color, boolean ff, Location spawn) {
    this.name = name;
    this.color = color;
    this.spawn = spawn;
    Team team = null;
    if (Plugin.board.getTeam(name) == null) {
      team = Plugin.board.registerNewTeam(name);
      teams.add(team);
    }
    team.setAllowFriendlyFire(ff);
    team.setPrefix("[" + name + "]");
  }

  public void addPlayer(Player player) {
    this.players.add(player);
    Plugin.board.getTeam(this.name).addEntry(player.getName());
  }

  public void removePlayer(Player player) {
    this.players.remove(player);
    Plugin.board.getTeam(this.name).removeEntry(player.getName());
  }

  public void checkteam(Player player) {
    player.sendMessage("あなたのチームは" + Plugin.board.getTeam(this.name).getName() + "です。");
  }
}
