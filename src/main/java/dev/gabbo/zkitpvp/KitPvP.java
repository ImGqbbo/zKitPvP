package dev.gabbo.zkitpvp;

import dev.gabbo.zkitpvp.blocks.BlockListener;
import dev.gabbo.zkitpvp.blocks.BlockTask;
import dev.gabbo.zkitpvp.commands.impl.DropSettingsCommand;
import dev.gabbo.zkitpvp.commands.impl.MainCommand;
import dev.gabbo.zkitpvp.commands.impl.SpawnCommand;
import dev.gabbo.zkitpvp.data.PlayerDataManager;
import dev.gabbo.zkitpvp.inventory.InventoryListener;
import dev.gabbo.zkitpvp.listeners.PlayerListener;
import dev.gabbo.zkitpvp.placeholders.MainPlaceholder;
import dev.gabbo.zkitpvp.tablist.TabUpdater;
import dev.gabbo.zkitpvp.tasks.GeneralTask;
import dev.gabbo.zkitpvp.tasks.SaveTask;
import dev.gabbo.zkitpvp.utils.FileManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class KitPvP extends JavaPlugin {

    @Override
    public void onEnable() {
        instance = this;
        fileManager = new FileManager(instance);

        Arrays.asList(new MainCommand(), new SpawnCommand(), new DropSettingsCommand())
                .forEach(command -> getCommand(command.getName()).setExecutor(command));

        Arrays.asList(new BlockListener(), new PlayerListener(), new InventoryListener())
                .forEach(event -> Bukkit.getPluginManager().registerEvents(event, KitPvP.getInstance()));

        dataManager = new PlayerDataManager();
        blockManager = new BlockTask();

        new MainPlaceholder().register();

        if (fileManager.getConfig().getBoolean("tab-list.enabled")) {
            Bukkit.getScheduler().runTaskTimer(this, new TabUpdater(), 10L, 10L);
        }

        Bukkit.getScheduler().runTaskTimer(this, saveManager = new SaveTask(), 1L, 1L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(KitPvP.getInstance(), new GeneralTask(), 2L, 2L);

        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }

        RegisteredServiceProvider<Economy> service = getServer().getServicesManager().getRegistration(Economy.class);
        if (service == null) {
            return;
        }

        economy = service.getProvider();
    }

    public void reloadConfiguration() {
        fileManager = new FileManager(instance);
    }

    @Override
    public void onDisable() {
        saveManager.run();
    }

    private static KitPvP instance;

    public static KitPvP getInstance() {
        return instance;
    }

    private static PlayerDataManager dataManager;

    public static PlayerDataManager getDataManager() {
        return dataManager;
    }

    private static BlockTask blockManager;

    public static BlockTask getBlockManager() {
        return blockManager;
    }

    private static SaveTask saveManager;

    public static SaveTask getSaveManager() {
        return saveManager;
    }

    private static FileManager fileManager;

    public static FileManager getFileManager() {
        return fileManager;
    }

    private static Economy economy;

    public static Economy getEconomy() {
        return economy;
    }
}
