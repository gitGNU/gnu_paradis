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
import se.kth.maandree.paradis.local.Properties; //Explicit


/**
 * Package name with verion or version bounds
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class VersionedPackage implements Comparable<VersionedPackage>
{
    /**
     * System file separator
     */
    private static final String FILE_SEPARATOR = Properties.getFileSeparator();
    
    
    
    /**
     * Constructor
     * 
     * @param  pkg  Package with version or version bounds, may be a file name
     */
    public VersionedPackage(final String pkg)
    {
        String _pkg = pkg;
        if (_pkg.contains(FILE_SEPARATOR))
            _pkg = _pkg.substring(_pkg.lastIndexOf(FILE_SEPARATOR) + FILE_SEPARATOR.length());
        if (_pkg.endsWith(".pkg.xz"))
            _pkg = _pkg.substring(0, _pkg.length() - ".pkg.xz".length());
        else if (_pkg.endsWith(".pkg"))
            _pkg = _pkg.substring(0, _pkg.length() - ".pkg".length());
        
        // name>low  name>=low  name=both  name<=high  name<high  name
        // low<=name<=high  low<=name<high  low<name<=high  low<name<high
        // high>=name>=low  high>=name>low  high>name>=low  high>name>low
        
        final String[] parts = _pkg.replace("<", "=").replace(">", "=").replace("==", "=").split("=");
        if (parts.length == 3)
        {
            this.name = parts[1];
            final String _versionA, _versionB;
            final Version versionA = new Version(_versionA = parts[0].replace(";", ":"));
            final Version versionB = new Version(_versionB = parts[2].replace(";", ":"));
            final String opA = _pkg.substring(_versionA.length(), _pkg.indexOf(this.name, _versionA.length()) - _versionA.length());
            String opB = _pkg.substring(_versionA.length() + opA.length() + this.name.length());
            opB = opB.substring(0, opB.length() - _versionB.length());
            if (opA.contains(">") && opB.contains(">"))
            {
                this.highClosed = opA.contains("=");
                this.lowClosed = opB.contains("=");
                this.high = versionA;
                this.low = versionB;
            }
            else
            {
                this.lowClosed = opA.contains("=");
                this.highClosed = opB.contains("=");
                this.low = versionA;
                this.high = versionB;
            }
        }
        else if (parts.length == 2)
        {
            this.name = parts[0];
            final Version version = new Version(parts[1].replace(";", ":"));
            final String op = _pkg.substring(this.name.length(), _pkg.length() - this.name.length() - parts[1].length());
            if (op.equals("<"))
            {   this.lowClosed = this.highClosed = false;
                this.low = null;
                this.high = version;
            }
            else if (op.equals("<="))
            {   this.lowClosed = (this.highClosed = true) == false;
                this.low = null;
                this.high = version;
            }
            else if (op.equals(">="))
            {   this.lowClosed = (this.highClosed = false) == false;
                this.high = null;
                this.low = version;
            }
            else if (op.equals(">"))
            {   this.lowClosed = this.highClosed = false;
                this.high = null;
                this.low = version;
            }
            else
            {   this.lowClosed = this.highClosed = true;
                this.low = this.high = version;
            }
        }
        else
        {
            this.name = _pkg;
            this.low = this.high = null;
            this.lowClosed = this.highClosed = false;
        }
    }
    
    
    
    /**
     * Package name
     */
    public final String name;
    
    /**
     * Package version lower bound, {@code null} if unbounded
     */
    public final Version low;
    
    /**
     * Package version upper bound, {@code null} if unbounded
     */
    public final Version high;
    
    /**
     * Whether the lower version bound is closed
     */
    public final boolean lowClosed;
    
    /**
     * Whether the ypper version bound is closed
     */
    public final boolean highClosed;
    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other)
    {
        if ((other == null) || (other instanceof VersionedPackage == false))
            return false;
        if (other == this)
            return true;
        return this.name.equals(((VersionedPackage)other).name);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        return this.name.hashCode();
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final VersionedPackage other)
    {
        int rc = this.name.compareTo(other.name);
        if (rc == 0)
            rc = this.low.compareTo(other.low);
        return rc;
    }
    
    
    /**
     * Test whether one instance satisficies another
     * 
     * @param   other  The other isntance
     * @return         The result of the test
     */
    public boolean intersects(final VersionedPackage other)
    {
        if (other == null)  return false;
        if (other == this)  return true;
        if (this.name.equals(other.name) == false)
            return false;
        
        if (((this.low == null) && (this.high == null)) || ((other.low == null) && (other.high == null)))
            return true;
        
        if (((this.low == null) ^ (this.high == null)) && ((other.low == null) ^ (other.high == null)) && ((this.low == null) == (other.low == null)))
            return true;
        
        if ((this.low != null) && (this.high == null) && (other.high != null))
        {   int comp = this.low.compareTo(other.high);
            return comp == 0 ? (this.lowClosed && other.highClosed) : (comp < 0);
        }
        
        if ((this.low == null) && (this.high != null) && (other.low != null))
        {   int comp = other.low.compareTo(this.high);
            return comp == 0 ? (other.lowClosed && this.highClosed) : (comp < 0);
        }
        
        if ((this.low != null) && (this.high != null) && (other.low == null) && (other.high != null))
        {   int comp = this.low.compareTo(other.high);
            return comp == 0 ? (this.lowClosed && other.highClosed) : (comp < 0);
        }
        
        if ((this.low == null) && (this.high != null) && (other.low != null) && (other.high != null))
        {   int comp = other.low.compareTo(this.high);
            return comp == 0 ? (other.lowClosed && this.highClosed) : (comp < 0);
        }
        
        final boolean olc = other. lowClosed;
        final boolean tlc = this . lowClosed;
        final boolean thc = this .highClosed;
        final boolean ohc = other.highClosed;
        
        int oltl = other.low .compareTo(this .low );  if (oltl == 0)  oltl = (olc && tlc) ? -1 : 0;
        int olth = other.low .compareTo(this .high);  if (olth == 0)  olth = (olc && thc) ? -1 : 0;
        int tloh = this .low .compareTo(other.high);  if (tloh == 0)  tloh = (tlc && ohc) ? -1 : 0;
        int thoh = this .high.compareTo(other.high);  if (thoh == 0)  thoh = (thc && ohc) ? -1 : 0;
        int tlol = this .low .compareTo(other.low );  if (tlol == 0)  tlol = (tlc && olc) ? -1 : 0;
        int ohth = other.high.compareTo(this .high);  if (ohth == 0)  ohth = (ohc && thc) ? -1 : 0;
        
        if ((oltl < 0) && (tloh < 0))  return true;
        if ((olth < 0) && (thoh < 0))  return true;
        if ((tlol < 0) && (olth < 0))  return true;
        if ((tloh < 0) && (ohth < 0))  return true;
        return false;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        if ((this.low == null) && (this.high == null))
            return this.name;
        if (this.low == null)   return this.name + (this.highClosed ? "<=" : "<") + this.high;
        if (this.high == null)   return this.name + (this.lowClosed ? ">=" : ">") + this.low;
        if (this.low.equals(this.high))
            return this.name + "=" + this.low;
        return this.low + (this.lowClosed ? "<=" : "<") + this.name + (this.highClosed ? "<=" : "<") + this.high;
    }
    
}

