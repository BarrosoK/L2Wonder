/*
package com.l2jfrozen.gameserver.ai.special;

import com.l2jfrozen.gameserver.model.quest.Quest;



public class Barakiel extends Quest implements Runnable
{
	// Barakiel NpcID
	private static final int BARAKIEL = 25325;
	
	public Barakiel(final int questId, final String name, final String descr)
	{
		super(questId, name, descr);
		
		addEventId(BARAKIEL, Quest.QuestEventType.ON_ATTACK);
	}
	
	@Override
	public void run()
	{
	}
}
*/

package com.l2jfrozen.gameserver.ai.special;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.managers.GrandBossManager;
import com.l2jfrozen.gameserver.model.L2Character;
import com.l2jfrozen.gameserver.model.L2Object;
import com.l2jfrozen.gameserver.model.L2Party;
import com.l2jfrozen.gameserver.model.actor.instance.L2GrandBossInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2WarehouseInstance;
import com.l2jfrozen.gameserver.model.entity.Announcements;
import com.l2jfrozen.gameserver.model.quest.Quest;
import com.l2jfrozen.gameserver.network.serverpackets.CreatureSay;
import com.l2jfrozen.gameserver.network.serverpackets.MagicSkillUser;
import com.l2jfrozen.gameserver.network.serverpackets.PlaySound;
import com.l2jfrozen.gameserver.templates.StatsSet;
import com.l2jfrozen.util.random.Rnd;

import java.util.Collection;

/**
 * Created by Server1 on 7/18/2017.
 */
public class Barakiel extends Quest implements Runnable {


    private static final int BARAKIEL = 25325;

    // BARAKIEL Status Tracking :
    private static final int LIVE = 0; // BARAKIEL is spawned.
    private static final int DEAD = 1; // BARAKIEL has been killed.

    private static boolean attacked = false;
    private static boolean halfHp = false;
    private static String msg1 = "When you kill me your party gets noblesse status.";
    private static String msg2 = "When I kill you I get bonus points and I get SUPER STRONG (OP).";
    private static String msg3 = "I was just joking about (OP) points. But take your noblesse and gtfo.";

    enum Event {

        BARAKIEL_SPAWN

    }


    public Barakiel(final int questId, final String name, final String descr) {
        super(questId, name, descr);

        addEventId(BARAKIEL, Quest.QuestEventType.ON_ATTACK);

        final int[] mobs =
                {
                        BARAKIEL
                };
        for (final int mob : mobs) {
            addEventId(mob, Quest.QuestEventType.ON_KILL);
            addEventId(mob, Quest.QuestEventType.ON_ATTACK);
        }


        final StatsSet info = GrandBossManager.getInstance().getStatsSet(BARAKIEL);

        final Integer status = GrandBossManager.getInstance().getBossStatus(BARAKIEL);

        switch (status) {
            case DEAD: {
                final long temp = info.getLong("respawn_time") - System.currentTimeMillis();
                if (temp > 0) {
                    startQuestTimer("BARAKIEL_SPAWN", temp, null, null);
                } else {
                    final L2GrandBossInstance barakiel = (L2GrandBossInstance) addSpawn(BARAKIEL, 91008, -85904, -2736, 0, false, 0);
                    if (Config.ANNOUNCE_TO_ALL_SPAWN_RB) {
                        Announcements.getInstance().announceToAll("Raid boss " + barakiel.getName() + " spawned in world.");
                    }
                    GrandBossManager.getInstance().setBossStatus(BARAKIEL, LIVE);
                    GrandBossManager.getInstance().addBoss(barakiel);
                    spawnBoss(barakiel);
                }
            }
            break;
            case LIVE: {
                /*
                 * int loc_x = info.getInteger("loc_x"); int loc_y = info.getInteger("loc_y"); int loc_z = info.getInteger("loc_z"); int heading = info.getInteger("heading");
				 */
                final int hp = info.getInteger("currentHP");
                final int mp = info.getInteger("currentMP");
                final L2GrandBossInstance barakiel = (L2GrandBossInstance) addSpawn(BARAKIEL, 91008, -85904, -2736, 0, false, 0);
                if (Config.ANNOUNCE_TO_ALL_SPAWN_RB) {
                    Announcements.getInstance().announceToAll("Raid boss " + barakiel.getName() + " spawned in world.");
                }
                GrandBossManager.getInstance().addBoss(barakiel);
                barakiel.setCurrentHpMp(hp, mp);
                spawnBoss(barakiel);

            }
            break;
            default: {
                final L2GrandBossInstance barakiel = (L2GrandBossInstance) addSpawn(BARAKIEL, 91008, -85904, -2736, 0, false, 0);
                if (Config.ANNOUNCE_TO_ALL_SPAWN_RB) {
                    Announcements.getInstance().announceToAll("Raid boss " + barakiel.getName() + " spawned in world.");
                }
                GrandBossManager.getInstance().setBossStatus(BARAKIEL, LIVE);
                GrandBossManager.getInstance().addBoss(barakiel);
                spawnBoss(barakiel);

            }

        }
    }


