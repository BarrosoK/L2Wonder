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
public class Baium extends Quest implements Runnable{


	private static final int BAIUM = 29020;

	// BAIUM Status Tracking :
	private static final int LIVE = 0; // BAIUM is spawned.
	private static final int DEAD = 3; // BAIUM has been killed.

	private static boolean attacked = false;
	private static boolean halfHp = false;
	private static String msg1 = "Finally a chance to redeem myself in the eyes of the gods.";
	private static String msg2 = "I will not give up so easily.";
	private static String msg3 = "And once more I am forever cursed in this tower.";

	enum Event
	{

		BAIUM_SPAWN

	}



	public Baium(final int questId, final String name, final String descr)
	{
		super(questId, name, descr);

		final int[] mobs =
				{
						BAIUM
				};
		for (final int mob : mobs)
		{
			addEventId(mob, Quest.QuestEventType.ON_KILL);
			addEventId(mob, Quest.QuestEventType.ON_ATTACK);
		}


		final StatsSet info = GrandBossManager.getInstance().getStatsSet(BAIUM);

		final Integer status = GrandBossManager.getInstance().getBossStatus(BAIUM);

		switch (status)
		{
			case DEAD:
			{
				final long temp = info.getLong("respawn_time") - System.currentTimeMillis();
				if (temp > 0)
				{
					startQuestTimer("BAIUM_SPAWN", temp, null, null);
				}
				else
				{
					final L2GrandBossInstance baium = (L2GrandBossInstance) addSpawn(BAIUM, 115801, 17211, 10080, 35686, false, 0);
					if (Config.ANNOUNCE_TO_ALL_SPAWN_RB)
					{
						Announcements.getInstance().announceToAll("Raid boss " + baium.getName() + " spawned in world.");
					}
					GrandBossManager.getInstance().setBossStatus(BAIUM, LIVE);
					GrandBossManager.getInstance().addBoss(baium);
					spawnBoss(baium);
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
				final L2GrandBossInstance baium = (L2GrandBossInstance) addSpawn(BAIUM, 115801, 17211, 10080, 35686, false, 0);
				if (Config.ANNOUNCE_TO_ALL_SPAWN_RB)
				{
					Announcements.getInstance().announceToAll("Raid boss " + baium.getName() + " spawned in world.");
				}
				GrandBossManager.getInstance().addBoss(baium);
				baium.setCurrentHpMp(hp, mp);
				spawnBoss(baium);

			}
			break;
			default:
			{
				final L2GrandBossInstance baium = (L2GrandBossInstance) addSpawn(BAIUM, 115801, 17211, 10080, 35686, false, 0);
				if (Config.ANNOUNCE_TO_ALL_SPAWN_RB)
				{
					Announcements.getInstance().announceToAll("Raid boss " + baium.getName() + " spawned in world.");
				}
				GrandBossManager.getInstance().setBossStatus(BAIUM, LIVE);
				GrandBossManager.getInstance().addBoss(baium);
				spawnBoss(baium);

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
		if (npc.getNpcId() == BAIUM)
		{


			if (!attacked)
			{
				final int objId = npc.getObjectId();
				npc.broadcastPacket(new CreatureSay(objId, 0,"Baium" , msg1));
				attacked = true;
			}

			if(!halfHp && npc.getCurrentHp()<npc.getMaxHp()/2){
				final int objId = npc.getObjectId();
				npc.broadcastPacket(new CreatureSay(objId, 0,"Baium" , msg2));
				halfHp = true;
			}


		}
		return super.onAttack(npc, attacker, damage, isPet);
	}




	@Override
	public String onAdvEvent(final String event, final L2NpcInstance npc, final L2PcInstance player)
	{
		final Baium.Event event_enum = Baium.Event.valueOf(event);

		switch (event_enum)
		{
			case BAIUM_SPAWN:
			{

				final L2GrandBossInstance baium = (L2GrandBossInstance) addSpawn(BAIUM, 115801, 17211, 10080, 35686, false, 0);
				if (Config.ANNOUNCE_TO_ALL_SPAWN_RB) {
					Announcements.getInstance().announceToAll("Raid boss " + baium.getName() + " spawned in world.");
				}
				GrandBossManager.getInstance().setBossStatus(BAIUM, LIVE);
				GrandBossManager.getInstance().addBoss(baium);
				spawnBoss(baium);

			}
			break;



			default:
			{
				LOGGER.info("BAIUM: Not defined event: " + event + "!");
			}
		}

		return super.onAdvEvent(event, npc, player);
	}



	@Override
	public String onKill(final L2NpcInstance npc, final L2PcInstance killer, final boolean isPet)
	{






		final int npcId = npc.getNpcId();

		final Integer status = GrandBossManager.getInstance().getBossStatus(BAIUM);

		if (npcId == BAIUM)
		{

			final int objId = npc.getObjectId();
			npc.broadcastPacket(new CreatureSay(objId, 0, "Baium", msg3));
			attacked = false;
			halfHp = false;



			npc.broadcastPacket(new PlaySound(1, "BS02_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));

			if (!npc.getSpawn().is_customBossInstance())
			{
				GrandBossManager.getInstance().setBossStatus(BAIUM, DEAD);
				// time is 4 hours
				final long respawnTime = (Config.BAIUM_RESP_FIRST + Rnd.get(Config.BAIUM_RESP_SECOND)) * 3600000;
				startQuestTimer("BAIUM_SPAWN", respawnTime, null, null);

				//cancelQuestTimer("CHECK_QA_ZONE", npc, null);
				// also save the respawn time so that the info is maintained past reboots
				final StatsSet info = GrandBossManager.getInstance().getStatsSet(BAIUM);
				info.set("respawn_time", System.currentTimeMillis() + respawnTime);
				GrandBossManager.getInstance().setStatsSet(BAIUM, info);
			}
			if (killer.getClan() != null) {
				Announcements.getInstance().announceToAll("Baium was killed by " + killer.getName() + " of Clan " + killer.getClan().getName() + ".");
			} else {
				Announcements.getInstance().announceToAll("Baium was killed by " + killer.getName() + ".");
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
