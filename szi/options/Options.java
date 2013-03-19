/**
 * Parse command line arguments.
 *
 * Copyright (C) 2013  Sascha Ziemann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package szi.options;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Options
{
    private List<Option>options_list = new LinkedList<Option>();
    private Map<String,Option>options_hash = new HashMap<String,Option>();
    private String[] about_text = null;
    private String[] usage_text = null;

    /**
     * Defines an option by adding it to the list of options and
     * returning the options.  Short options are identified by a flag
     * character and long options are identified by a naming
     * string. Options can have both a flag and a name but it is an
     * error, if both are undefined. The description for the usage
     * help is required.  An option can require one or more arguments
     * and each argument may have a default value.
     *
     * @param flag            the character for a short option
     * @param name            the long name of the option
     * @param description     a short description for the help text
     * @param required_values the number of required option values
     * @param default_values  the default values if the option is not
     *                        specified
     *
     * @return the option object
     *
     * @throws InvalidOptionException if flag and name are both null
     */
    public Options option (Character flag,
                           String    name,
                           String    description,
                           int       required_values,
                           String... default_values)
    {
        Option option = new Option (flag, name, description,
                                    required_values, default_values);
        if (flag != null)
            options_hash.put (flag.toString(), option);
        if (name != null)
            options_hash.put (name, option);
        options_list.add (option);
        return this;
    }
    
    /** @see #option(Character, String, String, int, String...) */
    public Options option (char      flag,
                           String    description,
                           int       required_values,
                           String... default_values)
    {
        return option (flag, null, description,
                       required_values, default_values);
    }

    /** @see #option(Character, String, String, int, String...) */
    public Options option (char      flag,
                           String    description)
    {
        return option (flag, null, description, 0);
    }

    /** @see #option(Character, String, String, int, String...) */
    public Options option (char      flag,
                           String    name,
                           String    description)
    {
        return option (flag, name, description, 0);
    }

    /** @see #option(Character, String, String, int, String...) */
    public Options option (String    name,
                           String    description,
                           int       required_values,
                           String... default_values)
    {
        return option (null, name, description,
                       required_values, default_values);
    }

    /** @see #option(Character, String, String, int, String...) */
    public Options option (String    name,
                           String    description)
    {
        return option ((Character)null, name, description, 0);
    }

    /**
     * @throws InvalidOptionException
     */
    private Option get_option (char flag)
    {
        Option option = options_hash.get(String.valueOf(flag));
        if (option == null)
            throw new InvalidOptionException
                ("Undefined short option: -" + flag);
        return option;
    }

    /**
     * @throws InvalidOptionException
     */
    private Option get_option (String name)
    {
        Option option = options_hash.get(name);
        if (option == null)
            throw new InvalidOptionException
                ("Undefined long option: --" + name);
        return option;
    }

    /**
     * Returns the nth value of a short option.
     *
     * @param flag  the character of a short option
     * @param index the intex to the value
     *
     * @return the value for the specified option
     */
    public String get (char flag, int index)
    {
        return get_option(flag).value(index);
    }

    /**
     * Returns the first value of an option.
     *
     * @see #get(char, int)
     */
    public String get (char flag)
    {
        return get_option(flag).value(0);
    }

    /**
     * Returns the nth value of a long option.
     *
     * @param name  the name of a long option
     * @param index the intex to the value
     *
     * @return the value for the specified option
     */
    public String get (String name, int index)
    {
        return get_option(name).value(index);
    }

    /**
     * Returns the first value of a long option.
     *
     * @see #get(String, int)
     */
    public String get (String name)
    {
        return get_option(name).value(0);
    }

    /**
     * Checks if a short option is set.
     *
     * @param flag the character of a short option
     *
     * @return true if the option is set
     */
    public boolean isset (char flag)
    {
        return get_option(flag).isset;
    }

    /**
     * Checks if a long option is set.
     *
     * @param name  the name of a long option
     *
     * @return true if the option is set
     */
    public boolean isset (String name)
    {
        return get_option(name).isset;
    }

    /**
     * Check if all required values are defined.
     *
     * @throws InvalidOptionException
     */
    private void check ()
    {
        for (Option option : options_list)
            if (option.isset && option.required_values > 0)
                for (int v = 0; v < option.required_values; v++)
                    if (option.values[v] == null)
                        throw new InvalidOptionException
                            ("Option value " + v + " missing for option: " +
                             option.flag != null ? option.flag.toString() :
                             option.name);
    }

    /**
     * Print the usage text to stdout.
     */
    public void print_usage ()
    {
        print_usage(System.out);
    }

    /**
     * Print the usage text to the specified print stream.
     *
     * @param out the output print stream
     */
    public void print_usage (PrintStream out)
    {
        // Display header
        if (about_text != null)
            for (String line : about_text)
                out.println(line);
        if (usage_text != null)
            if (usage_text.length == 1)
            {
                out.print("Usage: ");
                out.println(usage_text[0]);
            }
            else
            {
                out.println("Usage:");
                for (String line : usage_text)
                {
                    out.print("  ");
                    out.println(line);
                }
            }
        // Display options
        out.println("Options:");
        int max_name = 0;
        int max_vals = 0;
        for (Option option : options_list)
            if (option.name != null)
            {
                if (option.name.length() > max_name)
                    max_name = option.name.length();
                if (option.default_values != null &&
                    option.required_values > max_vals)
                    max_vals = option.required_values;
            }
        for (Option option : options_list)
        {
            out.print("  ");
            // Display short option
            if (option.flag != null)
            {
                out.print('-');
                out.print(option.flag);
            }
            else
                out.print("  ");
            if (option.flag != null && option.name != null)
                out.print(',');
            else
                out.print(' ');
            // Display long option
            if (option.name != null)
            {
                out.print(" --");
                out.print(option.name);
                for (int i = option.name.length(); i < max_name; i++)
                    out.print(' ');
            }
            else
            {
                out.print("   ");
                for (int i = 0; i < max_name; i++)
                    out.print(' ');
            }
            for (int i = 0; i < Math.min(8, max_vals); i++)
                if (option.required_values > 8 && i > 6)
                    out.print ("..");
                else
                    if (i < option.required_values)
                    {
                        out.print (' ');
                        out.print ((char)((int)'A' + i));
                    }
                    else
                        out.print ("  ");
            out.print("  ");
            // Display description
            out.print(option.description);
            out.println();
        }
    }

    /**
     * Define the about header
     *
     * @param text the text for the about header
     */
    public Options about (String... text)
    {
        about_text = text;
        return this;
    }

    /**
     * Define the about header
     *
     * @param text the text for the usage header
     */
    public Options usage (String... text)
    {
        usage_text = text;
        return this;
    }

    /**
     * Parse the arguments list by extracting the options and
     * returning the remaining arguments. Options are either short or
     * long options. A short option starts with a hyphen followed by a
     * character and zero or more optional argument. The following
     * list shows examples for valid short options.
     * <p>
     * <ul><li>-n</li><li>-o1</li><li>-o 1</li><li>-t 1 2</li></ul>
     * <p>
     * Long options are starting which two two hyphens followed by a
     * string and further optional arguments. The following list shows
     * examples for valid long options.
     * <p>
     * <ul><li>--help</li><li>--add 1 2</li><li>--debug hi</li></ul>
     * <p>
     * Two hyphens without any name terminate the option list.  All
     * options following the termination are returned as remaining
     * arguments.
     *
     * @param arguments the list of command line arguments
     *
     * @return the list of non option arguments
     *
     * @throws InvalidOptionException
     */
    public String[] parse (String[] arguments)
    {
        List<String> argument_list = new LinkedList<String>();

        for (int a = 0; a < arguments.length; a++)
        {
            String argument = arguments[a];
            if (argument.length() > 0 && argument.charAt(0) == '-')
                // This is an option.
                if (argument.length() > 1 && argument.charAt(1) == '-')
                    if (argument.length() == 2)
                        // This is the option termination.
                        for (a++; a < arguments.length; a++)
                            argument_list.add (arguments[a]);
                    else
                    {
                        // This is a long option.
                        Option option = get_option (argument.substring (2));
                        option.isset = true;
                        for (int v = 0; v < option.required_values; v++)
                        {
                            a++;
                            if (a < arguments.length)
                                option.values[v] = arguments[a];
                            else
                                throw new InvalidOptionException
                                    ("argument missing for option: "
                                     + option.id());
                        }
                    }
                else
                {
                    // This is a short option.
                    for (int i = 1; i < argument.length(); i++)
                    {
                        Option option = get_option(argument.charAt(i));
                        option.isset = true;
                        if (option.required_values > 0)
                        {
                            int v = 0;
                            String rest = argument.substring(++i);
                            if (rest.length() > 0)
                                // If pressent the remaining part of
                                // the argument is the value.
                                option.values[v++] = rest;

                            for (; v < option.required_values; v++)
                            {
                                // Otherwise the next argument is the
                                // value.
                                a++;
                                if (a < arguments.length)
                                    option.values[v] = arguments[a];
                                else
                                    throw new InvalidOptionException
                                        ("Argument missing for option: "
                                         + option.id());
                            }
                            break;
                        }
                    }
                }
            else
                // This is an argument and no option.
                argument_list.add(arguments[a]);
        }
        check();
        return argument_list.toArray(new String[0]);
    }

    public String toString()
    {
        return Arrays.toString(options_list.toArray());
    }
}
