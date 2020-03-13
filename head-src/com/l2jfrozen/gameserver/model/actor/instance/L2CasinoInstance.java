package com.l2jfrozen.gameserver.model.actor.instance;

import com.l2jfrozen.gameserver.ai.CtrlIntention;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jfrozen.gameserver.network.serverpackets.SetupGauge;
import com.l2jfrozen.gameserver.network.serverpackets.SocialAction;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;
import com.l2jfrozen.gameserver.thread.ThreadPoolManager;
import com.l2jfrozen.util.random.Rnd;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javolution.text.TextBuilder;

public class L2CasinoInstance extends L2NpcInstance {
    public L2CasinoInstance(int objectId, L2NpcTemplate template) {
        super(objectId, template);
    }

    public void onBypassFeedback(L2PcInstance player, String command) {
        int am_beep_t = 0;
        if (command.startsWith("play1")) {
            StringTokenizer st = new StringTokenizer(command);
            try {
                st.nextToken();
                am_beep_t = Integer.parseInt(st.nextToken());
            } catch (NoSuchElementException nse) {
            }
            Casino1(player, am_beep_t);
        }
    }

    public static void displayCongrats(L2PcInstance player) {
        player.broadcastPacket(new SocialAction(player.getObjectId(), 3));
        player.sendMessage("Congratulations You Won!");
    }

    public static void displayCongrats2(L2PcInstance player) {
        player.broadcastPacket(new SocialAction(player.getObjectId(), 13));
        player.sendMessage("You lost!");

    }

    public void showChatWindow(L2PcInstance player, int val) {
        NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
        msg.setHtml(casinoWindow(player));
        msg.replace("%objectId%", String.valueOf(getObjectId()));
        player.sendPacket(msg);
    }

    private String casinoWindow(L2PcInstance player) {
        TextBuilder tb = new TextBuilder();
        tb.append("<html><title>L2Casino</title><body>");
        tb.append("<center><img src=\"L2UI_CH3.onscrmsg_pattern01_1\" width=295 height=32><br><br>");
        tb.append("<font color=\"3b8d8d\">Chance to win : 50%</font><br>");
        tb.append("<img src=\"L2UI.SquareGray\" width=\"280\" height=\"1\"><br>");
        tb.append("<tr><td><font color=\"e0d6b9\">Double or Nothing </font></td></tr><br>");
        tb.append("<img src=\"L2UI.SquareGray\" width=\"280\" height=\"1\"></center><br>");
        tb.append("<center>");
        tb.append("<font color=\"e0d6b9\">Place your bet: Adena!(100k-MAX)</font>");
        tb.append("</center>");
        tb.append("<center><img src=\"L2UI.SquareGray\" width=\"280\" height=\"1\"></center><br>");
        tb.append("<br>");
        tb.append("<center>");
        tb.append("<tr>");
        tb.append("<td align=center><edit var=\"qbox\" width=120 height=15><br></td> <td align=right></td>");
        tb.append("<td align=center><button value=\"Bet\" action=\"bypass -h npc_%objectId%_play1 $qbox\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"></td>");
        tb.append("</tr>");
        tb.append("</center>");

        tb.append("<center><img src=\"L2UI_CH3.onscrmsg_pattern01_2\" width=295 height=32><br></center>");
        tb.append("</body></html>");
        return tb.toString();
    }

    public static void Casino1(L2PcInstance player, int am_beep_t) {
        int unstuckTimer = 100;
        player.setTarget(player);
        player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        player.disableAllSkills();
        SetupGauge sg = new SetupGauge(0, unstuckTimer);
        player.sendPacket(sg);

        Casino1 ef = new Casino1(player, am_beep_t);
        player.setSkillCast(ThreadPoolManager.getInstance().scheduleGeneral(ef, unstuckTimer));
    }

    static class Casino1 implements Runnable {
        private L2PcInstance _player;
        private int _am_beep_t;

        Casino1(L2PcInstance player, int am_beep_t) {
            this._am_beep_t = am_beep_t;
            this._player = player;
        }

        public void run() {
            if (this._player.isDead()) {
                return;
            }

            this._player.setIsIn7sDungeon(false);
            this._player.enableAllSkills();
            int chance = Rnd.get(2);

            if (this._am_beep_t <1) {
                this._player.sendMessage("Incorrect value.");
                return;
            }

            if (this._am_beep_t > 100000) {
                this._player.sendMessage("Maximum bet is 100k.");
                return;
            }

            if (this._player.getInventory().getInventoryItemCount(57, 0) >= this._am_beep_t) {

                if (chance == 0) {
                    L2CasinoInstance.displayCongrats(this._player);

                    this._player.addItem("Gift", 57, this._am_beep_t, this._player, true);
                    this._player.broadcastUserInfo();
                }

                if (chance == 1) {
                    L2CasinoInstance.displayCongrats2(this._player);

                    this._player.destroyItemByItemId("Consume", 57, this._am_beep_t, this._player, true);
                    this._player.broadcastUserInfo();
                }
            } else {
                this._player.sendMessage("You do not have enough Adena!");
            }
        }
    }
}