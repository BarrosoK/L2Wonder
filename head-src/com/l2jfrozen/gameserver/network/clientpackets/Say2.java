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
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.l2jfrozen.gameserver.network.clientpackets;

import java.nio.BufferUnderflowException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.l2jfrozen.gameserver.datatables.sql.NpcTable;
import com.l2jfrozen.gameserver.datatables.sql.SpawnTable;
import com.l2jfrozen.gameserver.gui.ConsoleTab;
import com.l2jfrozen.gameserver.managers.GrandBossManager;
import com.l2jfrozen.gameserver.managers.RaidBossSpawnManager;
import com.l2jfrozen.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jfrozen.gameserver.model.entity.Announcements;
import com.l2jfrozen.gameserver.model.entity.Trivia;
import com.l2jfrozen.gameserver.model.scripts.ArenaFight;
import com.l2jfrozen.gameserver.model.spawn.L2Spawn;
import com.l2jfrozen.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.datatables.csv.MapRegionTable;
import com.l2jfrozen.gameserver.handler.IVoicedCommandHandler;
import com.l2jfrozen.gameserver.handler.VoicedCommandHandler;
import com.l2jfrozen.gameserver.managers.PetitionManager;
import com.l2jfrozen.gameserver.model.L2Character;
import com.l2jfrozen.gameserver.model.L2Object;
import com.l2jfrozen.gameserver.model.L2World;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance.PunishLevel;
import com.l2jfrozen.gameserver.network.SystemChatChannelId;
import com.l2jfrozen.gameserver.network.SystemMessageId;
import com.l2jfrozen.gameserver.network.serverpackets.CreatureSay;
import com.l2jfrozen.gameserver.network.serverpackets.SocialAction;
import com.l2jfrozen.gameserver.network.serverpackets.SystemMessage;
import com.l2jfrozen.gameserver.powerpak.PowerPak;
import com.l2jfrozen.gameserver.powerpak.PowerPakConfig;
import com.l2jfrozen.gameserver.util.Util;

public final class Say2 extends L2GameClientPacket {
    private static Logger LOGGER = Logger.getLogger(Say2.class);
    private static java.util.logging.Logger _logChat = java.util.logging.Logger.getLogger("chat");

    public final static int ALL = 0;
    public final static int SHOUT = 1; // !
    public final static int TELL = 2;
    public final static int PARTY = 3; // #
    public final static int CLAN = 4; // @
    public final static int GM = 5; // //gmchat
    public final static int PETITION_PLAYER = 6; // used for petition
    public final static int PETITION_GM = 7; // * used for petition
    public final static int TRADE = 8; // +
    public final static int ALLIANCE = 9; // $
    public final static int ANNOUNCEMENT = 10; // //announce
    public final static int PARTYROOM_ALL = 16; // (Red)
    public final static int PARTYROOM_COMMANDER = 15; // (Yellow)
    public final static int HERO_VOICE = 17; // %
    public final static int CRITICAL_ANNOUNCE = 18;

    private final static String[] CHAT_NAMES =
            {
                    "ALL  ",
                    "SHOUT",
                    "TELL ",
                    "PARTY",
                    "CLAN ",
                    "GM   ",
                    "PETITION_PLAYER",
                    "PETITION_GM",
                    "TRADE",
                    "ALLIANCE",
                    "ANNOUNCEMENT", // 10
                    "WILLCRASHCLIENT:)",
                    "FAKEALL?",
                    "FAKEALL?",
                    "FAKEALL?",
                    "PARTYROOM_ALL",
                    "PARTYROOM_COMMANDER",
                    "CRITICAL_ANNOUNCE",
                    "HERO_VOICE"
            };

    private String _text;
    private int _type;
    private SystemChatChannelId _type2Check;
    private String _target;

    @Override
    protected void readImpl() {
        _text = readS();
        try {
            _type = readD();
            _type2Check = SystemChatChannelId.getChatType(_type);

        } catch (final BufferUnderflowException e) {
            if (Config.ENABLE_ALL_EXCEPTIONS)
                e.printStackTrace();

            _type = CHAT_NAMES.length;
            _type2Check = SystemChatChannelId.CHAT_NONE;
        }
        _target = _type == TELL ? readS() : null;
    }

