package com.l2jfrozen.gameserver.handler.itemhandlers;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.datatables.sql.NpcTable;
import com.l2jfrozen.gameserver.handler.IItemHandler;
import com.l2jfrozen.gameserver.idfactory.IdFactory;
import com.l2jfrozen.gameserver.model.L2Object;
import com.l2jfrozen.gameserver.model.actor.instance.L2ItemInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PlayableInstance;
import com.l2jfrozen.gameserver.model.spawn.L2Spawn;
import com.l2jfrozen.gameserver.network.SystemMessageId;
import com.l2jfrozen.gameserver.network.serverpackets.SystemMessage;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;
import com.l2jfrozen.gameserver.thread.ThreadPoolManager;

/**
 * Created by Server1 on 6/27/2017.
 */
public class ClanReputation implements IItemHandler
{
    // --------------------------------------------------
    // Interval configs.
    // --------------------------------------------------
    private static final int ITEM_IDS[] = {1515}; // item id here.
    private static final int Rep_Score = 10000; // reputation points here.

    @Override
    public void useItem(final L2PlayableInstance playable, final L2ItemInstance item)
    {
        if (!playable.isPlayer())
        {
            return;
        }

        L2PcInstance activeChar = playable.getActingPlayer();

        if ((activeChar.getClan() == null) || !activeChar.isClanLeader()){
            activeChar.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }
        activeChar.getClan().setReputationScore(activeChar.getClan().getReputationScore() + Rep_Score, true);
        activeChar.destroyItemByItemId("clan", ITEM_IDS[0], 1, activeChar, true);
        activeChar.sendMessage("You have successfully add "+Rep_Score+" reputation point(s) to your clan.");
        return;
    }

    @Override
    public int[] getItemIds()
    {
        return ITEM_IDS;
    }
}