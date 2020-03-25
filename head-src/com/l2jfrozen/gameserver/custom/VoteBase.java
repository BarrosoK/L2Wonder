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

import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.util.StringUtil;
import com.l2jfrozen.util.database.L2DatabaseFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;

/**
 * @author Elfocrash
 */
public abstract class VoteBase {


    public String getPlayerIp(L2PcInstance player) {
        return player.getClient().getConnection().getInetAddress().getHostAddress();
    }

    public abstract void reward(L2PcInstance player);

    public abstract void setVoted(L2PcInstance player);

    public abstract void setRewarded(L2PcInstance player);

    public abstract boolean isRewarded(L2PcInstance player);

    public void updateDB(L2PcInstance player, String columnName) {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            PreparedStatement statement = con.prepareStatement(String.format("Update characters set %s=? where obj_Id=?", columnName));
            statement.setLong(1, System.currentTimeMillis());
            statement.setInt(2, player.getObjectId());
            statement.execute();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in VoteBase::updateDB");
        }
    }

    public boolean hasVoted(L2PcInstance player) {
        try {
            String endpoint = getApiEndpoint(player);
            System.out.println("Endpoint: " + endpoint);
            if (endpoint.startsWith("err"))
                return false;
            String voted = endpoint.startsWith("https://api.hopzone.net") ? StringUtil.substringBetween(getApiResponse(endpoint), "\"voted\":", ",\"voteTime\"") : getApiResponse(endpoint);
            if (voted == null) {
                voted = endpoint.startsWith("https://api.hopzone.net") ? StringUtil.substringBetween(getApiResponse(endpoint), "\"voted\":", ",\"hopzoneServerTime\"") : getApiResponse(endpoint);
            }
            return tryParseBool(voted);
        } catch (Exception e) {
            player.sendMessage("Something went wrong. Please try again later.");
            e.printStackTrace();
        }
        return false;
    }

    public boolean tryParseBool(String bool) {
        System.out.println("Bool: " + bool);
        if (bool.startsWith("1"))
            return true;

        return Boolean.parseBoolean(bool.trim());
    }

    public abstract String getApiEndpoint(L2PcInstance player);

    public String getApiResponse(String endpoint) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            URL url = new URL(endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/4.76");
            connection.setRequestMethod("GET");

            connection.setReadTimeout(5 * 1000);
            connection.connect();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
            }
            connection.disconnect();
            return stringBuilder.toString();
        } catch (Exception e) {
            System.out.println("Something went wrong in VoteBase::getApiResponse");
            return "err";
        }
    }

}