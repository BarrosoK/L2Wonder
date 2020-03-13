/*
 * L2jFrozen Project - www.l2jfrozen.com
 *
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
package com.l2jfrozen.gameserver.custom.Events;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.l2jfrozen.gameserver.model.actor.instance.L2ArenaManagerInstance;
import com.l2jfrozen.gameserver.model.entity.event.DM;
import com.l2jfrozen.gameserver.network.serverpackets.*;
import javolution.text.TextBuilder;

import org.apache.log4j.Logger;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.datatables.SkillTable;
import com.l2jfrozen.gameserver.datatables.sql.ItemTable;
import com.l2jfrozen.gameserver.datatables.sql.NpcTable;
import com.l2jfrozen.gameserver.datatables.sql.SpawnTable;
import com.l2jfrozen.gameserver.managers.CastleManager;
import com.l2jfrozen.gameserver.model.L2Party;
import com.l2jfrozen.gameserver.model.L2Summon;
import com.l2jfrozen.gameserver.model.L2World;
import com.l2jfrozen.gameserver.model.Location;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PetInstance;
import com.l2jfrozen.gameserver.model.base.ClassId;
import com.l2jfrozen.gameserver.model.entity.Announcements;
import com.l2jfrozen.gameserver.model.entity.event.manager.EventTask;
import com.l2jfrozen.gameserver.model.entity.olympiad.Olympiad;
import com.l2jfrozen.gameserver.model.entity.siege.Castle;
import com.l2jfrozen.gameserver.model.spawn.L2Spawn;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;
import com.l2jfrozen.gameserver.thread.ThreadPoolManager;
import com.l2jfrozen.util.CloseUtil;
import com.l2jfrozen.util.database.DatabaseUtils;
import com.l2jfrozen.util.database.L2DatabaseFactory;
import com.l2jfrozen.util.random.Rnd;

/**
 * The Class BR.
 */
public class BattleRoyale implements EventTask {

    /**
     * The Constant LOGGER.
     */
    protected static final Logger LOGGER = Logger.getLogger(BattleRoyale.class);

    /**
     * The _joining location name.
     */
    private static String _eventName = "BattleRoyale";

    /**
     * The _in progress.
     */
    private static boolean _joining = false, _teleport = false, _started = false, _aborted = false, _sitForced = false, _inProgress = false;

    /**
     * The _player z.
     */
    protected static int _joinTime = 4, _eventTime = 20, _minPlayers = 2, _maxPlayers = 100, _playerColors = 2552550, _playerX = 174234, _playerY = -88019, _playerZ = -5135, _rewardId = 4037, _rewardAmount = 3;


    protected  static int _npcId = 384910, _npcX = 82566, _npcY=148614, _npcZ=-3495, _npcHeading = 0;

    /**
     * The _npc spawn.
     */
    private static L2Spawn _npcSpawn;

    protected static L2Spawn buffer;
    /**
     * The _interval between matchs.
     */
    private static long _intervalBetweenMatchs = 300000;
    /**
     * The start event time.
     */
    private String startEventTime;

    /**
     * The _players.
     */
    public static Vector<L2PcInstance> _players = new Vector<>();

    /**
     * Instantiates a new BR.
     */
    private BattleRoyale() {
    }

    /**
     * Gets the new instance.
     *
     * @return the new instance
     */
    public static BattleRoyale getNewInstance() {
        return new BattleRoyale();
    }

    /**
     * Gets the _event name.
     *
     * @return the _eventName
     */
    public static String get_eventName() {
        return _eventName;
    }

    /**
     * Checks if is _joining.
     *
     * @return the _joining
     */
    public static boolean is_joining() {
        return _joining;
    }

    /**
     * Checks if is _teleport.
     *
     * @return the _teleport
     */
    public static boolean is_teleport() {
        return _teleport;
    }

    /**
     * Checks if is _started.
     *
     * @return the _started
     */
    public static boolean is_started() {
        return _started;
    }

    /**
     * Checks if is _sit forced.
     *
     * @return the _sitForced
     */
    public static boolean is_sitForced() {
        return _sitForced;
    }

