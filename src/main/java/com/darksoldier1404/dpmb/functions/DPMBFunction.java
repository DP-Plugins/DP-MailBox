package com.darksoldier1404.dpmb.functions;

import com.darksoldier1404.dpmb.MailBox;
import com.darksoldier1404.dpmb.obj.MailItem;
import com.darksoldier1404.dpmb.obj.Presets;
import com.darksoldier1404.dpmb.obj.UserMailBox;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.utils.NBT;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import static com.darksoldier1404.dpmb.MailBox.plugin;

public class DPMBFunction {
    public static void init() {
        for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
            if (MailBox.udata.containsKey(op.getUniqueId())) continue;
            UserMailBox data = new UserMailBox(op.getUniqueId());
            MailBox.udata.put(op.getUniqueId(), data);
        }
    }

    public static void initUser(Player p) {
        if (MailBox.udata.containsKey(p.getUniqueId())) return;
        UserMailBox data = new UserMailBox(p.getUniqueId());
        MailBox.udata.put(p.getUniqueId(), data);
    }

    public static void saveUser(Player p) {
        if (!MailBox.udata.containsKey(p.getUniqueId())) return;
        MailBox.udata.save(p.getUniqueId());
    }

    public static boolean hasEnoughSpace(ItemStack[] content, ItemStack item) {
        if (item == null) {
            return false;
        } else {
            Inventory inv = Bukkit.createInventory(null, 54);
            inv.setContents(content);
            HashMap<Integer, ItemStack> leftover = new HashMap();
            leftover.putAll(inv.addItem(item));
            return leftover.isEmpty();
        }
    }

    public static void sendItemToAll(Player p) {
        ItemStack item = p.getInventory().getItemInMainHand().clone();
        if (item == null || item.getType().isAir()) {
            p.sendMessage(plugin.prefix + plugin.getLang().get("hold_item_to_send"));
            return;
        }
        NBT.setStringTag(item, "dpmb_mailitem", new MailItem(new Date().getTime(), 0, 0).serialize());
        for (UserMailBox box : MailBox.udata.values()) {
            DInventory inv = box.getInventory();
            while (true) {
                if (hasEnoughSpace(inv.getContents(), item)) {
                    inv.addItem(item);
                    inv.applyChanges();
                    box.setInventory(inv);
                    MailBox.udata.put(box.getOwnerUUID(), box);
                    if (Bukkit.getOfflinePlayer(box.getOwnerUUID()).isOnline()) {
                        Player user = Bukkit.getPlayer(box.getOwnerUUID());
                        user.sendMessage(plugin.prefix + plugin.getLang().get("new_mail_arrived"));
                    }
                    break;
                } else {
                    inv.setPages(inv.getPages() + 1);
                    inv.nextPage();
                    inv.applyChanges();
                }
            }
            inv.applyAllItemChanges(pi -> {
                MailItem mi = MailItem.deserialize(NBT.getStringTag(pi.getItem(), "dpmb_mailitem"));
                mi.setPage(pi.getPage());
                mi.setSlot(pi.getSlot());
                pi.setItem(NBT.setStringTag(pi.getItem(), "dpmb_mailitem", mi.serialize()));
                return pi;
            });
        }
        p.sendMessage(plugin.prefix + plugin.getLang().get("item_sent_to_all"));
    }

    // send item to player as api
    public static void sendItemToPlayer(UUID target, ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return;
        }
        NBT.setStringTag(item, "dpmb_mailitem", new MailItem(new Date().getTime(), 0, 0).serialize());

        if (!MailBox.udata.containsKey(target)) {
            return;
        }

        UserMailBox box = MailBox.udata.get(target);
        DInventory inv = box.getInventory();

        while (true) {
            if (hasEnoughSpace(inv.getContents(), item)) {
                inv.addItem(item);
                inv.applyChanges();
                box.setInventory(inv);
                MailBox.udata.put(box.getOwnerUUID(), box);
                if (Bukkit.getOfflinePlayer(box.getOwnerUUID()).isOnline()) {
                    Player rp = Bukkit.getPlayer(box.getOwnerUUID());
                    rp.sendMessage(plugin.prefix + plugin.getLang().get("new_mail_arrived"));
                }
                break;
            } else {
                inv.setPages(inv.getPages() + 1);
                inv.nextPage();
                inv.applyChanges();
            }
        }

        inv.applyAllItemChanges(pi -> {
            MailItem mi = MailItem.deserialize(NBT.getStringTag(pi.getItem(), "dpmb_mailitem"));
            mi.setPage(pi.getPage());
            mi.setSlot(pi.getSlot());
            pi.setItem(NBT.setStringTag(pi.getItem(), "dpmb_mailitem", mi.serialize()));
            return pi;
        });
    }

    public static void sendItemToPlayer(CommandSender sender, String sReceiver, boolean isAdmin) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.prefix + plugin.getLang().get("player_only_command"));
            return;
        }
        Player p = (Player) sender;
        OfflinePlayer receiver = Bukkit.getOfflinePlayer(sReceiver);
        if (receiver.getUniqueId().equals(p.getUniqueId())) {
            p.sendMessage(plugin.prefix + plugin.getLang().get("cannot_send_to_self"));
            return;
        }
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) {
            p.sendMessage(plugin.prefix + plugin.getLang().get("hold_item_to_send"));
            return;
        }
        NBT.setStringTag(item, "dpmb_mailitem", new MailItem(new Date().getTime(), 0, 0).serialize());

        if (!MailBox.udata.containsKey(receiver.getUniqueId())) {
            p.sendMessage(plugin.prefix + plugin.getLang().get("mailbox_not_found"));
            return;
        }

        UserMailBox box = MailBox.udata.get(receiver.getUniqueId());
        DInventory inv = box.getInventory();

        while (true) {
            if (hasEnoughSpace(inv.getContents(), item)) {
                inv.addItem(item);
                inv.applyChanges();
                box.setInventory(inv);
                MailBox.udata.put(box.getOwnerUUID(), box);
                if (receiver.isOnline()) {
                    Player rp = Bukkit.getPlayer(box.getOwnerUUID());
                    rp.sendMessage(plugin.prefix + plugin.getLang().get("new_mail_arrived"));
                }
                break;
            } else {
                inv.setPages(inv.getPages() + 1);
                inv.nextPage();
                inv.applyChanges();
            }
        }

        inv.applyAllItemChanges(pi -> {
            MailItem mi = MailItem.deserialize(NBT.getStringTag(pi.getItem(), "dpmb_mailitem"));
            mi.setPage(pi.getPage());
            mi.setSlot(pi.getSlot());
            pi.setItem(NBT.setStringTag(pi.getItem(), "dpmb_mailitem", mi.serialize()));
            return pi;
        });
        if (!isAdmin) {
            item.setAmount(0);
        }
        sender.sendMessage(plugin.prefix + plugin.getLang().getWithArgs("item_sent_to_player", receiver.getName()));
    }

    public static void reloadConfig() {
        plugin.reload();
        plugin.expireSeconds = plugin.config.getInt("Settings.expireSeconds", 604800);
    }

    public static void openMailBox(CommandSender p) {
        if (!(p instanceof Player)) {
            p.sendMessage(plugin.prefix + plugin.getLang().get("player_only_command"));
            return;
        }
        Player player = (Player) p;
        if (!MailBox.udata.containsKey(player.getUniqueId())) {
            p.sendMessage(plugin.prefix + plugin.getLang().get("mailbox_data_not_found"));
            return;
        }
        UserMailBox box = MailBox.udata.get(player.getUniqueId());
        box.openMailBox(player);
    }

    public static void adminOpenMailBox(CommandSender p, String player) {
        if (!(p instanceof Player)) {
            p.sendMessage(plugin.prefix + plugin.getLang().get("player_only_command"));
            return;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(player);
        if (!MailBox.udata.containsKey(target.getUniqueId())) {
            p.sendMessage(plugin.prefix + plugin.getLang().get("player_mailbox_not_found"));
            return;
        }
        UserMailBox box = MailBox.udata.get(target.getUniqueId());
        box.openMailBox((Player) p);
    }

    public static void adminSendItem(CommandSender p, String player) {
        if (!(p instanceof Player)) {
            p.sendMessage(plugin.prefix + plugin.getLang().get("player_only_command"));
            return;
        }
        if (player.equalsIgnoreCase("all")) {
            sendItemToAll((Player) p);
        } else {
            sendItemToPlayer(p, player, true);
        }
    }

    public static void setAdminSendTime(CommandSender p, String timeArg) {
        int seconds;
        // <0d 0h 0m 0s>

        try {
            seconds = parseTimeArgumentToSeconds(timeArg);
            plugin.expireSeconds = seconds;
            plugin.config.set("Settings.expireSeconds", seconds);
            plugin.saveDataContainer();
        } catch (NumberFormatException e) {
            p.sendMessage(plugin.prefix + plugin.getLang().get("invalid_time_format"));
        }
    }

    private static int parseTimeArgumentToSeconds(String timeArg) {
        String[] parts = timeArg.split(" ");
        int totalSeconds = 0;
        for (String part : parts) {
            int i = Integer.parseInt(part.substring(0, part.length() - 1));
            if (part.endsWith("d")) {
                int days = i;
                totalSeconds += days * 86400;
            } else if (part.endsWith("h")) {
                int hours = i;
                totalSeconds += hours * 3600;
            } else if (part.endsWith("m")) {
                int minutes = i;
                totalSeconds += minutes * 60;
            } else if (part.endsWith("s")) {
                int seconds = i;
                totalSeconds += seconds;
            } else {
                throw new NumberFormatException("Invalid time format");
            }
        }
        return totalSeconds;
    }

    public static boolean isExistMailPreset(String presetName) {
        return plugin.presets.containsKey(presetName);
    }

    public static void createMailPreset(Player p, String presetName) {
        if (isExistMailPreset(presetName)) {
            p.sendMessage(plugin.prefix + "해당 프리셋 이름은 이미 존재합니다.");
            return;
        }
        Presets presets = new Presets(presetName);
        plugin.presets.put(presetName, presets);
        plugin.presets.save(presetName);
        p.sendMessage(plugin.prefix + "메일 프리셋이 생성되었습니다: " + presetName);
    }

    public static void setMailPresetItem(Player p, String presetName) {
        if (!isExistMailPreset(presetName)) {
            p.sendMessage(plugin.prefix + "해당 프리셋 이름이 존재하지 않습니다.");
            return;
        }
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) {
            p.sendMessage(plugin.prefix + "설정할 아이템을 손에 들고 명령어를 사용해주세요.");
            return;
        }
        Presets presets = plugin.presets.get(presetName);
        presets.setItem(item.clone());
        plugin.presets.put(presetName, presets);
        plugin.presets.save(presetName);
        p.sendMessage(plugin.prefix + "메일 프리셋 아이템이 설정되었습니다: " + presetName);
    }

    public static void removeMailPreset(Player p, String presetName) {
        if (!isExistMailPreset(presetName)) {
            p.sendMessage(plugin.prefix + "해당 프리셋 이름이 존재하지 않습니다.");
            return;
        }
        plugin.presets.delete(presetName);
        plugin.presets.remove(presetName);
        p.sendMessage(plugin.prefix + "메일 프리셋이 삭제되었습니다: " + presetName);
    }

    public static void sendPresetToPlayer(CommandSender sender, String targetPlayer, String presetName) {
        if (!isExistMailPreset(presetName)) {
            sender.sendMessage(plugin.prefix + "해당 프리셋 이름이 존재하지 않습니다.");
            return;
        }
        Presets presets = plugin.presets.get(presetName);
        presets.send(targetPlayer);
        sender.sendMessage(plugin.prefix + "프리셋이 전송되었습니다: " + presetName + " -> " + targetPlayer);
    }
}
