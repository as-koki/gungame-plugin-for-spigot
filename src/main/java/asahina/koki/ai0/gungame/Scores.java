package asahina.koki.ai0.gungame;
import java.util.List;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Scores {
    public ArrayList<Score> scores = new ArrayList<Score>();

    public Scores() {
    }

    public void addPlayer(Player player) {
        this.scores.add(new Score(player));
    }

    public boolean hasPlayer(Player player) {
        for (Score p : this.scores) {
            if (p.getPlayer() == player) {
                return true;
            }
        }
        return false;
    }

    public Score getScore(Player player) {
        for (Score p : this.scores) {
            if (p.getPlayer() == player) {
                return p;
            }
        }
        return null;
    }

    public boolean removePlayer(Player player) {
        Score scoreToRemove = null;
        for (Score score : this.scores) {
            if (score.getPlayer() == player) {
                scoreToRemove = score;
                break;
            }
        }
        
        if (scoreToRemove != null) {
            this.scores.remove(scoreToRemove);
            return true;
        }
        
        return false;
    }

}

class Score {
    
    private Player player;
    private int kill;
    private int death;
    private int assist;
    private int balance;

    public Score(Player player) {
        this.player = player;
        this.kill = 0;
        this.death = 0;
        this.assist = 0;
        this.balance = 0;
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getKill() {
        return this.kill;
    }

    public int getDeath() {
        return this.death;
    }

    public int getAssist() {
        return this.assist;
    }

    public int getBalance() {
        return this.balance;
    }

    public void setKill(int val) {
        this.kill = val;
    }
    
    public void setDeath(int val) {
        this.death = val;
    }

    public void setAssist(int val) {
        this.assist = val;
    }
    
    public void updateBalance(int amount) {
        this.balance += amount;
    }

    public void resetScore() {
        this.kill = 0;
        this.death = 0;
        this.assist = 0;
        this.balance = 0;
    }

}