    /**
     * Checks if is _in progress.
     *
     * @return the _inProgress
     */
    public static boolean is_inProgress() {
        return _inProgress;
    }


    /**
     * returns true if participated players is higher or equal then minimum needed players.
     *
     * @param players the players
     * @return true, if successful
     */
    public static boolean checkMinPlayers(final int players) {
        if (_minPlayers <= players)
            return true;

        return false;
    }

    /**
     * returns true if max players is higher or equal then participated players.
     *
     * @param players the players
     * @return true, if successful
     */
    public static boolean checkMaxPlayers(final int players) {
        if (_maxPlayers > players)
            return true;

        return false;
    }

    /**
     * Check start join ok.
     *
     * @return true, if successful
     */
    public static boolean checkStartJoinOk() {
        if (_started || _teleport || _joining || _eventName.equals(""))
            return false;


        if (!checkStartJoinPlayerInfo())
            return false;


        if (!Config.ALLOW_EVENTS_DURING_OLY && Olympiad.getInstance().inCompPeriod())
            return false;

        for (final Castle castle : CastleManager.getInstance().getCastles()) {
            if (castle != null && castle.getSiege() != null && castle.getSiege().getIsInProgress())
                return false;
        }

        return true;
    }

    /**
     * Check start join player info.
     *
     * @return true, if successful
     */
    private static boolean checkStartJoinPlayerInfo() {
        if (_playerX == 0 || _playerY == 0 || _playerZ == 0 || _playerColors == 0) {
            return false;
        }

        return true;
    }

    /**
     * Check auto event start join ok.
     *
     * @return true, if successful
     */
    private static boolean checkAutoEventStartJoinOk() {
        if (_joinTime == 0 || _eventTime == 0) {
            return false;
        }

        return true;
    }

    /**
     * Start join.
     *
     * @return true, if successful
     */
    public static boolean startJoin() {
        if (!checkStartJoinOk()) {
            if (Config.DEBUG)
                LOGGER.warn(_eventName + " Engine[startJoin]: startJoinOk() = false");
            return false;
        }

        _inProgress = true;
        _joining = true;
        spawnEventNpc();
        Announcements.getInstance().gameAnnounceToAll(_eventName + ": Full Buff Event " + _eventName + "!");
        if (Config.BR_ANNOUNCE_REWARD && ItemTable.getInstance().getTemplate(_rewardId) != null)
            Announcements.getInstance().gameAnnounceToAll(_eventName + ": Reward: " + _rewardAmount + " " + ItemTable.getInstance().getTemplate(_rewardId).getName());
        Announcements.getInstance().gameAnnounceToAll(_eventName + ": Press .brjoin to participate.");

        if (Config.BR_COMMAND)
            Announcements.getInstance().gameAnnounceToAll(_eventName + ": Commands .brjoin .brleave");

        Announcements.getInstance().gameAnnounceToAll(_eventName + ": FULL BUFF Event: be ready with your buffs they won't be deleted!!!");

        return true;
    }

    /**
     * Start teleport.
     *
     * @return true, if successful
     */
    public static boolean startTeleport() {
        if (!_joining || _started || _teleport)
            return false;

        removeOfflinePlayers();
        unspawnEventNpc();

        synchronized (_players) {
            final int size = _players.size();
            if (!checkMinPlayers(size)) {
                Announcements.getInstance().gameAnnounceToAll(_eventName + ": Not enough players for event.\nMin Requested : " + _minPlayers + ", Participating : " + size);
                if (Config.BR_STATS_LOGGER)
                    LOGGER.info(_eventName + ":Not enough players for event. Min Requested : " + _minPlayers + ", Participating : " + size);

                return false;
            }
        }


        _joining = false;
        Announcements.getInstance().gameAnnounceToAll(_eventName + ": Teleport to the event spot in 20 seconds!");

        setUserData();
        ThreadPoolManager.getInstance().scheduleGeneral(new Runnable() {
            @Override
            public void run() {
                afterTeleportOperations();
                removeParties();
                spawnBuffer();
                synchronized (_players) {

                    for (final L2PcInstance player : _players) {
                        if (player != null) {
                            if (Config.BR_ON_START_UNSUMMON_PET) {
                                // Remove Summon's buffs
                                if (player.getPet() != null) {
                                    final L2Summon summon = player.getPet();


                                    if (summon instanceof L2PetInstance) {
                                        summon.unSummon(player);
                                    }
                                }
                            }

                            if (Config.BR_ON_START_REMOVE_ALL_EFFECTS) {
                                player.stopAllEffects();
                            }

                            // Remove player from his party
                            if (player.getParty() != null) {
                                final L2Party party = player.getParty();
                                party.removePartyMember(player);
                            }
                            player.getAppearance().setInvisible();

                            final int offset = Config.BR_SPAWN_OFFSET;
                            player.teleToLocation(_playerX + Rnd.get(offset), _playerY + Rnd.get(offset), _playerZ);
                            player.sendPacket(new ExShowScreenMessage("Get position and prepare yourself.", 5000));

                        }
                    }

                }

            }
        }, 20000);
        _teleport = true;
        return true;
    }

