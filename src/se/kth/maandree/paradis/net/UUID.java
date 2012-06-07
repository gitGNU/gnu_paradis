/**
 *  Paradis — Ever growing network for parallell and distributed computing.
 *  Copyright © 2012  Mattias Andrée
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
package se.kth.maandree.paradis.net;
import se.kth.maandree.paradis.io.*;

import java.util.Random;
import java.io.IOException;


/**
 * UUID class, that does not use just randoms as Java's does
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class UUID implements Comparable<UUID>
{
    /**
     * Constructor
     */
    public UUID()
    {
	final long _high = System.nanoTime();
	final long _lowhigh = (long)(System.getProperty("user.name").hashCode()) << 32L;
	final long _lowmid = (long)(counter++) << 24L;
	final long _lowlow = (long)(random.nextInt() >>> 8);
	
	this.high = _high;
	this.low = _lowhigh | _lowmid | _lowlow;
    }
    
    /**
     * Constructor
     * 
     * @param  high  The high 64 bits
     * @param  low   The low 64 bits
     */
    protected UUID(final long high, final long low)
    {
	this.high = high;
	this.low = low;
	counter++;
    }
    
    
    
    /**
     * The high 64 bits
     */
    protected final long high;
    
    /**
     * The low 64 bits
     */
    protected final long low;
    
    /**
     * Instance counter
     */
    private static volatile byte counter = 0;
    
    /**
     * Random generator
     */
    private static final Random random = new Random();
    
    
    
    /**
     * Protocol for transfering {@link UUID}s
     * 
     * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
     */
    public static class UUIDTransferProtocol implements TransferProtocol<UUID>
    {
	//Has default constructor
	
	
	
	/**
	 * {@inheritDoc}
	 */
	public UUID read(final TransferInputStream stream) throws IOException
	{   return new UUID(stream.readLong(), stream.readLong());
	}
    
    
	/**
	 * {@inheritDoc}
	 */
	public void write(final UUID data, final TransferOutputStream stream) throws IOException
	{   stream.writeLong(data.high);
	    stream.writeLong(data.low);
	}
    
    }
    
    
    
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(final Object other)
    {
	if ((other == null) || (other instanceof UUID == false))
	    return false;
	
	if (other == this)
	    return true;
	
	return (this.high == ((UUID)other).high) && (this.low == ((UUID)other).low);
    }
    
    
    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {
	long par = this.high ^ this.low;
	par ^= par >> 32L;
	return (int)par;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public int compareTo(final UUID other)
    {
	if (this.high == other.high)
	    return this.low < other.low ? -1 : this.low > other.low ? 1 : 0;
	
	return this.high < other.high ? -1 : this.high > other.high ? 1 : 0;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public String toString()
    {
	String rc0 = "0000000" + Long.toString(this.high >> 32L, 16);
	String rc1 = "0000000" + Long.toString(this.high, 16);
	String rc2 = "0000000" + Long.toString(this.low >> 32L, 16);
	String rc3 = "0000000" + Long.toString(this.low, 16);
	rc0 = rc0.substring(rc0.length() - 8);
	rc1 = rc1.substring(rc1.length() - 8);
	rc2 = rc2.substring(rc2.length() - 8);
	rc3 = rc3.substring(rc3.length() - 8);
	
	return rc0 + "-" + rc1.substring(0, 4) + "-" + rc1.substring(4) + "-" + rc2.substring(0, 4) + "-" + rc2.substring(4) + rc3;
    }
    
}