    private void spawnBoss(final L2GrandBossInstance npc) {

        startQuestTimer("ACTION", 10000, npc, null, true);


    }


    @Override
    public String onAdvEvent(final String event, final L2NpcInstance npc, final L2PcInstance player) {
        final Barakiel.Event event_enum = Barakiel.Event.valueOf(event);

        switch (event_enum) {
            case BARAKIEL_SPAWN: {

                final L2GrandBossInstance barakiel = (L2GrandBossInstance) addSpawn(BARAKIEL, 91008, -85904, -2736, 0, false, 0);
                if (Config.ANNOUNCE_TO_ALL_SPAWN_RB) {
                    Announcements.getInstance().announceToAll("Raid boss " + barakiel.getName() + " spawned in world.");
                }
                GrandBossManager.getInstance().setBossStatus(BARAKIEL, LIVE);
                GrandBossManager.getInstance().addBoss(barakiel);
                spawnBoss(barakiel);

            }
            break;


            default: {
                LOGGER.info("BARAKIEL: Not defined event: " + event + "!");
            }
        }

        return super.onAdvEvent(event, npc, player);
    }

    @Override
    public String onAttack(final L2NpcInstance npc, final L2PcInstance attacker, final int damage, final boolean isPet)
    {
        if (npc.getNpcId() == BARAKIEL)
        {


            if (!attacked)
            {
                final int objId = npc.getObjectId();
                npc.broadcastPacket(new CreatureSay(objId, 0,"Noblesse" , msg1));
                attacked = true;
            }

            if(!halfHp && npc.getCurrentHp()<npc.getMaxHp()/2){
                final int objId = npc.getObjectId();
                npc.broadcastPacket(new CreatureSay(objId, 0,"Noblesse" , msg2));
                halfHp = true;
            }


        }
        return super.onAttack(npc, attacker, damage, isPet);
    }

    @Override
    public String onKill(final L2NpcInstance npc, final L2PcInstance killer, final boolean isPet) {
        final int npcId = npc.getNpcId();

        final Integer status = GrandBossManager.getInstance().getBossStatus(BARAKIEL);

        if (npcId == BARAKIEL) {

            final int objId = npc.getObjectId();
            npc.broadcastPacket(new CreatureSay(objId, 0, "Barakiel", msg3));
            attacked = false;
            halfHp = false;

            giveNoblesse(killer);


            npc.broadcastPacket(new PlaySound(1, "BS02_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));

            if (!npc.getSpawn().is_customBossInstance()) {
                GrandBossManager.getInstance().setBossStatus(BARAKIEL, DEAD);
                // time is 4 hours
                final long respawnTime = (Config.BARAKIEL_RESP_FIRST + Rnd.get(Config.BARAKIEL_RESP_SECOND)) * 3600000;
                startQuestTimer("BARAKIEL_SPAWN", respawnTime, null, null);


                final StatsSet info = GrandBossManager.getInstance().getStatsSet(BARAKIEL);
                info.set("respawn_time", System.currentTimeMillis() + respawnTime);
                GrandBossManager.getInstance().setStatsSet(BARAKIEL, info);
            }

        } else if (status == LIVE) {

        }
        return super.onKill(npc, killer, isPet);
    }

    public void giveNoblesse(L2PcInstance killer) {


        if (!killer.isNoble()) {
            killer.setNoble(true);
        }
        L2Object target = killer.getTarget();
        killer.setTarget(null);
        killer.setTarget(killer);
        killer.broadcastPacket(new MagicSkillUser(killer, 5103, 1, 1000, 0));
        killer.broadcastUserInfo();
        killer.setTarget(target);


        L2Party pt = killer.getParty();

        if (pt == null) {
            return;
        }

        Collection<L2PcInstance> knowns  = killer.getKnownList().getKnownPlayersInRadius(2000);

        if (killer == null)
            return;


        for (L2PcInstance p : pt.getPartyMembers()) {
            if (p == null) {
                continue;
            }
            if (knowns.contains(p)) {
                if (!p.isNoble()) {
                    p.setNoble(true);
                }
                target = p.getTarget();
                p.setTarget(null);
                p.setTarget(p);
                p.broadcastPacket(new MagicSkillUser(p, 5103, 1, 1000, 0));
                p.broadcastUserInfo();
                p.setTarget(target);

            }
        }

    }


    @Override
    public void run() {

    }
}
