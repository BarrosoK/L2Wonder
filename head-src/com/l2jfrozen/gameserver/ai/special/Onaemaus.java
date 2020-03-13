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
public class Onaemaus extends Quest implements Runnable{


    private static final int ONAEMAUS = 660000;

    // ONAEMAUS Status Tracking :
    private static final int LIVE = 0; // ONAEMAUS is spawned.
    private static final int DEAD = 1; // ONAEMAUS has been killed.

    private static boolean attacked = false;
    private static boolean halfHp = false;
    private static boolean lowHp = false;
    private static String msg1 = "Finally, something to do in this forsaken place.";
    private static String msg2 = "You all fight well, gods are proud!";
    private static String extraMsg = "Today it shall be our final lesson!";
    private static String msg3 = "I die proudly to such worthy opponents.";

    enum Event
    {

        ONAEMAUS_SPAWN

    }



    public Onaemaus(final int questId, final String name, final String descr)
    {
        super(questId, name, descr);

        final int[] mobs =
                {
                        ONAEMAUS
                };
        for (final int mob : mobs)
        {
            addEventId(mob, Quest.QuestEventType.ON_KILL);
            addEventId(mob, Quest.QuestEventType.ON_ATTACK);
        }


        final StatsSet info = GrandBossManager.getInstance().getStatsSet(ONAEMAUS);

        final Integer status = GrandBossManager.getInstance().getBossStatus(ONAEMAUS);

        switch (status)
        {
            case DEAD:
            {
                final long temp = info.getLong("respawn_time") - System.currentTimeMillis();
                if (temp > 0)
                {
                    startQuestTimer("ONAEMAUS_SPAWN", temp, null, null);
                }
                else
                {
                    final L2GrandBossInstance onaemaus = (L2GrandBossInstance) addSpawn(ONAEMAUS, 186874, 56342, -4576, 0, false, 0);
                    if (Config.ANNOUNCE_TO_ALL_SPAWN_RB)
                    {
                        Announcements.getInstance().announceToAll("Raid boss " + onaemaus.getName() + " spawned in world.");
                    }
                    GrandBossManager.getInstance().setBossStatus(ONAEMAUS, LIVE);
                    GrandBossManager.getInstance().addBoss(onaemaus);
                    spawnBoss(onaemaus);
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
                final L2GrandBossInstance onaemaus = (L2GrandBossInstance) addSpawn(ONAEMAUS, 186874, 56342, -4576, 0, false, 0);
                if (Config.ANNOUNCE_TO_ALL_SPAWN_RB)
                {
                    Announcements.getInstance().announceToAll("Raid boss " + onaemaus.getName() + " spawned in world.");
                }
                GrandBossManager.getInstance().addBoss(onaemaus);
                onaemaus.setCurrentHpMp(hp, mp);
                spawnBoss(onaemaus);

            }
            break;
            default:
            {
                final L2GrandBossInstance onaemaus = (L2GrandBossInstance) addSpawn(ONAEMAUS,  186874, 56342, -4576, 0, false, 0);
                if (Config.ANNOUNCE_TO_ALL_SPAWN_RB)
                {
                    Announcements.getInstance().announceToAll("Raid boss " + onaemaus.getName() + " spawned in world.");
                }
                GrandBossManager.getInstance().setBossStatus(ONAEMAUS, LIVE);
                GrandBossManager.getInstance().addBoss(onaemaus);
                spawnBoss(onaemaus);

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
        if (npc.getNpcId() == ONAEMAUS)
        {


            if (!attacked)
            {
                final int objId = npc.getObjectId();
                npc.broadcastPacket(new CreatureSay(objId, 0,"Oenomaus" , msg1));
                attacked = true;
            }

            if(!halfHp && npc.getCurrentHp()<npc.getMaxHp()/2){
                final int objId = npc.getObjectId();
                npc.broadcastPacket(new CreatureSay(objId, 0,"Oenomaus" , msg2));
                halfHp = true;
            }else if(!lowHp && npc.getCurrentHp()<npc.getMaxHp()/5){
                final int objId = npc.getObjectId();
                npc.broadcastPacket(new CreatureSay(objId, 0,"Oenomaus" , extraMsg));
                lowHp = true;
            }


        }
        return super.onAttack(npc, attacker, damage, isPet);
    }



    @Override
    public String onAdvEvent(final String event, final L2NpcInstance npc, final L2PcInstance player)
    {
        final Onaemaus.Event event_enum = Onaemaus.Event.valueOf(event);

        switch (event_enum)
        {
            case ONAEMAUS_SPAWN:
            {

                    final L2GrandBossInstance onaemaus = (L2GrandBossInstance) addSpawn(ONAEMAUS,  186874, 56342, -4576, 0, false, 0);
                    if (Config.ANNOUNCE_TO_ALL_SPAWN_RB) {
                        Announcements.getInstance().announceToAll("Raid boss " + onaemaus.getName() + " spawned in world.");
                    }
                    GrandBossManager.getInstance().setBossStatus(ONAEMAUS, LIVE);
                    GrandBossManager.getInstance().addBoss(onaemaus);
                    spawnBoss(onaemaus);

                }
                break;



            default:
            {
                LOGGER.info("ONAEMAUS: Not defined event: " + event + "!");
            }
        }

        return super.onAdvEvent(event, npc, player);
    }



    @Override
    public String onKill(final L2NpcInstance npc, final L2PcInstance killer, final boolean isPet)
    {
        final int npcId = npc.getNpcId();

        final Integer status = GrandBossManager.getInstance().getBossStatus(ONAEMAUS);

        if (npcId == ONAEMAUS)
        {


            final int objId = npc.getObjectId();
            npc.broadcastPacket(new CreatureSay(objId, 0, "Oenomaus", msg3));
            attacked = false;
            halfHp = false;


            npc.broadcastPacket(new PlaySound(1, "BS02_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));

            if (!npc.getSpawn().is_customBossInstance())
            {
                GrandBossManager.getInstance().setBossStatus(ONAEMAUS, DEAD);
                // time is 4 hours
                final long respawnTime = (Config.ONAEMAUS_RESP_FIRST + Rnd.get(Config.ONAEMAUS_RESP_SECOND)) * 3600000;
                startQuestTimer("ONAEMAUS_SPAWN", respawnTime, null, null);

                 //cancelQuestTimer("CHECK_QA_ZONE", npc, null);
                // also save the respawn time so that the info is maintained past reboots
                final StatsSet info = GrandBossManager.getInstance().getStatsSet(ONAEMAUS);
                info.set("respawn_time", System.currentTimeMillis() + respawnTime);
                GrandBossManager.getInstance().setStatsSet(ONAEMAUS, info);
            }
            if (killer.getClan() != null) {
                Announcements.getInstance().announceToAll("Oenomaus was killed by " + killer.getName() + " of Clan " + killer.getClan().getName() + ".");
            } else {
                Announcements.getInstance().announceToAll("Oenomaus was killed by " + killer.getName() + ".");
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
