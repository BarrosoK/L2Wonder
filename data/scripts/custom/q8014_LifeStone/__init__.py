import sys
from java.util import Iterator
from com.l2jfrozen.util.random import Rnd
from com.l2jfrozen.gameserver.network.serverpackets import SystemMessage
from com.l2jfrozen.gameserver.model.quest import State
from com.l2jfrozen.gameserver.model.quest import QuestState
from com.l2jfrozen.gameserver.model.quest.jython import QuestJython as JQuest
from com.l2jfrozen.util.database import L2DatabaseFactory
from com.l2jfrozen.gameserver.templates import L2EtcItemType
from com.l2jfrozen.gameserver.templates import L2Item
from java.lang import System
from com.l2jfrozen.gameserver.idfactory import IdFactory
from com.l2jfrozen.gameserver.model.actor.instance import L2ItemInstance
from com.l2jfrozen.gameserver.datatables.xml import AugmentationData
from com.l2jfrozen.gameserver.model import L2Augmentation
from com.l2jfrozen.gameserver.network.serverpackets import ItemList
from com.l2jfrozen.gameserver.model import L2Skill
from com.l2jfrozen.gameserver.datatables import SkillTable

qn = "q8014_LifeStone"

#id нпц
NPC = 101
#id итема для переноса
ITEM = 4037
#стоимость переноса
COST = 30
#список запрещенных итемов
FORBIDDEN = [6611,6612,6613,6614,6615,6616,6617,6618,6619,6620,6621]

