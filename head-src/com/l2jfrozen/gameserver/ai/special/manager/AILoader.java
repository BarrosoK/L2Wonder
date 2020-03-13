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
package com.l2jfrozen.gameserver.ai.special.manager;

import com.l2jfrozen.gameserver.ai.special.*;
import org.apache.log4j.Logger;

import com.l2jfrozen.gameserver.thread.ThreadPoolManager;

/**
 * @author qwerty
 */

public class AILoader
{
	private static final Logger LOGGER = Logger.getLogger(AILoader.class);

	public static void init()
	{
		LOGGER.info("AI load:");

		LOGGER.info(" - Antharas");
		ThreadPoolManager.getInstance().scheduleAi(new Antharas(-1, "antharas", "ai"), 100);

		LOGGER.info(" - Baium");
		ThreadPoolManager.getInstance().scheduleAi(new Baium(-1, "baium", "ai"), 200);

		LOGGER.info(" - Core");
		ThreadPoolManager.getInstance().scheduleAi(new Core(-1, "core", "ai"), 300);

		LOGGER.info(" - Queen Ant");
		ThreadPoolManager.getInstance().scheduleAi(new QueenAnt(-1, "queen_ant", "ai"), 400);

		LOGGER.info(" - Orfen");
		ThreadPoolManager.getInstance().scheduleAi(new Orfen(-1, "Orfen", "ai"), 500);

		LOGGER.info(" - Zaken");
		ThreadPoolManager.getInstance().scheduleAi(new Zaken(-1, "Zaken", "ai"), 600);

		LOGGER.info(" - Valakas");
		ThreadPoolManager.getInstance().scheduleAi(new Valakas(-1, "valakas", "ai"), 700);

		LOGGER.info(" - Crixus");
		ThreadPoolManager.getInstance().scheduleAi(new Crixus(-1, "crixus", "ai"), 800);

		LOGGER.info(" - Spartakus");
		ThreadPoolManager.getInstance().scheduleAi(new Spartakus(-1, "spartakus", "ai"), 900);

		LOGGER.info(" - Onaemaus");
		ThreadPoolManager.getInstance().scheduleAi(new Onaemaus(-1, "onaemaus", "ai"), 1000);

		LOGGER.info(" - Gannicus");
		ThreadPoolManager.getInstance().scheduleAi(new Gannicus(-1, "gannicus", "ai"), 1100);

		LOGGER.info(" - Agron");
		ThreadPoolManager.getInstance().scheduleAi(new Agron(-1, "agron", "ai"), 1200);

		LOGGER.info(" - Ashur");
		ThreadPoolManager.getInstance().scheduleAi(new Ashur(-1, "ashur", "ai"), 1300);

		LOGGER.info(" - Barakiel");
		ThreadPoolManager.getInstance().scheduleAi(new Barakiel(-1, "Barakiel", "ai"), 1400);
	}
}
