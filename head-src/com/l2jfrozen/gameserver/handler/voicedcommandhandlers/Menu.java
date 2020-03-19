package com.l2jfrozen.gameserver.handler.voicedcommandhandlers;

import com.l2jfrozen.gameserver.handler.IVoicedCommandHandler;
import com.l2jfrozen.gameserver.model.L2World;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.network.serverpackets.ExAutoSoulShot;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;
import javolution.text.TextBuilder;

/**
 * Created by Server1 on 6/17/2017.
 */
public class Menu implements IVoicedCommandHandler
{
    private final String[] _voicedCommands =
            {
                    "menu"
            };

    @Override
    public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
    {
        mainHtml(activeChar);
        return true;
    }

    public static void mainHtml(L2PcInstance activeChar)
    {
        NpcHtmlMessage nhm = new NpcHtmlMessage(5);
        TextBuilder tb = new TextBuilder("");

        tb.append("<html><head><title>Personal Menu</title></head><body>");
        tb.append("<center>");
        tb.append("<table width=\"250\" cellpadding=\"5\" bgcolor=\"000000\">");
        tb.append("<tr>");
        tb.append("<td width=\"45\" valign=\"top\" align=\"center\"><img src=\"L2ui_ch3.menubutton4\" width=\"38\" height=\"38\"></td>");
        tb.append("<td valign=\"top\">Players online <font color=\"FF6600\"> "+ (int)(L2World.getInstance().getAllPlayers().size()*5)+"</font>");
        tb.append("<br1><font color=\"00FF00\">"+activeChar.getName()+"</font>, use this menu for everything related to your gameplay.<br1></td>");
        tb.append("</tr>");
        tb.append("</table>");
        tb.append("</center>");
        tb.append("<center>");
        tb.append("<table border=\"1\" width=\"100\" height=\"12\" bgcolor=\"000000\">");
        tb.append("<tr>");
        tb.append("<td width=\"52\">ON</td>");
        tb.append("<td width=\"16\"><button width=16 height=12 back=\"L2UI_CH3.br_bar1_hp\" fore=\"L2UI_CH3.br_bar1_hp\"></td>");
        tb.append("<td><button width=32 height=12 back=\"L2UI_CH3.tutorial_pointer1\" fore=\"L2UI_CH3.tutorial_pointer1\"></td>");
        tb.append("</tr>");
        tb.append("<tr>");
        tb.append("<td width=\"52\">OFF</td>");
        tb.append("<td width=\"16\"><button width=16 height=12 back=\"L2UI_CH3.br_bar1_mp\" fore=\"L2UI_CH3.br_bar1_mp\"></td>");
        tb.append("<td><button width=32 height=12 back=\"L2UI_CH3.tutorial_pointer1\" fore=\"L2UI_CH3.tutorial_pointer1\"></td>");
        tb.append("</tr>");
        tb.append("</table><br>");

        tb.append("<table border=\"1\" width=\"250\" height=\"12\" bgcolor=\"000000\">");
        tb.append("<tr>");
        tb.append("<td align=\"center\" width=\"52\">Auto Healing Potions</td>");
        if(activeChar.useAutoHPPots) {
            tb.append("<td width=\"16\"><button action=\"bypass -h autohppots\" width=24 height=12 back=\"L2UI_CH3.br_bar1_hp\" fore=\"L2UI_CH3.br_bar1_hp\"></td>");
        }
        else {
            tb.append("<td width=\"16\"><button action=\"bypass -h autohppots\" width=24 height=12 back=\"L2UI_CH3.br_bar1_mp\" fore=\"L2UI_CH3.br_bar1_mp\"></td>");

        }
        tb.append("</tr>");
        tb.append("<tr><td width=\"250\"><font color=\"00FF00\">By enabling that the use of healing potions will be automatic.</font></td></tr>");
        tb.append("</table>");

        tb.append("<table border=\"1\" width=\"250\" height=\"12\" bgcolor=\"000000\">");
        tb.append("<tr>");
        tb.append("<td align=\"center\" width=\"52\">Auto Mana Potions</td>");
        if(activeChar.useAutoMPPots)
            tb.append("<td width=\"16\"><button action=\"bypass -h automppots\" width=24 height=12 back=\"L2UI_CH3.br_bar1_hp\" fore=\"L2UI_CH3.br_bar1_hp\"></td>");
        else
            tb.append("<td width=\"16\"><button action=\"bypass -h automppots\" width=24 height=12 back=\"L2UI_CH3.br_bar1_mp\" fore=\"L2UI_CH3.br_bar1_mp\"></td>");
        tb.append("</tr>");
        tb.append("<tr><td width=\"250\"><font color=\"00FF00\">By enabling that the use of mana potions will be automatic.</font></td></tr>");
        tb.append("</table>");

        tb.append("<table border=\"1\" width=\"250\" height=\"12\" bgcolor=\"000000\">");
        tb.append("<tr>");
        tb.append("<td align=\"center\" width=\"52\">Auto CP Potions</td>");
        if(activeChar.useAutoCPPots)
            tb.append("<td width=\"16\"><button action=\"bypass -h autocppots\" width=24 height=12 back=\"L2UI_CH3.br_bar1_hp\" fore=\"L2UI_CH3.br_bar1_hp\"></td>");
        else
            tb.append("<td width=\"16\"><button action=\"bypass -h autocppots\" width=24 height=12 back=\"L2UI_CH3.br_bar1_mp\" fore=\"L2UI_CH3.br_bar1_mp\"></td>");
        tb.append("</tr>");
        tb.append("<tr><td width=\"250\"><font color=\"00FF00\">By enabling that the use of cp potions will be automatic.</font></td></tr>");
        tb.append("</table>");

        tb.append("<table border=\"1\" width=\"250\" height=\"12\" bgcolor=\"000000\">");
        tb.append("<tr>");
        tb.append("<td align=\"center\" width=\"52\">Buff Protection</td>");
        if(activeChar.isBuffProtected())
            tb.append("<td width=\"16\"><button action=\"bypass -h buffprot\" width=24 height=12 back=\"L2UI_CH3.br_bar1_hp\" fore=\"L2UI_CH3.br_bar1_hp\"></td>");
        if(!activeChar.isBuffProtected())
            tb.append("<td width=\"16\"><button action=\"bypass -h buffprot\" width=24 height=12 back=\"L2UI_CH3.br_bar1_mp\" fore=\"L2UI_CH3.br_bar1_mp\"></td>");
        tb.append("</tr>");
        tb.append("<tr><td width=\"250\"><font color=\"00FF00\">By enabling that you won't be able to recieve ANY buff from another character.</font></td></tr>");
        tb.append("</table>");

        tb.append("<table border=\"1\" width=\"250\" height=\"12\" bgcolor=\"000000\">");
        tb.append("<tr>");
        tb.append("<td align=\"center\" width=\"52\">Personal Message Refusal</td>");
        if(activeChar.getMessageRefusal())
            tb.append("<td width=\"16\"><button action=\"bypass -h pmref\" width=24 height=12 back=\"L2UI_CH3.br_bar1_hp\" fore=\"L2UI_CH3.br_bar1_hp\"></td>");
        if(!activeChar.getMessageRefusal())
            tb.append("<td width=\"16\"><button action=\"bypass -h pmref\" width=24 height=12 back=\"L2UI_CH3.br_bar1_mp\" fore=\"L2UI_CH3.br_bar1_mp\"></td>");
        tb.append("</tr>");
        tb.append("<tr><td width=\"250\"><font color=\"00FF00\">By enabling that you won't be able to recieve ANY pm from another character.</font></td></tr>");
        tb.append("</table>");
        tb.append("<table border=\"1\" width=\"250\" height=\"12\" bgcolor=\"000000\">");
        tb.append("<tr>");
        tb.append("<table border=\"1\" width=\"250\" height=\"12\" bgcolor=\"000000\">");
        tb.append("<tr>");
        tb.append("<td align=\"center\" width=\"52\">Trade Request Protection</td>");
        if(activeChar.isInTradeProt())
            tb.append("<td width=\"16\"><button action=\"bypass -h tradeprot\" width=24 height=12 back=\"L2UI_CH3.br_bar1_hp\" fore=\"L2UI_CH3.br_bar1_hp\"></td>");
        if(!activeChar.isInTradeProt())
            tb.append("<td width=\"16\"><button action=\"bypass -h tradeprot\" width=24 height=12 back=\"L2UI_CH3.br_bar1_mp\" fore=\"L2UI_CH3.br_bar1_mp\"></td>");
        tb.append("</tr>");
        tb.append("<tr><td width=\"250\"><font color=\"00FF00\">By enabling that you won't be able to recieve ANY trade request from another character.</font></td></tr>");
        tb.append("</table>");
        tb.append("<table border=\"1\" width=\"250\" height=\"12\" bgcolor=\"000000\">");
        tb.append("<tr>");
        tb.append("<td align=\"center\" width=\"52\">Party Invite Protection</td>");
        if(activeChar.isPartyInvProt())
            tb.append("<td width=\"16\"><button action=\"bypass -h partyin\" width=24 height=12 back=\"L2UI_CH3.br_bar1_hp\" fore=\"L2UI_CH3.br_bar1_hp\"></td>");
        if(!activeChar.isPartyInvProt())
            tb.append("<td width=\"16\"><button action=\"bypass -h partyin\" width=24 height=12 back=\"L2UI_CH3.br_bar1_mp\" fore=\"L2UI_CH3.br_bar1_mp\"></td>");
        tb.append("</tr>");
        tb.append("<tr><td width=\"250\"><font color=\"00FF00\">By enabling that you won't be able to recieve ANY party invite from another character.</font></td></tr>");
        tb.append("</table>");
        tb.append("</center>");
        tb.append("</body></html>");

        nhm.setHtml(tb.toString());
        activeChar.sendPacket(nhm);
    }

