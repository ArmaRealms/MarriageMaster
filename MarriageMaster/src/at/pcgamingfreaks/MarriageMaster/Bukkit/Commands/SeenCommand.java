/*
 *   Copyright (C) 2022 GeorgH93
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.MarriageMaster.Bukkit.Commands;

import at.pcgamingfreaks.Bukkit.Message.Message;
import at.pcgamingfreaks.Calendar.TimeSpan;
import at.pcgamingfreaks.MarriageMaster.Bukkit.API.MarriagePlayer;
import at.pcgamingfreaks.MarriageMaster.Bukkit.API.MarryCommand;
import at.pcgamingfreaks.MarriageMaster.Bukkit.MarriageMaster;
import at.pcgamingfreaks.MarriageMaster.DisplayNamePlaceholderProcessor;
import at.pcgamingfreaks.MarriageMaster.Permissions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SeenCommand extends MarryCommand
{
	private final Message messageLastSeen, messageCurrentlyOnline;
	private final SimpleDateFormat dateFormat;

	public SeenCommand(MarriageMaster plugin)
	{
		super(plugin, "seen", plugin.getLanguage().getTranslated("Commands.Description.Seen"), Permissions.SEEN, true, true, plugin.getLanguage().getCommandAliases("seen"));

		dateFormat = new SimpleDateFormat(plugin.getLanguage().getLangE().getString("Language.Ingame.Seen.DateFormat", "yyyy.MM.dd 'at' HH:mm:ss")); //TODO make format provider
		messageLastSeen = plugin.getLanguage().getMessage("Ingame.Seen.LastSeen").placeholder("Name").placeholder("Date").placeholder("CountTotalDays").placeholder("Count");
		messageCurrentlyOnline = plugin.getLanguage().getMessage("Ingame.Seen.CurrentlyOnline").placeholder("Name").placeholder("DisplayName", DisplayNamePlaceholderProcessor.INSTANCE);
	}

	@Override
	public void execute(@NotNull CommandSender sender, @NotNull String mainCommandAlias, @NotNull String alias, @NotNull String[] args)
	{
		MarriagePlayer player = getMarriagePlugin().getPlayerData((Player) sender);
		MarriagePlayer partner = (getMarriagePlugin().areMultiplePartnersAllowed() && args.length >= 1) ? player.getPartner(args[0]) : player.getNearestPartnerMarriageData().getPartner(player);
		if(partner == null)
		{
			player.sendMessage(((MarriageMaster) getMarriagePlugin()).messageTargetPartnerNotFound);
		}
		else if(partner.isOnline())
		{
			player.sendMessage(messageCurrentlyOnline, partner.getName(), partner);
		}
		else
		{
			Date lastOnline = new Date(partner.getPlayer().getLastPlayed());
			TimeSpan timeSpan = new TimeSpan(lastOnline);
			player.send(messageLastSeen, partner.getName(), dateFormat.format(lastOnline), timeSpan.getTotalDays(), timeSpan.toString());
		}
	}

	@Override
	public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String mainCommandAlias, @NotNull String alias, @NotNull String[] args)
	{
		return getMarriagePlugin().getCommandManager().getSimpleTabComplete(sender, args);
	}
}