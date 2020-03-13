/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jfrozen.gameserver.custom;


import com.l2jfrozen.gameserver.model.Location;

import java.util.ArrayList;

/**
 * @author MaVeN
**/

public class PvpZonesData {

    public ArrayList<PvpZoneInstance> pvpZones = new ArrayList<>();
    private static PvpZonesData _instance;


    public static PvpZonesData getInstance()
    {
        if (_instance == null)
        {
            _instance = new PvpZonesData();
        }

        return _instance;
    }

    public PvpZonesData() {

        createZones();
    }

    private void createZones() {


        /*                                FORBIDDEN GATEWAY                             */

        int pvpNumber = 0;
        String pvpName = "Forbidden Gateway";
        Location pvpTeleport = new Location(188508, 20568, -3722);
        ArrayList<Location> pvpSpawnLocs = new ArrayList<>();
        pvpSpawnLocs.add(new Location(189705, 21097, -3664));
        pvpSpawnLocs.add(new Location(191450, 21814, -3664));
        pvpSpawnLocs.add(new Location(192928, 22416, -3640));
        pvpSpawnLocs.add(new Location(192586, 20310, -3695));
        pvpSpawnLocs.add(new Location(190963, 19766, -3752));
        pvpSpawnLocs.add(new Location(189270, 22841, -3744));
        pvpSpawnLocs.add(new Location(191147, 23844, -3664));
        pvpSpawnLocs.add(new Location(188342, 22924, -3688));
        pvpSpawnLocs.add(new Location(189697, 18840, -3720));

        pvpZones.add(new PvpZoneInstance(pvpNumber, pvpName, pvpTeleport, pvpSpawnLocs));

        pvpNumber = 0;
        pvpName = "";
        pvpTeleport = null;
        pvpSpawnLocs = null;


        /*                            PRIMEVAL ISLAND                             */
        pvpNumber = 1;
        pvpName = "Primeval Island";
        pvpTeleport = new Location(10537, -24410, -3648);
        pvpSpawnLocs = new ArrayList<>();
        pvpSpawnLocs.add(new Location(11487, -24457, -3640));
        pvpSpawnLocs.add(new Location(10687, -24096, -3648));
        pvpSpawnLocs.add(new Location(10854, -23349, -3656));
        pvpSpawnLocs.add(new Location(9766, -23591, -3696));
        pvpSpawnLocs.add(new Location(8034, -23588, -3704));
        pvpSpawnLocs.add(new Location(6924, -23498, -3696));
        pvpSpawnLocs.add(new Location(6973, -22083, -3336));
        pvpSpawnLocs.add(new Location(8563, -20857, -3440));
        pvpSpawnLocs.add(new Location(9729, -22181, -3696));
        pvpSpawnLocs.add(new Location(10687, -24096, -3648));


        pvpZones.add(new PvpZoneInstance(pvpNumber, pvpName, pvpTeleport, pvpSpawnLocs));

        pvpNumber = 0;
        pvpName = "";
        pvpTeleport = null;
        pvpSpawnLocs = null;


        /*                            TOI 11th FLOOR                            */
        pvpNumber = 2;
        pvpName = "Tower Of Insolence";
        pvpTeleport = new Location(115715, 17120, 6832);
        pvpSpawnLocs = new ArrayList<>();
        pvpSpawnLocs.add(new Location(113640, 15062, 6971));
        pvpSpawnLocs.add(new Location(114639, 16072, 6971));
        pvpSpawnLocs.add(new Location(115418, 15295, 6992));
        pvpSpawnLocs.add(new Location(113822, 16880, 6992));
        pvpSpawnLocs.add(new Location(115314, 16745, 6992));
        pvpSpawnLocs.add(new Location(116196, 16044, 6971));
        pvpSpawnLocs.add(new Location(114582, 17651, 6971));
        pvpSpawnLocs.add(new Location(114674, 14455, 6971));
        pvpSpawnLocs.add(new Location(113093, 16029, 6971));

        pvpZones.add(new PvpZoneInstance(pvpNumber, pvpName, pvpTeleport, pvpSpawnLocs));

        pvpNumber = 0;
        pvpName = "";
        pvpTeleport = null;
        pvpSpawnLocs = null;



    }

}