package asahina.koki.ai0.gungame;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.shampaggon.crackshot.events.WeaponPlaceMineEvent;

public class Bomb implements Listener{
    Location location_t = new Location(Bukkit.getWorld("world"), 1, 1, 1, 0, 0); // tmp
    Location location_ct = new Location(Bukkit.getWorld("world"), 1, 1, 1, 0, 0); // tmp
    // Location bombA = new Location(Bukkit.getWorld("world"), 1, 1, 1, 0, 0); // tmp  2地点で設定
    // Location bombB = new Location(Bukkit.getWorld("world"), 1, 1, 1, 0, 0); // tmp  2地点で設定

    private Gteam t = new Gteam("テロリスト", "§c", false, location_t);
    private Gteam ct = new Gteam("カウンターテロリスト", "§1", false, location_ct);

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
        this.bombstatus = 0; // 0: nonplanted 1: planted, 2: explored
    }

    public void start() {
        t.sendTeamMessage("まもなくゲームが開始されます");
        ct.sendTeamMessage("まもなくゲームが開始されます");
        for (int i = 5; i > 0; i--) {
            t.sendTeamMessage(String.valueOf(i));
            ct.sendTeamMessage(String.valueOf(i));
            try {
                Thread.sleep(1000);
            }
            catch(InterruptedException e) {
                System.out.println("got interrupted!");
            }
        }
        this.t.sendTeamMessage("ゲームが開始されました");
        this.ct.sendTeamMessage("ゲームが開始されました");
        // tp
        this.t.teleport();
        this.ct.teleport();
        // timer start
        this.status = 1;
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent pde) {
        if (this.status == 1) {
            String player = pde.getEntity().getName();
            String killer = pde.getEntity().getKiller().getName();
            this.ct.sendTeamMessage(killer + "︻デ═一" + player);
            this.t.sendTeamMessage(killer + "︻デ═一" + player);
            // TODO: Something score fluctuation
        }
    }

    @EventHandler
    public void onPlant(WeaponPlaceMineEvent wpm) {
        if (this.status == 1) {
            String planter = wpm.getPlayer().getName();
            String mine = wpm.getWeaponTitle();
            this.ct.sendTeamMessage(planter + "が" + mine + "を設置しました!");
            wpm.getPlayer().sendMessage(mine + "の設置が完了しました!");
            this.t.sendTeamMessage(mine + "設置完了");

            for (int i = this.exploretime; i > 0; i--) {
                // TODO: play sound
                if (this.bombstatus==1) {
                    this.t.sendTeamMessage("カウンターテロリストの勝利");
                    this.ct.sendTeamMessage("カウンターテロリストの勝利");
                    this.status = 2;
                }
            }
        }
    }



    public String getName() {
        return this.name;
    }

    public Bomb getBomb(String str) {
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
