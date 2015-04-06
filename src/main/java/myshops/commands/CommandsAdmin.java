package myshops.commands;

import cpw.mods.fml.common.registry.GameRegistry;
import myshops.proxies.LocalizationProxy;
import myshops.storage.entities.ShopType;
import myshops.utils.Constants;
import myshops.utils.MyShopUtils;
import myshops.utils.exceptions.MyShopsWrongUsageException;
import mytown.core.utils.command.Command;
import mytown.core.utils.command.CommandNode;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class CommandsAdmin extends Commands {
    @Command(
            name = "myshopsadmin",
            permission = "myshops.adm.cmd",
            alias = {"myshops", "shop"})
    public static void shopsAdminCommand(ICommandSender sender, List<String> args) {
        callSubFunctions(sender, args, "myshops.adm.cmd");
    }

    @CommandNode(
            name = "itemname",
            permission = "myshops.adm.cmd.shop.itemname",
            parentName = "myshops.adm.cmd",
            nonPlayers = false)
    public static void shopItemNameCommand(ICommandSender sender, List<String> args) {
        if(sender instanceof EntityPlayer) {
            ItemStack stack = ((EntityPlayer) sender).getHeldItem();
            if(stack == null)
                return;
            String itemName = GameRegistry.findUniqueIdentifierFor(stack.getItem()).toString();
            if(stack.getItemDamage() != 0)
                itemName += ":" + stack.getItemDamage();
            sendMessageBackToSender(sender, LocalizationProxy.getLocalization().getLocalization("mytown.adm.cmd.shop.itemname", itemName));
        }
    }

    @CommandNode(
            name = "create",
            permission = "myshops.adm.cmd.create",
            parentName = "myshops.adm.cmd",
            nonPlayers = false)
    public static void shopCreateCommand(ICommandSender sender, List<String> args) {
        // /shops create sell|buy|sellBuy amount buyPrice sellPrice
        if(args.size() < 3)
            throw new MyShopsWrongUsageException("myshops.adm.cmd.usage.shop.create");

        ShopType shopType = ShopType.fromString(args.get(0));
        if(shopType == null)
            throw new MyShopsWrongUsageException("myshops.adm.cmd.usage.shop.create");

        if(!MyShopUtils.tryParseInt(args.get(1)) || Integer.parseInt(args.get(1)) <= 0)
            throw new MyShopsWrongUsageException("myshops.cmd.err.notPositiveInteger", args.get(1));

        if(!MyShopUtils.tryParseInt(args.get(2)) || Integer.parseInt(args.get(2)) <= 0)
            throw new MyShopsWrongUsageException("myshops.cmd.err.notPositiveInteger", args.get(2));

        EntityPlayer player = (EntityPlayer) sender;

        if(player.inventory.getCurrentItem() == null)
            throw new MyShopsWrongUsageException("myshops.adm.cmd.err.item");

        int amount = Integer.parseInt(args.get(1));
        int buyPrice = Integer.parseInt(args.get(2));
        int sellPrice = buyPrice;
        if (args.size() >= 4) {
            sellPrice = Integer.parseInt(args.get(3));
        }

        startShopCreation(player, player.inventory.getCurrentItem(), amount, buyPrice, sellPrice, shopType);
    }

    private static boolean startShopCreation(EntityPlayer player, ItemStack itemStack, int amount, int buyPrice, int sellPrice, ShopType type) {
        ItemStack signShop = new ItemStack(Items.wooden_hoe);
        signShop.setStackDisplayName(Constants.SIGN_SHOP_NAME);
        NBTTagList lore = new NBTTagList();
        lore.appendTag(new NBTTagString(EnumChatFormatting.DARK_AQUA + "AmountAndCost: " + amount + " | " + buyPrice + " | " + sellPrice));
        lore.appendTag(new NBTTagString(EnumChatFormatting.DARK_AQUA + "Type: " + type));
        lore.appendTag(new NBTTagString(EnumChatFormatting.DARK_AQUA + "Item: " + MyShopUtils.nameFromItemStack(itemStack)));
        signShop.getTagCompound().getCompoundTag("display").setTag("Lore", lore);

        return player.inventory.addItemStackToInventory(signShop);
    }
}
