package com.l2jfrozen.gameserver.ai.special;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.ai.CtrlIntention;
import com.l2jfrozen.gameserver.datatables.SkillTable;
import com.l2jfrozen.gameserver.managers.GrandBossManager;
import com.l2jfrozen.gameserver.model.L2Attackable;
import com.l2jfrozen.gameserver.model.actor.instance.L2GrandBossInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.entity.Announcements;
import com.l2jfrozen.gameserver.model.quest.Quest;
import com.l2jfrozen.gameserver.network.serverpackets.CreatureSay;
import com.l2jfrozen.gameserver.network.serverpackets.PlaySound;
import com.l2jfrozen.gameserver.templates.StatsSet;
import com.l2jfrozen.util.random.Rnd;
import javolution.util.FastList;

import java.util.List;

/**
 * Created by Server1 on 7/18/2017.
 */
public class Marguerite extends Quest implements Runnable{


	private static final int MARGUERITE = 680000;
	private static final int JOSEPINE = 680001;
	private L2MonsterInstance _marguerite = null;

	private final List<L2MonsterInstance> _Minions = new FastList<>();

	// MARGUERITE Status Tracking :
	private static final int LIVE = 0; // MARGUERITE is spawned.
	private static final int DEAD = 3; // MARGUERITE has been killed.

	private static boolean attacked = false;
	private static boolean halfHp = false;
	private static boolean quarterHp = false;

	private static String msg1 = "How dare you touch me you motherfcker";
	private static String msg2 = "You are stronger than i expected. I'll give you that.";
	private static String msg3 = "The unmoved goes to slumber once more.";

	enum Event
	{
		MARGUERITE_SPAWN,
		ACTION,
		HEAL,
		SPAWN_JOSEPHINE,
		RESPAWN_JOSEPHINE,
		DESPAWN_JOSEPHINE,
		CHECK_JOSEPHINE
	}



