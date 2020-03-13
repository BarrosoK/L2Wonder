/*
 * L2jFrozen Project - www.l2jfrozen.com 
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jfrozen.gameserver.handler.voicedcommandhandlers;

import com.l2jfrozen.gameserver.datatables.sql.NpcTable;
import com.l2jfrozen.gameserver.handler.IVoicedCommandHandler;
import com.l2jfrozen.gameserver.managers.GrandBossManager;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;
import javolution.text.TextBuilder;

public class RaidInfoCmd implements IVoicedCommandHandler
{


	private static final String[] VOICED_COMMANDS =
			{
					"raidinfo",
			};

	private static final int[] BOSSES =
			{
					25325, 29006, 29014, 29001, 29022, 29020, 29067, 29028, 13052, 13051, 13050, 13048, 13047, 660000
			};

	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target) {

		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setHtml(showRbInfo());
		activeChar.sendPacket(html);
		return true;
	}

	private final String showRbInfo()
	{
		TextBuilder tb = new TextBuilder();
		tb.append("<html><title>Chat</title><body><br>");
		tb.append("<br>");
		tb.append("<center><font color=0174DF>Grand Boss Info:</font></center>");
		tb.append("<center>");
		tb.append("<img src=L2UI.SquareWhite width=280 height=1><br>");
		tb.append("</center>");
		tb.append("<br>");
		tb.append("<center>");
		tb.append("<table width = 280>");
		for(int boss : BOSSES )
		{
			String name = NpcTable.getInstance().getTemplate(boss).getName();
			long delay = GrandBossManager.getInstance().getStatsSet(boss).getLong("respawn_time");
			if (delay <= System.currentTimeMillis())
			{
				tb.append("<tr>");
				tb.append("<td><font color=\"FA5858\">" + name + "</color>:</td> " + "<td><font color=\"00BFFF\">Is Alive</color></td>"+"<br1>");
				tb.append("</tr>");
			}
			else
			{
				int hours = (int) ((delay - System.currentTimeMillis()) / 1000 / 60 / 60);
				int mins = (int) (((delay - (hours * 60 * 60 * 1000)) - System.currentTimeMillis()) / 1000 / 60);
				int seconts = (int) (((delay - ((hours * 60 * 60 * 1000) + (mins * 60 * 1000))) - System.currentTimeMillis()) / 1000);
				tb.append("<tr>");
				tb.append("<td><font color=\"FA5858\">" + name + "</color></td>" + "<td><font color=\"00BFFF\">" +" " + "Respawn in :</color></td>" + " " + "<td><font color=\"00BFFF\">" + hours + " : " + mins + " : " + seconts + "</color></td><br1>");
				tb.append("</tr>");
			}
		}
		tb.append("</table>");
		tb.append("</center>");
		tb.append("<br><center>");
		tb.append("<br><img src=L2UI.SquareWhite width=280 height=1><br>");
		tb.append("</center>");
		tb.append("</body></html>");

		return tb.toString();
	}

	@Override
	public String[] getVoicedCommandList() {
		return VOICED_COMMANDS;
	}

}