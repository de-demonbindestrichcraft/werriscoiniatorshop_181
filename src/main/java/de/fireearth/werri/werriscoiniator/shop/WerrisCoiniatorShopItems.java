/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fireearth.werri.werriscoiniator.shop;

import de.demonbindestrichcraft.lib.bukkit.wbukkitlib.items.ItemBackwardsCompatibility;
import de.demonbindestrichcraft.lib.bukkit.wrapper.register.register.werriscoiniator.WerrisCoiniatorRegister;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ABC
 */
public class WerrisCoiniatorShopItems {

    private Map<Double, Double> keyItems = new ConcurrentHashMap<Double, Double>();
    private Map<Double,ItemStack> items = null;
    private List<String> itemsList = null;
    private WerrisCoiniatorShop werrisCoiniatorShop;
    private WerrisCoiniatorRegister werrisCoiniatorRegister;
    
    public WerrisCoiniatorShopItems(WerrisCoiniatorShop werrisCoiniatorShop, Map<String, String> items) {
        this.werrisCoiniatorShop = werrisCoiniatorShop;
        this.werrisCoiniatorRegister = werrisCoiniatorShop.getWerrisCoiniatorRegister();
                Map<Double, Map<Integer, Integer>> blockKeyItems = new ConcurrentHashMap<Double, Map<Integer, Integer>>();
        Map<Double, Map<Material, Integer>> blockMaterialItems = new ConcurrentHashMap<Double, Map<Material, Integer>>();
        Map<String, String> myitems = new ConcurrentHashMap<String, String>(items);
        Set<String> keySet = new HashSet<String>(myitems.keySet());
        List<Double> keyItemsList = new CopyOnWriteArrayList<Double>();
        List<Double> itemsList = new CopyOnWriteArrayList<Double>();
        Iterator iter = keySet.iterator();
        while (iter.hasNext()) {
            String next = (String) iter.next();
            double d = 0;
            double e = 0;
            try {
                d = Double.parseDouble(next);
                if (d <= 0) {
                    continue;
                }
                String get = myitems.get(next);
                if (!get.contains(":")) {
                    try {
                        e = Double.parseDouble(next);
                        if (e <= 0) {
                            continue;
                        }
                        keyItems.put(d, e);
                        keyItemsList.add(d);
                    } catch (Exception ex) {
                        continue;
                    }
                } else {
                    String[] split = get.split(":");
                    if (split.length != 2) {
                        continue;
                    }
                    int a = -1;
                    int b = -1;
                    Material m = null;
                    try {
                        a = Integer.parseInt(split[0]);
                        if (a <= 0) {
                            a = -1;
                            continue;
                        }
                    } catch (Exception ex) {
                        a = -1;
                        try {
                            m = Material.valueOf(split[0].toUpperCase());
                            if(m==Material.AIR)
                                continue;
                        } catch (Exception ex2) {
                            continue;
                        }
                    }
                    try {
                        b = Integer.parseInt(split[1]);
                        if (b <= 0) {
                            continue;
                        }
                    } catch (Exception ex) {
                        continue;
                    }

                    if (a < 0 && m != null) {
                        Map<Material, Integer> de = new EnumMap<Material, Integer>(Material.class);
                        de.put(m, b);
                        blockMaterialItems.put(d, de);
                        itemsList.add(d);
                    } else {
                        Map<Integer, Integer> de = new ConcurrentHashMap<Integer, Integer>();
                        de.put(a, b);
                        blockKeyItems.put(d, de);
                        itemsList.add(d);
                    }
                }
            } catch (Exception ex) {
                continue;
            }
        }
        this.items = getGeneratedItemStackMap(blockKeyItems,blockMaterialItems);
        this.itemsList = listItems(keyItemsList, itemsList);
    }
    
    private Map<Double, ItemStack> getGeneratedItemStackMap(Map<Double, Map<Integer, Integer>> blockKeyItems, Map<Double, Map<Material, Integer>> blockMaterialItems) {
        Map<Double, ItemStack> itemStackMap = new ConcurrentHashMap<Double, ItemStack>();
        Set<Double> my = new HashSet<Double>(blockKeyItems.keySet());
        Iterator iter = my.iterator();
        Iterator t = null;
        Double next = null;
        Map<Integer, Integer> get = null;
        Map<Material, Integer> get2 = null;
        Set<Integer> keySet = null;
        Set<Material> keySet2 = null;
        Integer n = null;
        Integer e = null;
        Material c = null;
        while (iter.hasNext()) {
            next = (Double) iter.next();
            get = blockKeyItems.get(next);
            keySet = new HashSet<Integer>(get.keySet());
            t = keySet.iterator();
            if (t.hasNext()) {
                n = (Integer) t.next();
                e = get.get(n);
                ItemStack temp=ItemBackwardsCompatibility.getItemStack(e, n);
                if(!ItemBackwardsCompatibility.isValidItemStack(temp))
                {
                    continue;
                }
                itemStackMap.put(next, temp);
            }
        }
        my = new HashSet<Double>(blockMaterialItems.keySet());
        iter = my.iterator();
        t = null;
        next = null;
        get = null;
        keySet = null;
        n = null;
        e = null;
        while (iter.hasNext()) {
            next = (Double) iter.next();
            get2 = blockMaterialItems.get(next);
            keySet2 = new HashSet<Material>(get2.keySet());
            t = keySet2.iterator();
            if (t.hasNext()) {
                c = (Material) t.next();
                e = get2.get(c);
                ItemStack temp = new ItemStack(c, e);
                if(!ItemBackwardsCompatibility.isValidItemStack(temp))
                    continue;
                itemStackMap.put(next, temp);
            }
        }
        return itemStackMap;
    }
    
    public Map<Double,ItemStack> getItemStackMap()
    {
        return items;
    }
    
    public Map<Double,Double> getKeyItemsMap()
    {
        return keyItems;
    }
    
    private List<String> listItems(List<Double> keyItemsList, List<Double> itemsList)
    {
        List<String> items = new CopyOnWriteArrayList<String>();
        List<Double> ki = new CopyOnWriteArrayList<Double>(keyItemsList);
        List<Double> il = new CopyOnWriteArrayList<Double>(itemsList);
        int kil = ki.size();
        int ill = il.size();
        double t = 0;
        double e = 0;
        int f = 0;
        ItemStack d;
        String mat;
        for(int i = 0; i < kil; i++)
        {
            t = ki.get(i);
            e = this.keyItems.get(t);
            items.add(t + " " + werrisCoiniatorShop.getCoinName() + " -> " + e + " " + werrisCoiniatorShop.getEconomyName());
        }
        
        for(int i = 0; i < ill; i++)
        {
            t = il.get(i);
            d = this.items.get(t);
            mat = d.getType().toString().toUpperCase();
            f = d.getAmount();
            items.add(t + " " + werrisCoiniatorShop.getCoinName() + " -> " + f + " " + mat + "/S");
        }
        return items;
    }
    
    public void sendPlayerList(Player player, int page)
    {
        List<String> al = new CopyOnWriteArrayList<String>(itemsList);
        int size = al.size();
        int div = size / 5;
        int mod = size % 5;
        if(mod > 0)
            div += 1;
        if(div == 0)
            div = 1;
        if(page < 1)
            page = 1;
        else if (page > div) {
            page = div;
        }
        player.sendMessage("Page " + page + "/" + div);
        for(int i = (page - 1) * 5, end = page * 5; i < end && i < size ; i++)
        { 
            player.sendMessage(al.get(i));
        }
    }
  
    
    public WerrisCoiniatorRegister getRegister()
    {
        return werrisCoiniatorRegister;
    }
}
