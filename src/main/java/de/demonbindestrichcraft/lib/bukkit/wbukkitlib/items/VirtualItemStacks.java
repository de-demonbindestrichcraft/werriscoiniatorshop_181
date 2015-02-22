/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.demonbindestrichcraft.lib.bukkit.wbukkitlib.items;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ABC
 */
public class VirtualItemStacks {

    private VirtualItemStack[] virtualItemStacks;
    private int length;

    public VirtualItemStacks(ItemStack[] itemStacks) {
        length = itemStacks.length;
        virtualItemStacks = new VirtualItemStack[length];
        for (int i = 0; i < length; i++) {
            virtualItemStacks[i] = new VirtualItemStack(itemStacks[i]);
        }
    }

    public ItemStack getItemStack(int i) {
        if (i >= length) {
            return null;
        }
        return virtualItemStacks[i].getItemStack();
    }

    public ItemStack[] getItemStacks() {
        ItemStack[] itemStacks = new ItemStack[length];
        for (int i = 0; i < length; i++) {
            itemStacks[i] = virtualItemStacks[i].getItemStack();
        }
        return itemStacks;
    }

    public static ItemStack[] getItemStacksOutString(String items) {
        if (!items.contains(",")) {
            return null;
        }
        String[] split = items.split(",");
        int lengthTemp = split.length;
        ItemStack[] itemStacks = new ItemStack[lengthTemp];
        for (int i = 0; i < lengthTemp; i++) {
            itemStacks[i] = VirtualItemStack.getItemStackOutString(split[i]);
        }
        return itemStacks;
    }

    public int getLength() {
        return length;
    }

    public void update(int i, int typeId, int amount) {
        if (i >= length) {
            return;
        }
        virtualItemStacks[i].update(typeId, amount);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(virtualItemStacks[i].toString());
        }
        return sb.toString();
    }

    public static boolean isValidItemStacksPlayerInventoryString(String items) {
        if (items == null) {
            return false;
        }
        if (items.isEmpty()) {
            return false;
        }
        if (!items.contains(",")) {
            return false;
        }
        String[] split = items.split(",");
        if (split.length != 36) {
            return false;
        }
        return true;
    }

    public static boolean isValidItemStacksArmorContentsString(String items) {
        if (items == null) {
            return false;
        }
        if (items.isEmpty()) {
            return false;
        }
        if (!items.contains(",")) {
            return false;
        }
        String[] split = items.split(",");
        if (split.length != 4) {
            return false;
        }
        return true;
    }
}
