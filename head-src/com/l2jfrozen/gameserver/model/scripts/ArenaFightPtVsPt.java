package com.l2jfrozen.gameserver.model.scripts;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.datatables.sql.NpcTable;
import com.l2jfrozen.gameserver.datatables.sql.SpawnTable;
import com.l2jfrozen.gameserver.model.L2Party;
import com.l2jfrozen.gameserver.model.L2Summon;
import com.l2jfrozen.gameserver.model.actor.instance.*;
import com.l2jfrozen.gameserver.model.entity.Announcements;
import com.l2jfrozen.gameserver.model.spawn.L2Spawn;
import com.l2jfrozen.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jfrozen.gameserver.network.serverpackets.PlaySound;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;
import com.l2jfrozen.util.database.L2DatabaseFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by MaVeN on 12/3/2018.
 */
public class ArenaFightPtVsPt {


    java.util.Timer allDeadTimer = null;
    private final Vector<L2PcInstance> fighters = new Vector<>();
    private int[][] spawns;
    private HashMap<L2PcInstance, Integer> karma = new HashMap<>();

    private final L2Party party1;
    private final L2Party party2;
    private final int p1Members;
    private final int p2Members;
    private final String p1Leader;
    private final String p2Leader;
    private final int p1LeaderId;
    private final int p2LeaderId;

    private int idArena;

    private Vector<L2Spawn> buffers = new Vector<>();
    private Vector<L2PcInstance> observers = new Vector<>();
    private int[] observersLoc;

    private boolean gameEnded = false;

    public boolean isStarted = false;

    public ArenaFightPtVsPt(L2Party party1, L2Party party2, int idArena) {
        fighters.addAll(party1.getPartyMembers());
        fighters.addAll(party2.getPartyMembers());
        this.idArena = idArena;
        this.party1 = party1;
        this.party2 = party2;
        p1Members = party1.getMemberCount();
        p2Members = party2.getMemberCount();
        p1Leader = party1.getLeader().getName();
        p2Leader = party2.getLeader().getName();
        p1LeaderId = party1.getLeader().getObjectId();
        p2LeaderId = party2.getLeader().getObjectId();
    }

    private void teleportPlayers() {
        for (L2PcInstance p : party1.getPartyMembers()) {
            p.teleToLocation(spawns[0][0], spawns[0][1], spawns[0][2], true);
        }
        for (L2PcInstance p : party2.getPartyMembers()) {
            p.teleToLocation(spawns[1][0], spawns[1][1], spawns[1][2], true);
        }
    }

    private void fightCountdown() {

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
                        if (fighter != null) {
                            fighter.sendPacket(new ExShowScreenMessage(i + " seconds...", 3000));
                        }
                    }

