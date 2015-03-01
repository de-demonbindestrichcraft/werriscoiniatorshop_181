/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.demonbindestrichcraft.lib.bukkit.wbukkitlib.items;

import de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.files.Config;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ABC
 */
public class ItemBackwardsCompatibility {

    private static Map<String, String> items = new ConcurrentHashMap<String, String>();
    private static Map<String, String> items2 = new ConcurrentHashMap<String, String>();
    private static boolean isInit = false;
    private static File file = new File("olditems.map");
    private static File file2 = new File("olditems2.map");
    private static Config olditems = null;
    private static Config olditems2 = null;
    private static boolean toold = false;

    public static int getTypeId(ItemStack itemStack) {
        if (!isInit) {
            init();
        }
        String get = items2.get(itemStack.getType().name());
        if (get != null) {
            try {
                int id = Integer.parseInt(get);
                return id;
            } catch (Throwable ex) {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public static Material getType(int typeId) {
        if (!isInit) {
            init();
        }
        String get = items.get("" + typeId);
        if (get != null) {
            try {
                Material valueOf = Material.valueOf(get);
                if (valueOf == null) {
                    return Material.AIR;
                }
                return valueOf;
            } catch (Throwable ex) {
                return Material.AIR;
            }
        } else {
            return Material.AIR;
        }
    }

    public static ItemStack getItemStack(int typeId, int amount, int durability) {
        short n = getShort(durability);
        ItemStack itemStack = getItemStack(typeId, amount, n);
        return itemStack;
    }

    public static ItemStack getItemStack(int typeId, int amount, short durability) {
        ItemStack itemStack = getItemStack(typeId, amount);
        itemStack.setDurability(durability);
        return itemStack;
    }

    public static ItemStack getItemStack(int typeId, int amount) {
        ItemStack itemStack = getItemStack(typeId);
        itemStack.setAmount(amount);
        return itemStack;
    }

    public static ItemStack getItemStack(int typeId) {
        Material type = getType(typeId);
        ItemStack itemStack = new ItemStack(type);
        return itemStack;
    }

    public static short getShort(int n) {
        short value = n > Short.MAX_VALUE ? Short.MAX_VALUE : n < Short.MIN_VALUE ? Short.MIN_VALUE : (short) n;
        return value;
    }

    private synchronized static void init() {
        if (!file.exists()) {
            items.put("0", "AIR");
            items2.put("AIR", "0");
            olditems = new Config(file);
            olditems.update(items);
            olditems.save("=");
            olditems2 = new Config(file2);
            olditems2.update(items2);
            olditems2.save("=");
            myInit();
            if (!toold) {
                olditems.update(items);
                olditems.save("=");
                olditems2.update(items2);
                olditems2.save("=");
            }
        } else {
            olditems = new Config(file);
            olditems.load(file, "=");
            items.putAll(olditems.getCopyOfProperties());
            olditems2 = new Config(file2);
            olditems2.load(file2, "=");
            items2.putAll(olditems2.getCopyOfProperties());
        }
        isInit = true;
    }

    public static synchronized void add(ItemStack itemStack) {
        if (toold) {
            return;
        }
        try {
            items.put("" + itemStack.getTypeId(), itemStack.getType().name());
            toold = false;
            olditems.update(items);
            olditems.save("=");
            items2.put("" + itemStack.getType().name(), "" + itemStack.getTypeId());
            olditems2.update(items2);
            olditems2.save("=");
        } catch (Throwable ex) {
            toold = true;
        }
    }

    public static synchronized void remove(ItemStack itemStack) {
        if (toold) {
            return;
        }
        try {
            items.remove("" + itemStack.getTypeId());
            toold = false;
            items2.remove(itemStack.getType().name());
            olditems.update(items);
            olditems.save("=");
            olditems2.update(items2);
            olditems2.save("=");
        } catch (Throwable ex) {
            toold = true;
        }
    }

    private static void myInit() {
        if (toold) {
            return;
        }
        try {
            Material[] values = Material.values();
            for (Material value : values) {
                items.put("" + value.getId(), value.name());
                items2.put(value.name(), "" + value.getId());
            }
            toold = false;
            olditems.update(items);
            olditems.save("=");
            olditems2.update(items2);
            olditems2.save("=");
        } catch (Throwable ex) {
            toold = true;
        }
    }
    
    public static boolean isValidItemStack(ItemStack itemStack)
    {
        if(!(itemStack instanceof ItemStack))
            return false;
        if(itemStack.getType()==Material.AIR)
        {
            return false;
        }
        return true;
    }
}
