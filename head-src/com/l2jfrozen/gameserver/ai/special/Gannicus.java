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
public class Gannicus extends Quest implements Runnable{


    private static final int GANNICUS = 13050;

    // GANNICUS Status Tracking :
    private static final int LIVE = 0; // GANNICUS is spawned.
    private static final int DEAD = 1; // GANNICUS has been killed.

    private static boolean attacked = false;
    private static boolean halfHp = false;
    private static String msg1 = "What is the meaning of this!?";
    private static String msg2 = "You have all betrayed me.";
    private static String msg3 = "I shall not forgive and i shan't forget.";

    enum Event
    {

        GANNICUS_SPAWN

    }



    public Gannicus(final int questId, final String name, final String descr)
    {
        super(questId, name, descr);

        final int[] mobs =
                {
                        GANNICUS
                };
        for (final int mob : mobs)
        {
            addEventId(mob, Quest.QuestEventType.ON_KILL);
            addEventId(mob, Quest.QuestEventType.ON_ATTACK);
        }


        final StatsSet info = GrandBossManager.getInstance().getStatsSet(GANNICUS);

        final Integer status = GrandBossManager.getInstance().getBossStatus(GANNICUS);

        switch (status)
        {
            case DEAD:
            {
                final long temp = info.getLong("respawn_time") - System.currentTimeMillis();
                if (temp > 0)
                {
                    startQuestTimer("GANNICUS_SPAWN", temp, null, null);
                }
                else
                {
                    final L2GrandBossInstance gannicus = (L2GrandBossInstance) addSpawn(GANNICUS, 39994, -110534, -1464, 0, false, 0);
                    if (Config.ANNOUNCE_TO_ALL_SPAWN_RB)
                    {
                        Announcements.getInstance().announceToAll("Raid boss " + gannicus.getName() + " spawned in world.");
                    }
                    GrandBossManager.getInstance().setBossStatus(GANNICUS, LIVE);
                    GrandBossManager.getInstance().addBoss(gannicus);
                    spawnBoss(gannicus);
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
                final L2GrandBossInstance gannicus = (L2GrandBossInstance) addSpawn(GANNICUS, 39994, -110534, -1464, 0, false, 0);
                if (Config.ANNOUNCE_TO_ALL_SPAWN_RB)
                {
                    Announcements.getInstance().announceToAll("Raid boss " + gannicus.getName() + " spawned in world.");
                }
                GrandBossManager.getInstance().addBoss(gannicus);
                gannicus.setCurrentHpMp(hp, mp);
                spawnBoss(gannicus);

            }
            break;
            default:
            {
                final L2GrandBossInstance gannicus = (L2GrandBossInstance) addSpawn(GANNICUS, 39994, -110534, -1464, 0, false, 0);
                if (Config.ANNOUNCE_TO_ALL_SPAWN_RB)
                {
                    Announcements.getInstance().announceToAll("Raid boss " + gannicus.getName() + " spawned in world.");
                }
                GrandBossManager.getInstance().setBossStatus(GANNICUS, LIVE);
                GrandBossManager.getInstance().addBoss(gannicus);
                spawnBoss(gannicus);

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
        if (npc.getNpcId() == GANNICUS)
        {


            if (!attacked)
            {
                final int objId = npc.getObjectId();
                npc.broadcastPacket(new CreatureSay(objId, 0,"Gannicus" , msg1));
                attacked = true;
            }

            if(!halfHp && npc.getCurrentHp()<npc.getMaxHp()/2){
                final int objId = npc.getObjectId();
                npc.broadcastPacket(new CreatureSay(objId, 0,"Gannicus" , msg2));
                halfHp = true;
            }


        }
        return super.onAttack(npc, attacker, damage, isPet);
    }



    @Override
    public String onAdvEvent(final String event, final L2NpcInstance npc, final L2PcInstance player)
    {
        final Gannicus.Event event_enum = Gannicus.Event.valueOf(event);

        switch (event_enum)
        {
            case GANNICUS_SPAWN:
            {

                    final L2GrandBossInstance gannicus = (L2GrandBossInstance) addSpawn(GANNICUS, 39994, -110534, -1464, 0, false, 0);
                    if (Config.ANNOUNCE_TO_ALL_SPAWN_RB) {
                        Announcements.getInstance().announceToAll("Raid boss " + gannicus.getName() + " spawned in world.");
                    }
                    GrandBossManager.getInstance().setBossStatus(GANNICUS, LIVE);
                    GrandBossManager.getInstance().addBoss(gannicus);
                    spawnBoss(gannicus);

                }
                break;



            default:
            {
                LOGGER.info("GANNICUS: Not defined event: " + event + "!");
            }
        }

        return super.onAdvEvent(event, npc, player);
    }



    @Override
    public String onKill(final L2NpcInstance npc, final L2PcInstance killer, final boolean isPet)
    {
        final int npcId = npc.getNpcId();

        final Integer status = GrandBossManager.getInstance().getBossStatus(GANNICUS);

        if (npcId == GANNICUS)
        {

            final int objId = npc.getObjectId();
            npc.broadcastPacket(new CreatureSay(objId, 0, "Gannicus", msg3));
            attacked = false;
            halfHp = false;


            npc.broadcastPacket(new PlaySound(1, "BS02_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));

            if (!npc.getSpawn().is_customBossInstance())
            {
                GrandBossManager.getInstance().setBossStatus(GANNICUS, DEAD);
                // time is 4 hours
                final long respawnTime = (Config.GANNICUS_RESP_FIRST + Rnd.get(Config.GANNICUS_RESP_SECOND)) * 3600000;
                startQuestTimer("GANNICUS_SPAWN", respawnTime, null, null);


                final StatsSet info = GrandBossManager.getInstance().getStatsSet(GANNICUS);
                info.set("respawn_time", System.currentTimeMillis() + respawnTime);
                GrandBossManager.getInstance().setStatsSet(GANNICUS, info);
            }
            if (killer.getClan() != null) {
                Announcements.getInstance().announceToAll("Gannicus was killed by " + killer.getName() + " of Clan " + killer.getClan().getName() + ".");
            } else {
                Announcements.getInstance().announceToAll("Gannicus was killed by " + killer.getName() + ".");
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
