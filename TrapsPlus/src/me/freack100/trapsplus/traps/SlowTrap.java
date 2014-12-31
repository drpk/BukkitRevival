/*
 * Copyright © 2014-2015 Paul Waslowski <freack1208@gmail.com>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the LICENSE file for more details.
 */

package me.freack100.trapsplus.traps;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SlowTrap extends Trap {

    public SlowTrap() {
        super("Slow");
    }

    @Override
    public void initConfig(FileConfiguration config) {
        config.addDefault("slow.duration", 2);
        config.addDefault("slow.amplifier", 1);
        config.addDefault("slow.ambient", true);

        duration = config.getInt("slow.duration");
        amplifier = config.getInt("slow.amplifier");
        ambient = config.getBoolean("slow.ambient");
    }

    @Override
    public void trigger(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration*20, amplifier, ambient));
    }
}
