/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.demonbindestrichcraft.lib.bukkit.wbukkitlib.items;

import de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.sql.SqlInterface;
import de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.sql.SqlType;
import de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.sql.SqlWrapper;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ABC
 */
public class VirtualPlayerInventorys {

    private static SqlInterface sqlInterface = null;
    private static String tableName = "";
    private static boolean debug = false;

    public static void createVirtualPlayerInventorysDb(SqlType sqlType, String host, String port, String datenbank, String username, String password, String tablename) {
        if (sqlInterface == null) {
            sqlInterface = SqlWrapper.getSql(host, port, datenbank, username, password, sqlType);
        }
        if (tableName.isEmpty()) {
            tableName = tablename;
        }

        tableName = tableName.toLowerCase();

        sqlInterface.executeSqlQuery("CREATE TABLE IF NOT EXISTS " + tablename + " ("
                + "playerName varchar(100) DEFAULT NULL,"
                + "itemsPlayerInventory varchar(1000) DEFAULT NULL,"
                + "itemsArmorContents varchar(100) DEFAULT NULL,"
                + "UNIQUE (playerName));", false);
    }

    public static SqlInterface getSqlInterface() {
        return sqlInterface;
    }

    public static void addItems(Logger logger, String playerName, String itemsPlayerInventory, String itemsArmorContents) {
        if (tableName.isEmpty()) {
            logger.log(Level.SEVERE, "addItems TableName: " + tableName + " is Empty!");
            return;
        }
        String sqlString = sqlInterface.getInsertIntoTableSqlQuery(tableName, new String[]{"playerName", "itemsPlayerInventory", "itemsArmorContents"}, new String[]{playerName, itemsPlayerInventory, itemsArmorContents});
        debug(logger, sqlString);
        sqlInterface.executeSqlQuery(sqlString, false);
    }

    public static void removeItems(Logger logger, String playerName) {
        if (tableName.isEmpty()) {
            debug(logger, "removeItems TableName: " + tableName + " is Empty!");
            return;
        }

        String sqlString = sqlInterface.getDeleteSqlQuery(tableName, "playerName = '" + playerName + "'");
        debug(logger, sqlString);
        sqlInterface.executeSqlQuery(sqlString, false);
    }

    public static void updateItems(Logger logger, String playerName, String itemsPlayerInventory, String itemsArmorContents) {
        if (tableName.isEmpty()) {
            debug(logger, "updateItems TableName: " + tableName + " is Empty!");
            return;
        }

        String sqlString = sqlInterface.getUpdateSqlQuery(tableName, "itemsPlayerInventory = '" + itemsPlayerInventory + "', itemsArmorContents = '" + itemsArmorContents + "'", "playerName = '" + playerName + "'");
        debug(logger, sqlString);
        sqlInterface.executeSqlQuery(sqlString, false);
    }

    public static String[] getItemsPlayerInventoryAndArmorContents(Logger logger, String playerName) {
        if (tableName.isEmpty()) {
            debug(logger, "getItemsPlayerInventoryAndArmorContents TableName: " + tableName + " is Empty!");
            return null;
        }

        String sqlString = sqlInterface.getSelectSqlQuery(tableName, "itemsPlayerInventory,itemsArmorContents", "playerName = '" + playerName + "'");
        logger.log(Level.WARNING, sqlString);
        sqlInterface.executeSqlQuery(sqlString, true);
        Map<String, List<String>> resultSql = sqlInterface.getResultSqlEx("itemsPlayerInventory", "itemsArmorContents");
        List<String> itemsPlayerInventory = resultSql.get("itemsPlayerInventory");
        List<String> itemsArmorContents = resultSql.get("itemsArmorContents");
        if (itemsPlayerInventory == null) {
            debug(logger, "getItemsPlayerInventoryAndArmorContents itemsPlayerInventory == null!");
            return null;
        }

        if (itemsPlayerInventory.isEmpty()) {
            debug(logger, "getItemsPlayerInventoryAndArmorContents itemsPlayerInventory isEmpty!");
            return null;
        }

        if (itemsArmorContents == null) {
            debug(logger, "getItemsPlayerInventoryAndArmorContents itemsArmorContents == null!");
            return null;
        }

        if (itemsArmorContents.isEmpty()) {
            debug(logger, "getItemsPlayerInventoryAndArmorContents itemsArmorContents isEmpty!");
            return null;
        }
        String[] items = new String[2];
        items[0] = itemsPlayerInventory.get(0);
        items[1] = itemsArmorContents.get(0);
        return items;
    }

    public static List<VirtualPlayerInventory> getVirtualPlayerInventorysAsList(Logger logger) {
        if (tableName.isEmpty()) {
            debug(logger, "getVirtualPlayerInventorysAsList TableName: " + tableName + " is Empty!");
            return null;
        }

        String sqlString = sqlInterface.getSelectSqlQuery(tableName, "playerName,itemsPlayerInventory,itemsArmorContents");
        debug(logger, sqlString);
        sqlInterface.executeSqlQuery(sqlString, true);
        List<VirtualPlayerInventory> playerInventorys = new LinkedList<VirtualPlayerInventory>();
        List<String> playerNames = sqlInterface.getResultSql("playerName");
        List<String> itemsPlayerInventory = sqlInterface.getResultSql("itemsPlayerInventory");
        List<String> itemsArmorContents = sqlInterface.getResultSql("itemsArmorContents");
        if (playerNames == null || itemsPlayerInventory == null || itemsArmorContents == null) {
            debug(logger, "getVirtualPlayerInventorysAsList playerNames == null || itemsPlayerInventory == null || itemsArmorContents == null");
            return null;
        }
        if (playerNames.size() != itemsPlayerInventory.size() || playerNames.size() != itemsArmorContents.size()) {
           debug(logger, "getVirtualPlayerInventorysAsList playerNames.size() != itemsPlayerInventory.size()! || playerNames.size() != itemsArmorContents.size()");
            return null;
        }
        int length = playerNames.size();
        for (int i = 0; i < length; i++) {
            VirtualPlayerInventory virtualPlayerInventory = new VirtualPlayerInventory(playerNames.get(i), itemsPlayerInventory.get(i), itemsArmorContents.get(i));
            playerInventorys.add(virtualPlayerInventory);
        }
        return playerInventorys;
    }

