package me.freack100.pigpoop;

import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Created by Freack100.
 */
public class PigPoop extends JavaPlugin implements Listener, CommandExecutor {

    private HashMap<Entity, Integer> cooldownList = new HashMap();
    private int cooldown;
    private ItemStack drop;
    private boolean usePermissions;
    private List<String> disabled;
    private boolean useParticles;
    private boolean dropItem;
    private boolean useSound;
    private boolean pushEntity;
    private double pushFactor;

    private int taskID;

    private FileConfiguration config;

    @Override
    public void onEnable() {
        config = this.getConfig();

        //Setting the defaults
        config.addDefault("drop.id", 1);
        config.addDefault("drop.amount", 1);
        config.addDefault("drop.damage", (short) 0);
        config.addDefault("drop.data", (byte) 0);
        config.addDefault("usePermissions", true);
        config.addDefault("disabledAnimals", new ArrayList<String>());
        config.addDefault("cooldown", 60);
        config.addDefault("useParticles", true);
        config.addDefault("useSound", true);
        config.addDefault("pushEntity", true);
        config.addDefault("pushFactor", 0.5);
        config.addDefault("dropItem", true);

        config.options().copyDefaults(true);
        saveConfig();

        //Getting the config values
        int id = config.getInt("drop.id");
        int amount = config.getInt("drop.amount");
        short damage = (short) config.getInt("drop.damage");
        byte data = (byte) config.getInt("drop.data");

        drop = new ItemStack(id, amount, damage, data);
        usePermissions = config.getBoolean("usePermissions");
        disabled = config.getStringList("disabledAnimals");
        cooldown = config.getInt("cooldown");
        useParticles = config.getBoolean("useParticles");
        useSound = config.getBoolean("useSound");
        pushEntity = config.getBoolean("pushEntity");
        pushFactor = config.getDouble("pushFactor");
        dropItem = config.getBoolean("dropItem");


        //Start Scheduler
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                Set<Entity> keySet = cooldownList.keySet();
                List<Entity> clearMe = new ArrayList();
                for (Entity entity : keySet) {
                    int current = cooldownList.get(entity);
                    current--;
                    if (current == 0) {
                        clearMe.add(entity);
                    } else {
                        cooldownList.replace(entity, current);
                    }
                }
                for (Entity ent : clearMe) cooldownList.remove(ent);
            }
        }, 0L, 20L);

        //Register Listener
        Bukkit.getPluginManager().registerEvents(this, this);

        //Register Commands
        //getCommand("pigpoop").setExecutor(this);

    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTask(taskID);
    }

    @EventHandler
    public void on(PlayerInteractEntityEvent e) {
        //Debugging
        //System.out.println(e.getRightClicked().getType().name());
        if (!disabled.contains(e.getRightClicked().getType().name())) {
            if (!cooldownList.containsKey(e.getRightClicked())) {
                if (usePermissions && !e.getPlayer().hasPermission("pigpoop.use")) return;
                Entity entity = e.getRightClicked();
                World world = entity.getWorld();

                if (pushEntity)
                    entity.setVelocity(entity.getLocation().getDirection().multiply(pushFactor).normalize());
                if (useParticles) world.playEffect(entity.getLocation(), Effect.SMOKE, 10, 10);
                if (useSound) world.playSound(entity.getLocation(), Sound.FIREWORK_LAUNCH, 1F, 1F);
                if (dropItem) world.dropItemNaturally(entity.getLocation(), drop);

                cooldownList.put(entity, cooldown);

            }
        }
    }


}
