package myshops.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import myshops.proxies.EconomyProxy;
import myshops.proxies.LocalizationProxy;
import myshops.utils.Constants;
import myshops.storage.entities.Shop;
import myshops.storage.entities.ShopType;
import myshops.storage.StorageHandler;
import mytown.core.ChatUtils;
import mytown.core.Utils;
import mytown.core.utils.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class PlayerTracker {
    @SubscribeEvent
    public void onItemUse(PlayerInteractEvent ev) {
        if (ev.entityPlayer.worldObj.isRemote)
            return;

        ItemStack currentStack = ev.entityPlayer.inventory.getCurrentItem();
        if (currentStack == null)
            return;

        if (ev.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            if (currentStack.getItem().equals(Items.wooden_hoe) && currentStack.getDisplayName().equals(Constants.SIGN_SHOP_NAME)) {
                ForgeDirection direction = ForgeDirection.getOrientation(ev.face);
                int x = ev.x + direction.offsetX;
                int y = ev.y + direction.offsetY;
                int z = ev.z + direction.offsetZ;

                if(ev.world.getBlock(x, y, z) != Blocks.air)
                    return;

                if(direction == ForgeDirection.DOWN || ev.face == 1) {
                    int i1 = MathHelper.floor_double((double) ((ev.entityPlayer.rotationYaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
                    ev.world.setBlock(x, y, z, Blocks.standing_sign, i1, 3);
                } else {
                    ev.world.setBlock(x, y, z, Blocks.wall_sign, ev.face, 3);
                }

                TileEntitySign te = (TileEntitySign)ev.world.getTileEntity(x, y, z);

                NBTTagList tagList = currentStack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
                int amount = Integer.parseInt(tagList.getStringTagAt(0).split(" ")[1]);
                int price = Integer.parseInt(tagList.getStringTagAt(0).split(" ")[3]);
                ShopType shopType = ShopType.fromString(tagList.getStringTagAt(1).split(" ")[1]);
                String itemString = tagList.getStringTagAt(2).split(" ")[1];

                Shop shop = new Shop(itemString, amount, price, price, shopType, ev.world.provider.dimensionId, x, y, z); // TODO Separate buy and sell prices
                StorageHandler.instance().addShop(shop);

                String[] signText = new String[4];
                signText[0] = EnumChatFormatting.BLACK + "[ " + shopType.toString() + " ]";
                for(int i = 0; i < (15 - signText[0].length()) / 2; i++)
                    signText[0] = " " + signText[0];

                signText[1] = (amount > 1 ? (amount + "x") : "") + EnumChatFormatting.DARK_BLUE;
                signText[1] += signText[1].length() + shop.itemStack.getDisplayName().length() > 15 ? shop.itemStack.getDisplayName().substring(0, 15 - signText[1].length()) : shop.itemStack.getDisplayName();
                for(int i = 0; i < (15 - signText[1].length()) / 2; i++)
                    signText[1] = " " + signText[1];

                signText[2] = "" + EnumChatFormatting.GOLD + price;
                signText[3] = " ";

                te.signText = signText;
            }
        }
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent ev) {
        if (ev.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK || ev.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            Block block = ev.world.getBlock(ev.x, ev.y, ev.z);
            if (block != Blocks.wall_sign && block != Blocks.standing_sign) return;

            if (ev.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK && ev.entityPlayer.isSneaking() && Utils.isOp(ev.entityPlayer)) {
                ev.world.setBlock(ev.x, ev.y, ev.z, Blocks.air);
                StorageHandler.instance().removeShop(ev.world.provider.dimensionId, ev.x, ev.y, ev.z);
            } else {
                Shop shop = StorageHandler.instance().getShop(ev.world.provider.dimensionId, ev.x, ev.y, ev.z);
                if (shop == null) return;

                // Left Click Buy
                if (ev.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
                    if(EconomyProxy.economy().takeMoneyFromPlayer(ev.entityPlayer, shop.buyPrice)) {
                        PlayerUtils.giveItemToPlayer(ev.entityPlayer, shop.itemStack, shop.getAmount());
                        ChatUtils.sendChat(ev.entityPlayer, LocalizationProxy.getLocalization().getLocalization("myshops.notification.shop.buy.success", shop.getAmount(), shop.itemStack.getDisplayName(), shop.buyPrice, EconomyProxy.economy().getCurrency(shop.buyPrice)));
                    } else {
                        ChatUtils.sendChat(ev.entityPlayer, LocalizationProxy.getLocalization().getLocalization("myshops.notification.shop.buy.failed", shop.buyPrice, EconomyProxy.economy().getCurrency(shop.buyPrice)));
                    }
                // Right Click Sell
                } else if (ev.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
                    if(PlayerUtils.takeItemFromPlayer(ev.entityPlayer, shop.itemStack, shop.getAmount())) {
                        EconomyProxy.economy().giveMoneyToPlayer(ev.entityPlayer, shop.sellPrice);
                        ChatUtils.sendChat(ev.entityPlayer, LocalizationProxy.getLocalization().getLocalization("myshops.notification.shop.sell.success", shop.getAmount(), shop.itemStack.getDisplayName(), shop.sellPrice, EconomyProxy.economy().getCurrency(shop.sellPrice)));
                    } else {
                        ChatUtils.sendChat(ev.entityPlayer, LocalizationProxy.getLocalization().getLocalization("myshops.notification.shop.sell.failed", shop.getAmount(), shop.itemStack.getDisplayName(), shop.sellPrice, EconomyProxy.economy().getCurrency(shop.sellPrice)));
                    }
                }

                ev.setCanceled(true);
            }
        }
    }
}
