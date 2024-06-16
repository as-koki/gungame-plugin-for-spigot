package asahina.koki.ai0.gungame;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import com.shampaggon.crackshot.events.WeaponPlaceMineEvent;

public class Bomb implements Listener {
    Location location_t = new Location(Bukkit.getWorld("world"), 1, 1, 1, 0, 0); // tmp
    Location location_ct = new Location(Bukkit.getWorld("world"), 1, 1, 1, 0, 0); // tmp
    // Location bombA = new Location(Bukkit.getWorld("world"), 1, 1, 1, 0, 0); // tmp  2地点で設定
    // Location bombB = new Location(Bukkit.getWorld("world"), 1, 1, 1, 0, 0); // tmp  2地点で設定

    private Gteam t = new Gteam("テロリスト", "§c", false, location_t);
    private Gteam ct = new Gteam("カウンターテロリスト", "§1", false, location_ct);
    public static List<Bomb> bombs = new ArrayList<Bomb>(); 
    private String name;
    private int matches;
    private boolean ff;
    private int planttime;
    private int defusetime;
    private int exploretime;
    private int status;
    private int bombstatus;

    public Bomb(String name) {
        this.name = name;
        this.matches = 15; // tmp
        this.ff = false;
        this.planttime = 3; // tmp
        this.defusetime = 10; // tmp
        this.exploretime = 40; // tmp
        this.status = 0; // 0: prepare, 1: ingame 2: end
        this.bombstatus = 0; // 0: nonplanted 1: planted, 2: explored 3: defused
        bombs.add(this);
    }

    public void addPlayer(Player player) { 
        this.t.addPlayer(player);
        this.ct.addPlayer(player);
    }

    public void start() {
        t.sendTeamMessage("まもなくゲームが開始されます");
        ct.sendTeamMessage("まもなくゲームが開始されます");
        
        new BukkitRunnable() {
            int countdown = 5;

            @Override
            public void run() {
                if (countdown > 0) {
                    t.sendTeamMessage(String.valueOf(countdown));
                    ct.sendTeamMessage(String.valueOf(countdown));
                    countdown --;
                }
                else {
                    t.sendTeamMessage("ゲームが開始されました");
                    ct.sendTeamMessage("ゲームが開始されました");
                    t.teleport();
                    ct.teleport();
                    status = 1;
                    this.cancel();
                }
            
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("gungame"), 0L, 20L); // 20L = 1 second (20 ticks)
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent pde) {
        if (this.status == 1) {
            String player = pde.getEntity().getName();
            String killer = pde.getEntity().getKiller().getName();
            
            this.ct.sendTeamMessage(killer + " ︻デ═一 " + player);
            this.t.sendTeamMessage(killer + " ︻デ═一 " + player);
            // TODO: Something score fluctuation
        }
    }
    /*
     * 特定アイテムでブロック右クリ→座標取得→サイト内の場合一定時間右クリでSpawning minesで設置
     * 爆弾設置座標+-5くらいの範囲で一定時間シフト→解除
     */
    @EventHandler
    public void onPlayerUse(PlayerInteractEvent pie) {
        if (this.status == 1 && this.bombstatus == 0) {
            Player player = pie.getPlayer();
            if (player.getInventory().getItemInMainHand().getType() == Material.IRON_AXE) {
                String planter = pie.getPlayer().getName();
                this.bombstatus = 1;
                this.ct.sendTeamMessage(planter + "が爆弾を設置しました!");
                pie.getPlayer().sendMessage("爆弾の設置が完了しました!");
                this.t.sendTeamMessage("設置完了");
    
                new BukkitRunnable() {
                    int countdown = exploretime;
                    @Override
                    public void run() {
                        countdown--;
                        if (countdown > 0) {
                            ct.sendTeamMessage("残り" + String.valueOf(countdown) + "秒");
                            if (bombstatus==3) {
                                t.sendTeamMessage("カウンターテロリストの勝利");
                                ct.sendTeamMessage("カウンターテロリストの勝利");
                                status = 2;
                                this.cancel();
                            }
                        }
                        else {
                            t.sendTeamMessage("テロリストの勝利");
                            ct.sendTeamMessage("テロリストの勝利");
                            this.cancel();
                        }
                    }
                }.runTaskTimer(Bukkit.getPluginManager().getPlugin("gungame"), 0L, 20L); // 20L = 1 second (20 ticks)


            }

        } 
    }
    

    public String getName() {
        return this.name;
    }

    public static Bomb getBomb(String str) {
        for (Bomb bomb : bombs) {
            if (bomb.getName().equalsIgnoreCase(str)) {
                return bomb;
            }
        }
        return null;
    }

    public void end() {
        status = 2;
    }

    public void defuse() {

    }

    public void explode() {

    }

    public void won() {

    }

    public void lost() {
        
    }
}
