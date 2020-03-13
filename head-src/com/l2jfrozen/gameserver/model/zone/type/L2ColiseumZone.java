/* L2jFrozen Project - www.l2jfrozen.com 
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
package com.l2jfrozen.gameserver.model.zone.type;

import com.l2jfrozen.gameserver.datatables.csv.MapRegionTable;
import com.l2jfrozen.gameserver.model.L2Character;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.zone.L2ZoneType;
import com.l2jfrozen.gameserver.network.SystemMessageId;
import com.l2jfrozen.gameserver.network.serverpackets.SystemMessage;

import java.util.ArrayList;
import java.util.Vector;

/**
 * An arena
 * @author durgus
 */
public class L2ColiseumZone extends L2ZoneType
{
	private static ArrayList<L2PcInstance> list = new ArrayList<>();
	
	public L2ColiseumZone(final int id)
	{
		super(id);
	}

	@Override
	protected void onEnter(final L2Character character)
	{
		
		if (character instanceof L2PcInstance)
		{
			if(!((L2PcInstance) character).isInFunEvent()) {
				if(!list.contains(character)) {
					list.add((L2PcInstance) character);
				}
			}
		}
	}
	
	@Override
	protected void onExit(final L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			if(!((L2PcInstance) character).isInFunEvent()) {
				if(list.contains(character)) {
					list.remove(character);
				}
			}
		}
	}

	public static void removeAllOthers(Vector<L2PcInstance> tvtList){
		for(int i=0; i<list.size();i++){
			if(list.get(i)!=null && !tvtList.contains(list.get(i))){
				list.get(i).teleToLocation(147531,46726,-3400,true);
			}
		}
	}

	@Override
	protected void onDieInside(L2Character character) {

	}

	@Override
	protected void onReviveInside(L2Character character) {

	}


}
