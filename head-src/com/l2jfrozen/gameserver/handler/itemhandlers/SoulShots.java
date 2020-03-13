/*
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
package com.l2jfrozen.gameserver.handler.itemhandlers;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.datatables.SkillTable;
import com.l2jfrozen.gameserver.handler.IItemHandler;
import com.l2jfrozen.gameserver.model.L2Skill;
import com.l2jfrozen.gameserver.model.actor.instance.L2ItemInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PlayableInstance;
import com.l2jfrozen.gameserver.model.entity.Announcements;
import com.l2jfrozen.gameserver.network.SystemMessageId;
import com.l2jfrozen.gameserver.network.serverpackets.ExAutoSoulShot;
import com.l2jfrozen.gameserver.network.serverpackets.MagicSkillUser;
import com.l2jfrozen.gameserver.network.serverpackets.SystemMessage;
import com.l2jfrozen.gameserver.skills.Stats;
import com.l2jfrozen.gameserver.templates.L2Item;
import com.l2jfrozen.gameserver.templates.L2Weapon;
import com.l2jfrozen.gameserver.thread.ThreadPoolManager;
import com.l2jfrozen.gameserver.util.Broadcast;

/**
 * This class ...
 *
 * @author programmos
 * @version $Revision: 1.2.4.5 $ $Date: 2009/04/13 03:12:07 $
 */

public class SoulShots implements IItemHandler {
    // All the item IDs that this handler knows.
    private static final int[] ITEM_IDS =
            {
                    5789,
                    1835,
                    1463,
                    1464,
                    1465,
                    1466,
                    1467,
                    1539,
                    728,
                    5592
            };
    private static final int[] SKILL_IDS =
            {
                    2039,
                    2150,
                    2151,
                    2152,
                    2153,
                    2154,
                    2037,
                    2005,
                    2166
            };

    private static final int HEALING_POT_CD = 11;
    private static final int MANA_POT_CD = 1;
    private static final int CP_POT_CD = 1;

