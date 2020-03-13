package com.l2jfrozen.gameserver.handler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.model.L2World;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.entity.Announcements;
import com.l2jfrozen.gameserver.powerpak.PowerPakConfig;
import com.l2jfrozen.gameserver.thread.ThreadPoolManager;

/**
 * @author L2jFrozen <br>
 * <br>
 * Network working with L2jFrozen AutoVoteReward: <br>
 * Hopzone -> http://l2.hopzone.net/ <br>
 * Topzone -> http://l2topzone.com/ <br>
 * L2network -> http://l2network.eu/ <br>
 */

public class AutoVoteRewardHandler {
    protected static final Logger LOGGER = Logger.getLogger(AutoVoteRewardHandler.class);

    protected List<String> already_rewardedTop;
    protected List<String> already_rewardedHop;
    protected List<String> already_rewardedNet;

    private int _l2networkVotesCount = 0;
    private int _hopzoneVotesCount = 0;
    private int _topzoneVotesCount = 0;

    protected static boolean _l2network = false;
    protected static boolean _topzone = false;
    protected static boolean _hopzone = false;

    private AutoVoteRewardHandler() {
        LOGGER.info("Vote Reward System Initiated.");

        if (_hopzone) {
            int hopzone_votes = getHopZoneVotes();

            if (hopzone_votes == -1) {
                hopzone_votes = 0;
            }

            setHopZoneVoteCount(hopzone_votes);
        }

        if (_l2network) {
            int l2network_votes = getL2NetworkVotes();

            if (l2network_votes == -1) {
                l2network_votes = 0;
            }

            setL2NetworkVoteCount(l2network_votes);
        }

        if (_topzone) {
            int topzone_votes = getTopZoneVotes();

            if (topzone_votes == -1) {
                topzone_votes = 0;
            }

            setTopZoneVoteCount(topzone_votes);
        }

        ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new AutoReward(), PowerPakConfig.VOTES_SYSYEM_INITIAL_DELAY, PowerPakConfig.VOTES_SYSYEM_STEP_DELAY);
    }

    protected class AutoReward implements Runnable {
        @Override
        public void run() {
            final int minutes = (PowerPakConfig.VOTES_SYSYEM_STEP_DELAY / 1000) / 60;

            if (_hopzone) {
                final int hopzone_votes = getHopZoneVotes();

                if (hopzone_votes != -1) {
                    // LOGGER.info("[AutoVoteReward] Server HOPZONE Votes: " + hopzone_votes);
                    Announcements.getInstance().gameAnnounceToAll("[AutoVoteReward] Actual HOPZONE Votes are " + hopzone_votes + "...");

                    if (hopzone_votes != 0 && hopzone_votes >= getHopZoneVoteCount() + PowerPakConfig.VOTES_FOR_REWARD) {
                        already_rewardedHop = new ArrayList<>();

                        final Collection<L2PcInstance> pls = L2World.getInstance().getAllPlayers();

                        Announcements.getInstance().gameAnnounceToAll("[AutoVoteReward] Great Work! Check your inventory for Reward!!");

                        // L2ItemInstance item;
                        for (final L2PcInstance player : pls) {
                            if (player != null && !player.isInOfflineMode() && player.isOnline() == 1) {
                                if (player._active_boxes <= 1 || (player._active_boxes > 1 && checkSingleBox(player, 1))) {

                                    player.addItem("Auto Vote Reward", 3677, 10, player, true);
                                    /*
                                    final Set<Integer> items = PowerPakConfig.VOTES_REWARDS_LIST.keySet();
                                    for (final Integer i : items) {
                                        // item = player.getInventory().getItemByItemId(i);

                                        // TODO: check on maxstack for item
                                        player.getInventory().addItem("reward", i, PowerPakConfig.VOTES_REWARDS_LIST.get(i), player, null);

                                    }
*/
                                }
                            }
                        }
                        setHopZoneVoteCount(hopzone_votes);
                    }
                    Announcements.getInstance().gameAnnounceToAll("[AutoVoteReward] Next HOPZONE Reward in " + minutes + " minutes at " + (getHopZoneVoteCount() + PowerPakConfig.VOTES_FOR_REWARD) + " Votes!!");
                    // site web
                    Announcements.getInstance().gameAnnounceToAll("[SiteWeb] " + PowerPakConfig.SERVER_WEB_SITE);

                }

            }

            if (_topzone && _hopzone && PowerPakConfig.VOTES_SYSYEM_STEP_DELAY > 0) {
                try {
                    Thread.sleep(PowerPakConfig.VOTES_SYSYEM_STEP_DELAY / 2);
                } catch (final InterruptedException e) {
                    if (Config.ENABLE_ALL_EXCEPTIONS)
                        e.printStackTrace();
                }
            }

            if (_topzone) {
                final int topzone_votes = getTopZoneVotes();

                if (topzone_votes != -1) {
                    //LOGGER.info("[AutoVoteReward] Server TOPZONE Votes: " + topzone_votes);
                    Announcements.getInstance().gameAnnounceToAll("[AutoVoteReward] Actual TOPZONE Votes are " + topzone_votes + "...");

                    if (topzone_votes != 0 && topzone_votes >= getTopZoneVoteCount() + PowerPakConfig.VOTES_FOR_REWARD) {
                        already_rewardedTop = new ArrayList<>();

                        final Collection<L2PcInstance> pls = L2World.getInstance().getAllPlayers();

                        Announcements.getInstance().gameAnnounceToAll("[AutoVoteReward] Great Work! Check your inventory for Reward!!");

                        // L2ItemInstance item;
                        for (final L2PcInstance player : pls) {
                            if (player != null && !player.isInOfflineMode() && player.isOnline() == 1) {
                                if (player._active_boxes <= 1 || (player._active_boxes > 1 && checkSingleBox(player, 2))) {

                                    player.addItem("Auto Vote Reward", 3677, 10, player, true);
                                   /* final Set<Integer> items = PowerPakConfig.VOTES_REWARDS_LIST.keySet();
                                    for (final Integer i : items) {
                                        // item = player.getInventory().getItemByItemId(i);

                                        // TODO: check on maxstack for item
                                        player.getInventory().addItem("reward", i, PowerPakConfig.VOTES_REWARDS_LIST.get(i), player, null);
                                    }*/
                                }
                            }
                        }
                        setTopZoneVoteCount(topzone_votes);
                    }

                    Announcements.getInstance().gameAnnounceToAll("[AutoVoteReward] Next TOPZONE Reward in " + minutes + " minutes at " + (getTopZoneVoteCount() + PowerPakConfig.VOTES_FOR_REWARD) + " Votes!!");
                    // site web
                    Announcements.getInstance().gameAnnounceToAll("[SiteWeb] " + PowerPakConfig.SERVER_WEB_SITE);
                }
            }

            if (_topzone && _hopzone && _l2network && PowerPakConfig.VOTES_SYSYEM_STEP_DELAY > 0) {
                try {
                    Thread.sleep(PowerPakConfig.VOTES_SYSYEM_STEP_DELAY / 2);
                } catch (final InterruptedException e) {
                    if (Config.ENABLE_ALL_EXCEPTIONS)
                        e.printStackTrace();
                }
            }

            if (_l2network) {
                final int l2network_votes = getL2NetworkVotes();

                if (l2network_votes != -1) {
                    //  LOGGER .info("[AutoVoteReward] Server L2NETWORK Votes: " + l2network_votes);
                    Announcements.getInstance().gameAnnounceToAll("[AutoVoteReward] Actual L2Network Votes are " + l2network_votes + "...");

                    if (l2network_votes != 0 && l2network_votes >= getL2NetworkVoteCount() + PowerPakConfig.VOTES_FOR_REWARD) {
                        already_rewardedNet = new ArrayList<>();

                        final Collection<L2PcInstance> pls = L2World.getInstance().getAllPlayers();

                        Announcements.getInstance().gameAnnounceToAll("[AutoVoteReward] Great Work! Check your inventory for Reward!!");

                        // L2ItemInstance item;
                        for (final L2PcInstance player : pls) {
                            if (player != null && !player.isInOfflineMode() && player.isOnline() == 1) {
                                if (player._active_boxes <= 1 || (player._active_boxes > 1 && checkSingleBox(player, 3))) {

                                    player.addItem("Auto Vote Reward", 3677, 10, player, true);
                                   /* final Set<Integer> items = PowerPakConfig.VOTES_REWARDS_LIST.keySet();
                                    for (final Integer i : items) {
                                        // item = player.getInventory().getItemByItemId(i);

                                        // TODO: check on maxstack for item
                                        player.getInventory().addItem("reward", i, PowerPakConfig.VOTES_REWARDS_LIST.get(i), player, null);
                                    }*/
                                }
                            }
                        }
                        setL2NetworkVoteCount(l2network_votes);
                    }
                    Announcements.getInstance().gameAnnounceToAll("[AutoVoteReward] Next L2Network Reward in " + minutes + " minutes at " + (getL2NetworkVoteCount() + PowerPakConfig.VOTES_FOR_REWARD) + " Votes!!");
                    // site web
                    Announcements.getInstance().gameAnnounceToAll("[SiteWeb] " + PowerPakConfig.SERVER_WEB_SITE);
                }
            }
        }
    }

    // Check boxes
    protected boolean checkSingleBox(final L2PcInstance player, int platform) {
        if (player == null)
            return false;

        if (player.getClient() != null && player.getClient().getConnection() != null && !player.getClient().getConnection().isClosed() && player.isOnline() == 1 && !player.isInOfflineMode()) {
            final String playerip = player.getClient().getConnection().getInetAddress().getHostAddress();
            switch (platform) {
                case 1: {
                    if (playerip == null || already_rewardedHop == null || already_rewardedHop.contains(playerip))
                        return false;
                    already_rewardedHop.add(playerip);
                    return true;
                }
                case 2: {
                    if (playerip == null || already_rewardedTop == null || already_rewardedTop.contains(playerip))
                        return false;
                    already_rewardedTop.add(playerip);
                    return true;
                }
                case 3: {
                    if (playerip == null || already_rewardedNet == null || already_rewardedNet.contains(playerip))
                        return false;
                    already_rewardedNet.add(playerip);
                    return true;
                }
            }

            return false;
        }

        // if no connection (maybe offline shop) dnt reward
        return false;
    }

    public int getHopZoneVotes() {
        int votes = -1;
        try {

            final URL obj = new URL(PowerPakConfig.VOTES_SITE_HOPZONE_URL);
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
            LOGGER.warn("[AutoVoteReward] Server HOPZONE is offline or something is wrong in link", e);
            Announcements.getInstance().gameAnnounceToAll("[AutoVoteReward] HOPZONE is offline. We will check reward as it will be online again");
        }
        return votes;
    }

    /*
    protected int getHopZoneVotes()
    {
        int votes = -1;

        try
        {
            final WebClient webClient = new WebClient(BrowserVersion.CHROME);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setPrintContentOnFailingStatusCode(false);

            final HtmlPage page = webClient.getPage(PowerPakConfig.VOTES_SITE_HOPZONE_URL);

            final String fullPage = page.asXml();
            final int constrainA = fullPage.indexOf("rank anonymous tooltip") + 24;
            String voteSection = fullPage.substring(constrainA);
            final int constrainB = voteSection.indexOf("span") - 2;
            voteSection = voteSection.substring(0, constrainB).trim();
            votes = Integer.parseInt(voteSection);

            // Try to free all the freaking resources
            page.cleanUp();
            webClient.getJavaScriptEngine().shutdown();
            webClient.closeAllWindows();
        }
        catch (final Exception e)
        {
            LOGGER.warn("[AutoVoteReward] Server HOPZONE is offline or something is wrong in link", e);
            Announcements.getInstance().gameAnnounceToAll("[AutoVoteReward] HOPZONE is offline. We will check reward as it will be online again");
        }

        return votes;
    }
    */
    protected int getTopZoneVotes() {
        URL url = null;
        URLConnection con = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;


        try {
            con = new URL("http://l2topzone.com/tv.php?id=15010").openConnection();
            con.addRequestProperty("User-Agent", "L2TopZone");
            isr = new InputStreamReader(con.getInputStream());
            br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {

                int votes = Integer.valueOf(line);
                return votes;

            }

            br.close();
            isr.close();

        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Error while getting server vote count on Topzone.");
        }

        return -1;
    }

    /*
        public int getL2NetworkVotes() {
            return queryAPI(1,null);
        }

        private int queryAPI(int type, String player) {
            try {
                String postParameters = "apiKey=8ecdb67f994786dd8fa51789d482e83c";
                postParameters += "&type=" + type;
                if (player != null && !player.equals(""))
                    postParameters += "&player=" + player;

                byte[] postData = postParameters.getBytes(Charset.forName("UTF-8"));

                URL url = new URL("https://l2network.eu/?a=details&u=L2-Bezaleel");
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("charset", "utf-8");
                conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36"); // so cloudflare doesn't 403 us
                conn.setDoOutput(true);

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.write(postData);
                os.flush();
                os.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return Integer.parseInt(response.toString());
            } catch (Exception e) {
                System.out.println("Error querying L2Network API: " + e.getMessage());
                e.printStackTrace();
            }

            return 0;
        }

     */
    public int getL2NetworkVotes() {

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


    /*

    protected int getL2NetworkVotes()
    {
        int votes = -1;
        URL url = null;
        URLConnection con = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader in = null;

        try
        {
            url = new URL(PowerPakConfig.VOTES_SITE_L2NETWORK_URL);
            con = url.openConnection();
            con.addRequestProperty("User-Agent", "L2Network");
            is = con.getInputStream();
            isr = new InputStreamReader(is);
            in = new BufferedReader(isr);
            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
                if (inputLine.contains("color:#e7ebf2"))
                {
                    votes = Integer.valueOf(inputLine.split(">")[2].replace("</b", ""));
                    break;
                }
            }
        }
        catch (final Exception e)
        {
            LOGGER.warn("[AutoVoteReward] Server L2NETWORK is offline or something is wrong in link");
            Announcements.getInstance().gameAnnounceToAll("[AutoVoteReward] L2Network is offline. We will check reward as it will be online again");
            // e.printStackTrace();
        }
        finally
        {
            if (in != null)
                try
                {
                    in.close();
                }
                catch (final IOException e1)
                {
                    e1.printStackTrace();
                }
            if (isr != null)
                try
                {
                    isr.close();
                }
                catch (final IOException e1)
                {
                    e1.printStackTrace();
                }
            if (is != null)
                try
                {
                    is.close();
                }
                catch (final IOException e1)
                {
                    e1.printStackTrace();
                }
        }
        return votes;
    }
    */
    protected void setHopZoneVoteCount(final int voteCount) {
        _hopzoneVotesCount = voteCount;
    }

    protected int getHopZoneVoteCount() {
        return _hopzoneVotesCount;
    }

    protected void setTopZoneVoteCount(final int voteCount) {
        _topzoneVotesCount = voteCount;
    }

    protected int getTopZoneVoteCount() {
        return _topzoneVotesCount;
    }

    protected void setL2NetworkVoteCount(final int voteCount) {
        _l2networkVotesCount = voteCount;
    }

    protected int getL2NetworkVoteCount() {
        return _l2networkVotesCount;
    }

    public static AutoVoteRewardHandler getInstance() {
        Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);

        if (PowerPakConfig.VOTES_SITE_HOPZONE_URL != null && !PowerPakConfig.VOTES_SITE_HOPZONE_URL.equals(""))
            _hopzone = true;

        if (PowerPakConfig.VOTES_SITE_TOPZONE_URL != null && !PowerPakConfig.VOTES_SITE_TOPZONE_URL.equals(""))
            _topzone = true;

        if (PowerPakConfig.VOTES_SITE_L2NETWORK_URL != null && !PowerPakConfig.VOTES_SITE_L2NETWORK_URL.equals(""))
            _l2network = true;

        if (_topzone || _hopzone || _l2network)
            return SingletonHolder._instance;

        return null;
    }

    @SuppressWarnings("synthetic-access")
    private static class SingletonHolder {
        protected static final AutoVoteRewardHandler _instance = new AutoVoteRewardHandler();
    }
}