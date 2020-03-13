package com.l2jfrozen.gameserver.model.scripts;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.datatables.sql.NpcTable;
import com.l2jfrozen.gameserver.datatables.sql.SpawnTable;
import com.l2jfrozen.gameserver.model.L2Character;
import com.l2jfrozen.gameserver.model.L2Skill;
import com.l2jfrozen.gameserver.model.L2Summon;
import com.l2jfrozen.gameserver.model.L2World;
import com.l2jfrozen.gameserver.model.actor.instance.*;
import com.l2jfrozen.gameserver.model.entity.Announcements;
import com.l2jfrozen.gameserver.model.spawn.L2Spawn;
import com.l2jfrozen.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jfrozen.gameserver.network.serverpackets.PlaySound;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;
import com.l2jfrozen.gameserver.thread.ThreadPoolManager;

import java.util.*;

/**
 * Created by Server1 on 8/3/2017.
 */
public class ArenaFight {


    private final Vector<L2PcInstance> fighters = new Vector<L2PcInstance>();
    private int[][] spawns;
    private HashMap<L2PcInstance, Integer> karma = new HashMap<L2PcInstance, Integer>();

    private int idArena;
    private int idFight;

    private Vector<L2Spawn> buffers = new Vector<L2Spawn>();
    private Vector<L2PcInstance> observers = new Vector<>();
    private int[] observersLoc;

    private boolean gameEnded = false;

    private boolean betActivated = false;
    public HashMap<Integer, long[]> bets = new HashMap<Integer, long[]>();

    private double p1TotalBetAmount = 0;
    private double p2TotalBetAmount = 0;


    private double p1Odds = 0;
    private double p2Odds = 0;

    public boolean isStarted = false;

    public ArenaFight(L2PcInstance playerOne, L2PcInstance playerTwo, int idArena, int idFight) {
        fighters.add(playerOne);
        fighters.add(playerTwo);
        this.idArena = idArena;
        this.idFight = idFight;
    }

    private void teleportPlayers() {
        int count = 0;

        for (L2PcInstance p : fighters) {
            if (p.isInParty()) {
                p.leaveParty();
            }

            p.teleToLocation(spawns[count][0], spawns[count][1], spawns[count][2]);
            count++;

        }
    }

    private boolean fightCountdown() {

        for (final L2PcInstance fighter : fighters) {
            if (fighter != null) {
                fighter.sendPacket(new ExShowScreenMessage("You have 60 seconds to prepare.", 3000));
            }
        }

        for (int i = 60; i > 0; i -= 1) {
            switch (i) {
                case 45:
                case 30:
                case 15:
                case 10:
                case 3:
                case 2:
                case 1:

                    for (final L2PcInstance fighter : fighters) {
                        if (fighter != null && fighter.isOnline() == 1) {
                            fighter.sendPacket(new ExShowScreenMessage(i + " seconds...", 3000));
                        } else {
                            return false;
                        }
                    }

                    break;
            }
            try {
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                return false;
            }
        }
        for (final L2PcInstance fighter : fighters) {
            if (fighter == null || fighter.isOnline() == 0) {
                return false;
            }
        }
        return true;
    }

    private void startFight() {

        betActivated = false;
        L2ArenaManagerInstance.refreshOpenBets(idArena, false);
        unspawnBuffers();
        isStarted = true;
        Announcements.getInstance().announceToAll("Street Fight : " + fighters.firstElement().getName() + " vs " + fighters.lastElement().getName() + " started!!");
        for (L2PcInstance p : fighters) {

            p.sendPacket(new ExShowScreenMessage("Fight to the death!", 3000));

            p.updatePvPFlag(1);
            p.updatePvPStatus();

            p.setCurrentHpMp(p.getMaxHp(), p.getMaxMp());
            p.setCurrentCp(p.getMaxCp());

            if (p.getClassId().getId() == 90 || p.getClassId().getId() == 113 || p.getClassId().getId() == 114) {
                for (final L2Skill skill : p.getAllSkills()) {
                    if (skill.getId() == 139 || skill.getId() == 420 || skill.getId() == 176 || skill.getId() == 406) {
                        p.enableSkill(skill);
                    }
                }
                p.updateEffectIcons();
            }


            p.setIsImobilised(false);

            p.sendPacket(new PlaySound("arena"));

            if (p.getPet() != null) {
                final L2Summon summon = p.getPet();
                summon.setIsImobilised(false);
            }
        }

        final java.util.Timer t = new java.util.Timer();
        t.schedule(new java.util.TimerTask() {
                       @Override
                       public void run() {
                           if (isStarted) {
                               if (!fighters.isEmpty()) {
                                   for (L2PcInstance p : fighters) {
                                       p.sendMessage("The match ended in a tie !");
                                       p.sendPacket(new ExShowScreenMessage("The match ended in a tie !", 3000));
                                       p.setPetrified(true);
                                       p.setIsImobilised(true);
                                   }
                               }
                               endGame();
                           }
                           t.cancel();
                           this.cancel();
                           return;
                       }
                   },
                300000
        );


    }

