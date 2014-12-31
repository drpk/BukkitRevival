/*
 * Copyright © 2014-2015 Paul Waslowski <freack1208@gmail.com>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the LICENSE file for more details.
 */

package me.freack100.trapsplus;

import me.freack100.trapsplus.listener.ClickListener;
import me.freack100.trapsplus.listener.MoveListener;
import me.freack100.trapsplus.traps.Trap;
import me.freack100.trapsplus.traps.Traps;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TrapsPlus extends JavaPlugin {

    private HashMap<Location,Traps> traps = new HashMap();
    public FileConfiguration config;
    public FileConfiguration savedTraps;

    public HashMap<Location,Traps> getTraps(){
        return traps;
    }

    @Override
    public void onEnable(){
        //Set up config
        config = getConfig();
        config.options().copyDefaults(true);

        for(Traps trap : Traps.values()){
            trap.getTrap().initConfig(config);
        }

        saveConfig();
        reloadConfig();

        if(!new File(getDataFolder() + "/traps.yml").exists()) try{new File(getDataFolder()+"/traps.yml").createNewFile();}catch(Exception e){};
        savedTraps = YamlConfiguration.loadConfiguration(new File(getDataFolder() + "/traps.yml"));

        //Load traps
        for(String key : savedTraps.getKeys(false)){
           traps.put(stringToLoc(key),Traps.valueOf(savedTraps.getString(key).toUpperCase()));
        }

        //Register listeners
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new ClickListener(this),this);
        pm.registerEvents(new MoveListener(this),this);
    }

    @Override
    public void onDisable(){
        saveConfig();
        try {
            File file = new File(getDataFolder() + "/traps.yml");
            file.delete();
            file.createNewFile();
            savedTraps = YamlConfiguration.loadConfiguration(file);
            Set<Map.Entry<Location,Traps>> entrySet = traps.entrySet();
            for(Map.Entry<Location,Traps> entry : entrySet){
                savedTraps.set(locToString(entry.getKey()),entry.getValue().getTrap().getName());
            }
            savedTraps.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void addTrap(Location loc,Traps trap){
        traps.put(loc,trap);
    }

    public void removeTrap(Location loc){
        traps.remove(loc);
    }

    private String locToString(Location loc){
        return ""+loc.getWorld().getName()+","+loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ()+"";
    }

    private Location stringToLoc(String str){
        String[] splitted = str.split(",");
        return new Location(Bukkit.getWorld(splitted[0]),Integer.valueOf(splitted[1]),Integer.valueOf(splitted[2]),Integer.valueOf(splitted[3]));
    }

}