    /**
     * After teleport operations.
     */
    protected static void afterTeleportOperations() {

    }

    /**
     * Start event.
     *
     * @return true, if successful
     */
    public static boolean startEvent() {
        if (!startEventOk()) {
            if (Config.DEBUG)
                LOGGER.warn(_eventName + " Engine[startEvent()]: startEventOk() = false");
            return false;
        }

        _teleport = false;

        removeParties();
        unspawnBuffer();
        afterStartOperations();

        Announcements.getInstance().gameAnnounceToAll(_eventName + ": Started. Go to kill your enemies!");
        _started = true;

        return true;
    }

    /**
     * Removes the parties.
     */
    private static void removeParties() {
        synchronized (_players) {

            for (final L2PcInstance player : _players) {
                if (player.getParty() != null) {
                    final L2Party party = player.getParty();
                    party.removePartyMember(player);
                }
            }
        }
    }

    /**
     * After start operations.
     */
    private static void afterStartOperations() {

        synchronized (_players) {

            for (final L2PcInstance player : _players) {
                if (player != null) {
                    player.getAppearance().setVisible();
                    player.broadcastUserInfo();
                }
            }

        }

    }

    /**
     * Restarts Event checks if event was aborted. and if true cancels restart task
     */
    public synchronized static void restartEvent() {
        LOGGER.info(_eventName + ": Event has been restarted...");
        _joining = false;
        _started = false;
        _inProgress = false;
        _aborted = false;
        final long delay = _intervalBetweenMatchs;

        Announcements.getInstance().gameAnnounceToAll(_eventName + ": joining period will be avaible again in " + _intervalBetweenMatchs + " minute(s)!");

        waiter(delay);

        try {
            if (!_aborted)
                autoEvent(); // start a new event
            else
                Announcements.getInstance().gameAnnounceToAll(_eventName + ": next event aborted!");
        } catch (final Exception e) {
            LOGGER.error(_eventName + ": Error While Trying to restart Event...", e);
            e.printStackTrace();
        }
    }

    /**
     * Finish event.
     */
    public static void finishEvent() {
        if (!finishEventOk()) {
            if (Config.DEBUG)
                LOGGER.warn(_eventName + " Engine[finishEvent]: finishEventOk() = false");
            return;
        }

        _started = false;
        _aborted = false;

        afterFinishOperations();
        rewardPlayer();

        teleportFinish();
    }

    /**
     * After finish operations.
     */
    private static void afterFinishOperations() {

    }

    /**
     * Abort event.
     */
    public static void abortEvent() {
        if (!_joining && !_teleport && !_started)
            return;

        if (_joining && !_teleport && !_started) {
            unspawnEventNpc();
            cleanBR();
            _joining = false;
            _inProgress = false;
            Announcements.getInstance().gameAnnounceToAll(_eventName + ": Match aborted!");
            return;
        }
        _joining = false;
        _teleport = false;
        _started = false;
        _aborted = true;
        unspawnEventNpc();
        afterFinish();

        Announcements.getInstance().gameAnnounceToAll(_eventName + ": Match aborted!");
        teleportFinish();
    }

    /**
     * After finish.
     */
    private static void afterFinish() {

    }