                    break;
            }
            try {
                Thread.sleep(1000);
            } catch (final InterruptedException ignored) {
            }
        }

    }

    private void startFight() {

        unspawnBuffers();
        isStarted = true;
        Announcements.getInstance().announceToAll("Street Fight : " + party1.getLeader().getName() + "'s Party VS " + party2.getLeader().getName() + "'s Party " + party1.getPartyMembers().size() + "v" + party2.getPartyMembers().size() + " started!!");
        for (L2PcInstance p : fighters) {

            p.sendPacket(new ExShowScreenMessage("Fight to the death!", 3000));

            p.updatePvPFlag(1);
            p.updatePvPStatus();
            p.setIsImobilised(false);

            PlaySound ps = new PlaySound(1, "arena", 0, 0, 0, 0, 0);
            p.sendPacket(ps);
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
                               endGame(null);
                           }
                           t.cancel();
                           this.cancel();
                           return;
                       }
                   },
                750000
        );


    }

    public void allDead(final L2Party pt) {

        if (allDeadTimer != null) {
            allDeadTimer.cancel();
            allDeadTimer = null;
        }

        allDeadTimer = new java.util.Timer();
        allDeadTimer.schedule(new java.util.TimerTask() {
                                  @Override
                                  public void run() {
                                      if (isStarted) {

                                          if (party1 == pt) {
                                              boolean allDead = true;
                                              for (L2PcInstance p : party1.getPartyMembers()) {
                                                  if (!p.isDead() && p.isInsideCustomPartyArenaZone) {
                                                      allDead = false;
                                                      break;
                                                  }
                                              }

                                              if (allDead || party1 == null || party1.getPartyMembers().size() <= 1) {

                                                  endGame(party2);

                                              }
                                          } else {

                                              boolean allDead = true;
                                              for (L2PcInstance p : party2.getPartyMembers()) {
                                                  if (!p.isDead() && p.isInsideCustomPartyArenaZone) {
                                                      allDead = false;
                                                      break;
                                                  }
                                              }

                                              if (allDead || party2 == null || party2.getPartyMembers().size() <= 1) {

                                                  endGame(party1);

                                              }

                                          }

                                      }
                                      allDeadTimer.cancel();
                                      this.cancel();
                                  }
                              },
                6000
        );


    }

    public void endGame(L2Party winner) {
        if (gameEnded) {
            return;
        }

        gameEnded = true;

        if (winner == party1) {
            Announcements.getInstance().announceToAll("Street Fight: The Winner is " + party1.getLeader().getName() + "'s Party.");
            addPoints(p1LeaderId, p2LeaderId, p1Members, p2Members);
        } else if (winner == party2) {
            Announcements.getInstance().announceToAll("Winner : " + party1.getLeader().getName() + "'s Party.");
            addPoints(p2LeaderId, p1LeaderId, p2Members, p1Members);
        } else {
            if (party1.getLeader() != null && party2.getLeader() != null) {
                Announcements.getInstance().announceToAll("Street Fight: " + party1.getLeader().getName() + "'s Party VS " + party2.getLeader().getName() + "'s Party ended in a TIE.");
            } else {
                Announcements.getInstance().announceToAll("Street Fight: Party VS Party fight ended in a TIE.");
            }
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
                           L2ArenaManagerInstance.freeArenas.put(idArena, true);
                           L2ArenaManagerInstance.fightPtVsPt.remove(ArenaFightPtVsPt.this);
                           t.cancel();
                           this.cancel();
                       }
                   },
                15000
        );
    }

    private void loadSpawns() {
        spawns = L2ArenaManagerInstance.arenasLoc.get(idArena);
        observersLoc = L2ArenaManagerInstance.observersLoc.get(idArena);
    }

    public void beginArena() {

        L2ArenaManagerInstance.refreshArenaAvailability(idArena, false);
        loadSpawns();
        spawnBuffers();

        teleportPlayers();

        if (fighters == null) {
            return;
        }
        Announcements.getInstance().announceToAll("Street Fight: " + party1.getLeader().getName() + "'s Party VS " + party2.getLeader().getName() + "'s Party will start in 60 seconds.");
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

            }

            // Remove Tamed Beast
            if (p.getTrainedBeast() != null) {
                final L2TamedBeastInstance traindebeast = p.getTrainedBeast();
                traindebeast.stopAllEffects();
                traindebeast.doDespawn();
            }
            p.setIsImobilised(true);

        }

        final java.util.Timer t = new java.util.Timer();
        t.schedule(new java.util.TimerTask() {
                       @Override
                       public void run() {

                           fightCountdown();
                           startFight();
                           t.cancel();
                           this.cancel();
                           return;
                       }
                   },
                3000
        );

    }

    public void playerDisconnected(L2PcInstance discPlayer) {

        if (discPlayer.isInParty()) {
            discPlayer.leaveParty();
        }

        if (party1 == null || party1.getPartyMembers().size() <= 1) {
            endGame(party2);
        } else if (party2 == null || party2.getPartyMembers().size() <= 1) {
            endGame(party1);
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
                b.setLocy(spawns[count][1]);
                b.setLocz(spawns[count][2]+15);
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

    private void addPoints(int winnerId, int loserId, int winnerPtCount, int loserPtCount) {
        try {
            setArenasInfos("arena_ptloses", getArenaInfos("arena_ptpoints", loserId) + 1, loserId);
        } catch (Exception e) {
            setArenasInfos("arena_ptloses", getArenaInfos("arena_ptpoints", loserId) + 1, loserId);
        }

        try {
            setArenasInfos("arena_ptwins", getArenaInfos("arena_ptpoints", winnerId) + 1, winnerId);
        } catch (Exception e) {
            setArenasInfos("arena_ptwins", getArenaInfos("arena_ptpoints", winnerId) + 1, winnerId);
        }

        try {
            setArenasInfos("arena_ptpoints", getArenaInfos("arena_ptpoints", loserId) - loserPtCount, loserId);
        } catch (Exception e) {
            setArenasInfos("arena_ptpoints", getArenaInfos("arena_ptpoints", loserId) - loserPtCount, loserId);
        }

        try {
            setArenasInfos("arena_ptpoints", getArenaInfos("arena_ptpoints", winnerId) + winnerPtCount, winnerId);
        } catch (Exception e) {
            setArenasInfos("arena_ptpoints", getArenaInfos("arena_ptpoints", winnerId) + winnerPtCount, winnerId);
        }
    }


    public void setArenasInfos(String value, int amount, int objId) {

        Connection con = null;
        try {

            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statment = con.prepareStatement("UPDATE characters SET " + value + "=? WHERE obj_id=?");
            statment.setInt(1, amount);
            statment.setInt(2, objId);
            statment.execute();
            statment.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getArenaInfos(String value, int objId) {

        Connection get = null;
        int wins = -1;

        try {
            get = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statment = get.prepareStatement("SELECT " + value + " from characters WHERE obj_id = ?");
            statment.setInt(1, objId);
            ResultSet rset = statment.executeQuery();

            if (rset.next()) {
                wins = rset.getInt(value);
            }
            rset.close();
            statment.close();
            get.close();
            return wins;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
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

    public int getIdArena() {
        return idArena;
    }

    public L2Party getParty1() {
        return party1;
    }

    public L2Party getParty2() {
        return party2;
    }


}
