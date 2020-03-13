package com.l2jfrozen.gameserver.model.actor.instance;

import com.l2jfrozen.gameserver.model.entity.olympiad.Olympiad;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;
import javolution.text.TextBuilder;

import java.util.StringTokenizer;

public class L2InformationManagerInstance extends L2NpcInstance {
    public L2InformationManagerInstance(int objectId, L2NpcTemplate template) {
        super(objectId, template);
    }


    public void onBypassFeedback(L2PcInstance player, String command) {
        if (command.startsWith("serverinfo")) {
            StringTokenizer st = new StringTokenizer(command);
            st.nextToken();

            String cmd = st.nextToken();

            switch (cmd) {
                case "rates": {
                    NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
                    msg.setHtml(rateWindow());
                    msg.replace("%objectId%", String.valueOf(getObjectId()));
                    player.sendPacket(msg);
                }
                break;
                case "commands": {

                    NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
                    msg.setHtml(commandWindow());
                    msg.replace("%objectId%", String.valueOf(getObjectId()));
                    player.sendPacket(msg);
                }
                break;
                case "gear": {

                    NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
                    msg.setHtml(gearWindow());
                    msg.replace("%objectId%", String.valueOf(getObjectId()));
                    player.sendPacket(msg);
                }
                break;
                case "olympiad": {

                    NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
                    msg.setHtml(olympiadWindow());
                    msg.replace("%objectId%", String.valueOf(getObjectId()));
                    player.sendPacket(msg);
                }
                break;
                case "farm": {

                    NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
                    msg.setHtml(farmWindow());
                    msg.replace("%objectId%", String.valueOf(getObjectId()));
                    player.sendPacket(msg);
                }
                break;
                case "raid": {

                    NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
                    msg.setHtml(raidbossWindow());
                    msg.replace("%objectId%", String.valueOf(getObjectId()));
                    player.sendPacket(msg);
                }
                break;
                case "sFightInfo": {

                    NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
                    msg.setHtml(streetFightInfo());
                    msg.replace("%objectId%", String.valueOf(getObjectId()));
                    player.sendPacket(msg);
                }
                break;
                case "rules": {

                    NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
                    msg.setHtml(serverRulesWindow());
                    msg.replace("%objectId%", String.valueOf(getObjectId()));
                    player.sendPacket(msg);
                }
                break;
                case "donate": {

                    NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
                    msg.setHtml(donateWindow());
                    msg.replace("%objectId%", String.valueOf(getObjectId()));
                    player.sendPacket(msg);
                }
                break;
                case "back": {

                    NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
                    msg.setHtml(infoWindow());
                    msg.replace("%objectId%", String.valueOf(getObjectId()));
                    player.sendPacket(msg);
                }
                break;
            }
        }
    }

    public void showChatWindow(L2PcInstance player, int val) {
        NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
        msg.setHtml(infoWindow());
        msg.replace("%objectId%", String.valueOf(getObjectId()));
        player.sendPacket(msg);
    }

