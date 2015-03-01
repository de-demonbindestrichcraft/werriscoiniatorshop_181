package de.fireearth.werri.werriscoiniator.shop;

import com.nitinsurana.bitcoinlitecoin.rpcconnector.RPCApp;
import de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.files.ConcurrentConfig;
import de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.files.Config;
import de.demonbindestrichcraft.werri.werriscoininatorregister.interfaces.VaultEconomy;
import de.demonbindestrichcraft.werri.werriscoininatorregister.interfaces.WCR_MyEconomy;
import de.demonbindestrichcraft.werri.werriscoininatorregister.interfaces.WerrisCoiniatorEconomy;
import de.fireearth.werri.werriscoiniator.WerrisRPCAppInterface;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Handler for the /pos sample command.
 * @author SpaceManiac
 */
public class WerrisCoiniatorShopCommand_wcoinshop implements CommandExecutor {

    private final WerrisCoiniatorShop plugin;
    private final WerrisCoiniatorShopItems werrisCoiniatorShopItems;
    private final WerrisRPCAppInterface werrisRPCAppInterface;
    private final WerrisCoiniatorEconomy werrisCoiniatorEconomy;
    private final VaultEconomy vaultEconomy;
    private String dir = "plugins"+File.separator+"WerrisCoiniatorShop"+File.separator+"WerrisCoiniatorShop.settings";
    private File file = null;
    private ConcurrentConfig config = null;
    private Map<String,String> settings = null;
    private boolean PlayersCanBuyItems=false;
    private boolean PlayersCanSellItems=false;
    private boolean PlayersCanBuyCurrency=false;
    private boolean PlayersCanSellCurrency=false;
    
