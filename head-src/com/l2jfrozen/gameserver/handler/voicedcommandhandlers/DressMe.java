/*
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


import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.datatables.sql.ItemTable;
import com.l2jfrozen.gameserver.handler.IVoicedCommandHandler;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author Anarchy
 *
 */
public class DressMe implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS = { "dressme" };


	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target) {
		if (command.equals("dressme"))
		{
			sendMainWindow(activeChar);
		}

		return true;
	}

	
	public static void sendMainWindow(L2PcInstance activeChar)
	{
		NpcHtmlMessage htm = new NpcHtmlMessage(0);
		htm.setFile("./data/html/custom/dressme/main.htm");
		htm.replace("%enabled%", activeChar.isDressMeEnabled() ? "Enabled" : "Disabled");
		if (activeChar.getDressMeData() == null)
		{
			htm.replace("%chestinfo%", "You have no Costume on.");

		}
		else {
			htm.replace("%chestinfo%", (activeChar.getDressMeData().getChestId() == 0 || !Config.DRESS_ME_CHESTS.containsValue(activeChar.getDressMeData().getChestId())) ? "You have no Costume on." : ItemTable.getInstance().getTemplate(activeChar.getDressMeData().getChestId()).getName());
		}
		
		activeChar.sendPacket(htm);
	}

	public static void setPart(L2PcInstance p, String part, String type) {
		if (p.getDressMeData() == null) {
			com.l2jfrozen.gameserver.handler.custom.DressMeData dmd = new com.l2jfrozen.gameserver.handler.custom.DressMeData();
			p.setDressMeData(dmd);
		}
		if (p.getInventory().getItemByItemId(Config.DRESS_ME_CHESTS.get(type)) == null) {
			p.sendMessage("You do not have that costume in your inventory.");
			sendEditWindow(p, part);
			return;
		}

		switch (part) {
			case "chest": {
				if (Config.DRESS_ME_CHESTS.keySet().contains(type)) {
					p.getDressMeData().setChestId(Config.DRESS_ME_CHESTS.get(type));
				}
				break;
			}
		}

		p.broadcastUserInfo();
		sendEditWindow(p, part);
	}

	public static void sendEditWindow(L2PcInstance p, String part) {
		NpcHtmlMessage htm = new NpcHtmlMessage(0);
		htm.setFile("./data/html/custom/dressme/edit.htm");

		htm.replace("%enabled%", p.isDressMeEnabled() ? "Enabled" : "Disabled");
		htm.replace("%part%", part);

		switch (part) {
			case "chest": {
				if (p.getDressMeData() == null) {
					htm.replace("%partinfo%", "You have no Costume on.");
				} else {
					htm.replace("%partinfo%", p.getDressMeData().getChestId() == 0 ? "You have no Costume on." : ItemTable.getInstance().getTemplate(p.getDressMeData().getChestId()).getName());
				}
				String temp = "";
				for (String s : Config.DRESS_ME_CHESTS.keySet()) {
					temp += s + ";";
				}
				htm.replace("%dropboxdata%", temp);
				break;

			}

		}

		p.sendPacket(htm);
	}


	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}