    private String infoWindow() {

        TextBuilder tb = new TextBuilder();
        tb.append("<html>" +
                "<title>Information Manager</title>" +
                "<body><center>" +
                "<table width=300 border=0>" +
                "<tr>" +
                "<td width=270 height=95><img src=l2wonderlust.logo width=275 height=100 align=center></td>" +
                "</table>");
        tb.append("<table><tr>");
        tb.append("<td><button value=\"Server Rates\" action=\"bypass -h npc_%objectId%_serverinfo rates\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"></td>");
        tb.append("</tr>");
        tb.append("<tr>");
        tb.append("<td><button value=\"Server Commands\" action=\"bypass -h npc_%objectId%_serverinfo commands\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"></td>");
        tb.append("</tr>");
        tb.append("<tr>");
        tb.append("<td><button value=\"Gear Information\" action=\"bypass -h npc_%objectId%_serverinfo gear\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"></td>");
        tb.append("</tr>");
        tb.append("<tr>");
        tb.append("<td><button value=\"Olympiad Information\" action=\"bypass -h npc_%objectId%_serverinfo olympiad\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"></td>");
        tb.append("</tr>");
        tb.append("<tr>");
        tb.append("<td><button value=\"Farm Zones Information\" action=\"bypass -h npc_%objectId%_serverinfo farm\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"></td>");
        tb.append("</tr>");
        tb.append("<tr>");
        tb.append("<td><button value=\"Raidboss Information\" action=\"bypass -h npc_%objectId%_serverinfo raid\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"></td>");
        tb.append("</tr>");
        tb.append("<tr>");
        tb.append("<td><button value=\"Street Fight Information\" action=\"bypass -h npc_%objectId%_serverinfo sFightInfo\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"></td>");
        tb.append("</tr>");
        tb.append("<tr>");
        tb.append("<td><button value=\"Server Rules\" action=\"bypass -h npc_%objectId%_serverinfo rules\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"></td>");
        tb.append("</tr>");
        tb.append("<tr>");
        tb.append("<td><button value=\"Donate Information\" action=\"bypass -h npc_%objectId%_serverinfo donate\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"></td>");
        tb.append("</tr></table>");
        tb.append("</center></body></html>");

        return tb.toString();
    }

    private String rateWindow() {

        TextBuilder tb = new TextBuilder();
        tb.append("<html>" +
                "<title>Information Manager</title>" +
                "<body><center><font color=\"0080FF\">Server Rates:</font>" +
                "<br><br>");

        tb.append("SP: x500. EXP: x500. Quest: Custom. Adena: Custom.<br>");

        tb.append("<font color=\"FFFF33\">Enchant Rates:</font><br>" +
                "Weapons/Armors (normal/blessed) +12, (crystal) +16<br>" +
                "<center><table width=200><tr>" +
                "<td><font color=\"00FF00\">[ Normal ]</font></td>" +
                "<td><font color=\"FF6600\">[ Blessed ]</font></td>" +
                "<td><font color=\"00FFFF\">[ Crystal ]</font></td>" +
                "</tr>" +
                "<tr>" +
                "<td><font color=\"00FF00\">    75%</font></td>" +
                "<td><font color=\"FF6600\">   100%</font></td>" +
                "<td><font color=\"00FFFF\">    50%</font></td>" +
                "</tr>" +
                "</table><br>If Crystal enchant fails, enchant of gear will stay the same.<br>" +
                "<br><font color=\"FFFF33\">Lifestone Rates:</font><br>" +
                "Mid = 10%, High = 15%, Top = 20%<br><br>" +
                "Server Auto restarts every day at 07:00 gmt+2" );

        tb.append("<br><img src=\"L2UI.SquareGray\" width=300 height=1><br>");
        tb.append("<button value=\"Back\" action=\"bypass -h npc_%objectId%_serverinfo back\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"><br>");
        tb.append("</center></body></html>");
        return tb.toString();
    }

    private String commandWindow() {
        TextBuilder tb = new TextBuilder();
        tb.append("<html>" +
                "<title>Information Manager</title>" +
                "<body><center><font color=\"0080FF\">Server Commands:</font>" +
                "<br><br>");

        tb.append("<font color=\"FFFF33\">.menu .repair .online .exit(pvp zone)</font><br>" +
                "<font color=\"FFFF33\">.vote .info .raidinfo .dressme</font><br>" +
                "<font color=\"FFFF33\">.menu:</font> Contains options for <br>Auto potions,Buff protection," +
                "Message refusal,<br> Trade and Party Request Protection.<br>" +
                "<font color=\"FFFF33\">.vote:</font> Use this command to collect your reward<br>" +
                "after you vote for us on www.l2wonderlust.com<br>" +
                "<font color=\"FFFF33\">.repair:</font> If your character is stuck, use .repair from<br>" +
                "another character of the same account to unstuck him.<br>" +
                "<font color=\"FFFF33\">.online:</font> Shows you how many players are currently online.<br>" +
                "<font color=\"FFFF33\">.exit:</font> Usable only in pvp zone, used to exit the pvp zone.<br>" +
                "<font color=\"FFFF33\">.info:</font> Shows general information about the server.<br>" +
                "<font color=\"FFFF33\">.raidinfo:</font> Shows information about raid bosses.<br>" +
                "<font color=\"FFFF33\">.dressme:</font>" +
                "A window will pop up, enable the system.<br>" +
                "Then press change costume and select<br>" +
                "the name of your costume.<br>"+
                "<font color=\"FFFF33\">.report:</font> You can report a player who is botting.<br> GMs will be notified.<br>" );

        tb.append("<br><img src=\"L2UI.SquareGray\" width=300 height=1><br>");
        tb.append("<button value=\"Back\" action=\"bypass -h npc_%objectId%_serverinfo back\" width=\"280\" height=\"25\" back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"><br>");
        tb.append("</center></body></html>");
        return tb.toString();
    }

    private String gearWindow() {
        TextBuilder tb = new TextBuilder();
        tb.append("<html>" +
                "<title>Information Manager</title>\n" +
                "<body><center><font color=\"0080FF\">Gear Information:</font>" +
                "<br>");

        tb.append("<font color=\"FFFF33\">DC ROBE SET -> APELLA ROBE SET</font><br>(Same stats +5% casting speed)<br>" +
                "<font color=\"FFFF33\">DRACONIC LIGHT SET -> APELLA LIGHT SET</font><br>(Same stats +5% attack speed)<br>" +
                "<font color=\"FFFF33\">IMPERIAL HEAVY SET -> APELLA HEAVY SET</font><br>(Same stats +5% p. attack)<br><br>" +
                "<font color=\"FFFF33\">APELLA ROBE SET -> DYNASTY ROBE SET</font><br>(Same stats +3% p.def)<br>" +
                "<font color=\"FFFF33\">APELLA LIGHT SET -> DYNASTY LIGHT SET</font><br>(Same stats +3% p.def)<br>" +
                "<font color=\"FFFF33\">APELLA HEAVY SET -> DYNASTY HEAVY SET</font><br>(Same stats +3% p.def)" +
                "<br><br>" +
                "<font color=\"FFFF33\">RETAIL S WEAPONS -> WONDER WEAPONS LVL 1</font><br>(Same stats +5% damage)"+
                "<br><font color=\"FFFF33\">RETAIL S WEAPONS -> WONDER WEAPONS LVL 2</font><br>(Same stats +8% damage)");

        tb.append("<br><img src=\"L2UI.SquareGray\" width=300 height=1><br>");
        tb.append("<button value=\"Back\" action=\"bypass -h npc_%objectId%_serverinfo back\" width=\"280\" height=\"25\" back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"><br>");
        tb.append("</center></body></html>");
        return tb.toString();
    }

    private String olympiadWindow() {
        TextBuilder tb = new TextBuilder();
        tb.append("<html>" +
                "<title>Information Manager</title>" +
                "<body><center><font color=\"0080FF\">Olympiad Information:</font>" +
                "<br><br>");

        tb.append("<font color=\"FFFF33\">"+Olympiad.getInstance().olyEndMsg() + "</font><br><br>" +
                "Olympiad period starts on Monday and ends on Sunday<br>" +
                "Olympiad time starts at 18:00 gmt+2<br>and ends at 00:00 gmt+2<br><br>" +
                "Custom items are allowed in Olympiad.<br>" +
                "Max enchant in Olympiad is +12.<br>" +
                "If you have more than +12, your stats<br>will be reduced as if you were +12 geared");

        tb.append("<br><img src=\"L2UI.SquareGray\" width=300 height=1><br>");
        tb.append("<button value=\"Back\" action=\"bypass -h npc_%objectId%_serverinfo back\" width=\"280\" height=\"25\" back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"><br>");
        tb.append("</center></body></html>");
        return tb.toString();
    }

    private String farmWindow() {
        TextBuilder tb = new TextBuilder();
        tb.append("<html>\n" +
                "<title>Information Manager</title>" +
                "<body><center><font color=\"0080FF\">Farm Zones Information:</font>" +
                "<br><br>");

        tb.append("<font color=\"FFFF33\">Adena farm zones (1,2,3,4) drops :</font><br>600-700 adena per mob.<br>" +
                "<font color=\"FFFF33\">Solo skull zones (safe,1,2,3,4,5) drops :</font><br>5-6 skulls and 250-300 adena per mob.<br>" +
                "<font color=\"FFFF33\">4+ Party zone drops:</font><br>150 skulls.Chance:(mid,high,top ls,bogs,crystal enchants).<br>" +
                "<font color=\"FFFF33\">LS/Bog zones (safe,1,2) drops:</font><br>Chance:(mid,high,top ls,bogs).<br>" +
                "<font color=\"FFFF33\">LS zones (1,2) drops:</font><br>Chance:(mid,high,top ls).<br>" +
                "<font color=\"FFFF33\">Safe Scrolls farm zone drops:</font><br>" +
                "Chance:(BEAS,BEWS).<br>" +
                "<font color=\"FFFF33\">PvP zone drops:</font><br>1 PvP Coin per kill.");

        tb.append("<br><img src=\"L2UI.SquareGray\" width=300 height=1><br>");
        tb.append("<button value=\"Back\" action=\"bypass -h npc_%objectId%_serverinfo back\" width=\"280\" height=\"25\" back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"><br>");
        tb.append("</center></body></html>");
        return tb.toString();
    }

    private String raidbossWindow() {
        TextBuilder tb = new TextBuilder();
        tb.append("<html>" +
                "<title>Information Manager</title>" +
                "<body><center><font color=\"0080FF\">Raidboss Drops Information:</font>" +
                "<br><br>");

        tb.append("<font color=\"FFFF33\">Gannicus</font><br>" +
                "TOP LS,BOG,CEAS-CEWS,50k Adena.<br>" +
                "Dynasty part (10%).<br>" +
                "<font color=\"FFFF33\">Agron</font><br>" +
                "TOP LS,BOG,CEAS-CEWS,50k Adena.<br>" +
                "Dynasty part (10%).<br>" +
                "<font color=\"FFFF33\">Ashur</font><br>" +
                "TOP LS,BOG,CEAS-CEWS,50k Adena.<br>" +
                "Dynasty part (10%).<br>" +
                "<font color=\"FFFF33\">Queen Ant</font><br>" +
                "Ring of Queen Ant 1-2,40k+ Adena,Bogs.<br>" +
                "Dynasty part (15%).<br>" +
                "<font color=\"FFFF33\">Core</font><br>" +
                "Ring of Core 1-2,30k Adena,Bogs.<br>" +
                "Dynasty part (15%).<br>" +
                "<font color=\"FFFF33\">Orfen</font><br>" +
                "Earring of Orfen 1-2,30k Adena,Bogs.<br>" +
                "Dynasty part (15%).<br>" +
                "<font color=\"FFFF33\">Baium</font><br>" +
                "Ring of Baium 1-2,100k Adena,CEWS,CEAS,Top ls.<br>" +
                "Dynasty part (30%).<br>" +
                "<font color=\"FFFF33\">Zaken</font><br>" +
                "Earring of Zaken,40k+ Adena,BoGs.<br>" +
                "Dynasty part (15%).<br>" +
                "<font color=\"FFFF33\">Antharas</font><br>" +
                "Earring of Antharas 1-2,100k Adena,CEWS,CEAS,Top Ls.<br>" +
                "Dynasty part (30%).<br>" +
                "<font color=\"FFFF33\">Valakas</font><br>" +
                "Necklace of Valakas 1-2,100k Adena,CEWS,CEAS,Top Ls.<br>" +
                "Dynasty part (30%).<br>" +
                "<font color=\"FFFF33\">Spartakus</font><br>" +
                "200k Adena,Clan ReP Coin,ToP Ls.<br>" +
                "Dynasty part (30%).<br>" +
                "<font color=\"FFFF33\">Crixus</font><br>" +
                "Frintezza's Necklace 1-2,100k Adena,CEWS,CEAS,ToP Ls.<br>" +
                "Dynasty part (30%).<br>" +
                "<font color=\"FFFF33\">Oenomaus</font><br>" +
                "Hero Coin,200k Adena,CEWS,CEAS,Random Jewl.<br>" +
                "Dynasty part(100%),Wonder Weapon lvl 2(100%).<br>" +
                "Accessory of Sorrow(30%),Accessory of Anger(30%).<br>" +
                "<font color=\"FFFF33\">Noblesse Boss</font><br>" +
                "Nobless status for full party,50k adena<br>");

        tb.append("<br><img src=\"L2UI.SquareGray\" width=300 height=1><br>");
        tb.append("<button value=\"Back\" action=\"bypass -h npc_%objectId%_serverinfo back\" width=\"280\" height=\"25\" back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"><br>");
        tb.append("</center></body></html>");
        return tb.toString();
    }

    private String streetFightInfo() {
        TextBuilder tb = new TextBuilder();
        tb.append("<html>" +
                "<title>Information Manager</title>" +
                "<body><center><font color=\"0080FF\">Street Fight Information:</font>" +
                "<br><br>");

        tb.append("Every Sunday at 00:00 gmt+2 the 1st ranked<br>" +
                "players will be rewarded.<br>" +
                "20 donate coins for 1v1 rankings winner<br>" +
                "40 donate coins for party rankings winner<br>" +
                "The party ranks reward is given to the party leader<br><br>" +
                "<font color=\"FFFF33\">1v1 Ranking System</font><br>" +
                "You get 1 point for each win and lose 1 point for each loss<br><br>" +
                "<font color=\"FFFF33\">Party Ranking System</font><br>" +
                "If your fight is 9vs9 and you win, you will get 9 points<br>" +
                "If you lose the fight you will lose 9 points<br>" +
                "If your fight is 3vs3 and you win, you will get 3 points<br>" +
                "If you lose the fight you will lose 3 points etc..<br>" +
                "The winners of the rankings will be announced<br>" +
                "and will be able to collect their<br>reward from the Arena Manager NPC.<br><br>"+
                "You do NOT receive points for fighting<br>the same player in a 10 minute period.");

        tb.append("</center><br><img src=\"L2UI.SquareGray\" width=300 height=1><br>");
        tb.append("<button value=\"Back\" action=\"bypass -h npc_%objectId%_serverinfo back\" width=280 height=25 back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"><br>");
        tb.append("</body></html>");
        return tb.toString();
    }

    private String serverRulesWindow() {
        TextBuilder tb = new TextBuilder();
        tb.append("<html>" +
                "<title>Information Manager</title>" +
                "<body><center><font color=\"0080FF\">Server Rules:</font>" +
                "<br><br>");
        tb.append("<font color=\"FFFF33\">1)</font>  " +
                "GM/ADMIN will never ask<br>" +
                "you for username or password.<br>" +
                "<font color=\"FFFF33\">2)</font> " +
                " Never shout GM/ADMIN pm me<br>" +
                "write a petition or post a thread in forum.<br>" +
                "<font color=\"FFFF33\">3)</font> " +
                "Do not ask GM/ADMIN for items<br>" +
                "nor for services beyond our donation system.<br>" +
                "<font color=\"FFFF33\">4)</font> " +
                "Do not use bots or 3rd party tool programs.<br>" +
                "Your main account will be suspended.<br>" +
                "<font color=\"FFFF33\">5)</font> " +
                "Do not advertise in any way other servers<br>" +
                "you will be jailed forever.<br>" +
                "<font color=\"FFFF33\">6)</font> " +
                "GM/ADMIN has no friends in game and<br>" +
                "will not treat any player differently.<br>" +
                "<font color=\"FFFF33\">7)</font> " +
                "Respect each other and read more<br>" +
                "about our rules on our website:<br>" +
                "www.l2wonderlust.com");
        tb.append("<br><img src=\"L2UI.SquareGray\" width=300 height=1><br>");
        tb.append("<button value=\"Back\" action=\"bypass -h npc_%objectId%_serverinfo back\" width=\"280\" height=\"25\" back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"><br>");
        tb.append("</center></body></html>");
        return tb.toString();
    }

    private String donateWindow() {
        TextBuilder tb = new TextBuilder();
        tb.append("<html>" +
                "<title>Information Manager</title>" +
                "<body><center><font color=\"0080FF\">Donate Information:</font>" +
                "<br>");

        tb.append("If you wish to support our server,<br>" +
                "you can do so in our website: www.l2wonderlust.com<br>" +
                "We accept paypal and paysafe donations.<br>" +
                "Paypal donations are automatic:<br>" +
                "Restart your character and you will receive the reward.<br>" +
                "Paysafe donations are manual:<br>contact us for more information..<br>" +
                "Our Facebook page: facebook.com/l2wonderlust<br>" +
                "Our Email: l2wonderlust@yahoo.com");

        tb.append("<br><img src=\"L2UI.SquareGray\" width=300 height=1><br>");
        tb.append("<button value=\"Back\" action=\"bypass -h npc_%objectId%_serverinfo back\" width=\"280\" height=\"25\" back=\"l2wonderlust.active\" fore=\"l2wonderlust.passive\"><br>");
        tb.append("</center></body></html>");
        return tb.toString();
    }

}