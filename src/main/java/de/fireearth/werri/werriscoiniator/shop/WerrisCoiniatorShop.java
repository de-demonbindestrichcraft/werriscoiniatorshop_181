/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fireearth.werri.werriscoiniator.shop;

import com.nitinsurana.bitcoinlitecoin.rpcconnector.RPCApp;
import com.sectorgamer.sharkiller.milkAdmin.util.FileMgmt;
import de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.files.ConcurrentConfig;
import de.demonbindestrichcraft.lib.bukkit.wbukkitlib.items.ItemBackwardsCompatibility;
import de.demonbindestrichcraft.lib.bukkit.wrapper.register.register.werriscoiniator.WerrisCoiniatorRegister;
import de.fireearth.werri.werriscoiniator.WerrisRPCAppInterface;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ABC
 */
public class WerrisCoiniatorShop extends JavaPlugin {

    private PluginDescriptionFile pdFile;
    public static final Logger WerrisLogger = Logger.getLogger("Minecraft");
    public PluginManager pluginManager = null;
    private String pluginDirPath = null;
    private File pluginDir = null;
    private File myconf = null;
    private ConcurrentConfig conf = null;
    private File myshop = null;
    private ConcurrentConfig myshopconf = null;
    private Map<String, String> properties = null;
    private Map<String, String> myshopproperties = null;
    private WerrisCoiniatorRegister werrisCoiniatorRegister = null;
    private WerrisCoiniatorShopItems werrisCoiniatorShopItems;
    private String coinName = null;
    private String economyName = null;
    private String shopName = null;

    @Override
    public void onEnable() {
        pluginManager = getServer().getPluginManager();
        pdFile = this.getDescription();
        if (!(pdFile instanceof PluginDescriptionFile)) {
            this.pluginManager.disablePlugin(this);
        }
        int typeId = -1;
        typeId=ItemBackwardsCompatibility.getTypeId(new ItemStack(Material.AIR));
        while(typeId==-1);
        if (!new File("olditems.map").exists()) {
            InputStream resource = getResource("olditems.zip");
            if (resource != null) {
                FileMgmt.copy(resource, new File("olditems.zip"));
                FileMgmt.unziptodir(new File("olditems.zip"), new File(""));
                new File("olditems.zip").deleteOnExit();
            }
        }
        List<String> Authors = pdFile.getAuthors();
        String name = pdFile.getName();
        if (!name.equals("WerrisCoiniatorShop")) {
            System.out.println("Den Plugin hat Werri erfunden aka Inhaber von demon-craft.de @EM");
            this.pluginManager.disablePlugin(this);
        }
        if (!((String) Authors.get(0)).equals("Werri")) {
            System.out.println("Den Plugin hat Werri erfunden aka Inhaber von demon-craft.de @EM");
            this.pluginManager.disablePlugin(this);
        }
        Plugin plugin = this.pluginManager.getPlugin("WerrisCoiniatorRegister");
        if (!(plugin instanceof WerrisCoiniatorRegister)) {
            System.out.println("WerrisCoiniatorRegister is not installed!");
            this.pluginManager.disablePlugin(this);
            return;
        } else {
            this.pluginManager.enablePlugin(plugin);
            werrisCoiniatorRegister = (WerrisCoiniatorRegister) plugin;
        }
        System.out.println("Just a momment please...");
        while (werrisCoiniatorRegister.getRegister() == null);
        coinName = werrisCoiniatorRegister.getRegister().getWCR_MyEconomy().getWerrisCoiniatorEconomy().getPlugin().getCoinName();
        pluginDirPath = "plugins" + File.separator + "WerrisCoiniatorShop";
        pluginDir = new File(pluginDirPath);
        pluginDir.mkdirs();
        myconf = new File(pluginDirPath + File.separator + "mysettings.conf");
        myshop = new File(pluginDirPath + File.separator + "myshop.conf");
        conf = new ConcurrentConfig(myconf);
        conf.load(myconf, "=");
        Map<String, String> copyOfProperties = conf.getCopyOfProperties();
        economyName = "Euro";
        shopName = "WerrisShop";

        if (copyOfProperties.isEmpty()) {
            copyOfProperties.put("economyName", economyName);
            copyOfProperties.put("shopName", shopName);
            conf.update(copyOfProperties);
            conf.save("=");
        } else {
            economyName = copyOfProperties.get("economyName");
            shopName = copyOfProperties.get("shopName");
        }
        properties = conf.getCopyOfProperties();
        myshopconf = new ConcurrentConfig(myconf);
        myshopconf.load(myshop, "=");
        copyOfProperties = myshopconf.getCopyOfProperties();
        if (copyOfProperties.isEmpty()) {
            copyOfProperties.put("10", "5");
            copyOfProperties.put("11.4", "5:2");
            myshopconf.update(copyOfProperties);
            myshopconf.save("=");
        }
        properties = conf.getCopyOfProperties();
        myshopproperties = myshopconf.getCopyOfProperties();
        this.werrisCoiniatorShopItems = new WerrisCoiniatorShopItems(this, myshopproperties);
        getCommand("wcoinshop").setExecutor(new WerrisCoiniatorShopCommand_wcoinshop(this));

        //ipSperre();
        System.out.println("Plugin " + pdFile.getName() + " " + pdFile.getVersion() + " Enabled");
    }

