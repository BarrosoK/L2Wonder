package com.l2jfrozen.gameserver.model.zone.type;

import com.l2jfrozen.gameserver.model.L2Character;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.zone.L2ZoneType;

import java.time.ZoneId;

/**
 * Created by Server1 on 6/27/2017.
 */
public class L2CraftZone extends L2ZoneType
{
 public L2CraftZone(final int id)
  {
  super(id);
  }

  @Override
protected void onEnter(final L2Character character)
 {
 //if (character instanceof L2PcInstance)
 //character.setInsideZone(ZoneId.CRAFT, true);
 }

 @Override
 protected void onExit(final L2Character character)
 {
 //if (character instanceof L2PcInstance)
 //character.setInsideZone(ZoneId.CRAFT, false);
 }

 @Override
 public void onDieInside(final L2Character character)
 {
 }

 @Override
public void onReviveInside(final L2Character character)
 {
 }
}