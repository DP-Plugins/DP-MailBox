package com.darksoldier1404.dpmb;

import com.darksoldier1404.dpmb.obj.UserMailBox;
import com.darksoldier1404.dppc.annotation.DPPCoreVersion;
import com.darksoldier1404.dppc.data.DPlugin;
import com.darksoldier1404.dppc.data.DataContainer;
import com.darksoldier1404.dppc.data.DataType;
import com.darksoldier1404.dppc.utils.PluginUtil;
import com.darksoldier1404.dpmb.commands.DPMBCommand;
import com.darksoldier1404.dpmb.events.DPMBEvent;

import java.util.UUID;

@DPPCoreVersion(since = "5.3.0")
public class MailBox extends DPlugin {
    public static MailBox plugin;
    public static DataContainer<UUID, UserMailBox> udata;
    public int expireSeconds;

    public MailBox() {
        super(false);
        plugin = this;
        init();
        udata = loadDataContainer(new DataContainer<>(plugin, DataType.CUSTOM, "udata"), UserMailBox.class);
        expireSeconds = config.getInt("Settings.expireSeconds", 604800);
    }

    @Override
    public void onLoad() {
        PluginUtil.addPlugin(plugin, 27647);
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new DPMBEvent(), plugin);
        getCommand("dpmb").setExecutor(new DPMBCommand().getExecutor());
    }

    @Override
    public void onDisable() {
        saveDataContainer();
    }
}
