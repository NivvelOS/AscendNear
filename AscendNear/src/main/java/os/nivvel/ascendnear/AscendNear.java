package os.nivvel.ascendnear;

import os.nivvel.ascendnear.command.NearCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class AscendNear extends JavaPlugin {

    private static AscendNear instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getCommand("near").setExecutor(new NearCommand(this));
        getLogger().info("AscendNear enabled!");
    }

    public static AscendNear get() {
        return instance;
    }
}