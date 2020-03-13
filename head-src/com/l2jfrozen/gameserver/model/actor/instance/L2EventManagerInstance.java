package com.l2jfrozen.gameserver.model.actor.instance;

import com.l2jfrozen.gameserver.custom.Events.BattleRoyale;
import com.l2jfrozen.gameserver.model.entity.event.CTF;
import com.l2jfrozen.gameserver.model.entity.event.DM;
import com.l2jfrozen.gameserver.model.entity.event.TvT;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;
import javolution.text.TextBuilder;

import java.util.StringTokenizer;

/**
 * Created by Server1 on 6/9/2018.
 */
public class L2EventManagerInstance extends L2NpcInstance {

    private int event = 0;
    private String eventName = "";
    private String eventfName = "";
    private int playersRegistered = 0;

    public L2EventManagerInstance(int objectId, L2NpcTemplate template) {
        super(objectId, template);
    }


    public void onBypassFeedback(L2PcInstance player, String command) {
        if (command.startsWith("event")) {
            StringTokenizer st = new StringTokenizer(command);
            st.nextToken();

            String cmd = st.nextToken();
            NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
            switch (cmd) {
                case "register": {

                    if (TvT.is_joining()) {
                        JoinTvT(player);
                    } else if (CTF.is_joining()) {
                        JoinCTF(player);
                    } else if (DM.is_joining()) {
                        JoinDM(player);
                    } else if (BattleRoyale.is_joining()) {
                        JoinBR(player);
                    } else {
                        player.sendMessage("There is no event in progress.");
                    }

                    break;
                }
                case "cancel": {

                    if (TvT.is_joining() && player.isRegisteredInTVTEvent()) {
                        LeaveTvT(player);
                    } else if (CTF.is_joining() && player.isRegisteredInCTFEvent()) {
                        LeaveCTF(player);
                    } else if (DM.is_joining() && player.isRegisteredInDMEvent()) {
                        LeaveDM(player);
                    } else if (BattleRoyale.is_joining() && player.isRegisteredInBREvent()) {
                        LeaveBR(player);
                    } else {
                        player.sendMessage("There is no event in progress.");
                    }

                    break;

                }
            }
        }
    }

    public void showChatWindow(L2PcInstance player, int val) {

        if (TvT.is_joining()) {
            event = 1;
            eventName = "TvT";
            playersRegistered = TvT._playersShuffle.size();
            eventfName = "Team vs Team";
        } else if (CTF.is_joining()) {
            event = 2;
            eventName = "CTF";
            playersRegistered = CTF._playersShuffle.size();
            eventfName = "Capture the Flag";
        } else if (DM.is_joining()) {
            event = 3;
            eventName = "DM";
            playersRegistered = DM._players.size();
            eventfName = "Deathmatch";
        } else if (BattleRoyale.is_joining()) {
            event = 4;
            eventName = "BR";
            playersRegistered = BattleRoyale._players.size();
            eventfName = "Battle Royale";
        }

        NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
        msg.setHtml(registerWindow(player));
        msg.replace("%objectId%", String.valueOf(getObjectId()));
        player.sendPacket(msg);
    }


