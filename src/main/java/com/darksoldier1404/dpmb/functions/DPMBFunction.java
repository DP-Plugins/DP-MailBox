package com.darksoldier1404.dpmb.functions;

import com.darksoldier1404.dpmb.MailBox;
import com.darksoldier1404.dpmb.obj.MailItem;
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
            p.sendMessage(plugin.prefix + "보낼 아이템을 손에 들고 있어야 합니다.");
            return;
        }
        NBT.setStringTag(item, "dpmb_mailitem", new MailItem(item.clone(), new Date().getTime(), 0, 0).serialize());
        for (UserMailBox box : MailBox.udata.values()) {
            DInventory inv = box.getInventory();
            while (true) {
                if (hasEnoughSpace(inv.getContents(), item)) {
                    inv.addItem(item);
                    inv.applyChanges();
                    box.setInventory(inv);
                    MailBox.udata.put(box.getUUID(), box);
                    if (Bukkit.getOfflinePlayer(box.getUUID()).isOnline()) {
                        Player user = Bukkit.getPlayer(box.getUUID());
                        user.sendMessage(plugin.prefix + "새로운 메일이 도착했습니다!");
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
                pi.setItem(NBT.setStringTag(mi.getItem(), "dpmb_mailitem", mi.serialize()));
                return pi;
            });
        }
        p.sendMessage(plugin.prefix + "아이템을 모든 유저의 메일함으로 보냈습니다.");
    }

    public static void sendItemToPlayer(CommandSender sender, String sReceiver, boolean isAdmin) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.prefix + "플레이어만 사용할 수 있는 명령어입니다.");
            return;
        }
        Player p = (Player) sender;
        OfflinePlayer receiver = Bukkit.getOfflinePlayer(sReceiver);
        if(receiver.getUniqueId().equals(p.getUniqueId())) {
            p.sendMessage(plugin.prefix + "자신에게는 아이템을 보낼 수 없습니다.");
            return;
        }
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) {
            p.sendMessage(plugin.prefix + "보낼 아이템을 손에 들고 있어야 합니다.");
            return;
        }
        NBT.setStringTag(item, "dpmb_mailitem", new MailItem(item.clone(), new Date().getTime(), 0, 0).serialize());

        if (!MailBox.udata.containsKey(receiver.getUniqueId())) {
            p.sendMessage(plugin.prefix + "해당 유저의 메일함을 찾을 수 없습니다.");
            return;
        }

        UserMailBox box = MailBox.udata.get(receiver.getUniqueId());
        DInventory inv = box.getInventory();

        while (true) {
            if (hasEnoughSpace(inv.getContents(), item)) {
                inv.addItem(item);
                inv.applyChanges();
                box.setInventory(inv);
                MailBox.udata.put(box.getUUID(), box);
                if (receiver.isOnline()) {
                    Player rp = Bukkit.getPlayer(box.getUUID());
                    rp.sendMessage(plugin.prefix + "새로운 메일이 도착했습니다!");
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
            pi.setItem(NBT.setStringTag(mi.getItem(), "dpmb_mailitem", mi.serialize()));
            return pi;
        });
        if (!isAdmin) {
            item.setAmount(0);
        }
        sender.sendMessage(plugin.prefix + "아이템을 " + receiver.getName() + "님의 메일함으로 보냈습니다.");
    }

    public static void reloadConfig() {
        plugin.reload();
        plugin.expireSeconds = plugin.config.getInt("Settings.expireSeconds", 604800);
    }

    public static void openMailBox(CommandSender p) {
        if (!(p instanceof Player)) {
            p.sendMessage(plugin.prefix + "§f플레이어만 사용할 수 있는 명령어입니다.");
            return;
        }
        Player player = (Player) p;
        if (!MailBox.udata.containsKey(player.getUniqueId())) {
            p.sendMessage(plugin.prefix + "§f메일함 데이터를 찾을 수 없습니다. 다시 시도해주세요.");
            return;
        }
        UserMailBox box = MailBox.udata.get(player.getUniqueId());
        box.openMailBox(player);
    }

    public static void adminOpenMailBox(CommandSender p, String player) {

    }

    public static void adminSendItem(CommandSender p, String player) {
        if (!(p instanceof Player)) {
            p.sendMessage(plugin.prefix + "플레이어만 사용할 수 있는 명령어입니다.");
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
            p.sendMessage(plugin.prefix + "시간 형식이 올바르지 않습니다. 예: 3d 6h 0m 0s");
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

    public static ItemStack getMailItemFromItemStack(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return null;
        }
        String serializedMailItem = NBT.getStringTag(item, "dpmb_mailitem");
        if (serializedMailItem == null || serializedMailItem.isEmpty()) {
            return null;
        }
        MailItem mailItem = MailItem.deserialize(serializedMailItem);
        return mailItem.getItem();
    }
}
