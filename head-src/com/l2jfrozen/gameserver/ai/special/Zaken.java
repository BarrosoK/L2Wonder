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
 * Created by Server1 on 7/18/2017.
 */
public class Zaken extends Quest implements Runnable{


	private static final int ZAKEN = 29022;

	// ZAKEN Status Tracking :
	private static final int LIVE = 0; // ZAKEN is spawned.
	private static final int DEAD = 3; // ZAKEN has been killed.

	private static boolean attacked = false;
	private static boolean halfHp = false;
	private static String msg1 = "Nobody steals from a pirate and lives to tell the tale!";
	private static String msg2 = "I'll protect my loot with my life!";
	private static String msg3 = "I have been defeated. My treasure is now yours....";


	enum Event
	{

		ZAKEN_SPAWN

	}



	public Zaken(final int questId, final String name, final String descr)
	{
		super(questId, name, descr);

		final int[] mobs =
				{
						ZAKEN
				};
		for (final int mob : mobs)
		{
			addEventId(mob, Quest.QuestEventType.ON_KILL);
			addEventId(mob, Quest.QuestEventType.ON_ATTACK);
		}


		final StatsSet info = GrandBossManager.getInstance().getStatsSet(ZAKEN);

		final Integer status = GrandBossManager.getInstance().getBossStatus(ZAKEN);

		switch (status)
		{
			case DEAD:
			{
				final long temp = info.getLong("respawn_time") - System.currentTimeMillis();
				if (temp > 0)
				{
					startQuestTimer("ZAKEN_SPAWN", temp, null, null);
				}
				else
				{
					final L2GrandBossInstance zaken = (L2GrandBossInstance) addSpawn(ZAKEN, 55312, 219168, -3223, 35686, false, 0);
					if (Config.ANNOUNCE_TO_ALL_SPAWN_RB)
					{
						Announcements.getInstance().announceToAll("Raid boss " + zaken.getName() + " spawned in world.");
					}
					GrandBossManager.getInstance().setBossStatus(ZAKEN, LIVE);
					GrandBossManager.getInstance().addBoss(zaken);
					spawnBoss(zaken);
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
				final L2GrandBossInstance zaken = (L2GrandBossInstance) addSpawn(ZAKEN, 55312, 219168, -3223, 35686, false, 0);
				if (Config.ANNOUNCE_TO_ALL_SPAWN_RB)
				{
					Announcements.getInstance().announceToAll("Raid boss " + zaken.getName() + " spawned in world.");
				}
				GrandBossManager.getInstance().addBoss(zaken);
				zaken.setCurrentHpMp(hp, mp);
				spawnBoss(zaken);

			}
			break;
			default:
			{
				final L2GrandBossInstance zaken = (L2GrandBossInstance) addSpawn(ZAKEN, 55312, 219168, -3223, 35686, false, 0);
				if (Config.ANNOUNCE_TO_ALL_SPAWN_RB)
				{
					Announcements.getInstance().announceToAll("Raid boss " + zaken.getName() + " spawned in world.");
				}
				GrandBossManager.getInstance().setBossStatus(ZAKEN, LIVE);
				GrandBossManager.getInstance().addBoss(zaken);
				spawnBoss(zaken);

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
		if (npc.getNpcId() == ZAKEN)
		{


			if (!attacked)
			{
				final int objId = npc.getObjectId();
				npc.broadcastPacket(new CreatureSay(objId, 0,"Zaken" , msg1));
				attacked = true;
			}

			if(!halfHp && npc.getCurrentHp()<npc.getMaxHp()/2){
				final int objId = npc.getObjectId();
				npc.broadcastPacket(new CreatureSay(objId, 0,"Zaken" , msg2));
				halfHp = true;
			}


		}
		return super.onAttack(npc, attacker, damage, isPet);
	}




	@Override
	public String onAdvEvent(final String event, final L2NpcInstance npc, final L2PcInstance player)
	{
		final Zaken.Event event_enum = Zaken.Event.valueOf(event);

		switch (event_enum)
		{
			case ZAKEN_SPAWN:
			{

				final L2GrandBossInstance zaken = (L2GrandBossInstance) addSpawn(ZAKEN, 55312, 219168, -3223, 35686, false, 0);
				if (Config.ANNOUNCE_TO_ALL_SPAWN_RB) {
					Announcements.getInstance().announceToAll("Raid boss " + zaken.getName() + " spawned in world.");
				}
				GrandBossManager.getInstance().setBossStatus(ZAKEN, LIVE);
				GrandBossManager.getInstance().addBoss(zaken);
				spawnBoss(zaken);

			}
			break;



			default:
			{
				LOGGER.info("ZAKEN: Not defined event: " + event + "!");
			}
		}

		return super.onAdvEvent(event, npc, player);
	}



	@Override
	public String onKill(final L2NpcInstance npc, final L2PcInstance killer, final boolean isPet)
	{
		final int npcId = npc.getNpcId();

		final Integer status = GrandBossManager.getInstance().getBossStatus(ZAKEN);

		if (npcId == ZAKEN)
		{
			final int objId = npc.getObjectId();
			npc.broadcastPacket(new CreatureSay(objId, 0, "Zaken", msg3));
			attacked = false;
			halfHp = false;



			npc.broadcastPacket(new PlaySound(1, "BS02_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));

			if (!npc.getSpawn().is_customBossInstance())
			{
				GrandBossManager.getInstance().setBossStatus(ZAKEN, DEAD);
				// time is 4 hours
				final long respawnTime = (Config.ZAKEN_RESP_FIRST + Rnd.get(Config.ZAKEN_RESP_SECOND)) * 3600000;
				startQuestTimer("ZAKEN_SPAWN", respawnTime, null, null);

				//cancelQuestTimer("CHECK_QA_ZONE", npc, null);
				// also save the respawn time so that the info is maintained past reboots
				final StatsSet info = GrandBossManager.getInstance().getStatsSet(ZAKEN);
				info.set("respawn_time", System.currentTimeMillis() + respawnTime);
				GrandBossManager.getInstance().setStatsSet(ZAKEN, info);
			}
			if (killer.getClan() != null) {
				Announcements.getInstance().announceToAll("Zaken was killed by " + killer.getName() + " of Clan " + killer.getClan().getName() + ".");
			} else {
				Announcements.getInstance().announceToAll("Zaken was killed by " + killer.getName() + ".");
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