    /**
     * Teleport finish.
     */
    public static void teleportFinish() {

        Announcements.getInstance().gameAnnounceToAll(_eventName + ": Teleport back to Giran in 20 seconds!");

        removeUserData();
        ThreadPoolManager.getInstance().scheduleGeneral(new Runnable() {
            @Override
            public void run() {
                synchronized (_players) {

                    for (final L2PcInstance player : _players) {
                        if (player != null) {
                            if (player.isOnline() != 0)
                                player.teleToLocation(81310, 148604, -3495, true);

                        }
                    }

                }

                cleanBR();
            }
        }, 20000);
    }

    /**
     * Auto event.
     */
    public static void autoEvent() {
        LOGGER.info("Starting " + _eventName + "!");
        LOGGER.info("Matchs Are Restarted At Every: " + getIntervalBetweenMatchs() + " Minutes.");
        if (checkAutoEventStartJoinOk() && startJoin() && !_aborted) {
            if (_joinTime > 0)
                waiter(_joinTime * 60 * 1000); // minutes for join event
            else if (_joinTime <= 0) {
                LOGGER.info(_eventName + ": join time <=0 aborting event.");
                abortEvent();
                return;
            }
            if (startTeleport() && !_aborted) {
                waiter(60 * 1000); // 30 sec wait time untill start fight after teleported
                if (startEvent() && !_aborted) {
                    LOGGER.warn(_eventName + ": waiting.....minutes for event time " + _eventTime);

                    waiter(_eventTime * 60 * 1000); // minutes for event time
                    finishEvent();

                    LOGGER.info(_eventName + ": waiting... delay for final messages ");
                    waiter(60000);// just a give a delay delay for final messages
                    sendFinalMessages();

                    if (!_started && !_aborted) { // if is not already started and it's not aborted

                        LOGGER.info(_eventName + ": waiting.....delay for restart event  " + _intervalBetweenMatchs + " minutes.");
                        waiter(60000);// just a give a delay to next restart

                        try {
                            if (!_aborted)
                                restartEvent();
                        } catch (final Exception e) {
                            LOGGER.error("Error while tying to Restart Event", e);
                            e.printStackTrace();
                        }

                    }

                }
            } else if (!_aborted) {

                abortEvent();
                restartEvent();

            }
        }
    }

    // start without restart

    /**
     * Event once start.
     */
    public static void eventOnceStart() {
        if (startJoin() && !_aborted) {
            if (_joinTime > 0)
                waiter(_joinTime * 60 * 1000); // minutes for join event
            else if (_joinTime <= 0) {
                abortEvent();
                return;
            }
            if (startTeleport() && !_aborted) {
                waiter(1 * 60 * 1000); // 1 min wait time until start fight after teleported
                if (startEvent() && !_aborted) {
                    waiter(_eventTime * 60 * 1000); // minutes for event time
                    finishEvent();
                }
            } else if (!_aborted) {
                abortEvent();
            }
        }
    }

