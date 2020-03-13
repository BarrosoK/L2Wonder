package com.l2jfrozen.gameserver.model.actor.instance;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.model.L2Party;
import com.l2jfrozen.gameserver.model.L2World;
import com.l2jfrozen.gameserver.model.base.ClassId;
import com.l2jfrozen.gameserver.model.entity.Announcements;
import com.l2jfrozen.gameserver.model.entity.olympiad.Olympiad;
import com.l2jfrozen.gameserver.model.scripts.ArenaFight;
import com.l2jfrozen.gameserver.model.scripts.ArenaFightPtVsPt;
import com.l2jfrozen.gameserver.model.scripts.ArenaFightRankings;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;
import com.l2jfrozen.util.database.L2DatabaseFactory;
import javolution.text.TextBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Server1 on 8/3/2017.
 */
public class L2ArenaManagerInstance extends L2NpcInstance {


    public static Vector<ArenaFight> fights = new Vector<ArenaFight>();
    public static Vector<ArenaFightPtVsPt> fightPtVsPt = new Vector<ArenaFightPtVsPt>();
    public static Vector<L2PcInstance> participants = new Vector<L2PcInstance>();
    public static Vector<L2Party> participantsLobby = new Vector<>();
    public static Vector<L2PcInstance> inFightOrWaiting = new Vector<L2PcInstance>();
    public static HashMap<Integer, Boolean> freeArenas = new HashMap<Integer, Boolean>();
    public static HashMap<Integer, Boolean> betsActivated = new HashMap<Integer, Boolean>();
    public static HashMap<Integer, int[][]> arenasLoc = new HashMap<Integer, int[][]>();
    public static HashMap<Integer, int[]> observersLoc = new HashMap<Integer, int[]>();
    public static HashMap<int[], Long> fighterIntervals = new HashMap<int[], Long>();

    private static int ptVsPtArenaId = 10;
    private static int idFight = 0;

    public L2ArenaManagerInstance(int objectId, L2NpcTemplate template) {
        super(objectId, template);
        freeArenas.put(1, true);
        freeArenas.put(2, true);
        freeArenas.put(3, true);
        freeArenas.put(10, true);

        betsActivated.put(1, false);
        betsActivated.put(2, false);
        betsActivated.put(3, false);

        arenasLoc.put(1, new int[][]{{-87277, -252850, -3344}, {-88886, -252843, -3356}});
        arenasLoc.put(2, new int[][]{{-87461, -239348, -8448}, {-88737, -239356, -8448}});
        arenasLoc.put(3, new int[][]{{-74748, -239184, -8208}, {-76158, -239183, -8208}});
        arenasLoc.put(10, new int[][]{{81811, -15637, -1864}, {84683, -17414, -1856}});

        observersLoc.put(1, new int[]{-88069, -252877, -3356});
        observersLoc.put(2, new int[]{-88102, -239222, -8478});
        observersLoc.put(3, new int[]{-75469, -238820, -8232});
        observersLoc.put(10, new int[]{83063, -16337, -1888});
    }

