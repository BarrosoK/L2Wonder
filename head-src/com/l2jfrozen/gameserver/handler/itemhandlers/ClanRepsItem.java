package com.l2jfrozen.gameserver.handler.itemhandlers;

/**
 * Created by Server1 on 7/7/2017.
 */

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.handler.IItemHandler;
import com.l2jfrozen.gameserver.model.actor.instance.L2ItemInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PlayableInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.entity.Announcements;
import com.l2jfrozen.gameserver.network.serverpackets.MagicSkillUser;

public class ClanRepsItem implements IItemHandler
{
    private static final int ITEM_IDS[] =
            {
                    6673
            };

    @Override
    public void useItem(L2PlayableInstance playable, L2ItemInstance item)
    {

        if (!(playable instanceof L2PcInstance))
        {
            return;
        }

        L2PcInstance activeChar = (L2PcInstance)playable;

        if (!activeChar.isClanLeader())
        {
            activeChar.sendMessage("This can be used only by Clan Leaders!");
            return;
        }

        else if (!(activeChar.getClan().getLevel() >= 1))
        {
            activeChar.sendMessage("Your Clan Level is not big enough to use this item!");
            return;
        }
        else
        {
            activeChar.getClan().setReputationScore(activeChar.getClan().getReputationScore() + 10000, true);
            activeChar.sendMessage("Your clan has earned 10000 rep points!");
            MagicSkillUser  MSU = new MagicSkillUser(activeChar, activeChar, 2024, 1, 1, 1);
            activeChar.broadcastPacket(MSU);
            playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
        }
    }

    public int[] getItemIds()
    {
        return ITEM_IDS;
    }
}