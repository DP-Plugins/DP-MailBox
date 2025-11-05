package com.darksoldier1404.dpmb.commands;

import com.darksoldier1404.dpmb.functions.DPMBFunction;
import com.darksoldier1404.dppc.builder.command.CommandBuilder;

import java.util.ArrayList;
import java.util.Arrays;

import static com.darksoldier1404.dpmb.MailBox.plugin;

public class DPMBCommand {
    private final CommandBuilder builder = new CommandBuilder(plugin);

    public DPMBCommand() {
        builder.addSubCommand("open", "dpmb.open", "/dpmb open", true, (p, args) -> {
            if (args.length == 1) {
                DPMBFunction.openMailBox(p);
                return true;
            }
            return false;
        });

        builder.addSubCommand("send", "dpmb.send", "/dpmb send <player>", true, (p, args) -> {
            if (args.length == 2) {
                DPMBFunction.sendItemToPlayer(p, args[1], false);
                return true;
            }
            return false;
        });

        builder.addSubCommand("admin", "dpmb.admin", "/dpmb admin open <player>", true, (p, args) -> {
            if (args.length == 3 && args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("open")) {
                DPMBFunction.adminOpenMailBox(p, args[2]);
                return true;
            }
            if (args.length == 3 && args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("send")) {
                DPMBFunction.adminSendItem(p, args[2]);
                return true;
            }
            if (args.length >= 3 && args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("time")) {
                String timeArg = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                DPMBFunction.setAdminSendTime(p, timeArg);
                return true;
            }
            return true;
        });
        for (String c : builder.getSubCommandNames()) {
            builder.addTabCompletion(c, (sender, args) -> {
                if (args.length == 1) {
                    return new ArrayList<>(Arrays.asList("open", "send", "admin"));
                }
                if (args.length == 2 && args[0].equalsIgnoreCase("admin")) {
                    return new ArrayList<>(Arrays.asList("open", "send"));
                }
                if (args.length == 3) {
                    if (args[0].equalsIgnoreCase("send")) {
                        ArrayList<String> playerNames = new ArrayList<>();
                        plugin.getServer().getOnlinePlayers().forEach(player -> playerNames.add(player.getName()));
                        return playerNames;
                    }
                    if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("open")) {
                        ArrayList<String> playerNames = new ArrayList<>();
                        plugin.getServer().getOnlinePlayers().forEach(player -> playerNames.add(player.getName()));
                        return playerNames;
                    }
                    if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("send")) {
                        ArrayList<String> playerNames = new ArrayList<>();
                        plugin.getServer().getOnlinePlayers().forEach(player -> playerNames.add(player.getName()));
                        playerNames.add("all");
                        return playerNames;
                    }
                }
                return null;
            });
        }
    }

    public CommandBuilder getExecutor() {
        return builder;
    }
}
