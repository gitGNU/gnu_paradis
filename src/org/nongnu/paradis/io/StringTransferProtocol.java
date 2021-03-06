/**
 *  Paradis — Ever growing network for parallel and distributed computing.
 *  Copyright © 2012, 2013  Mattias Andrée (maandree@member.fsf.org)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.nongnu.paradis.io;

import java.io.*;


/**
 * Protocol for transfering {@link String}s
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@member.fsf.org">maandree@member.fsf.org</a>
 */
class StringTransferProtocol implements TransferProtocol<String>
{
    //Has default constructor
    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String read(final TransferInputStream stream) throws IOException
    {
        final int len = stream.readLen();
        final char[] chars = new char[len << 1];
        int ptr = 0;
        
        int wc;
        for (int i = 0; i < len; i++)
            if ((wc = stream.readWChar()) < 0x10000)
                chars[ptr++] = (char)wc;
            else
            {
                wc -= 0x10000;
                chars[ptr++] = (char)((wc >>> 10) | 0xD800);
                chars[ptr++] = (char)((wc & 1023) | 0xDC00);
            }
        
        return new String(chars, 0, ptr);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final String data, final TransferOutputStream stream) throws IOException
    {
        final int[] chars = new int[data.length()];
        int ptr = 0;
        
        char c;
        for (int i = 0, n = data.length(); i < n; i++)
            if (((c = data.charAt(i)) & 0xDC00) == 0xD800)
            {
                int wc = (c & 1023) << 10;
                if (i + 1 < n)
                    wc |= ((data.charAt(++i)) & 1023);
                wc += 0x10000;
                chars[ptr++] = wc;
            }
            else
                chars[ptr++] = c;
        
        stream.writeLen(ptr);
        for (int i = 0; i < ptr; i++)
            stream.writeWChar(chars[i]);
    }
    
}

