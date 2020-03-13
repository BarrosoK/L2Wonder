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

import com.l2jfrozen.gameserver.handler.IVoicedCommandHandler;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;

public class L2MultiFunctionZoneCMD implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
			{
					"exit",
			};

	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target) {
		if (command.startsWith("exit")&&activeChar.isInsideMultifunctionZone)
		{
			if(activeChar.isInCombat()){
				activeChar.sendMessage("You cannot exit PvP Zone while in combat.");
				return false;
			}
			activeChar.teleToLocation(81190,148613,-3496,true);
		}
		return true;
	}

	@Override
	public String[] getVoicedCommandList() {
		return VOICED_COMMANDS;
	}

}