    public void endGame() {
        if (gameEnded) {
            return;
        }

        gameEnded = true;
        L2PcInstance winner = null;
        L2PcInstance loser = null;

        if (fighters.firstElement() != null && !fighters.firstElement().isDead() && (fighters.lastElement().isDead() || fighters.lastElement().isOnline() == 0) && fighters.firstElement().isOnline() == 1) {
            winner = fighters.firstElement();
            loser = fighters.lastElement();
            giveBetRewards(winner);
            Announcements.getInstance().announceToAll("Winner : " + winner.getName());
        } else if (fighters.lastElement() != null && !fighters.lastElement().isDead() &&
                (fighters.firstElement().isDead() || fighters.firstElement().isOnline() == 0) &&
                fighters.lastElement().isOnline() == 1) {
            winner = fighters.lastElement();
            loser = fighters.firstElement();
            giveBetRewards(winner);
            Announcements.getInstance().announceToAll("Winner : " + winner.getName());
        } else {
            Announcements.getInstance().announceToAll(fighters.firstElement().getName() + " VS " + fighters.lastElement().getName() + " ended in a tie.");
            moneyBack();
        }

        final java.util.Timer t = new java.util.Timer();
        t.schedule(new java.util.TimerTask() {
                       @Override
                       public void run() {

                           if (fighters.isEmpty()) {
                               return;
                           }
                           for (L2PcInstance p : fighters) {


                               L2ArenaManagerInstance.inFightOrWaiting.remove(p);

                               if (p.isPetrified()) {
                                   p.setPetrified(false);
                               }
                               if (p.isImobilised()) {
                                   p.setIsImobilised(false);
                               }
                               if (karma.containsKey(p)) {
                                   p.setKarma(karma.get(p));
                               }
                               p.setPvpFlag(0);
                               p.teleToLocation(82423, 149628, -3495, true);

                               if (p.isDead()) {
                                   p.doRevive();
                               }

                           }

                           isStarted = false;
                           fighters.clear();
                           removeObservers();
                           L2ArenaManagerInstance.fights.remove(ArenaFight.this);
                           L2ArenaManagerInstance.freeArenas.put(idArena, true);
                           L2ArenaManagerInstance.tryToCreateNewArena();
                           t.cancel();
                           this.cancel();
                       }
                   },
                15000
        );

        if (winner != null && loser != null) {
            winner.givePoints(winner, loser);
        }
    }

    //player disconnected
    public void endGame(L2PcInstance discPlayer) {
        if (gameEnded) {
            return;
        }

        gameEnded = true;
        L2PcInstance winner = null;

        if (fighters.firstElement() == discPlayer) {
            winner = fighters.lastElement();
            giveBetRewards(winner);
            Announcements.getInstance().announceToAll("Street Fight: The Winner is " + winner.getName() + ", " + fighters.firstElement().getName() + " disconnected.");
        } else if (fighters.lastElement() == discPlayer) {
            winner = fighters.firstElement();
            giveBetRewards(winner);
            Announcements.getInstance().announceToAll("Street Fight: The Winner is " + winner.getName() + ", " + fighters.lastElement().getName() + " disconnected.");
        } else {
            moneyBack();
            Announcements.getInstance().announceToAll("Street Fight: " + fighters.firstElement() + " VS " + fighters.lastElement() + " ended in a tie.");
        }
        if (winner != null) {
            winner.sendMessage("The other player disconnected, you won.");
        }
        final java.util.Timer t = new java.util.Timer();
        t.schedule(new java.util.TimerTask() {
                       @Override
                       public void run() {

                           if (fighters.isEmpty()) {
                               return;
                           }
                           for (L2PcInstance p : fighters) {


                               L2ArenaManagerInstance.inFightOrWaiting.remove(p);
                               if (p.isPetrified()) {
                                   p.setPetrified(false);
                               }
                               if (p.isImobilised()) {
                                   p.setIsImobilised(false);
                               }
                               if (karma.containsKey(p)) {
                                   p.setKarma(karma.get(p));
                               }
                               p.setPvpFlag(0);
                               p.teleToLocation(82423, 149628, -3495, true);

                               if (p.isDead()) {
                                   p.doRevive();
                               }

                               if (p.getClassId().getId() == 90 || p.getClassId().getId() == 113 || p.getClassId().getId() == 114 || p.getClassId().getId() == 48 || p.getClassId().getId() == 46 || p.getClassId().getId() == 5) {
                                   for (final L2Skill skill : p.getAllSkills()) {
                                       if (skill.getId() == 139 || skill.getId() == 420 || skill.getId() == 176 || skill.getId() == 406) {
                                           p.enableSkill(skill);
                                       }
                                   }
                                   p.updateEffectIcons();
                               }

                           }

                           isStarted = false;
                           fighters.clear();
                           removeObservers();
                           L2ArenaManagerInstance.fights.remove(ArenaFight.this);
                           L2ArenaManagerInstance.freeArenas.put(idArena, true);
                           L2ArenaManagerInstance.tryToCreateNewArena();
                           t.cancel();
                           this.cancel();
                       }
                   },
                15000
        );

        if (winner != null && discPlayer != null) {
            winner.givePoints(winner, discPlayer);
        }
    }

