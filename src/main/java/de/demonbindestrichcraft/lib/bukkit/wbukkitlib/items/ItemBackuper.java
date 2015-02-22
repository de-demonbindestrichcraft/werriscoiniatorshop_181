/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.demonbindestrichcraft.lib.bukkit.wbukkitlib.items;

import de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.sql.SqlType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ABC
 */
public class ItemBackuper {

    private static Map<String, VirtualPlayerInventory> playerInventorys = new HashMap<String, VirtualPlayerInventory>();
    private static List<String> players = new LinkedList<String>();
    private static boolean isInit = false;
    private static String sqlTypeOutString, host, port, datenbank, username, password, tableName;
    private static boolean debug = false;

    public static void init() {
        init("sqllite", "", "", "PlayerInventorys.db", "test1", "test", "PlayerInventorys");
    }

    public static void init(String sqlTypeOutString, String host, String port, String datenbank, String username, String password, String tableName) {
        if (isInit) {
            return;
        }
        ItemBackuper.sqlTypeOutString = sqlTypeOutString;
        ItemBackuper.host = host;
        ItemBackuper.port = port;
        ItemBackuper.datenbank = datenbank;
        ItemBackuper.username = username;
        ItemBackuper.password = password;
        ItemBackuper.tableName = tableName;
        SqlType sqlType = SqlType.getSqlTypeOutString(sqlTypeOutString);
        VirtualPlayerInventorys.createVirtualPlayerInventorysDb(sqlType, host, port, datenbank, username, password, tableName);
        isInit = true;
    }

    public static void backupItems(Logger logger) {
        backupItems(logger, playerInventorys);
        return;
    }

    public static void backupItems(Logger logger, Map<String, VirtualPlayerInventory> playerInventorys) {
        if (playerInventorys.isEmpty()) {
            return;
        }
        if (!isInit) {
            return;
        }
        Set<String> keySet = playerInventorys.keySet();
        List<VirtualPlayerInventory> virtualPlayerInventorys = new LinkedList<VirtualPlayerInventory>();
        for (String key : keySet) {
            virtualPlayerInventorys.add(playerInventorys.get(key));
        }
        debug(logger, virtualPlayerInventorys.toString());
        VirtualPlayerInventorys.addVirtualPlayerInventorys(logger, virtualPlayerInventorys);
        VirtualPlayerInventorys.updateVirtualPlayerInventorys(logger, virtualPlayerInventorys);
    }

    public static void restoreItems(Logger logger) {
        if (!isInit) {
            return;
        }
        Map<String, VirtualPlayerInventory> virtualPlayerInventorys = VirtualPlayerInventorys.getVirtualPlayerInventorys(logger);
        if (virtualPlayerInventorys == null) {
            return;
        }
        debug(logger, virtualPlayerInventorys.toString());
        playerInventorys.clear();
        playerInventorys.putAll(virtualPlayerInventorys);
        VirtualPlayerInventorys.removeVirtualPlayerInventorys(logger, VirtualPlayerInventorys.getVirtualPlayerInventorysAsListOutMap(logger, virtualPlayerInventorys));
    }

    public static Map<String, VirtualPlayerInventory> getPlayerInventorys() {
        return playerInventorys;
    }

    public static void putVirtualPlayerInventoryDirectInDb(Logger logger, String playerName, VirtualPlayerInventory virtualPlayerInventory) {
        putVirtualPlayerInventoryDirectInDb(logger, playerName, virtualPlayerInventory.getPlayerInventoryItems(), virtualPlayerInventory.getArmorContentsItems());
    }

    public static void putVirtualPlayerInventoryDirectInDb(Logger logger, String playerName, String itemsPlayerInventory, String itemsArmorContents) {
        if (!players.contains(playerName)) {
            VirtualPlayerInventorys.addItems(logger, playerName, itemsPlayerInventory, itemsArmorContents);
            players.add(playerName);
        } else {
            VirtualPlayerInventorys.updateItems(logger, playerName, itemsPlayerInventory, itemsArmorContents);
        }
    }

    public static VirtualPlayerInventory getVirtualPlayerInventoryDirectOutDb(Logger logger, String playerName) {
        String[] items = VirtualPlayerInventorys.getItemsPlayerInventoryAndArmorContents(logger, playerName);
        if (!(items instanceof String[])) {
            return null;
        }
        return new VirtualPlayerInventory(playerName, items[0], items[1]);
    }

    public static boolean existsVirtualPlayerInventoryDirectInDb(Logger logger, String playerName) {
               String[] items = VirtualPlayerInventorys.getItemsPlayerInventoryAndArmorContents(logger, playerName);
        if (!(items instanceof String[])) {
            return false;
        }
        return true;
    }

    public static void close() {
        if (!isInit) {
            return;
        }
        VirtualPlayerInventorys.closeVirtualPlayerInventorysDb();
        isInit = false;
    }

    public static void reload() {
        if (isInit) {
            close();
        }
        if (!isInit) {
            init(sqlTypeOutString, host, port, datenbank, username, password, tableName);
        }
    }
    
    private static void debug(Logger logger, String message)
    {
        if(debug)
        logger.log(Level.SEVERE, message);
    }
}