    public static Map<String, VirtualPlayerInventory> getVirtualPlayerInventorys(Logger logger) {
        if (tableName.isEmpty()) {
            debug(logger, "getVirtualPlayerInventorys TableName: " + tableName + " is Empty!");
            return null;
        }

        String sqlString = sqlInterface.getSelectSqlQuery(tableName, "playerName");
        logger.log(Level.WARNING, sqlString);
        sqlInterface.executeSqlQuery(sqlString, true);
        Map<String, VirtualPlayerInventory> playerInventorys = new HashMap<String, VirtualPlayerInventory>();
        List<String> playerNames = sqlInterface.getResultSql("playerName");
        List<String[]> items = getItemsPlayerInventoryAndArmorContents(logger, playerNames);
        if (playerNames == null || items == null) {
            debug(logger, "getVirtualPlayerInventorys playerNames == null || items == null");
            return null;
        }
        if (playerNames.size() != items.size()) {
            debug(logger, "getVirtualPlayerInventorys playerNames.size() (" + playerNames.size() + ") != items.size()!" + "(" + items.size() + ")");
            return null;
        }
        int length = playerNames.size();
        for (int i = 0; i < length; i++) {
            String[] itemsPlayerInventoryAndArmorContents = items.get(i);
            VirtualPlayerInventory virtualPlayerInventory = new VirtualPlayerInventory(playerNames.get(i), itemsPlayerInventoryAndArmorContents[0], itemsPlayerInventoryAndArmorContents[1]);
            playerInventorys.put(playerNames.get(i), virtualPlayerInventory);
        }
        return playerInventorys;
    }

    public static List<String[]> getItemsPlayerInventoryAndArmorContents(Logger logger, List<String> players) {
        List<String[]> playerInventorys = new LinkedList<String[]>();
        for (String player : players) {
            String[] items = getItemsPlayerInventoryAndArmorContents(logger, player);
            if (!(items instanceof String[])) {
                debug(logger, "getItemsPlayerInventoryAndArmorContents items not a instance of String[]");
                return null;
            }
            playerInventorys.add(items);
        }
        return playerInventorys;
    }

    public static List<VirtualPlayerInventory> getVirtualPlayerInventorysAsListOutMap(Logger logger, Map<String, VirtualPlayerInventory> playerInventorysMap) {
        if (playerInventorysMap == null) {
            debug(logger, "getVirtualPlayerInventorysAsListOutMap playerInventorysMap == null");
            return null;
        }
        List<VirtualPlayerInventory> playerInventorys = new LinkedList<VirtualPlayerInventory>();
        Set<String> keySet = new HashSet<String>();
        keySet.addAll(playerInventorysMap.keySet());
        for (String key : keySet) {
            VirtualPlayerInventory virtualPlayerInventory = playerInventorysMap.get(key);
            if (virtualPlayerInventory == null) {
                debug(logger, "getVirtualPlayerInventorysAsListOutMap virtualPlayerInventory == null");
                continue;
            }
            playerInventorys.add(virtualPlayerInventory);
        }
        return playerInventorys;
    }

    public static void addVirtualPlayerInventorys(Logger logger, List<VirtualPlayerInventory> playerInventorys) {
        if (tableName.isEmpty()) {
            debug(logger, "addVirtualPlayerInventorys tableName is Empty!");
            return;
        }
        if (playerInventorys == null) {
            debug(logger, "addVirtualPlayerInventorys playerInventorys == null!");
            return;
        }

        for (VirtualPlayerInventory virtualPlayerInventory : playerInventorys) {
            addItems(logger, virtualPlayerInventory.getPlayerName(), virtualPlayerInventory.getPlayerInventoryItems(), virtualPlayerInventory.getArmorContentsItems());
        }
    }

    public static void removeVirtualPlayerInventorys(Logger logger, List<VirtualPlayerInventory> playerInventorys) {
        if (tableName.isEmpty()) {
            debug(logger, "removeVirtualPlayerInventorys tableName is Empty!");
            return;
        }
        if (playerInventorys == null) {
            debug(logger, "removeVirtualPlayerInventorys playerInventorys == null!");
            return;
        }

        for (VirtualPlayerInventory virtualPlayerInventory : playerInventorys) {
            removeItems(logger, virtualPlayerInventory.getPlayerName());
        }
    }

    public static void updateVirtualPlayerInventorys(Logger logger, List<VirtualPlayerInventory> playerInventorys) {
        if (tableName.isEmpty()) {
            debug(logger, "updateVirtualPlayerInventorys tableName is Empty!");
            return;
        }
        if (playerInventorys == null) {
            debug(logger, "updateVirtualPlayerInventorys playerInventorys == null!");
            return;
        }

        for (VirtualPlayerInventory virtualPlayerInventory : playerInventorys) {
            updateItems(logger, virtualPlayerInventory.getPlayerName(), virtualPlayerInventory.getPlayerInventoryItems(), virtualPlayerInventory.getArmorContentsItems());
        }
    }

    public static void closeVirtualPlayerInventorysDb() {
        sqlInterface.close();
        sqlInterface = null;
    }

    private static void debug(Logger logger, String message) {
        if (debug) {
            logger.log(Level.WARNING, message);
        }
    }
}