    public WerrisCoiniatorShopCommand_wcoinshop(WerrisCoiniatorShop plugin) {
        this.plugin = plugin;
        this.werrisCoiniatorShopItems = this.plugin.getWerrisCoiniatorShopItems();
        this.werrisCoiniatorEconomy = werrisCoiniatorShopItems.getRegister().getRegister().getWCR_MyEconomy().getWerrisCoiniatorEconomy();
        this.werrisRPCAppInterface = werrisCoiniatorEconomy.getWerrisRPCAppInterface();
        this.vaultEconomy = werrisCoiniatorShopItems.getRegister().getRegister().getWCR_MyEconomy().getVaultEconomy();
        this.reload();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;

        int length = split.length;
        switch (length) {
            case 3: {
            }
            break;

            case 2: {
                int t = 0;
                if (split[0].equalsIgnoreCase("list")) {
                    try {
                        t = Integer.parseInt(split[1]);
                        werrisCoiniatorShopItems.sendPlayerList(player, t);
                    } catch (Exception ex) {
                        return true;
                    }
                } else if (split[0].equalsIgnoreCase("buyitem")) {
                    if(!hasPermission(player, split[0]))
                    {
                        return true;
                    }
                    try {
                        Double e = Double.parseDouble(split[1]);
                        if (!werrisCoiniatorEconomy.has(werrisRPCAppInterface.getName(player), e)) {
                            player.sendMessage("You don't have enough money to buy the item!");
                            return true;
                        }
                        boolean containsKey = werrisCoiniatorShopItems.getItemStackMap().containsKey(e);
                        if (!containsKey) {
                            player.sendMessage("The item is not available in the shop!");
                            return true;
                        }
                        ItemStack get = werrisCoiniatorShopItems.getItemStackMap().get(e);
                        int firstEmpty = player.getInventory().firstEmpty();
                        if (firstEmpty == -1) {
                            player.sendMessage("You don't have any place in your inventory!");
                            return true;
                        }
                        werrisCoiniatorEconomy.withdrawPlayer(werrisRPCAppInterface.getName(player), plugin.getShopName(), e);
                        player.getInventory().setItem(firstEmpty, get);
                        player.sendMessage("You have buy " + get.getAmount() + " " + get.getType().toString().toUpperCase() + "/S for " + e + " " + plugin.getCoinName());
                    } catch (Exception ex) {
                        return true;
                    }
                } else if (split[0].equalsIgnoreCase("buycurrency")) {
                    if(!hasPermission(player, split[0]))
                    {
                        return true;
                    }
                    try {
                        Double e = Double.parseDouble(split[1]);
                        if (!werrisCoiniatorEconomy.has(werrisRPCAppInterface.getName(player), e)) {
                            player.sendMessage("You don't have enough money to buy the currency!");
                            return true;
                        }
                        boolean containsKey = werrisCoiniatorShopItems.getKeyItemsMap().containsKey(e);
                        if (!containsKey) {
                            player.sendMessage("The currency is not available in the shop!");
                            return true;
                        }
                        Double f = werrisCoiniatorShopItems.getKeyItemsMap().get(e);
                        werrisCoiniatorEconomy.withdrawPlayer(werrisRPCAppInterface.getName(player), plugin.getShopName(), e);
                        vaultEconomy.depositPlayer(werrisRPCAppInterface.getName(player), f);
                        player.sendMessage("You have buy " + f + " " + plugin.getEconomyName() + " for " + e + " " + plugin.getCoinName());
                    } catch (Exception ex) {
                        return true;
                    }
                } else if (split[0].equalsIgnoreCase("sellitem")) {
                    if(!hasPermission(player, split[0]))
                    {
                        return true;
                    }
                    try {
                        Double e = Double.parseDouble(split[1]);
                        boolean containsKey = werrisCoiniatorShopItems.getItemStackMap().containsKey(e);
                        if (!containsKey) {
                            player.sendMessage("The item is not available in the shop!");
                            return true;
                        }
                        ItemStack get = werrisCoiniatorShopItems.getItemStackMap().get(e);
                        ItemStack[] contents = player.getInventory().getContents();
                        int size = contents.length;
                        boolean hasItem = false;
                        for (int i = 0; i < size; i++) {
                            if(contents[i] == null) {
                                continue;
                            }
                            if (contents[i].getType().equals(get.getType())) {
                                if (contents[i].getAmount() >= get.getAmount()) {
                                    contents[i].setAmount(contents[i].getAmount() - get.getAmount());
                                    player.getInventory().setContents(contents);
                                    hasItem = true;
                                    break;
                                }
                            }
                        }
                        if (!hasItem) {
                            player.sendMessage("You don't have enough items!");
                            return true;
                        }
                        werrisCoiniatorEconomy.depositPlayer(plugin.getShopName(), werrisRPCAppInterface.getName(player), e);
                        player.sendMessage("You have sell " + get.getAmount() + " " + get.getType().toString().toUpperCase() + "/S for " + e + " " + plugin.getCoinName());
                    } catch (Exception ex) {
                        return true;
                    }
                } else if (split[0].equalsIgnoreCase("sellcurrency")) {
                    if(!hasPermission(player, split[0]))
                    {
                        return true;
                    }
                    try {
                        Double e = Double.parseDouble(split[1]);
                        boolean containsKey = werrisCoiniatorShopItems.getKeyItemsMap().containsKey(e);
                        if (!containsKey) {
                            player.sendMessage("The currency is not available in the shop!");
                            return true;
                        }
                        Double f = werrisCoiniatorShopItems.getKeyItemsMap().get(e);
                        vaultEconomy.withdrawPlayer(werrisRPCAppInterface.getName(player), f);
                        werrisCoiniatorEconomy.depositPlayer(plugin.getShopName(), werrisRPCAppInterface.getName(player), e);
                        player.sendMessage("You have sell " + f + " " + plugin.getEconomyName() + " for " + e + " " + plugin.getCoinName());
                    } catch (Exception ex) {
                        return true;
                    }
                }
            }
            break;
                
            case 1:
            {
                if(split[0].equalsIgnoreCase("help"))
                {
                    player.sendMessage("---WerrisCoinatorShop v1.0 beta---");
                    player.sendMessage("/wcoinshop buycurrency <amount> - buy economy listed by the amount of coins!");
                    player.sendMessage("/wcoinshop sellcurrency <amount> - sell economy listed by the amount of coins!");
                    player.sendMessage("/wcoinshop buyitem <amount> - buy a item listed by the amount of coins!");
                    player.sendMessage("/wcoinshop sellitem <amount> - sell a item listed by the amount of coins!");
                    player.sendMessage("/wcoinshop info - listed infos of the current currency!");
                    player.sendMessage("/wcoinshop list <page> - listed items respectively currency from the shop!");
                    player.sendMessage("/wcoinshop reload - reloaded the shops config!");
                    player.sendMessage("----------------------------------");
                } else if (split[0].equalsIgnoreCase("info")) {
                    player.sendMessage("---WerrisCoinatorShop v1.0 beta---");
                    player.sendMessage("current economy: " + plugin.getEconomyName());
                    player.sendMessage("current coins: " + plugin.getCoinName());
                    player.sendMessage("----------------------------------");
                } else if (split[0].equalsIgnoreCase("reload")) {
                    if(!player.isOp())
                    {
                         player.sendMessage("You don't have the permission to reload the shop!");
                         return true;
                    }
                    this.reload();
                    player.sendMessage("You have reloaded the shop!");
                }
            }
            break;
                
            default:
            {
                player.sendMessage("/wcoinshop help");
            }
            break;
        }

        return true;
    }
    
