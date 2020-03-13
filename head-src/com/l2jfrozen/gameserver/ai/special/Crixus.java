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
 * Created by MaVeN
 */
public class Crixus extends Quest implements Runnable{


    private static final int CRIXUS = 13048;

    // CRIXUS Status Tracking :
    private static final int LIVE = 0; // CRIXUS is spawned.
    private static final int DEAD = 1; // CRIXUS has been killed.

    private static boolean attacked = false;
    private static boolean halfHp = false;
    private static String msg1 = "I shall be your doom. I'll enjoy watching life flee your body.";
    private static String msg2 = "I must...destroy...you!";
    private static String msg3 = "This is not the end...";

    enum Event
    {

        CRIXUS_SPAWN

    }



    public Crixus(final int questId, final String name, final String descr)
    {
        super(questId, name, descr);

        final int[] mobs =
                {
                        CRIXUS
                };
        for (final int mob : mobs)
        {
            addEventId(mob, Quest.QuestEventType.ON_KILL);
            addEventId(mob, Quest.QuestEventType.ON_ATTACK);
        }


        final StatsSet info = GrandBossManager.getInstance().getStatsSet(CRIXUS);

        final Integer status = GrandBossManager.getInstance().getBossStatus(CRIXUS);

        switch (status)
        {
            case DEAD:
            {
                final long temp = info.getLong("respawn_time") - System.currentTimeMillis();
                if (temp > 0)
                {
                    startQuestTimer("CRIXUS_SPAWN", temp, null, null);
                }
                else
                {
                    final L2GrandBossInstance crixus = (L2GrandBossInstance) addSpawn(CRIXUS, 79635, -55431, -6104, 0, false, 0);
                    if (Config.ANNOUNCE_TO_ALL_SPAWN_RB)
                    {
                        Announcements.getInstance().announceToAll("Raid boss " + crixus.getName() + " spawned in world.");
                    }
                    GrandBossManager.getInstance().setBossStatus(CRIXUS, LIVE);
                    GrandBossManager.getInstance().addBoss(crixus);
                    spawnBoss(crixus);
                }
            }
            break;
            case LIVE:
            {
				/*
				 * int loc_x = info.getInteger("loc_x"); int loc_y = info.getInteger("loc_y"); int loc_z = info.getInteger("loc_z"); int heading = info.getInteger("heading");
				 */
                final int hp = info.getInteger("currentHP");
                final int mp = info.getInteger("currentMP");
                final L2GrandBossInstance crixus = (L2GrandBossInstance) addSpawn(CRIXUS, 79635, -55431, -6104, 0, false, 0);
                if (Config.ANNOUNCE_TO_ALL_SPAWN_RB)
                {
                    Announcements.getInstance().announceToAll("Raid boss " + crixus.getName() + " spawned in world.");
                }
                GrandBossManager.getInstance().addBoss(crixus);
                crixus.setCurrentHpMp(hp, mp);
                spawnBoss(crixus);

            }
            break;
            default:
            {
                final L2GrandBossInstance crixus = (L2GrandBossInstance) addSpawn(CRIXUS, 79635, -55431, -6104, 0, false, 0);
                if (Config.ANNOUNCE_TO_ALL_SPAWN_RB)
                {
                    Announcements.getInstance().announceToAll("Raid boss " + crixus.getName() + " spawned in world.");
                }
                GrandBossManager.getInstance().setBossStatus(CRIXUS, LIVE);
                GrandBossManager.getInstance().addBoss(crixus);
                spawnBoss(crixus);

            }

        }
    }


    private void spawnBoss(final L2GrandBossInstance npc)
    {

            startQuestTimer("ACTION", 10000, npc, null, true);


    }



    @Override
    public String onAttack(final L2NpcInstance npc, final L2PcInstance attacker, final int damage, final boolean isPet)
    {
        if (npc.getNpcId() == CRIXUS)
        {


            if (!attacked)
            {
                final int objId = npc.getObjectId();
                npc.broadcastPacket(new CreatureSay(objId, 0,"Crixus" , msg1));
                attacked = true;
            }

            if(!halfHp && npc.getCurrentHp()<npc.getMaxHp()/2){
                final int objId = npc.getObjectId();
                npc.broadcastPacket(new CreatureSay(objId, 0,"Crixus" , msg2));
                halfHp = true;
            }


        }
        return super.onAttack(npc, attacker, damage, isPet);
    }



    @Override
    public String onAdvEvent(final String event, final L2NpcInstance npc, final L2PcInstance player)
    {
        final Crixus.Event event_enum = Crixus.Event.valueOf(event);

        switch (event_enum)
        {
            case CRIXUS_SPAWN:
            {

                    final L2GrandBossInstance crixus = (L2GrandBossInstance) addSpawn(CRIXUS, 79635, -55431, -6104, 0, false, 0);
                    if (Config.ANNOUNCE_TO_ALL_SPAWN_RB) {
                        Announcements.getInstance().announceToAll("Raid boss " + crixus.getName() + " spawned in world.");
                    }
                    GrandBossManager.getInstance().setBossStatus(CRIXUS, LIVE);
                    GrandBossManager.getInstance().addBoss(crixus);
                    spawnBoss(crixus);

                }
                break;



            default:
            {
                LOGGER.info("CRIXUS: Not defined event: " + event + "!");
            }
        }

        return super.onAdvEvent(event, npc, player);
    }



    @Override
    public String onKill(final L2NpcInstance npc, final L2PcInstance killer, final boolean isPet)
    {
        final int npcId = npc.getNpcId();

        final Integer status = GrandBossManager.getInstance().getBossStatus(CRIXUS);

        if (npcId == CRIXUS)
        {

            final int objId = npc.getObjectId();
            npc.broadcastPacket(new CreatureSay(objId, 0, "Crixus", msg3));
            attacked = false;
            halfHp = false;


            npc.broadcastPacket(new PlaySound(1, "BS02_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));

            if (!npc.getSpawn().is_customBossInstance())
            {
                GrandBossManager.getInstance().setBossStatus(CRIXUS, DEAD);
                // time is 4 hours
                final long respawnTime = (Config.CRIXUS_RESP_FIRST + Rnd.get(Config.CRIXUS_RESP_SECOND)) * 3600000;
                startQuestTimer("CRIXUS_SPAWN", respawnTime, null, null);


                final StatsSet info = GrandBossManager.getInstance().getStatsSet(CRIXUS);
                info.set("respawn_time", System.currentTimeMillis() + respawnTime);
                GrandBossManager.getInstance().setStatsSet(CRIXUS, info);
            }
            if (killer.getClan() != null) {
                Announcements.getInstance().announceToAll("Crixus was killed by " + killer.getName() + " of Clan " + killer.getClan().getName() + ".");
            } else {
                Announcements.getInstance().announceToAll("Crixus was killed by " + killer.getName() + ".");
            }
        }
        else if (status == LIVE)
        {

        }
        return super.onKill(npc, killer, isPet);
    }





    @Override
    public void run() {

    }
}