    public void onBypassFeedback(L2PcInstance player, String command) {
        int zone = 0;
        if (command.startsWith("arena")) {
            StringTokenizer st = new StringTokenizer(command);
            st.nextToken();

            String cmd = st.nextToken();
            switch (cmd) {
                case "register": {
                    register(player);
                }
                break;
                case "cancelReg": {
                    if (L2ArenaManagerInstance.participantsLobby.contains(player.getParty())) {
                        participantsLobby.remove(player.getParty());
                        player.sendMessage("Removed from the list !");
                        showChatWindow(player, 0);
                    } else if (participants.contains(player)) {
                        participants.remove(player);
                        player.sendMessage("Removed from the list !");
                        showChatWindow(player, 0);
                    }
                }
                break;
                case "1vs1fight": {
                    NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
                    msg.setHtml(arena1v1Window(player));
                    msg.replace("%objectId%", String.valueOf(getObjectId()));
                    player.sendPacket(msg);
                }
                break;
                case "ptvsptfight": {
                    NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
                    msg.setHtml(arenaPtvsPtWindow(player));
                    msg.replace("%objectId%", String.valueOf(getObjectId()));
                    player.sendPacket(msg);
                }
                break;
                case "info": {
                    int id = Integer.parseInt(st.nextToken());

                    NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
                    msg.setHtml(fightInfos(id));
                    msg.replace("%objectId%", String.valueOf(getObjectId()));
                    player.sendPacket(msg);
                }
                break;
                case "main": {
                    showChatWindow(player, 0);
                }
                break;
                case "bet": {
                    int id = Integer.parseInt(st.nextToken());

                    ArenaFight arena = null;

                    for (ArenaFight a : fights) {
                        if (a.getIdFight() == id) {
                            arena = a;
                            break;
                        }
                    }


                    if (arena == null) {
                        player.sendMessage("This fight is over.");
                        return;
                    }


                    if (betsActivated.get(arena.getIdArena())) {
                        int amount = Integer.parseInt(st.nextToken());

                        if (amount > 200000) {
                            player.sendMessage("Maximum bet is 200k");
                            return;
                        }

                        String name = st.nextToken();

                        NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
                        msg.setHtml(betWindow(id, amount, name, player));
                        msg.replace("%objectId%", String.valueOf(getObjectId()));
                        player.sendPacket(msg);
                        bet(player, amount, id, name);

                    } else {
                        player.sendMessage("Bets are closed, this fight is currently in progress.");
                        NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
                        msg.setHtml(fightInfos(id));
                        msg.replace("%objectId%", String.valueOf(getObjectId()));
                        player.sendPacket(msg);
                    }

                }
                break;
                case "profile": {
                    NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
                    msg.setHtml(myInfosWindow(player));
                    msg.replace("%objectId%", String.valueOf(getObjectId()));
                    player.sendPacket(msg);
                }
                break;
                case "rankings": {
                    NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
                    msg.setHtml(rankingsWindow(player));
                    msg.replace("%objectId%", String.valueOf(getObjectId()));
                    player.sendPacket(msg);
                }
                break;
                case "collectRewards": {
                    collectRewards(player);
                }
                break;
                case "rankInfo": {
                    NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
                    msg.setHtml(rankInfo());
                    msg.replace("%objectId%", String.valueOf(getObjectId()));
                    player.sendPacket(msg);
                }
                break;
                case "setWinners": {
                    setWinners();
                }
                break;
                case "observe": {
                    int id = Integer.parseInt(st.nextToken());
                    if (participants.contains(player) || (player.getParty() != null && participantsLobby.contains(player.getParty())) || inFightOrWaiting.contains(player)) {
                        player.sendMessage("You cannot observe a match if you are participated");
                        return;
                    }

                    if (player == null) {
                        return;
                    } else if (player.isInOlympiadMode()) {
                        player.sendMessage("You cannot observe a match if you are participated in olympiad");
                        return;
                    } else if (Olympiad.getInstance().isRegistered(player)) {
                        player.sendMessage("You cannot observe a match if you are participated in olympiad");
                        return;
                    } else if (player.isRegisteredInFunEvent()) {
                        player.sendMessage("You cannot observe a match if you are participated in an event");
                        return;
                    }

                    ArenaFight arena = null;

                    for (ArenaFight a : fights) {
                        if (a.getIdFight() == id) {
                            arena = a;
                            break;
                        }
                    }


                    if (arena == null) {
                        player.sendMessage("This fight is over.");
                        return;
                    }

                    if (player == null) {
                        return;
                    }

                    arena.newObserver(player);
                }
                break;
                case "lobby": {
                    cmd = st.nextToken();
                    switch (cmd) {
                        case "list": {
                            NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
                            msg.setHtml(arenaPtvsPtWindow(player));
                            msg.replace("%objectId%", String.valueOf(getObjectId()));
                            player.sendPacket(msg);
                        }
                        break;
                        case "join": {

                            if (player == null) {
                                return;
                            }
                            if (player.getParty() == null) {
                                player.sendMessage("You don't have a party.");
                                return;
                            }

                            if (player.getParty().getLeader() != player) {
                                player.sendMessage("You are not the leader of your party.");
                                return;
                            }

                            if (participantsLobby.contains(player.getParty())) {
                                player.sendMessage("You have already registered.");
                                return;
                            }

                            for (L2PcInstance p : player.getParty().getPartyMembers()) {

                                if (inFightOrWaiting.contains(p)) {
                                    player.sendMessage("You will start a duel soon.");
                                    return;
                                } else if (participants.contains(p)) {
                                    player.sendMessage("You cannot register because a party member (" + p.getName() + ") is registered in the 1v1 queue.");
                                    return;
                                } else if (p.isInOlympiadMode()) {
                                    player.sendMessage("You cannot register because a party member (" + p.getName() + ") is registered in olympiad.");
                                    return;
                                } else if (Olympiad.getInstance().isRegistered(p)) {
                                    player.sendMessage("You cannot register because a party member (" + p.getName() + ") is registered in olympiad.");
                                    return;
                                } else if (p.isRegisteredInFunEvent()) {
                                    player.sendMessage("You cannot register because a party member (" + p.getName() + ") is in an event");
                                    return;
                                }

                                if (p._active_boxes > 1) {
                                    final List<String> players_in_boxes = p.active_boxes_characters;

                                    if (players_in_boxes != null && players_in_boxes.size() > 1) {
                                        for (final String character_name : players_in_boxes) {
                                            final L2PcInstance p2 = L2World.getInstance().getPlayer(character_name);
                                            if (p2 != null && p2 != p && player.getParty().getPartyMembers().contains(p2)) {
                                                player.sendMessage("You cannot register because a party member (" + p.getName() + ") is dualboxing!");
                                                return;
                                            } else if (p2 != null && p2 != p && (participants.contains(p2) || participantsLobby.contains(p2.getParty()))) {
                                                player.sendMessage("You cannot register because a party member (" + p.getName() + ") is already participated in street fight with another char!");
                                                return;
                                            }
                                        }
                                    }
                                }

                            }
                            participantsLobby.add(player.getParty());
                            for (L2PcInstance p : player.getParty().getPartyMembers()) {
                                p.sendMessage("Your party has been registered for a " + player.getParty().getPartyMembers().size() + "vs" + player.getParty().getPartyMembers().size());
                            }

                            NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
                            msg.setHtml(arenaPtvsPtWindow(player));
                            msg.replace("%objectId%", String.valueOf(getObjectId()));
                            player.sendPacket(msg);
                        }
                        break;
                        case "leave": {
                            if (player.getParty().getLeader() != player) {
                                player.sendMessage("You are not the party leader.");
                            } else {
                                if (participantsLobby.contains(player.getParty())) {
                                    participantsLobby.remove(player.getParty());
                                    player.sendMessage("Removed from the list !");
                                }
                            }

                            NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
                            msg.setHtml(arenaPtvsPtWindow(player));
                            msg.replace("%objectId%", String.valueOf(getObjectId()));
                            player.sendPacket(msg);
                        }
                        break;
                        case "fight": {

                            if (player == null) {
                                return;
                            }
                            if (player.getParty() == null) {
                                player.sendMessage("You don't have a party.");
                                return;
                            }

                            if (player.getParty().getLeader() != player) {
                                player.sendMessage("You are not the leader of your party.");
                                return;
                            }

                            for (L2PcInstance p : player.getParty().getPartyMembers()) {

                                if (inFightOrWaiting.contains(p)) {
                                    player.sendMessage("You will start a duel soon.");
                                    return;
                                } else if (participants.contains(p)) {

                                    player.sendMessage("You cannot register because a party member (" + p.getName() + ") is registered in the 1v1 queue.");
                                    return;
                                } else if (p.isInOlympiadMode()) {
                                    player.sendMessage("You cannot register because a party member (" + p.getName() + ") is registered in olympiad.");
                                    return;
                                } else if (Olympiad.getInstance().isRegistered(p)) {
                                    player.sendMessage("You cannot register because a party member (" + p.getName() + ") is registered in olympiad.");
                                    return;
                                } else if (p.isRegisteredInFunEvent()) {
                                    player.sendMessage("You cannot register because a party member (" + p.getName() + ") is registered in an event");
                                    return;
                                }

                                if (p._active_boxes > 1) {
                                    final List<String> players_in_boxes = p.active_boxes_characters;
                                    if (players_in_boxes != null && players_in_boxes.size() > 1) {
                                        for (final String character_name : players_in_boxes) {
                                            final L2PcInstance p2 = L2World.getInstance().getPlayer(character_name);
                                            if (p2 != null && p2 != p && player.getParty().getPartyMembers().contains(p2)) {
                                                player.sendMessage("You cannot register because a party member (" + p.getName() + ") is dualboxing!");
                                                return;
                                            } else if (p2 != null && p2 != p && (participants.contains(p2) || participantsLobby.contains(p2.getParty()))) {
                                                player.sendMessage("You cannot register because a party member (" + p.getName() + ") is already participated in street fight with another char!");
                                                return;
                                            }
                                        }
                                    }
                                }

                            }
                            String playerName = st.nextToken();
                            L2PcInstance chosen = L2World.getInstance().getPlayer(playerName);

                            if (chosen.getParty() == null) {
                                player.sendMessage("This fight doesn't exist anymore.");
                                return;
                            } else if (!participantsLobby.contains(chosen.getParty())) {
                                player.sendMessage("This fight doesn't exist anymore.");
                                return;
                            } else if (player.getParty().getPartyMembers().size() != chosen.getParty().getPartyMembers().size()) {
                                player.sendMessage("The enemy party has " + chosen.getParty().getPartyMembers().size() + " party members.");
                                return;
                            }

                            for (L2PcInstance p : chosen.getParty().getPartyMembers()) {

                                if (p.isInOlympiadMode()) {
                                    player.sendMessage("A member of the enemy party is registered in Olympiad.");
                                    return;
                                } else if (Olympiad.getInstance().isRegistered(p)) {
                                    player.sendMessage("A member of the enemy party is registered in Olympiad.");
                                    return;
                                } else if (p.isRegisteredInFunEvent()) {
                                    player.sendMessage("A member of the enemy party is registered in an event.");
                                    return;
                                }
                            }


                            if (inFightOrWaiting.contains(player)) {
                                player.sendMessage("You will start a duel soon.");
                                return;
                            }


                            if (!freeArenas.get(ptVsPtArenaId)) {
                                player.sendMessage("The arena is currently occupied.");
                                return;
                            }


                            if (participantsLobby.contains(chosen.getParty())) {
                                participantsLobby.remove(chosen.getParty());
                            }
                            if (participants.contains(player)) {
                                participants.remove(player);
                            }
                            if (participantsLobby.contains(player.getParty())) {
                                participantsLobby.remove(player.getParty());
                            }

                            createArenaPtVsPt(player.getParty(), chosen.getParty(), ptVsPtArenaId);
                        }
                        break;
                        case "rankings": {
                            NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
                            msg.setHtml(ptRankingsWindow(player));
                            msg.replace("%objectId%", String.valueOf(getObjectId()));
                            player.sendPacket(msg);
                        }
                        break;
                        case "profile": {
                            NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
                            msg.setHtml(myPtInfosWindow(player));
                            msg.replace("%objectId%", String.valueOf(getObjectId()));
                            player.sendPacket(msg);
                        }
                        break;
                        case "observe": {
                            if (participants.contains(player) || (player.getParty() != null && participantsLobby.contains(player.getParty())) || inFightOrWaiting.contains(player)) {
                                player.sendMessage("You cannot observe a match if you are participated");
                                return;
                            }
                            player.sendMessage(".");

                            if (player == null) {
                                return;
                            } else if (player.isInOlympiadMode()) {
                                player.sendMessage("You cannot observe a match if you are participated in olympiad");
                                return;
                            } else if (Olympiad.getInstance().isRegistered(player)) {
                                player.sendMessage("You cannot observe a match if you are participated in olympiad");
                                return;
                            } else if (player.isRegisteredInFunEvent()) {
                                player.sendMessage("You cannot observe a match if you are participated in an event");
                                return;
                            }

                            ArenaFightPtVsPt arena = null;

                            if (!fightPtVsPt.isEmpty()) {
                                arena = fightPtVsPt.firstElement();
                                break;
                            }

                            if (arena == null) {
                                player.sendMessage("This fight is over.");
                                return;
                            }

                            if (player == null) {
                                return;
                            }

                            arena.newObserver(player);
                        }
                        break;
                    }
                }
                break;
            }
        }
    }

