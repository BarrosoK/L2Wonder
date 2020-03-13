/*
* This file is for L2J-Frozen Interlude Source
* You have to include it in head-src/com/l2jfrozen/gameserver/handler/voicedcommandhandlers/ folder, before compilation
* You will also have to modify head-src/com/l2jfrozen/gameserver/handler/VoicedCommandHandler.java file and add the following:
* ->>  import com.l2jfrozen.gameserver.handler.voicedcommandhandlers.RewardVote;
* ->>  registerVoicedCommandHandler(new RewardVote());
*/
package com.l2jfrozen.gameserver.handler.voicedcommandhandlers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.l2jfrozen.util.database.DatabaseUtils;
import com.l2jfrozen.util.random.Rnd;
import com.l2jfrozen.gameserver.thread.ThreadPoolManager;
import com.l2jfrozen.util.database.L2DatabaseFactory;
import com.l2jfrozen.gameserver.handler.IVoicedCommandHandler;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jfrozen.gameserver.network.VoteRead;

public class RewardVote implements IVoicedCommandHandler
{
	private static enum ValueType
	{
		ACCOUNT_NAME,
		IP_ADRESS,
		HWID
	}

	private static final Logger _log = LoggerFactory.getLogger(RewardVote.class);

	private static final String[] COMMANDS_LIST = new String[] { "getreward" };

	private static final long INTERVAL = 1 * 60 * 1000; // 5 minutes.