    /**
     * Waiter.
     *
     * @param interval the interval
     */
    private static void waiter(final long interval) {
        final long startWaiterTime = System.currentTimeMillis();
        int seconds = (int) (interval / 1000);

        while (startWaiterTime + interval > System.currentTimeMillis() && !_aborted) {
            seconds--; // Here because we don't want to see two time announce at the same time

            if (_joining || _started || _teleport) {
                switch (seconds) {
                    case 3600: // 1 hour left
                        removeOfflinePlayers();

                        if (_joining) {
                            Announcements.getInstance().gameAnnounceToAll(_eventName + ": Press .brjoin to participate!");
                            Announcements.getInstance().gameAnnounceToAll(_eventName + ": " + seconds / 60 / 60 + " hour(s) till registration close!");
                        } else if (_started)
                            Announcements.getInstance().gameAnnounceToAll(_eventName + ": " + seconds / 60 / 60 + " hour(s) till event finish!");

                        break;
                    case 1800: // 30 minutes left
                    case 900: // 15 minutes left
                    case 600: // 10 minutes left
                    case 300: // 5 minutes left
                    case 240: // 4 minutes left
                    case 180: // 3 minutes left
                    case 120: // 2 minutes left
                    case 60: // 1 minute left

                        if (_joining) {
                            Announcements.getInstance().gameAnnounceToAll(_eventName + ": Press .brjoin to participate!");
                            Announcements.getInstance().gameAnnounceToAll(_eventName + ": " + seconds / 60 + " minute(s) till registration close!");
                        } else if (_started)
                            Announcements.getInstance().gameAnnounceToAll(_eventName + ": " + seconds / 60 + " minute(s) till event finish!");

                        break;
                    case 30: // 30 seconds left
                    case 15: // 15 seconds left
                    case 10: // 10 seconds left
                        removeOfflinePlayers();
                    case 3: // 3 seconds left
                    case 2: // 2 seconds left
                    case 1: // 1 seconds left

                        if (_joining)
                            Announcements.getInstance().gameAnnounceToAll(_eventName + ": " + seconds + " second(s) till registration close!");
                        else if (_teleport)
                            Announcements.getInstance().gameAnnounceToAll(_eventName + ": " + seconds + " seconds(s) till start fight!");
                        else if (_started)
                            Announcements.getInstance().gameAnnounceToAll(_eventName + ": " + seconds + " second(s) till event finish!");

                        break;
                }
            }

            final long startOneSecondWaiterStartTime = System.currentTimeMillis();

            // Only the try catch with Thread.sleep(1000) give bad countdown on high wait times
            while (startOneSecondWaiterStartTime + 1000 > System.currentTimeMillis()) {
                try {
                    Thread.sleep(1);
                } catch (final InterruptedException ie) {
                    if (Config.ENABLE_ALL_EXCEPTIONS)
                        ie.printStackTrace();
                }
            }
        }
    }

    /**
     * Removes the offline players.
     */
    public static void removeOfflinePlayers() {
        try {
            synchronized (_players) {

                if (_players == null || _players.isEmpty())
                    return;

                final List<L2PcInstance> toBeRemoved = new ArrayList<>();

                for (final L2PcInstance player : _players) {
                    if (player == null)
                        continue;
                    else if (player._inEventBR && player.isOnline() == 0 || player.isInJail() || player.isInOfflineMode()) {

                        if (!_joining) {
                            player.getAppearance().setNameColor(player._originalNameColorDM);
                            player.setTitle(player._originalTitleDM);
                            player.setKarma(player._originalKarmaDM);

                            player.broadcastUserInfo();

                        }

                        player._originalNameColorDM = 0;
                        player._originalTitleDM = null;
                        player._originalKarmaDM = 0;
                        player._countDMkills = 0;
                        player._inEventBR = false;

                        toBeRemoved.add(player);

                        player.sendMessage("Your participation in the Battle Royale event has been removed.");
                    }

                }
                _players.removeAll(toBeRemoved);

            }
        } catch (final Exception e) {
            if (Config.ENABLE_ALL_EXCEPTIONS)
                e.printStackTrace();

            LOGGER.error(e.getMessage(), e);
            return;
        }
    }

    /**
     * Start event ok.
     *
     * @return true, if successful
     */
    private static boolean startEventOk() {
        if (_joining || !_teleport || _started)
            return false;

        return true;
    }

    /**
     * Finish event ok.
     *
     * @return true, if successful
     */
    private static boolean finishEventOk() {
        if (!_started)
            return false;

        return true;
    }

