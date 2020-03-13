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

import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.powerpak.PowerPakConfig;

import java.util.HashMap;

/**
 * @author Elfocrash
 *
 */
public class VoteNetwork extends VoteBase
{

    private HashMap<String, Long> rewardedIps = new HashMap<>();
    private HashMap<String, Long> rewardedAccounts = new HashMap<>();

   @Override
   public void reward(L2PcInstance player)
   {
       player.addItem("Reward", 3677, 10, player, true);
       player.addItem("Reward", 4443, 1, player, true);
   }

   @Override
   public String getApiEndpoint(L2PcInstance player)
   {
       return String.format("https://l2network.eu/index.php?a=in&u=%s&ipc=%s", PowerPakConfig.VOTE_NETWORK_NAME, getPlayerIp(player));
   }

   @Override
   public void setVoted(L2PcInstance player)
   {
       player.setLastNetVote(System.currentTimeMillis());
   }

    @Override
    public void setRewarded(L2PcInstance player) {
        VoteRewardData.getInstance().getRewardedIpsNet().put(getPlayerIp(player), System.currentTimeMillis());
        VoteRewardData.getInstance().getRewardedAccountsNet().put(player.getAccountName(), System.currentTimeMillis());
    }

    @Override
    public boolean isRewarded(L2PcInstance player) {
        if(VoteRewardData.getInstance().getRewardedIpsNet().containsKey(getPlayerIp(player))){
            if(VoteRewardData.getInstance().getRewardedIpsNet().get(getPlayerIp(player))+ 43200000 > System.currentTimeMillis()){
                return true;
            }
        }

        if(VoteRewardData.getInstance().getRewardedAccountsNet().containsKey(player.getAccountName())){
            if(VoteRewardData.getInstance().getRewardedAccountsNet().get(player.getAccountName())+ 43200000 > System.currentTimeMillis()){
                return true;
            }
        }

        return false;
    }

}