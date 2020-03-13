package com.l2jfrozen.gameserver.model.zone.type;

import com.l2jfrozen.gameserver.datatables.csv.MapRegionTable;
import com.l2jfrozen.gameserver.model.L2Character;
import com.l2jfrozen.gameserver.model.L2Party;
import com.l2jfrozen.gameserver.model.actor.instance.*;
import com.l2jfrozen.gameserver.model.scripts.ArenaFight;
import com.l2jfrozen.gameserver.model.scripts.ArenaFightPtVsPt;
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

public class L2PartyCustomArenaZone extends L2ZoneType {


    public L2PartyCustomArenaZone(final int id) {
        super(id);
    }


    @Override
    protected void onEnter(final L2Character character) {


        if (character instanceof L2PcInstance) {

            ((L2PcInstance) character).isInsideCustomPartyArenaZone = true;

            boolean observer = false;

            ArenaFightPtVsPt arena = null;
            for (ArenaFightPtVsPt a : L2ArenaManagerInstance.fightPtVsPt) {
                if (a != null && a.getObservers().contains(character)) {
                    observer = true;
                    break;
                }
            }


            if (!L2ArenaManagerInstance.inFightOrWaiting.contains(character) && !observer && !((L2PcInstance) character).isGM()) {
                character.teleToLocation(82423, 149628, -3495, true);
                ((L2PcInstance) character).sendMessage("You are not supposed to be here.");
            }

            if(((L2PcInstance) character).isGM()){
                ((L2PcInstance) character).sendMessage("Welcome to PartyCustomArena Zone Game Master.");
            }

        }

    }

    @Override
    protected void onExit(final L2Character character) {
        if (character instanceof L2PcInstance) {
            ((L2PcInstance) character).isInsideCustomPartyArenaZone = false;

            if(((L2PcInstance) character).isGM()){
                ((L2PcInstance) character).sendMessage("You exited PartyCustomArena Zone Game Master.");
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


