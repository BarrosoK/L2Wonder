package com.l2jfrozen.gameserver.model.actor.instance;

import com.l2jfrozen.gameserver.ai.CtrlIntention;
import com.l2jfrozen.gameserver.ai.L2PlayerAI;
import com.l2jfrozen.gameserver.model.L2World;
import com.l2jfrozen.gameserver.model.entity.Announcements;
import com.l2jfrozen.gameserver.model.entity.olympiad.Olympiad;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jfrozen.gameserver.network.serverpackets.SetupGauge;
import com.l2jfrozen.gameserver.network.serverpackets.SocialAction;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;
import com.l2jfrozen.gameserver.thread.ThreadPoolManager;
import com.l2jfrozen.util.random.Rnd;
import javolution.text.TextBuilder;

import java.util.*;

public class L2DiceEventInstance extends L2NpcInstance {

    public HashMap<Integer, L2PcInstance> participants = new HashMap<Integer, L2PcInstance>();
    public HashMap<L2PcInstance, Integer> results = new HashMap<L2PcInstance, Integer>();
    public int minimumParticipants = 2;
    public boolean canJoin = true;
    public int timeToThrow = 20;

    public L2DiceEventInstance(int objectId, L2NpcTemplate template) {
        super(objectId, template);
    }

    public void onBypassFeedback(L2PcInstance player, String command) {
        if (command.startsWith("dice")) {
            StringTokenizer st = new StringTokenizer(command);
            try {
                st.nextToken();
                String param = st.nextToken();

                switch (param) {

                    case "join": {
                        addPlayer(player);
                    }
                    break;
                    case "leave": {
                        removePlayer(player);
                    }
                    break;
                }
            } catch (NoSuchElementException nse) {
            }
        }
    }

    public void removePlayer(L2PcInstance player) {

        if (player == null) {
            return;
        }

        if (participants.containsKey(player.getObjectId())) {
            participants.remove(player.getObjectId());
            customWindow(player, "You left the queue");
            player.setIsInDiceEvent(false);
        } else {
            customWindow(player, "You're not registered");
        }
    }

    public void addPlayer(L2PcInstance player) {

        if (player == null) {
            return;
        }

        int playerId = player.getObjectId();

        if (participants.containsKey(playerId)) {
            sendCustomChatWindow(player, "You're already registered !");
            return;
        }

        if (!canJoin) {
            sendCustomChatWindow(player, "Registration disabled !");
            return;
        }

        if (player.isRegisteredInFunEvent() || player.isInFunEvent()) {
            sendCustomChatWindow(player, "You're already registered on another event");
            return;
        }

        if (player.isInOlympiadMode()) {
            sendCustomChatWindow(player, "You're already registered on olympiad");
            return;

        } else if (Olympiad.getInstance().isRegistered(player)) {
            sendCustomChatWindow(player, "You're already registered on olympiad");
            return;

        }

        if (L2ArenaManagerInstance.participants.contains(player) || L2ArenaManagerInstance.participantsLobby.contains(player) || L2ArenaManagerInstance.inFightOrWaiting.contains(player)) {
            sendCustomChatWindow(player, "You're already registered on 1v1 arena.");
            return;
        }

        participants.put(playerId, player);

        if (participants.size() >= minimumParticipants) {
            startEvent();
        } else {
            sendCustomChatWindow(player, "You're registered ! <br> Players left to start : " + (minimumParticipants - participants.size()));
        }
    }

    public void startEvent() {

        for (Map.Entry<Integer, L2PcInstance> playerMap : participants.entrySet()) {

            if (playerMap == null) {
                continue;
            }

            L2PcInstance player = playerMap.getValue();

            if (player == null) {
                player = L2World.getInstance().getPlayer(playerMap.getKey());
            }
            player.setIsInDiceEvent(true);
            player.setDiceEvent(this);
        }

        startRound(1);

    }

