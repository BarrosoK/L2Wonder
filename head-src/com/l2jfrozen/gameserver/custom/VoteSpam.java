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


import com.l2jfrozen.gameserver.model.L2World;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.network.clientpackets.Say2;
import com.l2jfrozen.gameserver.network.serverpackets.CreatureSay;

/**
 * @author MaVeN
 **/

public class VoteSpam implements Runnable {

    private static VoteSpam _instance;

    public VoteSpam() {
    }

    public static VoteSpam getInstance() {
        if (_instance == null) {
            _instance = new VoteSpam();
        }

        return _instance;
    }


    @Override
    public void run() {

        VoteHopzone voteHop = new VoteHopzone();
        VoteTopzone voteTop = new VoteTopzone();
        VoteNetwork voteNet = new VoteNetwork();

        for (L2PcInstance activeChar : L2World.getInstance().getAllPlayers()) {
            try {
                if (activeChar != null && activeChar.isOnline() == 1 && !activeChar.isInStoreMode() && !activeChar.isGM()) {
                    if ((activeChar.eligibleToVoteTop() && !voteTop.isRewarded(activeChar))
                            || (activeChar.eligibleToVoteNet() && !voteNet.isRewarded(activeChar))
                            || (activeChar.eligibleToVoteHop() && !voteHop.isRewarded(activeChar))) {
                        activeChar.sendPacket(new CreatureSay(123456, Say2.TELL, "Vote Manager", "Don't forget to vote every 12 hours."));
                    }
                }
            } catch (NullPointerException ignored) {
            }
        }

        voteHop = null;
        voteNet = null;
        voteTop = null;

    }
}