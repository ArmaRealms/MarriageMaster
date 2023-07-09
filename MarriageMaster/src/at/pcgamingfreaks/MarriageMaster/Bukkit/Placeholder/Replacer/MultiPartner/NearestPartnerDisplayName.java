/*
 *   Copyright (C) 2023 GeorgH93
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

package at.pcgamingfreaks.MarriageMaster.Bukkit.Placeholder.Replacer.MultiPartner;

import at.pcgamingfreaks.Bukkit.Placeholder.PlaceholderName;
import at.pcgamingfreaks.MarriageMaster.Bukkit.API.MarriagePlayer;
import at.pcgamingfreaks.MarriageMaster.Bukkit.MarriageMaster;
import at.pcgamingfreaks.MarriageMaster.Bukkit.Placeholder.PlaceholderFormatted;
import at.pcgamingfreaks.MarriageMaster.Bukkit.Placeholder.Replacer.MultiPartner.Formatted.NearestPartnerDisplayNameFormatted;
import at.pcgamingfreaks.MarriageMaster.Bukkit.Placeholder.Replacer.PlaceholderReplacerBaseValue;

import org.jetbrains.annotations.Nullable;

@PlaceholderName(aliases = { "Nearest_PartnerDisplayName", "Nearest_Partner_DisplayName" })
@PlaceholderFormatted(formattedClass = NearestPartnerDisplayNameFormatted.class)
public class NearestPartnerDisplayName extends PlaceholderReplacerBaseValue
{
	public NearestPartnerDisplayName(MarriageMaster plugin)
	{
		super(plugin);
	}

	@Override
	protected @Nullable String replaceMarried(MarriagePlayer player)
	{
		//noinspection ConstantConditions
		return player.getNearestPartnerMarriageData().getPartner(player).getDisplayName();
	}
}