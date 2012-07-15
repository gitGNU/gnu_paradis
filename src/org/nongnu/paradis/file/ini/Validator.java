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

import java.util.*;


/**
 * <p>INI validator</p>
 * <p>
 *   This validator most be used if you don't want comments on your keyvalues.
 * </p>
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Validator
{
    /**
     * Non-constructor
     */
    private Validator()
    {
        assert false : "You may not create instances of this class [Validator].";
    }
    
    
    
    /**
     * Validation result
     */
    public static class ValidationResult
    {
        /**
         * Contructors
         *
         * @param  suggestion  Suggested content
         * @param  warnings    Warnings
         * @param  errors      Errors
         */
        ValidationResult(String suggestion, ArrayList<Warnings> warnings, ArrayList<Errors> errors)
        {
            this.suggestion = suggestion;
            this.warnings   = warnings;
            this.errors     = errors;
        }
        
        
        
        /**
         * Suggested content
         */
        public final String suggestion;
        
        /**
         * Warnings
         */
        public final ArrayList<Warnings> warnings;
        
        /**
         * Errors
         */
        public final ArrayList<Errors> errors;
        
        
        
        /**
         * Possible Warnings
         */
        public static enum Warnings
        {
            /**
             * Comment on key line
             */
            CommentedKey,
            
            /**
             * Comment on hive line
             */
            CommentedHive,
            
            /**
             * Two (or more) hive have same name
             */
            DuplicateHiveDefinition,
            
            /**
             * Two (or more) key, in same hive name, have same name
             */
            DuplicateKeyDefinition,
            
            /**
             * Escaped line break
             */
            EscapedLineBreak,
            
            /**
             * Empty line, not all parsers support this
             */
            EmptyLine,
        }
        
        /**
         * Possible Errors
         */
        public static enum Errors
        {
            /**
             * Key missing value
             */
            UnvaluedKey,
            
            /**
             * Key outside hive
             */
            UnhivedKey,
            
            /**
             * Hive definition surrunded with spaces
             */
            HiveSurrounedWithSpaces,
            
            /**
             * Unnamed hive
             */
            UnnamedHive,
            
            /**
             * Unnamed key
             */
            UnnamedKey,
        }
    }
    
    
    
    /**
     * Validates the content
     *
     * @param   content  The content
     * @return           Validation result
     */
    public static ValidationResult validate(final String content)
    {
        if (content == null)
            throw new NullPointerException();
        
        {
            HashMap<String, ArrayList<String>> hives = new HashMap<String, ArrayList<String>>();
            
            ArrayList<ValidationResult.Warnings> warnings = new ArrayList<ValidationResult.Warnings>();
            ArrayList<ValidationResult.Errors>   errors   = new ArrayList<ValidationResult.Errors>();
            
            fillLists(hives, warnings, errors, content);
            
            String rc = "";
            {
                if (hives.containsKey(""))
                    for (String line : hives.get(""))
                        rc += line + "\n";
                
                String[] keys = hives.keySet().toArray(new String[0]);
                if (keys != null)
                    for (String key : keys)
                    {
                        if (key.equals(""))  continue;
                        
                        ArrayList<String> defininedKeys = new ArrayList<String>();
                        
                        rc += key + "\n";
                        for (String line : hives.get(key))
                        {
                            if ((line.charAt(0) == ';') || (line.charAt(0) == '#'))
                                rc += line;
                            else
                            {
                                String value = "";
                                String comment = null;
                                for (int i = 0, n = line.length(); i < n; i++)
                                {   char chr = line.charAt(i);
                                    if ((chr == ';') || (chr == '#'))
                                    {   comment = line.substring(i, line.length());
                                        break;
                                    }
                                    else if (chr == '\\')
                                    {   value += chr;
                                        i++;
                                        value += line.charAt(i);
                                    }
                                    else
                                        value += chr;
                                }
                                
                                if (comment != null)
                                {   warnings.add(ValidationResult.Warnings.CommentedKey);
                                    rc += comment + "\n";
                                }

                                if (value.startsWith(" = ") || value.startsWith("=") || value.startsWith(" : ") || value.startsWith(":"))
                                {   errors.add(ValidationResult.Errors.UnnamedKey);
                                    rc += ";" + value;
                                }
                                else
                                {   String keyname = "";
                                    for (int i = 0, n = value.length(); i < n; i++)
                                    {   char chr = value.charAt(i);
                                        if ((chr == '=') || (chr == ':') || ((chr == ' ') && (value.charAt(i + 2) == ' ') && ((value.charAt(i + 1) == '=') || (value.charAt(i + 1) == ':'))))
                                            break;
                                        else if (chr == '\\')
                                        {   keyname += chr;
                                            i++;
                                            keyname += value.charAt(i);
                                        }
                                        else
                                            keyname += chr;
                                    }
                                    
                                    if (keyname.length() > 0)
                                    {   if (keyname.equals(value))
                                        {   errors.add(ValidationResult.Errors.UnvaluedKey);
                                            rc += ";";
                                        }
                                        else if (defininedKeys.contains(keyname.toUpperCase()))
                                            warnings.add(ValidationResult.Warnings.DuplicateKeyDefinition);
                                        else
                                            defininedKeys.add(keyname.toUpperCase());
                                        rc += value;
                                    }
                                    else
                                    {   errors.add(ValidationResult.Errors.UnnamedKey);
                                        rc += ";" + value;
                                }   }
                            }
                            rc += "\n";
                        }
                    }
            }
            if (rc.endsWith("\n"))
                rc = rc.substring(0, rc.length() - "\n".length());
            return new ValidationResult(rc, warnings, errors);
        }
    }
    
    /**
     * Trims the text
     *
     * @param   text  The text
     * @return        The text trimmed
     */
    private static String trim(final String text)
    {
        if (text == null)
            return null;
        
        String rc = text;
        {
            while (rc.startsWith(" ") || rc.startsWith("\t") || rc.startsWith("\r") || rc.startsWith("\n"))
                rc = rc.substring(1, rc.length());
            
            while (rc.endsWith(" ")   || rc.endsWith("\t")   || rc.endsWith("\r")   || rc.endsWith("\n"))
                rc = rc.substring(0, rc.length() - 1);
        }
        return rc;
    }
    
    /**
     * Fills the hive-, warning- and errorlists
     *
     * @param  hives     The hivelist
     * @param  warnings  The warninglist
     * @param  errors    The errorlist
     * @param  content   The content
     */
    private static void fillLists(final HashMap<String, ArrayList<String>> hives,
                                  final ArrayList<ValidationResult.Warnings> warnings,
                                  final ArrayList<ValidationResult.Errors> errors,
                                  final String content)
    {
        ArrayList<String> lines;
        {
            String str = content.replace("\r\n", "\n").replace("\r", "\n");
            lines = new ArrayList<String>();
            
            String buf = "";
            boolean comment = false;
            for (int i = 0, n = str.length(); i < n; i++)
            {
                char chr = str.charAt(i);
                if (chr == '\\')
                {   buf += chr;
                    if (++i == n)
                        buf += "\\";
                    else
                        if (comment && (chr == '\n'))
                        {   lines.add(buf);
                            buf = "";
                            comment = false;
                        }
                        else
                        {   chr = str.charAt(i);
                            buf += (chr == '\n') ? " " : (new Character(chr)).toString();
                            if (chr == '\n')
                                warnings.add(ValidationResult.Warnings.EscapedLineBreak);
                        }
                }
                else if ((chr == ';') && (chr == '#'))
                    comment = true;
                else if (chr == '\n')
                {   lines.add(buf);
                    buf = "";
                    comment = false;
                }
                else
                    buf += chr;
            }
            lines.add(buf);
        }
        
        String hive = null;
        
        for (String line : lines)
            if (trim(line).length() == 0)
                warnings.add(ValidationResult.Warnings.EmptyLine);
            else if (trim(line).startsWith("["))
                hive = hiveStart(hives, warnings, errors, line, hive);
            else if (hive == null)
            {   if ((line.startsWith(";") || line.startsWith("#")) == false)
                {   line = ";" + line;
                    errors.add(ValidationResult.Errors.UnhivedKey);
                }

                if (hives.containsKey("") == false)
                    hives.put("", new ArrayList<String>());

                ArrayList<String> array = hives.get("");
                array.add(";" + line);
            }
            else
            {   ArrayList<String> array = hives.get("");
                array.add(";" + line);
            }
    }
    
    /**
     * Adds a line starting with [
     *
     * @param   hives     The hives
     * @param   warnings  The warnings
     * @param   errors    The errors
     * @param   line      The line
     * @param   hive      The hive name
     * @return            The hive name
     */
    private static String hiveStart(final HashMap<String, ArrayList<String>> hives,
                                    final ArrayList<ValidationResult.Warnings> warnings,
                                    final ArrayList<ValidationResult.Errors> errors,
                                    final String line, final String hive)
    {
        String _hive = hive; 
        
        boolean comments = false;
        for (int i = 0, n = line.length(); i < n; i++)
            if ((line.charAt(i) == ';') || (line.charAt(i) == '#'))
            {
                comments = true;
                break;
            }
            else if (line.charAt(i) == '\\')
                i++;
        
        if (comments)
            if (trim(line).endsWith("]"))
            {   if (trim(line).equals(line) == false)
                    errors.add(ValidationResult.Errors.HiveSurrounedWithSpaces);
                
                if (hives.containsKey(trim(line).toUpperCase()))
                    warnings.add(ValidationResult.Warnings.DuplicateHiveDefinition);
                else
                    hives.put(trim(line).toUpperCase(), new ArrayList<String>());
                
                _hive = trim(line).toUpperCase();
                
                if (_hive.equals("[]"))
                    errors.add(ValidationResult.Errors.UnnamedHive);
            }
            else if (_hive == null)
            {   errors.add(ValidationResult.Errors.UnhivedKey);
                
                if (hives.containsKey("") == false)
                    hives.put("", new ArrayList<String>());
                
                ArrayList<String> array = hives.get("");
                array.add(";" + line);
            }
            else
            {   ArrayList<String> array = hives.get(_hive);
                array.add(line);
            }
        else
        {
            String valued = "";
            String comment = "";
            for (int i = 0, n = line.length(); i < n; i++)
            {
                if ((line.charAt(i) == ';') || (line.charAt(i) == '#'))
                {   comment = line.substring(i, n);
                    break;
                }
                
                valued += line.charAt(i);
                
                if (line.charAt(i) == '\\')
                {   i++;
                    valued += line.charAt(i);
                }
            }
            
            {
                if (hives.containsKey(_hive == null ? "" : _hive) == false)
                    hives.put(_hive == null ? "" : _hive, new ArrayList<String>());

                ArrayList<String> array = hives.get(_hive == null ? "" : _hive);
                array.add(comment);
            }
            
            if (trim(valued).endsWith("]"))
            {
                warnings.add(ValidationResult.Warnings.CommentedHive);
                
                if (trim(valued).equals(valued) == false)
                    errors.add(ValidationResult.Errors.HiveSurrounedWithSpaces);
                
                if (hives.containsKey(trim(valued).toUpperCase()))
                    warnings.add(ValidationResult.Warnings.DuplicateHiveDefinition);
                else
                    hives.put(trim(valued).toUpperCase(), new ArrayList<String>());
                
                _hive = trim(valued).toUpperCase();
                
                if (_hive.equals("[]"))
                    errors.add(ValidationResult.Errors.UnnamedHive);
            }
            else if (_hive == null)
            {
                errors.add(ValidationResult.Errors.UnhivedKey);
                
                if (hives.containsKey("") == false)
                    hives.put("", new ArrayList<String>());
                
                ArrayList<String> array = hives.get("");
                array.add(";" + valued + comment);
            }
            else
            {
                warnings.add(ValidationResult.Warnings.CommentedKey);
                
                ArrayList<String> array = hives.get(_hive);
                array.add(valued);
            }
        }
        return _hive;
    }
    
    
}

