package com.octanepvp.splityosis.octanechat.files;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

public class DataFile {

    private File file;
    private FileConfiguration config;

    public DataFile(File file) {
        this.file = file;
    }

    public void initialize(JavaPlugin plugin){
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        new BukkitRunnable(){
            @Override
            public void run() {
                save();
            }
        }.runTaskTimerAsynchronously(plugin, 20*60*5, 20*60*5);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void save(){
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reload(){
        config = YamlConfiguration.loadConfiguration(file);
    }
}