    private void loadSpawns() {
        spawns = L2ArenaManagerInstance.arenasLoc.get(idArena);
        observersLoc = L2ArenaManagerInstance.observersLoc.get(idArena);
    }

    public void beginArena() {

        L2ArenaManagerInstance.refreshArenaAvailability(idArena, false);
        L2ArenaManagerInstance.refreshOpenBets(idArena, true);
        betActivated = true;
        loadSpawns();
        spawnBuffers();

        teleportPlayers();

        if (fighters == null) {
            return;
        }
        Announcements.getInstance().announceToAll("Street Fight: " + fighters.firstElement().getName() + " vs " + fighters.lastElement().getName() + " will start in 60 seconds, place your bets.");
        for (L2PcInstance p : fighters) {
            if (p == null) {
                continue;
            }
            if (p.getKarma() > 0) {
                karma.put(p, p.getKarma());
                p.setKarma(0);
            }
            // Remove Buffs
            p.stopAllEffects();

            // Remove Summon's Buffs
            if (p.getPet() != null) {
                final L2Summon summon = p.getPet();
                summon.stopAllEffects();
                summon.setIsImobilised(true);
            }

            // Remove Tamed Beast
            if (p.getTrainedBeast() != null) {
                final L2TamedBeastInstance traindebeast = p.getTrainedBeast();
                traindebeast.stopAllEffects();
                traindebeast.doDespawn();
            }

            if (p.getClassId().getId() == 90 || p.getClassId().getId() == 113 || p.getClassId().getId() == 114) {
                for (final L2Skill skill : p.getAllSkills()) {
                    if (skill.getId() == 139 || skill.getId() == 420 || skill.getId() == 176 || skill.getId() == 406) {
                        p.disableSkill(skill);
                    }
                }
                p.updateEffectIcons();
            }

            p.setIsImobilised(true);

        }

        final java.util.Timer t = new java.util.Timer();
        t.schedule(new java.util.TimerTask() {
                       @Override
                       public void run() {

                           if (fightCountdown()) {
                               startFight();
                           } else {
                               unspawnBuffers();
                               endGame();
                           }

                           t.cancel();
                           this.cancel();
                           return;
                       }
                   },
                3000
        );

    }

    public void giveBetRewards(L2PcInstance winner) {

        for (Map.Entry<Integer, long[]> p : bets.entrySet()) {

            L2PcInstance player = L2World.getInstance().getPlayer(p.getKey());
            long[] betInfos = betInfos = p.getValue();

            if (p1TotalBetAmount > 0 && p2TotalBetAmount > 0) {

                if (winner.getObjectId() == betInfos[1]) {
                    if (winner == fighters.firstElement()) {
                        player.sendMessage("You won " + (int) ((betInfos[0] * p1Odds) + 1) + " adena from your bet.");
                        player.getInventory().addItem("Bet Reward", 57, (int) ((betInfos[0] * p1Odds) + 1), player, null);
                        winner.sendMessage("You won " + (int) ((p2TotalBetAmount) * 15 / 100) + " adena from the bets.");
                        winner.getInventory().addItem("Bet Reward", 57, (int) ((p2TotalBetAmount) * 15 / 100), winner, null);
                    } else if (winner == fighters.lastElement()) {
                        player.sendMessage("You won " + (int) ((betInfos[0] * p2Odds) + 1) + " adena from your bet.");
                        player.getInventory().addItem("Bet Reward", 57, (int) ((betInfos[0] * p2Odds) + 1), player, null);
                        winner.sendMessage("You won " + (int) ((p1TotalBetAmount) * 15 / 100) + " adena from the bets.");
                        winner.getInventory().addItem("Bet Reward", 57, (int) ((p1TotalBetAmount) * 15 / 100), winner, null);
                    }


                } else {
                    player.sendMessage("You lost " + betInfos[0] + " adena from your bet.");
                }
            } else {
                player.getInventory().addItem("Bet Reward", 57, (int) (betInfos[0]), player, null);
                player.sendMessage("There were no bets for the other fighter so you get your money back.(" + betInfos[0] + ")");
            }
        }

    }