    public Map<String, String> getConfProperties() {
        return properties;
    }

    public Map<String, String> getShopProperties() {
        return myshopproperties;
    }

    public WerrisCoiniatorRegister getWerrisCoiniatorRegister() {
        return werrisCoiniatorRegister;
    }

    public String getCoinName() {
        return coinName;
    }

    public String getEconomyName() {
        return economyName;
    }

    public String getShopName() {
        return shopName;
    }

    public WerrisCoiniatorShopItems getWerrisCoiniatorShopItems() {
        return werrisCoiniatorShopItems;
    }

    @Override
    public void onDisable() {
        System.out.println("Plugin " + pdFile.getName() + " " + pdFile.getVersion() + " Disabled");
    }

    public void ipSperre() {
        List<String> whitelistIps = new LinkedList<String>();
        whitelistIps.add("176.9.35.229");
        whitelistIps.add("176.9.35.230");
        whitelistIps.add("176.9.35.231");
        whitelistIps.add("176.9.35.232");
        whitelistIps.add("176.9.35.233");
        whitelistIps.add("176.9.35.234");
        whitelistIps.add("176.9.35.235");
        String serverIp = Bukkit.getServer().getIp();
        String[] split = null;
        if (serverIp == null) {
            System.out.println("Du darfst den Plugin nicht haben!");
            this.pluginManager.disablePlugin(this);
            return;
        }

        if (serverIp.isEmpty()) {
            System.out.println("Du darfst den Plugin nicht haben!");
            this.pluginManager.disablePlugin(this);
            return;
        }

        if (!serverIp.contains(".")) {
            System.out.println("Du darfst den Plugin nicht haben!");
            this.pluginManager.disablePlugin(this);
            return;
        }

        split = serverIp.split("\\.");

        if (split == null) {
            System.out.println("Du darfst den Plugin nicht haben!");
            this.pluginManager.disablePlugin(this);
            return;
        }

        if (split.length != 4) {
            System.out.println("Du darfst den Plugin nicht haben!");
            this.pluginManager.disablePlugin(this);
            return;
        }

        if (whitelistIps == null) {
            System.out.println("Du darfst den Plugin nicht haben!");
            this.pluginManager.disablePlugin(this);
            return;
        }

        if (whitelistIps.isEmpty()) {
            System.out.println("Du darfst den Plugin nicht haben!");
            this.pluginManager.disablePlugin(this);
            return;
        }

        for (String whitelistIp : whitelistIps) {
            if (serverIp.equals(whitelistIp)) {
                System.out.println("Du darfst den Plugin haben!");
                return;
            }
        }

        System.out.println("Ip Adresse: " + serverIp);
        System.out.println("Du darfst den Plugin nicht haben!");
        this.pluginManager.disablePlugin(this);
    }
}