    public static void createArenaPtVsPt(L2Party partyOne, L2Party partyTwo, int idArena) {
        ArenaFightPtVsPt arena = new ArenaFightPtVsPt(partyOne, partyTwo, idArena);

        inFightOrWaiting.addAll(partyOne.getPartyMembers());
        inFightOrWaiting.addAll(partyTwo.getPartyMembers());

        if (participantsLobby.contains(partyOne))
            participantsLobby.remove(partyOne);
        if (participantsLobby.contains(partyTwo))
            participantsLobby.remove(partyTwo);
        arena.beginArena();
        fightPtVsPt.add(arena);
    }


    public static void createArena(L2PcInstance playerOne, L2PcInstance playerTwo, int idArena) {
        ArenaFight arena = new ArenaFight(playerOne, playerTwo, idArena, idFight);
        inFightOrWaiting.add(playerOne);
        inFightOrWaiting.add(playerTwo);

        if (participantsLobby.contains(playerOne.getParty()))
            participantsLobby.remove(playerOne.getParty());
        if (participantsLobby.contains(playerTwo.getParty()))
            participantsLobby.remove(playerTwo.getParty());
        arena.beginArena();

        fights.add(arena);
        idFight++;

        // add here code to remove fighterIntervals
    }

    public void register(L2PcInstance player) {

        if (player == null) {
            return;
        } else if (player.isInOlympiadMode()) {
            player.sendMessage("You cannot register because you are already registered in olympiad.");
            return;
        } else if (Olympiad.getInstance().isRegistered(player)) {
            player.sendMessage("You cannot register because you are already registered in olympiad.");
            return;
        } else if (inFightOrWaiting.contains(player)) {
            player.sendMessage("You will start a duel soon.");
            return;
        } else if (participants.contains(player)) {

            player.sendMessage("You're already in the queue.");
            return;
        } else if (player.isInParty() && participantsLobby.contains(player.getParty())) {
            player.sendMessage("Your party is registered for a party vs party fight.");
            return;
        } else if (player.isRegisteredInFunEvent()) {
            player.sendMessage("You cannot register because you are registered in an event");
            return;
        } else if ((player.getClassId() == ClassId.cardinal || player.getClassId() == ClassId.evaSaint || player.getClassId() == ClassId.shillienSaint)) {
            player.sendMessage("You cannot join with a Healer Class!");
            return;
        }

        if (player._active_boxes > 1 && !Config.ALLOW_DUALBOX_EVENT) {
            final List<String> players_in_boxes = player.active_boxes_characters;

            if (players_in_boxes != null && players_in_boxes.size() > 1) {
                for (final String character_name : players_in_boxes) {
                    final L2PcInstance p = L2World.getInstance().getPlayer(character_name);

                    if (p != null && player != p && (participants.contains(p) || participantsLobby.contains(p.getParty()))) {
                        player.sendMessage("You already participated in street fight with another char!");
                        return;
                    }
                }
            }
        }

        participants.add(player);

        if (participants.size() == 1) {
            player.sendMessage("You have been added to the queue.");
            return;
        } else if (participants.size() == 0) {
            player.sendMessage("Something went wrong.");
        } else {
            tryToCreateNewArena();
        }

    }

