package com.darksoldier1404.dpmb.events;

import com.darksoldier1404.dpmb.functions.DPMBFunction;
import com.darksoldier1404.dpmb.obj.MailItem;
import com.darksoldier1404.dpmb.obj.UserMailBox;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.events.dinventory.DInventoryClickEvent;
import com.darksoldier1404.dppc.events.dinventory.DInventoryCloseEvent;
import com.darksoldier1404.dppc.utils.InventoryUtils;
import com.darksoldier1404.dppc.utils.NBT;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import static com.darksoldier1404.dpmb.MailBox.plugin;

public class DPMBEvent implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        DPMBFunction.initUser(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        DPMBFunction.saveUser(e.getPlayer());
    }

    @EventHandler
    public void onInventoryClose(DInventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        DInventory inv = e.getDInventory();
        if (inv.isValidHandler(plugin)) {
            if (inv.isValidChannel(0)) { // user mailbox save
                inv.applyChanges();
                inv.applyAllItemChanges(pi -> {
                    ItemStack item = pi.getItem();
                    if (item == null || item.getType().isAir()) {
                        return pi;
                    }
                    if (!NBT.hasTagKey(item, "dpmb_mailitem")) {
                        return pi;
                    }
                    MailItem mi = MailItem.deserialize(NBT.getStringTag(item, "dpmb_mailitem"));
                    pi.setItem(mi.getAsItemStack());
                    return pi;
                });
                if (plugin.udata.containsKey(p.getUniqueId())) {
                    UserMailBox user = plugin.udata.get(p.getUniqueId());
                    user.setInventory(inv);
                    plugin.udata.put(p.getUniqueId(), user);
                    plugin.udata.save(p.getUniqueId());
                }
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryClick(DInventoryClickEvent e) {
        DInventory inv = e.getDInventory();
        if (inv.isValidHandler(plugin)) {
            Player p = (Player) e.getWhoClicked();
            ItemStack item = e.getCurrentItem();
            if (item == null || item.getType().isAir()) {
                return;
            }
            if (inv.isValidChannel(0)) { // user mailbox click event
                if (e.getClickedInventory().getType() == InventoryType.PLAYER) {
                    e.setCancelled(true);
                    return;
                }
                e.setCancelled(true);
                if (InventoryUtils.hasEnoughSpace(p.getInventory().getStorageContents(), item)) {
                    ItemStack r = DPMBFunction.getMailItemFromItemStack(item);
                    if (r != null) {
                        p.getInventory().addItem(r);
                    }
                    item.setAmount(0);
                    inv.applyChanges();
                    p.sendMessage(plugin.getPrefix() + "§a메일을 수령하였습니다.");
                } else {
                    p.sendMessage(plugin.getPrefix() + "§c인벤토리에 공간이 부족합니다.");
                }
                return;
            }
        }
    }
}