    /*
     * (non-Javadoc)
     * @see com.l2jfrozen.gameserver.handler.IItemHandler#useItem(com.l2jfrozen.gameserver.model.L2PcInstance, com.l2jfrozen.gameserver.model.L2ItemInstance)
     */
    @Override
    public void useItem(final L2PlayableInstance playable, final L2ItemInstance item) {
        if (!(playable instanceof L2PcInstance))
            return;

        L2PcInstance activeChar = (L2PcInstance) playable;
        L2ItemInstance weaponInst = activeChar.getActiveWeaponInstance();
        L2Weapon weaponItem = activeChar.getActiveWeaponItem();
        final int itemId = item.getItemId();

        if (itemId == 1539) {

            if (activeChar.isAutoHPPot(1539)) {
                activeChar.sendPacket(new ExAutoSoulShot(1539, 0));
                activeChar.sendMessage("Deactivated auto healing potions.");
                activeChar.setAutoHPPot(1539, null, false);
            } else {
                if (activeChar.getInventory().getItemByItemId(1539) != null) {
                    if (activeChar.getInventory().getItemByItemId(1539).getCount() > 1 && activeChar.useAutoHPPots) {
                        activeChar.sendPacket(new ExAutoSoulShot(1539, 1));
                        activeChar.sendMessage("Activated auto healing potions.");
                        activeChar.setAutoHPPot(1539, ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new AutoPot(1539, activeChar), 1000, HEALING_POT_CD * 1000), true);
                    } else {
                        if(!activeChar.isInOlympiadMode()&& !activeChar.isInFunEvent()){
                        final L2Skill skill = SkillTable.getInstance().getInfo(2037, 1);
                        if (!activeChar.isSkillDisabled(skill)) {
                            MagicSkillUser msu = new MagicSkillUser(activeChar, activeChar, 2037, 1, 0, 100);


                            activeChar.broadcastPacket(msu);
                            Potions itemSkill = new Potions();

                            itemSkill.usePotion(activeChar, 2037, 1);
                            activeChar.destroyItemByItemId("b", itemId, 1, null, false);
                        }  }
                    }
                }
            }
            return;
        } else if (itemId == 728) {

            if (activeChar.isAutoMPPot(728)) {
                activeChar.sendPacket(new ExAutoSoulShot(728, 0));
                activeChar.sendMessage("Deactivated auto mana potions.");
                activeChar.setAutoMPPot(728, null, false);
            } else {
                if (activeChar.getInventory().getItemByItemId(728) != null) {
                    if (activeChar.getInventory().getItemByItemId(728).getCount() > 1 && activeChar.useAutoMPPots) {
                        activeChar.sendPacket(new ExAutoSoulShot(728, 1));
                        activeChar.sendMessage("Activated auto mana potions.");
                        activeChar.setAutoMPPot(728, ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new AutoPot(728, activeChar), 1000, MANA_POT_CD * 1000), true);
                    } else {
                        if(!activeChar.isInOlympiadMode()&& !activeChar.isInFunEvent()){
                        final L2Skill skill = SkillTable.getInstance().getInfo(2005, 1);

                        if (!activeChar.isSkillDisabled(skill)) {
                            MagicSkillUser msu = new MagicSkillUser(activeChar, activeChar, 2005, 1, 0, 1000);
                            activeChar.broadcastPacket(msu);
                            Potions itemSkill = new Potions();

                            itemSkill.usePotion(activeChar, 2005, 1);
                            activeChar.destroyItemByItemId("b", itemId, 1, null, false);
                        }
                        }
                    }
                }
            }
            return;
        } else if (itemId == 5592) {

            if (activeChar.isAutoCPPot(5592)) {
                activeChar.sendPacket(new ExAutoSoulShot(5592, 0));
                activeChar.sendMessage("Deactivated auto cp potions.");
                activeChar.setAutoCPPot(5592, null, false);
            } else {
                if (activeChar.getInventory().getItemByItemId(5592) != null) {
                    if (activeChar.getInventory().getItemByItemId(5592).getCount() > 1 && activeChar.useAutoCPPots) {
                        activeChar.sendPacket(new ExAutoSoulShot(5592, 1));
                        activeChar.sendMessage("Activated auto cp potions.");
                        activeChar.setAutoCPPot(5592, ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new AutoPot(5592, activeChar), 1000, CP_POT_CD * 1000), true);
                    } else {
                        if(!activeChar.isInOlympiadMode() && !activeChar.isInFunEvent()){
                        final L2Skill skill = SkillTable.getInstance().getInfo(2166, 2);
                        if (!activeChar.isSkillDisabled(skill)) {
                            MagicSkillUser msu = new MagicSkillUser(activeChar, activeChar, 2166, 2, 0, 5000);

                            activeChar.broadcastPacket(msu);
                            Potions itemSkill = new Potions();

                            itemSkill.usePotion(activeChar, 2166, 2);
                            activeChar.destroyItemByItemId("b", itemId, 1, null, false);
                        }}
                    }
                }
            }
            return;
        }


        // Check if Soulshot can be used
        if (weaponInst == null || weaponItem.getSoulShotCount() == 0) {
            if (!activeChar.getAutoSoulShot().containsKey(itemId)) {
                activeChar.sendPacket(new SystemMessage(SystemMessageId.CANNOT_USE_SOULSHOTS));
            }
            return;
        }

        // Check for correct grade
        final int weaponGrade = weaponItem.getCrystalType();
        if (weaponGrade == L2Item.CRYSTAL_NONE && itemId != 5789 && itemId != 1835 || weaponGrade == L2Item.CRYSTAL_D && itemId != 1463 || weaponGrade == L2Item.CRYSTAL_C && itemId != 1464 || weaponGrade == L2Item.CRYSTAL_B && itemId != 1465 || weaponGrade == L2Item.CRYSTAL_A && itemId != 1466 || weaponGrade == L2Item.CRYSTAL_S && itemId != 1467) {
            if (!activeChar.getAutoSoulShot().containsKey(itemId)) {
                activeChar.sendPacket(new SystemMessage(SystemMessageId.SOULSHOTS_GRADE_MISMATCH));
            }
            return;
        }

        activeChar.soulShotLock.lock();
        try {
            // Check if Soulshot is already active
            if (weaponInst.getChargedSoulshot() != L2ItemInstance.CHARGED_NONE)
                return;

            // Consume Soulshots if player has enough of them
            final int saSSCount = (int) activeChar.getStat().calcStat(Stats.SOULSHOT_COUNT, 0, null, null);
            final int SSCount = saSSCount == 0 ? weaponItem.getSoulShotCount() : saSSCount;

            weaponItem = null;

            // TODO: test ss
            /*
            if (!Config.DONT_DESTROY_SS)
			{
				if (!activeChar.destroyItemWithoutTrace("Consume", item.getObjectId(), SSCount, null, false))
				{
					if (activeChar.getAutoSoulShot().containsKey(itemId))
					{
						activeChar.removeAutoSoulShot(itemId);
						activeChar.sendPacket(new ExAutoSoulShot(itemId, 0));
						
						SystemMessage sm = new SystemMessage(SystemMessageId.AUTO_USE_OF_S1_CANCELLED);
						sm.addString(item.getItem().getName());
						activeChar.sendPacket(sm);
						sm = null;
					}
					else
					{
						activeChar.sendPacket(new SystemMessage(SystemMessageId.NOT_ENOUGH_SOULSHOTS));
					}
					return;
				}
			}
			*/

            // Charge soulshot
            weaponInst.setChargedSoulshot(L2ItemInstance.CHARGED_SOULSHOT);

            weaponInst = null;
        } finally {
            activeChar.soulShotLock.unlock();
        }

        // Send message to client
        activeChar.sendPacket(new SystemMessage(SystemMessageId.ENABLED_SOULSHOT));
        Broadcast.toSelfAndKnownPlayersInRadius(activeChar, new MagicSkillUser(activeChar, activeChar, SKILL_IDS[weaponGrade], 1, 0, 0), 360000/* 600 */);

        activeChar = null;
    }

    private class AutoPot implements Runnable {
        private int id;
        private L2PcInstance activeChar;

        public AutoPot(int id, L2PcInstance activeChar) {
            this.id = id;
            this.activeChar = activeChar;
        }

        @Override
        public void run() {

            if (activeChar.getInventory().getItemByItemId(id) == null) {
                activeChar.sendPacket(new ExAutoSoulShot(id, 0));
                if (id == 1539) {
                    activeChar.setAutoHPPot(id, null, false);
                } else if (id == 728) {
                    activeChar.setAutoMPPot(id, null, false);
                } else if (id == 5592) {
                    activeChar.setAutoCPPot(id, null, false);
                }
            }

            switch (id) {
                case 1539: {
                    if (activeChar.useAutoHPPots && !activeChar.isInOlympiadMode() && !activeChar.isDead() && !activeChar.isStunned()&&!activeChar.isSleeping() && !activeChar.isParalyzed() && !activeChar.isInFunEvent()) {
                        if (activeChar.getCurrentHp() < 0.80 * activeChar.getMaxHp()) {
                            final L2Skill skill = SkillTable.getInstance().getInfo(2037, 1);
                            if (!activeChar.isSkillDisabled(skill)) {
                                MagicSkillUser msu = new MagicSkillUser(activeChar, activeChar, 2037, 1, 0, 1000);
                                activeChar.broadcastPacket(msu);
                                Potions itemSkill = new Potions();
                                itemSkill.usePotion(activeChar, 2037, 1);
                                activeChar.destroyItemByItemId("b", id, 1, null, false);
                            }
                        }
                    }
                    /*else
					{
						activeChar.sendPacket(new ExAutoSoulShot(1539, 0));
						activeChar.setAutoPot(1539, null, false);
					}*/
                    break;
                }
                case 728: {
                    if (activeChar.useAutoMPPots && !activeChar.isInOlympiadMode() && !activeChar.isDead() && !activeChar.isStunned()&&!activeChar.isSleeping() && !activeChar.isParalyzed()&& !activeChar.isInFunEvent()) {
                        if (activeChar.getCurrentMp() < 0.80 * activeChar.getMaxMp()) {
                            final L2Skill skill = SkillTable.getInstance().getInfo(2005, 1);
                            if (!activeChar.isSkillDisabled(skill)) {
                                MagicSkillUser msu = new MagicSkillUser(activeChar, activeChar, 2005, 1, 0, 1000);
                                activeChar.broadcastPacket(msu);
                                Potions itemSkill = new Potions();
                                itemSkill.usePotion(activeChar, 2005, 1);
                                activeChar.destroyItemByItemId("b", id, 1, null, false);
                            }
                        }
                    }
					/*else
					{
						activeChar.sendPacket(new ExAutoSoulShot(1539, 0));
						activeChar.setAutoPot(1539, null, false);
					}*/
                    break;
                }
                case 5592: {
                    if (activeChar.useAutoCPPots && !activeChar.isInOlympiadMode()&& !activeChar.isDead() && !activeChar.isStunned()&&!activeChar.isSleeping() && !activeChar.isParalyzed()&& !activeChar.isInFunEvent()) {
                        if (activeChar.getCurrentCp() < 0.80 * activeChar.getMaxCp()) {
                            final L2Skill skill = SkillTable.getInstance().getInfo(2166, 2);
                            if (!activeChar.isSkillDisabled(skill)) {
                                MagicSkillUser msu = new MagicSkillUser(activeChar, activeChar, 2166, 2, 0, 1300);
                                activeChar.broadcastPacket(msu);
                                Potions itemSkill = new Potions();
                                itemSkill.usePotion(activeChar, 2166, 2);
                                activeChar.destroyItemByItemId("b", id, 1, null, false);
                            }
                        }
                    }
					/*else
					{
						activeChar.sendPacket(new ExAutoSoulShot(1539, 0));
						activeChar.setAutoPot(1539, null, false);
					}*/
                    break;
                }
            }

        }
    }


    @Override
    public int[] getItemIds() {
        return ITEM_IDS;
    }
}
