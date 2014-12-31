/*
 * Copyright © 2014-2015 Paul Waslowski <freack1208@gmail.com>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the LICENSE file for more details.
 */

package me.freack100.trapsplus.listener;

import me.freack100.trapsplus.TrapsPlus;
import me.freack100.trapsplus.traps.Traps;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {

    TrapsPlus plugin;

    public MoveListener(TrapsPlus p) {
        this.plugin = p;
    }

    @EventHandler
    public void on(PlayerMoveEvent e) {
        Block block = e.getTo().add(0,-1,0).getBlock();
        Material mat = block.getType();
        if(mat == Material.OBSIDIAN) {
            if (plugin.getTraps().containsKey(block.getLocation())) {
                //sendMessage(e.getPlayer(), plugin.getTraps().get(block.getLocation()));
                plugin.getTraps().get(block.getLocation()).getTrap().trigger(e.getPlayer());
                plugin.removeTrap(block.getLocation());
            }
        }
    }

    private void sendMessage(Player p,Traps trap){
        p.sendMessage("You activated a trap!");
    }


}
