package de.fireearth.werri.werriscoiniator.shop;

import com.nitinsurana.bitcoinlitecoin.rpcconnector.RPCApp;
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

    public WerrisCoiniatorShopCommand_wcoinshop(WerrisCoiniatorShop plugin) {
        this.plugin = plugin;
        this.werrisCoiniatorShopItems = this.plugin.getWerrisCoiniatorShopItems();
        this.werrisCoiniatorEconomy = werrisCoiniatorShopItems.getRegister().getRegister().getWCR_MyEconomy().getWerrisCoiniatorEconomy();
        this.werrisRPCAppInterface = werrisCoiniatorEconomy.getWerrisRPCAppInterface();
        this.vaultEconomy = werrisCoiniatorShopItems.getRegister().getRegister().getWCR_MyEconomy().getVaultEconomy();
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
                    player.sendMessage("----------------------------------");
                } else if (split[0].equalsIgnoreCase("info")) {
                    player.sendMessage("---WerrisCoinatorShop v1.0 beta---");
                    player.sendMessage("current economy: " + plugin.getEconomyName());
                    player.sendMessage("current coins: " + plugin.getCoinName());
                    player.sendMessage("----------------------------------");
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
}