    public final synchronized void reload()
    {
        settings = new ConcurrentHashMap<String, String>();
        file = new File(dir);
        if(!file.exists())
        {
            settings.put("PlayersCanBuyItems", "true");
            settings.put("PlayersCanSellItems", "true");
            settings.put("PlayersCanBuyCurrency", "true");
            settings.put("PlayersCanSellCurrency", "true");
            config = new ConcurrentConfig(file);
            config.update(settings);
            config.save("=");
        } else {
            config = new ConcurrentConfig(file);
            config.load(file, "=");
            Map<String, String> copyOfProperties = config.getCopyOfProperties();
            settings.putAll(copyOfProperties);
        }
        if(!settings.containsKey("PlayersCanBuyItems"))
        {
            settings.put("PlayersCanBuyItems", "true");
            config.update(settings);
            config.save("=");
        }
        if(!settings.containsKey("PlayersCanSellItems"))
        {
            settings.put("PlayersCanSellItems", "true");
            config.update(settings);
            config.save("=");
        }
        if(!settings.containsKey("PlayersCanBuyCurrency"))
        {
            settings.put("PlayersCanBuyCurrency", "true");
            config.update(settings);
            config.save("=");
        }
        if(!settings.containsKey("PlayersCanSellCurrency"))
        {
            settings.put("PlayersCanSellCurrency", "true");
            config.update(settings);
            config.save("=");
        }
        try{
            PlayersCanBuyItems=Boolean.parseBoolean(settings.get("PlayersCanBuyItems"));
        } catch (Throwable ex)
        {
            PlayersCanBuyItems=true;
        }
        try{
            PlayersCanSellItems=Boolean.parseBoolean(settings.get("PlayersCanSellItems"));
        } catch (Throwable ex)
        {
            PlayersCanSellItems=true;
        }
        try{
            PlayersCanBuyCurrency=Boolean.parseBoolean(settings.get("PlayersCanBuyCurrency"));
        } catch (Throwable ex)
        {
            PlayersCanBuyCurrency=true;
        }
        try{
            PlayersCanSellCurrency=Boolean.parseBoolean(settings.get("PlayersCanSellCurrency"));
        } catch (Throwable ex)
        {
            PlayersCanSellCurrency=true;
        }
    }
    
    private boolean hasPermission(Player player, String command)
    {
        boolean havePermission=true;
        if(command==null||command.isEmpty())
        {
            havePermission=false;
        }
        if(!(player instanceof Player))
        {
            havePermission=false;
        }
        if(!havePermission)
        {
            return false;
        }
        if(command.equalsIgnoreCase("buyitem"))
        {
            if(PlayersCanBuyItems)
            {
                return true;
            } else {
                putMessageIfHaveNotPermmission(player, false);
                return false;
            }
        } else if(command.equalsIgnoreCase("sellitem"))
        {
            if(PlayersCanSellItems)
            {
                return true;
            } else {
                putMessageIfHaveNotPermmission(player, false);
                return false;
            }
        } else if(command.equalsIgnoreCase("buycurrency"))
        {
            if(PlayersCanBuyCurrency)
            {
                return true;
            } else {
                putMessageIfHaveNotPermmission(player, false);
                return false;
            }
        } else if(command.equalsIgnoreCase("sellcurrency"))
        {
            if(PlayersCanSellCurrency)
            {
                return true;
            } else {
                putMessageIfHaveNotPermmission(player, false);
                return false;
            }
        } else {
            return false;
        }
    }
    
    private void putMessageIfHaveNotPermmission(Player player, boolean havePermission)
    {
        if(!havePermission)
        {
            player.sendMessage("You don't have the permission to do that!");
            return;
        }
    }
}
