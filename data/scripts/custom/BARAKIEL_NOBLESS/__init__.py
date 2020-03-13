import sys

from com.l2jfrozen.gameserver.model.quest import State
from com.l2jfrozen.gameserver.model.quest import QuestState
from com.l2jfrozen.gameserver.model.quest.jython import QuestJython as JQuest

BARAKIEL = 25325

class nubless(JQuest):
 def __init__(self,id,name,descr):
     JQuest.__init__(self,id,name,descr)

 def onKill (self,npc,player,isPet):
     npcId = npc.getNpcId()
     if npcId == BARAKIEL :
      party = player.getParty()
      if party :
        for partyMember in party.getPartyMembers().toArray() :
            pst = partyMember.getQuestState(qn)
            if pst :
                if pst.isNoble() == 0 :
                  pst.getPlayer().setNoble(True)
                  pst.giveItems(NOBLESS_TIARA,1)
      else :
        pst = player.getQuestState(qn)
        if pst :
            if pst.isNoble() == 0 :
                pst.getPlayer().setNoble(True)
                pst.giveItems(NOBLESS_TIARA,1)
     return

QUEST = nubless(-1, "nubless", "ai")
CREATED     = State('Start',QUEST)

QUEST.setInitialState(CREATED)

QUEST.addKillId(BARAKIEL)