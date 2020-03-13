package com.l2jfrozen.gameserver.model.zone.type;

import com.l2jfrozen.gameserver.custom.Events.BattleRoyale;
import com.l2jfrozen.gameserver.model.L2Character;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.zone.L2ZoneType;

/**
 * Created by MaVeN on 3/16/2018.
 */

public class L2BattleRoyaleZone extends L2ZoneType {


    public L2BattleRoyaleZone(final int id) {
        super(id);
    }

    @Override
    protected void onEnter(final L2Character character) {

        if (character instanceof L2PcInstance) {

            ((L2PcInstance) character).isInsideBattleRoyaleZone = true;

            if (!BattleRoyale._players.contains(character) && !((L2PcInstance) character).isGM()) {
                character.teleToLocation(82423, 149628, -3495, true);
                ((L2PcInstance) character).sendMessage("You are not supposed to be here.");
            }

            if(((L2PcInstance) character).isGM()){
                ((L2PcInstance) character).sendMessage("Welcome to BattleRoyaleZone Zone Game Master.");
            }

        }

    }

    @Override
    protected void onExit(final L2Character character) {
        if (character instanceof L2PcInstance) {

            ((L2PcInstance) character).isInsideBattleRoyaleZone = false;

            if (BattleRoyale._players.contains(character)){
                BattleRoyale.removePlayer((L2PcInstance) character);
            }

            if(((L2PcInstance) character).isGM()){
                ((L2PcInstance) character).sendMessage("You exited BattleRoyaleZone Zone Game Master.");
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


