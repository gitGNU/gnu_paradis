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
package org.nongnu.paradis.file.ini;


/**
 * This class is used to escape and unescape text
 *
 * @author  Mattias Andrée, <a href="mailto:maandree@member.fsf.org">maandree@member.fsf.org</a>
 */
public class Escaper
{
    /**
     * Non-constructor
     */
    private Escaper()
    {
        assert false : "You may not create instances of this class [Constants].";
    }
    
    
    
    /**
     * Escapes texts
     *
     * @param   text     The texts to escape
     * @param   newline  Newline escapement used
     * @return           The texts escaped
     */
    public static String[] escape(final String[] text, final String newline)
    {
        String[] rc = text;
        for (int i = 0; i < rc.length; i++)
            rc[i] = escape(rc[i], newline);
        return rc;
    }
    
    /**
     * Escapes texts
     *
     * @param   text  The texts to escape
     * @return        The texts escaped
     */
    public static String[] escape(final String[] text)
    {
        String[] rc = text;
        for (int i = 0; i < rc.length; i++)
            rc[i] = escape(rc[i]);
        return rc;
    }
    
    /**
     * Escapes a text
     *
     * @param   text  The text to escape
     * @return        The text escaped
     */
    public static String escape(final String text)
    {
        return escape(text, null);
    }
    
    /**
     * Escapes a text
     *
     * @param   text     The text to escape
     * @param   newline  Newline escapement used
     * @return           The text escaped
     */
    public static String escape(final String text, final String newline)
    {
        String rc = text;
        
        if ((rc != null) && (rc != ""))
        {
            rc = rc.replace("\\", "\\\\"); //this one most be first
            if ((newline != null) && (newline != ""))
                rc = rc.replace(newline, "\\ ");
            rc = rc.replace("\0", "\\0");
            rc = rc.replace("\t", "\\t");
            rc = rc.replace("\r", "\\r");
            rc = rc.replace("\n", "\\n");
            
            final String HEXADEC = Constants.HEXADEC;
            
            int bColon     = ':';
            int bEqual     = '=';
            int bSemicolon = ';';
            int bSquare    = '#';
            int bHiveStart = '[';
            int bComma     = ',';
            
            String xColon     = "\\x00" + HEXADEC.charAt((bColon     & 0xf0) >> 4) + HEXADEC.charAt(bColon     & 0x0f);
            String xEqual     = "\\x00" + HEXADEC.charAt((bEqual     & 0xf0) >> 4) + HEXADEC.charAt(bEqual     & 0x0f);
            String xSemicolon = "\\x00" + HEXADEC.charAt((bSemicolon & 0xf0) >> 4) + HEXADEC.charAt(bSemicolon & 0x0f);
            String xSquare    = "\\x00" + HEXADEC.charAt((bSquare    & 0xf0) >> 4) + HEXADEC.charAt(bSquare    & 0x0f);
            String xHiveStart = "\\x00" + HEXADEC.charAt((bHiveStart & 0xf0) >> 4) + HEXADEC.charAt(bHiveStart & 0x0f);
            String xComma     = "\\x00" + HEXADEC.charAt((bComma     & 0xf0) >> 4) + HEXADEC.charAt(bComma     & 0x0f);
            
            rc = rc.replace(":", xColon    );
            rc = rc.replace("=", xEqual    );
            rc = rc.replace(";", xSemicolon);
            rc = rc.replace("#", xSquare   );
            rc = rc.replace("[", xHiveStart);
            rc = rc.replace(",", xComma    );
            
            for (int i = 0; i < ' '; i++)
            {
                String x = "\\x00";
                x += HEXADEC.charAt((i & 0xf0) >> (4 * 1));
                x += HEXADEC.charAt((i & 0x0f) >> (4 * 0));

                rc = rc.replace("" + (char)i, x);
            }
            for (int i = 255; i < (1 >> 16); ++i)
            {
                String x = "\\x";
                x += HEXADEC.charAt((i & 0xf000) >> (4 * 3));
                x += HEXADEC.charAt((i & 0x0f00) >> (4 * 2));
                x += HEXADEC.charAt((i & 0x00f0) >> (4 * 1));
                x += HEXADEC.charAt((i & 0x000f) >> (4 * 0));
                
                rc = rc.replace("" + (char)i, x);
            }
        }
        
        return rc;
    }
    
    
    /**
     * Unescapes texts
     *
     * @param   text  The texts to unescape
     * @return        The texts unescaped
     */
    public static String[] unescape(final String[] text)
    {
        String[] rc = text;
        for (int i = 0; i < rc.length; i++)
            rc[i] = unescape(rc[i]);
        return rc;
    }
    
    /**
     * Unescapes a text
     *
     * @param   text  The text to unescape
     * @return        The text unescaped
     */
    public static String unescape(final String text)
    {
        String rc = text;
        
        if ((rc != null) && (rc.length() > 0))
        {
            String buf = "";
            int col = 0;
            while (col < rc.length())
            {
                if (rc.charAt(col) == '\\')
                {
                    col++;
                    
                    if (col < rc.length())
                    {
                        if (rc.charAt(col) == '\\')
                            buf += "\\";
                        else if ((col + "r\\n".length() <= rc.length()) && (rc.substring(col, col + "r\\n".length()).equals("r\\n")))
                        {
                            buf += Constants.LINE_ENDING;
                            col += 2;
                        }
                        else
                        {
                            switch (rc.charAt(col))
                            {
                                case '0': buf += "\0"; break;
                                case 't': buf += "\t"; break;
                                case 'a': buf += "\u0007"; break;
                                case 'b': buf += "\b"; break;
                                case 'f': buf += "\u000C"; break;
                                case 'v': buf += "\u000B"; break;
                                case 'e': buf += "\u001B"; break;
                                case 'n':
                                case 'r':
                                case ' ': buf += Constants.LINE_ENDING; break;
                                case ';': buf += ";"; break;
                                case '#': buf += "#"; break;
                                case '=': buf += "="; break;
                                case ':': buf += ":"; break;
                                case '[': buf += "["; break;
                                case ',': buf += ","; break;
                            }
                            
                            if ((rc.charAt(col) == 'x') && (col + "x????".length() <= rc.length()))
                            {
                                int a = col + 1;
                                String tmp = rc.substring(a, a + "????".length()).toUpperCase();
                                String comp = tmp;
                                
                                comp = comp.replace("0", "").replace("1", "").replace("2", "").replace("3", "")
                                           .replace("4", "").replace("5", "").replace("6", "").replace("7", "")
                                           .replace("8", "").replace("9", "").replace("A", "").replace("B", "")
                                           .replace("C", "").replace("D", "").replace("E", "").replace("F", "");
                                
                                if (comp.length() == 0)
                                {
                                    int val = 0;
                                    
                                    for (int i = 0, n = tmp.length(); i < n; i++)
                                    {
                                        int v = Constants.HEXADEC.indexOf(tmp.charAt(i));
                                        val *= Constants.HEXADEC.length();
                                        val += v;
                                    }
                                    
                                    buf += (char)val;
                                }
                                
                                col += "????".length();
                            }
                        }
                    }
                }
                else
                    buf += rc.charAt(col);
                col++;
            }
            rc = buf;
        }
        
        return rc;
    }
    
}