	public List<L2PcInstance> _safePeople = new ArrayList<L2PcInstance>();

	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params)
	{
		if(command.equalsIgnoreCase("getreward"))
		{
			if(_safePeople.contains(activeChar))
			{
				activeChar.sendMessage("You can use this command only once every 1 minute.");
				return false;
			}
			
			_safePeople.add(activeChar);
			ThreadPoolManager.getInstance().scheduleGeneral(new PopSafePlayer(activeChar), INTERVAL);

			//getting IP of client, here we will have to check for HWID when we have LAMEGUARD
			String IPClient= activeChar.getClient().getConnection().getInetAddress().getHostAddress();
			//String IPIntern= activeChar.getClient().getConnectionAddress().get
			//sending IP to client for debug purpose
			//??
			//Return 0 if he didnt voted. Date when he voted on website
			long dateHeVotedOnWebsite = VoteRead.checkVotedIP(IPClient);
			if(dateHeVotedOnWebsite > 0)
			{
				if(activeChar.getLevel() < 76)
				{
					activeChar.sendMessage("You need to be at least level 76 to use this command.");
					return false;
				}

				//String uniqueID = activeChar.getNetConnection().getHWID();
				//if(uniqueID == null)
					//uniqueID = "";

				//Calculate if he can take reward
				if(canTakeReward(dateHeVotedOnWebsite, IPClient, activeChar))
				{
					//int[] reward = getReward();
					//if(reward.length < 2)
						//return false;

					activeChar.sendMessage("Successfully rewarded.");
   
					_log.info("Char ID: + 1");
					insertInDataBase(dateHeVotedOnWebsite, IPClient, activeChar);
					activeChar.addItem("Reward Vote", 5557, 1, activeChar, true);//put your reward item ID and amount.
					//activeChar.addItem(activeChar, reward[0], reward[1]);
				}
			}
			else //He didnt vote.
			{
				activeChar.sendMessage("You haven't voted.");
				return false;
			}
			return true;
		}
		return false;
	}

	private static void insertInDataBase(long dateHeVotedOnWebsite, String IPClient, L2PcInstance activeChar)
	{
		insertInDataBase(dateHeVotedOnWebsite, activeChar.getAccountName(), ValueType.ACCOUNT_NAME);
		insertInDataBase(dateHeVotedOnWebsite, IPClient, ValueType.IP_ADRESS);
		//insertInDataBase(dateHeVotedOnWebsite, HwID, ValueType.HWID);
	}

	private static void insertInDataBase(long dateHeVotedOnWebsite, String value, ValueType type)
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM votes WHERE value=? AND value_type=?");
			statement.setString(1, value);   
			statement.setInt(2, type.ordinal());
			rset = statement.executeQuery();
			
			if(rset.next()) // He already exit in database because he voted before.
			{
				int count = rset.getInt("vote_count");
				PreparedStatement statement2 = null;
				try
				{
					statement2 = con.prepareStatement("UPDATE votes SET date_voted_website=?, date_take_reward_in_game=?, vote_count=? WHERE value=? AND value_type=?");
					statement2.setLong(1, dateHeVotedOnWebsite);
					statement2.setLong(2, (System.currentTimeMillis() / 1000L));
					statement2.setInt(3, (count + 1));
					statement2.setString(4, value);   
					statement2.setInt(5, type.ordinal());
					statement2.executeUpdate();
				}
				catch(SQLException e)
				{
					_log.error("RewardVote:insertInDataBase(long,String,ValueType): " + e, e);
				}
				finally
				{
					DatabaseUtils.closeDatabaseSR(statement2, rset);
				}
			}
			else
			{
				PreparedStatement statement2 = null;
				try
				{
					statement2 = con.prepareStatement("INSERT INTO votes(value, value_type, date_voted_website, date_take_reward_in_game, vote_count) VALUES (?, ?, ?, ?, ?)");
					statement2.setString(1, value);
					statement2.setInt(2, type.ordinal());
					statement2.setLong(3,  dateHeVotedOnWebsite);
					statement2.setLong(4, (System.currentTimeMillis() / 1000L));
					statement2.setInt(5, 1);
					statement2.execute();
				}
				catch(SQLException e)
				{
					_log.error("RewardVote:insertInDataBase(long,String,ValueType): " + e, e);
				}
				finally
				{
					DatabaseUtils.closeDatabaseSR(statement2, rset);
				}
			}
		}
		catch(SQLException e)
		{
			_log.error("RewardVote:insertInDataBase(long,String,ValueType): " + e, e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS_LIST;
	}
	
	private static boolean canTakeReward(long dateHeVotedOnWebsite, String IPClient, L2PcInstance activeChar)
	{
		int whenCanVote = canTakeReward(dateHeVotedOnWebsite, activeChar.getAccountName(), ValueType.ACCOUNT_NAME);
		int whenCanVoteIP = canTakeReward(dateHeVotedOnWebsite, IPClient, ValueType.IP_ADRESS);
		//int whenCanVoteHWID = canTakeReward(dateHeVotedOnWebsite, HwID, ValueType.HWID);

		whenCanVote = Math.max(whenCanVote, Math.max(whenCanVoteIP, 0));
		
		if(whenCanVote > 0)
		{
			if(whenCanVote > 60)
				activeChar.sendMessage("You can vote only once every 12 hours. You still have to wait " + (int) (whenCanVote / 60) + " hours and " + (whenCanVote % 60) + " minute(s).");
			else
				activeChar.sendMessage("You can vote only once every 12 hours. You still have to wait " + whenCanVote + " minute(s).");
			return false;
		}
		return true;
	}
	
	private static int canTakeReward(long dateHeVotedOnWebsite, String value, ValueType type)
	{
		int dateLastVote = 0; // Date When he last voted on server
		int whenCanVote = 0; // The number of minutes when he can vote
		
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT date_take_reward_in_game FROM votes WHERE value=? AND value_type=?");
			statement.setString(1, value);
			statement.setInt(2, type.ordinal());
			rset = statement.executeQuery();

			if(rset.next())
				dateLastVote = rset.getInt("date_take_reward_in_game");
		}
		catch(SQLException e)
		{
			_log.error("RewardVote:canTakeReward(long,String,String): " + e, e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		
		//The number of minutes when he can vote
		if(dateLastVote == 0)
			whenCanVote = (int) ((dateHeVotedOnWebsite - (System.currentTimeMillis() / 1000L)) / 60);
		else
			whenCanVote = (int) (((dateLastVote + 12 * 60 * 60) - (System.currentTimeMillis() / 1000L)) / 60);

		return whenCanVote;
	}

	private class PopSafePlayer implements Runnable //this is the class to remove safe players to be reported again after 10 minutes
    {
		private L2PcInstance _safeplayer;

		public PopSafePlayer(L2PcInstance safeplayer)
		{
			_safeplayer = safeplayer;
		}

		public void run()
		{
			if(_safePeople.contains(_safeplayer))
				_safePeople.remove(_safeplayer);
		}
	}

/*	public static int[] getReward()
	{
		if(Rnd.chance(99))
			return MISC_CATEGORY[Rnd.get(MISC_CATEGORY.length)];
		else
			return BLESSED_ENCHANTS_CATEGORY[Rnd.get(BLESSED_ENCHANTS_CATEGORY.length)];
	}
*/


}