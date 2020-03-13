package com.l2jfrozen.gameserver.ai.special;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.managers.GrandBossManager;
import com.l2jfrozen.gameserver.model.actor.instance.L2GrandBossInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.entity.Announcements;
import com.l2jfrozen.gameserver.model.quest.Quest;
import com.l2jfrozen.gameserver.network.serverpackets.CreatureSay;
import com.l2jfrozen.gameserver.network.serverpackets.PlaySound;
import com.l2jfrozen.gameserver.templates.StatsSet;
import com.l2jfrozen.util.random.Rnd;

/**
 * Created by MaVeN on 7/18/2017.
 */
public class Spartakus extends Quest implements Runnable {

    private static boolean attacked = false;
    private static boolean halfHp = false;
    private static String msg1 = "Have you come to prove yourselves!?";
    private static String msg2 = "You fight well for ordinary mortals.";
    private static String msg3 = "Defeated by mere mortals...";


    private static final int SPARTAKUS = 13047;

    // SPARTAKUS Status Tracking :
    private static final int LIVE = 0; // SPARTAKUS is spawned.
    private static final int DEAD = 1; // SPARTAKUS has been killed.


    enum Event {

        SPARTAKUS_SPAWN

    }


    public Spartakus(final int questId, final String name, final String descr) {
        super(questId, name, descr);

        final int[] mobs =
                {
                        SPARTAKUS
                };
        for (final int mob : mobs) {
            addEventId(mob, Quest.QuestEventType.ON_KILL);
            addEventId(mob, Quest.QuestEventType.ON_ATTACK);
        }


        final StatsSet info = GrandBossManager.getInstance().getStatsSet(SPARTAKUS);

        final Integer status = GrandBossManager.getInstance().getBossStatus(SPARTAKUS);

        switch (status) {
            case DEAD: {
                final long temp = info.getLong("respawn_time") - System.currentTimeMillis();
                if (temp > 0) {
                    startQuestTimer("SPARTAKUS_SPAWN", temp, null, null);
                } else {
                    final L2GrandBossInstance spartakus = (L2GrandBossInstance) addSpawn(SPARTAKUS, 12296, -49278, -3008, 0, false, 0);
                    if (Config.ANNOUNCE_TO_ALL_SPAWN_RB) {
                        Announcements.getInstance().announceToAll("Raid boss " + spartakus.getName() + " spawned in world.");
                    }
                    GrandBossManager.getInstance().setBossStatus(SPARTAKUS, LIVE);
                    GrandBossManager.getInstance().addBoss(spartakus);
                    spawnBoss(spartakus);
                }
            }
            break;
            case LIVE: {
                /*
                 * int loc_x = info.getInteger("loc_x"); int loc_y = info.getInteger("loc_y"); int loc_z = info.getInteger("loc_z"); int heading = info.getInteger("heading");
                 */
                final int hp = info.getInteger("currentHP");
                final int mp = info.getInteger("currentMP");
                final L2GrandBossInstance spartakus = (L2GrandBossInstance) addSpawn(SPARTAKUS, 12296, -49278, -3008, 0, false, 0);
                if (Config.ANNOUNCE_TO_ALL_SPAWN_RB) {
                    Announcements.getInstance().announceToAll("Raid boss " + spartakus.getName() + " spawned in world.");
                }
                GrandBossManager.getInstance().addBoss(spartakus);
                spartakus.setCurrentHpMp(hp, mp);
                spawnBoss(spartakus);

            }
            break;
            default: {
                final L2GrandBossInstance spartakus = (L2GrandBossInstance) addSpawn(SPARTAKUS, 12296, -49278, -3008, 0, false, 0);
                if (Config.ANNOUNCE_TO_ALL_SPAWN_RB) {
                    Announcements.getInstance().announceToAll("Raid boss " + spartakus.getName() + " spawned in world.");
                }
                GrandBossManager.getInstance().setBossStatus(SPARTAKUS, LIVE);
                GrandBossManager.getInstance().addBoss(spartakus);
                spawnBoss(spartakus);

            }

        }
    }


