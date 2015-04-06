package myshops;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.*;
import myshops.commands.CommandsEveryone;
import myshops.handlers.PlayerTracker;
import myshops.proxies.LocalizationProxy;
import myshops.storage.StorageHandler;
import myshops.utils.Constants;
import mytown.core.utils.command.CommandManager;
import mytown.core.utils.config.ConfigProcessor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

@Mod(modid = Constants.MODID, name = Constants.MODNAME, version = Constants.VERSION, dependencies = Constants.DEPENDENCIES, acceptableRemoteVersions = "*")
public class MyShops {
    public Configuration config;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent ev) {
        Constants.CONFIG_FOLDER = ev.getModConfigurationDirectory() + "/MyShops/";

        // Read Configs
        config = new Configuration(new File(Constants.CONFIG_FOLDER, "MyTown.cfg"));
        ConfigProcessor.load(config, Config.class);

        LocalizationProxy.load();

        registerHandlers();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent ev) {
        CommandManager.registerCommands(CommandsEveryone.class);

        StorageHandler.instance().load(); // TODO Move this if necessary
    }

    public void serverStopping(FMLServerStoppingEvent ev) {
        StorageHandler.instance().save();
    }

    private void registerHandlers() {
        PlayerTracker playerTracker = new PlayerTracker();

        FMLCommonHandler.instance().bus().register(playerTracker);
        MinecraftForge.EVENT_BUS.register(playerTracker);
    }
}
