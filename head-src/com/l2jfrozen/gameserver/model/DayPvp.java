/* This program is free software; you can redistribute it and/or modify
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

package com.l2jfrozen.gameserver.model;

import com.l2jfrozen.gameserver.model.actor.instance.L2ItemInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.entity.Announcements;
import com.l2jfrozen.util.database.L2DatabaseFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * @author Iracundus
 */
public class DayPvp {

    public static void newPvpMaster() {

        deletePreviousMaster();


        int masterId = 0;
        int masterPvp = 0;
        String name = "";
        int sex = -1;
        Connection con = null;
        PreparedStatement state = null;
        ResultSet rset = null;


        try {
            con = L2DatabaseFactory.getInstance().getConnection();


            state = con.prepareStatement("UPDATE characters SET pvpmaster=0");
            state.execute();
            state.close();





            state = con.prepareStatement("SELECT obj_Id,daypvp,char_name,sex FROM characters ORDER BY daypvp DESC LIMIT 1;");
            rset = state.executeQuery();
            while (rset.next()) {
                masterId = rset.getInt("obj_Id");
                masterPvp = rset.getInt("daypvp");
                name = rset.getString("char_name");
                sex = rset.getInt("sex");
            }
            if (masterId == 0 || masterPvp == 0) {
                state.close();
                rset.close();
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return;
            }
            state.close();
            rset.close();




            Boolean isOnline = false;
            for (L2PcInstance p : L2World.getInstance().getAllPlayers()) {
                p.setDayPvp(0);
                if (p.getObjectId() == masterId) {

                    isOnline = true;
                    p.setPvpMaster(true);



                    boolean gender = p.getAppearance().getSex();
                    if (gender) {
                        p.setTitle("PvP Queen");
                        if(p.isOnline()==1) {
                            p.sendMessage("You are the PvP Queen today");
                        }
                        Announcements.getInstance().announceToAll("Today's PvP Queen is " + p.getName() + " with " + masterPvp + " PvPs");
                    } else {
                        p.setTitle("PvP King");
                        if(p.isOnline()==1) {
                            p.sendMessage("You are the PvP King today");
                        }
                        Announcements.getInstance().announceToAll("Today's PvP King is " + p.getName() + " with " + masterPvp + " PvPs");
                    }
                    p.getAppearance().setTitleColor(0x800080);

                    p.getInventory().addItem("PvP Master Reward", 10043, 1, p, null);
                    p.sendMessage("You have been rewarded with the PvP Master Hat");
                    p.broadcastUserInfo();
                }
            }



            if(!isOnline){

                if(sex==0) {


                    Announcements.getInstance().announceToAll("Today's PvP King is " + name + " with " + masterPvp + " PvPs");

                }else if(sex == 1){

                    Announcements.getInstance().announceToAll("Today's PvP Queen is " + name + " with " + masterPvp + " PvPs");
                }

            }


            state = con.prepareStatement("UPDATE characters SET pvpmaster=1 WHERE obj_Id=?");
            state.setInt(1, masterId);
            state.execute();
            state.close();


            state = con.prepareStatement("UPDATE characters SET daypvp=0 WHERE daypvp!=0");
            state.execute();
            state.close();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deletePreviousMaster() {

        Connection con = null;
        PreparedStatement state = null;
        ResultSet rset = null;
        int previousMasterId = 0;

        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            state = con.prepareStatement("SELECT obj_Id FROM characters WHERE pvpmaster=1;");
            rset = state.executeQuery();
            if (rset.next())
                previousMasterId = rset.getInt(1);

            if (previousMasterId == 0) {
                state.close();
                rset.close();
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return;
            }

            state.close();
            rset.close();

            for (L2PcInstance p : L2World.getInstance().getAllPlayers()) {

                if (p.getObjectId() == previousMasterId) {

                    p.setPvpMaster(false);

                    boolean gender = p.getAppearance().getSex();
                    if (gender) {
                        p.setTitle("");
                        p.sendMessage("You are no longer the PvP Queen");
                    } else {
                        p.setTitle("");
                        p.sendMessage("You are no longer the PvP King");
                    }

                    final L2ItemInstance acc = p.getInventory().getItemByItemId(10043);

                    if (acc != null) {
                        p.destroyItemByItemId("Consume", 10043, 1, null, false);
                    }

                    p.updatePkColor(p.getPkKills());
                    p.broadcastUserInfo();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}