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
package se.kth.maandree.paradis.file.ini;

import se.kth.maandree.paradis.file.ini.INI.CommentNotation;
import se.kth.maandree.paradis.file.ini.INI.KeyEnding;


/**
 * This class is used to fix and clean the content in the INI-file
 *
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
class DataFixer
{
    /**
     * Escapement character
     */
    private final static char ESCAPE_CHAR = Constants.ESCAPE_CHAR;
    
    /**
     * The characater used to continue the previouse line
     */
    private final static char PREV_LINE_CONTINUER = Constants.PREV_LINE_CONTINUER;
    
    /**
     * Hive declaration start
     */
    private final static String HIVE_START = Constants.HIVE_START;

    /**
     * Hive declaration end
     */
    private final static String HIVE_END = Constants.HIVE_END;
    
    /**
     * Line ending
     */
    private final static String LINE_ENDING = Constants.LINE_ENDING;
    

    
    /**
     * Non-constructor
     */
    private DataFixer()
    {
        assert false : "You may not create instances of this class [DataFixer].";
    }
    
    
    
    /**
     * Removes all whitelines in the ini-file
     * 
     * @param  iniData  The container of the INI data
     */
    static void clean(final INIData iniData)
    {
        if (iniData.filetext.isEmpty())
        {
            String[] lines = iniData.filetext.split(LINE_ENDING);
            String text = "";
            
            for (int index = 0; index < lines.length; index++)
                if (lines[index].length() > 0)
                {
                    if (lines[index].startsWith(HIVE_START) && lines[index].endsWith(HIVE_END))
                        text += LINE_ENDING;
                    text += lines[index] + LINE_ENDING;
                }
            
            while (text.startsWith(LINE_ENDING))
                text = text.substring(LINE_ENDING.length(), text.length());
            
            while (text.endsWith(LINE_ENDING))
                text = text.substring(0, text.length() - LINE_ENDING.length());
            
            iniData.filetext = text;
        }
    }
    
    /**
     * Fixed the text so it is readable of the machine
     * 
     * @param  iniData  The container of the INI data
     */
    static void prefixText(final INIData iniData)
    {
        {
            String buf = "";
            String from = iniData.filetext.replace("\r\n", "\n")
                                          .replace("\r", "\n")
                                          .replace("\n", LINE_ENDING);
            
            iniData.signature = "";
            boolean signatureEnded = false;
            
            for (int i = 0, n = from.length(); i < n;)
                if (signatureEnded == false)
                {
                    int nextLineEnd = from.indexOf(LINE_ENDING, i + 1);
                    
                    if (    (from.substring(i, i + 1 + LINE_ENDING.length()).equals(LINE_ENDING + '[')) && 
                            (nextLineEnd >= 0) && 
                            (from.charAt(nextLineEnd - 1) == ']')
                            )
                    {
                        signatureEnded = true;
                        i += LINE_ENDING.length();
                    }
                    else if (   (i == 0) &&
                                (from.substring(i, i + 1).equals("[")) &&
                                (nextLineEnd >= 0) && 
                                (from.charAt(nextLineEnd - 1) == ']')
                            )
                        signatureEnded = true;
                    else
                        iniData.signature += from.charAt(i++);
                }
                else
                    if (from.charAt(i) == ESCAPE_CHAR)
                    {
                        buf += ESCAPE_CHAR;
                        i++;
                        int len = LINE_ENDING.length();
                        if ((i == n) || (i + len <= n) && from.substring(i, i + len).equals(LINE_ENDING))
                        {
                            i += len;
                            buf += "n";
                        }
                        else
                            buf += from.charAt(i++);
                    }
                    else
                    {
                        int len = LINE_ENDING.length() + 1;
                        if ((i + len <= n) && from.substring(i, i + len).equals(LINE_ENDING + PREV_LINE_CONTINUER))
                        {
                            i += len;
                        }
                        else
                            buf += from.charAt(i++);
                    }
            
            iniData.filetext = buf;
        }
        
        String[] lines = iniData.filetext.split(LINE_ENDING);
        for (int i = 0; i < lines.length; i++)
        {
            if (lines[i].startsWith(CommentNotation.DEFAULT.getValue()) || lines[i].startsWith(CommentNotation.SQUARE.getValue()))
            {
                lines[i] = iniData.commentNotation + lines[i].substring(CommentNotation.DEFAULT.getValue().length(), lines[i].length());
            }
            else if ((lines[i].length() > 0) && !(lines[i].startsWith(iniData.commentNotation)) && !(lines[i].startsWith(HIVE_START) && lines[i].endsWith(HIVE_END)))
            {
                final String KEY_ENDING_A = KeyEnding.DEFAULT         .getValue();
                final String KEY_ENDING_B = KeyEnding.DEFAULT_UNSPACED.getValue();
                final String KEY_ENDING_C = KeyEnding.  COLON         .getValue();
                final String KEY_ENDING_D = KeyEnding.  COLON_UNSPACED.getValue();
                
                int sep0 = lines[i].indexOf(KEY_ENDING_A);
                int sep1 = lines[i].indexOf(KEY_ENDING_B);
                int sep2 = lines[i].indexOf(KEY_ENDING_C);
                int sep3 = lines[i].indexOf(KEY_ENDING_D);
                
                while ((sep0 > 0) && (lines[i].charAt(sep0 - 1) == ESCAPE_CHAR))  sep0 = lines[i].indexOf(KEY_ENDING_A, sep0 + 1);
                while ((sep1 > 0) && (lines[i].charAt(sep1 - 1) == ESCAPE_CHAR))  sep1 = lines[i].indexOf(KEY_ENDING_B, sep1 + 1);
                while ((sep2 > 0) && (lines[i].charAt(sep2 - 1) == ESCAPE_CHAR))  sep2 = lines[i].indexOf(KEY_ENDING_C, sep2 + 1);
                while ((sep3 > 0) && (lines[i].charAt(sep3 - 1) == ESCAPE_CHAR))  sep3 = lines[i].indexOf(KEY_ENDING_D, sep3 + 1);
                
                if ((sep0 >= 0) && (((sep0 < sep1) || (sep1 < 0)) && ((sep0 < sep2) || (sep2 < 0)) && ((sep0 < sep3) || (sep3 < 0))))
                {   lines[i] = lines[i].substring(0, sep0) + iniData.keyEnding + lines[i].substring(sep0 + KEY_ENDING_A.length(), lines[i].length());
                }
                else if ((sep1 >= 0) && (((sep1 < sep0) || (sep0 < 0)) && ((sep1 < sep2) || (sep2 < 0)) && ((sep1 < sep3) || (sep3 < 0))))
                {   lines[i] = lines[i].substring(0, sep1) + iniData.keyEnding + lines[i].substring(sep1 + KEY_ENDING_B.length(), lines[i].length());
                }
                else if ((sep2 >= 0) && (((sep2 < sep1) || (sep1 < 0)) && ((sep2 < sep0) || (sep0 < 0)) && ((sep2 < sep3) || (sep3 < 0))))
                {   lines[i] = lines[i].substring(0, sep2) + iniData.keyEnding + lines[i].substring(sep2 + KEY_ENDING_C.length(), lines[i].length());
                }
                else
                {   lines[i] = lines[i].substring(0, sep3) + iniData.keyEnding + lines[i].substring(sep3 + KEY_ENDING_D.length(), lines[i].length());
                }
            }
        }
        
        {
            iniData.filetext = "";
            for (int i = 0, n = lines.length; i < n; i++)
                iniData.filetext += lines[i] + LINE_ENDING;
            iniData.filetext = iniData.filetext.substring(0, iniData.filetext.length() - LINE_ENDING.length());
        }
    }
    
}

