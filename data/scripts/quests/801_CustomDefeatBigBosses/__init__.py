# Illegitimate Child Of A Goddess version 0.1
# by DrLecter
import sys
from com.l2jfrozen import Config
from com.l2jfrozen.gameserver.model.quest import State
from com.l2jfrozen.gameserver.model.quest import QuestState
from com.l2jfrozen.gameserver.model.quest.jython import QuestJython as JQuest

#Quest info
QUEST_NUMBER,QUEST_NAME,QUEST_DESCRIPTION = 801,"CustomDefeatBigBosses","In search of power"
qn = "801_CustomDefeatBigBosses"

REQUIRED=5 #how many items will be paid for a reward (affects onkill sounds too)
REQUIRED2=1

#Quest items
BIGBOSSITEM = 1044
OENOMAUSITEM = 944

#Rewards
REWARD = 4037
REWARDQTY = 4

#Messages
default   = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"

#NPCs
OLTLIN = 1234321

#Mobs
MOBS = [ 29028,29020,29067,13048,13047 ]
OENOMAUS = 660000

class Quest (JQuest) :

    def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

    def onEvent (self,event,st) :
        htmltext = event
        if event == "1234321-5.htm" :
            st.setState(STARTED)
            st.set("cond","1")
            st.playSound("ItemSound.quest_accept")
        elif event == "1234321-7.htm" :
            if st.getQuestItemsCount(BIGBOSSITEM) >= REQUIRED and st.getQuestItemsCount(OENOMAUSITEM) >= REQUIRED2 :
                st.takeItems(BIGBOSSITEM,REQUIRED)
                st.takeItems(OENOMAUSITEM,REQUIRED2)
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

        id = st.getState()
        if id == CREATED :
            st.set("cond","0")
            htmltext = "1234321-1.htm"
        elif id == STARTED :
            if st.getQuestItemsCount(BIGBOSSITEM) >= REQUIRED and st.getQuestItemsCount(OENOMAUSITEM) >= REQUIRED2:
                htmltext = "1234321-3.htm"
            else :
                htmltext = "1234321-4.htm"
        return htmltext

    def onKill(self,npc,player,isPet):
        st = player.getQuestState(qn)
        if not st : return
        if st.getState() != STARTED : return
        count = st.getQuestItemsCount(BIGBOSSITEM)
        count2 = st.getQuestItemsCount(OENOMAUSITEM)
        if npc.getNpcId()==OENOMAUS :
            numItems = REQUIRED2 - count2
            if count2 + 1 >= REQUIRED2 and count >= REQUIRED :
                if numItems != 0 :
                    st.playSound("ItemSound.quest_middle")
                    st.set("cond","2")
                if count2 + 1 == REQUIRED2:
                    st.giveItems(OENOMAUSITEM,1)
            elif count2 + 1 <= REQUIRED2 :
                st.playSound("ItemSound.quest_itemget")
                st.giveItems(OENOMAUSITEM,1)
            return
        else :
            numItems = REQUIRED - count
            if count + 1 >= REQUIRED and count2 >=REQUIRED2:
                if numItems != 0 :
                    st.playSound("ItemSound.quest_middle")
                    st.set("cond","2")
                if count + 1 == REQUIRED:
                    st.giveItems(BIGBOSSITEM,1)
            elif count + 1 <= REQUIRED :
                st.playSound("ItemSound.quest_itemget")
                st.giveItems(BIGBOSSITEM,1)
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
    STARTED.addQuestDrop(i,BIGBOSSITEM,1)

QUEST.addKillId(OENOMAUS)
STARTED.addQuestDrop(OENOMAUS,OENOMAUSITEM,1)