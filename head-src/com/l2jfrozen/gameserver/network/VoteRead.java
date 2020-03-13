/*
*This file is for L2J-Frozen Interlude Source
*You have to include it in com\l2jfrozen\gameserver\network folder, before compilation
*/
package com.l2jfrozen.gameserver.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VoteRead 
{
	private static final Logger _log = LoggerFactory.getLogger(VoteRead.class);

	public static long checkVotedIP(String IP)
	{
		int count = 0;
		long voteDate = 0;

		URL url = null;
		InputStreamReader isr = null;
		try
		{
			//HERE YOU HAVE TO ENTER YOUR SERVER'S ID
			url = new URL("https://l2net.net/pages/votecheck.php?id=123&ip="+IP);
			
			isr = new InputStreamReader(url.openStream());

			BufferedReader br = new BufferedReader(isr);
			String strLine;
			while((strLine = br.readLine()) != null) //Read File Line By Line
			{
				if(!strLine.equals("FALSE"))
					voteDate = System.currentTimeMillis() / 1000L;

				_log.info("VoteRead: DATE[" + voteDate + "], IP[" + IP + "]");
			}

			isr.close(); // Close the input stream
		}
		catch(Exception e) // Catch exception if any
		{
			//_log.error("VoteRead: ERROR: ", e);
			return 0;
		} 

		return voteDate;
	}
}