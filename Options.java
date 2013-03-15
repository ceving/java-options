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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Options
{
    private class Option
    {
        final String  flag;
        final String  name;
        final String  description;
        final boolean value_required;
        final String  default_value;

        boolean isset = false;
        String  value = null;

        /**
         * @throws InvalidOptionException
         */
        Option (String  flag,
                String  name,
                String  description,
                boolean value_required,
                String  default_value)
        {
            if (flag == null)
            {
                if (name == null)
                    throw new InvalidOptionException
                        ("Missing option identifier");
            }
            else
                if (flag.length() != 1)
                    throw new InvalidOptionException
                        ("Option flag length does not equal 1");
            if (description == null)
                throw new InvalidOptionException
                    ("Missing option description");
            this.flag           = flag;
            this.name           = name;
            this.description    = description;
            this.value_required = value_required;
            this.default_value  = default_value;
        }
        
        /**
         * @throws InvalidOptionException
         */
        Option (char    flag,
                String  name,
                String  description,
                boolean value_required,
                String  default_value)
        {
            this (String.valueOf(flag), name, description, 
                  value_required, default_value);
        }

        private String value ()
        {
            if (value == null)
                return default_value;
            else
                return value;
        }
    }

    private List<Option>options_list = new LinkedList<Option>();
    private Map<String,Option>options_hash = new HashMap<String,Option>();
    private boolean usage_enabled = true;
    private String[] usage_header = null;

    public Options option (String  flag,
                           String  name,
                           String  description,
                           boolean value_required,
                           String  default_value)
    {
        Option option = new Option (flag, name, description, 
                                    value_required, default_value);
        if (flag != null)
            options_hash.put (flag, option);
        if (name != null)
            options_hash.put (name, option);
        options_list.add (option);
        return this;
    }

    public Options option (char    flag,
                           String  name,
                           String  description,
                           boolean value_required,
                           String  default_value)
    {
        return option (String.valueOf(flag), name, description,
                       value_required, default_value);
    }

    public Options option (char    flag,
                           String  name,
                           String  description)
    {
        return option (flag, name, description, false, null);
    }

    public Options option (String  name,
                           String  description)
    {
        return option (null, name, description, false, null);
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

    private Option get_option (char flag)
    {
        return get_option (String.valueOf(flag));
    }

    public String get (char flag)
    {
        return get_option(flag).value();
    }

    public String get (String name)
    {
        return get_option(name).value();
    }

    public boolean isset (char flag)
    {
        return get_option(flag).isset;
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
            if (option.value_required && option.value() == null)
                throw new InvalidOptionException
                    ("Required option missing: " + 
                     option.flag == null ? option.name : option.flag);
    }

    private void print_usage ()
    {
        print_usage(System.out);
    }

    public void print_usage (PrintStream out)
    {
        if (usage_header != null)
            for (String line : usage_header)
                out.println(line);
        out.println("Options:");
        int max_name = 0;
        for (Option option : options_list)
            if (option.name != null)
                if (option.name.length() > max_name)
                    max_name = option.name.length();
        for (Option option : options_list)
        {
            out.print(' ');
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
            out.print(' ');
            if (option.value_required)
                out.print("VALUE");
            else
                out.print("     ");
            out.print("   ");
            out.print(option.description);
            out.println();
        }
    }

    public Options usage (String... header)
    {
        usage_enabled = true;
        usage_header = header;
        return this;
    }

    public Options help (char flag, String description)
    {
        return option (flag, null, description, false, null);
    }

    public Options help (String name, String description)
    {
        return option (null, name, description, false, null);
    }

    public Options help (char flag, String name, String description)
    {
        return option (flag, name, description, false, null);
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
