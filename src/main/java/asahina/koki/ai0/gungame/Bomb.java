package asahina.koki.ai0.gungame;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.World;
import javax.swing.text.html.parser.Entity;
import org.bukkit.entity.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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
    private Plugin plugin;
    private gameInfo info;

    public Bomb(Plugin plugin, gameInfo info) {
        this.info = info;
        this.plugin = plugin;
    }

    public void resetGame() {
        info.setStatus(0); // 0: prepare, 1: ingame 2: end
        info.setBombStatus(0); // 0: nonplanted 1: planted, 2: explored 3: defused
        info.setGameCount(info.getGameCount()+1);

        if (info.getGameCount() < info.getMaxGameCount()) {
            sendGameMessage("次のラウンドが開始されます");
            new BukkitRunnable() {
                int time = 10;
                @Override
                public void run() {
                    sendGameMessage(String.valueOf(time));
                    if (time > 0) {info.getT().setSpawn(info.getTspawn());
                        time--;
                    }
                    else {
                        startRound();
                        this.cancel();
                    }
                }
            }.runTaskTimer(Bukkit.getPluginManager().getPlugin("gungame"), 0L, 20L); // 20L = 1 second (20 ticks)
        }

        else {
            // endGame();
            sendGameMessage("ゲームが終了しました");
            if (info.getTScore() > info.getCTScore()) {
                sendGameMessage("このゲームはテロリストの勝利");
                
                // reward
            }
            else if (info.getTScore() < info.getCTScore()) {
                sendGameMessage("このゲームはカウンターテロリストの勝利");
                
                // reward
            }
            else {
                sendGameMessage("同点");
                
            }
        }
    }

    public void gameTimer() {
        new BukkitRunnable() {
            int time = info.getRoundTime();
            @Override
            public void run() {
                if (time > 0) {
                    time--;                
                }
                else {
                    sendGameMessage("カウンターテロリストの勝利");
                    info.setStatus(2);
                    resetGame();
                    this.cancel();
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("gungame"), 0L, 20L); // 20L = 1 second (20 ticks)
    }

    public void startGame() {
        if (info.getStatus() == 0) {
            sendGameMessage("まもなくゲームが開始されます");
            startRound();
            return;
        }
    }

    public void startRound() {
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
                    sendGameMessage(String.valueOf(info.getGameCount()+1) + "ラウンド目が開始されました");
                    playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 3);
                    info.getT().setGameMode(GameMode.SURVIVAL);
                    info.getCT().setGameMode(GameMode.SURVIVAL);
                    info.getT().teleportTeam(info.getTspawn());
                    info.getCT().teleportTeam(info.getCTspawn());
                    info.setStatus(1);
                    info.getT().setSpawn(info.getTspawn());
                    info.getCT().setSpawn(info.getCTspawn());
                    info.getT().setWalkSpeed(0.15f);
                    info.getCT().setWalkSpeed(0.15f);
                    gameTimer();
                    this.cancel();
                }
            
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("gungame"), 0L, 20L); // 20L = 1 second (20 ticks)
    }

    // if player was killed by weapon
    @EventHandler
    public void onWeaponDamage(WeaponDamageEntityEvent e) {
        if (e.getVictim() instanceof Player && info.getStatus() == 1 && inGame((Player) e.getPlayer()) && inGame((Player) e.getVictim())) { // when deploying, shoud change from "e.getPlayer()" to "e.getVictim()"
            Bukkit.getServer().getPluginManager().callEvent(new EntityDamageByEntityEvent(e.getPlayer(), e.getVictim(), EntityDamageEvent.DamageCause.ENTITY_ATTACK, e.getDamage())); 
            org.bukkit.entity.Entity v = e.getVictim();
            if ((((Damageable) v).getHealth() - e.getDamage() <= 0)) {
                // Bukkit.getServer().getPluginManager().callEvent(new EntityDeathEvent(e.getPlayer(), null, 0, 0, 0, 0, null)); なぜか動かん
                Player player = (Player) e.getVictim();
                Player killer = e.getPlayer();
                String weapon = e.getWeaponTitle();
                sendGameMessage(killer.getName() + " [" + weapon + "] " + player.getName());
                playerDied(killer, player);
                info.getScores().getScore(killer).increKill();
                info.getScores().getScore(player).increDeath();
            }
        }
    }

    // if player was killed by melee or vannila item
    @EventHandler
    public void onPlayerDie(PlayerDeathEvent pde) {
        if (info.getStatus() == 1 && inGame(pde.getEntity().getKiller()) && inGame((Player) pde.getEntity())) { // when deploying, shoud change from "pde.getEntity().getKiller()" to "pde.getEntity()"
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
        info.getScores().getScore(killer).increKill();
        info.getScores().getScore(victim).increDeath();
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> victim.spigot().respawn(), 2);
        printScores();
    }
    /*
     * 特定アイテムでブロック右クリ→座標取得→サイト内の場合一定時間右クリでSpawning minesで設置
     * 爆弾設置座標+-5くらいの範囲で一定時間特定アイテム使用→解除
     */
    @EventHandler
    public void onPlayerUse(PlayerInteractEvent pie) {
        if (pie.getAction() == Action.RIGHT_CLICK_BLOCK && pie.getClickedBlock().getType() != null) {
            Location location = pie.getClickedBlock().getLocation();
            if (isPlant(location)!=null) {
                if (info.getStatus() == 1 && info.getBombStatus() == 0 && info.getT().hasPlayer(pie.getPlayer())) {
                    plantBomb(pie.getPlayer(), isPlant(location));
                }
                
                else if (info.getStatus()==1 && info.getBombStatus()==1 && info.getCT().hasPlayer(pie.getPlayer()) && isPlant(location) == info.getBombSite()) {
                    defuseBomb(pie.getPlayer());
                }
            }
        }
        
    }

    public String isPlant(Location location) {
        final double range = 2.0;
        boolean inRangeA = 
            location.getX() >= info.getA().getX() - range && location.getX() <= info.getA().getX() + range &&
            location.getY() >= info.getA().getY() - range && location.getY() <= info.getA().getY() + range &&
            location.getZ() >= info.getA().getZ() - range && location.getZ() <= info.getA().getZ() + range;
        boolean inRangeB = 
            location.getX() >= info.getB().getX() - range && location.getX() <= info.getB().getX() + range &&
            location.getY() >= info.getB().getY() - range && location.getY() <= info.getB().getY() + range &&
            location.getZ() >= info.getB().getZ() - range && location.getZ() <= info.getB().getZ() + range;
        if (inRangeA) {return "A";}
        else if (inRangeB) {return "B";}
        return null;
    }

    public void plantBomb(Player planter, String site) {
        if (planter.getInventory().getItemInMainHand().getType() == Material.IRON_AXE) {
            String name = planter.getPlayer().getName();
            info.setBombStatus(1);
            info.setBombSite(site);
            info.getCT().sendTeamMessage(name + "が爆弾を設置しました!");
            planter.sendMessage("爆弾の設置が完了しました!");
            info.getT().sendTeamMessage("設置完了");
            bombCount();


        }

    }

    public void bombCount() {
        new BukkitRunnable() {
            int countdown = info.getExploreTime();
            @Override
            public void run() {
                countdown--;
                if (countdown > 0) {
                    if (info.getBombStatus()==3) {
                        sendGameMessage("カウンターテロリストの勝利");
                        info.setCTScore(info.getCTScore()+1);
                        info.setStatus(2);
                        info.setBombStatus(3);
                        resetGame();
                        this.cancel();
                    }
                if (countdown == 10) {info.getCT().sendTeamMessage("残り" + String.valueOf(countdown) + "秒");}
                }
                else {
                    sendGameMessage("テロリストの勝利");
                    explode(info.getBombSite());
                    info.setTScore(info.getTScore()+1);
                    info.setStatus(2);
                    resetGame();
                    this.cancel();
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("gungame"), 0L, 20L); // 20L = 1 second (20 ticks)
    }

    public void defuseBomb(Player defuser) {
        if (defuser.getInventory().getItemInMainHand().getType() == Material.IRON_HOE) {
            info.setBombStatus(3);
            info.setBombSite(null);
        }
    }

    public void playSound(Sound sound, int volume, int pitch) {
        for (Player player : info.getT().getPlayers()) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
        for (Player player : info.getCT().getPlayers()) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }
    
    public void sendGameMessage(String str) {
        info.getT().sendTeamMessage(info.getT().getColor() + str);
        info.getCT().sendTeamMessage(info.getCT().getColor() + str);
    }

    public boolean addPlayer(Player player, String team) { 
        if (!hasPlayer(player)) {
            if (team.equals("t")) {
                info.getT().addPlayer(player);
                info.getInGamePlayers().add(player);
                info.getScores().addPlayer(player); // add new score to scores array
                return true;
            }
            else if (team.equals("ct")) {
                info.getCT().addPlayer(player);
                info.getInGamePlayers().add(player);
                info.getScores().addPlayer(player);
                return true;
            }
            return false;
        }
        return false;
    }

    public boolean removePlayer(Player player, String team) {
        if (hasPlayer(player)) {
            if (team == "t") {
                info.getT().removePlayer(player);
                info.getInGamePlayers().remove(player);
                return true;
            }
            else if (team == "ct") {
                info.getCT().removePlayer(player);
                info.getInGamePlayers().remove(player);
                return true;
            }
            return false;
        }
        return false;
    }

    public Gteam getTeam(String str) {
        if (str.equals("t")) {
          return info.getT();
        }
        else {
            return info.getCT();
        }
      }

    public String getName() {
        return info.getName();
    }

    public boolean inGame(Player player) {
        if( info.getCT().hasPlayer(player) || info.getT().hasPlayer(player) ) {
            return true;
        }
        return false;
    }

    public boolean hasPlayer (Player player) {
        for (Player p : info.getInGamePlayers()) {
            if (p == player) {
                return true;
            }
        }
        return false;
    }

    public void printScores () {
        for (Player p : info.getInGamePlayers()) {
            Score s = info.getScores().getScore(p);
            int k = s.getKill();
            int d = s.getDeath();
            int a = s.getAssist();
            int b = s.getBalance();
            System.out.println("name: " + p.getName() + "\n" + "kill: " + k + "\n" + "death: " + d + "\n" + "assist: " + a + "\n" + "balance: " + b);
        }
    }

    public void endGame() {
        info.setStatus(0); // 0: prepare, 1: ingame 2: end
        info.setBombStatus(0); // 0: nonplanted 1: planted, 2: explored 3: defused
        info.setGameCount(0);
    }

    public void explode(String site) {
        World w = info.getA().getWorld();
        Location location;
        if (site.equals("A")) {location = info.getA();}
        else  {location = info.getB();}
        new BukkitRunnable() {
            int halfsec = 5;
            @Override
            public void run() {
                if (halfsec>0) {
                    w.createExplosion(location.getX(), location.getY(), location.getZ(), 10F, false, false);
                    for (int i = 0; i < 3000; i++) {
                        double offsetX = (Math.random() - 0.5) * 60;
                        double offsetY = (Math.random() - 0.5) * 60;
                        double offsetZ = (Math.random() - 0.5) * 60;
                        Location particleLoc = location.clone().add(offsetX, offsetY, offsetZ);
                        w.spawnParticle(Particle.EXPLOSION_LARGE, particleLoc, 1);
                    }
                    halfsec--;}
                else {
                    this.cancel();
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("gungame"), 0L, 10L); // 20L = 1 second (20 ticks)

    }

    public void won() {

    }

    public void lost() {
        
    }
    
}