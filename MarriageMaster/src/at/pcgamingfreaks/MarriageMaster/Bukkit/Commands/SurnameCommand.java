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
import at.pcgamingfreaks.MarriageMaster.Bukkit.API.MarriagePlayer;
import at.pcgamingfreaks.MarriageMaster.Bukkit.API.MarryCommand;
import at.pcgamingfreaks.MarriageMaster.Bukkit.MarriageMaster;
import at.pcgamingfreaks.MarriageMaster.Permissions;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class SurnameCommand extends MarryCommand
{
	private final String helpSurnameParam, helpPriestParam, helpMMParam, descSelf;

	public SurnameCommand(MarriageMaster plugin)
	{
		super(plugin, "surname", plugin.getLanguage().getTranslated("Commands.Description.Surname"), Permissions.CHANGE_SURNAME, plugin.getLanguage().getCommandAliases("Surname"));

		descSelf         = plugin.getLanguage().getTranslated("Commands.Description.SurnameSelf");
		helpSurnameParam = "<" + plugin.getLanguage().getTranslated("Commands.SurnameVariable") + ">";
		helpMMParam      = "<" + plugin.helpPartnerNameVariable + "> " + helpSurnameParam;
		if(plugin.areMultiplePartnersAllowed())
		{
			helpPriestParam = "<" + plugin.helpPlayerNameVariable + "> <" + plugin.helpPlayerNameVariable + "> " + helpSurnameParam;
		}
		else
		{
			helpPriestParam = "<" + plugin.helpPlayerNameVariable + "> " + helpSurnameParam;
		}
	}

	@Override
	public void execute(@NotNull CommandSender sender, @NotNull String mainCommandAlias, @NotNull String alias, @NotNull String[] args)
	{
		if(args.length == 0)
		{
			showHelp(sender, mainCommandAlias);
			return;
		}
		final String newSurname = args[args.length - 1];
		MarriagePlayer player = (sender instanceof Player) ? getMarriagePlugin().getPlayerData((Player) sender) : null;
		if(!(sender instanceof Player) || player.isPriest() || getMarriagePlugin().isSelfMarriageAllowed() && sender.hasPermission(Permissions.SELF_MARRY) && player.isMarried())
		{
			if(args.length == 1 && getMarriagePlugin().isSelfMarriageAllowed() && player != null && sender.hasPermission(Permissions.SELF_MARRY))
			{
				if(player.getPartners().size() == 1)
				{
					player.getMarriageData().setSurname(newSurname, player);
				}
				else
				{
					((MarriageMaster) getMarriagePlugin()).messageNotMarried.send(sender);
				}
			}
			else if(args.length == 2)
			{
				MarriagePlayer player2 = getMarriagePlugin().getPlayerData(args[0]);
				if(!(sender instanceof Player) || player.isPriest())
				{
					if(!player2.isOnline())
					{
						((MarriageMaster) getMarriagePlugin()).messagePlayerNotOnline.send(sender, args[0]);
					}
					else if(!player2.isMarried())
					{
						((MarriageMaster) getMarriagePlugin()).messagePlayerNotMarried.send(sender, args[0]);
					}
					else if(player2.getMultiMarriageData().size() > 1)
					{
						if(player != null && player.isPartner(player2))
						{
							player.getMarriageData(player2).setSurname(newSurname, player);
						}
						else
						{
							((MarriageMaster) getMarriagePlugin()).messageMarriageNotExact.send(sender);
						}
					}
					else
					{
						player2.getMarriageData().setSurname(newSurname, sender);
					}
				}
				else if(getMarriagePlugin().isSelfMarriageAllowed() && sender.hasPermission(Permissions.SELF_MARRY))
				{
					if(player.isMarried())
					{
						if(player.isPartner(player2))
						{
							player.getMarriageData(player2).setSurname(newSurname, player);
						}
						else
						{
							((MarriageMaster) getMarriagePlugin()).messageTargetPartnerNotFound.send(sender);
						}
					}
					else
					{
						((MarriageMaster) player).messageNotMarried.send(sender);
					}
				}
				else
				{
					showHelp(sender, mainCommandAlias);
				}
			}
			else if(args.length == 3 && getMarriagePlugin().areMultiplePartnersAllowed() && (!(sender instanceof Player) || player.isPriest()))
			{
				MarriagePlayer player1 = getMarriagePlugin().getPlayerData(args[0]), player2 = getMarriagePlugin().getPlayerData(args[1]);
				if(!player1.isMarried())
				{
					((MarriageMaster) getMarriagePlugin()).messagePlayerNotMarried.send(sender, player1.getName());
				}
				else if(!player2.isMarried())
				{
					((MarriageMaster) getMarriagePlugin()).messagePlayerNotMarried.send(sender, player2.getName());
				}
				else if(!player1.isPartner(player2))
				{
					((MarriageMaster) getMarriagePlugin()).messagePlayersNotMarried.send(sender);
				}
				else
				{
					player1.getMarriageData(player2).setSurname(newSurname, sender);
				}
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
		if(args.length == 1 || (getMarriagePlugin().areMultiplePartnersAllowed() && args.length == 2))
		{
			MarriagePlayer player = (sender instanceof Player) ? getMarriagePlugin().getPlayerData((Player) sender) : null;
			if(!(sender instanceof Player) || player.isPriest())
			{
				List<String> names = new LinkedList<>();
				String name = sender.getName().toLowerCase(Locale.ENGLISH), arg = args[args.length - 1].toLowerCase(Locale.ENGLISH), tmp;
				for(Player p : Bukkit.getOnlinePlayers())
				{
					tmp = p.getName().toLowerCase(Locale.ENGLISH);
					if(tmp.equals(name) || tmp.startsWith(arg))
					{
						names.add(p.getName());
					}
				}
				return names;
			}
			else if(player.getPartners().size() > 1)
			{
				return player.getMatchingPartnerNames(args[args.length - 1]);
			}
		}
		return null;
	}

	@Override
	public List<HelpData> getHelp(@NotNull CommandSender requester)
	{
		List<HelpData> help = new LinkedList<>();
		MarriagePlayer player = (requester instanceof Player) ? getMarriagePlugin().getPlayerData((Player) requester) : null;
		if(player != null && player.isMarried() && getMarriagePlugin().isSelfMarriageAllowed() && requester.hasPermission(Permissions.SELF_MARRY))
		{
			if(player.getPartners().size() > 1)
			{
				help.add(new HelpData(getTranslatedName(), helpMMParam, descSelf));
			}
			else
			{
				help.add(new HelpData(getTranslatedName(), helpSurnameParam, descSelf));
			}
		}
		if(!(requester instanceof Player) || player.isPriest())
		{
			help.add(new HelpData(getTranslatedName(), helpPriestParam, getDescription()));
		}
		return help;
	}
}