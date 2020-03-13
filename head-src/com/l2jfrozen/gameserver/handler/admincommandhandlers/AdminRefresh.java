/*
 * L2jFrozen Project - www.l2jfrozen.com
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * [URL]http://www.gnu.org/copyleft/gpl.html[/URL]
 */
package com.l2jfrozen.gameserver.handler.admincommandhandlers;

import com.l2jfrozen.gameserver.model.L2Skill;

import com.l2jfrozen.gameserver.handler.IAdminCommandHandler;
import com.l2jfrozen.gameserver.model.L2Object;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;


/**
 * This class handles following admin commands: - refresh = refreshes skills of target L2Pc
 * $Date: 2018/01/16 04:48:06 $
 */
public class AdminRefresh implements IAdminCommandHandler {

    private static final String[] ADMIN_COMMANDS =
            {
                    "admin_refresh",
            };

    @Override
    public boolean useAdminCommand(final String command, final L2PcInstance activeChar) {


        if (command.equals("admin_refresh")) {
            handleRefresh(activeChar);
        }

        return true;
    }

    @Override
    public String[] getAdminCommandList() {
        return ADMIN_COMMANDS;
    }

    private void handleRefresh(final L2PcInstance activeChar) {

        L2Object target = activeChar.getTarget();

        if (target instanceof L2PcInstance) {
            doRefresh((L2PcInstance) target);
        } else {
            activeChar.sendMessage("Incorrect Target.");
        }

    }


    private void doRefresh(final L2PcInstance targetChar) {

        // If the target is a player, then refresh all his skills.
        for (final L2Skill skill : targetChar.getAllSkills()) {
            if (skill.getId() != 1324) {
                targetChar.enableSkill(skill);
            }
        }
        targetChar.updateEffectIcons();
        targetChar.sendSkillList();

    }
}