	public Marguerite(final int questId, final String name, final String descr)
	{
		super(questId, name, descr);

		final int[] mobs =
				{
						MARGUERITE,
						JOSEPINE
				};
		for (final int mob : mobs)
		{
			addEventId(mob, QuestEventType.ON_KILL);
			addEventId(mob, QuestEventType.ON_ATTACK);
		}


		final StatsSet info = GrandBossManager.getInstance().getStatsSet(MARGUERITE);

		final Integer status = GrandBossManager.getInstance().getBossStatus(MARGUERITE);

		switch (status)
		{
			case DEAD:
			{
				final long temp = info.getLong("respawn_time") - System.currentTimeMillis();
				if (temp > 0)
				{
					startQuestTimer("MARGUERITE_SPAWN", temp, null, null);
				}
				else
				{
					final L2GrandBossInstance marguerite = (L2GrandBossInstance) addSpawn(MARGUERITE, 115255, -38247, -2427, 0, false, 0);
					if (Config.ANNOUNCE_TO_ALL_SPAWN_RB)
					{
						Announcements.getInstance().announceToAll("Raid boss " + marguerite.getName() + " spawned in world.");
					}
					GrandBossManager.getInstance().setBossStatus(MARGUERITE, LIVE);
					GrandBossManager.getInstance().addBoss(marguerite);
					spawnBoss(marguerite);
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
				final L2GrandBossInstance marguerite = (L2GrandBossInstance) addSpawn(MARGUERITE, 115255, -38247, -2427, 0, false, 0);
				if (Config.ANNOUNCE_TO_ALL_SPAWN_RB)
				{
					Announcements.getInstance().announceToAll("Raid boss " + marguerite.getName() + " spawned in world.");
				}
				GrandBossManager.getInstance().addBoss(marguerite);
				marguerite.setCurrentHpMp(hp, mp);
				spawnBoss(marguerite);

			}
			break;
			default:
			{
				final L2GrandBossInstance marguerite = (L2GrandBossInstance) addSpawn(MARGUERITE, 115255, -38247, -2427, 0, false, 0);
				if (Config.ANNOUNCE_TO_ALL_SPAWN_RB)
				{
					Announcements.getInstance().announceToAll("Raid boss " + marguerite.getName() + " spawned in world.");
				}
				GrandBossManager.getInstance().setBossStatus(MARGUERITE, LIVE);
				GrandBossManager.getInstance().addBoss(marguerite);
				spawnBoss(marguerite);

			}

		}
	}


	private void spawnBoss(final L2GrandBossInstance npc)
	{

		startQuestTimer("ACTION", 10000, npc, null, true);
		startQuestTimer("SPAWN_JOSEPHINE", 1000, npc, null);
		startQuestTimer("HEAL", 15000, null, null, true);
		startQuestTimer("CHECK_JOSEPHINE", 30000, npc, null, true);
		final int abnormal = Integer.decode("0x002000");
		_marguerite = npc;
		npc.startAbnormalEffect(abnormal);
	}


	@Override
	public String onAttack(final L2NpcInstance npc, final L2PcInstance attacker, final int damage, final boolean isPet)
	{
		if (npc.getNpcId() == MARGUERITE)
		{


			if (!attacked)
			{
				final int objId = npc.getObjectId();
				npc.broadcastPacket(new CreatureSay(objId, 0,"Marguerite" , msg1));
				for (final L2MonsterInstance josephine : _Minions)
				{
					josephine.addDamageHate(attacker, 0, 999);
					josephine.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
				}
				attacked = true;
			}
			if(!halfHp && npc.getCurrentHp()<npc.getMaxHp()/2){
				final int objId = npc.getObjectId();
				npc.broadcastPacket(new CreatureSay(objId, 0,"Marguerite" , msg2));
				halfHp = true;
			}
			if (!quarterHp && _marguerite.getCurrentHp() < (_marguerite.getMaxHp() / 4)) {

				quarterHp = true;
			}
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}




	@Override
	public String onAdvEvent(final String event, final L2NpcInstance npc, final L2PcInstance player)
	{
		final Marguerite.Event event_enum = Marguerite.Event.valueOf(event);

		switch (event_enum)
		{
			case MARGUERITE_SPAWN:
			{

				final L2GrandBossInstance marguerite = (L2GrandBossInstance) addSpawn(MARGUERITE, 115255, -38247, -2427, 0, false, 0);
				if (Config.ANNOUNCE_TO_ALL_SPAWN_RB) {
					Announcements.getInstance().announceToAll("Raid boss " + marguerite.getName() + " spawned in world.");
				}
				GrandBossManager.getInstance().setBossStatus(MARGUERITE, LIVE);
				GrandBossManager.getInstance().addBoss(marguerite);
				spawnBoss(marguerite);

			}
			break;
			case ACTION:
			{
				final int objId = npc.getObjectId();
				npc.broadcastPacket(new CreatureSay(objId, 0,"Marguerite" , "Moooh"));
			}
			break;
			case SPAWN_JOSEPHINE:
			{
				final int radius = 400;
				for (int i = 0; i < 8; i++)
				{
					final int x = (int) (radius * Math.cos(i * .7854));
					final int y = (int) (radius * Math.sin(i * .7854));
					_Minions.add((L2MonsterInstance) addSpawn(JOSEPINE, npc.getX() + x, npc.getY() + y, npc.getZ(), 0, false, 0));
				}
			}
			break;
			case DESPAWN_JOSEPHINE:
			{
				for (int i = 0; i < _Minions.size(); i++)
				{
					final L2Attackable mob = _Minions.get(i);
					if (mob != null)
					{
						mob.decayMe();
					}
				}
				_Minions.clear();
			}
			break;
			case CHECK_JOSEPHINE:
			{
				int deadJosephines = 0;
				for (final L2MonsterInstance josephine : _Minions)
				{
					if (josephine.isDead())
						deadJosephines++;
				}
				if (deadJosephines == _Minions.size()) {}
			}
			break;
			case HEAL:
			{
				boolean notCasting;
				final boolean margueriteNeedHeal = _marguerite != null && _marguerite.getCurrentHp() < _marguerite.getMaxHp();
				boolean josephineNeedHeal = false;
				for (final L2MonsterInstance josephine : _Minions)
				{
					josephineNeedHeal = josephine.getCurrentHp() < josephine.getMaxHp();
					if (josephine == null || josephine.isDead() || josephine.isCastingNow())
						continue;
					notCasting = josephine.getAI().getIntention() != CtrlIntention.AI_INTENTION_CAST;
					if (margueriteNeedHeal)
					{
						if (josephine.getTarget() != _marguerite || notCasting)
						{
							getIntoPosition(josephine, _marguerite);
							josephine.setTarget(_marguerite);
							final int objId = josephine.getObjectId();
							josephine.broadcastPacket(new CreatureSay(objId, 0,"Josephine" , "Here's some heal my queen"));
							josephine.doCast(SkillTable.getInstance().getInfo(262, 37));
						}
						continue;
					}
					if (josephineNeedHeal)
					{
						if (josephine.getTarget() != josephine || notCasting)
						{
							for (int k = 0; k < _Minions.size(); k++)
							{
								getIntoPosition(_Minions.get(k), josephine);
								_Minions.get(k).setTarget(josephine);
								_Minions.get(k).doCast(SkillTable.getInstance().getInfo(262, 37));
							}

						}
					}
					if (notCasting && josephine.getTarget() != null)
						josephine.setTarget(null);
				}
			}
			break;
			default:
			{
				LOGGER.info("MARGUERITE: Not defined event: " + event + "!");
			}
		}

		return super.onAdvEvent(event, npc, player);
	}

	public void getIntoPosition(final L2MonsterInstance josephine, final L2MonsterInstance caller)
	{
		if (!josephine.isInsideRadius(caller, 100, false, false))
			josephine.getAI().moveToPawn(caller, 100);
	}


	@Override
	public String onKill(final L2NpcInstance npc, final L2PcInstance killer, final boolean isPet)
	{
		final int npcId = npc.getNpcId();

		final Integer status = GrandBossManager.getInstance().getBossStatus(MARGUERITE);

		if (npcId == MARGUERITE)
		{
			final int objId = npc.getObjectId();
			npc.broadcastPacket(new CreatureSay(objId, 0, "Marguerite", msg3));
			attacked = false;
			halfHp = false;



			npc.broadcastPacket(new PlaySound(1, "BS02_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));

			if (!npc.getSpawn().is_customBossInstance())
			{
				GrandBossManager.getInstance().setBossStatus(MARGUERITE, DEAD);
				// time is 4 hours
				final long respawnTime = (Config.MARGUERITE_RESP_FIRST + Rnd.get(Config.MARGUERITE_RESP_SECOND)) * 3600000;
				startQuestTimer("MARGUERITE_SPAWN", respawnTime, null, null);

				//cancelQuestTimer("CHECK_QA_ZONE", npc, null);
				// also save the respawn time so that the info is maintained past reboots
				final StatsSet info = GrandBossManager.getInstance().getStatsSet(MARGUERITE);
				info.set("respawn_time", System.currentTimeMillis() + respawnTime);
				GrandBossManager.getInstance().setStatsSet(MARGUERITE, info);
			}
			if (killer.getClan() != null) {
				Announcements.getInstance().announceToAll("Marguerite was killed by " + killer.getName() + " of Clan " + killer.getClan().getName() + ".");
			} else {
				Announcements.getInstance().announceToAll("Marguerite was killed by " + killer.getName() + ".");
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
