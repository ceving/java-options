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

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Options
{
    private class Option
    {
        final Character flag;
        final String    name;
        final String    description;
        final String[]  default_values;

        boolean  isset  = false;
        String[] values = null;

        /**
         * @throws InvalidOptionException
         */
        Option (Character flag,
                String    name,
                String    description,
                String[]  default_values)
        {
            if (flag == null && name == null)
                throw new InvalidOptionException
                    ("Missing option identifier");
            if (description == null)
                throw new InvalidOptionException
                    ("Missing option description");
            this.flag           = flag;
            this.name           = name;
            this.description    = description;
            this.default_values = default_values;
        }
        
        private String value (int i)
        {
            if (values[i] == null)
                return default_values[i];
            else
                return values[i];
        }
    }

    private List<Option>options_list = new LinkedList<Option>();
    private Map<String,Option>options_hash = new HashMap<String,Option>();
    private String[] about_text = null;
    private String[] usage_text = null;

    public Options option (Character flag,
                           String    name,
                           String    description,
                           String... default_values)
    {
        Option option = new Option (flag, name, description, default_values);
        if (flag != null)
            options_hash.put (flag.toString(), option);
        if (name != null)
            options_hash.put (name, option);
        options_list.add (option);
        return this;
    }

    public Options option (Character flag,
                           String    name,
                           String    description,
                           int       values)
    {
        String[] default_values = new String[values];
        Arrays.fill(default_values, null);
        return option (flag, name, description, default_values);
    }

    /**
     * @throws InvalidOptionException
     */
    private Option get_option (String id)
    {
        Option option = options_hash.get(id);
        if (option == null)
            throw new InvalidOptionException
                ("Undefined option: " + id);
        return option;
    }

    public String get (char flag, int i)
    {
        return get_option(String.valueOf(flag)).value(i);
    }

    public String get (char flag)
    {
        return get_option(String.valueOf(flag)).value(0);
    }

    public String get (String name, int i)
    {
        return get_option(name).value(i);
    }

    public String get (String name)
    {
        return get_option(name).value(0);
    }

    public boolean isset (char flag)
    {
        return get_option(String.valueOf(flag)).isset;
    }

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
            if (option.default_values != null)
                if (option.values == null ||
                    option.values.length != option.default_values.length)
                    throw new InvalidOptionException
                        ("Required option missing: " + option.flag == null ?
                         option.name : option.flag.toString());
    }

    private void print_usage ()
    {
        print_usage(System.out);
    }

    public void print_usage (PrintStream out)
    {
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
        out.println("Options:");
        int max_name = 0;
        int max_vals = 0;
        for (Option option : options_list)
            if (option.name != null)
            {
                if (option.name.length() > max_name)
                    max_name = option.name.length();
                if (option.default_values != null &&
                    option.default_values.length > max_vals)
                    max_vals = option.default_values.length;
            }
        for (Option option : options_list)
        {
            out.print("  ");
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
            for (int i = 0; i < max_vals; i++)
                if (option.default_values[i] != null)
                {
                    out.println (' ');
                    out.println ((char)((int)'A' + i));
                }
                else
                    out.println ("  ");
            out.print("   ");
            out.print(option.description);
            out.println();
        }
    }

    public Options about (String... text)
    {
        about_text = text;
        return this;
    }

    public Options usage (String... text)
    {
        usage_text = text;
        return this;
    }

    /**
     * Parse the arguments list by extracting the options and
     * returning the remaining arguments.
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
                        if (option.value_required)
                        {
                            a++;
                            if (a < arguments.length)
                                option.value = arguments[a];
                            else
                                throw new InvalidOptionException
                                    ("Option argument missing");
                        }
                    }
                else
                {
                    // This is a short option.
                    for (int i = 1; i < argument.length(); i++)
                    {
                        String flag = String.valueOf(argument.charAt(i));
                        Option option = get_option(flag);
                        option.isset = true;
                        if (option.value_required)
                        {
                            String rest = argument.substring(i);
                            if (rest.length() > 0)
                                // If pressent the remaining part of
                                // the argument is the value.
                                option.value = argument.substring(i);
                            else
                            {
                                // Otherwise the next argument is the
                                // value.
                                a++;
                                if (a < arguments.length)
                                    option.value = arguments[a];
                                else
                                    throw new InvalidOptionException
                                        ("Option argument missing");
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
}