    /**
     * Adds the player ok.
     *
     * @param eventPlayer the event player
     * @return true, if successful
     */
    private static boolean addPlayerOk(final L2PcInstance eventPlayer) {
        if (eventPlayer.isAio() && !Config.ALLOW_AIO_IN_EVENTS) {
            eventPlayer.sendMessage("AIO charactes are not allowed to participate in events :/");
        }
        if (eventPlayer._inEventBR) {
            eventPlayer.sendMessage("You already participated in the event!");
            return false;
        }

        if (eventPlayer._inEventTvT || eventPlayer._inEventCTF) {
            eventPlayer.sendMessage("You already participated to another event!");
            return false;
        }

        if (Olympiad.getInstance().isRegistered(eventPlayer) || eventPlayer.isInOlympiadMode()) {
            eventPlayer.sendMessage("You already participated in Olympiad!");
            return false;
        }
        if (L2ArenaManagerInstance.participants.contains(eventPlayer) || (eventPlayer.isInParty() && L2ArenaManagerInstance.participantsLobby.contains(eventPlayer.getParty()))) {
            eventPlayer.sendMessage("You already participated in arena!");
            return false;
        }

        if (eventPlayer._active_boxes > 1 && !Config.ALLOW_DUALBOX_EVENT) {
            final List<String> players_in_boxes = eventPlayer.active_boxes_characters;

            if (players_in_boxes != null && players_in_boxes.size() > 1)
                for (final String character_name : players_in_boxes) {
                    final L2PcInstance player = L2World.getInstance().getPlayer(character_name);

                    if (player != null && player._inEventBR) {
                        eventPlayer.sendMessage("You already participated in event with another char!");
                        return false;
                    }
                }
        }

        if (!Config.BR_ALLOW_HEALER_CLASSES && (eventPlayer.getClassId() == ClassId.cardinal || eventPlayer.getClassId() == ClassId.evaSaint || eventPlayer.getClassId() == ClassId.shillienSaint)) {
            eventPlayer.sendMessage("You can't join with Healer Class!");
            return false;
        }

        synchronized (_players) {
            if (_players.contains(eventPlayer)) {
                eventPlayer.sendMessage("You already participated in the event!");
                return false;
            }

            for (final L2PcInstance player : _players) {
                if (player.getObjectId() == eventPlayer.getObjectId()) {
                    eventPlayer.sendMessage("You already participated in the event!");
                    return false;
                } else if (player.getName().equalsIgnoreCase(eventPlayer.getName())) {
                    eventPlayer.sendMessage("You already participated in the event!");
                    return false;
                }
            }

        }

        return true;
    }

    /**
     * Sets the user data.
     */
    public static void setUserData() {

        synchronized (_players) {

            for (final L2PcInstance player : _players) {
                player._originalNameColorDM = player.getAppearance().getNameColor();
                player._originalKarmaDM = player.getKarma();
                player._originalTitleDM = player.getTitle();
                player.getAppearance().setNameColor(_playerColors);
                player.getTemplate();
                player.setKarma(0);
                player.setTitle("Alive");

                if (player.isMounted()) {
                    if (player.setMountType(0)) {
                        if (player.isFlying()) {
                            player.removeSkill(SkillTable.getInstance().getInfo(4289, 1));
                        }

                        final Ride dismount = new Ride(player.getObjectId(), Ride.ACTION_DISMOUNT, 0);
                        player.broadcastPacket(dismount);
                        player.setMountObjectID(0);
                    }
                }
                player.broadcastUserInfo();
            }
        }
    }

    /**
     * Load data.
     */
    public static void loadData() {

        synchronized (_players) {
            _players.clear();
        }

        _joining = false;
        _teleport = false;
        _started = false;
        _sitForced = false;
        _aborted = false;
        _inProgress = false;

        _eventName = "BattleRoyale";
        _rewardId = 4037;
        _rewardAmount = 3;
        _joinTime = 2;
        _eventTime = 15;
        _minPlayers = 2;
        _maxPlayers = 100;
        _intervalBetweenMatchs = 300000;
        _playerColors = 2552550;
        _playerX = 174234;
        _playerY = -88019;
        _playerZ = -5135;

    }

    /**
     * Adds the player.
     *
     * @param player the player
     */
    public static void addPlayer(final L2PcInstance player) {
        if (!addPlayerOk(player))
            return;

        synchronized (_players) {
            _players.add(player);
        }

        player._inEventBR = true;
        player._countDMkills = 0;
        player.sendMessage("BR: You successfully registered for the Battle Royale event.");
    }

    /**
     * Removes the player.
     *
     * @param player the player
     */
    public static void removePlayer(final L2PcInstance player) {
        if (player != null && player._inEventBR) {
            synchronized (_players) {
                _players.remove(player);
            }
            if (!_joining) {
                player.getAppearance().setNameColor(player._originalNameColorDM);
                player.setTitle(player._originalTitleDM);
                player.setKarma(player._originalKarmaDM);
                player.getAppearance().setVisible();
                player.broadcastUserInfo();

            }

            // after remove, all event data must be cleaned in player
            player._originalNameColorDM = 0;
            player._originalTitleDM = null;
            player._originalKarmaDM = 0;
            player._countDMkills = 0;
            player._inEventBR = false;

            player.sendMessage("You were removed from the Battle Royale event.");
            player.broadcastUserInfo();
            if (_players.size() == 1) {
                finishEvent();
            }

        }
    }

