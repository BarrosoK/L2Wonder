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

import com.l2jfrozen.gameserver.custom.Events.BattleRoyale;
import com.l2jfrozen.gameserver.handler.IVoicedCommandHandler;
import com.l2jfrozen.gameserver.model.actor.instance.L2ArenaManagerInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;

public class BRCmd implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"brjoin",
		"brleave"
	};
	
	@Override
	public boolean useVoicedCommand(final String command, final L2PcInstance activeChar, final String target)
	{
		if (command.startsWith("brjoin"))
		{
			JoinBR(activeChar);
		}
		else if (command.startsWith("brleave"))
		{
			LeaveBR(activeChar);
		}

		
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
	
	public boolean JoinBR(final L2PcInstance activeChar)
	{
		if (activeChar == null)
		{
			return false;
		}
		
		if (!BattleRoyale.is_joining())
		{
			activeChar.sendMessage("There is no Battle Royale Event in progress.");
			return false;
		}
		else if (BattleRoyale.is_joining() && activeChar._inEventBR)
		{
			activeChar.sendMessage("You are already registered.");
			return false;
		}
		else if (activeChar.isCursedWeaponEquipped())
		{
			activeChar.sendMessage("You are not allowed to participate to the event because you are holding a Cursed Weapon.");
			return false;
		}
		else if (activeChar.isInOlympiadMode())
		{
			activeChar.sendMessage("You are not allowed to participate to the event because you are in Olympiad.");
			return false;
		}

		else if (activeChar.getClassId().getId()==16 || activeChar.getClassId().getId()==97) {
			activeChar.sendMessage("You cannot enter this event with this class.");
			return false;
		}
		else if (activeChar.getKarma() > 0)
		{
			activeChar.sendMessage("You are not allowed to participate to the event because you have Karma.");
			return false;
		}
		else if (BattleRoyale.is_teleport() || BattleRoyale.is_started())
		{
			activeChar.sendMessage("Battle Royale Event registration period is over. You can't register now.");
			return false;
		}
		else if(L2ArenaManagerInstance.participants.contains(activeChar) || (activeChar.isInParty() && L2ArenaManagerInstance.participantsLobby.contains(activeChar.getParty())) )
		{
			activeChar.sendMessage("You already participated in street fight arena!");
			return false;
		}
		else
		{
			activeChar.sendMessage("Your participation in the Battle Royale event has been approved.");
			BattleRoyale.addPlayer(activeChar);
			return true;
		}
	}
	
	public boolean LeaveBR(final L2PcInstance activeChar)
	{
		if (activeChar == null)
		{
			return false;
		}
		
		if (!BattleRoyale.is_joining())
		{
			activeChar.sendMessage("There is no Battle Royale Event in progress.");
			return false;
		}
		else if ((BattleRoyale.is_teleport() || BattleRoyale.is_started()) && activeChar._inEventBR)
		{
			activeChar.sendMessage("You can not leave now because Battle Royale event has started.");
			return false;
		}
		else if (BattleRoyale.is_joining() && !activeChar._inEventBR)
		{
			activeChar.sendMessage("You aren't registered in the Battle Royale Event.");
			return false;
		}
		else
		{
			BattleRoyale.removePlayer(activeChar);
			return true;
		}
	}
}