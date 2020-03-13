package com.l2jfrozen.gameserver.model.actor.instance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.StringTokenizer;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.ai.CtrlIntention;
import com.l2jfrozen.gameserver.cache.HtmCache;
import com.l2jfrozen.gameserver.custom.DonateAudit;
import com.l2jfrozen.gameserver.datatables.SkillTable;
import com.l2jfrozen.gameserver.model.Inventory;
import com.l2jfrozen.gameserver.model.L2Augmentation;
import com.l2jfrozen.gameserver.model.L2Skill;
import com.l2jfrozen.gameserver.network.SystemMessageId;
import com.l2jfrozen.gameserver.network.serverpackets.*;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;
import com.l2jfrozen.util.CloseUtil;
import com.l2jfrozen.util.database.L2DatabaseFactory;


public class L2DonateShopInstance extends L2FolkInstance {

    String INSERT_DATA = "REPLACE INTO characters_custom_data (obj_Id, char_name, hero, noble, donator, hero_end_date) VALUES (?,?,?,?,?,?)";

    public L2DonateShopInstance(int objectId, L2NpcTemplate template) {
        super(objectId, template);
    }

    // Config Donate Shop
    private static int itemid = 4037;

    @Override
    public void onBypassFeedback(L2PcInstance player, String command) {
        StringTokenizer st = new StringTokenizer(command, " ");
        String actualCommand = st.nextToken(); // Get actual command

        switch (command) {
            case "augments":
                winds(player, 1);
                break;
            case "page2":
                winds(player, 2);
                break;
            case "page3":
                winds(player, 3);
                break;
            case "page4":
                winds(player, 4);
                break;
            case "page5":
                winds(player, 5);
                break;
            case "augmentpanel":
                winds(player, 6);
                break;
            case "passive":
                winds(player, 7);
                break;
            case "passive2":
                winds(player, 8);
                break;
            case "donatewin":
                showEnchantSkillList(player, player.getClassId());
                break;
            case "clan":
                winds(player, 9);
                break;
            case "clanlvlup":
                winds(player, 10);
                break;
            case "clanskills":
                winds(player, 11);
                break;
            case "hero":
                winds(player, 12);
                break;
            case "hero1week":
                winds(player, 13);
                break;
            case "hero1month":
                winds(player, 14);
                break;
            case "heroforever":
                winds(player, 15);
                break;

        }
        if (command.startsWith("addaugment")) {
            StringTokenizer sts = new StringTokenizer(command);
            sts.nextToken();
            try {
                String type = sts.nextToken();
                switch (type) {
                    case "DuelMight":
                        augments(player, 2, 1062406807, 3134, 10);
                        winds(player, 1);
                        break;
                    case "Might":
                        augments(player, 2, 1062079106, 3132, 10);
                        winds(player, 1);
                        break;
                    case "Shield":
                        augments(player, 2, 968884225, 3135, 10);
                        winds(player, 1);
                        break;
                    case "MagicBarrier":
                        augments(player, 2, 956760065, 3136, 10);
                        winds(player, 1);
                        break;
                    case "Empower":
                        augments(player, 2, 1061423766, 3133, 10);
                        winds(player, 1);
                        break;
                    case "BattleRoar":
                        augments(player, 2, 968228865, 3125, 10);
                        winds(player, 3);
                        break;
                    case "Agility":
                        augments(player, 2, 1060444351, 3139, 10);
                        winds(player, 2);
                        break;
                    case "Heal":
                        augments(player, 2, 1061361888, 3123, 10);
                        winds(player, 3);
                        break;
                  /*  case "CelestialShield":
                        augments(player, 10, 974454785, 3158, 1);
                        break;
                        */
                    case "Guidance":
                        augments(player, 2, 1061034178, 3140, 10);
                        winds(player, 2);
                        break;
                    case "Focus":
                        augments(player, 2, 1067523168, 3141, 10);
                        winds(player, 2);
                        break;
                    case "WildMagic":
                        augments(player, 2, 1067850844, 3142, 10);
                        winds(player, 2);
                        break;
                    case "ReflectDamage":
                        augments(player, 2, 1067588698, 3204, 3);
                        winds(player, 3);
                        break;
                    case "Stone":
                        augments(player, 2, 1060640984, 3169, 10);
                        winds(player, 4);
                        break;
                    case "HealEmpower":
                        augments(player, 2, 1061230760, 3138, 10);
                        winds(player, 1);
                        break;
                    case "ShadowFlare":
                        augments(player, 2, 1063520931, 3171, 10);
                        winds(player, 4);
                        break;
                    case "AuraFlare":
                        augments(player, 2, 1063455338, 3172, 10);
                        winds(player, 5);
                        break;
                    case "Prominence":
                        augments(player, 2, 1063327898, 3165, 10);
                        winds(player, 5);
                        break;
                    case "HydroBlast":
                        augments(player, 2, 1063590051, 3167, 10);
                        winds(player, 5);
                        break;
                    case "SolarFlare":
                        augments(player, 2, 1061158912, 3177, 10);
                        winds(player, 5);
                        break;
                    case "ManaBurn":
                        augments(player, 2, 956825600, 3154, 10);
                        winds(player, 4);
                        break;
                    case "Refresh":
                        augments(player, 2, 997392384, 3202, 3);
                        winds(player, 3);
                        break;
                    case "Hurricane":
                        augments(player, 2, 1064108032, 3168, 10);
                        winds(player, 5);
                        break;
                    case "SpellRefresh":
                        augments(player, 2, 1068302336, 3200, 3);
                        winds(player, 4);
                        break;
                    case "SkillRefresh":
                        augments(player, 2, 1068040192, 3199, 3);
                        winds(player, 4);
                        break;
                    case "Stun":
                        augments(player, 2, 969867264, 3189, 10);
                        winds(player, 4);
                        break;
                    case "Prayer":
                        augments(player, 2, 991297536, 3126, 10);
                        winds(player, 3);
                        break;
                    case "Cheer":
                        augments(player, 2, 979828736, 3131, 10);
                        winds(player, 3);
                        break;
                    case "BlessedSoul":
                        augments(player, 2, 991690752, 3128, 10);
                        winds(player, 2);
                        break;
                    case "BlessedBody":
                        augments(player, 2, 991625216, 3124, 10);
                        winds(player, 2);
                        break;
                    case "Clarity":
                        augments(player, 2, 1067451241, 3164, 3);
                        winds(player, 5);
                        break;
                    case "DuelMightp":
                        augments(player, 2, 1067260101, 3243, 10);
                        winds(player, 7);
                        break;
                    case "Mightp":
                        augments(player, 2, 1067125363, 3240, 10);
                        winds(player, 7);
                        break;
                    case "Shieldp":
                        augments(player, 2, 1067194549, 3244, 10);
                        winds(player, 7);
                        break;
                    case "MagicBarrierp":
                        augments(player, 2, 962068481, 3245, 10);
                        winds(player, 7);
                        break;
                    case "Empowerp":
                        augments(player, 2, 1066994296, 3241, 10);
                        winds(player, 7);
                        break;
                    case "Agilityp":
                        augments(player, 2, 965279745, 3247, 10);
                        winds(player, 8);
                        break;
                    case "Guidancep":
                        augments(player, 2, 1070537767, 3248, 10);
                        winds(player, 8);
                        break;
                    case "Focusp":
                        augments(player, 2, 1070406728, 3249, 10);
                        winds(player, 8);
                        break;
                    case "WildMagicp":
                        augments(player, 2, 1070599653, 3250, 10);
                        winds(player, 8);
                        break;
                    case "ReflectDamagep":
                        augments(player, 2, 1070472227, 3259, 3);
                        winds(player, 8);
                        break;
                    case "HealEmpowerp":
                        augments(player, 2, 1066866909, 3246, 10);
                        winds(player, 7);
                        break;
                    case "Prayerp":
                        augments(player, 2, 1066932422, 3238, 10);
                        winds(player, 8);
                        break;
                }
            } catch (Exception e) {
                player.sendMessage("Usage : Bar>");
            }
        }
    }


