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
public class Valakas extends Quest implements Runnable{


	private static final int VALAKAS = 29028;

	// VALAKAS Status Tracking :
	private static final int LIVE = 0; // VALAKAS is spawned.
	private static final int DEAD = 3; // VALAKAS has been killed.

	private static boolean attacked = false;
	private static boolean halfHp = false;
	private static String msg1 = "How dare you strike the GOD of dragons. I shall have your lives for this!";
	private static String msg2 = "I will live FOREVER!";
	private static String msg3 = "I curse you for all eternity!";

	enum Event
	{

		VALAKAS_SPAWN

	}

	public Valakas(final int questId, final String name, final String descr)
	{
		super(questId, name, descr);

		final int[] mobs =
				{
						VALAKAS
				};
		for (final int mob : mobs)
		{
			addEventId(mob, Quest.QuestEventType.ON_KILL);
			addEventId(mob, Quest.QuestEventType.ON_ATTACK);
		}

		final StatsSet info = GrandBossManager.getInstance().getStatsSet(VALAKAS);

		final Integer status = GrandBossManager.getInstance().getBossStatus(VALAKAS);

		switch (status)
		{
			case DEAD:
			{
				final long temp = info.getLong("respawn_time") - System.currentTimeMillis();
				if (temp > 0)
				{
					startQuestTimer("VALAKAS_SPAWN", temp, null, null);
				}
				else
				{
					final L2GrandBossInstance valakas = (L2GrandBossInstance) addSpawn(VALAKAS, 213004, -114890, -1595, 0, false, 0);
					if (Config.ANNOUNCE_TO_ALL_SPAWN_RB)
					{
						Announcements.getInstance().announceToAll("Raid boss " + valakas.getName() + " spawned in world.");
					}
					GrandBossManager.getInstance().setBossStatus(VALAKAS, LIVE);
					GrandBossManager.getInstance().addBoss(valakas);
					spawnBoss(valakas);
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
				final L2GrandBossInstance valakas = (L2GrandBossInstance) addSpawn(VALAKAS, 213004, -114890, -1595, 0, false, 0);
				if (Config.ANNOUNCE_TO_ALL_SPAWN_RB)
				{
					Announcements.getInstance().announceToAll("Raid boss " + valakas.getName() + " spawned in world.");
				}
				GrandBossManager.getInstance().addBoss(valakas);
				valakas.setCurrentHpMp(hp, mp);
				spawnBoss(valakas);

			}
			break;
			default:
			{
				final L2GrandBossInstance valakas = (L2GrandBossInstance) addSpawn(VALAKAS, 213004, -114890, -1595, 0, false, 0);
				if (Config.ANNOUNCE_TO_ALL_SPAWN_RB)
				{
					Announcements.getInstance().announceToAll("Raid boss " + valakas.getName() + " spawned in world.");
				}
				GrandBossManager.getInstance().setBossStatus(VALAKAS, LIVE);
				GrandBossManager.getInstance().addBoss(valakas);
				spawnBoss(valakas);

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
		if (npc.getNpcId() == VALAKAS)
		{


			if (!attacked)
			{
				final int objId = npc.getObjectId();
				npc.broadcastPacket(new CreatureSay(objId, 0,"Valakas" , msg1));
				attacked = true;
			}

			if(!halfHp && npc.getCurrentHp()<npc.getMaxHp()/2){
				final int objId = npc.getObjectId();
				npc.broadcastPacket(new CreatureSay(objId, 0,"Valakas" , msg2));
				halfHp = true;
			}


		}
		return super.onAttack(npc, attacker, damage, isPet);
	}

	@Override
	public String onAdvEvent(final String event, final L2NpcInstance npc, final L2PcInstance player)
	{
		final Valakas.Event event_enum = Valakas.Event.valueOf(event);

		switch (event_enum)
		{
			case VALAKAS_SPAWN:
			{

				final L2GrandBossInstance valakas = (L2GrandBossInstance) addSpawn(VALAKAS, 213004, -114890, -1595, 0, false, 0);
				if (Config.ANNOUNCE_TO_ALL_SPAWN_RB) {
					Announcements.getInstance().announceToAll("Raid boss " + valakas.getName() + " spawned in world.");
				}
				GrandBossManager.getInstance().setBossStatus(VALAKAS, LIVE);
				GrandBossManager.getInstance().addBoss(valakas);
				spawnBoss(valakas);

			}
			break;



			default:
			{
				LOGGER.info("VALAKAS: Not defined event: " + event + "!");
			}
		}

		return super.onAdvEvent(event, npc, player);
	}

	@Override
	public String onKill(final L2NpcInstance npc, final L2PcInstance killer, final boolean isPet)
	{
		final int npcId = npc.getNpcId();

		final Integer status = GrandBossManager.getInstance().getBossStatus(VALAKAS);

		if (npcId == VALAKAS)
		{

			final int objId = npc.getObjectId();
			npc.broadcastPacket(new CreatureSay(objId, 0, "Valakas", msg3));
			attacked = false;
			halfHp = false;

			npc.broadcastPacket(new PlaySound(1, "BS02_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));

			if (!npc.getSpawn().is_customBossInstance())
			{
				GrandBossManager.getInstance().setBossStatus(VALAKAS, DEAD);
				// time is 4 hours
				final long respawnTime = (Config.VALAKAS_RESP_FIRST + Rnd.get(Config.VALAKAS_RESP_SECOND)) * 3600000;
				startQuestTimer("VALAKAS_SPAWN", respawnTime, null, null);

				//cancelQuestTimer("CHECK_QA_ZONE", npc, null);
				// also save the respawn time so that the info is maintained past reboots
				final StatsSet info = GrandBossManager.getInstance().getStatsSet(VALAKAS);
				info.set("respawn_time", System.currentTimeMillis() + respawnTime);
				GrandBossManager.getInstance().setStatsSet(VALAKAS, info);
			}
			if (killer.getClan() != null) {
				Announcements.getInstance().announceToAll("Valakas was killed by " + killer.getName() + " of Clan " + killer.getClan().getName() + ".");
			} else {
				Announcements.getInstance().announceToAll("Valakas was killed by " + killer.getName() + ".");
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
