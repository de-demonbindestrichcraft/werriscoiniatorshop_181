/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.demonbindestrichcraft.lib.bukkit.wbukkitlib.items;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 *
 * @author ABC
 */
public class VirtualPlayerInventory {

    private String playerName;
    private String itemsPlayerInventory;
    private String itemsArmorContents;
    private ItemStack[] itemStacksPlayerInventory;
    private ItemStack[] itemStacksArmorContents;

    public VirtualPlayerInventory(Player player) {
        this.update(player);
    }

    public VirtualPlayerInventory(String playerName, String itemsPlayerInventory, String itemsArmorContents) {
        this.playerName = playerName;
        this.itemsPlayerInventory = itemsPlayerInventory;
        this.itemsArmorContents = itemsArmorContents;
        this.itemStacksPlayerInventory = VirtualItemStacks.getItemStacksOutString(itemsPlayerInventory);
        this.itemStacksArmorContents = VirtualItemStacks.getItemStacksOutString(itemsArmorContents);
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPlayerInventoryItems() {
        return itemsPlayerInventory;
    }

    public ItemStack[] getItemStacksPlayerInventory() {
        return itemStacksPlayerInventory;
    }

    public String getArmorContentsItems() {
        return itemsArmorContents;
    }

    public ItemStack[] getItemStacksArmorContents() {
        return itemStacksArmorContents;
    }

    public void setItemStacks(ItemStack[] itemStacksPlayerInventory, ItemStack[] itemStacksArmorContents) {
        setItemStacksPlayerInventory(itemStacksPlayerInventory);
        setItemStacksArmorContents(itemStacksArmorContents);
    }

    public void setItemStacksPlayerInventory(ItemStack[] itemStacksPlayerInventory) {
        updateItemStacksPlayerInventory(itemStacksPlayerInventory);
    }

    public void setItemStacksArmorContents(ItemStack[] itemStacksArmorContents) {
        updateItemStacksArmorContents(itemStacksArmorContents);
    }

    public void update(Player player) {
        playerName = player.getName();
        PlayerInventory playerInventory = player.getInventory();
        update(playerInventory.getContents(), playerInventory.getArmorContents());
    }

    public void updatePlayerInventory(Player player) {
        playerName = player.getName();
        updateItemStacksPlayerInventory(player.getInventory().getContents());
    }

    public void updateArmorContents(Player player) {
        playerName = player.getName();
        updateItemStacksArmorContents(player.getInventory().getArmorContents());
    }

    public void update(ItemStack[] itemStacksPlayerInventory, ItemStack[] itemStacksArmorContents) {
        updateItemStacksPlayerInventory(itemStacksPlayerInventory);
        updateItemStacksArmorContents(itemStacksArmorContents);
    }

    public void updateItemStacksPlayerInventory(ItemStack[] itemStacksPlayerInventory) {
        this.itemStacksPlayerInventory = itemStacksPlayerInventory;
        VirtualItemStacks virtualItemStacksPlayerInventory = new VirtualItemStacks(itemStacksPlayerInventory);
        itemsPlayerInventory = virtualItemStacksPlayerInventory.toString();
    }

    public void updateItemStacksArmorContents(ItemStack[] itemStacksArmorContents) {
        this.itemStacksArmorContents = itemStacksArmorContents;
        VirtualItemStacks virtualItemStacksArmorContents = new VirtualItemStacks(itemStacksArmorContents);
        itemsArmorContents = virtualItemStacksArmorContents.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(playerName);
        sb.append("'s PlayerInventoryItems: ");
        sb.append(itemsPlayerInventory);
        sb.append(", " + "PlayerArmorItems: ");
        sb.append(itemsArmorContents);
        return sb.toString();
    }
}
