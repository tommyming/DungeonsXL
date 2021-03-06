/*
 * Copyright (C) 2012-2018 Frank Baumann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.dre2n.dungeonsxl.command;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.command.DRECommand;
import de.erethon.commons.compatibility.CompatibilityHandler;
import de.erethon.commons.compatibility.Internals;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessage;
import io.github.dre2n.dungeonsxl.event.DataReloadEvent;
import io.github.dre2n.dungeonsxl.player.DInstancePlayer;
import io.github.dre2n.dungeonsxl.player.DPermission;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class ReloadCommandNoSpigot extends DRECommand {

    DungeonsXL plugin = DungeonsXL.getInstance();

    public ReloadCommandNoSpigot() {
        setCommand("reload");
        setMinArgs(0);
        setMaxArgs(1);
        setHelp(DMessage.HELP_CMD_RELOAD.getMessage());
        setPermission(DPermission.RELOAD.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        List<DInstancePlayer> dPlayers = plugin.getDPlayers().getDInstancePlayers();

        PluginManager plugins = Bukkit.getPluginManager();

        DataReloadEvent event = new DataReloadEvent();
        plugins.callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        for (DInstancePlayer dPlayer : dPlayers) {
            dPlayer.leave();
        }

        int maps = DungeonsXL.MAPS.listFiles().length;
        int dungeons = DungeonsXL.DUNGEONS.listFiles().length;
        int loaded = plugin.getDWorlds().getEditWorlds().size() + plugin.getDWorlds().getGameWorlds().size();
        int players = plugin.getDPlayers().getDGamePlayers().size();
        Internals internals = CompatibilityHandler.getInstance().getInternals();
        String vault = "";
        if (plugins.getPlugin("Vault") != null) {
            vault = plugins.getPlugin("Vault").getDescription().getVersion();
        }
        String mythicMobs = "";
        if (plugins.getPlugin("MythicMobs") != null) {
            mythicMobs = plugins.getPlugin("MythicMobs").getDescription().getVersion();
        }

        plugin.onDisable();
        plugin.loadCore();
        plugin.loadData();

        MessageUtil.sendPluginTag(sender, plugin);
        MessageUtil.sendCenteredMessage(sender, DMessage.CMD_RELOAD_DONE.getMessage());
        MessageUtil.sendCenteredMessage(sender, DMessage.CMD_MAIN_LOADED.getMessage(String.valueOf(maps), String.valueOf(dungeons), String.valueOf(loaded), String.valueOf(players)));
        MessageUtil.sendCenteredMessage(sender, DMessage.CMD_MAIN_COMPATIBILITY.getMessage(String.valueOf(internals), vault, mythicMobs));
    }

}
