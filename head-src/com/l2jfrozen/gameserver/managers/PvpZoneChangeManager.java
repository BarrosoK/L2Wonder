package com.l2jfrozen.gameserver.managers;


import com.l2jfrozen.gameserver.custom.PvpZonesData;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.entity.Announcements;
import com.l2jfrozen.gameserver.model.zone.type.L2MultiFunctionZone;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author MaVeN
**/

public class PvpZoneChangeManager implements Runnable {

    private static Random rand = new Random();
    public static int currentZone = rand.nextInt(PvpZonesData.getInstance().pvpZones.size());
    private static int tempZone = 0;
    private static PvpZoneChangeManager _instance;
    private boolean firstTime = true;

    public static PvpZoneChangeManager getInstance() {
        if (_instance == null) {
            _instance = new PvpZoneChangeManager();
        }

        return _instance;
    }

    private void teleportToNewZone() {
        ArrayList<L2PcInstance> list = L2MultiFunctionZone.getList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != null && list.get(i).isOnline() == 1) {
                list.get(i).teleToLocation(PvpZonesData.getInstance().pvpZones.get(currentZone).getTeleportLoc(), true);
            }
        }
    }


    @Override
    public void run() {

        if (!firstTime) {
            currentZone = tempZone;
            teleportToNewZone();
        }
        firstTime = false;
        Announcements.getInstance().announceToAll("PvP Zone Changed.\nNew Zone : " + PvpZonesData.getInstance().pvpZones.get(currentZone).getName() + "\nThe zone will change again in 60 minutes.");

        new java.util.Timer().schedule(new java.util.TimerTask() {
                       @Override
                       public void run() {

                           if (currentZone >= PvpZonesData.getInstance().pvpZones.size()-1) {
                               tempZone = 0;
                           } else {
                               tempZone = currentZone + 1;
                           }
                           Announcements.getInstance().announceToAll("Zone will change in 5 minutes.\nThe new zone will be : " + PvpZonesData.getInstance().pvpZones.get(tempZone).getName() + ".");

                           this.cancel();

                       }
                   },
                1000 * 60 * 55
        );


    }

    public static int getCurrentZone() {
        return currentZone;
    }

}