    public static void tryToCreateNewArena() {

        if (participants.size() < 2) {
            return;
        }

        Vector<Integer> rndArena = new Vector<>();
        for (int i = 1; i <= freeArenas.size() - 1; i++) {

            if (freeArenas.get(i)) {
                rndArena.add(i);
            }
        }

        if (rndArena.isEmpty()) {
            return;
        }


        L2PcInstance player1 = participants.elementAt(0);
        L2PcInstance player2 = participants.elementAt(1);

        if (player1 == null) {
            participants.remove(player1);
        } else if (player1.isOnline() == 0) {
            participants.remove(player1);
        } else if (player1.isInOlympiadMode()) {
            participants.remove(player1);
        } else if (Olympiad.getInstance().isRegistered(player1)) {
            participants.remove(player1);
        } else if (player1.isInDiceEvent()) {
            participants.remove(player1);
        }


        if (player2 == null) {
            participants.remove(player2);
        } else if (player2.isOnline() == 0) {
            participants.remove(player2);
        } else if (player2.isInOlympiadMode()) {
            participants.remove(player2);
        } else if (Olympiad.getInstance().isRegistered(player2)) {
            participants.remove(player2);
        } else if (player2.isInDiceEvent()) {
            participants.remove(player2);
        }


        if (participants.size() >= 2) {

            Random rand = new Random();
            int r = rand.nextInt(rndArena.size());
            createArena(player1, player2, rndArena.elementAt(r));
            participants.remove(player1);
            participants.remove(player2);
        }


        Iterator<Map.Entry<int[], Long>> iterator = L2ArenaManagerInstance.fighterIntervals.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<int[], Long> entry = iterator.next();
            if (entry.getValue() + 600000 < System.currentTimeMillis()) {
                iterator.remove();
            }
        }

