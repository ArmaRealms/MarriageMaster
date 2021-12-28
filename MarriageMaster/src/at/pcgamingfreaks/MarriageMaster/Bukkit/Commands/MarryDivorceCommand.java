/*
 *   Copyright (C) 2021 GeorgH93
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

import at.pcgamingfreaks.Command.HelpData;
import at.pcgamingfreaks.MarriageMaster.Bukkit.API.Marriage;
import at.pcgamingfreaks.MarriageMaster.Bukkit.API.MarriagePlayer;
import at.pcgamingfreaks.MarriageMaster.Bukkit.API.MarryCommand;
import at.pcgamingfreaks.MarriageMaster.Bukkit.MarriageMaster;
import at.pcgamingfreaks.MarriageMaster.Permissions;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class MarryDivorceCommand extends MarryCommand
{
	private String descriptionSelf, helpPriest, helpParam;

	public MarryDivorceCommand(MarriageMaster plugin)
	{
		super(plugin, "divorce", plugin.getLanguage().getTranslated("Commands.Description.Divorce"), plugin.getLanguage().getCommandAliases("Divorce"));

		descriptionSelf = plugin.getLanguage().getTranslated("Commands.Description.DivorceSelf");
		helpParam = "<" + plugin.helpPartnerNameVariable + ">";
		helpPriest = helpParam;
		if(plugin.areMultiplePartnersAllowed())
		{
			helpPriest += " " + helpParam;
		}
	}

	@Override
	public void execute(@NotNull CommandSender sender, @NotNull String mainCommandAlias, @NotNull String alias, @NotNull String[] args)
	{
		MarriagePlayer player = (sender instanceof Player) ? getMarriagePlugin().getPlayerData((Player) sender) : null;
		if(player == null || player.isPriest() || (getMarriagePlugin().isSelfDivorceAllowed() && player.isMarried() && sender.hasPermission(Permissions.SELF_DIVORCE)))
		{
			if((player == null || player.isPriest()) && (getMarriagePlugin().areMultiplePartnersAllowed() ? args.length == 2 : args.length == 1))
			{
				MarriagePlayer p1 = getMarriagePlugin().getPlayerData(args[0]);
				if(args.length == 1)
				{
					Marriage marriage = p1.getMarriageData();
					if(marriage == null)
					{
						((MarriageMaster) getMarriagePlugin()).messagePlayerNotMarried.send(sender, args[0]);
					}
					else
					{
						if(player != null)
						{
							getMarriagePlugin().getMarriageManager().divorce(marriage, player, p1);
						}
						else
						{
							getMarriagePlugin().getMarriageManager().divorce(marriage, sender);
						}
					}
				}
				else
				{
					MarriagePlayer p2 = getMarriagePlugin().getPlayerData(args[1]);
					if(!p1.isMarried())
					{
						((MarriageMaster) getMarriagePlugin()).messagePlayerNotMarried.send(sender, args[0]);
					}
					else if(!p2.isMarried())
					{
						((MarriageMaster) getMarriagePlugin()).messagePlayerNotMarried.send(sender, args[1]);
					}
					else if(p1.isPartner(p2))
					{
						if(player != null)
						{
							getMarriagePlugin().getMarriageManager().divorce(p1.getMarriageData(p2), player, p1);
						}
						else
						{
							getMarriagePlugin().getMarriageManager().divorce(p1.getMarriageData(p2), sender);
						}
					}
					else
					{
						((MarriageMaster) getMarriagePlugin()).messagePlayersNotMarried.send(sender);
					}
				}
			}
			else if(player != null && getMarriagePlugin().isSelfDivorceAllowed() && player.isMarried() && sender.hasPermission(Permissions.SELF_DIVORCE) &&
					(getMarriagePlugin().areMultiplePartnersAllowed() ? (args.length == 1 || player.getPartners().size() == 1 && args.length == 0) : args.length == 0))
			{
				// Self marriage
				Marriage marriage;
				if(args.length == 1)
				{
					MarriagePlayer player2 = getMarriagePlugin().getPlayerData(args[0]);
					if(!player.isPartner(player2))
					{
						((MarriageMaster) getMarriagePlugin()).messageTargetPartnerNotFound.send(sender);
						return;
					}
					else
					{
						marriage = player.getMarriageData(player2);
					}
				}
				else
				{
					marriage = player.getMarriageData();
				}
				marriage.divorce(player);
			}
			else
			{
				showHelp(sender, mainCommandAlias);
			}
		}
		else
		{
			((MarriageMaster) getMarriagePlugin()).messageNoPermission.send(sender);
		}
	}

	@Override
	public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String mainCommandAlias, @NotNull String alias, @NotNull String[] args)
	{
		if(args.length == 0 || !canUse(sender)) return null;
		List<String> names = null;
		MarriagePlayer marriagePlayerData = (sender instanceof Player) ? getMarriagePlugin().getPlayerData((Player) sender) : null;
		if(marriagePlayerData == null || marriagePlayerData.isPriest())
		{
			names = new LinkedList<>();
			String arg = args[args.length - 1].toLowerCase(Locale.ENGLISH);
			for(Player player : Bukkit.getOnlinePlayers())
			{
				if(!player.getName().equals(sender.getName()) && player.getName().toLowerCase(Locale.ENGLISH).startsWith(arg))
				{
					names.add(player.getName());
				}
			}
		}
		else if(getMarriagePlugin().isSelfDivorceAllowed() && marriagePlayerData.isMarried() && sender.hasPermission(Permissions.SELF_DIVORCE))
		{
			names = marriagePlayerData.getMatchingPartnerNames(args[args.length - 1]);
		}
		return names;
	}

	@Override
	public List<HelpData> getHelp(@NotNull CommandSender requester)
	{
		List<HelpData> help = new ArrayList<>(2);
		MarriagePlayer player = (requester instanceof Player) ? getMarriagePlugin().getPlayerData((Player) requester) : null;
		if(player != null && getMarriagePlugin().isSelfDivorceAllowed() && player.isMarried() && (requester.hasPermission(Permissions.SELF_MARRY) || requester.hasPermission(Permissions.SELF_DIVORCE)))
		{
			if(player.getPartners().size() > 1)
			{
				help.add(new HelpData(getTranslatedName(), helpParam, descriptionSelf));
			}
			else
			{
				help.add(new HelpData(getTranslatedName(), "", descriptionSelf));
			}
		}
		if(player == null || player.isPriest())
		{
			help.add(new HelpData(getTranslatedName(), helpPriest, getDescription()));
		}
		return help;
	}
}