# ####################
#  www.l2jfrozen.com #
# ####################

import sys
from com.l2jfrozen.gameserver.model.actor.instance import L2PcInstance
from java.util import Iterator
from com.l2jfrozen.gameserver.datatables import SkillTable
from com.l2jfrozen.util.database import L2DatabaseFactory
from com.l2jfrozen.gameserver.model.quest import State
from com.l2jfrozen.gameserver.model.quest import QuestState
from com.l2jfrozen.gameserver.model.quest.jython import QuestJython as JQuest

print "importing custom: TOP PVP/PK LIST"

# ***************************************
# Quest Initialization Variables vol.2  *
# ***************************************

NPC=[440457]

QuestId     = 80300
QuestName   = "rank"
QuestDesc   = "custom"
InitialHtml = "1.htm"

# *************************
# Creating the Class Quest*
# *************************

class Quest (JQuest) :

	def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

	# ************************
	# If you talk to the NPC:*
	# ************************
	def onTalk (self,npc,player):
		return InitialHtml
	# ***********************

	# *****************************************************
	# Now govern the events coming from the HTML vol.2    *
	# *****************************************************
	def onEvent(self,event,st):
		htmltext = event
		
		# *********
		# PK info *
		# *********
		
		if event == "1" :
			total_asesinados = 0
			htmltext_ini = "<html><head><title>Pk Info</title></head><body><table width=300><tr><td><font color =\"FF00FF\">Pos.</td><td><center><font color =\"FFFF00\">*** Player ***</color></center></td><td><center>*** Kills ***</center></td></tr>"
			htmltext_info =""			
			color = 1
			pos = 0
			con = L2DatabaseFactory.getInstance().getConnection()
			pks = con.prepareStatement("SELECT char_name,pkkills FROM characters WHERE pkkills>0 and accesslevel=0 order by pkkills desc limit 30")
			rs = pks.executeQuery()
			while (rs.next()) :
				char_name = rs.getString("char_name")
				char_pkkills = rs.getString("pkkills")
				total_asesinados = total_asesinados + int(char_pkkills)
				pos = pos + 1
				posstr = str(pos)
				if color == 1:
					color_text = "<font color =\"00FFFF\">"
					color = 2
					htmltext_info = htmltext_info + "<tr><td><center><font color =\"FF00FF\">" + posstr + "</td><td><center>" + color_text + char_name +"</center></td><td><center>" + char_pkkills + "</center></td></tr>"
				elif color == 2:
					color_text = "<font color =\"FF0000\">"
					color = 1
					htmltext_info = htmltext_info + "<tr><td><center><font color =\"FF00FF\">" + posstr + "</td><td><center>" + color_text + char_name +"</center></td><td><center>" + char_pkkills + "</center></td></tr>"
			htmltext_end = "</table><center><font color=\"FFFFFF\">" + "A Total of " + str(total_asesinados) + " Pk's.</center></body></html>"
			htmltext_pklist = htmltext_ini + htmltext_info + htmltext_end
			con.close()
			return htmltext_pklist
			
		# **********
		# PvP info *
		# **********
			
		if event == "2" :
			total_asesinados = 0
			htmltext_ini = "<html><head><title>PvP info</title></head><body><table width=300><tr><td><font color =\"FF00FF\">Pos.</td><td><center><font color =\"FFFF00\">*** Player ***</color></center></td><td><center>*** Kills ***</center></td></tr>"
			htmltext_info =""			
			color = 1
			pos = 0
			con = L2DatabaseFactory.getInstance().getConnection()
			pks = con.prepareStatement("SELECT char_name,pvpkills FROM characters WHERE pvpkills>0 and accesslevel=0 order by pvpkills desc limit 30")
			rs = pks.executeQuery()
			while (rs.next()) :
				char_name = rs.getString("char_name")
				char_pkkills = rs.getString("pvpkills")
				total_asesinados = total_asesinados + int(char_pkkills)
				pos = pos + 1
				posstr = str(pos)
				if color == 1:
					color_text = "<font color =\"00FFFF\">"
					color = 2
					htmltext_info = htmltext_info + "<tr><td><center><font color =\"FF00FF\">" + posstr + "</td><td><center>" + color_text + char_name +"</center></td><td><center>" + char_pkkills + "</center></td></tr>"
				elif color == 2:
					color_text = "<font color =\"FF0000\">"
					color = 1
					htmltext_info = htmltext_info + "<tr><td><center><font color =\"FF00FF\">" + posstr + "</td><td><center>" + color_text + char_name +"</center></td><td><center>" + char_pkkills + "</center></td></tr>"
			htmltext_end = "</table><center><font color=\"FFFFFF\">" + "A Total of " + str(total_asesinados) + " Kills.</center></body></html>"
			htmltext_pklist = htmltext_ini + htmltext_info + htmltext_end
			con.close()
			return htmltext_pklist

		# *************
		# Adenas info *
		# *************
	

	
# *************************************************
# Inserting Quest Quest in the list of available  *
# *************************************************

QUEST       = Quest(QuestId,str(QuestId) + "_" + QuestName,QuestDesc)


for npcId in NPC:
	QUEST.addStartNpc(npcId)
	QUEST.addTalkId(npcId)