    @Override
    protected void runImpl() {
        if (Config.DEBUG) {
            LOGGER.info("Say2: Msg Type = '" + _type + "' Text = '" + _text + "'.");
        }

        if (_type < 0 || _type >= CHAT_NAMES.length) {
            LOGGER.warn("Say2: Invalid type: " + _type);
            return;
        }

        final L2PcInstance activeChar = getClient().getActiveChar();

        // Anti-PHX Announce
        if (_type2Check == SystemChatChannelId.CHAT_NONE || _type2Check == SystemChatChannelId.CHAT_ANNOUNCE || _type2Check == SystemChatChannelId.CHAT_CRITICAL_ANNOUNCE || _type2Check == SystemChatChannelId.CHAT_SYSTEM || _type2Check == SystemChatChannelId.CHAT_CUSTOM || (_type2Check == SystemChatChannelId.CHAT_GM_PET && !activeChar.isGM())) {
            LOGGER.warn("[Anti-PHX Announce] Illegal Chat ( " + _type2Check + " ) channel was used by character: [" + activeChar.getName() + "]");
            return;
        }

        if (activeChar == null) {
            LOGGER.warn("[Say2.java] Active Character is null.");
            return;
        }

        if (activeChar.isChatBanned() && !activeChar.isGM() && _type != CLAN && _type != ALLIANCE && _type != PARTY) {
            activeChar.sendMessage("You may not chat while a chat ban is in effect.");
            return;
        }

        if (activeChar.isInJail() && Config.JAIL_DISABLE_CHAT) {
            if (_type == TELL || _type == SHOUT || _type == TRADE || _type == HERO_VOICE) {
                activeChar.sendMessage("You can not chat with players outside of the jail.");
                return;
            }
        }

        if (!getClient().getFloodProtectors().getSayAction().tryPerformAction("Say2")) {
            activeChar.sendMessage("You cannot speak too fast.");
            return;
        }

        if (activeChar.isCursedWeaponEquiped() && (_type == TRADE || _type == SHOUT)) {
            activeChar.sendMessage("Shout and trade chatting cannot be used while possessing a cursed weapon.");
            return;
        }

        if (_type == PETITION_PLAYER && activeChar.isGM()) {
            _type = PETITION_GM;
        }

        if (_text.length() > Config.MAX_CHAT_LENGTH) {
            if (Config.DEBUG) {
                LOGGER.info("Say2: Msg Type = '" + _type + "' Text length more than " + Config.MAX_CHAT_LENGTH + " truncate them.");
            }
            _text = _text.substring(0, Config.MAX_CHAT_LENGTH);
            // return;
        }

        if (Config.LOG_CHAT) {
            final LogRecord record = new LogRecord(Level.INFO, _text);
            record.setLoggerName("chat");

            if (_type == TELL) {
                record.setParameters(new Object[]
                        {
                                CHAT_NAMES[_type],
                                "[" + activeChar.getName() + " to " + _target + "]"
                        });
            } else {
                record.setParameters(new Object[]
                        {
                                CHAT_NAMES[_type],
                                "[" + activeChar.getName() + "]"
                        });
            }

            _logChat.log(record);
        }

        if (Config.L2WALKER_PROTEC && _type == TELL && checkBot(_text)) {
            Util.handleIllegalPlayerAction(activeChar, "Client Emulator Detect: Player " + activeChar.getName() + " using l2walker.", Config.DEFAULT_PUNISH);
            return;
        }
        _text = _text.replaceAll("\\\\n", "");


//custom chat filter
        if (!activeChar.isGM()) {

            // Say Filter implementation
            if (Config.USE_SAY_FILTER) {
                checkText(activeChar);
            }
            checkTextForAds(activeChar);
            if (_text.equals("")) {
                return;
            }
        }

        if (PowerPakConfig.ENABLE_SAY_SOCIAL_ACTIONS && !activeChar.isAlikeDead() && !activeChar.isDead()) {
            if ((_text.equalsIgnoreCase("hello") || _text.equalsIgnoreCase("hey") || _text.equalsIgnoreCase("aloha") || _text.equalsIgnoreCase("alo") || _text.equalsIgnoreCase("ciao") || _text.equalsIgnoreCase("hi")) && (!activeChar.isRunning() || !activeChar.isAttackingNow() || !activeChar.isCastingNow() || !activeChar.isCastingPotionNow()))
                activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(), 2));

