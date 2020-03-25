package com.l2jfrozen.gameserver.gui.playerTable;



import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.gui.ServerGui;
import com.l2jfrozen.gameserver.model.L2World;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;

import javax.swing.table.AbstractTableModel;

public class PlayerTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    private static final String[] columnNames = {"Id", "Name", "Level"};

    private L2PcInstance[] players = new L2PcInstance[]{};

    public PlayerTableModel() {
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return players.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        switch (col) {
            case 0:
                return players[row].getObjectId();
            case 1:
                return players[row].getName();
            case 2:
                return players[row].getLevel();
        }
        return "";
    }

    public synchronized boolean updateData() {
        L2PcInstance[] players = new L2PcInstance[L2World.getInstance().getAllPlayersCount()];
        L2World.getInstance().getAllPlayers().toArray(players);
        int playerCount = 0;
        int shopCount = 0;
        for (L2PcInstance player : players) {
            if (player != null && player.isOnline() == 1) {
                if (player.getClient() == null || player.getClient().isDetached()) {
                    shopCount++;
                } else {
                    playerCount++;
                }
            }
        }

        ServerGui.getMainFrame().setTitle(
                "L2 Server [" + "WONDERLUST" + "] | Players online: " + playerCount + " | Offline shops: " + shopCount + " | Total: " +
                        (playerCount + shopCount));
        if (players.length == players.length && !(players.length > 0 && players[0] == players[0])) {
            return false;
        }

        this.players = players;
        return true;
    }
}
