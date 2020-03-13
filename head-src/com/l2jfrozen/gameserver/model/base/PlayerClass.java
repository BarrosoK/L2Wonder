/*
 * $Header: PlayerClass.java, 24/11/2005 12:56:01 luisantonioa Exp $
 *
 * $Author: luisantonioa $
 * $Date: 24/11/2005 12:56:01 $
 * $Revision: 1 $
 * $Log: PlayerClass.java,v $
 * Revision 1  24/11/2005 12:56:01  luisantonioa
 * Added copyright notice
 *
 *
 * L2jFrozen Project - www.l2jfrozen.com 
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.l2jfrozen.gameserver.model.base;

import static com.l2jfrozen.gameserver.model.base.ClassLevel.First;
import static com.l2jfrozen.gameserver.model.base.ClassLevel.Fourth;
import static com.l2jfrozen.gameserver.model.base.ClassLevel.Second;
import static com.l2jfrozen.gameserver.model.base.ClassLevel.Third;
import static com.l2jfrozen.gameserver.model.base.ClassType.Fighter;
import static com.l2jfrozen.gameserver.model.base.ClassType.Mystic;
import static com.l2jfrozen.gameserver.model.base.ClassType.Priest;
import static com.l2jfrozen.gameserver.model.base.PlayerRace.*;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;

/**
 * This class ...
 * @author programmos, l2jfrozen dev
 * @version $Revision: 1.2.1 $ $Date: 2009/04/13 02:01:21 $
 */
public enum PlayerClass
{
	HumanFighter(all, Fighter, First),
	Warrior(all, Fighter, Second),
	Gladiator(all, Fighter, Third),
	Warlord(all, Fighter, Third),
	HumanKnight(all, Fighter, Second),
	Paladin(all, Fighter, Third),
	DarkAvenger(all, Fighter, Third),
	Rogue(all, Fighter, Second),
	TreasureHunter(all, Fighter, Third),
	Hawkeye(all, Fighter, Third),
	HumanMystic(all, Mystic, First),
	HumanWizard(all, Mystic, Second),
	Sorceror(all, Mystic, Third),
	Necromancer(all, Mystic, Third),
	Warlock(all, Mystic, Third),
	Cleric(all, Priest, Second),
	Bishop(all, Priest, Third),
	Prophet(all, Priest, Third),

	ElvenFighter(all, Fighter, First),
	ElvenKnight(all, Fighter, Second),
	TempleKnight(all, Fighter, Third),
	Swordsinger(all, Fighter, Third),
	ElvenScout(all, Fighter, Second),
	Plainswalker(all, Fighter, Third),
	SilverRanger(all, Fighter, Third),
	ElvenMystic(all, Mystic, First),
	ElvenWizard(all, Mystic, Second),
	Spellsinger(all, Mystic, Third),
	ElementalSummoner(all, Mystic, Third),
	ElvenOracle(all, Priest, Second),
	ElvenElder(all, Priest, Third),
	
	DarkElvenFighter(all, Fighter, First),
	PalusKnight(all, Fighter, Second),
	ShillienKnight(all, Fighter, Third),
	Bladedancer(all, Fighter, Third),
	Assassin(all, Fighter, Second),
	AbyssWalker(all, Fighter, Third),
	PhantomRanger(all, Fighter, Third),
	DarkElvenMystic(all, Mystic, First),
	DarkElvenWizard(all, Mystic, Second),
	Spellhowler(all, Mystic, Third),
	PhantomSummoner(all, Mystic, Third),
	ShillienOracle(all, Priest, Second),
	ShillienElder(all, Priest, Third),
	
	OrcFighter(all, Fighter, First),
	OrcRaider(all, Fighter, Second),
	Destroyer(all, Fighter, Third),
	OrcMonk(all, Fighter, Second),
	Tyrant(all, Fighter, Third),
	OrcMystic(all, Mystic, First),
	OrcShaman(all, Mystic, Second),
	Overlord(all, Mystic, Third),
	Warcryer(all, Mystic, Third),
	
	DwarvenFighter(all, Fighter, First),
	DwarvenScavenger(all, Fighter, Second),
	BountyHunter(all, Fighter, Third),
	DwarvenArtisan(all, Fighter, Second),
	Warsmith(all, Fighter, Third),
	
	dummyEntry1(null, null, null),
	dummyEntry2(null, null, null),
	dummyEntry3(null, null, null),
	dummyEntry4(null, null, null),
	dummyEntry5(null, null, null),
	dummyEntry6(null, null, null),
	dummyEntry7(null, null, null),
	dummyEntry8(null, null, null),
	dummyEntry9(null, null, null),
	dummyEntry10(null, null, null),
	dummyEntry11(null, null, null),
	dummyEntry12(null, null, null),
	dummyEntry13(null, null, null),
	dummyEntry14(null, null, null),
	dummyEntry15(null, null, null),
	dummyEntry16(null, null, null),
	dummyEntry17(null, null, null),
	dummyEntry18(null, null, null),
	dummyEntry19(null, null, null),
	dummyEntry20(null, null, null),
	dummyEntry21(null, null, null),
	dummyEntry22(null, null, null),
	dummyEntry23(null, null, null),
	dummyEntry24(null, null, null),
	dummyEntry25(null, null, null),
	dummyEntry26(null, null, null),
	dummyEntry27(null, null, null),
	dummyEntry28(null, null, null),
	dummyEntry29(null, null, null),
	dummyEntry30(null, null, null),
	