    @Override
    public void onAction(L2PcInstance player) {
        player.setLastFolkNPC(this);
        if (this != player.getTarget()) {
            player.setTarget(this);

            player.sendPacket(new MyTargetSelected(getObjectId(), 0));

            player.sendPacket(new ValidateLocation(this));
        } else if (!canInteract(player)) {
            player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
        } else {
            showClanWindow(player);
        }
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }

    public void showClanWindow(L2PcInstance activeChar) {
        NpcHtmlMessage nhm = new NpcHtmlMessage(5);
        StringBuilder tb = new StringBuilder("");

        tb.append("<html><head><title>L2 Donate Shop</title></head><body>");
        tb.append("<center>");
        tb.append("<table width=300 border=0><tr><td width=270 height=100>" +
                "<img src=l2wonderlust.logo width=275 height=100 align=center></td></table>" +
                "<body><img src=\"L2UI.SquareGray\" width=300 height=1>" +
                "<table bgcolor=000000 width=319>" +
                "<tr>" +
                "<td><center><font color=\"0080FF\">Hello, here you can buy services</font> <font color=\"FFFFFF\"></font></center></td>" +
                "</tr>" +
                "</table>" +
                "<img src=\"L2UI.SquareGray\" width=300 height=1>" +
                "<br><br>");
        tb.append("<table><tr>");
        tb.append("<td align=center><button value=\"Augment Skills\" action=\"bypass -h npc_" + getObjectId() + "_augmentpanel\" width=\"280\" height=\"25\" back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"></td>");
        tb.append("</tr>");
        tb.append("<tr>");
        tb.append("<td align=center><button value=\"Skill Enchanter\" action=\"bypass -h npc_" + getObjectId() + "_donatewin\" width=\"280\" height=\"25\" back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"></td>");
        tb.append("</tr>");
        tb.append("<tr>");
        tb.append("<td align=center><button value=\"Clan Level Up & Skills\" action=\"bypass -h npc_" + getObjectId() + "_clan\" width=\"280\" height=\"25\" back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"></td>");
        tb.append("</tr>");
        tb.append("<tr>");
        tb.append("<td align=center><button value=\"Hero\" action=\"bypass -h npc_" + getObjectId() + "_hero\" width=\"280\" height=\"25\" back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"></td>");
        tb.append("</tr>");

        tb.append("</table>");
        tb.append("<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32>");
        tb.append("</center>");
        tb.append("<table width=300>");
        tb.append("<tr>");
        tb.append("<td><center><font color=\"0088ff\">WebSite:</font>  <font color=\"a9a9a2\">L2Wonderlust.com</font></center></td>");
        tb.append("</tr>");
        tb.append("</table>");

        tb.append("</body></html>");

        nhm.setHtml(tb.toString());
        activeChar.sendPacket(nhm);
    }

    public static void augments(L2PcInstance activeChar, int ammount, int attributes, int idaugment, int levelaugment) {
        L2ItemInstance rhand = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
        if (activeChar.getInventory().getInventoryItemCount(itemid, 0) >= ammount) {


            if (rhand == null) {
                activeChar.sendMessage(activeChar.getName() + " have to equip a weapon.");
                return;
            } else if (rhand.getItem().getCrystalType() == 0 || rhand.getItem().getCrystalType() == 1 || rhand.getItem().getCrystalType() == 2) {
                activeChar.sendMessage("Your weapon must be at least B grade to be augmented");
                return;
            } else if (rhand.isHeroItem()) {
                activeChar.sendMessage("You cannot add augment on a hero weapon" + rhand.getItemName() + " !");
                return;
            } else if (!rhand.isAugmented() || ((rhand.isAugmented() && rhand.getAugmentation().getSkill().getLevel() < levelaugment) && rhand.getAugmentation().getSkill().getId() == idaugment)) {

                if (!rhand.isAugmented()) {
                    //get items
                    if (!activeChar.destroyItemByItemId("Donate Coin", itemid, ammount, activeChar, true)) {
                        return;
                    }
                    activeChar.sendMessage("You succesfully added " + SkillTable.getInstance().getInfo(idaugment, levelaugment).getName() + " level 1.");
                    //give augment
                    augmentweapondatabase(activeChar, attributes, idaugment, 1);
                } else {
                    int augmentationLevel = rhand.getAugmentation().getSkill().getLevel();
                    //get items
                    if (!activeChar.destroyItemByItemId("Donate Coin", itemid, ammount, activeChar, true)) {
                        return;
                    }
                    activeChar.sendMessage("You succesfully added " + SkillTable.getInstance().getInfo(idaugment, levelaugment).getName() + " level " + (augmentationLevel + 1) + ".");
                    rhand.removeAugmentation();
                    //give augment
                    augmentweapondatabase(activeChar, attributes, idaugment, (augmentationLevel + 1));

                }

                DonateAudit.auditGMAction(activeChar.getName() + " [" + activeChar.getObjectId() + "]", "Donated Augment : " + SkillTable.getInstance().getInfo(idaugment, levelaugment).getName() + " Level " + rhand.getAugmentation().getSkill().getLevel() + " on " + rhand.getItemName() + ". ", "Donate Coins : " + ammount);

            } else if (rhand.getAugmentation().getSkill().getLevel() == levelaugment && rhand.getAugmentation().getSkill().getId() == idaugment) {
                activeChar.sendMessage("Your augment is already at the maximum level");
                return;
            } else {
                activeChar.sendMessage("You have another augment on this weapon");
            }


        } else {
            activeChar.sendMessage("You do not have enough Donate Coins.");
        }
    }

    public static void augmentweapondatabase(L2PcInstance player, int attributes, int id, int level) {
        L2ItemInstance item = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
        L2Augmentation augmentation = new L2Augmentation(item, attributes, id, level, true);
        augmentation.applyBoni(player);
        item.setAugmentation(augmentation);

        try (
                Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            PreparedStatement statement = con.prepareStatement("REPLACE INTO augmentations VALUES(?,?,?,?)");
            statement.setInt(1, item.getObjectId());
            statement.setInt(2, attributes);
            statement.setInt(3, id);
            statement.setInt(4, level);
            InventoryUpdate iu = new InventoryUpdate();
            player.sendPacket(iu);
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }


    private void winds(L2PcInstance player, int count) {
        L2ItemInstance rhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
        NpcHtmlMessage html = new NpcHtmlMessage(1);
        switch (count) {


            case 1:
                String htmContent8 = HtmCache.getInstance().getHtm("data/html/mods/donate/augment/active/page1.htm");
                html.setHtml(htmContent8);
                html.replace("%objectId%", String.valueOf(this.getObjectId()));
                html.replace("%charname%", player.getName());
                if (rhand != null && rhand.isAugmented() && rhand.getAugmentation() != null && rhand.getAugmentation().getSkill() != null && rhand.getAugmentation().getSkill().getLevel() >= 1) {
                    html.replace("%level%", rhand.getAugmentation().getSkill().getLevel());
                }
                html.replace("%level%", "None");
                player.sendPacket(html);
                break;
            case 2:
                String htmContent9 = HtmCache.getInstance().getHtm("data/html/mods/donate/augment/active/page2.htm");
                html.setHtml(htmContent9);
                html.replace("%objectId%", String.valueOf(this.getObjectId()));
                html.replace("%charname%", player.getName());
                if (rhand != null && rhand.isAugmented() && rhand.getAugmentation() != null && rhand.getAugmentation().getSkill() != null && rhand.getAugmentation().getSkill().getLevel() >= 1) {
                    html.replace("%level%", rhand.getAugmentation().getSkill().getLevel());
                }
                html.replace("%level%", "None");
                player.sendPacket(html);
                break;
            case 3:
                String htmContent10 = HtmCache.getInstance().getHtm("data/html/mods/donate/augment/active/page3.htm");
                html.setHtml(htmContent10);
                html.replace("%objectId%", String.valueOf(this.getObjectId()));
                html.replace("%charname%", player.getName());
                if (rhand != null && rhand.isAugmented() && rhand.getAugmentation() != null && rhand.getAugmentation().getSkill() != null && rhand.getAugmentation().getSkill().getLevel() >= 1) {
                    html.replace("%level%", rhand.getAugmentation().getSkill().getLevel());
                }
                html.replace("%level%", "None");
                player.sendPacket(html);
                break;
            case 4:
                String htmContent11 = HtmCache.getInstance().getHtm("data/html/mods/donate/augment/active/page4.htm");
                html.setHtml(htmContent11);
                html.replace("%objectId%", String.valueOf(this.getObjectId()));
                html.replace("%charname%", player.getName());
                if (rhand != null && rhand.isAugmented() && rhand.getAugmentation() != null && rhand.getAugmentation().getSkill() != null && rhand.getAugmentation().getSkill().getLevel() >= 1) {
                    html.replace("%level%", rhand.getAugmentation().getSkill().getLevel());
                }
                html.replace("%level%", "None");
                player.sendPacket(html);
                break;
            case 5:
                String htmContent12 = HtmCache.getInstance().getHtm("data/html/mods/donate/augment/active/page5.htm");
                html.setHtml(htmContent12);
                html.replace("%objectId%", String.valueOf(this.getObjectId()));
                html.replace("%charname%", player.getName());
                if (rhand != null && rhand.isAugmented() && rhand.getAugmentation() != null && rhand.getAugmentation().getSkill() != null && rhand.getAugmentation().getSkill().getLevel() >= 1) {
                    html.replace("%level%", rhand.getAugmentation().getSkill().getLevel());
                }
                html.replace("%level%", "None");
                player.sendPacket(html);
                break;
            case 6:
                String htmContent13 = HtmCache.getInstance().getHtm("data/html/mods/donate/augment.htm");
                html.setHtml(htmContent13);
                html.replace("%objectId%", String.valueOf(this.getObjectId()));
                html.replace("%charname%", player.getName());
                player.sendPacket(html);
                break;
            case 7:
                String htmContent14 = HtmCache.getInstance().getHtm("data/html/mods/donate/augment/passive/page1.htm");
                html.setHtml(htmContent14);
                html.replace("%objectId%", String.valueOf(this.getObjectId()));
                html.replace("%charname%", player.getName());
                if (rhand != null && rhand.isAugmented() && rhand.getAugmentation() != null && rhand.getAugmentation().getSkill() != null && rhand.getAugmentation().getSkill().getLevel() >= 1) {
                    html.replace("%level%", rhand.getAugmentation().getSkill().getLevel());
                }
                html.replace("%level%", "None");
                player.sendPacket(html);
                break;
            case 8:
                String htmContent15 = HtmCache.getInstance().getHtm("data/html/mods/donate/augment/passive/page2.htm");
                html.setHtml(htmContent15);
                html.replace("%objectId%", String.valueOf(this.getObjectId()));
                html.replace("%charname%", player.getName());
                if (rhand != null && rhand.isAugmented() && rhand.getAugmentation() != null && rhand.getAugmentation().getSkill() != null && rhand.getAugmentation().getSkill().getLevel() >= 1) {
                    html.replace("%level%", rhand.getAugmentation().getSkill().getLevel());
                }
                html.replace("%level%", "None");
                player.sendPacket(html);
                break;
            case 9:
                String htmContent16 = HtmCache.getInstance().getHtm("data/html/mods/donate/clan.htm");
                html.setHtml(htmContent16);
                html.replace("%objectId%", String.valueOf(this.getObjectId()));
                html.replace("%charname%", player.getName());
                player.sendPacket(html);
                break;
            case 10:
                if (player.getClan() == null) {
                    player.sendMessage("You Do Not Have A Clan");
                } else if (!player.isClanLeader()) {
                    player.sendMessage("You Are Not The Leader");
                } else {
                    if (player.getClan().getLevel() == 8) {
                        player.sendMessage("Your Clan Is Already Level 8");
                    } else {
                        final L2ItemInstance dc = player.getInventory().getItemByItemId(4037);

                        if (dc == null || dc.getCount() < 10) {
                            player.sendMessage("You don't have enough donate coins.");
                        } else {
                            player.destroyItemByItemId("Consume", 4037, 10, null, false);

                            player.getClan().changeLevel(8);
                            player.getClan().broadcastClanStatus();
                            player.sendMessage("Your clan is now level 8.");
                            player.setTarget(player);
                            player.broadcastPacket(new MagicSkillUser(player, 5103, 1, 1000, 0));
                        }
                    }
                }

                break;
            case 11:
                if (player.getClan() == null) {
                    player.sendMessage("You Do Not Have A Clan");
                } else if (!player.isClanLeader()) {
                    player.sendMessage("You Are Not The Leader");
                } else {
                    if (player.getClan().getLevel() < 8) {
                        player.sendMessage("Your Clan Has To Be Level 8");
                    } else {
                        if (!player.getClan().gotFullSkills) {
                            final L2ItemInstance dc = player.getInventory().getItemByItemId(4037);

                            if (dc == null || dc.getCount() < 20) {
                                player.sendMessage("You don't have enough donate coins.");
                            } else {
                                player.destroyItemByItemId("Consume", 4037, 20, null, false);

                                for (int id = 370; id < 391; id++) {
                                    final L2Skill skill = SkillTable.getInstance().getInfo(id, 3);
                                    if (skill != null) {

                                        final SystemMessage sm = new SystemMessage(SystemMessageId.CLAN_SKILL_S1_ADDED);
                                        sm.addSkillName(id);
                                        player.getClan().broadcastToOnlineMembers(sm);
                                        player.getClan().addNewSkill(skill);
                                        player.getClan().broadcastToOnlineMembers(new PledgeSkillList(player.getClan()));

                                    }
                                }
                                final L2Skill skill = SkillTable.getInstance().getInfo(391, 1);
                                if (skill != null) {
                                    final SystemMessage sm = new SystemMessage(SystemMessageId.CLAN_SKILL_S1_ADDED);
                                    sm.addSkillName(391);
                                    player.getClan().broadcastToOnlineMembers(sm);
                                    player.getClan().addNewSkill(skill);
                                    player.getClan().broadcastToOnlineMembers(new PledgeSkillList(player.getClan()));

                                }

                                for (final L2PcInstance member : player.getClan().getOnlineMembers("")) {
                                    member.sendSkillList();
                                }
                                player.getClan().gotFullSkills = true;
                                player.sendMessage("You received full clan skills.");
                            }
                        } else {
                            player.sendMessage("You already received full clan skills.");
                        }
                    }
                }
                break;
            case 12:
                String htmContent17 = HtmCache.getInstance().getHtm("data/html/mods/donate/hero.htm");
                html.setHtml(htmContent17);
                html.replace("%objectId%", String.valueOf(this.getObjectId()));
                html.replace("%charname%", player.getName());
                player.sendPacket(html);
                break;
            case 13:
                if (player.isHero()) {
                    player.sendMessage("You Are Already A Hero!.");
                } else if (!player.isNoble()) {
                    player.sendMessage("You Have To Be Noblesse To Buy Hero Status!.");
                } else {
                    final L2ItemInstance dc = player.getInventory().getItemByItemId(4037);

                    if (dc == null || dc.getCount() < 30) {
                        player.sendMessage("You don't have enough donate coins.");
                    } else {
                        player.destroyItemByItemId("Consume", 4037, 30, null, false);
                        player.broadcastPacket(new SocialAction(player.getObjectId(), 16));
                        player.setHero(true);
                        updateDatabase(player, 7 * 24L * 60L * 60L * 1000L);
                        player.sendMessage("You Are Now a Hero,You Are Granted With Hero Status,Skills,Aura For 1 Week.");
                        player.broadcastUserInfo();
                        player.getInventory().addItem("Wings", 6842, 1, player, null);
                    }
                }
                break;
            case 14:
                if (player.isHero()) {
                    player.sendMessage("You Are Already A Hero!.");
                } else if (!player.isNoble()) {
                    player.sendMessage("You Have To Be Noblesse To Buy Hero Status!.");
                } else {
                    final L2ItemInstance dc = player.getInventory().getItemByItemId(4037);

                    if (dc == null || dc.getCount() < 60) {
                        player.sendMessage("You don't have enough donate coins.");
                    } else {
                        player.destroyItemByItemId("Consume", 4037, 60, null, false);


                        player.broadcastPacket(new SocialAction(player.getObjectId(), 16));
                        player.setHero(true);
                        updateDatabase(player, 30 * 24L * 60L * 60L * 1000L);
                        player.sendMessage("You Are Now a Hero,You Are Granted With Hero Status,Skills,Aura For 1 Month.");
                        player.broadcastUserInfo();
                        player.getInventory().addItem("Wings", 6842, 1, player, null);
                    }
                }
                break;
            case 15:
                if (player.isHero()) {
                    player.sendMessage("You Are Already A Hero!.");
                } else if (!player.isNoble()) {
                    player.sendMessage("You Have To Be Noblesse To Buy Hero Status!.");
                } else {
                    final L2ItemInstance dc = player.getInventory().getItemByItemId(4037);

                    if (dc == null || dc.getCount() < 90) {
                        player.sendMessage("You don't have enough donate coins.");
                    } else {
                        player.destroyItemByItemId("Consume", 4037, 90, null, false);


                        player.broadcastPacket(new SocialAction(player.getObjectId(), 16));
                        player.setHero(true);
                        updateDatabase(player, 1000 * 24L * 60L * 60L * 1000L);
                        player.sendMessage("You Are Now a Hero,You Are Granted With Hero Status,Skills,Aura Forever.");
                        player.broadcastUserInfo();
                        player.getInventory().addItem("Wings", 6842, 1, player, null);
                    }
                }
                break;
        }
    }

    private void updateDatabase(final L2PcInstance player, final long heroTime) {
        Connection con = null;
        try {
            if (player == null)
                return;

            con = L2DatabaseFactory.getInstance().getConnection(false);
            PreparedStatement stmt = con.prepareStatement(INSERT_DATA);

            stmt.setInt(1, player.getObjectId());
            stmt.setString(2, player.getName());
            stmt.setInt(3, 1);
            stmt.setInt(4, player.isNoble() ? 1 : 0);
            stmt.setInt(5, player.isDonator() ? 1 : 0);
            stmt.setLong(6, heroTime == 0 ? 0 : System.currentTimeMillis() + heroTime);
            stmt.execute();
            stmt.close();
            stmt = null;
        } catch (final Exception e) {
            if (Config.ENABLE_ALL_EXCEPTIONS)
                e.printStackTrace();

            LOGGER.error("Error: could not update database: ", e);
        } finally {
            CloseUtil.close(con);

            con = null;
        }
    }

}
