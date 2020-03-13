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


import com.l2jfrozen.gameserver.handler.IVoicedCommandHandler;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.network.serverpackets.ActionFailed;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;
import javolution.text.TextBuilder;

/**
 * @author Elfocrash
 */
public class VoteCommand implements IVoicedCommandHandler {
    private static final String[] VOICED_COMMANDS = {"vote"};


    @Override
    public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target) {
        final NpcHtmlMessage html = new NpcHtmlMessage(0);
        html.setHtml(getVoteHtml(activeChar));
        html.replace("%countdownHop%", String.valueOf(activeChar.getVoteCountdownHop()));
        html.replace("%countdownTop%", String.valueOf(activeChar.getVoteCountdownTop()));
        html.replace("%countdownNet%", String.valueOf(activeChar.getVoteCountdownNet()));
        activeChar.sendPacket(html);

        activeChar.sendPacket(ActionFailed.STATIC_PACKET);

        return true;
    }

    @Override
    public String[] getVoicedCommandList() {
        return VOICED_COMMANDS;
    }


    private static String getVoteHtml(L2PcInstance player) {
        TextBuilder tb = new TextBuilder();
        tb.append("<html> <body> <center><table width=300 border=0>\n" +
                "<tr>\n" +
                "    <td width=270 height=100><img src=l2wonderlust.logo width=275 height=100 align=center></td>\n" +
                "</table>");
        tb.append("Vote Manager");
        tb.append("<br><br>");
        if (player.eligibleToVoteHop()) {
            tb.append("<center><button value=\"I have voted on L2Hopzone\" action=\"bypass -h vote hopzone\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\">");
        } else {
            tb.append("You can vote again in: %countdownHop%");
        }
        tb.append("<td align=center><img src=L2UI_CH3.herotower_deco width=256 height=32>");
        if (player.eligibleToVoteTop()) {
            tb.append("<center><button value=\"I have voted on L2Topzone\" action=\"bypass -h vote topzone\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\">");
        } else {
            tb.append("You can vote again in: %countdownTop%");
        }
        tb.append("<td align=center><img src=L2UI_CH3.herotower_deco width=256 height=32>");

        if (player.eligibleToVoteNet()) {
            tb.append("<center><button value=\"I have voted on L2Network\" action=\"bypass -h vote network\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\">");
        } else {
            tb.append("You can vote again in: %countdownNet%");
        }
        tb.append("</center></body></html>");
        return tb.toString();
    }


}