package com.darksoldier1404.dpmb.obj;

import com.darksoldier1404.dpmb.functions.DPMBFunction;
import com.darksoldier1404.dppc.data.DataCargo;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Presets implements DataCargo {
    private String name;
    private ItemStack item;

    public Presets() {
    }

    public Presets(String name, ItemStack item) {
        this.name = name;
        this.item = item;
    }

    public Presets(String presetName) {
        this.name = presetName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public void send(UUID uuid) {
        if (item == null) return;
        DPMBFunction.sendItemToPlayer(uuid, item);
    }

    public void send(String playerName) {
        if (item == null) return;
        UUID uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();
        DPMBFunction.sendItemToPlayer(uuid, item);
    }

    @Override
    public YamlConfiguration serialize() {
        YamlConfiguration data = new YamlConfiguration();
        data.set("Name", name);
        data.set("Item", item);
        return data;
    }

    @Override
    public Presets deserialize(YamlConfiguration data) {
        this.name = data.getString("Name");
        this.item = data.getItemStack("Item");
        return this;
    }
}
