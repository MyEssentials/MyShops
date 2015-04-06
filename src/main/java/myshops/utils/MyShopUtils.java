package myshops.utils;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;

/**
 * @author Joe Goett, AfterWind
 */
public class MyShopUtils {
    /**
     * Returns whether or not the String can be parsed as an Integer
     */
    public static boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

    /**
     * Returns the unique identifier of given ItemStack
     */
    public static String nameFromItemStack(ItemStack itemStack) {
        String name = GameRegistry.findUniqueIdentifierFor(itemStack.getItem()).toString();
        if(itemStack.getItemDamage() != 0)
            name += ":" + itemStack.getItemDamage();
        return name;
    }
}
