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
package org.nongnu.paradis.test;

import org.tukaani.xz.*;

import java.util.*;
import java.io.*;


/**
 * Test class for XZ compression and decompression
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@member.fsf.org">maandree@member.fsf.org</a>
 */
public class XZ
{
    /**
     * Non-constructor
     */
    private XZ()
    {
        assert false : "You may not create instances of this class [XZ].";
    }
    
    
    
    /**
     * This is the main entry point of this test
     * 
     * @param  args  Startup arguments, unused
     */
    public static void main(final String... args)
    {
	try (final Scanner sc = new Scanner(System.in))
	{
	    System.out.print("Compression input file:    ");
	    final String cif = sc.nextLine();
	    System.out.print("Compression output file:   ");
	    final String cof = sc.nextLine();
	    
	    final LZMA2Options lzma2 = new LZMA2Options(LZMA2Options.PRESET_MAX);
	    try (final XZOutputStream xos = new XZOutputStream(new FileOutputStream(cof), lzma2); final FileInputStream fis = new FileInputStream(cif))
	    {
		final byte[] bs = new byte[4096];
		while (fis.available() != 0)
		    xos.write(bs, 0, fis.read(bs, 0, bs.length));
	    }
	    
	    System.out.print("Decompression input file:  ");
	    final String dif = sc.nextLine();
	    System.out.print("Decompression output file: ");
	    final String dof = sc.nextLine();
	    
	    try (final XZInputStream xis = new XZInputStream(new FileInputStream(dif)); final FileOutputStream fos = new FileOutputStream(dof))
	    {
		final byte[] bs = new byte[4096];
		int av;
		while ((av = xis.read(bs, 0, bs.length)) > 0)
		    fos.write(bs, 0, av);
	    }
	}
	catch (final Throwable err)
	{   err.printStackTrace(System.err);
	}
    }
    
}

