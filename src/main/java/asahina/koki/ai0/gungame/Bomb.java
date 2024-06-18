package asahina.koki.ai0.gungame;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.html.parser.Entity;
import org.bukkit.entity.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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

    private Gteam t;
    private Gteam ct;
    private String name;
    private boolean ff;
    private int planttime;
    private int defusetime;
    private int exploretime;
    private int status;
    private int bombstatus;
    private int gametime;
    private List<Player> inGamePlayers;

    public Bomb(String name) {
        this.name = name;
        this.ff = false;
        this.planttime = 3; // tmp
        this.defusetime = 10; // tmp
        this.exploretime = 40; // tmp
        this.status = 0; // 0: prepare, 1: ingame 2: end
        this.bombstatus = 0; // 0: nonplanted 1: planted, 2: explored 3: defused
        this.gametime = 115;
        this.inGamePlayers = new ArrayList<>();
        this.t = new Gteam(name +"テロリスト", "§c", false, location_t);
        this.ct = new Gteam(name +"カウンターテロリスト", "§1", false, location_ct);
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
        if (status == 1 && inGame((Player) e.getPlayer())) { // when deploying, shoud change from "e.getPlayer()" to "e.getVictim()"
            Bukkit.getServer().getPluginManager().callEvent(new EntityDamageByEntityEvent(e.getPlayer(), e.getVictim(), EntityDamageEvent.DamageCause.ENTITY_ATTACK, e.getDamage())); 
            org.bukkit.entity.Entity v = e.getVictim();
            if ((((Damageable) v).getHealth() - e.getDamage() <= 0)) {
                // Bukkit.getServer().getPluginManager().callEvent(new EntityDeathEvent(e.getPlayer(), null, 0, 0, 0, 0, null)); なぜか動かん
                Player player = (Player) e.getVictim();
                Player killer = e.getPlayer();
                String weapon = e.getWeaponTitle();
                sendGameMessage(killer.getName() + " [" + weapon + "] " + player.getName());
                playerDied(killer, player);
                // TODO: Something score fluctuation
            }
        }
    }

    // if player was killed by melee or vannila item
    @EventHandler
    public void onPlayerDie(PlayerDeathEvent pde) {
        if (this.status == 1 && inGame(pde.getEntity().getKiller())) { // when deploying, shoud change from "pde.getEntity().getKiller()" to "pde.getEntity()"
            Player player = pde.getEntity();
            Player killer = pde.getEntity().getKiller();
            String weapon = killer.getInventory().getItemInMainHand().getType().name();
            sendGameMessage(killer.getName() + " [" + weapon + "] " + player.getName());
            playerDied(killer, player);
            // TODO: Something score fluctuation
        }
    }

    public void playerDied(Player killer, Player victim) {
        victim.setGameMode(GameMode.SPECTATOR);
        killer.sendMessage("kill + 1");
    }
    /*
     * 特定アイテムでブロック右クリ→座標取得→サイト内の場合一定時間右クリでSpawning minesで設置
     * 爆弾設置座標+-5くらいの範囲で一定時間特定アイテム使用→解除
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

    public boolean addPlayer(Player player, String team) { 
        if (!hasPlayer(player)) {
            if (team.equals("t")) {
                t.addPlayer(player);
                inGamePlayers.add(player);
                return true;
            }
            else if (team.equals("ct")) {
                ct.addPlayer(player);
                inGamePlayers.add(player);
                return true;
            }
            return false;
        }
        return false;
    }

    public boolean removePlayer(Player player, String team) {
        if (hasPlayer(player)) {
            if (team == "t") {
                this.t.removePlayer(player);
                this.inGamePlayers.remove(player);
                return true;
            }
            else if (team == "ct") {
                this.ct.removePlayer(player);
                this.inGamePlayers.remove(player);
                return true;
            }
            return false;
        }
        return false;
    }


    public String getName() {
        return name;
    }

    public boolean inGame(Player player) {
        if( ct.hasPlayer(player) || t.hasPlayer(player) ) {
            return true;
        }
        return false;
    }

    public boolean hasPlayer (Player player) {
        for (Player p : inGamePlayers) {
            if (p == player) {
                return true;
            }
        }
        return false;
    }

    public Bomb getBomb(String str) {
        return this;
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
