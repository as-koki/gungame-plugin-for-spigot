package asahina.koki.ai0.gungame;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;


import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Gteam {
    private String name;
    private String color;
    private Location spawn;
    private List<Player> players;
    private static List<Team> teams = new ArrayList<Team>(); 
    private List<Player> alivePlayer = new ArrayList<Player>();
    private List<Player> deadPlayer = new ArrayList<Player>();
    // Objective objective = Plugin.board.registerNewObjective(name, "air", "aaa", RenderType.INTEGER);

  public Gteam(String name, String color, boolean ff) {
    this.name = name;
    this.color = color;
    this.players = new ArrayList<Player>();
    Team team = null;
    if (Plugin.board.getTeam(name) == null) {
      team = Plugin.board.registerNewTeam(name);
      teams.add(team);
    }
    team.setAllowFriendlyFire(ff);
    team.setPrefix("[" + name + "]");
  }

  public void addAlivePlayer(Player player) {
    alivePlayer.add(player);
  }

  public void removeAlivePlayer(Player player) {
    alivePlayer.remove(player);
  }

  public boolean isAlive(Player player) {
    for(Player p : alivePlayer) {
      if(player == p) {return true;}
    }
    return false;
  }

  public int countAlive() {
    return alivePlayer.size();
  }

  public void addDeadPlayer(Player player) {
    deadPlayer.add(player);
  }

  public String getName() {
    return this.name;
  }

  public void setWalkSpeed(float value) {
    for(Player p : players) {
      p.setWalkSpeed(value);
    }
  }

  public void setSpawn(Location A) {
    this.spawn = A; 
    for (Player p : this.players) {
      p.setBedSpawnLocation(A, true);
    }
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

  public String getColor() {
    return this.color;
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

  public boolean hasPlayer(Player player) {
    for (Player p : players) {
      if (p == player) {
        return true;
      }
    }
    return false;
  }

  public static Team getTeam(String name) {
    for (Team team : teams) {
        if (team.getName().equalsIgnoreCase(name)) {
            return team;
        }
    }
    return null;
  }

  public void setGameMode(org.bukkit.GameMode mode) {
    for (Player player : players) {
      player.setGameMode(mode);
    }
  }

  public void teleportTeam(Location location) {
    for (Player player : this.players) {
      player.teleport(location);
    }
  }
}
