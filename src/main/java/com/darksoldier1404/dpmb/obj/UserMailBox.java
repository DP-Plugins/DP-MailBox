package com.darksoldier1404.dpmb.obj;

import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.data.DataCargo;
import com.darksoldier1404.dppc.utils.NBT;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.darksoldier1404.dpmb.MailBox.plugin;

public class UserMailBox implements DataCargo {
    private UUID ownerUUID;
    private DInventory inventory;
    private boolean isOpened = false;

    public UserMailBox() {
    }

    public UserMailBox(UUID uuid) {
        this.ownerUUID = uuid;
        this.inventory = new DInventory(plugin.getLang().get("mailbox_title"), 54, true, true, plugin);
        this.inventory.setChannel(0);
    }

    public UserMailBox(UUID uuid, DInventory inventory) {
        this.ownerUUID = uuid;
        this.inventory = inventory;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    public DInventory getInventory() {
        return inventory;
    }

    public void setInventory(DInventory inventory) {
        this.inventory = inventory;
    }

    public void openMailBox(Player p) {
        if (isOpened()) {
            p.sendMessage(plugin.getLang().get("mailbox_already_open"));
            return;
        }
        setOpened(true);
        inventory.setCurrentPage(0);
        inventory.applyDefaultPageTools();
        inventory.update();
        inventory.applyAllItemChanges(pi -> {
            MailItem mi = MailItem.deserialize(NBT.getStringTag(pi.getItem(), "dpmb_mailitem"));
            ItemStack item = mi.getAsItemStack();
            Date received = new Date(mi.getReceiveDate());
            long nowMs = new Date().getTime();
            long expireSeconds = plugin.expireSeconds;
            long expireMillis = expireSeconds * 1000L;
            long expireAt = received.getTime() + expireMillis;
            long remaining = expireAt - nowMs;
            if (remaining <= 0) {
                item.setAmount(0);
                pi.setItem(item);
                return pi;
            }
            long totalSeconds = remaining / 1000L;
            long diffDays = totalSeconds / (24L * 60L * 60L);
            long diffHours = (totalSeconds % (24L * 60L * 60L)) / (60L * 60L);
            long diffMinutes = (totalSeconds % (60L * 60L)) / 60L;
            long diffSeconds = totalSeconds % 60L;

            StringBuilder timeBuilder = new StringBuilder("§e");
            boolean first = true;
            if (diffDays > 0) {
                timeBuilder.append(diffDays).append("일");
                first = false;
            }
            if (diffHours > 0 || !first) {
                if (!first) timeBuilder.append(" ");
                timeBuilder.append(diffHours).append("시간");
                first = false;
            }
            if (diffMinutes > 0 || !first) {
                if (!first) timeBuilder.append(" ");
                timeBuilder.append(diffMinutes).append("분");
                first = false;
            }
            if (diffSeconds > 0 || !first || totalSeconds < 60) {
                if (!first) timeBuilder.append(" ");
                timeBuilder.append(diffSeconds).append("초");
            }
            String timeString = timeBuilder.toString();

            String receivedDate = String.format("%tF %tT", received, received);
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.getLore() != null ? meta.getLore() : new ArrayList<>();
            lore.add("");
            lore.add(plugin.getLang().get("click_to_receive"));
            lore.add(plugin.getLang().getWithArgs("remaining_time", timeString));
            lore.add(plugin.getLang().getWithArgs("received_date", receivedDate));
            meta.setLore(lore);
            item.setItemMeta(meta);
            pi.setItem(item);
            return pi;
        });
        inventory.update();
        inventory.applyChanges();
        inventory.setObj(ownerUUID);
        inventory.openInventory(p);
    }

    @Override
    public YamlConfiguration serialize() {
        YamlConfiguration data = new YamlConfiguration();
        data.set("uuid", ownerUUID.toString());
        inventory.serialize(data);
        return data;
    }

    @Override
    public UserMailBox deserialize(YamlConfiguration data) {
        this.ownerUUID = UUID.fromString(data.getString("uuid"));
        this.inventory = new DInventory(plugin.getLang().get("mailbox_title"), 54, true, true, plugin).deserialize(data);
        this.inventory.setChannel(0);
        return this;
    }
}
