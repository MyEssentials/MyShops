package myshops.storage;

import myshops.storage.entities.Shop;
import myshops.storage.entities.ShopType;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.DimensionManager;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles storing Shops
 *
 * @author Joe Goett
 */
public class StorageHandler {
    private int version = 1;
    private File saveDir;
    private File shopsFile;
    private NBTTagCompound saveTag;
    private Map<String, Shop> shops = new HashMap<String, Shop>();

    public Shop getShop(int dim, int x, int y, int z) {
        return shops.get(String.format(Shop.shopKey, dim, x, y, z));
    }

    public Collection<Shop> getShops() {
        return shops.values();
    }

    public void addShop(Shop shop) {
        shops.put(shop.getKey(), shop);

        this.save();
    }

    public void removeShop(int dim, int x, int y, int z) {
        shops.remove(String.format(Shop.shopKey, dim, x, y, z));

        this.save();
    }

    public void removeShop(Shop shop) {
        shops.remove(shop.getKey());

        this.save();
    }

    public void load() {
        saveDir = new File(DimensionManager.getCurrentSaveRootDirectory(), "MyShops");
        if (!saveDir.exists())
            saveDir.mkdir();

        shopsFile = new File(saveDir, "shops.dat");

        boolean dataLoaded = false;

        try {
            if (shopsFile.exists() && shopsFile.length() > 0) {
                DataInputStream din = new DataInputStream(new FileInputStream(shopsFile));
                saveTag = CompressedStreamTools.readCompressed(din);
                din.close();
                dataLoaded = true;
            }
        } catch(Exception ex) {
        }

        if (!dataLoaded) {
            saveTag = new NBTTagCompound();
        }

        readFromNBT(saveTag);
    }

    public void save() {
        writeToNBT(saveTag);

        try {
            if (!shopsFile.exists()) {
                shopsFile.createNewFile();
            }

            DataOutputStream dout = new DataOutputStream(new FileOutputStream(shopsFile));
            CompressedStreamTools.writeCompressed(saveTag, dout);
            dout.close();
        } catch(Exception ex) {
        }
    }

    /**
     * Writes all the Shops to the NBTTagCompound
     *
     * @param nbt
     */
    private void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("version", this.version);

        NBTTagList list = new NBTTagList();

        for (Shop shop : shops.values()) {
            NBTTagCompound shopTag = shopToNBT(shop);
            if (shopTag == null) continue;
            list.appendTag(shopTag);
        }

        nbt.setTag("shops", list);
    }

    /**
     * Reads Shops from the NBTTagCompound
     * @param nbt
     */
    private void readFromNBT(NBTTagCompound nbt) {
        NBTTagList tagList = nbt.getTagList("shops", 10);

        for (int i=0; i<tagList.tagCount(); i++) {
            Shop shop = NBTToShop(tagList.getCompoundTagAt(i));
            if (shop == null) continue;
            shops.put(shop.getKey(), shop);
        }
    }

    /**
     * Turns a Shop into a NBTTagCompound
     *
     * @param shop
     * @return
     */
    private NBTTagCompound shopToNBT(Shop shop) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("dim", shop.dim);
        nbt.setInteger("x", shop.x);
        nbt.setInteger("y", shop.y);
        nbt.setInteger("z", shop.z);
        nbt.setInteger("sellPrice", shop.sellPrice);
        nbt.setInteger("buyPrice", shop.buyPrice);
        nbt.setString("type", shop.type.toString());
        nbt.setString("item", shop.itemString);
        nbt.setInteger("amount", shop.itemStack.stackSize);
        return nbt;
    }

    /**
     * Turns an NBTTagCompound into a Shop
     *
     * @param nbt
     * @return
     */
    private Shop NBTToShop(NBTTagCompound nbt) {
        int dim = nbt.getInteger("dim"),
                x = nbt.getInteger("x"),
                y = nbt.getInteger("y"),
                z = nbt.getInteger("z"),
                sellPrice = nbt.getInteger("sellPrice"),
                buyPrice = nbt.getInteger("buyPrice"),
                amount = nbt.getInteger("amount");
        ShopType type = ShopType.fromString(nbt.getString("type"));
        String itemString = nbt.getString("item");

        return new Shop(itemString, amount, buyPrice, sellPrice, type,  dim, x, y, z);
    }

    private static StorageHandler instance = null;

    /**
     * Gets the StorageHandler instance
     * @return
     */
    public static StorageHandler instance() {
        if (instance == null) {
            instance = new StorageHandler();
        }
        return instance;
    }
}
