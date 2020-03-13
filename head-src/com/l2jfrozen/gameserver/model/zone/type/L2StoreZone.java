package com.l2jfrozen.gameserver.model.zone.type;

import com.l2jfrozen.gameserver.datatables.csv.MapRegionTable;
import com.l2jfrozen.gameserver.model.L2Character;
import com.l2jfrozen.gameserver.model.L2Party;
import com.l2jfrozen.gameserver.model.actor.instance.*;
import com.l2jfrozen.gameserver.model.scripts.ArenaFight;
import com.l2jfrozen.gameserver.model.zone.L2ZoneType;
import com.l2jfrozen.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jfrozen.util.L2FastList;
import javolution.util.FastMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by Server1 on 7/20/2017.
 */

public class L2StoreZone extends L2ZoneType {


    public L2StoreZone(final int id) {
        super(id);
    }


    @Override
    protected void onEnter(final L2Character character) {


        if (character instanceof L2PcInstance) {

            ((L2PcInstance) character).isInsideStoreZone = true;
            character.sendPacket(new ExShowScreenMessage("You entered Store Zone",3000));

        }

    }

    @Override
    protected void onExit(final L2Character character) {

        if (character instanceof L2PcInstance) {

            ((L2PcInstance) character).isInsideStoreZone = false;
            character.sendPacket(new ExShowScreenMessage("You left Store Zone",3000));

        }

    }

    @Override
    protected void onDieInside(L2Character character) {

    }

    @Override
    protected void onReviveInside(L2Character character) {

    }

}


