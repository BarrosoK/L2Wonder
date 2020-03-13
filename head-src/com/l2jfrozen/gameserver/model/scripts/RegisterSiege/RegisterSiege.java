package com.l2jfrozen.gameserver.model.scripts.RegisterSiege;

import com.l2jfrozen.gameserver.managers.CastleManager;
import com.l2jfrozen.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.entity.siege.Castle;
import com.l2jfrozen.gameserver.model.quest.Quest;
import com.l2jfrozen.gameserver.model.quest.QuestState;
import com.l2jfrozen.gameserver.network.serverpackets.SiegeInfo;

/**
 * Created by Server1 on 7/1/2017.
 */

public class RegisterSiege extends Quest
{
    private static final int[] NPCS = {50050, 13031};

    public RegisterSiege(int questid, String name, String descr)
    {
        super(questid, name, descr);

        for(int NPC_ID : NPCS)
        {
            addStartNpc(NPC_ID);
            addFirstTalkId(NPC_ID);
            addTalkId(NPC_ID);
        }
    }

    public String onAdvEvent (String event, L2NpcInstance npc, L2PcInstance player)
    {
        QuestState st = player.getQuestState(getName());
        String htmltext = "";
        int npcId = npc.getNpcId();

        if(event.equalsIgnoreCase("gludio_castle"))
        {
            if(npcId == NPCS[0])


                    showSiegeInfoWindow(player, 1);
                    return "1.htm";

        }
        else if(event.equalsIgnoreCase("dion_castle"))
        {
            if(npcId == NPCS[0])


                    showSiegeInfoWindow(player, 2);
                    return "1.htm";


        }
        else if(event.equalsIgnoreCase("giran_castle"))
        {
            if(npcId == NPCS[0])

                    showSiegeInfoWindow(player, 3);
                    return "1.htm";

        }
        else if(event.equalsIgnoreCase("oren_castle"))
        {
            if(npcId == NPCS[0])

                    showSiegeInfoWindow(player, 4);
                    return "1.htm";

        }
        else if(event.equalsIgnoreCase("aden_castle"))
        {
            if(npcId == NPCS[0])

                    showSiegeInfoWindow(player, 5);
                    return "1.htm";

        }
        else if(event.equalsIgnoreCase("innadril_castle"))
        {
            if(npcId == NPCS[0])

                    showSiegeInfoWindow(player, 6);
                    return "1.htm";

        }
        else if(event.equalsIgnoreCase("goddard_castle"))
        {
            if(npcId == NPCS[0])

                    showSiegeInfoWindow(player, 7);
                    return "1.htm";

        }
        else if(event.equalsIgnoreCase("rune_castle"))
        {
            if(npcId == NPCS[0])
                if(st.getQuestItemsCount(57)>= 1000)

                    showSiegeInfoWindow(player, 8);
                    return "1.htm";

        }

        else if(event.equalsIgnoreCase("schuttgart_castle"))
        {
            if(npcId == NPCS[0])

                    showSiegeInfoWindow(player, 9);
                    return "1.htm";

        }

        st.exitQuest(true);
        return htmltext;
    }



    public void showSiegeInfoWindow(L2PcInstance player, int castleId)
    {
        Castle c = CastleManager.getInstance().getCastleById(castleId);
        if(c != null)
            player.sendPacket(new SiegeInfo(c));
    }

    public String onFirstTalk(L2NpcInstance npc, L2PcInstance player)
    {
        QuestState st = player.getQuestState(getName());
        if(st == null)
            st = newQuestState(player);
        String htmltext = "";
        int npcId = npc.getNpcId();
        if(npcId == NPCS[0])
            htmltext = "1.htm";
        return htmltext;
    }

    public static void main(String[] args)
    {
        new RegisterSiege(-1, "RegisterSiege", "custom");
    }
}