    public void startRound(final int round) {

        for (Map.Entry<Integer, L2PcInstance> playerMap : participants.entrySet()) {

            L2PcInstance player = playerMap.getValue();

            if (player == null) {
                player = L2World.getInstance().getPlayer(playerMap.getKey());
            }
            results.put(player, 0);
        }

        for (Map.Entry<Integer, L2PcInstance> playerMap : participants.entrySet()) {

            L2PcInstance player = playerMap.getValue();

            if (player == null) {
                player = L2World.getInstance().getPlayer(playerMap.getKey());
            }

            customWindow(player, "Round " + round + " started ! Throw ur dices you have 20 secs !");

        }
        new java.util.Timer().schedule(

                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        endRound(round);
                        this.cancel();
                    }
                },
                (1000 * timeToThrow)
        );
    }

    public void endRound(int round) {

        Vector<L2PcInstance> worstPlayer = new Vector<L2PcInstance>();
        int worstNumber = 0;
        for (Map.Entry<L2PcInstance, Integer> playerThrow : results.entrySet()) {

            if (playerThrow == null) {
                continue;
            }

            L2PcInstance player = playerThrow.getKey();
            int number = playerThrow.getValue();

            if (number == 0) {
                sendCustomChatWindow(player, "Throw ur dice next time");
            }

            if (worstNumber == 0) {
                worstPlayer.add(player);
                worstNumber = number;
            } else {
                if (number < worstNumber) {
                    worstPlayer.clear();
                    worstPlayer.add(player);
                } else if (number == worstNumber) {
                    worstPlayer.add(player);
                }

            }
        }

        for (L2PcInstance looser : worstPlayer) {

            if (looser == null) {
                continue;
            }

            if (worstNumber != 0) {
                Announcements.getInstance().announceToAll("" + looser.getName() + " lost with " + worstNumber);
            }
            looser.setDiceEvent(null);
            looser.setIsInDiceEvent(false);
            participants.remove(looser.getObjectId());
            results.remove(looser);
        }

        for (Map.Entry<L2PcInstance, Integer> playerThrow : results.entrySet()) {

            if (playerThrow == null) {
                continue;
            }
            playerThrow.getKey().addAdena("diceEvent", (playerThrow.getValue() * 100), null, true);
        }

        if (participants.size() > 1) {
            startRound(round + 1);
        } else if (participants.size() == 1) {
            Announcements.getInstance().announceToAll("Dice ended winner : " + participants.get(participants.keySet().toArray()[0]).getName());
            participants.get(participants.keySet().toArray()[0]).setIsInDiceEvent(false);
            participants.get(participants.keySet().toArray()[0]).setDiceEvent(null);
            participants.clear();
            canJoin = true;
        } else if (participants.size() == 0) {
            Announcements.getInstance().announceToAll("No winners for this one");
        }
    }

    public void showChatWindow(L2PcInstance player, int val) {
        NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());

        if (!participants.containsKey(player.getObjectId())) {
            msg.setHtml(joinWindow(player));
        } else {
            msg.setHtml(waitingWindow(player));
        }

        msg.replace("%objectId%", String.valueOf(getObjectId()));
        player.sendPacket(msg);
    }

    private void sendCustomChatWindow(L2PcInstance player, String text) {

        NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
        msg.setHtml(customWindow(player, text));
        msg.replace("%objectId%", String.valueOf(getObjectId()));
        player.sendPacket(msg);
    }

    private String joinWindow(L2PcInstance player) {
        TextBuilder tb = new TextBuilder();
        tb.append("<html><title>L2DICE</title><body>");
        tb.append("<center><img src=\"L2UI_CH3.onscrmsg_pattern01_1\" width=295 height=32><br><br>");
        tb.append("<font color=\"3b8d8d\">DICE</font><br>");
        tb.append("<img src=\"L2UI.SquareGray\" width=\"280\" height=\"1\"><br>");
        tb.append("<button value=\"Join\" action=\"bypass -h npc_%objectId%_dice join\" width=204 height=20 back=\"sek.cbui75\" fore=\"sek.cbui75\">");
        tb.append("");
        tb.append("</center>");
        tb.append("<center><img src=\"L2UI_CH3.onscrmsg_pattern01_2\" width=295 height=32><br></center>");
        tb.append("</body></html>");
        return tb.toString();
    }

    private String waitingWindow(L2PcInstance player) {
        TextBuilder tb = new TextBuilder();
        tb.append("<html><title>L2DICE</title><body>");
        tb.append("<center><img src=\"L2UI_CH3.onscrmsg_pattern01_1\" width=295 height=32><br><br>");
        tb.append("<font color=\"3b8d8d\">DICE</font><br>");
        tb.append("<img src=\"L2UI.SquareGray\" width=\"280\" height=\"1\"><br>");
        tb.append("Remaining people " + (minimumParticipants - participants.size()));
        tb.append("<button value=\"Leave\" action=\"bypass -h npc_%objectId%_dice leave\" width=204 height=20 back=\"sek.cbui75\" fore=\"sek.cbui75\">");
        tb.append("</center>");
        tb.append("<center><img src=\"L2UI_CH3.onscrmsg_pattern01_2\" width=295 height=32><br></center>");
        tb.append("</body></html>");
        return tb.toString();
    }

    private String customWindow(L2PcInstance player, String text) {
        TextBuilder tb = new TextBuilder();
        tb.append("<html><title>L2DICE</title><body>");
        tb.append("<center><img src=\"L2UI_CH3.onscrmsg_pattern01_1\" width=295 height=32><br><br>");
        tb.append("<font color=\"3b8d8d\">DICE</font><br>");
        tb.append("<img src=\"L2UI.SquareGray\" width=\"280\" height=\"1\"><br>");
        tb.append("" + text + "<br>");
        tb.append("</center>");
        tb.append("<center><img src=\"L2UI_CH3.onscrmsg_pattern01_2\" width=295 height=32><br></center>");
        tb.append("</body></html>");
        return tb.toString();
    }
}