    public static void mainHtml2(L2PcInstance activeChar)
    {
        NpcHtmlMessage nhm = new NpcHtmlMessage(5);
        TextBuilder tb = new TextBuilder("");

        tb.append("<html><head><title>Personal Menu</title></head><body>");
        tb.append("<center>");
        tb.append("<table width=\"250\" cellpadding=\"5\" bgcolor=\"000000\">");
        tb.append("<tr>");
        tb.append("<td width=\"45\" valign=\"top\" align=\"center\"><img src=\"L2ui_ch3.menubutton4\" width=\"38\" height=\"38\"></td>");
        tb.append("<td valign=\"top\">Players online <font color=\"FF6600\"> "+L2World.getInstance().getAllPlayers().size()+"</font>");
        tb.append("<br1><font color=\"00FF00\">"+activeChar.getName()+"</font>, use this menu for everything related to your gameplay.<br1></td>");
        tb.append("</tr>");
        tb.append("</table>");
        tb.append("</center>");
        tb.append("<center>");
        tb.append("<table border=\"1\" width=\"100\" height=\"12\" bgcolor=\"000000\">");
        tb.append("<tr>");
        tb.append("<td width=\"52\">ON</td>");
        tb.append("<td width=\"16\"><button width=16 height=12 back=\"L2UI_CH3.br_bar1_hp\" fore=\"L2UI_CH3.br_bar1_hp\"></td>");
        tb.append("<td><button width=32 height=12 back=\"L2UI_CH3.tutorial_pointer1\" fore=\"L2UI_CH3.tutorial_pointer1\"></td>");
        tb.append("</tr>");
        tb.append("<tr>");
        tb.append("<td width=\"52\">OFF</td>");
        tb.append("<td width=\"16\"><button width=16 height=12 back=\"L2UI_CH3.br_bar1_mp\" fore=\"L2UI_CH3.br_bar1_mp\"></td>");
        tb.append("<td><button width=32 height=12 back=\"L2UI_CH3.tutorial_pointer1\" fore=\"L2UI_CH3.tutorial_pointer1\"></td>");
        tb.append("</tr>");
        tb.append("</table><br>");

        tb.append("</center>");
        tb.append("</body></html>");

        nhm.setHtml(tb.toString());
        activeChar.sendPacket(nhm);
    }

    @Override
    public String[] getVoicedCommandList()
    {
        return _voicedCommands;
    }
}