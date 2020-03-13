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
public class Ashur extends Quest implements Runnable{


    private static final int ASHUR = 13052;

    // ASHUR Status Tracking :
    private static final int LIVE = 0; // ASHUR is spawned.
    private static final int DEAD = 1; // ASHUR has been killed.

    private static boolean attacked = false;
    private static boolean halfHp = false;
    private static String msg1 = "Nobody tricks me, i shall have your heads for this.";
    private static String msg2 = "You might think you have the upper hand, but i am the one who will have the last laugh!";
    private static String msg3 = "You only think you have won.. Watch your backs from now on.";

    enum Event
    {

        ASHUR_SPAWN

    }



    public Ashur(final int questId, final String name, final String descr)
    {
        super(questId, name, descr);

        final int[] mobs =
                {
                        ASHUR
                };
        for (final int mob : mobs)
        {
            addEventId(mob, Quest.QuestEventType.ON_KILL);
            addEventId(mob, Quest.QuestEventType.ON_ATTACK);
        }


        final StatsSet info = GrandBossManager.getInstance().getStatsSet(ASHUR);

        final Integer status = GrandBossManager.getInstance().getBossStatus(ASHUR);

        switch (status)
        {
            case DEAD:
            {
                final long temp = info.getLong("respawn_time") - System.currentTimeMillis();
                if (temp > 0)
                {
                    startQuestTimer("ASHUR_SPAWN", temp, null, null);
                }
                else
                {
                    final L2GrandBossInstance ashur = (L2GrandBossInstance) addSpawn(ASHUR, 105441 , -43256 , -1728, 0, false, 0);
                    if (Config.ANNOUNCE_TO_ALL_SPAWN_RB)
                    {
                        Announcements.getInstance().announceToAll("Raid boss " + ashur.getName() + " spawned in world.");
                    }
                    GrandBossManager.getInstance().setBossStatus(ASHUR, LIVE);
                    GrandBossManager.getInstance().addBoss(ashur);
                    spawnBoss(ashur);
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
                final L2GrandBossInstance ashur = (L2GrandBossInstance) addSpawn(ASHUR, 105441 , -43256 , -1728, 0, false, 0);
                if (Config.ANNOUNCE_TO_ALL_SPAWN_RB)
                {
                    Announcements.getInstance().announceToAll("Raid boss " + ashur.getName() + " spawned in world.");
                }
                GrandBossManager.getInstance().addBoss(ashur);
                ashur.setCurrentHpMp(hp, mp);
                spawnBoss(ashur);

            }
            break;
            default:
            {
                final L2GrandBossInstance ashur = (L2GrandBossInstance) addSpawn(ASHUR, 105441 , -43256 , -1728, 0, false, 0);
                if (Config.ANNOUNCE_TO_ALL_SPAWN_RB)
                {
                    Announcements.getInstance().announceToAll("Raid boss " + ashur.getName() + " spawned in world.");
                }
                GrandBossManager.getInstance().setBossStatus(ASHUR, LIVE);
                GrandBossManager.getInstance().addBoss(ashur);
                spawnBoss(ashur);

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
        if (npc.getNpcId() == ASHUR)
        {


            if (!attacked)
            {
                final int objId = npc.getObjectId();
                npc.broadcastPacket(new CreatureSay(objId, 0,"Ashur" , msg1));
                attacked = true;
            }

            if(!halfHp && npc.getCurrentHp()<npc.getMaxHp()/2){
                final int objId = npc.getObjectId();
                npc.broadcastPacket(new CreatureSay(objId, 0,"Ashur" , msg2));
                halfHp = true;
            }


        }
        return super.onAttack(npc, attacker, damage, isPet);
    }



    @Override
    public String onAdvEvent(final String event, final L2NpcInstance npc, final L2PcInstance player)
    {
        final Ashur.Event event_enum = Ashur.Event.valueOf(event);

        switch (event_enum)
        {
            case ASHUR_SPAWN:
            {

                    final L2GrandBossInstance ashur = (L2GrandBossInstance) addSpawn(ASHUR, 105441 , -43256 , -1728, 0, false, 0);
                    if (Config.ANNOUNCE_TO_ALL_SPAWN_RB) {
                        Announcements.getInstance().announceToAll("Raid boss " + ashur.getName() + " spawned in world.");
                    }
                    GrandBossManager.getInstance().setBossStatus(ASHUR, LIVE);
                    GrandBossManager.getInstance().addBoss(ashur);
                    spawnBoss(ashur);

                }
                break;



            default:
            {
                LOGGER.info("ASHUR: Not defined event: " + event + "!");
            }
        }

        return super.onAdvEvent(event, npc, player);
    }



    @Override
    public String onKill(final L2NpcInstance npc, final L2PcInstance killer, final boolean isPet)
    {
        final int npcId = npc.getNpcId();

        final Integer status = GrandBossManager.getInstance().getBossStatus(ASHUR);

        if (npcId == ASHUR)
        {

            final int objId = npc.getObjectId();
            npc.broadcastPacket(new CreatureSay(objId, 0, "Ashur", msg3));
            attacked = false;
            halfHp = false;


            npc.broadcastPacket(new PlaySound(1, "BS02_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));

            if (!npc.getSpawn().is_customBossInstance())
            {
                GrandBossManager.getInstance().setBossStatus(ASHUR, DEAD);
                // time is 4 hours
                final long respawnTime = (Config.ASHUR_RESP_FIRST + Rnd.get(Config.ASHUR_RESP_SECOND)) * 3600000;
                startQuestTimer("ASHUR_SPAWN", respawnTime, null, null);


                final StatsSet info = GrandBossManager.getInstance().getStatsSet(ASHUR);
                info.set("respawn_time", System.currentTimeMillis() + respawnTime);
                GrandBossManager.getInstance().setStatsSet(ASHUR, info);
            }
            if (killer.getClan() != null) {
                Announcements.getInstance().announceToAll("Ashur was killed by " + killer.getName() + " of Clan " + killer.getClan().getName() + ".");
            } else {
                Announcements.getInstance().announceToAll("Ashur was killed by " + killer.getName() + ".");
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
