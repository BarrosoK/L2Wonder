package com.l2jfrozen.gameserver.model.actor.instance;

import com.l2jfrozen.gameserver.handler.IUserCommandHandler;
import com.l2jfrozen.gameserver.handler.UserCommandHandler;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;
import javolution.text.TextBuilder;

public class L2VoteManagerInstance extends L2NpcInstance {
    public L2VoteManagerInstance(int objectId, L2NpcTemplate template) {
        super(objectId, template);
    }

    public void onBypassFeedback(L2PcInstance player, String command) {
        if (command.startsWith("vote")) {
            final IUserCommandHandler handler = UserCommandHandler.getInstance().getUserCommandHandler(115);
            handler.useUserCommand(115, player);
        }
    }

    public void showChatWindow(L2PcInstance player, int val) {
        NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
        msg.setHtml(voteManagerWindow(player));
        msg.replace("%objectId%", String.valueOf(getObjectId()));
        msg.replace("%countdownHop%", String.valueOf(player.getVoteCountdownHop()));
        msg.replace("%countdownTop%", String.valueOf(player.getVoteCountdownTop()));
        msg.replace("%countdownNet%", String.valueOf(player.getVoteCountdownNet()));
        player.sendPacket(msg);
    }

    private String voteManagerWindow(L2PcInstance player) {

        TextBuilder tb = new TextBuilder();
        tb.append("<html> <body> <center><table width=300 border=0>\n" +
                "<tr>\n" +
                "    <td width=270 height=100><img src=l2wonderlust.logo width=275 height=100 align=center></td>\n" +
                "</table>");
        tb.append("Vote Manager");
        tb.append("<br><br>" +
                "<center><font color=\"FFFF33\">L2Hopzone</font></center><br>");
        if (player.eligibleToVoteHop()) {
            tb.append("<center><button value=\"I have voted on L2Hopzone\" action=\"bypass -h vote hopzone\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\">");
        } else {
            tb.append("You can vote again in: %countdownHop%");
        }

        tb.append("<br><center><font color=\"FFFF33\">L2Topzone</font></center><br>");
        if (player.eligibleToVoteTop()) {
            tb.append("<center><button value=\"I have voted on L2Topzone\" action=\"bypass -h vote topzone\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\">");
        } else {
            tb.append("You can vote again in: %countdownTop%");
        }

        tb.append("<br><center><font color=\"FFFF33\">L2Network</font></center><br>");
        if (player.eligibleToVoteNet()) {
            tb.append("<center><button value=\"I have voted on L2Network\" action=\"bypass -h vote network\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\">");
        } else {
            tb.append("You can vote again in: %countdownNet%");
        }
        tb.append("</center></body></html>");
        return tb.toString();
    }






        /*  TextBuilder tb = new TextBuilder();
        tb.append("<html><title>Vote Manager</title><body>");
        tb.append("<center><table width=300 border=0>\n" +
                "<tr>\n" +
                "    <td width=270 height=100><img src=l2wonderlust.logo width=275 height=100 align=center></td>\n" +
                "</table>" +
                "<font color=\"3b8d8d\">Vote Manager</font></center><br><br><br><br><br><br>");
        tb.append("<center<button value=\"Vote Now\" action=\"bypass -h npc_%objectId%_vote\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\">");
        tb.append("</body></html>");
        return tb.toString();
    }*/


}