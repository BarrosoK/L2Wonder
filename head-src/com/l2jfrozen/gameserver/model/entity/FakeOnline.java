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
package com.l2jfrozen.gameserver.model.entity;


import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.datatables.sql.ItemTable;
import com.l2jfrozen.gameserver.model.ItemContainer;
import com.l2jfrozen.gameserver.model.L2Character;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.network.L2GameClient;
import com.l2jfrozen.gameserver.thread.LoginServerThread;
import com.l2jfrozen.util.database.L2DatabaseFactory;
import com.l2jfrozen.util.random.Rnd;
import org.apache.log4j.Logger;


import java.sql.Connection;

import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Random;


/**
 * @author FOFAS
 */

public class FakeOnline {

    protected static final Logger LOGGER = Logger.getLogger(FakeOnline.class); //select fake player

    private static final String LOAD_OFFLINE_STATUS = "SELECT charId FROM fakeplayer ORDER BY RAND() LIMIT " + Config.FAKEPLAYERS_COUNT;

    //insert fake player

    private static final String SET_OFFLINE_STATUS = "INSERT INTO fakeplayer (charId) VALUES (?)";

    public static ArrayList<L2PcInstance> fakeplayers;

    public static void restoreFakePlayers() {

        fakeplayers = new ArrayList<>();

        int nfakeplayer = 0;

        try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {

            PreparedStatement stm = con.prepareStatement(LOAD_OFFLINE_STATUS);

            ResultSet rs = stm.executeQuery();

            while (rs.next()) {

                L2PcInstance player = null;

                try {

                    L2GameClient client = new L2GameClient(null);

                    client.setFakePlayer(true);

                    player = L2PcInstance.restore(rs.getInt("charId"));

                    client.setActiveChar(player);

                    assert player != null;
                    player.setOnlineStatus(true);

                    client.setAccountName(player.getAccountName());

                    client.setState(L2GameClient.GameClientState.IN_GAME);

                    player.setClient(client);

                    int n = Rnd.nextInt(100);
                    player.spawnMe(player.getX(), player.getY(), player.getZ());
                    if (n <= 20) {
                        player.sitDown();
                        player.setIsParalyzed(false);
                    }

                    LoginServerThread.getInstance().addGameServerLogin(player.getAccountName(), client);

                    ItemContainer items = player.getInventory();

                    items.restore();

                    for (int z = 0; z < items.getItems().length; z++) {
                        if (items.getItems()[z].isEquipable() && !items.getItems()[z].isEquipped() && items.getItems()[z].getItemId() != 6583)
                            player.getInventory().equipItemAndRecord(items.getItems()[z]);
                    }

                    player.setfakeplayer(true);

                    player.setOnlineStatus(true);

                    player.restoreEffects();

                    player.broadcastUserInfo();

                    nfakeplayer++;
                    fakeplayers.add(player);

                } catch (Exception e) {


                    LOGGER.warn("FakePlayer: Error loading fake player: " + player, e);

                    if (player != null) {

                        player.deleteMe();

                    }

                }

            }

            rs.close();

            stm.close();

            LOGGER.info("Loaded: " + nfakeplayer + " Fake player(s)");

        } catch (Exception e) {

            LOGGER.warn("FakePlayer: Error while loading FakePlayer: ", e);

        }


    }


    public static void setfakeplayers(L2PcInstance player) {

        try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {

            PreparedStatement statement = con.prepareStatement(SET_OFFLINE_STATUS);

            statement.setInt(1, player.getObjectId());

            statement.execute();

            statement.close();

        } catch (Exception e) {
        }

    }

}