        /*for (HashMap.Entry<int[], Long> entry : L2ArenaManagerInstance.fighterIntervals.entrySet()) {
            if (entry.getValue() + 600000 < System.currentTimeMillis()) {
                L2ArenaManagerInstance.fighterIntervals.remove(entry.getKey());
            }
        }*/

    }


    public void showChatWindow(L2PcInstance player, int val) {
        NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
        msg.setHtml(mainWindow(player));
        msg.replace("%objectId%", String.valueOf(getObjectId()));
        player.sendPacket(msg);
    }

    public void bet(L2PcInstance player, int amount, int fightId, String playerNameBet) {

        ArenaFight arena = null;

        for (ArenaFight a : fights) {
            if (a.getIdFight() == fightId)
                arena = a;
        }

        if (arena == null) {
            return;
        }

        arena.addBet(player, amount, L2World.getInstance().getPlayer(playerNameBet).getObjectId());

    }


    private String fightInfos(int id) {
        TextBuilder tb = new TextBuilder();
        ArenaFight arena = null;

        for (ArenaFight a : fights) {
            if (a.getIdFight() == id)
                arena = a;
        }

        if (arena == null) {
            return "";
        }

        L2PcInstance playerOne = arena.getFighters().firstElement();
        L2PcInstance playerTwo = arena.getFighters().lastElement();

        tb.append("<html><title>Duel id : " + id + "</title><body>");
        tb.append("<center><table width=300 border=0>" +
                "<tr>" +
                "<td width=270 height=100><img src=l2wonderlust.logo width=275 height=100 align=center></td>" +
                "</tr></table><br>");
        tb.append("<center>" + playerOne.getName() + "      VS      " + playerTwo.getName() + "<br><br></center>");
        tb.append("<center>Odds<br></center>");
        if (arena.getP1TotalBetAmount() == 0 && arena.getP2TotalBetAmount() == 0) {
            tb.append("<center>No Bets            No Bets<br></center>");
        } else if (arena.getP1TotalBetAmount() == 0 && arena.getP2TotalBetAmount() > 0) {
            tb.append("<center>No Bets            1/1" + "<br></center>");
        } else if (arena.getP1TotalBetAmount() > 0 && arena.getP2TotalBetAmount() == 0) {
            tb.append("<center>" + "1/1" + "            No Bets" + "<br></center>");
        } else {
            tb.append("<center>" + new DecimalFormat("#.##").format(arena.getP1Odds()) + "/1" + "           " + new DecimalFormat("#.###").format(arena.getP2Odds()) + "/1" + "<br></center>");
        }
        tb.append("<center>____________________<br></center>");
        tb.append("<center>Total Bet Adenas<br></center>");
        tb.append("<center>" + (int) arena.getP1TotalBetAmount() + "             " + (int) arena.getP2TotalBetAmount() + "<br></center>");
        if (betsActivated.get(arena.getIdArena())) {
            tb.append("<combobox var=\"name\" list=\" " + playerOne.getName() + ";" + playerTwo.getName() + "\" width=\"170\" height=\"15\"/><br>");
            tb.append("<edit var=\"qbox\" width=120 height=20 back=\"sek.cbui75\" fore=\"sek.cbui75\"><br>");
            tb.append("<button value=\"Bet !\" action=\"bypass -h npc_%objectId%_arena bet " + id + " $qbox $name\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"><br>");
        } else {
            tb.append("This Fight is in progress.<br>");
        }
        tb.append("<button value=\"Observe Fight\" action=\"bypass -h npc_%objectId%_arena observe " + id + "\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"><br>");
        tb.append("<br><img src=\"L2UI.SquareGray\" width=300 height=1><br>");
        tb.append("<button value=\"Back to list\" action=\"bypass -h npc_%objectId%_arena 1vs1fight\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"><br>");
        tb.append("</body></html>");

        return tb.toString();
    }

    private String betWindow(int id, int amount, String name, L2PcInstance player) {
        TextBuilder tb = new TextBuilder();
        ArenaFight arena = null;
        for (ArenaFight a : fights) {
            if (a.getIdFight() == id)
                arena = a;
        }

        if (arena == null) {
            return "";
        }

        L2PcInstance playerOne = arena.getFighters().firstElement();
        L2PcInstance playerTwo = arena.getFighters().lastElement();

        tb.append("<html><title>Duel id : " + id + "</title><body>");
        tb.append("<center><table width=300 border=0>" +
                "<tr>" +
                "<td width=270 height=100><img src=l2wonderlust.logo width=275 height=100 align=center></td>" +
                "</tr></table><br>");
        if (!arena.bets.containsKey(player.getObjectId())) {

            final L2ItemInstance adena = player.getInventory().getItemByItemId(57);

            if (amount < 0) {
                tb.append("<center>Incorrect value." + " <br>");
            } else if (adena == null || adena.getCount() < amount) {
                tb.append("<center>You don't have enough adena." + " <br>");
            } else {
                tb.append("<center> You placed a bet of " + amount + " adena on " + name + "." + " <br>");
            }
        } else {
            tb.append("<center> You have already placed a bet on this fight." + "<br>");
        }
        tb.append("<br><img src=\"L2UI.SquareGray\" width=300 height=1><br>");
        tb.append("<button value=\"Back to list\" action=\"bypass -h npc_%objectId%_arena 1vs1fight\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"><br>");

        tb.append("</body></html>");

        return tb.toString();
    }

    private String myInfosWindow(L2PcInstance player) {
        TextBuilder tb =
                new TextBuilder();

        tb.append("<html><title>" + player.getName() + "</title><body>");


        tb.append("<center><table width=300 border=0>" +
                "<tr>" +
                "<td width=270 height=100><img src=l2wonderlust.logo width=275 height=100 align=center></td>" +
                "</tr></table><br>");
        tb.append("<center>My Profile<br>");
        tb.append("<center><br><br>Wins : " + player.getArenaInfos("arena_wins") + " <br>" +
                "Losses : " + player.getArenaInfos("arena_loses") + "<br>" +
                "Points : " + player.getArenaInfos("arena_points") + "<br>");
        tb.append("<br><img src=\"L2UI.SquareGray\" width=300 height=1><br>");
        tb.append("<center><button value=\"Back to list\" action=\"bypass -h npc_%objectId%_arena 1vs1fight\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"></center><br>");
        tb.append("</body></html>");

        return tb.toString();
    }

    private String myPtInfosWindow(L2PcInstance player) {
        TextBuilder tb =
                new TextBuilder();

        tb.append("<html><title>" + player.getName() + "</title><body>");


        tb.append("<center><table width=300 border=0>" +
                "<tr>" +
                "<td width=270 height=100><img src=l2wonderlust.logo width=275 height=100 align=center></td>" +
                "</tr></table><br>");
        tb.append("<center>My Profile As Party Leader<br>");
        tb.append("<center><br><br>Party Wins : " + player.getArenaInfos("arenapt_wins") + " <br>" +
                "Party Losses : " + player.getArenaInfos("arenapt_loses") + "<br>" +
                "Party Points : " + player.getArenaInfos("arenapt_points") + "<br>");
        tb.append("<br><img src=\"L2UI.SquareGray\" width=300 height=1><br>");
        tb.append("<center><button value=\"Back to list\" action=\"bypass -h npc_%objectId%_arena ptvsptfight\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"></center><br>");
        tb.append("</body></html>");

        return tb.toString();
    }

    private String rankingsWindow(L2PcInstance player) {


        TextBuilder tb = new TextBuilder();

        tb.append("<html><title>Rankings</title><body>");
        tb.append("<center><table width=300 border=0>" +
                "<tr>" +
                "<td width=270 height=100><img src=l2wonderlust.logo width=275 height=100 align=center></td>" +
                "</tr></table><br>");
        tb.append("<center><br><br>Top 25 Rankings</center><br>");


        Connection con = null;

        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement state = con.prepareStatement("SELECT char_name,arena_wins,arena_loses,arena_points FROM characters WHERE arena_wins>0 OR arena_loses>0 ORDER BY arena_points DESC LIMIT 25;");
            ResultSet rset = state.executeQuery();

            int rank = 1;
            while (rset.next()) {
                String name = rset.getString("char_name");
                int wins = rset.getInt("arena_wins");
                int loses = rset.getInt("arena_loses");
                int points = rset.getInt("arena_points");
                tb.append("<p align=\"left\"> " + rank + ")  " + name + " Wins:" + wins + "|Losses:" + loses + "|Points:" + points + "</p><br>");
                rank++;
            }
            rset.close();
            state.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }


        tb.append("<br><img src=\"L2UI.SquareGray\" width=300 height=1><br>");
        tb.append("<center><button value=\"Back to list\" action=\"bypass -h npc_%objectId%_arena 1vs1fight\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"></center><br>");
        tb.append("</body></html>");

        return tb.toString();
    }

    private String ptRankingsWindow(L2PcInstance player) {


        TextBuilder tb = new TextBuilder();

        tb.append("<html><title>Party Rankings</title><body>");
        tb.append("<center><table width=300 border=0>" +
                "<tr>" +
                "<td width=270 height=100><img src=l2wonderlust.logo width=275 height=100 align=center></td>" +
                "</tr></table><br>");
        tb.append("<center><br><br>Top 25 Party Rankings</center><br>");


        Connection con = null;

        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement state = con.prepareStatement("SELECT char_name,arenapt_wins,arenapt_loses,arenapt_points FROM characters WHERE arenapt_wins>0 OR arenapt_loses>0 ORDER BY arenapt_points DESC LIMIT 25;");
            ResultSet rset = state.executeQuery();

            int rank = 1;
            while (rset.next()) {
                String name = rset.getString("char_name");
                int wins = rset.getInt("arenapt_wins");
                int loses = rset.getInt("arenapt_loses");
                int points = rset.getInt("arenapt_points");
                tb.append("<p align=\"left\"> " + rank + ")  " + name + "'s Party Wins:" + wins + "|Loses:" + loses + "|Points:" + points + "</p><br>");
                rank++;
            }
            rset.close();
            state.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }


        tb.append("<br><img src=\"L2UI.SquareGray\" width=300 height=1><br>");
        tb.append("<center><button value=\"Back to list\" action=\"bypass -h npc_%objectId%_arena ptvsptfight\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"></center><br>");
        tb.append("</body></html>");

        return tb.toString();
    }

    private void collectRewards(L2PcInstance player) {

        if (player != null && player.isOnline() == 1 && player.isEligibleForRankingsReward) {

            String name = "";
            int type = -1;
            int points = 0;

            Connection con = null;
            PreparedStatement state = null;
            ResultSet rset = null;


            try {
                con = L2DatabaseFactory.getInstance().getConnection();

                state = con.prepareStatement("SELECT character_name,type,points FROM rankings_delivery_pending WHERE character_name=? ;");
                state.setString(1, player.getName());
                rset = state.executeQuery();


                while (rset.next()) {

                    name = rset.getString("character_name");
                    type = rset.getInt("type");
                    points = rset.getInt("points");

                    if (name.equals(player.getName()) && points > 0) {

                        switch (type) {
                            case 0: {
                                player.addItem("Street fight 1v1 Rankings", 4037, 20, player, true);
                                player.sendMessage("You have been rewarded for finishing 1st in Street Fight 1v1 Rankings!");
                                player.broadcastUserInfo();
                            }
                            break;
                            case 1: {
                                player.addItem("Street fight Party Rankings", 4037, 40, player, true);
                                player.sendMessage("You have been rewarded for finishing 1st in Street Fight Party Rankings!");
                                player.broadcastUserInfo();
                            }
                            break;
                        }


                    }

                }

                state = con.prepareStatement("DELETE FROM rankings_delivery_pending WHERE character_name=? ;");
                state.setString(1, player.getName());
                state.executeUpdate();

                rset.close();
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

                if (rset != null) {
                    try {
                        rset.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

            player.isEligibleForRankingsReward = false;
        }

    }

    private String rankInfo() {
        TextBuilder tb = new TextBuilder();
        tb.append("<html>" +
                "<title>Arena Manager</title>" +
                "<body><center><font color=\"0080FF\">Rankings Information</font>" +
                "<br><br>");

        tb.append("Every Sunday at 00:00 gmt+2 the 1st ranked<br>" +
                "players will be rewarded.<br>" +
                "20 donate coins for 1v1 rankings winner<br>" +
                "40 donate coins for party rankings winner<br>" +
                "The party ranks reward is given to the party leader<br><br>" +
                "<font color=\"FFFF33\">1v1 Ranking System</font><br>" +
                "You get 1 point for each win and lose 1 point for each loss<br><br>" +
                "<font color=\"FFFF33\">Party Ranking System</font><br>" +
                "If your fight is 9vs9 and you win, you will get 9 points<br>" +
                "If you lose the fight you will lose 9 points<br>" +
                "If your fight is 3vs3 and you win, you will get 3 points<br>" +
                "If you lose the fight you will lose 3 points etc..<br>" +
                "The winners of the rankings will be announced<br>" +
                "and will be able to collect their<br>reward from the Arena Manager NPC");

        tb.append("</center><br><img src=\"L2UI.SquareGray\" width=300 height=1><br>");
        tb.append("<center><button value=\"Back to list\" action=\"bypass -h npc_%objectId%_arena main\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"></center><br>");
        tb.append("</body></html>");
        return tb.toString();
    }

    private void setWinners() {
        new ArenaFightRankings();
    }

    private String mainWindow(L2PcInstance player) {
        TextBuilder tb = new TextBuilder();
        tb.append("<html><title>Arena Manager</title><body><center>");
        tb.append("</center>");
        tb.append("<center><table width=300 border=0>" +
                "<tr>" +
                "<td width=270 height=100><img src=l2wonderlust.logo width=275 height=100 align=center></td>" +
                "</tr></table><br>");
        tb.append("<button value=\"1 VS 1\" action=\"bypass -h npc_%objectId%_arena 1vs1fight\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"><br>");
        tb.append("<button value=\"Party VS Party\" action=\"bypass -h npc_%objectId%_arena ptvsptfight\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"><br>");
        tb.append("<button value=\"Rankings Information\" action=\"bypass -h npc_%objectId%_arena rankInfo\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"><br>");

        if (player.isEligibleForRankingsReward) {
            tb.append("<button value=\"Collect Ranking Rewards\" action=\"bypass -h npc_%objectId%_arena collectRewards\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"><br>");
        }
        tb.append("<br><img src=\"L2UI.SquareGray\" width=300 height=1><br>");
        tb.append("</body></html>");
        return tb.toString();
    }

    private String arena1v1Window(L2PcInstance player) {
        TextBuilder tb = new TextBuilder();
        tb.append("<html><title>Arena Manager</title><body><center>");
        tb.append("</center>");
        tb.append("<center><table width=300 border=0>" +
                "<tr>" +
                "<td width=270 height=100><img src=l2wonderlust.logo width=275 height=100 align=center></td>" +
                "</tr></table><br>");
        if (!participants.contains(player)) {
            tb.append("<button value=\"Register\" action=\"bypass -h npc_%objectId%_arena register\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"><br>");
        } else {
            tb.append("<button value=\"Cancel Registration\" action=\"bypass -h npc_%objectId%_arena cancelReg\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"><br>");
        }
        tb.append("<button value=\"My Profile\" action=\"bypass -h npc_%objectId%_arena profile\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"><br>");
        tb.append("<button value=\"Rankings\" action=\"bypass -h npc_%objectId%_arena rankings\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"><br>");
        tb.append("Current Fights : " + fights.size() + "<br>");
        if (participants.size() != 0) {
            tb.append("Current player waiting : " + participants.firstElement().getName() + "<br>");
            tb.append("Players in queue : " + (participants.size() - 1) + "<br>");
        }

        for (ArenaFight arena : fights) {
            if (arena.getFighters().firstElement() != null && arena.getFighters().lastElement() != null)
                tb.append("<button value=\"" + arena.getFighters().firstElement().getName() + " VS " + arena.getFighters().lastElement().getName() + "\" action=\"bypass -h npc_%objectId%_arena info " + arena.getIdFight() + "\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"><br>");
        }

        tb.append("<br><img src=\"L2UI.SquareGray\" width=300 height=1><br>");
        tb.append("<center><button value=\"Back to list\" action=\"bypass -h npc_%objectId%_arena main\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"></center><br>");
        tb.append("</body></html>");
        return tb.toString();
    }

    private String arenaPtvsPtWindow(L2PcInstance player) {
        TextBuilder tb = new TextBuilder();

        tb.append("<html><title>Lobby</title><body><center>");
        tb.append("<center><table width=300 border=0>" +
                "<tr>" +
                "<td width=270 height=100><img src=l2wonderlust.logo width=275 height=100 align=center></td>" +
                "</tr></table><br>");

        if (participantsLobby.contains(player.getParty())) {
            tb.append("<button value=\"Cancel Registration\" action=\"bypass -h npc_%objectId%_arena lobby leave\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"><br><br>");
        } else if (participants.contains(player)) {
            tb.append("<button value=\"Cancel 1v1 Registration\" action=\"bypass -h npc_%objectId%_arena cancelReg\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"><br><br>");
        } else if (!inFightOrWaiting.contains(player)) {
            tb.append("<button value=\"Register My Party\" action=\"bypass -h npc_%objectId%_arena lobby join\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"><br><br>");
        }
        tb.append("<button value=\"My Party Profile\" action=\"bypass -h npc_%objectId%_arena lobby profile\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"><br>");
        tb.append("<button value=\"Party Rankings\" action=\"bypass -h npc_%objectId%_arena lobby rankings\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"><br>");
        if (!fightPtVsPt.isEmpty()) {
            tb.append("<center>Current Fight<br>");

            for (ArenaFightPtVsPt a : fightPtVsPt) {
                if (a != null && a.getParty1() != null && a.getParty2() != null)
                    tb.append("<button value=\"" + a.getParty1().getLeader().getName() + "'s Party VS " + a.getParty2().getLeader().getName() + "'s Party" + "\" action=\"bypass -h npc_%objectId%_arena lobby observe\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"><br>");
            }
        }

        List<L2Party> toRemove = new ArrayList<>();
        for (L2Party p : participantsLobby) {
            if (p == null || p.getPartyMembers().size() <= 1) {
                toRemove.add(p);
            }
        }
        participantsLobby.removeAll(toRemove);

        if (participantsLobby.isEmpty()) {
            tb.append("<center>No parties registered.<br>");
        } else {
            for (L2Party p : participantsLobby) {
                if (p != player.getParty()) {
                    tb.append("<center>" + p.getLeader().getName() + "'s Party " + p.getPartyMembers().size() + "vs" + p.getPartyMembers().size() + " <button value=\"Fight\" action=\"bypass -h npc_%objectId%_arena lobby fight " + p.getLeader().getName() + "\" width=132 height=21 back=\"l2wonderlust.activemedium\" fore=\"l2wonderlust.passivemedium\"></center><br>");
                } else {
                    tb.append("<center>" + p.getLeader().getName() + "'s Party" + "<br><font color=\"LEVEL\">(Registered)</font></center><br>");
                }
            }
        }
        tb.append("<br><img src=\"L2UI.SquareGray\" width=300 height=1><br>");

        tb.append("<button value=\"Back to list\" action=\"bypass -h npc_%objectId%_arena main\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\">");
        tb.append("</body></html>");

        return tb.toString();
    }

    public static void refreshArenaAvailability(int idArena, boolean free) {
        freeArenas.put(idArena, free);
    }

    public static void refreshOpenBets(int idArena, boolean open) {
        betsActivated.put(idArena, open);
    }

    public static L2ArenaManagerInstance getInstance() {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final L2ArenaManagerInstance _instance = new L2ArenaManagerInstance(-1, null);
    }

}
