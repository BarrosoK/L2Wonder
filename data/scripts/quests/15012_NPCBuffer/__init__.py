import sys
from net.sf.l2j.gameserver.model.actor.instance import L2PcInstance
from java.util import Iterator
from net.sf.l2j.gameserver.datatables import SkillTable
from net.sf.l2j			       import L2DatabaseFactory
from net.sf.l2j.gameserver.model.quest import State
from net.sf.l2j.gameserver.model.quest import QuestState
from net.sf.l2j.gameserver.model.quest.jython import QuestJython as JQuest

qn = "15010_NPCBuffer"

NPC=[979,40009,30256]
ADENA_ID=57
QuestId     = 15012
QuestName   = "NPCBuffer"
QuestDesc   = "custom"
InitialHtml = "1.htm"


print "..............! - Importing Custom: 15012: NPC BUFFER.............."

class Quest (JQuest) :

	def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)


	def onEvent(self,event,st):
		htmltext = event
		count=st.getQuestItemsCount(ADENA_ID)
		if count < 1  or st.getPlayer().getLevel() < 0 :
			htmltext = "<html><head><body>Can only be used by clan donator members.</body></html>"
		else:
			st.takeItems(ADENA_ID,0)
			st.getPlayer().setTarget(st.getPlayer())
			
			if event == "2":
				st.takeItems(ADENA_ID,0)
				st.getPlayer().stopAllEffects()
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4344,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4346,4).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4349,2).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(1389,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4345,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4347,6).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4348,6).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4352,2).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4354,4).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(1087,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4360,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4358,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4357,2).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4359,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(1032,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4342,2).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(1397,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(264,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(266,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(267,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(268,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(269,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(304,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(271,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(274,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(275,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(310,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(1363,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4700,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4703,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(1323,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(State.COMPLETED)

			if event == "3": 
				st.takeItems(ADENA_ID,0)
				st.getPlayer().stopAllEffects()
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4344,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4346,4).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4349,2).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(1243,6).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(1389,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4347,6).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4348,6).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4355,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4356,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4352,2).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(1303,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(1087,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(1397,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4351,6).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(1044,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(264,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(266,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(268,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(267,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(269,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(304,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(273,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(276,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(365,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(1413,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4699,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4702,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4703,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(1323,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(State.COMPLETED)

			if event == "4":
				st.takeItems(ADENA_ID,0)
				st.getPlayer().stopAllEffects()
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4344,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4346,4).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4349,2).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(1389,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4345,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4347,6).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4348,6).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4352,2).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4354,4).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(1087,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4360,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4358,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4357,2).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4359,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(1032,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4342,2).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(1397,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(264,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(266,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(267,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(268,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(269,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(304,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(271,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(274,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(275,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(310,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(1363,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4700,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4703,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(1323,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(State.COMPLETED)
                        
			if event == "5":
				st.takeItems(ADENA_ID,0)
				st.getPlayer().stopAllEffects()
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4344,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4346,4).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4349,2).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4345,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(1388,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4347,6).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4348,6).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4352,2).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(1087,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4360,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4358,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4357,2).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4359,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(1032,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4342,2).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(1397,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(264,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(266,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(267,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(268,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(269,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(304,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(271,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(274,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(275,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(1363,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4700,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(4703,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(1323,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "1.htm"			
				st.setState(State.COMPLETED)

			if event == "6":
				st.takeItems(ADENA_ID,0)
				st.getPlayer().restoreHPMP()
				return "1.htm"		
				st.setState(State.COMPLETED)

			#Wind Walk
			if event == "7":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(4342,2).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "5.htm"		
				st.setState(State.COMPLETED)

			#Decrease Weight
			if event == "8":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(4343,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "5.htm"		
				st.setState(State.COMPLETED)

			#Shield
			if event == "9":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(4344,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "5.htm"		
				st.setState(State.COMPLETED)

			#Might
			if event == "10":
				st.takeItems(ADENA_ID,0)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(1068,3),False,False)
				st.getPlayer().restoreHPMP()
				return "5.htm"		
				st.setState(State.COMPLETED)

			#Mental Shield
			if event == "11":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(4346,4).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "5.htm"		
				st.setState(State.COMPLETED)

			#Bless the Body
			if event == "12":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(4347,6).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "5.htm"		
				st.setState(State.COMPLETED)

			#Bless the Soul
			if event == "13":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(4348,6).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "5.htm"		
				st.setState(State.COMPLETED)

			#Magic Barrier
			if event == "14":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(4349,2).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "5.htm"		
				st.setState(State.COMPLETED)

			#Resist Shock
			if event == "15":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(4350,4).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "5.htm"		
				st.setState(State.COMPLETED)

			#Concentration
			if event == "16":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(4351,6).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "5.htm"		
				st.setState(State.COMPLETED)

			#Berserker Spirit
			if event == "17":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(4352,2).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "5.htm"		
				st.setState(State.COMPLETED)

			#Bless Shield
			if event == "18":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1243,6).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "5.htm"		
				st.setState(State.COMPLETED)

			#Vampiric Rage
			if event == "19":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(4354,4).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "5.htm"		
				st.setState(State.COMPLETED)

			#Acumen
			if event == "20":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(4355,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "5.htm"		
				st.setState(State.COMPLETED)

			#Empower
			if event == "21":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(4356,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "5.htm"		
				st.setState(State.COMPLETED)

			#Haste
			if event == "22":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(4357,2).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "5.htm"		
				st.setState(State.COMPLETED)

			#Guidance
			if event == "23":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(4358,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "5.htm"		
				st.setState(State.COMPLETED)

			#Focus
			if event == "24":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(4359,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "5.htm"		
				st.setState(State.COMPLETED)

			#Death Whisper
			if event == "25":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(4360,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "5.htm"		
				st.setState(State.COMPLETED)

			if event == "26":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(271,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "2.htm"

			if event == "27":
				st.takeItems(ADENA_ID,0)	
				SkillTable.getInstance().getInfo(272,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "2.htm"

			if event == "28":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(273,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "2.htm"

			if event == "29":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(274,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "2.htm"

			if event == "30":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(275,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "2.htm"

			if event == "31":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(276,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "2.htm"

			if event == "32":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(277,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "2.htm"

			if event == "33":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(307,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "2.htm"

			if event == "34":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(309,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "2.htm"

			if event == "35":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(310,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "2.htm"

			if event == "36":
				st.takeItems(ADENA_ID,0)		
				SkillTable.getInstance().getInfo(311,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "2.htm"

			if event == "37":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(366,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "2.htm"

			if event == "38":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(365,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()			
				return "2.htm"

			if event == "39":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(264,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "3.htm"

			if event == "40":
				st.takeItems(ADENA_ID,0)	
				SkillTable.getInstance().getInfo(265,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "3.htm"

			if event == "41":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(266,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "3.htm"

			if event == "42":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(267,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "3.htm"

			if event == "43":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(268,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "3.htm"

			if event == "44":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(269,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "3.htm"

			if event == "45":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(270,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "3.htm"

			if event == "46":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(304,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "3.htm"

			if event == "47":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(305,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "3.htm"

			if event == "48":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(306,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "3.htm"	

			if event == "49":
				st.takeItems(ADENA_ID,0)	
				SkillTable.getInstance().getInfo(308,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "3.htm"

			if event == "50":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(363,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "3.htm"

			if event == "51":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(364,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "3.htm"	

			if event == "52":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(349,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "3.htm"		
				st.setState(State.COMPLETED)
				
				
			#Chant of Battle
			if event == "53":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1007,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "7.htm"
				st.setState(State.COMPLETED)
				
			#Chant of Shielding
			if event == "54":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1009,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "7.htm"
				st.setState(State.COMPLETED)
				
			#Chant of Fire
			if event == "55":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1006,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "7.htm"
				st.setState(State.COMPLETED)
				
			#Chant of Flame
			if event == "56":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1002,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "7.htm"
				st.setState(State.COMPLETED)
				
			#Chant of life
			if event == "57":
				st.takeItems(ADENA_ID,0)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(1229,18),False,False)
				st.getPlayer().restoreHPMP()
				return "7.htm"
				st.setState(State.COMPLETED)
				
			#Chant of Fury
			if event == "58":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1251,2).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "7.htm"
				st.setState(State.COMPLETED)
				
			#Chant of Evasion
			if event == "59":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1252,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "7.htm"
				st.setState(State.COMPLETED)
				
			#Chant of Rage
			if event == "60":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1253,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "7.htm"
				st.setState(State.COMPLETED)
				
			#Chant of Revenge
			if event == "61":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1284,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "7.htm"
				st.setState(State.COMPLETED)
				
			#Chant of Vampire
			if event == "62":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1310,4).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "7.htm"
				st.setState(State.COMPLETED)
				
			#Chant of Eagle
			if event == "63":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1309,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "7.htm"
				st.setState(State.COMPLETED)
				
			#Chant of Predator
			if event == "64":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1308,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "7.htm"
				st.setState(State.COMPLETED)
			
			#Greater Might
			if event == "71":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1388,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "5.htm"		
				st.setState(State.COMPLETED)
                        
                        #Noblesse Blessing
			if event == "70":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1323,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(State.COMPLETED)	
		        
                        #Invigor
			if event == "72":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1032,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "5.htm"
				st.setState(State.COMPLETED)		
                           
                        #Elemental Protection
			if event == "73":
				st.takeItems(ADENA_ID,0)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(1352,1),False,False)
				st.getPlayer().restoreHPMP()
				return "5.htm"
				st.setState(State.COMPLETED)	
                                              
                        #Divine Protection
			if event == "74":
				st.takeItems(ADENA_ID,0)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(1353,1),False,False)
				st.getPlayer().restoreHPMP()
				return "5.htm"
				st.setState(State.COMPLETED)	
                                                
                        #Arcane Protection
			if event == "75":
				st.takeItems(ADENA_ID,0)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(1354,1),False,False)
				st.getPlayer().restoreHPMP()
				return "5.htm"
				st.setState(State.COMPLETED)	
                                                
                        #Regeneration
			if event == "76":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1044,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "5.htm"
				st.setState(State.COMPLETED)	
                                                
                        #Agility
			if event == "77":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1087,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "5.htm"
				st.setState(State.COMPLETED)	
                                                
                        #Clarity
			if event == "78":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1397,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "5.htm"
				st.setState(State.COMPLETED)	
                                                
                        #Advanced Block
			if event == "79":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1304,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "5.htm"
				st.setState(State.COMPLETED)	
                                                
                        #Kiss of Eva
			if event == "80":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1073,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "5.htm"
				st.setState(State.COMPLETED)	
                                                
                        #Greater Shield
			if event == "81":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1389,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "5.htm"
				st.setState(State.COMPLETED)	
                                               
                        #Wild Magic
			if event == "82":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1303,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "5.htm"
				st.setState(State.COMPLETED)	
                                                
                        #Body of Avatar
			if event == "83":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1311,6).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "5.htm"
				st.setState(State.COMPLETED)	
                                                
                        #Resist Aqua
			if event == "84":
				st.takeItems(ADENA_ID,0)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(1182,3),False,False)
				st.getPlayer().restoreHPMP()
				return "5.htm"
				st.setState(State.COMPLETED)	
                                                
                        #Resist Fire
			if event == "85":
				st.takeItems(ADENA_ID,0)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(1191,3),False,False)
				st.getPlayer().restoreHPMP()
				return "5.htm"
				st.setState(State.COMPLETED)	
                                                
                        #Resist Wind
			if event == "86":
				st.takeItems(ADENA_ID,0)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(1189,3),False,False)
				st.getPlayer().restoreHPMP()
				return "5.htm"
				st.setState(State.COMPLETED)	
                                                
                        #Resist Poison
			if event == "87":
				st.takeItems(ADENA_ID,0)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(1033,3),False,False)
				st.getPlayer().restoreHPMP()
				return "5.htm"
				st.setState(State.COMPLETED)	
                                                
                        #Salvation
			if event == "88":
				st.takeItems(ADENA_ID,0)
				st.getPlayer().useMagic(SkillTable.getInstance().getInfo(1410,1),False,False)
				st.getPlayer().restoreHPMP()
				return "1.htm"
				st.setState(State.COMPLETED)
                                                
                        #Prophecy of Water
			if event == "89":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1355,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "8.htm"
				st.setState(State.COMPLETED)
                                                
                        #Prophecy of Fire
			if event == "90":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1356,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "8.htm"
				st.setState(State.COMPLETED)
                                                
                        #Prophecy of Wind
			if event == "91":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1357,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "8.htm"
				st.setState(State.COMPLETED)
                                                
                        #War Chant
			if event == "92":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1390,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "7.htm"
				st.setState(State.COMPLETED)
                                                
                        #Earth Chant
			if event == "93":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1391,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "7.htm"
				st.setState(State.COMPLETED)
                                                
                        #Blessing of Queen
			if event == "94":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(4699,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "9.htm"
				st.setState(State.COMPLETED)
                                                
                        #Gift of Queen
			if event == "95":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(4700,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "9.htm"
				st.setState(State.COMPLETED)
                                                
                        #Blessing of Seraphim
			if event == "96":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(4702,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "9.htm"
				st.setState(State.COMPLETED)
                                                
                        #Gift of Seraphim
			if event == "97":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(4703,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "9.htm"
				st.setState(State.COMPLETED)
                        
                        #Chant of Spirit
			if event == "65":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1362,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "7.htm"
				st.setState(State.COMPLETED)
				
			#Chant of Victory
			if event == "66":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1363,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "7.htm"
				st.setState(State.COMPLETED)
				
			#chant of magnus
			if event == "67":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1413,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "7.htm"
				st.setState(State.COMPLETED)	
			
			#Cancel
			if event == "98": 
				st.getPlayer().stopAllEffects()
				return "1.htm"
				st.setState(State.COMPLETED)

			#CPHEAL
			if event == "69":
				st.takeItems(ADENA_ID,0)
				st.getPlayer().restoreCP()
				return "1.htm"		
				st.setState(State.COMPLETED)	
			#Mage Buff
			if event == "100":
				st.takeItems(ADENA_ID,0)
				st.getPlayer().restoreCP()
				SkillTable.getInstance().getInfo(3133,10).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(1410,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(3142,10).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(5105,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(395,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "1.htm"		
				st.setState(State.COMPLETED)	

			if event == "101":
				st.takeItems(ADENA_ID,0)
				st.getPlayer().restoreCP()
				SkillTable.getInstance().getInfo(3132,10).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(1410,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(3141,10).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(5104,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				SkillTable.getInstance().getInfo(395,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "1.htm"		
				st.setState(State.COMPLETED)	

			#Argument Might
			if event == "102":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(3132,10).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "10.htm"
				st.setState(State.COMPLETED)

			#Argument Focus
			if event == "103":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(3141,10).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "10.htm"
				st.setState(State.COMPLETED)

			#Argument Empower
			if event == "104":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(3133,10).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "10.htm"
				st.setState(State.COMPLETED)

			#Argument Wild Magic
			if event == "105":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(3142,10).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "10.htm"
				st.setState(State.COMPLETED)

			#Salvation
			if event == "106":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(1410,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "10.htm"
				st.setState(State.COMPLETED)
			#Heroic Valor
			if event == "107":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(395,1).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "10.htm"
				st.setState(State.COMPLETED)

			#HBatle Force
			if event == "108":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(5104,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "10.htm"
				st.setState(State.COMPLETED)

			#Spel Force
			if event == "109":
				st.takeItems(ADENA_ID,0)
				SkillTable.getInstance().getInfo(5105,3).getEffects(st.getPlayer(),st.getPlayer())
				st.getPlayer().restoreHPMP()
				return "10.htm"
				st.setState(State.COMPLETED)
				
			if htmltext != event:
				st.setState(State.COMPLETED)
				st.exitQuest(1)
		return htmltext


	def onTalk (self,npc,player):
	   st = player.getQuestState(qn)
	   htmltext = "<html><head><body>I have nothing to say to you</body></html>"
	   st.setState(State.STARTED)
	   return InitialHtml

QUEST       = Quest(QuestId,str(QuestId) + "_" + QuestName,QuestDesc)

for npcId in NPC:
 QUEST.addStartNpc(npcId)
 QUEST.addTalkId(npcId)
