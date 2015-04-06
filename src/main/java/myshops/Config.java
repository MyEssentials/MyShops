package myshops;

import cpw.mods.fml.common.registry.GameRegistry;
import mytown.core.utils.config.ConfigProperty;
import net.minecraft.init.Items;

public class Config {
    @ConfigProperty(category = "general", name = "Localization", comment = "Localization file without file extension.\\nLoaded from config/MyTown/localization/ first, then from the jar, then finally will fallback to en_US if needed.")
    public static String localization = "en_US";

    @ConfigProperty(category = "cost", name = "costItem", comment = "The item which is used for buying items from shops. Use $ForgeEssentials if you want to use ForgeEssentials economy or $Vault if you want Vault economy.")
    public static String costItemName = GameRegistry.findUniqueIdentifierFor(Items.diamond).toString();
}