	/*
	 * (3rd classes)
	 */
	duelist(all, Fighter, Fourth),
	dreadnought(all, Fighter, Fourth),
	phoenixKnight(all, Fighter, Fourth),
	hellKnight(all, Fighter, Fourth),
	sagittarius(all, Fighter, Fourth),
	adventurer(all, Fighter, Fourth),
	archmage(all, Mystic, Fourth),
	soultaker(all, Mystic, Fourth),
	arcanaLord(all, Mystic, Fourth),
	cardinal(all, Mystic, Fourth),
	hierophant(all, Mystic, Fourth),
	
	evaTemplar(all, Fighter, Fourth),
	swordMuse(all, Fighter, Fourth),
	windRider(all, Fighter, Fourth),
	moonlightSentinel(all, Fighter, Fourth),
	mysticMuse(all, Mystic, Fourth),
	elementalMaster(all, Mystic, Fourth),
	evaSaint(all, Mystic, Fourth),
	
	shillienTemplar(all, Fighter, Fourth),
	spectralDancer(all, Fighter, Fourth),
	ghostHunter(all, Fighter, Fourth),
	ghostSentinel(all, Fighter, Fourth),
	stormScreamer(all, Mystic, Fourth),
	spectralMaster(all, Mystic, Fourth),
	shillienSaint(all, Mystic, Fourth),
	
	titan(all, Fighter, Fourth),
	grandKhauatari(all, Fighter, Fourth),
	dominator(all, Mystic, Fourth),
	doomcryer(all, Mystic, Fourth),
	
	fortuneSeeker(all, Fighter, Fourth),
	maestro(all, Fighter, Fourth);
	
	private PlayerRace _race;
	private ClassLevel _level;
	private ClassType _type;
	
	private static final Set<PlayerClass> mainSubclassSet;


	private static final Set<PlayerClass> neverSubclassed = EnumSet.of(Overlord, Warsmith);
	
	private static final Set<PlayerClass> subclasseSet1 = EnumSet.of(DarkAvenger, Paladin, TempleKnight, ShillienKnight);
	private static final Set<PlayerClass> subclasseSet2 = EnumSet.of(TreasureHunter, AbyssWalker, Plainswalker);
	private static final Set<PlayerClass> subclasseSet3 = EnumSet.of(Hawkeye, SilverRanger, PhantomRanger);
	private static final Set<PlayerClass> subclasseSet4 = EnumSet.of(Warlock, ElementalSummoner, PhantomSummoner);
	private static final Set<PlayerClass> subclasseSet5 = EnumSet.of(Sorceror, Spellsinger, Spellhowler);


	private static final EnumMap<PlayerClass, Set<PlayerClass>> subclassSetMap = new EnumMap<>(PlayerClass.class);
	
	static
	{
		Set<PlayerClass> subclasses = getSet(null, Third);
		subclasses.removeAll(neverSubclassed);
		
		mainSubclassSet = subclasses;
		
		subclassSetMap.put(DarkAvenger, subclasseSet1);
		subclassSetMap.put(Paladin, subclasseSet1);
		subclassSetMap.put(TempleKnight, subclasseSet1);
		subclassSetMap.put(ShillienKnight, subclasseSet1);
		
		subclassSetMap.put(TreasureHunter, subclasseSet2);
		subclassSetMap.put(AbyssWalker, subclasseSet2);
		subclassSetMap.put(Plainswalker, subclasseSet2);
		
		subclassSetMap.put(Hawkeye, subclasseSet3);
		subclassSetMap.put(SilverRanger, subclasseSet3);
		subclassSetMap.put(PhantomRanger, subclasseSet3);
		
		subclassSetMap.put(Warlock, subclasseSet4);
		subclassSetMap.put(ElementalSummoner, subclasseSet4);
		subclassSetMap.put(PhantomSummoner, subclasseSet4);
		
		subclassSetMap.put(Sorceror, subclasseSet5);
		subclassSetMap.put(Spellsinger, subclasseSet5);
		subclassSetMap.put(Spellhowler, subclasseSet5);
		
		subclasses = null;
	}
	
	PlayerClass(final PlayerRace pRace, final ClassType pType, final ClassLevel pLevel)
	{
		_race = pRace;
		_level = pLevel;
		_type = pType;
	}
	
	public final Set<PlayerClass> getAvailableSubclasses(final L2PcInstance player)
	{
		Set<PlayerClass> subclasses = null;
		
		if (_level == Third)
		{
			subclasses = EnumSet.copyOf(mainSubclassSet);
			
			subclasses.removeAll(neverSubclassed);
			subclasses.remove(this);

/*
			switch (player.getRace())
			{
				case elf:
					subclasses.removeAll(getSet(DarkElf, Third));
					break;
				case darkelf:
					subclasses.removeAll(getSet(LightElf, Third));
					break;
			}

			
			Set<PlayerClass> unavailableClasses = subclassSetMap.get(this);
			
			if (unavailableClasses != null)
			{
				subclasses.removeAll(unavailableClasses);
			}

			unavailableClasses = null;

			*/
		}
		
		return subclasses;
	}
	
	public static final EnumSet<PlayerClass> getSet(final PlayerRace race, final ClassLevel level)
	{
		final EnumSet<PlayerClass> allOf = EnumSet.noneOf(PlayerClass.class);
		
		for (final PlayerClass playerClass : EnumSet.allOf(PlayerClass.class))
		{
			if (race == null || playerClass.isOfRace(race))
			{
				if (level == null || playerClass.isOfLevel(level))
				{
					allOf.add(playerClass);
				}
			}
		}
		
		return allOf;
	}
	
	public final boolean isOfRace(final PlayerRace pRace)
	{
		return _race == pRace;
	}
	
	public final boolean isOfType(final ClassType pType)
	{
		return _type == pType;
	}
	
	public final boolean isOfLevel(final ClassLevel pLevel)
	{
		return _level == pLevel;
	}
	
	public final ClassLevel getLevel()
	{
		return _level;
	}
}
