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
 * Created by MaVeN on 3/17/2018.
 */
public class Agron extends Quest implements Runnable{


    private static final int AGRON = 13051;

    // AGRON Status Tracking :
    private static final int LIVE = 0; // AGRON is spawned.
    private static final int DEAD = 1; // AGRON has been killed.

    private static boolean attacked = false;
    private static boolean halfHp = false;
    private static String msg1 = "Hello my brothers, let's make this one hell of a fight!";
    private static String msg2 = "Ha-ha, we shall have a feast after this glory fight!";
    private static String msg3 = "Well fought my brothers and sisters we shall meet again in the after life";

    enum Event
    {

        AGRON_SPAWN

    }



    public Agron(final int questId, final String name, final String descr)
    {
        super(questId, name, descr);

        final int[] mobs =
                {
                        AGRON
                };
        for (final int mob : mobs)
        {
            addEventId(mob, Quest.QuestEventType.ON_KILL);
            addEventId(mob, Quest.QuestEventType.ON_ATTACK);
        }


        final StatsSet info = GrandBossManager.getInstance().getStatsSet(AGRON);

        final Integer status = GrandBossManager.getInstance().getBossStatus(AGRON);

        switch (status)
        {
            case DEAD:
            {
                final long temp = info.getLong("respawn_time") - System.currentTimeMillis();
                if (temp > 0)
                {
                    startQuestTimer("AGRON_SPAWN", temp, null, null);
                }
                else
                {
                    final L2GrandBossInstance agron = (L2GrandBossInstance) addSpawn(AGRON, 148071 , -73207 , -4935, 0, false, 0);
                    if (Config.ANNOUNCE_TO_ALL_SPAWN_RB)
                    {
                        Announcements.getInstance().announceToAll("Raid boss " + agron.getName() + " spawned in world.");
                    }
                    GrandBossManager.getInstance().setBossStatus(AGRON, LIVE);
                    GrandBossManager.getInstance().addBoss(agron);
                    spawnBoss(agron);
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
                final L2GrandBossInstance agron = (L2GrandBossInstance) addSpawn(AGRON, 148071 , -73207 , -4935, 0, false, 0);
                if (Config.ANNOUNCE_TO_ALL_SPAWN_RB)
                {
                    Announcements.getInstance().announceToAll("Raid boss " + agron.getName() + " spawned in world.");
                }
                GrandBossManager.getInstance().addBoss(agron);
                agron.setCurrentHpMp(hp, mp);
                spawnBoss(agron);

            }
            break;
            default:
            {
                final L2GrandBossInstance agron = (L2GrandBossInstance) addSpawn(AGRON, 148071 , -73207 , -4935, 0, false, 0);
                if (Config.ANNOUNCE_TO_ALL_SPAWN_RB)
                {
                    Announcements.getInstance().announceToAll("Raid boss " + agron.getName() + " spawned in world.");
                }
                GrandBossManager.getInstance().setBossStatus(AGRON, LIVE);
                GrandBossManager.getInstance().addBoss(agron);
                spawnBoss(agron);

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
        if (npc.getNpcId() == AGRON)
        {


            if (!attacked)
            {
                final int objId = npc.getObjectId();
                npc.broadcastPacket(new CreatureSay(objId, 0,"Agron" , msg1));
                attacked = true;
            }

            if(!halfHp && npc.getCurrentHp()<npc.getMaxHp()/2){
                final int objId = npc.getObjectId();
                npc.broadcastPacket(new CreatureSay(objId, 0,"Agron" , msg2));
                halfHp = true;
            }


        }
        return super.onAttack(npc, attacker, damage, isPet);
    }



    @Override
    public String onAdvEvent(final String event, final L2NpcInstance npc, final L2PcInstance player)
    {
        final Agron.Event event_enum = Agron.Event.valueOf(event);

        switch (event_enum)
        {
            case AGRON_SPAWN:
            {

                    final L2GrandBossInstance agron = (L2GrandBossInstance) addSpawn(AGRON, 148071 , -73207 , -4935, 0, false, 0);
                    if (Config.ANNOUNCE_TO_ALL_SPAWN_RB) {
                        Announcements.getInstance().announceToAll("Raid boss " + agron.getName() + " spawned in world.");
                    }
                    GrandBossManager.getInstance().setBossStatus(AGRON, LIVE);
                    GrandBossManager.getInstance().addBoss(agron);
                    spawnBoss(agron);

                }
                break;



            default:
            {
                LOGGER.info("AGRON: Not defined event: " + event + "!");
            }
        }

        return super.onAdvEvent(event, npc, player);
    }



    @Override
    public String onKill(final L2NpcInstance npc, final L2PcInstance killer, final boolean isPet)
    {
        final int npcId = npc.getNpcId();

        final Integer status = GrandBossManager.getInstance().getBossStatus(AGRON);

        if (npcId == AGRON)
        {

            final int objId = npc.getObjectId();
            npc.broadcastPacket(new CreatureSay(objId, 0, "Agron", msg3));
            attacked = false;
            halfHp = false;


            npc.broadcastPacket(new PlaySound(1, "BS02_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));

            if (!npc.getSpawn().is_customBossInstance())
            {
                GrandBossManager.getInstance().setBossStatus(AGRON, DEAD);
                // time is 4 hours
                final long respawnTime = (Config.AGRON_RESP_FIRST + Rnd.get(Config.AGRON_RESP_SECOND)) * 3600000;
                startQuestTimer("AGRON_SPAWN", respawnTime, null, null);


                final StatsSet info = GrandBossManager.getInstance().getStatsSet(AGRON);
                info.set("respawn_time", System.currentTimeMillis() + respawnTime);
                GrandBossManager.getInstance().setStatsSet(AGRON, info);
            }
            if (killer.getClan() != null) {
                Announcements.getInstance().announceToAll("Agron was killed by " + killer.getName() + " of Clan " + killer.getClan().getName() + ".");
            } else {
                Announcements.getInstance().announceToAll("Agron was killed by " + killer.getName() + ".");
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