    private String registerWindow(L2PcInstance player) {
        TextBuilder tb = new TextBuilder();
        tb.append("<html><title>"+eventfName+"</title><body>");
        tb.append("<table width=300 border=0>" +
                "<tr>" +
                "<td width=270 height=100>" +
                "<img src=l2wonderlust.logo width=275 height=100 align=center>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "<img src=\"L2UI.SquareGray\" width=300 height=1>" +
                "<table bgcolor=000000 width=319>" +
                "<tr>" +
                "<td><center><font color=\"0080FF\">Hello, do you wish to participate in the event?</font></center></td>" +
                "</tr>" +
                "<tr>" +
                "<td><center>Current event : <font color=\"0080FF\">"+eventfName+"</font></center></td>" +
                "</tr>" +
                "</table>" +
                "<img src=\"L2UI.SquareGray\" width=300 height=1>" +
                "<br><br>");
        if (event != 0) {
            if (player.isRegisteredInFunEvent()) {
                tb.append("<br><center><button value=\"Cancel Participation\" action=\"bypass -h npc_%objectId%_event cancel\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"></center>");
            } else {
                tb.append("<br><center><button value=\"Participate\" action=\"bypass -h npc_%objectId%_event register\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"></center>");
            }
            tb.append("<br><center>Players Registered : <font color=\"0080FF\">" + playersRegistered + "</font></center><br>");
        } else {
            tb.append("<center><font color=\"0080FF\">There is no event in progress.</font></center>");
        }
        tb.append("<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32>");
        tb.append("</center>");
        tb.append("<table width=300>");
        tb.append("<tr>");
        tb.append("<td><center><font color=\"0088ff\">Website : </font><font color=\"a9a9a2\">L2Wonderlust.com</font></center></td>");
        tb.append("</tr>");
        tb.append("</table>");
        tb.append("</body></html>");
        return tb.toString();
    }


    private boolean JoinTvT(final L2PcInstance activeChar) {
        if (activeChar == null) {
            return false;
        }

        if (!TvT.is_joining()) {
            activeChar.sendMessage("There is no TvT Event in progress.");
            return false;
        } else if (TvT.is_joining() && activeChar._inEventTvT) {
            activeChar.sendMessage("You are already registered.");
            return false;
        } else if (activeChar.isCursedWeaponEquipped()) {
            activeChar.sendMessage("You are not allowed to participate to the event because you are holding a Cursed Weapon.");
            return false;
        } else if (activeChar.isInOlympiadMode()) {
            activeChar.sendMessage("You are not allowed to participate to the event because you are in Olympiad.");
            return false;
        } else if (activeChar.getLevel() < TvT.get_minlvl()) {
            activeChar.sendMessage("You are not allowed to participate to the event because your level is too low.");
            return false;
        } else if (activeChar.getLevel() > TvT.get_maxlvl()) {
            activeChar.sendMessage("You are not allowed to participate to the event because your level is too high.");
            return false;
        } else if (activeChar.getClassId().getId() == 16 || activeChar.getClassId().getId() == 97) {
            activeChar.sendMessage("You cannot enter this event with this class.");
            return false;
        } else if (activeChar.getKarma() > 0) {
            activeChar.sendMessage("You are not allowed to participate to the event because you have Karma.");
            return false;
        } else if (TvT.is_teleport() || TvT.is_started()) {
            activeChar.sendMessage("TvT Event registration period is over. You can't register now.");
            return false;
        } else if (L2ArenaManagerInstance.participants.contains(activeChar) || L2ArenaManagerInstance.inFightOrWaiting.contains(activeChar) || (activeChar.isInParty() && L2ArenaManagerInstance.participantsLobby.contains(activeChar.getParty()))) {
            activeChar.sendMessage("You already participated in street fight arena!");
            return false;
        } else {
            activeChar.sendMessage("Your participation in the TvT event has been approved.");
            TvT.addPlayer(activeChar, "");
            return false;
        }
    }

    private boolean LeaveTvT(final L2PcInstance activeChar) {
        if (activeChar == null) {
            return false;
        }

        if (!TvT.is_joining()) {
            activeChar.sendMessage("There is no TvT Event in progress.");
            return false;
        } else if ((TvT.is_teleport() || TvT.is_started()) && activeChar._inEventTvT) {
            activeChar.sendMessage("You can not leave now because TvT event has started.");
            return false;
        } else if (TvT.is_joining() && !activeChar._inEventTvT) {
            activeChar.sendMessage("You aren't registered in the TvT Event.");
            return false;
        } else {
            TvT.removePlayer(activeChar);
            return true;
        }
    }


    private boolean JoinDM(final L2PcInstance activeChar) {
        if (activeChar == null) {
            return false;
        }

        if (!DM.is_joining()) {
            activeChar.sendMessage("There is no Deathmatch Event in progress.");
            return false;
        } else if (DM.is_joining() && activeChar._inEventDM) {
            activeChar.sendMessage("You are already registered.");
            return false;
        } else if (activeChar.isCursedWeaponEquipped()) {
            activeChar.sendMessage("You are not allowed to participate to the event because you are holding a Cursed Weapon.");
            return false;
        } else if (activeChar.isInOlympiadMode()) {
            activeChar.sendMessage("You are not allowed to participate to the event because you are in Olympiad.");
            return false;
        } else if (activeChar.getLevel() < DM.get_minlvl()) {
            activeChar.sendMessage("You are not allowed to participate to the event because your level is too low.");
            return false;
        } else if (activeChar.getLevel() > DM.get_maxlvl()) {
            activeChar.sendMessage("You are not allowed to participate to the event because your level is too high.");
            return false;
        } else if (activeChar.getClassId().getId() == 16 || activeChar.getClassId().getId() == 97) {
            activeChar.sendMessage("You cannot enter this event with this class.");
            return false;
        } else if (activeChar.getKarma() > 0) {
            activeChar.sendMessage("You are not allowed to participate to the event because you have Karma.");
            return false;
        } else if (DM.is_teleport() || DM.is_started()) {
            activeChar.sendMessage("Deathmatch Event registration period is over. You can't register now.");
            return false;
        } else if (L2ArenaManagerInstance.participants.contains(activeChar) || L2ArenaManagerInstance.inFightOrWaiting.contains(activeChar) || (activeChar.isInParty() && L2ArenaManagerInstance.participantsLobby.contains(activeChar.getParty()))) {
            activeChar.sendMessage("You already participated in street fight arena!");
            return false;
        } else {
            activeChar.sendMessage("Your participation in the Deathmatch event has been approved.");
            DM.addPlayer(activeChar);
            return true;
        }
    }

    private boolean LeaveDM(final L2PcInstance activeChar) {
        if (activeChar == null) {
            return false;
        }

        if (!DM.is_joining()) {
            activeChar.sendMessage("There is no Deathmatch Event in progress.");
            return false;
        } else if ((DM.is_teleport() || DM.is_started()) && activeChar._inEventDM) {
            activeChar.sendMessage("You can not leave now because Deathmatch event has started.");
            return false;
        } else if (DM.is_joining() && !activeChar._inEventDM) {
            activeChar.sendMessage("You aren't registered in the Deathmatch Event.");
            return false;
        } else {
            DM.removePlayer(activeChar);
            return true;
        }
    }


    private boolean JoinCTF(final L2PcInstance activeChar) {
        if (activeChar == null) {
            return false;
        }

        if (!CTF.is_joining()) {
            activeChar.sendMessage("There is no CTF Event in progress.");
            return false;
        } else if (CTF.is_joining() && activeChar._inEventCTF) {
            activeChar.sendMessage("You are already registered.");
            return false;
        } else if (activeChar.isCursedWeaponEquipped()) {
            activeChar.sendMessage("You are not allowed to participate to the event because you are holding a Cursed Weapon.");
            return false;
        } else if (activeChar.isInOlympiadMode()) {
            activeChar.sendMessage("You are not allowed to participate to the event because you are in Olympiad.");
            return false;
        } else if (activeChar.getLevel() < CTF.get_minlvl()) {
            activeChar.sendMessage("You are not allowed to participate to the event because your level is too low.");
            return false;
        } else if (activeChar.getLevel() > CTF.get_maxlvl()) {
            activeChar.sendMessage("You are not allowed to participate to the event because your level is too high.");
            return false;
        } else if (activeChar.getClassId().getId() == 16 || activeChar.getClassId().getId() == 97) {
            activeChar.sendMessage("You cannot enter this event with this class.");
            return false;
        } else if (activeChar.getKarma() > 0) {
            activeChar.sendMessage("You are not allowed to participate to the event because you have Karma.");
            return false;
        } else if (CTF.is_teleport() || CTF.is_started()) {
            activeChar.sendMessage("CTF Event registration period is over. You can't register now.");
            return false;
        } else if (L2ArenaManagerInstance.participants.contains(activeChar) || L2ArenaManagerInstance.inFightOrWaiting.contains(activeChar) || (activeChar.isInParty() && L2ArenaManagerInstance.participantsLobby.contains(activeChar.getParty()))) {
            activeChar.sendMessage("You already participated in street fight arena!");
            return false;
        } else {
            activeChar.sendMessage("Your participation in the CTF event has been approved.");
            CTF.addPlayer(activeChar, "");
            return true;
        }
    }

    private boolean LeaveCTF(final L2PcInstance activeChar) {
        if (activeChar == null) {
            return false;
        }

        if (!CTF.is_joining()) {
            activeChar.sendMessage("There is no CTF Event in progress.");
            return false;
        } else if ((CTF.is_teleport() || CTF.is_started()) && activeChar._inEventCTF) {
            activeChar.sendMessage("You can not leave now because CTF event has started.");
            return false;
        } else if (CTF.is_joining() && !activeChar._inEventCTF) {
            activeChar.sendMessage("You aren't registered in the CTF Event.");
            return false;
        } else {
            CTF.removePlayer(activeChar);
            return true;
        }
    }


    private boolean JoinBR(final L2PcInstance activeChar) {
        if (activeChar == null) {
            return false;
        }

        if (!BattleRoyale.is_joining()) {
            activeChar.sendMessage("There is no Battle Royale Event in progress.");
            return false;
        } else if (BattleRoyale.is_joining() && activeChar._inEventBR) {
            activeChar.sendMessage("You are already registered.");
            return false;
        } else if (activeChar.isCursedWeaponEquipped()) {
            activeChar.sendMessage("You are not allowed to participate to the event because you are holding a Cursed Weapon.");
            return false;
        } else if (activeChar.isInOlympiadMode()) {
            activeChar.sendMessage("You are not allowed to participate to the event because you are in Olympiad.");
            return false;
        } else if (activeChar.getClassId().getId() == 16 || activeChar.getClassId().getId() == 97) {
            activeChar.sendMessage("You cannot enter this event with this class.");
            return false;
        } else if (activeChar.getKarma() > 0) {
            activeChar.sendMessage("You are not allowed to participate to the event because you have Karma.");
            return false;
        } else if (BattleRoyale.is_teleport() || BattleRoyale.is_started()) {
            activeChar.sendMessage("Battle Royale Event registration period is over. You can't register now.");
            return false;
        } else if (L2ArenaManagerInstance.participants.contains(activeChar) || (activeChar.isInParty() && L2ArenaManagerInstance.participantsLobby.contains(activeChar.getParty()))) {
            activeChar.sendMessage("You already participated in street fight arena!");
            return false;
        } else {
            activeChar.sendMessage("Your participation in the Battle Royale event has been approved.");
            BattleRoyale.addPlayer(activeChar);
            return true;
        }
    }

    private boolean LeaveBR(final L2PcInstance activeChar) {
        if (activeChar == null) {
            return false;
        }

        if (!BattleRoyale.is_joining()) {
            activeChar.sendMessage("There is no Battle Royale Event in progress.");
            return false;
        } else if ((BattleRoyale.is_teleport() || BattleRoyale.is_started()) && activeChar._inEventBR) {
            activeChar.sendMessage("You can not leave now because Battle Royale event has started.");
            return false;
        } else if (BattleRoyale.is_joining() && !activeChar._inEventBR) {
            activeChar.sendMessage("You aren't registered in the Battle Royale Event.");
            return false;
        } else {
            BattleRoyale.removePlayer(activeChar);
            return true;
        }
    }

}