    private void spawnBoss(final L2GrandBossInstance npc) {

        startQuestTimer("ACTION", 10000, npc, null, true);


    }

    @Override
    public String onAttack(final L2NpcInstance npc, final L2PcInstance attacker, final int damage, final boolean isPet)
    {
        if (npc.getNpcId() == SPARTAKUS)
        {


            if (!attacked)
            {
                final int objId = npc.getObjectId();
                npc.broadcastPacket(new CreatureSay(objId, 0,"Spartakus" , msg1));
                attacked = true;
            }

            if(!halfHp && npc.getCurrentHp()<npc.getMaxHp()/2){
                final int objId = npc.getObjectId();
                npc.broadcastPacket(new CreatureSay(objId, 0,"Spartakus" , msg2));
                halfHp = true;
            }


        }
        return super.onAttack(npc, attacker, damage, isPet);
    }


    @Override
    public String onAdvEvent(final String event, final L2NpcInstance npc, final L2PcInstance player) {
        final Spartakus.Event event_enum = Spartakus.Event.valueOf(event);

        switch (event_enum) {
            case SPARTAKUS_SPAWN: {

                final L2GrandBossInstance spartakus = (L2GrandBossInstance) addSpawn(SPARTAKUS, 12296, -49278, -3008, 0, false, 0);
                if (Config.ANNOUNCE_TO_ALL_SPAWN_RB) {
                    Announcements.getInstance().announceToAll("Raid boss " + spartakus.getName() + " spawned in world.");
                }
                GrandBossManager.getInstance().setBossStatus(SPARTAKUS, LIVE);
                GrandBossManager.getInstance().addBoss(spartakus);
                spawnBoss(spartakus);

            }
            break;


            default: {
                LOGGER.info("SPARTAKUS: Not defined event: " + event + "!");
            }
        }

        return super.onAdvEvent(event, npc, player);
    }


    @Override
    public String onKill(final L2NpcInstance npc, final L2PcInstance killer, final boolean isPet) {
        final int npcId = npc.getNpcId();

        final Integer status = GrandBossManager.getInstance().getBossStatus(SPARTAKUS);

        if (npcId == SPARTAKUS) {



            final int objId = npc.getObjectId();
            npc.broadcastPacket(new CreatureSay(objId, 0, "Spartakus", msg3));
            attacked = false;
            halfHp = false;



            npc.broadcastPacket(new PlaySound(1, "BS02_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));

            if (!npc.getSpawn().is_customBossInstance()) {
                GrandBossManager.getInstance().setBossStatus(SPARTAKUS, DEAD);
                // time is 4 hours
                final long respawnTime = (Config.SPARTAKUS_RESP_FIRST + Rnd.get(Config.SPARTAKUS_RESP_SECOND)) * 3600000;
                startQuestTimer("SPARTAKUS_SPAWN", respawnTime, null, null);

                //cancelQuestTimer("CHECK_QA_ZONE", npc, null);
                // also save the respawn time so that the info is maintained past reboots
                final StatsSet info = GrandBossManager.getInstance().getStatsSet(SPARTAKUS);
                info.set("respawn_time", System.currentTimeMillis() + respawnTime);
                GrandBossManager.getInstance().setStatsSet(SPARTAKUS, info);
            }
            if (killer.getClan() != null) {
                Announcements.getInstance().announceToAll("Spartakus was killed by " + killer.getName() + " of Clan " + killer.getClan().getName() + ".");
            } else {
                Announcements.getInstance().announceToAll("Spartakus was killed by " + killer.getName() + ".");
            }
        } else if (status == LIVE) {

        }
        return super.onKill(npc, killer, isPet);
    }


    @Override
    public void run() {

    }
}
