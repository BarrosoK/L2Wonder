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
package com.l2jfrozen.gameserver.handler.usercommandhandlers;

import com.l2jfrozen.gameserver.handler.IUserCommandHandler;
import com.l2jfrozen.gameserver.model.actor.instance.BotsPreventionManager;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.entity.Announcements;
import com.l2jfrozen.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jfrozen.gameserver.powerpak.PowerPakConfig;
import com.l2jfrozen.gameserver.thread.ThreadPoolManager;
import com.l2jfrozen.util.database.L2DatabaseFactory;
import javolution.text.TextBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Eimantas
 */
public class VotingReward implements IUserCommandHandler {
    // Queries
    private static final String DELETE_QUERY = "DELETE FROM mods_voting_reward WHERE time < ?";
    private static final String SELECT_QUERY = "SELECT * FROM mods_voting_reward";
    private static final String INSERT_QUERY = "INSERT INTO mods_voting_reward (data, scope, time, top) VALUES (?, ?, ?, ?)";

    private static final Logger _log = Logger.getLogger(VotingReward.class.getName());

    private static final long VOTING_INTERVAL = TimeUnit.HOURS.toMillis(12);

    private static final Map<UserScope, ScopeContainer> VOTTERS_CACHE = new EnumMap<>(UserScope.class);

    public static long TimeToVote = TimeUnit.SECONDS.toMillis(60);
    static boolean isVotingHopzone = false;
    static boolean isVotingTopzone = false;
    static boolean isVotingNetwork = false;
    public static String TopzoneURL = PowerPakConfig.VOTES_SITE_TOPZONE_URL;
    public static String HopZoneURL = PowerPakConfig.VOTES_SITE_HOPZONE_URL;

    static final int[] COMMANDS =
            {
                    115
            };

    public VotingReward() {
        load();
    }

    public static final VotingReward getInstance() {
        return VotingReward.SingletonHolder._instance;
    }


    private static class SingletonHolder {

        protected static final VotingReward _instance = new VotingReward();
    }

    @Override
    public boolean useUserCommand(int id, L2PcInstance activeChar) {
        if (id == COMMANDS[0]) {
            NpcHtmlMessage html = new NpcHtmlMessage(0);
            html.setHtml(showVoteHtml(activeChar));
            html.replace("%objectId%", String.valueOf(0));
            activeChar.sendPacket(html);

        }
        return false;
    }

    @Override
    public int[] getUserCommandList() {
        return COMMANDS;
    }

    public static String showVoteHtml(L2PcInstance player) {
        TextBuilder tb = new TextBuilder();
        tb.append("<html> <body> <center><table width=300 border=0>\n" +
                "<tr>\n" +
                "    <td width=270 height=100><img src=l2wonderlust.logo width=275 height=100 align=center></td>\n" +
                "</table>");
        tb.append("Vote Manager");
        tb.append("<br><br>");
        tb.append("<center><button value=\"Vote for us on L2HopZone\" action=\"bypass -h voteReward;hopzone\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\">");
        tb.append("<td align=center><img src=L2UI_CH3.herotower_deco width=256 height=32>");
        tb.append("<center<button value=\"Vote for us on L2TopZone\" action=\"bypass -h voteReward;topzone\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\">");
        tb.append("<td align=center><img src=L2UI_CH3.herotower_deco width=256 height=32>");
        tb.append("<center<button value=\"Vote for us on L2Network\" action=\"bypass -h voteReward;network\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\">");
        tb.append("</center></body></html>");

        return tb.toString();

    }

    public void parseCmd(L2PcInstance player, String command) {

        if (command.startsWith("voteReward;hopzone")) {
            voteHopzone(player);
        } else if (command.startsWith("voteReward;topzone")) {
            voteTopzone(player);
        }else if(command.startsWith("voteReward;network")){
            voteNetwork(player);
        }

    }

    public static void voteHopzone(L2PcInstance player) {
        final L2PcInstance player2 = player;
        long time = getLastVotedTime(player, "hopzone");
        if (player2.isVoting()) {
            player2.sendMessage("You are already voting!");
            return;
        }
        if (time > 0) {
            sendReEnterMessage(time, player);
            return;
        }
        if (isVotingHopzone) {
            player2.sendMessage("Someone is already voting.Please wait!");
            return;
        }
        final int currVotes = getHopzoneCurrentVotes();
        isVotingHopzone = true;

        player2.sendMessage("You have " + TimeToVote / 1000 + " seconds to vote for us on HopZone!");
        player2.sendPacket(new ExShowScreenMessage("You have " + TimeToVote / 1000 + " seconds to vote on Hopzone!",3000));
        player2.setVoting(true);
        ThreadPoolManager.getInstance().scheduleGeneral(new Runnable() {
            @Override
            public void run() {
                if (getHopzoneCurrentVotes() > currVotes) {
                    player2.sendMessage("Thank You for voting!");
                    markAsVotted(player2, "hopzone");
                    giveReward(player2);
                } else {
                    player2.sendMessage("You did not vote for server!");
                }
                isVotingHopzone = false;
                player2.setVoting(false);
            }
        }, TimeToVote);

    }

