/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jfrozen.gameserver.custom;

import com.l2jfrozen.Config;
import com.l2jfrozen.util.CloseUtil;
import com.l2jfrozen.util.database.DatabaseUtils;
import com.l2jfrozen.util.database.L2DatabaseFactory;
import org.apache.log4j.Logger;
import org.python.parser.ast.Str;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Elfocrash
 */
public class VoteRewardData {

    private static final String STORE_REWARD_DATA_IPS = "INSERT INTO vote_reward_ip (vote_reward_ip,vote_site,time) VALUES (?,?,?)";
    private static final String STORE_REWARD_DATA_ACCOUNTS = "INSERT INTO vote_reward_account (vote_reward_account,vote_site,time) VALUES (?,?,?)";

    protected static final Logger LOGGER = Logger.getLogger(VoteRewardData.class);

    private static VoteRewardData _instance;

    private HashMap<String, Long> rewardedIpsHop = new HashMap<>();
    private HashMap<String, Long> rewardedAccountsHop = new HashMap<>();
    private HashMap<String, Long> rewardedIpsTop = new HashMap<>();
    private HashMap<String, Long> rewardedAccountsTop = new HashMap<>();
    private HashMap<String, Long> rewardedIpsNet = new HashMap<>();
    private HashMap<String, Long> rewardedAccountsNet = new HashMap<>();


    public static VoteRewardData getInstance() {
        if (_instance == null) {
            _instance = new VoteRewardData();
        }

        return _instance;
    }

    private VoteRewardData() {
        load();
    }

    public HashMap<String, Long> getRewardedAccountsHop() {
        return rewardedAccountsHop;
    }


    public HashMap<String, Long> getRewardedIpsHop() {
        return rewardedIpsHop;
    }


    public HashMap<String, Long> getRewardedAccountsNet() {
        return rewardedAccountsNet;
    }


    public HashMap<String, Long> getRewardedIpsNet() {
        return rewardedIpsNet;
    }


    public HashMap<String, Long> getRewardedAccountsTop() {
        return rewardedAccountsTop;
    }


    public HashMap<String, Long> getRewardedIpsTop() {
        return rewardedIpsTop;
    }

    public void load() {

        loadIpRewards();
        loadAccountRewards();
        truncateTableIps();
        truncateTableAccounts();

    }

