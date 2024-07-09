package com.ashkiano.worldbackup;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WorldBackup extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("WorldBackup has been enabled.");
        Metrics metrics = new Metrics(this, 22409);
        this.getLogger().info("Thank you for using the WorldBackup plugin! If you enjoy using this plugin, please consider making a donation to support the development. You can donate at: https://donate.ashkiano.com");
    }

    @Override
    public void onDisable() {
        getLogger().info("WorldBackup has been disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("backup")) {
            if (args.length == 0) {
                sender.sendMessage("Please specify the world name to backup.");
                return true;
            }

            String worldName = args[0];
            World world = getServer().getWorld(worldName);

            if (world == null) {
                sender.sendMessage("World not found!");
                return true;
            }

            if (sender.hasPermission("worldbackup.backup")) {
                new Thread(() -> {
                    File worldFolder = world.getWorldFolder();

                    if (!worldFolder.exists()) {
                        sender.sendMessage("World folder not found!");
                        return;
                    }

                    String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                    File backupFolder = new File(getServer().getWorldContainer(), worldName + "_backup_" + timeStamp);

                    try {
                        copyFolder(worldFolder.toPath(), backupFolder.toPath());
                        sender.sendMessage("World has been backed up successfully.");
                    } catch (IOException e) {
                        sender.sendMessage("An error occurred while backing up the world.");
                        e.printStackTrace();
                    }
                }).start();
            } else {
                sender.sendMessage("You do not have permission to use this command.");
            }
            return true;
        }
        return false;
    }

    private void copyFolder(Path src, Path dest) throws IOException {
        Files.walk(src).forEach(source -> {
            try {
                Path destination = dest.resolve(src.relativize(source));
                Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}