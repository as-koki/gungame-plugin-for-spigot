package asahina.koki.ai0.gungame;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.html.parser.Entity;
import org.bukkit.entity.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import com.shampaggon.crackshot.CSUtility;
import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;

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
    private int gametime;

    public Bomb(String name) {
        this.name = name;
        this.matches = 15; // tmp
        this.ff = false;
        this.planttime = 3; // tmp
        this.defusetime = 10; // tmp
        this.exploretime = 40; // tmp
        this.status = 0; // 0: prepare, 1: ingame 2: end
        this.bombstatus = 0; // 0: nonplanted 1: planted, 2: explored 3: defused
        this.gametime = 115;
        bombs.add(this);
    }

    public void addPlayer(Player player) { 
        this.t.addPlayer(player);
        this.ct.addPlayer(player);
    }

    public void gameTimer() {
        new BukkitRunnable() {
            int time = gametime;
            @Override
            public void run() {
                if (time > 0) {
                    time--;                
                }
                else {
                    sendGameMessage("カウンターテロリストの勝利");
                    status = 2;
                    this.cancel();
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("gungame"), 0L, 20L); // 20L = 1 second (20 ticks)
    }

    public void start() {
        sendGameMessage("まもなくゲームが開始されます");
        
        new BukkitRunnable() {
            int countdown = 5;

            @Override
            public void run() {
                if (countdown > 0) {
                    sendGameMessage(String.valueOf(countdown));
                    playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
                    countdown --;
                }
                else {
                    sendGameMessage("ゲームが開始されました");
                    playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 3);
                    t.teleport();
                    ct.teleport();
                    status = 1;
                    gameTimer();
                    this.cancel();
                }
            
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("gungame"), 0L, 20L); // 20L = 1 second (20 ticks)
    }

    // if player was killed by weapon
    @EventHandler
    public void onWeaponDamage(WeaponDamageEntityEvent e) {
        // inGame(e.getPlayer()) && 
        if (status == 1) {
            Bukkit.getServer().getPluginManager().callEvent(new EntityDamageByEntityEvent(e.getPlayer(), e.getVictim(), EntityDamageEvent.DamageCause.ENTITY_ATTACK, e.getDamage()));
            org.bukkit.entity.Entity v = e.getVictim();
            if ((((Damageable) v).getHealth() - e.getDamage() <= 0)) {
                // Bukkit.getServer().getPluginManager().callEvent(new EntityDeathEvent(e.getPlayer(), null, 0, 0, 0, 0, null)); なぜか動かん
                String player = e.getVictim().getName();
                String killer = e.getPlayer().getName();
                String weapon = e.getWeaponTitle();
                sendGameMessage(killer + "[" + weapon + "]" + player);
                // TODO: Something score fluctuation
            }
        }
    }

    // if player was killed by melee or vannila item
    @EventHandler
    public void onPlayerDie(PlayerDeathEvent pde) {
        if (this.status == 1 && inGame(pde.getEntity())) {
            String player = pde.getEntity().getName();
            String killer = pde.getEntity().getKiller().getName();
            sendGameMessage(killer + " ︻デ═一 " + player);
            // TODO: Something score fluctuation
        }
    }
    /*
     * 特定アイテムでブロック右クリ→座標取得→サイト内の場合一定時間右クリでSpawning minesで設置
     * 爆弾設置座標+-5くらいの範囲で一定時間シフト→解除
     */
    @EventHandler
    public void onPlayerUse(PlayerInteractEvent pie) {
        if (this.status == 1 && this.bombstatus == 0 && ct.hasPlayer(pie.getPlayer())) {
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
                            if (bombstatus==3) {
                                sendGameMessage("カウンターテロリストの勝利");
                                status = 2;
                                this.cancel();
                            }
                        if (countdown == 10) {ct.sendTeamMessage("残り" + String.valueOf(countdown) + "秒");}
                        }
                        else {
                            sendGameMessage("テロリストの勝利");
                            this.cancel();
                        }
                    }
                }.runTaskTimer(Bukkit.getPluginManager().getPlugin("gungame"), 0L, 20L); // 20L = 1 second (20 ticks)


            }

        } 
    }

    public void playSound(Sound sound, int volume, int pitch) {
        for (Player player : t.getPlayers()) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
        for (Player player : ct.getPlayers()) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }
    
    public void sendGameMessage(String str) {
        t.sendTeamMessage(t.getColor() + str);
        ct.sendTeamMessage(ct.getColor() + str);
    }

    public String getName() {
        return this.name;
    }

    public boolean inGame(Player player) {
        if( ct.hasPlayer(player) || t.hasPlayer(player) ) {
            return true;
        }
        return false;
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
