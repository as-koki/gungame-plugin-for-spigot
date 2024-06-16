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
    this.players = new ArrayList<Player>();
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

  public Location getSpawn() {
    return this.spawn;
  }

  public List<Player> getPlayers() {
    return this.players;
  }

  public void sendTeamMessage(String msg) {
    for(Player player : this.players) {
      if (player.isOnline()) {
        player.sendMessage(msg);
      }
    }
  }

  public static Team getTeam(String name) {
    for (Team team : teams) {
        if (team.getName().equalsIgnoreCase(name)) {
            return team;
        }
    }
    return null;
}

  public void teleport() {
    for (Player player : this.players) {
      player.getInventory().clear();
      player.setExp(0.0F);
      player.teleport(this.spawn);
    }
  }
}
