package com.l2jfrozen.gameserver.model.scripts;

import com.l2jfrozen.gameserver.datatables.sql.CharNameTable;
import com.l2jfrozen.gameserver.datatables.sql.ClanTable;
import com.l2jfrozen.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.entity.olympiad.Olympiad;
import com.l2jfrozen.gameserver.model.quest.Quest;
import com.l2jfrozen.gameserver.model.quest.QuestState;
import com.l2jfrozen.gameserver.network.serverpackets.MagicSkillUser;
import com.l2jfrozen.util.database.L2DatabaseFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Logger;

/**
 * Created by Server1 on 7/7/2017.
 */
/*
 * Copyright (C) 2004-2013 L2J DataPack
 *
 * This file is part of L2J DataPack.
 *
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

public class Services extends Quest {
    public static final Logger _log = Logger.getLogger(Services.class.getName());

    // NPC Id
    int servicesNpc = 50008;

    // Noble Items
    int nobleItemId = 4037;
    int nobleItemCount = 5;

    int adenaNobleItemCount = 100000;
    int adenaNobleItemId = 57;

    // PK Reduce Items
    int pkReduceItemId = 4037;
    int pkReduceItemCount = 5;

    // Change Name Items
    int changeNameItemId = 4037;
    int changeNameItemCount = 5;
    boolean logNameChanges = true;

    // Change Clan Name Items
    int changeClanNameItemId = 4037;
    long changeClanNameItemCount = 5;
    boolean logClanNameChanges = true;
    int clanMinLevel = 5;

    // Clan Level Items
    int[] clanLevelItemsId =
            {
                    4037, // Level 1 to 5
                    4037, // Level 5 to 6
                    4037, // Level 6 to 7
                    4037, // Level 7 to 8
            };

    int[] clanLevelItemsCount =
            {
                    1,
                    6, // Level 5 to 6
                    7, // Level 6 to 7
                    8, // Level 7 to 8
            };

    // Clan Reputation Points Items
    int clanReputationPointsItemId = 4037;
    int clanReputationPointsItemCount = 5;

    // Change Gender Items
    int changeGenderItemId = 4037;
    int changeGenderItemCount = 5;

    public Services(int questId, String name, String descr) {
        super(questId, name, descr);

        addStartNpc(servicesNpc);
        addFirstTalkId(servicesNpc);
        addTalkId(servicesNpc);
    }

    public static void main(String[] args) {
        new Services(-1, Services.class.getSimpleName(), "custom");
    }

    @Override
    public String onFirstTalk(L2NpcInstance npc, L2PcInstance player) {
        if (player.getQuestState(getName()) == null) {
            newQuestState(player);
        } else if (player.isInCombat())
            return "Services-Blocked.htm";

        else if (player.getPvpFlag() == 1)
            return "Services-Blocked.htm";

        else if (player.getKarma() != 0)
            return "Services-Blocked.htm";

        else if (Olympiad.getInstance().isRegistered(player))
            return "Services-Blocked.htm";

        else if (player.isDead() || player.isFakeDeath())
            return "Services-Blocked.htm";

        return "Services.htm";
    }

    @Override
    public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player) {
        String htmlText = event;
        QuestState st = player.getQuestState(getName());

        if (event.equals("setNoble")) {
            if (!player.isNoble()) {
                if (st.getQuestItemsCount(nobleItemId) >= nobleItemCount) {
                    st.takeItems(nobleItemId, nobleItemCount);
                    player.setNoble(true);
                    player.setTarget(player);
                    player.broadcastPacket(new MagicSkillUser(player, 5103, 1, 1000, 0));
                    player.broadcastUserInfo();
                    return "NoblesseServices-Success.htm";
                }
                return "NoblesseServices-NoItems.htm";
            }
            return "NoblesseServices-AlredyNoble.htm";
        }
        else if (event.equals("setNobleAdena")) {
            if (!player.isNoble()) {
                if (st.getQuestItemsCount(adenaNobleItemId) >= adenaNobleItemCount) {
                    st.takeItems(adenaNobleItemId, adenaNobleItemCount);
                    player.setNoble(true);
                    player.setTarget(player);
                    player.broadcastPacket(new MagicSkillUser(player, 5103, 1, 1000, 0));
                    player.broadcastUserInfo();
                    return "NoblesseServices-Success.htm";
                }
                return "NoblesseServices-NoItems.htm";
            }
            return "NoblesseServices-AlredyNoble.htm";
        }
       /* else if (event.equals("levelUpClan")) {
            if (player.getClan() == null) {
                return "ClanLevelUp-NoClan.htm";
            } else if (!player.isClanLeader()) {
                return "ClanLevelUp-NoLeader.htm";
            } else {
                if (player.getClan().getLevel() == 8) {
                    return "ClanLevelUp-MaxLevel.htm";
                }
                if (((player.getClan().getLevel() <= 1) || (player.getClan().getLevel() == 2) || (player.getClan().getLevel() == 3) || (player.getClan().getLevel() == 4))) {
                    if (st.getQuestItemsCount(clanLevelItemsId[0]) >= clanLevelItemsCount[0]) {
                        st.takeItems(clanLevelItemsId[0], clanLevelItemsCount[0]);
                        player.getClan().changeLevel(player.getClan().getLevel() + 1);
                        player.getClan().broadcastClanStatus();
                        player.sendMessage("Your clan is now level " + player.getClan().getLevel() + ".");
                        player.setTarget(player);
                        player.broadcastPacket(new MagicSkillUser(player, 5103, 1, 1000, 0));

                        return "ClanLevelUp.htm";
                    }
                    return "ClanLevelUp-NoItems.htm";
                } else if (player.getClan().getLevel() == 5) {
                    if (st.getQuestItemsCount(clanLevelItemsId[1]) >= clanLevelItemsCount[1]) {
                        st.takeItems(clanLevelItemsId[1], clanLevelItemsCount[1]);
                        player.getClan().changeLevel(player.getClan().getLevel() + 1);
                        player.getClan().broadcastClanStatus();
                        player.sendMessage("Your clan is now level " + player.getClan().getLevel() + ".");
                        player.setTarget(player);
                        player.broadcastPacket(new MagicSkillUser(player, 5103, 1, 1000, 0));

                        return "ClanLevelUp.htm";
                    }
                    return "ClanLevelUp-NoItems.htm";
                } else if (player.getClan().getLevel() == 6) {
                    if (st.getQuestItemsCount(clanLevelItemsId[2]) >= clanLevelItemsCount[2]) {
                        st.takeItems(clanLevelItemsId[2], clanLevelItemsCount[2]);
                        player.getClan().changeLevel(player.getClan().getLevel() + 1);
                        player.getClan().broadcastClanStatus();
                        player.sendMessage("Your clan is now level " + player.getClan().getLevel() + ".");
                        player.setTarget(player);
                        player.broadcastPacket(new MagicSkillUser(player, 5103, 1, 1000, 0));

                        return "ClanLevelUp.htm";
                    }
                    return "ClanLevelUp-NoItems.htm";
                } else if (player.getClan().getLevel() == 7) {
                    if (st.getQuestItemsCount(clanLevelItemsId[3]) >= clanLevelItemsCount[3]) {
                        st.takeItems(clanLevelItemsId[3], clanLevelItemsCount[3]);
                        player.getClan().changeLevel(player.getClan().getLevel() + 1);
                        player.getClan().broadcastClanStatus();
                        player.sendMessage("Your clan is now level " + player.getClan().getLevel() + ".");
                        player.setTarget(player);
                        player.broadcastPacket(new MagicSkillUser(player, 5103, 1, 1000, 0));

                        return "ClanLevelUp.htm";
                    }
                    return "ClanLevelUp-NoItems.htm";
                }

                player.getClan().broadcastClanStatus();
                return "ClanLevelUp.htm";
            }
        }*/
        else if (event.equals("changeGender")) {
            if (st.getQuestItemsCount(changeGenderItemId) >= changeGenderItemCount) {
                st.takeItems(changeGenderItemId, changeGenderItemCount);
                player.getAppearance().setSex(player.getAppearance().getSex() ? false : true);
                player.store();
                player.setTarget(player);
                player.broadcastPacket(new MagicSkillUser(player, 5103, 1, 1000, 0));
                player.broadcastUserInfo();

                return "ChangeGender-Success.htm";
            }
            return "ChangeGender-NoItems.htm";
        } else if (event.startsWith("changeName")) {
            try {
                String newName = event.substring(11);

                if (st.getQuestItemsCount(changeNameItemId) >= changeNameItemCount) {
                    if (newName == null) {
                        return "ChangeName.htm";
                    }
                    if (!newName.matches("^[a-zA-Z0-9]+$")) {
                        player.sendMessage("Incorrect name. Please try again.");
                        return "ChangeName.htm";
                    } else if (newName.equals(player.getName())) {
                        player.sendMessage("Please, choose a different name.");
                        return "ChangeName.htm";
                    } else if (CharNameTable.getInstance().doesCharNameExist(newName)) {
                        player.sendMessage("The name " + newName + " already exists.");
                        return "ChangeName.htm";
                    } else {
                        st.takeItems(changeNameItemId, changeNameItemCount);
                        player.setName(newName);
                        player.store();
                        player.sendMessage("Your new character name is " + newName);
                        player.broadcastUserInfo();
                        player.getClan().broadcastClanStatus();

                        return "ChangeName-Success.htm";
                    }
                }
                return "ChangeName-NoItems.htm";
            } catch (Exception e) {
                player.sendMessage("Please, insert a correct name.");
                return "ChangeName.htm";
            }
        } else if (event.startsWith("reducePks")) {
            try {


                if (player.getPkKills() != 0) {
                    int pkReduceCount = player.getPkKills();
                    if (pkReduceCount == 0) {
                        player.sendMessage("You don't have any Pks to clean.");
                        return "PkServices.htm";
                    }
                    if (st.getQuestItemsCount(pkReduceItemId) >= pkReduceItemCount) {

                        st.takeItems(pkReduceItemId, pkReduceItemCount);
                        player.setPkKills(0);
                        if (player.getPkKills() < 0) {
                            player.setPkKills(0);
                        }
                        player.sendMessage("You have successfuly cleaned " + pkReduceCount + " PKs.");
                        player.broadcastUserInfo();

                        return "PkServices-Success.htm";
                    }
                    return "PkServices-NoItems.htm";
                }
                return "PkServices-NoPks.htm";
            } catch (Exception e) {
                player.sendMessage("Incorrect value. Please try again.");
                return "PkServices.htm";
            }
        } else if (event.startsWith("changeClanName")) {
            if (player.getClan() == null) {
                return "ChangeClanName-NoClan.htm";
            }
            try {
                String newClanName = event.substring(15);

                if (st.getQuestItemsCount(changeClanNameItemId) >= changeClanNameItemCount) {
                    if (newClanName == null) {
                        return "ChangeClanName.htm";
                    }
                    if (!player.isClanLeader()) {
                        player.sendMessage("Only the clan leader can change the clan name.");
                        return "ChangeClanName.htm";
                    } else if (player.getClan().getLevel() < clanMinLevel) {
                        player.sendMessage("Your clan must be at least level " + clanMinLevel + " to change the name.");
                        return "ChangeClanName.htm";
                    } else if (!newClanName.matches("^[a-zA-Z0-9]+$")) {
                        player.sendMessage("Incorrect name. Please try again.");
                        return "ChangeClanName.htm";
                    } else if (newClanName.equals(player.getClan().getName())) {
                        player.sendMessage("Please, choose a different name.");
                        return "ChangeClanName.htm";
                    } else if (ClanTable.getInstance().getClanByName(newClanName) != null) {
                        player.sendMessage("The name " + newClanName + " already exists.");
                        return "ChangeClanName.htm";
                    } else {
                        st.takeItems(changeNameItemId, changeNameItemCount);
                        player.getClan().setName(newClanName);

                        try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
                            PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET clan_name=? WHERE clan_id=?");
                            statement.setString(1, newClanName);
                            statement.setInt(2, player.getClan().getClanId());
                            statement.execute();
                            statement.close();
                        } catch (Exception e) {
                            _log.info("Error updating clan name for player " + player.getName() + ". Error: " + e);
                        }

                        player.sendMessage("Your new clan name is " + newClanName);
                        player.getClan().broadcastClanStatus();

                        return "ChangeClanName-Success.htm";
                    }
                }
                return "ChangeClanName-NoItems.htm";
            } catch (Exception e) {
                player.sendMessage("Please, insert a correct name.");
                return "ChangeClanName.htm";
            }
        }/* else if (event.startsWith("setReputationPoints")) {
            try {
                int reputationPointsCount = 10000;

                if (player.getClan() == null) {
                    return "ClanReputationPoints-NoClan.htm";
                } else if (!player.isClanLeader()) {
                    return "ClanReputationPoints-NoLeader.htm";
                } else {
                    if (st.getQuestItemsCount(clanReputationPointsItemId) >= clanReputationPointsItemCount) {
                        st.takeItems(clanReputationPointsItemId, clanReputationPointsItemCount);
                        player.getClan().setReputationScore(player.getClan().getReputationScore() + reputationPointsCount, true);
                        player.getClan().broadcastClanStatus();
                        return "ClanReputationPoints-Success.htm";
                    }
                    return "ClanReputationPoints-NoItems.htm";
                }
            } catch (Exception e) {
                player.sendMessage("Incorrect value. Please try again.");
                return "ClanReputationPoints.htm";
            }
        }*/

        return htmlText;
    }
}