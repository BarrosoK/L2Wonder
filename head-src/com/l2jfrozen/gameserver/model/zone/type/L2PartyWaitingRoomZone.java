package com.l2jfrozen.gameserver.model.zone.type;

import com.l2jfrozen.gameserver.datatables.csv.MapRegionTable;
import com.l2jfrozen.gameserver.model.L2Character;
import com.l2jfrozen.gameserver.model.L2Party;
import com.l2jfrozen.gameserver.model.actor.instance.L2ItemInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PlayableInstance;
import com.l2jfrozen.gameserver.model.zone.L2ZoneType;
import com.l2jfrozen.util.L2FastList;
import javolution.util.FastMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by Server1 on 7/20/2017.
 */

public class L2PartyWaitingRoomZone extends L2ZoneType {


    public L2PartyWaitingRoomZone(final int id) {
        super(id);
    }


    @Override
    protected void onEnter(final L2Character character) {
		character.setInsideZone(L2Character.ZONE_PEACE, true);

        if (character instanceof L2PcInstance) {
            ((L2PcInstance) character).isInsideWaitingRoom = true;
            L2PcInstance player = (L2PcInstance) character;

            player.sendMessage("You entered the waiting room.");

            player = null;
        }

    }

    @Override
    protected void onExit(final L2Character character) {
		character.setInsideZone(L2Character.ZONE_PEACE, false);
        if (character instanceof L2PcInstance) {
            ((L2PcInstance) character).isInsideWaitingRoom = false;
        }
    }

    @Override
    protected void onDieInside(L2Character character) {

    }

    @Override
    protected void onReviveInside(L2Character character) {

    }

}