    public void moneyBack() {
        for (Map.Entry<Integer, long[]> p : bets.entrySet()) {

            L2PcInstance player = L2World.getInstance().getPlayer(p.getKey());
            long[] betInfos = betInfos = p.getValue();

            if (fighters.firstElement().getObjectId() == betInfos[1] || fighters.lastElement().getObjectId() == betInfos[1]) {

                player.sendMessage("The match ended in TIE so you got your money back.(" + betInfos[0] + ")");
                player.getInventory().addItem("Bet Reward", 57, (int) (betInfos[0]), player, null);

            }


        }


    }

    private void spawnBuffers() {

        L2NpcTemplate template1;
        template1 = NpcTable.getInstance().getTemplate(50019);

        int count = 0;
        try {
            for (int i = 0; i < 2; i++) {

                L2Spawn b = new L2Spawn(template1);
                b.setCustom(false);
                b.setLocx(spawns[count][0]);
                b.setLocy(spawns[count][1] + 40);
                b.setLocz(spawns[count][2]);
                b.setAmount(1);
                b.setHeading(0);
                b.setRespawnDelay(1);

                SpawnTable.getInstance().addNewSpawn(b, false);


                b.init();
                buffers.add(b);
                count++;
                b = null;
            }
        } catch (final Exception e) {
            if (Config.ENABLE_ALL_EXCEPTIONS)
                e.printStackTrace();

        }

        template1 = null;

    }

    private void unspawnBuffers() {
        for (L2Spawn b : buffers) {

            if (b == null || b.getLastSpawn() == null)
                return;

            b.getLastSpawn().deleteMe();
            b.stopRespawn();
            SpawnTable.getInstance().deleteSpawn(b, false);

        }
    }

    public void addBet(L2PcInstance player, long amount, long winner) {

        if (!betActivated) {
            player.sendMessage("Bets are off, this fight is in progress.");
            return;
        }

        if (bets.containsKey(player.getObjectId())) {
            player.sendMessage("You have already bet for this fight.");
            return;
        }

        final L2ItemInstance adena = player.getInventory().getItemByItemId(57);

        if (adena.getCount() < (int) amount) {
            player.sendMessage("You don't have enough adena.");
            return;
        }

        if (amount < 0) {
            player.sendMessage("Incorrect Value.");
            return;
        }

        player.destroyItemByItemId("Consume", 57, (int) amount, null, false);
        long[] infos = new long[3];

        infos[0] = amount;
        infos[1] = winner;


        bets.put(player.getObjectId(), infos);

        calculateOdds(amount, winner);

        if (fighters.firstElement().getObjectId() == winner) {
            fighters.firstElement().sendMessage(player.getName() + " bet " + amount + " adena on you.");
            fighters.lastElement().sendMessage(player.getName() + " bet " + amount + " adena on your opponent.");
        } else if (fighters.lastElement().getObjectId() == winner) {
            fighters.firstElement().sendMessage(player.getName() + " bet " + amount + " adena on your opponent.");
            fighters.lastElement().sendMessage(player.getName() + " bet " + amount + " adena on you.");
        }

    }


    public void calculateOdds(long amount, long winner) {


        if (fighters.firstElement().getObjectId() == winner) {

            p1TotalBetAmount += amount;

        } else if (fighters.lastElement().getObjectId() == winner) {

            p2TotalBetAmount += amount;

        }
        if (p2TotalBetAmount > 0 && p1TotalBetAmount > 0) {
            p1Odds = ((p2TotalBetAmount + p1TotalBetAmount) / p1TotalBetAmount) - ((p2TotalBetAmount / p1TotalBetAmount) * 15 / 100);
            p2Odds = ((p1TotalBetAmount + p2TotalBetAmount) / p2TotalBetAmount) - ((p1TotalBetAmount / p2TotalBetAmount) * 15 / 100);
        }

    }

    public void newObserver(L2PcInstance observer) {

        observers.add(observer);

        observer.enterObserverMode(observersLoc[0], observersLoc[1], observersLoc[2]);

    }

    public Vector<L2PcInstance> getObservers() {
        return observers;
    }

    public void removeObservers() {


        for (L2PcInstance o : observers) {
            o.leaveObserverMode();
        }
        observers.clear();
    }

    public void removeOneObserver(L2PcInstance observer) {
        observers.remove(observer);
    }


    public Vector<L2PcInstance> getFighters() {
        return fighters;
    }

    public boolean isBetActivated() {
        return betActivated;
    }

    public int getIdFight() {
        return idFight;
    }

    public int getIdArena() {
        return idArena;
    }

    public double getP1TotalBetAmount() {
        return p1TotalBetAmount;
    }

    public double getP2TotalBetAmount() {
        return p2TotalBetAmount;
    }

    public double getP1Odds() {
        return p1Odds;
    }

    public double getP2Odds() {
        return p2Odds;
    }


}