            if ((_text.equalsIgnoreCase("lol") || _text.equalsIgnoreCase("haha") || _text.equalsIgnoreCase("xaxa") || _text.equalsIgnoreCase("ghgh") || _text.equalsIgnoreCase("jaja")) && (!activeChar.isRunning() || !activeChar.isAttackingNow() || !activeChar.isCastingNow() || !activeChar.isCastingPotionNow()))
                activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(), 10));

            if ((_text.equalsIgnoreCase("yes") || _text.equalsIgnoreCase("si") || _text.equalsIgnoreCase("yep")) && (!activeChar.isRunning() || !activeChar.isAttackingNow() || !activeChar.isCastingNow() || !activeChar.isCastingPotionNow()))
                activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(), 6));

            if ((_text.equalsIgnoreCase("no") || _text.equalsIgnoreCase("nop") || _text.equalsIgnoreCase("nope")) && (!activeChar.isRunning() || !activeChar.isAttackingNow() || !activeChar.isCastingNow() || !activeChar.isCastingPotionNow()))
                activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(), 5));

        }

        // by Azagthtot
        PowerPak.getInstance().chatHandler(activeChar, _type, _text);
        // CreatureSay cs = new CreatureSay(activeChar.getObjectId(),_type, activeChar.getName(), _text);

        final L2Object saymode = activeChar.getSayMode();
        if (saymode != null) {
            final String name = saymode.getName();
            final int actor = saymode.getObjectId();
            _type = 0;
            final Collection<L2Object> list = saymode.getKnownList().getKnownObjects().values();

            final CreatureSay cs = new CreatureSay(actor, _type, name, _text);
            for (final L2Object obj : list) {
                if (obj == null || !(obj instanceof L2Character)) {
                    continue;
                }
                final L2Character chara = (L2Character) obj;
                chara.sendPacket(cs);
            }
            return;
        }


        final CreatureSay cs = new CreatureSay(activeChar.getObjectId(), _type, activeChar.getName(), _text);
        String nearTown = MapRegionTable.getInstance().getClosestTownName(activeChar);
        switch (_type) {
            case TELL:

                if (_target.equalsIgnoreCase("trivia")) {
                    if (Trivia.isInactive()) {
                        activeChar.sendMessage("Trivia event is not currently running.");
                        return;
                    } else if (!Trivia.isAnswering() || Trivia.isCorrect() || Trivia.isRewarding()) {
                        activeChar.sendMessage("You cannot answer now.");
                        return;
                    } else {
                        Trivia.handleAnswer(_text, activeChar);
                        return;
                    }
                }


                final L2PcInstance receiver = L2World.getInstance().getPlayer(_target);

                if (receiver == null) {

                    SystemMessage sm = new SystemMessage(SystemMessageId.S1_IS_NOT_ONLINE);
                    sm.addString(_target);
                    activeChar.sendPacket(sm);
                    sm = null;
                    return;

                }


                if (!receiver.getBlockList().isInBlockList(activeChar.getName()) || activeChar.isGM()) {
                    if (receiver.isAway()) {
                        activeChar.sendMessage("Player is Away try again later.");
                    }

                    if (Config.JAIL_DISABLE_CHAT && receiver.isInJail()) {
                        activeChar.sendMessage("Player is in jail.");
                        return;
                    }

                    if (receiver.isChatBanned() && !activeChar.isGM()) {
                        activeChar.sendMessage("Player is chat banned.");
                        return;
                    }

                    if (receiver.isInOfflineMode()) {
                        activeChar.sendMessage("Player is in offline mode.");
                        return;
                    }

                    if (!receiver.getMessageRefusal()) {
                        receiver.sendPacket(cs);
                        activeChar.sendPacket(new CreatureSay(activeChar.getObjectId(), _type, "->" + receiver.getName(), _text));
                    } else {
                        activeChar.sendPacket(new SystemMessage(SystemMessageId.THE_PERSON_IS_IN_MESSAGE_REFUSAL_MODE));
                    }
                } else if (receiver.getBlockList().isInBlockList(activeChar.getName())) {
                    SystemMessage sm = new SystemMessage(SystemMessageId.S1_HAS_ADDED_YOU_TO_IGNORE_LIST);
                    sm.addString(_target);
                    activeChar.sendPacket(sm);
                    sm = null;
                }

                break;
            case SHOUT:

                // Flood protect Say
                if (!getClient().getFloodProtectors().getGlobalChat().tryPerformAction("global chat"))
                    return;
                  ConsoleTab.appendMessage(ConsoleTab.ConsoleFilter.ShoutChat, activeChar.getName() + ": " + _text, nearTown);
                if (Config.DEFAULT_GLOBAL_CHAT.equalsIgnoreCase("on") || Config.DEFAULT_GLOBAL_CHAT.equalsIgnoreCase("gm") && activeChar.isGM()) {
                    if (Config.GLOBAL_CHAT_WITH_PVP) {
                        if ((activeChar.getPvpKills() < Config.GLOBAL_PVP_AMOUNT) && !activeChar.isGM()) {
                            activeChar.sendMessage("You must have at least " + Config.GLOBAL_PVP_AMOUNT + " pvp kills in order to speak in global chat");
                            return;
                        }
                        final int region = MapRegionTable.getInstance().getMapRegion(activeChar.getX(), activeChar.getY());
                        for (final L2PcInstance player : L2World.getInstance().getAllPlayers()) {
                            if (region == MapRegionTable.getInstance().getMapRegion(player.getX(), player.getY())) {
                                // Like L2OFF if player is blocked can't read the message
                                if (!player.getBlockList().isInBlockList(activeChar.getName()))
                                    player.sendPacket(cs);
                            }
                        }
                    } else {
                        final int region = MapRegionTable.getInstance().getMapRegion(activeChar.getX(), activeChar.getY());
                        for (final L2PcInstance player : L2World.getInstance().getAllPlayers()) {
                            if (region == MapRegionTable.getInstance().getMapRegion(player.getX(), player.getY())) {
                                // Like L2OFF if player is blocked can't read the message
                                if (!player.getBlockList().isInBlockList(activeChar.getName()))
                                    player.sendPacket(cs);
                            }
                        }
                    }
                } else if (Config.DEFAULT_GLOBAL_CHAT.equalsIgnoreCase("GLOBAL")) {
                    if (Config.GLOBAL_CHAT_WITH_PVP) {
                        if ((activeChar.getPvpKills() < Config.GLOBAL_PVP_AMOUNT) && !activeChar.isGM()) {
                            activeChar.sendMessage("You must have at least " + Config.GLOBAL_PVP_AMOUNT + " pvp kills in order to speak in global chat");
                            return;
                        }
                        for (final L2PcInstance player : L2World.getInstance().getAllPlayers()) {
                            // Like L2OFF if player is blocked can't read the message
                            if (!player.getBlockList().isInBlockList(activeChar.getName()))
                                player.sendPacket(cs);
                        }
                    } else {
                        for (final L2PcInstance player : L2World.getInstance().getAllPlayers()) {
                            // Like L2OFF if player is blocked can't read the message
                            if (!player.getBlockList().isInBlockList(activeChar.getName()))
                                player.sendPacket(cs);
                        }
                    }
                }
                break;
            case TRADE:
                if (Config.DEFAULT_TRADE_CHAT.equalsIgnoreCase("ON")) {
                    if (Config.TRADE_CHAT_WITH_PVP) {
                        if ((activeChar.getPvpKills() <= Config.TRADE_PVP_AMOUNT) && !activeChar.isGM()) {
                            activeChar.sendMessage("You must have at least " + Config.TRADE_PVP_AMOUNT + "  pvp kills in order to speak in trade chat");
                            return;
                        }
                        for (final L2PcInstance player : L2World.getInstance().getAllPlayers()) {
                            // Like L2OFF if player is blocked can't read the message
                            if (!player.getBlockList().isInBlockList(activeChar.getName()))
                                player.sendPacket(cs);
                        }
                    } else {
                        for (final L2PcInstance player : L2World.getInstance().getAllPlayers()) {
                            // Like L2OFF if player is blocked can't read the message
                            if (!player.getBlockList().isInBlockList(activeChar.getName()))
                                player.sendPacket(cs);
                        }
                    }
                } else if (Config.DEFAULT_TRADE_CHAT.equalsIgnoreCase("limited")) {
                    if (Config.TRADE_CHAT_WITH_PVP) {
                        if ((activeChar.getPvpKills() <= Config.TRADE_PVP_AMOUNT) && !activeChar.isGM()) {
                            activeChar.sendMessage("You must have at least " + Config.TRADE_PVP_AMOUNT + "  pvp kills in order to speak in trade chat");
                            return;
                        }
                        final int region = MapRegionTable.getInstance().getMapRegion(activeChar.getX(), activeChar.getY());
                        for (final L2PcInstance player : L2World.getInstance().getAllPlayers()) {
                            if (region == MapRegionTable.getInstance().getMapRegion(player.getX(), player.getY())) {
                                // Like L2OFF if player is blocked can't read the message
                                if (!player.getBlockList().isInBlockList(activeChar.getName()))
                                    player.sendPacket(cs);
                            }
                        }
                    } else if (Config.TRADE_CHAT_IS_NOOBLE) {
                        if (!activeChar.isNoble() && !activeChar.isGM()) {
                            activeChar.sendMessage("Only Nobless Players Can Use This Chat");
                            return;
                        }

                        final int region = MapRegionTable.getInstance().getMapRegion(activeChar.getX(), activeChar.getY());
                        for (final L2PcInstance player : L2World.getInstance().getAllPlayers()) {
                            if (region == MapRegionTable.getInstance().getMapRegion(player.getX(), player.getY())) {
                                // Like L2OFF if player is blocked can't read the message
                                if (!player.getBlockList().isInBlockList(activeChar.getName()))
                                    player.sendPacket(cs);
                            }
                        }

                    } else {
                        final int region = MapRegionTable.getInstance().getMapRegion(activeChar.getX(), activeChar.getY());
                        for (final L2PcInstance player : L2World.getInstance().getAllPlayers()) {
                            if (region == MapRegionTable.getInstance().getMapRegion(player.getX(), player.getY())) {
                                // Like L2OFF if player is blocked can't read the message
                                if (!player.getBlockList().isInBlockList(activeChar.getName()))
                                    player.sendPacket(cs);
                            }
                        }
                    }

                }
                ConsoleTab.appendMessage(ConsoleTab.ConsoleFilter.TradeChat, activeChar.getName() + ": " + _text, nearTown);
                break;
            case ALL:


                if (_text.startsWith(".")) {
                    final StringTokenizer st = new StringTokenizer(_text);
                    IVoicedCommandHandler vch;
                    String command = "";
                    String target = "";

                    if (st.countTokens() > 1) {
                        command = st.nextToken().substring(1);
                        target = _text.substring(command.length() + 2);
                        vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler(command);
                    } else {
                        command = _text.substring(1);
                        if (Config.DEBUG) {
                            LOGGER.info("Command: " + command);
                        }
                        vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler(command);
                    }

                    if (vch != null) {
                        vch.useVoicedCommand(command, activeChar, target);
                        break;
                    }
                }


                for (final L2PcInstance player : activeChar.getKnownList().getKnownPlayers().values()) {
                    if (player != null && activeChar.isInsideRadius(player, 1250, false, true)) {
                        // Like L2OFF if player is blocked can't read the message
                        if (!player.getBlockList().isInBlockList(activeChar.getName()))
                            player.sendPacket(cs);
                    }
                }
                activeChar.sendPacket(cs);
                ConsoleTab.appendMessage(ConsoleTab.ConsoleFilter.AllChat,
                        "[Somewhere near " + nearTown + "] " + activeChar.getName() + ": " + _text,
                        nearTown,
                        activeChar.getName());
                break;
            case CLAN:
                if (activeChar.getClan() != null) {
                    ConsoleTab.appendMessage(ConsoleTab.ConsoleFilter.ClanChat,
                            "[" + activeChar.getClan().getName() + "] " + activeChar.getName() + ": " + _text,
                            activeChar.getClan().getName(),
                            activeChar.getName());

                    activeChar.getClan().broadcastToOnlineMembers(cs);
                }
                break;
            case ALLIANCE:
                if (activeChar.getClan() != null) {
                    String allyName = activeChar.getClan().getAllyName();
                    ConsoleTab.appendMessage(ConsoleTab.ConsoleFilter.AllyChat,
                            "[" + allyName + "] " + activeChar.getName() + ": " + _text,
                            allyName,
                            activeChar.getName());

                    activeChar.getClan().broadcastToOnlineAllyMembers(cs);
                }
                break;
            case PARTY:
                if (activeChar.isInParty()) {
                    String leaderName = activeChar.getParty().getLeader().getName();
                    ConsoleTab.appendMessage(ConsoleTab.ConsoleFilter.PartyChat,
                            "[" + leaderName + "'s party] " + activeChar.getName() + ": " + _text,
                            leaderName,
                            activeChar.getName());
                    activeChar.getParty().broadcastToPartyMembers(cs);
                }
                break;
            case PETITION_PLAYER:
            case PETITION_GM:
                if (!PetitionManager.getInstance().isPlayerInConsultation(activeChar)) {
                    activeChar.sendPacket(new SystemMessage(SystemMessageId.YOU_ARE_NOT_IN_PETITION_CHAT));
                    break;
                }

                PetitionManager.getInstance().sendActivePetitionMessage(activeChar, _text);
                break;
            case PARTYROOM_ALL:
                if (activeChar.isInParty()) {
                    if (activeChar.getParty().isInCommandChannel() && activeChar.getParty().isLeader(activeChar)) {
                        activeChar.getParty().getCommandChannel().broadcastCSToChannelMembers(cs, activeChar);
                    }
                }
                break;
            case PARTYROOM_COMMANDER:
                if (activeChar.isInParty()) {
                    if (activeChar.getParty().isInCommandChannel() && activeChar.getParty().getCommandChannel().getChannelLeader().equals(activeChar)) {
                        activeChar.getParty().getCommandChannel().broadcastCSToChannelMembers(cs, activeChar);
                    }
                }
                break;
            case HERO_VOICE:
                if (activeChar.isGM()) {
                    for (final L2PcInstance player : L2World.getInstance().getAllPlayers()) {

                        if (player == null)
                            continue;
                        ConsoleTab.appendMessage(ConsoleTab.ConsoleFilter.HeroChat, activeChar.getName() + ": " + _text);
                        player.sendPacket(cs);
                    }
                } else if (activeChar.isHero()) {
                    // Flood protect Hero Voice
                    if (!getClient().getFloodProtectors().getHeroVoice().tryPerformAction("hero voice"))
                        return;

                    for (final L2PcInstance player : L2World.getInstance().getAllPlayers()) {

                        if (player == null)
                            continue;

                        // Like L2OFF if player is blocked can't read the message
                        ConsoleTab.appendMessage(ConsoleTab.ConsoleFilter.HeroChat, activeChar.getName() + ": " + _text);
                        if (!player.getBlockList().isInBlockList(activeChar.getName()))
                            player.sendPacket(cs);
                    }
                }
                break;
        }
    }

    private static final String[] WALKER_COMMAND_LIST =
            {
                    "USESKILL",
                    "USEITEM",
                    "BUYITEM",
                    "SELLITEM",
                    "SAVEITEM",
                    "LOADITEM",
                    "MSG",
                    "SET",
                    "DELAY",
                    "LABEL",
                    "JMP",
                    "CALL",
                    "RETURN",
                    "MOVETO",
                    "NPCSEL",
                    "NPCDLG",
                    "DLGSEL",
                    "CHARSTATUS",
                    "POSOUTRANGE",
                    "POSINRANGE",
                    "GOHOME",
                    "SAY",
                    "EXIT",
                    "PAUSE",
                    "STRINDLG",
                    "STRNOTINDLG",
                    "CHANGEWAITTYPE",
                    "FORCEATTACK",
                    "ISMEMBER",
                    "REQUESTJOINPARTY",
                    "REQUESTOUTPARTY",
                    "QUITPARTY",
                    "MEMBERSTATUS",
                    "CHARBUFFS",
                    "ITEMCOUNT",
                    "FOLLOWTELEPORT"
            };

    private boolean checkBot(final String text) {
        for (final String botCommand : WALKER_COMMAND_LIST) {
            if (text.startsWith(botCommand))
                return true;
        }
        return false;
    }

    private void checkText(final L2PcInstance activeChar) {
        if (Config.USE_SAY_FILTER) {
            String filteredText = _text;

            for (final String pattern : Config.FILTER_LIST) {
                filteredText = filteredText.replaceAll("(?i)" + pattern, Config.CHAT_FILTER_CHARS);
            }

            if (!filteredText.equalsIgnoreCase(_text)) {
                if (Config.CHAT_FILTER_PUNISHMENT.equalsIgnoreCase("chat")) {
                    activeChar.setPunishLevel(PunishLevel.CHAT, Config.CHAT_FILTER_PUNISHMENT_PARAM1);
                    activeChar.sendMessage("Administrator banned you chat from " + Config.CHAT_FILTER_PUNISHMENT_PARAM1 + " minutes");
                } else if (Config.CHAT_FILTER_PUNISHMENT.equalsIgnoreCase("karma")) {
                    activeChar.setKarma(Config.CHAT_FILTER_PUNISHMENT_PARAM2);
                    activeChar.sendMessage("You have get " + Config.CHAT_FILTER_PUNISHMENT_PARAM2 + " karma for bad words");
                } else if (Config.CHAT_FILTER_PUNISHMENT.equalsIgnoreCase("jail")) {
                    activeChar.setPunishLevel(PunishLevel.JAIL, Config.CHAT_FILTER_PUNISHMENT_PARAM1);
                }
                activeChar.sendMessage("The word " + _text + " is not allowed!");
                _text = filteredText;
            }
        }
    }

    private void checkTextForAds(final L2PcInstance activeChar) {

        String filteredText = _text + " ";

        try {
            if (filteredText.toLowerCase().contains("l2 ")) {
                if (!StringUtils.substringBetween(filteredText.toLowerCase(), "l2 ", " ").trim().equals("") && !StringUtils.substringBetween(filteredText.toLowerCase(), "l2 ", " ").toLowerCase().equals("wonderlust")) {
                    addToAdList("l2" + StringUtils.substringBetween(filteredText.toLowerCase(), "l2 ", " ").toLowerCase());
                    addToAdList("l2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "l2 ", " ").toLowerCase());
                    addToAdList("la2" + StringUtils.substringBetween(filteredText.toLowerCase(), "l2 ", " ").toLowerCase());
                    addToAdList("la2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "l2 ", " ").toLowerCase());
                    addToAdList("la 2" + StringUtils.substringBetween(filteredText.toLowerCase(), "l2 ", " ").toLowerCase());
                    addToAdList("la 2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "l2 ", " ").toLowerCase());
                    addToAdList("lineage2" + StringUtils.substringBetween(filteredText.toLowerCase(), "l2 ", " ").toLowerCase());
                    addToAdList("lineage2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "l2 ", " ").toLowerCase());
                    addToAdList("lineage 2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "l2 ", " ").toLowerCase());
                }
            } else if (filteredText.toLowerCase().contains("l2")) {
                if (!StringUtils.substringBetween(filteredText.toLowerCase(), "l2", " ").trim().equals("") && !StringUtils.substringBetween(filteredText.toLowerCase(), "l2", " ").toLowerCase().contains("wonderlust")) {
                    addToAdList("l2" + StringUtils.substringBetween(filteredText.toLowerCase(), "l2", " ").toLowerCase());
                    addToAdList("l2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "l2", " ").toLowerCase());
                    addToAdList("la2" + StringUtils.substringBetween(filteredText.toLowerCase(), "l2", " ").toLowerCase());
                    addToAdList("la2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "l2", " ").toLowerCase());
                    addToAdList("la 2" + StringUtils.substringBetween(filteredText.toLowerCase(), "l2", " ").toLowerCase());
                    addToAdList("la 2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "l2", " ").toLowerCase());
                    addToAdList("lineage2" + StringUtils.substringBetween(filteredText.toLowerCase(), "l2", " ").toLowerCase());
                    addToAdList("lineage2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "l2", " ").toLowerCase());
                    addToAdList("lineage 2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "l2", " ").toLowerCase());
                }
            } else if (filteredText.toLowerCase().contains("la2 ")) {
                if (!StringUtils.substringBetween(filteredText.toLowerCase(), "la2 ", " ").trim().equals("") && !StringUtils.substringBetween(filteredText.toLowerCase(), "la2 ", " ").toLowerCase().equals("wonderlust")) {
                    addToAdList("l2" + StringUtils.substringBetween(filteredText.toLowerCase(), "la2 ", " ").toLowerCase());
                    addToAdList("l2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "la2 ", " ").toLowerCase());
                    addToAdList("la2" + StringUtils.substringBetween(filteredText.toLowerCase(), "la2 ", " ").toLowerCase());
                    addToAdList("la2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "la2 ", " ").toLowerCase());
                    addToAdList("la 2" + StringUtils.substringBetween(filteredText.toLowerCase(), "la2 ", " ").toLowerCase());
                    addToAdList("la 2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "la2 ", " ").toLowerCase());
                    addToAdList("lineage2" + StringUtils.substringBetween(filteredText.toLowerCase(), "la2 ", " ").toLowerCase());
                    addToAdList("lineage2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "la2 ", " ").toLowerCase());
                    addToAdList("lineage 2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "la2 ", " ").toLowerCase());
                }
            } else if (filteredText.toLowerCase().contains("la2")) {
                if (!StringUtils.substringBetween(filteredText.toLowerCase(), "la2", " ").trim().equals("") && !StringUtils.substringBetween(filteredText.toLowerCase(), "la2", " ").toLowerCase().equals("wonderlust")) {
                    addToAdList("l2" + StringUtils.substringBetween(filteredText.toLowerCase(), "la2", " ").toLowerCase());
                    addToAdList("l2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "la2", " ").toLowerCase());
                    addToAdList("la2" + StringUtils.substringBetween(filteredText.toLowerCase(), "la2", " ").toLowerCase());
                    addToAdList("la2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "la2", " ").toLowerCase());
                    addToAdList("la 2" + StringUtils.substringBetween(filteredText.toLowerCase(), "la2", " ").toLowerCase());
                    addToAdList("la 2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "la2", " ").toLowerCase());
                    addToAdList("lineage2" + StringUtils.substringBetween(filteredText.toLowerCase(), "la2", " ").toLowerCase());
                    addToAdList("lineage2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "la2", " ").toLowerCase());
                    addToAdList("lineage 2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "la2", " ").toLowerCase());
                }
            } else if (filteredText.toLowerCase().contains("la 2 ")) {
                if (!StringUtils.substringBetween(filteredText.toLowerCase(), "la 2 ", " ").trim().equals("") && !StringUtils.substringBetween(filteredText.toLowerCase(), "la 2 ", " ").toLowerCase().equals("wonderlust")) {
                    addToAdList("l2" + StringUtils.substringBetween(filteredText.toLowerCase(), "la 2 ", " ").toLowerCase());
                    addToAdList("l2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "la 2 ", " ").toLowerCase());
                    addToAdList("la2" + StringUtils.substringBetween(filteredText.toLowerCase(), "la 2 ", " ").toLowerCase());
                    addToAdList("la2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "la 2 ", " ").toLowerCase());
                    addToAdList("la 2" + StringUtils.substringBetween(filteredText.toLowerCase(), "la 2 ", " ").toLowerCase());
                    addToAdList("la 2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "la 2 ", " ").toLowerCase());
                    addToAdList("lineage2" + StringUtils.substringBetween(filteredText.toLowerCase(), "la 2 ", " ").toLowerCase());
                    addToAdList("lineage2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "la 2 ", " ").toLowerCase());
                    addToAdList("lineage 2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "la 2 ", " ").toLowerCase());
                }
            } else if (filteredText.toLowerCase().contains("la 2")) {
                if (!StringUtils.substringBetween(filteredText.toLowerCase(), "la 2", " ").trim().equals("") && !StringUtils.substringBetween(filteredText.toLowerCase(), "la 2", " ").toLowerCase().equals("wonderlust")) {
                    addToAdList("l2" + StringUtils.substringBetween(filteredText.toLowerCase(), "la 2", " ").toLowerCase());
                    addToAdList("l2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "la 2", " ").toLowerCase());
                    addToAdList("la2" + StringUtils.substringBetween(filteredText.toLowerCase(), "la 2", " ").toLowerCase());
                    addToAdList("la2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "la 2", " ").toLowerCase());
                    addToAdList("la 2" + StringUtils.substringBetween(filteredText.toLowerCase(), "la 2", " ").toLowerCase());
                    addToAdList("la 2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "la 2", " ").toLowerCase());
                    addToAdList("lineage2" + StringUtils.substringBetween(filteredText.toLowerCase(), "la 2", " ").toLowerCase());
                    addToAdList("lineage2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "la 2", " ").toLowerCase());
                    addToAdList("lineage 2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "la 2", " ").toLowerCase());
                }
            } else if (filteredText.toLowerCase().contains("lineage2 ")) {
                if (!StringUtils.substringBetween(filteredText.toLowerCase(), "lineage2 ", " ").trim().equals("") && !StringUtils.substringBetween(filteredText.toLowerCase(), "lineage2 ", " ").toLowerCase().equals("wonderlust")) {
                    addToAdList("l2" + StringUtils.substringBetween(filteredText.toLowerCase(), "lineage2 ", " ").toLowerCase());
                    addToAdList("l2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "lineage2 ", " ").toLowerCase());
                    addToAdList("la2" + StringUtils.substringBetween(filteredText.toLowerCase(), "lineage2 ", " ").toLowerCase());
                    addToAdList("la2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "lineage2 ", " ").toLowerCase());
                    addToAdList("la 2" + StringUtils.substringBetween(filteredText.toLowerCase(), "lineage2 ", " ").toLowerCase());
                    addToAdList("la 2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "lineage2 ", " ").toLowerCase());
                    addToAdList("lineage2" + StringUtils.substringBetween(filteredText.toLowerCase(), "lineage2 ", " ").toLowerCase());
                    addToAdList("lineage2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "lineage2 ", " ").toLowerCase());
                    addToAdList("lineage 2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "lineage2 ", " ").toLowerCase());
                }
            } else if (filteredText.toLowerCase().contains("lineage2")) {
                if (!StringUtils.substringBetween(filteredText.toLowerCase(), "lineage2", " ").trim().equals("") && !StringUtils.substringBetween(filteredText.toLowerCase(), "lineage2", " ").toLowerCase().equals("wonderlust")) {
                    addToAdList("l2" + StringUtils.substringBetween(filteredText.toLowerCase(), "lineage2", " ").toLowerCase());
                    addToAdList("l2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "lineage2", " ").toLowerCase());
                    addToAdList("la2" + StringUtils.substringBetween(filteredText.toLowerCase(), "lineage2", " ").toLowerCase());
                    addToAdList("la2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "lineage2", " ").toLowerCase());
                    addToAdList("la 2" + StringUtils.substringBetween(filteredText.toLowerCase(), "lineage2", " ").toLowerCase());
                    addToAdList("la 2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "lineage2", " ").toLowerCase());
                    addToAdList("lineage2" + StringUtils.substringBetween(filteredText.toLowerCase(), "lineage2", " ").toLowerCase());
                    addToAdList("lineage2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "lineage2", " ").toLowerCase());
                    addToAdList("lineage 2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "lineage2", " ").toLowerCase());
                }
            } else if (filteredText.toLowerCase().contains("lineage 2 ")) {
                if (!StringUtils.substringBetween(filteredText.toLowerCase(), "lineage 2 ", " ").trim().equals("") && !StringUtils.substringBetween(filteredText.toLowerCase(), "lineage 2 ", " ").toLowerCase().equals("wonderlust")) {
                    addToAdList("l2" + StringUtils.substringBetween(filteredText.toLowerCase(), "lineage 2 ", " ").toLowerCase());
                    addToAdList("l2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "lineage 2 ", " ").toLowerCase());
                    addToAdList("la2" + StringUtils.substringBetween(filteredText.toLowerCase(), "lineage 2 ", " ").toLowerCase());
                    addToAdList("la2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "lineage 2 ", " ").toLowerCase());
                    addToAdList("la 2" + StringUtils.substringBetween(filteredText.toLowerCase(), "lineage 2 ", " ").toLowerCase());
                    addToAdList("la 2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "lineage 2 ", " ").toLowerCase());
                    addToAdList("lineage2" + StringUtils.substringBetween(filteredText.toLowerCase(), "lineage 2 ", " ").toLowerCase());
                    addToAdList("lineage2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "lineage 2 ", " ").toLowerCase());
                    addToAdList("lineage 2 " + StringUtils.substringBetween(filteredText.toLowerCase(), "lineage 2 ", " ").toLowerCase());
                }
            }

            String tempText = filteredText;

            tempText = tempText.replaceAll("\\s+", "");
            tempText = tempText.replaceAll("4", "a");
            tempText = tempText.replaceAll("@", "a");
            tempText = tempText.replaceAll("0", "o");
            tempText = tempText.replaceAll("1", "i");
            tempText = tempText.replaceAll("3", "e");
            tempText = tempText.replaceAll("5", "s");

            for (String s : Config.ADFILTER_LIST) {
                if (tempText.toLowerCase().contains(s.toLowerCase())) {
                    filteredText = "";
                    activeChar.sendPacket(new ExShowScreenMessage("Advertising Other Servers Is Prohibited", 3000));
                    activeChar.sendMessage("Do not advertise other servers!");
                    break;
                }
            }

        } catch (NullPointerException ignored) {}

        if (filteredText.toLowerCase().contains("www.")) {

            String s = filteredText;

            if (!s.toLowerCase().contains("wonderlust")) {
                filteredText = "";
                activeChar.sendPacket(new ExShowScreenMessage("Advertising Links Is Prohibited", 3000));
                activeChar.sendMessage("Do not advertise links!");
            }
        }

        if (!filteredText.equals(_text + " ")) {
            _text = filteredText;
        }

    }

    private void addToAdList(String text) {
        if (!Config.ADFILTER_LIST.contains(text)) {
            Config.ADFILTER_LIST.add(text);
        }
    }

    @Override
    public String getType() {
        return "[C] 38 Say2";
    }
}