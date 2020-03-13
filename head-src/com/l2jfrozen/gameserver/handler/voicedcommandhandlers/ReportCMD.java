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

import com.l2jfrozen.gameserver.datatables.GmListTable;
import com.l2jfrozen.gameserver.handler.IVoicedCommandHandler;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.util.database.L2DatabaseFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class ReportCMD implements IVoicedCommandHandler

{
    private static final String[] VOICED_COMMANDS =
            {
                    "report",
            };

    @Override
    public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target) {
        if (command.startsWith("report")) {
            if (activeChar.getTarget() instanceof L2PcInstance && activeChar.getTarget() != activeChar && !((L2PcInstance) activeChar.getTarget()).isGM()) {
                if (!activeChar.playersReported.contains(((L2PcInstance) activeChar.getTarget()).getCharId()) && !((L2PcInstance) activeChar.getTarget()).reportedBy.contains(activeChar.getCharId())) {
                    activeChar.sendMessage("You have reported " + activeChar.getTarget().getName() + " for botting. GMs notified.");
                    ((L2PcInstance) activeChar.getTarget()).sendMessage(activeChar.getName() + " reported you for botting, GMs notified.");
                    activeChar.playersReported.add(((L2PcInstance) activeChar.getTarget()).getCharId());
                    ((L2PcInstance) activeChar.getTarget()).reportedBy.add(activeChar.getCharId());

                    for (L2PcInstance gm : GmListTable.getInstance().getAllGms(true)) {
                        gm.sendMessage(activeChar.getName() + " reported " + activeChar.getTarget().getName() + " for botting.");
                    }

                    databaseSave(activeChar, (L2PcInstance) activeChar.getTarget());

                    return true;
                } else {
                    activeChar.sendMessage("You have already reported " + activeChar.getTarget().getName() + " for botting.");
                    return false;
                }
            } else {
                activeChar.sendMessage("You are not targeting a player.");
                return false;
            }
        }
        return true;
    }

    private void databaseSave(L2PcInstance activeChar, L2PcInstance target) {

        Connection con = null;
        PreparedStatement state = null;
        java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());

        try {

            con = L2DatabaseFactory.getInstance().getConnection();
            state = con.prepareStatement("REPLACE INTO reports VALUES ( ?, ?, ?);");
            state.setString(1, activeChar.getName());
            state.setString(2, target.getName());
            state.setTimestamp(3, date);
            state.execute();

            state.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (state != null) {
                try {
                    state.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    @Override
    public String[] getVoicedCommandList() {
        return VOICED_COMMANDS;
    }

}