    /**
     * Clean dm.
     */
    public static void cleanBR() {
        synchronized (_players) {

            for (final L2PcInstance player : _players) {
                if (player != null) {
                    if (player._inEventBR) {
                        if (!_joining) {
                            player.getAppearance().setNameColor(player._originalNameColorDM);
                            player.setTitle(player._originalTitleDM);
                            player.setKarma(player._originalKarmaDM);
                            player.getAppearance().setVisible();
                            player.broadcastUserInfo();
                        }

                        // after remove, all event data must be cleaned in player
                        player._originalNameColorDM = 0;
                        player._originalTitleDM = null;
                        player._originalKarmaDM = 0;
                        player._countDMkills = 0;
                        player._inEventBR = false;

                        player.sendMessage("Your participation in the Battle Royale event has been removed.");

                    }

                    player._inEventBR = false;
                }
            }

            _players.clear();

        }

        _inProgress = false;

        loadData();
    }

    /**
     * Spawn event npc.
     */
    private static void spawnEventNpc()
    {
        final L2NpcTemplate tmpl = NpcTable.getInstance().getTemplate(_npcId);

        try
        {
            _npcSpawn = new L2Spawn(tmpl);

            _npcSpawn.setLocx(_npcX);
            _npcSpawn.setLocy(_npcY);
            _npcSpawn.setLocz(_npcZ);
            _npcSpawn.setAmount(1);
            _npcSpawn.setHeading(_npcHeading);
            _npcSpawn.setRespawnDelay(1);

            SpawnTable.getInstance().addNewSpawn(_npcSpawn, false);

            _npcSpawn.init();
            _npcSpawn.getLastSpawn().getStatus().setCurrentHp(999999999);
            _npcSpawn.getLastSpawn().setTitle(_eventName);
           // _npcSpawn.getLastSpawn()._isEventMobTvT = true;
            _npcSpawn.getLastSpawn().isAggressive();
            _npcSpawn.getLastSpawn().decayMe();
            _npcSpawn.getLastSpawn().spawnMe(_npcSpawn.getLastSpawn().getX(), _npcSpawn.getLastSpawn().getY(), _npcSpawn.getLastSpawn().getZ());

            _npcSpawn.getLastSpawn().broadcastPacket(new MagicSkillUser(_npcSpawn.getLastSpawn(), _npcSpawn.getLastSpawn(), 1034, 1, 1, 1));
        }
        catch (final Exception e)
        {
            if (Config.ENABLE_ALL_EXCEPTIONS)
                e.printStackTrace();

            LOGGER.error(_eventName + " Engine[spawnEventNpc(exception: " + e.getMessage());
        }
    }

    /**
     * Unspawn event npc.
     */
    private static void unspawnEventNpc()
    {
        if (_npcSpawn == null || _npcSpawn.getLastSpawn() == null)
            return;

        _npcSpawn.getLastSpawn().deleteMe();
        _npcSpawn.stopRespawn();
        SpawnTable.getInstance().deleteSpawn(_npcSpawn, true);
    }



    private static void spawnBuffer() {

        L2NpcTemplate template1;
        template1 = NpcTable.getInstance().getTemplate(50019);

        try {

            buffer = new L2Spawn(template1);
            buffer.setCustom(false);
            buffer.setLocx(_playerX + 40);
            buffer.setLocy(_playerY + 40);
            buffer.setLocz(_playerZ);
            buffer.setAmount(1);
            buffer.setHeading(0);
            buffer.setRespawnDelay(1);

            SpawnTable.getInstance().addNewSpawn(buffer, false);

            buffer.init();

        } catch (final Exception e) {
            if (Config.ENABLE_ALL_EXCEPTIONS)
                e.printStackTrace();

        }

        template1 = null;

    }

