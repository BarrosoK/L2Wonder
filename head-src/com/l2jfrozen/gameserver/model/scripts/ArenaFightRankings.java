package com.l2jfrozen.gameserver.model.scripts;


import com.l2jfrozen.gameserver.model.L2World;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.entity.Announcements;
import com.l2jfrozen.util.database.L2DatabaseFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by MaVeN on 18/3/2018.
 */
public class ArenaFightRankings {

    public ArenaFightRankings() {
        findRanked();
        deleteRanks();
    }

    private void findRanked() {


        int max1v1Points = 0;
        int maxPartyPoints = 0;
        String winnerPartyName = "";
        String winner1v1Name = "";
        Connection con = null;
        PreparedStatement state = null;
        ResultSet rset = null;


        try {
            con = L2DatabaseFactory.getInstance().getConnection();

            state = con.prepareStatement("SELECT char_name,arenapt_points FROM characters ORDER BY arenapt_points DESC LIMIT 1;");
            rset = state.executeQuery();


            while (rset.next()) {
                maxPartyPoints = rset.getInt("arenapt_points");
                winnerPartyName = rset.getString("char_name");
            }

            state = con.prepareStatement("SELECT char_name,arena_points FROM characters ORDER BY arena_points DESC LIMIT 1;");
            rset = state.executeQuery();

            while (rset.next()) {
                max1v1Points = rset.getInt("arena_points");
                winner1v1Name = rset.getString("char_name");
            }

            String query = "insert into rankings_delivery_pending (character_name, type, points)"
                    + " values (?, ?, ?)";

            // create the mysql insert preparedstatement
            state = con.prepareStatement(query);

            if (max1v1Points > 0) {

                state.setString(1, winner1v1Name);
                state.setInt(2, 0);
                state.setInt(3, max1v1Points);
                state.execute();

            }

            if (maxPartyPoints > 0) {

                state.setString(1, winnerPartyName);
                state.setInt(2, 1);
                state.setInt(3, maxPartyPoints);
                state.execute();

            }


            rset.close();
            state.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (state != null) {
                try {
                    state.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (rset != null) {
                try {
                    rset.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }


        for (L2PcInstance p : L2World.getInstance().getAllPlayers()) {

            if (p != null && p.isOnline() == 1) {
                if ((max1v1Points > 0 && (p.getName().equals(winner1v1Name)) || (maxPartyPoints > 0 && p.getName().equals(winnerPartyName)))) {

                    p.isEligibleForRankingsReward = true;

                }

            }
        }

        if (max1v1Points > 0 && !winner1v1Name.equals("") && maxPartyPoints > 0 && !winnerPartyName.equals("")) {
            Announcements.getInstance().announceToAll("Street Fight Rankings Announced!\n" +
                    "1v1 Rankings Winner is " + winner1v1Name + " with " + max1v1Points + " points!\n" +
                    "Party Rankings Winner is " + winnerPartyName + "'s Party with " + maxPartyPoints + " points!\n" +
                    "Visit the Arena Manager NPC to collect your rewards.");
        } else if (max1v1Points > 0 && !winner1v1Name.equals("")) {
            Announcements.getInstance().announceToAll("Street Fight Rankings Announced!\n" +
                    "1v1 Rankings Winner is " + winner1v1Name + " with " + max1v1Points + " points!\n" +
                    "Visit the Arena Manager NPC to collect your rewards.");
        } else if (maxPartyPoints > 0 && !winnerPartyName.equals("")) {
            Announcements.getInstance().announceToAll("Street Fight Rankings Announced!\n" +
                    "Party Rankings Winner is " + winnerPartyName + "'s Party with " + maxPartyPoints + " points!\n" +
                    "Visit the Arena Manager NPC to collect your rewards.");
        }


    }

    private void deleteRanks() {

        Connection con = null;
        PreparedStatement state = null;


        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            state = con.prepareStatement("UPDATE characters SET arena_points=0, arenapt_points=0 WHERE arena_points!=0 OR arenapt_points!=0");

            state.execute();

            state.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (state != null) {
                try {
                    state.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
