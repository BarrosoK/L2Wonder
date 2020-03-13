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
package com.l2jfrozen.gameserver.network.clientpackets;

import com.l2jfrozen.gameserver.model.actor.instance.L2ArenaManagerInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.scripts.ArenaFight;
import com.l2jfrozen.gameserver.model.scripts.ArenaFightPtVsPt;

public final class ObserverReturn extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final L2PcInstance activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        ArenaFight arena = null;
        for (ArenaFight a : L2ArenaManagerInstance.fights) {
            if (a != null) {
                for (L2PcInstance p : a.getObservers()) {
                    if (p.getName() == activeChar.getName()) {
                        arena = a;
                        break;
                    }
                }
            }
        }

        if (arena != null) {
            arena.removeOneObserver(activeChar);
        }
        ArenaFightPtVsPt ar = null;
        for (ArenaFightPtVsPt a : L2ArenaManagerInstance.fightPtVsPt) {
            if (a != null) {
                for (L2PcInstance p : a.getObservers()) {
                    if (p.getName() == activeChar.getName()) {
                        ar = a;
                        break;
                    }
                }
            }
        }

        if (ar != null) {
            ar.removeOneObserver(activeChar);
        }


        if (activeChar.inObserverMode()) {
            activeChar.leaveObserverMode();
        }
    }

    @Override
    public String getType() {
        return "[C] b8 ObserverReturn";
    }
}