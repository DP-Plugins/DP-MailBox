package com.darksoldier1404.dpmb.obj;

import com.darksoldier1404.dppc.utils.ItemStackSerializer;
import com.darksoldier1404.dppc.utils.NBT;
import org.bukkit.inventory.ItemStack;

public class MailItem {
    ItemStack item;
    long receiveDate;
    int page;
    int slot;

    public MailItem() {
    }

    public MailItem(ItemStack item, long receiveDate, int page, int slot) {
        this.item = item;
        this.receiveDate = receiveDate;
        this.page = page;
        this.slot = slot;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public long getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(long receiveDate) {
        this.receiveDate = receiveDate;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public ItemStack getAsItemStack() {
        return NBT.setStringTag(item.clone(), "dpmb_mailitem", this.serialize());
    }

    public String serialize() {
        return ItemStackSerializer.serialize(item) + ";" + receiveDate + ";" + page + ";" + slot;
    }

    public static MailItem deserialize(String data) {
        String[] parts = data.split(";");
        ItemStack item = ItemStackSerializer.deserialize(parts[0]);
        long receiveDate = Long.parseLong(parts[1]);
        int page = Integer.parseInt(parts[2]);
        int slot = Integer.parseInt(parts[3]);
        return new MailItem(item, receiveDate, page, slot);
    }
}
