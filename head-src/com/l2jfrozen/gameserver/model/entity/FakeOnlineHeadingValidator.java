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
package com.l2jfrozen.gameserver.model.entity;


import com.l2jfrozen.gameserver.model.L2Character;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.network.serverpackets.ValidateLocation;
import com.l2jfrozen.util.random.Rnd;

/**
 * @author MaVeN
 **/

public class FakeOnlineHeadingValidator implements Runnable {

    private static FakeOnlineHeadingValidator _instance;


    public static FakeOnlineHeadingValidator getInstance() {
        if (_instance == null) {
            _instance = new FakeOnlineHeadingValidator();
        }

        return _instance;
    }


    @Override
    public void run() {

        for (L2PcInstance p : FakeOnline.fakeplayers) {

            int n = Rnd.nextInt(100);
            if (n <= 50 && p.getHeading() + 10000 < 61000) {
                p.setHeading(p.getHeading() + 10000);
            } else if (p.getHeading() - 10000 > 0) {
                p.setHeading(p.getHeading() - 10000);
            }

            for (L2PcInstance player : p.getKnownList().getKnownPlayersInRadius(2000)) {

                if (!FakeOnline.fakeplayers.contains(player)) {
                    player.sendPacket(new ValidateLocation(p));
                    p.sendPacket(new ValidateLocation(player));
                }

            }

        }

    }

}
