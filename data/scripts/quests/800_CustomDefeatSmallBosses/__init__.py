# Illegitimate Child Of A Goddess version 0.1
# by DrLecter
import sys
from com.l2jfrozen import Config
from com.l2jfrozen.gameserver.model.quest import State
from com.l2jfrozen.gameserver.model.quest import QuestState
from com.l2jfrozen.gameserver.model.quest.jython import QuestJython as JQuest

#Quest info
QUEST_NUMBER,QUEST_NAME,QUEST_DESCRIPTION = 800,"CustomDefeatSmallBosses","Finding revenge"
qn = "800_CustomDefeatSmallBosses"

REQUIRED=7 #how many items will be paid for a reward (affects onkill sounds too)

#Quest items
SMALLBOSSITEM = 3196

#Rewards
REWARD = 4037
REWARDQTY = 2

#Messages
default   = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"

#NPCs
OLTLIN = 1234321

#Mobs
MOBS = [ 29001,29006,29014,13052,13050,13051,29022 ]

class Quest (JQuest) :

    def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

    def onEvent (self,event,st) :
        htmltext = event
        if event == "1234321-5.htm" :
            st.setState(STARTED)
            st.set("cond","1")
            st.playSound("ItemSound.quest_accept")
        elif event == "1234321-7.htm" :
            if st.getQuestItemsCount(SMALLBOSSITEM) >= REQUIRED :
                st.takeItems(SMALLBOSSITEM,REQUIRED)
                item=REWARD
                st.giveItems(item ,REWARDQTY)
                st.exitQuest(1)
                st.playSound("ItemSound.quest_finish")
            else :
                htmltext = "1234321-4.htm"
        return htmltext

    def onTalk (self,npc,player):
        htmltext = default
        st = player.getQuestState(qn)
        if not st : return htmltext

        npcId = npc.getNpcId()
        id = st.getState()
        if id == CREATED :
            st.set("cond","0")
            htmltext = "1234321-1.htm"
        elif id == STARTED :
            if st.getQuestItemsCount(SMALLBOSSITEM) >= REQUIRED :
                htmltext = "1234321-3.htm"
            else :
                htmltext = "1234321-4.htm"
        return htmltext

    def onKill(self,npc,player,isPet):
        st = player.getQuestState(qn)
        if not st : return
        if st.getState() != STARTED : return

        count = st.getQuestItemsCount(SMALLBOSSITEM)
        numItems = REQUIRED - count
        if count + 1 >= REQUIRED :
            if numItems != 0 :
                st.playSound("ItemSound.quest_middle")
                st.set("cond","2")
        else :
            st.playSound("ItemSound.quest_itemget")
        st.giveItems(SMALLBOSSITEM,1)
        return

# Quest class and state definition
QUEST       = Quest(QUEST_NUMBER, str(QUEST_NUMBER)+"_"+QUEST_NAME, QUEST_DESCRIPTION)

CREATED     = State('Start',     QUEST)
STARTED     = State('Started',   QUEST)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)

# Quest NPC starter initialization
QUEST.addStartNpc(OLTLIN)
# Quest initialization
QUEST.addTalkId(OLTLIN)

for i in MOBS :
    QUEST.addKillId(i)
    STARTED.addQuestDrop(i,SMALLBOSSITEM,1)