    private void loadIpRewards() {

        Connection con = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection(false);
            PreparedStatement statement;
            statement = con.prepareStatement("SELECT vote_reward_ip,vote_site,time FROM vote_reward_ip");

            final ResultSet rset = statement.executeQuery();

            while (rset.next()) {

                final Long time = rset.getLong("time");
                if (time > 0 && time + 43200000 < System.currentTimeMillis()) {
                    continue;
                }

                final String ip = rset.getString("vote_reward_ip");
                final String site = rset.getString("vote_site");

                switch (site) {
                    case "hopzone": {
                        rewardedIpsHop.put(ip, time);
                    }
                    break;
                    case "topzone": {
                        rewardedIpsTop.put(ip, time);
                    }
                    break;
                    case "network": {
                        rewardedIpsNet.put(ip, time);
                    }
                    break;

                }
            }

            DatabaseUtils.close(rset);
            DatabaseUtils.close(statement);
        } catch (final Exception e) {
            if (Config.ENABLE_ALL_EXCEPTIONS)
                e.printStackTrace();

            LOGGER.warn("could not restore hopzone reward ip data:" + e);
        } finally {
            CloseUtil.close(con);
            con = null;
        }
    }

    private void loadAccountRewards() {
        Connection con = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection(false);
            PreparedStatement statement;
            statement = con.prepareStatement("SELECT vote_reward_account,vote_site,time FROM vote_reward_account");

            final ResultSet rset = statement.executeQuery();

            while (rset.next()) {

                final Long time = rset.getLong("time");
                if (time > 0 && time + 43200000 < System.currentTimeMillis()) {
                    continue;
                }

                final String account = rset.getString("vote_reward_account");
                final String site = rset.getString("vote_site");


                switch (site) {
                    case "hopzone": {
                        rewardedAccountsHop.put(account, time);
                    }
                    break;
                    case "topzone": {
                        rewardedAccountsTop.put(account, time);
                    }
                    break;
                    case "network": {
                        rewardedAccountsNet.put(account, time);
                    }
                    break;
                }

            }

            DatabaseUtils.close(rset);
            DatabaseUtils.close(statement);
        } catch (final Exception e) {
            if (Config.ENABLE_ALL_EXCEPTIONS)
                e.printStackTrace();

            LOGGER.warn("could not restore hopzone reward ip data:" + e);
        } finally {
            CloseUtil.close(con);
            con = null;
        }
    }

    private void truncateTableIps() {

        Connection con = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection(false);
            PreparedStatement statement;
            statement = con.prepareStatement("TRUNCATE vote_reward_ip");

            final ResultSet rset = statement.executeQuery();


            DatabaseUtils.close(rset);
            DatabaseUtils.close(statement);
        } catch (final Exception e) {
            if (Config.ENABLE_ALL_EXCEPTIONS)
                e.printStackTrace();

            LOGGER.warn("could not truncate reward ip data:" + e);
        } finally {
            CloseUtil.close(con);
            con = null;
        }

    }

    private void truncateTableAccounts() {

        Connection con = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection(false);
            PreparedStatement statement;
            statement = con.prepareStatement("TRUNCATE vote_reward_account");

            final ResultSet rset = statement.executeQuery();


            DatabaseUtils.close(rset);
            DatabaseUtils.close(statement);
        } catch (final Exception e) {
            if (Config.ENABLE_ALL_EXCEPTIONS)
                e.printStackTrace();

            LOGGER.warn("could not truncate reward ip data:" + e);
        } finally {
            CloseUtil.close(con);
            con = null;
        }

    }

    public void store() {

        LOGGER.info("Storing Vote Reward Data...");

        List<String> keys;

        keys = new ArrayList<>(rewardedIpsHop.keySet());
        for (int i = 0; i < rewardedIpsHop.size(); i++) {
            if (rewardedIpsHop.get(keys.get(i)) + 43200000 < System.currentTimeMillis()) {
                continue;
            }
            storeIpRewards(keys.get(i), "hopzone", rewardedIpsHop.get(keys.get(i)));
        }

        keys = new ArrayList<>(rewardedAccountsHop.keySet());
        for (int i = 0; i < rewardedAccountsHop.size(); i++) {
            if (rewardedAccountsHop.get(keys.get(i)) + 43200000 < System.currentTimeMillis()) {
                continue;
            }
            storeAccountRewards(keys.get(i), "hopzone", rewardedAccountsHop.get(keys.get(i)));
        }
        keys = new ArrayList<>(rewardedIpsTop.keySet());
        for (int i = 0; i < rewardedIpsTop.size(); i++) {
            if (rewardedIpsTop.get(keys.get(i)) + 43200000 < System.currentTimeMillis()) {
                continue;
            }
            storeIpRewards(keys.get(i), "topzone", rewardedIpsTop.get(keys.get(i)));
        }

        keys = new ArrayList<>(rewardedAccountsTop.keySet());
        for (int i = 0; i < rewardedAccountsTop.size(); i++) {
            if (rewardedAccountsTop.get(keys.get(i)) + 43200000 < System.currentTimeMillis()) {
                continue;
            }
            storeAccountRewards(keys.get(i), "topzone", rewardedAccountsTop.get(keys.get(i)));
        }
        keys = new ArrayList<>(rewardedIpsNet.keySet());
        for (int i = 0; i < rewardedIpsNet.size(); i++) {
            if (rewardedIpsNet.get(keys.get(i)) + 43200000 < System.currentTimeMillis()) {
                continue;
            }
            storeIpRewards(keys.get(i), "network", rewardedIpsNet.get(keys.get(i)));
        }

        keys = new ArrayList<>(rewardedAccountsNet.keySet());
        for (int i = 0; i < rewardedAccountsNet.size(); i++) {
            if (rewardedAccountsNet.get(keys.get(i)) + 43200000 < System.currentTimeMillis()) {
                continue;
            }
            storeAccountRewards(keys.get(i), "network", rewardedAccountsNet.get(keys.get(i)));
        }

    }


    public void storeIpRewards(String ip, String site, Long rewardTime) {
        Connection con = null;

        try {

            con = L2DatabaseFactory.getInstance().getConnection(false);
            PreparedStatement statement;

            // Update base class
            statement = con.prepareStatement(STORE_REWARD_DATA_IPS);
            statement.setString(1, ip);
            statement.setString(2, site);
            statement.setLong(3, rewardTime);

            statement.execute();
            DatabaseUtils.close(statement);
            statement = null;
        } catch (final Exception e) {
            LOGGER.warn("Could not store reward data: ");
            e.printStackTrace();
        } finally {
            CloseUtil.close(con);
        }
    }

    public void storeAccountRewards(String accountName, String site, Long rewardTime) {
        Connection con = null;

        try {

            con = L2DatabaseFactory.getInstance().getConnection(false);
            PreparedStatement statement;

            // Update base class
            statement = con.prepareStatement(STORE_REWARD_DATA_ACCOUNTS);
            statement.setString(1, accountName);
            statement.setString(2, site);
            statement.setLong(3, rewardTime);

            statement.execute();
            DatabaseUtils.close(statement);
            statement = null;
        } catch (final Exception e) {
            LOGGER.warn("Could not store reward data: ");
            e.printStackTrace();
        } finally {
            CloseUtil.close(con);
        }
    }

}