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
 
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
 
import com.l2jfrozen.gameserver.util.Util;
 
public class DonateAudit
{
    static
    {
        new File("log/Donates").mkdirs();
    }
   
    private static final Logger LOGGER = Logger.getLogger(DonateAudit.class.getName());
   
    public static void auditGMAction(String activeChar, String action, String target, String params)
    {
        final File file = new File("log/Donates/" + activeChar + ".txt");
            if (!file.exists())
            try
            {
                file.createNewFile();
            }
            catch (IOException e)
            {
            }
       
        try (FileWriter save = new FileWriter(file, true))
        {
            final String format = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date date = new Date();
            save.write((sdf.format(date)) + " > " + activeChar + " > " + action + " > " + target + " > " + params + "\r\n");
        }
        catch (IOException e)
        {
            LOGGER.log(Level.SEVERE, "GMAudit for GM " + activeChar + " could not be saved: ", e);
        }
    }
   
    public static void auditGMAction(String activeChar, String action, String target)
    {
        auditGMAction(activeChar, action, target, "");
    }
}