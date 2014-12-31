/*
 * Copyright © 2014-2015 Paul Waslowski <freack1208@gmail.com>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package me.freack100.pigpoop;

import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Level;

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
        config.addDefault("drop.id", Material.STONE.name());
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
        Material id = Material.getMaterial(config.getString("drop.id").toUpperCase().replace(" ", "_"));
        int amount = config.getInt("drop.amount");
        short damage = (short) config.getInt("drop.damage");
        byte data = (byte) config.getInt("drop.data");
        try {
            drop = new ItemStack(id, amount, damage, data);
        } catch(Exception e){
            //Something went wrong when creating the itemstack :O
            getLogger().log(Level.SEVERE,"Could not load plugin because of malfunctioning drop, please fix that ASAP. (It might be the 'id' field, be sure that it's a correct item name!)");
            getLogger().log(Level.SEVERE,"Disabling...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
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

@EventHandler
	public void onFeed(PlayerInteractEntityEvent evt){
		if (!(evt.getRightClicked().getType() == EntityType.COW))
			return;

		Cow cow = (Cow) evt.getRightClicked();

		if (evt.getPlayer().getInventory().getItemInHand() == null)
			return;

		if (evt.getPlayer().getInventory().getItemInHand() != new ItemStack(
				Material.RED_MUSHROOM))
			return;

		evt.getRightClicked().getLocation().getWorld().spawnEntity(evt
				.getRightClicked().getLocation(), EntityType.MUSHROOM_COW);
		evt.getPlayer().getInventory().removeItem(new ItemStack(
				Material.RED_MUSHROOM, 1));
		cow.setHealth(0);
	}
}