    public static void voteNetwork(L2PcInstance player) {
        final L2PcInstance player2 = player;
        long time = getLastVotedTime(player, "network");
        if (player2.isVoting()) {
            player2.sendMessage("You are already voting!");
            return;
        }
        if (time > 0) {
            sendReEnterMessage(time, player);
            return;
        }
        if (isVotingNetwork) {
            player2.sendMessage("Someone is already voting.Please wait!");
            return;
        }
        final int currVotes = getNetworkCurrentVotes();
        isVotingNetwork = true;
        player2.sendMessage("You have " + TimeToVote / 1000 + " seconds to vote for us on L2Network!");
        player2.sendPacket(new ExShowScreenMessage("You have " + TimeToVote / 1000 + " seconds to vote on L2Network!",3000));
        player2.setVoting(true);
        ThreadPoolManager.getInstance().scheduleGeneral(new Runnable() {
            @Override
            public void run() {
                if (getNetworkCurrentVotes() > currVotes) {
                    player2.sendMessage("Thank You for voting!");
                    markAsVotted(player2, "network");
                    giveReward(player2);
                } else {
                    player2.sendMessage("You did not vote for server!");
                }
                isVotingNetwork = false;
                player2.setVoting(false);
            }
        }, TimeToVote);

    }

    public static void voteTopzone(L2PcInstance player) {
        final L2PcInstance player2 = player;
        long time = getLastVotedTime(player, "topzone");
        if (player2.isVoting()) {
            player2.sendMessage("You are already voting!");
            return;
        }
        if (time > 0) {
            sendReEnterMessage(time, player);
            return;
        }
        if (isVotingTopzone) {
            player2.sendMessage("Someone is already voting.Please wait!");
            return;
        }
        final int currVotes = getTopzoneCurrentVotes();
        isVotingTopzone = true;
        player2.sendMessage("You have " + TimeToVote / 1000 + " seconds to vote for us on L2Topzone!");
        player2.sendPacket(new ExShowScreenMessage("You have " + TimeToVote / 1000 + " seconds to vote on L2Topzone!",3000));
        player2.setVoting(true);
        ThreadPoolManager.getInstance().scheduleGeneral(new Runnable() {
            @Override
            public void run() {
                if (getTopzoneCurrentVotes() > currVotes) {
                    player2.sendMessage("Thank You for voting!");
                    markAsVotted(player2, "topzone");
                    giveReward(player2);
                } else {
                    player2.sendMessage("You did not vote for server!");
                }
                isVotingTopzone = false;
                player2.setVoting(false);
            }
        }, TimeToVote);

    }

    public static int getHopzoneCurrentVotes() {
        int votes = -1;
        try {
            final URL obj = new URL("https://l2.hopzone.net/lineage2/details/102607/L2Wonderlust");
            final HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.addRequestProperty("User-Agent", "L2Hopzone");
            con.setConnectTimeout(5000);

            final int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        if (inputLine.contains("<span class=\"rank tooltip\" title")) {
                            votes = Integer.valueOf(inputLine.split(">")[2].replace("</span", ""));
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            _log.log(Level.WARNING, "Error while getting server vote count on HopZone!");
        }

        return votes;
    }

    public static int getNetworkCurrentVotes() {

        InputStreamReader isr = null;
        BufferedReader br = null;

        try {
            URLConnection con = new URL("https://l2network.eu/details/L2Wonderlust/").openConnection();
            con.addRequestProperty("User-L2Network", "Mozilla/5.0");
            isr = new InputStreamReader(con.getInputStream());
            br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("<div class=\"tls-in-sts\"><b style")) {
                    int votes = Integer.valueOf(line.split(">")[2].replace("</b", ""));
                    return votes;
                }
            }

            br.close();
            isr.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while getting server vote count from L2Network.");
        }

