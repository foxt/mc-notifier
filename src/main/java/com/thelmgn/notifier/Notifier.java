package com.thelmgn.notifier;

import org.bukkit.Effect;
import org.bukkit.Instrument;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Notifier extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Notifier is running!");
        getServer().getPluginManager().registerEvents(this,this);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("notify")) {
            if (args.length < 2) {
                sender.sendMessage("§c[Notifier] You're missing an argument. Use the following format:");
                return false;
            }
            String playerArg = args[0];
            Player player = (Player) sender; // java screams at me if I don't initialize this
            boolean found = false;
            for (Player p : getServer().getOnlinePlayers()) {
                if (p.getName().equals(playerArg)) {
                    player = p;
                    found = true;
                }
            }
            if (!found) {
                sender.sendMessage("§c[Notifier] No player matched the name '" + playerArg + "'. The argument requires the §lexact§r§c player name.");
                return false;
            }
            String[] messageArray = new String[args.length-1];
            System.arraycopy(args,1,messageArray,0,args.length-1);
            String message = String.join(" ", messageArray).trim();

            if (message.length() <= 0) {
                sender.sendMessage("§c[Notifier] You're missing the message argument. Use the following format:");
                return false;
            }
            BossBar messagebar = getServer().createBossBar(((Player) sender).getDisplayName() + ": " + message,
                                                            BarColor.BLUE, BarStyle.SOLID, BarFlag.PLAY_BOSS_MUSIC);
            messagebar.addPlayer(player);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
            Player finalPlayer = player;
            int task = this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
                public void run() {
                    if (messagebar.getProgress() <= 0.01) {
                        messagebar.removeAll();
                        messagebar.setVisible(false);
                        messagebar.removePlayer(finalPlayer);
                    } else {
                        messagebar.setProgress(messagebar.getProgress() - 0.01);
                    }

                }
            },0L, 1L);
            return true;
        }
        return false;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
