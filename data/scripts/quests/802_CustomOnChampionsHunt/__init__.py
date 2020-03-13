# Illegitimate Child Of A Goddess version 0.1
# by DrLecter
import sys
from com.l2jfrozen import Config
from com.l2jfrozen.gameserver.model.quest import State
from com.l2jfrozen.gameserver.model.quest import QuestState
from com.l2jfrozen.gameserver.model.quest.jython import QuestJython as JQuest

#Quest info
QUEST_NUMBER,QUEST_NAME,QUEST_DESCRIPTION = 802,"CustomOnChampionsHunt","Cleanse the lands"
qn = "802_CustomOnChampionsHunt"

REQUIRED=100 #how many items will be paid for a reward (affects onkill sounds too)

#Quest items
CHAMPIONITEM = 4326

#Rewards
REWARD = 4037
REWARDQTY = 1

#Messages
default   = "<html><body>You are either not carrying out your quest or don't meet the criteria.</body></html>"

#NPCs
OLTLIN = 1234321

class Quest (JQuest) :

    def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

    def onEvent (self,event,st) :
        htmltext = event
        if event == "1234321-5.htm" :
            st.setState(STARTED)
            st.set("cond","1")
            st.playSound("ItemSound.quest_accept")
        elif event == "1234321-7.htm" :
            count = st.getQuestItemsCount(CHAMPIONITEM)
            if count >= REQUIRED :
                st.takeItems(CHAMPIONITEM,(count//REQUIRED)*REQUIRED)
                item=REWARD
                st.giveItems(item ,REWARDQTY*(count//REQUIRED))
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
            if st.getQuestItemsCount(CHAMPIONITEM) >= REQUIRED :
                htmltext = "1234321-3.htm"
            else :
                htmltext = "1234321-4.htm"
        return htmltext

    def onKill(self,npc,player,isPet):
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


