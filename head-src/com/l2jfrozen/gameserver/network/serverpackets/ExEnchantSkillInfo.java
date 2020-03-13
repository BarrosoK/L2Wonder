/*
 * L2jFrozen Project - www.l2jfrozen.com 
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.l2jfrozen.gameserver.network.serverpackets;

import com.l2jfrozen.gameserver.model.actor.instance.L2DonateShopInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2FolkInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.network.SystemMessageId;
import javolution.util.FastList;

public class ExEnchantSkillInfo extends L2GameServerPacket {
    private static final String _S__FE_18_EXENCHANTSKILLINFO = "[S] FE:18 ExEnchantSkillInfo";
    private final FastList<Req> _reqs;
    private final int _id;
    private final int _level;
    private final int _spCost;
    private final int _xpCost;
    private final int _rate;

    class Req {
        public int id;
        public int count;
        public int type;
        public int unk;

        Req(final int pType, final int pId, final int pCount, final int pUnk) {
            id = pId;
            type = pType;
            count = pCount;
            unk = pUnk;
        }
    }

    public ExEnchantSkillInfo(final int id, final int level, final int spCost, final int xpCost, final int rate) {
        _reqs = new FastList<>();
        _id = id;
        _level = level;
        _spCost = spCost;
        _xpCost = xpCost;
        _rate = rate;
    }

    public void addRequirement(final int type, final int id, final int count, final int unk) {
        _reqs.add(new Req(type, id, count, unk));
    }

    /*
     * (non-Javadoc)
     * @see com.l2jfrozen.gameserver.serverpackets.ServerBasePacket#writeImpl()
     */
    @Override
    protected void writeImpl() {


        final L2PcInstance player = getClient().getActiveChar();
        final L2FolkInstance trainer = player.getLastFolkNPC();
        boolean isInDonationShop = false;

        if (trainer instanceof L2DonateShopInstance) {
            isInDonationShop = true;
        }

        writeC(0xfe);
        writeH(0x18);

        writeD(_id);
        writeD(_level);
        writeD(_spCost);
        writeQ(_xpCost);
        if (isInDonationShop)
            writeD(100);
        else
            writeD(_rate);

        writeD(_reqs.size());

        for (final Req temp : _reqs) {
            writeD(temp.type);
            if (isInDonationShop) {
                writeD(4037); // put there ur donation coin id and ant to check
                if (_level > 140) {
                    switch (_level) {

                        case 150:
                            writeD(2);
                            break;
                        case 151:
                            writeD(5);
                            break;
                        case 152:
                            writeD(8);
                            break;

                        case 153:

                            writeD(15);
                            break;
                        case 154:

                            return;
                        case 155:

                            return;


                        default:
                            writeD(1);
                            break;

                    }



                } else {
                    switch (_level) {
                        case 110:
                            writeD(2);
                            break;
                        case 111:

                            writeD(5);
                            break;
                        case 112:
                            writeD(8);
                            break;

                        case 113:
                            writeD(15);
                            break;
                        case 114:

                            return;
                        case 115:

                            return;

                        default:

                            writeD(1);
                            break;
                    }
                }
            } else {
                writeD(temp.id); // put there ur donation coin id and ant to check
                writeD(temp.count);
            }
            writeD(temp.unk);
        }

    }

    /*
     * (non-Javadoc)
     * @see com.l2jfrozen.gameserver.BasePacket#getType()
     */
    @Override
    public String getType() {
        return _S__FE_18_EXENCHANTSKILLINFO;
    }

}