print "Donate Manager: Loaded 80%"

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onAdvEvent (self,event,npc,player):
   st = player.getQuestState(qn)
   if event == "spisok":
     if st.getQuestItemsCount(ITEM) < COST:
       htmltext = u"<html><body><center><img src=\"L2UI_CH3.onscrmsg_pattern01_1\" width=300 height=32><br><br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br><img src=\"L2UI.SquareBlank\" width=260 height=2>You need <font color=74bff5>"+str(COST)+" Donate Coins </font>to use the transfer!<br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br><img src=\"L2UI_CH3.onscrmsg_pattern01_2\" width=300 height=32></center></body></html>"
       return htmltext
     htmltext = "<html><body><center><img src=\"L2UI_CH3.onscrmsg_pattern01_1\" width=300 height=32><br><br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br><img src=\"L2UI.SquareBlank\" width=260 height=2><br1>"       
     htmltext += u"<table width=260><tr><td align=center><font color=bef574>Remember to unequip the augment item!</font></font></td></tr></table>"
     htmltext += u"<br><button value=\"Show me the list!\" action=\"bypass -h Quest q8014_LifeStone step1\" width=100 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"><br1>"
     htmltext += "<center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br><img src=\"L2UI_CH3.onscrmsg_pattern01_2\" width=300 height=32></center></body></html>"
   elif event == "step1":
     htmltext = u"<html><body><center><img src=\"L2UI_CH3.onscrmsg_pattern01_1\" width=300 height=32><br><br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br><img src=\"L2UI.SquareBlank\" width=260 height=2><br>Select the item you wish to transfer the augmentation!<br><br><table width=300>"
     SPWEAPONS = ["Sword","Blunt","Dagger","Bow","Etc","Pole","Fist","Dual Sword","Dual Fist","Big Sword","Big Blunt"]
     for Item in st.getPlayer().getInventory().getItems():
       itemTemplate = Item.getItem()
       idtest = Item.getItemId()
       itype = str(Item.getItemType())
       if idtest not in FORBIDDEN and Item.isAugmented() and not Item.isEquipped() and itype in SPWEAPONS:
         cnt = Item.getCount()
         count = str(cnt)
         grade = itemTemplate.getCrystalType()   
         con=L2DatabaseFactory.getInstance().getConnection()
         listitems=con.prepareStatement("SELECT itemIcon FROM z_market_icons WHERE itemId=?")
         listitems.setInt(1, idtest)
         rs=listitems.executeQuery()
         while (rs.next()) :
           icon=rs.getString("itemIcon")
           try :
             if grade == 1:
               pgrade = str("[D]")
             elif grade == 2:
               pgrade = str("[C]")
             elif grade == 3:
               pgrade = str("[B]")
             elif grade == 4:
               pgrade = str("[A]")
             elif grade == 5:
               pgrade = str("[S]")
             else:
               pgrade = str("")
             if Item.getEnchantLevel() == 0:
               enchant = str("")
             else:
               enchant = " +"+str(Item.getEnchantLevel())+""
             htmltext += "<tr><td><img src=\"Icon."+str(icon)+"\" width=32 height=32></td><td><a action=\"bypass -h Quest q8014_LifeStone step1next_" + str(Item.getObjectId()) +"\">" + itemTemplate.getName() + " "+str(pgrade)+" " + enchant + "</a></td></tr>"
           except :
             try : insertion.close()
             except : pass
         try :
           con.close()
         except :
           pass
     htmltext += u"</table><br><a action=\"bypass -h Quest q8014_LifeStone spisok\">Back</a><br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br><br><img src=\"L2UI_CH3.onscrmsg_pattern01_2\" width=300 height=32><br><img src=\"L2UI.SquareBlank\" width=260 height=2></body></html>"
   elif event.startswith("step1next_"):
     itemObjId = int(event.replace("step1next_", ""))
     obj = str(itemObjId)
     Item = st.getPlayer().getInventory().getItemByObjectId(itemObjId)
     itemTemplate = Item.getItem()
     if Item and not Item.isEquipped():
       cnt = Item.getCount()
       count = str(cnt)
       grade = itemTemplate.getCrystalType()
       igrade = str(itemTemplate.getCrystalType())
       itype = str(Item.getItemType())
       idtest = Item.getItemId()
       con=L2DatabaseFactory.getInstance().getConnection()
       listitems=con.prepareStatement("SELECT itemIcon, skill, attributes FROM `z_market_icons` icon, `augmentations` aug WHERE icon.itemId=? AND aug.item_id=?")
       listitems.setInt(1, idtest)
       listitems.setInt(2, itemObjId)
       rs=listitems.executeQuery()
       while (rs.next()) :
         icon=rs.getString("itemIcon")
         skill=rs.getInt("skill")
         attributes=rs.getInt("attributes")
         try :
           st.set("oneitem",obj)
           st.set("skill",str(skill))
           grades = {1: "d", 2: "c", 3: "b", 4: "a", 5: "s"}
           pgrade = grades.get(grade, str(""))
           enchant = (Item.getEnchantLevel() > 0 and " +"+str(Item.getEnchantLevel())+"") or str("")
           htmltext = u"<html><body><center><img src=\"L2UI_CH3.onscrmsg_pattern01_1\" width=300 height=32><br><br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br><img src=\"L2UI.SquareBlank\" width=260 height=2><br>"
           htmltext += "<table width=300><tr><td><img src=\"Icon."+str(icon)+"\" width=32 height=32></td><td><font color=LEVEL>" + itemTemplate.getName() + " " + enchant + "</font><img src=\"symbol.grade_"+str(pgrade)+"\" width=16 height=16><br></td></tr></table><br><br>"
           if skill != 0:
             skill = SkillTable.getInstance().getInfo(skill, 1)
             name = skill.getName()
             htmltext += "<br><font color=bef574>Skill: [" + str(name) + "]</font><br>"
             htmltext += u"<button value=\"I agree\" action=\"bypass -h Quest q8014_LifeStone step2\" width=60 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"><br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br><br><img src=\"L2UI_CH3.onscrmsg_pattern01_2\" width=300 height=32><br><img src=\"L2UI.SquareBlank\" width=260 height=2>"
           else:
             htmltext += u"<font color=LEVEL>Only augmentations with skill can use the transfer!</font>"
             htmltext += u"<button value=\"Back\" action=\"bypass -h Quest q8014_LifeStone spisok\" width=60 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"><br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br><br><img src=\"L2UI_CH3.onscrmsg_pattern01_2\" width=300 height=32><br><img src=\"L2UI.SquareBlank\" width=260 height=2>"
         except :
           try : listitems.close()
           except : pass
       try :
         con.close()
       except :
         pass
     else :
       htmltext = u"<html><body><center><img src=\"L2UI_CH3.onscrmsg_pattern01_1\" width=300 height=32><br><br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br><img src=\"L2UI.SquareBlank\" width=260 height=2><br>Dont be fast! Unequip the weapon and try again!<br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br><br><img src=\"L2UI_CH3.onscrmsg_pattern01_1\" width=256 height=32><br><img src=\"L2UI.SquareBlank\" width=260 height=2></body></html>"
   elif event == "step2":
     htmltext = u"<html><body><center><img src=\"L2UI_CH3.onscrmsg_pattern01_1\" width=300 height=32><br><br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br><img src=\"L2UI.SquareBlank\" width=260 height=2><br1>Select the item for the transfer!<br1><table width=300>"
     SPWEAPONS = ["Sword","Blunt","Dagger","Bow","Etc","Pole","Fist","Dual Sword","Dual Fist","Big Sword","Big Blunt"]
     weapon1 = st.getInt("oneitem")
     for Item in st.getPlayer().getInventory().getItems():
       itemTemplate = Item.getItem()
       idtest = Item.getItemId()
       itype = str(Item.getItemType())
       if idtest not in FORBIDDEN and not Item.isEquipped() and itype in SPWEAPONS and Item.getObjectId() != weapon1 and not Item.isAugmented():
         grade = itemTemplate.getCrystalType()   
         con=L2DatabaseFactory.getInstance().getConnection()
         listitems=con.prepareStatement("SELECT itemIcon FROM z_market_icons WHERE itemId=?")
         listitems.setInt(1, idtest)
         rs=listitems.executeQuery()
         while (rs.next()) :
           icon=rs.getString("itemIcon")
           try :
             if grade == 1:
               pgrade = str("[D]")
             elif grade == 2:
               pgrade = str("[C]")
             elif grade == 3:
               pgrade = str("[B]")
             elif grade == 4:
               pgrade = str("[A]")
             elif grade == 5:
               pgrade = str("[S]")
             else:
               pgrade = str("")
             if Item.getEnchantLevel() == 0:
               enchant = str("")
             else:
               enchant = " +"+str(Item.getEnchantLevel())+""
             htmltext += "<tr><td><img src=\"Icon."+str(icon)+"\" width=32 height=32></td><td><a action=\"bypass -h Quest q8014_LifeStone step2next_" + str(Item.getObjectId()) +"\">" + itemTemplate.getName() + ""+str(pgrade)+" " + enchant + "</a></td></tr>"
           except :
             try : insertion.close()
             except : pass
         try :
           con.close()
         except :
           pass
     htmltext += u"</table><br><a action=\"bypass -h Quest q8014_LifeStone spisok\">Back</a><br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br><br><img src=\"L2UI_CH3.onscrmsg_pattern01_2\" width=300 height=32><br><img src=\"L2UI.SquareBlank\" width=260 height=2></body></html>"
   elif event.startswith("step2next_"):
     itemObjId = int(event.replace("step2next_", ""))
     obj = str(itemObjId)
     Item = st.getPlayer().getInventory().getItemByObjectId(itemObjId)
     itemTemplate = Item.getItem()
     if Item and not Item.isAugmented() and not Item.isEquipped():
       cnt = Item.getCount()
       count = str(cnt)
       grade = itemTemplate.getCrystalType()
       igrade = str(itemTemplate.getCrystalType())
       itype = str(Item.getItemType())
       idtest = Item.getItemId()
       con=L2DatabaseFactory.getInstance().getConnection()
       listitems=con.prepareStatement("SELECT itemIcon FROM z_market_icons WHERE itemId=?")
       listitems.setInt(1, idtest)
       rs=listitems.executeQuery()
       while (rs.next()) :
         icon=rs.getString("itemIcon")
         try :
           st.set("twoitem",obj)
           st.set("lcount",count)
           st.set("grade",igrade)
           st.set("type",itype)
           grades = {1: "d", 2: "c", 3: "b", 4: "a", 5: "s"}
           pgrade = grades.get(grade, str(""))
           enchant = (Item.getEnchantLevel() > 0 and " +"+str(Item.getEnchantLevel())+"") or str("")
           htmltext = u"<html><body><center><img src=\"L2UI_CH3.onscrmsg_pattern01_1\" width=300 height=32><br><br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br><img src=\"L2UI.SquareBlank\" width=260 height=2><br>"
           htmltext += "<table width=300><tr><td><img src=\"Icon."+str(icon)+"\" width=32 height=32></td><td><font color=LEVEL>" + itemTemplate.getName() + " " + enchant + "</font><img src=\"symbol.grade_"+str(pgrade)+"\" width=16 height=16><br></td></tr></table><br><br>"
           htmltext += u"<button value=\"I agree\" action=\"bypass -h Quest q8014_LifeStone step3\" width=60 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"><br>"
           htmltext += u"<br><a action=\"bypass -h Quest q8014_LifeStone spisok\">Cancel</a><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br><br><img src=\"L2UI_CH3.onscrmsg_pattern01_2\" width=300 height=32><br><img src=\"L2UI.SquareBlank\" width=260 height=2><br></body></html>"
         except :
           try : listitems.close()
           except : pass
       try :
         con.close()
       except :
         pass
     else :
       htmltext = u"<html><body>Something went wrong! Make sure both of the weapons are in your inventory and unequipped.<br></body></html>"
   elif event.startswith("step3"):
     weapon1 = st.getInt("oneitem")
     weapon2 = st.getInt("twoitem")
     skillp = st.getInt("skill")
     htmltext =  u"<html><body><center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br><img src=\"L2UI.SquareBlank\" width=260 height=2><br><font color=LEVEL>Augmentation Transfer:</font><br>"
     skill = SkillTable.getInstance().getInfo(skillp, 1)
     name = skill.getName()
     htmltext += "<font color=bef574>["+str(name)+"]</font><br>"
     htmltext += u"The augmentation of the next weapon will dissapear!"
     Item = st.getPlayer().getInventory().getItemByObjectId(weapon1)
     itemTemplate = Item.getItem()
     if Item and Item.isAugmented() and not Item.isEquipped():
       cnt = Item.getCount()
       count = str(cnt)
       grade = itemTemplate.getCrystalType()
       igrade = str(itemTemplate.getCrystalType())
       itype = str(Item.getItemType())
       idtest = Item.getItemId()
       con=L2DatabaseFactory.getInstance().getConnection()
       listitems=con.prepareStatement("SELECT itemIcon FROM z_market_icons WHERE itemId=?")
       listitems.setInt(1, idtest)
       rs=listitems.executeQuery()
       while (rs.next()) :
         icon=rs.getString("itemIcon")
         try :
           grades = {1: "d", 2: "c", 3: "b", 4: "a", 5: "s"}
           pgrade = grades.get(grade, str(""))
           enchant = (Item.getEnchantLevel() > 0 and " +"+str(Item.getEnchantLevel())+"") or str("")
           htmltext += "<table width=300><tr><td><img src=\"Icon."+str(icon)+"\" width=32 height=32></td><td><font color=LEVEL>" + itemTemplate.getName() + " " + enchant + "</font><img src=\"symbol.grade_"+str(pgrade)+"\" width=16 height=16><br></td></tr></table><br><br>"
         except :
           try : listitems.close()
           except : pass
       try :
         con.close()
       except :
         pass
     htmltext += "The next weapon will gain the augmentation attributes!<br>"
     Item = st.getPlayer().getInventory().getItemByObjectId(weapon2)
     itemTemplate = Item.getItem()
     if Item and not Item.isAugmented() and not Item.isEquipped():
       cnt = Item.getCount()
       count = str(cnt)
       grade = itemTemplate.getCrystalType()
       igrade = str(itemTemplate.getCrystalType())
       itype = str(Item.getItemType())
       idtest = Item.getItemId()
       cons=L2DatabaseFactory.getInstance().getConnection()
       listitemss=cons.prepareStatement("SELECT itemIcon FROM z_market_icons WHERE itemId=?")
       listitemss.setInt(1, idtest)
       rs=listitemss.executeQuery()
       while (rs.next()) :
         icon=rs.getString("itemIcon")
         try :
           grades = {1: "d", 2: "c", 3: "b", 4: "a", 5: "s"}
           pgrade = grades.get(grade, str(""))
           enchant = (Item.getEnchantLevel() > 0 and " +"+str(Item.getEnchantLevel())+"") or str("")
           htmltext += "<table width=300><tr><td><img src=\"Icon."+str(icon)+"\" width=32 height=32></td><td><font color=LEVEL>" + itemTemplate.getName() + " " + enchant + "</font><img src=\"symbol.grade_"+str(pgrade)+"\" width=16 height=16><br></td></tr></table><br><br>"
         except :
           try : listitemss.close()
           except : pass
       try :
         cons.close()
       except :
         pass
       htmltext += u"<font color=LEVEL>Price : 30 Donate Coins</font><br><button value=\"Complete\" action=\"bypass -h Quest q8014_LifeStone step4\" width=60 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"><br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32>"
     else :
       htmltext = u"<html><body>Something went wrong! Make sure both of the weapons are in your inventory and unequipped.</body></html>"
   elif event.startswith("step4"):
     weapon1 = st.getInt("oneitem")
     weapon2 = st.getInt("twoitem")
     htmltext = u"<html><body><center><img src=\"L2UI_CH3.onscrmsg_pattern01_1\" width=300 height=32><br><br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br><img src=\"L2UI.SquareBlank\" width=260 height=2>Augmentation transfer:<br1>"
     if st.getQuestItemsCount(ITEM) < COST:
       htmltext += u"<font color=FF0000>Canceled!</font><br> You need <font color=74bff5>"+str(COST)+" Donate Coins</font><br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br><br><img src=\"L2UI_CH3.onscrmsg_pattern01_1\" width=300 height=32><br><img src=\"L2UI.SquareBlank\" width=260 height=2><br></body></html>"
       return htmltext
     count = 0
     for Item in st.getPlayer().getInventory().getItems():
	   if Item.getObjectId() == weapon1:
	     count = count + 1
	   elif Item.getObjectId() == weapon2:
	     count = count + 1
     if count == 2:
       itemTest = st.getPlayer().getInventory().getItemByObjectId(weapon2)
       wp=itemTest.getItem()
       wpname=wp.getName()
       item2 = st.getPlayer().getInventory().getItemByObjectId(weapon2)
       item1 = st.getPlayer().getInventory().getItemByObjectId(weapon1)
       level = item1.getAugmentation().getSkill().getLevel()
       con=L2DatabaseFactory.getInstance().getConnection()
       listitems=con.prepareStatement("SELECT * FROM `augmentations` WHERE item_id=?")
       listitems.setInt(1, weapon1)
       rs=listitems.executeQuery()
       while (rs.next()) :
         attributes=rs.getInt("attributes")
         skill=rs.getInt("skill")
         try :
           st.takeItems(ITEM,COST)
           item2.setAugmentation(L2Augmentation(item2, attributes, skill, level, True))
           player.sendPacket(ItemList(player, False))
           item1.removeAugmentation()
           skilla = SkillTable.getInstance().getInfo(skill, 1)
           name = skilla.getName()
           htmltext += u"<font color=FF0000>Completed!</font><br1>Your "+str(wpname)+" <br1>obtained <font color=bef574>["+str(name)+"]</font> <br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br><img src=\"L2UI_CH3.onscrmsg_pattern01_2\" width=300 height=32></center><br>"
         except :
           try : listitems.close()
           except : pass
       try :
         con.close()
       except :
         pass
     else:
	   htmltext = u"<html><body>Something went wrong! Make sure both of the weapons are in your inventory and unequipped.</body></html>"
	   return htmltext
   else:
     htmltext = u"<html><body>Something went wrong! Make sure both of the weapons are in your inventory and unequipped.</body></html>"
   return htmltext

 def onTalk (self,npc,player):
   st = player.getQuestState(qn)
   npcId = npc.getNpcId()
   if npcId == NPC:
     htmltext = "privetstvie.htm"
   return htmltext

QUEST       = Quest(8014,qn,"custom")
CREATED     = State('Start', QUEST)

QUEST.setInitialState(CREATED)

QUEST.addStartNpc(NPC)
QUEST.addTalkId(NPC)