        return -1;
    }

        public static int getTopzoneCurrentVotes(){

        URL url = null;
        URLConnection con = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

            try
            {
                con = new URL("http://l2topzone.com/tv.php?id=15010").openConnection();
                con.addRequestProperty("User-Agent", "L2TopZone");
                isr = new InputStreamReader(con.getInputStream());
                br = new BufferedReader(isr);

                String line;
                while ((line = br.readLine()) != null)
                {

                    int votes = Integer.valueOf(line);
                    return votes;

                }

                br.close();
                isr.close();

            }

        catch (Exception e) {
            System.out.println(e);
            System.out.println("Error while getting server vote count on Topzone.");
            _log.log(Level.WARNING, "Error while getting server vote count on Topzone!");
        }

        return -1;

    }

    private static final long getLastVotedTime(L2PcInstance activeChar, String top) {
        for (Entry<UserScope, ScopeContainer> entry : VOTTERS_CACHE.entrySet()) {
            final String data = entry.getKey().getData(activeChar);
            final long reuse = entry.getValue().getReuse(data, top);
            if (reuse > 0) {
                return reuse;
            }
        }
        return 0;
    }

    private static void sendReEnterMessage(long time, L2PcInstance player) {
        if (time > System.currentTimeMillis()) {
            final long remainingTime = (time - System.currentTimeMillis()) / 1000;
            final int hours = (int) (remainingTime / 3600);
            final int minutes = (int) ((remainingTime % 3600) / 60);
            final int seconds = (int) ((remainingTime % 3600) % 60);

            String msg = "You have received your reward already try again in: " + hours + " hours";
            if (minutes > 0) {
                msg += " " + minutes + " minutes";
            }
            if (seconds > 0) {
                msg += " " + seconds + " seconds";
            }
            player.sendMessage(msg);
        }
    }

    private static final void load() {
        // Initialize the cache
        for (UserScope scope : UserScope.values()) {
            VOTTERS_CACHE.put(scope, new ScopeContainer());
        }

        // Cleanup old entries and load the data for votters
        try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement(DELETE_QUERY); Statement st = con.createStatement()) {
            ps.setLong(1, System.currentTimeMillis());
            ps.execute();

            // Load the data
            try (ResultSet rset = st.executeQuery(SELECT_QUERY)) {
                while (rset.next()) {
                    final String data = rset.getString("data");
                    final UserScope scope = UserScope.findByName(rset.getString("scope"));
                    final Long time = rset.getLong("time");
                    final String top = rset.getString("top");
                    if (scope != null) {
                        VOTTERS_CACHE.get(scope).registerVotter(data, time, top);
                    }
                }
            }
        } catch (SQLException e) {
            _log.log(Level.WARNING, VotingReward.class.getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    private enum UserScope {
        ACCOUNT {
            @Override
            public String getData(L2PcInstance player) {
                return player.getAccountName();
            }
        },
        IP {
            @Override
            public String getData(L2PcInstance player) {
                if(player.getClient().getConnection().getInetAddress().getHostAddress()==null){
                    return "";
                }
                return player.getClient().getConnection().getInetAddress().getHostAddress();
            }
        },
        //@formatter:off
        /*HWID
        {
            @Override
            public String getData(L2PcInstance player)
            {
                return player.getHWID();
            }
        }*/
        //@formatter:on
        ;

        public abstract String getData(L2PcInstance player);

        public static UserScope findByName(String name) {
            for (UserScope scope : values()) {
                if (scope.name().equals(name)) {
                    return scope;
                }
            }
            return null;
        }
    }

    private static class ScopeContainer {
        private final Map<String, Long> _HopzoneVotters = new ConcurrentHashMap<>();
        private final Map<String, Long> _TopzoneVotters = new ConcurrentHashMap<>();

        public ScopeContainer() {
        }

        public void registerVotter(String data, long reuse, String top) {
            if (top.equalsIgnoreCase("hopzone")) {
                _HopzoneVotters.put(data, reuse);
            }
            if (top.equalsIgnoreCase("topzone")) {
                _TopzoneVotters.put(data, reuse);
            }
        }

        public long getReuse(String data, String top) {
            if (top.equalsIgnoreCase("hopzone")) {
                if (_HopzoneVotters.containsKey(data)) {
                    long time = _HopzoneVotters.get(data);
                    if (time > System.currentTimeMillis()) {
                        return time;
                    }
                }
            }
            if (top.equalsIgnoreCase("topzone")) {
                if (_TopzoneVotters.containsKey(data)) {
                    long time = _TopzoneVotters.get(data);
                    if (time > System.currentTimeMillis()) {
                        return time;
                    }
                }
            }
            return 0;
        }
    }

    static void markAsVotted(L2PcInstance player, String top) {
        final long reuse = System.currentTimeMillis() + VOTING_INTERVAL;
        try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement(INSERT_QUERY)) {
            for (UserScope scope : UserScope.values()) {
                final String data = scope.getData(player);
                final ScopeContainer container = VOTTERS_CACHE.get(scope);
                container.registerVotter(data, reuse, top);

                ps.setString(1, data);
                ps.setString(2, scope.name());
                ps.setLong(3, reuse);
                ps.setString(4, top);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            _log.log(Level.WARNING, VotingReward.class.getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    static void giveReward(L2PcInstance player) {
        player.addItem("Reward", 3677, 10, player, true);
        player.addItem("Reward", 4443, 1, player, true);
    }

}