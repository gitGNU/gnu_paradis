/**
 *  Paradis — Ever growing network for parallel and distributed computing.
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
package se.kth.maandree.paradis.pacman;


/**
 * Package verion
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Version implements Comparable<Version>
{
    /**
     * Constructor
     * 
     * @param  version  The version
     */
    public Version(final String version)
    {
        final String ver = version.replace(";", ":");
        final int te = ver.contains("=")
                       ? Integer.parseInt(ver.substring(0, ver.indexOf(":")))
                       : 0;
        
        String t = ver;
        if (t.contains(":"))
            t = t.substring(t.indexOf(":") + 1);
        
        int tc = t.contains("-rc")
                 ? (t.indexOf("-rc") + 3)
                 : 0;
        
        if (tc > 0)
            tc = Integer.parseInt(t.substring(tc, t.indexOf("-", tc) < 0
                                              ? t.length()
                                              : t.indexOf("-", tc)
                                              ));
        
        String _tv = t.contains("-") ? t.substring(0, t.indexOf("-")) : t;
        
        t = t.substring(_tv.length());  if (t.startsWith("-"))  t = t.substring(1);
        
        final int tr = t.contains("-") ? Integer.parseInt(t.substring(t.indexOf("-") + 1)) : 0;
        
        final String[] _tvs = _tv.split(".");
        final int[] tvs = this.versions = new int[_tvs.length];
        
        final StringBuilder v = new StringBuilder();
        v.append((this.epoch = te) + ":");
        for (int i = 0, n = tvs.length; i < n; i++)
        {
            if (i > 0)
                v.append(".");
            v.append(Integer.toString(tvs[i] = Integer.parseInt(_tvs[i])));
        }
        v.append("-rc" + (this.candidate = tc));
        v.append("-" + (this.release = tr));
        this.version = v.toString();
    }
    
    
    
    /**
     * The version
     */
    private final String version;
    
    /**
     * Version epoch
     */
    private final int epoch;
    
    /**
     * Standard version parts
     */
    private final int[] versions;
    
    /**
     * Release candidate
     */
    private final int candidate;
    
    /**
     * Version release
     */
    private final int release;
    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other)
    {
        if ((other == null) || (other instanceof Version == false))
            return false;
        if (other == this)
            return true;
        return this.compareTo((Version)other) == 0;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        return this.version.hashCode();
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final Version other)
    {
        if (this.epoch != other.epoch)
            return this.epoch - other.epoch;
        
        int tl, ol, t, o;
        final int n = (tl = this.versions.length) > (ol = other.versions.length)
                      ? this.versions.length
                      : other.versions.length;
        for (int i = 0; i < n; i++)
            if ((t = i >= tl ? 0 : this .versions[i]) != (o = i >= ol ? 0 : other.versions[i]))
                return t - o;
        
        if (this.candidate != other.candidate)
            return this.candidate - other.candidate;
        
        return this.release - other.release;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return this.version;
    }
    
}

