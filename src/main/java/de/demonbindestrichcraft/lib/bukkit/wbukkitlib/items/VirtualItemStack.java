package de.demonbindestrichcraft.lib.bukkit.wbukkitlib.items;

import java.io.Serializable;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ABC
 */
public class VirtualItemStack {

    private int typeId;
    private int amount;
    private short durability;

    public VirtualItemStack(ItemStack itemStack) {
        if (itemStack != null) {
            this.typeId = ItemBackwardsCompatibility.getTypeId(itemStack);
            this.amount = itemStack.getAmount();
            this.durability = itemStack.getDurability();
        } else {
            this.typeId = 0;
            this.amount = 0;
            this.durability = 0;
        }
    }

    public VirtualItemStack(int typeId, int amount, int durability) {
        this(typeId, amount, ItemBackwardsCompatibility.getShort(durability));
    }

    public VirtualItemStack(int typeId, int amount, short durability) {
        if (typeId <= 0) {
            typeId = 0;
        }
        if (amount <= 0) {
            amount = 0;
        }
        if (durability <= 0) {
            durability = 0;
        }
        this.typeId = typeId;
        this.amount = amount;
        this.durability = durability;
    }

    public VirtualItemStack(int typeId, int amount) {
        this(typeId, amount, (short) 0);
    }

    public VirtualItemStack(int typeId) {
        this(typeId, 0, (short) 0);
    }

    public ItemStack getItemStack() {
        if (this.typeId == 0 && this.amount == 0 && this.durability == 0) {
            return null;
        }
        return ItemBackwardsCompatibility.getItemStack(typeId, amount, durability);
    }

    public void update(int typeId, int amount) {
        this.update(typeId, amount, (short) 0);
    }

    public void update(int typeId, int amount, short durability) {
        this.typeId = typeId;
        this.amount = amount;
        this.durability = durability;
    }

    public static ItemStack getItemStackOutString(String item) {
        if (!item.contains(":")) {
            return null;
        }
        String[] split = item.split(":");
        try {
            int typeIdTemp = Integer.parseInt(split[0]);
            int amountTemp = Integer.parseInt(split[1]);
            short durabilityTemp = Short.parseShort(split[2]);
            return ItemBackwardsCompatibility.getItemStack(typeIdTemp, amountTemp, durabilityTemp);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (typeId != 0 && amount != 0) {
            sb.append(typeId);
            sb.append(":");
            sb.append(amount);
            sb.append(":");
            sb.append(durability);
        } else {
            sb.append("null");
        }
        return sb.toString();
    }
}
