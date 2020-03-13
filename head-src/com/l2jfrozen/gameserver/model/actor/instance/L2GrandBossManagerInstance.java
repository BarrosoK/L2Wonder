package com.l2jfrozen.gameserver.model.actor.instance;

import com.l2jfrozen.gameserver.ai.CtrlIntention;
import com.l2jfrozen.gameserver.datatables.sql.NpcTable;
import com.l2jfrozen.gameserver.managers.GrandBossManager;
import com.l2jfrozen.gameserver.network.serverpackets.ActionFailed;
import com.l2jfrozen.gameserver.network.serverpackets.MyTargetSelected;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jfrozen.gameserver.network.serverpackets.ValidateLocation;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;
import javolution.text.TextBuilder;

public class L2GrandBossManagerInstance extends L2NpcInstance
{
    private static final int[] BOSSES =
            {
                    25325, 29006, 29014, 29001, 29022, 29020, 29067, 29028, 13052, 13051, 13050, 13048, 13047, 660000
            };

    public L2GrandBossManagerInstance(int objectId, L2NpcTemplate template)
    {
        super(objectId, template);
    }

    @Override
    public void onAction(L2PcInstance player)
    {
        if (!canTarget(player)) {
            return;
        }

        if (this != player.getTarget())
        {
            player.setTarget(this);

            player.sendPacket(new MyTargetSelected(getObjectId(), 0));

            player.sendPacket(new ValidateLocation(this));
        }
        else if (!canInteract(player))
        {
            player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
        }
        else
        {
            showHtmlWindow(player);
        }

        player.sendPacket(new ActionFailed());
    }

    private void showHtmlWindow(L2PcInstance activeChar)
    {
        showRbInfo(activeChar);

        activeChar.sendPacket(new ActionFailed());
    }

    private final void showRbInfo(L2PcInstance player)
    {
        NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        TextBuilder tb = new TextBuilder();
        tb.append("<html><title>Grand Boss Manager</title><body><br>");
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
                tb.append("<td><font color=\"FA5858\">" + name + "</color>:</td> " + "<td><font color=\"00BFFF\"> Is Alive</color></td>"+"<br1>");
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
        html.setHtml(tb.toString());
        html.replace("%objectId%", String.valueOf(getObjectId()));
        player.sendPacket(html);
    }
}