    private static void unspawnBuffer() {

        if (buffer == null || buffer.getLastSpawn() == null)
            return;

        buffer.getLastSpawn().deleteMe();
        buffer.stopRespawn();
        SpawnTable.getInstance().deleteSpawn(buffer, false);
        buffer = null;

    }

    /**
     * Reward player.
     */
    public static void rewardPlayer() {


        if (_players.size() == 1) {
            if (!_players.firstElement().isDead() && _players.firstElement().isOnline() == 1) {
                _players.firstElement().addItem("Battle Royale Event: " + _eventName, _rewardId, _rewardAmount, _players.firstElement(), true);
                Announcements.getInstance().announceToAll("Battle Royale: The last survivor is " + _players.firstElement().getName() + "!");
                final StatusUpdate su = new StatusUpdate(_players.firstElement().getObjectId());
                su.addAttribute(StatusUpdate.CUR_LOAD, _players.firstElement().getCurrentLoad());
                _players.firstElement().sendPacket(su);

                final NpcHtmlMessage nhm = new NpcHtmlMessage(5);
                final TextBuilder replyMSG = new TextBuilder("");

                replyMSG.append("<html><body><center><font color=\"FFFF33\">Congratulations, you won the event.<br>Look in your inventory for the reward.</font></center></body></html>");

                nhm.setHtml(replyMSG.toString());
                _players.firstElement().sendPacket(nhm);

                // Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
                _players.firstElement().sendPacket(ActionFailed.STATIC_PACKET);
            }
        } else {
            Announcements.getInstance().announceToAll("Battle Royale: Noone won the event.");
        }

    }

    /**
     * Gets the _players spawn location.
     *
     * @return the _players spawn location
     */
    public static Location get_playersSpawnLocation() {
        final Location npc_loc = new Location(_playerX + Rnd.get(Config.BR_SPAWN_OFFSET), _playerY + Rnd.get(Config.BR_SPAWN_OFFSET), _playerZ, 0);

        return npc_loc;
    }

    /**
     * Gets the players.
     * @return the players
     */
    /*
     * protected synchronized static Vector<L2PcInstance> getPlayers() { return _players; }
     */


    /**
     * Removes the user data.
     */
    public static void removeUserData() {
        // final Vector<L2PcInstance> players = getPlayers();
        synchronized (_players) {
            for (final L2PcInstance player : _players) {
                player.getAppearance().setNameColor(player._originalNameColorDM);
                player.setTitle(player._originalTitleDM);
                player.setKarma(player._originalKarmaDM);
                player._inEventBR = false;
                player.broadcastUserInfo();
            }
        }

    }

    /**
     * just an announcer to send termination messages.
     */
    public static void sendFinalMessages() {
        if (!_started && !_aborted)
            Announcements.getInstance().gameAnnounceToAll(_eventName + ": Thank you For participating!");
    }

    /**
     * returns the interval between each event.
     *
     * @return the interval between matchs
     */
    public static int getIntervalBetweenMatchs() {
        final long actualTime = System.currentTimeMillis();
        final long totalTime = actualTime + _intervalBetweenMatchs;
        final long interval = totalTime - actualTime;
        final int seconds = (int) (interval / 1000);

        return seconds / 60;
    }

    /**
     * Sets the event start time.
     *
     * @param newTime the new event start time
     */
    public void setEventStartTime(final String newTime) {
        startEventTime = newTime;
    }

    /*
     * (non-Javadoc)
     * @see com.l2jfrozen.gameserver.model.entity.event.manager.EventTask#getEventIdentifier()
     */
    @Override
    public String getEventIdentifier() {
        return _eventName;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        LOGGER.info("BR: Event notification start");
        eventOnceStart();
    }

    /*
     * (non-Javadoc)
     * @see com.l2jfrozen.gameserver.model.entity.event.manager.EventTask#getEventStartTime()
     */
    @Override
    public String getEventStartTime() {
        return startEventTime;
    }

    /**
     * On disconnect.
     *
     * @param player the player
     */
    public static void onDisconnect(final L2PcInstance player) {
        if (player._inEventBR) {
            removePlayer(player);
            player.teleToLocation(81310, 148604, -3495, true);
        }
    }
}
